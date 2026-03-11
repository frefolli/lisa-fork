package it.unive.lisa.analysis.avase;

import java.util.Map;

import it.unive.lisa.analysis.ScopeToken;
import it.unive.lisa.analysis.SemanticException;
import it.unive.lisa.analysis.Lattice;
import it.unive.lisa.analysis.BaseLattice;
import it.unive.lisa.program.cfg.ProgramPoint;
import it.unive.lisa.program.cfg.edge.Edge;
import it.unive.lisa.program.cfg.statement.Statement;
import it.unive.lisa.symbolic.SymbolicExpression;
import it.unive.lisa.symbolic.value.Identifier;
import it.unive.lisa.symbolic.value.ValueExpression;
import it.unive.lisa.util.representation.StringRepresentation;
import it.unive.lisa.util.representation.StructuredRepresentation;
import it.unive.lisa.program.cfg.statement.comparison.*;

import it.unive.lisa.program.cfg.controlFlow.*;
import it.unive.lisa.program.cfg.CFG;

import jbse.val.exc.InvalidOperandException;
import jbse.val.exc.InvalidOperatorException;
import jbse.val.exc.InvalidTypeException;

import jbse.val.Simplex;
import jbse.val.Primitive;
import jbse.val.Operator;
import jbse.val.Expression;


public class PathConditions
  implements BaseLattice<PathConditions>, Speculator {

  Primitive pathCondition;

	/**
	 * Builds a (nonBottom) path condition.
	 *
	 * @param pathCondition, the path condition
	 */
	public PathConditions(Primitive pathCondition) {
    this.pathCondition = pathCondition;
	}

	public PathConditions mk(Primitive pathCondition) {
		return new PathConditions(pathCondition);
	}

	private static Primitive implicit(boolean isBottom) {
    try {
      return Simplex.make(!isBottom);
    } catch (InvalidOperandException e) {
      throw new RuntimeException(e.toString());
    }
	}

	private PathConditions(boolean isBottom) {
    this(implicit(isBottom));
	}

	/**
	 * Builds an empty path condition (T)
	 */
	public PathConditions() {
		this(false);
	}

	@Override
	public PathConditions top() {
		return new PathConditions(false);
	}

	@Override
	public PathConditions bottom() {
		return new PathConditions(true);
	}

	@Override
	public boolean isTop() {
    return this.pathCondition.surelyTrue();
	}

	@Override
	public boolean isBottom() {
    return this.pathCondition.surelyFalse();
	}

  public Primitive getPathCondition() {
    return this.pathCondition;
  }

  /* Speculator */

	public boolean shouldConsiderProgramPoint(ProgramPoint pp) {
		if (!(pp instanceof Statement)) {
			return false;
		}
		if (pp.getCFG().getEntrypoints().contains(pp)
				|| !(pp.getCFG().getIngoingEdges((Statement)pp).isEmpty()))
			return true;
		return false;
	}

	public PathConditions initializeState(ProgramPoint pp) {
		assert shouldConsiderProgramPoint(pp);
		return this.top();
	}

	public PathConditions getPrecedentState(ProgramPoint pp) {
    Map<ProgramPoint, PathConditions> function = DataflowStateMap.getPathConditionsMap();
    assert(function != null);
    if (!function.containsKey(pp)) {
      return initializeState(pp);
    } else {
      return function.get(pp);
    }
  }

	public PathConditions joinPrecedentStates(ProgramPoint pp) throws SemanticException {
    PathConditions state = this.top();
    for (Edge edge : pp.getCFG().getIngoingEdges((Statement)pp)) {
      ProgramPoint source = edge.getSource();
      if (shouldConsiderProgramPoint(source)) {
        PathConditions prev = getPrecedentState(source);
        if (state.isTop()) {
          state = prev;
        } else {
          state = state.lub(prev);
        }
      }
    }
    return state;
  }

  /* SPECULATOR */

  @Override
	public PathConditions normalStep(ProgramPoint pp) throws SemanticException {
		if (shouldConsiderProgramPoint(pp)) {
      PathConditions state = joinPrecedentStates(pp);
      Map<ProgramPoint, PathConditions> function = DataflowStateMap.getPathConditionsMap();
			function.put(pp, state);
      return state;
		} else {
      return mk(pathCondition);
    }
  }

  @Override
	public PathConditions assignStep(ProgramPoint pp, Identifier id, SymbolicExpression expr) throws SemanticException {
    return normalStep(pp);
  }

  private int dumpCfg(ProgramPoint pp, String mark, int counter) {
    int id = counter++;
    System.out.println("[" + mark + "][" + id + "]<" + pp.getClass() + ">" + pp + " BEGIN ");
    for (Edge edge : pp.getCFG().getIngoingEdges((Statement)pp)) {
      ProgramPoint source = edge.getSource();
      counter = dumpCfg(source, mark, counter);
    }
    System.out.println("[" + mark + "][" + id + "]<" + pp.getClass() + ">" + pp + " END ");
    return counter;
  }

  @Override
	public PathConditions controlStep(ProgramPoint condition, ProgramPoint branch) throws SemanticException {
    CFG cfg = condition.getCFG();
    boolean condition_is_true = false;
    if (condition == cfg.getMostRecentGuard(branch)) {
      ControlFlowStructure cfs = cfg.getControlFlowStructureOf(condition);
      if (cfs instanceof IfThenElse) {
        IfThenElse stmt = (IfThenElse)cfs;
        if (stmt.getTrueBranch().contains((Statement)branch)) {
          condition_is_true = true;
        } else if (stmt.getFalseBranch().contains((Statement)branch)) {
          condition_is_true = false;
        } else {
          System.err.println("Something is off with " + stmt);
        }
      } else if (cfs instanceof Loop) {
        Loop stmt = (Loop)cfs;
        if (stmt.contains((Statement)branch)) {
          condition_is_true = true;
        } else if (stmt.getFirstFollower().equals(branch)) {
          condition_is_true = false;
        } else {
          System.err.println("Something is off with " + stmt);
        }
      } else {
        System.err.println(cfg.getClass() + " is not a supported ControlFlowStructure");
      }
    } else {
      System.err.println("controlStep.src is not a Guard");
    }
    if (condition_is_true) {
      System.out.println("[CS][" + condition + "]<TRUE>" + branch);
    } else {
      System.out.println("[CS][" + condition + "]<FALSE>" + branch);
    }
    return mk(pathCondition);
  }

  @Override
	public PathConditions pushScope(ScopeToken token) throws SemanticException {
    return mk(pathCondition);
  }

  @Override
	public PathConditions popScope(ScopeToken token) throws SemanticException {
    return mk(pathCondition);
  }

	@Override
	public StructuredRepresentation representation() {
		if (isBottom())
			return Lattice.bottomRepresentation();
		if (isTop())
			return Lattice.topRepresentation();
    return new StringRepresentation(pathCondition.toString());
	}

	@Override
	public boolean lessOrEqualAux(PathConditions other) {
		return false;
	}

  /* Towards TOP */
	@Override
	public PathConditions lubAux(PathConditions other)
			throws SemanticException {
    try {
      PathConditions state = mk(Expression.makeExpressionBinary(pathCondition, Operator.OR, other.getPathCondition()));
      return state;
    } catch (InvalidOperandException e) {
      throw new RuntimeException(e.toString());
    } catch (InvalidOperatorException e) {
      throw new RuntimeException(e.toString());
    } catch (InvalidTypeException e) {
      throw new RuntimeException(e.toString());
    }
	}

  /* Towards BOTTOM */
	@Override
	public PathConditions glbAux(PathConditions other)
			throws SemanticException {
    try {
      PathConditions state = mk(Expression.makeExpressionBinary(pathCondition, Operator.AND, other.getPathCondition()));
      return state;
    } catch (InvalidOperandException e) {
      throw new RuntimeException(e.toString());
    } catch (InvalidOperatorException e) {
      throw new RuntimeException(e.toString());
    } catch (InvalidTypeException e) {
      throw new RuntimeException(e.toString());
    }
	}
}

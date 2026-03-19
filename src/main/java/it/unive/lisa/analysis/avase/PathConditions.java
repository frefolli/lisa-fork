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
  implements BaseLattice<PathConditions>, Speculator<PathConditions> {

  Primitive condition;

	/**
	 * Builds a (nonBottom) path condition.
	 *
	 * @param condition, the path condition
	 */
	private PathConditions(Primitive condition) {
    this.condition = condition;
	}

	public PathConditions mk(Primitive condition) {
		return new PathConditions(Calculator.simplify(condition));
	}

	private static Primitive implicit(boolean isBottom) {
    return Calculator.constant(!isBottom);
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
    return this.condition.surelyTrue();
	}

	@Override
	public boolean isBottom() {
    return this.condition.surelyFalse();
	}

  public Primitive getCondition() {
    return this.condition;
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

	public PathConditions getState(ProgramPoint pp) {
    Map<ProgramPoint, PathConditions> function = DataflowStateMap.getPathConditionsMap();
    assert(function != null);
    if (!function.containsKey(pp)) {
      return initializeState(pp);
    } else {
      return function.get(pp);
    }
  }

	public PathConditions getControl(ProgramPoint pp) {
    Map<ProgramPoint, ControlConditions> function = DataflowStateMap.getControlConditionsMap();
    assert(function != null);
    if (!function.containsKey(pp)) {
      return initializeState(pp);
    } else {
      return mk(function.get(pp).getCondition());
    }
  }

	public PathConditions joinPrecedentStates(ProgramPoint pp) throws SemanticException {
    PathConditions state = this.top();
    for (Edge edge : pp.getCFG().getIngoingEdges((Statement)pp)) {
      ProgramPoint source = edge.getSource();
      if (shouldConsiderProgramPoint(source)) {
        PathConditions prev = getState(source);
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
      PathConditions precedent = joinPrecedentStates(pp);
      PathConditions control = getControl(pp);
      PathConditions state = precedent.glb(control);
      Map<ProgramPoint, PathConditions> function = DataflowStateMap.getPathConditionsMap();
			function.put(pp, state);
      System.out.println("[S:AS](" + pp + " -> " + control + " GLB " + precedent + " = " + state + ")");
      return state;
		} else {
      return mk(condition);
    }
  }

  @Override
	public PathConditions assignStep(ProgramPoint pp, Identifier id, SymbolicExpression expr) throws SemanticException {
		if (shouldConsiderProgramPoint(pp)) {
      PathConditions precedent = joinPrecedentStates(pp);
      PathConditions control = getControl(pp);
      PathConditions state = precedent.glb(control);
      Map<ProgramPoint, PathConditions> function = DataflowStateMap.getPathConditionsMap();
			function.put(pp, state);
      System.out.println("[S:AS](" + pp + " -> " + control + " GLB " + precedent + " = " + state + ")");
      return state;
		} else {
      return mk(condition);
    }
  }

  @Override
	public PathConditions controlStep(ProgramPoint src, ProgramPoint dest) throws SemanticException {
    return mk(condition);
  }

  @Override
	public PathConditions pushScope(ScopeToken token) throws SemanticException {
    return mk(condition);
  }

  @Override
	public PathConditions popScope(ScopeToken token) throws SemanticException {
    return mk(condition);
  }

	@Override
	public StructuredRepresentation representation() {
		if (isBottom())
			return Lattice.bottomRepresentation();
		if (isTop())
			return Lattice.topRepresentation();
    return new StringRepresentation(condition.toString());
	}

	@Override
	public boolean lessOrEqualAux(PathConditions other) {
		return false;
	}

  /* Towards TOP */
	@Override
	public PathConditions lubAux(PathConditions other)
			throws SemanticException {
    return mk(Calculator.binaryExpression(condition, Operator.OR, other.condition));
	}

  /* Towards BOTTOM */
	@Override
	public PathConditions glbAux(PathConditions other)
			throws SemanticException {
    return mk(Calculator.binaryExpression(condition, Operator.AND, other.condition));
	}

  /* Invert Path Condition */
	public PathConditions invert() {
    return mk(Calculator.unaryExpression(Operator.NOT, this.condition));
	}

	@Override
	public String toString() {
		return representation().toString();
	}
}

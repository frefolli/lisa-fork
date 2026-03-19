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


public class ControlConditions
  implements BaseLattice<ControlConditions>, Speculator<ControlConditions> {

  Primitive condition;

	/**
	 * Builds a (nonBottom) control condition.
	 *
	 * @param condition, the control condition
	 */
	private ControlConditions(Primitive condition) {
    this.condition = condition;
	}

	public ControlConditions mk(Primitive condition) {
		return new ControlConditions(Calculator.simplify(condition));
	}

	private static Primitive implicit(boolean isBottom) {
    return Calculator.constant(!isBottom);
	}

	private ControlConditions(boolean isBottom) {
    this(implicit(isBottom));
	}

	/**
	 * Builds an empty control condition (T)
	 */
	public ControlConditions() {
		this(false);
	}

	@Override
	public ControlConditions top() {
		return new ControlConditions(false);
	}

	@Override
	public ControlConditions bottom() {
		return new ControlConditions(true);
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

	public ControlConditions initializeState(ProgramPoint pp) {
		assert shouldConsiderProgramPoint(pp);
		return this.top();
	}

	public ControlConditions getState(ProgramPoint pp) {
    Map<ProgramPoint, ControlConditions> function = DataflowStateMap.getControlConditionsMap();
    assert(function != null);
    if (!function.containsKey(pp)) {
      return initializeState(pp);
    } else {
      return function.get(pp);
    }
  }

  /* SPECULATOR */

  @Override
	public ControlConditions normalStep(ProgramPoint pp) throws SemanticException {
    ControlConditions state = getState(pp);
    Map<ProgramPoint, ControlConditions> function = DataflowStateMap.getControlConditionsMap();
    function.put(pp, state);
    return state;
  }

  @Override
	public ControlConditions assignStep(ProgramPoint pp, Identifier id, SymbolicExpression expr) throws SemanticException {
    ControlConditions state = getState(pp);
    Map<ProgramPoint, ControlConditions> function = DataflowStateMap.getControlConditionsMap();
    function.put(pp, state);
    return state;
  }

  @Override
	public ControlConditions controlStep(ProgramPoint src, ProgramPoint dest) throws SemanticException {
    ControlConditions state = mk(AstTransmuter.visit(src));
    if (BranchController.isBranchConditionFalse(src, dest)) {
      state = state.invert();
    }

    Map<ProgramPoint, ControlConditions> function = DataflowStateMap.getControlConditionsMap();
    function.put(dest, state);
    return state;
  }

  @Override
	public ControlConditions pushScope(ScopeToken token) throws SemanticException {
    return mk(condition);
  }

  @Override
	public ControlConditions popScope(ScopeToken token) throws SemanticException {
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
	public boolean lessOrEqualAux(ControlConditions other) {
		return false;
	}

  /* Towards TOP */
	@Override
	public ControlConditions lubAux(ControlConditions other)
			throws SemanticException {
    return mk(Calculator.binaryExpression(condition, Operator.OR, other.condition));
	}

  /* Towards BOTTOM */
	@Override
	public ControlConditions glbAux(ControlConditions other)
			throws SemanticException {
    return mk(Calculator.binaryExpression(condition, Operator.AND, other.condition));
	}

  /* Invert Control Condition */
	public ControlConditions invert() {
    return mk(Calculator.unaryExpression(Operator.NOT, this.condition));
	}
 
	@Override
	public String toString() {
		return representation().toString();
	}
}

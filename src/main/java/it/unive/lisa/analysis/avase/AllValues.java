package it.unive.lisa.analysis.avase;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Collections;
import java.util.function.Predicate;

import it.unive.lisa.analysis.ScopeToken;
import it.unive.lisa.analysis.SemanticException;
import it.unive.lisa.analysis.lattices.ExpressionSet;
import it.unive.lisa.program.cfg.ProgramPoint;
import it.unive.lisa.program.cfg.edge.Edge;
import it.unive.lisa.program.cfg.statement.Statement;
import it.unive.lisa.symbolic.SymbolicExpression;
import it.unive.lisa.symbolic.heap.HeapExpression;
import it.unive.lisa.symbolic.value.Identifier;
import it.unive.lisa.symbolic.value.ValueExpression;
import it.unive.lisa.type.Type;
import it.unive.lisa.type.TypeTokenType;
import it.unive.lisa.analysis.lattices.SetLattice;
import it.unive.lisa.program.cfg.controlFlow.Loop;

import jbse.val.Primitive;

public class AllValues extends SetLattice<AllValues, SymbolicValue>
  implements Speculator<AllValues> {
	public AllValues(Set<SymbolicValue> elements, boolean isTop) {
		super(elements, isTop);
	}

	/**
	 * Builds the empty set lattice element.
	 */
	public AllValues() {
		this(Collections.emptySet(), false);
	}

	private AllValues(
			boolean isTop) {
		this(Collections.emptySet(), isTop);
	}

	/**
	 * Builds a singleton set lattice element.
	 * 
	 * @param exp the expression
	 */
	public AllValues(
			SymbolicValue exp) {
		this(Collections.singleton(exp), false);
	}

	/**
	 * Builds a set lattice element.
	 * 
	 * @param set the set of expression
	 */
	public AllValues(
			Set<SymbolicValue> set) {
		this(Collections.unmodifiableSet(set), false);
	}
	@Override
	public AllValues mk(Set<SymbolicValue> set) {
		return new AllValues(set);
	}
	
	@Override
	public AllValues top() {
		return new AllValues(true);
	}

	@Override
	public AllValues bottom() {
		return new AllValues();
	}

	public boolean shouldConsiderProgramPoint(ProgramPoint pp) {
		if (!(pp instanceof Statement)) {
			return false;
		}
		if (pp.getCFG().getEntrypoints().contains(pp)
				|| !(pp.getCFG().getIngoingEdges((Statement)pp).isEmpty()))
			return true;
		return false;
	}

  /* Inspects */

  public static boolean isCondition(ProgramPoint pp) {
    return pp.getCFG().getControlFlowStructureOf(pp) != null;
  }

  public static boolean isLoopCondition(ProgramPoint pp) {
    return pp.getCFG().getControlFlowStructureOf(pp) instanceof Loop;
  }

  /* SPECULATOR */

  @Override
	public AllValues normalStep(ProgramPoint pp) throws SemanticException {
    if (shouldConsiderProgramPoint(pp)) {
      if (isCondition(pp)) {
        AllValues state = new AllValues(new SymbolicValue(AstTransmuter.visit(pp), Calculator.makeTrue()));
        DataflowStateMap.getAllValuesMap().put(pp, state);
        System.out.println("AV::Update(" + pp + ")");
        return state;
      }
    }
    return new AllValues();
  }

  @Override
	public AllValues assignStep(ProgramPoint pp, Identifier id, SymbolicExpression expr) throws SemanticException {
    if (shouldConsiderProgramPoint(pp)) {
      AllValues state = new AllValues(new SymbolicValue(SymExpTransmuter.visit(expr), Calculator.makeTrue()));
      DataflowStateMap.getAllValuesMap().put(pp, state);
      System.out.println("AV::Update(" + pp + ")");
      return state;
    }
    return new AllValues();
  }

  @Override
	public AllValues controlStep(ProgramPoint src, ProgramPoint dest) throws SemanticException {
    return new AllValues();
  }

	@Override
	public AllValues pushScope(
			ScopeToken scope)
			throws SemanticException {
		return mk(elements);
	}

	@Override
	public AllValues popScope(
			ScopeToken scope)
			throws SemanticException {
		return mk(elements);
	}
}

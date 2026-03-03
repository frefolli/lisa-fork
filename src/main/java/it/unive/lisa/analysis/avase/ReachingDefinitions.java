package it.unive.lisa.analysis.avase;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Collections;
import java.util.function.Predicate;

import it.unive.lisa.analysis.AbstractState;
import it.unive.lisa.analysis.ScopeToken;
import it.unive.lisa.analysis.SemanticException;
import it.unive.lisa.analysis.SemanticOracle;
import it.unive.lisa.analysis.lattices.ExpressionSet;
import it.unive.lisa.analysis.lattices.FunctionalLattice;
import it.unive.lisa.analysis.lattices.Satisfiability;
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

public class ReachingDefinitions extends SetLattice<ReachingDefinitions, Definition>
  implements Speculator {
	public ReachingDefinitions(Set<Definition> elements, boolean isTop) {
		super(elements, isTop);
	}

	/**
	 * Builds the empty set lattice element.
	 */
	public ReachingDefinitions() {
		this(Collections.emptySet(), false);
	}

	private ReachingDefinitions(
			boolean isTop) {
		this(Collections.emptySet(), isTop);
	}

	/**
	 * Builds a singleton set lattice element.
	 * 
	 * @param exp the expression
	 */
	public ReachingDefinitions(
			Definition exp) {
		this(Collections.singleton(exp), false);
	}

	/**
	 * Builds a set lattice element.
	 * 
	 * @param set the set of expression
	 */
	public ReachingDefinitions(
			Set<Definition> set) {
		this(Collections.unmodifiableSet(set), false);
	}
	@Override
	public ReachingDefinitions mk(Set<Definition> set) {
		return new ReachingDefinitions(set);
	}
	
	@Override
	public ReachingDefinitions top() {
		return new ReachingDefinitions(true);
	}

	@Override
	public ReachingDefinitions bottom() {
		return new ReachingDefinitions();
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

	public ReachingDefinitions initializeState(ProgramPoint pp) {
		assert shouldConsiderProgramPoint(pp);
		return new ReachingDefinitions();
	}

	public ReachingDefinitions getPrecedentState(ProgramPoint pp) {
    Map<ProgramPoint, ReachingDefinitions> function = DataflowStateMap.getReachingDefinitionsMap();
    assert(function != null);
    if (!function.containsKey(pp)) {
      return initializeState(pp);
    } else {
      return function.get(pp);
    }
  }

	public ReachingDefinitions joinPrecedentStates(ProgramPoint pp) throws SemanticException {
    ReachingDefinitions state = this.bottom();
    for (Edge edge : pp.getCFG().getIngoingEdges((Statement)pp)) {
      ProgramPoint source = edge.getSource();
      if (shouldConsiderProgramPoint(source)) {
        ReachingDefinitions prev = getPrecedentState(source);
        if (state.isBottom()) {
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
	public ReachingDefinitions normalStep(ProgramPoint pp) throws SemanticException {
		if (shouldConsiderProgramPoint(pp)) {
      ReachingDefinitions state = joinPrecedentStates(pp);
      Map<ProgramPoint, ReachingDefinitions> function = DataflowStateMap.getReachingDefinitionsMap();
			function.put(pp, state);
		}
		return mk(elements);
  }

  @Override
	public ReachingDefinitions assignStep(ProgramPoint pp, Identifier id, SymbolicExpression expr) throws SemanticException {
		if (shouldConsiderProgramPoint(pp)) {
      ReachingDefinitions state = joinPrecedentStates(pp);
      Set<Definition> filtered = new HashSet<>();
      for (Definition d : state.elements) {
        if (!d.variable.equals(id)) {
          filtered.add(d);
        }
      }
      state = new ReachingDefinitions(filtered);
      state = state.lub(new ReachingDefinitions(new Definition(id, pp)));
      Map<ProgramPoint, ReachingDefinitions> function = DataflowStateMap.getReachingDefinitionsMap();
			function.put(pp, state);
      return state;
    }
		return mk(elements);
  }

  @Override
	public ReachingDefinitions controlStep(ProgramPoint src, ProgramPoint dest) throws SemanticException {
    return mk(elements);
  }

	@Override
	public ReachingDefinitions pushScope(
			ScopeToken scope)
			throws SemanticException {
		return mk(elements);
		// return new ReachingDefinitions((Identifier) variable.pushScope(scope), programPoint);
	}

	@Override
	public ReachingDefinitions popScope(
			ScopeToken scope)
			throws SemanticException {
		// if (!variable.canBeScoped())
		// 	return this;

		// SymbolicExpression popped = variable.popScope(scope);
		// if (popped == null)
		// 	return null;

		// return new ReachingDefinitions((Identifier) popped, programPoint);
		return mk(elements);
	}

	/* FUNCTIONAL LATTICE */

	public ReachingDefinitions stateOfUnknown(ProgramPoint key) {
		return this.isBottom() ? this.bottom() : this.top();
	}
}

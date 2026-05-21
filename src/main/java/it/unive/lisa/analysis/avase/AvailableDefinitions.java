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
import it.unive.lisa.logging.Logger;

public class AvailableDefinitions extends SetLattice<AvailableDefinitions, Definition>
  implements Speculator<AvailableDefinitions> {
	public AvailableDefinitions(Set<Definition> elements, boolean isTop) {
		super(elements, isTop);
	}

	/**
	 * Builds the empty set lattice element.
	 */
	public AvailableDefinitions() {
		this(Collections.emptySet(), false);
	}

	private AvailableDefinitions(
			boolean isTop) {
		this(Collections.emptySet(), isTop);
	}

	/**
	 * Builds a singleton set lattice element.
	 * 
	 * @param exp the expression
	 */
	public AvailableDefinitions(
			Definition exp) {
		this(Collections.singleton(exp), false);
	}

	/**
	 * Builds a set lattice element.
	 * 
	 * @param set the set of expression
	 */
	public AvailableDefinitions(
			Set<Definition> set) {
		this(Collections.unmodifiableSet(set), false);
	}
	@Override
	public AvailableDefinitions mk(Set<Definition> set) {
		return new AvailableDefinitions(set);
	}
	
	@Override
	public AvailableDefinitions top() {
		return new AvailableDefinitions(true);
	}

	@Override
	public AvailableDefinitions bottom() {
		return new AvailableDefinitions();
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

	public ReachingDefinitions getReachingDefinitions(ProgramPoint pp) {
    Map<ProgramPoint, ReachingDefinitions> function = DataflowStateMap.getReachingDefinitionsMap();
    assert(function != null);
    if (!function.containsKey(pp)) {
      return new ReachingDefinitions();
    } else {
      return function.get(pp);
    }
  }

	public KilledDefinitions getKilledDefinitions(ProgramPoint pp) {
    Map<ProgramPoint, KilledDefinitions> function = DataflowStateMap.getKilledDefinitionsMap();
    assert(function != null);
    if (!function.containsKey(pp)) {
      return new KilledDefinitions();
    } else {
      return function.get(pp);
    }
  }

  /* SPECULATOR */

  @Override
	public AvailableDefinitions normalStep(ProgramPoint pp) throws SemanticException {
		if (shouldConsiderProgramPoint(pp)) {
      Set<Definition> reachingDefinitions = new HashSet<>(getReachingDefinitions(pp).elements);
      Set<Definition> killedDefinitions = getKilledDefinitions(pp).elements;
      reachingDefinitions.removeAll(killedDefinitions);
      AvailableDefinitions state = new AvailableDefinitions(reachingDefinitions);
      Map<ProgramPoint, AvailableDefinitions> function = DataflowStateMap.getAvailableDefinitionsMap();
			function.put(pp, state);
      return state;
		}
		return mk(elements);
  }

  @Override
	public AvailableDefinitions assignStep(ProgramPoint pp, Identifier id, SymbolicExpression expr) throws SemanticException {
		if (shouldConsiderProgramPoint(pp)) {
      Set<Definition> reachingDefinitions = new HashSet<>(getReachingDefinitions(pp).elements);
      Set<Definition> killedDefinitions = getKilledDefinitions(pp).elements;
      reachingDefinitions.removeAll(killedDefinitions);
      AvailableDefinitions state = new AvailableDefinitions(reachingDefinitions);
      state = state.lub(new AvailableDefinitions(new Definition(id, pp)));
      Map<ProgramPoint, AvailableDefinitions> function = DataflowStateMap.getAvailableDefinitionsMap();
			function.put(pp, state);
      return state;
    }
		return mk(elements);
  }

  @Override
	public AvailableDefinitions controlStep(ProgramPoint src, ProgramPoint dest) throws SemanticException {
    return mk(elements);
  }

	@Override
	public AvailableDefinitions pushScope(
			ScopeToken scope)
			throws SemanticException {
		return mk(elements);
	}

	@Override
	public AvailableDefinitions popScope(
			ScopeToken scope)
			throws SemanticException {
		return mk(elements);
	}
}

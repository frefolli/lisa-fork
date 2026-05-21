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

public class KilledDefinitions extends SetLattice<KilledDefinitions, Definition>
  implements Speculator<KilledDefinitions> {
	public KilledDefinitions(Set<Definition> elements, boolean isTop) {
		super(elements, isTop);
	}

	/**
	 * Builds the empty set lattice element.
	 */
	public KilledDefinitions() {
		this(Collections.emptySet(), false);
	}

	private KilledDefinitions(
			boolean isTop) {
		this(Collections.emptySet(), isTop);
	}

	/**
	 * Builds a singleton set lattice element.
	 * 
	 * @param exp the expression
	 */
	public KilledDefinitions(
			Definition exp) {
		this(Collections.singleton(exp), false);
	}

	/**
	 * Builds a set lattice element.
	 * 
	 * @param set the set of expression
	 */
	public KilledDefinitions(
			Set<Definition> set) {
		this(Collections.unmodifiableSet(set), false);
	}
	@Override
	public KilledDefinitions mk(Set<Definition> set) {
		return new KilledDefinitions(set);
	}
	
	@Override
	public KilledDefinitions top() {
		return new KilledDefinitions(true);
	}

	@Override
	public KilledDefinitions bottom() {
		return new KilledDefinitions();
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

  /* SPECULATOR */

  @Override
	public KilledDefinitions normalStep(ProgramPoint pp) throws SemanticException {
		return mk(elements);
  }

  @Override
	public KilledDefinitions assignStep(ProgramPoint pp, Identifier id, SymbolicExpression expr) throws SemanticException {
		if (shouldConsiderProgramPoint(pp)) {
      ReachingDefinitions precedents = getReachingDefinitions(pp);
      Set<Definition> filtered = new HashSet<>();
      for (Definition d : precedents.elements) {
        if (d.variable.equals(id)) {
          filtered.add(d);
        }
      }
      KilledDefinitions state = new KilledDefinitions(filtered);
      Map<ProgramPoint, KilledDefinitions> function = DataflowStateMap.getKilledDefinitionsMap();
			function.put(pp, state);
      return state;
    }
		return mk(elements);
  }

  @Override
	public KilledDefinitions controlStep(ProgramPoint src, ProgramPoint dest) throws SemanticException {
    return mk(elements);
  }

	@Override
	public KilledDefinitions pushScope(
			ScopeToken scope)
			throws SemanticException {
		return mk(elements);
		// return new KilledDefinitions((Identifier) variable.pushScope(scope), programPoint);
	}

	@Override
	public KilledDefinitions popScope(
			ScopeToken scope)
			throws SemanticException {
		// if (!variable.canBeScoped())
		// 	return this;

		// SymbolicExpression popped = variable.popScope(scope);
		// if (popped == null)
		// 	return null;

		// return new KilledDefinitions((Identifier) popped, programPoint);
		return mk(elements);
	}
}

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
  implements Speculator<ReachingDefinitions> {
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

	public AvailableDefinitions getAvailableDefinitions(ProgramPoint pp) {
    Map<ProgramPoint, AvailableDefinitions> function = DataflowStateMap.getAvailableDefinitionsMap();
    assert(function != null);
    if (!function.containsKey(pp)) {
      return new AvailableDefinitions();
    } else {
      return function.get(pp);
    }
  }

	public ReachingDefinitions joinAvailableDefinitions(ProgramPoint pp) throws SemanticException {
    Set<Definition> definitions = new HashSet<>();
    for (Edge edge : pp.getCFG().getIngoingEdges((Statement)pp)) {
      ProgramPoint source = edge.getSource();
      if (shouldConsiderProgramPoint(source)) {
        AvailableDefinitions prev = getAvailableDefinitions(source);
        for (Definition definition : prev.elements) {
          definitions.add(definition);
        }
      }
    }
    return new ReachingDefinitions(definitions);
  }

  /* SPECULATOR */

  @Override
	public ReachingDefinitions normalStep(ProgramPoint pp) throws SemanticException {
		if (shouldConsiderProgramPoint(pp)) {
      ReachingDefinitions state = joinAvailableDefinitions(pp);
      Map<ProgramPoint, ReachingDefinitions> function = DataflowStateMap.getReachingDefinitionsMap();
			function.put(pp, state);
      return state;
		}
		return mk(elements);
  }

  @Override
	public ReachingDefinitions assignStep(ProgramPoint pp, Identifier id, SymbolicExpression expr) throws SemanticException {
		if (shouldConsiderProgramPoint(pp)) {
      ReachingDefinitions state = joinAvailableDefinitions(pp);
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
	}

	@Override
	public ReachingDefinitions popScope(
			ScopeToken scope)
			throws SemanticException {
		return mk(elements);
	}

	/* FUNCTIONAL LATTICE */

	public ReachingDefinitions stateOfUnknown(ProgramPoint key) {
		return this.isBottom() ? this.bottom() : this.top();
	}
}

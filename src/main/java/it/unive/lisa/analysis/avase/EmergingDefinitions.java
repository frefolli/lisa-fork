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

public class EmergingDefinitions extends SetLattice<EmergingDefinitions, Definition>
  implements Speculator<EmergingDefinitions> {
	public EmergingDefinitions(Set<Definition> elements, boolean isTop) {
		super(elements, isTop);
	}

	/**
	 * Builds the empty set lattice element.
	 */
	public EmergingDefinitions() {
		this(Collections.emptySet(), false);
	}

	private EmergingDefinitions(
			boolean isTop) {
		this(Collections.emptySet(), isTop);
	}

	/**
	 * Builds a singleton set lattice element.
	 * 
	 * @param exp the expression
	 */
	public EmergingDefinitions(
			Definition exp) {
		this(Collections.singleton(exp), false);
	}

	/**
	 * Builds a set lattice element.
	 * 
	 * @param set the set of expression
	 */
	public EmergingDefinitions(
			Set<Definition> set) {
		this(Collections.unmodifiableSet(set), false);
	}
	@Override
	public EmergingDefinitions mk(Set<Definition> set) {
		return new EmergingDefinitions(set);
	}
	
	@Override
	public EmergingDefinitions top() {
		return new EmergingDefinitions(true);
	}

	@Override
	public EmergingDefinitions bottom() {
		return new EmergingDefinitions();
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

  /* SPECULATOR */

  @Override
	public EmergingDefinitions normalStep(ProgramPoint pp) throws SemanticException {
    return mk(elements);
  }

  @Override
	public EmergingDefinitions assignStep(ProgramPoint pp, Identifier id, SymbolicExpression expr) throws SemanticException {
    if (shouldConsiderProgramPoint(pp)) {
      Map<ProgramPoint, KilledDefinitions> KD = DataflowStateMap.getKilledDefinitionsMap();
      Map<ProgramPoint, EmergingDefinitions> ED = DataflowStateMap.getEmergingDefinitionsMap();

      if (!ED.containsKey(pp)) {
        ED.put(pp, new EmergingDefinitions());
      }
      if (KD.containsKey(pp)) {
        for (Definition peer : KD.get(pp).elements) {
          if (!ED.containsKey(peer)) {
            ED.put(peer.programPoint, new EmergingDefinitions(new Definition(id, pp)));
          } else {
            ED.put(peer.programPoint, ED.get(peer).lub(new EmergingDefinitions(new Definition(id, pp))));
          }
        }
      }
      return mk(ED.get(pp).elements);
    }
    return mk(elements);
  }

  @Override
	public EmergingDefinitions controlStep(ProgramPoint src, ProgramPoint dest) throws SemanticException {
    return mk(elements);
  }

	@Override
	public EmergingDefinitions pushScope(
			ScopeToken scope)
			throws SemanticException {
		return mk(elements);
		// return new EmergingDefinitions((Identifier) variable.pushScope(scope), programPoint);
	}

	@Override
	public EmergingDefinitions popScope(
			ScopeToken scope)
			throws SemanticException {
		// if (!variable.canBeScoped())
		// 	return this;

		// SymbolicExpression popped = variable.popScope(scope);
		// if (popped == null)
		// 	return null;

		// return new EmergingDefinitions((Identifier) popped, programPoint);
		return mk(elements);
	}
}

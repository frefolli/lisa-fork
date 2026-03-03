package it.unive.lisa.analysis.avase;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

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
import it.unive.lisa.analysis.lattices.InverseSetLattice;

public class Dominance
  extends InverseSetLattice<Dominance, ProgramPoint>
  implements Speculator {

	public Dominance(Set<ProgramPoint> elements, boolean isTop) {
		super(elements, isTop);
	}

	/**
	 * Builds the empty set lattice element.
	 */
	public Dominance() {
		this(Collections.emptySet(), false);
	}

	private Dominance(
			boolean isTop) {
		this(Collections.emptySet(), isTop);
	}

	/**
	 * Builds a singleton set lattice element.
	 * 
	 * @param exp the expression
	 */
	public Dominance(
			ProgramPoint exp) {
		this(Collections.singleton(exp), false);
	}

	/**
	 * Builds a set lattice element.
	 * 
	 * @param set the set of expression
	 */
	public Dominance(
			Set<ProgramPoint> set) {
		this(Collections.unmodifiableSet(set), false);
	}

	@Override
	public Dominance mk(Set<ProgramPoint> set) {
		return new Dominance(set);
	}
	
	@Override
	public Dominance top() {
		return new Dominance(true);
	}

	@Override
	public Dominance bottom() {
		return new Dominance();
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

	public Dominance initializeState(ProgramPoint pp) {
		assert shouldConsiderProgramPoint(pp);
		Set<ProgramPoint> result = new HashSet<>();
		if (pp.getCFG().getEntrypoints().contains(pp)) {
			result.add(pp);
		} else {
			for (Statement stmt : pp.getCFG().getNodes()) {
				if (shouldConsiderProgramPoint(stmt)) {
					result.add(stmt);
				}
			}
		}
		return new Dominance(result);
	}

	public Dominance getPrecedentState(ProgramPoint pp) {
    Map<ProgramPoint, Dominance> function = DataflowStateMap.getDominanceMap();
    assert(function != null);
    if (!function.containsKey(pp)) {
      return initializeState(pp);
    } else {
      return function.get(pp);
    }
  }

	public Dominance joinPrecedentStates(ProgramPoint pp) throws SemanticException {
    Dominance state = this.bottom();
    for (Edge edge : pp.getCFG().getIngoingEdges((Statement)pp)) {
      ProgramPoint source = edge.getSource();
      if (shouldConsiderProgramPoint(source)) {
        Dominance prev = getPrecedentState(source);
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
	public Dominance normalStep(ProgramPoint pp) throws SemanticException {
		if (shouldConsiderProgramPoint(pp)) {
      Dominance state = joinPrecedentStates(pp);
      Dominance current = new Dominance(pp);
      if (state.isBottom()) {
        state = current;
      } else {
        state = state.glb(current);
      }
      Map<ProgramPoint, Dominance> function = DataflowStateMap.getDominanceMap();
			function.put(pp, state);
      return state;
		} else {
      return mk(elements);
    }
  }

  @Override
	public Dominance assignStep(ProgramPoint pp, Identifier id, SymbolicExpression expr) throws SemanticException {
    return normalStep(pp);
  }

  @Override
	public Dominance controlStep(ProgramPoint src, ProgramPoint dest) throws SemanticException {
    return mk(elements);
  }

  @Override
	public Dominance pushScope(ScopeToken token) throws SemanticException {
    return mk(elements);
  }

  @Override
	public Dominance popScope(ScopeToken token) throws SemanticException {
    return mk(elements);
  }
}

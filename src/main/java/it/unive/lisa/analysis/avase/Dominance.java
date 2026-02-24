package it.unive.lisa.analysis.avase;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
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
import it.unive.lisa.analysis.dataflow.PPInverseSetLattice;

public class Dominance
  extends FunctionalLattice<Dominance, ProgramPoint, PPInverseSetLattice>
  implements Speculator {

	private Dominance(PPInverseSetLattice lattice,
			Map<ProgramPoint, PPInverseSetLattice> function) {
		super(lattice, function);
	}

	public Dominance() {
		super(new PPInverseSetLattice(), new HashMap<ProgramPoint, PPInverseSetLattice>());
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

	public PPInverseSetLattice initializeState(ProgramPoint pp) {
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
		return new PPInverseSetLattice(result);
	}

	public PPInverseSetLattice getPrecedentState(ProgramPoint pp) {
    if (function == null || !function.containsKey(pp)) {
      return initializeState(pp);
    } else {
      return function.get(pp);
    }
  }

	public PPInverseSetLattice joinPrecedentStates(ProgramPoint pp) throws SemanticException {
    PPInverseSetLattice state = lattice.bottom();
    for (Edge edge : pp.getCFG().getIngoingEdges((Statement)pp)) {
      ProgramPoint source = edge.getSource();
      if (shouldConsiderProgramPoint(source)) {
        PPInverseSetLattice prev = getPrecedentState(source);
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
    System.out.println("NORMAL: " + pp);
		Map<ProgramPoint, PPInverseSetLattice> newFunction = mkNewFunction(function, false);
		if (shouldConsiderProgramPoint(pp)) {
      PPInverseSetLattice state = joinPrecedentStates(pp);
      PPInverseSetLattice current = new PPInverseSetLattice(pp);
      if (state.isBottom()) {
        state = current;
      } else {
        state = state.glb(current);
      }
			newFunction.put(pp, state);
		}
		return mk(lattice, newFunction);
  }

  @Override
	public Dominance assignStep(ProgramPoint pp, Identifier id, SymbolicExpression expr) throws SemanticException {
    System.out.println("ASSIGN: " + pp);
    return normalStep(pp);
  }

  @Override
	public Dominance controlStep(ProgramPoint src, ProgramPoint dest) throws SemanticException {
    System.out.println("CONTROL: " + src + " -> " + dest);

    PPInverseSetLattice bb = lattice.bottom();
    PPInverseSetLattice tt = lattice.top();
    PPInverseSetLattice ll = new PPInverseSetLattice(src);
    PPInverseSetLattice rr = new PPInverseSetLattice(dest);
    
    return mk(lattice, function);
  }

  @Override
	public Dominance pushScope(ScopeToken token) throws SemanticException {
    return mk(lattice, function);
  }

  @Override
	public Dominance popScope(ScopeToken token) throws SemanticException {
    return mk(lattice, function);
  }

	/* FUNCTIONAL LATTICE */

  @Override
	public PPInverseSetLattice stateOfUnknown(ProgramPoint key) {
		return this.isBottom() ? lattice.bottom() : lattice.top();
	}

  @Override
	public Dominance mk(PPInverseSetLattice lattice,
			Map<ProgramPoint, PPInverseSetLattice> function) {
		return new Dominance(lattice, function);
	}

  @Override
	public Dominance top() {
		return new Dominance(lattice.top(), null);
	}

  @Override
	public Dominance bottom() {
		return new Dominance(lattice.bottom(), null);
	}
}

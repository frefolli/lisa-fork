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

public class ReachingDefinitions
  extends FunctionalLattice<ReachingDefinitions, ProgramPoint, DefinitionSetLattice>
  implements Speculator {

	private ReachingDefinitions(DefinitionSetLattice lattice,
			Map<ProgramPoint, DefinitionSetLattice> function) {
		super(lattice, function);
	}

	public ReachingDefinitions() {
		super(new DefinitionSetLattice(), new HashMap<ProgramPoint, DefinitionSetLattice>());
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

	public DefinitionSetLattice initializeState(ProgramPoint pp) {
		assert shouldConsiderProgramPoint(pp);
		return new DefinitionSetLattice();
	}

	public DefinitionSetLattice getPrecedentState(ProgramPoint pp) {
    if (function == null || !function.containsKey(pp)) {
      return initializeState(pp);
    } else {
      return function.get(pp);
    }
  }

	public DefinitionSetLattice joinPrecedentStates(ProgramPoint pp) throws SemanticException {
    DefinitionSetLattice state = lattice.bottom();
    for (Edge edge : pp.getCFG().getIngoingEdges((Statement)pp)) {
      ProgramPoint source = edge.getSource();
      if (shouldConsiderProgramPoint(source)) {
        DefinitionSetLattice prev = getPrecedentState(source);
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
    System.out.println("NORMAL: " + pp);
		Map<ProgramPoint, DefinitionSetLattice> newFunction = mkNewFunction(function, false);
		if (shouldConsiderProgramPoint(pp)) {
      DefinitionSetLattice state = joinPrecedentStates(pp);
			newFunction.put(pp, state);
		}
		return mk(lattice, newFunction);
  }

  @Override
	public ReachingDefinitions assignStep(ProgramPoint pp, Identifier id, SymbolicExpression expr) throws SemanticException {
    System.out.println("ASSIGN: " + pp + " -> " + id + " = " + expr);
		Map<ProgramPoint, DefinitionSetLattice> newFunction = mkNewFunction(function, false);
		if (shouldConsiderProgramPoint(pp)) {
      DefinitionSetLattice state = joinPrecedentStates(pp);
      Set<Definition> filtered = new HashSet<>();
      for (Definition d : state.elements) {
        if (!d.variable.equals(id)) {
          filtered.add(d);
        }
      }
      state = new DefinitionSetLattice(filtered);
      state = state.lub(new DefinitionSetLattice(new Definition(id, pp)));
			newFunction.put(pp, state);
    }
		return mk(lattice, newFunction);
  }

  @Override
	public ReachingDefinitions controlStep(ProgramPoint src, ProgramPoint dest) throws SemanticException {
    System.out.println("CONTROL: " + src + " -> " + dest);
    return mk(lattice, function);
  }

	@Override
	public ReachingDefinitions pushScope(
			ScopeToken scope)
			throws SemanticException {
		Map<ProgramPoint, DefinitionSetLattice> newFunction = mkNewFunction(function, false);
		return mk(lattice, newFunction);
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
		Map<ProgramPoint, DefinitionSetLattice> newFunction = mkNewFunction(function, false);
		return mk(lattice, newFunction);
	}

	/* FUNCTIONAL LATTICE */

  @Override
	public DefinitionSetLattice stateOfUnknown(ProgramPoint key) {
		return this.isBottom() ? lattice.bottom() : lattice.top();
	}

  @Override
	public ReachingDefinitions mk(DefinitionSetLattice lattice,
			Map<ProgramPoint, DefinitionSetLattice> function) {
		return new ReachingDefinitions(lattice, function);
	}

  @Override
	public ReachingDefinitions top() {
		return new ReachingDefinitions(lattice.top(), null);
	}

  @Override
	public ReachingDefinitions bottom() {
		return new ReachingDefinitions(lattice.bottom(), null);
	}
}

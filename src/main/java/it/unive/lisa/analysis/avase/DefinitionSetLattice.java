package it.unive.lisa.analysis.avase;

import java.util.Collections;
import java.util.Set;
import it.unive.lisa.analysis.lattices.SetLattice;

public class DefinitionSetLattice extends SetLattice<DefinitionSetLattice, Definition> {
	public DefinitionSetLattice(Set<Definition> elements, boolean isTop) {
		super(elements, isTop);
	}

	/**
	 * Builds the empty set lattice element.
	 */
	public DefinitionSetLattice() {
		this(Collections.emptySet(), false);
	}

	private DefinitionSetLattice(
			boolean isTop) {
		this(Collections.emptySet(), isTop);
	}

	/**
	 * Builds a singleton set lattice element.
	 * 
	 * @param exp the expression
	 */
	public DefinitionSetLattice(
			Definition exp) {
		this(Collections.singleton(exp), false);
	}

	/**
	 * Builds a set lattice element.
	 * 
	 * @param set the set of expression
	 */
	public DefinitionSetLattice(
			Set<Definition> set) {
		this(Collections.unmodifiableSet(set), false);
	}
	@Override
	public DefinitionSetLattice mk(Set<Definition> set) {
		return new DefinitionSetLattice(set);
	}
	
	@Override
	public DefinitionSetLattice top() {
		return new DefinitionSetLattice(true);
	}

	@Override
	public DefinitionSetLattice bottom() {
		return new DefinitionSetLattice();
	}
}

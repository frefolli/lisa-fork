package it.unive.lisa.analysis.dataflow;

import java.util.Collections;
import java.util.Set;

import it.unive.lisa.analysis.lattices.InverseSetLattice;
import it.unive.lisa.program.cfg.ProgramPoint;

public class PPInverseSetLattice extends InverseSetLattice<PPInverseSetLattice, ProgramPoint> {

	public PPInverseSetLattice(Set<ProgramPoint> elements, boolean isTop) {
		super(elements, isTop);
	}

	/**
	 * Builds the empty set lattice element.
	 */
	public PPInverseSetLattice() {
		this(Collections.emptySet(), false);
	}
	

	private PPInverseSetLattice(
			boolean isTop) {
		this(Collections.emptySet(), isTop);
	}

	/**
	 * Builds a singleton set lattice element.
	 * 
	 * @param exp the expression
	 */
	public PPInverseSetLattice(
			ProgramPoint exp) {
		this(Collections.singleton(exp), false);
	}

	/**
	 * Builds a set lattice element.
	 * 
	 * @param set the set of expression
	 */
	public PPInverseSetLattice(
			Set<ProgramPoint> set) {
		this(Collections.unmodifiableSet(set), false);
	}
	@Override
	public PPInverseSetLattice mk(Set<ProgramPoint> set) {
		return new PPInverseSetLattice(set);
	}
	
	@Override
	public PPInverseSetLattice top() {
		return new PPInverseSetLattice(true);
	}

	@Override
	public PPInverseSetLattice bottom() {
		return new PPInverseSetLattice();
	}


}

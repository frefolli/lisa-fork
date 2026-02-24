package it.unive.lisa.analysis.avase;

import it.unive.lisa.program.cfg.ProgramPoint;
import it.unive.lisa.analysis.SemanticException;
import it.unive.lisa.symbolic.SymbolicExpression;
import it.unive.lisa.symbolic.value.Identifier;
import it.unive.lisa.analysis.ScopeToken;

public interface Speculator {
	/**
	 * Returns a copy of itself after applying the speculating logic to a generic statement.
   * In the case of Dataflow Analysis Components, this coincides with the Dataflow Step
	 * 
	 * @param pp The ProgramPoint being processed
	 * @return a copy of the Speculator after processing the ProgramPoint
	 */
	public Speculator normalStep(ProgramPoint pp) throws SemanticException;

	/**
	 * Returns a copy of itself after applying the speculating logic to a variable assignment.
   * In the case of Dataflow Analysis Components, this coincides with the Dataflow Step
	 * 
	 * @param pp The ProgramPoint being processed
   * @param id The Identifier being assigned
   * @param expr The Expression being assigned
	 * @return a copy of the Speculator after processing the ProgramPoint
	 */
	public Speculator assignStep(ProgramPoint pp, Identifier id, SymbolicExpression expr) throws SemanticException;

	/**
	 * Returns a copy of itself after applying the speculating logic to a flow control transition.
   * In the case of Dataflow Analysis Components, this coincides with the Dataflow Step
	 * 
	 * @param src The ProgramPoint representing the point of flow control (the condition)
	 * @param dest The ProgramPoint representing the point of flow control choice (the branch selected)
	 * @return a copy of the Speculator after processing the ProgramPoint
	 */
	public Speculator controlStep(ProgramPoint src, ProgramPoint dest) throws SemanticException;

	/**
	 * Pushes a new scope, identified by the give token, in this object. This
	 * causes all variables not associated with a scope (and thus visible) to be
	 * mapped to the given scope and hidden away, until the scope is popped with
	 * {@link #popScope(ScopeToken)}.
	 *
	 * @param token the token identifying the scope to push
	 * 
	 * @return a copy of this object where the local unscoped variables have
	 *             been hidden
	 * 
	 * @throws SemanticException if an error occurs during the computation
   */
	public Speculator pushScope(ScopeToken token) throws SemanticException;

	/**
	 * Pops the scope identified by the given token from this object. This
	 * causes all the visible variables (i.e. that are not mapped to a scope) to
	 * be removed, while the local variables that were associated to the given
	 * scope token (and thus hidden) will become visible again.
	 *
	 * @param token the token of the scope to be restored
	 * 
	 * @return a copy of this object where the local variables have been
	 *             removed, while the variables mapped to the given scope are
	 *             visible again
	 * 
	 * @throws SemanticException if an error occurs during the computation
	 */
	public Speculator popScope(ScopeToken token) throws SemanticException;
}

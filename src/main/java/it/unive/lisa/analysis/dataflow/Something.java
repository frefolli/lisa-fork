package it.unive.lisa.analysis.dataflow;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import it.unive.lisa.analysis.lattices.ExpressionSet;
import it.unive.lisa.analysis.lattices.FunctionalLattice;
import it.unive.lisa.analysis.lattices.GenericSetLattice;
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
import it.unive.lisa.analysis.AbstractState;
import it.unive.lisa.analysis.ScopeToken;
import it.unive.lisa.analysis.SemanticException;
import it.unive.lisa.analysis.SemanticOracle;
import java.util.function.Predicate;

public class Something
  extends FunctionalLattice<Something, ProgramPoint, GenericSetLattice<ProgramPoint>>
  implements AbstractState<Something> {
  private Something(GenericSetLattice<ProgramPoint> lattice,
                    Map<ProgramPoint, GenericSetLattice<ProgramPoint>> function) {
    super(lattice, function);
  }

  public Something() {
    super(new GenericSetLattice<ProgramPoint>(), new HashMap<ProgramPoint, GenericSetLattice<ProgramPoint>>());
  }

  /* FUNCTIONAL LATTICE */

  /**
   * Yields the value that should be returned by {@link #getState(Object)}
   * whenever the given key is not present in this map.
   * 
   * @param key the key that is missing
   * 
   * @return the lattice element for keys not in the mapping
   */
  public GenericSetLattice<ProgramPoint> stateOfUnknown(ProgramPoint key) {
    Set<ProgramPoint> result = new HashSet<>();
    if (key.getCFG().getEntrypoints().contains(key)) {
      result.add(key);
    } else {
      for (Statement stmt : key.getCFG().getNodes()) {
        result.add(stmt);
      }
    }
    return new GenericSetLattice<>(result);
  }

  /**
   * Builds a instance of this class from the given lattice instance and the
   * given mapping.
   * 
   * @param lattice  an instance of lattice to be used during semantic
   *                     operations to retrieve top and bottom values
   * @param function the function representing the mapping contained in the
   *                     new environment; can be {@code null}
   * 
   * @return a new instance of this class
   */
  public Something mk(GenericSetLattice<ProgramPoint> lattice,
                      Map<ProgramPoint, GenericSetLattice<ProgramPoint>> function) {
    return new Something(lattice, function);
  }

  /**
   * Yields the top element of this lattice. The returned element should be
   * unique across different calls to this method, since {@link #isTop()} uses
   * reference equality by default. If the value returned by this method is
   * not a singleton, override {@link #isTop()} accordingly to provide a
   * coherent test.
   *
   * {@link FunctionalLattice} already reimplements {@link #isTop()} to
   * have a non-unique returned {@link #top()} element. 
   * It checks that lattice.top() and function=null.
   * @return the top element
   */
  public Something top() {
    return new Something(lattice.top(), null);
  }

  /**
   * Yields the bottom element of this lattice. The returned element should be
   * unique across different calls to this method, since {@link #isBottom()}
   * uses reference equality by default. If the value returned by this method
   * is not a singleton, override {@link #isBottom()} accordingly to provide a
   * coherent test.
   * 
   * {@link FunctionalLattice} already reimplements {@link #isBottom()} to
   * have a non-unique returned {@link #bottom()} element.
   * It checks that lattice.bottom() and function=null.
   * @return the bottom element
   */
  public Something bottom() {
    return new Something(lattice.bottom(), null);
  }

  /* ABSTRACT STATE */
  /**
   * Yields a copy of this state, but with its memory abstraction set to top.
   * This is useful to represent effects of unknown calls that arbitrarily
   * manipulate the memory.
   * 
   * @return the copy with top memory
   */
  public Something withTopMemory() {
    return mk(lattice, function);
  }

  /**
   * Yields a copy of this state, but with its value abstraction set to top.
   * This is useful to represent effects of unknown calls that arbitrarily
   * manipulate the values of variables.
   * 
   * @return the copy with top values
   */
  public Something withTopValues() {
    return mk(lattice, function);
  }

  /**
   * Yields a copy of this state, but with its type abstraction set to top.
   * This is useful to represent effects of unknown calls that arbitrarily
   * manipulate the values of variables (and their type accordingly).
   * 
   * @return the copy with top types
   */
  public Something withTopTypes() {
    return mk(lattice, function);
  }

  /* MEMORY-ORACLE */
  /**
   * Rewrites the given expression to a simpler form containing no sub
   * expressions regarding the heap (that is, {@link HeapExpression}s). Every
   * expression contained in the result can be safely cast to
   * {@link ValueExpression}.
   * 
   * @param expression the expression to rewrite
   * @param pp         the program point where the rewrite happens
   * @param oracle     the oracle for inter-domain communication
   * 
   * @return the rewritten expressions
   * 
   * @throws SemanticException if something goes wrong while rewriting
   */
  public ExpressionSet rewrite(SymbolicExpression expression,
                               ProgramPoint pp,
                               SemanticOracle oracle) throws SemanticException {
    return new ExpressionSet(expression);
  }

  /**
   * Yields whether or not the two given expressions are aliases, that is, if
   * they point to the same region of memory. Note that, for this method to
   * return {@link Satisfiability#SATISFIED}, both expressions should be
   * pointers to other expressions.
   * 
   * @param x      the first expression
   * @param y      the second expression
   * @param pp     the {@link ProgramPoint} where the computation happens
   * @param oracle the oracle for inter-domain communication
   * 
   * @return whether or not the two expressions are aliases
   * 
   * @throws SemanticException if something goes wrong during the computation
   */
  public Satisfiability alias(SymbolicExpression x,
                              SymbolicExpression y,
                              ProgramPoint pp,
                              SemanticOracle oracle) throws SemanticException {
    return Satisfiability.SATISFIED;
  }

  /**
   * Yields whether or not the {@link Identifier} represented (directly or
   * after rewriting) by the second expression is reachable starting from the
   * {@link Identifier} represented (directly or after rewriting) by the first
   * expression. Note that, for this method to return
   * {@link Satisfiability#SATISFIED}, not only {@code x} needs to be a
   * pointer to another expression, but the latter should be a pointer as
   * well, and so on until {@code y} is reached.
   * 
   * @param x      the first expression
   * @param y      the second expression
   * @param pp     the {@link ProgramPoint} where the computation happens
   * @param oracle the oracle for inter-domain communication
   * 
   * @return whether or not the second expression can be reached from the
   *             first one
   * 
   * @throws SemanticException if something goes wrong during the computation
   */
  public Satisfiability isReachableFrom(SymbolicExpression x,
                                        SymbolicExpression y,
                                        ProgramPoint pp,
                                        SemanticOracle oracle) throws SemanticException {
    return Satisfiability.SATISFIED;
  }

  /* TYPE-ORACLE */
  /**
   * Yields the runtime types that this analysis infers for the given
   * expression.
   * 
   * @param e      the expression to type
   * @param pp     the program point where the types are required
   * @param oracle the oracle for inter-domain communication
   * 
   * @return the runtime types
   * 
   * @throws SemanticException if something goes wrong during the computation
   */
  public Set<Type> getRuntimeTypesOf(SymbolicExpression e,
                                     ProgramPoint pp,
                                     SemanticOracle oracle) throws SemanticException {
    return Set.of();
  }

  /**
   * Yields the dynamic type that this analysis infers for the given
   * expression. The dynamic type is the least common supertype of all its
   * runtime types.
   * 
   * @param e      the expression to type
   * @param pp     the program point where the types are required
   * @param oracle the oracle for inter-domain communication
   * 
   * @return the dynamic type
   * 
   * @throws SemanticException if something goes wrong during the computation
   */
  public Type getDynamicTypeOf(SymbolicExpression e,
                               ProgramPoint pp,
                               SemanticOracle oracle) throws SemanticException {
    return new TypeTokenType(Set.of());
  }

  /* SCOPED-OBJECT */
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
  public Something pushScope(ScopeToken token) throws SemanticException {
    return mk(lattice, function);
  }

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
  public Something popScope(ScopeToken token) throws SemanticException {
    return mk(lattice, function);
  }

  /* SEMANTIC-DOMAIN */
	/**
	 * Yields a copy of this domain, where {@code id} has been assigned to
	 * {@code value}.
	 * 
	 * @param id         the identifier to assign the value to
	 * @param expression the expression to assign
	 * @param pp         the program point that where this operation is being
	 *                       evaluated
	 * @param oracle     the oracle for inter-domain communication
	 * 
	 * @return a copy of this domain, modified by the assignment
	 * 
	 * @throws SemanticException if an error occurs during the computation
	 */
	public Something assign(Identifier id,
			                    SymbolicExpression expression,
			                    ProgramPoint pp,
			                    SemanticOracle oracle) throws SemanticException {
    Map<ProgramPoint, GenericSetLattice<ProgramPoint>> newFunction = mkNewFunction(function, false);
    return mk(lattice, newFunction);
  }

	/**
	 * Yields a copy of this domain, that has been modified accordingly to the
	 * semantics of the given {@code expression}.
	 * 
	 * @param expression the expression whose semantics need to be computed
	 * @param pp         the program point that where this operation is being
	 *                       evaluated
	 * @param oracle     the oracle for inter-domain communication
	 * 
	 * @return a copy of this domain, modified accordingly to the semantics of
	 *             {@code expression}
	 * 
	 * @throws SemanticException if an error occurs during the computation
	 */
	public Something smallStepSemantics(SymbolicExpression expression,
			                                ProgramPoint pp,
			                                SemanticOracle oracle) throws SemanticException {
    Map<ProgramPoint, GenericSetLattice<ProgramPoint>> newFunction = mkNewFunction(function, false);
    if (!newFunction.containsKey(pp)) {
      newFunction.put(pp, stateOfUnknown(pp));
    }
    GenericSetLattice<ProgramPoint> state = null;
    for (Edge edge : pp.getCFG().getIngoingEdges((Statement)pp)) {
      GenericSetLattice<ProgramPoint> prev = null;
      if (!newFunction.containsKey(edge.getSource())) {
        newFunction.put(edge.getSource(), stateOfUnknown(edge.getSource()));
      }
      prev = newFunction.get(edge.getSource());
      if (state == null) {
        state = prev;
      } else {
        state = state.glb(prev);
      }
    }
    if (state == null) {
      state = new GenericSetLattice<ProgramPoint>(pp);
    } else {
      state = state.lub(new GenericSetLattice<ProgramPoint>(pp));
    }
    newFunction.put(pp, state);
    return mk(lattice, newFunction);
  }

	/**
	 * Yields a copy of this domain, modified by assuming that the given
	 * expression holds. It is required that the returned domain is in relation
	 * with this one. A safe (but imprecise) implementation of this method can
	 * always return {@code this}.
	 * 
	 * @param expression the expression to assume to hold.
	 * @param src        the program point that where this operation is being
	 *                       evaluated, corresponding to the one that generated
	 *                       the given expression
	 * @param dest       the program point where the execution will move after
	 *                       the expression has been assumed
	 * @param oracle     the oracle for inter-domain communication
	 * 
	 * @return the (optionally) modified copy of this domain
	 * 
	 * @throws SemanticException if an error occurs during the computation
	 */
	public Something assume(SymbolicExpression expression,
			                    ProgramPoint src,
			                    ProgramPoint dest,
			                    SemanticOracle oracle) throws SemanticException {
    Map<ProgramPoint, GenericSetLattice<ProgramPoint>> newFunction = mkNewFunction(function, false);
    return mk(lattice, newFunction);
  }

	/**
	 * Yields {@code true} if this instance is currently tracking abstract
	 * information for the given identifier.
	 * 
	 * @param id the identifier
	 * 
	 * @return whether or not this domain knows about {@code id}
	 */
	public boolean knowsIdentifier(Identifier id) {
    return false;
  }

	/**
	 * Forgets an {@link Identifier}. This means that all information regarding
	 * the given {@code id} will be lost. This method should be invoked whenever
	 * an identifier gets out of scope.
	 * 
	 * @param id the identifier to forget
	 * 
	 * @return the semantic domain without information about the given id
	 * 
	 * @throws SemanticException if an error occurs during the computation
	 */
	public Something forgetIdentifier(Identifier id) throws SemanticException {
    return mk(lattice, function);
  }

	/**
	 * Forgets all {@link Identifier}s that match the given predicate. This
	 * means that all information regarding the those identifiers will be lost.
	 * This method should be invoked whenever an identifier gets out of scope.
	 * 
	 * @param test the test to identify the targets of the removal
	 * 
	 * @return the semantic domain without information about the ids
	 * 
	 * @throws SemanticException if an error occurs during the computation
	 */
	public Something forgetIdentifiersIf(Predicate<Identifier> test) throws SemanticException {
    return mk(lattice, function);
  }
}

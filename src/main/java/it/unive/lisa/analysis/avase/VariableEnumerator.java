package it.unive.lisa.analysis.avase;

import java.util.Set;
import java.util.HashSet;

import jbse.val.Any;
import jbse.val.Simplex;
import jbse.val.Primitive;
import jbse.val.Term;
import jbse.val.Expression;

public class VariableEnumerator {
  private final Set<String> variables;

  public VariableEnumerator() {
    this.variables = new HashSet<>();
  }

  public static Set<String> process(Primitive primitive) {
    VariableEnumerator ve = new VariableEnumerator();
    ve.visit(primitive);
    return ve.variables;
  }

  public void visit(Primitive primitive) {
    if (primitive instanceof Any) {
      // This is `(*)`
    } else if (primitive instanceof Simplex) {
      // This are primitive values: bools, int, float
    } else if (primitive instanceof Expression) {
      Expression expression = (Expression)primitive;
      if (expression.isUnary()) {
        visit(expression.getOperand());
      } else {
        visit(expression.getFirstOperand());
        visit(expression.getSecondOperand());
      }
    } else if (primitive instanceof Term) {
      Term term = (Term) primitive;
      this.variables.add(term.getValue());
    } else {
      throw new AvaseImplException("Unknown primitive: " + primitive.getClass());
    }
  }
}

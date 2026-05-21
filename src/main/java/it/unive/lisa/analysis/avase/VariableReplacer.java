package it.unive.lisa.analysis.avase;

import java.util.Map;

import jbse.val.Any;
import jbse.val.Simplex;
import jbse.val.Primitive;
import jbse.val.Term;
import jbse.val.Expression;

public class VariableReplacer {
  private final Map<String, Primitive> variables;

  public VariableReplacer(Map<String, Primitive> variables) {
    this.variables = variables;
  }

  public Primitive visit(Primitive primitive) {
    if (primitive instanceof Any) {
      // This is `(*)`
      return primitive;
    } else if (primitive instanceof Simplex) {
      // This are primitive values: bools, int, float
      return primitive;
    } else if (primitive instanceof Expression) {
      Expression expression = (Expression)primitive;
      if (expression.isUnary()) {
        return Calculator.unaryExpression(expression.getOperator(), visit(expression.getOperand()));
      } else {
        return Calculator.binaryExpression(visit(expression.getFirstOperand()), expression.getOperator(), visit(expression.getSecondOperand()));
      }
    } else if (primitive instanceof Term) {
      Term term = (Term) primitive;
      String varID = term.getValue();
      if (variables.containsKey(varID)) {
        return variables.get(varID);
      } else {
        return term;
      }
    } else {
      throw new AvaseImplException("Unknown primitive: " + primitive.getClass());
    }
  }

  public static Primitive process(Map<String, Primitive> variables, Primitive primitive) {
    VariableReplacer ve = new VariableReplacer(variables);
    return ve.visit(primitive);
  }
}

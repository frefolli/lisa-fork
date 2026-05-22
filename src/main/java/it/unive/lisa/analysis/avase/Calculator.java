package it.unive.lisa.analysis.avase;

import jbse.val.*;
import jbse.rewr.*;
import jbse.common.exc.*;

public class Calculator {
  private static jbse.val.Calculator CALCULATOR = null;

  public static Primitive simplify(Primitive expr) {
    return getCalculator().simplify(expr);
  }

  public static Primitive makeTrue() {
    try {
      return Simplex.make(Boolean.valueOf(true));
    } catch (Exception e) {
      throw new AvaseImplException(e);
    }
  }

  public static Primitive makeFalse() {
    try {
      return Simplex.make(Boolean.valueOf(false));
    } catch (Exception e) {
      throw new AvaseImplException(e);
    }
  }

  public static Primitive constant(Object value) {
    try {
      return Simplex.make(value);
    } catch (Exception e) {
      throw new AvaseImplException(e);
    }
  }

  public static Primitive variable(char staticType, String variableName) {
    try {
      return getCalculator().valTerm(staticType, variableName);
    } catch (Exception e) {
      throw new AvaseImplException(e);
    }
  }

  public static Primitive unaryExpression(Operator operator, Primitive operand) {
    try {
      return Expression.makeExpressionUnary(operator, operand);
    } catch (Exception e) {
      throw new AvaseImplException(e);
    }
  }

  public static Primitive binaryExpression(Primitive left, Operator operator, Primitive right) {
    try {
      return Expression.makeExpressionBinary(left, operator, right);
    } catch (Exception e) {
      throw new AvaseImplException(e);
    }
  }

  public static Primitive unaryNot(Primitive value) {
    try {
      return Expression.makeExpressionUnary(Operator.NOT, value);
    } catch (Exception e) {
      throw new AvaseImplException(e);
    }
  }

  public static Primitive naryExpression(Operator operator, Primitive ...arguments) {
    if (arguments.length == 0) {
      return makeTrue();
    }
    if (arguments.length == 1) {
      return arguments[0];
    }
    Primitive result = binaryExpression(arguments[0], operator, arguments[1]);
    for (int argi = 2; argi < arguments.length; ++argi) {
      result = binaryExpression(result, operator, arguments[1]);
    }
    return result;
  }

  public static Primitive naryAndExpression(Primitive ...arguments) {
    return naryExpression(Operator.AND, arguments);
  }

  public static Primitive naryOrExpression(Primitive ...arguments) {
    return naryExpression(Operator.OR, arguments);
  }

  public static jbse.val.Calculator getCalculator() {
    if (CALCULATOR == null) {
      CALCULATOR = createCalculator();
    }
    return CALCULATOR;
  }

  private static jbse.val.Calculator createCalculator() {
    final CalculatorRewriting calc = new CalculatorRewriting();
    try {
      // calc.addRewriter(new RewriterExpressionOrConversionOnSimplex());
      // calc.addRewriter(new RewriterFunctionApplicationOnSimplex());
      // calc.addRewriter(new RewriterZeroUnit());
      // calc.addRewriter(new RewriterNegationElimination());
      calc.addRewriter(new RewriterExpressionSorting());
    } catch (UnexpectedInternalException e) {
      throw new RuntimeException(e);
    }
    return calc;
  }
}

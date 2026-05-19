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

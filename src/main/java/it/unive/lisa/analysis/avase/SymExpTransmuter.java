package it.unive.lisa.analysis.avase;
import it.unive.lisa.symbolic.SymbolicExpression;

import it.unive.lisa.symbolic.value.Constant;
import it.unive.lisa.symbolic.value.PushAny;
import it.unive.lisa.symbolic.value.BinaryExpression;
import it.unive.lisa.symbolic.value.UnaryExpression;
import it.unive.lisa.symbolic.value.TernaryExpression;
import it.unive.lisa.symbolic.value.operator.binary.*;
import it.unive.lisa.symbolic.value.operator.unary.*;
import it.unive.lisa.symbolic.value.Variable;

import jbse.val.Any;
import jbse.val.Simplex;
import jbse.val.Primitive;
import jbse.val.Operator;
import jbse.val.Expression;
import jbse.val.HistoryPoint;

public class SymExpTransmuter {
  public static Primitive visitConstant(Constant constant) {
    return Calculator.constant(constant.getValue());
  }

  public static Primitive visitPushAny(PushAny pushany) {
    return Any.make();
  }

  public static Primitive visitVariable(Variable variable) {
    SymbolTable.Variable var = SymbolTable.find(variable.getName());
    if (var  == null) {
      throw new AvaseImplException("Variable " + variable + " was never added to SymbolTable");
    }
    String variableName = var.getName();
    char staticType = 'I';
    try {
      return Calculator.variable(staticType, variableName);
    } catch (Exception e) {
      throw new AvaseImplException(e);
    }
  }

  public static Primitive visitTernaryExpression(TernaryExpression texp) {
    throw new AvaseImplException("TernaryExpression is not supported: " + texp);
  }

  public static Primitive visitBinaryExpression(BinaryExpression binexp) {
    Operator binop = visitBinaryOperator(binexp.getOperator());
    Primitive leftop = visit(binexp.getLeft());
    Primitive rightop = visit(binexp.getRight());
    return Calculator.binaryExpression(leftop, binop, rightop);
  }

  public static Primitive visitUnaryExpression(UnaryExpression monexp) {
    Operator operator = visitUnaryOperator(monexp.getOperator());
    Primitive operand = visit(monexp.getExpression());
    try {
      return Expression.makeExpressionUnary(operator, operand);
    } catch (Exception e) {
      throw new AvaseImplException(e);
    }
  }

  public static Operator visitBinaryOperator(BinaryOperator binop) {
    if (binop instanceof BitwiseAnd) {
      return Operator.ANDBW;
    } else if (binop instanceof BitwiseOr) {
      return Operator.ORBW;
    } else if (binop instanceof BitwiseShiftLeft) {
      // TODO: Check
      return Operator.SHL;
    } else if (binop instanceof BitwiseShiftRight) {
      // TODO: Check
      return Operator.SHR;
    } else if (binop instanceof BitwiseUnsignedShiftRight) {
      // TODO: Check
      return Operator.USHR;
    } else if (binop instanceof BitwiseXor) {
      return Operator.XORBW;
    } else if (binop instanceof ComparisonEq) {
      return Operator.EQ;
    } else if (binop instanceof ComparisonGe) {
      return Operator.GE;
    } else if (binop instanceof ComparisonGt) {
      return Operator.GT;
    } else if (binop instanceof ComparisonLe) {
      return Operator.LE;
    } else if (binop instanceof ComparisonLt) {
      return Operator.LT;
    } else if (binop instanceof ComparisonNe) {
      return Operator.NE;
    } else if (binop instanceof LogicalAnd) {
      return Operator.AND;
    } else if (binop instanceof LogicalOr) {
      return Operator.OR;
    } else if (binop instanceof Numeric16BitAdd) {
      // TODO:
    } else if (binop instanceof Numeric16BitDiv) {
      // TODO:
    } else if (binop instanceof Numeric16BitMod) {
      // TODO:
    } else if (binop instanceof Numeric16BitMul) {
      // TODO:
    } else if (binop instanceof Numeric16BitRem) {
      // TODO:
    } else if (binop instanceof Numeric16BitSub) {
      // TODO:
    } else if (binop instanceof Numeric32BitAdd) {
      // TODO:
    } else if (binop instanceof Numeric32BitDiv) {
      // TODO:
    } else if (binop instanceof Numeric32BitMod) {
      // TODO:
    } else if (binop instanceof Numeric32BitMul) {
      // TODO:
    } else if (binop instanceof Numeric32BitRem) {
      // TODO:
    } else if (binop instanceof Numeric32BitSub) {
      // TODO:
    } else if (binop instanceof Numeric64BitAdd) {
      // TODO:
    } else if (binop instanceof Numeric64BitDiv) {
      // TODO:
    } else if (binop instanceof Numeric64BitMod) {
      // TODO:
    } else if (binop instanceof Numeric64BitMul) {
      // TODO:
    } else if (binop instanceof Numeric64BitRem) {
      // TODO:
    } else if (binop instanceof Numeric64BitSub) {
      // TODO:
    } else if (binop instanceof Numeric8BitAdd) {
      // TODO:
    } else if (binop instanceof Numeric8BitDiv) {
      // TODO:
    } else if (binop instanceof Numeric8BitMod) {
      // TODO:
    } else if (binop instanceof Numeric8BitMul) {
      // TODO:
    } else if (binop instanceof Numeric8BitRem) {
      // TODO:
    } else if (binop instanceof Numeric8BitSub) {
      // TODO:
    } else if (binop instanceof NumericNonOverflowingAdd) {
      return Operator.ADD;
    } else if (binop instanceof NumericNonOverflowingDiv) {
      return Operator.DIV;
    } else if (binop instanceof NumericNonOverflowingMod) {
      return Operator.REM;
    } else if (binop instanceof NumericNonOverflowingMul) {
      return Operator.MUL;
    } else if (binop instanceof NumericNonOverflowingRem) {
      return Operator.REM;
    } else if (binop instanceof NumericNonOverflowingSub) {
      return Operator.SUB;
    } else if (binop instanceof StringConcat) {
      // TODO:
    } else if (binop instanceof StringContains) {
      // TODO:
    } else if (binop instanceof StringEndsWith) {
      // TODO:
    } else if (binop instanceof StringEquals) {
      // TODO:
    } else if (binop instanceof StringIndexOf) {
      // TODO:
    } else if (binop instanceof StringStartsWith) {
      // TODO:
    } else if (binop instanceof TypeCast) {
      // TODO:
    } else if (binop instanceof TypeCheck) {
      // TODO:
    } else if (binop instanceof TypeConv) {
      // TODO:
    }
    throw new AvaseImplException("binop (" + binop.getClass() + ") is not supported yet!");
  }

  public static Operator visitUnaryOperator(UnaryOperator binop) {
    if (binop instanceof BitwiseNegation) {
      return Operator.NOT;
    } else if (binop instanceof LogicalNegation) {
      return Operator.NOT;
    } else if (binop instanceof NumericNegation) {
      return Operator.NEG;
    }
    throw new AvaseImplException("binop (" + binop.getClass() + ") is not supported yet!");
  }

  public static Primitive visit(SymbolicExpression expr) {
    if (expr instanceof Constant) {
      return visitConstant((Constant)expr);
    } else if (expr instanceof PushAny) {
      return visitPushAny((PushAny)expr);
    } else if (expr instanceof BinaryExpression) {
      return visitBinaryExpression((BinaryExpression)expr);
    } else if (expr instanceof UnaryExpression) {
      return visitUnaryExpression((UnaryExpression)expr);
    } else if (expr instanceof TernaryExpression) {
      return visitTernaryExpression((TernaryExpression)expr);
    } else if (expr instanceof Variable) {
      return visitVariable((Variable)expr);
    } else {
    throw new AvaseImplException(expr.getClass() + " is not supported yet!");
    }
  }
}

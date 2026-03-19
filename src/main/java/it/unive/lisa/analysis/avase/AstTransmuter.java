package it.unive.lisa.analysis.avase;
import it.unive.lisa.program.cfg.statement.*;
import it.unive.lisa.program.cfg.statement.comparison.*;
import it.unive.lisa.program.cfg.statement.logic.*;
import it.unive.lisa.program.cfg.statement.numeric.*;
import it.unive.lisa.program.cfg.statement.literal.*;
import it.unive.lisa.program.cfg.ProgramPoint;
import it.unive.lisa.program.cfg.statement.evaluation.EvaluationOrder;

import jbse.val.Any;
import jbse.val.Simplex;
import jbse.val.Primitive;
import jbse.val.Operator;
import jbse.val.HistoryPoint;

public class AstTransmuter {
  public static Primitive visitLiteral(Literal literal) {
    return Calculator.constant(literal.getValue());
  }

  public static Primitive visitVariableRef(VariableRef variable) {
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

  public static Primitive visitUnaryExpression(UnaryExpression monexp) {
    // TODO: Ignoring ORDER
    Primitive operand = visit(monexp.getSubExpression());

    Operator operator = null;
    if (monexp instanceof Not) {
      operator = Operator.NOT;
    } else if (monexp instanceof Negation) {
      operator = Operator.NEG;
    }
    if (operator == null) {
      throw new AvaseImplException("Unknown UnaryExpression: " + monexp.getClass());
    }

    try {
      return jbse.val.Expression.makeExpressionUnary(operator, operand);
    } catch (Exception e) {
      throw new AvaseImplException(e);
    }
  }

  public static Primitive visitBinaryExpression(BinaryExpression binexp) {
    // TODO: Ignoring ORDER
    Primitive left = visit(binexp.getLeft());
    Primitive right = visit(binexp.getRight());

    Operator operator = null;
    if (binexp instanceof Equal) {
      operator = Operator.EQ;
    } else if (binexp instanceof GreaterOrEqual) {
      operator = Operator.GE;
    } else if (binexp instanceof GreaterThan) {
      operator = Operator.GT;
    } else if (binexp instanceof LessOrEqual) {
      operator = Operator.LE;
    } else if (binexp instanceof LessThan) {
      operator = Operator.LT;
    } else if (binexp instanceof NotEqual) {
      operator = Operator.NE;
    } else if (binexp instanceof And) {
      operator = Operator.AND;
    } else if (binexp instanceof Or) {
      operator = Operator.OR;
    } else if (binexp instanceof Addition) {
      operator = Operator.ADD;
    } else if (binexp instanceof Division) {
      operator = Operator.DIV;
    } else if (binexp instanceof Modulo) {
      operator = Operator.REM;
    } else if (binexp instanceof Multiplication) {
      operator = Operator.MUL;
    } else if (binexp instanceof Remainder) {
      operator = Operator.REM;
    } else if (binexp instanceof Subtraction) {
      operator = Operator.SUB;
    }
    if (operator == null) {
      throw new AvaseImplException("Unknown BinaryExpression: " + binexp.getClass());
    }

    try {
      return jbse.val.Expression.makeExpressionBinary(left, operator, right);
    } catch (Exception e) {
      throw new AvaseImplException(e);
    }
  }

  public static Primitive visitTernaryExpression(TernaryExpression texp) {
    throw new AvaseImplException("TernaryExpression are not supported: " + texp.getClass());
  }

  public static Primitive visitExpression(Expression expr) {
    if (expr instanceof UnaryExpression) {
      return visitUnaryExpression((UnaryExpression)expr);
    } else if (expr instanceof BinaryExpression) {
      return visitBinaryExpression((BinaryExpression)expr);
    } else if (expr instanceof TernaryExpression) {
      return visitTernaryExpression((TernaryExpression)expr);
    } else if (expr instanceof VariableRef) {
      return visitVariableRef((VariableRef)expr);
    } else if (expr instanceof Literal) {
      return visitLiteral((Literal)expr);
    } else {
      throw new AvaseImplException("Unknown Expression: " + expr.getClass());
    }
  }

  public static Primitive visit(it.unive.lisa.program.cfg.ProgramPoint pp) {
    if (pp instanceof Expression) {
      return visitExpression((Expression)pp);
    } else {
      throw new AvaseImplException("Unknown ProgramPoint: " + pp.getClass());
    }
  }
}

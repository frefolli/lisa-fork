package it.unive.lisa;
import it.unive.lisa.analysis.avase.Calculator;
import jbse.val.Operator;
import jbse.val.Primitive;

import org.sosy_lab.java_smt.SolverContextFactory;
import org.sosy_lab.java_smt.api.*;

public class Test {
  public static void main(String[] args) throws Exception {
    smt();
  }

  public static void jbse() throws Exception {
    Primitive x = Calculator.variable('I', "x");
    Primitive y = Calculator.variable('I', "y");
    Primitive x_gt_e = Calculator.binaryExpression(x, Operator.GT, y);
    Primitive not_x_gt_e = Calculator.unaryExpression(Operator.NOT, x_gt_e);
    Primitive expr = Calculator.binaryExpression(
        x_gt_e,
        Operator.OR,
        Calculator.binaryExpression(
          not_x_gt_e,
          Operator.OR,
          x_gt_e
        )
    );
    
    Primitive simp = Calculator.simplify(expr);
    System.out.println("[MAIN]('" + simp + "' = SIMPLIFY('" + expr + "'))");
  }

  public static void smt() throws Exception {
    // create solver context (using Z3)
    SolverContext context =
      SolverContextFactory.createSolverContext(
          SolverContextFactory.Solvers.Z3);

    FormulaManager fmgr = context.getFormulaManager();
    BooleanFormulaManager bmgr = fmgr.getBooleanFormulaManager();
    NumeralFormulaManager nmgr = fmgr.getIntegerFormulaManager();

    // create variables
    NumeralFormula x = nmgr.makeVariable("x");
    NumeralFormula y = nmgr.makeVariable("y");

    // create comparison
    BooleanFormula xGreaterThanY = nmgr.greaterThan(x, y);

    // build the expression
    BooleanFormula cond =
      bmgr.and(
      bmgr.or(
          bmgr.not(xGreaterThanY),
          bmgr.or(
            xGreaterThanY,
            bmgr.not(xGreaterThanY)
            )
          ),
      xGreaterThanY);

    System.out.println("Expression:");
    System.out.println(cond);

    cond = fmgr.simplify(cond);
    System.out.println("Rewritten:");
    System.out.println(cond);
  }
}

package jbse.rewr;

import jbse.common.exc.UnexpectedInternalException;
import jbse.val.Expression;
import jbse.val.Operator;
import jbse.val.Primitive;
import jbse.val.exc.InvalidOperandException;
import jbse.val.exc.InvalidTypeException;
import jbse.val.exc.NoResultException;

/**
 * Rewrites all the {@link Expression}s by attempting negation elimination.
 *
 * @author Pietro Braione
 */
public final class RewriterExpressionSorting extends RewriterCalculatorRewriting {
    public RewriterExpressionSorting() { }

    @Override
    protected void rewriteExpression(Expression expr) throws NoResultException {
      if (expr.isBinary()) {
        Primitive left = rewrite(expr.getFirstOperand());
        Primitive right = rewrite(expr.getSecondOperand());

        int leftHash = left.hashCode();
        int rightHash = right.hashCode();
        // System.out.println(left + " = " + leftHash);
        // System.out.println(right + " = " + rightHash);
        setResult(expr);
      } else {
        setResult(expr);
      }
    }
}

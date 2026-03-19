package it.unive.lisa.analysis.avase;

import java.util.Map;

import it.unive.lisa.analysis.ScopeToken;
import it.unive.lisa.analysis.SemanticException;
import it.unive.lisa.analysis.Lattice;
import it.unive.lisa.analysis.BaseLattice;
import it.unive.lisa.program.cfg.ProgramPoint;
import it.unive.lisa.program.cfg.edge.Edge;
import it.unive.lisa.program.cfg.statement.Statement;
import it.unive.lisa.symbolic.SymbolicExpression;
import it.unive.lisa.symbolic.value.Identifier;
import it.unive.lisa.symbolic.value.ValueExpression;
import it.unive.lisa.util.representation.StringRepresentation;
import it.unive.lisa.util.representation.StructuredRepresentation;
import it.unive.lisa.program.cfg.statement.comparison.*;

import it.unive.lisa.program.cfg.controlFlow.*;
import it.unive.lisa.program.cfg.CFG;

import jbse.val.exc.InvalidOperandException;
import jbse.val.exc.InvalidOperatorException;
import jbse.val.exc.InvalidTypeException;

import jbse.val.Simplex;
import jbse.val.Primitive;
import jbse.val.Operator;
import jbse.val.Expression;

public class BranchController {
  public static boolean isBranchConditionTrue(ProgramPoint condition, ProgramPoint branch) {
    CFG cfg = condition.getCFG();
    ControlFlowStructure cfs = cfg.getControlFlowStructureOf(condition);
    if (cfs instanceof IfThenElse) {
      IfThenElse stmt = (IfThenElse)cfs;
      if (stmt.getTrueBranch().contains((Statement)branch)) {
        return true;
      } else if (stmt.getFalseBranch().contains((Statement)branch)) {
        return false;
      } else {
        throw new AvaseImplException("Something is off with " + stmt);
      }
    } else if (cfs instanceof Loop) {
      Loop stmt = (Loop)cfs;
      if (stmt.contains((Statement)branch)) {
        return true;
      } else if (stmt.getFirstFollower().equals(branch)) {
        return false;
      } else {
        throw new AvaseImplException("Something is off with " + stmt);
      }
    } else {
      throw new AvaseImplException(cfg.getClass() + " is not a supported ControlFlowStructure");
    }
  }

  public static boolean isBranchConditionFalse(ProgramPoint condition, ProgramPoint branch) {
    return !isBranchConditionTrue(condition, branch);
  }
}

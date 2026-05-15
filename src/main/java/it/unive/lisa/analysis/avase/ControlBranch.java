package it.unive.lisa.analysis.avase;

import it.unive.lisa.program.Program;
import it.unive.lisa.program.cfg.ProgramPoint;
import java.util.Set;
import java.util.HashSet;
import java.util.Map;

import it.unive.lisa.program.Program;
import it.unive.lisa.program.Unit;
import it.unive.lisa.program.ClassUnit;
import it.unive.lisa.program.cfg.CFG;
import it.unive.lisa.program.cfg.edge.Edge;
import it.unive.lisa.program.cfg.statement.Statement;
import it.unive.lisa.program.cfg.CodeMember;
import it.unive.lisa.program.cfg.CodeMemberDescriptor;
import it.unive.lisa.program.cfg.controlFlow.*;

public class ControlBranch extends ProgramVisitor {
  public static void computeAll(Program program) {
    ControlBranch computer = new ControlBranch();
    computer.visit(program);
  }

  public void initializeStates(CFG cfg) {
    Set<ProgramPoint> nodes = DataflowStateMap.getCFGMap().get(cfg);
    Map<ProgramPoint, Branch> function = DataflowStateMap.getControlBranchMap();
    for (ProgramPoint pp : nodes) {
      function.put(pp, new Branch());
    }
  }

  public void visitCFG(CFG cfg) {
    initializeStates(cfg);

    Map<ProgramPoint, Branch> function = DataflowStateMap.getControlBranchMap();
    Set<ProgramPoint> nodes = DataflowStateMap.getCFGMap().get(cfg);
    for (ProgramPoint pp : nodes) {
      Branch state = compute(cfg, pp);
      function.put(pp, state);
    }
  }

  public Branch compute(CFG cfg, ProgramPoint sink) {
    for (Edge edge : cfg.getIngoingEdges((Statement)sink)) {
      ProgramPoint source = edge.getSource();
      ControlFlowStructure cfs = cfg.getControlFlowStructureOf(source);
      if (cfs != null) {
        if (cfs instanceof IfThenElse) {
          return computeIfElse(cfs, source, sink);
        } else if (cfs instanceof Loop) {
          return computeLoop(cfs, source, sink);
        } else {
          throw new AvaseImplException(cfg.getClass() + " is not a supported ControlFlowStructure");
        }
      }
    }
    return new Branch();
  }

  private Branch computeIfElse(ControlFlowStructure cfs, ProgramPoint condition, ProgramPoint sink) {
    IfThenElse stmt = (IfThenElse)cfs;
    if (stmt.getTrueBranch().contains((Statement)sink)) {
      return new Branch(condition, Boolean.valueOf(true));
    } else if (stmt.getFalseBranch().contains((Statement)sink)) {
      return new Branch(condition, Boolean.valueOf(false));
    } else {
      return new Branch();
    }
  }

  private Branch computeLoop(ControlFlowStructure cfs, ProgramPoint condition, ProgramPoint sink) {
    Loop stmt = (Loop)cfs;
    if (stmt.contains((Statement)sink)) {
      return new Branch(condition, Boolean.valueOf(true));
    } else if (stmt.getFirstFollower().equals(sink)) {
      return new Branch(condition, Boolean.valueOf(false));
    } else {
      throw new AvaseImplException("Something is off with " + stmt + ". Condition = " + condition + " and Sink = " + sink);
    }
  }
}

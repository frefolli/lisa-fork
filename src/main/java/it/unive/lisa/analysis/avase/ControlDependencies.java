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

public class ControlDependencies extends ProgramVisitor {
  public static void computeAll(Program program) {
    ControlDependencies computer = new ControlDependencies();
    computer.visit(program);
  }

  public void initializeStates(CFG cfg) {
    Set<ProgramPoint> nodes = DataflowStateMap.getCFGMap().get(cfg);
    Map<ProgramPoint, Set<Branch>> function = DataflowStateMap.getControlDependenciesMap();
    for (ProgramPoint pp : nodes) {
      function.put(pp, new HashSet<>());
    }
  }

  public void visitCFG(CFG cfg) {
    initializeStates(cfg);

    Map<ProgramPoint, Set<Branch>> function = DataflowStateMap.getControlDependenciesMap();
    Set<ProgramPoint> nodes = DataflowStateMap.getCFGMap().get(cfg);
    for (ProgramPoint pp : nodes) {
      Set<Branch> state = compute(cfg, pp);
      function.put(pp, state);
    }
  }

  public Set<Branch> compute(CFG cfg, ProgramPoint n) {
    Map<ProgramPoint, Set<ProgramPoint>> POF = DataflowStateMap.getPostDominanceFrontierMap();
    Map<ProgramPoint, Set<ProgramPoint>> POD = DataflowStateMap.getPostDominatorsMap();
    Map<ProgramPoint, Branch> CB = DataflowStateMap.getControlBranchMap();
    Set<Branch> state = new HashSet<>();
    for (ProgramPoint d : POF.get(n)) {
      for (Edge edge : cfg.getOutgoingEdges((Statement)d)) {
        ProgramPoint s = edge.getDestination();
        if (POD.get(s).contains(n)) {
          if (CB.get(s).choice == null) {
            throw new AvaseImplException("proc: " + d.getCFG().getDescriptor().getFullName() + "; d = {" + DataflowStateMap.labelize(d) + "}; s = {" + DataflowStateMap.labelize(s) + "}; n = {" + DataflowStateMap.labelize(n) + "}");
          } else {
            state.add(new Branch(d, CB.get(s).choice));
          }
        }
      }
    }
    return state;
  }
}

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

public class ImmediatePostDominators extends ProgramVisitor {
  public static void computeAll(Program program) {
    ImmediatePostDominators computer = new ImmediatePostDominators();
    computer.visit(program);
  }

  public void initializeStates(CFG cfg) {
    Map<ProgramPoint, Set<ProgramPoint>> function = DataflowStateMap.getImmediatePostDominatorsMap();
    Set<ProgramPoint> nodes = DataflowStateMap.getCFGMap().get(cfg);
    for (ProgramPoint pp : nodes) {
      function.put(pp, new HashSet<>());
    }
  }

  public void visitCFG(CFG cfg) {
    initializeStates(cfg);

    Set<ProgramPoint> nodes = DataflowStateMap.getCFGMap().get(cfg);
    Map<ProgramPoint, Set<ProgramPoint>> function = DataflowStateMap.getImmediatePostDominatorsMap();
    for (ProgramPoint pp : nodes) {
      Set<ProgramPoint> state = compute(cfg, pp);
      function.put(pp, state);
    }
  }

  public Set<ProgramPoint> compute(CFG cfg, ProgramPoint pp) {
    Map<ProgramPoint, Set<ProgramPoint>> POD = DataflowStateMap.getPostDominatorsMap();
    Set<ProgramPoint> PODn = new HashSet<ProgramPoint>(POD.get(pp));
    PODn.remove(pp);
    Set<ProgramPoint> state = new HashSet<ProgramPoint>(PODn);
    for (ProgramPoint peer : PODn) {
      Set<ProgramPoint> PODpeer = new HashSet<ProgramPoint>(POD.get(peer));
      PODpeer.remove(peer);
      state.removeAll(PODpeer);
    }
    return state;
  }
}

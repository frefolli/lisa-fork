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

public class ImmediatePreDominators extends ProgramVisitor {
  public static void computeAll(Program program) {
    ImmediatePreDominators computer = new ImmediatePreDominators();
    computer.visit(program);
  }

  public void initializeStates(CFG cfg) {
    Set<ProgramPoint> nodes = DataflowStateMap.getCFGMap().get(cfg);
    Map<ProgramPoint, Set<ProgramPoint>> function = DataflowStateMap.getImmediatePreDominatorsMap();
    for (ProgramPoint pp : nodes) {
      function.put(pp, new HashSet<>());
    }
  }

  public void visitCFG(CFG cfg) {
    initializeStates(cfg);

    Set<ProgramPoint> nodes = DataflowStateMap.getCFGMap().get(cfg);
    Map<ProgramPoint, Set<ProgramPoint>> function = DataflowStateMap.getImmediatePreDominatorsMap();
    for (ProgramPoint pp : nodes) {
      Set<ProgramPoint> state = compute(cfg, pp);
      function.put(pp, state);
    }
  }

  public Set<ProgramPoint> compute(CFG cfg, ProgramPoint pp) {
    Map<ProgramPoint, Set<ProgramPoint>> PED = DataflowStateMap.getPreDominatorsMap();
    Set<ProgramPoint> PEDn = new HashSet<ProgramPoint>(PED.get(pp));
    PEDn.remove(pp);
    Set<ProgramPoint> state = new HashSet<ProgramPoint>(PEDn);
    for (ProgramPoint peer : PEDn) {
      Set<ProgramPoint> PEDpeer = new HashSet<ProgramPoint>(PED.get(peer));
      PEDpeer.remove(peer);
      state.removeAll(PEDpeer);
    }
    return state;
  }
}

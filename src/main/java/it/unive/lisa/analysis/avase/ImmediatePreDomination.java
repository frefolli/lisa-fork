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

public class ImmediatePreDomination extends ProgramVisitor {
  public static void computeAll(Program program) {
    ImmediatePreDomination computer = new ImmediatePreDomination();
    computer.visit(program);
  }

  public void initializeStates(CFG cfg) {
    Map<ProgramPoint, Set<ProgramPoint>> function = DataflowStateMap.getImmediatePreDominationMap();
    Set<ProgramPoint> nodes = DataflowStateMap.getCFGMap().get(cfg);
    for (ProgramPoint pp : nodes) {
      function.put(pp, new HashSet<>());
    }
  }

  public void visitCFG(CFG cfg) {
    initializeStates(cfg);

    Map<ProgramPoint, Set<ProgramPoint>> function = DataflowStateMap.getImmediatePreDominationMap();
    Map<ProgramPoint, Set<ProgramPoint>> IPED = DataflowStateMap.getImmediatePreDominatorsMap();
    Set<ProgramPoint> nodes = DataflowStateMap.getCFGMap().get(cfg);
    for (ProgramPoint pp : nodes) {
      for (ProgramPoint peer : IPED.get(pp)) {
        function.get(peer).add(pp);
      }
    }
  }
}

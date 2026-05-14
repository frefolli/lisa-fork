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

public class ImmediatePostDomination extends ProgramVisitor {
  public static void computeAll(Program program) {
    ImmediatePostDomination computer = new ImmediatePostDomination();
    computer.visit(program);
  }

  public void initializeStates(CFG cfg) {
    Map<ProgramPoint, Set<ProgramPoint>> function = DataflowStateMap.getImmediatePostDominationMap();
    Map<ProgramPoint, Set<ProgramPoint>> IPOD = DataflowStateMap.getImmediatePostDominatorsMap();
    for (ProgramPoint pp : IPOD.keySet()) {
      function.put(pp, new HashSet<>());
    }
  }

  public void visitCFG(CFG cfg) {
    initializeStates(cfg);

    Map<ProgramPoint, Set<ProgramPoint>> function = DataflowStateMap.getImmediatePostDominationMap();
    Map<ProgramPoint, Set<ProgramPoint>> IPOD = DataflowStateMap.getImmediatePostDominatorsMap();
    for (ProgramPoint pp : function.keySet()) {
      for (ProgramPoint peer : IPOD.get(pp)) {
        function.get(peer).add(pp);
      }
    }
  }
}

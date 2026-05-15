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

public class PreDominators extends ProgramVisitor {
  public static void computeAll(Program program) {
    PreDominators computer = new PreDominators();
    computer.visit(program);
  }

  public void initializeStates(CFG cfg) {
    Map<ProgramPoint, Set<ProgramPoint>> function = DataflowStateMap.getPreDominatorsMap();
    Set<ProgramPoint> nodes = DataflowStateMap.getCFGMap().get(cfg);
    for (ProgramPoint pp : nodes) {
      boolean is_entrypoint = cfg.getEntrypoints().contains(pp);
      Set<ProgramPoint> state = new HashSet<ProgramPoint>();
      if (!is_entrypoint) {
        for (ProgramPoint p : cfg.getNodes()) {
          state.add(p);
        }
      }
      function.put(pp, state);
    }
  }

  public void visitCFG(CFG cfg) {
    initializeStates(cfg);

    Map<ProgramPoint, Set<ProgramPoint>> function = DataflowStateMap.getPreDominatorsMap();
    Set<ProgramPoint> nodes = DataflowStateMap.getCFGMap().get(cfg);
    boolean changed = true;
    while (changed) {
      changed = false;
      for (ProgramPoint pp : nodes) {
        Set<ProgramPoint> old_state = function.get(pp);
        Set<ProgramPoint> new_state = compute(cfg, pp);
        if (!old_state.equals(new_state)) {
          changed = true;
          function.put(pp, new_state);
        }
      }
    }
  }

  public Set<ProgramPoint> compute(CFG cfg, ProgramPoint pp) {
    Map<ProgramPoint, Set<ProgramPoint>> function = DataflowStateMap.getPreDominatorsMap();
    Set<ProgramPoint> state = null;
    for (Edge edge : cfg.getIngoingEdges((Statement)pp)) {
      ProgramPoint peer = edge.getSource();
      Set<ProgramPoint> peer_state = function.get(peer);
      if (state == null) {
        state = new HashSet(peer_state);
      } else {
        state.retainAll(peer_state);
      }
    }
    if (state == null) {
      state = new HashSet<ProgramPoint>();
    }
    state.add(pp);
    return state;
  }
}

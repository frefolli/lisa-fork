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

import java.io.Writer;

public class PreDominanceFrontier extends ProgramVisitor {
  public static void computeAll(Program program) {
    PreDominanceFrontier computer = new PreDominanceFrontier();
    computer.visit(program);
  }

  public void initializeStates(CFG cfg) {
    Set<ProgramPoint> nodes = DataflowStateMap.getCFGMap().get(cfg);
    Map<ProgramPoint, Set<ProgramPoint>> function = DataflowStateMap.getPreDominanceFrontierMap();
    for (ProgramPoint pp : nodes) {
      Set<ProgramPoint> state = new HashSet<ProgramPoint>();
      function.put(pp, state);
    }
  }

  public void visitCFG(CFG cfg) {
    initializeStates(cfg);

    Set<ProgramPoint> nodes = DataflowStateMap.getCFGMap().get(cfg);
    Map<ProgramPoint, Set<ProgramPoint>> function = DataflowStateMap.getPreDominanceFrontierMap();
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

  public Set<ProgramPoint> computeFirstPass(CFG cfg, ProgramPoint n) {
    Map<ProgramPoint, Set<ProgramPoint>> IPED = DataflowStateMap.getImmediatePreDominatorsMap();
    Set<ProgramPoint> state = new HashSet();
    for (Edge edge : cfg.getOutgoingEdges((Statement)n)) {
      ProgramPoint d = edge.getDestination();
      if (!IPED.get(d).contains(n)) {
        state.add(d);
      }
    }
    return state;
  }

  public Set<ProgramPoint> computeSecondPass(CFG cfg, ProgramPoint n) {
    Map<ProgramPoint, Set<ProgramPoint>> IPED = DataflowStateMap.getImmediatePreDominatorsMap();
    Map<ProgramPoint, Set<ProgramPoint>> rIPED = DataflowStateMap.getImmediatePreDominationMap();
    Map<ProgramPoint, Set<ProgramPoint>> function = DataflowStateMap.getPreDominanceFrontierMap();
    Set<ProgramPoint> state = new HashSet();

    for (ProgramPoint z : rIPED.get(n)) {
      for (ProgramPoint d : function.get(z)) {
        if (!IPED.get(d).contains(n)) {
          state.add(d);
        }
      }
    }
    return state;
  }

  public Set<ProgramPoint> compute(CFG cfg, ProgramPoint pp) {
    Set<ProgramPoint> state = computeFirstPass(cfg, pp);
    state.addAll(computeSecondPass(cfg, pp));
    return state;
  }
}

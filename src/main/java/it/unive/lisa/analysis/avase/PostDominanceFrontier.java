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

public class PostDominanceFrontier extends ProgramVisitor {
  public static void computeAll(Program program) {
    PostDominanceFrontier computer = new PostDominanceFrontier();
    computer.visit(program);
  }

  public void initializeStates(CFG cfg) {
    Map<ProgramPoint, Set<ProgramPoint>> function = DataflowStateMap.getPostDominanceFrontierMap();
    for (ProgramPoint pp : cfg.getNodes()) {
      boolean is_statement = (pp instanceof Statement);
      if (is_statement) {
        boolean has_outgoing_edges = !cfg.getOutgoingEdges((Statement)pp).isEmpty();
        boolean is_exitpoint = cfg.getNormalExitpoints().contains(pp);
        if (has_outgoing_edges || is_exitpoint) {
          Set<ProgramPoint> state = new HashSet<ProgramPoint>();
          function.put(pp, state);
        }
      }
    }
  }

  public void visitCFG(CFG cfg) {
    initializeStates(cfg);

    Map<ProgramPoint, Set<ProgramPoint>> function = DataflowStateMap.getPostDominanceFrontierMap();
    boolean changed = true;
    while (changed) {
      changed = false;
      for (ProgramPoint pp : function.keySet()) {
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
    Map<ProgramPoint, Set<ProgramPoint>> IPOD = DataflowStateMap.getImmediatePostDominatorsMap();
    Set<ProgramPoint> state = new HashSet();
    for (Edge edge : cfg.getIngoingEdges((Statement)n)) {
      ProgramPoint d = edge.getSource();
      if (!IPOD.get(d).contains(n)) {
        state.add(d);
      }
    }
    return state;
  }

  public Set<ProgramPoint> computeSecondPass(CFG cfg, ProgramPoint n) {
    Map<ProgramPoint, Set<ProgramPoint>> IPOD = DataflowStateMap.getImmediatePostDominatorsMap();
    Map<ProgramPoint, Set<ProgramPoint>> rIPOD = DataflowStateMap.getImmediatePostDominationMap();
    Map<ProgramPoint, Set<ProgramPoint>> function = DataflowStateMap.getPostDominanceFrontierMap();
    Set<ProgramPoint> state = new HashSet();

    for (ProgramPoint z : rIPOD.get(n)) {
      for (ProgramPoint d : function.get(z)) {
        if (!IPOD.get(d).contains(n)) {
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

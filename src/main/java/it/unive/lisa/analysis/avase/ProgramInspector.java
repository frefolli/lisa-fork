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

public class ProgramInspector extends ProgramVisitor {
  public static void computeAll(Program program) {
    ProgramInspector computer = new ProgramInspector();
    computer.visit(program);
  }

  public void visitCFG(CFG cfg) {
    Map<CFG, Set<ProgramPoint>> cfgMap = DataflowStateMap.getCFGMap();
    Map<ProgramPoint, String> labellingMap = DataflowStateMap.getLabellingMap();

    Set<ProgramPoint> nodes = new HashSet<>();

    for (ProgramPoint pp : cfg.getNodes()) {
      if (isStatement(cfg, pp)) {
        assert !labellingMap.containsKey(pp);
        String ID = (1 + labellingMap.size()) + "";
        labellingMap.put(pp, ID);
        nodes.add(pp);
      } else {
        System.out.println("Excluded: " + pp);
      }
    }
    cfgMap.put(cfg, nodes);
  }

  private boolean isStatement(CFG cfg, ProgramPoint pp) {
    boolean is_statement = (pp instanceof Statement);
    if (is_statement) {
      boolean has_outgoing_edges = !cfg.getOutgoingEdges((Statement)pp).isEmpty();
      boolean has_ingoing_edges = !cfg.getIngoingEdges((Statement)pp).isEmpty();
      boolean is_exitpoint = cfg.getNormalExitpoints().contains(pp);
      boolean is_entrypoint = cfg.getEntrypoints().contains(pp);
      if (has_outgoing_edges || is_exitpoint || has_ingoing_edges || is_entrypoint) {
        return true;
      }
    }
    return false;
  }
}

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
    Map<String, Set<ProgramPoint>> function = DataflowStateMap.getCFGMap();
    CodeMemberDescriptor descriptor = cfg.getDescriptor();
    String ID = descriptor.getFullName();
    function.put(ID, new HashSet<>(cfg.getNodes()));
  }
}

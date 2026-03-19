package it.unive.lisa;
import it.unive.lisa.analysis.avase.AvaseImplException;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

import it.unive.lisa.program.Program;
import it.unive.lisa.program.Unit;
import it.unive.lisa.program.ClassUnit;
import it.unive.lisa.program.cfg.CFG;
import it.unive.lisa.program.cfg.edge.Edge;
import it.unive.lisa.program.cfg.statement.Statement;
import it.unive.lisa.program.cfg.CodeMember;
import it.unive.lisa.program.cfg.CodeMemberDescriptor;
import it.unive.lisa.program.cfg.controlFlow.*;

public class ProgramInspector {
  public static void visit(Unit unit) {
    if (unit instanceof Program) {
      ProgramInspector.visitProgram((Program)unit);
    } else if (unit instanceof ClassUnit) {
      ProgramInspector.visitClassUnit((ClassUnit)unit);
    } else {
      throw new AvaseImplException("Uknown unit class: " + unit.getClass());
    }
  }

  public static void visitProgram(Program program) {
    for (Map.Entry<String, Unit> entry : program.getUnitsEx()) {
      ProgramInspector.visit(entry.getValue());
    }
  }

  public static void visitClassUnit(ClassUnit classUnit) {
    for (CodeMember codeMember : classUnit.getCodeMembersRecursively()) {
      ProgramInspector.visitCodeMember(codeMember);
    }
  }

  public static void visitCodeMember(CodeMember codeMember) {
    if (codeMember instanceof CFG) {
      ProgramInspector.visitCFG((CFG)codeMember);
    } else {
      throw new AvaseImplException("Uknown CodeMember class: " + codeMember.getClass());
    }
  }

  public static void visitCFG(CFG cfg) {
    CodeMemberDescriptor descriptor = cfg.getDescriptor();
    System.out.println("fn " + descriptor.getFullName());
    // System.out.println("classes: {");
    // System.out.println("  if: {");
    // System.out.println("    style: {");
    // System.out.println("      stroke-width: 0");
    // System.out.println("      fill: \"#44C7B1\"");
    // System.out.println("      shadow: true");
    // System.out.println("      border-radius: 5");
    // System.out.println("    }");
    // System.out.println("  }");
    // System.out.println("  while: {");
    // System.out.println("    style: {");
    // System.out.println("      fill: \"#FE7070\"");
    // System.out.println("      stroke: \"#F69E03\"");
    // System.out.println("    }");
    // System.out.println("  }");
    // System.out.println("}");

    Map<Statement, Integer> context = new HashMap<>();
    for (Statement statement : cfg.getEntrypoints()) {
      ProgramInspector.visitStatement(context, cfg, statement);
    }
  }

  public static void visitStatement(Map<Statement, Integer> context, CFG cfg, Statement statement) {
    if (context.containsKey(statement)) {
      return;
    }
    Integer id = Integer.valueOf(context.size());
    context.put(statement, id);
    String title = statement.toString();
    String klass = "statement";
    ControlFlowStructure cfs = cfg.getControlFlowStructureOf(statement);
    if (cfg != null) {
      if (cfs instanceof IfThenElse) {
        klass = "if";
      } else if (cfs instanceof Loop) {
        klass = "while";
      }
    }

    System.out.println("s" + id + ": \"" + title + "\" {class: " + klass + "}");
    for (Edge edge : cfg.getOutgoingEdges(statement)) {
      Statement sink = edge.getDestination();
      visitStatement(context, cfg, sink);
    System.out.println("s" + id + " -> s" + context.get(sink));
    }
  }
}

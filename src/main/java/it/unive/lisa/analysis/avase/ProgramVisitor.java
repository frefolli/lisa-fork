package it.unive.lisa.analysis.avase;

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

public class ProgramVisitor {
  public void visit(Unit unit) {
    if (unit instanceof Program) {
      this.visitProgram((Program)unit);
    } else if (unit instanceof ClassUnit) {
      this.visitClassUnit((ClassUnit)unit);
    } else {
      throw new AvaseImplException("Uknown unit class: " + unit.getClass());
    }
  }

  public void visitProgram(Program program) {
    for (Map.Entry<String, Unit> entry : program.getUnitsEx()) {
      this.visit(entry.getValue());
    }
  }

  public void visitClassUnit(ClassUnit classUnit) {
    for (CodeMember codeMember : classUnit.getCodeMembersRecursively()) {
      this.visitCodeMember(codeMember);
    }
  }

  public void visitCodeMember(CodeMember codeMember) {
    if (codeMember instanceof CFG) {
      this.visitCFG((CFG)codeMember);
    } else {
      throw new AvaseImplException("Uknown CodeMember class: " + codeMember.getClass());
    }
  }

  public void visitCFG(CFG cfg) {
    CodeMemberDescriptor descriptor = cfg.getDescriptor();
    System.out.println("fn " + descriptor.getFullName());
  }
}

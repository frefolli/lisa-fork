package it.unive.lisa.analysis.avase;

import jbse.val.Primitive;
import jbse.val.Operator;
import it.unive.lisa.program.cfg.ProgramPoint;
import java.util.Map;
import java.util.Set;
import it.unive.lisa.logging.Logger;

public class DataDependencyPathConditionComputer {
  public static Primitive process(DefinitionCombination combination) {
    Primitive[] conditions = new Primitive[combination.symbolicValues.size()];
    int idx = 0;
    for (SymbolicValue symbolicValue : combination.symbolicValues.values()) {
      conditions[idx] = symbolicValue.condition;
      idx += 1;
    }
    return Calculator.naryAndExpression(conditions);
  }
}

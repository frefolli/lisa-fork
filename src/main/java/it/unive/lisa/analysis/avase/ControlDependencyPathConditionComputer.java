package it.unive.lisa.analysis.avase;

import jbse.val.Primitive;
import jbse.val.Operator;
import it.unive.lisa.program.cfg.ProgramPoint;
import java.util.Map;
import java.util.Set;
import it.unive.lisa.logging.Logger;

public class ControlDependencyPathConditionComputer {
  public static Primitive process(ProgramPoint programPoint) {
    Map<ProgramPoint, Set<Branch>> CD = DataflowStateMap.getControlDependenciesMap();
    Map<ProgramPoint, AllValues> AV = DataflowStateMap.getAllValuesMap();

    if (!CD.containsKey(programPoint)) {
      throw new AvaseImplException("ControlDependencyPathConditionComputer cannot compute PC_{cdep} for programPoint = (" + DataflowStateMap.labelize(programPoint) + ") which is not in CD Application!");
    }

    Primitive pathCondition = null;
    for (Branch branch : CD.get(programPoint)) {
      ProgramPoint condition = branch.condition;
      Boolean label = branch.choice;
      if (!AV.containsKey(condition)) {
        Logger.logWarn("PC_{cdep} of programPoint = (" + DataflowStateMap.labelize(programPoint) + ") depends on AV[" + DataflowStateMap.labelize(condition) + "] which however is not already defined.");
      } else {
        for (SymbolicValue symbolicValue : AV.get(condition).elements) {
          Primitive adaptedValue = symbolicValue.value;
          if (!label.booleanValue()) {
            adaptedValue = Calculator.unaryExpression(Operator.NOT, adaptedValue);
          }
          Primitive member = Calculator.binaryExpression(symbolicValue.condition, Operator.AND, adaptedValue);
          if (pathCondition == null) {
            pathCondition = member;
          } else {
            pathCondition = Calculator.binaryExpression(pathCondition, Operator.OR, member);
          }
        }
      }
    }
    if (pathCondition == null) {
      pathCondition = Calculator.makeTrue();
    }
    return pathCondition;
  }
}

package it.unive.lisa.analysis.avase;

import java.util.Map;
import java.util.Set;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.ArrayList;

import jbse.val.Primitive;
import it.unive.lisa.program.cfg.ProgramPoint;

public class DefinitionSurvivalPathConditionPrecomputer {
  public static Map<Definition, Primitive> process(Map<Definition, Primitive> definitionControlConditions, Map<String, Set<Definition>> definitions) {
    Map<ProgramPoint, EmergingDefinitions> ED = DataflowStateMap.getEmergingDefinitionsMap();
    Map<Definition, Primitive> result = new HashMap<>();
    for (Set<Definition> subset : definitions.values()) {
      for (Definition A : subset) {
        Set<Definition> killers = new HashSet<>(subset);
        killers.remove(A);
        if (!ED.containsKey(A.programPoint)) {
          throw new AvaseImplException("DefinitionSurvivalPathConditionPrecomputer cannot precompute PC_{surv} for definitions = (" + definitions + ") because A = " + A + " is not in ED Application!");
        }
        killers.retainAll(ED.get(A.programPoint).elements);
        Primitive[] conditions = new Primitive[killers.size()];
        int idx = 0;
        for (Definition killer : killers) {
          conditions[idx] = Calculator.unaryNot(definitionControlConditions.get(killer));
          idx += 1;
        }
        result.put(A, Calculator.naryAndExpression(conditions));
      }
    }
    return result;
  }
}

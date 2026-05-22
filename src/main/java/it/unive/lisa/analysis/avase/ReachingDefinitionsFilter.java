package it.unive.lisa.analysis.avase;
import java.util.Set;
import java.util.HashSet;
import java.util.Map;
import it.unive.lisa.program.cfg.ProgramPoint;

/* @brief This component returns a filtered view of the set passed as input so that is ready for AllValues.
 * @details Every definition that is reaching but that is null in AV[p] or AV[p] = $\emptyset$, it is excluded.
 * */
public class ReachingDefinitionsFilter {
  public static Set<Definition> process(Set<Definition> definitions) {
    Map<ProgramPoint, AllValues> AV = DataflowStateMap.getAllValuesMap();
    Set<Definition> result = new HashSet<>();
    for (Definition definition : definitions) {
      if (AV.containsKey(definition.programPoint)) {
        AllValues AVn = AV.get(definition.programPoint);
        if (!AVn.elements.isEmpty()) {
          result.add(definition);
        }
      }
    }
    return result;
  }
}

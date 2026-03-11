package it.unive.lisa.analysis.avase;

import it.unive.lisa.program.cfg.ProgramPoint;
import java.util.HashMap;
import java.util.Map;

public class DataflowStateMap {
  private static final Map<ProgramPoint, Dominance> dominanceMap = new HashMap<>();
  private static final Map<ProgramPoint, ReachingDefinitions> reachingDefinitionsMap = new HashMap<>();
  private static final Map<ProgramPoint, PathConditions> pathConditionsMap = new HashMap<>();

  public static Map<ProgramPoint, Dominance> getDominanceMap() {
    return dominanceMap;
  }

  public static Map<ProgramPoint, ReachingDefinitions> getReachingDefinitionsMap() {
    return reachingDefinitionsMap;
  }

  public static Map<ProgramPoint, PathConditions> getPathConditionsMap() {
    return pathConditionsMap;
  }

  public static void clear() {
    dominanceMap.clear();
    reachingDefinitionsMap.clear();
    pathConditionsMap.clear();
  }
}

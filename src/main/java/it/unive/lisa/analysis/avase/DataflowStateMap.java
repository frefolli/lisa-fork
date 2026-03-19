package it.unive.lisa.analysis.avase;

import it.unive.lisa.program.cfg.ProgramPoint;
import java.util.HashMap;
import java.util.Map;

public class DataflowStateMap {
  private static final Map<ProgramPoint, Dominance> dominanceMap = new HashMap<>();
  private static final Map<ProgramPoint, ReachingDefinitions> reachingDefinitionsMap = new HashMap<>();
  private static final Map<ProgramPoint, KilledDefinitions> killedDefinitionsMap = new HashMap<>();
  private static final Map<ProgramPoint, PathConditions> pathConditionsMap = new HashMap<>();
  private static final Map<ProgramPoint, ControlConditions> controlConditionsMap = new HashMap<>();

  public static Map<ProgramPoint, Dominance> getDominanceMap() {
    return dominanceMap;
  }

  public static Map<ProgramPoint, ReachingDefinitions> getReachingDefinitionsMap() {
    return reachingDefinitionsMap;
  }

  public static Map<ProgramPoint, KilledDefinitions> getKilledDefinitionsMap() {
    return killedDefinitionsMap;
  }

  public static Map<ProgramPoint, PathConditions> getPathConditionsMap() {
    return pathConditionsMap;
  }

  public static Map<ProgramPoint, ControlConditions> getControlConditionsMap() {
    return controlConditionsMap;
  }

  public static void clear() {
    dominanceMap.clear();
    reachingDefinitionsMap.clear();
    killedDefinitionsMap.clear();
    pathConditionsMap.clear();
    controlConditionsMap.clear();
  }
}

package it.unive.lisa.analysis.avase;

import it.unive.lisa.program.cfg.ProgramPoint;
import java.util.HashMap;
import java.util.Map;

public class DataflowStateMap {
  private static final Map<ProgramPoint, Dominance> dominance_map = new HashMap<>();
  private static final Map<ProgramPoint, ReachingDefinitions> reaching_definitions_map = new HashMap<>();

  public static Map<ProgramPoint, Dominance> getDominanceMap() {
    return dominance_map;
  }

  public static Map<ProgramPoint, ReachingDefinitions> getReachingDefinitionsMap() {
    return reaching_definitions_map;
  }

  public static void clear() {
    dominance_map.clear();
    reaching_definitions_map.clear();
  }
}

package it.unive.lisa.analysis.avase;

import it.unive.lisa.program.cfg.ProgramPoint;
import it.unive.lisa.program.cfg.CFG;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.io.FileOutputStream;
import java.io.Writer;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.io.IOException;

public class DataflowStateMap {
  private static final Map<CFG, Set<ProgramPoint>> cfgMap = new HashMap<>();
  private static final Map<ProgramPoint, Integer> labellingMap = new HashMap<>();
  private static final Map<ProgramPoint, Set<ProgramPoint>> postDominatorsMap = new HashMap<>();
  private static final Map<ProgramPoint, Set<ProgramPoint>> preDominatorsMap = new HashMap<>();
  private static final Map<ProgramPoint, Set<ProgramPoint>> immediatePostDominatorsMap = new HashMap<>();
  private static final Map<ProgramPoint, Set<ProgramPoint>> immediatePreDominatorsMap = new HashMap<>();
  private static final Map<ProgramPoint, Set<ProgramPoint>> postDominationMap = new HashMap<>();
  private static final Map<ProgramPoint, Set<ProgramPoint>> preDominationMap = new HashMap<>();
  private static final Map<ProgramPoint, Set<ProgramPoint>> immediatePostDominationMap = new HashMap<>();
  private static final Map<ProgramPoint, Set<ProgramPoint>> immediatePreDominationMap = new HashMap<>();
  private static final Map<ProgramPoint, Set<ProgramPoint>> postDominanceFrontierMap = new HashMap<>();
  private static final Map<ProgramPoint, Set<ProgramPoint>> preDominanceFrontierMap = new HashMap<>();
  private static final Map<ProgramPoint, Branch> controlBranchMap = new HashMap<>();
  private static final Map<ProgramPoint, Set<Branch>> controlDependenciesMap = new HashMap<>();

  private static final Map<ProgramPoint, AvailableDefinitions> availableDefinitionsMap = new HashMap<>();
  private static final Map<ProgramPoint, ReachingDefinitions> reachingDefinitionsMap = new HashMap<>();
  private static final Map<ProgramPoint, KilledDefinitions> killedDefinitionsMap = new HashMap<>();
  private static final Map<ProgramPoint, EmergingDefinitions> emergingDefinitionsMap = new HashMap<>();
  private static final Map<ProgramPoint, AllValues> allValuesMap = new HashMap<>();

  public static Map<CFG, Set<ProgramPoint>> getCFGMap() {
    return cfgMap;
  }

  public static Map<ProgramPoint, Integer> getLabellingMap() {
    return labellingMap;
  }

  public static Map<ProgramPoint, Set<ProgramPoint>> getPostDominatorsMap() {
    return postDominatorsMap;
  }

  public static Map<ProgramPoint, Set<ProgramPoint>> getPreDominatorsMap() {
    return preDominatorsMap;
  }

  public static Map<ProgramPoint, Set<ProgramPoint>> getImmediatePostDominatorsMap() {
    return immediatePostDominatorsMap;
  }

  public static Map<ProgramPoint, Set<ProgramPoint>> getImmediatePreDominatorsMap() {
    return immediatePreDominatorsMap;
  }

  public static Map<ProgramPoint, Set<ProgramPoint>> getPostDominationMap() {
    return postDominationMap;
  }

  public static Map<ProgramPoint, Set<ProgramPoint>> getPreDominationMap() {
    return preDominationMap;
  }

  public static Map<ProgramPoint, Set<ProgramPoint>> getImmediatePostDominationMap() {
    return immediatePostDominationMap;
  }

  public static Map<ProgramPoint, Set<ProgramPoint>> getImmediatePreDominationMap() {
    return immediatePreDominationMap;
  }

  public static Map<ProgramPoint, Set<ProgramPoint>> getPostDominanceFrontierMap() {
    return postDominanceFrontierMap;
  }

  public static Map<ProgramPoint, Set<ProgramPoint>> getPreDominanceFrontierMap() {
    return preDominanceFrontierMap;
  }

  public static Map<ProgramPoint, Branch> getControlBranchMap() {
    return controlBranchMap;
  }

  public static Map<ProgramPoint, Set<Branch>> getControlDependenciesMap() {
    return controlDependenciesMap;
  }

  public static Map<ProgramPoint, AvailableDefinitions> getAvailableDefinitionsMap() {
    return availableDefinitionsMap;
  }

  public static Map<ProgramPoint, ReachingDefinitions> getReachingDefinitionsMap() {
    return reachingDefinitionsMap;
  }

  public static Map<ProgramPoint, KilledDefinitions> getKilledDefinitionsMap() {
    return killedDefinitionsMap;
  }

  public static Map<ProgramPoint, EmergingDefinitions> getEmergingDefinitionsMap() {
    return emergingDefinitionsMap;
  }

  public static Map<ProgramPoint, AllValues> getAllValuesMap() {
    return allValuesMap;
  }

  public static void clear() {
    cfgMap.clear();
    labellingMap.clear();
    postDominatorsMap.clear();
    preDominatorsMap.clear();
    immediatePostDominatorsMap.clear();
    immediatePreDominatorsMap.clear();
    postDominationMap.clear();
    preDominationMap.clear();
    immediatePostDominationMap.clear();
    immediatePreDominationMap.clear();
    postDominanceFrontierMap.clear();
    preDominanceFrontierMap.clear();
    controlBranchMap.clear();
    controlDependenciesMap.clear();
    reachingDefinitionsMap.clear();
    availableDefinitionsMap.clear();
    killedDefinitionsMap.clear();
    emergingDefinitionsMap.clear();
  }
}

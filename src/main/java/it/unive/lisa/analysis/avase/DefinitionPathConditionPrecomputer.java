package it.unive.lisa.analysis.avase;

import java.util.Map;
import java.util.Set;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.ArrayList;

import jbse.val.Primitive;
import it.unive.lisa.program.cfg.ProgramPoint;

public class DefinitionPathConditionPrecomputer {
  public static Map<Definition, Primitive> process(Map<String, Set<Definition>> definitions) {
    Map<Definition, Primitive> result = new HashMap<>();
    for (Set<Definition> subset : definitions.values()) {
      for (Definition D : subset) {
        result.put(D, ControlDependencyPathConditionComputer.process(D.programPoint));
      }
    }
    return result;
  }
}

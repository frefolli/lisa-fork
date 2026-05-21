package it.unive.lisa.analysis.avase;

import java.util.Set;
import java.util.HashSet;
import java.util.Map;
import java.util.HashMap;

import jbse.val.Any;
import jbse.val.Simplex;
import jbse.val.Primitive;
import jbse.val.Term;
import jbse.val.Expression;

public class DefinitionPartitioner {
  public static Map<String, Set<Definition>> process(Set<String> variables, Set<Definition> toFilter) {
    Map<String, Set<Definition>> definitions = new HashMap();
    for (Definition def : toFilter) {
      String varID = def.variable.getName();
      if (variables.contains(varID)) {
        if (!definitions.containsKey(varID)) {
          definitions.put(varID, new HashSet<>());
        }
        definitions.get(varID).add(def);
      }
    }
    return definitions;
  }
}

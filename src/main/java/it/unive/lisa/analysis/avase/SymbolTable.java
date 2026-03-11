package it.unive.lisa.analysis.avase;

import it.unive.lisa.program.cfg.ProgramPoint;
import java.util.HashMap;
import java.util.Map;

public class SymbolTable {
  public static class Variable {
    private String name;

    public Variable(String name) {
      this.name = name;
    }

    public String getName() {
      return name;
    }

    public String toString() {
      return "'" + name + "'";
    }
  }

  private static int id_counter = 0;
  private static final Map<String, Variable> VARIABLES_BY_NAME = new HashMap<String, Variable>();

  public static void add(Variable variable) {
    if (VARIABLES_BY_NAME.get(variable.getName()) != null) {
      throw new RuntimeException("Variable " + variable + " is being added more than one time to SymbolTable");
    }
    VARIABLES_BY_NAME.put(variable.getName(), variable);
  }

  public static Variable find(String name) {
    return VARIABLES_BY_NAME.get(name);
  }

  public static void clear() {
    VARIABLES_BY_NAME.clear();
  }
}

package it.unive.lisa.analysis.avase;
import java.util.Map;
import java.util.HashMap;

public class DefinitionCombination {
  public final Map<String, SymbolicValue> symbolicValues;
  public final Map<String, Definition> definitions;

  public DefinitionCombination() {
    this.symbolicValues = new HashMap<>();
    this.definitions = new HashMap<>();
  }

  public DefinitionCombination(Map<String, SymbolicValue> symbolicValues, Map<String, Definition> definitions) {
    this.symbolicValues = symbolicValues;
    this.definitions = definitions;
  }

  public DefinitionCombination fork() {
    return new DefinitionCombination(new HashMap<>(symbolicValues), new HashMap<>(definitions));
  }

  public String toString() {
    return "<" + symbolicValues + "|" + definitions + ">";
  }
};

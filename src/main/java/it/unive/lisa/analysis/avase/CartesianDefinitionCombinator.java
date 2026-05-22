package it.unive.lisa.analysis.avase;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import java.util.HashMap;
import java.util.HashSet;
import java.util.stream.Stream;
import java.util.stream.Collectors;
import it.unive.lisa.logging.Logger;

public class CartesianDefinitionCombinator {
  private final Map<String, Set<Definition>> definitions;
  private final List<String> variables;

  // Old Recursive Implementation
  private final List<DefinitionCombination> combinations;

  private CartesianDefinitionCombinator(Map<String, Set<Definition>> definitions) {
    this.definitions = definitions;
    this.variables = new ArrayList<>(definitions.keySet());
    this.combinations = new ArrayList<>();
  }

  public static List<DefinitionCombination> processRecursive(Map<String, Set<Definition>> definitions) {
    CartesianDefinitionCombinator cdc = new CartesianDefinitionCombinator(definitions);
    cdc.visit(new DefinitionCombination(), 0);
    return cdc.combinations;
  }

  public static List<DefinitionCombination> processEager(Map<String, Set<Definition>> definitions) {
    CartesianDefinitionCombinator cdc = new CartesianDefinitionCombinator(definitions);
    return cdc.getCartesianProductUsingStreams().collect(Collectors.toList());
  }

  public static Stream<DefinitionCombination> process/*Lazy*/(Map<String, Set<Definition>> definitions) {
    CartesianDefinitionCombinator cdc = new CartesianDefinitionCombinator(definitions);
    return cdc.getCartesianProductUsingStreams();
  }

  public void visit(DefinitionCombination combination, int varIdx) {
    if (varIdx < variables.size()) {
      String varID = variables.get(varIdx);
      DefinitionCombination newCombination = combination.fork();
      for (Definition altDefinition : definitions.get(varID)) {
        newCombination.definitions.put(varID, altDefinition);
        for (SymbolicValue altSymbolicValue : DataflowStateMap.getAllValuesMap().get(altDefinition.programPoint)) {
          newCombination.symbolicValues.put(varID, altSymbolicValue);
          visit(newCombination, varIdx + 1);
        }
      }
    } else {
      combinations.add(combination);
    }
  }

  public Stream<DefinitionCombination> getCartesianProductUsingStreams() {
    return cartesianProduct(0);
  }

  public Stream<DefinitionCombination> cartesianProduct(int varIdx) {
    if (varIdx == definitions.size()) {
      DefinitionCombination combination = new DefinitionCombination();
      return Stream.of(combination);
    }

    String varID = variables.get(varIdx);
    Set<Definition> currentDefinitions = definitions.get(varID);
    return currentDefinitions.stream().flatMap(definition -> {
        Set<SymbolicValue> symbolicValues = DataflowStateMap.getAllValuesMap().get(definition.programPoint).elements;
        return symbolicValues.stream().flatMap(symbolicValue -> 
            cartesianProduct(varIdx + 1).map(combination -> {
              DefinitionCombination newCombination = combination.fork();
              newCombination.definitions.put(varID, definition);
              newCombination.symbolicValues.put(varID, symbolicValue);
              return newCombination;
            }));
    });
  }
}

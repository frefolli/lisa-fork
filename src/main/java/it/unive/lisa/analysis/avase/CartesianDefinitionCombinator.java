package it.unive.lisa.analysis.avase;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import java.util.HashMap;
import java.util.HashSet;
import it.unive.lisa.logging.Logger;

public class CartesianDefinitionCombinator {
  public static class State {
    public final Map<String, SymbolicValue> symbolicValues;
    public final Map<String, Definition> definitions;

    public State() {
      this.symbolicValues = new HashMap<>();
      this.definitions = new HashMap<>();
    }

    public State(Map<String, SymbolicValue> symbolicValues, Map<String, Definition> definitions) {
      this.symbolicValues = symbolicValues;
      this.definitions = definitions;
    }

    public State fork() {
      return new State(new HashMap<>(symbolicValues), new HashMap<>(definitions));
    }

    public String toString() {
      return "<" + symbolicValues + "|" + definitions + ">";
    }
  };

  private final Map<String, Set<Definition>> definitions;
  private final List<String> variables;
  private CartesianDefinitionCombinator(Map<String, Set<Definition>> definitions) {
    this.definitions = definitions;
    this.variables = new ArrayList<>(definitions.keySet());
  }

  public static void process(Map<String, Set<Definition>> definitions) {
    CartesianDefinitionCombinator cdc = new CartesianDefinitionCombinator(definitions);
    cdc.visit(new CartesianDefinitionCombinator.State(), 0);
  }

  public void visit(CartesianDefinitionCombinator.State state, int varIdx) {
    if (varIdx < variables.size()) {
      String varID = variables.get(varIdx);
      State new_state = state.fork();
      for (Definition altDefinition : definitions.get(varID)) {
        new_state.definitions.put(varID, altDefinition);
        for (SymbolicValue altSymbolicValue : DataflowStateMap.getAllValuesMap().get(altDefinition.programPoint)) {
          new_state.symbolicValues.put(varID, altSymbolicValue);
          visit(new_state, varIdx + 1);
        }
      }
    } else {
      Logger.logDebug(state.toString());
    }
  }
}

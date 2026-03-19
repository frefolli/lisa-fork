package it.unive.lisa;

public class CommandLineArguments {
  private String inputName;
  private boolean withReachingDefinitions;
  private boolean withKilledDefinitions;
  private boolean verbose;

  public CommandLineArguments(String inputName,
                              boolean withReachingDefinitions,
                              boolean withKilledDefinitions,
                              boolean verbose) {
    this.inputName = inputName;
    this.withReachingDefinitions = withReachingDefinitions;
    this.withKilledDefinitions = withKilledDefinitions;
    this.verbose = verbose;
  }

  public CommandLineArguments() {
    this("input", false, false, false);
  }

  public void setInputName(String inputName) {
    this.inputName = inputName;
  }

  public String getInputName() {
    return inputName;
  }

  public void setWithKilledDefinitions(boolean value) {
    this.withKilledDefinitions = value;
  }

  public boolean withKilledDefinitions() {
    return withKilledDefinitions;
  }

  public void setWithReachingDefinitions(boolean value) {
    this.withReachingDefinitions = value;
  }

  public boolean withReachingDefinitions() {
    return withReachingDefinitions;
  }

  public void setVerbose(boolean value) {
    this.verbose = value;
  }

  public boolean isVerbose() {
    return verbose;
  }
}

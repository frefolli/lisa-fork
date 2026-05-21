package it.unive.lisa;
import java.util.List;
import java.util.ArrayList;

public class CommandLineArguments {
  private List<String> inputPaths;
  private boolean verbose;
  private String output;

  public CommandLineArguments(List<String> inputPaths,
                              boolean verbose,
                              String output) {
    this.inputPaths = inputPaths;
    this.verbose = verbose;
    this.output = output;
  }

  public CommandLineArguments() {
    this(new ArrayList<>(), false, "/tmp/avase-lisa-out");
  }

  public void addInputPath(String inputPath) {
    this.inputPaths.add(inputPath);
  }

  public List<String> getInputPaths() {
    return inputPaths;
  }

  public void setOutput(String value) {
    this.output = value;
  }

  public String getOutput() {
    return output;
  }

  public void setVerbose(boolean value) {
    this.verbose = value;
  }

  public boolean isVerbose() {
    return verbose;
  }
}

package it.unive.lisa;

public class CommandLineInterface {
  String[] args;
  int argc;
  int argi;

  public CommandLineInterface() {
    this.args = new String[0];
    this.argc = 0;
    this.argi = 0;
  }

  private void setup(String[] args) {
    this.args = args;
    this.argc = args.length;
    this.argi = 0;
  }

  public CommandLineArguments parse(String[] args) throws CommandLineException {
    setup(args);
    CommandLineArguments cla = new CommandLineArguments();
    while (notDone()) {
      if (matchFlag("=v", "==verbose")) {
        cla.setVerbose(true);
      } else if (matchFlag("=o", "==output")) {
        cla.setOutput(acceptString());
      } else if (matchFlag("=h", "==help")) {
        throw new CommandLineException("Help Requested", false);
      } else {
        cla.addInputPath(acceptString());
      }
    }
    return cla;
  }

  public void printHelp() {
    System.out.println("kou [=w<Feature> | =W<Feature>] <INPUT-FILE>...");
    System.out.println("Arguments:");
    System.out.println("  =h, ==help                             Print this Help Screen");
    System.out.println("  =v, ==verbose                          Enable Verbose Behavior");
    System.out.println("  =o, ==output                           The Output Directory in which output files are stored (will be cleaned if exists, half warned half saved)");
    System.out.println("  INPUT-NAME                             The Input File of the program that will be analyzed (if a directory is passed, it is recursively searched for files with .imp extension).");
  }

  private boolean notDone() {
    return argi < argc;
  }

  private boolean matchFlag(String shortForm, String longForm) {
    if (argi >= argc) {
      return false;
    }
    if (args[argi].equals(shortForm)) {
      argi += 1;
      return true;
    }
    if (args[argi].equals(longForm)) {
      argi += 1;
      return true;
    }
    return false;
  }

  private String acceptString() throws CommandLineException {
    if (argi >= argc) {
      if (argi > 0) {
        throw new CommandLineException("Expected string argument after " + args[argi - 1]);
      } else {
        throw new CommandLineException("Expected string argument");
      }
    }
    String value = args[argi];
    argi += 1;
    return value;
  }
}

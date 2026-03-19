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
      if (matchFlag("=i", "==input=name")) {
        cla.setInputName(acceptString());
      } else if (matchFlag("=v", "==verbose")) {
        cla.setVerbose(true);
      } else if (matchFlag("=wRD", "==with=reaching=definitions")) {
        cla.setWithReachingDefinitions(true);
      } else if (matchFlag("=WRD", "==without=reaching=definitions")) {
        cla.setWithReachingDefinitions(false);
      } else if (matchFlag("=wKD", "==with=killed=definitions")) {
        cla.setWithKilledDefinitions(true);
      } else if (matchFlag("=WKD", "==without=killed=definitions")) {
        cla.setWithKilledDefinitions(false);
      } else if (matchFlag("=wAll", "==with=all")) {
        cla.setWithKilledDefinitions(true);
        cla.setWithReachingDefinitions(true);
      } else if (matchFlag("=WAll", "==without=all")) {
        cla.setWithKilledDefinitions(false);
        cla.setWithReachingDefinitions(false);
      } else if (matchFlag("=h", "==help")) {
        throw new CommandLineException("Help Requested", false);
      } else {
        throw new CommandLineException("Unexpected argument: " + args[argi]);
      }
    }
    return cla;
  }

  public void printHelp() {
    System.out.println("kou [=i INPUT-NAME] [=w<Feature> | =W<Feature>]");
    System.out.println("Arguments:");
    System.out.println("  =h, ==help                             Print this Help Screen");
    System.out.println("  =i, ==input INPUT-NAME                 The Input Name of the program that will be analyzed (es. example, simple, ...).");
    System.out.println("                                         It is the name that is found from inputs/<name>.imp");
    System.out.println("  =v, ==verbose                          Enable Verbose Behavior");
    System.out.println("  =wRD, ==with=reaching=definitions      Enable  Reaching Definition");
    System.out.println("  =WRD, ==without=reaching=definitions   Disable Reaching Definition");
    System.out.println("  =wKD, ==with=killed=definitions        Enable  KilledDefinitions");
    System.out.println("  =WKD, ==without=killed=definitions     Disable KilledDefinitions");
    System.out.println("  =wAll, ==with=all                      Enable  every analysis speculator (RD, KD)");
    System.out.println("  =WAll, ==without=all                   Disable every analysis speculator (RD, KD)");
    System.out.println();
    System.out.println("Warnings:");
    System.out.println("  * The options =w<F> and =W<F> are interpreted one by one as are encountered in the CLI args");
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

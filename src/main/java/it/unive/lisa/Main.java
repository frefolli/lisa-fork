package it.unive.lisa;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

import it.unive.lisa.analysis.avase.Speculator;
import it.unive.lisa.analysis.avase.Dominance;
import it.unive.lisa.analysis.avase.ReachingDefinitions;
import it.unive.lisa.analysis.avase.KilledDefinitions;
import it.unive.lisa.analysis.avase.PathConditions;
import it.unive.lisa.analysis.avase.ControlConditions;
import it.unive.lisa.analysis.avase.AdvancedAbstractState;
import it.unive.lisa.analysis.traces.TracePartitioning;
import it.unive.lisa.conf.LiSAConfiguration;
import it.unive.lisa.conf.LiSAConfiguration.GraphType;
import it.unive.lisa.imp.IMPFrontend;
import it.unive.lisa.imp.ParsingException;
import it.unive.lisa.program.Program;
import org.apache.commons.io.FileUtils;

import it.unive.lisa.analysis.heap.HeapDomain;
import it.unive.lisa.analysis.heap.MonolithicHeap;
import it.unive.lisa.analysis.nonrelational.value.TypeEnvironment;
import it.unive.lisa.analysis.nonrelational.value.ValueEnvironment;
import it.unive.lisa.analysis.numeric.Interval;
import it.unive.lisa.analysis.types.InferredTypes;

public class Main {
  private static void assertInputFileExists(String filePath) throws IllegalArgumentException {
    File f = new File(filePath);
    if (!f.exists() || f.isDirectory()) {
      throw new IllegalArgumentException("Input path '" + filePath + "' is not a file");
    }
  }

  private static void cleanOutputDirectoryIfDirty(String filePath) throws IOException, IllegalArgumentException {
    File dir = new File(filePath);
    if (dir.exists()) {
      if (!dir.isDirectory()) {
        throw new IllegalArgumentException("Output path '" + filePath + "' is not a directory");
      }
      FileUtils.deleteDirectory(dir);
    }
  }

  private static String getInputFilePath(String programName) {
    return Path.of("./inputs", programName + ".imp").toString();
  }

  private static String getOutputDirectoryPath(String programName) {
    return Path.of("./outputs", programName).toString();
  }

  public static void main(String[] args) throws IOException, IllegalArgumentException, ParsingException, AnalysisException {
    CommandLineInterface cli = new CommandLineInterface();
    try {
      CommandLineArguments cliArgs = cli.parse(args);

      String programName = cliArgs.getInputName();
      String inputPath = getInputFilePath(programName);
      assertInputFileExists(inputPath);
      String outputPath = getOutputDirectoryPath(programName);
      cleanOutputDirectoryIfDirty(outputPath);

      // we parse the program to get the CFG representation of the code in it
      Program program = IMPFrontend.processFile(inputPath);
      
      if (cliArgs.isVerbose()) {
        ProgramInspector.visit(program);
        return;
      }

      // we build a new configuration for the analysis
      LiSAConfiguration conf = new DefaultConfiguration();
      // we specify where we want files to be generated
      conf.workdir = outputPath;
      // we specify the visual format of the analysis results
      conf.analysisGraphs = GraphType.HTML;

      Map<String, Speculator> speculators = new HashMap<>();
      List<String> speculatorsOrder = new ArrayList<>();

      if (cliArgs.withReachingDefinitions()) {
        String key = "RD";
        speculators.put(key, new ReachingDefinitions());
        speculatorsOrder.add(key);
      }
      if (cliArgs.withKilledDefinitions()) {
        String key = "KD";
        speculators.put(key, new KilledDefinitions());
        speculatorsOrder.add(key);
      }

      // we specify the analysis that we want to execute
      conf.abstractState = AdvancedAbstractState.make(
        new MonolithicHeap(),
        new ValueEnvironment<>(new Interval()),
        new TypeEnvironment<>(new InferredTypes()),
        speculators,
        speculatorsOrder
      );
      // conf.abstractState = new OldAbstractState(
      // );
      // conf.abstractState = new Dominance();

      // we instantiate LiSA with our configuration
      LiSA lisa = new LiSA(conf);

      // finally, we tell LiSA to analyze the program
      lisa.run(program);
    } catch (CommandLineException e) {
      cli.printHelp();
      if (e.isError()) {
        throw e;
      }
    }
  }
}

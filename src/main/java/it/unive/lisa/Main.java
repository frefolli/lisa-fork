package it.unive.lisa;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

import it.unive.lisa.analysis.avase.Dominance;
import it.unive.lisa.analysis.avase.ReachingDefinitions;
import it.unive.lisa.analysis.avase.AdvancedAbstractState;
import it.unive.lisa.analysis.avase.OldAbstractState;
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
    System.out.println(filePath);
    File f = new File(filePath);
    if (!f.exists() || f.isDirectory()) {
      throw new IllegalArgumentException("Input path '" + filePath + "' is not a file");
    }
  }

  private static void cleanOutputDirectoryIfDirty(String filePath) throws IOException, IllegalArgumentException {
    System.out.println(filePath);
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
    String programName = "simple";
    if (args.length > 0) {
      programName = args[0];
    }
    String inputPath = getInputFilePath(programName);
    assertInputFileExists(inputPath);
    String outputPath = getOutputDirectoryPath(programName);
    cleanOutputDirectoryIfDirty(outputPath);

    // we parse the program to get the CFG representation of the code in it
    Program program = IMPFrontend.processFile(inputPath);

    // we build a new configuration for the analysis
    LiSAConfiguration conf = new DefaultConfiguration();

    // we specify where we want files to be generated
    conf.workdir = outputPath;

    // we specify the visual format of the analysis results
    conf.analysisGraphs = GraphType.HTML;

    // we specify the analysis that we want to execute
    conf.abstractState = new AdvancedAbstractState<>(
      new MonolithicHeap(),
      new ValueEnvironment<>(new Interval()),
      new TypeEnvironment<>(new InferredTypes()),
      new Dominance(),
      new ReachingDefinitions()
    );
    // conf.abstractState = new OldAbstractState(
    // );
    // conf.abstractState = new Dominance();

    // we instantiate LiSA with our configuration
    LiSA lisa = new LiSA(conf);

    // finally, we tell LiSA to analyze the program
    lisa.run(program);
  }
}

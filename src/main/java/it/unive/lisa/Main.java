package it.unive.lisa;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

import it.unive.lisa.analysis.avase.ProgramInspector;
import it.unive.lisa.analysis.avase.PreDominators;
import it.unive.lisa.analysis.avase.PostDominators;
import it.unive.lisa.analysis.avase.ImmediatePreDominators;
import it.unive.lisa.analysis.avase.ImmediatePostDominators;
import it.unive.lisa.analysis.avase.PreDomination;
import it.unive.lisa.analysis.avase.PostDomination;
import it.unive.lisa.analysis.avase.ImmediatePreDomination;
import it.unive.lisa.analysis.avase.ImmediatePostDomination;
import it.unive.lisa.analysis.avase.PreDominanceFrontier;
import it.unive.lisa.analysis.avase.PostDominanceFrontier;
import it.unive.lisa.analysis.avase.ControlBranch;
import it.unive.lisa.analysis.avase.ControlDependencies;
import it.unive.lisa.analysis.avase.AllValues;

import it.unive.lisa.analysis.avase.Speculator;
import it.unive.lisa.analysis.avase.ReachingDefinitions;
import it.unive.lisa.analysis.avase.KilledDefinitions;
import it.unive.lisa.analysis.avase.EmergingDefinitions;
import it.unive.lisa.analysis.avase.AvailableDefinitions;

import it.unive.lisa.analysis.avase.AdvancedAbstractState;
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
import it.unive.lisa.analysis.avase.Documenter;

import it.unive.lisa.logging.Logger;

public class Main {
  private static void discoverInputFiles(List<File> inputFiles, File rawInputFile) throws IOException, IllegalArgumentException {
    if (!rawInputFile.exists()) {
      throw new IllegalArgumentException("Input path '" + rawInputFile.getAbsolutePath() + "' is not a file or a directory");
    }
    if (rawInputFile.isDirectory()) {
      for (File child : rawInputFile.listFiles()) {
        if (child.isDirectory()) {
          discoverInputFiles(inputFiles, child);
        } else if (child.getCanonicalPath().endsWith(".imp")) {
          inputFiles.add(child);
        }
      }
    } else if (rawInputFile.getCanonicalPath().endsWith(".imp")) {
      inputFiles.add(rawInputFile);
    } else {
      throw new IllegalArgumentException("Input path '" + rawInputFile.getAbsolutePath() + "' is not a IMP file or a directory");
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

  private static String mangleFilePath(Path currentPath, File file) throws IOException {
    return currentPath.relativize(Path.of(file.getCanonicalPath())).toString().replace("/", "__");
  }

  public static void main(String[] args) throws IOException, IllegalArgumentException, ParsingException, AnalysisException {
    CommandLineInterface cli = new CommandLineInterface();
    try {
      CommandLineArguments cliArgs = cli.parse(args);
      Logger.init(cliArgs.isVerbose() ? Logger.LogLevel.DEBUG : Logger.LogLevel.INFO);

      List<File> inputFiles = new ArrayList<>();
      for (String rawInputFile : cliArgs.getInputPaths()) {
        discoverInputFiles(inputFiles, new File(rawInputFile));
      }

      if (inputFiles.isEmpty()) {
        throw new IllegalArgumentException("avase-lisa requires IMP files as input");
      }
        
      Path currentPath = Paths.get(".").toAbsolutePath().normalize();
      cleanOutputDirectoryIfDirty(cliArgs.getOutput());
      for (File inputFile :inputFiles) {
        String outputPath = cliArgs.getOutput() + "/" + mangleFilePath(currentPath, inputFile);
        cleanOutputDirectoryIfDirty(outputPath);

        // we parse the program to get the CFG representation of the code in it
        Logger.logInfo("> IMPFrontend.processFile(inputFile) :: START");
        Program program = IMPFrontend.processFile(inputFile.getCanonicalPath());
        Logger.logInfo("> IMPFrontend.processFile(inputFile) :: START");
        
        Logger.logInfo("> ProgramInspector.computeAll(program) :: START");
        ProgramInspector.computeAll(program);
        Logger.logInfo("> ProgramInspector.computeAll(program) :: END");

        Logger.logInfo("> PreDominators.computeAll(program) :: START");
        PreDominators.computeAll(program);
        Logger.logInfo("> PreDominators.computeAll(program) :: END");

        Logger.logInfo("> PostDominators.computeAll(program) :: START");
        PostDominators.computeAll(program);
        Logger.logInfo("> PostDominators.computeAll(program) :: END");

        Logger.logInfo("> ImmediatePreDominators.computeAll(program) :: START");
        ImmediatePreDominators.computeAll(program);
        Logger.logInfo("> ImmediatePreDominators.computeAll(program) :: END");

        Logger.logInfo("> ImmediatePostDominators.computeAll(program) :: START");
        ImmediatePostDominators.computeAll(program);
        Logger.logInfo("> ImmediatePostDominators.computeAll(program) :: END");

        Logger.logInfo("> PreDomination.computeAll(program) :: START");
        PreDomination.computeAll(program);
        Logger.logInfo("> PreDomination.computeAll(program) :: END");

        Logger.logInfo("> PostDomination.computeAll(program) :: START");
        PostDomination.computeAll(program);
        Logger.logInfo("> PostDomination.computeAll(program) :: END");

        Logger.logInfo("> ImmediatePreDomination.computeAll(program) :: START");
        ImmediatePreDomination.computeAll(program);
        Logger.logInfo("> ImmediatePreDomination.computeAll(program) :: END");

        Logger.logInfo("> ImmediatePostDomination.computeAll(program) :: START");
        ImmediatePostDomination.computeAll(program);
        Logger.logInfo("> ImmediatePostDomination.computeAll(program) :: END");

        Logger.logInfo("> PreDominanceFrontier.computeAll(program) :: START");
        PreDominanceFrontier.computeAll(program);
        Logger.logInfo("> PreDominanceFrontier.computeAll(program) :: END");

        Logger.logInfo("> PostDominanceFrontier.computeAll(program) :: START");
        PostDominanceFrontier.computeAll(program);
        Logger.logInfo("> PostDominanceFrontier.computeAll(program) :: END");

        Logger.logInfo("> ControlBranch.computeAll(program) :: START");
        ControlBranch.computeAll(program);
        Logger.logInfo("> ControlBranch.computeAll(program) :: END");

        Logger.logInfo("> ControlDependencies.computeAll(program) :: START");
        ControlDependencies.computeAll(program);
        Logger.logInfo("> ControlDependencies.computeAll(program) :: END");

        // we build a new configuration for the analysis
        LiSAConfiguration conf = new DefaultConfiguration();
        // we specify where we want files to be generated
        conf.workdir = outputPath;
        // we specify the visual format of the analysis results
        conf.analysisGraphs = GraphType.HTML;

        Map<String, Speculator> speculators = new HashMap<>();
        List<String> speculatorsOrder = new ArrayList<>();

        speculators.put("RD", new ReachingDefinitions());
        speculatorsOrder.add("RD");
        speculators.put("KD", new KilledDefinitions());
        speculatorsOrder.add("KD");
        speculators.put("AD", new AvailableDefinitions());
        speculatorsOrder.add("AD");
        speculators.put("ED", new EmergingDefinitions());
        speculatorsOrder.add("ED");
        speculators.put("AV", new AllValues());
        speculatorsOrder.add("AV");

        // we specify the analysis that we want to execute
        conf.abstractState = AdvancedAbstractState.make(
          new MonolithicHeap(),
          new ValueEnvironment<>(new Interval()),
          new TypeEnvironment<>(new InferredTypes()),
          speculators,
          speculatorsOrder
        );

        // we instantiate LiSA with our configuration
        LiSA lisa = new LiSA(conf);

        // finally, we tell LiSA to analyze the program
        lisa.run(program);

        Documenter.dump(outputPath);
      }
    } catch (CommandLineException e) {
      cli.printHelp();
      if (e.isError()) {
        throw e;
      }
    }
  }
}

package it.unive.lisa;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

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

import it.unive.lisa.analysis.avase.Speculator;
import it.unive.lisa.analysis.avase.ReachingDefinitions;
import it.unive.lisa.analysis.avase.KilledDefinitions;
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
      System.out.println("> IMPFrontend.processFile(inputPath) :: START");
      Program program = IMPFrontend.processFile(inputPath);
      System.out.println("> IMPFrontend.processFile(inputPath) :: START");
      
      System.out.println("> ProgramInspector.computeAll(program) :: START");
      ProgramInspector.computeAll(program);
      System.out.println("> ProgramInspector.computeAll(program) :: END");

      System.out.println("> PreDominators.computeAll(program) :: START");
      PreDominators.computeAll(program);
      System.out.println("> PreDominators.computeAll(program) :: END");

      System.out.println("> PostDominators.computeAll(program) :: START");
      PostDominators.computeAll(program);
      System.out.println("> PostDominators.computeAll(program) :: END");

      System.out.println("> ImmediatePreDominators.computeAll(program) :: START");
      ImmediatePreDominators.computeAll(program);
      System.out.println("> ImmediatePreDominators.computeAll(program) :: END");

      System.out.println("> ImmediatePostDominators.computeAll(program) :: START");
      ImmediatePostDominators.computeAll(program);
      System.out.println("> ImmediatePostDominators.computeAll(program) :: END");

      System.out.println("> PreDomination.computeAll(program) :: START");
      PreDomination.computeAll(program);
      System.out.println("> PreDomination.computeAll(program) :: END");

      System.out.println("> PostDomination.computeAll(program) :: START");
      PostDomination.computeAll(program);
      System.out.println("> PostDomination.computeAll(program) :: END");

      System.out.println("> ImmediatePreDomination.computeAll(program) :: START");
      ImmediatePreDomination.computeAll(program);
      System.out.println("> ImmediatePreDomination.computeAll(program) :: END");

      System.out.println("> ImmediatePostDomination.computeAll(program) :: START");
      ImmediatePostDomination.computeAll(program);
      System.out.println("> ImmediatePostDomination.computeAll(program) :: END");

      System.out.println("> PreDominanceFrontier.computeAll(program) :: START");
      PreDominanceFrontier.computeAll(program);
      System.out.println("> PreDominanceFrontier.computeAll(program) :: END");

      System.out.println("> PostDominanceFrontier.computeAll(program) :: START");
      PostDominanceFrontier.computeAll(program);
      System.out.println("> PostDominanceFrontier.computeAll(program) :: END");

      System.out.println("> ControlBranch.computeAll(program) :: START");
      ControlBranch.computeAll(program);
      System.out.println("> ControlBranch.computeAll(program) :: END");

      System.out.println("> ControlDependencies.computeAll(program) :: START");
      ControlDependencies.computeAll(program);
      System.out.println("> ControlDependencies.computeAll(program) :: END");

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

      Documenter.dump(outputPath);
    } catch (CommandLineException e) {
      cli.printHelp();
      if (e.isError()) {
        throw e;
      }
    }
  }
}

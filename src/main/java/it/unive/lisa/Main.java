package it.unive.lisa;

import it.unive.lisa.analysis.dataflow.Something;
import it.unive.lisa.conf.LiSAConfiguration;
import it.unive.lisa.conf.LiSAConfiguration.GraphType;
import it.unive.lisa.imp.IMPFrontend;
import it.unive.lisa.imp.ParsingException;
import it.unive.lisa.program.Program;

public class Main {
  public static void main(String[] args) throws ParsingException, AnalysisException {
    // we parse the program to get the CFG representation of the code in it
    Program program = IMPFrontend.processFile("inputs/simple.imp");

    // we build a new configuration for the analysis
    LiSAConfiguration conf = new DefaultConfiguration();

    // we specify where we want files to be generated
    conf.workdir = "outputs/example";

    // we specify the visual format of the analysis results
    conf.analysisGraphs = GraphType.HTML;

    // we specify the analysis that we want to execute
    // conf.abstractState = DefaultConfiguration.defaultAbstractState();
    conf.abstractState = new Something();

    // we instantiate LiSA with our configuration
    LiSA lisa = new LiSA(conf);

    // finally, we tell LiSA to analyze the program
    lisa.run(program);
  }
}

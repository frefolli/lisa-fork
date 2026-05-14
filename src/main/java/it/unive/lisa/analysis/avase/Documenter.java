package it.unive.lisa.analysis.avase;

import it.unive.lisa.program.cfg.ProgramPoint;
import java.util.Map;
import java.util.Set;
import java.io.FileOutputStream;
import java.io.Writer;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.io.IOException;
import it.unive.lisa.program.cfg.CFG;
import it.unive.lisa.program.cfg.edge.Edge;
import it.unive.lisa.program.cfg.statement.Statement;

public class Documenter {
  public static String nodeLabel(String ID) {
    return "$N_{" + ID + "}$";
  }

  public static String nodeAtomLabel(String ID) {
    return "N_{" + ID + "}";
  }

  public static String nodeId(String ID) {
    return "N" + ID;
  }

  public static void dump(String dirPath) throws IOException {
    Map<ProgramPoint, String> labellingMap = DataflowStateMap.getLabellingMap();
    labellingMap.clear();
    int counter = 1;
    for (ProgramPoint pp : DataflowStateMap.getPostDominatorsMap().keySet()) {
      if (!labellingMap.containsKey(pp)) {
        labellingMap.put(pp, counter + "");
        counter += 1;
      }
    }

    String readme = dirPath + "/README.md";
		try (Writer writer = new OutputStreamWriter(new FileOutputStream(readme), StandardCharsets.UTF_8.newEncoder())) {
      dumpControlFlowGraphs(dirPath, writer);
      dumpMapOfProgramPointToString(writer, "LABEL", DataflowStateMap.getLabellingMap());
      dumpMapOfProgramPointToSetOfProgramPoints(writer, "POD", DataflowStateMap.getPostDominatorsMap());
      dumpMapOfProgramPointToSetOfProgramPoints(writer, "PED", DataflowStateMap.getPreDominatorsMap());
      dumpMapOfProgramPointToSetOfProgramPoints(writer, "IPOD", DataflowStateMap.getImmediatePostDominatorsMap());
      dumpMapOfProgramPointToSetOfProgramPoints(writer, "IPED", DataflowStateMap.getImmediatePreDominatorsMap());
      dumpMapOfProgramPointToSetOfProgramPoints(writer, "rPOD", DataflowStateMap.getPostDominationMap());
      dumpMapOfProgramPointToSetOfProgramPoints(writer, "rPED", DataflowStateMap.getPreDominationMap());
      dumpMapOfProgramPointToSetOfProgramPoints(writer, "rIPOD", DataflowStateMap.getImmediatePostDominationMap());
      dumpMapOfProgramPointToSetOfProgramPoints(writer, "rIPED", DataflowStateMap.getImmediatePreDominationMap());
      dumpMapOfProgramPointToSetOfProgramPoints(writer, "POF", DataflowStateMap.getPostDominanceFrontierMap());
      dumpMapOfProgramPointToSetOfProgramPoints(writer, "PEF", DataflowStateMap.getPreDominanceFrontierMap());
    }
  }

  public static void dumpMapOfProgramPointToString(Writer writer, String ID, Map<ProgramPoint, String> map) throws IOException {
    Map<ProgramPoint, String> labellingMap = DataflowStateMap.getLabellingMap();
    writer.write("# " + ID + "\n");
    writer.write("|  N  | " + ID + " |\n");
    writer.write("| --- | --- |\n");
    for (ProgramPoint pp : map.keySet()) {
      writer.write("| " + nodeLabel(labellingMap.get(pp)) + " | " + pp + " |\n");
    }
    writer.write("\n");
  }

  public static void dumpMapOfProgramPointToSetOfProgramPoints(Writer writer, String ID, Map<ProgramPoint, Set<ProgramPoint>> map) throws IOException {
    Map<ProgramPoint, String> labellingMap = DataflowStateMap.getLabellingMap();
    writer.write("# " + ID + "\n");
    writer.write("|  N  | " + ID + " |\n");
    writer.write("| --- | --- |\n");
    for (ProgramPoint pp : map.keySet()) {
      Set<ProgramPoint> state = map.get(pp);
      writer.write("| " + nodeLabel(labellingMap.get(pp)) + " | $\\{");
      boolean first = true;
      for (ProgramPoint peer : state) {
        if (first) {
          first = false;
        } else {
          writer.write(", ");
        }
        writer.write(nodeAtomLabel(labellingMap.get(peer)));
      }
      writer.write("\\}$ |\n");
    }
    writer.write("\n");
  }

  public static void dumpControlFlowGraphs(String dirPath, Writer writer) throws IOException {
    writer.write("# CFGs\n");
    Map<String, Set<ProgramPoint>> cfgMap = DataflowStateMap.getCFGMap();
    for (String cfgID : cfgMap.keySet()) {
      String d2File = dirPath + "/cfg-" + cfgID + ".d2";
      String pngFile = "cfg-" + cfgID + ".png";
      writer.write("## " + cfgID + "\n");
      writer.write("![./" + pngFile + "](./" + pngFile + ")\n\n");
      try (Writer writerD2 = new OutputStreamWriter(new FileOutputStream(d2File), StandardCharsets.UTF_8.newEncoder())) {
        dumpControlFlowGraph(writerD2, cfgMap.get(cfgID));
      }
      compileWithD2(d2File, dirPath + "/" + pngFile);
    }
    writer.write("\n");
  }

  public static void dumpControlFlowGraph(Writer writer, Set<ProgramPoint> nodes) throws IOException {
    Map<ProgramPoint, String> labellingMap = DataflowStateMap.getLabellingMap();
    for (ProgramPoint node : nodes) {
      writer.write(nodeId(labellingMap.get(node)) + " : \"" + nodeId(labellingMap.get(node)) + " | " + node + "\"\n");
    }
    for (ProgramPoint node : nodes) {
      for (Edge edge : node.getCFG().getOutgoingEdges((Statement)node)) {
        writer.write(nodeId(labellingMap.get(edge.getSource())) + " -> " + nodeId(labellingMap.get(edge.getDestination())) + "\n");
      }
    }
    writer.write("\n");
  }

  public static void compileWithD2(String input, String output) throws IOException {
    try {
      String command = String.format("d2 %s %s", input, output);
      System.out.println("|> " + command);
      int retval = Runtime.getRuntime().exec(command).waitFor();
      System.out.println("|> " + command + " :: " + retval);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}

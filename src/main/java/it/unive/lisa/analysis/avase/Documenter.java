package it.unive.lisa.analysis.avase;

import it.unive.lisa.program.cfg.ProgramPoint;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;
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
    String readme = dirPath + "/README.md";
		try (Writer writer = new OutputStreamWriter(new FileOutputStream(readme), StandardCharsets.UTF_8.newEncoder())) {
      dumpControlFlowGraphs(dirPath, writer);
      dumpMapOfProgramPointToString(writer, "LABEL", DataflowStateMap.getLabellingMap());
      dumpMapOfProgramPointToSetOfProgramPoints(dirPath, writer, "POD", DataflowStateMap.getPostDominatorsMap());
      dumpMapOfProgramPointToSetOfProgramPoints(dirPath, writer, "PED", DataflowStateMap.getPreDominatorsMap());
      dumpMapOfProgramPointToSetOfProgramPoints(dirPath, writer, "IPOD", DataflowStateMap.getImmediatePostDominatorsMap());
      dumpMapOfProgramPointToSetOfProgramPoints(dirPath, writer, "IPED", DataflowStateMap.getImmediatePreDominatorsMap());
      dumpMapOfProgramPointToSetOfProgramPoints(dirPath, writer, "rPOD", DataflowStateMap.getPostDominationMap());
      dumpMapOfProgramPointToSetOfProgramPoints(dirPath, writer, "rPED", DataflowStateMap.getPreDominationMap());
      dumpMapOfProgramPointToSetOfProgramPoints(dirPath, writer, "rIPOD", DataflowStateMap.getImmediatePostDominationMap());
      dumpMapOfProgramPointToSetOfProgramPoints(dirPath, writer, "rIPED", DataflowStateMap.getImmediatePreDominationMap());
      dumpMapOfProgramPointToSetOfProgramPoints(dirPath, writer, "POF", DataflowStateMap.getPostDominanceFrontierMap());
      dumpMapOfProgramPointToSetOfProgramPoints(dirPath, writer, "PEF", DataflowStateMap.getPreDominanceFrontierMap());
      dumpMapOfProgramPointToBranch(dirPath, writer, "CB", DataflowStateMap.getControlBranchMap());
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

  public static void dumpControlFlowGraphs(String dirPath, Writer writer) throws IOException {
    writer.write("# CFGs\n");
    Map<CFG, Set<ProgramPoint>> cfgMap = DataflowStateMap.getCFGMap();
    for (CFG cfg : cfgMap.keySet()) {
      String cfgID = cfg.getDescriptor().getFullName();
      String d2File = dirPath + "/cfg-" + cfgID + ".d2";
      String pngFile = "cfg-" + cfgID + ".png";
      writer.write("## " + cfgID + "\n");
      writer.write("![./" + pngFile + "](./" + pngFile + ")\n\n");
      try (Writer writerD2 = new OutputStreamWriter(new FileOutputStream(d2File), StandardCharsets.UTF_8.newEncoder())) {
        dumpControlFlowGraph(writerD2, cfgMap.get(cfg));
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
  }

  public static void dumpMapOfProgramPointToSetOfProgramPointsAsGraph(String dirPath, Writer writer, String ID, Map<ProgramPoint, Set<ProgramPoint>> map) throws IOException {
    String d2File = dirPath + "/pp2setpp-" + ID + ".d2";
    String pngFile = "pp2setpp-" + ID + ".png";
    writer.write("![./" + pngFile + "](./" + pngFile + ")\n\n");
    try (Writer writerD2 = new OutputStreamWriter(new FileOutputStream(d2File), StandardCharsets.UTF_8.newEncoder())) {
      Map<ProgramPoint, String> labellingMap = DataflowStateMap.getLabellingMap();

      Set<ProgramPoint> nodes = new HashSet<>();
      for (ProgramPoint source : map.keySet()) {
        if (!map.get(source).isEmpty()) {
          nodes.add(source);
        }
        for (ProgramPoint sink : map.get(source)) {
          nodes.add(sink);
        }
      }

      for (ProgramPoint node : nodes) {
        writerD2.write(nodeId(labellingMap.get(node)) + " : \"" + nodeId(labellingMap.get(node)) + " | " + node + "\"\n");
      }
      for (ProgramPoint source : map.keySet()) {
        for (ProgramPoint sink : map.get(source)) {
          writerD2.write(nodeId(labellingMap.get(source)) + " -> " + nodeId(labellingMap.get(sink)) + "\n");
        }
      }
    }
    compileWithD2(d2File, dirPath + "/" + pngFile);
  }

  public static void dumpMapOfProgramPointToSetOfProgramPointsAsTable(Writer writer, String ID, Map<ProgramPoint, Set<ProgramPoint>> map) throws IOException {
    Map<ProgramPoint, String> labellingMap = DataflowStateMap.getLabellingMap();
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

  public static void dumpMapOfProgramPointToSetOfProgramPoints(String dirPath, Writer writer, String ID, Map<ProgramPoint, Set<ProgramPoint>> map) throws IOException {
    writer.write("# " + ID + "\n");
    dumpMapOfProgramPointToSetOfProgramPointsAsTable(writer, ID, map);
    if (ID.equals("POF"))
      dumpMapOfProgramPointToSetOfProgramPointsAsGraph(dirPath, writer, ID, map);
  }

  public static void dumpMapOfProgramPointToBranchAsGraph(String dirPath, Writer writer, String ID, Map<ProgramPoint, Branch> map) throws IOException {
    String d2File = dirPath + "/pp2setpp-" + ID + ".d2";
    String pngFile = "pp2setpp-" + ID + ".png";
    writer.write("![./" + pngFile + "](./" + pngFile + ")\n\n");
    try (Writer writerD2 = new OutputStreamWriter(new FileOutputStream(d2File), StandardCharsets.UTF_8.newEncoder())) {
      Map<ProgramPoint, String> labellingMap = DataflowStateMap.getLabellingMap();

      Set<ProgramPoint> nodes = new HashSet<>();
      for (ProgramPoint source : map.keySet()) {
        Branch sink = map.get(source);
        if (!sink.isBottom()) {
          nodes.add(source);
          nodes.add(sink.getCondition());
        }
      }

      for (ProgramPoint node : nodes) {
        writerD2.write(nodeId(labellingMap.get(node)) + " : \"" + nodeId(labellingMap.get(node)) + " | " + node + "\"\n");
      }
      for (ProgramPoint source : map.keySet()) {
        Branch sink = map.get(source);
        if (!sink.isBottom()) {
          writerD2.write(nodeId(labellingMap.get(source)) + " -> " + nodeId(labellingMap.get(sink.getCondition())) + " : " + sink.getChoice() + "\n");
        }
      }
    }
    compileWithD2(d2File, dirPath + "/" + pngFile);
  }

  public static void dumpMapOfProgramPointToBranchAsTable(Writer writer, String ID, Map<ProgramPoint, Branch> map) throws IOException {
    Map<ProgramPoint, String> labellingMap = DataflowStateMap.getLabellingMap();
    writer.write("|  N  | " + ID + " |\n");
    writer.write("| --- | --- |\n");
    for (ProgramPoint pp : map.keySet()) {
      Branch state = map.get(pp);
      writer.write("| " + nodeLabel(labellingMap.get(pp)) + " | ");
      if (state.isBottom()) {
        writer.write("$\\bot$");
      } else {
        writer.write(nodeAtomLabel("$(" + nodeAtomLabel(labellingMap.get(state.getCondition())) + ", " + state.getChoice() + ")$"));
      }
      writer.write(" |\n");
    }
    writer.write("\n");
  }

  public static void dumpMapOfProgramPointToBranch(String dirPath, Writer writer, String ID, Map<ProgramPoint, Branch> map) throws IOException {
    writer.write("# " + ID + "\n");
    dumpMapOfProgramPointToBranchAsTable(writer, ID, map);
    dumpMapOfProgramPointToBranchAsGraph(dirPath, writer, ID, map);
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

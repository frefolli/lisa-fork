package it.unive.lisa.analysis.avase;

import it.unive.lisa.program.cfg.ProgramPoint;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.List;
import java.util.HashSet;
import java.util.TreeSet;
import java.util.Comparator;
import java.io.FileOutputStream;
import java.io.Writer;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.io.IOException;
import it.unive.lisa.program.cfg.CFG;
import it.unive.lisa.program.cfg.edge.Edge;
import it.unive.lisa.program.cfg.statement.Statement;
import it.unive.lisa.analysis.lattices.SetLattice;

public class Documenter {
  private static class ProgramPointComparator implements Comparator<ProgramPoint> {
    public boolean equals(ProgramPoint pp1, ProgramPoint pp2) {
      return compare(pp1, pp2) == 0;
    }

    public int compare(ProgramPoint pp1, ProgramPoint pp2) {
      Map<ProgramPoint, Integer> labellingMap = DataflowStateMap.getLabellingMap();
      Integer labelPP1 = labellingMap.get(pp1);
      Integer labelPP2 = labellingMap.get(pp2);
      if (labelPP1 == null) {
        throw new AvaseImplException("Trying to l-compare node " + pp1 + ", which has not yet a label");
      }
      if (labelPP2 == null) {
        throw new AvaseImplException("Trying to r-compare node " + pp2 + ", which has not yet a label");
      }
      return labelPP1.compareTo(labelPP2);
    }
  }

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
      List<String> codes = List.of("CFGS", "LABEL",
                                   "POD", "IPOD", "rPOD", "rIPOD",
                                   "PED", "IPED", "rPED", "rIPED",
                                   "POF", "PEF",
                                   "CB", "CD",
                                   "RD", "KD",
                                   "AD", "ED",
                                   "AV");

      writer.write("# Index \n");
      for (String code : codes) {
        writer.write(" - [" + code + "](#user-content-" + code.toLowerCase() + ")\n");
      }
      writer.write("\n");
      writer.write("\n");

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
      dumpMapOfProgramPointToSetOfBranches(dirPath, writer, "CD", DataflowStateMap.getControlDependenciesMap());
      dumpMapOfProgramPointToSetLatticeOfDefinitions(dirPath, writer, "RD", DataflowStateMap.getReachingDefinitionsMap());
      dumpMapOfProgramPointToSetLatticeOfDefinitions(dirPath, writer, "KD", DataflowStateMap.getKilledDefinitionsMap());
      dumpMapOfProgramPointToSetLatticeOfDefinitions(dirPath, writer, "AD", DataflowStateMap.getAvailableDefinitionsMap());
      dumpMapOfProgramPointToSetLatticeOfDefinitions(dirPath, writer, "ED", DataflowStateMap.getEmergingDefinitionsMap());
      dumpMapOfProgramPointToSetLatticeOfSymbolicValues(dirPath, writer, "AV", DataflowStateMap.getAllValuesMap());
    }
  }

  public static SortedSet<ProgramPoint> getSortedViewOfKeys(Map<ProgramPoint, ?> map) {
    SortedSet<ProgramPoint> nodes = new TreeSet<>(new ProgramPointComparator());
    for (ProgramPoint pp : map.keySet()) {
      nodes.add(pp);
    }
    return nodes;
  }

  public static void dumpMapOfProgramPointToString(Writer writer, String ID, Map<ProgramPoint, Integer> map) throws IOException {
    Map<ProgramPoint, Integer> labellingMap = DataflowStateMap.getLabellingMap();
    writer.write("# " + ID + "\n");
    writer.write("|  N  | " + ID + " |\n");
    writer.write("| --- | --- |\n");
    for (ProgramPoint pp : getSortedViewOfKeys(map)) {
      writer.write("| " + nodeLabel(labellingMap.get(pp).toString()) + " | " + pp + " |\n");
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
    Map<ProgramPoint, Integer> labellingMap = DataflowStateMap.getLabellingMap();
    for (ProgramPoint node : nodes) {
      writer.write(nodeId(labellingMap.get(node).toString()) + " : \"" + nodeId(labellingMap.get(node).toString()) + " | " + node + "\"\n");
    }
    for (ProgramPoint node : nodes) {
      for (Edge edge : node.getCFG().getOutgoingEdges((Statement)node)) {
        writer.write(nodeId(labellingMap.get(edge.getSource()).toString()) + " -> " + nodeId(labellingMap.get(edge.getDestination()).toString()) + "\n");
      }
    }
  }

  public static void dumpMapOfProgramPointToSetOfProgramPointsAsGraph(String dirPath, Writer writer, String ID, Map<ProgramPoint, Set<ProgramPoint>> map) throws IOException {
    String d2File = dirPath + "/pp2setpp-" + ID + ".d2";
    String pngFile = "pp2setpp-" + ID + ".png";
    writer.write("![./" + pngFile + "](./" + pngFile + ")\n\n");
    try (Writer writerD2 = new OutputStreamWriter(new FileOutputStream(d2File), StandardCharsets.UTF_8.newEncoder())) {
      Map<ProgramPoint, Integer> labellingMap = DataflowStateMap.getLabellingMap();

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
        writerD2.write(nodeId(labellingMap.get(node).toString()) + " : \"" + nodeId(labellingMap.get(node).toString()) + " | " + node + "\"\n");
      }
      for (ProgramPoint source : getSortedViewOfKeys(map)) {
        for (ProgramPoint sink : map.get(source)) {
          writerD2.write(nodeId(labellingMap.get(source).toString()) + " -> " + nodeId(labellingMap.get(sink).toString()) + "\n");
        }
      }
    }
    compileWithD2(d2File, dirPath + "/" + pngFile);
  }

  public static void dumpMapOfProgramPointToSetOfProgramPointsAsTable(Writer writer, String ID, Map<ProgramPoint, Set<ProgramPoint>> map) throws IOException {
    Map<ProgramPoint, Integer> labellingMap = DataflowStateMap.getLabellingMap();
    writer.write("|  N  | " + ID + " |\n");
    writer.write("| --- | --- |\n");
    for (ProgramPoint pp : getSortedViewOfKeys(map)) {
      Set<ProgramPoint> state = map.get(pp);
      writer.write("| " + nodeLabel(labellingMap.get(pp).toString()) + " | $\\{");
      boolean first = true;
      for (ProgramPoint peer : state) {
        if (first) {
          first = false;
        } else {
          writer.write(", ");
        }
        writer.write(nodeAtomLabel(labellingMap.get(peer).toString()));
      }
      writer.write("\\}$ |\n");
    }
    writer.write("\n");
  }

  public static void dumpMapOfProgramPointToSetOfProgramPoints(String dirPath, Writer writer, String ID, Map<ProgramPoint, Set<ProgramPoint>> map) throws IOException {
    writer.write("# " + ID + "\n");
    dumpMapOfProgramPointToSetOfProgramPointsAsTable(writer, ID, map);
    // dumpMapOfProgramPointToSetOfProgramPointsAsGraph(dirPath, writer, ID, map);
  }

  public static void dumpMapOfProgramPointToBranchAsGraph(String dirPath, Writer writer, String ID, Map<ProgramPoint, Branch> map) throws IOException {
    String d2File = dirPath + "/pp2setpp-" + ID + ".d2";
    String pngFile = "pp2setpp-" + ID + ".png";
    writer.write("![./" + pngFile + "](./" + pngFile + ")\n\n");
    try (Writer writerD2 = new OutputStreamWriter(new FileOutputStream(d2File), StandardCharsets.UTF_8.newEncoder())) {
      Map<ProgramPoint, Integer> labellingMap = DataflowStateMap.getLabellingMap();

      Set<ProgramPoint> nodes = new HashSet<>();
      for (ProgramPoint source : map.keySet()) {
        Branch sink = map.get(source);
        if (!sink.isBottom()) {
          nodes.add(source);
          nodes.add(sink.condition);
        }
      }

      for (ProgramPoint node : nodes) {
        writerD2.write(nodeId(labellingMap.get(node).toString()) + " : \"" + nodeId(labellingMap.get(node).toString()) + " | " + node + "\"\n");
      }
      for (ProgramPoint source : getSortedViewOfKeys(map)) {
        Branch sink = map.get(source);
        if (!sink.isBottom()) {
          writerD2.write(nodeId(labellingMap.get(source).toString()) + " -> " + nodeId(labellingMap.get(sink.condition).toString()) + " : " + sink.choice + "\n");
        }
      }
    }
    compileWithD2(d2File, dirPath + "/" + pngFile);
  }

  public static void dumpMapOfProgramPointToBranchAsTable(Writer writer, String ID, Map<ProgramPoint, Branch> map) throws IOException {
    Map<ProgramPoint, Integer> labellingMap = DataflowStateMap.getLabellingMap();
    writer.write("|  N  | " + ID + " |\n");
    writer.write("| --- | --- |\n");
    for (ProgramPoint pp : getSortedViewOfKeys(map)) {
      Branch state = map.get(pp);
      writer.write("| " + nodeLabel(labellingMap.get(pp).toString()) + " | ");
      if (state.isBottom()) {
        writer.write("$\\bot$");
      } else {
        writer.write(nodeAtomLabel("$(" + nodeAtomLabel(labellingMap.get(state.condition).toString()) + ", " + state.choice + ")$"));
      }
      writer.write(" |\n");
    }
    writer.write("\n");
  }

  public static void dumpMapOfProgramPointToBranch(String dirPath, Writer writer, String ID, Map<ProgramPoint, Branch> map) throws IOException {
    writer.write("# " + ID + "\n");
    dumpMapOfProgramPointToBranchAsTable(writer, ID, map);
    // dumpMapOfProgramPointToBranchAsGraph(dirPath, writer, ID, map);
  }

  public static void dumpMapOfProgramPointToSetOfBranchesAsGraph(String dirPath, Writer writer, String ID, Map<ProgramPoint, Set<Branch>> map) throws IOException {
    String d2File = dirPath + "/pp2setpp-" + ID + ".d2";
    String pngFile = "pp2setpp-" + ID + ".png";
    writer.write("![./" + pngFile + "](./" + pngFile + ")\n\n");
    try (Writer writerD2 = new OutputStreamWriter(new FileOutputStream(d2File), StandardCharsets.UTF_8.newEncoder())) {
      Map<ProgramPoint, Integer> labellingMap = DataflowStateMap.getLabellingMap();
      Set<ProgramPoint> nodes = new HashSet<>();
      for (ProgramPoint source : map.keySet()) {
        if (!map.get(source).isEmpty()) {
          nodes.add(source);
        }
        for (Branch sink : map.get(source)) {
          nodes.add(sink.condition);
        }
      }

      for (ProgramPoint node : nodes) {
        writerD2.write(nodeId(labellingMap.get(node).toString()) + " : \"" + nodeId(labellingMap.get(node).toString()) + " | " + node + "\"\n");
      }
      for (ProgramPoint source : getSortedViewOfKeys(map)) {
        for (Branch sink : map.get(source)) {
          writerD2.write(nodeId(labellingMap.get(source).toString()) + " -> " + nodeId(labellingMap.get(sink.condition).toString()) + " : " + sink.choice + "\n");
        }
      }
    }
    compileWithD2(d2File, dirPath + "/" + pngFile);
  }

  public static void dumpMapOfProgramPointToSetOfBranchesAsTable(Writer writer, String ID, Map<ProgramPoint, Set<Branch>> map) throws IOException {
    Map<ProgramPoint, Integer> labellingMap = DataflowStateMap.getLabellingMap();
    writer.write("|  N  | " + ID + " |\n");
    writer.write("| --- | --- |\n");
    for (ProgramPoint pp : getSortedViewOfKeys(map)) {
      Set<Branch> state = map.get(pp);
      writer.write("| " + nodeLabel(labellingMap.get(pp).toString()) + " | $\\{");
      boolean first = true;
      for (Branch peer : state) {
        if (first) {
          first = false;
        } else {
          writer.write(", ");
        }
        writer.write("(" + nodeAtomLabel(labellingMap.get(peer.condition).toString()) + ", " + peer.choice + ")");
      }
      writer.write("\\}$ |\n");
    }
    writer.write("\n");
  }

  public static void dumpMapOfProgramPointToSetOfBranches(String dirPath, Writer writer, String ID, Map<ProgramPoint, Set<Branch>> map) throws IOException {
    writer.write("# " + ID + "\n");
    dumpMapOfProgramPointToSetOfBranchesAsTable(writer, ID, map);
    dumpMapOfProgramPointToSetOfBranchesAsGraph(dirPath, writer, ID, map);
  }

  public static void dumpMapOfProgramPointToSetLatticeOfDefinitionsAsTable(Writer writer, String ID, Map<ProgramPoint, ? extends SetLattice<?, Definition>> map) throws IOException {
    Map<ProgramPoint, Integer> labellingMap = DataflowStateMap.getLabellingMap();
    writer.write("|  N  | " + ID + " |\n");
    writer.write("| --- | --- |\n");
    for (ProgramPoint pp : getSortedViewOfKeys(map)) {
      Set<Definition> state = map.get(pp).elements;
      writer.write("| " + nodeLabel(labellingMap.get(pp).toString()) + " | $\\{");
      boolean first = true;
      for (Definition peer : state) {
        if (first) {
          first = false;
        } else {
          writer.write(", ");
        }
        writer.write("(" + nodeAtomLabel(labellingMap.get(peer.programPoint).toString()) + ", " + peer.variable + ")");
      }
      writer.write("\\}$ |\n");
    }
    writer.write("\n");
  }

  public static void dumpMapOfProgramPointToSetLatticeOfDefinitions(String dirPath, Writer writer, String ID, Map<ProgramPoint, ? extends SetLattice<?, Definition>> map) throws IOException {
    writer.write("# " + ID + "\n");
    dumpMapOfProgramPointToSetLatticeOfDefinitionsAsTable(writer, ID, map);
  }

  public static void dumpMapOfProgramPointToSetLatticeOfSymbolicValuesAsTable(Writer writer, String ID, Map<ProgramPoint, ? extends SetLattice<?, SymbolicValue>> map) throws IOException {
    Map<ProgramPoint, Integer> labellingMap = DataflowStateMap.getLabellingMap();
    writer.write("|  N  | " + ID + " |\n");
    writer.write("| --- | --- |\n");
    for (ProgramPoint pp : getSortedViewOfKeys(map)) {
      Set<SymbolicValue> state = map.get(pp).elements;
      writer.write("| " + nodeLabel(labellingMap.get(pp).toString()) + " | $\\{");
      boolean first = true;
      for (SymbolicValue peer : state) {
        if (first) {
          first = false;
        } else {
          writer.write(", ");
        }
        writer.write("(" + peer.value + ", " + peer.condition + ")");
      }
      writer.write("\\}$ |\n");
    }
    writer.write("\n");
  }

  public static void dumpMapOfProgramPointToSetLatticeOfSymbolicValues(String dirPath, Writer writer, String ID, Map<ProgramPoint, ? extends SetLattice<?, SymbolicValue>> map) throws IOException {
    writer.write("# " + ID + "\n");
    dumpMapOfProgramPointToSetLatticeOfSymbolicValuesAsTable(writer, ID, map);
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

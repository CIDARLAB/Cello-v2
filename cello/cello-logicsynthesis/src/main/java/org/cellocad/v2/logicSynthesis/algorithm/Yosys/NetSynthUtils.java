/*
 * Copyright (C) 2020 Boston University (BU)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package org.cellocad.v2.logicSynthesis.algorithm.Yosys;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.io.FileUtils;
import org.cellocad.BU.dom.DAGW;
import org.cellocad.BU.netsynth.NetSynth;
import org.cellocad.BU.netsynth.NetSynthSwitch;
import org.cellocad.MIT.dnacompiler.Gate;
import org.cellocad.MIT.dnacompiler.Wire;
import org.cellocad.v2.common.CelloException;
import org.cellocad.v2.common.Utils;
import org.cellocad.v2.common.graph.algorithm.SinkDFS;
import org.cellocad.v2.results.logicSynthesis.LSResults;
import org.cellocad.v2.results.logicSynthesis.LSResultsUtils;
import org.cellocad.v2.results.logicSynthesis.netlist.LSResultNetlistUtils;
import org.cellocad.v2.results.netlist.Netlist;
import org.cellocad.v2.results.netlist.NetlistEdge;
import org.cellocad.v2.results.netlist.NetlistNode;
import org.cellocad.v2.results.netlist.data.ResultNetlistData;
import org.json.JSONException;
import org.json.simple.JSONArray;

/**
 * Utility methods for processing netlists with NetSynth.
 *
 * @author Timothy Jones
 * @date 2020-01-29
 */
public class NetSynthUtils {

  private static class NodeNamer {

    public String next() {
      String rtn = null;
      i++;
      rtn = String.format("$%d", i);
      return rtn;
    }

    private int i = 0;
  }

  private static class EdgeNamer {

    public String next(final NetlistNode n1, final NetlistNode n2) {
      String rtn = null;
      i++;
      rtn = String.format("e%d__%s_%s", i, n1.getName(), n2.getName());
      return rtn;
    }

    private int i = 0;
  }

  /**
   * Escape special characters in the given string before NetSynth processing.
   *
   * @param str A string.
   * @return The string with escaped special characters.
   */
  public static String escapeSpecialCharacters(final String str) {
    String rtn = null;
    rtn = str.replace("$", "\\$");
    return rtn;
  }

  /**
   * Get a structural Verilog representation of the given netlist.
   *
   * @param netlist A netlist.
   * @return A structural Verilog representation of the given netlist.
   */
  public static String getVerilog(final Netlist netlist) {
    String rtn = "";
    final Collection<String> input = new ArrayList<>();
    final Collection<String> output = new ArrayList<>();
    for (int i = 0; i < netlist.getNumVertex(); i++) {
      final NetlistNode node = netlist.getVertexAtIdx(i);
      if (LSResultsUtils.isPrimaryInput(node)) {
        input.add(NetSynthUtils.escapeSpecialCharacters(node.getName()));
      }
      if (LSResultsUtils.isPrimaryOutput(node)) {
        output.add(NetSynthUtils.escapeSpecialCharacters(node.getName()));
      }
    }
    final String i = String.join(", ", input);
    final String o = String.join(", ", output);
    rtn += String.format("module %s (input %s, output %s);", netlist.getName(), i, o);
    rtn += Utils.getNewLine() + Utils.getNewLine();
    for (int j = 0; j < netlist.getNumEdge(); j++) {
      final NetlistEdge e = netlist.getEdgeAtIdx(j);
      if (input.contains(e.getSrc().getName())) {
        continue;
      }
      if (output.contains(e.getDst().getName())) {
        continue;
      }
      rtn += Utils.getTabCharacter();
      rtn += String.format("wire %s;", NetSynthUtils.escapeSpecialCharacters(e.getName()));
      rtn += Utils.getNewLine();
    }
    rtn += Utils.getNewLine();
    LSResultNetlistUtils.setVertexTypeUsingLSResult(netlist);
    final SinkDFS<NetlistNode, NetlistEdge, Netlist> DFS = new SinkDFS<>(netlist);
    NetlistNode node = null;
    while ((node = DFS.getNextVertex()) != null) {
      if (LSResultsUtils.isPrimaryInput(node)) {
        continue;
      }
      final String nodeType = node.getResultNetlistNodeData().getNodeType().toLowerCase();
      for (int k = 0; k < node.getNumOutEdge(); k++) {
        final NetlistEdge e = node.getOutEdgeAtIdx(k);
        final NetlistNode dst = e.getDst();
        String y = "";
        final Collection<String> args = new ArrayList<>();
        if (LSResultsUtils.isPrimaryOutput(dst)) {
          y = NetSynthUtils.escapeSpecialCharacters(dst.getName());
        } else {
          y = NetSynthUtils.escapeSpecialCharacters(e.getName());
        }
        for (int l = 0; l < node.getNumInEdge(); l++) {
          final NetlistEdge f = node.getInEdgeAtIdx(l);
          final NetlistNode src = f.getSrc();
          if (LSResultsUtils.isPrimaryInput(src)) {
            args.add(NetSynthUtils.escapeSpecialCharacters(src.getName()));
          } else {
            args.add(NetSynthUtils.escapeSpecialCharacters(f.getName()));
          }
        }
        rtn += Utils.getTabCharacter();
        rtn += String.format("%s (%s, %s);", nodeType, y, String.join(", ", args));
        rtn += Utils.getNewLine();
      }
    }
    rtn += Utils.getNewLine();
    rtn += "endmodule";
    rtn += Utils.getNewLine();
    return rtn;
  }

  private static org.json.JSONArray getMotifJson(final JSONArray motifs) throws JSONException {
    final org.json.JSONArray rtn = new org.json.JSONArray();
    for (int i = 0; i < motifs.size(); ++i) {
      final String objString = motifs.get(i).toString();
      rtn.put(new org.json.JSONObject(objString));
    }
    return rtn;
  }

  private static File copyResource(final Path resource, final Path directory) throws IOException {
    File rtn = null;
    rtn = new File(directory.toString(), resource.getFileName().toString());
    final InputStream is = Utils.getResourceAsStream(resource.toString());
    FileUtils.copyInputStreamToFile(is, rtn);
    return rtn;
  }

  private static Path initResources() throws IOException, CelloException {
    Path rtn = null;
    rtn = Files.createTempDirectory("cello_");
    final String sourceBase = "netsynthResources";
    final String targetBase = "resources" + Utils.getFileSeparator() + "netsynthResources";
    final Path targetPath = Files.createDirectories(Paths.get(rtn.toString(), targetBase));
    Path p = null;
    List<String> files = null;
    if (Utils.isMac()) {
      files = Arrays.asList(new String[] {"espresso.mac", "abc.mac", "script"});
    }
    if (Utils.isUnix()) {
      files = Arrays.asList(new String[] {"espresso.linux", "abc", "script"});
    }
    if (Utils.isWin()) {
      files = Arrays.asList(new String[] {"espresso.exe", "abc.exe", "script.cmd"});
    }
    for (final String file : files) {
      p = Paths.get(sourceBase, file);
      File f = null;
      Boolean a = false;
      f = NetSynthUtils.copyResource(p, targetPath);
      a = f.setExecutable(true);
      Boolean b = false;
      f = NetSynthUtils.copyResource(p, rtn);
      b = f.setExecutable(true);
      if (!(a && b)) {
        throw new CelloException("Unable to set executable permissions on file " + f.toString());
      }
    }
    files =
        Arrays.asList(
            new String[] {
              "abc.rc", "netlist_in3out1.json", "netlist_in3out1_OR.json", "tempVerilog.v"
            });
    for (final String file : files) {
      p = Paths.get(sourceBase, file);
      NetSynthUtils.copyResource(p, targetPath);
      NetSynthUtils.copyResource(p, rtn);
    }
    return rtn;
  }

  /**
   * Gets the {@link LSResults} node type from the given NetSynth gate type.
   *
   * @param type The NetSynth gate type.
   * @return The {@link LSResults} node type from the given NetSynth gate type.
   */
  public static String getNodeType(final Gate.GateType type) {
    String rtn = "";
    if (type.equals(Gate.GateType.AND)) {
      rtn = LSResults.S_AND;
    } else if (type.equals(Gate.GateType.NOR)) {
      rtn = LSResults.S_NOR;
    } else if (type.equals(Gate.GateType.NOT)) {
      rtn = LSResults.S_NOT;
    } else if (type.equals(Gate.GateType.NAND)) {
      rtn = LSResults.S_NAND;
    } else if (type.equals(Gate.GateType.XOR)) {
      rtn = LSResults.S_XOR;
    } else if (type.equals(Gate.GateType.XNOR)) {
      rtn = LSResults.S_XNOR;
    } else if (type.equals(Gate.GateType.OR)) {
      rtn = LSResults.S_OR;
    } else if (type.equals(Gate.GateType.OUTPUT)) {
      rtn = LSResults.S_PRIMARYOUTPUT;
    } else if (type.equals(Gate.GateType.INPUT)) {
      rtn = LSResults.S_PRIMARYINPUT;
    } else if (type.equals(Gate.GateType.OUTPUT_OR)) {
      rtn = LSResults.S_PRIMARYOUTPUT;
    }
    return rtn;
  }

  /**
   * Run NetSynth on the given netlist.
   *
   * @param netlist A netlist.
   * @param motifs The motif library.
   * @param outputDir The output directory.
   * @return A NetSynth-processed netlist.
   * @throws JSONException Unable to parse JSON motif library.
   * @throws IOException Unable to read resources.
   * @throws CelloException Unable to generate NetSynth-processed netlist.
   */
  public static Netlist getNetSynthNetlist(
      final Netlist netlist, final JSONArray motifs, final String outputDir)
      throws JSONException, IOException, CelloException {
    Netlist rtn = null;
    final Path path = NetSynthUtils.initResources();
    final NetSynth n =
        new NetSynth("netSynth", path.toString() + Utils.getFileSeparator(), outputDir);
    // verilog
    final String verilog = NetSynthUtils.getVerilog(netlist);
    final String verilogFilePath =
        outputDir
            + Utils.getFileSeparator()
            + Utils.getFilename(netlist.getInputFilename())
            + ".struct.v";
    Utils.writeToFile(verilog, verilogFilePath);
    // args
    final List<NetSynthSwitch> args = new ArrayList<>();
    args.add(NetSynthSwitch.output_or);
    // motifs
    final org.json.JSONArray m = NetSynthUtils.getMotifJson(motifs);
    // netsynth
    final DAGW dagw = n.runNetSynth(verilogFilePath, args, m);
    // clean
    n.cleanDirectory();
    FileUtils.deleteDirectory(path.toFile());
    // netlist
    rtn = new Netlist();
    rtn.setName(netlist.getName());
    rtn.setType(netlist.getType());
    rtn.setIdx(netlist.getIdx());
    rtn.setInputFilename(netlist.getInputFilename());
    rtn.setResultNetlistData(new ResultNetlistData(netlist.getResultNetlistData()));
    final Map<Gate, NetlistNode> nodeMap = new HashMap<>();
    final NodeNamer nNamer = new NodeNamer();
    final EdgeNamer eNamer = new EdgeNamer();
    for (final Wire w : dagw.Wires) {
      final NetlistEdge e = new NetlistEdge();
      NetlistNode src = nodeMap.get(w.to);
      if (w.to.equals(w.from)) {
        continue;
      }
      if (w.to.name.isEmpty()) {
        w.to.name = nNamer.next();
      }
      if (src == null) {
        src = new NetlistNode();
        src.setName(w.to.name);
        src.getResultNetlistNodeData().setNodeType(NetSynthUtils.getNodeType(w.to.type));
        nodeMap.put(w.to, src);
        rtn.addVertex(src);
      }
      NetlistNode dst = nodeMap.get(w.from);
      if (w.from.name.isEmpty()) {
        w.from.name = nNamer.next();
      }
      if (dst == null) {
        dst = new NetlistNode();
        dst.setName(w.from.name);
        dst.getResultNetlistNodeData().setNodeType(NetSynthUtils.getNodeType(w.from.type));
        nodeMap.put(w.from, dst);
        rtn.addVertex(dst);
      }
      e.setSrc(src);
      e.setDst(dst);
      e.setName(eNamer.next(src, dst));
      dst.addInEdge(e);
      src.addOutEdge(e);
      rtn.addEdge(e);
    }
    return rtn;
  }
}

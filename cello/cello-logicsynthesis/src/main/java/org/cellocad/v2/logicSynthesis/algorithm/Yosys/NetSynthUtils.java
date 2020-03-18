/**
 * Copyright (C) 2020 Boston University (BU)
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:

 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.

 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
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
 *
 *
 * @author Timothy Jones
 *
 * @date 2020-01-29
 *
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

		public String next(NetlistNode n1, NetlistNode n2) {
			String rtn = null;
			i++;
			rtn = String.format("e%d__%s_%s", i, n1.getName(), n2.getName());
			return rtn;
		}

		private int i = 0;

	}

	public static String escapeSpecialCharacters(String str) {
		String rtn = null;
		rtn = str.replace("$", "\\$");
		return rtn;
	}

	public static String getVerilog(final Netlist netlist) {
		String rtn = "";
		Collection<String> input = new ArrayList<>();
		Collection<String> output = new ArrayList<>();
		for (int i = 0; i < netlist.getNumVertex(); i++) {
			NetlistNode node = netlist.getVertexAtIdx(i);
			if (LSResultsUtils.isPrimaryInput(node))
				input.add(escapeSpecialCharacters(node.getName()));
			if (LSResultsUtils.isPrimaryOutput(node))
				output.add(escapeSpecialCharacters(node.getName()));
		}
		String i = String.join(", ", input);
		String o = String.join(", ", output);
		rtn += String.format("module %s (input %s, output %s);", netlist.getName(), i, o);
		rtn += Utils.getNewLine() + Utils.getNewLine();
		for (int j = 0; j < netlist.getNumEdge(); j++) {
			NetlistEdge e = netlist.getEdgeAtIdx(j);
			if (input.contains(e.getSrc().getName()))
				continue;
			if (output.contains(e.getDst().getName()))
				continue;
			rtn += Utils.getTabCharacter();
			rtn += String.format("wire %s;", escapeSpecialCharacters(e.getName()));
			rtn += Utils.getNewLine();
		}
		rtn += Utils.getNewLine();
		LSResultNetlistUtils.setVertexTypeUsingLSResult(netlist);
		SinkDFS<NetlistNode, NetlistEdge, Netlist> DFS = new SinkDFS<NetlistNode, NetlistEdge, Netlist>(netlist);
		NetlistNode node = null;
		while ((node = DFS.getNextVertex()) != null) {
			if (LSResultsUtils.isPrimaryInput(node))
				continue;
			String nodeType = node.getResultNetlistNodeData().getNodeType().toLowerCase();
			for (int k = 0; k < node.getNumOutEdge(); k++) {
				NetlistEdge e = node.getOutEdgeAtIdx(k);
				NetlistNode dst = e.getDst();
				String y = "";
				Collection<String> args = new ArrayList<>();
				if (LSResultsUtils.isPrimaryOutput(dst))
					y = escapeSpecialCharacters(dst.getName());
				else
					y = escapeSpecialCharacters(e.getName());
				for (int l = 0; l < node.getNumInEdge(); l++) {
					NetlistEdge f = node.getInEdgeAtIdx(l);
					NetlistNode src = f.getSrc();
					if (LSResultsUtils.isPrimaryInput(src))
						args.add(escapeSpecialCharacters(src.getName()));
					else
						args.add(escapeSpecialCharacters(f.getName()));
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

	private static org.json.JSONArray getMotifJSON(JSONArray motifs) throws JSONException {
		org.json.JSONArray rtn = new org.json.JSONArray();
		for (int i = 0; i < motifs.size(); ++i) {
			String objString = motifs.get(i).toString();
			rtn.put(new org.json.JSONObject(objString));
		}
		return rtn;
	}

	private static File copyResource(Path resource, Path directory) throws IOException {
		File rtn = null;
		rtn = new File(directory.toString(), resource.getFileName().toString());
		InputStream is = Utils.getResourceAsStream(resource.toString());
		FileUtils.copyInputStreamToFile(is, rtn);
		return rtn;
	}

	private static Path initResources() throws IOException, CelloException {
		Path rtn = null;
		rtn = Files.createTempDirectory("cello_");
		String sourceBase = "netsynthResources";
		String targetBase = "resources" + Utils.getFileSeparator() + "netsynthResources";
		Path targetPath = Files.createDirectories(Paths.get(rtn.toString(), targetBase));
		Path p = null;
		List<String> files = null;
		if (Utils.isMac())
			files = Arrays.asList(new String[] { "espresso.mac", "abc.mac", "script" });
		if (Utils.isUnix())
			files = Arrays.asList(new String[] { "espresso.linux", "abc", "script" });
		if (Utils.isWin())
			files = Arrays.asList(new String[] { "espresso.exe", "abc.exe", "script.cmd" });
		for (String file : files) {
			p = Paths.get(sourceBase, file);
			File f = null;
			Boolean a = false;
			Boolean b = false;
			f = copyResource(p, targetPath);
			a = f.setExecutable(true);
			f = copyResource(p, rtn);
			b = f.setExecutable(true);
			if (!(a && b)) {
				throw new CelloException("Unable to set executable permissions on file " + f.toString());
			}
		}
		p = Paths.get(sourceBase, "abc.rc");
		copyResource(p, targetPath);
		copyResource(p, rtn);
		return rtn;
	}

	public static String getNodeType(Gate.GateType type) {
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

	public static Netlist getNetSynthNetlist(final Netlist netlist, final JSONArray motifs, final String outputDir)
			throws JSONException, IOException, CelloException {
		Netlist rtn = null;
		Path path = initResources();
		System.out.println(path);
		NetSynth n = new NetSynth("netSynth", path.toString() + Utils.getFileSeparator(), outputDir);
		// verilog
		String verilog = getVerilog(netlist);
		String verilogFilePath = outputDir + Utils.getFileSeparator() + Utils.getFilename(netlist.getInputFilename())
				+ ".struct.v";
		Utils.writeToFile(verilog, verilogFilePath);
		// args
		List<NetSynthSwitch> args = new ArrayList<>();
		args.add(NetSynthSwitch.output_or);
		// motifs
		org.json.JSONArray m = getMotifJSON(motifs);
		// netsynth
		DAGW dagw = n.runNetSynth(verilogFilePath, args, m);
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
		Map<Gate,NetlistNode> nodeMap = new HashMap<>();
		NodeNamer nNamer = new NodeNamer();
		EdgeNamer eNamer = new EdgeNamer();
		for (Wire w : dagw.Wires) {
			NetlistEdge e = new NetlistEdge();
			NetlistNode src = nodeMap.get(w.to);
			if (w.to.equals(w.from))
				continue;
			if (w.to.name.isEmpty())
				w.to.name = nNamer.next();
			if (src == null) {
				src = new NetlistNode();
				src.setName(w.to.name);
				src.getResultNetlistNodeData().setNodeType(getNodeType(w.to.type));
				nodeMap.put(w.to, src);
				rtn.addVertex(src);
			}
			NetlistNode dst = nodeMap.get(w.from);
			if (w.from.name.isEmpty())
				w.from.name = nNamer.next();
			if (dst == null) {
				dst = new NetlistNode();
				dst.setName(w.from.name);
				dst.getResultNetlistNodeData().setNodeType(getNodeType(w.from.type));
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

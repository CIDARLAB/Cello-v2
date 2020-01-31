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
package org.cellocad.cello2.logicSynthesis.algorithm.Yosys;

import java.util.ArrayList;
import java.util.Collection;

import org.cellocad.cello2.common.Utils;
import org.cellocad.cello2.results.logicSynthesis.LSResultsUtils;
import org.cellocad.cello2.results.netlist.Netlist;
import org.cellocad.cello2.results.netlist.NetlistEdge;
import org.cellocad.cello2.results.netlist.NetlistNode;

/**
 *
 *
 * @author Timothy Jones
 *
 * @date 2020-01-29
 *
 */
public class NetSynthUtils {

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
		for (int j = 0; j < netlist.getNumVertex(); j++) {
			NetlistNode node = netlist.getVertexAtIdx(j);
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

	public static Netlist getNetSynthNetlist(Netlist netlist) {
		Netlist rtn = null;
		return rtn;
	}

}

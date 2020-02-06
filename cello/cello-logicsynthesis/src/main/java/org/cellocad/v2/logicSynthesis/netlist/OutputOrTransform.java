/**
 * Copyright (C) 2019 Boston University (BU)
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
package org.cellocad.v2.logicSynthesis.netlist;

import java.util.Collection;

import org.cellocad.v2.common.CObject;
import org.cellocad.v2.results.logicSynthesis.LSResults;
import org.cellocad.v2.results.logicSynthesis.LSResultsUtils;
import org.cellocad.v2.results.netlist.Netlist;
import org.cellocad.v2.results.netlist.NetlistEdge;
import org.cellocad.v2.results.netlist.NetlistNode;

/**
 * The OutputOrTransform class transforms the structure of the Netlist object to include an OR operation at the output if applicable.
 *
 * @author Timothy Jones
 *
 * @date 2019-05-08
 *
 */
public class OutputOrTransform extends CObject {

	public OutputOrTransform(Netlist netlist) {
		Collection<NetlistNode> outputNodes = LSResultsUtils.getPrimaryOutputNodes(netlist);
		for (NetlistNode out : outputNodes) {
			NetlistEdge e1 = out.getInEdgeAtIdx(0);
			NetlistNode src1 = e1.getSrc();
			if (src1.getResultNetlistNodeData().getNodeType().equals(LSResults.S_NOT)) {
				for (int j = 0; j < src1.getNumInEdge(); j++) {
					NetlistEdge e2 = src1.getInEdgeAtIdx(j);
					NetlistNode src2 = e2.getSrc();
					if (src2.getResultNetlistNodeData().getNodeType().equals(LSResults.S_NOR)) {
						NetlistEdge e3 = src2.getInEdgeAtIdx(0);
						NetlistEdge e4 = src2.getInEdgeAtIdx(1);
						NetlistNode src3 = e3.getSrc();
						NetlistNode src4 = e4.getSrc();
						netlist.removeEdge(e1);
						netlist.removeEdge(e2);
						netlist.removeEdge(e3);
						netlist.removeEdge(e4);
						netlist.removeVertex(src1);
						netlist.removeVertex(src2);
						out.removeInEdge(e1);
						src3.removeOutEdge(e3);
						src4.removeOutEdge(e4);
						e3 = new NetlistEdge(src3,out);
						e4 = new NetlistEdge(src4,out);
						out.addInEdge(e3);
						out.addInEdge(e4);
						src3.addOutEdge(e3);
						src4.addOutEdge(e4);
						netlist.addEdge(e3);
						netlist.addEdge(e4);
					}
				}
			}
		}
	}
	
}

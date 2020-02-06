/**
 * Copyright (C) 2017 Massachusetts Institute of Technology (MIT)
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
package org.cellocad.v2.results.logicSynthesis.netlist;

import org.cellocad.v2.common.graph.AbstractVertex.VertexType;
import org.cellocad.v2.results.logicSynthesis.LSResultsUtils;
import org.cellocad.v2.results.netlist.Netlist;
import org.cellocad.v2.results.netlist.NetlistNode;

/**
 * The LSResultNetlistUtils class is class with utility methods for the Netlist instance using the result from the <i>logicSynthesis</i> stage.
 * @author Vincent Mirian
 * 
 * @date 2018-05-21
 *
 */
public class LSResultNetlistUtils {

	/**
	 * Set the <i>VertexType</i> of the NetlistNode depending on its NodeType in the Netlist defined by parameter <i>netlist</i>
	 * 
	 * @param netlist the Netlist
	 *
	 */
	static public void setVertexTypeUsingLSResult(Netlist netlist) {
		NetlistNode node = null;
		for (int i = 0; i < netlist.getNumVertex(); i++) {
			node = netlist.getVertexAtIdx(i);
			if (LSResultsUtils.isAllInput(node)) {
				node.setVertexType(VertexType.SOURCE);
			}
			else if (LSResultsUtils.isAllOutput(node)) {
				node.setVertexType(VertexType.SINK);
			}
			else {
				node.setVertexType(VertexType.NONE);
			}
		}
	}
	
}

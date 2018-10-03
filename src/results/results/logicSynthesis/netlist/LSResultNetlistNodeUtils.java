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
package results.logicSynthesis.netlist;

import common.CObjectCollection;
import results.logicSynthesis.LSResultsUtils;
import results.netlist.NetlistNode;

/**
 * The LSResultNetlistNodeUtils class is class with utility methods for the NetlistNode instance using the result from the <i>logicSynthesis</i> stage.
 * @author Vincent Mirian
 * 
 * @date 2018-05-21
 *
 */
public class LSResultNetlistNodeUtils {

	/**
	 * Returns the Collection of outputs nodes of the NetlistNode defined by parameter <i>node</i>
	 * 
	 * @param node the NetlistNode
	 * @return the Collection of outputs nodes of the NetlistNode defined by parameter <i>node</i>
	 *
	 */
	static public CObjectCollection<NetlistNode> getMyOutput(NetlistNode node) {
		CObjectCollection<NetlistNode> rtn = new CObjectCollection<NetlistNode>();
		for (int i = 0; i < node.getNumOutEdge(); i++) {
			NetlistNode outputNode = node.getOutEdgeAtIdx(i).getDst();
			if (LSResultsUtils.isOutput(outputNode)){
				rtn.add(outputNode);
			}
		}
		return rtn;
	}
}

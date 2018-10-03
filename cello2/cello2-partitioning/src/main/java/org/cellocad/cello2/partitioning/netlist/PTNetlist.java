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
package org.cellocad.cello2.partitioning.netlist;

import org.cellocad.cello2.common.Utils;
import org.cellocad.cello2.common.graph.graph.GraphTemplate;

/**
 * @author Vincent Mirian
 * 
 * @date Oct 30, 2017
 *
 */
public class PTNetlist extends GraphTemplate<PTNetlistNode, PTNetlistEdge> {

	public PTNetlist() {
		super();
	}
	
	public PTNetlist(final PTNetlist other) {
		super();
	}
	
	@Override
	public PTNetlistNode createV(final PTNetlistNode other) {
		PTNetlistNode rtn = null;
		rtn = new PTNetlistNode(other);
		return rtn;
	}

	@Override
	public PTNetlistEdge createE(final PTNetlistEdge other) {
		PTNetlistEdge rtn = null;
		rtn = new PTNetlistEdge(other);
		return rtn;
	}
	
	/**
	 * Returns a string containing the header in DOT (graph description language) format of this instance
	 * 
	 * @return a string containing the header in DOT (graph description language) format of this instance
	 */
	protected String getDotHeader(){
		String rtn = "";
		String inputs = "";
		String outputs = "";
		rtn = super.getDotHeader();
		rtn += "compound=true";
		rtn += Utils.getNewLine();
		rtn += "splines=ortho" + Utils.getNewLine(); // add lines
		// rank input and output
		for (int i = 0; i < this.getNumVertex(); i++) {
			PTNetlistNode node = this.getVertexAtIdx(i);
			if (PTNetlistNodeUtils.isPrimaryInput(node)) {
				inputs += "\"";
				inputs += node.getName();
				inputs += "\";";
			}
			if (PTNetlistNodeUtils.isPrimaryOutput(node)) {
				outputs += "\"";
				outputs += node.getName();
				outputs += "\";";
			}
		}
		if (!inputs.isEmpty()){
			rtn += "{rank = same; ";
			rtn += inputs;
			rtn += "}" + Utils.getNewLine();
		}
		if (!outputs.isEmpty()){
			rtn += "{rank = same; ";
			rtn += outputs;
			rtn += "}" + Utils.getNewLine();
		}
		return rtn;
	}
	
}

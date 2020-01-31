/**
 * Copyright (C) 2018 Boston University (BU)
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
package org.cellocad.cello2.technologyMapping.algorithm.SimulatedAnnealing;

import org.cellocad.cello2.results.netlist.Netlist;
import org.cellocad.cello2.results.netlist.NetlistEdge;
import org.cellocad.cello2.results.netlist.NetlistNode;
import org.cellocad.cello2.technologyMapping.algorithm.SimulatedAnnealing.data.SimulatedAnnealingNetlistData;
import org.cellocad.cello2.technologyMapping.algorithm.SimulatedAnnealing.data.SimulatedAnnealingNetlistEdgeData;
import org.cellocad.cello2.technologyMapping.algorithm.SimulatedAnnealing.data.SimulatedAnnealingNetlistNodeData;

/**
 * 
 *
 * @author Timothy Jones
 *
 * @date 2018-07-16
 *
 */
public class SimulatedAnnealingUtils {

	/**
	 * Returns the <i>SimulatedAnnealingNetlistNodeData</i> of the <i>node</i>
	 *
	 * @param node a node within the <i>netlist</i> of this instance
	 * @return the <i>SimulatedAnnealingNetlistNodeData</i> instance if it exists,
	 *         null otherwise
	 */
	public static SimulatedAnnealingNetlistNodeData getSimulatedAnnealingNetlistNodeData(NetlistNode node) {
		SimulatedAnnealingNetlistNodeData rtn = null;
		rtn = (SimulatedAnnealingNetlistNodeData) node.getNetlistNodeData();
		return rtn;
	}

	/**
	 * Returns the <i>SimulatedAnnealingNetlistEdgeData</i> of the <i>edge</i>
	 *
	 * @param edge an edge within the <i>netlist</i> of this instance
	 * @return the <i>SimulatedAnnealingNetlistEdgeData</i> instance if it exists,
	 *         null otherwise
	 */
	public static SimulatedAnnealingNetlistEdgeData getSimulatedAnnealingNetlistEdgeData(NetlistEdge edge) {
		SimulatedAnnealingNetlistEdgeData rtn = null;
		rtn = (SimulatedAnnealingNetlistEdgeData) edge.getNetlistEdgeData();
		return rtn;
	}

	/**
	 * Returns the <i>SimulatedAnnealingNetlistData</i> of the <i>netlist</i>
	 *
	 * @param netlist the netlist of this instance
	 * @return the <i>SimulatedAnnealingNetlistData</i> instance if it exists, null
	 *         otherwise
	 */
	public static SimulatedAnnealingNetlistData getSimulatedAnnealingNetlistData(Netlist netlist) {
		SimulatedAnnealingNetlistData rtn = null;
		rtn = (SimulatedAnnealingNetlistData) netlist.getNetlistData();
		return rtn;
	}

}

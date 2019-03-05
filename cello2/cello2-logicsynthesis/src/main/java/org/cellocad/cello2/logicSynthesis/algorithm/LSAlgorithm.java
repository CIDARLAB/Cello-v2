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
package org.cellocad.cello2.logicSynthesis.algorithm;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cellocad.cello2.common.algorithm.Algorithm;
import org.cellocad.cello2.logicSynthesis.netlist.data.LSStageNetlistData;
import org.cellocad.cello2.logicSynthesis.netlist.data.LSStageNetlistEdgeData;
import org.cellocad.cello2.logicSynthesis.netlist.data.LSStageNetlistNodeData;
import org.cellocad.cello2.results.netlist.Netlist;
import org.cellocad.cello2.results.netlist.NetlistEdge;
import org.cellocad.cello2.results.netlist.NetlistNode;

/**
 * The LSAlgorithm class is the base class for all algorithms in the <i>logicSynthesis</i> stage.
 * 
 * @author Vincent Mirian
 * 
 * @date 2018-05-21
 *
 */
public abstract class LSAlgorithm extends Algorithm{

	/**
	 *  Returns the <i>LSStageNetlistNodeData</i> of the <i>node</i>
	 *
	 *  @param node a node within the <i>netlist</i> of this instance
	 *  @return the <i>LSStageNetlistNodeData</i> instance if it exists, null otherwise
	 */
	protected LSStageNetlistNodeData getStageNetlistNodeData(NetlistNode node){
		LSStageNetlistNodeData rtn = null;
		rtn = (LSStageNetlistNodeData) node.getStageNetlistNodeData();
		return rtn;
	}

	/**
	 *  Returns the <i>LSStageNetlistEdgeData</i> of the <i>edge</i>
	 *
	 *  @param edge an edge within the <i>netlist</i> of this instance
	 *  @return the <i>LSStageNetlistEdgeData</i> instance if it exists, null otherwise
	 */
	protected LSStageNetlistEdgeData getStageNetlistEdgeData(NetlistEdge edge){
		LSStageNetlistEdgeData rtn = null;
		rtn = (LSStageNetlistEdgeData) edge.getStageNetlistEdgeData();
		return rtn;
	}

	/**
	 *  Returns the <i>LSStageNetlistData</i> of the <i>netlist</i>
	 *
	 *  @param netlist the netlist of this instance
	 *  @return the <i>LSStageNetlistData</i> instance if it exists, null otherwise
	 */
	protected LSStageNetlistData getStageNetlistData(Netlist netlist){
		LSStageNetlistData rtn = null;
		rtn = (LSStageNetlistData) netlist.getStageNetlistData();
		return rtn;
	}

	/**
	 *  Returns the Logger for the <i>LSAlgorithm</i> algorithm
	 *
	 *  @return the logger for the <i>LSAlgorithm</i> algorithm
	 */
	@Override
	protected Logger getLogger() {
		return LSAlgorithm.logger;
	}
	
	private static final Logger logger = LogManager.getLogger(LSAlgorithm.class);
}

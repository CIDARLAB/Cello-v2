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
package org.cellocad.cello2.clustering.algorithm;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cellocad.cello2.clustering.netlist.data.CLStageNetlistData;
import org.cellocad.cello2.clustering.netlist.data.CLStageNetlistEdgeData;
import org.cellocad.cello2.clustering.netlist.data.CLStageNetlistNodeData;
import org.cellocad.cello2.common.algorithm.Algorithm;
import org.cellocad.cello2.results.netlist.Netlist;
import org.cellocad.cello2.results.netlist.NetlistEdge;
import org.cellocad.cello2.results.netlist.NetlistNode;

/**
 * The CLAlgorithm class is the base class for all algorithms in the <i>clustering</i> stage.
 * 
 * @author Vincent Mirian
 * 
 * @date 2018-05-21
 *
 */
public abstract class CLAlgorithm extends Algorithm{

	/**
	 *  Returns the <i>CLStageNetlistNodeData</i> of the <i>node</i>
	 *
	 *  @param node a node within the <i>netlist</i> of this instance
	 *  @return the <i>CLStageNetlistNodeData</i> instance if it exists, null otherwise
	 */
	protected CLStageNetlistNodeData getStageNetlistNodeData(NetlistNode node){
		CLStageNetlistNodeData rtn = null;
		rtn = (CLStageNetlistNodeData) node.getStageNetlistNodeData();
		return rtn;
	}

	/**
	 *  Returns the <i>CLStageNetlistEdgeData</i> of the <i>edge</i>
	 *
	 *  @param edge an edge within the <i>netlist</i> of this instance
	 *  @return the <i>CLStageNetlistEdgeData</i> instance if it exists, null otherwise
	 */
	protected CLStageNetlistEdgeData getStageNetlistEdgeData(NetlistEdge edge){
		CLStageNetlistEdgeData rtn = null;
		rtn = (CLStageNetlistEdgeData) edge.getStageNetlistEdgeData();
		return rtn;
	}

	/**
	 *  Returns the <i>CLStageNetlistData</i> of the <i>netlist</i>
	 *
	 *  @param netlist the netlist of this instance
	 *  @return the <i>CLStageNetlistData</i> instance if it exists, null otherwise
	 */
	protected CLStageNetlistData getStageNetlistData(Netlist netlist){
		CLStageNetlistData rtn = null;
		rtn = (CLStageNetlistData) netlist.getStageNetlistData();
		return rtn;
	}

	/**
	 *  Returns the Logger for the <i>CLAlgorithm</i> algorithm
	 *
	 *  @return the logger for the <i>CLAlgorithm</i> algorithm
	 */
	@Override
	protected Logger getLogger() {
		return CLAlgorithm.logger;
	}
	
	private static final Logger logger = LogManager.getLogger(CLAlgorithm.class.getSimpleName());
}

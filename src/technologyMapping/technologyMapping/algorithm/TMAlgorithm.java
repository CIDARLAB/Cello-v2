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
package technologyMapping.algorithm;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import common.algorithm.Algorithm;
import results.netlist.Netlist;
import results.netlist.NetlistEdge;
import results.netlist.NetlistNode;
import technologyMapping.netlist.data.TMStageNetlistData;
import technologyMapping.netlist.data.TMStageNetlistEdgeData;
import technologyMapping.netlist.data.TMStageNetlistNodeData;

/**
 * The TMAlgorithm class is the base class for all algorithms in the <i>technologyMapping</i> stage.
 * 
 * @author Vincent Mirian
 * 
 * @date 2018-05-21
 *
 */
public abstract class TMAlgorithm extends Algorithm{

	/**
	 *  Returns the <i>TMStageNetlistNodeData</i> of the <i>node</i>
	 *
	 *  @param node a node within the <i>netlist</i> of this instance
	 *  @return the <i>TMStageNetlistNodeData</i> instance if it exists, null otherwise
	 */
	protected TMStageNetlistNodeData getStageNetlistNodeData(NetlistNode node){
		TMStageNetlistNodeData rtn = null;
		rtn = (TMStageNetlistNodeData) node.getStageNetlistNodeData();
		return rtn;
	}

	/**
	 *  Returns the <i>TMStageNetlistEdgeData</i> of the <i>edge</i>
	 *
	 *  @param edge an edge within the <i>netlist</i> of this instance
	 *  @return the <i>TMStageNetlistEdgeData</i> instance if it exists, null otherwise
	 */
	protected TMStageNetlistEdgeData getStageNetlistEdgeData(NetlistEdge edge){
		TMStageNetlistEdgeData rtn = null;
		rtn = (TMStageNetlistEdgeData) edge.getStageNetlistEdgeData();
		return rtn;
	}

	/**
	 *  Returns the <i>TMStageNetlistData</i> of the <i>netlist</i>
	 *
	 *  @param netlist the netlist of this instance
	 *  @return the <i>TMStageNetlistData</i> instance if it exists, null otherwise
	 */
	protected TMStageNetlistData getStageNetlistData(Netlist netlist){
		TMStageNetlistData rtn = null;
		rtn = (TMStageNetlistData) netlist.getStageNetlistData();
		return rtn;
	}

	/**
	 *  Returns the Logger for the <i>TMAlgorithm</i> algorithm
	 *
	 *  @return the logger for the <i>TMAlgorithm</i> algorithm
	 */
	@Override
	protected Logger getLogger() {
		return TMAlgorithm.logger;
	}
	
	private static final Logger logger = LogManager.getLogger(TMAlgorithm.class.getSimpleName());
}

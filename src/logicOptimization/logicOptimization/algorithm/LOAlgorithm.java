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
package logicOptimization.algorithm;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import common.algorithm.Algorithm;
import results.netlist.Netlist;
import results.netlist.NetlistEdge;
import results.netlist.NetlistNode;
import logicOptimization.netlist.data.LOStageNetlistData;
import logicOptimization.netlist.data.LOStageNetlistEdgeData;
import logicOptimization.netlist.data.LOStageNetlistNodeData;

/**
 * The LOAlgorithm class is the base class for all algorithms in the <i>logicOptimization</i> stage.
 * 
 * @author Vincent Mirian
 * 
 * @date 2018-05-21
 *
 */
public abstract class LOAlgorithm extends Algorithm{

	/**
	 *  Returns the <i>LOStageNetlistNodeData</i> of the <i>node</i>
	 *
	 *  @param node a node within the <i>netlist</i> of this instance
	 *  @return the <i>LOStageNetlistNodeData</i> instance if it exists, null otherwise
	 */
	protected LOStageNetlistNodeData getStageNetlistNodeData(NetlistNode node){
		LOStageNetlistNodeData rtn = null;
		rtn = (LOStageNetlistNodeData) node.getStageNetlistNodeData();
		return rtn;
	}

	/**
	 *  Returns the <i>LOStageNetlistEdgeData</i> of the <i>edge</i>
	 *
	 *  @param edge an edge within the <i>netlist</i> of this instance
	 *  @return the <i>LOStageNetlistEdgeData</i> instance if it exists, null otherwise
	 */
	protected LOStageNetlistEdgeData getStageNetlistEdgeData(NetlistEdge edge){
		LOStageNetlistEdgeData rtn = null;
		rtn = (LOStageNetlistEdgeData) edge.getStageNetlistEdgeData();
		return rtn;
	}

	/**
	 *  Returns the <i>LOStageNetlistData</i> of the <i>netlist</i>
	 *
	 *  @param netlist the netlist of this instance
	 *  @return the <i>LOStageNetlistData</i> instance if it exists, null otherwise
	 */
	protected LOStageNetlistData getStageNetlistData(Netlist netlist){
		LOStageNetlistData rtn = null;
		rtn = (LOStageNetlistData) netlist.getStageNetlistData();
		return rtn;
	}

	/**
	 *  Returns the Logger for the <i>LOAlgorithm</i> algorithm
	 *
	 *  @return the logger for the <i>LOAlgorithm</i> algorithm
	 */
	@Override
	protected Logger getLogger() {
		return LOAlgorithm.logger;
	}
	
	private static final Logger logger = LogManager.getLogger(LOAlgorithm.class.getSimpleName());
}

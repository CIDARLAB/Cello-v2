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
package placing.algorithm;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import common.algorithm.Algorithm;
import results.netlist.Netlist;
import results.netlist.NetlistEdge;
import results.netlist.NetlistNode;
import placing.netlist.data.PLStageNetlistData;
import placing.netlist.data.PLStageNetlistEdgeData;
import placing.netlist.data.PLStageNetlistNodeData;

/**
 * The PLAlgorithm class is the base class for all algorithms in the <i>placing</i> stage.
 * 
 * @author Vincent Mirian
 * 
 * @date 2018-05-21
 *
 */
public abstract class PLAlgorithm extends Algorithm{

	/**
	 *  Returns the <i>PLStageNetlistNodeData</i> of the <i>node</i>
	 *
	 *  @param node a node within the <i>netlist</i> of this instance
	 *  @return the <i>PLStageNetlistNodeData</i> instance if it exists, null otherwise
	 */
	protected PLStageNetlistNodeData getStageNetlistNodeData(NetlistNode node){
		PLStageNetlistNodeData rtn = null;
		rtn = (PLStageNetlistNodeData) node.getStageNetlistNodeData();
		return rtn;
	}

	/**
	 *  Returns the <i>PLStageNetlistEdgeData</i> of the <i>edge</i>
	 *
	 *  @param edge an edge within the <i>netlist</i> of this instance
	 *  @return the <i>PLStageNetlistEdgeData</i> instance if it exists, null otherwise
	 */
	protected PLStageNetlistEdgeData getStageNetlistEdgeData(NetlistEdge edge){
		PLStageNetlistEdgeData rtn = null;
		rtn = (PLStageNetlistEdgeData) edge.getStageNetlistEdgeData();
		return rtn;
	}

	/**
	 *  Returns the <i>PLStageNetlistData</i> of the <i>netlist</i>
	 *
	 *  @param netlist the netlist of this instance
	 *  @return the <i>PLStageNetlistData</i> instance if it exists, null otherwise
	 */
	protected PLStageNetlistData getStageNetlistData(Netlist netlist){
		PLStageNetlistData rtn = null;
		rtn = (PLStageNetlistData) netlist.getStageNetlistData();
		return rtn;
	}

	/**
	 *  Returns the Logger for the <i>PLAlgorithm</i> algorithm
	 *
	 *  @return the logger for the <i>PLAlgorithm</i> algorithm
	 */
	@Override
	protected Logger getLogger() {
		return PLAlgorithm.logger;
	}
	
	private static final Logger logger = LogManager.getLogger(PLAlgorithm.class.getSimpleName());
}

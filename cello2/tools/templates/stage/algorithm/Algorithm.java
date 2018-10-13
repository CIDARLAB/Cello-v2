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
package org.cellocad.cello2.##NONCE##21##STAGENAME##21##NONCE.algorithm;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.cellocad.cello2.common.algorithm.Algorithm;
import org.cellocad.cello2.results.netlist.Netlist;
import org.cellocad.cello2.results.netlist.NetlistEdge;
import org.cellocad.cello2.results.netlist.NetlistNode;
import org.cellocad.cello2.##NONCE##21##STAGENAME##21##NONCE.netlist.data.##NONCE##21##STAGEPREFIX##21##NONCEStageNetlistData;
import org.cellocad.cello2.##NONCE##21##STAGENAME##21##NONCE.netlist.data.##NONCE##21##STAGEPREFIX##21##NONCEStageNetlistEdgeData;
import org.cellocad.cello2.##NONCE##21##STAGENAME##21##NONCE.netlist.data.##NONCE##21##STAGEPREFIX##21##NONCEStageNetlistNodeData;

/**
 * The ##NONCE##21##STAGEPREFIX##21##NONCEAlgorithm class is the base class for all algorithms in the <i>##NONCE##21##STAGENAME##21##NONCE</i> stage.
 * 
 * @author Vincent Mirian
 * 
 * @date Today
 *
 */
public abstract class ##NONCE##21##STAGEPREFIX##21##NONCEAlgorithm extends Algorithm{

	/**
	 *  Returns the <i>##NONCE##21##STAGEPREFIX##21##NONCEStageNetlistNodeData</i> of the <i>node</i>
	 *
	 *  @param node a node within the <i>netlist</i> of this instance
	 *  @return the <i>##NONCE##21##STAGEPREFIX##21##NONCEStageNetlistNodeData</i> instance if it exists, null otherwise
	 */
	protected ##NONCE##21##STAGEPREFIX##21##NONCEStageNetlistNodeData getStageNetlistNodeData(NetlistNode node){
		##NONCE##21##STAGEPREFIX##21##NONCEStageNetlistNodeData rtn = null;
		rtn = (##NONCE##21##STAGEPREFIX##21##NONCEStageNetlistNodeData) node.getStageNetlistNodeData();
		return rtn;
	}

	/**
	 *  Returns the <i>##NONCE##21##STAGEPREFIX##21##NONCEStageNetlistEdgeData</i> of the <i>edge</i>
	 *
	 *  @param edge an edge within the <i>netlist</i> of this instance
	 *  @return the <i>##NONCE##21##STAGEPREFIX##21##NONCEStageNetlistEdgeData</i> instance if it exists, null otherwise
	 */
	protected ##NONCE##21##STAGEPREFIX##21##NONCEStageNetlistEdgeData getStageNetlistEdgeData(NetlistEdge edge){
		##NONCE##21##STAGEPREFIX##21##NONCEStageNetlistEdgeData rtn = null;
		rtn = (##NONCE##21##STAGEPREFIX##21##NONCEStageNetlistEdgeData) edge.getStageNetlistEdgeData();
		return rtn;
	}

	/**
	 *  Returns the <i>##NONCE##21##STAGEPREFIX##21##NONCEStageNetlistData</i> of the <i>netlist</i>
	 *
	 *  @param netlist the netlist of this instance
	 *  @return the <i>##NONCE##21##STAGEPREFIX##21##NONCEStageNetlistData</i> instance if it exists, null otherwise
	 */
	protected ##NONCE##21##STAGEPREFIX##21##NONCEStageNetlistData getStageNetlistData(Netlist netlist){
		##NONCE##21##STAGEPREFIX##21##NONCEStageNetlistData rtn = null;
		rtn = (##NONCE##21##STAGEPREFIX##21##NONCEStageNetlistData) netlist.getStageNetlistData();
		return rtn;
	}

	/**
	 *  Returns the Logger for the <i>##NONCE##21##STAGEPREFIX##21##NONCEAlgorithm</i> algorithm
	 *
	 *  @return the logger for the <i>##NONCE##21##STAGEPREFIX##21##NONCEAlgorithm</i> algorithm
	 */
	@Override
	protected Logger getLogger() {
		return ##NONCE##21##STAGEPREFIX##21##NONCEAlgorithm.logger;
	}
	
	private static final Logger logger = LogManager.getLogger(##NONCE##21##STAGEPREFIX##21##NONCEAlgorithm.class.getSimpleName());
}

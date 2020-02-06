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
package org.cellocad.v2.clustering.algorithm.CL_RC;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cellocad.v2.clustering.algorithm.CLAlgorithm;
import org.cellocad.v2.clustering.algorithm.CL_RC.data.CL_RCNetlistData;
import org.cellocad.v2.clustering.algorithm.CL_RC.data.CL_RCNetlistEdgeData;
import org.cellocad.v2.clustering.algorithm.CL_RC.data.CL_RCNetlistNodeData;
import org.cellocad.v2.results.netlist.Netlist;
import org.cellocad.v2.results.netlist.NetlistEdge;
import org.cellocad.v2.results.netlist.NetlistNode;

/**
 * The CL_RC class implements the <i>CL_RC</i> algorithm in the <i>clustering</i> stage.
 * 
 * @author Vincent Mirian
 * 
 * @date 2018-05-21
 *
 */
public class CL_RC extends CLAlgorithm{

	/**
	 *  Returns the <i>CL_RCNetlistNodeData</i> of the <i>node</i>
	 *
	 *  @param node a node within the <i>netlist</i> of this instance
	 *  @return the <i>CL_RCNetlistNodeData</i> instance if it exists, null otherwise
	 */
	protected CL_RCNetlistNodeData getCL_RCNetlistNodeData(NetlistNode node){
		CL_RCNetlistNodeData rtn = null;
		rtn = (CL_RCNetlistNodeData) node.getNetlistNodeData();
		return rtn;
	}

	/**
	 *  Returns the <i>CL_RCNetlistEdgeData</i> of the <i>edge</i>
	 *
	 *  @param edge an edge within the <i>netlist</i> of this instance
	 *  @return the <i>CL_RCNetlistEdgeData</i> instance if it exists, null otherwise
	 */
	protected CL_RCNetlistEdgeData getCL_RCNetlistEdgeData(NetlistEdge edge){
		CL_RCNetlistEdgeData rtn = null;
		rtn = (CL_RCNetlistEdgeData) edge.getNetlistEdgeData();
		return rtn;
	}

	/**
	 *  Returns the <i>CL_RCNetlistData</i> of the <i>netlist</i>
	 *
	 *  @param netlist the netlist of this instance
	 *  @return the <i>CL_RCNetlistData</i> instance if it exists, null otherwise
	 */
	protected CL_RCNetlistData getCL_RCNetlistData(Netlist netlist){
		CL_RCNetlistData rtn = null;
		rtn = (CL_RCNetlistData) netlist.getNetlistData();
		return rtn;
	}

	/**
	 *  Gets the Constraint data from the NetlistConstraintFile
	 */
	@Override
	protected void getConstraintFromNetlistConstraintFile() {

	}

	/**
	 *  Gets the data from the UCF
	 */
	@Override
	protected void getDataFromUCF() {

	}

	/**
	 *  Set parameter(s) value(s) of the algorithm
	 */
	@Override
	protected void setParameterValues() {
	}

	/**
	 *  Validate parameter value of the algorithm
	 */
	@Override
	protected void validateParameterValues() {
		
	}

	/**
	 *  Perform preprocessing
	 */
	@Override
	protected void preprocessing() {
		
	}

	/**
	 *  Run the (core) algorithm
	 */
	@Override
	protected void run() {
		
	}

	/**
	 *  Perform postprocessing
	 */
	@Override
	protected void postprocessing() {
		
	}


	/**
	 *  Returns the Logger for the <i>CL_RC</i> algorithm
	 *
	 *  @return the logger for the <i>CL_RC</i> algorithm
	 */
	@Override
	protected Logger getLogger() {
		return CL_RC.logger;
	}
	
	private static final Logger logger = LogManager.getLogger(CL_RC.class);
}

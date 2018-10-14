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
package org.cellocad.cello2.technologyMapping.algorithm.DSGRN;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.cellocad.cello2.results.netlist.Netlist;
import org.cellocad.cello2.results.netlist.NetlistEdge;
import org.cellocad.cello2.results.netlist.NetlistNode;
import org.cellocad.cello2.technologyMapping.algorithm.TMAlgorithm;
import org.cellocad.cello2.technologyMapping.algorithm.DSGRN.data.DSGRNNetlistData;
import org.cellocad.cello2.technologyMapping.algorithm.DSGRN.data.DSGRNNetlistEdgeData;
import org.cellocad.cello2.technologyMapping.algorithm.DSGRN.data.DSGRNNetlistNodeData;
import org.cellocad.cello2.results.technologyMapping.TMResults;

/**
 * The DSGRN class implements the <i>DSGRN</i> algorithm in the <i>technologyMapping</i> stage.
 * 
 * @author Timothy Jones
 * 
 * @date Today
 *
 */
public class DSGRN extends TMAlgorithm{

	/**
	 *  Returns the <i>DSGRNNetlistNodeData</i> of the <i>node</i>
	 *
	 *  @param node a node within the <i>netlist</i> of this instance
	 *  @return the <i>DSGRNNetlistNodeData</i> instance if it exists, null otherwise
	 */
	protected DSGRNNetlistNodeData getDSGRNNetlistNodeData(NetlistNode node){
		DSGRNNetlistNodeData rtn = null;
		rtn = (DSGRNNetlistNodeData) node.getNetlistNodeData();
		return rtn;
	}

	/**
	 *  Returns the <i>DSGRNNetlistEdgeData</i> of the <i>edge</i>
	 *
	 *  @param edge an edge within the <i>netlist</i> of this instance
	 *  @return the <i>DSGRNNetlistEdgeData</i> instance if it exists, null otherwise
	 */
	protected DSGRNNetlistEdgeData getDSGRNNetlistEdgeData(NetlistEdge edge){
		DSGRNNetlistEdgeData rtn = null;
		rtn = (DSGRNNetlistEdgeData) edge.getNetlistEdgeData();
		return rtn;
	}

	/**
	 *  Returns the <i>DSGRNNetlistData</i> of the <i>netlist</i>
	 *
	 *  @param netlist the netlist of this instance
	 *  @return the <i>DSGRNNetlistData</i> instance if it exists, null otherwise
	 */
	protected DSGRNNetlistData getDSGRNNetlistData(Netlist netlist){
		DSGRNNetlistData rtn = null;
		rtn = (DSGRNNetlistData) netlist.getNetlistData();
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
	 *  Returns the Logger for the <i>DSGRN</i> algorithm
	 *
	 *  @return the logger for the <i>DSGRN</i> algorithm
	 */
	@Override
	protected Logger getLogger() {
		return DSGRN.logger;
	}
	
	private static final Logger logger = LogManager.getLogger(DSGRN.class.getSimpleName());
}

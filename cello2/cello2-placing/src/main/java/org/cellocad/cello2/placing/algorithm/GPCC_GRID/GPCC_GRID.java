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
package org.cellocad.cello2.placing.algorithm.GPCC_GRID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cellocad.cello2.placing.algorithm.PLAlgorithm;
import org.cellocad.cello2.placing.algorithm.GPCC_GRID.data.GPCC_GRIDNetlistData;
import org.cellocad.cello2.placing.algorithm.GPCC_GRID.data.GPCC_GRIDNetlistEdgeData;
import org.cellocad.cello2.placing.algorithm.GPCC_GRID.data.GPCC_GRIDNetlistNodeData;
import org.cellocad.cello2.results.netlist.Netlist;
import org.cellocad.cello2.results.netlist.NetlistEdge;
import org.cellocad.cello2.results.netlist.NetlistNode;

/**
 * The GPCC_GRID class implements the <i>GPCC_GRID</i> algorithm in the <i>placing</i> stage.
 * 
 * @author Vincent Mirian
 * 
 * @date 2018-05-21
 *
 */
public class GPCC_GRID extends PLAlgorithm{

	/**
	 *  Returns the <i>GPCC_GRIDNetlistNodeData</i> of the <i>node</i>
	 *
	 *  @param node a node within the <i>netlist</i> of this instance
	 *  @return the <i>GPCC_GRIDNetlistNodeData</i> instance if it exists, null otherwise
	 */
	protected GPCC_GRIDNetlistNodeData getGPCC_GRIDNetlistNodeData(NetlistNode node){
		GPCC_GRIDNetlistNodeData rtn = null;
		rtn = (GPCC_GRIDNetlistNodeData) node.getNetlistNodeData();
		return rtn;
	}

	/**
	 *  Returns the <i>GPCC_GRIDNetlistEdgeData</i> of the <i>edge</i>
	 *
	 *  @param edge an edge within the <i>netlist</i> of this instance
	 *  @return the <i>GPCC_GRIDNetlistEdgeData</i> instance if it exists, null otherwise
	 */
	protected GPCC_GRIDNetlistEdgeData getGPCC_GRIDNetlistEdgeData(NetlistEdge edge){
		GPCC_GRIDNetlistEdgeData rtn = null;
		rtn = (GPCC_GRIDNetlistEdgeData) edge.getNetlistEdgeData();
		return rtn;
	}

	/**
	 *  Returns the <i>GPCC_GRIDNetlistData</i> of the <i>netlist</i>
	 *
	 *  @param netlist the netlist of this instance
	 *  @return the <i>GPCC_GRIDNetlistData</i> instance if it exists, null otherwise
	 */
	protected GPCC_GRIDNetlistData getGPCC_GRIDNetlistData(Netlist netlist){
		GPCC_GRIDNetlistData rtn = null;
		rtn = (GPCC_GRIDNetlistData) netlist.getNetlistData();
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
		// analyze and insert dummy cells to relay
		// see notes for algo
	}

	/**
	 *  Run the (core) algorithm
	 */
	@Override
	protected void run() {
		// see notes for algo
	}

	/**
	 *  Perform postprocessing
	 */
	@Override
	protected void postprocessing() {
		// verify
	}

	/**
	 *  Returns the Logger for the <i>GPCC_GRID</i> algorithm
	 *
	 *  @return the logger for the <i>GPCC_GRID</i> algorithm
	 */
	@Override
	protected Logger getLogger() {
		return GPCC_GRID.logger;
	}
	
	private static final Logger logger = LogManager.getLogger(GPCC_GRID.class.getSimpleName());
}

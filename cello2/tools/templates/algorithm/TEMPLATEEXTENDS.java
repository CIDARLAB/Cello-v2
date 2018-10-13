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
package org.cellocad.cello2.##NONCE##21##STAGENAME##21##NONCE.algorithm.##NONCE##21##ALGONAME##21##NONCE;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.cellocad.cello2.results.netlist.Netlist;
import org.cellocad.cello2.results.netlist.NetlistEdge;
import org.cellocad.cello2.results.netlist.NetlistNode;
import org.cellocad.cello2.##NONCE##21##STAGENAME##21##NONCE.algorithm.##NONCE##21##EXTENDS##21##NONCE;
import org.cellocad.cello2.##NONCE##21##STAGENAME##21##NONCE.algorithm.##NONCE##21##ALGONAME##21##NONCE.data.##NONCE##21##ALGONAME##21##NONCENetlistData;
import org.cellocad.cello2.##NONCE##21##STAGENAME##21##NONCE.algorithm.##NONCE##21##ALGONAME##21##NONCE.data.##NONCE##21##ALGONAME##21##NONCENetlistEdgeData;
import org.cellocad.cello2.##NONCE##21##STAGENAME##21##NONCE.algorithm.##NONCE##21##ALGONAME##21##NONCE.data.##NONCE##21##ALGONAME##21##NONCENetlistNodeData;
import org.cellocad.cello2.results.##NONCE##21##STAGENAME##21##NONCE.##NONCE##21##STAGEPREFIX##21##NONCEResults;

/**
 * The ##NONCE##21##ALGONAME##21##NONCE class implements the <i>##NONCE##21##ALGONAME##21##NONCE</i> algorithm in the <i>##NONCE##21##STAGENAME##21##NONCE</i> stage.
 * 
 * @author ##NONCE##21##AUTHORNAME##21##NONCE
 * 
 * @date Today
 *
 */
public class ##NONCE##21##ALGONAME##21##NONCE extends ##NONCE##21##EXTENDS##21##NONCE{

	/**
	 *  Returns the <i>##NONCE##21##ALGONAME##21##NONCENetlistNodeData</i> of the <i>node</i>
	 *
	 *  @param node a node within the <i>netlist</i> of this instance
	 *  @return the <i>##NONCE##21##ALGONAME##21##NONCENetlistNodeData</i> instance if it exists, null otherwise
	 */
	protected ##NONCE##21##ALGONAME##21##NONCENetlistNodeData get##NONCE##21##ALGONAME##21##NONCENetlistNodeData(NetlistNode node){
		##NONCE##21##ALGONAME##21##NONCENetlistNodeData rtn = null;
		rtn = (##NONCE##21##ALGONAME##21##NONCENetlistNodeData) node.getNetlistNodeData();
		return rtn;
	}

	/**
	 *  Returns the <i>##NONCE##21##ALGONAME##21##NONCENetlistEdgeData</i> of the <i>edge</i>
	 *
	 *  @param edge an edge within the <i>netlist</i> of this instance
	 *  @return the <i>##NONCE##21##ALGONAME##21##NONCENetlistEdgeData</i> instance if it exists, null otherwise
	 */
	protected ##NONCE##21##ALGONAME##21##NONCENetlistEdgeData get##NONCE##21##ALGONAME##21##NONCENetlistEdgeData(NetlistEdge edge){
		##NONCE##21##ALGONAME##21##NONCENetlistEdgeData rtn = null;
		rtn = (##NONCE##21##ALGONAME##21##NONCENetlistEdgeData) edge.getNetlistEdgeData();
		return rtn;
	}

	/**
	 *  Returns the <i>##NONCE##21##ALGONAME##21##NONCENetlistData</i> of the <i>netlist</i>
	 *
	 *  @param netlist the netlist of this instance
	 *  @return the <i>##NONCE##21##ALGONAME##21##NONCENetlistData</i> instance if it exists, null otherwise
	 */
	protected ##NONCE##21##ALGONAME##21##NONCENetlistData get##NONCE##21##ALGONAME##21##NONCENetlistData(Netlist netlist){
		##NONCE##21##ALGONAME##21##NONCENetlistData rtn = null;
		rtn = (##NONCE##21##ALGONAME##21##NONCENetlistData) netlist.getNetlistData();
		return rtn;
	}

	/**
	 *  Gets the Constraint data from the NetlistConstraintFile
	 */
	@Override
	protected void getConstraintFromNetlistConstraintFile() {
		super.getConstraintFromNetlistConstraintFile();
	}

	/**
	 *  Gets the data from the UCF
	 */
	@Override
	protected void getDataFromUCF() {
		super.getDataFromUCF();
	}

	/**
	 *  Set parameter(s) value(s) of the algorithm
	 */
	@Override
	protected void setParameterValues() {
		super.setParameterValues();
		##NONCE##21##SETPARAMETERVALUES##21##NONCE
	}

	/**
	 *  Validate parameter value of the algorithm
	 */
	@Override
	protected void validateParameterValues() {
		super.validateParameterValues();
	}

	/**
	 *  Perform preprocessing
	 */
	@Override
	protected void preprocessing() {
		super.preprocessing();	
	}

	/**
	 *  Run the (core) algorithm
	 */
	@Override
	protected void run() {
		super.run();
	}

	/**
	 *  Perform postprocessing
	 */
	@Override
	protected void postprocessing() {
		super.postprocessing();
	}

	##NONCE##21##PARAMETERGETTERSETTER##21##NONCE

	/**
	 *  Returns the Logger for the <i>##NONCE##21##ALGONAME##21##NONCE</i> algorithm
	 *
	 *  @return the logger for the <i>##NONCE##21##ALGONAME##21##NONCE</i> algorithm
	 */
	@Override
	protected Logger getLogger() {
		return ##NONCE##21##ALGONAME##21##NONCE.logger;
	}
	
	private static final Logger logger = LogManager.getLogger(##NONCE##21##ALGONAME##21##NONCE.class.getSimpleName());

}

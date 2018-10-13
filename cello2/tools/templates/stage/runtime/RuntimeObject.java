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
package org.cellocad.cello2.##NONCE##21##STAGENAME##21##NONCE.runtime;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.cellocad.cello2.results.netlist.Netlist;
import org.cellocad.cello2.results.netlist.NetlistEdge;
import org.cellocad.cello2.results.netlist.NetlistNode;
import org.cellocad.cello2.common.netlistConstraint.data.NetlistConstraint;
import org.cellocad.cello2.common.runtime.RuntimeObject;
import org.cellocad.cello2.common.runtime.environment.RuntimeEnv;
import org.cellocad.cello2.common.stage.Stage;
import org.cellocad.cello2.common.target.data.TargetData;
import org.cellocad.cello2.##NONCE##21##STAGENAME##21##NONCE.algorithm.##NONCE##21##STAGEPREFIX##21##NONCEAlgorithm;
import org.cellocad.cello2.##NONCE##21##STAGENAME##21##NONCE.algorithm.##NONCE##21##STAGEPREFIX##21##NONCEAlgorithmFactory;
import org.cellocad.cello2.##NONCE##21##STAGENAME##21##NONCE.algorithm.data.##NONCE##21##STAGEPREFIX##21##NONCENetlistData;
import org.cellocad.cello2.##NONCE##21##STAGENAME##21##NONCE.algorithm.data.##NONCE##21##STAGEPREFIX##21##NONCENetlistDataFactory;
import org.cellocad.cello2.##NONCE##21##STAGENAME##21##NONCE.algorithm.data.##NONCE##21##STAGEPREFIX##21##NONCENetlistEdgeData;
import org.cellocad.cello2.##NONCE##21##STAGENAME##21##NONCE.algorithm.data.##NONCE##21##STAGEPREFIX##21##NONCENetlistEdgeDataFactory;
import org.cellocad.cello2.##NONCE##21##STAGENAME##21##NONCE.algorithm.data.##NONCE##21##STAGEPREFIX##21##NONCENetlistNodeData;
import org.cellocad.cello2.##NONCE##21##STAGENAME##21##NONCE.algorithm.data.##NONCE##21##STAGEPREFIX##21##NONCENetlistNodeDataFactory;
import org.cellocad.cello2.##NONCE##21##STAGENAME##21##NONCE.common.##NONCE##21##STAGEPREFIX##21##NONCEUtils;
import org.cellocad.cello2.##NONCE##21##STAGENAME##21##NONCE.netlist.data.##NONCE##21##STAGEPREFIX##21##NONCEStageNetlistData;
import org.cellocad.cello2.##NONCE##21##STAGENAME##21##NONCE.netlist.data.##NONCE##21##STAGEPREFIX##21##NONCEStageNetlistNodeData;
import org.cellocad.cello2.##NONCE##21##STAGENAME##21##NONCE.netlist.data.##NONCE##21##STAGEPREFIX##21##NONCEStageNetlistEdgeData;
import org.cellocad.cello2.##NONCE##21##STAGENAME##21##NONCE.runtime.environment.##NONCE##21##STAGEPREFIX##21##NONCEArgString;

/**
 * The ##NONCE##21##STAGEPREFIX##21##NONCERuntimeObject class is the RuntimeObject class for the <i>##NONCE##21##STAGENAME##21##NONCE</i> stage.
 * 
 * @author Vincent Mirian
 * 
 * @date Today
 *
 */
public class ##NONCE##21##STAGEPREFIX##21##NONCERuntimeObject extends RuntimeObject{

	/**
	 *  Initializes a newly created ##NONCE##21##STAGEPREFIX##21##NONCERuntimeObject with its <i>stage</i> set to parameter <i>stage</i>,
	 *  its <i>targetData</i> set to parameter <i>targetData</i>,
	 *  its <i>netlist</i> set to parameter <i>netlist</i>, and,
	 *  its <i>runEnv</i> set to parameter <i>runEnv</i>.
	 *  
	 *  @param stage Stage used during execution
	 *  @param targetData TargetData used during execution
	 *  @param netlistConstraint NetlistConstraint used during execution
	 *  @param netlist Netlist used during execution
	 *  @param runEnv RuntimeEnv used during execution
	 *  @throws RuntimeException if any of the parameters are null
	 */
	public ##NONCE##21##STAGEPREFIX##21##NONCERuntimeObject(
			final Stage stage,
			final TargetData targetData,
			final NetlistConstraint netlistConstraint,
			final Netlist netlist,
			final RuntimeEnv runEnv
			) {
		super(stage, targetData, netlistConstraint, netlist, runEnv);
	}

	/**
	 * 	Prepares the DataFactory for the Netlist, NetlistNode and NetlistEdge of the ##NONCE##21##STAGENAME##21##NONCE stage.
	 */
	@Override
	protected void prepareDataFactory() {
		this.setNetlistDataFactory(new ##NONCE##21##STAGEPREFIX##21##NONCENetlistDataFactory());
		this.setNetlistNodeDataFactory(new ##NONCE##21##STAGEPREFIX##21##NONCENetlistNodeDataFactory());
		this.setNetlistEdgeDataFactory(new ##NONCE##21##STAGEPREFIX##21##NONCENetlistEdgeDataFactory());
	}

	
	/**
	 *  Sets the ##NONCE##21##STAGEPREFIX##21##NONCEStageNetlistData for the ##NONCE##21##STAGENAME##21##NONCE stage in parameter <i>netlist</i>
	 *  <b>Note: this method will be deprecated in the future.</b>
	 *  
	 *  @param netlist the <i>netlist</i> of this instance
	 */
	@Override
	protected void setStageNetlistData(Netlist netlist) {
		netlist.setStageNetlistData(new ##NONCE##21##STAGEPREFIX##21##NONCEStageNetlistData());
	}

	/**
	 *  Sets the ##NONCE##21##STAGEPREFIX##21##NONCEStageNetlistNodeData for the ##NONCE##21##STAGENAME##21##NONCE stage in parameter <i>node</i>
	 *  <b>Note: this method will be deprecated in the future.</b>
	 *  
	 *  @param node a node within the <i>netlist</i> of this instance
	 */
	@Override
	protected void setStageNetlistNodeData(NetlistNode node) {
		node.setStageNetlistNodeData(new ##NONCE##21##STAGEPREFIX##21##NONCEStageNetlistNodeData());
	}

	/**
	 *  Sets the ##NONCE##21##STAGEPREFIX##21##NONCEStageNetlistEdgeData for the ##NONCE##21##STAGENAME##21##NONCE stage in parameter <i>edge</i>
	 *  <b>Note: method this will be deprecated in the future.</b>
	 *  
	 *  @param edge an edge within the <i>netlist</i> of this instance
	 */
	@Override
	protected void setStageNetlistEdgeData(NetlistEdge edge) {
		edge.setStageNetlistEdgeData(new ##NONCE##21##STAGEPREFIX##21##NONCEStageNetlistEdgeData());
	}

	/**
	 *  Sets the NetlistData of the appropriate algorithm in parameter <i>netlist</i>
	 *  @param netlist the <i>netlist</i> of this instance
	 */
	@Override
	protected void setNetlistData(Netlist netlist) {
		##NONCE##21##STAGEPREFIX##21##NONCENetlistData data = this.getNetlistDataFactory().getNetlistData(this.getAlgorithmProfile());
		netlist.setNetlistData(data);
	}

	/**
	 *  Sets the NetlistNodeData of the appropriate algorithm in parameter <i>node</i>
	 *  @param node a node within the <i>netlist</i> of this instance
	 */
	@Override
	protected void setNetlistNodeData(NetlistNode node) {
		##NONCE##21##STAGEPREFIX##21##NONCENetlistNodeData data = this.getNetlistNodeDataFactory().getNetlistNodeData(this.getAlgorithmProfile());
		node.setNetlistNodeData(data);
	}

	/**
	 *  Sets the NetlistEdgeData of the appropriate algorithm in parameter <i>edge</i>
	 *  @param edge an edge within the <i>netlist</i> of this instance
	 */
	@Override
	protected void setNetlistEdgeData(NetlistEdge edge) {
		##NONCE##21##STAGEPREFIX##21##NONCENetlistEdgeData data = this.getNetlistEdgeDataFactory().getNetlistEdgeData(this.getAlgorithmProfile());
		edge.setNetlistEdgeData(data);
	}
	
	/**
	 * Returns the path of the Resources directory for the ##NONCE##21##STAGENAME##21##NONCE stage
	 * 
	 * @return the path of the Resources directory for the ##NONCE##21##STAGENAME##21##NONCE stage
	 *
	 */
	@Override
	protected String getResourcesFilepath() {
		String rtn = null;
		rtn = ##NONCE##21##STAGEPREFIX##21##NONCEUtils.getResourcesFilepath();
		return rtn;
	}

	/**
	 *  Returns a string representing the OPTIONS command line argument for the ##NONCE##21##STAGENAME##21##NONCE stage
	 *  
	 *  @return a string representing the OPTIONS command line argument for the ##NONCE##21##STAGENAME##21##NONCE stage
	 */
	@Override
	protected String getOptionsString() {
		return ##NONCE##21##STAGEPREFIX##21##NONCEArgString.OPTIONS;
	}

	/**
	 * 	Executes the algorithm of the ##NONCE##21##STAGENAME##21##NONCE stage.
	 */
	@Override
	protected void runAlgo() {
		// get Algorithm from Factory
		##NONCE##21##STAGEPREFIX##21##NONCEAlgorithmFactory AF = new ##NONCE##21##STAGEPREFIX##21##NONCEAlgorithmFactory();
		##NONCE##21##STAGEPREFIX##21##NONCEAlgorithm algo = AF.getAlgorithm(this.getAlgorithmProfile());
		//executeAlgo
		this.executeAlgo(algo);
	}

	/**
	 *  Getter for the ##NONCE##21##STAGEPREFIX##21##NONCENetlistDataFactory
	 *  @return the ##NONCE##21##STAGEPREFIX##21##NONCENetlistDataFactory
	 */
	protected ##NONCE##21##STAGEPREFIX##21##NONCENetlistDataFactory getNetlistDataFactory() {
		return this.netlistDataFactory;
	}
	
	/**
	 *  Setter for the ##NONCE##21##STAGEPREFIX##21##NONCENetlistDataFactory
	 *  @param netlistDataFactory the ##NONCE##21##STAGEPREFIX##21##NONCENetlistDataFactory
	 */
	private void setNetlistDataFactory(final ##NONCE##21##STAGEPREFIX##21##NONCENetlistDataFactory netlistDataFactory) {
		this.netlistDataFactory = netlistDataFactory;
	}
	
	/**
	 *  Getter for the ##NONCE##21##STAGEPREFIX##21##NONCENetlistNodeDataFactory
	 *  @return the ##NONCE##21##STAGEPREFIX##21##NONCENetlistNodeDataFactory
	 */
	protected ##NONCE##21##STAGEPREFIX##21##NONCENetlistNodeDataFactory getNetlistNodeDataFactory() {
		return this.netlistNodeDataFactory;
	}
	
	/**
	 *  Setter for the ##NONCE##21##STAGEPREFIX##21##NONCENetlistNodeDataFactory
	 *  @param netlistNodeDataFactory the ##NONCE##21##STAGEPREFIX##21##NONCENetlistNodeDataFactory
	 */
	private void setNetlistNodeDataFactory(final ##NONCE##21##STAGEPREFIX##21##NONCENetlistNodeDataFactory netlistNodeDataFactory) {
		this.netlistNodeDataFactory = netlistNodeDataFactory;
	}
	
	/**
	 *  Getter for the ##NONCE##21##STAGEPREFIX##21##NONCENetlistEdgeDataFactory
	 *  @return the ##NONCE##21##STAGEPREFIX##21##NONCENetlistEdgeDataFactory
	 */
	protected ##NONCE##21##STAGEPREFIX##21##NONCENetlistEdgeDataFactory getNetlistEdgeDataFactory() {
		return this.netlistEdgeDataFactory;
	}
	
	/**
	 *  Setter for the ##NONCE##21##STAGEPREFIX##21##NONCENetlistEdgeDataFactor
	 *  @param netlistEdgeDataFactory the ##NONCE##21##STAGEPREFIX##21##NONCENetlistEdgeDataFactor
	 */
	private void setNetlistEdgeDataFactory(final ##NONCE##21##STAGEPREFIX##21##NONCENetlistEdgeDataFactory netlistEdgeDataFactory) {
		this.netlistEdgeDataFactory = netlistEdgeDataFactory;
	}
	
	private ##NONCE##21##STAGEPREFIX##21##NONCENetlistDataFactory netlistDataFactory;
	private ##NONCE##21##STAGEPREFIX##21##NONCENetlistEdgeDataFactory netlistEdgeDataFactory;
	private ##NONCE##21##STAGEPREFIX##21##NONCENetlistNodeDataFactory netlistNodeDataFactory;


	/**
	 *  Returns the Logger instance for the <i>##NONCE##21##STAGENAME##21##NONCE</i> stage.
	 *  @return the Logger instance for the <i>##NONCE##21##STAGENAME##21##NONCE</i> stage.
	 */
	protected Logger getLogger() {
		return ##NONCE##21##STAGEPREFIX##21##NONCERuntimeObject.logger;
	}
	private static final Logger logger = LogManager.getLogger(##NONCE##21##STAGEPREFIX##21##NONCERuntimeObject.class.getSimpleName());

}

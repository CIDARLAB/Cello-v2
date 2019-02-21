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
package org.cellocad.cello2.placing.runtime;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cellocad.cello2.common.netlistConstraint.data.NetlistConstraint;
import org.cellocad.cello2.common.runtime.RuntimeObject;
import org.cellocad.cello2.common.runtime.environment.RuntimeEnv;
import org.cellocad.cello2.common.stage.Stage;
import org.cellocad.cello2.common.target.data.TargetData;
import org.cellocad.cello2.placing.algorithm.PLAlgorithm;
import org.cellocad.cello2.placing.algorithm.PLAlgorithmFactory;
import org.cellocad.cello2.placing.algorithm.data.PLNetlistData;
import org.cellocad.cello2.placing.algorithm.data.PLNetlistDataFactory;
import org.cellocad.cello2.placing.algorithm.data.PLNetlistEdgeData;
import org.cellocad.cello2.placing.algorithm.data.PLNetlistEdgeDataFactory;
import org.cellocad.cello2.placing.algorithm.data.PLNetlistNodeData;
import org.cellocad.cello2.placing.algorithm.data.PLNetlistNodeDataFactory;
import org.cellocad.cello2.placing.common.PLUtils;
import org.cellocad.cello2.placing.netlist.data.PLStageNetlistData;
import org.cellocad.cello2.placing.netlist.data.PLStageNetlistEdgeData;
import org.cellocad.cello2.placing.netlist.data.PLStageNetlistNodeData;
import org.cellocad.cello2.placing.runtime.environment.PLArgString;
import org.cellocad.cello2.results.netlist.Netlist;
import org.cellocad.cello2.results.netlist.NetlistEdge;
import org.cellocad.cello2.results.netlist.NetlistNode;

/**
 * The PLRuntimeObject class is the RuntimeObject class for the <i>placing</i> stage.
 * 
 * @author Vincent Mirian
 * 
 * @date 2018-05-21
 *
 */
public class PLRuntimeObject extends RuntimeObject{

	/**
	 *  Initializes a newly created PLRuntimeObject with its <i>stage</i> set to parameter <i>stage</i>,
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
	public PLRuntimeObject(
			final Stage stage,
			final TargetData targetData,
			final NetlistConstraint netlistConstraint,
			final Netlist netlist,
			final RuntimeEnv runEnv
			) {
		super(stage, targetData, netlistConstraint, netlist, runEnv);
	}

	/**
	 * 	Prepares the DataFactory for the Netlist, NetlistNode and NetlistEdge of the placing stage.
	 */
	@Override
	protected void prepareDataFactory() {
		this.setNetlistDataFactory(new PLNetlistDataFactory());
		this.setNetlistNodeDataFactory(new PLNetlistNodeDataFactory());
		this.setNetlistEdgeDataFactory(new PLNetlistEdgeDataFactory());
	}

	
	/**
	 *  Sets the PLStageNetlistData for the placing stage in parameter <i>netlist</i>
	 *  <b>Note: this method will be deprecated in the future.</b>
	 *  
	 *  @param netlist the <i>netlist</i> of this instance
	 */
	@Override
	protected void setStageNetlistData(Netlist netlist) {
		netlist.setStageNetlistData(new PLStageNetlistData());
	}

	/**
	 *  Sets the PLStageNetlistNodeData for the placing stage in parameter <i>node</i>
	 *  <b>Note: this method will be deprecated in the future.</b>
	 *  
	 *  @param node a node within the <i>netlist</i> of this instance
	 */
	@Override
	protected void setStageNetlistNodeData(NetlistNode node) {
		node.setStageNetlistNodeData(new PLStageNetlistNodeData());
	}

	/**
	 *  Sets the PLStageNetlistEdgeData for the placing stage in parameter <i>edge</i>
	 *  <b>Note: method this will be deprecated in the future.</b>
	 *  
	 *  @param edge an edge within the <i>netlist</i> of this instance
	 */
	@Override
	protected void setStageNetlistEdgeData(NetlistEdge edge) {
		edge.setStageNetlistEdgeData(new PLStageNetlistEdgeData());
	}

	/**
	 *  Sets the NetlistData of the appropriate algorithm in parameter <i>netlist</i>
	 *  @param netlist the <i>netlist</i> of this instance
	 */
	@Override
	protected void setNetlistData(Netlist netlist) {
		PLNetlistData data = this.getNetlistDataFactory().getNetlistData(this.getAlgorithmProfile());
		netlist.setNetlistData(data);
	}

	/**
	 *  Sets the NetlistNodeData of the appropriate algorithm in parameter <i>node</i>
	 *  @param node a node within the <i>netlist</i> of this instance
	 */
	@Override
	protected void setNetlistNodeData(NetlistNode node) {
		PLNetlistNodeData data = this.getNetlistNodeDataFactory().getNetlistNodeData(this.getAlgorithmProfile());
		node.setNetlistNodeData(data);
	}

	/**
	 *  Sets the NetlistEdgeData of the appropriate algorithm in parameter <i>edge</i>
	 *  @param edge an edge within the <i>netlist</i> of this instance
	 */
	@Override
	protected void setNetlistEdgeData(NetlistEdge edge) {
		PLNetlistEdgeData data = this.getNetlistEdgeDataFactory().getNetlistEdgeData(this.getAlgorithmProfile());
		edge.setNetlistEdgeData(data);
	}

	/**
	 *  Returns a string representing the OPTIONS command line argument for the placing stage
	 *  
	 *  @return a string representing the OPTIONS command line argument for the placing stage
	 */
	@Override
	protected String getOptionsString() {
		return PLArgString.OPTIONS;
	}

	/**
	 * 	Executes the algorithm of the placing stage.
	 */
	@Override
	protected void runAlgo() {
		// get Algorithm from Factory
		PLAlgorithmFactory AF = new PLAlgorithmFactory();
		PLAlgorithm algo = AF.getAlgorithm(this.getAlgorithmProfile());
		//executeAlgo
		this.executeAlgo(algo);
	}

	/**
	 *  Getter for the PLNetlistDataFactory
	 *  @return the PLNetlistDataFactory
	 */
	protected PLNetlistDataFactory getNetlistDataFactory() {
		return this.netlistDataFactory;
	}
	
	/**
	 *  Setter for the PLNetlistDataFactory
	 *  @param netlistDataFactory the PLNetlistDataFactory
	 */
	private void setNetlistDataFactory(final PLNetlistDataFactory netlistDataFactory) {
		this.netlistDataFactory = netlistDataFactory;
	}
	
	/**
	 *  Getter for the PLNetlistNodeDataFactory
	 *  @return the PLNetlistNodeDataFactory
	 */
	protected PLNetlistNodeDataFactory getNetlistNodeDataFactory() {
		return this.netlistNodeDataFactory;
	}
	
	/**
	 *  Setter for the PLNetlistNodeDataFactory
	 *  @param netlistNodeDataFactory the PLNetlistNodeDataFactory
	 */
	private void setNetlistNodeDataFactory(final PLNetlistNodeDataFactory netlistNodeDataFactory) {
		this.netlistNodeDataFactory = netlistNodeDataFactory;
	}
	
	/**
	 *  Getter for the PLNetlistEdgeDataFactory
	 *  @return the PLNetlistEdgeDataFactory
	 */
	protected PLNetlistEdgeDataFactory getNetlistEdgeDataFactory() {
		return this.netlistEdgeDataFactory;
	}
	
	/**
	 *  Setter for the PLNetlistEdgeDataFactor
	 *  @param netlistEdgeDataFactory the PLNetlistEdgeDataFactor
	 */
	private void setNetlistEdgeDataFactory(final PLNetlistEdgeDataFactory netlistEdgeDataFactory) {
		this.netlistEdgeDataFactory = netlistEdgeDataFactory;
	}
	
	private PLNetlistDataFactory netlistDataFactory;
	private PLNetlistEdgeDataFactory netlistEdgeDataFactory;
	private PLNetlistNodeDataFactory netlistNodeDataFactory;


	/**
	 *  Returns the Logger instance for the <i>placing</i> stage.
	 *  @return the Logger instance for the <i>placing</i> stage.
	 */
	protected Logger getLogger() {
		return PLRuntimeObject.logger;
	}
	private static final Logger logger = LogManager.getLogger(PLRuntimeObject.class.getSimpleName());

}

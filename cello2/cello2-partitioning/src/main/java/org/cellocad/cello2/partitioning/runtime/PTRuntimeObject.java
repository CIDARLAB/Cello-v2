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
package org.cellocad.cello2.partitioning.runtime;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cellocad.cello2.common.netlistConstraint.data.NetlistConstraint;
import org.cellocad.cello2.common.runtime.RuntimeObject;
import org.cellocad.cello2.common.runtime.environment.RuntimeEnv;
import org.cellocad.cello2.common.stage.Stage;
import org.cellocad.cello2.common.target.data.TargetData;
import org.cellocad.cello2.partitioning.algorithm.PTAlgorithm;
import org.cellocad.cello2.partitioning.algorithm.PTAlgorithmFactory;
import org.cellocad.cello2.partitioning.algorithm.data.PTNetlistData;
import org.cellocad.cello2.partitioning.algorithm.data.PTNetlistDataFactory;
import org.cellocad.cello2.partitioning.algorithm.data.PTNetlistEdgeData;
import org.cellocad.cello2.partitioning.algorithm.data.PTNetlistEdgeDataFactory;
import org.cellocad.cello2.partitioning.algorithm.data.PTNetlistNodeData;
import org.cellocad.cello2.partitioning.algorithm.data.PTNetlistNodeDataFactory;
import org.cellocad.cello2.partitioning.netlist.data.PTStageNetlistData;
import org.cellocad.cello2.partitioning.netlist.data.PTStageNetlistEdgeData;
import org.cellocad.cello2.partitioning.netlist.data.PTStageNetlistNodeData;
import org.cellocad.cello2.partitioning.runtime.environment.PTArgString;
import org.cellocad.cello2.results.netlist.Netlist;
import org.cellocad.cello2.results.netlist.NetlistEdge;
import org.cellocad.cello2.results.netlist.NetlistNode;

/**
 * The PTRuntimeObject class is the RuntimeObject class for the <i>partitioning</i> stage.
 * 
 * @author Vincent Mirian
 * 
 * @date 2018-05-21
 *
 */
public class PTRuntimeObject extends RuntimeObject{

	/**
	 *  Initializes a newly created PTRuntimeObject with its <i>stage</i> set to parameter <i>stage</i>,
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
	public PTRuntimeObject(
			final Stage stage,
			final TargetData targetData,
			final NetlistConstraint netlistConstraint,
			final Netlist netlist,
			final RuntimeEnv runEnv
			) {
		super(stage, targetData, netlistConstraint, netlist, runEnv);
	}

	/**
	 * 	Prepares the DataFactory for the Netlist, NetlistNode and NetlistEdge of the partitioning stage.
	 */
	@Override
	protected void prepareDataFactory() {
		this.setNetlistDataFactory(new PTNetlistDataFactory());
		this.setNetlistNodeDataFactory(new PTNetlistNodeDataFactory());
		this.setNetlistEdgeDataFactory(new PTNetlistEdgeDataFactory());
	}

	
	/**
	 *  Sets the PTStageNetlistData for the partitioning stage in parameter <i>netlist</i>
	 *  <b>Note: this method will be deprecated in the future.</b>
	 *  
	 *  @param netlist the <i>netlist</i> of this instance
	 */
	@Override
	protected void setStageNetlistData(Netlist netlist) {
		netlist.setStageNetlistData(new PTStageNetlistData());
	}

	/**
	 *  Sets the PTStageNetlistNodeData for the partitioning stage in parameter <i>node</i>
	 *  <b>Note: this method will be deprecated in the future.</b>
	 *  
	 *  @param node a node within the <i>netlist</i> of this instance
	 */
	@Override
	protected void setStageNetlistNodeData(NetlistNode node) {
		node.setStageNetlistNodeData(new PTStageNetlistNodeData());
	}

	/**
	 *  Sets the PTStageNetlistEdgeData for the partitioning stage in parameter <i>edge</i>
	 *  <b>Note: method this will be deprecated in the future.</b>
	 *  
	 *  @param edge an edge within the <i>netlist</i> of this instance
	 */
	@Override
	protected void setStageNetlistEdgeData(NetlistEdge edge) {
		edge.setStageNetlistEdgeData(new PTStageNetlistEdgeData());
	}

	/**
	 *  Sets the NetlistData of the appropriate algorithm in parameter <i>netlist</i>
	 *  @param netlist the <i>netlist</i> of this instance
	 */
	@Override
	protected void setNetlistData(Netlist netlist) {
		PTNetlistData data = this.getNetlistDataFactory().getNetlistData(this.getAlgorithmProfile());
		netlist.setNetlistData(data);
	}

	/**
	 *  Sets the NetlistNodeData of the appropriate algorithm in parameter <i>node</i>
	 *  @param node a node within the <i>netlist</i> of this instance
	 */
	@Override
	protected void setNetlistNodeData(NetlistNode node) {
		PTNetlistNodeData data = this.getNetlistNodeDataFactory().getNetlistNodeData(this.getAlgorithmProfile());
		node.setNetlistNodeData(data);
	}

	/**
	 *  Sets the NetlistEdgeData of the appropriate algorithm in parameter <i>edge</i>
	 *  @param edge an edge within the <i>netlist</i> of this instance
	 */
	@Override
	protected void setNetlistEdgeData(NetlistEdge edge) {
		PTNetlistEdgeData data = this.getNetlistEdgeDataFactory().getNetlistEdgeData(this.getAlgorithmProfile());
		edge.setNetlistEdgeData(data);
	}

	/**
	 *  Returns a string representing the OPTIONS command line argument for the partitioning stage
	 *  
	 *  @return a string representing the OPTIONS command line argument for the partitioning stage
	 */
	@Override
	protected String getOptionsString() {
		return PTArgString.OPTIONS;
	}

	/**
	 * 	Executes the algorithm of the partitioning stage.
	 */
	@Override
	protected void runAlgo() {
		// get Algorithm from Factory
		PTAlgorithmFactory AF = new PTAlgorithmFactory();
		PTAlgorithm algo = AF.getAlgorithm(this.getAlgorithmProfile());
		//executeAlgo
		this.executeAlgo(algo);
	}

	/**
	 *  Getter for the PTNetlistDataFactory
	 *  @return the PTNetlistDataFactory
	 */
	protected PTNetlistDataFactory getNetlistDataFactory() {
		return this.netlistDataFactory;
	}
	
	/**
	 *  Setter for the PTNetlistDataFactory
	 *  @param netlistDataFactory the PTNetlistDataFactory
	 */
	private void setNetlistDataFactory(final PTNetlistDataFactory netlistDataFactory) {
		this.netlistDataFactory = netlistDataFactory;
	}
	
	/**
	 *  Getter for the PTNetlistNodeDataFactory
	 *  @return the PTNetlistNodeDataFactory
	 */
	protected PTNetlistNodeDataFactory getNetlistNodeDataFactory() {
		return this.netlistNodeDataFactory;
	}
	
	/**
	 *  Setter for the PTNetlistNodeDataFactory
	 *  @param netlistNodeDataFactory the PTNetlistNodeDataFactory
	 */
	private void setNetlistNodeDataFactory(final PTNetlistNodeDataFactory netlistNodeDataFactory) {
		this.netlistNodeDataFactory = netlistNodeDataFactory;
	}
	
	/**
	 *  Getter for the PTNetlistEdgeDataFactory
	 *  @return the PTNetlistEdgeDataFactory
	 */
	protected PTNetlistEdgeDataFactory getNetlistEdgeDataFactory() {
		return this.netlistEdgeDataFactory;
	}
	
	/**
	 *  Setter for the PTNetlistEdgeDataFactor
	 *  @param netlistEdgeDataFactory the PTNetlistEdgeDataFactor
	 */
	private void setNetlistEdgeDataFactory(final PTNetlistEdgeDataFactory netlistEdgeDataFactory) {
		this.netlistEdgeDataFactory = netlistEdgeDataFactory;
	}
	
	private PTNetlistDataFactory netlistDataFactory;
	private PTNetlistEdgeDataFactory netlistEdgeDataFactory;
	private PTNetlistNodeDataFactory netlistNodeDataFactory;


	/**
	 *  Returns the Logger instance for the <i>partitioning</i> stage.
	 *  @return the Logger instance for the <i>partitioning</i> stage.
	 */
	protected Logger getLogger() {
		return PTRuntimeObject.logger;
	}
	private static final Logger logger = LogManager.getLogger(PTRuntimeObject.class);

}

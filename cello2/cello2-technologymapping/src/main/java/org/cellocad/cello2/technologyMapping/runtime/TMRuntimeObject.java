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
package org.cellocad.cello2.technologyMapping.runtime;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cellocad.cello2.common.netlistConstraint.data.NetlistConstraint;
import org.cellocad.cello2.common.runtime.RuntimeObject;
import org.cellocad.cello2.common.runtime.environment.RuntimeEnv;
import org.cellocad.cello2.common.stage.Stage;
import org.cellocad.cello2.common.target.data.TargetData;
import org.cellocad.cello2.results.netlist.Netlist;
import org.cellocad.cello2.results.netlist.NetlistEdge;
import org.cellocad.cello2.results.netlist.NetlistNode;
import org.cellocad.cello2.technologyMapping.algorithm.TMAlgorithm;
import org.cellocad.cello2.technologyMapping.algorithm.TMAlgorithmFactory;
import org.cellocad.cello2.technologyMapping.algorithm.data.TMNetlistData;
import org.cellocad.cello2.technologyMapping.algorithm.data.TMNetlistDataFactory;
import org.cellocad.cello2.technologyMapping.algorithm.data.TMNetlistEdgeData;
import org.cellocad.cello2.technologyMapping.algorithm.data.TMNetlistEdgeDataFactory;
import org.cellocad.cello2.technologyMapping.algorithm.data.TMNetlistNodeData;
import org.cellocad.cello2.technologyMapping.algorithm.data.TMNetlistNodeDataFactory;
import org.cellocad.cello2.technologyMapping.common.TMUtils;
import org.cellocad.cello2.technologyMapping.netlist.data.TMStageNetlistData;
import org.cellocad.cello2.technologyMapping.netlist.data.TMStageNetlistEdgeData;
import org.cellocad.cello2.technologyMapping.netlist.data.TMStageNetlistNodeData;
import org.cellocad.cello2.technologyMapping.runtime.environment.TMArgString;

/**
 * The TMRuntimeObject class is the RuntimeObject class for the <i>technologyMapping</i> stage.
 * 
 * @author Vincent Mirian
 * 
 * @date 2018-05-21
 *
 */
public class TMRuntimeObject extends RuntimeObject{

	/**
	 *  Initializes a newly created TMRuntimeObject with its <i>stage</i> set to parameter <i>stage</i>,
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
	public TMRuntimeObject(
			final Stage stage,
			final TargetData targetData,
			final NetlistConstraint netlistConstraint,
			final Netlist netlist,
			final RuntimeEnv runEnv
			) {
		super(stage, targetData, netlistConstraint, netlist, runEnv);
	}

	/**
	 * 	Prepares the DataFactory for the Netlist, NetlistNode and NetlistEdge of the technologyMapping stage.
	 */
	@Override
	protected void prepareDataFactory() {
		this.setNetlistDataFactory(new TMNetlistDataFactory());
		this.setNetlistNodeDataFactory(new TMNetlistNodeDataFactory());
		this.setNetlistEdgeDataFactory(new TMNetlistEdgeDataFactory());
	}

	
	/**
	 *  Sets the TMStageNetlistData for the technologyMapping stage in parameter <i>netlist</i>
	 *  <b>Note: this method will be deprecated in the future.</b>
	 *  
	 *  @param netlist the <i>netlist</i> of this instance
	 */
	@Override
	protected void setStageNetlistData(Netlist netlist) {
		netlist.setStageNetlistData(new TMStageNetlistData());
	}

	/**
	 *  Sets the TMStageNetlistNodeData for the technologyMapping stage in parameter <i>node</i>
	 *  <b>Note: this method will be deprecated in the future.</b>
	 *  
	 *  @param node a node within the <i>netlist</i> of this instance
	 */
	@Override
	protected void setStageNetlistNodeData(NetlistNode node) {
		node.setStageNetlistNodeData(new TMStageNetlistNodeData());
	}

	/**
	 *  Sets the TMStageNetlistEdgeData for the technologyMapping stage in parameter <i>edge</i>
	 *  <b>Note: method this will be deprecated in the future.</b>
	 *  
	 *  @param edge an edge within the <i>netlist</i> of this instance
	 */
	@Override
	protected void setStageNetlistEdgeData(NetlistEdge edge) {
		edge.setStageNetlistEdgeData(new TMStageNetlistEdgeData());
	}

	/**
	 *  Sets the NetlistData of the appropriate algorithm in parameter <i>netlist</i>
	 *  @param netlist the <i>netlist</i> of this instance
	 */
	@Override
	protected void setNetlistData(Netlist netlist) {
		TMNetlistData data = this.getNetlistDataFactory().getNetlistData(this.getAlgorithmProfile());
		netlist.setNetlistData(data);
	}

	/**
	 *  Sets the NetlistNodeData of the appropriate algorithm in parameter <i>node</i>
	 *  @param node a node within the <i>netlist</i> of this instance
	 */
	@Override
	protected void setNetlistNodeData(NetlistNode node) {
		TMNetlistNodeData data = this.getNetlistNodeDataFactory().getNetlistNodeData(this.getAlgorithmProfile());
		node.setNetlistNodeData(data);
	}

	/**
	 *  Sets the NetlistEdgeData of the appropriate algorithm in parameter <i>edge</i>
	 *  @param edge an edge within the <i>netlist</i> of this instance
	 */
	@Override
	protected void setNetlistEdgeData(NetlistEdge edge) {
		TMNetlistEdgeData data = this.getNetlistEdgeDataFactory().getNetlistEdgeData(this.getAlgorithmProfile());
		edge.setNetlistEdgeData(data);
	}
	
	/**
	 * Returns the path of the Resources directory for the technologyMapping stage
	 * 
	 * @return the path of the Resources directory for the technologyMapping stage
	 *
	 */
	@Override
	protected String getResourcesFilepath() {
		String rtn = null;
		rtn = TMUtils.getResourcesFilepath();
		return rtn;
	}

	/**
	 *  Returns a string representing the OPTIONS command line argument for the technologyMapping stage
	 *  
	 *  @return a string representing the OPTIONS command line argument for the technologyMapping stage
	 */
	@Override
	protected String getOptionsString() {
		return TMArgString.OPTIONS;
	}

	/**
	 * 	Executes the algorithm of the technologyMapping stage.
	 */
	@Override
	protected void runAlgo() {
		// get Algorithm from Factory
		TMAlgorithmFactory AF = new TMAlgorithmFactory();
		TMAlgorithm algo = AF.getAlgorithm(this.getAlgorithmProfile());
		//executeAlgo
		this.executeAlgo(algo);
	}

	/**
	 *  Getter for the TMNetlistDataFactory
	 *  @return the TMNetlistDataFactory
	 */
	protected TMNetlistDataFactory getNetlistDataFactory() {
		return this.netlistDataFactory;
	}
	
	/**
	 *  Setter for the TMNetlistDataFactory
	 *  @param netlistDataFactory the TMNetlistDataFactory
	 */
	private void setNetlistDataFactory(final TMNetlistDataFactory netlistDataFactory) {
		this.netlistDataFactory = netlistDataFactory;
	}
	
	/**
	 *  Getter for the TMNetlistNodeDataFactory
	 *  @return the TMNetlistNodeDataFactory
	 */
	protected TMNetlistNodeDataFactory getNetlistNodeDataFactory() {
		return this.netlistNodeDataFactory;
	}
	
	/**
	 *  Setter for the TMNetlistNodeDataFactory
	 *  @param netlistNodeDataFactory the TMNetlistNodeDataFactory
	 */
	private void setNetlistNodeDataFactory(final TMNetlistNodeDataFactory netlistNodeDataFactory) {
		this.netlistNodeDataFactory = netlistNodeDataFactory;
	}
	
	/**
	 *  Getter for the TMNetlistEdgeDataFactory
	 *  @return the TMNetlistEdgeDataFactory
	 */
	protected TMNetlistEdgeDataFactory getNetlistEdgeDataFactory() {
		return this.netlistEdgeDataFactory;
	}
	
	/**
	 *  Setter for the TMNetlistEdgeDataFactor
	 *  @param netlistEdgeDataFactory the TMNetlistEdgeDataFactor
	 */
	private void setNetlistEdgeDataFactory(final TMNetlistEdgeDataFactory netlistEdgeDataFactory) {
		this.netlistEdgeDataFactory = netlistEdgeDataFactory;
	}
	
	private TMNetlistDataFactory netlistDataFactory;
	private TMNetlistEdgeDataFactory netlistEdgeDataFactory;
	private TMNetlistNodeDataFactory netlistNodeDataFactory;


	/**
	 *  Returns the Logger instance for the <i>technologyMapping</i> stage.
	 *  @return the Logger instance for the <i>technologyMapping</i> stage.
	 */
	protected Logger getLogger() {
		return TMRuntimeObject.logger;
	}
	private static final Logger logger = LogManager.getLogger(TMRuntimeObject.class.getSimpleName());

}

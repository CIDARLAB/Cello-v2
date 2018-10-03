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
package logicSynthesis.runtime;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import results.netlist.Netlist;
import results.netlist.NetlistEdge;
import results.netlist.NetlistNode;
import common.netlistConstraint.data.NetlistConstraint;
import common.runtime.RuntimeObject;
import common.runtime.environment.RuntimeEnv;
import common.stage.Stage;
import common.target.data.TargetData;
import logicSynthesis.algorithm.LSAlgorithm;
import logicSynthesis.algorithm.LSAlgorithmFactory;
import logicSynthesis.algorithm.data.LSNetlistData;
import logicSynthesis.algorithm.data.LSNetlistDataFactory;
import logicSynthesis.algorithm.data.LSNetlistEdgeData;
import logicSynthesis.algorithm.data.LSNetlistEdgeDataFactory;
import logicSynthesis.algorithm.data.LSNetlistNodeData;
import logicSynthesis.algorithm.data.LSNetlistNodeDataFactory;
import logicSynthesis.common.LSUtils;
import logicSynthesis.netlist.data.LSStageNetlistData;
import logicSynthesis.netlist.data.LSStageNetlistNodeData;
import logicSynthesis.netlist.data.LSStageNetlistEdgeData;
import logicSynthesis.runtime.environment.LSArgString;

/**
 * The LSRuntimeObject class is the RuntimeObject class for the <i>logicSynthesis</i> stage.
 * 
 * @author Vincent Mirian
 * 
 * @date 2018-05-21
 *
 */
public class LSRuntimeObject extends RuntimeObject{

	/**
	 *  Initializes a newly created LSRuntimeObject with its <i>stage</i> set to parameter <i>stage</i>,
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
	public LSRuntimeObject(
			final Stage stage,
			final TargetData targetData,
			final NetlistConstraint netlistConstraint,
			final Netlist netlist,
			final RuntimeEnv runEnv
			) {
		super(stage, targetData, netlistConstraint, netlist, runEnv);
	}

	/**
	 * 	Prepares the DataFactory for the Netlist, NetlistNode and NetlistEdge of the logicSynthesis stage.
	 */
	@Override
	protected void prepareDataFactory() {
		this.setNetlistDataFactory(new LSNetlistDataFactory());
		this.setNetlistNodeDataFactory(new LSNetlistNodeDataFactory());
		this.setNetlistEdgeDataFactory(new LSNetlistEdgeDataFactory());
	}

	
	/**
	 *  Sets the LSStageNetlistData for the logicSynthesis stage in parameter <i>netlist</i>
	 *  <b>Note: this method will be deprecated in the future.</b>
	 *  
	 *  @param netlist the <i>netlist</i> of this instance
	 */
	@Override
	protected void setStageNetlistData(Netlist netlist) {
		netlist.setStageNetlistData(new LSStageNetlistData());
	}

	/**
	 *  Sets the LSStageNetlistNodeData for the logicSynthesis stage in parameter <i>node</i>
	 *  <b>Note: this method will be deprecated in the future.</b>
	 *  
	 *  @param node a node within the <i>netlist</i> of this instance
	 */
	@Override
	protected void setStageNetlistNodeData(NetlistNode node) {
		node.setStageNetlistNodeData(new LSStageNetlistNodeData());
	}

	/**
	 *  Sets the LSStageNetlistEdgeData for the logicSynthesis stage in parameter <i>edge</i>
	 *  <b>Note: method this will be deprecated in the future.</b>
	 *  
	 *  @param edge an edge within the <i>netlist</i> of this instance
	 */
	@Override
	protected void setStageNetlistEdgeData(NetlistEdge edge) {
		edge.setStageNetlistEdgeData(new LSStageNetlistEdgeData());
	}

	/**
	 *  Sets the NetlistData of the appropriate algorithm in parameter <i>netlist</i>
	 *  @param netlist the <i>netlist</i> of this instance
	 */
	@Override
	protected void setNetlistData(Netlist netlist) {
		LSNetlistData data = this.getNetlistDataFactory().getNetlistData(this.getAlgorithmProfile());
		netlist.setNetlistData(data);
	}

	/**
	 *  Sets the NetlistNodeData of the appropriate algorithm in parameter <i>node</i>
	 *  @param node a node within the <i>netlist</i> of this instance
	 */
	@Override
	protected void setNetlistNodeData(NetlistNode node) {
		LSNetlistNodeData data = this.getNetlistNodeDataFactory().getNetlistNodeData(this.getAlgorithmProfile());
		node.setNetlistNodeData(data);
	}

	/**
	 *  Sets the NetlistEdgeData of the appropriate algorithm in parameter <i>edge</i>
	 *  @param edge an edge within the <i>netlist</i> of this instance
	 */
	@Override
	protected void setNetlistEdgeData(NetlistEdge edge) {
		LSNetlistEdgeData data = this.getNetlistEdgeDataFactory().getNetlistEdgeData(this.getAlgorithmProfile());
		edge.setNetlistEdgeData(data);
	}
	
	/**
	 * Returns the path of the Resources directory for the logicSynthesis stage
	 * 
	 * @return the path of the Resources directory for the logicSynthesis stage
	 *
	 */
	@Override
	protected String getResourcesFilepath() {
		String rtn = null;
		rtn = LSUtils.getResourcesFilepath();
		return rtn;
	}

	/**
	 *  Returns a string representing the OPTIONS command line argument for the logicSynthesis stage
	 *  
	 *  @return a string representing the OPTIONS command line argument for the logicSynthesis stage
	 */
	@Override
	protected String getOptionsString() {
		return LSArgString.OPTIONS;
	}

	/**
	 * 	Executes the algorithm of the logicSynthesis stage.
	 */
	@Override
	protected void runAlgo() {
		// get Algorithm from Factory
		LSAlgorithmFactory AF = new LSAlgorithmFactory();
		LSAlgorithm algo = AF.getAlgorithm(this.getAlgorithmProfile());
		//executeAlgo
		this.executeAlgo(algo);
	}

	/**
	 *  Getter for the LSNetlistDataFactory
	 *  @return the LSNetlistDataFactory
	 */
	protected LSNetlistDataFactory getNetlistDataFactory() {
		return this.netlistDataFactory;
	}
	
	/**
	 *  Setter for the LSNetlistDataFactory
	 *  @param netlistDataFactory the LSNetlistDataFactory
	 */
	private void setNetlistDataFactory(final LSNetlistDataFactory netlistDataFactory) {
		this.netlistDataFactory = netlistDataFactory;
	}
	
	/**
	 *  Getter for the LSNetlistNodeDataFactory
	 *  @return the LSNetlistNodeDataFactory
	 */
	protected LSNetlistNodeDataFactory getNetlistNodeDataFactory() {
		return this.netlistNodeDataFactory;
	}
	
	/**
	 *  Setter for the LSNetlistNodeDataFactory
	 *  @param netlistNodeDataFactory the LSNetlistNodeDataFactory
	 */
	private void setNetlistNodeDataFactory(final LSNetlistNodeDataFactory netlistNodeDataFactory) {
		this.netlistNodeDataFactory = netlistNodeDataFactory;
	}
	
	/**
	 *  Getter for the LSNetlistEdgeDataFactory
	 *  @return the LSNetlistEdgeDataFactory
	 */
	protected LSNetlistEdgeDataFactory getNetlistEdgeDataFactory() {
		return this.netlistEdgeDataFactory;
	}
	
	/**
	 *  Setter for the LSNetlistEdgeDataFactor
	 *  @param netlistEdgeDataFactory the LSNetlistEdgeDataFactor
	 */
	private void setNetlistEdgeDataFactory(final LSNetlistEdgeDataFactory netlistEdgeDataFactory) {
		this.netlistEdgeDataFactory = netlistEdgeDataFactory;
	}
	
	private LSNetlistDataFactory netlistDataFactory;
	private LSNetlistEdgeDataFactory netlistEdgeDataFactory;
	private LSNetlistNodeDataFactory netlistNodeDataFactory;


	/**
	 *  Returns the Logger instance for the <i>logicSynthesis</i> stage.
	 *  @return the Logger instance for the <i>logicSynthesis</i> stage.
	 */
	protected Logger getLogger() {
		return LSRuntimeObject.logger;
	}
	private static final Logger logger = LogManager.getLogger(LSRuntimeObject.class.getSimpleName());

}

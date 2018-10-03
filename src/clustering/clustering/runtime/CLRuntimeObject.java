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
package clustering.runtime;

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
import clustering.algorithm.CLAlgorithm;
import clustering.algorithm.CLAlgorithmFactory;
import clustering.algorithm.data.CLNetlistData;
import clustering.algorithm.data.CLNetlistDataFactory;
import clustering.algorithm.data.CLNetlistEdgeData;
import clustering.algorithm.data.CLNetlistEdgeDataFactory;
import clustering.algorithm.data.CLNetlistNodeData;
import clustering.algorithm.data.CLNetlistNodeDataFactory;
import clustering.common.CLUtils;
import clustering.netlist.data.CLStageNetlistData;
import clustering.netlist.data.CLStageNetlistNodeData;
import clustering.netlist.data.CLStageNetlistEdgeData;
import clustering.runtime.environment.CLArgString;

/**
 * The CLRuntimeObject class is the RuntimeObject class for the <i>clustering</i> stage.
 * 
 * @author Vincent Mirian
 * 
 * @date 2018-05-21
 *
 */
public class CLRuntimeObject extends RuntimeObject{

	/**
	 *  Initializes a newly created CLRuntimeObject with its <i>stage</i> set to parameter <i>stage</i>,
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
	public CLRuntimeObject(
			final Stage stage,
			final TargetData targetData,
			final NetlistConstraint netlistConstraint,
			final Netlist netlist,
			final RuntimeEnv runEnv
			) {
		super(stage, targetData, netlistConstraint, netlist, runEnv);
	}

	/**
	 * 	Prepares the DataFactory for the Netlist, NetlistNode and NetlistEdge of the clustering stage.
	 */
	@Override
	protected void prepareDataFactory() {
		this.setNetlistDataFactory(new CLNetlistDataFactory());
		this.setNetlistNodeDataFactory(new CLNetlistNodeDataFactory());
		this.setNetlistEdgeDataFactory(new CLNetlistEdgeDataFactory());
	}

	
	/**
	 *  Sets the CLStageNetlistData for the clustering stage in parameter <i>netlist</i>
	 *  <b>Note: this method will be deprecated in the future.</b>
	 *  
	 *  @param netlist the <i>netlist</i> of this instance
	 */
	@Override
	protected void setStageNetlistData(Netlist netlist) {
		netlist.setStageNetlistData(new CLStageNetlistData());
	}

	/**
	 *  Sets the CLStageNetlistNodeData for the clustering stage in parameter <i>node</i>
	 *  <b>Note: this method will be deprecated in the future.</b>
	 *  
	 *  @param node a node within the <i>netlist</i> of this instance
	 */
	@Override
	protected void setStageNetlistNodeData(NetlistNode node) {
		node.setStageNetlistNodeData(new CLStageNetlistNodeData());
	}

	/**
	 *  Sets the CLStageNetlistEdgeData for the clustering stage in parameter <i>edge</i>
	 *  <b>Note: method this will be deprecated in the future.</b>
	 *  
	 *  @param edge an edge within the <i>netlist</i> of this instance
	 */
	@Override
	protected void setStageNetlistEdgeData(NetlistEdge edge) {
		edge.setStageNetlistEdgeData(new CLStageNetlistEdgeData());
	}

	/**
	 *  Sets the NetlistData of the appropriate algorithm in parameter <i>netlist</i>
	 *  @param netlist the <i>netlist</i> of this instance
	 */
	@Override
	protected void setNetlistData(Netlist netlist) {
		CLNetlistData data = this.getNetlistDataFactory().getNetlistData(this.getAlgorithmProfile());
		netlist.setNetlistData(data);
	}

	/**
	 *  Sets the NetlistNodeData of the appropriate algorithm in parameter <i>node</i>
	 *  @param node a node within the <i>netlist</i> of this instance
	 */
	@Override
	protected void setNetlistNodeData(NetlistNode node) {
		CLNetlistNodeData data = this.getNetlistNodeDataFactory().getNetlistNodeData(this.getAlgorithmProfile());
		node.setNetlistNodeData(data);
	}

	/**
	 *  Sets the NetlistEdgeData of the appropriate algorithm in parameter <i>edge</i>
	 *  @param edge an edge within the <i>netlist</i> of this instance
	 */
	@Override
	protected void setNetlistEdgeData(NetlistEdge edge) {
		CLNetlistEdgeData data = this.getNetlistEdgeDataFactory().getNetlistEdgeData(this.getAlgorithmProfile());
		edge.setNetlistEdgeData(data);
	}
	
	/**
	 * Returns the path of the Resources directory for the clustering stage
	 * 
	 * @return the path of the Resources directory for the clustering stage
	 *
	 */
	@Override
	protected String getResourcesFilepath() {
		String rtn = null;
		rtn = CLUtils.getResourcesFilepath();
		return rtn;
	}

	/**
	 *  Returns a string representing the OPTIONS command line argument for the clustering stage
	 *  
	 *  @return a string representing the OPTIONS command line argument for the clustering stage
	 */
	@Override
	protected String getOptionsString() {
		return CLArgString.OPTIONS;
	}

	/**
	 * 	Executes the algorithm of the clustering stage.
	 */
	@Override
	protected void runAlgo() {
		// get Algorithm from Factory
		CLAlgorithmFactory AF = new CLAlgorithmFactory();
		CLAlgorithm algo = AF.getAlgorithm(this.getAlgorithmProfile());
		//executeAlgo
		this.executeAlgo(algo);
	}

	/**
	 *  Getter for the CLNetlistDataFactory
	 *  @return the CLNetlistDataFactory
	 */
	protected CLNetlistDataFactory getNetlistDataFactory() {
		return this.netlistDataFactory;
	}
	
	/**
	 *  Setter for the CLNetlistDataFactory
	 *  @param netlistDataFactory the CLNetlistDataFactory
	 */
	private void setNetlistDataFactory(final CLNetlistDataFactory netlistDataFactory) {
		this.netlistDataFactory = netlistDataFactory;
	}
	
	/**
	 *  Getter for the CLNetlistNodeDataFactory
	 *  @return the CLNetlistNodeDataFactory
	 */
	protected CLNetlistNodeDataFactory getNetlistNodeDataFactory() {
		return this.netlistNodeDataFactory;
	}
	
	/**
	 *  Setter for the CLNetlistNodeDataFactory
	 *  @param netlistNodeDataFactory the CLNetlistNodeDataFactory
	 */
	private void setNetlistNodeDataFactory(final CLNetlistNodeDataFactory netlistNodeDataFactory) {
		this.netlistNodeDataFactory = netlistNodeDataFactory;
	}
	
	/**
	 *  Getter for the CLNetlistEdgeDataFactory
	 *  @return the CLNetlistEdgeDataFactory
	 */
	protected CLNetlistEdgeDataFactory getNetlistEdgeDataFactory() {
		return this.netlistEdgeDataFactory;
	}
	
	/**
	 *  Setter for the CLNetlistEdgeDataFactor
	 *  @param netlistEdgeDataFactory the CLNetlistEdgeDataFactor
	 */
	private void setNetlistEdgeDataFactory(final CLNetlistEdgeDataFactory netlistEdgeDataFactory) {
		this.netlistEdgeDataFactory = netlistEdgeDataFactory;
	}
	
	private CLNetlistDataFactory netlistDataFactory;
	private CLNetlistEdgeDataFactory netlistEdgeDataFactory;
	private CLNetlistNodeDataFactory netlistNodeDataFactory;


	/**
	 *  Returns the Logger instance for the <i>clustering</i> stage.
	 *  @return the Logger instance for the <i>clustering</i> stage.
	 */
	protected Logger getLogger() {
		return CLRuntimeObject.logger;
	}
	private static final Logger logger = LogManager.getLogger(CLRuntimeObject.class.getSimpleName());

}

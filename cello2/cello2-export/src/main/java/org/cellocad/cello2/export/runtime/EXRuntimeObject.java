/**
 * Copyright (C) 2018 Boston University (BU)
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
package org.cellocad.cello2.export.runtime;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cellocad.cello2.common.CelloException;
import org.cellocad.cello2.common.netlistConstraint.data.NetlistConstraint;
import org.cellocad.cello2.common.runtime.RuntimeObject;
import org.cellocad.cello2.common.runtime.environment.RuntimeEnv;
import org.cellocad.cello2.common.stage.Stage;
import org.cellocad.cello2.common.target.data.TargetData;
import org.cellocad.cello2.export.algorithm.EXAlgorithm;
import org.cellocad.cello2.export.algorithm.EXAlgorithmFactory;
import org.cellocad.cello2.export.algorithm.data.EXNetlistData;
import org.cellocad.cello2.export.algorithm.data.EXNetlistDataFactory;
import org.cellocad.cello2.export.algorithm.data.EXNetlistEdgeData;
import org.cellocad.cello2.export.algorithm.data.EXNetlistEdgeDataFactory;
import org.cellocad.cello2.export.algorithm.data.EXNetlistNodeData;
import org.cellocad.cello2.export.algorithm.data.EXNetlistNodeDataFactory;
import org.cellocad.cello2.export.netlist.data.EXStageNetlistData;
import org.cellocad.cello2.export.netlist.data.EXStageNetlistEdgeData;
import org.cellocad.cello2.export.netlist.data.EXStageNetlistNodeData;
import org.cellocad.cello2.export.runtime.environment.EXArgString;
import org.cellocad.cello2.results.netlist.Netlist;
import org.cellocad.cello2.results.netlist.NetlistEdge;
import org.cellocad.cello2.results.netlist.NetlistNode;

/**
 * The EXRuntimeObject class is the RuntimeObject class for the <i>export</i>
 * stage.
 * 
 * @author Timothy Jones
 * 
 * @date 2018-06-04
 *
 */
public class EXRuntimeObject extends RuntimeObject {

	/**
	 * Initializes a newly created EXRuntimeObject with its <i>stage</i> set to
	 * parameter <i>stage</i>, its <i>targetData</i> set to parameter
	 * <i>targetData</i>, its <i>netlist</i> set to parameter <i>netlist</i>, and,
	 * its <i>runEnv</i> set to parameter <i>runEnv</i>.
	 * 
	 * @param stage             Stage used during execution
	 * @param targetData        TargetData used during execution
	 * @param netlistConstraint NetlistConstraint used during execution
	 * @param netlist           Netlist used during execution
	 * @param runEnv            RuntimeEnv used during execution
	 * @throws RuntimeException if any of the parameters are null
	 */
	public EXRuntimeObject(final Stage stage, final TargetData targetData, final NetlistConstraint netlistConstraint,
			final Netlist netlist, final RuntimeEnv runEnv) {
		super(stage, targetData, netlistConstraint, netlist, runEnv);
	}

	/**
	 * Prepares the DataFactory for the Netlist, NetlistNode and NetlistEdge of the
	 * export stage.
	 */
	@Override
	protected void prepareDataFactory() {
		this.setNetlistDataFactory(new EXNetlistDataFactory());
		this.setNetlistNodeDataFactory(new EXNetlistNodeDataFactory());
		this.setNetlistEdgeDataFactory(new EXNetlistEdgeDataFactory());
	}

	/**
	 * Sets the EXStageNetlistData for the export stage in parameter <i>netlist</i>
	 * <b>Note: this method will be deprecated in the future.</b>
	 * 
	 * @param netlist the <i>netlist</i> of this instance
	 */
	@Override
	protected void setStageNetlistData(Netlist netlist) {
		netlist.setStageNetlistData(new EXStageNetlistData());
	}

	/**
	 * Sets the EXStageNetlistNodeData for the export stage in parameter <i>node</i>
	 * <b>Note: this method will be deprecated in the future.</b>
	 * 
	 * @param node a node within the <i>netlist</i> of this instance
	 */
	@Override
	protected void setStageNetlistNodeData(NetlistNode node) {
		node.setStageNetlistNodeData(new EXStageNetlistNodeData());
	}

	/**
	 * Sets the EXStageNetlistEdgeData for the export stage in parameter <i>edge</i>
	 * <b>Note: method this will be deprecated in the future.</b>
	 * 
	 * @param edge an edge within the <i>netlist</i> of this instance
	 */
	@Override
	protected void setStageNetlistEdgeData(NetlistEdge edge) {
		edge.setStageNetlistEdgeData(new EXStageNetlistEdgeData());
	}

	/**
	 * Sets the NetlistData of the appropriate algorithm in parameter <i>netlist</i>
	 * 
	 * @param netlist the <i>netlist</i> of this instance
	 */
	@Override
	protected void setNetlistData(Netlist netlist) {
		EXNetlistData data = this.getNetlistDataFactory().getNetlistData(this.getAlgorithmProfile());
		netlist.setNetlistData(data);
	}

	/**
	 * Sets the NetlistNodeData of the appropriate algorithm in parameter
	 * <i>node</i>
	 * 
	 * @param node a node within the <i>netlist</i> of this instance
	 */
	@Override
	protected void setNetlistNodeData(NetlistNode node) {
		EXNetlistNodeData data = this.getNetlistNodeDataFactory().getNetlistNodeData(this.getAlgorithmProfile());
		node.setNetlistNodeData(data);
	}

	/**
	 * Sets the NetlistEdgeData of the appropriate algorithm in parameter
	 * <i>edge</i>
	 * 
	 * @param edge an edge within the <i>netlist</i> of this instance
	 */
	@Override
	protected void setNetlistEdgeData(NetlistEdge edge) {
		EXNetlistEdgeData data = this.getNetlistEdgeDataFactory().getNetlistEdgeData(this.getAlgorithmProfile());
		edge.setNetlistEdgeData(data);
	}

	/**
	 * Returns a string representing the OPTIONS command line argument for the
	 * export stage
	 * 
	 * @return a string representing the OPTIONS command line argument for the
	 *         export stage
	 */
	@Override
	protected String getOptionsString() {
		return EXArgString.OPTIONS;
	}

	/**
	 * Executes the algorithm of the export stage.
	 * 
	 * @throws CelloException
	 */
	@Override
	protected void runAlgo() throws CelloException {
		// get Algorithm from Factory
		EXAlgorithmFactory AF = new EXAlgorithmFactory();
		EXAlgorithm algo = AF.getAlgorithm(this.getAlgorithmProfile());
		// executeAlgo
		this.executeAlgo(algo);
	}

	/**
	 * Getter for the EXNetlistDataFactory
	 * 
	 * @return the EXNetlistDataFactory
	 */
	protected EXNetlistDataFactory getNetlistDataFactory() {
		return this.netlistDataFactory;
	}

	/**
	 * Setter for the EXNetlistDataFactory
	 * 
	 * @param netlistDataFactory the EXNetlistDataFactory
	 */
	private void setNetlistDataFactory(final EXNetlistDataFactory netlistDataFactory) {
		this.netlistDataFactory = netlistDataFactory;
	}

	/**
	 * Getter for the EXNetlistNodeDataFactory
	 * 
	 * @return the EXNetlistNodeDataFactory
	 */
	protected EXNetlistNodeDataFactory getNetlistNodeDataFactory() {
		return this.netlistNodeDataFactory;
	}

	/**
	 * Setter for the EXNetlistNodeDataFactory
	 * 
	 * @param netlistNodeDataFactory the EXNetlistNodeDataFactory
	 */
	private void setNetlistNodeDataFactory(final EXNetlistNodeDataFactory netlistNodeDataFactory) {
		this.netlistNodeDataFactory = netlistNodeDataFactory;
	}

	/**
	 * Getter for the EXNetlistEdgeDataFactory
	 * 
	 * @return the EXNetlistEdgeDataFactory
	 */
	protected EXNetlistEdgeDataFactory getNetlistEdgeDataFactory() {
		return this.netlistEdgeDataFactory;
	}

	/**
	 * Setter for the EXNetlistEdgeDataFactor
	 * 
	 * @param netlistEdgeDataFactory the EXNetlistEdgeDataFactor
	 */
	private void setNetlistEdgeDataFactory(final EXNetlistEdgeDataFactory netlistEdgeDataFactory) {
		this.netlistEdgeDataFactory = netlistEdgeDataFactory;
	}

	private EXNetlistDataFactory netlistDataFactory;
	private EXNetlistEdgeDataFactory netlistEdgeDataFactory;
	private EXNetlistNodeDataFactory netlistNodeDataFactory;

	/**
	 * Returns the Logger instance for the <i>export</i> stage.
	 * 
	 * @return the Logger instance for the <i>export</i> stage.
	 */
	protected Logger getLogger() {
		return EXRuntimeObject.logger;
	}

	private static final Logger logger = LogManager.getLogger(EXRuntimeObject.class);

}

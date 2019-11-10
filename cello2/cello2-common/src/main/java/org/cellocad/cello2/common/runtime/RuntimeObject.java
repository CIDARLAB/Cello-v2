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
package org.cellocad.cello2.common.runtime;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cellocad.cello2.common.CObject;
import org.cellocad.cello2.common.CelloException;
import org.cellocad.cello2.common.Utils;
import org.cellocad.cello2.common.algorithm.Algorithm;
import org.cellocad.cello2.common.netlistConstraint.data.NetlistConstraint;
import org.cellocad.cello2.common.options.Options;
import org.cellocad.cello2.common.options.OptionsUtils;
import org.cellocad.cello2.common.profile.AlgorithmProfile;
import org.cellocad.cello2.common.profile.AlgorithmProfileUtils;
import org.cellocad.cello2.common.runtime.environment.RuntimeEnv;
import org.cellocad.cello2.common.stage.Stage;
import org.cellocad.cello2.common.target.data.TargetData;
import org.cellocad.cello2.results.netlist.Netlist;
import org.cellocad.cello2.results.netlist.NetlistEdge;
import org.cellocad.cello2.results.netlist.NetlistNode;

/**
 * The RuntimeObject class is a base class for all stage RuntimeObject classes
 * within the Poros framework.
 * 
 * @author Vincent Mirian
 * 
 * @date Nov 17, 2017
 *
 */
//Object that aggregates the netlist, the stage configuration, target data and RuntimeEnv
abstract public class RuntimeObject extends CObject {

	/**
	 * Initializes a newly created RuntimeObject with its <i>stage</i> set to
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
	public RuntimeObject(final Stage stage, final TargetData targetData, final NetlistConstraint netlistConstraint,
			final Netlist netlist, final RuntimeEnv runEnv) {
		super();
		Utils.isNullRuntimeException(stage, "stage");
		Utils.isNullRuntimeException(targetData, "targetData");
		Utils.isNullRuntimeException(netlistConstraint, "netlistConstraint");
		Utils.isNullRuntimeException(netlist, "netlist");
		Utils.isNullRuntimeException(runEnv, "runEnv");
		this.stage = stage;
		this.targetData = targetData;
		this.netlistConstraint = netlistConstraint;
		this.netlist = netlist;
		this.runEnv = runEnv;
		this.setName(stage.getName());
	}

	/**
	 * Getter for <i>AProfile</i>
	 * 
	 * @return the AlgorithmProfile of this instance
	 */
	protected AlgorithmProfile getAlgorithmProfile() {
		return this.AProfile;
	}

	private void setAlgorithmProfile(final AlgorithmProfile AProfile) {
		this.AProfile = AProfile;
	}

	/**
	 * Getter for <i>stage</i>
	 * 
	 * @return the Stage of this instance
	 */
	protected Stage getStage() {
		return this.stage;
	}

	/**
	 * Getter for <i>targetData</i>
	 * 
	 * @return the TargetData of this instance
	 */
	protected TargetData getTargetData() {
		return this.targetData;
	}

	/**
	 * Getter for <i>netlistConstraint</i>
	 * 
	 * @return the NetlistConstraint of this instance
	 */
	protected NetlistConstraint getNetlistConstraint() {
		return this.netlistConstraint;
	}

	/**
	 * Getter for <i>netlist</i>
	 * 
	 * @return the Netlist of this instance
	 */
	protected Netlist getNetlist() {
		return this.netlist;
	}

	/**
	 * Getter for <i>runtimeEnv</i>
	 * 
	 * @return the RuntimeEnv of this instance
	 */
	protected RuntimeEnv getRuntimeEnv() {
		return this.runEnv;
	}

	/**
	 * Log parameter <i>str</i> at the Trace level
	 * 
	 * @param str string to log
	 */
	protected void logTrace(String str) {
		this.getLogger().trace(str);
	}

	/**
	 * Log parameter <i>str</i> at the Debug level
	 * 
	 * @param str string to log
	 */
	protected void logDebug(String str) {
		this.getLogger().debug(str);
	}

	/**
	 * Log parameter <i>str</i> at the Info level
	 * 
	 * @param str string to log
	 */
	protected void logInfo(String str) {
		this.getLogger().info(str);
	}

	/**
	 * Log parameter <i>str</i> at the Warn level
	 * 
	 * @param str string to log
	 */
	protected void logWarn(String str) {
		this.getLogger().warn(str);
	}

	/**
	 * Log parameter <i>str</i> at the Error level
	 * 
	 * @param str string to log
	 */
	protected void logError(String str) {
		this.getLogger().error(str);
	}

	/**
	 * Log parameter <i>str</i> at the Fatal level
	 * 
	 * @param str string to log
	 */
	protected void logFatal(String str) {
		this.getLogger().fatal(str);
	}

	/**
	 * Returns the Logger instance for the class
	 * 
	 * @return the Logger instance for the class
	 */
	protected Logger getLogger() {
		return RuntimeObject.logger;
	}

	/**
	 * Prepares the DataFactory for the Netlist, NetlistNode and NetlistEdge of this
	 * stage
	 */
	protected abstract void prepareDataFactory();

	/**
	 * Executes the Algorithm for the stage.
	 * 
	 * @throws CelloException
	 */
	protected abstract void runAlgo() throws CelloException;

	/**
	 * Sets the StageNetlistData for the stage in parameter <i>netlist</i> <b>Note:
	 * this method will be deprecated in the future.</b>
	 * 
	 * @param netlist the <i>netlist</i> of this instance
	 */
	protected abstract void setStageNetlistData(Netlist netlist);

	/**
	 * Sets the StageNetlistNodeData for the stage in parameter <i>node</i> <b>Note:
	 * this method will be deprecated in the future.</b>
	 * 
	 * @param node a node within the <i>netlist</i> of this instance
	 */
	protected abstract void setStageNetlistNodeData(NetlistNode node);

	/**
	 * Sets the StageNetlistEdgeData for the stage in parameter <i>edge</i> <b>Note:
	 * method this will be deprecated in the future.</b>
	 * 
	 * @param edge an edge within the <i>netlist</i> of this instance
	 */
	protected abstract void setStageNetlistEdgeData(NetlistEdge edge);

	/**
	 * Sets the NetlistData of the appropriate algorithm in parameter <i>netlist</i>
	 * 
	 * @param netlist the <i>netlist</i> of this instance
	 */
	protected abstract void setNetlistData(Netlist netlist);

	/**
	 * Sets the NetlistNodeData of the appropriate algorithm in parameter
	 * <i>node</i>
	 * 
	 * @param node a node within the <i>netlist</i> of this instance
	 */
	protected abstract void setNetlistNodeData(NetlistNode node);

	/**
	 * Sets the NetlistEdgeData of the appropriate algorithm in parameter
	 * <i>edge</i>
	 * 
	 * @param edge an edge within the <i>netlist</i> of this instance
	 */
	protected abstract void setNetlistEdgeData(NetlistEdge edge);

	/**
	 * Returns a string representing the OPTIONS command line argument for the stage
	 * 
	 * @return a string representing the OPTIONS command line argument for the stage
	 */
	protected abstract String getOptionsString();

	/**
	 * Returns an AlgorithmProfile of the stage
	 * 
	 * @return an AlgorithmProfile if a valid algorithm exists, otherwise null
	 */
	// Prepare AlgorithmProfile
	protected AlgorithmProfile prepareAProfile() {
		AlgorithmProfile rtn = null;
		if (!this.getStage().getAlgorithmName().isEmpty()) {
			String path = "";
			path += "algorithms";
			path += "/";
			path += this.getStage().getAlgorithmName();
			path += "/";
			path += this.getStage().getAlgorithmName();
			path += ".json";
			Options options = OptionsUtils.getOptions(getRuntimeEnv(), this.getOptionsString());
			rtn = AlgorithmProfileUtils.getAlgorithmProfile(path);
			rtn.setStageName(this.getStage().getName());
			AlgorithmProfileUtils.OverrideWithOptions(rtn, options);
		}
		return rtn;
	}

	/**
	 * Initialize temporary data for the Stage's Netlist, NetlistNode and
	 * NetlistEdge
	 */
	// Prepare Stage Temporary data in Netlist
	protected void prepareStageNetlistData() {
		Netlist netlist = this.getNetlist();
		this.setStageNetlistData(netlist);
		for (int i = 0; i < netlist.getNumVertex(); i++) {
			NetlistNode node = netlist.getVertexAtIdx(i);
			this.setStageNetlistNodeData(node);
		}
		for (int i = 0; i < netlist.getNumEdge(); i++) {
			NetlistEdge edge = netlist.getEdgeAtIdx(i);
			this.setStageNetlistEdgeData(edge);
		}
	}

	/**
	 * Initialize temporary data for the Algorithm's Netlist, NetlistNode and
	 * NetlistEdge
	 */
	// Prepare Temporary data in Netlist
	protected void prepareNetlistData() {
		Netlist netlist = this.getNetlist();
		this.setNetlistData(netlist);
		for (int i = 0; i < netlist.getNumVertex(); i++) {
			NetlistNode node = netlist.getVertexAtIdx(i);
			this.setNetlistNodeData(node);
		}
		for (int i = 0; i < netlist.getNumEdge(); i++) {
			NetlistEdge edge = netlist.getEdgeAtIdx(i);
			this.setNetlistEdgeData(edge);
		}
	}

	/*
	 * private void setStage(final Stage stage) { this.stage = stage; } private void
	 * setTargetData(final TargetData targetData) { this.targetData = targetData; }
	 * private void setNetlist(final Netlist netlist) { this.netlist = netlist; }
	 * private void setRuntimeEnv(final RuntimeEnv runEnv) { this.runEnv = runEnv; }
	 */

	/**
	 * Perform postprocessing. Executes the following methods in sequential
	 * order:<br>
	 * {@link #prepareDataFactory()}<br>
	 * {@link #prepareStageNetlistData()}<br>
	 * {@link #setAlgorithmProfile(AlgorithmProfile)}<br>
	 * {@link #prepareNetlistData()}<br>
	 */
	protected void preprocessing() {
		// Data factory
		this.prepareDataFactory();
		// Netlist
		this.prepareStageNetlistData();
		// AlgorithmProfile
		this.setAlgorithmProfile(this.prepareAProfile());
		// Netlist
		this.prepareNetlistData();
	}

	/**
	 * Executes Algorithm <i>algo</i>
	 * 
	 * @param algo the Algorithm to execute
	 * @throws CelloException
	 * @throws RuntimeException if <i>algo</i> is invalid
	 */
	protected void executeAlgo(Algorithm algo) throws CelloException {
		if ((algo == null) && (!this.getStage().getAlgorithmName().isEmpty())) {
			throw new RuntimeException("Algorithm not found!");
		} else if (algo != null) {
			this.getLogger().info("Executing Algorithm: " + algo.getName());
			algo.execute(this.getNetlist(), this.getTargetData(), this.getNetlistConstraint(), AProfile,
					this.getRuntimeEnv());
		} else {
			this.getLogger().info("No Algorithm Executing!");
		}
	}

	/**
	 * Perform postprocessing
	 */
	protected void postprocessing() {
	}

	/**
	 * Executes the RuntimeObject. Executes the following methods in sequential
	 * order:<br>
	 * {@link #preprocessing()}<br>
	 * {@link #runAlgo()}<br>
	 * {@link #postprocessing()}<br>
	 * 
	 * @throws CelloException
	 */
	public void execute() throws CelloException {
		if (this.getStage() != null) {
			this.getLogger().info("Executing Stage: " + this.getName());
			this.preprocessing();
			this.runAlgo();
			this.postprocessing();
		}
	}

	private AlgorithmProfile AProfile;
	private final Stage stage;
	private final TargetData targetData;
	private final Netlist netlist;
	private final NetlistConstraint netlistConstraint;
	private final RuntimeEnv runEnv;
	private static final Logger logger = LogManager.getLogger(RuntimeObject.class);
}

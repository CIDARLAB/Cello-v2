/*
 * Copyright (C) 2017-2020 Massachusetts Institute of Technology (MIT), Boston University (BU)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package org.cellocad.v2.common.runtime;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cellocad.v2.common.CObject;
import org.cellocad.v2.common.CelloException;
import org.cellocad.v2.common.Utils;
import org.cellocad.v2.common.algorithm.Algorithm;
import org.cellocad.v2.common.netlistConstraint.data.NetlistConstraint;
import org.cellocad.v2.common.options.Options;
import org.cellocad.v2.common.options.OptionsUtils;
import org.cellocad.v2.common.profile.AlgorithmProfile;
import org.cellocad.v2.common.profile.AlgorithmProfileUtils;
import org.cellocad.v2.common.runtime.environment.RuntimeEnv;
import org.cellocad.v2.common.stage.Stage;
import org.cellocad.v2.common.target.data.TargetData;
import org.cellocad.v2.results.common.Results;
import org.cellocad.v2.results.netlist.Netlist;
import org.cellocad.v2.results.netlist.NetlistEdge;
import org.cellocad.v2.results.netlist.NetlistNode;

/**
 * The RuntimeObject class is a base class for all stage RuntimeObject classes within the Poros
 * framework.
 *
 * @author Vincent Mirian
 * @author Timothy Jones
 *
 * @date Nov 17, 2017
 */
// Object that aggregates the netlist, the stage configuration, target data and RuntimeEnv
public abstract class RuntimeObject extends CObject {

  /**
   * Initializes a newly created {@link RuntimeObject} with its <i>stage</i> set to parameter
   * {@code stage}, its <i>targetData</i> set to parameter {@code targetData}, its <i>netlist</i>
   * set to parameter {@code netlist}, its <i>results</i> set to parameter {@code results}, and, its
   * <i>runEnv</i> set to parameter {@code runEnv}.
   *
   * @param stage             The {@link Stage} used during execution.
   * @param targetData        The {@link TargetData} used during execution.
   * @param netlistConstraint The {@link NetlistConstraint} used during execution.
   * @param netlist           The {@link Netlist} used during execution.
   * @param results           The {@link Results} used during execution.
   * @param runEnv            The {@link RuntimeEnv} used during execution.
   * @throws RuntimeException if any of the parameters are null.
   */
  public RuntimeObject(final Stage stage, final TargetData targetData,
      final NetlistConstraint netlistConstraint, final Netlist netlist, final Results results,
      final RuntimeEnv runEnv) {
    super();
    Utils.isNullRuntimeException(stage, "stage");
    Utils.isNullRuntimeException(targetData, "targetData");
    Utils.isNullRuntimeException(netlistConstraint, "netlistConstraint");
    Utils.isNullRuntimeException(netlist, "netlist");
    Utils.isNullRuntimeException(results, "results");
    Utils.isNullRuntimeException(runEnv, "runEnv");
    this.stage = stage;
    this.targetData = targetData;
    this.netlistConstraint = netlistConstraint;
    this.netlist = netlist;
    this.results = results;
    this.runEnv = runEnv;
    setName(stage.getName());
  }

  /**
   * Getter for {@code algProfile}.
   *
   * @return The {@link AlgorithmProfile} of this instance.
   */
  protected AlgorithmProfile getAlgorithmProfile() {
    return algProfile;
  }

  private void setAlgorithmProfile(final AlgorithmProfile algProfile) {
    this.algProfile = algProfile;
  }

  /**
   * Getter for {@code stage}.
   *
   * @return The {@link Stage} of this instance.
   */
  protected Stage getStage() {
    return stage;
  }

  /**
   * Getter for {@code targetData}.
   *
   * @return The {@link TargetData} of this instance.
   */
  protected TargetData getTargetData() {
    return targetData;
  }

  /**
   * Getter for {@code netlistConstraint}.
   *
   * @return The {@link NetlistConstraint} of this instance.
   */
  protected NetlistConstraint getNetlistConstraint() {
    return netlistConstraint;
  }

  /**
   * Getter for {@code netlist}.
   *
   * @return The {@link Netlist} of this instance.
   */
  protected Netlist getNetlist() {
    return netlist;
  }

  /**
   * Getter for {@code results}.
   *
   * @return The value of {@code results}.
   */
  protected Results getResults() {
    return results;
  }

  /**
   * Getter for {@code runtimeEnv}.
   *
   * @return The RuntimeEnv of this instance.
   */
  protected RuntimeEnv getRuntimeEnv() {
    return runEnv;
  }

  /**
   * Log parameter {@code str} at the Trace level.
   *
   * @param str string to log.
   */
  protected void logTrace(final String str) {
    getLogger().trace(str);
  }

  /**
   * Log parameter {@code str} at the Debug level.
   *
   * @param str string to log.
   */
  protected void logDebug(final String str) {
    getLogger().debug(str);
  }

  /**
   * Log parameter {@code str} at the Info level.
   *
   * @param str string to log.
   */
  protected void logInfo(final String str) {
    getLogger().info(str);
  }

  /**
   * Log parameter {@code str} at the Warn level.
   *
   * @param str string to log.
   */
  protected void logWarn(final String str) {
    getLogger().warn(str);
  }

  /**
   * Log parameter {@code str} at the Error level.
   *
   * @param str string to log.
   */
  protected void logError(final String str) {
    getLogger().error(str);
  }

  /**
   * Log parameter {@code str} at the Fatal level.
   *
   * @param str string to log.
   */
  protected void logFatal(final String str) {
    getLogger().fatal(str);
  }

  /**
   * Returns the Logger instance for the class.
   *
   * @return The Logger instance for the class.
   */
  protected Logger getLogger() {
    return RuntimeObject.logger;
  }

  /**
   * Prepares the DataFactory for the Netlist, NetlistNode and NetlistEdge of this stage.
   */
  protected abstract void prepareDataFactory();

  /**
   * Executes the Algorithm for the stage.
   *
   * @throws CelloException Unable to execute the algorithm.
   */
  protected abstract void runAlgo() throws CelloException;

  /**
   * Sets the StageNetlistData for the stage in parameter {@code netlist} <b>Note: this method will
   * be deprecated in the future.</b>.
   *
   * @param netlist The netlist of this instance.
   */
  protected abstract void setStageNetlistData(Netlist netlist);

  /**
   * Sets the StageNetlistNodeData for the stage in parameter {@code node} <b>Note: this method will
   * be deprecated in the future.</b>.
   *
   * @param node A node within the netlist of this instance.
   */
  protected abstract void setStageNetlistNodeData(NetlistNode node);

  /**
   * Sets the StageNetlistEdgeData for the stage in parameter {@code edge} <b>Note: method this will
   * be deprecated in the future.</b>.
   *
   * @param edge An edge within the netlist of this instance.
   */
  protected abstract void setStageNetlistEdgeData(NetlistEdge edge);

  /**
   * Sets the NetlistData of the appropriate algorithm in parameter {@code netlist}.
   *
   * @param netlist The <i>netlist</i> of this instance.
   */
  protected abstract void setNetlistData(Netlist netlist);

  /**
   * Sets the NetlistNodeData of the appropriate algorithm in parameter {@code node}.
   *
   * @param node A node within the netlist of this instance.
   */
  protected abstract void setNetlistNodeData(NetlistNode node);

  /**
   * Sets the NetlistEdgeData of the appropriate algorithm in parameter {@code edge}.
   *
   * @param edge An edge within the netlist of this instance.
   */
  protected abstract void setNetlistEdgeData(NetlistEdge edge);

  /**
   * Returns a string representing the OPTIONS command line argument for the stage.
   *
   * @return A string representing the OPTIONS command line argument for the stage.
   */
  protected abstract String getOptionsString();

  /**
   * Returns an AlgorithmProfile of the stage.
   *
   * @return An AlgorithmProfile if a valid algorithm exists, otherwise null.
   */
  // Prepare AlgorithmProfile
  protected AlgorithmProfile preparealgProfile() {
    AlgorithmProfile rtn = null;
    if (!getStage().getAlgorithmName().isEmpty()) {
      String path = "";
      path += "algorithms";
      path += Utils.getFileSeparator();
      path += getStage().getName();
      path += Utils.getFileSeparator();
      path += getStage().getAlgorithmName();
      path += Utils.getFileSeparator();
      path += getStage().getAlgorithmName();
      path += ".json";
      final Options options = OptionsUtils.getOptions(getRuntimeEnv(), getOptionsString());
      rtn = AlgorithmProfileUtils.getAlgorithmProfile(path);
      rtn.setStageName(getStage().getName());
      AlgorithmProfileUtils.overrideWithOptions(rtn, options);
    }
    return rtn;
  }

  /**
   * Initialize temporary data for the Stage's Netlist, NetlistNode and NetlistEdge.
   */
  // Prepare Stage Temporary data in Netlist
  protected void prepareStageNetlistData() {
    final Netlist netlist = getNetlist();
    setStageNetlistData(netlist);
    for (int i = 0; i < netlist.getNumVertex(); i++) {
      final NetlistNode node = netlist.getVertexAtIdx(i);
      setStageNetlistNodeData(node);
    }
    for (int i = 0; i < netlist.getNumEdge(); i++) {
      final NetlistEdge edge = netlist.getEdgeAtIdx(i);
      setStageNetlistEdgeData(edge);
    }
  }

  /**
   * Initialize temporary data for the Algorithm's Netlist, NetlistNode and NetlistEdge.
   */
  // Prepare Temporary data in Netlist
  protected void prepareNetlistData() {
    final Netlist netlist = getNetlist();
    setNetlistData(netlist);
    for (int i = 0; i < netlist.getNumVertex(); i++) {
      final NetlistNode node = netlist.getVertexAtIdx(i);
      setNetlistNodeData(node);
    }
    for (int i = 0; i < netlist.getNumEdge(); i++) {
      final NetlistEdge edge = netlist.getEdgeAtIdx(i);
      setNetlistEdgeData(edge);
    }
  }

  /*
   * private void setStage(final Stage stage) { this.stage = stage; } private void
   * setTargetData(final TargetData targetData) { this.targetData = targetData; } private void
   * setNetlist(final Netlist netlist) { this.netlist = netlist; } private void setRuntimeEnv(final
   * RuntimeEnv runEnv) { this.runEnv = runEnv; }
   */

  /**
   * Perform postprocessing. Executes the following methods in sequential order:<br>
   * {@link #prepareDataFactory()}<br>
   * {@link #prepareStageNetlistData()}<br>
   * {@link #setAlgorithmProfile(AlgorithmProfile)}<br>
   * {@link #prepareNetlistData()}<br>
   * .
   */
  protected void preprocessing() {
    // Data factory
    prepareDataFactory();
    // Netlist
    prepareStageNetlistData();
    // AlgorithmProfile
    setAlgorithmProfile(preparealgProfile());
    // Netlist
    prepareNetlistData();
  }

  /**
   * Executes algorithm {@code algo}.
   *
   * @param algo The Algorithm to execute.
   * @throws CelloException   Unable to execute the algorithm.
   * @throws RuntimeException If {@code algo} is invalid.
   */
  protected void executeAlgo(final Algorithm algo) throws CelloException {
    if (algo == null && !getStage().getAlgorithmName().isEmpty()) {
      throw new RuntimeException("Algorithm not found!");
    } else if (algo != null) {
      getLogger().info("Executing Algorithm: " + algo.getName());
      algo.execute(getNetlist(), getTargetData(), getNetlistConstraint(), getResults(), algProfile,
          getRuntimeEnv());
    } else {
      getLogger().info("No Algorithm Executing!");
    }
  }

  /**
   * Perform postprocessing.
   */
  protected void postprocessing() {
  }

  /**
   * Executes the RuntimeObject. Executes the following methods in sequential order:<br>
   * {@link #preprocessing()}<br>
   * {@link #runAlgo()}<br>
   * {@link #postprocessing()}<br>
   * .
   *
   * @throws CelloException Unable to execute the runtime object.
   */
  public void execute() throws CelloException {
    if (getStage() != null) {
      getLogger().info("Executing Stage: " + getName());
      preprocessing();
      runAlgo();
      postprocessing();
    }
  }

  private AlgorithmProfile algProfile;
  private final Stage stage;
  private final TargetData targetData;
  private final Netlist netlist;
  private final NetlistConstraint netlistConstraint;
  private final Results results;
  private final RuntimeEnv runEnv;
  private static final Logger logger = LogManager.getLogger(RuntimeObject.class);

}

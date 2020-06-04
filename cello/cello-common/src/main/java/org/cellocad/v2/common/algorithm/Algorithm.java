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

package org.cellocad.v2.common.algorithm;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cellocad.v2.common.CObject;
import org.cellocad.v2.common.Utils;
import org.cellocad.v2.common.exception.CelloException;
import org.cellocad.v2.common.netlistConstraint.data.NetlistConstraint;
import org.cellocad.v2.common.profile.AlgorithmProfile;
import org.cellocad.v2.common.runtime.environment.RuntimeEnv;
import org.cellocad.v2.common.target.data.TargetData;
import org.cellocad.v2.results.common.Results;
import org.cellocad.v2.results.netlist.Netlist;

/**
 * Algorithm class is the base class for all algorithms using the Poros framework.
 *
 * @author Vincent Mirian
 * @author Timothy Jones
 * @date Nov 17, 2017
 */
public abstract class Algorithm extends CObject {

  /**
   * Executes the algorithm. Executes the following methods in sequential order:<br>
   * {@link #getConstraintFromNetlistConstraintFile()}<br>
   * {@link #getDataFromUcf()}<br>
   * {@link #setParameterValues()}<br>
   * {@link #validateParameterValues()}<br>
   * {@link #preprocessing()}<br>
   * {@link #run()}<br>
   * {@link #postprocessing()}<br>
   * .
   *
   * @param netlist The {@link Netlist} used during execution.
   * @param targetData The {@link TargetData} used during execution.
   * @param netlistConstraint The {@link NetlistConstraint} used during execution.
   * @param results The {@link Results} used during execution.
   * @param algProfile The {@link AlgorithmProfile} used during execution.
   * @param runtimeEnv The {@link RuntimeEnv} used during execution.
   * @throws CelloException Unable to execute algorithm.
   */
  public void execute(
      final Netlist netlist,
      final TargetData targetData,
      final NetlistConstraint netlistConstraint,
      final Results results,
      final AlgorithmProfile algProfile,
      final RuntimeEnv runtimeEnv)
      throws CelloException {
    Utils.isNullRuntimeException(netlist, "netlist");
    Utils.isNullRuntimeException(targetData, "targetData");
    Utils.isNullRuntimeException(netlistConstraint, "netlistConstraint");
    Utils.isNullRuntimeException(results, "results");
    Utils.isNullRuntimeException(algProfile, "algProfile");
    Utils.isNullRuntimeException(runtimeEnv, "runtimeEnv");
    // init
    setNetlist(netlist);
    setTargetData(targetData);
    setNetlistConstraint(netlistConstraint);
    setResults(results);
    setAlgorithmProfile(algProfile);
    setRuntimeEnv(runtimeEnv);
    // execute
    getConstraintFromNetlistConstraintFile();
    getDataFromUcf();
    setParameterValues();
    validateParameterValues();
    preprocessing();
    run();
    postprocessing();
  }

  /*
   * Getter and Setter
   */

  /**
   * Setter for {@code netlist}.
   *
   * @param netlist The value to set {@link netlist}.
   */
  private void setNetlist(final Netlist netlist) {
    this.netlist = netlist;
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
   * Setter for {@code targetData}.
   *
   * @param targetData The value to set {@link targetData}.
   */
  private void setTargetData(final TargetData targetData) {
    this.targetData = targetData;
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
   * Setter for {@code netlistConstraint}.
   *
   * @param netlistConstraint The value to set {@link netlistConstraint}.
   */
  private void setNetlistConstraint(final NetlistConstraint netlistConstraint) {
    this.netlistConstraint = netlistConstraint;
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
   * Getter for {@code results}.
   *
   * @return The value of {@code results}.
   */
  protected Results getResults() {
    return results;
  }

  /**
   * Setter for {@code results}.
   *
   * @param results The value to set {@code results}.
   */
  private void setResults(final Results results) {
    this.results = results;
  }

  /**
   * Setter for {@code algProfile}.
   *
   * @param algProfile The value to set {@link algProfile}.
   */
  private void setAlgorithmProfile(final AlgorithmProfile algProfile) {
    this.algProfile = algProfile;
  }

  /**
   * Getter for {@code algProfile}.
   *
   * @return The {@link AlgorithmProfile} of this instance.
   */
  protected AlgorithmProfile getAlgorithmProfile() {
    return algProfile;
  }

  /**
   * Setter for {@code runtimeEnv}.
   *
   * @param runtimeEnv The value to set {@link runtimeEnv}.
   */
  private void setRuntimeEnv(final RuntimeEnv runtimeEnv) {
    this.runtimeEnv = runtimeEnv;
  }

  /**
   * Getter for {@code runtimeEnv}.
   *
   * @return The {@link RuntimeEnv} of this instance.
   */
  protected RuntimeEnv getRuntimeEnv() {
    return runtimeEnv;
  }

  private Netlist netlist;
  private TargetData targetData;
  private NetlistConstraint netlistConstraint;
  private Results results;
  private AlgorithmProfile algProfile;
  private RuntimeEnv runtimeEnv;

  /** Gets the constraint data from the netlist constraint file. */
  protected abstract void getConstraintFromNetlistConstraintFile();

  /**
   * Gets the data from the UCF.
   *
   * @throws CelloException Unable to get data from UCF.
   */
  protected abstract void getDataFromUcf() throws CelloException;

  /** Set parameter values of the algorithm. */
  protected abstract void setParameterValues();

  /** Validate parameter values of the algorithm. */
  protected abstract void validateParameterValues();

  /**
   * Perform preprocessing.
   *
   * @throws CelloException Unable to perform preprocessing.
   */
  protected abstract void preprocessing() throws CelloException;

  /**
   * Run the (core) algorithm.
   *
   * @throws CelloException Unable to run the (core) algorithm.
   */
  protected abstract void run() throws CelloException;

  /**
   * Perform postprocessing.
   *
   * @throws CelloException Unable to perform postprocessing.
   */
  protected abstract void postprocessing() throws CelloException;

  /**
   * Log parameter {@code str} at the Trace level.
   *
   * @param str The {@link String} to log.
   */
  protected void logTrace(final String str) {
    getLogger().trace(str);
  }

  /**
   * Log parameter {@code str} at the Debug level.
   *
   * @param str The {@link String} to log.
   */
  protected void logDebug(final String str) {
    getLogger().debug(str);
  }

  /**
   * Log parameter {@code str} at the Info level.
   *
   * @param str The {@link String} to log.
   */
  protected void logInfo(final String str) {
    getLogger().info(str);
  }

  /**
   * Log parameter {@code str} at the Warn level.
   *
   * @param str The {@link String} to log.
   */
  protected void logWarn(final String str) {
    getLogger().warn(str);
  }

  /**
   * Log parameter {@code str} at the Error level.
   *
   * @param str The {@link String} to log.
   */
  protected void logError(final String str) {
    getLogger().error(str);
  }

  /**
   * Log parameter {@code str} at the Fatal level.
   *
   * @param str The {@link String} to log.
   */
  protected void logFatal(final String str) {
    getLogger().fatal(str);
  }

  /**
   * Returns the {@link Logger}.
   *
   * @return The {@link Logger}.
   */
  protected Logger getLogger() {
    return Algorithm.logger;
  }

  private static final Logger logger = LogManager.getLogger(Algorithm.class);
}

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

package org.cellocad.v2.placing.runtime;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cellocad.v2.common.CelloException;
import org.cellocad.v2.common.netlistConstraint.data.NetlistConstraint;
import org.cellocad.v2.common.runtime.RuntimeObject;
import org.cellocad.v2.common.runtime.environment.ArgString;
import org.cellocad.v2.common.runtime.environment.RuntimeEnv;
import org.cellocad.v2.common.stage.Stage;
import org.cellocad.v2.common.target.data.TargetData;
import org.cellocad.v2.placing.algorithm.PLAlgorithm;
import org.cellocad.v2.placing.algorithm.PLAlgorithmFactory;
import org.cellocad.v2.placing.algorithm.data.PLNetlistData;
import org.cellocad.v2.placing.algorithm.data.PLNetlistDataFactory;
import org.cellocad.v2.placing.algorithm.data.PLNetlistEdgeData;
import org.cellocad.v2.placing.algorithm.data.PLNetlistEdgeDataFactory;
import org.cellocad.v2.placing.algorithm.data.PLNetlistNodeData;
import org.cellocad.v2.placing.algorithm.data.PLNetlistNodeDataFactory;
import org.cellocad.v2.placing.netlist.data.PLStageNetlistData;
import org.cellocad.v2.placing.netlist.data.PLStageNetlistEdgeData;
import org.cellocad.v2.placing.netlist.data.PLStageNetlistNodeData;
import org.cellocad.v2.results.common.Results;
import org.cellocad.v2.results.netlist.Netlist;
import org.cellocad.v2.results.netlist.NetlistEdge;
import org.cellocad.v2.results.netlist.NetlistNode;

/**
 * The PLRuntimeObject class is the RuntimeObject class for the <i>placing</i> stage.
 *
 * @author Vincent Mirian
 * @author Timothy Jones
 *
 * @date 2018-05-21
 */
public class PLRuntimeObject extends RuntimeObject {

  /**
   * Initializes a newly created {@link PLRuntimeObject} with its <i>stage</i> set to parameter
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
  public PLRuntimeObject(final Stage stage, final TargetData targetData,
      final NetlistConstraint netlistConstraint, final Netlist netlist, final Results results,
      final RuntimeEnv runEnv) {
    super(stage, targetData, netlistConstraint, netlist, results, runEnv);
  }

  /**
   * Prepares the DataFactory for the Netlist, NetlistNode and NetlistEdge of the placing stage.
   */
  @Override
  protected void prepareDataFactory() {
    setNetlistDataFactory(new PLNetlistDataFactory());
    setNetlistNodeDataFactory(new PLNetlistNodeDataFactory());
    setNetlistEdgeDataFactory(new PLNetlistEdgeDataFactory());
  }

  /**
   * Sets the PLStageNetlistData for the placing stage in parameter {@code netlist} <b>Note: this
   * method will be deprecated in the future.</b>.
   *
   * @param netlist The <i>netlist</i> of this instance.
   */
  @Override
  protected void setStageNetlistData(final Netlist netlist) {
    netlist.setStageNetlistData(new PLStageNetlistData());
  }

  /**
   * Sets the PLStageNetlistNodeData for the placing stage in parameter {@code node} <b>Note: this
   * method will be deprecated in the future.</b>.
   *
   * @param node A node within the netlist of this instance.
   */
  @Override
  protected void setStageNetlistNodeData(final NetlistNode node) {
    node.setStageNetlistNodeData(new PLStageNetlistNodeData());
  }

  /**
   * Sets the PLStageNetlistEdgeData for the placing stage in parameter {@code edge} <b>Note: method
   * this will be deprecated in the future.</b>.
   *
   * @param edge An edge within the netlist of this instance.
   */
  @Override
  protected void setStageNetlistEdgeData(final NetlistEdge edge) {
    edge.setStageNetlistEdgeData(new PLStageNetlistEdgeData());
  }

  /**
   * Sets the NetlistData of the appropriate algorithm in parameter {@code netlist}.
   *
   * @param netlist The <i>netlist</i> of this instance.
   */
  @Override
  protected void setNetlistData(final Netlist netlist) {
    final PLNetlistData data = getNetlistDataFactory().getNetlistData(getAlgorithmProfile());
    netlist.setNetlistData(data);
  }

  /**
   * Sets the NetlistNodeData of the appropriate algorithm in parameter {@code node}.
   *
   * @param node A node within the netlist of this instance.
   */
  @Override
  protected void setNetlistNodeData(final NetlistNode node) {
    final PLNetlistNodeData data =
        getNetlistNodeDataFactory().getNetlistNodeData(getAlgorithmProfile());
    node.setNetlistNodeData(data);
  }

  /**
   * Sets the NetlistEdgeData of the appropriate algorithm in parameter {@code edge}.
   *
   * @param edge An edge within the netlist of this instance.
   */
  @Override
  protected void setNetlistEdgeData(final NetlistEdge edge) {
    final PLNetlistEdgeData data =
        getNetlistEdgeDataFactory().getNetlistEdgeData(getAlgorithmProfile());
    edge.setNetlistEdgeData(data);
  }

  /**
   * Returns a string representing the {@code OPTIONS} command line argument for the <i>placing</i>
   * stage.
   *
   * @return A string representing the {@code OPTIONS} command line argument for the <i>placing</i>
   *         stage.
   */
  @Override
  protected String getOptionsString() {
    return ArgString.OPTIONS;
  }

  /**
   * Executes the algorithm of the <i>placing</i> stage.
   *
   * @throws CelloException Unable to execute algorithm.
   */
  @Override
  protected void runAlgo() throws CelloException {
    // get Algorithm from Factory
    final PLAlgorithmFactory AF = new PLAlgorithmFactory();
    final PLAlgorithm algo = AF.getAlgorithm(getAlgorithmProfile());
    // executeAlgo
    executeAlgo(algo);
  }

  /**
   * Getter for the {@link PLNetlistDataFactorFactory}.
   *
   * @return The {@link PLNetlistDataFactory}.
   */
  protected PLNetlistDataFactory getNetlistDataFactory() {
    return netlistDataFactory;
  }

  /**
   * Setter for the {@link PLNetlistDataFactorFactory}.
   *
   * @param netlistDataFactory The {@link PLNetlistDataFactory}.
   */
  private void setNetlistDataFactory(final PLNetlistDataFactory netlistDataFactory) {
    this.netlistDataFactory = netlistDataFactory;
  }

  /**
   * Getter for the {@link PLNetlistNodeDataFactorFactory}.
   *
   * @return The {@link PLNetlistNodeDataFactory}.
   */
  protected PLNetlistNodeDataFactory getNetlistNodeDataFactory() {
    return netlistNodeDataFactory;
  }

  /**
   * Setter for the {@link PLNetlistNodeDataFactorFactory}.
   *
   * @param netlistNodeDataFactory The {@link PLNetlistNodeDataFactory}.
   */
  private void setNetlistNodeDataFactory(final PLNetlistNodeDataFactory netlistNodeDataFactory) {
    this.netlistNodeDataFactory = netlistNodeDataFactory;
  }

  /**
   * Getter for the {@link PLNetlistEdgeDataFactorFactory}.
   *
   * @return The {@link PLNetlistEdgeDataFactory}.
   */
  protected PLNetlistEdgeDataFactory getNetlistEdgeDataFactory() {
    return netlistEdgeDataFactory;
  }

  /**
   * Setter for the {@link PLNetlistEdgeDataFactorFactory}.
   *
   * @param netlistEdgeDataFactory The {@link PLNetlistEdgeDataFactory}.
   */
  private void setNetlistEdgeDataFactory(final PLNetlistEdgeDataFactory netlistEdgeDataFactory) {
    this.netlistEdgeDataFactory = netlistEdgeDataFactory;
  }

  private PLNetlistDataFactory netlistDataFactory;
  private PLNetlistEdgeDataFactory netlistEdgeDataFactory;
  private PLNetlistNodeDataFactory netlistNodeDataFactory;

  /**
   * Returns the {@link Logger} instance for the <i>placing</i> stage.
   *
   * @return The {@link Logger} for the <i>placing</i> stage.
   */
  @Override
  protected Logger getLogger() {
    return PLRuntimeObject.logger;
  }

  private static final Logger logger = LogManager.getLogger(PLRuntimeObject.class);

}

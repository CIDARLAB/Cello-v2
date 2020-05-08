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

package org.cellocad.v2.logicSynthesis.runtime;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cellocad.v2.common.CelloException;
import org.cellocad.v2.common.netlistConstraint.data.NetlistConstraint;
import org.cellocad.v2.common.runtime.RuntimeObject;
import org.cellocad.v2.common.runtime.environment.ArgString;
import org.cellocad.v2.common.runtime.environment.RuntimeEnv;
import org.cellocad.v2.common.stage.Stage;
import org.cellocad.v2.common.target.data.TargetData;
import org.cellocad.v2.logicSynthesis.algorithm.LSAlgorithm;
import org.cellocad.v2.logicSynthesis.algorithm.LSAlgorithmFactory;
import org.cellocad.v2.logicSynthesis.algorithm.data.LSNetlistData;
import org.cellocad.v2.logicSynthesis.algorithm.data.LSNetlistDataFactory;
import org.cellocad.v2.logicSynthesis.algorithm.data.LSNetlistEdgeData;
import org.cellocad.v2.logicSynthesis.algorithm.data.LSNetlistEdgeDataFactory;
import org.cellocad.v2.logicSynthesis.algorithm.data.LSNetlistNodeData;
import org.cellocad.v2.logicSynthesis.algorithm.data.LSNetlistNodeDataFactory;
import org.cellocad.v2.logicSynthesis.netlist.data.LSStageNetlistData;
import org.cellocad.v2.logicSynthesis.netlist.data.LSStageNetlistEdgeData;
import org.cellocad.v2.logicSynthesis.netlist.data.LSStageNetlistNodeData;
import org.cellocad.v2.results.common.Results;
import org.cellocad.v2.results.netlist.Netlist;
import org.cellocad.v2.results.netlist.NetlistEdge;
import org.cellocad.v2.results.netlist.NetlistNode;

/**
 * The LSRuntimeObject class is the RuntimeObject class for the <i>logicSynthesis</i> stage.
 *
 * @author Vincent Mirian
 * @author Timothy Jones
 * @date 2018-05-21
 */
public class LSRuntimeObject extends RuntimeObject {

  /**
   * Initializes a newly created {@link LSRuntimeObject} with its <i>stage</i> set to parameter
   * {@code stage}, its <i>targetData</i> set to parameter {@code targetData}, its <i>netlist</i>
   * set to parameter {@code netlist}, its <i>results</i> set to parameter {@code results}, and, its
   * <i>runEnv</i> set to parameter {@code runEnv}.
   *
   * @param stage The {@link Stage} used during execution.
   * @param targetData The {@link TargetData} used during execution.
   * @param netlistConstraint The {@link NetlistConstraint} used during execution.
   * @param netlist The {@link Netlist} used during execution.
   * @param results The {@link Results} used during execution.
   * @param runEnv The {@link RuntimeEnv} used during execution.
   * @throws RuntimeException if any of the parameters are null.
   */
  public LSRuntimeObject(
      final Stage stage,
      final TargetData targetData,
      final NetlistConstraint netlistConstraint,
      final Netlist netlist,
      final Results results,
      final RuntimeEnv runEnv) {
    super(stage, targetData, netlistConstraint, netlist, results, runEnv);
  }

  /**
   * Prepares the DataFactory for the Netlist, NetlistNode and NetlistEdge of the logicSynthesis
   * stage.
   */
  @Override
  protected void prepareDataFactory() {
    setNetlistDataFactory(new LSNetlistDataFactory());
    setNetlistNodeDataFactory(new LSNetlistNodeDataFactory());
    setNetlistEdgeDataFactory(new LSNetlistEdgeDataFactory());
  }

  /**
   * Sets the LSStageNetlistData for the logicSynthesis stage in parameter {@code netlist} <b>Note:
   * this method will be deprecated in the future.</b>.
   *
   * @param netlist The <i>netlist</i> of this instance.
   */
  @Override
  protected void setStageNetlistData(final Netlist netlist) {
    netlist.setStageNetlistData(new LSStageNetlistData());
  }

  /**
   * Sets the LSStageNetlistNodeData for the logicSynthesis stage in parameter {@code node} <b>Note:
   * this method will be deprecated in the future.</b>.
   *
   * @param node A node within the netlist of this instance.
   */
  @Override
  protected void setStageNetlistNodeData(final NetlistNode node) {
    node.setStageNetlistNodeData(new LSStageNetlistNodeData());
  }

  /**
   * Sets the LSStageNetlistEdgeData for the logicSynthesis stage in parameter {@code edge} <b>Note:
   * method this will be deprecated in the future.</b>.
   *
   * @param edge An edge within the netlist of this instance.
   */
  @Override
  protected void setStageNetlistEdgeData(final NetlistEdge edge) {
    edge.setStageNetlistEdgeData(new LSStageNetlistEdgeData());
  }

  /**
   * Sets the NetlistData of the appropriate algorithm in parameter {@code netlist}.
   *
   * @param netlist The <i>netlist</i> of this instance.
   */
  @Override
  protected void setNetlistData(final Netlist netlist) {
    final LSNetlistData data = getNetlistDataFactory().getNetlistData(getAlgorithmProfile());
    netlist.setNetlistData(data);
  }

  /**
   * Sets the NetlistNodeData of the appropriate algorithm in parameter {@code node}.
   *
   * @param node A node within the netlist of this instance.
   */
  @Override
  protected void setNetlistNodeData(final NetlistNode node) {
    final LSNetlistNodeData data =
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
    final LSNetlistEdgeData data =
        getNetlistEdgeDataFactory().getNetlistEdgeData(getAlgorithmProfile());
    edge.setNetlistEdgeData(data);
  }

  /**
   * Returns a string representing the {@code OPTIONS} command line argument for the
   * <i>logicSynthesis</i> stage.
   *
   * @return A string representing the {@code OPTIONS} command line argument for the
   *     <i>logicSynthesis</i> stage.
   */
  @Override
  protected String getOptionsString() {
    return ArgString.OPTIONS;
  }

  /**
   * Executes the algorithm of the <i>logicSynthesis</i> stage.
   *
   * @throws CelloException Unable to execute the algorithm.
   */
  @Override
  protected void runAlgo() throws CelloException {
    // get Algorithm from Factory
    final LSAlgorithmFactory AF = new LSAlgorithmFactory();
    final LSAlgorithm algo = AF.getAlgorithm(getAlgorithmProfile());
    // executeAlgo
    executeAlgo(algo);
  }

  /**
   * Getter for the {@link LSNetlistDataFactorFactory}.
   *
   * @return The {@link LSNetlistDataFactory}.
   */
  protected LSNetlistDataFactory getNetlistDataFactory() {
    return netlistDataFactory;
  }

  /**
   * Setter for the {@link LSNetlistDataFactorFactory}.
   *
   * @param netlistDataFactory The {@link LSNetlistDataFactory}.
   */
  private void setNetlistDataFactory(final LSNetlistDataFactory netlistDataFactory) {
    this.netlistDataFactory = netlistDataFactory;
  }

  /**
   * Getter for the {@link LSNetlistNodeDataFactorFactory}.
   *
   * @return The {@link LSNetlistNodeDataFactory}.
   */
  protected LSNetlistNodeDataFactory getNetlistNodeDataFactory() {
    return netlistNodeDataFactory;
  }

  /**
   * Setter for the {@link LSNetlistNodeDataFactorFactory}.
   *
   * @param netlistNodeDataFactory The {@link LSNetlistNodeDataFactory}.
   */
  private void setNetlistNodeDataFactory(final LSNetlistNodeDataFactory netlistNodeDataFactory) {
    this.netlistNodeDataFactory = netlistNodeDataFactory;
  }

  /**
   * Getter for the {@link LSNetlistEdgeDataFactorFactory}.
   *
   * @return The {@link LSNetlistEdgeDataFactory}.
   */
  protected LSNetlistEdgeDataFactory getNetlistEdgeDataFactory() {
    return netlistEdgeDataFactory;
  }

  /**
   * Setter for the {@link LSNetlistEdgeDataFactorFactory}.
   *
   * @param netlistEdgeDataFactory The {@link LSNetlistEdgeDataFactory}.
   */
  private void setNetlistEdgeDataFactory(final LSNetlistEdgeDataFactory netlistEdgeDataFactory) {
    this.netlistEdgeDataFactory = netlistEdgeDataFactory;
  }

  private LSNetlistDataFactory netlistDataFactory;
  private LSNetlistEdgeDataFactory netlistEdgeDataFactory;
  private LSNetlistNodeDataFactory netlistNodeDataFactory;

  /**
   * Returns the {@link Logger} instance for the <i>logicSynthesis</i> stage.
   *
   * @return The {@link Logger} for the <i>logicSynthesis</i> stage.
   */
  @Override
  protected Logger getLogger() {
    return LSRuntimeObject.logger;
  }

  private static final Logger logger = LogManager.getLogger(LSRuntimeObject.class);
}

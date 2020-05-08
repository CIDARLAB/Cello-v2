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

package org.cellocad.v2.partitioning.runtime;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cellocad.v2.common.CelloException;
import org.cellocad.v2.common.netlistConstraint.data.NetlistConstraint;
import org.cellocad.v2.common.runtime.RuntimeObject;
import org.cellocad.v2.common.runtime.environment.ArgString;
import org.cellocad.v2.common.runtime.environment.RuntimeEnv;
import org.cellocad.v2.common.stage.Stage;
import org.cellocad.v2.common.target.data.TargetData;
import org.cellocad.v2.partitioning.algorithm.PTAlgorithm;
import org.cellocad.v2.partitioning.algorithm.PTAlgorithmFactory;
import org.cellocad.v2.partitioning.algorithm.data.PTNetlistData;
import org.cellocad.v2.partitioning.algorithm.data.PTNetlistDataFactory;
import org.cellocad.v2.partitioning.algorithm.data.PTNetlistEdgeData;
import org.cellocad.v2.partitioning.algorithm.data.PTNetlistEdgeDataFactory;
import org.cellocad.v2.partitioning.algorithm.data.PTNetlistNodeData;
import org.cellocad.v2.partitioning.algorithm.data.PTNetlistNodeDataFactory;
import org.cellocad.v2.partitioning.netlist.data.PTStageNetlistData;
import org.cellocad.v2.partitioning.netlist.data.PTStageNetlistEdgeData;
import org.cellocad.v2.partitioning.netlist.data.PTStageNetlistNodeData;
import org.cellocad.v2.results.common.Results;
import org.cellocad.v2.results.netlist.Netlist;
import org.cellocad.v2.results.netlist.NetlistEdge;
import org.cellocad.v2.results.netlist.NetlistNode;

/**
 * The PTRuntimeObject class is the RuntimeObject class for the <i>partitioning</i> stage.
 *
 * @author Vincent Mirian
 * @author Timothy Jones
 * @date 2018-05-21
 */
public class PTRuntimeObject extends RuntimeObject {

  /**
   * Initializes a newly created {@link PTRuntimeObject} with its <i>stage</i> set to parameter
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
  public PTRuntimeObject(
      final Stage stage,
      final TargetData targetData,
      final NetlistConstraint netlistConstraint,
      final Netlist netlist,
      final Results results,
      final RuntimeEnv runEnv) {
    super(stage, targetData, netlistConstraint, netlist, results, runEnv);
  }

  /**
   * Prepares the DataFactory for the Netlist, NetlistNode and NetlistEdge of the partitioning
   * stage.
   */
  @Override
  protected void prepareDataFactory() {
    setNetlistDataFactory(new PTNetlistDataFactory());
    setNetlistNodeDataFactory(new PTNetlistNodeDataFactory());
    setNetlistEdgeDataFactory(new PTNetlistEdgeDataFactory());
  }

  /**
   * Sets the PTStageNetlistData for the partitioning stage in parameter {@code netlist} <b>Note:
   * this method will be deprecated in the future.</b>.
   *
   * @param netlist The <i>netlist</i> of this instance.
   */
  @Override
  protected void setStageNetlistData(final Netlist netlist) {
    netlist.setStageNetlistData(new PTStageNetlistData());
  }

  /**
   * Sets the PTStageNetlistNodeData for the partitioning stage in parameter {@code node} <b>Note:
   * this method will be deprecated in the future.</b>.
   *
   * @param node A node within the netlist of this instance.
   */
  @Override
  protected void setStageNetlistNodeData(final NetlistNode node) {
    node.setStageNetlistNodeData(new PTStageNetlistNodeData());
  }

  /**
   * Sets the PTStageNetlistEdgeData for the partitioning stage in parameter {@code edge} <b>Note:
   * method this will be deprecated in the future.</b>.
   *
   * @param edge An edge within the netlist of this instance.
   */
  @Override
  protected void setStageNetlistEdgeData(final NetlistEdge edge) {
    edge.setStageNetlistEdgeData(new PTStageNetlistEdgeData());
  }

  /**
   * Sets the NetlistData of the appropriate algorithm in parameter {@code netlist}.
   *
   * @param netlist The <i>netlist</i> of this instance.
   */
  @Override
  protected void setNetlistData(final Netlist netlist) {
    final PTNetlistData data = getNetlistDataFactory().getNetlistData(getAlgorithmProfile());
    netlist.setNetlistData(data);
  }

  /**
   * Sets the NetlistNodeData of the appropriate algorithm in parameter {@code node}.
   *
   * @param node A node within the netlist of this instance.
   */
  @Override
  protected void setNetlistNodeData(final NetlistNode node) {
    final PTNetlistNodeData data =
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
    final PTNetlistEdgeData data =
        getNetlistEdgeDataFactory().getNetlistEdgeData(getAlgorithmProfile());
    edge.setNetlistEdgeData(data);
  }

  /**
   * Returns a string representing the {@code OPTIONS} command line argument for the
   * <i>partitioning</i> stage.
   *
   * @return A string representing the {@code OPTIONS} command line argument for the
   *     <i>partitioning</i> stage.
   */
  @Override
  protected String getOptionsString() {
    return ArgString.OPTIONS;
  }

  /**
   * Executes the algorithm of the <i>partitioning</i> stage.
   *
   * @throws CelloException Unable to execute algorithm.
   */
  @Override
  protected void runAlgo() throws CelloException {
    // get Algorithm from Factory
    final PTAlgorithmFactory AF = new PTAlgorithmFactory();
    final PTAlgorithm algo = AF.getAlgorithm(getAlgorithmProfile());
    // executeAlgo
    executeAlgo(algo);
  }

  /**
   * Getter for the {@link PTNetlistDataFactorFactory}.
   *
   * @return The {@link PTNetlistDataFactory}.
   */
  protected PTNetlistDataFactory getNetlistDataFactory() {
    return netlistDataFactory;
  }

  /**
   * Setter for the {@link PTNetlistDataFactorFactory}.
   *
   * @param netlistDataFactory The {@link PTNetlistDataFactory}.
   */
  private void setNetlistDataFactory(final PTNetlistDataFactory netlistDataFactory) {
    this.netlistDataFactory = netlistDataFactory;
  }

  /**
   * Getter for the {@link PTNetlistNodeDataFactorFactory}.
   *
   * @return The {@link PTNetlistNodeDataFactory}.
   */
  protected PTNetlistNodeDataFactory getNetlistNodeDataFactory() {
    return netlistNodeDataFactory;
  }

  /**
   * Setter for the {@link PTNetlistNodeDataFactorFactory}.
   *
   * @param netlistNodeDataFactory The {@link PTNetlistNodeDataFactory}.
   */
  private void setNetlistNodeDataFactory(final PTNetlistNodeDataFactory netlistNodeDataFactory) {
    this.netlistNodeDataFactory = netlistNodeDataFactory;
  }

  /**
   * Getter for the {@link PTNetlistEdgeDataFactorFactory}.
   *
   * @return The {@link PTNetlistEdgeDataFactory}.
   */
  protected PTNetlistEdgeDataFactory getNetlistEdgeDataFactory() {
    return netlistEdgeDataFactory;
  }

  /**
   * Setter for the {@link PTNetlistEdgeDataFactorFactory}.
   *
   * @param netlistEdgeDataFactory The {@link PTNetlistEdgeDataFactory}.
   */
  private void setNetlistEdgeDataFactory(final PTNetlistEdgeDataFactory netlistEdgeDataFactory) {
    this.netlistEdgeDataFactory = netlistEdgeDataFactory;
  }

  private PTNetlistDataFactory netlistDataFactory;
  private PTNetlistEdgeDataFactory netlistEdgeDataFactory;
  private PTNetlistNodeDataFactory netlistNodeDataFactory;

  /**
   * Returns the {@link Logger} instance for the <i>partitioning</i> stage.
   *
   * @return The {@link Logger} for the <i>partitioning</i> stage.
   */
  @Override
  protected Logger getLogger() {
    return PTRuntimeObject.logger;
  }

  private static final Logger logger = LogManager.getLogger(PTRuntimeObject.class);
}

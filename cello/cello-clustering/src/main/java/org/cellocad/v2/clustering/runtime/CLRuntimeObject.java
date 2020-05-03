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

package org.cellocad.v2.clustering.runtime;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cellocad.v2.clustering.algorithm.CLAlgorithm;
import org.cellocad.v2.clustering.algorithm.CLAlgorithmFactory;
import org.cellocad.v2.clustering.algorithm.data.CLNetlistData;
import org.cellocad.v2.clustering.algorithm.data.CLNetlistDataFactory;
import org.cellocad.v2.clustering.algorithm.data.CLNetlistEdgeData;
import org.cellocad.v2.clustering.algorithm.data.CLNetlistEdgeDataFactory;
import org.cellocad.v2.clustering.algorithm.data.CLNetlistNodeData;
import org.cellocad.v2.clustering.algorithm.data.CLNetlistNodeDataFactory;
import org.cellocad.v2.clustering.netlist.data.CLStageNetlistData;
import org.cellocad.v2.clustering.netlist.data.CLStageNetlistEdgeData;
import org.cellocad.v2.clustering.netlist.data.CLStageNetlistNodeData;
import org.cellocad.v2.common.CelloException;
import org.cellocad.v2.common.netlistConstraint.data.NetlistConstraint;
import org.cellocad.v2.common.runtime.RuntimeObject;
import org.cellocad.v2.common.runtime.environment.ArgString;
import org.cellocad.v2.common.runtime.environment.RuntimeEnv;
import org.cellocad.v2.common.stage.Stage;
import org.cellocad.v2.common.target.data.TargetData;
import org.cellocad.v2.results.common.Results;
import org.cellocad.v2.results.netlist.Netlist;
import org.cellocad.v2.results.netlist.NetlistEdge;
import org.cellocad.v2.results.netlist.NetlistNode;

/**
 * The CLRuntimeObject class is the RuntimeObject class for the <i>clustering</i> stage.
 *
 * @author Vincent Mirian
 * @author Timothy Jones
 *
 * @date 2018-05-21
 */
public class CLRuntimeObject extends RuntimeObject {

  /**
   * Initializes a newly created {@link CLRuntimeObject} with its <i>stage</i> set to parameter
   * {@code stage}, its <i>targetData</i> set to parameter {@code targetData}, its <i>netlist</i>
   * set to parameter {@code netlist}, and, its <i>runEnv</i> set to parameter {@code runEnv}.
   *
   * @param stage             The {@link Stage} used during execution.
   * @param targetData        The {@link TargetData} used during execution.
   * @param netlistConstraint The {@link NetlistConstraint} used during execution.
   * @param netlist           The {@link Netlist} used during execution.
   * @param results           The {@link Results} used during execution.
   * @param runEnv            The {@link RuntimeEnv} used during execution.
   * @throws RuntimeException if any of the parameters are null.
   */
  public CLRuntimeObject(final Stage stage, final TargetData targetData,
      final NetlistConstraint netlistConstraint, final Netlist netlist, final Results results,
      final RuntimeEnv runEnv) {
    super(stage, targetData, netlistConstraint, netlist, results, runEnv);
  }

  /**
   * Prepares the DataFactory for the Netlist, NetlistNode and NetlistEdge of the clustering stage.
   */
  @Override
  protected void prepareDataFactory() {
    setNetlistDataFactory(new CLNetlistDataFactory());
    setNetlistNodeDataFactory(new CLNetlistNodeDataFactory());
    setNetlistEdgeDataFactory(new CLNetlistEdgeDataFactory());
  }

  /**
   * Sets the CLStageNetlistData for the clustering stage in parameter {@code netlist} <b>Note: this
   * method will be deprecated in the future.</b>.
   *
   * @param netlist The <i>netlist</i> of this instance.
   */
  @Override
  protected void setStageNetlistData(final Netlist netlist) {
    netlist.setStageNetlistData(new CLStageNetlistData());
  }

  /**
   * Sets the CLStageNetlistNodeData for the clustering stage in parameter {@code node} <b>Note:
   * this method will be deprecated in the future.</b>.
   *
   * @param node A node within the netlist of this instance.
   */
  @Override
  protected void setStageNetlistNodeData(final NetlistNode node) {
    node.setStageNetlistNodeData(new CLStageNetlistNodeData());
  }

  /**
   * Sets the CLStageNetlistEdgeData for the clustering stage in parameter {@code edge} <b>Note:
   * method this will be deprecated in the future.</b>.
   *
   * @param edge An edge within the netlist of this instance.
   */
  @Override
  protected void setStageNetlistEdgeData(final NetlistEdge edge) {
    edge.setStageNetlistEdgeData(new CLStageNetlistEdgeData());
  }

  /**
   * Sets the NetlistData of the appropriate algorithm in parameter {@code netlist}.
   *
   * @param netlist The <i>netlist</i> of this instance.
   */
  @Override
  protected void setNetlistData(final Netlist netlist) {
    final CLNetlistData data = getNetlistDataFactory().getNetlistData(getAlgorithmProfile());
    netlist.setNetlistData(data);
  }

  /**
   * Sets the NetlistNodeData of the appropriate algorithm in parameter {@code node}.
   *
   * @param node A node within the netlist of this instance.
   */
  @Override
  protected void setNetlistNodeData(final NetlistNode node) {
    final CLNetlistNodeData data =
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
    final CLNetlistEdgeData data =
        getNetlistEdgeDataFactory().getNetlistEdgeData(getAlgorithmProfile());
    edge.setNetlistEdgeData(data);
  }

  /**
   * Returns a string representing the {@code OPTIONS} command line argument for the
   * <i>clustering</i> stage.
   *
   * @return A string representing the {@code OPTIONS} command line argument for the
   *         <i>clustering</i> stage.
   */
  @Override
  protected String getOptionsString() {
    return ArgString.OPTIONS;
  }

  /**
   * Executes the algorithm of the <i>clustering</i> stage.
   *
   * @throws CelloException Unable to execute the algorithm.
   */
  @Override
  protected void runAlgo() throws CelloException {
    // get Algorithm from Factory
    final CLAlgorithmFactory AF = new CLAlgorithmFactory();
    final CLAlgorithm algo = AF.getAlgorithm(getAlgorithmProfile());
    // executeAlgo
    executeAlgo(algo);
  }

  /**
   * Getter for the {@link CLNetlistDataFactorFactory}.
   *
   * @return The {@link CLNetlistDataFactory}.
   */
  protected CLNetlistDataFactory getNetlistDataFactory() {
    return netlistDataFactory;
  }

  /**
   * Setter for the {@link CLNetlistDataFactorFactory}.
   *
   * @param netlistDataFactory The {@link CLNetlistDataFactory}.
   */
  private void setNetlistDataFactory(final CLNetlistDataFactory netlistDataFactory) {
    this.netlistDataFactory = netlistDataFactory;
  }

  /**
   * Getter for the {@link CLNetlistNodeDataFactorFactory}.
   *
   * @return The {@link CLNetlistNodeDataFactory}.
   */
  protected CLNetlistNodeDataFactory getNetlistNodeDataFactory() {
    return netlistNodeDataFactory;
  }

  /**
   * Setter for the {@link CLNetlistNodeDataFactorFactory}.
   *
   * @param netlistNodeDataFactory The {@link CLNetlistNodeDataFactory}.
   */
  private void setNetlistNodeDataFactory(final CLNetlistNodeDataFactory netlistNodeDataFactory) {
    this.netlistNodeDataFactory = netlistNodeDataFactory;
  }

  /**
   * Getter for the {@link CLNetlistEdgeDataFactorFactory}.
   *
   * @return The {@link CLNetlistEdgeDataFactory}.
   */
  protected CLNetlistEdgeDataFactory getNetlistEdgeDataFactory() {
    return netlistEdgeDataFactory;
  }

  /**
   * Setter for the {@link CLNetlistEdgeDataFactorFactory}.
   *
   * @param netlistEdgeDataFactory The {@link CLNetlistEdgeDataFactory}.
   */
  private void setNetlistEdgeDataFactory(final CLNetlistEdgeDataFactory netlistEdgeDataFactory) {
    this.netlistEdgeDataFactory = netlistEdgeDataFactory;
  }

  private CLNetlistDataFactory netlistDataFactory;
  private CLNetlistEdgeDataFactory netlistEdgeDataFactory;
  private CLNetlistNodeDataFactory netlistNodeDataFactory;

  /**
   * Returns the {@link Logger} instance for the <i>clustering</i> stage.
   *
   * @return The {@link Logger} for the <i>clustering</i> stage.
   */
  @Override
  protected Logger getLogger() {
    return CLRuntimeObject.logger;
  }

  private static final Logger logger = LogManager.getLogger(CLRuntimeObject.class);

}

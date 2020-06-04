/*
 * Copyright (C) 2018-2020 Boston University (BU)
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

package org.cellocad.v2.export.runtime;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cellocad.v2.common.exception.CelloException;
import org.cellocad.v2.common.netlistConstraint.data.NetlistConstraint;
import org.cellocad.v2.common.runtime.RuntimeObject;
import org.cellocad.v2.common.runtime.environment.ArgString;
import org.cellocad.v2.common.runtime.environment.RuntimeEnv;
import org.cellocad.v2.common.stage.Stage;
import org.cellocad.v2.common.target.data.TargetData;
import org.cellocad.v2.export.algorithm.EXAlgorithm;
import org.cellocad.v2.export.algorithm.EXAlgorithmFactory;
import org.cellocad.v2.export.algorithm.data.EXNetlistData;
import org.cellocad.v2.export.algorithm.data.EXNetlistDataFactory;
import org.cellocad.v2.export.algorithm.data.EXNetlistEdgeData;
import org.cellocad.v2.export.algorithm.data.EXNetlistEdgeDataFactory;
import org.cellocad.v2.export.algorithm.data.EXNetlistNodeData;
import org.cellocad.v2.export.algorithm.data.EXNetlistNodeDataFactory;
import org.cellocad.v2.export.netlist.data.EXStageNetlistData;
import org.cellocad.v2.export.netlist.data.EXStageNetlistEdgeData;
import org.cellocad.v2.export.netlist.data.EXStageNetlistNodeData;
import org.cellocad.v2.results.common.Results;
import org.cellocad.v2.results.netlist.Netlist;
import org.cellocad.v2.results.netlist.NetlistEdge;
import org.cellocad.v2.results.netlist.NetlistNode;

/**
 * The EXRuntimeObject class is the RuntimeObject class for the <i>export</i> stage.
 *
 * @author Timothy Jones
 * @date 2018-06-04
 */
public class EXRuntimeObject extends RuntimeObject {

  /**
   * Initializes a newly created {@link EXRuntimeObject} with its <i>stage</i> set to parameter
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
  public EXRuntimeObject(
      final Stage stage,
      final TargetData targetData,
      final NetlistConstraint netlistConstraint,
      final Netlist netlist,
      final Results results,
      final RuntimeEnv runEnv) {
    super(stage, targetData, netlistConstraint, netlist, results, runEnv);
  }

  /** Prepares the DataFactory for the Netlist, NetlistNode and NetlistEdge of the export stage. */
  @Override
  protected void prepareDataFactory() {
    setNetlistDataFactory(new EXNetlistDataFactory());
    setNetlistNodeDataFactory(new EXNetlistNodeDataFactory());
    setNetlistEdgeDataFactory(new EXNetlistEdgeDataFactory());
  }

  /**
   * Sets the EXStageNetlistData for the export stage in parameter {@code netlist} <b>Note: this
   * method will be deprecated in the future.</b>.
   *
   * @param netlist The <i>netlist</i> of this instance.
   */
  @Override
  protected void setStageNetlistData(final Netlist netlist) {
    netlist.setStageNetlistData(new EXStageNetlistData());
  }

  /**
   * Sets the EXStageNetlistNodeData for the export stage in parameter {@code node} <b>Note: this
   * method will be deprecated in the future.</b>.
   *
   * @param node A node within the netlist of this instance.
   */
  @Override
  protected void setStageNetlistNodeData(final NetlistNode node) {
    node.setStageNetlistNodeData(new EXStageNetlistNodeData());
  }

  /**
   * Sets the EXStageNetlistEdgeData for the export stage in parameter {@code edge} <b>Note: method
   * this will be deprecated in the future.</b>.
   *
   * @param edge An edge within the netlist of this instance.
   */
  @Override
  protected void setStageNetlistEdgeData(final NetlistEdge edge) {
    edge.setStageNetlistEdgeData(new EXStageNetlistEdgeData());
  }

  /**
   * Sets the NetlistData of the appropriate algorithm in parameter {@code netlist}.
   *
   * @param netlist The <i>netlist</i> of this instance.
   */
  @Override
  protected void setNetlistData(final Netlist netlist) {
    final EXNetlistData data = getNetlistDataFactory().getNetlistData(getAlgorithmProfile());
    netlist.setNetlistData(data);
  }

  /**
   * Sets the NetlistNodeData of the appropriate algorithm in parameter {@code node}.
   *
   * @param node A node within the netlist of this instance.
   */
  @Override
  protected void setNetlistNodeData(final NetlistNode node) {
    final EXNetlistNodeData data =
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
    final EXNetlistEdgeData data =
        getNetlistEdgeDataFactory().getNetlistEdgeData(getAlgorithmProfile());
    edge.setNetlistEdgeData(data);
  }

  /**
   * Returns a string representing the {@code OPTIONS} command line argument for the <i>export</i>
   * stage.
   *
   * @return A string representing the {@code OPTIONS} command line argument for the <i>export</i>
   *     stage.
   */
  @Override
  protected String getOptionsString() {
    return ArgString.OPTIONS;
  }

  /**
   * Executes the algorithm of the <i>export</i> stage.
   *
   * @throws CelloException Unable to execute the algorithm.
   */
  @Override
  protected void runAlgo() throws CelloException {
    // get Algorithm from Factory
    final EXAlgorithmFactory AF = new EXAlgorithmFactory();
    final EXAlgorithm algo = AF.getAlgorithm(getAlgorithmProfile());
    // executeAlgo
    executeAlgo(algo);
  }

  /**
   * Getter for the {@link EXNetlistDataFactorFactory}.
   *
   * @return The {@link EXNetlistDataFactory}.
   */
  protected EXNetlistDataFactory getNetlistDataFactory() {
    return netlistDataFactory;
  }

  /**
   * Setter for the {@link EXNetlistDataFactorFactory}.
   *
   * @param netlistDataFactory The {@link EXNetlistDataFactory}.
   */
  private void setNetlistDataFactory(final EXNetlistDataFactory netlistDataFactory) {
    this.netlistDataFactory = netlistDataFactory;
  }

  /**
   * Getter for the {@link EXNetlistNodeDataFactorFactory}.
   *
   * @return The {@link EXNetlistNodeDataFactory}.
   */
  protected EXNetlistNodeDataFactory getNetlistNodeDataFactory() {
    return netlistNodeDataFactory;
  }

  /**
   * Setter for the {@link EXNetlistNodeDataFactorFactory}.
   *
   * @param netlistNodeDataFactory The {@link EXNetlistNodeDataFactory}.
   */
  private void setNetlistNodeDataFactory(final EXNetlistNodeDataFactory netlistNodeDataFactory) {
    this.netlistNodeDataFactory = netlistNodeDataFactory;
  }

  /**
   * Getter for the {@link EXNetlistEdgeDataFactorFactory}.
   *
   * @return The {@link EXNetlistEdgeDataFactory}.
   */
  protected EXNetlistEdgeDataFactory getNetlistEdgeDataFactory() {
    return netlistEdgeDataFactory;
  }

  /**
   * Setter for the {@link EXNetlistEdgeDataFactorFactory}.
   *
   * @param netlistEdgeDataFactory The {@link EXNetlistEdgeDataFactory}.
   */
  private void setNetlistEdgeDataFactory(final EXNetlistEdgeDataFactory netlistEdgeDataFactory) {
    this.netlistEdgeDataFactory = netlistEdgeDataFactory;
  }

  private EXNetlistDataFactory netlistDataFactory;
  private EXNetlistEdgeDataFactory netlistEdgeDataFactory;
  private EXNetlistNodeDataFactory netlistNodeDataFactory;

  /**
   * Returns the {@link Logger} instance for the <i>export</i> stage.
   *
   * @return The {@link Logger} for the <i>export</i> stage.
   */
  @Override
  protected Logger getLogger() {
    return EXRuntimeObject.logger;
  }

  private static final Logger logger = LogManager.getLogger(EXRuntimeObject.class);
}

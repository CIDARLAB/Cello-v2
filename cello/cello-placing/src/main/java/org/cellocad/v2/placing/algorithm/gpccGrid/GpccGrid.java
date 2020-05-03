/*
 * Copyright (C) 2017 Massachusetts Institute of Technology (MIT)
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

package org.cellocad.v2.placing.algorithm.gpccGrid;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cellocad.v2.placing.algorithm.PLAlgorithm;
import org.cellocad.v2.placing.algorithm.gpccGrid.data.GpccGridNetlistData;
import org.cellocad.v2.placing.algorithm.gpccGrid.data.GpccGridNetlistEdgeData;
import org.cellocad.v2.placing.algorithm.gpccGrid.data.GpccGridNetlistNodeData;
import org.cellocad.v2.results.netlist.Netlist;
import org.cellocad.v2.results.netlist.NetlistEdge;
import org.cellocad.v2.results.netlist.NetlistNode;

/**
 * The implementation the <i>GPCC_GRID</i> algorithm in the <i>placing</i> stage.
 *
 * @author Vincent Mirian
 *
 * @date 2018-05-21
 */
public class GpccGrid extends PLAlgorithm {

  /**
   * Returns the {@link GpccGridNetlistNodeData} of the given node.
   *
   * @param node A node within the netlist of this instance.
   * @return The {@link GpccGridNetlistNodeData} instance if it exists, null otherwise.
   */
  protected GpccGridNetlistNodeData getGpccGridNetlistNodeData(final NetlistNode node) {
    GpccGridNetlistNodeData rtn = null;
    rtn = (GpccGridNetlistNodeData) node.getNetlistNodeData();
    return rtn;
  }

  /**
   * Returns the {@link GpccGridNetlistEdgeData} of the given edge.
   *
   * @param edge An edge within the netlist of this instance.
   * @return The {@link GpccGridNetlistEdgeData} instance if it exists, null otherwise.
   */
  protected GpccGridNetlistEdgeData getGpccGridNetlistEdgeData(final NetlistEdge edge) {
    GpccGridNetlistEdgeData rtn = null;
    rtn = (GpccGridNetlistEdgeData) edge.getNetlistEdgeData();
    return rtn;
  }

  /**
   * Returns the {@link GpccGridNetlistData} of the given netlist.
   *
   * @param netlist The netlist of this instance.
   * @return The {@link GpccGridNetlistData} instance if it exists, null otherwise.
   */
  protected GpccGridNetlistData getGpccGridNetlistData(final Netlist netlist) {
    GpccGridNetlistData rtn = null;
    rtn = (GpccGridNetlistData) netlist.getNetlistData();
    return rtn;
  }

  /**
   * Gets the constraint data from the netlist constraint file.
   */
  @Override
  protected void getConstraintFromNetlistConstraintFile() {

  }

  /**
   * Gets the data from the UCF.
   */
  @Override
  protected void getDataFromUcf() {

  }

  /**
   * Set parameter values of the algorithm.
   */
  @Override
  protected void setParameterValues() {
  }

  /**
   * Validate parameter values of the algorithm.
   */
  @Override
  protected void validateParameterValues() {

  }

  /**
   * Perform preprocessing.
   */
  @Override
  protected void preprocessing() {
    // analyze and insert dummy cells to relay
    // see notes for algo
  }

  /**
   * Run the (core) algorithm.
   */
  @Override
  protected void run() {
    // see notes for algo
  }

  /**
   * Perform postprocessing.
   */
  @Override
  protected void postprocessing() {
    // verify
  }

  /**
   * Returns the {@link Logger} for the <i>GPCC_GRID</i> algorithm.
   *
   * @return The {@link Logger} for the <i>GPCC_GRID</i> algorithm.
   */
  @Override
  protected Logger getLogger() {
    return GpccGrid.logger;
  }

  private static final Logger logger = LogManager.getLogger(GpccGrid.class);

}

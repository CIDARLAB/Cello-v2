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

package org.cellocad.v2.clustering.algorithm.CL_RC;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cellocad.v2.clustering.algorithm.CLAlgorithm;
import org.cellocad.v2.clustering.algorithm.CL_RC.data.ClRcNetlistData;
import org.cellocad.v2.clustering.algorithm.CL_RC.data.ClRcNetlistEdgeData;
import org.cellocad.v2.clustering.algorithm.CL_RC.data.ClRcNetlistNodeData;
import org.cellocad.v2.results.netlist.Netlist;
import org.cellocad.v2.results.netlist.NetlistEdge;
import org.cellocad.v2.results.netlist.NetlistNode;

/**
 * The implementation of the <i>CL_RC</i> algorithm in the <i>clustering</i> stage.
 *
 * @author Vincent Mirian
 *
 * @date 2018-05-21
 */
public class ClRc extends CLAlgorithm {

  /**
   * Returns the {@link ClRcNetlistNodeData} of the given node.
   *
   * @param node A node within the netlist of this instance.
   * @return The {@link ClRcNetlistNodeData} instance if it exists, null otherwise.
   */
  protected ClRcNetlistNodeData getCL_RCNetlistNodeData(final NetlistNode node) {
    ClRcNetlistNodeData rtn = null;
    rtn = (ClRcNetlistNodeData) node.getNetlistNodeData();
    return rtn;
  }

  /**
   * Returns the {@link ClRcNetlistEdgeData} of the given edge.
   *
   * @param edge An edge within the netlist of this instance.
   * @return The {@link ClRcNetlistEdgeData} instance if it exists, null otherwise.
   */
  protected ClRcNetlistEdgeData getCL_RCNetlistEdgeData(final NetlistEdge edge) {
    ClRcNetlistEdgeData rtn = null;
    rtn = (ClRcNetlistEdgeData) edge.getNetlistEdgeData();
    return rtn;
  }

  /**
   * Returns the {@link ClRcNetlistData} of the given netlist.
   *
   * @param netlist The netlist of this instance.
   * @return The {@link ClRcNetlistData} instance if it exists, null otherwise.
   */
  protected ClRcNetlistData getCL_RCNetlistData(final Netlist netlist) {
    ClRcNetlistData rtn = null;
    rtn = (ClRcNetlistData) netlist.getNetlistData();
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

  }

  /**
   * Run the (core) algorithm.
   */
  @Override
  protected void run() {

  }

  /**
   * Perform postprocessing.
   */
  @Override
  protected void postprocessing() {

  }

  /**
   * Returns the {@link Logger} for the <i>CL_RC</i> algorithm.
   *
   * @return The {@link Logger} for the <i>CL_RC</i> algorithm.
   */
  @Override
  protected Logger getLogger() {
    return ClRc.logger;
  }

  private static final Logger logger = LogManager.getLogger(ClRc.class);

}

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

package org.cellocad.v2.logicOptimization.algorithm;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cellocad.v2.common.algorithm.Algorithm;
import org.cellocad.v2.logicOptimization.netlist.data.LOStageNetlistData;
import org.cellocad.v2.logicOptimization.netlist.data.LOStageNetlistEdgeData;
import org.cellocad.v2.logicOptimization.netlist.data.LOStageNetlistNodeData;
import org.cellocad.v2.results.netlist.Netlist;
import org.cellocad.v2.results.netlist.NetlistEdge;
import org.cellocad.v2.results.netlist.NetlistNode;

/**
 * The base class for all algorithms in the <i>logicOptimization</i> stage.
 *
 * @author Vincent Mirian
 * @date 2018-05-21
 */
public abstract class LOAlgorithm extends Algorithm {

  /**
   * Returns the {@link LOStageNetlistNodeData} of the given node.
   *
   * @param node A node within the netlist of this instance.
   * @return The {@link LOStageNetlistNodeData} instance if it exists, null otherwise.
   */
  protected LOStageNetlistNodeData getStageNetlistNodeData(final NetlistNode node) {
    LOStageNetlistNodeData rtn = null;
    rtn = (LOStageNetlistNodeData) node.getStageNetlistNodeData();
    return rtn;
  }

  /**
   * Returns the {@link LOStageNetlistEdgeData} of the given edge.
   *
   * @param edge An edge within the netlist of this instance.
   * @return The {@link LOStageNetlistEdgeData} instance if it exists, null otherwise.
   */
  protected LOStageNetlistEdgeData getStageNetlistEdgeData(final NetlistEdge edge) {
    LOStageNetlistEdgeData rtn = null;
    rtn = (LOStageNetlistEdgeData) edge.getStageNetlistEdgeData();
    return rtn;
  }

  /**
   * Returns the {@link LOStageNetlistData} of the given netlist.
   *
   * @param netlist The netlist of this instance.
   * @return The {@link LOStageNetlistData} instance if it exists, null otherwise.
   */
  protected LOStageNetlistData getStageNetlistData(final Netlist netlist) {
    LOStageNetlistData rtn = null;
    rtn = (LOStageNetlistData) netlist.getStageNetlistData();
    return rtn;
  }

  /**
   * Returns the {@link Logger} for the <i>LOAlgorithm</i> algorithm.
   *
   * @return The {@link Logger} for the <i>LOAlgorithm</i> algorithm.
   */
  @Override
  protected Logger getLogger() {
    return LOAlgorithm.logger;
  }

  private static final Logger logger = LogManager.getLogger(LOAlgorithm.class);
}

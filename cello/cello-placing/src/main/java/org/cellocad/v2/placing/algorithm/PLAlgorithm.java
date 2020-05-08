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

package org.cellocad.v2.placing.algorithm;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cellocad.v2.common.algorithm.Algorithm;
import org.cellocad.v2.placing.netlist.data.PLStageNetlistData;
import org.cellocad.v2.placing.netlist.data.PLStageNetlistEdgeData;
import org.cellocad.v2.placing.netlist.data.PLStageNetlistNodeData;
import org.cellocad.v2.results.netlist.Netlist;
import org.cellocad.v2.results.netlist.NetlistEdge;
import org.cellocad.v2.results.netlist.NetlistNode;

/**
 * The base class for all algorithms in the <i>placing</i> stage.
 *
 * @author Vincent Mirian
 * @date 2018-05-21
 */
public abstract class PLAlgorithm extends Algorithm {

  /**
   * Returns the {@link PLStageNetlistNodeData} of the given node.
   *
   * @param node A node within the netlist of this instance.
   * @return The {@link PLStageNetlistNodeData} instance if it exists, null otherwise.
   */
  protected PLStageNetlistNodeData getStageNetlistNodeData(final NetlistNode node) {
    PLStageNetlistNodeData rtn = null;
    rtn = (PLStageNetlistNodeData) node.getStageNetlistNodeData();
    return rtn;
  }

  /**
   * Returns the {@link PLStageNetlistEdgeData} of the given edge.
   *
   * @param edge An edge within the netlist of this instance.
   * @return The {@link PLStageNetlistEdgeData} instance if it exists, null otherwise.
   */
  protected PLStageNetlistEdgeData getStageNetlistEdgeData(final NetlistEdge edge) {
    PLStageNetlistEdgeData rtn = null;
    rtn = (PLStageNetlistEdgeData) edge.getStageNetlistEdgeData();
    return rtn;
  }

  /**
   * Returns the {@link PLStageNetlistData} of the given netlist.
   *
   * @param netlist The netlist of this instance.
   * @return The {@link PLStageNetlistData} instance if it exists, null otherwise.
   */
  protected PLStageNetlistData getStageNetlistData(final Netlist netlist) {
    PLStageNetlistData rtn = null;
    rtn = (PLStageNetlistData) netlist.getStageNetlistData();
    return rtn;
  }

  /**
   * Returns the {@link Logger} for the <i>PLAlgorithm</i> algorithm.
   *
   * @return The {@link Logger} for the <i>PLAlgorithm</i> algorithm.
   */
  @Override
  protected Logger getLogger() {
    return PLAlgorithm.logger;
  }

  private static final Logger logger = LogManager.getLogger(PLAlgorithm.class);
}

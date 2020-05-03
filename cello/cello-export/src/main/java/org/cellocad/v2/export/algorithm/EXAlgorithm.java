/*
 * Copyright (C) 2018 Boston University (BU)
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

package org.cellocad.v2.export.algorithm;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cellocad.v2.common.algorithm.Algorithm;
import org.cellocad.v2.export.netlist.data.EXStageNetlistData;
import org.cellocad.v2.export.netlist.data.EXStageNetlistEdgeData;
import org.cellocad.v2.export.netlist.data.EXStageNetlistNodeData;
import org.cellocad.v2.results.netlist.Netlist;
import org.cellocad.v2.results.netlist.NetlistEdge;
import org.cellocad.v2.results.netlist.NetlistNode;

/**
 * The base class for all algorithms in the <i>export</i> stage.
 *
 * @author Timothy Jones
 *
 * @date 2018-06-04
 */
public abstract class EXAlgorithm extends Algorithm {

  /**
   * Returns the {@link EXStageNetlistNodeData} of the given node.
   *
   * @param node A node within the netlist of this instance.
   * @return The {@link EXStageNetlistNodeData} instance if it exists, null otherwise.
   */
  protected EXStageNetlistNodeData getStageNetlistNodeData(final NetlistNode node) {
    EXStageNetlistNodeData rtn = null;
    rtn = (EXStageNetlistNodeData) node.getStageNetlistNodeData();
    return rtn;
  }

  /**
   * Returns the {@link EXStageNetlistEdgeData} of the given edge.
   *
   * @param edge An edge within the netlist of this instance.
   * @return The {@link EXStageNetlistEdgeData} instance if it exists, null otherwise.
   */
  protected EXStageNetlistEdgeData getStageNetlistEdgeData(final NetlistEdge edge) {
    EXStageNetlistEdgeData rtn = null;
    rtn = (EXStageNetlistEdgeData) edge.getStageNetlistEdgeData();
    return rtn;
  }

  /**
   * Returns the {@link EXStageNetlistData} of the given netlist.
   *
   * @param netlist The netlist of this instance.
   * @return The {@link EXStageNetlistData} instance if it exists, null otherwise.
   */
  protected EXStageNetlistData getStageNetlistData(final Netlist netlist) {
    EXStageNetlistData rtn = null;
    rtn = (EXStageNetlistData) netlist.getStageNetlistData();
    return rtn;
  }

  /**
   * Returns the {@link Logger} for the <i>EXAlgorithm</i> algorithm.
   *
   * @return The {@link Logger} for the <i>EXAlgorithm</i> algorithm.
   */
  @Override
  protected Logger getLogger() {
    return EXAlgorithm.logger;
  }

  private static final Logger logger = LogManager.getLogger(EXAlgorithm.class);

}

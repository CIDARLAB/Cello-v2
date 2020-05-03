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

package org.cellocad.v2.technologyMapping.algorithm;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cellocad.v2.common.algorithm.Algorithm;
import org.cellocad.v2.results.netlist.Netlist;
import org.cellocad.v2.results.netlist.NetlistEdge;
import org.cellocad.v2.results.netlist.NetlistNode;
import org.cellocad.v2.technologyMapping.netlist.data.TMStageNetlistData;
import org.cellocad.v2.technologyMapping.netlist.data.TMStageNetlistEdgeData;
import org.cellocad.v2.technologyMapping.netlist.data.TMStageNetlistNodeData;

/**
 * The base class for all algorithms in the <i>technologyMapping</i> stage.
 *
 * @author Vincent Mirian
 *
 * @date 2018-05-21
 */
public abstract class TMAlgorithm extends Algorithm {

  /**
   * Returns the {@link TMStageNetlistNodeData} of the given node.
   *
   * @param node A node within the netlist of this instance.
   * @return The {@link TMStageNetlistNodeData} instance if it exists, null otherwise.
   */
  protected TMStageNetlistNodeData getStageNetlistNodeData(final NetlistNode node) {
    TMStageNetlistNodeData rtn = null;
    rtn = (TMStageNetlistNodeData) node.getStageNetlistNodeData();
    return rtn;
  }

  /**
   * Returns the {@link TMStageNetlistEdgeData} of the given edge.
   *
   * @param edge An edge within the netlist of this instance.
   * @return The {@link TMStageNetlistEdgeData} instance if it exists, null otherwise.
   */
  protected TMStageNetlistEdgeData getStageNetlistEdgeData(final NetlistEdge edge) {
    TMStageNetlistEdgeData rtn = null;
    rtn = (TMStageNetlistEdgeData) edge.getStageNetlistEdgeData();
    return rtn;
  }

  /**
   * Returns the {@link TMStageNetlistData} of the given netlist.
   *
   * @param netlist The netlist of this instance.
   * @return The {@link TMStageNetlistData} instance if it exists, null otherwise.
   */
  protected TMStageNetlistData getStageNetlistData(final Netlist netlist) {
    TMStageNetlistData rtn = null;
    rtn = (TMStageNetlistData) netlist.getStageNetlistData();
    return rtn;
  }

  /**
   * Returns the {@link Logger} for the <i>TMAlgorithm</i> algorithm.
   *
   * @return The {@link Logger} for the <i>TMAlgorithm</i> algorithm.
   */
  @Override
  protected Logger getLogger() {
    return TMAlgorithm.logger;
  }

  private static final Logger logger = LogManager.getLogger(TMAlgorithm.class);

}

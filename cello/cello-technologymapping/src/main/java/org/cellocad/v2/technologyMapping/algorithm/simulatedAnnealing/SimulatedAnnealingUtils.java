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

package org.cellocad.v2.technologyMapping.algorithm.SimulatedAnnealing;

import org.cellocad.v2.results.netlist.Netlist;
import org.cellocad.v2.results.netlist.NetlistEdge;
import org.cellocad.v2.results.netlist.NetlistNode;
import org.cellocad.v2.technologyMapping.algorithm.SimulatedAnnealing.data.SimulatedAnnealingNetlistData;
import org.cellocad.v2.technologyMapping.algorithm.SimulatedAnnealing.data.SimulatedAnnealingNetlistEdgeData;
import org.cellocad.v2.technologyMapping.algorithm.SimulatedAnnealing.data.SimulatedAnnealingNetlistNodeData;

/**
 * Utility methods for the <i>SimulatedAnnealing</i> algorithm in the <i>technologyMapping</i>
 * stage.
 *
 * @author Timothy Jones
 *
 * @date 2018-07-16
 */
public class SimulatedAnnealingUtils {

  /**
   * Returns the {@link SimulatedAnnealingNetlistNodeData} of the given node.
   *
   * @param node A node within the netlist of this instance.
   * @return The {@link SimulatedAnnealingNetlistNodeData} instance if it exists, null otherwise.
   */
  public static SimulatedAnnealingNetlistNodeData getSimulatedAnnealingNetlistNodeData(
      final NetlistNode node) {
    SimulatedAnnealingNetlistNodeData rtn = null;
    rtn = (SimulatedAnnealingNetlistNodeData) node.getNetlistNodeData();
    return rtn;
  }

  /**
   * Returns the {@link SimulatedAnnealingNetlistEdgeData} of the given edge.
   *
   * @param edge An edge within the netlist of this instance.
   * @return The {@link SimulatedAnnealingNetlistEdgeData} instance if it exists, null otherwise.
   */
  public static SimulatedAnnealingNetlistEdgeData getSimulatedAnnealingNetlistEdgeData(
      final NetlistEdge edge) {
    SimulatedAnnealingNetlistEdgeData rtn = null;
    rtn = (SimulatedAnnealingNetlistEdgeData) edge.getNetlistEdgeData();
    return rtn;
  }

  /**
   * Returns the {@link SimulatedAnnealingNetlistData} of the given netlist.
   *
   * @param netlist The netlist of this instance.
   * @return The {@link SimulatedAnnealingNetlistData} instance if it exists, null otherwise.
   */
  public static SimulatedAnnealingNetlistData getSimulatedAnnealingNetlistData(
      final Netlist netlist) {
    SimulatedAnnealingNetlistData rtn = null;
    rtn = (SimulatedAnnealingNetlistData) netlist.getNetlistData();
    return rtn;
  }

}

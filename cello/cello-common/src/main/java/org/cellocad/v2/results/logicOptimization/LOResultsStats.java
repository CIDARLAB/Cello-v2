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

package org.cellocad.v2.results.logicOptimization;

import org.cellocad.v2.common.CObjectCollection;
import org.cellocad.v2.common.Utils;
import org.cellocad.v2.results.logicSynthesis.LSResultsUtils;
import org.cellocad.v2.results.netlist.Netlist;
import org.cellocad.v2.results.netlist.NetlistNode;

/**
 * Stats from a netlist instance in the <i>logicOptimization</i> stage.
 *
 * @author Vincent Mirian
 * @date 2018-05-21
 */
public class LOResultsStats {

  /**
   * Gets the logic optimization stats of netlist defined by parameter {@code myNetlist}.
   *
   * @param myNetlist The {@link Netlist}.
   */
  public static String getLogicOptimizationStats(final Netlist myNetlist) {
    String rtn = "";
    rtn += Utils.getNewLine();
    rtn += LOResultsStats.S_HEADER + Utils.getNewLine();
    rtn += "LogicOptimizationStats" + Utils.getNewLine();
    rtn += LOResultsStats.S_HEADER + Utils.getNewLine();
    final String[] validnodes = LSResultsUtils.ValidNodeTypes;
    for (int i = 0; i < validnodes.length; i++) {
      final String nodeTypeName = validnodes[i];
      final CObjectCollection<NetlistNode> nodeTypeList =
          LSResultsUtils.getNodeType(myNetlist, nodeTypeName);
      final int numNodes = nodeTypeList.size();
      if (numNodes == 0) {
        continue;
      }
      // fan-in
      int totalFI = 0;
      int maxFI = Integer.MIN_VALUE;
      int minFI = Integer.MAX_VALUE;
      // fan-out
      int totalFO = 0;
      int maxFO = Integer.MIN_VALUE;
      int minFO = Integer.MAX_VALUE;
      for (int j = 0; j < numNodes; j++) {
        final NetlistNode node = nodeTypeList.get(j);
        final int FI = node.getNumInEdge();
        final int FO = node.getNumOutEdge();
        totalFI += FI;
        if (maxFI < FI) {
          maxFI = FI;
        }
        if (minFI > FI) {
          minFI = FI;
        }
        totalFO += FO;
        if (maxFO < FO) {
          maxFO = FO;
        }
        if (minFO > FO) {
          minFO = FO;
        }
      }
      rtn += LOResultsStats.S_HEADER + Utils.getNewLine();
      rtn += "Node Type: " + nodeTypeName + Utils.getNewLine();
      rtn +=
          "Max Fan-in: "
              + maxFI
              + ", min Fan-in: "
              + minFI
              + ", avg Fan-in: "
              + (double) totalFI / numNodes
              + Utils.getNewLine();
      rtn +=
          "Max Fan-out: "
              + maxFO
              + ", min Fan-out: "
              + minFO
              + ", avg Fan-out: "
              + (double) totalFO / numNodes
              + Utils.getNewLine();
    }
    return rtn;
  }

  private static final String S_HEADER = "--------------------------------------------";
}

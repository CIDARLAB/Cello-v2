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

package org.cellocad.v2.results.logicSynthesis;

import org.cellocad.v2.common.Utils;
import org.cellocad.v2.results.netlist.Netlist;

/**
 * Stats from the <i>logicSynthesis</i> stage.
 *
 * @author Vincent Mirian
 *
 * @date 2018-05-21
 */
public class LSResultsStats {

  /**
   * Gets the logic synthesis stats of netlist defined by parameter {@code myNetlist}.
   *
   * @param myNetlist The {@link Netlist}.
   */
  public static String getLogicSynthesisStats(final Netlist myNetlist) {
    String rtn = "";
    rtn += Utils.getNewLine();
    rtn += LSResultsStats.S_HEADER + Utils.getNewLine();
    rtn += "LogicSynthesisStats" + Utils.getNewLine();
    rtn += LSResultsStats.S_HEADER + Utils.getNewLine();
    rtn += "Netlist name: " + myNetlist.getName() + Utils.getNewLine();
    rtn += LSResultsStats.S_HEADER + Utils.getNewLine();
    rtn += "Total number of inputs: " + LSResultsUtils.getAllInputNodes(myNetlist).size()
        + Utils.getNewLine();
    rtn += "Number of primary inputs: " + LSResultsUtils.getPrimaryInputNodes(myNetlist).size()
        + Utils.getNewLine();
    rtn +=
        "Number of inputs: " + LSResultsUtils.getInputNodes(myNetlist).size() + Utils.getNewLine();
    rtn += LSResultsStats.S_HEADER + Utils.getNewLine();
    rtn += "Total number of outputs: " + LSResultsUtils.getAllOutputNodes(myNetlist).size()
        + Utils.getNewLine();
    rtn += "Number of primary outputs: " + LSResultsUtils.getPrimaryOutputNodes(myNetlist).size()
        + Utils.getNewLine();
    rtn += "Number of outputs: " + LSResultsUtils.getOutputNodes(myNetlist).size()
        + Utils.getNewLine();
    rtn += LSResultsStats.S_HEADER + Utils.getNewLine();
    rtn += "Number of gates : "
        + (myNetlist.getNumVertex() - LSResultsUtils.getAllInputOutputNodes(myNetlist).size())
        + Utils.getNewLine();
    final String[] validnodes = LSResultsUtils.ValidNodeTypes;
    for (int i = 0; i < validnodes.length; i++) {
      final String nodeTypeName = validnodes[i];
      final int numNodes = LSResultsUtils.getNodeType(myNetlist, nodeTypeName).size();
      if (numNodes == 0) {
        continue;
      }
      rtn += "Number of " + nodeTypeName + " gates: " + numNodes + Utils.getNewLine();
    }
    return rtn;
  }

  private static final String S_HEADER = "--------------------------------------------";

}

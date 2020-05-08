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

package org.cellocad.v2.results.partitioning;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.cellocad.v2.common.CObjectCollection;
import org.cellocad.v2.common.Utils;
import org.cellocad.v2.results.logicOptimization.LOResultsStats;
import org.cellocad.v2.results.logicSynthesis.LSResults;
import org.cellocad.v2.results.logicSynthesis.LSResultsStats;
import org.cellocad.v2.results.logicSynthesis.LSResultsUtils;
import org.cellocad.v2.results.netlist.Netlist;
import org.cellocad.v2.results.netlist.NetlistNode;
import org.cellocad.v2.results.partitioning.block.PTBlockNetlist;

/**
 * Stats from a {@link PTBlockNetlist} instance.
 *
 * @author Vincent Mirian
 * @date 2018-05-21
 */
public class PTResultsStats {

  /**
   * Returns the minimum value between parameters <i>left</i> and <i>right</i>.
   *
   * @param left a value.
   * @param right a value.
   */
  private static int min(final int left, final int right) {
    int rtn = left;
    if (right < left) {
      rtn = right;
    }
    return rtn;
  }

  /**
   * Returns the maximum value between parameters <i>left</i> and <i>right</i>.
   *
   * @param left a value.
   * @param right a value.
   */
  private static int max(final int left, final int right) {
    int rtn = left;
    if (right > left) {
      rtn = right;
    }
    return rtn;
  }

  /**
   * The <i>getPartitioningStats</i> gets the partitioning stats of netlist defined by parameter
   * {@code myNetlist}.
   *
   * @param myNetlist The {@link Netlist}.
   */
  public static String getPartitioningStats(final Netlist myNetlist) {
    String rtn = "";
    rtn += Utils.getNewLine();
    rtn += PTResultsStats.S_HEADER + Utils.getNewLine();
    rtn += "PartitioningStats" + Utils.getNewLine();
    rtn += PTResultsStats.S_HEADER + Utils.getNewLine();
    final PTBlockNetlist ptBlockNetlist = new PTBlockNetlist(myNetlist);
    Netlist netlist = null;
    final int numBlocks = ptBlockNetlist.getNetlistFONum();
    rtn += "Number of Blocks: " + numBlocks + Utils.getNewLine();
    // input
    int minInput = Integer.MAX_VALUE;
    int maxInput = Integer.MIN_VALUE;
    int totalInput = 0;
    int minAInput = Integer.MAX_VALUE;
    int maxAInput = Integer.MIN_VALUE;
    int totalAInput = 0;
    int minPInput = Integer.MAX_VALUE;
    int maxPInput = Integer.MIN_VALUE;
    int totalPInput = 0;
    // output
    int minOutput = Integer.MAX_VALUE;
    int maxOutput = Integer.MIN_VALUE;
    int totalOutput = 0;
    int minAOutput = Integer.MAX_VALUE;
    int maxAOutput = Integer.MIN_VALUE;
    int totalAOutput = 0;
    int minPOutput = Integer.MAX_VALUE;
    int maxPOutput = Integer.MIN_VALUE;
    int totalPOutput = 0;
    // gates
    int minGates = Integer.MAX_VALUE;
    int maxGates = Integer.MIN_VALUE;
    int totalGates = 0;
    for (int i = 0; i < numBlocks; i++) {
      netlist = ptBlockNetlist.getNetlistFOAtIdx(i);
      rtn += PTResultsStats.S_HEADER + Utils.getNewLine();
      rtn += "Block name: " + netlist.getName() + Utils.getNewLine();
      rtn += LSResultsStats.getLogicSynthesisStats(netlist);
      rtn += LOResultsStats.getLogicOptimizationStats(netlist);
      //
      final int numTotalNodeBlock = netlist.getNumVertex();
      final int numAInputBlock = LSResultsUtils.getAllInputNodes(netlist).size();
      final int numAOutputBlock = LSResultsUtils.getAllOutputNodes(netlist).size();
      final int numPInputBlock = LSResultsUtils.getPrimaryInputNodes(netlist).size();
      final int numPOutputBlock = LSResultsUtils.getPrimaryOutputNodes(netlist).size();
      final int numInputBlock = LSResultsUtils.getPrimaryInputNodes(netlist).size();
      final int numOutputBlock = LSResultsUtils.getPrimaryOutputNodes(netlist).size();
      final int numGatesBlock = numTotalNodeBlock - numAInputBlock - numAOutputBlock;
      // Input
      minInput = PTResultsStats.min(numInputBlock, minInput);
      maxInput = PTResultsStats.max(numInputBlock, maxInput);
      totalInput += numInputBlock;
      // Output
      minOutput = PTResultsStats.min(numOutputBlock, minOutput);
      maxOutput = PTResultsStats.max(numOutputBlock, maxOutput);
      totalOutput += numOutputBlock;
      // AInput
      minAInput = PTResultsStats.min(numAInputBlock, minAInput);
      maxAInput = PTResultsStats.max(numAInputBlock, maxAInput);
      totalAInput += numAInputBlock;
      // AOutput
      minAOutput = PTResultsStats.min(numAOutputBlock, minAOutput);
      maxAOutput = PTResultsStats.max(numAOutputBlock, maxAOutput);
      totalAOutput += numAOutputBlock;
      // PInput
      minPInput = PTResultsStats.min(numPInputBlock, minPInput);
      maxPInput = PTResultsStats.max(numPInputBlock, maxPInput);
      totalPInput += numPInputBlock;
      // POutput
      minPOutput = PTResultsStats.min(numPOutputBlock, minPOutput);
      maxPOutput = PTResultsStats.max(numPOutputBlock, maxPOutput);
      totalPOutput += numPOutputBlock;
      // Gates
      minGates = PTResultsStats.min(numGatesBlock, minGates);
      maxGates = PTResultsStats.max(numGatesBlock, maxGates);
      totalGates += numGatesBlock;
      rtn += PTResultsStats.S_HEADER + Utils.getNewLine();
      rtn += "Quorum signals" + Utils.getNewLine();
      rtn += PTResultsStats.S_HEADER + Utils.getNewLine();
      // unique signals
      CObjectCollection<NetlistNode> conns;
      final Set<NetlistNode> nodes = new HashSet<>();
      final Map<Integer, Integer> PID = new HashMap<>();
      // output
      conns = LSResultsUtils.getNodeType(netlist, LSResults.S_OUTPUT);
      nodes.clear();
      PID.clear();
      for (int j = 0; j < conns.size(); j++) {
        // get unique output
        final NetlistNode node = conns.get(j);
        final NetlistNode outputNode = node.getInEdgeAtIdx(0).getSrc();
        nodes.add(outputNode);
        // get number of connection to individual blocks
        final String inputName = PTBlockNetlist.getMatchingVioNodeName(node.getName());
        final NetlistNode inputNode =
            ptBlockNetlist
                .getVirtualLargeNetlistFO()
                .getVertexByName(inputName)
                .getOutEdgeAtIdx(0)
                .getDst();
        final int pid = inputNode.getResultNetlistNodeData().getPartitionID();
        Integer count = PID.get(pid);
        if (count == null) {
          count = 0;
        }
        count = count + 1;
        PID.put(pid, count);
      }
      rtn += "Number of unique output signals: " + nodes.size() + Utils.getNewLine();
      rtn += "Signals sent to [PID, count]: " + PID.toString() + Utils.getNewLine();
      // input
      conns = LSResultsUtils.getNodeType(netlist, LSResults.S_INPUT);
      nodes.clear();
      PID.clear();
      for (int j = 0; j < conns.size(); j++) {
        final NetlistNode node = conns.get(j);
        final NetlistNode inputNode = node.getOutEdgeAtIdx(0).getSrc();
        nodes.add(inputNode);
        final String outputName = PTBlockNetlist.getMatchingVioNodeName(node.getName());
        final NetlistNode oNode =
            ptBlockNetlist
                .getVirtualLargeNetlistFO()
                .getVertexByName(outputName)
                .getInEdgeAtIdx(0)
                .getSrc();
        final int pid = oNode.getResultNetlistNodeData().getPartitionID();
        Integer count = PID.get(pid);
        if (count == null) {
          count = 0;
        }
        count = count + 1;
        PID.put(pid, count);
      }
      rtn += "Number of unique input signals: " + nodes.size() + Utils.getNewLine();
      rtn += "Signals sent from [PID, count]: " + PID.toString() + Utils.getNewLine();
    }
    rtn += PTResultsStats.S_HEADER + Utils.getNewLine();
    rtn +=
        "Minimum number of total inputs to a cell: "
            + minAInput
            + ", Maximum number of total inputs to a cell: ";
    rtn +=
        maxAInput
            + ", Average number of total inputs per cell: "
            + (double) totalAInput / numBlocks
            + Utils.getNewLine();
    rtn +=
        "Minimum number of primary inputs to a cell: "
            + minPInput
            + ", Maximum number of primary inputs to a cell: ";
    rtn +=
        maxPInput
            + ", Average number of primary inputs per cell: "
            + (double) totalPInput / numBlocks
            + Utils.getNewLine();
    rtn +=
        "Minimum number of inputs to a cell: "
            + minInput
            + ", Maximum number of inputs to a cell: ";
    rtn +=
        maxInput
            + ", Average number of inputs per cell: "
            + (double) totalInput / numBlocks
            + Utils.getNewLine();
    rtn += PTResultsStats.S_HEADER + Utils.getNewLine();
    rtn +=
        "Minimum number of total outputs from a cell: "
            + minAOutput
            + ", Maximum number of total outputs from a cell: ";
    rtn +=
        maxAOutput
            + ", Average number of total outputs per cell: "
            + (double) totalAOutput / numBlocks
            + Utils.getNewLine();
    rtn +=
        "Minimum number of primary outputs from a cell: "
            + minPOutput
            + ", Maximum number of primary outputs from a cell: ";
    rtn +=
        maxPOutput
            + ", Average number of primary outputs per cell: "
            + (double) totalPOutput / numBlocks
            + Utils.getNewLine();
    rtn +=
        "Minimum number of outputs from a cell: "
            + minOutput
            + ", Maximum number of outputs from a cell: ";
    rtn +=
        maxOutput
            + ", Average number of outputs per cell: "
            + (double) totalOutput / numBlocks
            + Utils.getNewLine();
    rtn += PTResultsStats.S_HEADER + Utils.getNewLine();
    rtn +=
        "Total number of gates within all cells: "
            + totalGates
            + ", Minimum number of gates within a cell: "
            + minGates
            + ", Maximum number of gates within a cell: ";
    rtn +=
        maxGates
            + ", Average number of gates per cell: "
            + (double) totalGates / numBlocks
            + Utils.getNewLine();
    return rtn;
  }

  private static final String S_HEADER = "--------------------------------------------";
}

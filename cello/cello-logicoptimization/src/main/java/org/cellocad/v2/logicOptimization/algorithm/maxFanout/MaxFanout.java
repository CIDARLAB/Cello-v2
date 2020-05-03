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

package org.cellocad.v2.logicOptimization.algorithm.maxFanout;

import java.util.ArrayList;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cellocad.v2.common.graph.algorithm.BFS;
import org.cellocad.v2.logicOptimization.algorithm.LOAlgorithm;
import org.cellocad.v2.logicOptimization.algorithm.maxFanout.data.MaxFanoutNetlistData;
import org.cellocad.v2.logicOptimization.algorithm.maxFanout.data.MaxFanoutNetlistEdgeData;
import org.cellocad.v2.logicOptimization.algorithm.maxFanout.data.MaxFanoutNetlistNodeData;
import org.cellocad.v2.results.logicSynthesis.LSResultsUtils;
import org.cellocad.v2.results.logicSynthesis.netlist.LSResultNetlistUtils;
import org.cellocad.v2.results.netlist.Netlist;
import org.cellocad.v2.results.netlist.NetlistEdge;
import org.cellocad.v2.results.netlist.NetlistNode;

/**
 * The implementation of the <i>maxFanout</i> algorithm in the <i>logicOptimization</i> stage.
 *
 * @author Vincent Mirian
 *
 * @date 2018-05-21
 */
public class MaxFanout extends LOAlgorithm {

  /**
   * Returns the {@link MaxFanoutNetlistNodeData} of the given node.
   *
   * @param node A node within the netlist of this instance.
   * @return The {@link MaxFanoutNetlistNodeData} instance if it exists, null otherwise.
   */
  protected MaxFanoutNetlistNodeData getMaxFanoutNetlistNodeData(final NetlistNode node) {
    MaxFanoutNetlistNodeData rtn = null;
    rtn = (MaxFanoutNetlistNodeData) node.getNetlistNodeData();
    return rtn;
  }

  /**
   * Returns the {@link MaxFanoutNetlistEdgeData} of the given edge.
   *
   * @param edge An edge within the netlist of this instance.
   * @return The {@link MaxFanoutNetlistEdgeData} instance if it exists, null otherwise.
   */
  protected MaxFanoutNetlistEdgeData getMaxFanoutNetlistEdgeData(final NetlistEdge edge) {
    MaxFanoutNetlistEdgeData rtn = null;
    rtn = (MaxFanoutNetlistEdgeData) edge.getNetlistEdgeData();
    return rtn;
  }

  /**
   * Returns the {@link MaxFanoutNetlistData} of the given netlist.
   *
   * @param netlist The netlist of this instance.
   * @return The {@link MaxFanoutNetlistData} instance if it exists, null otherwise.
   */
  protected MaxFanoutNetlistData getMaxFanoutNetlistData(final Netlist netlist) {
    MaxFanoutNetlistData rtn = null;
    rtn = (MaxFanoutNetlistData) netlist.getNetlistData();
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
    Boolean present = true;
    present = getAlgorithmProfile().getIntParameter("max").getFirst();
    if (present) {
      setmax(getAlgorithmProfile().getIntParameter("max").getSecond());
    }
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
    final Netlist netlist = getNetlist();
    boolean inserted = false;
    final int max = getmax();
    logInfo("Max fanout for node: " + max);
    do {
      final BFS<NetlistNode, NetlistEdge, Netlist> bfs = new BFS<>(netlist);
      NetlistNode node = null;
      inserted = false;
      LSResultNetlistUtils.setVertexTypeUsingLSResult(netlist);
      while ((node = bfs.getNextVertex()) != null) {
        if (LSResultsUtils.isAllInput(node) || LSResultsUtils.isAllOutput(node)) {
          continue;
        }
        int fanout = node.getNumOutEdge();
        if (fanout > max) {
          logInfo("---------------------------------");
          logInfo("Duplicating node " + node.getName());
          inserted = true;
          // minus one to keep original
          final int numToAdd = (int) Math.ceil((double) fanout / max) - 1;
          final int numEdgePerNode = fanout / (numToAdd + 1);
          for (int i = 0; i < numToAdd; i++) {
            final NetlistNode duplicate = new NetlistNode(node);
            duplicate.setName(duplicate.getName() + MaxFanout.S_DUPLICATE + i);
            logInfo("Duplicating node name " + duplicate.getName());
            // add in edges
            for (int j = 0; j < node.getNumInEdge(); j++) {
              final NetlistEdge edge = node.getInEdgeAtIdx(j);
              final NetlistNode src = edge.getSrc();
              // make new edge
              final NetlistEdge duplicateEdge = new NetlistEdge(edge);
              duplicateEdge.setName(duplicateEdge.getName() + MaxFanout.S_DUPLICATE);
              logInfo("Creating edge name " + duplicateEdge.getName());
              logInfo("Connecting to source node " + src.getName());
              logInfo("Connecting to destination node " + duplicate.getName());
              // set edge source
              duplicateEdge.setSrc(src);
              src.addOutEdge(duplicateEdge);
              // set edge destination
              duplicateEdge.setDst(duplicate);
              duplicate.addInEdge(duplicateEdge);
              // add edge to netlist
              netlist.addEdge(duplicateEdge);
            }
            final List<NetlistEdge> edgesToRemove = new ArrayList<>();
            edgesToRemove.clear();
            // add out edges
            for (int j = 0; j < fanout && j < numEdgePerNode; j++) {
              final NetlistEdge edge = node.getOutEdgeAtIdx(j);
              edgesToRemove.add(edge);
              // set edge source to duplicate node
              edge.setSrc(duplicate);
              duplicate.addOutEdge(edge);
              logInfo(
                  "Seeting source of edge name " + edge.getName() + " to " + duplicate.getName());
            }
            fanout -= numEdgePerNode;
            // remove out edges
            for (int j = 0; j < edgesToRemove.size(); j++) {
              final NetlistEdge edge = edgesToRemove.get(j);
              node.removeOutEdge(edge);
              logInfo("Removing edge " + edge.getName() + " from node " + node.getName());
            }
            // add node
            netlist.addVertex(duplicate);
          }
        }
      }
    } while (inserted);
    assert netlist.isValid();
    if (!netlist.isValid()) {
      throw new RuntimeException("Netlist not valid!");
    }
  }

  /**
   * Perform postprocessing.
   */
  @Override
  protected void postprocessing() {

  }

  /**
   * Setter for {@code max}.
   *
   * @param value The value to set {@code max}.
   */
  protected void setmax(final int value) {
    max = value;
  }

  /**
   * Getter for {@code max}.
   *
   * @return The value of {@code max}.
   */
  protected int getmax() {
    return max;
  }

  private int max;

  /**
   * Returns the {@link Logger} for the <i>maxFanout</i> algorithm.
   *
   * @return The {@link Logger} for the <i>maxFanout</i> algorithm.
   */
  @Override
  protected Logger getLogger() {
    return MaxFanout.logger;
  }

  private static final Logger logger = LogManager.getLogger(MaxFanout.class);

  private static final String S_DUPLICATE = "_Duplicate";

}

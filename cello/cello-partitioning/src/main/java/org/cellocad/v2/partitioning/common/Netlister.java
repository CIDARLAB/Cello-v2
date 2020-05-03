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

package org.cellocad.v2.partitioning.common;

import java.util.HashMap;
import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cellocad.v2.common.CObject;
import org.cellocad.v2.common.Utils;
import org.cellocad.v2.partitioning.netlist.PTNetlist;
import org.cellocad.v2.partitioning.netlist.PTNetlistEdge;
import org.cellocad.v2.partitioning.netlist.PTNetlistNode;
import org.cellocad.v2.results.netlist.Netlist;
import org.cellocad.v2.results.netlist.NetlistEdge;
import org.cellocad.v2.results.netlist.NetlistNode;

/**
 * A netlist generator for the <i>partitioning</i> stage.
 * 
 * @author Vincent Mirian
 *
 * @date Oct 27, 2017
 */
public class Netlister extends CObject {

  private void init() {
    setPTNetlist(new PTNetlist());
    netlistNodeMap = new HashMap<>();
    netlistEdgeMap = new HashMap<>();
    ptNetlistNodeMap = new HashMap<>();
    ptNetlistEdgeMap = new HashMap<>();
  }

  /**
   * Initializes a newly created {@link Netlister}.
   *
   * @param netlist A netlist.
   */
  public Netlister(final Netlist netlist) {
    Utils.isNullRuntimeException(netlist, "netlist");
    setNetlist(netlist);
    init();
    convertNetlistToPTNetlist();
  }

  private void convertNetlistToPTNetlist() {
    final PTNetlist ptnetlist = getPTNetlist();
    final Netlist netlist = getNetlist();
    final Map<String, NetlistNode> NetlistNodeMap = getNetlistNodeMap();
    final Map<String, NetlistEdge> NetlistEdgeMap = getNetlistEdgeMap();
    final Map<String, PTNetlistNode> PTNetlistNodeMap = getPTNetlistNodeMap();
    final Map<String, PTNetlistEdge> PTNetlistEdgeMap = getPTNetlistEdgeMap();
    final Map<NetlistNode, PTNetlistNode> NetlistToPTNetlistNode = new HashMap<>();
    final Map<NetlistEdge, PTNetlistEdge> NetlistToPTNetlistEdge = new HashMap<>();
    // convert Vertex to PNode
    for (int i = 0; i < netlist.getNumVertex(); i++) {
      final NetlistNode node = netlist.getVertexAtIdx(i);
      final PTNetlistNode ptnode = new PTNetlistNode();
      ptnode.setNetlistNode(node);
      final String name = node.getName();
      ptnode.setName(name);
      ptnode.setNodeType(node.getResultNetlistNodeData().getNodeType());
      NetlistToPTNetlistNode.put(node, ptnode);
      NetlistNodeMap.put(name, node);
      PTNetlistNodeMap.put(name, ptnode);
      ptnetlist.addVertex(ptnode);
    }
    // convert Edge to PEdge
    for (int i = 0; i < netlist.getNumEdge(); i++) {
      final NetlistEdge edge = netlist.getEdgeAtIdx(i);
      final PTNetlistEdge ptedge = new PTNetlistEdge();
      ptedge.setNetlistEdge(edge);
      final String name = edge.getName();
      ptedge.setName(name);
      NetlistToPTNetlistEdge.put(edge, ptedge);
      NetlistEdgeMap.put(name, edge);
      PTNetlistEdgeMap.put(name, ptedge);
      ptnetlist.addEdge(ptedge);
    }
    // for each PNode:
    for (int i = 0; i < netlist.getNumVertex(); i++) {
      final NetlistNode node = netlist.getVertexAtIdx(i);
      final PTNetlistNode ptnode = NetlistToPTNetlistNode.get(node);
      assert ptnode != null;
      // set outEdge for PNode
      for (int j = 0; j < node.getNumOutEdge(); j++) {
        final NetlistEdge edge = node.getOutEdgeAtIdx(j);
        final PTNetlistEdge ptedge = NetlistToPTNetlistEdge.get(edge);
        assert ptedge != null;
        ptnode.addOutEdge(ptedge);
      }
      // set inEdge for PNode
      for (int j = 0; j < node.getNumInEdge(); j++) {
        final NetlistEdge edge = node.getInEdgeAtIdx(j);
        final PTNetlistEdge ptedge = NetlistToPTNetlistEdge.get(edge);
        assert ptedge != null;
        ptnode.addInEdge(ptedge);
      }
    }
    // for each PEdge:
    for (int i = 0; i < netlist.getNumEdge(); i++) {
      final NetlistEdge edge = netlist.getEdgeAtIdx(i);
      final PTNetlistEdge ptedge = NetlistToPTNetlistEdge.get(edge);
      assert ptedge != null;
      // set src for PEdge
      {
        final NetlistNode node = edge.getSrc();
        final PTNetlistNode ptnode = NetlistToPTNetlistNode.get(node);
        assert ptnode != null;
        ptedge.setSrc(ptnode);
      }
      // set dst for PEdge
      {
        final NetlistNode node = edge.getDst();
        final PTNetlistNode ptnode = NetlistToPTNetlistNode.get(node);
        assert ptnode != null;
        ptedge.setDst(ptnode);
      }
    }
    // set name
    ptnetlist.setName(netlist.getName() + "_PTNetlist");
    assert ptnetlist.isValid();
  }

  protected void attachPidToNetlist() {
    final PTNetlist ptNetlist = getPTNetlist();
    final Map<String, NetlistNode> NetlistNodeMap = getNetlistNodeMap();
    for (int i = 0; i < ptNetlist.getNumVertex(); i++) {
      final PTNetlistNode ptnode = ptNetlist.getVertexAtIdx(i);
      final Block block = ptnode.getMyBlock();
      if (block != null) {
        final NetlistNode node = NetlistNodeMap.get(ptnode.getName());
        if (node != null) {
          final int pID = block.getIdx();
          getLogger().info("Setting partitionID of " + node.getName() + " to " + pID);
          node.getResultNetlistNodeData().setPartitionID(pID);
        } else {
          getLogger().info(ptnode.getName() + " does not have a NetlistNode!");
        }
      }
    }
  }

  protected void setNetlist(final Netlist netlist) {
    this.netlist = netlist;
  }

  public Netlist getNetlist() {
    attachPidToNetlist();
    return netlist;
  }

  protected void setPTNetlist(final PTNetlist ptNetlist) {
    this.ptNetlist = ptNetlist;
  }

  public PTNetlist getPTNetlist() {
    return ptNetlist;
  }

  protected Map<String, NetlistNode> getNetlistNodeMap() {
    return netlistNodeMap;
  }

  protected Map<String, NetlistEdge> getNetlistEdgeMap() {
    return netlistEdgeMap;
  }

  protected Map<String, PTNetlistNode> getPTNetlistNodeMap() {
    return ptNetlistNodeMap;
  }

  protected Map<String, PTNetlistEdge> getPTNetlistEdgeMap() {
    return ptNetlistEdgeMap;
  }

  private Netlist netlist;
  private PTNetlist ptNetlist;
  // S_MAP for conversion
  Map<String, NetlistNode> netlistNodeMap;
  Map<String, NetlistEdge> netlistEdgeMap;
  Map<String, PTNetlistNode> ptNetlistNodeMap;
  Map<String, PTNetlistEdge> ptNetlistEdgeMap;

  protected Logger getLogger() {
    return Netlister.logger;
  }

  private static final Logger logger = LogManager.getLogger(Netlister.class);

}

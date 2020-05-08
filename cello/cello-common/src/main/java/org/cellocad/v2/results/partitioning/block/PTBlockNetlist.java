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

package org.cellocad.v2.results.partitioning.block;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.cellocad.v2.common.CObjectCollection;
import org.cellocad.v2.results.logicSynthesis.LSResults;
import org.cellocad.v2.results.logicSynthesis.netlist.LSResultNetlistNodeUtils;
import org.cellocad.v2.results.netlist.Netlist;
import org.cellocad.v2.results.netlist.NetlistEdge;
import org.cellocad.v2.results.netlist.NetlistNode;
import org.cellocad.v2.results.netlist.data.ResultNetlistNodeData;
import org.cellocad.v2.results.partitioning.PTResults;

/**
 * The PTResults class is class containing the results assigned by the <i>partitioning</i> stage.
 *
 * @author Vincent Mirian
 * @date 2018-05-21
 */
public class PTBlockNetlist {

  private void init() {
    netlists = new ArrayList<>();
    netlistsFO = new ArrayList<>();
    virtualLargeNetlist = new Netlist();
    virtualLargeNetlistFO = new Netlist();
    clusterNetlist = new Netlist();
    clusterRepeatedEdgesNetlist = new Netlist();
    partitionIDNetlistNodeMap = new HashMap<>();
  }

  private void clear() {
    getNetlists().clear();
    getVirtualLargeNetlist().clear();
    getClusterNetlist().clear();
    getPartitionIDNetlistNodeMap().clear();
  }

  private void separateByPID() {
    final Map<Integer, List<NetlistNode>> pidNodeMap = getPartitionIDNetlistNodeMap();
    final Netlist netlist = getNetlist();
    NetlistNode node = null;
    List<NetlistNode> nodeList = null;
    int pid = PTResults.S_DEFAULT;
    for (int i = 0; i < netlist.getNumVertex(); i++) {
      node = netlist.getVertexAtIdx(i);
      pid = node.getResultNetlistNodeData().getPartitionID();
      if (pid < 0) {
        pid = PTResults.S_DEFAULT;
      }
      nodeList = pidNodeMap.get(pid);
      if (nodeList == null) {
        nodeList = new ArrayList<>();
        pidNodeMap.put(pid, nodeList);
      }
      nodeList.add(node);
    }
  }

  private String getEdgeName(
      final NetlistEdge edge, final NetlistNode srcNode, final NetlistNode dstNode) {
    String rtn = "";
    rtn += srcNode.getName();
    rtn += PTBlockNetlist.S_UNDERSCORE;
    rtn += PTBlockNetlist.S_TO;
    rtn += PTBlockNetlist.S_UNDERSCORE;
    rtn += dstNode.getName();
    rtn += PTBlockNetlist.S_UNDERSCORE;
    rtn += PTBlockNetlist.S_FROM;
    rtn += PTBlockNetlist.S_UNDERSCORE;
    rtn += edge.getName();
    return rtn;
  }

  private String getVINodeName(
      final NetlistEdge edge, final NetlistNode srcNode, final NetlistNode dstNode) {
    String rtn = "";
    rtn += PTBlockNetlist.S_INPUT;
    rtn += PTBlockNetlist.S_UNDERSCORE;
    rtn += srcNode.getName();
    rtn += PTBlockNetlist.S_UNDERSCORE;
    rtn += PTBlockNetlist.S_TO;
    rtn += PTBlockNetlist.S_UNDERSCORE;
    rtn += dstNode.getName();
    return rtn;
  }

  private String getVONodeName(
      final NetlistEdge edge, final NetlistNode srcNode, final NetlistNode dstNode) {
    String rtn = "";
    rtn += PTBlockNetlist.S_OUTPUT;
    rtn += PTBlockNetlist.S_UNDERSCORE;
    rtn += srcNode.getName();
    rtn += PTBlockNetlist.S_UNDERSCORE;
    rtn += PTBlockNetlist.S_TO;
    rtn += PTBlockNetlist.S_UNDERSCORE;
    rtn += dstNode.getName();
    return rtn;
  }

  private String getVINodeNameFO(final NetlistNode node) {
    String rtn = "";
    rtn += PTBlockNetlist.S_INPUT;
    rtn += PTBlockNetlist.S_UNDERSCORE;
    rtn += node.getName();
    return rtn;
  }

  private String getVONodeNameFO(final NetlistNode node) {
    String rtn = "";
    rtn += PTBlockNetlist.S_OUTPUT;
    rtn += PTBlockNetlist.S_UNDERSCORE;
    rtn += node.getName();
    return rtn;
  }

  /**
   * Get the matching virtual I/O node name.
   *
   * @param name The original name.
   * @return The matching virtual I/O node name.
   */
  public static String getMatchingVioNodeName(final String name) {
    String rtn = "";
    rtn += name;
    if (name.startsWith(PTBlockNetlist.S_OUTPUT)) {
      rtn = name.replaceFirst(PTBlockNetlist.S_OUTPUT, PTBlockNetlist.S_INPUT);
    }
    if (name.startsWith(PTBlockNetlist.S_INPUT)) {
      rtn = name.replaceFirst(PTBlockNetlist.S_INPUT, PTBlockNetlist.S_OUTPUT);
    }
    return rtn;
  }

  private void computeVirtualLargeNetlist() {
    final Netlist vlNetlist = new Netlist(getNetlist());
    vlNetlist.setName(getNetlist().getName() + "VirtualNetlist");
    final List<NetlistEdge> edgesToRemove = new ArrayList<>();
    final List<NetlistEdge> edgesToAdd = new ArrayList<>();
    /*
     * make virtual node for edges that cross blocks, except for edges with nodes not in a valid
     * block
     */
    int srcPID = PTResults.S_DEFAULT;
    int dstPID = PTResults.S_DEFAULT;
    NetlistNode srcnode = null;
    NetlistNode dstnode = null;
    NetlistEdge edge = null;
    NetlistNode vnode = null;
    NetlistEdge vedge = null;
    for (int i = 0; i < vlNetlist.getNumEdge(); i++) {
      edge = vlNetlist.getEdgeAtIdx(i);
      srcnode = edge.getSrc();
      dstnode = edge.getDst();
      srcPID = srcnode.getResultNetlistNodeData().getPartitionID();
      dstPID = dstnode.getResultNetlistNodeData().getPartitionID();
      // skip if edge is not crossing
      // or edges with nodes not in a valid block
      if (srcPID == dstPID || srcPID < 0 || dstPID < 0) {
        continue;
      }
      // src to block
      // virtual output
      vnode = new NetlistNode();
      vnode.setName(getVONodeName(edge, srcnode, dstnode));
      vnode.getResultNetlistNodeData().setNodeType(LSResults.S_OUTPUT);
      vlNetlist.addVertex(vnode);
      // new edge
      vedge = new NetlistEdge(srcnode, vnode);
      vedge.setName(getEdgeName(edge, srcnode, vnode));
      edgesToAdd.add(vedge);
      srcnode.addOutEdge(vedge);
      vnode.addInEdge(vedge);
      // block to dst
      // virtual input
      vnode = new NetlistNode();
      vnode.setName(getVINodeName(edge, srcnode, dstnode));
      vnode.getResultNetlistNodeData().setNodeType(LSResults.S_INPUT);
      vlNetlist.addVertex(vnode);
      // new edge
      vedge = new NetlistEdge(vnode, dstnode);
      vedge.setName(getEdgeName(edge, vnode, dstnode));
      edgesToAdd.add(vedge);
      vnode.addOutEdge(vedge);
      dstnode.addInEdge(vedge);
      // remove edge
      srcnode.removeOutEdge(edge);
      dstnode.removeInEdge(edge);
      edgesToRemove.add(edge);
    }
    // remove
    for (int i = 0; i < edgesToRemove.size(); i++) {
      vlNetlist.removeEdge(edgesToRemove.get(i));
    }
    // add
    for (int i = 0; i < edgesToAdd.size(); i++) {
      vlNetlist.addEdge(edgesToAdd.get(i));
    }
    setVirtualLargeNetlist(vlNetlist);
    assert vlNetlist.isValid();
  }

  private void computeVirtualLargeNetlistFO() {
    final Netlist vlNetlist = new Netlist(getNetlist());
    vlNetlist.setName(getNetlist().getName() + "VirtualNetlistFO");
    final List<NetlistEdge> edgesToRemove = new ArrayList<>();
    final List<NetlistEdge> edgesToAdd = new ArrayList<>();
    /*
     * make virtual node for edges that cross blocks, except for edges with nodes not in a valid
     * block
     */
    int srcPID = PTResults.S_DEFAULT;
    int dstPID = PTResults.S_DEFAULT;
    NetlistNode srcnode = null;
    NetlistNode dstnode = null;
    NetlistEdge edge = null;
    NetlistNode vnode = null;
    NetlistEdge vedge = null;
    String nodeName;
    for (int i = 0; i < vlNetlist.getNumEdge(); i++) {
      edge = vlNetlist.getEdgeAtIdx(i);
      srcnode = edge.getSrc();
      dstnode = edge.getDst();
      srcPID = srcnode.getResultNetlistNodeData().getPartitionID();
      dstPID = dstnode.getResultNetlistNodeData().getPartitionID();
      // skip if edge is not crossing
      // or edges with nodes not in a valid block
      if (srcPID == dstPID || srcPID < 0 || dstPID < 0) {
        continue;
      }
      // src to block
      // virtual output
      nodeName = getVONodeNameFO(srcnode);
      vnode = vlNetlist.getVertexByName(nodeName);
      if (vnode == null) {
        vnode = new NetlistNode();
        vnode.setName(nodeName);
        vnode.getResultNetlistNodeData().setNodeType(LSResults.S_OUTPUT);
        vlNetlist.addVertex(vnode);
        // new edge
        vedge = new NetlistEdge(srcnode, vnode);
        vedge.setName(getEdgeName(edge, srcnode, vnode));
        edgesToAdd.add(vedge);
        srcnode.addOutEdge(vedge);
        vnode.addInEdge(vedge);
      }
      // block to dst
      // virtual input
      nodeName = getVINodeNameFO(srcnode);
      vnode = vlNetlist.getVertexByName(nodeName);
      if (vnode == null) {
        vnode = new NetlistNode();
        vnode.setName(nodeName);
        vnode.getResultNetlistNodeData().setNodeType(LSResults.S_INPUT);
        vlNetlist.addVertex(vnode);
      }
      // new edge
      vedge = new NetlistEdge(vnode, dstnode);
      vedge.setName(getEdgeName(edge, vnode, dstnode));
      edgesToAdd.add(vedge);
      vnode.addOutEdge(vedge);
      dstnode.addInEdge(vedge);
      // remove edge
      srcnode.removeOutEdge(edge);
      dstnode.removeInEdge(edge);
      edgesToRemove.add(edge);
    }
    // remove
    for (int i = 0; i < edgesToRemove.size(); i++) {
      vlNetlist.removeEdge(edgesToRemove.get(i));
    }
    // add
    for (int i = 0; i < edgesToAdd.size(); i++) {
      vlNetlist.addEdge(edgesToAdd.get(i));
    }
    setVirtualLargeNetlistFO(vlNetlist);
    assert vlNetlist.isValid();
  }

  @SuppressWarnings("unused")
  private void computeVirtualLargeNetlistFO_Other() {
    final Netlist vlNetlist = new Netlist(getVirtualLargeNetlist());
    vlNetlist.setName(getNetlist().getName() + "VirtualNetlistFO");
    final List<NetlistEdge> edgesToRemove = new ArrayList<>();
    final List<NetlistEdge> edgesToAdd = new ArrayList<>();
    final List<NetlistNode> nodesToRemove = new ArrayList<>();
    final List<NetlistNode> nodesToAdd = new ArrayList<>();
    /*
     * collapse Output into one node and Input Nodes into one node
     */
    NetlistNode srcnode = null;
    NetlistNode dstnode = null;
    NetlistNode node = null;
    NetlistNode vnode = null;
    NetlistEdge vedge = null;
    // for all nodes get output nodes
    for (int i = 0; i < vlNetlist.getNumVertex(); i++) {
      node = vlNetlist.getVertexAtIdx(i);
      final CObjectCollection<NetlistNode> myOutput = LSResultNetlistNodeUtils.getMyOutput(node);
      final String OutputNode = getVONodeNameFO(node);
      final String InputNode = getVINodeNameFO(node);
      // create node
      if (myOutput.size() > 0) {
        // Output
        // node
        vnode = new NetlistNode();
        nodesToAdd.add(vnode);
        vnode.setName(OutputNode);
        vnode.getResultNetlistNodeData().setNodeType(LSResults.S_OUTPUT);
        // edge
        vedge = new NetlistEdge();
        edgesToAdd.add(vedge);
        vedge.setName(getVONodeName(null, node, vnode));
        vnode.addInEdge(vedge);
        node.addOutEdge(vedge);
        vedge.setDst(vnode);
        vedge.setSrc(node);
        // Input
        // node
        srcnode = new NetlistNode();
        nodesToAdd.add(srcnode);
        srcnode.setName(InputNode);
        srcnode.getResultNetlistNodeData().setNodeType(LSResults.S_INPUT);
      }
      // remove from output
      for (int j = 0; j < myOutput.size(); j++) {
        // remove output node
        vnode = myOutput.get(j);
        nodesToRemove.add(vnode);
        // remove output edge
        vedge = vnode.getInEdgeAtIdx(0);
        dstnode = vedge.getSrc();
        dstnode.removeOutEdge(vedge);
        edgesToRemove.add(vedge);
        // connect input node
        vnode = vlNetlist.getVertexByName(PTBlockNetlist.getMatchingVioNodeName(vnode.getName()));
        nodesToRemove.add(vnode);
        vedge = vnode.getOutEdgeAtIdx(0);
        edgesToRemove.add(vedge);
        dstnode = vedge.getDst();
        dstnode.removeInEdge(vedge);
        // make connection (input)
        vedge = new NetlistEdge();
        edgesToAdd.add(vedge);
        vedge.setName(getVINodeName(null, node, vnode));
        srcnode.addOutEdge(vedge);
        dstnode.addInEdge(vedge);
        vedge.setSrc(srcnode);
        vedge.setDst(dstnode);
      }
    }
    // remove
    for (int i = 0; i < edgesToRemove.size(); i++) {
      vlNetlist.removeEdge(edgesToRemove.get(i));
    }
    for (int i = 0; i < nodesToRemove.size(); i++) {
      vlNetlist.removeVertex(nodesToRemove.get(i));
    }
    // add
    for (int i = 0; i < edgesToAdd.size(); i++) {
      vlNetlist.addEdge(edgesToAdd.get(i));
    }
    for (int i = 0; i < nodesToAdd.size(); i++) {
      vlNetlist.addVertex(nodesToAdd.get(i));
    }
    setVirtualLargeNetlistFO(vlNetlist);
    assert vlNetlist.isValid();
  }

  private String getBlockName(final int pid) {
    String rtn = "";
    rtn += PTBlockNetlist.S_BLOCKNAMEPREFIX;
    rtn += pid;
    return rtn;
  }

  // TODO: verify and test
  private void computeClusterNetlist(final boolean repeat) {
    final Netlist netlist = getNetlist();
    Netlist cnetlist = null;
    if (repeat) {
      cnetlist = getClusterRepeatedEdgesNetlist();
      cnetlist.setName(netlist.getName() + "_PTClusterRepeatedEdgesNetlist");
    } else {
      cnetlist = getClusterNetlist();
      cnetlist.setName(netlist.getName() + "_PTClusterNetlist");
    }
    final Map<Integer, List<NetlistNode>> pidMap = getPartitionIDNetlistNodeMap();
    // key = block id dest
    // value = node input to block id
    final Multimap<Integer, NetlistNode> dstBlockIDSrcNodeMap = ArrayListMultimap.create();
    NetlistNode srcnode = null;
    NetlistNode dstnode = null;
    int srcPID = PTResults.S_DEFAULT;
    int dstPID = PTResults.S_DEFAULT;
    List<NetlistNode> nodeList = null;
    // using iterators
    Iterator<Map.Entry<Integer, List<NetlistNode>>> itr = null;
    // make nodes
    itr = pidMap.entrySet().iterator();
    while (itr.hasNext()) {
      // create new node for all blocks
      final Map.Entry<Integer, List<NetlistNode>> entry = itr.next();
      srcPID = entry.getKey();
      if (srcPID < 0) {
        nodeList = entry.getValue();
        for (int i = 0; i < nodeList.size(); i++) {
          srcnode = nodeList.get(i);
          dstnode = new NetlistNode();
          dstnode.setName(srcnode.getName());
          dstnode.setResultNetlistNodeData(
              new ResultNetlistNodeData(srcnode.getResultNetlistNodeData()));
          cnetlist.addVertex(dstnode);
        }
      } else {
        srcnode = new NetlistNode();
        srcnode.setName(getBlockName(srcPID));
        srcnode.getResultNetlistNodeData().setPartitionID(srcPID);
        cnetlist.addVertex(srcnode);
      }
    }
    // make connections
    NetlistNode pidSrcNode = null;
    NetlistNode pidDstNode = null;
    itr = pidMap.entrySet().iterator();
    while (itr.hasNext()) {
      final Map.Entry<Integer, List<NetlistNode>> entry = itr.next();
      srcPID = entry.getKey();
      nodeList = entry.getValue();
      for (int i = 0; i < nodeList.size(); i++) {
        srcnode = nodeList.get(i);
        for (int j = 0; j < srcnode.getNumOutEdge(); j++) {
          final NetlistEdge edge = srcnode.getOutEdgeAtIdx(j);
          dstnode = edge.getDst();
          dstPID = dstnode.getResultNetlistNodeData().getPartitionID();
          // no self edge and unpartitioned nodes
          if (dstPID == srcPID) {
            continue;
          }
          if (srcPID < 0) {
            pidSrcNode = cnetlist.getVertexByName(srcnode.getName());
          } else {
            pidSrcNode = cnetlist.getVertexByName(getBlockName(srcPID));
          }
          if (dstPID < 0) {
            pidDstNode = cnetlist.getVertexByName(dstnode.getName());
          } else {
            pidDstNode = cnetlist.getVertexByName(getBlockName(dstPID));
          }
          // if do not allow repeated edges
          if (!repeat) {
            // skip connections already done
            if (dstBlockIDSrcNodeMap.get(dstPID).contains(srcnode)) {
              continue;
            }
            // add
            dstBlockIDSrcNodeMap.put(dstPID, srcnode);
          }
          final NetlistEdge myedge = new NetlistEdge(pidSrcNode, pidDstNode);
          myedge.setName(getEdgeName(edge, pidSrcNode, pidDstNode));
          cnetlist.addEdge(myedge);
          pidSrcNode.addOutEdge(myedge);
          pidDstNode.addInEdge(myedge);
        }
      }
    }
    assert cnetlist.isValid();
  }

  /*
   * private void computeClusterNetlistNew () { Netlist netlist = this.getNetlist(); Netlist
   * cnetlist = null; if (repeat) { cnetlist = this.getClusterRepeatedEdgesNetlist();
   * cnetlist.setName(netlist.getName() + "_PTClusterRepeatedEdgesNetlist"); } else { cnetlist =
   * this.getClusterNetlist(); cnetlist.setName(netlist.getName() + "_PTClusterNetlist"); }
   * Map<Integer, List<NetlistNode>> pidMap = this.getPartitionIDNetlistNodeMap(); // key = block id
   * dest // value = node input to block id Multimap<Integer, NetlistNode> dstBlockIDSrcNodeMap =
   * ArrayListMultimap.create(); NetlistNode srcnode = null; NetlistNode dstnode = null; int srcPID
   * = PTResults.S_DEFAULT; int dstPID = PTResults.S_DEFAULT; List<NetlistNode> nodeList = null; //
   * using iterators Iterator<Map.Entry<Integer, List<NetlistNode>>> itr = null; // make nodes itr =
   * pidMap.entrySet().iterator(); while(itr.hasNext()){ // create new node for all blocks
   * Map.Entry<Integer, List<NetlistNode>> entry = itr.next(); srcPID = entry.getKey(); if (srcPID <
   * 0) { nodeList = entry.getValue(); for (int i = 0; i < nodeList.size(); i++) { srcnode =
   * nodeList.get(i); dstnode = new NetlistNode(); dstnode.setName(srcnode.getName());
   * dstnode.setResultNetlistNodeData(new
   * ResultNetlistNodeData(srcnode.getResultNetlistNodeData())); cnetlist.addVertex(dstnode); } }
   * else { srcnode = new NetlistNode(); srcnode.setName(this.getBlockName(srcPID));
   * srcnode.getResultNetlistNodeData().setPartitionID(srcPID); cnetlist.addVertex(srcnode); } } //
   * make connections NetlistNode pidSrcNode = null; NetlistNode pidDstNode = null; itr =
   * pidMap.entrySet().iterator(); while(itr.hasNext()){ Map.Entry<Integer, List<NetlistNode>> entry
   * = itr.next(); srcPID = entry.getKey(); nodeList = entry.getValue(); for (int i = 0; i <
   * nodeList.size(); i++) { srcnode = nodeList.get(i); for (int j = 0; j < srcnode.getNumOutEdge();
   * j++) { NetlistEdge edge = srcnode.getOutEdgeAtIdx(j); dstnode = edge.getDst(); dstPID =
   * dstnode.getResultNetlistNodeData().getPartitionID(); // no self edge and unpartitioned nodes if
   * ((dstPID == srcPID)) { continue; } if (srcPID < 0) { pidSrcNode =
   * cnetlist.getVertexByName(srcnode.getName()); } else { pidSrcNode =
   * cnetlist.getVertexByName(this.getBlockName(srcPID)); } if (dstPID < 0) { pidDstNode =
   * cnetlist.getVertexByName(dstnode.getName()); } else { pidDstNode =
   * cnetlist.getVertexByName(this.getBlockName(dstPID)); } // if do not allow repeated edges if
   * (!repeat) { // skip connections already done if
   * (dstBlockIDSrcNodeMap.get(dstPID).contains(srcnode)) { continue; } // add
   * dstBlockIDSrcNodeMap.put(dstPID, srcnode); } NetlistEdge myedge = new NetlistEdge(pidSrcNode,
   * pidDstNode); myedge.setName(this.getEdgeName(edge, pidSrcNode, pidDstNode));
   * cnetlist.addEdge(myedge); pidSrcNode.addOutEdge(myedge); pidDstNode.addInEdge(myedge); } } }
   * assert(cnetlist.isValid()); }
   */
  private void computeNetlists() {
    final Map<Integer, List<NetlistNode>> pidMap = getPartitionIDNetlistNodeMap();
    final List<Netlist> netlists = getNetlists();
    final List<NetlistEdge> edgesToRemove = new ArrayList<>();
    final List<NetlistNode> nodesToRemove = new ArrayList<>();
    NetlistEdge edge = null;
    Netlist ptNetlist = null;
    int pid = PTResults.S_DEFAULT;
    NetlistNode srcnode = null;
    NetlistNode dstnode = null;
    int srcPID = PTResults.S_DEFAULT;
    int dstPID = PTResults.S_DEFAULT;
    // using iterators
    Iterator<Map.Entry<Integer, List<NetlistNode>>> itr = null;
    // make nodes
    itr = pidMap.entrySet().iterator();
    while (itr.hasNext()) {
      final Map.Entry<Integer, List<NetlistNode>> entry = itr.next();
      pid = entry.getKey();
      if (pid < 0) {
        continue;
      }
      ptNetlist = new Netlist(getVirtualLargeNetlist());
      ptNetlist.setName(getNetlist().getName() + pid);
      edgesToRemove.clear();
      nodesToRemove.clear();
      // remove all nodes/edges not in pid
      // or that it is not connected
      for (int i = 0; i < ptNetlist.getNumEdge(); i++) {
        edge = ptNetlist.getEdgeAtIdx(i);
        srcnode = edge.getSrc();
        dstnode = edge.getDst();
        srcPID = srcnode.getResultNetlistNodeData().getPartitionID();
        dstPID = dstnode.getResultNetlistNodeData().getPartitionID();
        if (srcPID >= 0 && srcPID != pid || dstPID >= 0 && dstPID != pid) {
          srcnode.removeOutEdge(edge);
          dstnode.removeInEdge(edge);
          edgesToRemove.add(edge);
        }
      }
      // remove
      for (int i = 0; i < edgesToRemove.size(); i++) {
        ptNetlist.removeEdge(edgesToRemove.get(i));
      }
      // nodes (remove unconnected nodes)
      for (int i = 0; i < ptNetlist.getNumVertex(); i++) {
        final NetlistNode node = ptNetlist.getVertexAtIdx(i);
        if (node.getNumOutEdge() == 0 && node.getNumInEdge() == 0) {
          nodesToRemove.add(node);
        }
      }
      // remove
      for (int i = 0; i < nodesToRemove.size(); i++) {
        ptNetlist.removeVertex(nodesToRemove.get(i));
      }
      // add
      assert ptNetlist.isValid();
      netlists.add(ptNetlist);
    }
  }

  private void computeNetlistsFO() {
    final Map<Integer, List<NetlistNode>> pidMap = getPartitionIDNetlistNodeMap();
    final List<Netlist> netlists = getNetlistsFO();
    final List<NetlistEdge> edgesToRemove = new ArrayList<>();
    final List<NetlistNode> nodesToRemove = new ArrayList<>();
    NetlistEdge edge = null;
    Netlist ptNetlist = null;
    int pid = PTResults.S_DEFAULT;
    NetlistNode srcnode = null;
    NetlistNode dstnode = null;
    int srcPID = PTResults.S_DEFAULT;
    int dstPID = PTResults.S_DEFAULT;
    // using iterators
    Iterator<Map.Entry<Integer, List<NetlistNode>>> itr = null;
    // make nodes
    itr = pidMap.entrySet().iterator();
    while (itr.hasNext()) {
      final Map.Entry<Integer, List<NetlistNode>> entry = itr.next();
      pid = entry.getKey();
      if (pid < 0) {
        continue;
      }
      ptNetlist = new Netlist(getVirtualLargeNetlistFO());
      ptNetlist.setName(getNetlist().getName() + pid);
      edgesToRemove.clear();
      nodesToRemove.clear();
      // remove all nodes/edges not in pid
      // or that it is not connected
      for (int i = 0; i < ptNetlist.getNumEdge(); i++) {
        edge = ptNetlist.getEdgeAtIdx(i);
        srcnode = edge.getSrc();
        dstnode = edge.getDst();
        srcPID = srcnode.getResultNetlistNodeData().getPartitionID();
        dstPID = dstnode.getResultNetlistNodeData().getPartitionID();
        if (srcPID >= 0 && srcPID != pid || dstPID >= 0 && dstPID != pid) {
          srcnode.removeOutEdge(edge);
          dstnode.removeInEdge(edge);
          edgesToRemove.add(edge);
        }
      }
      // remove
      for (int i = 0; i < edgesToRemove.size(); i++) {
        ptNetlist.removeEdge(edgesToRemove.get(i));
      }
      // nodes (remove unconnected nodes)
      for (int i = 0; i < ptNetlist.getNumVertex(); i++) {
        final NetlistNode node = ptNetlist.getVertexAtIdx(i);
        if (node.getNumOutEdge() == 0 && node.getNumInEdge() == 0) {
          nodesToRemove.add(node);
        }
      }
      // remove
      for (int i = 0; i < nodesToRemove.size(); i++) {
        ptNetlist.removeVertex(nodesToRemove.get(i));
      }
      // add
      assert ptNetlist.isValid();
      netlists.add(ptNetlist);
    }
  }

  public PTBlockNetlist(final Netlist netlist) {
    init();
    setNetlist(netlist);
  }

  /*
   * Netlist
   */
  public Netlist getNetlist() {
    return netlist;
  }

  /**
   * Sets the netlist of this instance.
   *
   * @param netlist The netlist.
   */
  public void setNetlist(final Netlist netlist) {
    this.netlist = netlist;
    clear();
    separateByPID();
    computeVirtualLargeNetlist();
    computeVirtualLargeNetlistFO();
    computeClusterNetlist(false);
    computeClusterNetlist(true);
    computeNetlists();
    computeNetlistsFO();
  }

  private Netlist netlist;

  /*
   * VirtualLargeNetlistFO
   */
  private void setVirtualLargeNetlistFO(final Netlist netlist) {
    virtualLargeNetlistFO = netlist;
  }

  public Netlist getVirtualLargeNetlistFO() {
    return virtualLargeNetlistFO;
  }

  private Netlist virtualLargeNetlistFO;

  /*
   * VirtualLargeNetlist
   */
  private void setVirtualLargeNetlist(final Netlist netlist) {
    virtualLargeNetlist = netlist;
  }

  public Netlist getVirtualLargeNetlist() {
    return virtualLargeNetlist;
  }

  private Netlist virtualLargeNetlist;

  /*
   * ClusterNetlist
   */
  public Netlist getClusterNetlist() {
    return clusterNetlist;
  }

  private Netlist clusterNetlist;

  /*
   * ClusterRepeatedEdgesNetlist
   */
  public Netlist getClusterRepeatedEdgesNetlist() {
    return clusterRepeatedEdgesNetlist;
  }

  private Netlist clusterRepeatedEdgesNetlist;

  /*
   * ClusterRepeatedEdgesNetlistNew
   */
  /*
   * public Netlist getClusterRepeatedEdgesNetlistNew(){ return clusterRepeatedEdgesNetlistNew; }
   *
   * private hyperNetlist clusterRepeatedEdgesNetlistNew;
   */
  /*
   * Netlists
   */
  private List<Netlist> getNetlists() {
    return netlists;
  }

  private List<Netlist> netlists;

  /**
   * Get the netlist at the specified index.
   *
   * @param index An index.
   * @return The netlist at the specified index.
   */
  public Netlist getNetlistAtIdx(final int index) {
    Netlist rtn = null;
    if (0 <= index && index < getNetlistNum()) {
      rtn = getNetlists().get(index);
    }
    return rtn;
  }

  public int getNetlistNum() {
    return getNetlists().size();
  }

  /*
   * NetlistsFO
   */
  private List<Netlist> getNetlistsFO() {
    return netlistsFO;
  }

  private List<Netlist> netlistsFO;

  /**
   * Get the netlist FO at the given index.
   *
   * @param index An index.
   * @return The netlist FO at the given index.
   */
  public Netlist getNetlistFOAtIdx(final int index) {
    Netlist rtn = null;
    if (0 <= index && index < getNetlistFONum()) {
      rtn = getNetlistsFO().get(index);
    }
    return rtn;
  }

  public int getNetlistFONum() {
    return getNetlistsFO().size();
  }

  /*
   * PartitionIDNetlistNode
   */
  private Map<Integer, List<NetlistNode>> getPartitionIDNetlistNodeMap() {
    return partitionIDNetlistNodeMap;
  }

  private Map<Integer, List<NetlistNode>> partitionIDNetlistNodeMap;

  private static String S_BLOCKNAMEPREFIX = "block";
  private static String S_UNDERSCORE = "_";
  private static String S_OUTPUT = "OUTPUT";
  private static String S_INPUT = "INPUT";
  private static String S_TO = "TO";
  private static String S_FROM = "FROM";
}

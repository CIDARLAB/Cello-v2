/**
 * Copyright (C) 2017 Massachusetts Institute of Technology (MIT)
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:

 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.

 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package org.cellocad.v2.results.partitioning.block;

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

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

/**
 * The PTResults class is class containing the results assigned by the <i>partitioning</i> stage.
 * 
 * @author Vincent Mirian
 * 
 * @date 2018-05-21
 *
 */
public class PTBlockNetlist {

	private void init () {
		this.netlists = new ArrayList<Netlist>();
		this.netlistsFO = new ArrayList<Netlist>();
		this.virtualLargeNetlist = new Netlist();
		this.virtualLargeNetlistFO = new Netlist();
		this.clusterNetlist = new Netlist();
		this.clusterRepeatedEdgesNetlist = new Netlist();
		this.partitionIDNetlistNodeMap = new HashMap<Integer, List<NetlistNode>>();
	}
	
	private void clear () {
		this.getNetlists().clear();
		this.getVirtualLargeNetlist().clear();
		this.getClusterNetlist().clear();
		this.getPartitionIDNetlistNodeMap().clear();
	}

	private void separateByPID () {
		Map<Integer, List<NetlistNode>> pidNodeMap = this.getPartitionIDNetlistNodeMap();
		Netlist netlist = this.getNetlist();
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
				nodeList = new ArrayList<NetlistNode>();
				pidNodeMap.put(pid, nodeList);
			}
			nodeList.add(node);
		}
	}

	private String getEdgeName(NetlistEdge edge, NetlistNode srcNode, NetlistNode dstNode) {
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

	private String getVINodeName(NetlistEdge edge, NetlistNode srcNode, NetlistNode dstNode) {
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

	private String getVONodeName(NetlistEdge edge, NetlistNode srcNode, NetlistNode dstNode) {
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

	private String getVINodeNameFO(NetlistNode node) {
		String rtn = "";
		rtn += PTBlockNetlist.S_INPUT;
		rtn += PTBlockNetlist.S_UNDERSCORE;
		rtn += node.getName();
		return rtn;
	}

	private String getVONodeNameFO(NetlistNode node) {
		String rtn = "";
		rtn += PTBlockNetlist.S_OUTPUT;
		rtn += PTBlockNetlist.S_UNDERSCORE;
		rtn += node.getName();
		return rtn;
	}
	
	public static String getMatchingVIONodeName(String name) {
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

	private void computeVirtualLargeNetlist () {
		Netlist vlNetlist = new Netlist(this.getNetlist());
		vlNetlist.setName(this.getNetlist().getName() + "VirtualNetlist");
		List<NetlistEdge> edgesToRemove = new ArrayList<NetlistEdge>();
		List<NetlistEdge> edgesToAdd = new ArrayList<NetlistEdge>();
		/*
		 * make virtual node for edges that cross blocks,
		 * except for edges with nodes not in a valid block
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
			if (
					(srcPID == dstPID) ||
					(srcPID < 0) ||
					(dstPID < 0)
					)
					{
						continue;
			}
			// src to block
			// virtual output
			vnode = new NetlistNode();
			vnode.setName(this.getVONodeName(edge, srcnode, dstnode));
			vnode.getResultNetlistNodeData().setNodeType(LSResults.S_OUTPUT);
			vlNetlist.addVertex(vnode);
			// new edge
			vedge = new NetlistEdge(srcnode, vnode);
			vedge.setName(this.getEdgeName(edge, srcnode, vnode));
			edgesToAdd.add(vedge);
			srcnode.addOutEdge(vedge);
			vnode.addInEdge(vedge);
			// block to dst
			// virtual input
			vnode = new NetlistNode();
			vnode.setName(this.getVINodeName(edge, srcnode, dstnode));
			vnode.getResultNetlistNodeData().setNodeType(LSResults.S_INPUT);
			vlNetlist.addVertex(vnode);
			// new edge
			vedge = new NetlistEdge(vnode, dstnode);
			vedge.setName(this.getEdgeName(edge, vnode, dstnode));
			edgesToAdd.add(vedge);
			vnode.addOutEdge(vedge);
			dstnode.addInEdge(vedge);
			// remove edge
			srcnode.removeOutEdge(edge);
			dstnode.removeInEdge(edge);
			edgesToRemove.add(edge);
		}
		// remove
		for(int i = 0; i < edgesToRemove.size(); i++) {
			vlNetlist.removeEdge(edgesToRemove.get(i));
		}
		// add
		for(int i = 0; i < edgesToAdd.size(); i++) {
			vlNetlist.addEdge(edgesToAdd.get(i));
		}
		this.setVirtualLargeNetlist(vlNetlist);
		assert(vlNetlist.isValid());
	}

	private void computeVirtualLargeNetlistFO() {
		Netlist vlNetlist = new Netlist(this.getNetlist());
		vlNetlist.setName(this.getNetlist().getName() + "VirtualNetlistFO");
		List<NetlistEdge> edgesToRemove = new ArrayList<NetlistEdge>();
		List<NetlistEdge> edgesToAdd = new ArrayList<NetlistEdge>();
		/*
		 * make virtual node for edges that cross blocks,
		 * except for edges with nodes not in a valid block
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
			if (
					(srcPID == dstPID) ||
					(srcPID < 0) ||
					(dstPID < 0)
					)
					{
						continue;
			}
			// src to block
			// virtual output
			nodeName = this.getVONodeNameFO(srcnode);
			vnode = vlNetlist.getVertexByName(nodeName);
			if (vnode == null) {
				vnode = new NetlistNode();
				vnode.setName(nodeName);
				vnode.getResultNetlistNodeData().setNodeType(LSResults.S_OUTPUT);
				vlNetlist.addVertex(vnode);
				// new edge
				vedge = new NetlistEdge(srcnode, vnode);
				vedge.setName(this.getEdgeName(edge, srcnode, vnode));
				edgesToAdd.add(vedge);
				srcnode.addOutEdge(vedge);
				vnode.addInEdge(vedge);
			}
			// block to dst
			// virtual input
			nodeName = this.getVINodeNameFO(srcnode);
			vnode = vlNetlist.getVertexByName(nodeName);
			if (vnode == null) {
				vnode = new NetlistNode();
				vnode.setName(nodeName);
				vnode.getResultNetlistNodeData().setNodeType(LSResults.S_INPUT);
				vlNetlist.addVertex(vnode);
			}
			// new edge
			vedge = new NetlistEdge(vnode, dstnode);
			vedge.setName(this.getEdgeName(edge, vnode, dstnode));
			edgesToAdd.add(vedge);
			vnode.addOutEdge(vedge);
			dstnode.addInEdge(vedge);
			// remove edge
			srcnode.removeOutEdge(edge);
			dstnode.removeInEdge(edge);
			edgesToRemove.add(edge);
		}
		// remove
		for(int i = 0; i < edgesToRemove.size(); i++) {
			vlNetlist.removeEdge(edgesToRemove.get(i));
		}
		// add
		for(int i = 0; i < edgesToAdd.size(); i++) {
			vlNetlist.addEdge(edgesToAdd.get(i));
		}
		this.setVirtualLargeNetlistFO(vlNetlist);
		assert(vlNetlist.isValid());
	}

	private void computeVirtualLargeNetlistFO_Other() {
		Netlist vlNetlist = new Netlist(this.getVirtualLargeNetlist());
		vlNetlist.setName(this.getNetlist().getName() + "VirtualNetlistFO");
		List<NetlistEdge> edgesToRemove = new ArrayList<NetlistEdge>();
		List<NetlistEdge> edgesToAdd = new ArrayList<NetlistEdge>();
		List<NetlistNode> nodesToRemove = new ArrayList<NetlistNode>();
		List<NetlistNode> nodesToAdd = new ArrayList<NetlistNode>();
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
			CObjectCollection<NetlistNode> myOutput = LSResultNetlistNodeUtils.getMyOutput(node);
			String OutputNode = this.getVONodeNameFO(node);
			String InputNode = this.getVINodeNameFO(node);
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
				vedge.setName(this.getVONodeName(null, node, vnode));
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
				vnode = vlNetlist.getVertexByName(PTBlockNetlist.getMatchingVIONodeName(vnode.getName()));
				nodesToRemove.add(vnode);
				vedge = vnode.getOutEdgeAtIdx(0);
				edgesToRemove.add(vedge);
				dstnode = vedge.getDst();
				dstnode.removeInEdge(vedge);
				// make connection (input)
				vedge = new NetlistEdge();
				edgesToAdd.add(vedge);
				vedge.setName(this.getVINodeName(null, node, vnode));
				srcnode.addOutEdge(vedge);
				dstnode.addInEdge(vedge);
				vedge.setSrc(srcnode);
				vedge.setDst(dstnode);
			}
		}
		// remove
		for(int i = 0; i < edgesToRemove.size(); i++) {
			vlNetlist.removeEdge(edgesToRemove.get(i));
		}
		for(int i = 0; i < nodesToRemove.size(); i++) {
			vlNetlist.removeVertex(nodesToRemove.get(i));
		}
		// add
		for(int i = 0; i < edgesToAdd.size(); i++) {
			vlNetlist.addEdge(edgesToAdd.get(i));
		}
		for(int i = 0; i < nodesToAdd.size(); i++) {
			vlNetlist.addVertex(nodesToAdd.get(i));
		}
		this.setVirtualLargeNetlistFO(vlNetlist);
		assert(vlNetlist.isValid());
	}
	
	private String getBlockName(int pid) {
		String rtn = "";
		rtn += PTBlockNetlist.S_BLOCKNAMEPREFIX;
		rtn += pid;
		return rtn;
	}
	
	// TODO: verify and test
	private void computeClusterNetlist (boolean repeat) {
		Netlist netlist = this.getNetlist();
		Netlist cnetlist = null;
		if (repeat) {
			cnetlist = this.getClusterRepeatedEdgesNetlist();
			cnetlist.setName(netlist.getName() + "_PTClusterRepeatedEdgesNetlist");
		}
		else {
			cnetlist = this.getClusterNetlist();
			cnetlist.setName(netlist.getName() + "_PTClusterNetlist");
		}		
		Map<Integer, List<NetlistNode>> pidMap = this.getPartitionIDNetlistNodeMap();
		// key = block id dest
		// value = node input to block id
		Multimap<Integer, NetlistNode> dstBlockIDSrcNodeMap = ArrayListMultimap.create();
		NetlistNode srcnode = null;
		NetlistNode dstnode = null;
		int srcPID = PTResults.S_DEFAULT;
		int dstPID = PTResults.S_DEFAULT;
		List<NetlistNode> nodeList = null;
		// using iterators
		Iterator<Map.Entry<Integer, List<NetlistNode>>> itr = null;
		// make nodes
		itr = pidMap.entrySet().iterator();
		while(itr.hasNext()){
			// create new node for all blocks
			Map.Entry<Integer, List<NetlistNode>> entry = itr.next();
			srcPID = entry.getKey();
			if (srcPID < 0) {
				nodeList = entry.getValue();
				for (int i = 0; i < nodeList.size(); i++) {
					srcnode = nodeList.get(i);
					dstnode = new NetlistNode();
					dstnode.setName(srcnode.getName());
					dstnode.setResultNetlistNodeData(new ResultNetlistNodeData(srcnode.getResultNetlistNodeData()));
					cnetlist.addVertex(dstnode);
				}
			}
			else {
				srcnode = new NetlistNode();
				srcnode.setName(this.getBlockName(srcPID));
				srcnode.getResultNetlistNodeData().setPartitionID(srcPID);
				cnetlist.addVertex(srcnode);
			}
		}
		// make connections
		NetlistNode pidSrcNode = null;
		NetlistNode pidDstNode = null;
		itr = pidMap.entrySet().iterator();
		while(itr.hasNext()){
			Map.Entry<Integer, List<NetlistNode>> entry = itr.next();
			srcPID = entry.getKey();
			nodeList = entry.getValue();
			for (int i = 0; i < nodeList.size(); i++) {
				srcnode = nodeList.get(i);
				for (int j = 0; j < srcnode.getNumOutEdge(); j++) {
					NetlistEdge edge = srcnode.getOutEdgeAtIdx(j);
					dstnode = edge.getDst();
					dstPID = dstnode.getResultNetlistNodeData().getPartitionID();
					// no self edge and unpartitioned nodes
					if ((dstPID == srcPID)) {
						continue;
					}
					if (srcPID < 0) {
						pidSrcNode = cnetlist.getVertexByName(srcnode.getName());
					}
					else {
						pidSrcNode = cnetlist.getVertexByName(this.getBlockName(srcPID));
					}
					if (dstPID < 0) {
						pidDstNode = cnetlist.getVertexByName(dstnode.getName());
					}
					else {
						pidDstNode = cnetlist.getVertexByName(this.getBlockName(dstPID));
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
					NetlistEdge myedge = new NetlistEdge(pidSrcNode, pidDstNode);
					myedge.setName(this.getEdgeName(edge, pidSrcNode, pidDstNode));
					cnetlist.addEdge(myedge);
					pidSrcNode.addOutEdge(myedge);
					pidDstNode.addInEdge(myedge);
				}
			}
		}
		assert(cnetlist.isValid());
	}
/*
	private void computeClusterNetlistNew () {
		Netlist netlist = this.getNetlist();
		Netlist cnetlist = null;
		if (repeat) {
			cnetlist = this.getClusterRepeatedEdgesNetlist();
			cnetlist.setName(netlist.getName() + "_PTClusterRepeatedEdgesNetlist");
		}
		else {
			cnetlist = this.getClusterNetlist();
			cnetlist.setName(netlist.getName() + "_PTClusterNetlist");
		}		
		Map<Integer, List<NetlistNode>> pidMap = this.getPartitionIDNetlistNodeMap();
		// key = block id dest
		// value = node input to block id
		Multimap<Integer, NetlistNode> dstBlockIDSrcNodeMap = ArrayListMultimap.create();
		NetlistNode srcnode = null;
		NetlistNode dstnode = null;
		int srcPID = PTResults.S_DEFAULT;
		int dstPID = PTResults.S_DEFAULT;
		List<NetlistNode> nodeList = null;
		// using iterators
		Iterator<Map.Entry<Integer, List<NetlistNode>>> itr = null;
		// make nodes
		itr = pidMap.entrySet().iterator();
		while(itr.hasNext()){
			// create new node for all blocks
			Map.Entry<Integer, List<NetlistNode>> entry = itr.next();
			srcPID = entry.getKey();
			if (srcPID < 0) {
				nodeList = entry.getValue();
				for (int i = 0; i < nodeList.size(); i++) {
					srcnode = nodeList.get(i);
					dstnode = new NetlistNode();
					dstnode.setName(srcnode.getName());
					dstnode.setResultNetlistNodeData(new ResultNetlistNodeData(srcnode.getResultNetlistNodeData()));
					cnetlist.addVertex(dstnode);
				}
			}
			else {
				srcnode = new NetlistNode();
				srcnode.setName(this.getBlockName(srcPID));
				srcnode.getResultNetlistNodeData().setPartitionID(srcPID);
				cnetlist.addVertex(srcnode);
			}
		}
		// make connections
		NetlistNode pidSrcNode = null;
		NetlistNode pidDstNode = null;
		itr = pidMap.entrySet().iterator();
		while(itr.hasNext()){
			Map.Entry<Integer, List<NetlistNode>> entry = itr.next();
			srcPID = entry.getKey();
			nodeList = entry.getValue();
			for (int i = 0; i < nodeList.size(); i++) {
				srcnode = nodeList.get(i);
				for (int j = 0; j < srcnode.getNumOutEdge(); j++) {
					NetlistEdge edge = srcnode.getOutEdgeAtIdx(j);
					dstnode = edge.getDst();
					dstPID = dstnode.getResultNetlistNodeData().getPartitionID();
					// no self edge and unpartitioned nodes
					if ((dstPID == srcPID)) {
						continue;
					}
					if (srcPID < 0) {
						pidSrcNode = cnetlist.getVertexByName(srcnode.getName());
					}
					else {
						pidSrcNode = cnetlist.getVertexByName(this.getBlockName(srcPID));
					}
					if (dstPID < 0) {
						pidDstNode = cnetlist.getVertexByName(dstnode.getName());
					}
					else {
						pidDstNode = cnetlist.getVertexByName(this.getBlockName(dstPID));
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
					NetlistEdge myedge = new NetlistEdge(pidSrcNode, pidDstNode);
					myedge.setName(this.getEdgeName(edge, pidSrcNode, pidDstNode));
					cnetlist.addEdge(myedge);
					pidSrcNode.addOutEdge(myedge);
					pidDstNode.addInEdge(myedge);
				}
			}
		}
		assert(cnetlist.isValid());
	}
*/
	private void computeNetlists () {
		Map<Integer, List<NetlistNode>> pidMap = this.getPartitionIDNetlistNodeMap();
		List<Netlist> netlists = this.getNetlists();
		List<NetlistEdge> edgesToRemove = new ArrayList<NetlistEdge>();
		List<NetlistNode> nodesToRemove = new ArrayList<NetlistNode>();
		NetlistEdge edge = null;
		Netlist pNetlist = null;
		int pid = PTResults.S_DEFAULT;
		NetlistNode srcnode = null;
		NetlistNode dstnode = null;
		int srcPID = PTResults.S_DEFAULT;
		int dstPID = PTResults.S_DEFAULT;
		// using iterators
		Iterator<Map.Entry<Integer, List<NetlistNode>>> itr = null;
		// make nodes
		itr = pidMap.entrySet().iterator();
		while(itr.hasNext()){
			Map.Entry<Integer, List<NetlistNode>> entry = itr.next();
			pid = entry.getKey();
			if (pid < 0) {
				continue;
			}
			pNetlist = new Netlist(this.getVirtualLargeNetlist());
			pNetlist.setName(this.getNetlist().getName() + pid);
			edgesToRemove.clear();
			nodesToRemove.clear();
			// remove all nodes/edges not in pid
			// or that it is not connected
			for (int i = 0; i < pNetlist.getNumEdge(); i++) {
				edge = pNetlist.getEdgeAtIdx(i);
				srcnode = edge.getSrc();
				dstnode = edge.getDst();
				srcPID = srcnode.getResultNetlistNodeData().getPartitionID();
				dstPID = dstnode.getResultNetlistNodeData().getPartitionID();
				if (
						((srcPID >= 0) && (srcPID != pid)) ||
						((dstPID >= 0) && (dstPID != pid))
						) {
					srcnode.removeOutEdge(edge);
					dstnode.removeInEdge(edge);
					edgesToRemove.add(edge);
				}
			}
			// remove
			for(int i = 0; i < edgesToRemove.size(); i++) {
				pNetlist.removeEdge(edgesToRemove.get(i));
			}
			// nodes (remove unconnected nodes)
			for(int i = 0; i < pNetlist.getNumVertex(); i++) {
				NetlistNode node = pNetlist.getVertexAtIdx(i);
				if ((node.getNumOutEdge() == 0) && (node.getNumInEdge() == 0)) {
					nodesToRemove.add(node);
				}
			}
			// remove
			for(int i = 0; i < nodesToRemove.size(); i++) {
				pNetlist.removeVertex(nodesToRemove.get(i));
			}
			// add
			assert(pNetlist.isValid());
			netlists.add(pNetlist);
		}
	}
	
	private void computeNetlistsFO() {
		Map<Integer, List<NetlistNode>> pidMap = this.getPartitionIDNetlistNodeMap();
		List<Netlist> netlists = this.getNetlistsFO();
		List<NetlistEdge> edgesToRemove = new ArrayList<NetlistEdge>();
		List<NetlistNode> nodesToRemove = new ArrayList<NetlistNode>();
		NetlistEdge edge = null;
		Netlist pNetlist = null;
		int pid = PTResults.S_DEFAULT;
		NetlistNode srcnode = null;
		NetlistNode dstnode = null;
		int srcPID = PTResults.S_DEFAULT;
		int dstPID = PTResults.S_DEFAULT;
		// using iterators
		Iterator<Map.Entry<Integer, List<NetlistNode>>> itr = null;
		// make nodes
		itr = pidMap.entrySet().iterator();
		while(itr.hasNext()){
			Map.Entry<Integer, List<NetlistNode>> entry = itr.next();
			pid = entry.getKey();
			if (pid < 0) {
				continue;
			}
			pNetlist = new Netlist(this.getVirtualLargeNetlistFO());
			pNetlist.setName(this.getNetlist().getName() + pid);
			edgesToRemove.clear();
			nodesToRemove.clear();
			// remove all nodes/edges not in pid
			// or that it is not connected
			for (int i = 0; i < pNetlist.getNumEdge(); i++) {
				edge = pNetlist.getEdgeAtIdx(i);
				srcnode = edge.getSrc();
				dstnode = edge.getDst();
				srcPID = srcnode.getResultNetlistNodeData().getPartitionID();
				dstPID = dstnode.getResultNetlistNodeData().getPartitionID();
				if (
						((srcPID >= 0) && (srcPID != pid)) ||
						((dstPID >= 0) && (dstPID != pid))
						) {
					srcnode.removeOutEdge(edge);
					dstnode.removeInEdge(edge);
					edgesToRemove.add(edge);
				}
			}
			// remove
			for(int i = 0; i < edgesToRemove.size(); i++) {
				pNetlist.removeEdge(edgesToRemove.get(i));
			}
			// nodes (remove unconnected nodes)
			for(int i = 0; i < pNetlist.getNumVertex(); i++) {
				NetlistNode node = pNetlist.getVertexAtIdx(i);
				if ((node.getNumOutEdge() == 0) && (node.getNumInEdge() == 0)) {
					nodesToRemove.add(node);
				}
			}
			// remove
			for(int i = 0; i < nodesToRemove.size(); i++) {
				pNetlist.removeVertex(nodesToRemove.get(i));
			}
			// add
			assert(pNetlist.isValid());
			netlists.add(pNetlist);
		}
	}
	
	public PTBlockNetlist(Netlist netlist) {
		this.init();
		this.setNetlist(netlist);
	}
	
	/*
	 * Netlist
	 */
	public Netlist getNetlist(){
		return netlist;
	}
	
	public void setNetlist(Netlist netlist){
		this.netlist = netlist;
		this.clear();
		this.separateByPID();
		this.computeVirtualLargeNetlist();
		this.computeVirtualLargeNetlistFO();
		this.computeClusterNetlist(false);
		this.computeClusterNetlist(true);
		this.computeNetlists();
		this.computeNetlistsFO();
	}
	
	private Netlist netlist;

	/*
	 * VirtualLargeNetlistFO
	 */
	private void setVirtualLargeNetlistFO(Netlist netlist){
		this.virtualLargeNetlistFO = netlist;
	}
	
	public Netlist getVirtualLargeNetlistFO(){
		return virtualLargeNetlistFO;
	}
		
	private Netlist virtualLargeNetlistFO;
	/*
	 * VirtualLargeNetlist
	 */
	private void setVirtualLargeNetlist(Netlist netlist){
		this.virtualLargeNetlist = netlist;
	}
	
	public Netlist getVirtualLargeNetlist(){
		return virtualLargeNetlist;
	}
		
	private Netlist virtualLargeNetlist;

	/*
	 * ClusterNetlist
	 */
	public Netlist getClusterNetlist(){
		return clusterNetlist;
	}
	
	private Netlist clusterNetlist;

	/*
	 * ClusterRepeatedEdgesNetlist
	 */
	public Netlist getClusterRepeatedEdgesNetlist(){
		return clusterRepeatedEdgesNetlist;
	}
	
	private Netlist clusterRepeatedEdgesNetlist;

	/*
	 * ClusterRepeatedEdgesNetlistNew
	 */
/*	public Netlist getClusterRepeatedEdgesNetlistNew(){
		return clusterRepeatedEdgesNetlistNew;
	}
	
	private hyperNetlist clusterRepeatedEdgesNetlistNew;
*/
	/*
	 * Netlists
	 */
	private List<Netlist> getNetlists(){
		return netlists;
	}
	
	private List<Netlist> netlists;
	
	public Netlist getNetlistAtIdx(int index) {
		Netlist rtn = null;
		if (
				(0 <= index) &&
				(index < this.getNetlistNum())
				){
			rtn = this.getNetlists().get(index);
		}
		return rtn;
	}
	
	public int getNetlistNum() {
		return this.getNetlists().size();
	}
	/*
	 * NetlistsFO
	 */
	private List<Netlist> getNetlistsFO(){
		return netlistsFO;
	}
	
	private List<Netlist> netlistsFO;
	
	public Netlist getNetlistFOAtIdx(int index) {
		Netlist rtn = null;
		if (
				(0 <= index) &&
				(index < this.getNetlistFONum())
				){
			rtn = this.getNetlistsFO().get(index);
		}
		return rtn;
	}
	
	public int getNetlistFONum() {
		return this.getNetlistsFO().size();
	}

	/*
	 * PartitionIDNetlistNode
	 */
	private Map<Integer, List<NetlistNode>> getPartitionIDNetlistNodeMap(){
		return partitionIDNetlistNodeMap;
	}
	
	private Map<Integer, List<NetlistNode>> partitionIDNetlistNodeMap;
	
	static private String S_BLOCKNAMEPREFIX = "block";
	static private String S_UNDERSCORE = "_";
	static private String S_OUTPUT = "OUTPUT";
	static private String S_INPUT = "INPUT";
	static private String S_TO = "TO";
	static private String S_FROM = "FROM";
}

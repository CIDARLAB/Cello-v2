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
package org.cellocad.cello2.partitioning.common;

import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cellocad.cello2.common.CObject;
import org.cellocad.cello2.common.Utils;
import org.cellocad.cello2.partitioning.netlist.PTNetlist;
import org.cellocad.cello2.partitioning.netlist.PTNetlistEdge;
import org.cellocad.cello2.partitioning.netlist.PTNetlistNode;
import org.cellocad.cello2.results.netlist.Netlist;
import org.cellocad.cello2.results.netlist.NetlistEdge;
import org.cellocad.cello2.results.netlist.NetlistNode;

/**
 * @author Vincent Mirian
 * 
 * @date Oct 27, 2017
 *
 */
public class Netlister extends CObject {

	private void init() {
		this.setPTNetlist(new PTNetlist());
		this.NetlistNodeMap = new HashMap<String, NetlistNode>();
		this.NetlistEdgeMap = new HashMap<String, NetlistEdge>();
		this.PTNetlistNodeMap = new HashMap<String, PTNetlistNode>();
		this.PTNetlistEdgeMap = new HashMap<String, PTNetlistEdge>();
	}
	
	public Netlister(final Netlist netlist){
		Utils.isNullRuntimeException(netlist, "netlist");
		this.setNetlist(netlist);
		init();
		this.convertNetlistToPTNetlist();
	}
	
	private void convertNetlistToPTNetlist(){
		PTNetlist ptnetlist = this.getPTNetlist();
		Netlist netlist = this.getNetlist();
		Map<String, NetlistNode> NetlistNodeMap = this.getNetlistNodeMap();
		Map<String, NetlistEdge> NetlistEdgeMap = this.getNetlistEdgeMap();
		Map<String, PTNetlistNode> PTNetlistNodeMap = this.getPTNetlistNodeMap();
		Map<String, PTNetlistEdge> PTNetlistEdgeMap = this.getPTNetlistEdgeMap();
		Map<NetlistNode, PTNetlistNode> NetlistToPTNetlistNode = new HashMap<NetlistNode, PTNetlistNode>();
		Map<NetlistEdge, PTNetlistEdge> NetlistToPTNetlistEdge = new HashMap<NetlistEdge, PTNetlistEdge>();
		// convert Vertex to PNode
		for (int i = 0; i < netlist.getNumVertex(); i++){
			NetlistNode node = netlist.getVertexAtIdx(i);
			PTNetlistNode ptnode = new PTNetlistNode();
			ptnode.setNetlistNode(node);
			String name = node.getName();
			ptnode.setName(name);
			ptnode.setNodeType(node.getResultNetlistNodeData().getNodeType());
			NetlistToPTNetlistNode.put(node, ptnode);
			NetlistNodeMap.put(name, node);
			PTNetlistNodeMap.put(name, ptnode);
			ptnetlist.addVertex(ptnode);
		}
		// convert Edge to PEdge
		for (int i = 0; i < netlist.getNumEdge(); i++){
			NetlistEdge edge = netlist.getEdgeAtIdx(i);
			PTNetlistEdge ptedge = new PTNetlistEdge();
			ptedge.setNetlistEdge(edge);
			String name = edge.getName();
			ptedge.setName(name);
			NetlistToPTNetlistEdge.put(edge, ptedge);
			NetlistEdgeMap.put(name, edge);
			PTNetlistEdgeMap.put(name, ptedge);
			ptnetlist.addEdge(ptedge);	
		}
		// for each PNode:
		for (int i = 0; i < netlist.getNumVertex(); i++){
			NetlistNode node = netlist.getVertexAtIdx(i);
			PTNetlistNode ptnode = NetlistToPTNetlistNode.get(node);
			assert (ptnode != null);
			// set outEdge for PNode
			for (int j = 0; j < node.getNumOutEdge(); j ++){
				NetlistEdge edge = node.getOutEdgeAtIdx(j);
				PTNetlistEdge ptedge = NetlistToPTNetlistEdge.get(edge);
				assert (ptedge != null);
				ptnode.addOutEdge(ptedge);				
			}
			// set inEdge for PNode
			for (int j = 0; j < node.getNumInEdge(); j ++){
				NetlistEdge edge = node.getInEdgeAtIdx(j);
				PTNetlistEdge ptedge = NetlistToPTNetlistEdge.get(edge);
				assert (ptedge != null);
				ptnode.addInEdge(ptedge);				
			}			
		}
		// for each PEdge:
		for (int i = 0; i < netlist.getNumEdge(); i++){
			NetlistEdge edge = netlist.getEdgeAtIdx(i);
			PTNetlistEdge ptedge = NetlistToPTNetlistEdge.get(edge);
			assert (ptedge != null);
			// set src for PEdge
			{
				NetlistNode node = edge.getSrc();
				PTNetlistNode ptnode = NetlistToPTNetlistNode.get(node);
				assert (ptnode != null);
				ptedge.setSrc(ptnode);
			}
			// set dst for PEdge
			{
				NetlistNode node = edge.getDst();
				PTNetlistNode ptnode = NetlistToPTNetlistNode.get(node);
				assert (ptnode != null);
				ptedge.setDst(ptnode);
			}
		}
		// set name
		ptnetlist.setName(netlist.getName()+"_PTNetlist");
		assert(ptnetlist.isValid());
	}
	
	protected void attachPIDToNetlist(){
		PTNetlist ptNetlist = this.getPTNetlist();
		Map<String, NetlistNode> NetlistNodeMap = this.getNetlistNodeMap();
		for (int i = 0; i < ptNetlist.getNumVertex(); i++) {
			PTNetlistNode ptnode = ptNetlist.getVertexAtIdx(i);
			Block block = ptnode.getMyBlock();
			if (block != null) {
				NetlistNode node = NetlistNodeMap.get(ptnode.getName());
				if (node != null) {
					int pID = block.getIdx();
					this.getLogger().info("Setting partitionID of " + node.getName() + " to " + pID);
					node.getResultNetlistNodeData().setPartitionID(pID);
				}
				else {
					this.getLogger().info(ptnode.getName() + " does not have a NetlistNode!");
				}
			}
		}
	}

	protected void setNetlist(final Netlist netlist) {
		this.netlist = netlist;
	}
	
	public Netlist getNetlist(){
		this.attachPIDToNetlist();
		return this.netlist;
	}

	protected void setPTNetlist(final PTNetlist ptNetlist) {
		this.ptNetlist = ptNetlist;
	}
	
	public PTNetlist getPTNetlist(){
		return this.ptNetlist;
	}

	protected Map<String, NetlistNode> getNetlistNodeMap(){
		return this.NetlistNodeMap;
	}

	protected Map<String, NetlistEdge> getNetlistEdgeMap(){
		return this.NetlistEdgeMap;
	}

	protected Map<String, PTNetlistNode> getPTNetlistNodeMap(){
		return this.PTNetlistNodeMap;
	}

	protected Map<String, PTNetlistEdge> getPTNetlistEdgeMap(){
		return this.PTNetlistEdgeMap;
	}
	
	private Netlist netlist;
	private PTNetlist ptNetlist;
	// map for conversion
	Map<String, NetlistNode> NetlistNodeMap;
	Map<String, NetlistEdge> NetlistEdgeMap;
	Map<String, PTNetlistNode> PTNetlistNodeMap;
	Map<String, PTNetlistEdge> PTNetlistEdgeMap;
	
	protected Logger getLogger() {
		return Netlister.logger;
	}
	
	private static final Logger logger = LogManager.getLogger(Netlister.class);
}

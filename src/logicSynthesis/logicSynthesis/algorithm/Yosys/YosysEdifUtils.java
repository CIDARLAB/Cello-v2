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
package logicSynthesis.algorithm.Yosys;

import java.io.FileNotFoundException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import common.Utils;
import common.graph.AbstractVertex.VertexType;
import results.netlist.Netlist;
import results.netlist.NetlistEdge;
import results.netlist.NetlistNode;
import edu.byu.ece.edif.core.EdifCellInstance;
import edu.byu.ece.edif.core.EdifEnvironment;
import edu.byu.ece.edif.core.EdifNet;
import edu.byu.ece.edif.core.EdifPortRef;
import edu.byu.ece.edif.util.parse.EdifParser;
import edu.byu.ece.edif.util.parse.ParseException;
import results.logicSynthesis.LSResults;

/**
 * The YosysEdifUtils class is class with utility methods for the <i>Yosys</i> instances.
 * 
 * @author Vincent Mirian
 * 
 * @date 2018-05-21
 *
 */
public class YosysEdifUtils {

	/*
	 * EDIF
	 */
	/**
	 *  Translates the EDIF file referenced by parameter <i>filename</i> into
	 *  the Netlist in parameter <i>netlist</i> 
	 *  using the <i>Yosys</i> algorithm instance.
	 *  
	 *  @param yosys the <i>Yosys</i> algorithm instance
	 *  @param filename the EDIF file
	 *  @param netlist the netlist
	 *  @throws RuntimeException if: <br>
	 *  Any of the parameters are null<br>
	 *  Error accessing <i>filename</i><br>
	 *  Error parsing <i>filename</i><br>
	 */
	static public void convertEdifToNetlist(Yosys yosys, String filename, Netlist netlist){
		Utils.isNullRuntimeException(yosys, "yosys");
		Utils.isNullRuntimeException(filename, "filename");
		Utils.isNullRuntimeException(netlist, "netlist");
		EdifEnvironment edifEnv = null;
		try {
			edifEnv = EdifParser.translate(filename);
		}
		catch (FileNotFoundException | ParseException e) {
			e.printStackTrace();
		}
		EdifCellInstance top = edifEnv.getTopDesign().getTopCellInstance();
		netlist.setName(top.getOldName());
		Map<String, NetlistNode> map = new HashMap<String, NetlistNode>();
		Collection<EdifNet> nets = top.getCellType().getNetList();
		EdifCellInstance srcCell = null;
		EdifCellInstance dstCell = null;
		NetlistNode srcNode = null;
		NetlistNode dstNode = null;
		String type = null;
		for (EdifNet net : nets) {
			// Top Input
			if (YosysEdifUtils.hasTopInput(net)) {
				// Top PortRef
				for (EdifPortRef topPortRef: net.getInputPortRefs()) {
					if (topPortRef.isTopLevelPortRef()) {
						srcNode = YosysEdifUtils.getNode(topPortRef.getSingleBitPort().getPortName(), LSResults.S_PRIMARYINPUT, map, netlist);
						srcNode.setVertexType(VertexType.SOURCE);
						// Other Input/Output PortRef
						for (EdifPortRef otherPortRef: net.getPortRefList()) {
							if (topPortRef == otherPortRef) {
								continue;
							}
							EdifCellInstance cell = otherPortRef.getCellInstance();
							// generic
							if (cell != null) {
								assert(otherPortRef.getPort().isInput());
								type = YosysUtils.getNodeType(cell.getCellType().getOldName());
								dstNode = YosysEdifUtils.getNode(cell.getName(), type, map, netlist);
								dstNode.setVertexType(VertexType.NONE);
							}
							// Top output
							else {
								assert(otherPortRef.getPort().isOutput());
								dstNode = YosysEdifUtils.getNode(otherPortRef.getSingleBitPort().getPortName(), LSResults.S_PRIMARYOUTPUT, map, netlist);
								dstNode.setVertexType(VertexType.SINK);
							}
							// setEdge
							YosysEdifUtils.setEdge(srcNode, dstNode, net, netlist);
						}
					}
				}
			}
			// Top Output
			else if (YosysEdifUtils.hasTopOutput(net)) {
				// Top PortRef
				for (EdifPortRef topPortRef: net.getOutputPortRefs()) {
					if (topPortRef.isTopLevelPortRef()) {
						dstNode = YosysEdifUtils.getNode(topPortRef.getSingleBitPort().getPortName(), LSResults.S_PRIMARYOUTPUT, map, netlist);
						dstNode.setVertexType(VertexType.SINK);
						// Other Output PortRef
						for (EdifPortRef otherPortRef: net.getOutputPortRefs()) {
							if (topPortRef == otherPortRef) {
								continue;
							}
							EdifCellInstance cell = otherPortRef.getCellInstance();
							// generic
							assert(otherPortRef.getPort().isOutput());
							type = YosysUtils.getNodeType(cell.getCellType().getOldName());
							srcNode = YosysEdifUtils.getNode(cell.getName(), type, map, netlist);
							srcNode.setVertexType(VertexType.NONE);
							// setEdge
							YosysEdifUtils.setEdge(srcNode, dstNode, net, netlist);
						}
					}
				}				
			}
			// Other
			else {
				// Outputs
				for (EdifPortRef outputPortRef: net.getOutputPortRefs()) {
					srcCell = outputPortRef.getCellInstance();
					// create vertex if not present
					type = YosysUtils.getNodeType(srcCell.getCellType().getOldName());
					srcNode = YosysEdifUtils.getNode(srcCell.getName(), type, map, netlist);
					srcNode.setVertexType(VertexType.NONE);
					for (EdifPortRef inputPortRef: net.getInputPortRefs()) {
						dstCell = inputPortRef.getCellInstance();
						// create vertex if not present
						type = YosysUtils.getNodeType(dstCell.getCellType().getOldName());
						dstNode = YosysEdifUtils.getNode(dstCell.getName(), type, map, netlist);
						dstNode.setVertexType(VertexType.NONE);
						// setEdge
						YosysEdifUtils.setEdge(srcNode, dstNode, net, netlist);
					}			
				}
			}
		}
	}

	/**
	 *  Initializes a NetlistEdge in the Netlist defined by parameter <i>netlist</i> with:
	 *  attributes defined by parameter <i>net</i>,
	 *  source node defined by parameter <i>src</i>, and,
	 *  destination node defined by parameter <i>dst</i>
	 *  
	 *  @param src the source node
	 *  @param dst the destination node
	 *  @param net the attributes
	 *  @param netlist the Netlist
	 */
	static protected void setEdge(NetlistNode src, NetlistNode dst, EdifNet net, Netlist netlist) {
		NetlistEdge edge = new NetlistEdge(src, dst);
		edge.setName(net.getOldName());
		src.addOutEdge(edge);
		dst.addInEdge(edge);
		netlist.addEdge(edge);
	}

	/**
	 *  Assigns the NetlistNode with its name defined by parameter <i>Name</i>
	 *  with result for the <i>logicSynthesis</i> stage defined by parameter <i>type</i>.
	 *  If the NetlistNode with its name defined by parameter <i>Name</i> is not present in the
	 *  translation bookeeper defined by parameter <i>map</i>, then initialize a new NetlistNode and
	 *  insert it into the Netlist defined by parameter <i>netlist</i>.
	 *  Returns the assigned or initialized NetlistNode.
	 *  
	 *  @param Name the name of the NetlistNode
	 *  @param type the result for the <i>logicSynthesis</i> stage
	 *  @param map the translation bookeeper
	 *  @param netlist the Netlist
	 *  @return the assigned or initialized NetlistNode
	 */
	static protected NetlistNode getNode(String Name, String type, Map<String, NetlistNode> map, Netlist netlist) {
		NetlistNode rtn = null;
		rtn = map.get(Name);
		if (rtn == null) {
			rtn = new NetlistNode();
			rtn.setName(Name);
			rtn.getResultNetlistNodeData().setNodeType(type);
			netlist.addVertex(rtn);
			map.put(Name, rtn);
		}
		return rtn;
	}

	/**
	 * Returns a boolean flag signifying that the parameter <i>net</i> is connected to an input port
	 * 
	 * @param net the Net
	 * @return true if the parameter <i>net</i> is connected to an input port, otherwise false.
	 */
	static protected boolean hasTopInput(EdifNet net) {
		boolean rtn = false;
		for (EdifPortRef portRef: net.getInputPortRefs()) {
			rtn = rtn || portRef.isTopLevelPortRef();
		}
		return rtn;
	}

	/**
	 * Returns a boolean flag signifying that the parameter <i>net</i> is connected to an output port
	 * 
	 * @param net the Net
	 * @return true if the parameter <i>net</i> is connected to an output port, otherwise false.
	 */
	static protected boolean hasTopOutput(EdifNet net) {
		boolean rtn = false;
		for (EdifPortRef portRef: net.getOutputPortRefs()) {
			rtn = rtn || portRef.isTopLevelPortRef();
		}
		return rtn;
	}
}

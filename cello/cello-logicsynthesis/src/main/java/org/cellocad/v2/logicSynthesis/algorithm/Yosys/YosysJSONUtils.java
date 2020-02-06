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
package org.cellocad.v2.logicSynthesis.algorithm.Yosys;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.cellocad.v2.common.Utils;
import org.cellocad.v2.common.graph.AbstractVertex.VertexType;
import org.cellocad.v2.common.profile.ProfileUtils;
import org.cellocad.v2.results.logicSynthesis.LSResults;
import org.cellocad.v2.results.netlist.Netlist;
import org.cellocad.v2.results.netlist.NetlistEdge;
import org.cellocad.v2.results.netlist.NetlistNode;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

/**
 * The YosysJSONUtils class is class with utility methods for the <i>Yosys</i> instances.
 * 
 * @author Vincent Mirian
 * 
 * @date 2018-05-21
 *
 */
public class YosysJSONUtils {

	/**
	 *  Translates the JSON file referenced by parameter <i>filename</i> into
	 *  the Netlist in parameter <i>netlist</i> 
	 *  using the <i>Yosys</i> algorithm instance.
	 *  
	 *  @param yosys the <i>Yosys</i> algorithm instance
	 *  @param filename the JSON file
	 *  @param netlist the netlist
	 *  @throws RuntimeException if: <br>
	 *  Any of the parameters are null<br>
	 *  Error accessing <i>filename</i><br>
	 *  Error parsing <i>filename</i><br>
	 */
	static public void getNetlistFromYosysJSONFile(Yosys yosys, String filename, Netlist netlist){
		Utils.isNullRuntimeException(yosys, "yosys");
		Utils.isNullRuntimeException(filename, "filename");
		Utils.isNullRuntimeException(netlist, "netlist");
		// get JSON File
	    File jsonFile = new File(filename);
	    Reader jsonReader = null;
		JSONObject jsonTop = null;
		// Create File Reader
		try {
			jsonReader = new FileReader(jsonFile);
		} catch (FileNotFoundException e) {
			throw new RuntimeException("Error with file: " + jsonFile);
		}
		// Create JSON object from File Reader
		JSONParser parser = new JSONParser();
        try{
        	jsonTop = (JSONObject) parser.parse(jsonReader);
	    } catch (IOException e) {
	        throw new RuntimeException("File IO Exception for: " + jsonFile + ".");
	    } catch (ParseException e) {
	        throw new RuntimeException("Parser Exception for: " + jsonFile + ".");
	    }
		// Create netlist
        YosysJSONUtils.parseJSON(jsonTop, netlist, yosys);
	    try {
			jsonReader.close();
		} catch (IOException e) {
			throw new RuntimeException("Error with file: " + jsonFile);
		
		}
	}

	/**
	 *  Copies the name of the top module defined in parameter <i>jsonTop</i> into
	 *  the Netlist in parameter <i>netlist</i>.
	 *  Returns the JSONObject representing value of the the top module
	 *  
	 *  @param jsonTop the JSONObject
	 *  @param netlist the netlist
	 *  @return the JSONObject representing value of the the top module
	 */
	static protected JSONObject parseModuleName(JSONObject jsonTop, Netlist netlist){
		JSONObject rtn = null;
		JSONObject jModules = (JSONObject) jsonTop.get(YosysJSONUtils.S_MODULES);
		if (jModules.keySet().size() != 1) {
			throw new RuntimeException("More than one module!");
		}
		@SuppressWarnings("unchecked")
		Set<Map.Entry<?,?>> entrySet = (Set<Map.Entry<?,?>>) jModules.entrySet();
        String name = (String) entrySet.iterator().next().getKey();
		netlist.setName(name);
		rtn = (JSONObject) entrySet.iterator().next().getValue();
		return rtn;
	}
	
	/**
	 *  Creates NetlistNode instances for the port(s) from the top module defined in parameter <i>jsonValue</i>.<br>
	 *  Updates the map of integer to input netlistNode defined by parameter <i>inputNetlistNode</i>.<br>
	 *  Updates the map of integer to output netlistNode defined by parameter <i>outputNetlistNode</i>.<br>
	 *  Updates the Netlist defined by parameter <i>netlist</i>.<br>
	 *  
	 *  @param jsonValue the JSONObject
	 *  @param inputNetlistNode the map of integer to input netlistNode
	 *  @param outputNetlistNode the map of integer to output netlistNode
	 *  @param netlist the netlist
	 */
	static protected void parseModule(JSONObject jsonValue,
			Multimap<Integer, NetlistNode> inputNetlistNode,
			Multimap<Integer, NetlistNode> outputNetlistNode,
			Netlist netlist){
		JSONObject jPorts = (JSONObject) jsonValue.get(YosysJSONUtils.S_PORTS);
		@SuppressWarnings("unchecked")
		Set<Map.Entry<?,?>> entrySet = (Set<Map.Entry<?,?>>) jPorts.entrySet();
		Iterator<?> iter = entrySet.iterator();
		while (iter.hasNext()) {
			Map.Entry<?,?> item = (Map.Entry<?, ?>) iter.next();
			String name = (String) item.getKey();
			NetlistNode node = new NetlistNode();
			netlist.addVertex(node);
			node.setName(name);
			JSONObject value = (JSONObject) item.getValue();
			// direction
			String direction = ProfileUtils.getString(value, YosysJSONUtils.S_DIRECTION);
			if (direction.equals(YosysJSONUtils.S_INPUT)) {
				node.getResultNetlistNodeData().setNodeType(LSResults.S_PRIMARYINPUT);
				node.setVertexType(VertexType.SOURCE);
			}
			else if (direction.equals(YosysJSONUtils.S_OUTPUT)) {
				node.getResultNetlistNodeData().setNodeType(LSResults.S_PRIMARYOUTPUT);
				node.setVertexType(VertexType.SINK);				
			}
			else {
				throw new RuntimeException("Unknown direction!");
			}
			// bits
			JSONArray JArray = (JSONArray) value.get(YosysJSONUtils.S_BITS);
			for (int i = 0; i < JArray.size(); i ++) {
				Object element = (Object) JArray.get(i);
				Integer intObj = ProfileUtils.getInteger(element);
				if (direction.equals(YosysJSONUtils.S_INPUT)) {
					outputNetlistNode.put(intObj, node);
				}
				else if (direction.equals(YosysJSONUtils.S_OUTPUT)) {
					inputNetlistNode.put(intObj, node);	
				}
			}
		}
	}
	
	/**
	 *  Return a string representing the marshalled name of the instance using the name defined by parameter <i>name</i> and
	 *  the flag defined by parameter <i>hidename</i>.
	 *  
	 *  @param name the name
	 *  @param hidename flag to enable name marshalling
	 *  @return the marshalled name
	 */
	static protected String getName(String name, int hidename){
		String rtn = name;
		if (hidename != 0) {
			int index = rtn.lastIndexOf(YosysJSONUtils.S_NAME_DELIMETER); 
			if (index > -1) {
				rtn = rtn.substring(index);
			}
		}
		return rtn;
	}

	/**
	 *  Creates NetlistNode instances for the cell(s) from the top module defined in parameter <i>jsonValue</i>.<br>
	 *  Updates the map of integer to input netlistNode defined by parameter <i>inputNetlistNode</i>.<br>
	 *  Updates the map of integer to output netlistNode defined by parameter <i>outputNetlistNode</i>.<br>
	 *  Updates the Netlist defined by parameter <i>netlist</i>.<br>
	 *  
	 *  @param jsonValue the JSONObject
	 *  @param inputNetlistNode the map of integer to input netlistNode
	 *  @param outputNetlistNode the map of integer to output netlistNode
	 *  @param netlist the netlist
	 */
	static protected void parseCells(JSONObject jsonValue,
			Multimap<Integer, NetlistNode> inputNetlistNode,
			Multimap<Integer, NetlistNode> outputNetlistNode,
			Netlist netlist){
		JSONObject jCells = (JSONObject) jsonValue.get(YosysJSONUtils.S_CELLS);
		@SuppressWarnings("unchecked")
		Set<Map.Entry<?,?>> entrySet = (Set<Map.Entry<?,?>>) jCells.entrySet();
		Iterator<?> iter = entrySet.iterator();
		while (iter.hasNext()) {
			Map.Entry<?,?> item = (Map.Entry<?, ?>) iter.next();
			String name = (String) item.getKey();
			NetlistNode node = new NetlistNode();
			netlist.addVertex(node);
			JSONObject value = (JSONObject) item.getValue();
			// name
			int hidename = ProfileUtils.getInteger(value, YosysJSONUtils.S_HIDENAME);
			name = getName(name, hidename);
			node.setName(name);
			// type
			String type = ProfileUtils.getString(value, YosysJSONUtils.S_TYPE);
			type = YosysUtils.getNodeType(type);
			node.getResultNetlistNodeData().setNodeType(type);
			// port_directions
			JSONObject port_directions = (JSONObject) value.get(YosysJSONUtils.S_PORT_DIRECTIONS);
			// connections
			JSONObject connections = (JSONObject) value.get(YosysJSONUtils.S_CONNECTIONS);
			@SuppressWarnings("unchecked")
			// for each port:
			// get direction
			// get connection index
			Set<Map.Entry<?,?>> portDirectionSet = (Set<Map.Entry<?,?>>) port_directions.entrySet();
			Iterator<?> pditer = portDirectionSet.iterator();
			while (pditer.hasNext()) {
				Map.Entry<?,?> pditem = (Map.Entry<?, ?>) pditer.next();
				String pdname = (String) pditem.getKey();
				JSONArray JArray = (JSONArray) connections.get(pdname);
				String pdvalue = (String) pditem.getValue();
				if (pdvalue.equals(YosysJSONUtils.S_INPUT)) {
					// connections
					for (int i = 0; i < JArray.size(); i++) {
						Object element = (Object) JArray.get(i);
						Integer intObj = ProfileUtils.getInteger(element);
						inputNetlistNode.put(intObj, node);	
					}
				}
				else if (pdvalue.equals(YosysJSONUtils.S_OUTPUT)) {
					// connections
					for (int i = 0; i < JArray.size(); i++) {
						Object element = (Object) JArray.get(i);
						Integer intObj = ProfileUtils.getInteger(element);
						outputNetlistNode.put(intObj, node);	
					}	
				}
			}
		}
	}

	/**
	 *  Creates NetlistEdge instances for the net(s) from the top module defined in parameter <i>jsonValue</i>.<br>
	 *  Updates the map of integer to input netlistNode defined by parameter <i>inputNetlistNode</i>.<br>
	 *  Updates the map of integer to output netlistNode defined by parameter <i>outputNetlistNode</i>.<br>
	 *  Updates the Netlist defined by parameter <i>netlist</i>.<br>
	 *  
	 *  @param jsonValue the JSONObject
	 *  @param inputNetlistNode the map of integer to input netlistNode
	 *  @param outputNetlistNode the map of integer to output netlistNode
	 *  @param netlist the netlist
	 */
	static protected void parseNets(JSONObject jsonValue,
			Multimap<Integer, NetlistNode> inputNetlistNode,
			Multimap<Integer, NetlistNode> outputNetlistNode,
			Netlist netlist){
		JSONObject jNets = (JSONObject) jsonValue.get(YosysJSONUtils.S_NETS);
		Set<Integer> netSet = new HashSet<Integer>();
		@SuppressWarnings("unchecked")
		Set<Map.Entry<?,?>> entrySet = (Set<Map.Entry<?,?>>) jNets.entrySet();
		Iterator<?> iter = entrySet.iterator();
		while (iter.hasNext()) {
			Map.Entry<?,?> item = (Map.Entry<?, ?>) iter.next();
			String name = (String) item.getKey();
			JSONObject value = (JSONObject) item.getValue();
			// name
			int hidename = ProfileUtils.getInteger(value, YosysJSONUtils.S_HIDENAME);
			name = getName(name, hidename);
			// bits
			JSONArray JArray = (JSONArray) value.get(YosysJSONUtils.S_BITS);
			for (int i = 0; i < JArray.size(); i ++) {
				Object element = (Object) JArray.get(i);
				Integer intObj = ProfileUtils.getInteger(element);
				// skip net if already assigned
				if (netSet.contains(intObj)) {
					continue;
				}
				netSet.add(intObj);
				// outputNetlistNode
				Collection<NetlistNode> nodes = outputNetlistNode.get(intObj);
				NetlistNode srcNode = null;				
				if (nodes.size() != 1) {
					throw new RuntimeException("Error with outputNetlistNode!");
				}
				srcNode = nodes.iterator().next();
				// inputNetlistNode
				nodes = inputNetlistNode.get(intObj);
				NetlistNode dstNode = null;				
				Iterator<NetlistNode> niter = nodes.iterator();
				int temp = 0;
				while (niter.hasNext()) {
					dstNode = niter.next();
					NetlistEdge edge = new NetlistEdge();
					edge.setName(name + YosysJSONUtils.S_NET_SEPARATOR + Integer.toString(temp));
					netlist.addEdge(edge);
					srcNode.addOutEdge(edge);
					edge.setSrc(srcNode);
					dstNode.addInEdge(edge);
					edge.setDst(dstNode);
					temp++;
				}
			}
		}
	}
	
	/**
	 *  Translates the JSONObject referenced by parameter <i>jsonTop</i> into
	 *  the Netlist in parameter <i>netlist</i> 
	 *  using the <i>Yosys</i> algorithm instance.
	 *  
	 *  @param jsonTop the JSONObject
	 *  @param netlist the netlist
	 *  @param yosys the <i>Yosys</i> algorithm instance
	 */
	static public void parseJSON(JSONObject jsonTop, Netlist netlist, Yosys yosys){
		Multimap<Integer, NetlistNode> inputNetlistNode = ArrayListMultimap.create();
		Multimap<Integer, NetlistNode> outputNetlistNode = ArrayListMultimap.create();
		JSONObject jsonValue = YosysJSONUtils.parseModuleName(jsonTop, netlist);
		YosysJSONUtils.parseModule(jsonValue, inputNetlistNode, outputNetlistNode, netlist);
		YosysJSONUtils.parseCells(jsonValue, inputNetlistNode, outputNetlistNode, netlist);
		YosysJSONUtils.parseNets(jsonValue, inputNetlistNode, outputNetlistNode, netlist);
	}

	static private String S_MODULES = "modules";
	static private String S_CELLS = "cells";
	static private String S_NETS = "netnames";
	static private String S_PORTS = "ports";
	static private String S_INPUT = "input";
	static private String S_OUTPUT = "output";
	static private String S_DIRECTION = "direction";
	static private String S_BITS = "bits";
	static private String S_TYPE = "type";
	static private String S_HIDENAME = "hide_name";
	static private String S_NAME_DELIMETER = "$";
	static private String S_PORT_DIRECTIONS = "port_directions";
	static private String S_CONNECTIONS = "connections";
	static private String S_NET_SEPARATOR = "_";
}

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

package org.cellocad.v2.logicSynthesis.algorithm.Yosys;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
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

/**
 * Utility methods for the <i>Yosys</i> instances.
 *
 * @author Vincent Mirian
 *
 * @date 2018-05-21
 */
public class YosysJsonUtils {

  /**
   * Translates the JSON file referenced by parameter {@code filename} into the Netlist in parameter
   * {@code netlist} using the <i>Yosys</i> algorithm instance.
   *
   * @param yosys    The <i>Yosys</i> algorithm instance.
   * @param filename The JSON file.
   * @param netlist  The netlist.
   * @throws RuntimeException if: <br>
   *                          Any of the parameters are null<br>
   *                          Error accessing {@code filename}<br>
   *                          Error parsing {@code filename}<br>
   *                          .
   */
  public static void getNetlistFromYosysJsonFile(final Yosys yosys, final String filename,
      final Netlist netlist) {
    Utils.isNullRuntimeException(yosys, "yosys");
    Utils.isNullRuntimeException(filename, "filename");
    Utils.isNullRuntimeException(netlist, "netlist");
    // get JSON File
    final File jsonFile = new File(filename);
    Reader jsonReader = null;
    JSONObject jsonTop = null;
    // Create File Reader
    try {
      jsonReader = new FileReader(jsonFile);
    } catch (final FileNotFoundException e) {
      throw new RuntimeException("Error with file: " + jsonFile);
    }
    // Create JSON object from File Reader
    final JSONParser parser = new JSONParser();
    try {
      jsonTop = (JSONObject) parser.parse(jsonReader);
    } catch (final IOException e) {
      throw new RuntimeException("File IO Exception for: " + jsonFile + ".");
    } catch (final ParseException e) {
      throw new RuntimeException("Parser Exception for: " + jsonFile + ".");
    }
    // Create netlist
    YosysJsonUtils.parseJson(jsonTop, netlist, yosys);
    try {
      jsonReader.close();
    } catch (final IOException e) {
      throw new RuntimeException("Error with file: " + jsonFile);

    }
  }

  /**
   * Copies the name of the top module defined in parameter {@code jsonTop} into the Netlist in
   * parameter {@code netlist}. Returns the JSONObject representing value of the the top module.
   *
   * @param jsonTop The JSONObject.
   * @param netlist The netlist.
   * @return The JSONObject representing value of the the top module.
   */
  protected static JSONObject parseModuleName(final JSONObject jsonTop, final Netlist netlist) {
    JSONObject rtn = null;
    final JSONObject jModules = (JSONObject) jsonTop.get(YosysJsonUtils.S_MODULES);
    if (jModules.keySet().size() != 1) {
      throw new RuntimeException("More than one module!");
    }
    @SuppressWarnings("unchecked")
    final Set<Map.Entry<?, ?>> entrySet = jModules.entrySet();
    final String name = (String) entrySet.iterator().next().getKey();
    netlist.setName(name);
    rtn = (JSONObject) entrySet.iterator().next().getValue();
    return rtn;
  }

  /**
   * Creates NetlistNode instances for the port(s) from the top module defined in parameter
   * {@code jsonValue}.<br>
   * Updates the S_MAP of integer to input netlistNode defined by parameter
   * {@code inputNetlistNode}.<br>
   * Updates the S_MAP of integer to output netlistNode defined by parameter
   * {@code outputNetlistNode}.<br>
   * Updates the Netlist defined by parameter {@code netlist}.<br>
   * .
   *
   * @param jsonValue         The JSONObject.
   * @param inputNetlistNode  The S_MAP of integer to input netlistNode.
   * @param outputNetlistNode The S_MAP of integer to output netlistNode.
   * @param netlist           The netlist.
   */
  protected static void parseModule(final JSONObject jsonValue,
      final Multimap<Integer, NetlistNode> inputNetlistNode,
      final Multimap<Integer, NetlistNode> outputNetlistNode, final Netlist netlist) {
    final JSONObject jPorts = (JSONObject) jsonValue.get(YosysJsonUtils.S_PORTS);
    @SuppressWarnings("unchecked")
    final Set<Map.Entry<?, ?>> entrySet = jPorts.entrySet();
    final Iterator<?> iter = entrySet.iterator();
    while (iter.hasNext()) {
      final Map.Entry<?, ?> item = (Map.Entry<?, ?>) iter.next();
      final String name = (String) item.getKey();
      final NetlistNode node = new NetlistNode();
      netlist.addVertex(node);
      node.setName(name);
      final JSONObject value = (JSONObject) item.getValue();
      // direction
      final String direction = ProfileUtils.getString(value, YosysJsonUtils.S_DIRECTION);
      if (direction.equals(YosysJsonUtils.S_INPUT)) {
        node.getResultNetlistNodeData().setNodeType(LSResults.S_PRIMARYINPUT);
        node.setVertexType(VertexType.SOURCE);
      } else if (direction.equals(YosysJsonUtils.S_OUTPUT)) {
        node.getResultNetlistNodeData().setNodeType(LSResults.S_PRIMARYOUTPUT);
        node.setVertexType(VertexType.SINK);
      } else {
        throw new RuntimeException("Unknown direction!");
      }
      // bits
      final JSONArray JArray = (JSONArray) value.get(YosysJsonUtils.S_BITS);
      for (int i = 0; i < JArray.size(); i++) {
        final Object element = JArray.get(i);
        final Integer intObj = ProfileUtils.getInteger(element);
        if (direction.equals(YosysJsonUtils.S_INPUT)) {
          outputNetlistNode.put(intObj, node);
        } else if (direction.equals(YosysJsonUtils.S_OUTPUT)) {
          inputNetlistNode.put(intObj, node);
        }
      }
    }
  }

  /**
   * Return a string representing the marshalled name of the instance using the name defined by
   * parameter {@code name} and the flag defined by parameter {@code hidename}.
   *
   * @param name     The name.
   * @param hidename flag to enable name marshalling.
   * @return The marshalled name.
   */
  protected static String getName(final String name, final int hidename) {
    String rtn = name;
    if (hidename != 0) {
      final int index = rtn.lastIndexOf(YosysJsonUtils.S_NAME_DELIMETER);
      if (index > -1) {
        rtn = rtn.substring(index);
      }
    }
    return rtn;
  }

  /**
   * Creates NetlistNode instances for the cell(s) from the top module defined in parameter
   * {@code jsonValue}.<br>
   * Updates the S_MAP of integer to input netlistNode defined by parameter
   * {@code inputNetlistNode}.<br>
   * Updates the S_MAP of integer to output netlistNode defined by parameter
   * {@code outputNetlistNode}.<br>
   * Updates the Netlist defined by parameter {@code netlist}.<br>
   * .
   *
   * @param jsonValue         The JSONObject.
   * @param inputNetlistNode  The S_MAP of integer to input netlistNode.
   * @param outputNetlistNode The S_MAP of integer to output netlistNode.
   * @param netlist           The netlist.
   */
  protected static void parseCells(final JSONObject jsonValue,
      final Multimap<Integer, NetlistNode> inputNetlistNode,
      final Multimap<Integer, NetlistNode> outputNetlistNode, final Netlist netlist) {
    final JSONObject jCells = (JSONObject) jsonValue.get(YosysJsonUtils.S_CELLS);
    @SuppressWarnings("unchecked")
    final Set<Map.Entry<?, ?>> entrySet = jCells.entrySet();
    final Iterator<?> iter = entrySet.iterator();
    while (iter.hasNext()) {
      final Map.Entry<?, ?> item = (Map.Entry<?, ?>) iter.next();
      String name = (String) item.getKey();
      final NetlistNode node = new NetlistNode();
      netlist.addVertex(node);
      final JSONObject value = (JSONObject) item.getValue();
      // name
      final int hidename = ProfileUtils.getInteger(value, YosysJsonUtils.S_HIDENAME);
      name = YosysJsonUtils.getName(name, hidename);
      node.setName(name);
      // type
      String type = ProfileUtils.getString(value, YosysJsonUtils.S_TYPE);
      type = YosysUtils.getNodeType(type);
      node.getResultNetlistNodeData().setNodeType(type);
      // port_directions
      final JSONObject port_directions = (JSONObject) value.get(YosysJsonUtils.S_PORT_DIRECTIONS);
      // connections
      final JSONObject connections = (JSONObject) value.get(YosysJsonUtils.S_CONNECTIONS);
      @SuppressWarnings("unchecked")
      final Set<Map.Entry<?, ?>> portDirectionSet = port_directions.entrySet();
      final Iterator<?> pditer = portDirectionSet.iterator();
      while (pditer.hasNext()) {
        final Map.Entry<?, ?> pditem = (Map.Entry<?, ?>) pditer.next();
        final String pdname = (String) pditem.getKey();
        final JSONArray JArray = (JSONArray) connections.get(pdname);
        final String pdvalue = (String) pditem.getValue();
        if (pdvalue.equals(YosysJsonUtils.S_INPUT)) {
          // connections
          for (int i = 0; i < JArray.size(); i++) {
            final Object element = JArray.get(i);
            final Integer intObj = ProfileUtils.getInteger(element);
            inputNetlistNode.put(intObj, node);
          }
        } else if (pdvalue.equals(YosysJsonUtils.S_OUTPUT)) {
          // connections
          for (int i = 0; i < JArray.size(); i++) {
            final Object element = JArray.get(i);
            final Integer intObj = ProfileUtils.getInteger(element);
            outputNetlistNode.put(intObj, node);
          }
        }
      }
    }
  }

  /**
   * Creates NetlistEdge instances for the net(s) from the top module defined in parameter
   * {@code jsonValue}.<br>
   * Updates the S_MAP of integer to input netlistNode defined by parameter
   * {@code inputNetlistNode}.<br>
   * Updates the S_MAP of integer to output netlistNode defined by parameter
   * {@code outputNetlistNode}.<br>
   * Updates the Netlist defined by parameter {@code netlist}.<br>
   * .
   *
   * @param jsonValue         The JSONObject.
   * @param inputNetlistNode  The S_MAP of integer to input netlistNode.
   * @param outputNetlistNode The S_MAP of integer to output netlistNode.
   * @param netlist           The netlist.
   */
  protected static void parseNets(final JSONObject jsonValue,
      final Multimap<Integer, NetlistNode> inputNetlistNode,
      final Multimap<Integer, NetlistNode> outputNetlistNode, final Netlist netlist) {
    final JSONObject jNets = (JSONObject) jsonValue.get(YosysJsonUtils.S_NETS);
    final Set<Integer> netSet = new HashSet<>();
    @SuppressWarnings("unchecked")
    final Set<Map.Entry<?, ?>> entrySet = jNets.entrySet();
    final Iterator<?> iter = entrySet.iterator();
    while (iter.hasNext()) {
      final Map.Entry<?, ?> item = (Map.Entry<?, ?>) iter.next();
      String name = (String) item.getKey();
      final JSONObject value = (JSONObject) item.getValue();
      // name
      final int hidename = ProfileUtils.getInteger(value, YosysJsonUtils.S_HIDENAME);
      name = YosysJsonUtils.getName(name, hidename);
      // bits
      final JSONArray JArray = (JSONArray) value.get(YosysJsonUtils.S_BITS);
      for (int i = 0; i < JArray.size(); i++) {
        final Object element = JArray.get(i);
        final Integer intObj = ProfileUtils.getInteger(element);
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
        final Iterator<NetlistNode> niter = nodes.iterator();
        int temp = 0;
        while (niter.hasNext()) {
          dstNode = niter.next();
          final NetlistEdge edge = new NetlistEdge();
          edge.setName(name + YosysJsonUtils.S_NET_SEPARATOR + Integer.toString(temp));
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
   * Translates the JSONObject referenced by parameter {@code jsonTop} into the Netlist in parameter
   * {@code netlist} using the <i>Yosys</i> algorithm instance.
   *
   * @param jsonTop The JSONObject.
   * @param netlist The netlist.
   * @param yosys   The <i>Yosys</i> algorithm instance.
   */
  public static void parseJson(final JSONObject jsonTop, final Netlist netlist, final Yosys yosys) {
    final Multimap<Integer, NetlistNode> inputNetlistNode = ArrayListMultimap.create();
    final Multimap<Integer, NetlistNode> outputNetlistNode = ArrayListMultimap.create();
    final JSONObject jsonValue = YosysJsonUtils.parseModuleName(jsonTop, netlist);
    YosysJsonUtils.parseModule(jsonValue, inputNetlistNode, outputNetlistNode, netlist);
    YosysJsonUtils.parseCells(jsonValue, inputNetlistNode, outputNetlistNode, netlist);
    YosysJsonUtils.parseNets(jsonValue, inputNetlistNode, outputNetlistNode, netlist);
  }

  private static String S_MODULES = "modules";
  private static String S_CELLS = "cells";
  private static String S_NETS = "netnames";
  private static String S_PORTS = "ports";
  private static String S_INPUT = "input";
  private static String S_OUTPUT = "output";
  private static String S_DIRECTION = "direction";
  private static String S_BITS = "bits";
  private static String S_TYPE = "type";
  private static String S_HIDENAME = "hide_name";
  private static String S_NAME_DELIMETER = "$";
  private static String S_PORT_DIRECTIONS = "port_directions";
  private static String S_CONNECTIONS = "connections";
  private static String S_NET_SEPARATOR = "_";

}

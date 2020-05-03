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

package org.cellocad.v2.common.netlist;

import java.io.IOException;
import java.io.Writer;
import org.cellocad.v2.common.algorithm.data.NetlistData;
import org.cellocad.v2.common.graph.graph.GraphTemplate;
import org.cellocad.v2.common.json.JsonUtils;
import org.cellocad.v2.common.netlist.data.StageNetlistData;
import org.cellocad.v2.common.profile.ProfileUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 * The NetlistEdge class is a class representing the user design.
 *
 * @author Vincent Mirian
 *
 * @date Nov 17, 2017
 */
public class Netlist extends GraphTemplate<NetlistNode, NetlistEdge> {

  private void setDefault() {
    setInputFilename(null);
  }

  /**
   * Initializes a newly created {@link Netlist}.
   */
  public Netlist() {
    super();
    setDefault();
  }

  /**
   * Initializes a newly created {@link Netlist} using the parameter {@code jsonObj}.
   *
   * @param jsonObj The JavaScript Object Notation (JSON) representation of the Netlist Object.
   */
  public Netlist(final JSONObject jsonObj) {
    this();
    parse(jsonObj);
  }

  /*
   * Parse
   */
  private void parseName(final JSONObject jsonObj) {
    final String name = ProfileUtils.getString(jsonObj, "name");
    if (name != null) {
      setName(name);
    }
  }

  private void parseInputFilename(final JSONObject jsonObj) {
    final String inputFilename = ProfileUtils.getString(jsonObj, "inputFilename");
    if (inputFilename != null) {
      setInputFilename(inputFilename);
    }
  }

  private void parseNetlistNodes(final JSONObject jsonObj) {
    JSONArray jsonArr;
    jsonArr = (JSONArray) jsonObj.get("nodes");
    if (jsonArr == null) {
      throw new RuntimeException("'nodes' missing in Netlist!");
    }
    for (int i = 0; i < jsonArr.size(); i++) {
      final JSONObject nodeObj = (JSONObject) jsonArr.get(i);
      final NetlistNode node = new NetlistNode(nodeObj);
      addVertex(node);
    }
  }

  private NetlistNode getNetlistNode(final JSONObject jsonObj, final String str) {
    NetlistNode rtn = null;
    String name = null;
    name = ProfileUtils.getString(jsonObj, str);
    if (name == null) {
      throw new RuntimeException("No name for" + str + "edges in Netlist!");
    }
    rtn = getVertexByName(name);
    if (rtn == null) {
      throw new RuntimeException("Node missing in Netlist " + name + ".");
    }
    return rtn;
  }

  private void parseNetlistEdges(final JSONObject jsonObj) {
    JSONArray jsonArr;
    NetlistNode node = null;
    jsonArr = (JSONArray) jsonObj.get("edges");
    if (jsonArr == null) {
      throw new RuntimeException("'edges' missing in Netlist!");
    }
    for (int i = 0; i < jsonArr.size(); i++) {
      final JSONObject edgeObj = (JSONObject) jsonArr.get(i);
      final NetlistEdge edge = new NetlistEdge(edgeObj);
      addEdge(edge);
      node = getNetlistNode(edgeObj, "src");
      node.addOutEdge(edge);
      edge.setSrc(node);
      node = getNetlistNode(edgeObj, "dst");
      node.addInEdge(edge);
      edge.setDst(node);
    }
  }

  private void parse(final JSONObject jsonObj) {
    parseName(jsonObj);
    parseInputFilename(jsonObj);
    parseNetlistNodes(jsonObj);
    parseNetlistEdges(jsonObj);
  }

  /*
   * WriteJSON
   */
  /**
   * Returns a string containing the header in JSON format of this instance.
   *
   * @return A string containing the header in JSON format of this instance.
   */
  protected String getJsonHeader() {
    String rtn = "";
    // name
    rtn += JsonUtils.getEntryToString("name", getName());
    // inputFilename
    rtn += JsonUtils.getEntryToString("inputFilename", getInputFilename());
    return rtn;
  }

  /**
   * Returns a string containing the footer in JSON format of this instance.
   *
   * @return A string containing the footer in JSON format of this instance.
   */
  protected String getJsonFooter() {
    final String rtn = "";
    return rtn;
  }

  /**
   * Writes this instance in JSON format to the writer defined by parameter {@code os} with the
   * number of indents equivalent to the parameter {@code indent}.
   *
   * @param indent The number of indents.
   * @param os     The writer.
   * @throws IOException If an I/O error occurs.
   */
  public void writeJson(final int indent, final Writer os) throws IOException {
    String str = null;
    // header
    str = getJsonHeader();
    str = JsonUtils.addIndent(indent, str);
    os.write(str);
    // nodes
    str = JsonUtils.getStartArrayWithMemberString("nodes");
    str = JsonUtils.addIndent(indent, str);
    os.write(str);
    for (int i = 0; i < getNumVertex(); i++) {
      str = JsonUtils.addIndent(indent + 1, JsonUtils.getStartEntryString());
      os.write(str);
      getVertexAtIdx(i).writeJson(indent + 2, os);
      str = JsonUtils.addIndent(indent + 1, JsonUtils.getEndEntryString());
      os.write(str);
    }
    str = JsonUtils.getEndArrayString();
    str = JsonUtils.addIndent(indent, str);
    os.write(str);
    // edges
    str = JsonUtils.getStartArrayWithMemberString("edges");
    str = JsonUtils.addIndent(indent, str);
    os.write(str);
    for (int i = 0; i < getNumEdge(); i++) {
      str = JsonUtils.addIndent(indent + 1, JsonUtils.getStartEntryString());
      os.write(str);
      getEdgeAtIdx(i).writeJson(indent + 2, os);
      str = JsonUtils.addIndent(indent + 1, JsonUtils.getEndEntryString());
      os.write(str);
    }
    str = JsonUtils.getEndArrayString();
    str = JsonUtils.addIndent(indent, str);
    os.write(str);
    // footer
    str = getJsonFooter();
    str = JsonUtils.addIndent(indent, str);
    os.write(str);
  }

  /**
   * Return a newly created {@link NetlistNode} object with its contents set to those of parameter
   * {@code other}.
   *
   * @param other The other NetlistNode.
   * @return A newly created {@link NetlistNode} object with its contents set to those of parameter
   *         {@code other}.
   */
  @Override
  public NetlistNode createV(final NetlistNode other) {
    final NetlistNode rtn = new NetlistNode(other);
    return rtn;
  }

  /**
   * Return a newly created {@link NetlistEdge} object with its contents set to those of parameter
   * {@code other}.
   *
   * @param other The other NetlistEdge.
   * @return A newly created {@link NetlistEdge} object with its contents set to those of parameter
   *         {@code other}.
   */
  @Override
  public NetlistEdge createE(final NetlistEdge other) {
    final NetlistEdge rtn = new NetlistEdge(other);
    return rtn;
  }

  /*
   * inputFilename
   */
  /**
   * Setter for {@code inputFilename}.
   *
   * @param inputFilename The value to set {@code inputFilename}.
   */
  public void setInputFilename(final String inputFilename) {
    this.inputFilename = inputFilename;
  }

  /**
   * Getter for {@code inputFilename}.
   *
   * @return The inputFilename of this instance.
   */
  public String getInputFilename() {
    return inputFilename;
  }

  private String inputFilename;

  /*
   * NetlistData
   */
  /**
   * Setter for {@code netlistData}.
   *
   * @param nData The NetlistData to set <i>netlistData</i>.
   */
  public void setNetlistData(final NetlistData nData) {
    netlistData = nData;
  }

  /**
   * Getter for {@code netlistData}.
   *
   * @return The NetlistData of this instance.
   */
  public NetlistData getNetlistData() {
    return netlistData;
  }

  private NetlistData netlistData;

  /*
   * StageNetlistData
   */
  /**
   * Setter for {@code stageNetlistData}.
   *
   * @param nData The StageNetlistData to set <i>stageNetlistData</i>.
   */
  public void setStageNetlistData(final StageNetlistData nData) {
    stageNetlistData = nData;
  }

  /**
   * Getter for {@code stageNetlistData}.
   *
   * @return The StageNetlistData of this instance.
   */
  public StageNetlistData getStageNetlistData() {
    return stageNetlistData;
  }

  private StageNetlistData stageNetlistData;

}

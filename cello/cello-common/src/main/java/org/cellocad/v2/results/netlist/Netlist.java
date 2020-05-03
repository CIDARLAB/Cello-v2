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

package org.cellocad.v2.results.netlist;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import java.io.IOException;
import java.io.Writer;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import org.cellocad.v2.common.Utils;
import org.cellocad.v2.common.algorithm.data.NetlistData;
import org.cellocad.v2.common.graph.graph.GraphTemplate;
import org.cellocad.v2.common.json.JsonUtils;
import org.cellocad.v2.common.netlist.data.StageNetlistData;
import org.cellocad.v2.common.profile.ProfileUtils;
import org.cellocad.v2.results.logicSynthesis.LSResultsUtils;
import org.cellocad.v2.results.netlist.data.ResultNetlistData;
import org.cellocad.v2.results.netlist.data.ResultNetlistEdgeData;
import org.cellocad.v2.results.netlist.data.ResultNetlistNodeData;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 * The Netlist class is a class representing the netlist of the project.
 *
 * @author Vincent Mirian
 *
 * @date 2018-05-21
 */
public class Netlist extends GraphTemplate<NetlistNode, NetlistEdge> {

  private void setDefault() {
    setInputFilename(null);
    setResultNetlistData(new ResultNetlistData());
  }

  /**
   * Initializes a newly created {@link Netlist} with its node(s) and edge(s) cloned from those of
   * parameter {@code other}.
   *
   * @param other The other Netlist.
   */
  public Netlist(final Netlist other) {
    super(other);
    for (int i = 0; i < other.getNumVertex(); i++) {
      final NetlistNode node = other.getVertexAtIdx(i);
      final NetlistNode mynode = getVertexByName(node.getName());
      mynode.setResultNetlistNodeData(new ResultNetlistNodeData(node.getResultNetlistNodeData()));
    }
    for (int i = 0; i < other.getNumEdge(); i++) {
      final NetlistEdge edge = other.getEdgeAtIdx(i);
      final NetlistEdge myedge = getEdgeByName(edge.getName());
      myedge.setResultNetlistEdgeData(new ResultNetlistEdgeData(edge.getResultNetlistEdgeData()));
    }
    setInputFilename(other.getInputFilename());
    setResultNetlistData(new ResultNetlistData(other.getResultNetlistData()));
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
   * @param jsonObj The JavaScript Object Notation (JSON) representation of the {@link Netlist}
   *                object.
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
    getResultNetlistData().parse(jsonObj);
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
   * @throws IOException If an I/O error occurs
   */
  public void writeJson(final int indent, final Writer os) throws IOException {
    String str = null;
    // header
    str = getJsonHeader();
    str = JsonUtils.addIndent(indent, str);
    os.write(str);
    // data
    getResultNetlistData().writeJson(indent, os);
    /*
     * StageNetlistData sdata = this.getStageNetlistData(); if (sdata != null) {
     * sdata.writeJSON(indent, os); } NetlistData data = this.getNetlistData(); if (data != null) {
     * data.writeJSON(indent, os); }.
     */
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

  // TODO: TEST!
  protected String getDotVerticesPrefixRankInternalNodes() {
    final String rtn = "";
    final Queue<NetlistNode> BFS = new LinkedList<>();
    final Set<Integer> rankSet = new HashSet<>();
    final ListMultimap<NetlistNode, Integer> nodeRankMap = ArrayListMultimap.create();
    // for each node
    for (int i = 0; i < getNumVertex(); i++) {
      final NetlistNode node = getVertexAtIdx(i);
      // get dst nodes of input nodes
      if (LSResultsUtils.isAllInput(node)) {
        for (int j = 0; j < node.getNumOutEdge(); j++) {
          final NetlistNode dstNode = node.getOutEdgeAtIdx(j).getDst();
          if (dstNode == null) {
            continue;
          }
          BFS.add(dstNode);
          nodeRankMap.put(dstNode, 0);
        }
      }
    }
    // set rank
    while (!BFS.isEmpty()) {
      final NetlistNode node = BFS.remove();
      int maxRank = -1;
      boolean assignedRank = true;
      for (int i = 0; i < node.getNumInEdge(); i++) {
        final NetlistNode srcNode = node.getInEdgeAtIdx(i).getSrc();
        if (srcNode == null) {
          continue;
        }
        final List<Integer> rankList = nodeRankMap.get(srcNode);
        if (rankList.size() == 0) {
          assignedRank = false;
          break;
        }
        assert rankList.size() < 2;
        if (rankList.size() >= 2) {
          throw new RuntimeException("Error with rank!");
        }
        final int myMax = rankList.get(0);
        if (myMax > maxRank) {
          maxRank = myMax;
        }
      }
      if (assignedRank) {
        rankSet.add(maxRank);
        nodeRankMap.put(node, maxRank);
      } else {
        BFS.add(node);
      }
    }
    return rtn;
  }

  /**
   * Returns a string that prefix the vertices declaration.
   *
   * @return A string that prefix the vertices declaration.
   */
  @Override
  protected String getDotVerticesPrefix() {
    String rtn = "";
    String inputs = "";
    String outputs = "";
    // add lines
    rtn += "splines=ortho" + Utils.getNewLine();
    // rank input and output
    for (int i = 0; i < getNumVertex(); i++) {
      final NetlistNode node = getVertexAtIdx(i);
      if (LSResultsUtils.isAllInput(node)) {
        inputs += "\"";
        inputs += node.getName();
        inputs += "\";";
      }
      if (LSResultsUtils.isAllOutput(node)) {
        outputs += "\"";
        outputs += node.getName();
        outputs += "\";";
      }
    }
    if (!inputs.isEmpty()) {
      rtn += "{rank = same; ";
      rtn += inputs;
      rtn += "}" + Utils.getNewLine();
    }
    if (!outputs.isEmpty()) {
      rtn += "{rank = same; ";
      rtn += outputs;
      rtn += "}" + Utils.getNewLine();
    }
    // rank internal nodes
    // rtn += getDotVerticesPrefixRankInternalNodes();
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
   * ResultNetlistData
   */
  /**
   * Setter for {@code resultNetlistData}.
   *
   * @param nData The ResultNetlistData to set <i>resultNetlistData</i>.
   */
  public void setResultNetlistData(final ResultNetlistData nData) {
    resultNetlistData = nData;
  }

  /**
   * Getter for {@code resultNetlistData}.
   *
   * @return The ResultNetlistData of this instance.
   */
  public ResultNetlistData getResultNetlistData() {
    return resultNetlistData;
  }

  private ResultNetlistData resultNetlistData;

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

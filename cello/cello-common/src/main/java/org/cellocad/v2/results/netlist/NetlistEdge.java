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

import java.io.IOException;
import java.io.Writer;
import org.cellocad.v2.common.Utils;
import org.cellocad.v2.common.algorithm.data.NetlistEdgeData;
import org.cellocad.v2.common.graph.graph.EdgeTemplate;
import org.cellocad.v2.common.json.JsonUtils;
import org.cellocad.v2.common.netlist.data.StageNetlistEdgeData;
import org.cellocad.v2.common.profile.ProfileUtils;
import org.cellocad.v2.results.netlist.data.ResultNetlistEdgeData;
import org.json.simple.JSONObject;

/**
 * The NetlistEdge class is a class representing the edge(s) of the project.
 *
 * @author Vincent Mirian
 *
 * @date 2018-05-21
 */
public class NetlistEdge extends EdgeTemplate<NetlistNode> {

  private void setDefault() {
    setResultNetlistEdgeData(new ResultNetlistEdgeData());
  }

  /**
   * Initializes a newly created {@link NetlistEdge}.
   */
  public NetlistEdge() {
    super();
    setDefault();
  }

  /**
   * Initializes a newly created {@link NetlistEdge} with its source node defined by parameter
   * {@code src} and its destination node define by parameter {@code dst}.
   *
   * @param src The source node.
   * @param dst The destination node.
   */
  public NetlistEdge(final NetlistNode src, final NetlistNode dst) {
    super(src);
    setDst(dst);
    setDefault();
  }

  /**
   * Initializes a newly created {@link NetlistEdge} with its contents set to those of parameter
   * {@code other}.
   *
   * @param other The other NetlistEdge.
   */
  public NetlistEdge(final NetlistEdge other) {
    super(other);
    setSrc(other.getSrc());
    setDst(other.getDst());
    setDefault();
    setResultNetlistEdgeData(new ResultNetlistEdgeData(other.getResultNetlistEdgeData()));
  }

  /**
   * Initializes a newly created {@link NetlistEdge} using the parameter {@code jsonObj}.
   *
   * @param jsonObj The JavaScript Object Notation (JSON) representation of the {@link NetlistEdge}
   *                object.
   */
  public NetlistEdge(final JSONObject jsonObj) {
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

  private void parse(final JSONObject jsonObj) {
    parseName(jsonObj);
    getResultNetlistEdgeData().parse(jsonObj);
  }

  /*
   * Write
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
    // src
    rtn += JsonUtils.getEntryToString("src", getSrc().getName());
    // dst
    rtn += JsonUtils.getEntryToString("dst", getDst().getName());
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
    // data
    getResultNetlistEdgeData().writeJson(indent, os);
    /*
     * StageNetlistEdgeData sdata = this.getStageNetlistEdgeData(); if (sdata != null) {
     * sdata.writeJSON(indent, os); } NetlistEdgeData data = this.getNetlistEdgeData(); if (data !=
     * null) { data.writeJSON(indent, os); }.
     */
    // footer
    str = getJsonFooter();
    str = Utils.addIndent(indent, str);
    os.write(str);
  }

  /**
   * Returns a string containing this instance in DOT (graph description language) format.
   *
   * @return A string containing this instance in DOT (graph description language) format.
   */
  // TODO: this is hack, it should be in hypergraph Netlist
  // Recommend adding pins to netlist (but biologist think it is too confusing)
  @Override
  protected String getData() {
    String rtn = "";
    final NetlistNode src = getSrc();
    String srcName = src.getName();
    if (src.getNumOutEdge() > 1) {
      srcName = "\"" + srcName + "Point\":e";
    } else {
      srcName = "\"" + srcName + "\"";
    }
    final int numDst = getNumDst();
    if (src != null && numDst > 0) {
      for (int i = 0; i < numDst; i++) {
        rtn += srcName;
        rtn += " -> \"";
        rtn += getDstAtIdx(i).getName();
        rtn += "\"";
        rtn += Utils.getNewLine();
      }
    }
    return rtn;
  }

  /*
   * ResultNetlistData
   */
  /**
   * Setter for {@code resultNetlistData}.
   *
   * @param nData The ResultNetlistEdgeData to set <i>resultNetlistData</i>.
   */
  public void setResultNetlistEdgeData(final ResultNetlistEdgeData nData) {
    resultNetlistData = nData;
  }

  /**
   * Getter for {@code resultNetlistData}.
   *
   * @return The ResultNetlistEdgeData of this instance.
   */
  public ResultNetlistEdgeData getResultNetlistEdgeData() {
    return resultNetlistData;
  }

  private ResultNetlistEdgeData resultNetlistData;

  /*
   * NetlistData
   */
  /**
   * Setter for {@code netlistData}.
   *
   * @param nData The NetlistEdgeData to set <i>netlistData</i>.
   */
  public void setNetlistEdgeData(final NetlistEdgeData nData) {
    netlistData = nData;
  }

  /**
   * Getter for {@code netlistData}.
   *
   * @return The NetlistEdgeData of this instance.
   */
  public NetlistEdgeData getNetlistEdgeData() {
    return netlistData;
  }

  private NetlistEdgeData netlistData;

  /*
   * StageNetlistEdgeData
   */
  /**
   * Setter for {@code stageNetlistData}.
   *
   * @param nData The StageNetlistEdgeData to set <i>stageNetlistData</i>.
   */
  public void setStageNetlistEdgeData(final StageNetlistEdgeData nData) {
    stageNetlistData = nData;
  }

  /**
   * Getter for {@code stageNetlistData}.
   *
   * @return The StageNetlistEdgeData of this instance.
   */
  public StageNetlistEdgeData getStageNetlistEdgeData() {
    return stageNetlistData;
  }

  private StageNetlistEdgeData stageNetlistData;

}

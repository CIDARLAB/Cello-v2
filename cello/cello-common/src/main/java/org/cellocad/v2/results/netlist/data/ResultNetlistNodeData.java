/*
 * Copyright (C) 2017 Massachusetts Institute of Technology (MIT), Boston University (BU)
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

package org.cellocad.v2.results.netlist.data;

import java.io.IOException;
import java.io.Writer;
import org.cellocad.v2.common.Utils;
import org.cellocad.v2.common.application.data.ApplicationNetlistNodeData;
import org.cellocad.v2.common.json.JsonUtils;
import org.cellocad.v2.common.profile.ProfileUtils;
import org.cellocad.v2.common.target.data.data.AssignableDevice;
import org.cellocad.v2.results.clustering.CLResults;
import org.cellocad.v2.results.logicSynthesis.LSResults;
import org.cellocad.v2.results.partitioning.PTResults;
import org.cellocad.v2.results.technologyMapping.TMResults;
import org.json.simple.JSONObject;

/**
 * The ResultNetlistNodeData class contaiata for a node used within the project. ns all the d
 *
 * @author Vincent Mirian
 * @author Timothy Jones
 *
 * @date 2018-05-21
 */
public class ResultNetlistNodeData extends ApplicationNetlistNodeData {

  /**
   * Set Defaults.
   */
  private void setDefault() {
    setNodeType(LSResults.S_DEFAULT);
    setPartitionID(PTResults.S_DEFAULT);
    setClusterID(CLResults.S_DEFAULT);
    setDeviceName(TMResults.S_DEFAULT);
  }

  /**
   * Initializes a newly created {@link ResultNetlistNodeData}.
   */
  public ResultNetlistNodeData() {
    super();
    setDefault();
  }

  /**
   * Initializes a newly created {@link ResultNetlistNodeData} with its parameters cloned from those
   * of parameter {@code other}.
   *
   * @param other The other ResultNetlistNodeData.
   */
  public ResultNetlistNodeData(final ResultNetlistNodeData other) {
    super();
    setDefault();
    setNodeType(other.getNodeType());
    setPartitionID(other.getPartitionID());
    setDeviceName(other.getDeviceName());
  }

  /**
   * Initializes a newly created {@link ResultNetlistNodeData} using the parameter {@code jsonObj}.
   *
   * @param jsonObj The JavaScript Object Notation (JSON) representation of the
   *                ResultNetlistNodeData Object.
   */
  public ResultNetlistNodeData(final JSONObject jsonObj) {
    super();
    setDefault();
    parse(jsonObj);
  }

  /*
   * Write
   */
  /**
   * Returns a string containing the data of this instance.
   *
   * @return A string containing the data of this instance.
   */
  protected String getInfo() {
    String rtn = "";
    // nodeType
    rtn += JsonUtils.getEntryToString("nodeType", getNodeType());
    // partitionID
    rtn += JsonUtils.getEntryToString("partitionID", getPartitionID());
    // deviceName
    rtn += JsonUtils.getEntryToString("deviceName", getDeviceName());
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
  @Override
  public void writeJson(final int indent, final Writer os) throws IOException {
    String str = null;
    // header
    str = getInfo();
    str = JsonUtils.addIndent(indent, str);
    os.write(str);
  }

  /*
   * Parse
   */
  private void parseNodeType(final JSONObject jsonObj) {
    final String value = ProfileUtils.getString(jsonObj, "nodeType");
    if (value != null) {
      setNodeType(value);
    }
  }

  private void parsePartitionID(final JSONObject jsonObj) {
    final int value = ProfileUtils.getInteger(jsonObj, "partitionID");
    setPartitionID(value);
  }

  private void parseDevice(final JSONObject jsonObj) {
    final String value = ProfileUtils.getString(jsonObj, "deviceName");
    if (value != null) {
      setDeviceName(value);
    }
  }

  /**
   * Parses the data attached to this instance.
   *
   * @param jsonObj The JavaScript Object Notation (JSON) representation of the Project
   *                NetlistNodeData Object.
   */
  @Override
  public void parse(final JSONObject jsonObj) {
    parseNodeType(jsonObj);
    parsePartitionID(jsonObj);
    parseDevice(jsonObj);
  }

  /*
   * NodeType
   */
  /**
   * Setter for {@code nodeType}.
   *
   * @param nodeType The value to set {@code nodeType}.
   */
  public void setNodeType(final String nodeType) {
    this.nodeType = nodeType;
  }

  /**
   * Getter for {@code nodeType}.
   *
   * @return The nodeType of this instance.
   */
  public String getNodeType() {
    return nodeType;
  }

  private String nodeType;

  /*
   * PartitionID
   */
  /**
   * Setter for {@code partitionID}.
   *
   * @param partitionID The value to set {@code partitionID}.
   */
  public void setPartitionID(final int partitionID) {
    this.partitionID = partitionID;
  }

  /**
   * Getter for {@code partitionID}.
   *
   * @return The partitionID of this instance.
   */
  public int getPartitionID() {
    return partitionID;
  }

  private int partitionID;

  /*
   * ClusterID
   */
  /**
   * Setter for {@code clusterID}.
   *
   * @param clusterID The value to set {@code clusterID}.
   */
  public void setClusterID(final int clusterID) {
    this.clusterID = clusterID;
  }

  /**
   * Getter for {@code clusterID}.
   *
   * @return The clusterID of this instance.
   */
  public int getClusterID() {
    return clusterID;
  }

  private int clusterID;

  /*
   * GateType
   */
  /**
   * Setter for {@code deviceName}.
   *
   * @param deviceName The value to set {@code deviceName}.
   */
  public void setDeviceName(final String deviceName) {
    this.deviceName = deviceName;
  }

  /**
   * Getter for {@code deviceName}.
   *
   * @return The deviceName of this instance.
   */
  public String getDeviceName() {
    return deviceName;
  }

  private String deviceName;

  /**
   * Getter for {@code device}.
   *
   * @return The value of {@code device}.
   */
  public AssignableDevice getDevice() {
    return device;
  }

  /**
   * Setter for {@code device}.
   *
   * @param device The device to set.
   */
  public void setDevice(final AssignableDevice device) {
    this.device = device;
  }

  private AssignableDevice device;

  /**
   * Returns a string representation of the object.
   *
   * @return A string representation of the object.
   */
  @Override
  public String toString() {
    String rtn = "";
    String data = null;
    // NodeType
    data = getNodeType();
    if (!data.isEmpty()) {
      // modify
      rtn += Utils.getNewLine();
      rtn += data;
    }
    // GateType
    data = getDeviceName();
    if (!data.isEmpty()) {
      // modify
      rtn += Utils.getNewLine();
      rtn += data;
    }
    return rtn;
  }

}

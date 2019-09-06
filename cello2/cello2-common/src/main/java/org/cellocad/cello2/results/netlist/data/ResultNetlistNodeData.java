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
package org.cellocad.cello2.results.netlist.data;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;

import org.cellocad.cello2.common.Utils;
import org.cellocad.cello2.common.JSON.JSONUtils;
import org.cellocad.cello2.common.application.data.ApplicationNetlistNodeData;
import org.cellocad.cello2.common.profile.ProfileUtils;
import org.cellocad.cello2.results.clustering.CLResults;
import org.cellocad.cello2.results.logicSynthesis.LSResults;
import org.cellocad.cello2.results.partitioning.PTResults;
import org.cellocad.cello2.results.technologyMapping.TMResults;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 * The ResultNetlistNodeData class contaiata for a node used within the project.
 * ns all the d
 * 
 * @author Vincent Mirian
 * @author Timothy Jones
 * 
 * @date 2018-05-21
 *
 */
public class ResultNetlistNodeData extends ApplicationNetlistNodeData {

	/**
	 * Set Defaults
	 */
	private void setDefault() {
		this.setNodeType(LSResults.S_DEFAULT);
		this.setPartitionID(PTResults.S_DEFAULT);
		this.setClusterID(CLResults.S_DEFAULT);
		this.setGateType(TMResults.S_DEFAULT);
		this.setParts(new ArrayList<String>());
	}

	/**
	 * Initializes a newly created ResultNetlistNodeData
	 */
	public ResultNetlistNodeData() {
		super();
		this.setDefault();
	}

	/**
	 * Initializes a newly created ResultNetlistNodeData with its parameters cloned
	 * from those of parameter <i>other</i>.
	 * 
	 * @param other the other ResultNetlistNodeData
	 */
	public ResultNetlistNodeData(ResultNetlistNodeData other) {
		super();
		this.setDefault();
		this.setNodeType(other.getNodeType());
		this.setPartitionID(other.getPartitionID());
		this.setGateType(other.getGateType());
		this.setParts(other.getParts());
	}

	/**
	 * Initializes a newly created ResultNetlistNodeData using the parameter
	 * <i>JObj</i>.
	 * 
	 * @param JObj the JavaScript Object Notation (JSON) representation of the
	 *             ResultNetlistNodeData Object
	 */
	public ResultNetlistNodeData(final JSONObject JObj) {
		super();
		this.setDefault();
		this.parse(JObj);
	}

	/*
	 * Write
	 */
	/**
	 * Returns a string containing the data of this instance
	 * 
	 * @return a string containing the data of this instance
	 */
	protected String getInfo() {
		String rtn = "";
		// nodeType
		rtn += JSONUtils.getEntryToString("nodeType", this.getNodeType());
		// partitionID
		rtn += JSONUtils.getEntryToString("partitionID", this.getPartitionID());
		// gateType
		rtn += JSONUtils.getEntryToString("gateType", this.getGateType());
		// parts
		rtn += JSONUtils.getStartArrayWithMemberString("parts");
		String str = "";
		for (String part : this.getParts()) {
			str += JSONUtils.getValueToString(part);
		}
		str = JSONUtils.addIndent(1, str);
		rtn += str;
		rtn += JSONUtils.getEndArrayString();
		return rtn;
	}

	/**
	 * Writes this instance in JSON format to the writer defined by parameter
	 * <i>os</i> with the number of indents equivalent to the parameter
	 * <i>indent</i>
	 * 
	 * @param indent the number of indents
	 * @param os     the writer
	 * @throws IOException If an I/O error occurs
	 */
	public void writeJSON(int indent, Writer os) throws IOException {
		String str = null;
		// header
		str = this.getInfo();
		str = JSONUtils.addIndent(indent, str);
		os.write(str);
	}

	/*
	 * Parse
	 */
	private void parseNodeType(final JSONObject JObj) {
		String value = ProfileUtils.getString(JObj, "nodeType");
		if (value != null) {
			this.setNodeType(value);
		}
	}

	private void parsePartitionID(final JSONObject JObj) {
		int value = ProfileUtils.getInteger(JObj, "partitionID");
		this.setPartitionID(value);
	}

	private void parseGateType(final JSONObject JObj) {
		String value = ProfileUtils.getString(JObj, "gateType");
		if (value != null) {
			this.setGateType(value);
		}
	}

	private void parseParts(final JSONObject JObj) {
		JSONArray value = (JSONArray) ProfileUtils.getObject(JObj, "parts");
		if (value != null) {
			Collection<String> parts = new ArrayList<>();
			for (int i = 0; i < value.size(); i++) {
				parts.add((String) value.get(i));
			}
			this.setParts(parts);
		}
	}

	/**
	 * Parses the data attached to this instance
	 * 
	 * @param JObj the JavaScript Object Notation (JSON) representation of the
	 *             Project NetlistNodeData Object
	 */
	public void parse(final JSONObject JObj) {
		this.parseNodeType(JObj);
		this.parsePartitionID(JObj);
		this.parseGateType(JObj);
		this.parseParts(JObj);
	}

	/*
	 * NodeType
	 */
	/**
	 * Setter for <i>nodeType</i>
	 * 
	 * @param nodeType the value to set <i>nodeType</i>
	 */
	public void setNodeType(String nodeType) {
		this.nodeType = nodeType;
	}

	/**
	 * Getter for <i>nodeType</i>
	 * 
	 * @return the nodeType of this instance
	 */
	public String getNodeType() {
		return this.nodeType;
	}

	private String nodeType;

	/*
	 * PartitionID
	 */
	/**
	 * Setter for <i>partitionID</i>
	 * 
	 * @param partitionID the value to set <i>partitionID</i>
	 */
	public void setPartitionID(int partitionID) {
		this.partitionID = partitionID;
	}

	/**
	 * Getter for <i>partitionID</i>
	 * 
	 * @return the partitionID of this instance
	 */
	public int getPartitionID() {
		return this.partitionID;
	}

	private int partitionID;

	/*
	 * ClusterID
	 */
	/**
	 * Setter for <i>clusterID</i>
	 * 
	 * @param clusterID the value to set <i>clusterID</i>
	 */
	public void setClusterID(int clusterID) {
		this.clusterID = clusterID;
	}

	/**
	 * Getter for <i>clusterID</i>
	 * 
	 * @return the clusterID of this instance
	 */
	public int getClusterID() {
		return this.clusterID;
	}

	private int clusterID;

	/*
	 * GateType
	 */
	/**
	 * Setter for <i>gateType</i>
	 * 
	 * @param gateType the value to set <i>gateType</i>
	 */
	public void setGateType(String gateType) {
		this.gateType = gateType;
	}

	/**
	 * Getter for <i>gateType</i>
	 * 
	 * @return the gateType of this instance
	 */
	public String getGateType() {
		return this.gateType;
	}

	private String gateType;

	/*
	 * Parts
	 */
	/**
	 * Getter for <i>parts</i>
	 * 
	 * @return the parts of this instance
	 */
	public Collection<String> getParts() {
		return parts;
	}

	/**
	 * Setter for <i>parts</i>
	 * 
	 * @param parts the value to set <i>parts</i>
	 */
	public void setParts(Collection<String> parts) {
		this.parts = parts;
	}

	private Collection<String> parts;

	/**
	 * Returns a string representation of the object.
	 * 
	 * @return a string representation of the object.
	 */
	@Override
	public String toString() {
		String rtn = "";
		String data = null;
		// NodeType
		data = this.getNodeType();
		if (!data.isEmpty()) {
			// modify
			rtn += Utils.getNewLine();
			rtn += data;
		}
		// GateType
		data = this.getGateType();
		if (!data.isEmpty()) {
			// modify
			rtn += Utils.getNewLine();
			rtn += data;
		}
		return rtn;
	}
}

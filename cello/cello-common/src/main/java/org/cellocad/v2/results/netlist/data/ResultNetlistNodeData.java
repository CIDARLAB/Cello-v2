/**
 * Copyright (C) 2017
 * Massachusetts Institute of Technology (MIT)
 * Boston University (BU)
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
package org.cellocad.v2.results.netlist.data;

import java.io.IOException;
import java.io.Writer;

import org.cellocad.v2.common.Utils;
import org.cellocad.v2.common.JSON.JSONUtils;
import org.cellocad.v2.common.application.data.ApplicationNetlistNodeData;
import org.cellocad.v2.common.profile.ProfileUtils;
import org.cellocad.v2.common.target.data.component.AssignableDevice;
import org.cellocad.v2.results.clustering.CLResults;
import org.cellocad.v2.results.logicSynthesis.LSResults;
import org.cellocad.v2.results.partitioning.PTResults;
import org.cellocad.v2.results.technologyMapping.TMResults;
import org.json.simple.JSONObject;

/**
 * The ResultNetlistNodeData class contaiata for a node used within the project.
 * ns all the d
 * @author Vincent Mirian
 * @author Timothy Jones
 * 
 * @date 2018-05-21
 *
 */
public class ResultNetlistNodeData extends ApplicationNetlistNodeData{

	/**
	 *  Set Defaults
	 */
	private void setDefault() {
		this.setNodeType(LSResults.S_DEFAULT);
		this.setPartitionID(PTResults.S_DEFAULT);
		this.setClusterID(CLResults.S_DEFAULT);
		this.setDeviceName(TMResults.S_DEFAULT);
	}

	/**
	 *  Initializes a newly created ResultNetlistNodeData 
	 */
	public ResultNetlistNodeData(){
		super();
		this.setDefault();
	}

	/**
	 *  Initializes a newly created ResultNetlistNodeData with its parameters cloned from those of parameter <i>other</i>.
	 *  
	 *  @param other the other ResultNetlistNodeData
	 */
	public ResultNetlistNodeData(ResultNetlistNodeData other){
		super();
		this.setDefault();
		this.setNodeType(other.getNodeType());
		this.setPartitionID(other.getPartitionID());
		this.setDeviceName(other.getDeviceName());
	}

	/**
	 *  Initializes a newly created ResultNetlistNodeData using the parameter <i>JObj</i>.
	 *  
	 *  @param JObj the JavaScript Object Notation (JSON) representation of the ResultNetlistNodeData Object
	 */
	public ResultNetlistNodeData(final JSONObject JObj){
		super();
		this.setDefault();
		this.parse(JObj);
	}
	
	/*
	 * Write
	 */	
	/**
	 *  Returns a string containing the data of this instance
	 *  @return a string containing the data of this instance
	 */
	protected String getInfo(){	
		String rtn = "";
		// nodeType
		rtn += JSONUtils.getEntryToString("nodeType", this.getNodeType());
		// partitionID
		rtn += JSONUtils.getEntryToString("partitionID", this.getPartitionID());
		// deviceName
		rtn += JSONUtils.getEntryToString("deviceName", this.getDeviceName());
		return rtn;
	}
	
	/**
	 *  Writes this instance in JSON format to the writer defined by parameter <i>os</i> with the number of indents equivalent to the parameter <i>indent</i>
	 *  @param indent the number of indents
	 *  @param os the writer
	 *  @throws IOException If an I/O error occurs
	 */
	public void writeJSON(int indent, Writer os) throws IOException{
		String str = null;
		//header
		str = this.getInfo();
		str = JSONUtils.addIndent(indent, str);
		os.write(str);
	}

	/*
	 * Parse
	 */	
	private void parseNodeType(final JSONObject JObj){
		String value = ProfileUtils.getString(JObj, "nodeType");
		if (value != null) {
			this.setNodeType(value);
		}
	}
	private void parsePartitionID(final JSONObject JObj){
		int value = ProfileUtils.getInteger(JObj, "partitionID");
		this.setPartitionID(value);
	}

	private void parseDevice(final JSONObject JObj) {
		String value = ProfileUtils.getString(JObj, "deviceName");
		if (value != null) {
			this.setDeviceName(value);
		}
	}
	/**
	 *  Parses the data attached to this instance
	 *  
	 *  @param JObj the JavaScript Object Notation (JSON) representation of the Project NetlistNodeData Object
	 */
	public void parse(final JSONObject JObj){
    	this.parseNodeType(JObj);
    	this.parsePartitionID(JObj);
		this.parseDevice(JObj);
	}

	/*
	 * NodeType
	 */
	/**
	 *  Setter for <i>nodeType</i>
	 *  @param nodeType the value to set <i>nodeType</i>
	 */
	public void setNodeType(String nodeType) {
		this.nodeType = nodeType;
	}

	/**
	 *  Getter for <i>nodeType</i>
	 *  @return the nodeType of this instance
	 */
	public String getNodeType() {
		return this.nodeType;
	}
	
	private String nodeType;

	/*
	 * PartitionID
	 */
	/**
	 *  Setter for <i>partitionID</i>
	 *  @param partitionID the value to set <i>partitionID</i>
	 */
	public void setPartitionID(int partitionID) {
		this.partitionID = partitionID;
	}

	/**
	 *  Getter for <i>partitionID</i>
	 *  @return the partitionID of this instance
	 */
	public int getPartitionID() {
		return this.partitionID;
	}
	
	private int partitionID;

	/*
	 * ClusterID
	 */
	/**
	 *  Setter for <i>clusterID</i>
	 *  @param clusterID the value to set <i>clusterID</i>
	 */
	public void setClusterID(int clusterID) {
		this.clusterID = clusterID;
	}

	/**
	 *  Getter for <i>clusterID</i>
	 *  @return the clusterID of this instance
	 */
	public int getClusterID() {
		return this.clusterID;
	}
	
	private int clusterID;
	
	/*
	 * GateType
	 */
	/**
	 * Setter for <i>deviceName</i>
	 * 
	 * @param deviceName the value to set <i>deviceName</i>
	 */
	public void setDeviceName(String device) {
		this.deviceName = device;
	}

	/**
	 * Getter for <i>deviceName</i>
	 * 
	 * @return the deviceName of this instance
	 */
	public String getDeviceName() {
		return this.deviceName;
	}
	
	private String deviceName;
	
	/**
	 * Getter for <i>device</i>.
	 *
	 * @return value of device
	 */
	public AssignableDevice getDevice() {
		return device;
	}

	/**
	 * Setter for <i>device</i>.
	 *
	 * @param device the device to set
	 */
	public void setDevice(AssignableDevice device) {
		this.device = device;
	}

	private AssignableDevice device;

	/**
	 *  Returns a string representation of the object.
	 *  @return a string representation of the object.
	 */
	@Override
	public String toString(){
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
		data = this.getDeviceName();
		if (!data.isEmpty()) {
			// modify
			rtn += Utils.getNewLine();
			rtn += data;
		}
		return rtn;
	}
}

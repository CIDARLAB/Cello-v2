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
package common.netlist;

import java.io.IOException;
import java.io.Writer;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import common.JSON.JSONUtils;
import common.algorithm.data.NetlistData;
import common.graph.graph.GraphTemplate;
import common.netlist.data.StageNetlistData;
import common.profile.ProfileUtils;

/**
 * The NetlistEdge class is a class representing the user design. 
 * 
 * @author Vincent Mirian
 * 
 * @date Nov 17, 2017
 *
 */
public class Netlist extends GraphTemplate<NetlistNode, NetlistEdge>{

	private void setDefault() {
		setInputFilename(null);
	}

	/**
	 *  Initializes a newly created Netlist 
	 */
	public Netlist () {
		super();
		this.setDefault();
	}

	/**
	 *  Initializes a newly created Netlist using the parameter <i>JObj</i>.
	 *  
	 *  @param JObj the JavaScript Object Notation (JSON) representation of the Netlist Object
	 */
	public Netlist (final JSONObject JObj) {
		this();
		this.parse(JObj);
	}

	/*
	 * Parse
	 */
	private void parseName(final JSONObject JObj){
		String name = ProfileUtils.getString(JObj, "name");
		if (name != null) {
			this.setName(name);
		}
	}
	
	private void parseInputFilename(final JSONObject JObj){
		String inputFilename = ProfileUtils.getString(JObj, "inputFilename");
		if (inputFilename != null) {
			this.setInputFilename(inputFilename);
		}
	}

	private void parseNetlistNodes(final JSONObject JObj){
    	JSONArray jsonArr;
    	jsonArr = (JSONArray) JObj.get("nodes");
		if (jsonArr == null) {
			throw new RuntimeException("'nodes' missing in Netlist!");
		}
    	for (int i = 0; i < jsonArr.size(); i++)
    	{
    	    JSONObject jsonObj = (JSONObject) jsonArr.get(i);
    	    NetlistNode node = new NetlistNode(jsonObj);
    	    this.addVertex(node);
    	}
	}

	private NetlistNode getNetlistNode(final JSONObject JObj, final String str){
		NetlistNode rtn = null;
    	String name = null;
		name = ProfileUtils.getString(JObj, str);
		if (name == null) {
			throw new RuntimeException("No name for" + str + "edges in Netlist!");
		}
		rtn = this.getVertexByName(name);
		if (rtn == null) {
			throw new RuntimeException("Node missing in Netlist " + name + ".");
		}
		return rtn;
	}

	private void parseNetlistEdges(final JSONObject JObj){
    	JSONArray jsonArr;
    	NetlistNode node = null;
    	jsonArr = (JSONArray) JObj.get("edges");
		if (jsonArr == null) {
			throw new RuntimeException("'edges' missing in Netlist!");
		}
    	for (int i = 0; i < jsonArr.size(); i++)
    	{
    	    JSONObject jsonObj = (JSONObject) jsonArr.get(i);
    	    NetlistEdge edge = new NetlistEdge(jsonObj);
    	    this.addEdge(edge);
    	    node = getNetlistNode(jsonObj, "src");
    		node.addOutEdge(edge);
    		edge.setSrc(node);
    	    node = getNetlistNode(jsonObj, "dst");
    		node.addInEdge(edge);
    		edge.setDst(node);
    	}
	}

	private void parse(final JSONObject JObj){
    	this.parseName(JObj);
    	this.parseInputFilename(JObj);
    	this.parseNetlistNodes(JObj);
    	this.parseNetlistEdges(JObj);
	}
	
	/*
	 * WriteJSON
	 */
	/**
	 *  Returns a string containing the header in JSON format of this instance
	 *  @return a string containing the header in JSON format of this instance
	 */
	protected String getJSONHeader(){	
		String rtn = "";
		// name
		rtn += JSONUtils.getEntryToString("name", this.getName());
		// inputFilename
		rtn += JSONUtils.getEntryToString("inputFilename", this.getInputFilename());
		return rtn;
	}

	/**
	 *  Returns a string containing the footer in JSON format of this instance
	 *  @return a string containing the footer in JSON format of this instance
	 */
	protected String getJSONFooter(){	
		String rtn = "";
		return rtn;
	}

	/**
	 *  Writes this instance in JSON format to the writer defined by parameter <i>os</i> with the number of indents equivalent to the parameter <i>indent</i>
	 *  @param indent the number of indents
	 *  @param os the writer
	 *  @throws IOException If an I/O error occurs
	 */
	public void writeJSON(int indent, Writer os) throws IOException {
		String str = null;
		//header
		str = this.getJSONHeader();
		str = JSONUtils.addIndent(indent, str);
		os.write(str);
		// nodes
		str = JSONUtils.getStartArrayWithMemberString("nodes");
		str = JSONUtils.addIndent(indent, str);
		os.write(str);
		for (int i = 0; i < this.getNumVertex(); i++){
			str = JSONUtils.addIndent(indent + 1, JSONUtils.getStartEntryString());
			os.write(str);
			this.getVertexAtIdx(i).writeJSON(indent + 2, os);
			str = JSONUtils.addIndent(indent + 1, JSONUtils.getEndEntryString());
			os.write(str);
		}
		str = JSONUtils.getEndArrayString();
		str = JSONUtils.addIndent(indent, str);
		os.write(str);
		// edges
		str = JSONUtils.getStartArrayWithMemberString("edges");
		str = JSONUtils.addIndent(indent, str);
		os.write(str);
		for (int i = 0; i < this.getNumEdge(); i++){
			str = JSONUtils.addIndent(indent + 1, JSONUtils.getStartEntryString());
			os.write(str);
			this.getEdgeAtIdx(i).writeJSON(indent + 2, os);
			str = JSONUtils.addIndent(indent + 1, JSONUtils.getEndEntryString());
			os.write(str);
		}
		str = JSONUtils.getEndArrayString();
		str = JSONUtils.addIndent(indent, str);
		os.write(str);
		//footer
		str = this.getJSONFooter();
		str = JSONUtils.addIndent(indent, str);
		os.write(str);
	}
	
	/**
	 *  Return a newly created NetlistNode with its contents set to those of <i>other</i>.
	 *  
	 *  @param other the other NetlistNode
	 *  @return a newly created NetlistNode with its contents set to those of <i>other</i>.
	 */
	@Override
	public NetlistNode createV(final NetlistNode other) {
		NetlistNode rtn = new NetlistNode(other);
		return rtn;
	}

	/**
	 *  Return a newly created NetlistEdge with its contents set to those of <i>other</i>.
	 *  
	 *  @param other the other NetlistEdge
	 *  @return a newly created NetlistEdge with its contents set to those of <i>other</i>.
	 */
	@Override
	public NetlistEdge createE(final NetlistEdge other) {
		NetlistEdge rtn = new NetlistEdge(other);
		return rtn;
	}
	
	/*
	 * inputFilename
	 */
	/**
	 *  Setter for <i>inputFilename</i>
	 *  @param inputFilename the value to set <i>inputFilename</i>
	 */
	public void setInputFilename(String inputFilename) {
		this.inputFilename = inputFilename;
	}
	/**
	 *  Getter for <i>inputFilename</i>
	 *  @return the inputFilename of this instance
	 */
	public String getInputFilename() {
		return this.inputFilename;
	}
	
	private String inputFilename;

	/*
	 * NetlistData
	 */
	/**
	 *  Setter for <i>netlistData</i>
	 *  @param nData the NetlistData to set <i>netlistData</i>
	 */
	public void setNetlistData(final NetlistData nData){
		this.netlistData = nData;
	}
	/**
	 *  Getter for <i>netlistData</i>
	 *  @return the NetlistData of this instance
	 */
	public NetlistData getNetlistData(){
		return this.netlistData;
	}
	private NetlistData netlistData;

	/*
	 * StageNetlistData
	 */
	/**
	 *  Setter for <i>stageNetlistData</i>
	 *  @param nData the StageNetlistData to set <i>stageNetlistData</i>
	 */
	public void setStageNetlistData(final StageNetlistData nData){
		this.stageNetlistData = nData;
	}
	/**
	 *  Getter for <i>stageNetlistData</i>
	 *  @return the StageNetlistData of this instance
	 */
	public StageNetlistData getStageNetlistData(){
		return this.stageNetlistData;
	}
	private StageNetlistData stageNetlistData;
	
}

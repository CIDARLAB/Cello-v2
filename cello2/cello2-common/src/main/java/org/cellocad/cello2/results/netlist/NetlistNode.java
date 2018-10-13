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
package org.cellocad.cello2.results.netlist;

import java.io.IOException;
import java.io.Writer;

import org.cellocad.cello2.common.Utils;
import org.cellocad.cello2.common.JSON.JSONUtils;
import org.cellocad.cello2.common.algorithm.data.NetlistNodeData;
import org.cellocad.cello2.common.graph.graph.VertexTemplate;
import org.cellocad.cello2.common.netlist.data.StageNetlistNodeData;
import org.cellocad.cello2.common.profile.ProfileUtils;
import org.cellocad.cello2.results.logicSynthesis.LSResultsUtils;
import org.cellocad.cello2.results.netlist.data.ResultNetlistNodeData;
import org.json.simple.JSONObject;

/**
 * The NetlistNode class is a class representing the node(s) of the  project.
 * 
 * @author Vincent Mirian
 * 
 * @date 2018-05-21
 *
 */
public class NetlistNode extends VertexTemplate<NetlistEdge>{

	private void setDefault() {
		this.setResultNetlistNodeData(new ResultNetlistNodeData());
	}

	/**
	 *  Initializes a newly created NetlistNode 
	 */
	public NetlistNode(){
		super();
		this.setDefault();
	}

	/**
	 *  Initializes a newly created NetlistNode with its contents set to those of <i>other</i>.
	 *  
	 *  @param other the other NetlistNode
	 */
	public NetlistNode(final NetlistNode other){
		super(other);
		this.setDefault();
		this.setResultNetlistNodeData(new ResultNetlistNodeData(other.getResultNetlistNodeData()));
	}

	/**
	 *  Initializes a newly created NetlistNode using the parameter <i>JObj</i>.
	 *  
	 *  @param JObj the JavaScript Object Notation (JSON) representation of the NetlistNode Object
	 */
	public NetlistNode(final JSONObject JObj){
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
	
	private void parse(final JSONObject JObj){
    	this.parseName(JObj);
    	this.getResultNetlistNodeData().parse(JObj);
	}
	
	/*
	 * Inherit
	 */
	/**
	 *  Adds this instance to the source node of the NetlistEdge defined by parameter <i>e</i>
	 *  
	 *  @param e the NetlistEdge
	 */
	@Override
	protected void addMeToSrc(final NetlistEdge e) {
		e.setSrc(this);
	}

	/**
	 *  Adds this instance to the destination node of the NetlistEdge defined by parameter <i>e</i>
	 *  
	 *  @param e the NetlistEdge
	 */
	@Override
	protected void addMeToDst(final NetlistEdge e) {
		e.setDst(this);
	}

	/**
	 *  Return a newly created NetlistEdge with its contents set to those of parameter <i>e</i>.
	 *  
	 *  @param e the other NetlistEdge
	 *  @return a newly created NetlistEdge with its contents set to those of parameter <i>e</i>.
	 */
	@Override
	public NetlistEdge createT(final NetlistEdge e) {
		NetlistEdge rtn = null;
		rtn = new NetlistEdge(e);
		return rtn;
	}

	/*
	 * Write
	 */	
	/**
	 *  Returns a string containing the header in JSON format of this instance
	 *  @return a string containing the header in JSON format of this instance
	 */
	protected String getJSONHeader(){	
		String rtn = "";
		// name
		rtn += JSONUtils.getEntryToString("name", this.getName());
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
	public void writeJSON(int indent, final Writer os) throws IOException {
		String str = null;
		//header
		str = this.getJSONHeader();
		str = JSONUtils.addIndent(indent, str);
		os.write(str);
		// data
		this.getResultNetlistNodeData().writeJSON(indent, os);
		/*StageNetlistNodeData sdata = this.getStageNetlistNodeData();
		if (sdata != null) {
			sdata.writeJSON(indent, os);
		}
		NetlistNodeData data = this.getNetlistNodeData();
		if (data != null) {
			data.writeJSON(indent, os);
		}*/
		//footer
		str = this.getJSONFooter();
		str = JSONUtils.addIndent(indent, str);
		os.write(str);
	}

	/*
	 * dot file
	 */
	/**
	 * Returns a string representing the shape of this instance in DOT (graph description language) format
	 * 
	 * @return a string representing the shape of this instance in DOT (graph description language) format
	 */
	@Override
	protected String getShape(){
		String rtn = "";
		rtn = "circle";
		if (
				(this.getResultNetlistNodeData().getPartitionID() < 0)
			){
			rtn = "octagon";
		}
		if (!(LSResultsUtils.isAllInput(this) || LSResultsUtils.isAllOutput(this))) {
			rtn = "box";
		}
		return rtn;
	}

	/**
	 * Returns a string containing this instance in DOT (graph description language) format
	 * 
	 * @return a string containing this instance in DOT (graph description language) format
	 */
	@Override
	protected String getData(){
		String rtn = "";
		// strip
		rtn += super.getData();
		rtn = rtn.substring(0, rtn.lastIndexOf("\"]" + Utils.getNewLine()));
		// add
		rtn += this.getResultNetlistNodeData().toString();
		// update
		rtn += "\"]";
		rtn += Utils.getNewLine();
		// add point for fanout
		// TODO: hack this should be in a hypergraph Netlist
		if (this.getNumOutEdge() > 1) {
			rtn += "\"" + this.getName() + "Point\" [ shape=point ]";
			rtn += Utils.getNewLine();
			rtn += "\"" + this.getName() + "\" -> \"" + this.getName() + "Point\":w";
			rtn += Utils.getNewLine();
		}
		return rtn;
	}

	/*
	 * ResultNetlistData
	 */
	/**
	 *  Setter for <i>resultNetlistData</i>
	 *  @param nData the ResultNetlistNodeData to set <i>resultNetlistData</i>
	 */
	public void setResultNetlistNodeData(final ResultNetlistNodeData nData){
		this.resultNetlistData = nData;
	}
	/**
	 *  Getter for <i>resultNetlistData</i>
	 *  @return the ResultNetlistNodeData of this instance
	 */
	public ResultNetlistNodeData getResultNetlistNodeData(){
		return this.resultNetlistData;
	}
	private ResultNetlistNodeData resultNetlistData;

	/*
	 * NetlistData
	 */
	/**
	 *  Setter for <i>netlistData</i>
	 *  @param nData the NetlistNodeData to set <i>netlistData</i>
	 */
	public void setNetlistNodeData(final NetlistNodeData nData){
		this.netlistData = nData;
	}
	/**
	 *  Getter for <i>netlistData</i>
	 *  @return the NetlistNodeData of this instance
	 */
	public NetlistNodeData getNetlistNodeData(){
		return this.netlistData;
	}
	private NetlistNodeData netlistData;

	/*
	 * StageNetlistNodeData
	 */
	/**
	 *  Setter for <i>stageNetlistData</i>
	 *  @param nData the StageNetlistNodeData to set <i>stageNetlistData</i>
	 */
	public void setStageNetlistNodeData(final StageNetlistNodeData nData){
		this.stageNetlistData = nData;
	}
	/**
	 *  Getter for <i>stageNetlistData</i>
	 *  @return the StageNetlistNodeData of this instance
	 */
	public StageNetlistNodeData getStageNetlistNodeData(){
		return this.stageNetlistData;
	}
	private StageNetlistNodeData stageNetlistData;
	
}

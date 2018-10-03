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
package org.cellocad.cello2.common.netlist;

import java.io.IOException;
import java.io.Writer;

import org.cellocad.cello2.common.JSON.JSONUtils;
import org.cellocad.cello2.common.algorithm.data.NetlistNodeData;
import org.cellocad.cello2.common.graph.graph.VertexTemplate;
import org.cellocad.cello2.common.netlist.data.StageNetlistNodeData;
import org.cellocad.cello2.common.profile.ProfileUtils;
import org.json.simple.JSONObject;

/**
 * The NetlistEdge class is a class representing the node(s) of the user design.
 * 
 * @author Vincent Mirian
 * 
 * @date Nov 17, 2017
 *
 */
public class NetlistNode extends VertexTemplate<NetlistEdge>{

	private void setDefault() {
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
		//footer
		str = this.getJSONFooter();
		str = JSONUtils.addIndent(indent, str);
		os.write(str);
	}
	
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

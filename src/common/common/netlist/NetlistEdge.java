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

import org.json.simple.JSONObject;

import common.Utils;
import common.JSON.JSONUtils;
import common.algorithm.data.NetlistEdgeData;
import common.graph.graph.EdgeTemplate;
import common.netlist.data.StageNetlistEdgeData;
import common.profile.ProfileUtils;

/**
 * The NetlistEdge class is a class representing the edge(s) of the user design.
 * 
 * @author Vincent Mirian
 * 
 * @date Nov 17, 2017
 *
 */
public class NetlistEdge extends EdgeTemplate<NetlistNode>{

	private void setDefault() {
	}

	/**
	 *  Initializes a newly created NetlistEdge 
	 */
	public NetlistEdge(){
		super();
		this.setDefault();
	}

	/**
	 *  Initializes a newly created NetlistEdge with its source node defined by parameter <i>Src</i> and its destination node define by parameter <i>Dst</i>
	 *  @param Src the source node
	 *  @param Dst the destination node
	 */
	public NetlistEdge(final NetlistNode Src, final NetlistNode Dst) {
        super(Src);
        this.setDst(Dst);
    }

	/**
	 *  Initializes a newly created NetlistEdge with its contents set to those of <i>other</i>.
	 *  
	 *  @param other the other NetlistEdge
	 */
	public NetlistEdge(final NetlistEdge other) {
		super(other);
        this.setSrc(other.getSrc());
        this.setDst(other.getDst());
    }

	/**
	 *  Initializes a newly created NetlistEdge using the parameter <i>JObj</i>.
	 *  
	 *  @param JObj the JavaScript Object Notation (JSON) representation of the NetlistNode Object
	 */
	public NetlistEdge(final JSONObject JObj){
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
		// src
		rtn += JSONUtils.getEntryToString("src", this.getSrc().getName());
		// dst
		rtn += JSONUtils.getEntryToString("dst", this.getDst().getName());
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
		str = Utils.addIndent(indent, str);
		os.write(str);
	}
	
	/*
	 * NetlistData
	 */
	/**
	 *  Setter for <i>netlistData</i>
	 *  @param nData the NetlistEdgeData to set <i>netlistData</i>
	 */
	public void setNetlistEdgeData(final NetlistEdgeData nData){
		this.netlistData = nData;
	}
	/**
	 *  Getter for <i>netlistData</i>
	 *  @return the NetlistEdgeData of this instance
	 */
	public NetlistEdgeData getNetlistEdgeData(){
		return this.netlistData;
	}
	private NetlistEdgeData netlistData;

	/*
	 * StageNetlistEdgeData
	 */
	/**
	 *  Setter for <i>stageNetlistData</i>
	 *  @param nData the StageNetlistEdgeData to set <i>stageNetlistData</i>
	 */
	public void setStageNetlistEdgeData(final StageNetlistEdgeData nData){
		this.stageNetlistData = nData;
	}
	/**
	 *  Getter for <i>stageNetlistData</i>
	 *  @return the StageNetlistEdgeData of this instance
	 */
	public StageNetlistEdgeData getStageNetlistEdgeData(){
		return this.stageNetlistData;
	}
	private StageNetlistEdgeData stageNetlistData;
	
}

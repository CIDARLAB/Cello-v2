/**
 * Copyright (C) 2018 Boston University (BU)
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
package org.cellocad.cello2.results.placing.placement;

import java.io.IOException;
import java.io.Writer;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.cellocad.cello2.common.CObject;
import org.cellocad.cello2.common.JSON.JSONUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 * 
 *
 * @author Timothy Jones
 *
 * @date 2018-06-05
 *
 */
public class Placement extends CObject {
	
	/**
	 *  Initialize class members
	 */
	private void init() {
		placement = new ArrayList<PlacementGroup>();
	}
	
	/**
	 *  Initializes a newly created Placement
	 */
	public Placement(final Boolean Up, final Boolean Down) {
		init();
		this.setUp(Up);
		this.setDown(Down);
	}
	
	/**
	 *  Initializes a newly created Placement using the parameter <i>JObj</i>.
	 *  
	 *  @param JObj the JavaScript Object Notation (JSON) representation of the NetlistNode Object
	 */
	public Placement(final JSONArray JObj, final Boolean Up, final Boolean Down) {
		this(Up,Down);
		this.parse(JObj);
	}
	
	/*
	 * Parse
	 */
	private void parsePlacement(final JSONArray JObj) {
		if (JObj == null) {
			throw new RuntimeException("Placement is empty!");
		}
		for (int i = 0; i < JObj.size(); i++) {
			JSONObject obj = (JSONObject) JObj.get(i);
			PlacementGroup group = new PlacementGroup(obj,this.getUp(),this.getDown());
			this.addPlacementGroup(group);
		}
	}
	
	private void parse(final JSONArray JObj) {
    	this.parsePlacement(JObj);
	}
	
	/**
	 *  Writes this instance in JSON format to the writer defined by parameter <i>os</i> with the number of indents equivalent to the parameter <i>indent</i>
	 *  @param indent the number of indents
	 *  @param os the writer
	 *  @throws IOException If an I/O error occurs
	 */
	public void writeJSON(int indent, Writer os) throws IOException {
		String str = null;
		str = JSONUtils.getStartArrayString();
		str = JSONUtils.addIndent(indent, str);
		os.write(str);
		for (int i = 0; i < this.getNumPlacementGroup(); i++) {
			this.getPlacementGroupAtIdx(i).writeJSON(indent + 1,os);
		}
		str = JSONUtils.getEndArrayString();
		str = JSONUtils.addIndent(indent, str);
		os.write(str);
	}
	
	/**
	 * Add a component and its placement index to this instance
	 * @param component add the <i>component</i> to the placement unit
	 * @param index assign placement index <i>index</i> to the placement unit
	 */
	public void addPlacementGroup(final PlacementGroup group) {
		placement.add(group);
	}
	
	/**
	 * Add a component and its placement index to this instance
	 * @param index assign placement index <i>index</i> to the placement unit
	 * @param component add the <i>component</i> to the placement unit
	 */
	public void addPlacementGroup(final Integer index, final PlacementGroup group) {
		placement.add(index,group);
	}
	
	/**
	 * Returns the Pair<String,Integer> at the specified position in this instance.
	 * 
	 * @param index index of the Pair<String,Integer> to return
	 * @return if the index is within the bounds (0 <= bounds < this.getNumPlacementUnitPosition()), returns the Pair<String,Integer> at the specified position in this instance, otherwise null
	 */
	public PlacementGroup getPlacementGroupAtIdx(final int index){
		PlacementGroup rtn = null;
		if (
				(0 <= index)
				&&
				(index < this.getNumPlacementGroup())
				) {
			rtn = this.getPlacement().get(index);
		}
		return rtn;
	}
	
	/**
	 * Returns the number of Pair<String,Integer> in this instance.
	 * 
	 * @return the number of Pair<String,Integer> in this instance.
	 */
	public int getNumPlacementGroup() {
		return this.getPlacement().size();
	}
	
	/**
	 * Getter for <i>placement</i>
	 * @return value of <i>placement</i>
	 */
	protected List<PlacementGroup> getPlacement() {
		return placement;
	}

	/**
	 * Getter for <i>position</i>
	 * @return value of <i>position</i>
	 */
	protected Integer getPosition() {
		return this.getIdx();
	}
	
	/*
	 * Up
	 */
	/**
	 * Getter for <i>bUp</i>
	 * @return value of <i>bUp</i>
	 */
	protected Boolean getUp() {
		return bUp;
	}
	/**
	 * Setter for <i>bUp</i>
	 * @param Up the value to set <i>bUp</i>
	 */
	protected void setUp(final Boolean Up) {
		this.bUp = Up;
	}
	/*
	 * Down
	 */
	/**
	 * Getter for <i>bDown</i>
	 * @return value of <i>bDown</i>
	 */
	protected Boolean getDown() {
		return bDown;
	}
	/**
	 * Setter for <i>bDown</i>
	 * @param Down the value to set <i>bDown</i>
	 */
	protected void setDown(final Boolean Down) {
		this.bDown = Down;
	}
	/*
	 * Direction
	 */
	/**
	 * Getter for <i>direction</i>
	 * @return value of <i>direction</i>
	 */
	public Boolean getDirection() {
		return direction;
	}
	/**
	 * Setter for <i>direction</i>
	 * @param direction the value to set <i>direction</i>
	 */
	public void setDirection(final Boolean direction) {
		this.direction = direction;
	}

	/**
	 * Getter for <i>uri</i>
	 * @return value of <i>uri</i>
	 */
	public URI getUri() {
		return uri;
	}

	/**
	 * Setter for <i>uri</i>
	 * @param uri the value to set <i>uri</i>
	 */
	public void setUri(final URI uri) {
		this.uri = uri;
	}

	private List<PlacementGroup> placement;
	private Boolean bUp;
	private Boolean bDown;
	private Boolean direction;
	private URI uri;

}

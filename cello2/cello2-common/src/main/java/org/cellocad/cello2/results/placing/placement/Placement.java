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

import java.util.ArrayList;
import java.util.List;

import org.cellocad.cello2.common.CObject;
import org.cellocad.cello2.common.JSON.JSONUtils;
import org.cellocad.cello2.common.profile.ProfileUtils;
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
		placementEntry = new ArrayList<String>();
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
	public Placement(final JSONObject JObj, final Boolean Up, final Boolean Down){
		this(Up,Down);
		this.parse(JObj);
	}
	
	private String componentsToJSON() {
		String rtn = "";
		for (int i = 0; i < this.getPlacement().size(); i++) {
			String str = this.getPlacement().get(i);
			rtn += JSONUtils.getValueToString(str);
		}
		rtn = JSONUtils.addIndent(1, rtn);
		return rtn;
	}
	
	protected String toJSON() {
		String rtn = "";
		rtn += JSONUtils.getStartEntryString();
		String str = "";
		str += JSONUtils.getEntryToString("position", this.getPosition());
		str += JSONUtils.getEntryToString("direction", this.getDirection() ? 1 : 0);
		str += JSONUtils.getStartArrayWithMemberString("components");
		str += this.componentsToJSON();
		str += JSONUtils.getEndArrayString();
		str = JSONUtils.addIndent(1, str);
		rtn += str;
		rtn += JSONUtils.getEndEntryString();
		return rtn;
	}
	
	/*
	 * Parse
	 */
	private void parsePlacement(final JSONObject JObj){
	    Integer position =  ProfileUtils.getInteger(JObj, "position");
		if (position == null) {
			throw new RuntimeException("'position' missing in Placement!");
		}
		this.setIdx(position);
	    Integer direction =  ProfileUtils.getInteger(JObj, "direction");
		if (direction == null) {
			throw new RuntimeException("'direction' missing in Placement!");
		}
		if (direction < 0) {
			this.setDirection(this.getDown());
		}
		else {
			this.setDirection(this.getUp());
		}
		JSONArray jsonArr;
    	jsonArr = (JSONArray) JObj.get("components");
		if (jsonArr == null) {
			throw new RuntimeException("'components' missing in Placement!");
		}
		for (int i = 0; i < jsonArr.size(); i++)
		{
			String component = (String) jsonArr.get(i);
			this.addComponentToPlacement(component);
		}
	}
	
	private void parse(final JSONObject JObj){
    	this.parsePlacement(JObj);
	}
	
	/**
	 * Add a component and its placement index to this instance
	 * @param component add the <i>component</i> to the placement unit
	 * @param index assign placement index <i>index</i> to the placement unit
	 */
	public void addComponentToPlacement(final String component) {
		placementEntry.add(component);
	}
	
	/**
	 * Add a component and its placement index to this instance
	 * @param index assign placement index <i>index</i> to the placement unit
	 * @param component add the <i>component</i> to the placement unit
	 */
	public void addComponentToPlacement(final Integer index, final String component) {
		placementEntry.add(index,component);
	}
	
	/**
	 * Returns the Pair<String,Integer> at the specified position in this instance.
	 * 
	 * @param index index of the Pair<String,Integer> to return
	 * @return if the index is within the bounds (0 <= bounds < this.getNumPlacementUnitPosition()), returns the Pair<String,Integer> at the specified position in this instance, otherwise null
	 */
	public String getComponentAtIdx(final int index){
		String rtn = null;
		if (
				(0 <= index)
				&&
				(index < this.getNumComponent())
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
	public int getNumComponent() {
		return this.getPlacement().size();
	}
	
	/**
	 * Getter for <i>placementEntry</i>
	 * @return value of <i>placementEntry</i>
	 */
	protected List<String> getPlacement() {
		return placementEntry;
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

	List<String> placementEntry;
	private Boolean bUp;
	private Boolean bDown;
	private Boolean direction;

}

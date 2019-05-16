/**
 * Copyright (C) 2019 Boston University (BU)
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
 * @date 2019-05-15
 *
 */
public class Component extends CObject {

	private void init() {
		parts = new ArrayList<>();
	}
	
	protected Component(final Boolean Up, final Boolean Down) {
		init();
		this.setUp(Up);
		this.setDown(Down);
	}

	public Component(List<String> parts, final Boolean Up, final Boolean Down) {
		this(Up,Down);
		this.parts = parts;
	}
	
	public Component(final JSONObject JObj, final Boolean Up, final Boolean Down) {
		this(Up,Down);
		parse(JObj);
	}

	/*
	 * Parse
	 */
	private void parseComponent(final JSONObject JObj) {
		String node = ProfileUtils.getString(JObj, "node");
		if (node == null) {
			throw new RuntimeException("'node' missing in Netlist!");
		}
		this.setNode(node);
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
    	jsonArr = (JSONArray) JObj.get("parts");
		if (jsonArr == null) {
			throw new RuntimeException("'parts' missing in Placement!");
		}
		for (int i = 0; i < jsonArr.size(); i++) {
			String part = (String) jsonArr.get(i);
			this.getParts().add(part);
		}
	}

	private void parse(final JSONObject JObj){
    	this.parseComponent(JObj);
	}
	
	/**
	 *  Writes this instance in JSON format to the writer defined by parameter <i>os</i> with the number of indents equivalent to the parameter <i>indent</i>
	 *  @param indent the number of indents
	 *  @param os the writer
	 *  @throws IOException If an I/O error occurs
	 */
	public void writeJSON(int indent, Writer os) throws IOException {
		String str = null;
		str = JSONUtils.getStartEntryString();
		str = JSONUtils.addIndent(indent, str);
		os.write(str);
		str = "";
		str += JSONUtils.getEntryToString("node",this.getNode());
		str += JSONUtils.getEntryToString("direction",this.getDirection().toString());
		str += JSONUtils.getStartArrayWithMemberString("parts");
		str = JSONUtils.addIndent(indent + 1, str);
		os.write(str);
		str = "";
		for (int i = 0; i < this.getNumPart(); i++) {
			str += JSONUtils.getValueToString(this.getPartAtIdx(i));
		}
		str = JSONUtils.addIndent(indent + 2, str);
		os.write(str);
		str = JSONUtils.getEndArrayString();
		str = JSONUtils.addIndent(indent + 1, str);
		os.write(str);
		str = JSONUtils.getEndEntryString();
		str = JSONUtils.addIndent(indent, str);
		os.write(str);
	}

	public int getNumPart() {
		return this.getParts().size();
	}

	public String getPartAtIdx(final int index) {
		String rtn = null;
		rtn = this.getParts().get(index);
		return rtn;
	}

	/**
	 * @return the parts
	 */
	private List<String> getParts() {
		return parts;
	}

	/**
	 * @return the node
	 */
	public String getNode() {
		return node;
	}

	/**
	 * @param node the node to set
	 */
	public void setNode(String node) {
		this.node = node;
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

	private String node;
	private List<String> parts;
	private Boolean bUp;
	private Boolean bDown;
	private Boolean direction;

}

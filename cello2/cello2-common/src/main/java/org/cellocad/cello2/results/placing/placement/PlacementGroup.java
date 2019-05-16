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
public class PlacementGroup extends CObject {
	
	/**
	 *  Initialize class members
	 */
	private void init() {
		placementGroup = new ArrayList<>();
	}
	
	public PlacementGroup(final Boolean Up, final Boolean Down) {
		init();
		this.setUp(Up);
		this.setDown(Down);
	}
	
	public PlacementGroup(final JSONObject JObj, final Boolean Up, final Boolean Down) {
		this(Up,Down);
		parse(JObj);
	}

	/*
	 * Parse
	 */
	private void parsePlacementGroup(final JSONObject JObj) {
		String name = ProfileUtils.getString(JObj, "name");
		if (name == null) {
			throw new RuntimeException("'name' missing in placement group!");
		}
		this.setName(name);
		String backbone = ProfileUtils.getString(JObj, "backbone");
		this.setBackbone(backbone);
		JSONArray jsonArr;
    	jsonArr = (JSONArray) JObj.get("components");
		if (jsonArr == null) {
			throw new RuntimeException("'components' missing in placement group!");
		}
		for (int i = 0; i < jsonArr.size(); i++) {
			JSONObject obj = (JSONObject) jsonArr.get(i);
			Component component = new Component(obj,this.getUp(),this.getDown());
			this.getPlacementGroup().add(component);
		}
	}

	private void parse(final JSONObject JObj) {
    	this.parsePlacementGroup(JObj);
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
		str += JSONUtils.getEntryToString("name",this.getName());
		if (this.getBackbone() != null) {
			str += JSONUtils.getEntryToString("backbone",this.getBackbone());
		}
		str += JSONUtils.getStartArrayWithMemberString("components");
		str = JSONUtils.addIndent(indent + 1, str);
		os.write(str);
		for (int i = 0; i < this.getNumComponent(); i++) {
			this.getComponentAtIdx(i).writeJSON(indent + 2,os);
		}
		str = JSONUtils.getEndArrayString();
		str = JSONUtils.addIndent(indent + 1, str);
		os.write(str);
		str = JSONUtils.getEndEntryString();
		str = JSONUtils.addIndent(indent, str);
		os.write(str);
	}
	
	/**
	 * Add a component and its placement index to this instance
	 * @param component add the <i>component</i> to the placement unit
	 * @param index assign placement index <i>index</i> to the placement unit
	 */
	public void addComponent(final Component component) {
		this.getPlacementGroup().add(component);
	}

	/**
	 * @return the components
	 */
	protected List<Component> getPlacementGroup() {
		return placementGroup;
	}
	
	public int getNumComponent() {
		return this.getPlacementGroup().size();
	}
	
	public Component getComponentAtIdx(final int index) {
		Component rtn = null;
		rtn = this.getPlacementGroup().get(index);
		return rtn;
	}

	/**
	 * @return the backbone
	 */
	public String getBackbone() {
		return backbone;
	}

	/**
	 * @param backbone the backbone to set
	 */
	public void setBackbone(final String backbone) {
		this.backbone = backbone;
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

	private String backbone;
	private List<Component> placementGroup;
	private Boolean bUp;
	private Boolean bDown;
	
}

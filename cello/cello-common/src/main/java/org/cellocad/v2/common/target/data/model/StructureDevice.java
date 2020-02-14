/**
 * Copyright (C) 2020 Boston University (BU)
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
package org.cellocad.v2.common.target.data.model;

import java.util.ArrayList;
import java.util.List;

import org.cellocad.v2.common.profile.ProfileUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 *
 *
 * @author Timothy Jones
 *
 * @date 2020-02-11
 *
 */
public class StructureDevice extends StructureObject {

	private void init() {
		this.components = new ArrayList<>();
	}

	private void parseName(final JSONObject jObj) {
		String value = ProfileUtils.getString(jObj, "name");
		this.setName(value);
	}

	private void parseMapsToVariable(final JSONObject jObj) {
		String value = ProfileUtils.getString(jObj, "name");
		this.mapsToVariable = value;
	}

	private void parseComponents(final JSONObject jObj) {
		JSONArray jArr = (JSONArray) jObj.get(S_COMPONENTS);
		for (int i = 0; i < jArr.size(); i++) {
			String str = (String) jArr.get(i);
			if (str.startsWith(StructureTemplate.S_PREFIX)) {
				StructureTemplate t = new StructureTemplate(str);
				this.getComponents().add(t);
			} else {
				StructurePart p = new StructurePart();
				p.setName(str);
				this.getComponents().add(p);
			}
		}
	}

	private void parseGateStructureDevice(final JSONObject jObj) {
		this.parseName(jObj);
		this.parseMapsToVariable(jObj);
		this.parseComponents(jObj);
	}

	public StructureDevice(final JSONObject jObj) {
		this.init();
		this.parseGateStructureDevice(jObj);
	}

	public StructureDevice(final StructureDevice device) {
		super(device);
		this.mapsToVariable = device.getMapsToVariable();
		this.components = device.getComponents();
	}

	/**
	 * Getter for <i>mapsToVariable</i>
	 *
	 * @return value of mapsToVariable
	 */
	public String getMapsToVariable() {
		return mapsToVariable;
	}

	/**
	 * Getter for <i>components</i>
	 *
	 * @return value of components
	 */
	public List<StructureObject> getComponents() {
		return components;
	}

	private String mapsToVariable;
	private List<StructureObject> components;

	private static final String S_COMPONENTS = "components";

}

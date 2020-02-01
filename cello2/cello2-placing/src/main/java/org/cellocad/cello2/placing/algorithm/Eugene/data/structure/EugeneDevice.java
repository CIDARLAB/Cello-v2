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
package org.cellocad.cello2.placing.algorithm.Eugene.data.structure;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.cellocad.cello2.common.Utils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 *
 *
 * @author Timothy Jones
 *
 * @date 2020-01-09
 *
 */
public class EugeneDevice extends EugeneObject {

	private void init() {
		this.components = new ArrayList<>();
	}

	private void parseName(JSONObject jObj) {
		this.name = (String) jObj.get("name");
	}

	private void parseComponents(JSONObject jObj) {
		JSONArray jArr = (JSONArray) jObj.get("components");
		for (int i = 0; i < jArr.size(); i++) {
			String str = (String) jArr.get(i);
			if (str.startsWith(EugeneTemplate.S_PREFIX)) {
				EugeneTemplate t = new EugeneTemplate(str);
				this.getComponents().add(t);
			} else {
				EugenePart p = new EugenePart(str);
				this.getComponents().add(p);
			}
		}
	}

	private void parseOutput(JSONObject jObj) {
		if (jObj.containsKey("output"))
			this.output = new EugenePart((String) jObj.get("output"));
	}

	private void parseMapsToVariable(JSONObject jObj) {
		if (jObj.containsKey("maps_to_variable"))
			this.mapsToVariable = (String) jObj.get("maps_to_variable");
	}

	public EugeneDevice(JSONObject jObj) {
		this.init();
		this.parseName(jObj);
		this.parseComponents(jObj);
		this.parseOutput(jObj);
		this.parseMapsToVariable(jObj);
	}

	/**
	 * @param device
	 */
	public EugeneDevice(EugeneDevice device) {
		this.name = device.getName();
		this.components = device.getComponents();
		this.output = device.getOutput();
		this.mapsToVariable = device.getMapsToVariable();
	}

	public String getDevice() {
		String rtn = "";
		Collection<EugeneDevice> devices = new ArrayList<>();
		rtn += "Device " + this.name + "(" + Utils.getNewLine();
		for (int i = 0; i < this.getComponents().size(); i++) {
			EugeneObject o = this.getComponents().get(i);
			if (o instanceof EugeneDevice)
				devices.add((EugeneDevice) o);
			rtn += "    " + o.getName();
			if (i < this.getComponents().size() - 1)
				rtn += ",";
			rtn += Utils.getNewLine();
		}
		rtn.substring(0, rtn.length() - 2);
		rtn += ");" + Utils.getNewLine();
		for (EugeneDevice d : devices) {
			rtn += d.getDevice();
		}
		return rtn;
	}

	public String getPartTypes() {
		String rtn = "";
		return rtn;
	}

	/**
	 * Getter for <i>components</i>
	 *
	 * @return value of <i>components</i>
	 */
	public List<EugeneObject> getComponents() {
		return components;
	}

	/**
	 * Getter for <i>output</i>
	 *
	 * @return value of <i>output</i>
	 */
	public EugenePart getOutput() {
		return output;
	}

	/**
	 * Getter for <i>mapsToVariable</i>
	 *
	 * @return value of <i>mapsToVariable</i>
	 */
	public String getMapsToVariable() {
		return mapsToVariable;
	}

	private List<EugeneObject> components;
	private EugenePart output;
	private String mapsToVariable;

}

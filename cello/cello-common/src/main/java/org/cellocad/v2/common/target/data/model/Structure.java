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
import java.util.Iterator;
import java.util.List;

import org.cellocad.v2.common.CObject;
import org.cellocad.v2.common.CObjectCollection;
import org.cellocad.v2.common.profile.ProfileUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 *
 *
 * @author Timothy Jones
 *
 * @date 2020-02-13
 *
 */
public class Structure extends CObject {

	private void init() {
		this.inputs = new CObjectCollection<>();
		this.outputs = new ArrayList<>();
		this.devices = new CObjectCollection<>();
	}

	private void parseName(final JSONObject jObj) {
		String value = ProfileUtils.getString(jObj, S_NAME);
		this.setName(value);
	}

	private void parseInputs(final JSONObject jObj) {
		JSONArray jArr = (JSONArray) jObj.get(S_INPUTS);
		if (jArr == null)
			return;
		for (int i = 0; i < jArr.size(); i++) {
			JSONObject o = (JSONObject) jArr.get(i);
			Input input = new Input(o);
			this.getInputs().add(input);
		}
	}

	private void parseOutputs(final JSONObject jObj) {
		JSONArray jArr = (JSONArray) jObj.get(S_OUTPUTS);
		if (jArr == null)
			return;
		for (int i = 0; i < jArr.size(); i++) {
			String value = (String) jArr.get(i);
			this.getOutputs().add(value);
		}
	}

	private static CObjectCollection<StructureDevice> nestDevices(CObjectCollection<StructureDevice> devices) {
		CObjectCollection<StructureDevice> rtn = new CObjectCollection<>();
		rtn.addAll(devices);
		Iterator<StructureDevice> it = devices.iterator();
		while (it.hasNext()) {
			StructureDevice d = it.next();
			for (int i = 0; i < d.getComponents().size(); i++) {
				StructureObject o = d.getComponents().get(i);
				if (o instanceof StructureTemplate)
					continue;
				Iterator<StructureDevice> jt = devices.iterator();
				while (jt.hasNext()) {
					StructureDevice e = jt.next();
					if (e.equals(d))
						continue;
					if (e.getName().equals(o.getName())) {
						d.getComponents().set(i, e);
						rtn.remove(e);
						break;
					}
				}
			}
		}
		return rtn;
	}

	private void parseDevices(final JSONObject jObj) {
		JSONArray jArr = (JSONArray) jObj.get(S_DEVICES);
		if (jArr == null)
			return;
		for (int i = 0; i < jArr.size(); i++) {
			JSONObject o = (JSONObject) jArr.get(i);
			StructureDevice d = new StructureDevice(o);
			this.getDevices().add(d);
		}
		this.devices = nestDevices(this.getDevices());
	}

	private void parseStructure(final JSONObject jObj) {
		this.parseName(jObj);
		this.parseInputs(jObj);
		this.parseOutputs(jObj);
		this.parseDevices(jObj);
	}

	public Structure(final JSONObject jObj) {
		this.init();
		this.parseStructure(jObj);
	}

	@Override
	public boolean isValid() {
		boolean rtn = super.isValid();
		rtn = rtn && (this.getName() != null);
		return rtn;
	}

	/*
	 * Input
	 */

	/**
	 * Returns the Input with name equivalent to parameter <i>name</i>.
	 * 
	 * @param name name of the Input to return
	 * @return the element with name equivalent to parameter <i>name</i>
	 */
	public Input getInputByName(final String name) {
		return this.getInputs().findCObjectByName(name);
	}

	/**
	 * Getter for <i>inputs</i>.
	 *
	 * @return value of inputs
	 */
	private CObjectCollection<Input> getInputs() {
		return inputs;
	}

	private CObjectCollection<Input> inputs;

	/*
	 * Output
	 */

	/**
	 * Getter for <i>outputs</i>.
	 *
	 * @return value of outputs
	 */
	public List<String> getOutputs() {
		return outputs;
	}

	private List<String> outputs;

	/*
	 * Device
	 */

	/**
	 * Getter for <i>devices</i>.
	 *
	 * @return value of <i>devices</i>
	 */
	public CObjectCollection<StructureDevice> getDevices() {
		return devices;
	}

	private CObjectCollection<StructureDevice> devices;

	static final String S_NAME = "name";
	static final String S_INPUTS = "inputs";
	static final String S_OUTPUTS = "outputs";
	static final String S_DEVICES = "devices";

}

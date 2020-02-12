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
package org.cellocad.v2.common.target.data.data;

import java.util.ArrayList;
import java.util.Collection;

import org.cellocad.v2.common.profile.ProfileUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 *
 *
 * @author Timothy Jones
 *
 * @date 2020-01-29
 *
 */
public class OutputDeviceStructure {

	private void parseGateName(final JSONObject jObj) {
		String value = ProfileUtils.getString(jObj, "gate_name");
		this.gateName = value;
	}

	private void parseDevices(final JSONObject jObj) {
		JSONArray jArr = (JSONArray) jObj.get("devices");
		for (int i = 0; i < jArr.size(); i++) {
			JSONObject o = (JSONObject) jArr.get(i);
			StructureDevice d = new StructureDevice(o);
			this.getDevices().add(d);
		}
	}

	private void parseOutputDeviceStructure(final JSONObject jObj) {
		this.parseGateName(jObj);
		this.parseDevices(jObj);
	}

	private void init() {
		this.devices = new ArrayList<StructureDevice>();
	}

	public OutputDeviceStructure(final JSONObject jObj) {
		this.init();
		this.parseOutputDeviceStructure(jObj);
	}

	/**
	 * Getter for <i>devices</i>
	 *
	 * @return value of <i>devices</i>
	 */
	public Collection<StructureDevice> getDevices() {
		return devices;
	}

	/**
	 * Getter for <i>gateName</i>
	 *
	 * @return value of <i>gateName</i>
	 */
	public String getGateName() {
		return gateName;
	}

	/**
	 * Getter for <i>outputDevice</i>
	 *
	 * @return value of <i>outputDevice</i>
	 */
	public OutputDevice getOutputDevice() {
		return outputDevice;
	}

	/**
	 * Setter for <i>outputDevice</i>
	 *
	 * @param outputDevice the value to set <i>outputDevice</i>
	 */
	public void setOutputDevice(final OutputDevice outputDevice) {
		this.outputDevice = outputDevice;
	}

	private Collection<StructureDevice> devices;
	private String gateName;
	private OutputDevice outputDevice;

}

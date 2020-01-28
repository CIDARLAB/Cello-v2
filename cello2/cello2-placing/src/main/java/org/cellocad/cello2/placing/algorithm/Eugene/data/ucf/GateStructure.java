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
package org.cellocad.cello2.placing.algorithm.Eugene.data.ucf;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.cellocad.cello2.common.CObject;
import org.cellocad.cello2.common.profile.ProfileUtils;
import org.cellocad.cello2.placing.algorithm.Eugene.data.structure.EugeneDevice;
import org.cellocad.cello2.placing.algorithm.Eugene.data.structure.EugeneObject;
import org.cellocad.cello2.placing.algorithm.Eugene.data.structure.EugeneTemplate;
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
public class GateStructure extends CObject {

	private static Collection<EugeneDevice> nestDevices(Collection<EugeneDevice> devices) {
		Collection<EugeneDevice> rtn = new ArrayList<>();
		rtn.addAll(devices);
		Iterator<EugeneDevice> it = devices.iterator();
		while (it.hasNext()) {
			EugeneDevice d = it.next();
			for (int i = 0; i < d.getComponents().size(); i++) {
				EugeneObject o = d.getComponents().get(i);
				if (o instanceof EugeneTemplate)
					continue;
				Iterator<EugeneDevice> jt = devices.iterator();
				while (jt.hasNext()) {
					EugeneDevice e = jt.next();
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

	private void parseGateName(final JSONObject jObj) {
		String value = ProfileUtils.getString(jObj, "gate_name");
		this.gateName = value;
	}

	private void parseOutput(final JSONObject jObj) {
		String value = ProfileUtils.getString(jObj, "output");
		this.setOutput(value);
	}

	private void parseDevices(final JSONObject jObj) {
		JSONArray jArr = (JSONArray) jObj.get("devices");
		for (int i = 0; i < jArr.size(); i++) {
			JSONObject o = (JSONObject) jArr.get(i);
			EugeneDevice d = new EugeneDevice(o);
			this.getDevices().add(d);
		}
		this.devices = nestDevices(this.getDevices());
	}

	private void parseGateStructure(final JSONObject jObj) {
		this.parseGateName(jObj);
		this.parseOutput(jObj);
		this.parseDevices(jObj);
	}

	private void init() {
		this.devices = new ArrayList<EugeneDevice>();
	}

	public GateStructure(final JSONObject jObj) {
		this.init();
		this.parseGateStructure(jObj);
	}

	public Collection<EugeneDevice> getDevices(String variable) {
		Collection<EugeneDevice> rtn = new ArrayList<>();
		for (EugeneDevice d : this.getDevices()) {
			if (d.getMapsToVariable().equals(variable))
				rtn.add(d);
		}
		return rtn;
	}

	/**
	 * @return the output
	 */
	public String getOutput() {
		return output;
	}

	/**
	 * @param output the output to set
	 */
	public void setOutput(String output) {
		this.output = output;
	}

	/**
	 * Getter for <i>devices</i>
	 *
	 * @return value of <i>devices</i>
	 */
	public Collection<EugeneDevice> getDevices() {
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
	 * Getter for <i>gate</i>
	 *
	 * @return value of <i>gate</i>
	 */
	public Gate getGate() {
		return gate;
	}

	/**
	 * Setter for <i>gate</i>
	 *
	 * @param gate the value to set <i>gate</i>
	 */
	public void setGate(final Gate gate) {
		this.gate = gate;
	}

	private String output;
	private Collection<EugeneDevice> devices;
	private String gateName;
	private Gate gate;

}

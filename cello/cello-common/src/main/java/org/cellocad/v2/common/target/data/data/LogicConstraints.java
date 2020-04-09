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

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.cellocad.v2.common.CObject;
import org.cellocad.v2.common.profile.ProfileUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 * Represents the logic constraints section of the target data.
 *
 * @author Timothy Jones
 *
 * @date 2020-03-30
 *
 */
public class LogicConstraints extends CObject {

	public static final String S_AVAILABLE_GATES = "available_gates";
	public static final String S_TYPE = "type";
	public static final String S_MAX_INSTANCES = "max_instances";

	private Map<String, Integer> gates;

	private void init() {
		this.gates = new HashMap<>();
	}

	private void parseAvailableGates(final JSONObject jObj) {
		JSONArray jArr = (JSONArray) jObj.get(S_AVAILABLE_GATES);
		for (int i = 0; i < jArr.size(); i++) {
			JSONObject o = (JSONObject) jArr.get(i);
			String type = ProfileUtils.getString(o, S_TYPE);
			Object value = o.get(S_MAX_INSTANCES);
			if (value instanceof Boolean && ((Boolean) value)) {
				this.gates.put(type, Integer.MAX_VALUE);
			} else {
				Integer max = ProfileUtils.getInteger(value);
				this.gates.put(type, max);
			}
		}
	}

	private void parseGate(final JSONObject jObj) {
		this.parseAvailableGates(jObj);
	}

	public LogicConstraints(final JSONObject jObj) {
		super();
		this.init();
		this.parseGate(jObj);
	}

	public Collection<String> getAvailableGates() {
		Collection<String> rtn = this.gates.keySet();
		return rtn;
	}

	public Integer getMaxInstances(String gateType) {
		Integer rtn = null;
		rtn = this.gates.get(gateType);
		return rtn;
	}

}

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

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 *
 *
 * @author Timothy Jones
 *
 * @date 2020-01-13
 *
 */
public class DeviceRules {

	private void init() {
		this.rules = new ArrayList<>();
	}

	private void parseDeviceRules(JSONObject jObj) {
		JSONArray jArr = (JSONArray) jObj.get("rules");
		for (Object o : jArr) {
			String str = (String) o;
			this.getRules().add(str);
		}
	}

	public DeviceRules(JSONObject jObj) {
		init();
		this.parseDeviceRules(jObj);
	}

	public Collection<String> getRulesByObjectName(String name) {
		Collection<String> rtn = new ArrayList<>();
		for (String rule : this.getRules()) {
			if (EugeneRules.GlobalOrientationRuleKeywords.contains(rule)) {
				rtn.add(rule);
			}
			Collection<String> objects = EugeneRules.getObjects(rule);
			for (String str : objects) {
				if (str.equals(name))
					rtn.add(rule);
			}
		}
		return rtn;
	}

	/**
	 * Getter for <i>rules</i>
	 *
	 * @return value of <i>rules</i>
	 */
	private Collection<String> getRules() {
		return rules;
	}

	private Collection<String> rules;

}

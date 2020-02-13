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
package org.cellocad.v2.common.target.data.structure;

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
 * @date 2020-01-09
 *
 */
public class GateStructure extends Structure {

	private void init() {
		this.outputs = new ArrayList<>();
	}

	private void parseOutputs(final JSONObject jObj) {
		JSONArray jArr = (JSONArray) jObj.get(S_OUTPUTS);
		for (int i = 0; i < jArr.size(); i++) {
			JSONObject o = (JSONObject) jArr.get(i);
			String value = ProfileUtils.getString(o);
			this.getOutputs().add(value);
		}
	}

	private void parseGateStructure(final JSONObject jObj) {
		this.parseOutputs(jObj);
	}

	public GateStructure(final JSONObject jObj) {
		super(jObj);
		this.init();
		this.parseGateStructure(jObj);
	}

	/**
	 * Getter for <i>outputs</i>.
	 *
	 * @return value of outputs
	 */
	public List<String> getOutputs() {
		return outputs;
	}

	private List<String> outputs;

	private static final String S_OUTPUTS = "outputs";

}

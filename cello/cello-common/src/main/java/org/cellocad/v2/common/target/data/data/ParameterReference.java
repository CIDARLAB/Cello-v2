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

import org.cellocad.v2.common.profile.ProfileUtils;
import org.json.simple.JSONObject;

/**
 *
 *
 * @author Timothy Jones
 *
 * @date 2020-02-12
 *
 */
public class ParameterReference extends Parameter {

	private void init() {
	}

	private void parseMap(final JSONObject JObj) {
		String value = ProfileUtils.getString(JObj, S_MAP);
		this.map = value;
	}

	private void parseParameterReference(final JSONObject JObj) {
		this.parseName(JObj);
		this.parseMap(JObj);
	}

	public ParameterReference(final JSONObject jObj) {
		this.init();
		this.parseParameterReference(jObj);
	}

	@Override
	public Number evaluate(GateContextEvaluator ce) {
		return null;
	}

	@Override
	public boolean isValid() {
		boolean rtn = super.isValid();
		rtn = rtn && (this.getName() != null);
		rtn = rtn && (this.getMap() != null);
		return rtn;
	}

	private String getMap() {
		return map;
	}

	private String map;

	private static final String S_MAP = "map";

}

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

import org.cellocad.v2.common.CObjectCollection;
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
public abstract class Function extends Evaluatable {

	private void init() {
		this.variables = new CObjectCollection<>();
	}

	private void parseVariables(final JSONObject JObj) {
		CObjectCollection<Variable> variables = this.getVariables();
		JSONArray jArr = (JSONArray) JObj.get(S_VARIABLES);
		if (jArr == null)
			return;
		for (int i = 0; i < jArr.size(); i++) {
			JSONObject jObj = (JSONObject) jArr.get(i);
			Variable variable = new Variable(jObj);
			if (variable.isValid())
				variables.add(variable);
		}
	}

	private void parseFunction(JSONObject jObj) {
		this.parseVariables(jObj);
	}

	public Function(JSONObject jObj) {
		super(jObj);
		this.init();
		this.parseFunction(jObj);
	}

	/**
	 * Getter for <i>variables</i>.
	 *
	 * @return value of variables
	 */
	public CObjectCollection<Variable> getVariables() {
		return variables;
	}

	private CObjectCollection<Variable> variables;

	public static final String S_VARIABLES = "variables";

}

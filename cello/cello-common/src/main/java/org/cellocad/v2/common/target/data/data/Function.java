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
 * @date 2020-02-11
 *
 */
public class Function extends CObject {

	private void init() {
		this.variables = new CObjectCollection<>();
		this.parameters = new CObjectCollection<>();
	}

	private void parseName(final JSONObject JObj) {
		String value = ProfileUtils.getString(JObj, S_NAME);
		this.setName(value);
	}

	private void parseEquation(final JSONObject JObj) {
		String value = ProfileUtils.getString(JObj, S_EQUATION);
		this.equation = value;
	}

	private void parseVariables(final JSONObject JObj) {
		CObjectCollection<Variable> variables = this.getVariables();
		JSONArray jArr = (JSONArray) JObj.get(S_VARIABLES);
		for (int i = 0; i < jArr.size(); i++) {
			JSONObject jObj = (JSONObject) jArr.get(i);
			Variable variable = new Variable(jObj);
			if (variable.isValid())
			variables.add(variable);
		}
	}

	private void parseParameters(final JSONObject JObj) {
		CObjectCollection<Parameter> parameters = this.getParameters();
		JSONArray jArr = (JSONArray) JObj.get(S_PARAMETERS);
		for (int i = 0; i < jArr.size(); i++) {
			JSONObject jObj = (JSONObject) jArr.get(i);
			Parameter e = null;
			if (jObj.containsKey(Reference.S_MAP)) {
				e = new ParameterReference(jObj);
			} else {
				e = new FixedParameter(jObj);
			}
			if (e != null && e.isValid())
				parameters.add(e);
		}
	}

	private void parseFunction(JSONObject jObj) {
		this.init();
		this.parseName(jObj);
		this.parseEquation(jObj);
		this.parseParameters(jObj);
		this.parseVariables(jObj);
	}

	public Function(JSONObject jObj) {
		this.parseFunction(jObj);
	}

	@Override
	public boolean isValid() {
		boolean rtn = super.isValid();
		rtn = rtn && (this.getName() != null);
		rtn = rtn && (this.getEquation() != null);
		return rtn;
	}

	/**
	 * Getter for <i>equation</i>.
	 *
	 * @return value of equation
	 */
	public String getEquation() {
		return equation;
	}

	/**
	 * Getter for <i>variables</i>.
	 *
	 * @return value of variables
	 */
	public CObjectCollection<Variable> getVariables() {
		return variables;
	}

	/**
	 * Getter for <i>parameters</i>.
	 *
	 * @return value of parameters
	 */
	public CObjectCollection<Parameter> getParameters() {
		return parameters;
	}

	private String equation;
	private CObjectCollection<Variable> variables;
	private CObjectCollection<Parameter> parameters;

	public static final String S_NAME = "name";
	public static final String S_EQUATION = "equation";
	public static final String S_VARIABLES = "variables";
	public static final String S_PARAMETERS = "parameters";

}

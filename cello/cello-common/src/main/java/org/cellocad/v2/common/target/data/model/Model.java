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

import java.util.HashMap;
import java.util.Map;

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
public class Model extends CObject {

	private void init() {
		this.functions = new HashMap<>();
		this.parameters = new CObjectCollection<>();
	}

	private void parseName(final JSONObject JObj) {
		String value = ProfileUtils.getString(JObj, S_NAME);
		this.setName(value);
	}

	private void parseParameters(final JSONObject JObj) {
		CObjectCollection<FixedParameter> parameters = this.getParameters();
		JSONArray jArr = (JSONArray) JObj.get(S_PARAMETERS);
		for (int i = 0; i < jArr.size(); i++) {
			JSONObject jObj = (JSONObject) jArr.get(i);
			FixedParameter parameter = new FixedParameter(jObj);
			parameters.add(parameter);
		}
	}

	private void parseModel(final JSONObject jObj) {
		this.init();
		this.parseName(jObj);
		this.parseParameters(jObj);
	}

	public Model(final JSONObject jObj) {
		this.parseModel(jObj);
	}

	@Override
	public boolean isValid() {
		boolean rtn = super.isValid();
		rtn = rtn && (this.getName() != null);
		return rtn;
	}

	/*
	 * Function
	 */

	/**
	 * Returns the Function with name equivalent to parameter <i>name</i>.
	 * 
	 * @param name name of the Function to return
	 * @return the element with name equivalent to parameter <i>name</i>
	 */
	public Function getFunction(final String name) {
		return this.getFunctions().get(name);
	}

	/**
	 * Getter for <i>functions</i>.
	 *
	 * @return value of functions
	 */
	private Map<String, Function> getFunctions() {
		return functions;
	}

	private Map<String, Function> functions;

	/*
	 * FixedParameter
	 */

	public FixedParameter getParameterByName(final String name) {
		return this.getParameters().findCObjectByName(name);
	}

	public FixedParameter getParameterAtIdx(final int index) {
		FixedParameter rtn = null;
		if ((0 <= index) && (index < this.getNumParameter())) {
			rtn = this.getParameters().get(index);
		}
		return rtn;
	}

	/**
	 * Returns the number of FixedParameter in this instance.
	 * 
	 * @return the number of FixedParameter in this instance
	 */
	public int getNumParameter() {
		return this.getParameters().size();
	}

	/**
	 * Getter for <i>parameters</i>.
	 *
	 * @return value of parameters
	 */
	private CObjectCollection<FixedParameter> getParameters() {
		return parameters;
	}

	private CObjectCollection<FixedParameter> parameters;

	public static final String S_NAME = "name";
	public static final String S_FUNCTIONS = "functions";
	public static final String S_PARAMETERS = "parameters";

}

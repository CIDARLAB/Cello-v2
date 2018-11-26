/**
 * Copyright (C) 2018 Boston Univeristy (BU)
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
package org.cellocad.cello2.technologyMapping.algorithm.DSGRN.data.inequality;

import org.cellocad.cello2.common.CObject;
import org.cellocad.cello2.common.profile.ProfileUtils;
import org.json.simple.JSONObject;

/**
 *
 *
 * @author Timothy Jones
 *
 * @date 2018-10-29
 *
 */
public class Parameter extends CObject{
	
	private void init() {
	}
	
	private void parseParam(final JSONObject JObj) {
		String value = ProfileUtils.getString(JObj, "param");
		this.setParam(value);
	}
	
	private void parseInequalities(final JSONObject JObj) {
		String value = ProfileUtils.getString(JObj, "inequalities");
		Inequalities temp = new Inequalities(value);
		this.setInequalities(temp);
	}
	
	private void parseVariables(final JSONObject JObj) {
		String value = ProfileUtils.getString(JObj, "variables");
		Variables variables = new Variables(value);
		this.setVariables(variables);
	}
	
	private void parseParameter(final JSONObject JObj){
		this.parseParam(JObj);
		this.parseInequalities(JObj);
		this.parseVariables(JObj);
	}

	public Parameter(final JSONObject jObj) {
		init();
		this.parseParameter(jObj);
	}
	
	/*
	 * Param
	 */
	/**
	 * Getter for <i>param</i>
	 * @return value of <i>param</i>
	 */
	public String getParam() {
		return param;
	}

	/**
	 * Setter for <i>param</i>
	 * @param param the value to set <i>param</i>
	 */
	private void setParam(final String param) {
		this.param = param;
	}

	private String param;
	
	/*
	 * Inequalities
	 */
	/**
	 * Getter for <i>inequalities</i>
	 * @return value of <i>inequalities</i>
	 */
	public Inequalities getInequalities() {
		return inequalities;
	}

	/**
	 * Setter for <i>inequalities</i>
	 * @param inequalities the value to set <i>inequalities</i>
	 */
	private void setInequalities(final Inequalities inequalities) {
		this.inequalities = inequalities;
	}

	private Inequalities inequalities;
	
	/*
	 * Variables
	 */
	/**
	 * Getter for <i>variables</i>
	 * @return value of <i>variables</i>
	 */
	public Variables getVariables() {
		return variables;
	}

	/**
	 * Setter for <i>variables</i>
	 * @param variables the value to set <i>variables</i>
	 */
	private	void setVariables(final Variables variables) {
		this.variables = variables;
	}

	private Variables variables;

}

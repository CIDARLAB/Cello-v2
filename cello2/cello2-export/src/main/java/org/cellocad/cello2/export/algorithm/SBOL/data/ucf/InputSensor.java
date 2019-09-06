/**
 * Copyright (C) 2018 Boston University (BU)
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
package org.cellocad.cello2.export.algorithm.SBOL.data.ucf;

import org.cellocad.cello2.common.CObjectCollection;
import org.cellocad.cello2.common.profile.ProfileUtils;
import org.cellocad.cello2.export.algorithm.SBOL.data.Device;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 * The InputSensor is class representing an input sensor for the gate assignment
 * in the <i>SimulatedAnnealing</i> algorithm.
 *
 * @author Timothy Jones
 *
 * @date 2018-05-23
 *
 */
public class InputSensor extends Device {

	private void init() {
		this.parameters = new CObjectCollection<>();
	}

	private void parseName(final JSONObject JObj) {
		String value = ProfileUtils.getString(JObj, "name");
		this.setName(value);
	}

	private void parseUri(final JSONObject JObj) {
		String value = ProfileUtils.getString(JObj, "uri");
		this.setUri(value);
	}

	private void parsePromoter(final JSONObject JObj) {
		String value = ProfileUtils.getString(JObj, "promoter");
		this.setPromoter(value);
	}

	private void parseParameters(final JSONObject JObj) {
		CObjectCollection<Parameter> parameters = this.getParameters();
		JSONArray jArr = (JSONArray) JObj.get("parameters");
		for (int i = 0; i < jArr.size(); i++) {
			JSONObject jObj = (JSONObject) jArr.get(i);
			Parameter parameter = new Parameter(jObj);
			parameters.add(parameter);
		}
	}

	private void parseInputSensor(final JSONObject jObj, CObjectCollection<Part> parts) {
		this.parseName(jObj);
		this.parseUri(jObj);
		this.parsePromoter(jObj);
		this.parseParameters(jObj);
	}

	public InputSensor(final JSONObject jObj, CObjectCollection<Part> parts) {
		this.init();
		this.parseInputSensor(jObj, parts);
	}

	@Override
	public boolean isValid() {
		boolean rtn = super.isValid();
		rtn = rtn && (this.getPromoter() != null);
		rtn = rtn && (this.getParameters() != null);
		return rtn;
	}

	/*
	 * Promoter
	 */
	/**
	 * Getter for <i>promoter</i>
	 * 
	 * @return the promoter
	 */
	public String getPromoter() {
		return promoter;
	}

	/**
	 * Setter for <i>promoter</i>
	 * 
	 * @param promoter the promoter to set
	 */
	private void setPromoter(final String promoter) {
		this.promoter = promoter;
	}

	private String promoter;

	public Parameter getParameterValueByName(final String name) {
		return this.getParameters().findCObjectByName(name);
	}

	public Parameter getParameterAtIdx(final int index) {
		Parameter rtn = null;
		if ((0 <= index) && (index < this.getNumParameter())) {
			rtn = this.getParameters().get(index);
		}
		return rtn;
	}

	public int getNumParameter() {
		return this.getParameters().size();
	}

	private CObjectCollection<Parameter> getParameters() {
		return this.parameters;
	}

	private CObjectCollection<Parameter> parameters;

	public static String S_HI = "signal_high";
	public static String S_LO = "signal_low";

}

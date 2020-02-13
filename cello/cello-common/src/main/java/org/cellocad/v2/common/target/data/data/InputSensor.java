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
package org.cellocad.v2.common.target.data.data;

import org.cellocad.v2.common.profile.ProfileUtils;
import org.json.simple.JSONObject;

/**
 * 
 *
 * @author Timothy Jones
 *
 * @date 2018-05-23
 *
 */
public class InputSensor extends AssignableDevice {
	
	private void init() {
	}
	
	private void parseOutput(final JSONObject JObj) {
		String value = ProfileUtils.getString(JObj, S_OUTPUT);
		this.setOutput(value);
	}
	
	private void parseInputSensor(final JSONObject jObj) {
		this.parseOutput(jObj);
	}
	
	public InputSensor(final JSONObject jObj) {
		super(jObj);
		this.init();
		this.parseInputSensor(jObj);
	}
	
	@Override
	public boolean isValid() {
		boolean rtn = super.isValid();
		rtn = rtn && (this.getOutput() != null);
		return rtn;
	}
	
	/*
	 * Output
	 */

	/**
	 * Getter for <i>output</i>.
	 * 
	 * @return the output
	 */
	public String getOutput() {
		return output;
	}

	/**
	 * Setter for <i>output</i>.
	 * 
	 * @param output the output to set
	 */
	private void setOutput(final String output) {
		this.output = output;
	}

	private String output;

	/*
	 * InputSensorModel
	 */

	/**
	 * Getter for <i>inputSensorModel</i>.
	 *
	 * @return value of inputSensorModel
	 */
	public InputSensorModel getInputSensorModel() {
		return inputSensorModel;
	}

	/**
	 * Setter for <i>inputSensorModel</i>.
	 *
	 * @param inputSensorModel the inputSensorModel to set
	 */
	public void setInputSensorModel(InputSensorModel inputSensorModel) {
		this.inputSensorModel = inputSensorModel;
	}

	private InputSensorModel inputSensorModel;

	private static final String S_OUTPUT = "output";
	public static final String S_MODEL = "model";

}

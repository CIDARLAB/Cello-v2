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
package org.cellocad.cello2.technologyMapping.algorithm.MinCrosstalkYeast.data.ucf;

import org.cellocad.cello2.common.CObject;
import org.cellocad.cello2.common.profile.ProfileUtils;
import org.json.simple.JSONObject;

/**
 * The ResponseFunctionParameter is class representing a Parameter of a Response Function for the gate assignment in the <i>SimulatedAnnealing</i> algorithm.
 * 
 * @author Timothy Jones
 * 
 * @date 2018-07-19
 *
 */
public class ResponseFunctionParameter extends CObject{
	
	private void parseName(final JSONObject JObj){
		String value = ProfileUtils.getString(JObj, "name");
		this.setName(value);
	}
	
	private void parseValue(final JSONObject JObj){
		Double value = ProfileUtils.getDouble(JObj, "value");
		this.setValue(value);
	}

		
	private void parseResponseFunctionParameter(final JSONObject jObj) {
		this.parseName(jObj);
		this.parseValue(jObj);
    }
	
	private void init() {
	}
	
	public ResponseFunctionParameter(final JSONObject jobj) {
		this.init();
		this.parseResponseFunctionParameter(jobj);
	}
	
	/*
	 * Value
	 */
	private void setValue(final Double value){
		this.value = value;
	}
	
	public Double getValue(){
		return this.value;
	}
	
	private Double value;

	
}

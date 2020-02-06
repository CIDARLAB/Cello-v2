/**
 * Copyright (C) 2017 Massachusetts Institute of Technology (MIT)
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
package org.cellocad.v2.constraint.technologyMapping.Cello_JY_TP;

import org.cellocad.v2.common.profile.ProfileObject;
import org.cellocad.v2.common.profile.ProfileUtils;
import org.json.simple.JSONObject;

/**
 * The Cello_JY_TP class implements the constraints for <i>Cello_JY_TP</i> algorithm in the <i>technologyMapping</i> stage.
 * 
 * @author Vincent Mirian
 * 
 * @date 2018-05-21
 *
 */
public class Cello_JY_TP_Constraint extends ProfileObject{

	/*
	 * Parse
	 */
	/**
	 *  Extracts the value of type attribute from the parameter <i>JObj</i>, and sets the name to the extracted value
	 *  
	 *  @param JObj the JavaScript Object Notation (JSON) representation of the ProfileObject Object
	 *  @throws RuntimeException if the name is not specified in the parameter <i>JObj</i>
	 */
	protected void parseType(final JSONObject JObj){
		// type
		String type = (String) ProfileUtils.getString(JObj, "type");
		if (type == null) {
	    	throw new RuntimeException("Type not specified!");
		}
		this.setSType(type);
	}
	
	/**
	 *  Extracts the value of type attribute from the parameter <i>JObj</i>, and sets the name to the extracted value
	 *  
	 *  @param JObj the JavaScript Object Notation (JSON) representation of the ProfileObject Object
	 *  @throws RuntimeException if the name is not specified in the parameter <i>JObj</i>
	 */
	protected void parseData(final JSONObject JObj){
		// data
		String data = (String) ProfileUtils.getString(JObj, "data");
		if (data == null) {
	    	throw new RuntimeException("Type not specified!");
		}
		this.setData(data);
	}
	
	public Cello_JY_TP_Constraint(JSONObject JObj){
		super(JObj);
		this.parseType(JObj);
		this.parseData(JObj);
	}

	/*
	 * SType
	 */
	public String getSType() {
		return this.stype;
	}
	public void setSType(String sType) {
		this.stype = sType;
	}
	private String stype;

	/*
	 * Data
	 */
	public String getData() {
		return this.data;
	}
	public void setData(String data) {
		this.data = data;
	}
	private String data;
	
}

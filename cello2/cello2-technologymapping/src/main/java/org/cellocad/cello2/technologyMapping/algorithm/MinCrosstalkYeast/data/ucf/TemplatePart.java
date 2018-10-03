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
package org.cellocad.cello2.technologyMapping.algorithm.MinCrosstalkYeast.data.ucf;

import org.cellocad.cello2.common.profile.ProfileUtils;
import org.json.simple.JSONObject;

/**
 * The Part is class representing a genetic part for the gate assignment in the <i>SimulatedAnnealing</i> algorithm.
 * 
 * @author Vincent Mirian
 * 
 * @date 2018-05-21
 *
 */
public class TemplatePart extends AbstractPart{

	private void parsePartType(final JSONObject JObj){
		String value = ProfileUtils.getString(JObj, "type");
		this.setPartType(value);
	}
	
	private void parsePart(final JSONObject jObj) {
		this.parsePartType(jObj);
    }
	
	public TemplatePart(final JSONObject jObj) {
		this.parsePart(jObj);
	}
	
	public TemplatePart(String type) {
		this.setPartType(type);
	}

	/*
	 * Type
	 */
	private void setPartType(final String partType){
		this.partType = partType;
	}
	
	public String getPartType(){
		return this.partType;
	}
	
	private String partType;
	
}

/**
 * Copyright (C) 2017-2020
 * Massachusetts Institute of Technology (MIT)
 * Boston University (BU)
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
 * The Part is class representing a genetic part for the gate assignment in the
 * <i>SimulatedAnnealing</i> algorithm.
 * 
 * @author Vincent Mirian
 * @author Timothy Jones
 * 
 * @date 2018-05-21
 *
 */
public class Part extends DNAComponent {

	private void init() {
	}
	
	private void parsePartType(final JSONObject JObj){
		String value = ProfileUtils.getString(JObj, S_TYPE);
		this.setPartType(value);
	}
	
	private void parseDNASequence(final JSONObject JObj){
		String value = ProfileUtils.getString(JObj, S_DNASEQUENCE);
		this.setDNASequence(value);
	}
	
	private void parsePart(final JSONObject jObj) {
		this.parsePartType(jObj);
		this.parseDNASequence(jObj);
    }
	
	public Part(final JSONObject jObj) {
		super(jObj);
		this.init();
		this.parsePart(jObj);
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
	
	/*
	 * DNASequence
	 */
	private void setDNASequence(final String dnaSequence){
		this.dnaSequence = dnaSequence;
	}
	
	public String getDNASequence(){
		return this.dnaSequence;
	}
	
	private String dnaSequence;

	private static final String S_TYPE = "type";
	private static final String S_DNASEQUENCE = "dnasequence";

}

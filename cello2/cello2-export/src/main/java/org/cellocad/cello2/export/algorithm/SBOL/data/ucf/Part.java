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
package org.cellocad.cello2.export.algorithm.SBOL.data.ucf;

import org.cellocad.cello2.common.CObject;
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
public class Part extends CObject{

	private void parseName(final JSONObject JObj){
		String value = ProfileUtils.getString(JObj, "name");
		this.setName(value);
	}
	
	private void parsePartType(final JSONObject JObj){
		String value = ProfileUtils.getString(JObj, "type");
		this.setPartType(value);
	}
	
	private void parseDNASequence(final JSONObject JObj){
		String value = ProfileUtils.getString(JObj, "dnasequence");
		this.setDNASequence(value);
	}
	
	private void parseUri(final JSONObject JObj){
		String value = ProfileUtils.getString(JObj, "uri");
		this.setUri(value);
	}
	
	private void parsePart(final JSONObject jObj) {
		this.parseName(jObj);
		this.parsePartType(jObj);
		this.parseDNASequence(jObj);
		this.parseUri(jObj);
    }
	
	public Part(final JSONObject jObj) {
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
	
	/*
	 * URI
	 */
	public void setUri(final String uri){
		this.uri = uri;
	}
	
	public String getUri(){
		return this.uri;
	}
	
	private String uri;
}

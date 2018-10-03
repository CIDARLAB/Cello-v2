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
package org.cellocad.cello2.placing.algorithm.Eugene.data.ucf;

import org.cellocad.cello2.common.CObjectCollection;
import org.cellocad.cello2.common.profile.ProfileUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 * 
 *
 * @author Timothy Jones
 *
 * @date 2018-05-23
 *
 */
public class InputSensor extends Assignable{
	
	private void init() {
		parts = new CObjectCollection<Part>();
	}
	
	private void parseName(final JSONObject JObj){
		String value = ProfileUtils.getString(JObj, "name");
		this.setName(value);
	}
	
	private void parsePromoter(final JSONObject JObj){
		String value = ProfileUtils.getString(JObj, "promoter");
		this.setPromoter(value);
	}
	
	private void parseLowSignal(final JSONObject JObj){
		Double value = ProfileUtils.getDouble(JObj, "signal_low");
		this.setLowSignal(value);
	}
	
	private void parseHighSignal(final JSONObject JObj){
		Double value = ProfileUtils.getDouble(JObj, "signal_high");
		this.setHighSignal(value);
	}
	
	private void parseParts(final JSONObject jObj, CObjectCollection<Part> parts) {
		JSONArray jArr = (JSONArray) jObj.get("parts");
		for (int i = 0; i < jArr.size(); i++) {
			String partName = (String) jArr.get(i);
			Part part = parts.findCObjectByName(partName);
			this.getParts().add(part);
		}
	}
	
	private void parseInputSensor(final JSONObject jObj, CObjectCollection<Part> parts) {
		this.parseName(jObj);
		this.parsePromoter(jObj);
		this.parseLowSignal(jObj);
		this.parseHighSignal(jObj);
		this.parseParts(jObj,parts);
	}
	
	public InputSensor(final JSONObject jObj, CObjectCollection<Part> parts) {
		this.init();
		this.parseInputSensor(jObj,parts);
	}
	
	@Override
	public boolean isValid() {
		boolean rtn = super.isValid();
		rtn = rtn && (this.getName() != null);
		rtn = rtn && (this.getPromoter() != null);
		rtn = rtn && (this.getLowSignal() != null);
		rtn = rtn && (this.getHighSignal() != null);
		return rtn;
	}
	
	/*
	 * Promoter
	 */
	/**
	 * Getter for <i>promoter</i>
	 * @return the promoter
	 */
	public String getPromoter() {
		return promoter;
	}

	/**
	 * Setter for <i>promoter</i>
	 * @param promoter the promoter to set
	 */
	private void setPromoter(final String promoter) {
		this.promoter = promoter;
	}

	private String promoter;
	
	/*
	 * Low Signal
	 */
	/**
	 * Getter for <i>lowSignal</i>
	 * @return the lowSignal
	 */
	public Double getLowSignal() {
		return lowSignal;
	}

	/**
	 * Setter for <i>lowSignal</i>
	 * @param promoter the promoter to set
	 */
	private void setLowSignal(final Double lowSignal) {
		this.lowSignal = lowSignal;
	}

	private Double lowSignal;
	
	/*
	 * High Signal
	 */
	/**
	 * Getter for <i>highSignal</i>
	 * @return the highSignal
	 */
	public Double getHighSignal() {
		return highSignal;
	}

	/**
	 * Setter for <i>highSignal</i>
	 * @param promoter the promoter to set
	 */
	private void setHighSignal(final Double highSignal) {
		this.highSignal = highSignal;
	}

	private Double highSignal;
	
	/*
	 * Parts
	 */
	private CObjectCollection<Part> getParts(){
		return this.parts;
	}
	
	public Part getPartAtIdx(final int index){
		Part rtn = null;
		if (
				(0 <= index)
				&&
				(index < this.getNumParts())
				) {
			rtn = this.getParts().get(index);
		}
		return rtn;
	}
	
	public int getNumParts(){
		return this.getParts().size();
	}
	
	private CObjectCollection<Part> parts;
	
}

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
package org.cellocad.cello2.technologyMapping.algorithm.DSGRN.data.ucf;

import java.util.ArrayList;
import java.util.List;

import org.cellocad.cello2.common.CObject;
import org.cellocad.cello2.common.profile.ProfileUtils;
import org.json.simple.JSONObject;

/**
 * The ResponseFunctionVariable is class representing a Variable of a Response Function for the gate assignment in the <i>SimulatedAnnealing</i> algorithm.
 * 
 * @author Vincent Mirian
 * 
 * @date 2018-05-21
 *
 */
public class ResponseFunctionVariable extends CObject{

	private void parseName(final JSONObject JObj){
		String value = ProfileUtils.getString(JObj, "name");
		this.setName(value);
	}
	
	private void parseOffThreshold(final JSONObject JObj){
		Double value = ProfileUtils.getDouble(JObj, "off_threshold");
		this.setOffThreshold(value);
	}
	
	private void parseOnThreshold(final JSONObject JObj){
		Double value = ProfileUtils.getDouble(JObj, "on_threshold");
		this.setOnThreshold(value);
	}
	
	private void parseResponseFunctionObj(final JSONObject jObj) {
		this.parseName(jObj);
		this.parseOffThreshold(jObj);
		this.parseOnThreshold(jObj);
    }
	
	private void init() {
		cytometryData = new ArrayList<CytometryData>();
	}
	
	public ResponseFunctionVariable(final JSONObject jobj) {
		init();
		this.parseResponseFunctionObj(jobj);
	}
	
	/*
	 * OffThreshold
	 */
	private void setOffThreshold(final double offThreshold){
		this.offThreshold = offThreshold;
	}
	
	public double getOffThreshold(){
		return this.offThreshold;
	}
	
	private double offThreshold;
	
	/*
	 * OnThreshold
	 */
	private void setOnThreshold(final double onThreshold){
		this.onThreshold = onThreshold;
	}
	
	public double getOnThreshold(){
		return this.onThreshold;
	}
	
	private double onThreshold;

	/*
	 * CasetteParts
	 */
	public void setCasetteParts(final CasetteParts casetteParts){
		this.casetteParts = casetteParts;
	}
	
	public CasetteParts getCasetteParts(){
		return this.casetteParts;
	}
	
	private CasetteParts casetteParts;
	
	/*
	 * Toxicity
	 */
	public void setToxicity(final Toxicity toxicity){
		this.toxicity = toxicity;
	}
	
	public Toxicity getToxicity(){
		return this.toxicity;
	}
	
	private Toxicity toxicity;

	/*
	 * CytometryData
	 */
	private List<CytometryData> getCytometryData(){
		return this.cytometryData;
	}
	
	public void addCytometryData(final CytometryData cytometryData){
		this.getCytometryData().add(cytometryData);
	}
	
	public CytometryData getCytometryDataAtIdx(int index){
		CytometryData rtn = null;
		if (
				(0 <= index)
				&&
				(index < this.getNumCytometryData())
				) {
			rtn = this.getCytometryData().get(index);
		}
		return rtn;
	}
	
	public int getNumCytometryData(){
		return this.getCytometryData().size();
	}
	
	private List<CytometryData> cytometryData;
}

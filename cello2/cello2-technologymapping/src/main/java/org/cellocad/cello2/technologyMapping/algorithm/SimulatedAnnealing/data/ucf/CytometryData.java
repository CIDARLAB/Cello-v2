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
package org.cellocad.cello2.technologyMapping.algorithm.SimulatedAnnealing.data.ucf;

import java.util.ArrayList;
import java.util.List;

import org.cellocad.cello2.common.CObject;
import org.cellocad.cello2.common.profile.ProfileUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 * The CytometryData is class representing the cytometry data for a gate in the gate assignment of the <i>SimulatedAnnealing</i> algorithm.
 * 
 * @author Vincent Mirian
 * 
 * @date 2018-05-21
 *
 */
public class CytometryData extends CObject{

	private void parseMapVariable(final JSONObject JObj){
		String value = ProfileUtils.getString(JObj, "maps_to_variable");
		this.setName(value);
		this.setMapVariable(value);
	}
	
	private void parseInput(final JSONObject JObj){
		Double value = ProfileUtils.getDouble(JObj, "input");
		this.setInput(value);
	}

	private void parseOutputBins(final JSONObject JObj){
		JSONArray JArr = (JSONArray) JObj.get("output_bins");
		for (int i = 0; i < JArr.size(); i++) {
			this.getOutputBins().add((Double) JArr.get(i));
		}
	}

	private void parseOutputCounts(final JSONObject JObj){
		JSONArray JArr = (JSONArray) JObj.get("output_counts");
		for (int i = 0; i < JArr.size(); i++) {
			this.getOutputBins().add((Double) JArr.get(i));
		}
	}

	private void parseCytometryData(final JSONObject jObj) {
		this.parseMapVariable(jObj);
		this.parseInput(jObj);
		this.parseOutputBins(jObj);
		this.parseOutputCounts(jObj);
    }
	
	private void init() {
		outputBins = new ArrayList<Double>();
		outputCounts = new ArrayList<Double>();
	}
	
	public CytometryData(final JSONObject jobj) {
		this.init();
		this.parseCytometryData(jobj);
	}

	/*
	 * MapVariable
	 */
	private void setMapVariable(final String mapVariable){
		this.mapVariable = mapVariable;
	}
	
	public String getMapVariable(){
		return this.mapVariable;
	}
	
	private String mapVariable;
	
	/*
	 * input
	 */
	private void setInput(final Double input){
		this.input = input;
	}
	
	public Double getInput(){
		return this.input;
	}
	
	private Double input;
	
	/*
	 * OutputBins
	 */
	private List<Double> getOutputBins(){
		return this.outputBins;
	}
	
	public Double getOutputBinsAtIdx(final int index){
		Double rtn = null;
		if (
				(0 <= index)
				&&
				(index < this.getNumOutputBins())
				) {
			rtn = this.getOutputBins().get(index);
		}
		return rtn;
	}
	
	public int getNumOutputBins(){
		return this.getOutputBins().size();
	}
	
	private List<Double> outputBins;
	
	/*
	 * OutputCounts
	 */
	private List<Double> getOutputCounts(){
		return this.outputCounts;
	}
	
	public Double getOutputCountsAtIdx(final int index){
		Double rtn = null;
		if (
				(0 <= index)
				&&
				(index < this.getNumOutputCounts())
				) {
			rtn = this.getOutputCounts().get(index);
		}
		return rtn;
	}
	
	public int getNumOutputCounts(){
		return this.getOutputCounts().size();
	}
	
	private List<Double> outputCounts;
}

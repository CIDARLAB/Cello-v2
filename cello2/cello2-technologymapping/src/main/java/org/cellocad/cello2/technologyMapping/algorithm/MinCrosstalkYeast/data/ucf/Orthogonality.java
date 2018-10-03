/**
 * Copyright (C) 2018 Massachusetts Institute of Technology (MIT), Boston Univerity (BU)
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

import java.util.ArrayList;
import java.util.HashMap;
/**
 * The GateParts is class representing the parts description for a gate in the gate assignment of the <i>SimulatedAnnealing</i> algorithm.
 * 
 * @author Vincent Mirian
 * @author Timothy Jones
 * 
 * @date 2018-09-20
 *
 */
import java.util.List;
import java.util.Map;

import org.cellocad.cello2.common.CObject;
import org.cellocad.cello2.common.Pair;
import org.cellocad.cello2.common.profile.ProfileUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class Orthogonality extends CObject{

	private void parseName(final JSONObject JObj){
		String value = ProfileUtils.getString(JObj, "gate_name");
		this.setGateName(value);
	}
	
	private void parseMapVariable(final JSONObject JObj){
		String value = ProfileUtils.getString(JObj, "maps_to_variable");
		this.setMapVariable(value);
	}

	private void parseInputs(final JSONObject JObj){
		JSONArray jArr = (JSONArray) JObj.get("inputs");
		for (int i = 0; i < jArr.size(); i++) {
			String input = ProfileUtils.getString(jArr.get(i));
			this.getInput().add(input);
		}
	}

	private void parseOutputs(final JSONObject JObj){
		JSONArray jArr = (JSONArray) JObj.get("outputs");
		for (int i = 0; i < jArr.size(); i++) {
			Double output = ProfileUtils.getDouble(jArr.get(i));
			this.getOutput().add(output);
		}
	}
	
	private void parseCrosstalkData(final JSONObject JObj) {
		JSONObject data = (JSONObject) JObj.get("crosstalk_data");
		this.parseInputs(data);
		this.parseOutputs(data);
	}
	
	private void parseInputOutputPairs(){
		if (this.getNumInput() != this.getNumOutput()) {
			throw new RuntimeException ("Error with Orthogonality");
		}
		for (int i = 0; i < this.getNumInput(); i++) {
			String input = this.getInputAtIdx(i);
			Double output = this.getOutputAtIdx(i);
			Pair<String, Double> pair = new Pair<String,Double>(input, output);
			this.getInputOutputPairs().add(pair);
		}
	}
	
	private void parseInputOutputEntryMap() {
		if (this.getNumInput() != this.getNumOutput()) {
			throw new RuntimeException ("Error with Orthogonality");
		}
		for (int i = 0; i < this.getNumInput(); i++) {
			String input = this.getInputAtIdx(i);
			Double output = this.getOutputAtIdx(i);
			this.getInputOutputEntryMap().put(input,output);
		}
	}
	
	private void init() {
		input = new ArrayList<String>();
		output = new ArrayList<Double>();
		inputOutputPairs = new ArrayList<Pair<String,Double>>();
		inputOutputEntryMap = new HashMap<String,Double>();
	}
	

	private void parseOrthogonality(final JSONObject jObj) {
		init();
		this.parseName(jObj);
		this.parseMapVariable(jObj);
		this.parseCrosstalkData(jObj);
		this.parseInputOutputPairs();
		this.parseInputOutputEntryMap();
    }
	
	public Orthogonality(final JSONObject jObj) {
		this.parseOrthogonality(jObj);
	}

	/*
	 * GateName
	 */
	private void setGateName(final String gateName){
		this.setName(gateName);
	}
	
	public String getGateName(){
		return this.getName();
	}
	
	/*
	 * Gate
	 */
	public void setGate(final Gate gate) {
		this.gate = gate;
	}
	
	public Gate getGate() {
		return this.gate;
	}
	
	private Gate gate;
	
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
	 * Entry Map
	 */
	private Map<String,Double> getInputOutputEntryMap() {
		return this.inputOutputEntryMap;
	}
	
	public Double getOutputByInput(String input) {
		return this.getInputOutputEntryMap().get(input);
	}
	
	private Map<String,Double> inputOutputEntryMap;
	
	/*
	 * Input
	 */
	private List<String> getInput(){
		return this.input;
	}
	
	public String getInputAtIdx(final int index){
		String rtn = null;
		if (
				(0 <= index)
				&&
				(index < this.getNumInput())
				) {
			rtn = this.getInput().get(index);
		}
		return rtn;
	}
	
	public int getNumInput(){
		return this.getInput().size();
	}
	
	private List<String> input;
	
	/*
	 * Output
	 */
	private List<Double> getOutput(){
		return this.output;
	}
	
	public Double getOutputAtIdx(final int index){
		Double rtn = null;
		if (
				(0 <= index)
				&&
				(index < this.getNumOutput())
				) {
			rtn = this.getOutput().get(index);
		}
		return rtn;
	}
	
	public int getNumOutput(){
		return this.getOutput().size();
	}
	
	private List<Double> output;
	
	/*
	 * Output
	 */
	private List<Pair<String,Double>> getInputOutputPairs(){
		return this.inputOutputPairs;
	}
	
	public Pair<String,Double> getInputOutputPairAtIdx(final int index){
		Pair<String,Double> rtn = null;
		if (
				(0 <= index)
				&&
				(index < this.getNumInputOutputPairs())
				) {
			rtn = this.getInputOutputPairs().get(index);
		}
		return rtn;
	}
	
	public int getNumInputOutputPairs(){
		return this.getInputOutputPairs().size();
	}
	
	private List<Pair<String,Double>> inputOutputPairs;
	
}

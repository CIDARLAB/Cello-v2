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
package placing.algorithm.Eugene.data.ucf;

import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import common.CObject;
import common.profile.ProfileUtils;

/**
 * The Cytometry is class representing the collection of cytometry data for a gate in the gate assignment of the <i>SimulatedAnnealing</i> algorithm.
 * 
 * @author Vincent Mirian
 * 
 * @date 2018-05-21
 *
 */
public class Cytometry extends CObject{

	private void parseGateName(final JSONObject JObj){
		String value = ProfileUtils.getString(JObj, "gate_name");
		this.setGateName(value);
	}
	
	private void parseCytometryData(final JSONObject JObj){
		JSONArray JArr = (JSONArray) JObj.get("cytometry_data");
		for (int i = 0; i < JArr.size(); i++) {
			this.getCytometryData().add(new CytometryData((JSONObject) JArr.get(i)));
		}
	}

	private void parseCytometry(final JSONObject jObj) {
		this.parseGateName(jObj);
		this.parseCytometryData(jObj);
    }
	
	private void init() {
		cytometryData = new ArrayList<CytometryData>();
	}
	
	public Cytometry(final JSONObject jobj) {
		this.init();
		this.parseCytometry(jobj);
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
	 * CytometryData
	 */
	private List<CytometryData> getCytometryData(){
		return this.cytometryData;
	}
	
	public CytometryData getCytometryDataAtIdx(final int index){
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

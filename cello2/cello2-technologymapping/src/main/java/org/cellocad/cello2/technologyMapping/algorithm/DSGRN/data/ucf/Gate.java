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

import org.cellocad.cello2.common.profile.ProfileUtils;
import org.json.simple.JSONObject;

/**
 * The Gate is class representing a gate for the gate assignment in the <i>SimulatedAnnealing</i> algorithm.
 * 
 * @author Vincent Mirian
 * 
 * @date 2018-05-21
 *
 */
public class Gate extends Assignable{

	private void parseRegulator(final JSONObject JObj){
		String value = ProfileUtils.getString(JObj, "regulator");
		this.setRegulator(value);
	}

	private void parseGroupName(final JSONObject JObj){
		String value = ProfileUtils.getString(JObj, "group_name");
		this.setGroupName(value);
	}

	private void parseGateName(final JSONObject JObj){
		String value = ProfileUtils.getString(JObj, "gate_name");
		this.setGateName(value);
		this.setName(value);
	}
	
	private void parseGateType(final JSONObject JObj){
		String value = ProfileUtils.getString(JObj, "gate_type");
		this.setGateType(value);
	}
	
	private void parseSystem(final JSONObject JObj){
		String value = ProfileUtils.getString(JObj, "system");
		this.setSystem(value);
	}
	
	private void parseGate(final JSONObject jObj) {
		this.parseRegulator(jObj);
		this.parseGroupName(jObj);
		this.parseGateName(jObj);
		this.parseGateType(jObj);
		this.parseSystem(jObj);
	}
	
	public Gate(final JSONObject jObj) {
		this.parseGate(jObj);
	}
	
	@Override
	public boolean isValid() {
		boolean rtn = super.isValid();
		rtn = rtn && (this.getRegulator() != null);
		rtn = rtn && (this.getGroupName() != null);
		rtn = rtn && (this.getGateName() != null);
		rtn = rtn && (this.getGateType() != null);
		rtn = rtn && (this.getSystem() != null);
		return rtn;
	}
	
	/*
	 * Regulator
	 */
	private void setRegulator(final String regulator){
		this.regulator = regulator;
	}
	
	public String getRegulator(){
		return this.regulator;
	}
	
	private String regulator;

	/*
	 * GroupName
	 */
	private void setGroupName(final String groupName){
		this.groupName = groupName;
	}
	
	public String getGroupName(){
		return this.groupName;
	}
	
	private String groupName;
	
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
	 * GateType
	 */
	private void setGateType(final String gateType){
		this.gateType = gateType;
	}
	
	public String getGateType(){
		return this.gateType;
	}
	
	private String gateType;
	
	/*
	 * System
	 */
	private void setSystem(final String system){
		this.system = system;
	}
	
	public String getSystem(){
		return this.system;
	}
	
	private String system;
	
	/*
	 * ResponseFunction
	 */
	public void setResponseFunction(final ResponseFunction responseFunction){
		this.responseFunction = responseFunction;
	}
	
	public ResponseFunction getResponseFunction(){
		return this.responseFunction;
	}
	
	private ResponseFunction responseFunction;

	/*
	 * GateParts
	 */
	public void setGateParts(final GateParts gateParts){
		this.gateParts = gateParts;
	}
	
	public GateParts getGateParts(){
		return this.gateParts;
	}
	
	private GateParts gateParts;
	
}

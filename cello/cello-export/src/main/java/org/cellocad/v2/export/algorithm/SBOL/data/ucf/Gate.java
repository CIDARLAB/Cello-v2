/**
 * Copyright (C) 2020
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
package org.cellocad.v2.export.algorithm.SBOL.data.ucf;

import java.awt.Color;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.cellocad.v2.common.profile.ProfileUtils;
import org.cellocad.v2.export.algorithm.SBOL.data.Device;
import org.json.simple.JSONObject;

/**
 * The Gate is class representing a gate for the gate assignment in the
 * <i>SimulatedAnnealing</i> algorithm.
 * 
 * @author Vincent Mirian
 * @author Timothy Jones
 * 
 * @date 2018-05-21
 *
 */
public class Gate extends Device{

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
	}
	
	private void parseGateType(final JSONObject JObj){
		String value = ProfileUtils.getString(JObj, "gate_type");
		this.setGateType(value);
	}
	
	private void parseSystem(final JSONObject JObj){
		String value = ProfileUtils.getString(JObj, "system");
		this.setSystem(value);
	}

	private void parseColor(final JSONObject JObj) {
		String value = ProfileUtils.getString(JObj, "color_hexcode");
		Pattern pattern = Pattern.compile("[0-9A-Fa-f]{6}");
		Matcher matcher = pattern.matcher(value);
		if (matcher.matches()) {
			Color color = Color.decode("0x" + value);
			this.setColor(color);
		}
	}

	private void parseUri(final JSONObject JObj){
		String value = ProfileUtils.getString(JObj, "uri");
		this.setUri(value);
	}
	
	private void parseGate(final JSONObject jObj) {
		this.parseRegulator(jObj);
		this.parseGroupName(jObj);
		this.parseGateName(jObj);
		this.parseGateType(jObj);
		this.parseSystem(jObj);
		this.parseColor(jObj);
		this.parseUri(jObj);
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
		rtn = rtn && (this.getColor() != null);
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
	 * Color
	 */
	private void setColor(final Color color) {
		this.color = color;
	}

	public Color getColor() {
		return this.color;
	}

	private Color color;

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

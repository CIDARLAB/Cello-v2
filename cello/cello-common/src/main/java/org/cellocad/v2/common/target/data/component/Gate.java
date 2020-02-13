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
package org.cellocad.v2.common.target.data.component;

import java.awt.Color;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.cellocad.v2.common.profile.ProfileUtils;
import org.cellocad.v2.common.target.data.model.GateModel;
import org.cellocad.v2.common.target.data.structure.GateStructure;
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
public class Gate extends AssignableDevice {

	private void init() {
	}

	private void parseRegulator(final JSONObject JObj){
		String value = ProfileUtils.getString(JObj, S_REGULATOR);
		this.setRegulator(value);
	}

	private void parseGroup(final JSONObject JObj){
		String value = ProfileUtils.getString(JObj, S_GROUP);
		this.setGroup(value);
	}

	private void parseGateType(final JSONObject JObj){
		String value = ProfileUtils.getString(JObj, S_GATETYPE);
		this.setGateType(value);
	}
	
	private void parseSystem(final JSONObject JObj){
		String value = ProfileUtils.getString(JObj, S_SYSTEM);
		this.setSystem(value);
	}
	
	private void parseColor(final JSONObject JObj) {
		String value = ProfileUtils.getString(JObj, S_COLOR);
		Pattern pattern = Pattern.compile("[0-9A-Fa-f]{6}");
		Matcher matcher = pattern.matcher(value);
		if (matcher.matches()) {
			Color color = Color.decode("0x" + value);
			this.setColor(color);
		}
	}

	private void parseGate(final JSONObject jObj) {
		this.parseRegulator(jObj);
		this.parseGroup(jObj);
		this.parseGateType(jObj);
		this.parseSystem(jObj);
		this.parseColor(jObj);
	}
	
	public Gate(final JSONObject jObj) {
		super(jObj);
		this.init();
		this.parseGate(jObj);
	}
	
	@Override
	public boolean isValid() {
		boolean rtn = super.isValid();
		rtn = rtn && (this.getRegulator() != null);
		rtn = rtn && (this.getGroup() != null);
		rtn = rtn && (this.getGateType() != null);
		rtn = rtn && (this.getSystem() != null);
		rtn = rtn && (this.getColor() != null);
		rtn = rtn && (this.getModel() != null);
		rtn = rtn && (this.getGateStructure() != null);
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
	 * Group
	 */
	private void setGroup(final String group) {
		this.group = group;
	}
	
	public String getGroup() {
		return this.group;
	}
	
	private String group;
	
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
	 * GateModel
	 */

	/**
	 * Getter for <i>gateModel</i>.
	 *
	 * @return value of gateModel
	 */
	public GateModel getModel() {
		return gateModel;
	}

	/**
	 * Setter for <i>gateModel</i>.
	 *
	 * @param gateModel the gateModel to set
	 */
	public void setModel(GateModel gateModel) {
		this.gateModel = gateModel;
	}

	private GateModel gateModel;

	/*
	 * GateStructure
	 */

	/**
	 * Getter for <i>gateStructure</i>.
	 *
	 * @return value of <i>gateStructure</i>
	 */
	public GateStructure getGateStructure() {
		return gateStructure;
	}

	/**
	 * Setter for <i>gateStructure</i>.
	 *
	 * @param gateStructure the value to set <i>gateStructure</i>
	 */
	public void setGateStructure(final GateStructure gateStructure) {
		this.gateStructure = gateStructure;
	}
	
	private GateStructure gateStructure;

	public static final String S_REGULATOR = "regulator";
	public static final String S_GROUP = "group";
	public static final String S_GATETYPE = "gate_type";
	public static final String S_SYSTEM = "system";
	public static final String S_COLOR = "color";
	public static final String S_MODEL = "model";
	public static final String S_STRUCTURE = "structure";

}

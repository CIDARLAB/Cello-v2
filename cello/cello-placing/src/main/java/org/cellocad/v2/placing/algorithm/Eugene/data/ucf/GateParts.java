/**
 * Copyright (C) 2017 Massachusetts Institute of Technology (MIT)
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
package org.cellocad.v2.placing.algorithm.Eugene.data.ucf;

import java.util.HashMap;
import java.util.Map;

import org.cellocad.v2.common.CObject;
import org.cellocad.v2.common.CObjectCollection;
import org.cellocad.v2.common.profile.ProfileUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 * The GateParts is class representing the parts description for a gate in the gate assignment of the <i>SimulatedAnnealing</i> algorithm.
 * 
 * @author Vincent Mirian
 * @author Timothy Jones
 * 
 * @date 2018-05-21
 *
 */
public class GateParts extends CObject{

	private void parseGateName(final JSONObject JObj){
		String value = ProfileUtils.getString(JObj, "gate_name");
		this.setGateName(value);
	}
	
	private void parseExpressionCassettes(final JSONObject JObj, CObjectCollection<Part> parts){
		Map< String, CasetteParts > expressionCassettes = this.getExpressionCassettes();
		JSONArray jArr = (JSONArray) JObj.get("expression_cassettes");
		for (int i = 0; i < jArr.size(); i++) {
			JSONObject jObj = (JSONObject) jArr.get(i);
			String name = ProfileUtils.getString(jObj, "maps_to_variable");
			if (name == null) {
				continue;
			}
			JSONArray JArr = (JSONArray) jObj.get("cassette_parts");
			int size = JArr.size();
			if ((JArr == null) || (size == 0)) {
				continue;
			}
			CasetteParts data = new CasetteParts(JArr, parts);
			expressionCassettes.put(name, data);
		}
	}

	private void parsePromoter(final JSONObject JObj){
		String value = ProfileUtils.getString(JObj, "promoter");
		this.setPromoter(value);
	}

	private void parseGateParts(final JSONObject jObj, CObjectCollection<Part> parts) {
		this.parseGateName(jObj);
		this.parseExpressionCassettes(jObj, parts);
		this.parsePromoter(jObj);
    }
	
	private void init() {
		expressionCassettes = new HashMap< String, CasetteParts >();
	}
	
	public GateParts(final JSONObject jobj, CObjectCollection<Part> parts) {
		this.init();
		this.parseGateParts(jobj, parts);
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
	 * Promoter
	 */
	private void setPromoter(final String promoter){
		this.promoter = promoter;
	}
	
	public String getPromoter(){
		return this.promoter;
	}
	
	private String promoter;
	
	/*
	 * CasetteParts
	 */
	/**
	 *  Returns the CasetteParts of Part of the variable defined by parameter, <i>variable</i>
	 *  
	 *  @param variable the variable
	 *  @return the CasetteParts
	 */
	public CasetteParts getCasetteParts(String variable) {
		CasetteParts rtn = null;
		rtn = this.getExpressionCassettes().get(variable);
		return rtn;
	}

	private Map< String, CasetteParts > getExpressionCassettes() {
		return this.expressionCassettes;
	}
	
	Map<String, CasetteParts> expressionCassettes;
	
	/**
	 * Getter for <i>regulatoryParts</i>
	 * @return value of <i>regulatoryParts</i>
	 */
	protected RegulatoryParts getRegulatoryParts() {
		return regulatoryParts;
	}

	/**
	 * Setter for <i>regulatoryParts</i>
	 * @param regulatoryParts the value to set <i>regulatoryParts</i>
	 */
	protected void setRegulatoryParts(RegulatoryParts regulatoryParts) {
		this.regulatoryParts = regulatoryParts;
	}
	private RegulatoryParts regulatoryParts;
}

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
package org.cellocad.cello2.technologyMapping.algorithm.MinCrosstalkYeast.data;

import org.cellocad.cello2.common.CObjectCollection;
import org.cellocad.cello2.common.Utils;
import org.cellocad.cello2.common.target.data.TargetData;
import org.cellocad.cello2.technologyMapping.algorithm.MinCrosstalkYeast.data.ucf.Gate;
import org.cellocad.cello2.technologyMapping.algorithm.MinCrosstalkYeast.data.ucf.GateParts;
import org.cellocad.cello2.technologyMapping.algorithm.MinCrosstalkYeast.data.ucf.Orthogonality;
import org.cellocad.cello2.technologyMapping.algorithm.MinCrosstalkYeast.data.ucf.Part;
import org.cellocad.cello2.technologyMapping.algorithm.MinCrosstalkYeast.data.ucf.ResponseFunction;
import org.json.simple.JSONObject;

/**
 * The MinCrosstalkYeastDataUtils is class with utility methods for the data used in the <i>MinCrosstalkYeast</i> algorithm.
 * 
 * @author Timothy Jones
 * 
 * @date 2018-08-19
 *
 */
public class MinCrosstalkYeastDataUtils {

	static public CObjectCollection<Gate> getGates(final TargetData td){
		CObjectCollection<Gate> rtn = new CObjectCollection<Gate>();
		// gates
		for (int i = 0; i < td.getNumJSONObject(MinCrosstalkYeastDataUtils.S_GATES); i++) {
			JSONObject jObj = td.getJSONObjectAtIdx(MinCrosstalkYeastDataUtils.S_GATES, i);
			Gate gate = new Gate(jObj);
			rtn.add(gate);
		}
		// response function
		for (int i = 0; i < td.getNumJSONObject(MinCrosstalkYeastDataUtils.S_RESPONSEFUNCTION); i++) {
			JSONObject jObj = td.getJSONObjectAtIdx(MinCrosstalkYeastDataUtils.S_RESPONSEFUNCTION, i);
			ResponseFunction rf = new ResponseFunction(jObj);
			// processing
			Gate gate = rtn.findCObjectByName(rf.getName());
			Utils.isNullRuntimeException(gate, "gate");
			rf.setGate(gate);
			gate.setResponseFunction(rf);
		}
		// parts
		CObjectCollection<Part> parts = new CObjectCollection<Part>();
		for (int i = 0; i < td.getNumJSONObject(MinCrosstalkYeastDataUtils.S_PARTS); i++) {
			JSONObject jObj = td.getJSONObjectAtIdx(MinCrosstalkYeastDataUtils.S_PARTS, i);
			Part part = new Part(jObj);
			parts.add(part);
		}
		// gateParts
		for (int i = 0; i < td.getNumJSONObject(MinCrosstalkYeastDataUtils.S_GATEPARTS); i++) {
			JSONObject jObj = td.getJSONObjectAtIdx(MinCrosstalkYeastDataUtils.S_GATEPARTS, i);
			GateParts gatePart = new GateParts(jObj, parts);
			// processing
			Gate gate = rtn.findCObjectByName(gatePart.getName());
			Utils.isNullRuntimeException(gate, "gate");
			gatePart.setGate(gate);
			gate.setGateParts(gatePart);
		}
		// orthogonality
		for (int i = 0; i < td.getNumJSONObject(MinCrosstalkYeastDataUtils.S_GATEORTHOGONALITY); i++) {
			JSONObject jObj = td.getJSONObjectAtIdx(MinCrosstalkYeastDataUtils.S_GATEORTHOGONALITY, i);
			Orthogonality orthogonality = new Orthogonality(jObj);
			// processing
			Gate gate = rtn.findCObjectByName(orthogonality.getGateName());
			Utils.isNullRuntimeException(gate, "gate");
			orthogonality.setGate(gate);
			//gate.getResponseFunction().getVariableByName(orthogonality.getMapVariable()).setOrthogonality(orthogonality);
			gate.setOrthogonality(orthogonality);
		}
		return rtn;
	}
	
	private static String S_GATES = "gates";
	private static String S_RESPONSEFUNCTION = "response_functions";
	private static String S_PARTS = "parts";
	private static String S_GATEPARTS = "gate_parts";
	private static String S_GATEORTHOGONALITY = "gate_orthogonality";
}

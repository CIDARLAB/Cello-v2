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
package org.cellocad.cello2.export.algorithm.SBOL.data;

import org.cellocad.cello2.common.CObjectCollection;
import org.cellocad.cello2.common.Utils;
import org.cellocad.cello2.common.target.data.TargetData;
import org.cellocad.cello2.export.algorithm.SBOL.data.ucf.Cytometry;
import org.cellocad.cello2.export.algorithm.SBOL.data.ucf.CytometryData;
import org.cellocad.cello2.export.algorithm.SBOL.data.ucf.Gate;
import org.cellocad.cello2.export.algorithm.SBOL.data.ucf.GateParts;
import org.cellocad.cello2.export.algorithm.SBOL.data.ucf.Part;
import org.cellocad.cello2.export.algorithm.SBOL.data.ucf.ResponseFunction;
import org.cellocad.cello2.export.algorithm.SBOL.data.ucf.ResponseFunctionVariable;
import org.cellocad.cello2.export.algorithm.SBOL.data.ucf.Toxicity;
import org.json.simple.JSONObject;

/**
 * The SimulatedAnnealingDataUtils is class with utility methods for the data used in the <i>SimulatedAnnealing</i> algorithm.
 * 
 * @author Vincent Mirian
 * 
 * @date 2018-05-21
 *
 */
public class SBOLDataUtils {
	
	static public CObjectCollection<Part> getParts(final TargetData td){
		CObjectCollection<Part> rtn = new CObjectCollection<Part>();
		for (int i = 0; i < td.getNumJSONObject(SBOLDataUtils.S_PARTS); i++) {
			JSONObject jObj = td.getJSONObjectAtIdx(SBOLDataUtils.S_PARTS, i);
			Part part = new Part(jObj);
			rtn.add(part);
		}
		return rtn;
	}

	static public CObjectCollection<Gate> getGates(final TargetData td){
		CObjectCollection<Gate> rtn = new CObjectCollection<Gate>();
		// gates
		for (int i = 0; i < td.getNumJSONObject(SBOLDataUtils.S_GATES); i++) {
			JSONObject jObj = td.getJSONObjectAtIdx(SBOLDataUtils.S_GATES, i);
			Gate gate = new Gate(jObj);
			rtn.add(gate);
		}
		// response function
		for (int i = 0; i < td.getNumJSONObject(SBOLDataUtils.S_RESPONSEFUNCTION); i++) {
			JSONObject jObj = td.getJSONObjectAtIdx(SBOLDataUtils.S_RESPONSEFUNCTION, i);
			ResponseFunction rf = new ResponseFunction(jObj);
			// processing
			Gate gate = rtn.findCObjectByName(rf.getName());
			Utils.isNullRuntimeException(gate, "gate");
			rf.setGate(gate);
			gate.setResponseFunction(rf);
		}
		// parts
		CObjectCollection<Part> parts = getParts(td);
		// gateParts
		for (int i = 0; i < td.getNumJSONObject(SBOLDataUtils.S_GATEPARTS); i++) {
			JSONObject jObj = td.getJSONObjectAtIdx(SBOLDataUtils.S_GATEPARTS, i);
			GateParts gatePart = new GateParts(jObj, parts);
			// processing
			Gate gate = rtn.findCObjectByName(gatePart.getName());
			Utils.isNullRuntimeException(gate, "gate");
			gatePart.setGate(gate);
			gate.setGateParts(gatePart);
			ResponseFunction rf = gate.getResponseFunction();
			for (int j = 0; j < rf.getNumVariable(); j++) {
				ResponseFunctionVariable rfVar = rf.getVariableAtIdx(j);
				rfVar.setCasetteParts(gatePart.getCasetteParts(rfVar.getName()));
				
			}
		}
		// toxicity
		for (int i = 0; i < td.getNumJSONObject(SBOLDataUtils.S_GATETOXICITY); i++) {
			JSONObject jObj = td.getJSONObjectAtIdx(SBOLDataUtils.S_GATETOXICITY, i);
			Toxicity toxicity = new Toxicity(jObj);
			// processing
			Gate gate = rtn.findCObjectByName(toxicity.getGateName());
			Utils.isNullRuntimeException(gate, "gate");
			toxicity.setGate(gate);
			ResponseFunction rf = gate.getResponseFunction();
			if (rf != null) {
				gate.getResponseFunction().getVariableByName(toxicity.getMapVariable()).setToxicity(toxicity);
			}
		}
		// gate cytometry
		for (int i = 0; i < td.getNumJSONObject(SBOLDataUtils.S_GATECYTOMETRY); i++) {
			JSONObject jObj = td.getJSONObjectAtIdx(SBOLDataUtils.S_GATECYTOMETRY, i);
			Cytometry cytometry = new Cytometry(jObj);
			// processing
			Gate gate = rtn.findCObjectByName(cytometry.getGateName());
			Utils.isNullRuntimeException(gate, "gate");
			ResponseFunction rf = gate.getResponseFunction();
			for (int j = 0; j < cytometry.getNumCytometryData(); j++) {
				CytometryData cd = cytometry.getCytometryDataAtIdx(j);
				rf.getVariableByName(cd.getMapVariable()).addCytometryData(cd);
			}
		}
		return rtn;
	}
	
	private static String S_GATES = "gates";
	private static String S_RESPONSEFUNCTION = "response_functions";
	private static String S_PARTS = "parts";
	private static String S_GATEPARTS = "gate_parts";
	private static String S_GATETOXICITY = "gate_toxicity";
	private static String S_GATECYTOMETRY = "gate_cytometry";
}

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
package org.cellocad.v2.placing.algorithm.Eugene.data;

import org.cellocad.v2.common.CObjectCollection;
import org.cellocad.v2.common.Utils;
import org.cellocad.v2.common.target.data.TargetData;
import org.cellocad.v2.placing.algorithm.Eugene.data.ucf.CircuitRules;
import org.cellocad.v2.placing.algorithm.Eugene.data.ucf.Cytometry;
import org.cellocad.v2.placing.algorithm.Eugene.data.ucf.CytometryData;
import org.cellocad.v2.placing.algorithm.Eugene.data.ucf.DeviceRules;
import org.cellocad.v2.placing.algorithm.Eugene.data.ucf.Gate;
import org.cellocad.v2.placing.algorithm.Eugene.data.ucf.GateStructure;
import org.cellocad.v2.placing.algorithm.Eugene.data.ucf.GeneticLocation;
import org.cellocad.v2.placing.algorithm.Eugene.data.ucf.InputSensor;
import org.cellocad.v2.placing.algorithm.Eugene.data.ucf.OutputDevice;
import org.cellocad.v2.placing.algorithm.Eugene.data.ucf.OutputDeviceStructure;
import org.cellocad.v2.placing.algorithm.Eugene.data.ucf.Part;
import org.cellocad.v2.placing.algorithm.Eugene.data.ucf.ResponseFunction;
import org.cellocad.v2.placing.algorithm.Eugene.data.ucf.ResponseFunctionVariable;
import org.cellocad.v2.placing.algorithm.Eugene.data.ucf.Toxicity;
import org.json.simple.JSONObject;

/**
 * The SimulatedAnnealingDataUtils is class with utility methods for the data used in the <i>SimulatedAnnealing</i> algorithm.
 * 
 * @author Timothy Jones
 * 
 * @date 2018-05-21
 *
 */
public class EugeneDataUtils {
	
	static public CObjectCollection<Part> getParts(final TargetData td){
		CObjectCollection<Part> rtn = new CObjectCollection<Part>();
		for (int i = 0; i < td.getNumJSONObject(EugeneDataUtils.S_PARTS); i++) {
			JSONObject jObj = td.getJSONObjectAtIdx(EugeneDataUtils.S_PARTS, i);
			Part part = new Part(jObj);
			rtn.add(part);
		}
		return rtn;
	}

	static public CObjectCollection<Gate> getGates(final TargetData td){
		CObjectCollection<Gate> rtn = new CObjectCollection<Gate>();
		// gates
		for (int i = 0; i < td.getNumJSONObject(EugeneDataUtils.S_GATES); i++) {
			JSONObject jObj = td.getJSONObjectAtIdx(EugeneDataUtils.S_GATES, i);
			Gate gate = new Gate(jObj);
			rtn.add(gate);
		}
		// response function
		for (int i = 0; i < td.getNumJSONObject(EugeneDataUtils.S_RESPONSEFUNCTION); i++) {
			JSONObject jObj = td.getJSONObjectAtIdx(EugeneDataUtils.S_RESPONSEFUNCTION, i);
			ResponseFunction rf = new ResponseFunction(jObj);
			// processing
			Gate gate = rtn.findCObjectByName(rf.getName());
			Utils.isNullRuntimeException(gate, "gate");
			rf.setGate(gate);
			gate.setResponseFunction(rf);
		}
		// parts
		// CObjectCollection<Part> parts = getParts(td);
//		// gateParts
//		for (int i = 0; i < td.getNumJSONObject(EugeneDataUtils.S_GATEPARTS); i++) {
//			JSONObject jObj = td.getJSONObjectAtIdx(EugeneDataUtils.S_GATEPARTS, i);
//			GateParts gatePart = new GateParts(jObj, parts);
//			// processing
//			Gate gate = rtn.findCObjectByName(gatePart.getName());
//			Utils.isNullRuntimeException(gate, "gate");
//			gatePart.setGate(gate);
//			gate.setGateParts(gatePart);
//			ResponseFunction rf = gate.getResponseFunction();
//			for (int j = 0; j < rf.getNumVariable(); j++) {
//				ResponseFunctionVariable rfVar = rf.getVariableAtIdx(j);
//				rfVar.setCasetteParts(gatePart.getCasetteParts(rfVar.getName()));
//				
//			}
//		}
		// gate_structure
		for (int i = 0; i < td.getNumJSONObject(EugeneDataUtils.S_GATESTRUCTURE); i++) {
			JSONObject jObj = td.getJSONObjectAtIdx(EugeneDataUtils.S_GATESTRUCTURE, i);
			GateStructure structure = new GateStructure(jObj);
			Gate gate = rtn.findCObjectByName(structure.getGateName());
			Utils.isNullRuntimeException(gate, "gate");
			structure.setGate(gate);
			gate.setGateStructure(structure);
			ResponseFunction rf = gate.getResponseFunction();
			for (int j = 0; j < rf.getNumVariable(); j++) {
				ResponseFunctionVariable rfVar = rf.getVariableAtIdx(j);
				rfVar.setEugeneDevices(structure.getDevices(rfVar.getName()));
			}
		}
		// toxicity
		for (int i = 0; i < td.getNumJSONObject(EugeneDataUtils.S_GATETOXICITY); i++) {
			JSONObject jObj = td.getJSONObjectAtIdx(EugeneDataUtils.S_GATETOXICITY, i);
			Toxicity toxicity = new Toxicity(jObj);
			// processing
			Gate gate = rtn.findCObjectByName(toxicity.getGateName());
			Utils.isNullRuntimeException(gate, "gate");
			toxicity.setGate(gate);
			gate.getResponseFunction().getVariableByName(toxicity.getMapVariable()).setToxicity(toxicity);
		}
		// gate cytometry
		for (int i = 0; i < td.getNumJSONObject(EugeneDataUtils.S_GATECYTOMETRY); i++) {
			JSONObject jObj = td.getJSONObjectAtIdx(EugeneDataUtils.S_GATECYTOMETRY, i);
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
	
	static public CObjectCollection<InputSensor> getInputSensors(final TargetData td) {
		CObjectCollection<InputSensor> rtn = new CObjectCollection<InputSensor>();
		for (int i = 0; i < td.getNumJSONObject(EugeneDataUtils.S_INPUTSENSORS); i++) {
			JSONObject jObj = td.getJSONObjectAtIdx(EugeneDataUtils.S_INPUTSENSORS, i);
			InputSensor sensor = new InputSensor(jObj,getParts(td));
			rtn.add(sensor);
		}
		return rtn;
	}
	
	static public CObjectCollection<OutputDevice> getOutputReporters(final TargetData td) {
		CObjectCollection<OutputDevice> rtn = new CObjectCollection<OutputDevice>();
		for (int i = 0; i < td.getNumJSONObject(EugeneDataUtils.S_OUTPUTDEVICES); i++) {
			JSONObject jObj = td.getJSONObjectAtIdx(EugeneDataUtils.S_OUTPUTDEVICES, i);
			OutputDevice device = new OutputDevice(jObj);
			rtn.add(device);
		}
		for (int i = 0; i < td.getNumJSONObject(EugeneDataUtils.S_OUTPUTSTRUCTURE); i++) {
			JSONObject jObj = td.getJSONObjectAtIdx(EugeneDataUtils.S_OUTPUTSTRUCTURE, i);
			OutputDeviceStructure structure = new OutputDeviceStructure(jObj);
			OutputDevice device = rtn.findCObjectByName(structure.getGateName());
			device.setOutputDeviceStructure(structure);
			structure.setOutputDevice(device);
		}
		return rtn;
	}
	
	static public CircuitRules getCircuitRules(final TargetData td){
		CircuitRules rtn = null;
		JSONObject jObj = td.getJSONObjectAtIdx(EugeneDataUtils.S_CIRCUITRULES, 0);
		rtn = new CircuitRules(jObj);
		return rtn;
	}
	
	/**
	 * @param targetData
	 * @return
	 */
	public static DeviceRules getDeviceRules(TargetData td) {
		DeviceRules rtn = null;
		JSONObject jObj = td.getJSONObjectAtIdx(EugeneDataUtils.S_DEVICERULES, 0);
		rtn = new DeviceRules(jObj);
		return rtn;
	}
	
	static public CObjectCollection<GeneticLocation> getGeneticLocations(final TargetData td) {
		CObjectCollection<GeneticLocation> rtn = new CObjectCollection<>();
		for (int i = 0; i < td.getNumJSONObject(S_GENETICLOCATIONS); i++) {
			JSONObject jObj = td.getJSONObjectAtIdx(S_GENETICLOCATIONS, i);
			GeneticLocation l = new GeneticLocation(jObj);
			rtn.add(l);
		}
		return rtn;
	}

	private static String S_GATES = "gates";
	private static String S_RESPONSEFUNCTION = "response_functions";
	private static String S_PARTS = "parts";
	private static String S_GATESTRUCTURE = "gate_structure";
	private static String S_GATETOXICITY = "gate_toxicity";
	private static String S_GATECYTOMETRY = "gate_cytometry";
	private static String S_INPUTSENSORS = "input_sensors";
	private static String S_OUTPUTDEVICES = "output_devices";
	private static String S_OUTPUTSTRUCTURE = "output_structure";
	private static String S_CIRCUITRULES = "circuit_rules";
	private static String S_DEVICERULES = "device_rules";
	private static String S_GENETICLOCATIONS = "genetic_locations";

}

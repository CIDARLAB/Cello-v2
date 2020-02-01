/**
 * Copyright (C) 2019 Boston University (BU)
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
package org.cellocad.cello2.placing.algorithm.Eugene;

import org.cellocad.cello2.common.CObjectCollection;
import org.cellocad.cello2.placing.algorithm.Eugene.data.Component;
import org.cellocad.cello2.placing.algorithm.Eugene.data.Device;
import org.cellocad.cello2.placing.algorithm.Eugene.data.Devices;
import org.cellocad.cello2.placing.algorithm.Eugene.data.ucf.CasetteParts;
import org.cellocad.cello2.placing.algorithm.Eugene.data.ucf.Gate;
import org.cellocad.cello2.placing.algorithm.Eugene.data.ucf.GateParts;
import org.cellocad.cello2.placing.algorithm.Eugene.data.ucf.InputSensor;
import org.cellocad.cello2.placing.algorithm.Eugene.data.ucf.OutputReporter;
import org.cellocad.cello2.placing.algorithm.Eugene.data.ucf.Part;
import org.cellocad.cello2.placing.algorithm.Eugene.data.ucf.ResponseFunction;
import org.cellocad.cello2.results.logicSynthesis.LSResultsUtils;
import org.cellocad.cello2.results.netlist.NetlistEdge;
import org.cellocad.cello2.results.netlist.NetlistNode;

/**
 * 
 *
 * @author Timothy Jones
 *
 * @date 2019-05-29
 *
 */
public class EugeneUtils {
	
	private static String getGateDeviceName(NetlistNode node,  final CObjectCollection<Gate> gates) {
		String rtn = "";
		rtn += getGateBaseName(node, gates);
		rtn += "_device";
		return rtn;
	}
	
	private static String getGateBaseName(final NetlistNode node, final CObjectCollection<Gate> gates) {
		String rtn = null;
		String gateType = node.getResultNetlistNodeData().getGateType();
		if (LSResultsUtils.isPrimaryInput(node)) {
			rtn = gateType;
		}
		else if (LSResultsUtils.isPrimaryOutput(node)) {
			rtn = gateType;
		}
		else if (!LSResultsUtils.isPrimaryInput(node) && !LSResultsUtils.isPrimaryOutput(node)) {
			Gate gate = gates.findCObjectByName(gateType);
			if (gate == null) {
				throw new RuntimeException("Unknown gate.");
			}
			rtn = gate.getRegulator();
		}
		else {
			throw new RuntimeException("Unknown gateType.");
		}
		return rtn;
	}

	private static CObjectCollection<Part> getInputPromoters(
			final NetlistNode node,
			final CObjectCollection<InputSensor> sensors,
			final CObjectCollection<Gate> gates,
			final CObjectCollection<Part> parts
			) {
		CObjectCollection<Part> rtn = new CObjectCollection<>();
		for (int i = 0; i < node.getNumInEdge(); i++) {
			NetlistEdge e = node.getInEdgeAtIdx(i);
			NetlistNode src = e.getSrc();
			String promoter = "";
			String gateType = src.getResultNetlistNodeData().getGateType();
			if (LSResultsUtils.isAllInput(src)) {
				InputSensor sensor = sensors.findCObjectByName(gateType);
				promoter = sensor.getPromoter();
			} else {
				Gate gate = gates.findCObjectByName(gateType);
				if (gate == null) {
					new RuntimeException("Unknown gate.");
				}
				promoter = gate.getGateParts().getPromoter();
			}
			Part part = parts.findCObjectByName(promoter);
			rtn.add(part);
		}
		return rtn;
	}

	private static CObjectCollection<Part> getOutputReporterParts(
			final NetlistNode node,
			final CObjectCollection<OutputReporter> reporters
			) {
		CObjectCollection<Part> rtn = new CObjectCollection<>();
		String gateType = node.getResultNetlistNodeData().getGateType();
		OutputReporter reporter = reporters.findCObjectByName(gateType);
		if (reporter == null) {
			new RuntimeException("Unknown output reporter.");
		}
		for (int i = 0; i < reporter.getNumParts(); i++) {
			Part part = reporter.getPartAtIdx(i);
			if (part == null) {
				throw new RuntimeException("Unknown part.");
			}
			rtn.add(part);
		}
		return rtn;
	}

	private static CObjectCollection<Part> getCasetteParts(
			final NetlistNode node,
			final CObjectCollection<Gate> gates
			) {
		CObjectCollection<Part> rtn = new CObjectCollection<>();
		String gateType = node.getResultNetlistNodeData().getGateType();
		Gate gate = gates.findCObjectByName(gateType);
		if (gate == null) {
			throw new RuntimeException("Unknown gate.");
		}
		GateParts gateParts = gate.getGateParts();
		ResponseFunction rf = gate.getResponseFunction();
		for (int i = 0; i < rf.getNumVariable(); i++) {
			String variable = rf.getVariableAtIdx(i).getName();
			CasetteParts casetteParts = gateParts.getCasetteParts(variable);
			for (int j = 0; j < casetteParts.getNumParts(); j++) {
				Part part = casetteParts.getPartAtIdx(j);
				if (part == null) {
					throw new RuntimeException("Unknown part.");
				}
				rtn.add(part);
			}
		}
		return rtn;
	}

	static Devices getDevices(
			final NetlistNode node,
			final CObjectCollection<InputSensor> sensors,
			final CObjectCollection<OutputReporter> reporters,
			final CObjectCollection<Gate> gates,
			final CObjectCollection<Part> parts,
			final Boolean bSplit
			) {
		Devices rtn = null;
		CObjectCollection<Device> devices = new CObjectCollection<>();
		String name = getGateDeviceName(node,gates);
		if (LSResultsUtils.isPrimaryInput(node)) {
			CObjectCollection<Component> components = new CObjectCollection<>();
			Device device = new Device(components);
			// cassette parts
			CObjectCollection<Component> componentParts = new CObjectCollection<>();
			Device sensor = new Device(componentParts);
			sensor.setName(node.getResultNetlistNodeData().getGateType());
			components.add(sensor);
			device.setName(name);
			devices.add(device);
		}
		else if (LSResultsUtils.isPrimaryOutput(node)) {
			CObjectCollection<Component> components = new CObjectCollection<>();
			Device device = new Device(components);
			// input promoters
			components.addAll(getInputPromoters(node,sensors,gates,parts));
			// cassette parts
			CObjectCollection<Component> componentParts = new CObjectCollection<>();
			componentParts.addAll(getOutputReporterParts(node,reporters));
			Device reporter = new Device(componentParts);
			reporter.setName(node.getResultNetlistNodeData().getGateType());
			components.add(reporter);
			device.setName(name);
			devices.add(device);
		}
		else if (!LSResultsUtils.isAllInput(node) && !LSResultsUtils.isAllOutput(node)) {
			CObjectCollection<Part> inputPromoters = getInputPromoters(node,sensors,gates,parts);
			CObjectCollection<Part> cassetteParts = getCasetteParts(node,gates);
			if (bSplit) {
				int i = 0;
				for (Part promoter : inputPromoters) {
					CObjectCollection<Component> components = new CObjectCollection<>();
					Device device = new Device(components);
					// input promoters
					components.add(promoter);
					// cassette parts
					CObjectCollection<Component> componentParts = new CObjectCollection<>();
					componentParts.addAll(cassetteParts);
					Device gate = new Device(componentParts);
					gate.setName(node.getResultNetlistNodeData().getGateType());
					components.add(gate);
					String split = "__SPLIT_" + String.valueOf(i);
					if (inputPromoters.size() > 1)
						device.setName(name + split);
					else
						device.setName(name);
					devices.add(device);
					i++;
				}
			} else {
				CObjectCollection<Component> components = new CObjectCollection<>();
				Device device = new Device(components);
				// input promoters
				components.addAll(inputPromoters);
				// cassette parts
				CObjectCollection<Component> componentParts = new CObjectCollection<>();
				componentParts.addAll(cassetteParts);
				Device gate = new Device(componentParts);
				gate.setName(node.getResultNetlistNodeData().getGateType());
				components.add(gate);
				device.setName(name);
				devices.add(device);
			}

		}
		rtn = new Devices(devices);
		return rtn;
	}

}

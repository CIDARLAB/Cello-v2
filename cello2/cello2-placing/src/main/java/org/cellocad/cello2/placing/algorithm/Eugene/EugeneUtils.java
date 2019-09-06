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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.cellocad.cello2.common.CObjectCollection;
import org.cellocad.cello2.placing.algorithm.Eugene.data.structure.EugeneDevice;
import org.cellocad.cello2.placing.algorithm.Eugene.data.structure.EugeneObject;
import org.cellocad.cello2.placing.algorithm.Eugene.data.structure.EugenePart;
import org.cellocad.cello2.placing.algorithm.Eugene.data.structure.EugeneTemplate;
import org.cellocad.cello2.placing.algorithm.Eugene.data.ucf.Gate;
import org.cellocad.cello2.placing.algorithm.Eugene.data.ucf.InputSensor;
import org.cellocad.cello2.placing.algorithm.Eugene.data.ucf.OutputDevice;
import org.cellocad.cello2.placing.algorithm.Eugene.data.ucf.Part;
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

	public static String getDeviceDeviceName(String name) {
		String rtn = null;
		rtn = name + "Device";
		return rtn;
	}

	public static String getDeviceBaseName(String name) {
		String rtn = null;
		rtn = name.replaceAll("Device$", "");
		return rtn;
	}

	public static Part getOutputPart(final EugeneDevice device, final CObjectCollection<Part> parts) {
		Part rtn = null;
		EugenePart p = device.getOutput();
		if (p != null) {
			Part part = parts.findCObjectByName(p.getName());
			rtn = part;
		}
		return rtn;
	}

	public static String getPartTypeDefinition(final String type) {
		String rtn = "";
		rtn = String.format("PartType %s;", type);
		return rtn;
	}

	public static Set<String> getPartTypes(final EugeneDevice device, final CObjectCollection<Part> parts) {
		Set<String> rtn = new HashSet<String>();
		for (EugeneObject o : device.getComponents()) {
			if (o instanceof EugenePart) {
				Part p = parts.findCObjectByName(o.getName());
				if (p != null)
					rtn.add(p.getPartType());
			}
			if (o instanceof EugeneTemplate) {
				rtn.add(o.getName());
			}
			if (o instanceof EugeneDevice) {
				EugeneDevice d = (EugeneDevice) o;
				rtn.addAll(getPartTypes(d, parts));
			}
		}
		Part p = getOutputPart(device, parts);
		if (p != null)
			rtn.add(p.getPartType());
		return rtn;
	}

	public static String getPartDefinition(final Part part) {
		String rtn = "";
		String type = part.getPartType();
		String name = part.getName();
		String seq = part.getDNASequence();
		rtn = String.format("%s %s(.SEQUENCE(\"%s\"));", type, name, seq);
		return rtn;
	}

	public static Set<String> getPartDefinitions(final EugeneDevice device, final CObjectCollection<Part> parts) {
		Set<String> rtn = new HashSet<String>();
		for (EugeneObject o : device.getComponents()) {
			if (o instanceof EugenePart) {
				Part p = parts.findCObjectByName(o.getName());
				if (p != null) {
					rtn.add(getPartDefinition(p));
				}
			}
			if (o instanceof EugeneDevice) {
				EugeneDevice d = (EugeneDevice) o;
				rtn.addAll(getPartDefinitions(d, parts));
			}
		}
		return rtn;
	}

	public static CObjectCollection<Part> getInputs(final NetlistNode node,
			final CObjectCollection<InputSensor> sensors, final CObjectCollection<Gate> gates,
			final CObjectCollection<Part> parts) {
		CObjectCollection<Part> rtn = new CObjectCollection<>();
		Collection<String> partNames = node.getResultNetlistNodeData().getParts();
		if (partNames.size() > 0) {
			for (String p : partNames) {
				Part part = parts.findCObjectByName(p);
				rtn.add(part);
			}
		}
		for (int i = 0; i < node.getNumInEdge(); i++) {
			NetlistEdge e = node.getInEdgeAtIdx(i);
			NetlistNode src = e.getSrc();
			String input = "";
			String gateType = src.getResultNetlistNodeData().getGateType();
			if (LSResultsUtils.isAllInput(src)) {
				InputSensor sensor = sensors.findCObjectByName(gateType);
				input = sensor.getPromoter();
			} else {
				Gate gate = gates.findCObjectByName(gateType);
				if (gate == null) {
					new RuntimeException("Unknown gate.");
				}
				input = gate.getGateStructure().getOutput();
			}
			Part part = parts.findCObjectByName(input);
			rtn.add(part);
		}
		return rtn;
	}

	/**
	 * Obtain the EugeneDevice objects associated with a given node. If more device
	 * objects are specified in a gate than there are inputs to the node, the extra
	 * devices will be discarded.
	 * 
	 * @param node      the NetlistNode
	 * @param gates     the Gate objects
	 * @param sensors   the InputSensor objects
	 * @param reporters the OutputReporter objects
	 * @return a collection of EugeneDevice objects associated with the NetlistNode
	 */
	static Collection<EugeneDevice> getDevices(final NetlistNode node, final CObjectCollection<Gate> gates,
			final CObjectCollection<OutputDevice> reporters) {
		Collection<EugeneDevice> rtn = new ArrayList<>();
		String gateType = node.getResultNetlistNodeData().getGateType();
		Integer num = node.getNumInEdge();
		Gate gate = gates.findCObjectByName(gateType);
		OutputDevice reporter = reporters.findCObjectByName(gateType);
		if (reporter != null) {
			Collection<EugeneDevice> devices = reporter.getOutputDeviceStructure().getDevices();
			Integer i = 0;
			for (EugeneDevice d : devices) {
				EugeneDevice e = new EugeneDevice(d);
				Collection<EugeneTemplate> inputs = new ArrayList<>();
				for (EugeneObject o : e.getComponents()) {
					if (o instanceof EugeneTemplate) {
						i++;
						EugeneTemplate t = (EugeneTemplate) o;
						if (i <= num) {
							continue;
						}
						inputs.add(t);
					}
				}
				for (EugeneTemplate t : inputs) {
					e.getComponents().remove(t);
				}
				for (EugeneObject o : e.getComponents()) {
					if (o instanceof EugeneTemplate) {
						rtn.add(e);
						break;
					}
				}
			}
		}
		if (gate != null) {
			Collection<EugeneDevice> devices = gate.getGateStructure().getDevices();
			Integer i = 0;
			for (EugeneDevice d : devices) {
				EugeneDevice e = new EugeneDevice(d);
				Collection<EugeneTemplate> inputs = new ArrayList<>();
				for (EugeneObject o : e.getComponents()) {
					if (o instanceof EugeneTemplate) {
						i++;
						EugeneTemplate t = (EugeneTemplate) o;
						if (i <= num) {
							continue;
						}
						inputs.add(t);
					}
				}
				for (EugeneTemplate t : inputs) {
					e.getComponents().remove(t);
				}
				for (EugeneObject o : e.getComponents()) {
					if (o instanceof EugeneTemplate) {
						rtn.add(e);
						break;
					}
				}
			}
		}
		return rtn;
	}

}

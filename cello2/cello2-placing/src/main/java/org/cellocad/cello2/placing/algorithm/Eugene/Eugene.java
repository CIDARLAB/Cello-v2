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
package org.cellocad.cello2.placing.algorithm.Eugene;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cellocad.cello2.common.CObjectCollection;
import org.cellocad.cello2.common.Utils;
import org.cellocad.cello2.common.graph.algorithm.SinkBFS;
import org.cellocad.cello2.placing.algorithm.PLAlgorithm;
import org.cellocad.cello2.placing.algorithm.Eugene.data.Component;
import org.cellocad.cello2.placing.algorithm.Eugene.data.Device;
import org.cellocad.cello2.placing.algorithm.Eugene.data.Devices;
import org.cellocad.cello2.placing.algorithm.Eugene.data.EugeneDataUtils;
import org.cellocad.cello2.placing.algorithm.Eugene.data.EugeneNetlistData;
import org.cellocad.cello2.placing.algorithm.Eugene.data.EugeneNetlistEdgeData;
import org.cellocad.cello2.placing.algorithm.Eugene.data.EugeneNetlistNodeData;
import org.cellocad.cello2.placing.algorithm.Eugene.data.ucf.Gate;
import org.cellocad.cello2.placing.algorithm.Eugene.data.ucf.InputSensor;
import org.cellocad.cello2.placing.algorithm.Eugene.data.ucf.OutputReporter;
import org.cellocad.cello2.placing.algorithm.Eugene.data.ucf.Part;
import org.cellocad.cello2.placing.algorithm.Eugene.data.ucf.Rules;
import org.cellocad.cello2.placing.runtime.environment.PLArgString;
import org.cellocad.cello2.results.logicSynthesis.LSResultsUtils;
import org.cellocad.cello2.results.logicSynthesis.netlist.LSResultNetlistUtils;
import org.cellocad.cello2.results.netlist.Netlist;
import org.cellocad.cello2.results.netlist.NetlistEdge;
import org.cellocad.cello2.results.netlist.NetlistNode;
import org.cellocad.cello2.results.placing.placement.Placement;
import org.cellocad.cello2.results.placing.placement.PlacementGroup;
import org.cellocad.cello2.results.placing.placement.Placements;
import org.cidarlab.eugene.dom.NamedElement;
import org.cidarlab.eugene.dom.imp.container.EugeneArray;
import org.cidarlab.eugene.dom.imp.container.EugeneCollection;
import org.cidarlab.eugene.exception.EugeneException;
import org.cidarlab.eugene.util.DeviceUtils;

/**
 * The Eugene class implements the <i>Eugene</i> algorithm in the <i>placing</i> stage.
 *
 * @author Timothy Jones
 *
 * @date 2018-06-06
 *
 */
public class Eugene extends PLAlgorithm{

	/**
	 *  Returns the <i>EugeneNetlistNodeData</i> of the <i>node</i>
	 *
	 *  @param node a node within the <i>netlist</i> of this instance
	 *  @return the <i>EugeneNetlistNodeData</i> instance if it exists, null otherwise
	 */
	protected EugeneNetlistNodeData getEugeneNetlistNodeData(NetlistNode node){
		EugeneNetlistNodeData rtn = null;
		rtn = (EugeneNetlistNodeData) node.getNetlistNodeData();
		return rtn;
	}

	/**
	 *  Returns the <i>EugeneNetlistEdgeData</i> of the <i>edge</i>
	 *
	 *  @param edge an edge within the <i>netlist</i> of this instance
	 *  @return the <i>EugeneNetlistEdgeData</i> instance if it exists, null otherwise
	 */
	protected EugeneNetlistEdgeData getEugeneNetlistEdgeData(NetlistEdge edge){
		EugeneNetlistEdgeData rtn = null;
		rtn = (EugeneNetlistEdgeData) edge.getNetlistEdgeData();
		return rtn;
	}

	/**
	 *  Returns the <i>EugeneNetlistData</i> of the <i>netlist</i>
	 *
	 *  @param netlist the netlist of this instance
	 *  @return the <i>EugeneNetlistData</i> instance if it exists, null otherwise
	 */
	protected EugeneNetlistData getEugeneNetlistData(Netlist netlist){
		EugeneNetlistData rtn = null;
		rtn = (EugeneNetlistData) netlist.getNetlistData();
		return rtn;
	}

	/**
	 *  Gets the Constraint data from the NetlistConstraintFile
	 */
	@Override
	protected void getConstraintFromNetlistConstraintFile() {

	}

	/**
	 *  Gets the data from the UCF
	 */
	@Override
	protected void getDataFromUCF() {
		this.setGates(EugeneDataUtils.getGates(this.getTargetData()));
		this.setParts(EugeneDataUtils.getParts(this.getTargetData()));
		this.setInputSensors(EugeneDataUtils.getInputSensors(this.getTargetData()));
		this.setOutputReporters(EugeneDataUtils.getOutputReporters(this.getTargetData()));
		this.setRules(EugeneDataUtils.getRules(this.getTargetData()));
	}

	/**
	 *  Set parameter(s) value(s) of the algorithm
	 */
	@Override
	protected void setParameterValues() {
		String outputDir = this.getRuntimeEnv().getOptionValue(PLArgString.OUTPUTDIR);
		String inputFilename = this.getNetlist().getInputFilename();
		String filename = outputDir + Utils.getFileSeparator() + Utils.getFilename(inputFilename) + "_eugeneScript.eug";
		this.setEugeneScriptFilename(filename);

		Boolean present = false;

		present = this.getAlgorithmProfile().getIntParameter("MaxPlacements").getFirst();
		if (present) {
			this.setMaxPlacements(this.getAlgorithmProfile().getIntParameter("MaxPlacements").getSecond());
		}
		present = this.getAlgorithmProfile().getBooleanParameter("SplitTandemPromoters").getFirst();
		if (present) {
			this.setSplitTandemPromoters(this.getAlgorithmProfile().getBooleanParameter("SplitTandemPromoters").getSecond());
		}
		present = this.getAlgorithmProfile().getStringParameter("OutputReporters").getFirst();
		if (present) {
			this.setOutputReporterOption(this.getAlgorithmProfile().getStringParameter("OutputReporters").getSecond());
		}
		present = this.getAlgorithmProfile().getStringParameter("InputSensors").getFirst();
		if (present) {
			this.setInputSensorOption(this.getAlgorithmProfile().getStringParameter("InputSensors").getSecond());
		}
	}

	/**
	 *  Validate parameter value of the algorithm
	 */
	@Override
	protected void validateParameterValues() {
		if (this.getMaxPlacements() == null || this.getMaxPlacements() <= 0)
			this.setMaxPlacements(5);
		if (this.getSplitTandemPromoters() == null)
			this.setSplitTandemPromoters(false);
	}

	private void setGroups() {
		Devices input = new Devices();
		Devices circuit = new Devices();
		Devices output = new Devices();
		for (NetlistNode node : this.getDevicesMap().keySet()) {
			Devices devices = this.getDevicesMap().get(node);
			if (LSResultsUtils.isPrimaryInput(node)) {
				if (this.getInputSensorOption().equals(S_SEPARATE)) {
					input.addAll(devices);
				}
				else if (this.getInputSensorOption().equals(S_WITH_CIRCUIT)) {
					circuit.addAll(devices);
				}
			}
			else if (LSResultsUtils.isPrimaryOutput(node)) {
				if (this.getOutputReporterOption().equals(S_SEPARATE)) {
					output.addAll(devices);
				}
				else if (this.getOutputReporterOption().equals(S_WITH_CIRCUIT)) {
					circuit.addAll(devices);
				}
			}
			else {
				circuit.addAll(devices);
			}
		}
		if (input.getNumDevice() > 0) {
			this.getGroups().add(input);
		}
		if (circuit.getNumDevice() > 0) {
			this.getGroups().add(circuit);
		}
		if (output.getNumDevice() > 0) {
			this.getGroups().add(output);
		}
	}

	private void setDevices() {
		SinkBFS<NetlistNode, NetlistEdge, Netlist> BFS = new SinkBFS<NetlistNode, NetlistEdge, Netlist>(this.getNetlist());
		NetlistNode node = null;
		node = BFS.getNextVertex();
		while (node != null) {
			Devices devices = EugeneUtils.getDevices(
					node, 
					this.getInputSensors(), 
					this.getOutputReporters(),
					this.getGates(),
					this.getParts(),
					this.getSplitTandemPromoters());
			this.getDevicesMap().put(node, devices);
			node = BFS.getNextVertex();
		}
	}

	private Collection<String> getPartTypes() {
		Set<String> rtn = new HashSet<String>();
		for (Devices devices : this.getDevicesMap().values()) {
			for (int i = 0; i < devices.getNumDevice(); i++) {
				Device device = devices.getDeviceAtIdx(i);
				for (int j = 0; j < device.getNumComponent(); j++) {
					Component component = device.getComponentAtIdx(j);
					String str = "";
					if (component instanceof Part) {
						str = ((Part)component).getPartType();
					}
					else if (component instanceof Device) {
						str = "cassette";
					}
					String type = String.format("PartType %s;", str);
					rtn.add(type);
				}
			}
		}
		return rtn;
	}

	private Collection<String> getPartSequences() {
		Set<String> rtn = new HashSet<String>();
		for (Devices devices : this.getDevicesMap().values()) {
			for (int i = 0; i < devices.getNumDevice(); i++) {
				Device device = devices.getDeviceAtIdx(i);
				for (int j = 0; j < device.getNumComponent(); j++) {
					Component component = device.getComponentAtIdx(j);
					if (component instanceof Part) {
						Part p = (Part)component;
						String seq = String.format("%s %s(.SEQUENCE(\"%s\"));", p.getPartType(), p.getName(), p.getDNASequence());
						rtn.add(seq);
					}
					else if (component instanceof Device) {
						String str = "";
						Device d = (Device)component;
						for (int k = 0; k < d.getNumComponent(); k++) {
							Part p = (Part)(d.getComponentAtIdx(k));
							str += p.getDNASequence();
						}
						String seq = String.format("%s %s(.SEQUENCE(\"%s\"));", "cassette", component.getName(), str);
						rtn.add(seq);
					}
				}
			}
		}
		return rtn;
	}

	private Collection<String> getDeviceDefinitions() {
		Collection<String> rtn = new ArrayList<String>();
		for (Devices devices : this.getDevicesMap().values()) {
			for (int i = 0; i < devices.getNumDevice(); i++) {
				Device device = devices.getDeviceAtIdx(i);
				String str = "";
				str += String.format("Device %s(",device.getName());
				str += Utils.getNewLine();
				for (int j = 0; j < device.getNumComponent(); j++) {
					Component component = device.getComponentAtIdx(j);
					String entry = "";
					if (component instanceof Part && ((Part)component).getPartType().equalsIgnoreCase(Eugene.S_PROMOTER)) {
						entry = ((Part)component).getPartType();
					} else {
						entry = component.getName();
					}
					str += Utils.getTabCharacter();
					str += String.format("%s,",entry);
					str += Utils.getNewLine();
				}
				str = str.replaceAll("," + Utils.getNewLine() + "$", Utils.getNewLine());
				str += ");";
				rtn.add(str);
			}
		}
		return rtn;
	}

	private Collection<String> getNamesFromRule(String rule) {
		Collection<String> rtn = new HashSet<>();
		Collection<String> keywords = Arrays.asList(Rules.ValidRuleKeywords);
		StringTokenizer st = new StringTokenizer(rule, " \t\n\r\f,");
		while (st.hasMoreTokens()) {
			String token = st.nextToken();
			if (!keywords.contains(token) && token.substring(0, 1).matches("[a-z,A-Z]")) {
				rtn.add(token);
			}
		}
		return rtn;
	}

	private Collection<String> getDeviceRules() {
		Collection<String> rtn = new ArrayList<>();
		Collection<String> rules = this.getRules().getPartRules();
		for (Devices devices : this.getDevicesMap().values()) {
			for (int i = 0; i < devices.getNumDevice(); i++) {
				Device device = devices.getDeviceAtIdx(i);
				// rule
				String str = "";
				str += String.format("Rule %s_rules ( ON %s:",device.getName(),device.getName());
				str += Utils.getNewLine();
				// Collection<String> parts = this.getPartNamesFromParts(device);
				Collection<String> parts = new ArrayList<>();
				for (int j = 0; j < device.getNumComponent(); j++) {
					Component component = device.getComponentAtIdx(j);
					if (component instanceof Part) {
						Part part = (Part)component;
						parts.add(component.getName());
						if (part.getPartType().equalsIgnoreCase(Eugene.S_PROMOTER)) {
							str += Utils.getTabCharacter();
							str += String.format("%s %s %s",Rules.S_CONTAINS,part.getName(),Rules.S_AND);
							str += Utils.getNewLine();
						}
					}
					for (String r : rules) {
						Collection<String> ruleParts = this.getNamesFromRule(r);
						if (parts.containsAll(ruleParts)) {
							if (!str.toLowerCase().contains(Rules.S_STARTSWITH.toLowerCase())) {
								str += Utils.getTabCharacter();
								str += String.format("%s %s",r,Rules.S_AND);
								str += Utils.getNewLine();
							} else {
								// TODO log something about duplicate startswith
							}
						}
					}
				}
				str += Utils.getTabCharacter();
				str += "ALL_FORWARD";
				str += Utils.getNewLine();
				str += ");";
				str += Utils.getNewLine();
				rtn.add(str);
			}
		}
		return rtn;
	}

	private Collection<String> getProducts() {
		Collection<String> rtn = new ArrayList<>();
		for (Devices devices : this.getDevicesMap().values()) {
			for (int i = 0; i < devices.getNumDevice(); i++) {
				Device device = devices.getDeviceAtIdx(i);
				String str = "";
				str += String.format("%s_devices = product(%s);",device.getName(),device.getName());
				rtn.add(str);
			}
		}
		return rtn;
	}

	private Collection<String> getGateDeviceNames(Devices devices) {
		Collection<String> rtn = new ArrayList<>();
		for (int i = 0; i < devices.getNumDevice(); i++) {
			Device device = devices.getDeviceAtIdx(i);
			rtn.add(device.getName());
		}
		return rtn;
	}

	private Collection<String> getGateDefinitions() {
		Collection<String> rtn = new ArrayList<>();
		for (Devices devices : this.getDevicesMap().values()) {
			Collection<String> names = this.getGateDeviceNames(devices);
			for (String name : names) {
				String str = "";
				str += String.format("Device unit_%s();",name);
				rtn.add(str);
			}
		}
		return rtn;
	}

	private String getCircuitDeclaration() {
		String rtn = "";
		for (int i = 0; i < this.getGroups().size(); i++) {
			rtn += String.format("Device group_%d();",i);
			rtn += Utils.getNewLine();
		}
		rtn += Utils.getNewLine();
		rtn += "Device groups();";
		rtn += Utils.getNewLine() + Utils.getNewLine();
		return rtn;
	}

	private String getCircuitInstantiation() {
		String rtn = "";
		int i = 0;
		for (Devices devices : this.getGroups()) {
			rtn += String.format("Device group_%d(",i);
			for (String str : this.getGateDeviceNames(devices)) {
				rtn += Utils.getNewLine();
				rtn += Utils.getTabCharacter();
				rtn += "unit_" + str;
				rtn += ",";
			}
			rtn = rtn.substring(0, rtn.length() - 1);
			rtn += Utils.getNewLine();
			rtn += ");";
			rtn += Utils.getNewLine();
			i++;
		}
		rtn += "Device groups(";
		i = 0;
		for (Devices devices : this.getGroups()) {
			rtn += Utils.getNewLine();
			rtn += Utils.getTabCharacter();
			rtn += String.format("group_%d",i);
			rtn += ",";
			i++;
		}
		rtn = rtn.substring(0, rtn.length() - 1);
		rtn += Utils.getNewLine();
		rtn += ");";
		rtn += Utils.getNewLine();
		return rtn;
	}

	private String getCircuitRules() {
		String rtn = "";
		int i = 0;
		for (Devices devices : this.getGroups()) {
			Collection<String> gates = this.getGateDeviceNames(devices);
			rtn += String.format("Rule allRules_%d( ON group_%d:",i,i);
			for (String str : gates) {
				rtn += Utils.getNewLine();
				rtn += Utils.getTabCharacter();
				rtn += String.format("unit_%s %s 1 %s", str, Rules.S_EXACTLY, Rules.S_AND);
			}
			for (String str : this.getRules().getGateRules()) {
				Collection<String> names = getNamesFromRule(str);
				if (gates.containsAll(names)) {
					rtn += Utils.getNewLine();
					rtn += Utils.getTabCharacter();
					rtn += String.format("%s %s",str,Rules.S_AND);
				}
			}
			rtn = rtn.replaceAll(" " + Rules.S_AND + "$","");
			rtn += Utils.getNewLine();
			rtn += ");";
			rtn += Utils.getNewLine() + Utils.getNewLine();
			i++;
		}
		return rtn;
	}

	private String getResults() {
		String rtn = "";
		rtn += "Array allResults;";
		rtn += Utils.getNewLine() + Utils.getNewLine();
		int j = 0;
		for (Devices devices : this.getGroups()) {
			for (int i = 0; i < devices.getNumDevice(); i++) {
				Device device = devices.getDeviceAtIdx(i);
				j++;
				rtn += String.format("for(num i%d = 0; i%d < sizeof(%s_devices); i%d = i%d + 1) {",j,j,device.getName(),j,j);
				rtn += Utils.getNewLine();
			}
		}
		rtn += Utils.getNewLine();
		j = 0;
		for (Devices devices : this.getGroups()) {
			for (int i = 0; i < devices.getNumDevice(); i++) {
				Device device = devices.getDeviceAtIdx(i);
				j++;
				rtn += String.format("unit_%s = %s_devices[i%d];",device.getName(),device.getName(),j);
				rtn += Utils.getNewLine();
			}
		}
		rtn += Utils.getNewLine();
		rtn += this.getCircuitInstantiation();
		rtn += Utils.getNewLine() + Utils.getNewLine();
		rtn += "result = permute(groups);";
		rtn += Utils.getNewLine() + Utils.getNewLine();
		rtn += "allResults = allResults + result;";
		rtn += Utils.getNewLine() + Utils.getNewLine();
		for (Devices devices : this.getGroups()) {
			for (int i = 0; i < devices.getNumDevice(); i++) {
				rtn += "}";
				rtn += Utils.getNewLine();
			}
		}
		return rtn;
	}

	private String getBlock(Collection<String> str) {
		String rtn = "";
		rtn += String.join(Utils.getNewLine(), str);
		rtn += Utils.getNewLine() + Utils.getNewLine();
		return rtn;
	}

	/**
	 *  Perform preprocessing
	 */
	@Override
	protected void preprocessing() {
		LSResultNetlistUtils.setVertexTypeUsingLSResult(this.getNetlist());
		this.setDevicesMap(new HashMap<NetlistNode,Devices>());
		this.setGroups(new CObjectCollection<Devices>());

		// devices
		this.setDevices();
		// groups
		this.setGroups();

		logInfo("building Eugene input script");

		String script = "";

			// part types
			script += this.getBlock(this.getPartTypes());
			// part sequences
			script += this.getBlock(this.getPartSequences());
			// device definitions
			script += this.getBlock(this.getDeviceDefinitions());
			// device rules
			script += this.getBlock(this.getDeviceRules());
			// products
			script += this.getBlock(this.getProducts());
			// gate definitions
			script += this.getBlock(this.getGateDefinitions());
			// circuit definition
			script += this.getCircuitDeclaration();
			// circuit rules
			script += this.getCircuitRules();
			// results
			script += this.getResults();

			this.setEugeneScript(script);
			Utils.writeToFile(script,this.getEugeneScriptFilename());
	}

	/**
	 *  Run the (core) algorithm
	 */
	@Override
	protected void run() {
		logInfo("running Eugene");
		try {
			org.cidarlab.eugene.Eugene eugene = new org.cidarlab.eugene.Eugene();

			File cruft = new File(Utils.getWorkingDirectory()
			                      + Utils.getFileSeparator()
			                      + "exports");
			(new File(cruft.getPath()
			          + Utils.getFileSeparator()
			          + "pigeon")).delete();
			cruft.delete();

			EugeneCollection ec = eugene.executeScript(this.getEugeneScript());
			this.setEugeneResults((EugeneArray) ec.get("allResults"));
		} catch ( EugeneException e ) {
			e.printStackTrace();
		}
	}

	private NetlistNode getNetlistNodeByGateName(String name) {
		NetlistNode rtn = null;
		name = name.replaceAll("^unit_","");
		name = name.replaceAll("__SPLIT_[0-9]+$","");
		name = name.replaceAll("_device$","");
		for (NetlistNode node : this.getDevicesMap().keySet()) {
			String gateType = node.getResultNetlistNodeData().getGateType();
			if (gateType.contains(name)) {
				rtn = node;
				break;
			}
		}
		return rtn;
	}

	/**
	 *  Perform postprocessing
	 */
	@Override
	protected void postprocessing() {
		logInfo("processing Eugene output");

		Placements placements = new Placements();
		this.getNetlist().getResultNetlistData().setPlacements(placements);

		EugeneArray results = this.getEugeneResults();

		if (results == null) {
			throw new RuntimeException("Eugene error!");
		}

		for (int i = 0; i < results.getElements().size(); i++) {
			if (i >= this.getMaxPlacements())
				break;

			Placement placement = new Placement(true,false);
			placements.addPlacement(placement);

			NamedElement groupsElement = null;

			try {
				groupsElement = results.getElement(i);
			} catch (EugeneException e) {
				e.printStackTrace();
			}

			if (groupsElement instanceof org.cidarlab.eugene.dom.Device) {
				org.cidarlab.eugene.dom.Device groupsDevice = (org.cidarlab.eugene.dom.Device) groupsElement;
				List<NamedElement> groups = groupsDevice.getComponentList();
				for (int j = 0; j < groups.size(); j++) {
					NamedElement groupElement = groups.get(j);
					PlacementGroup group = new PlacementGroup(true,false);
					group.setName(groupElement.getName());
					group.setBackbone("backbone");
					placement.addPlacementGroup(group);
					if (groupElement instanceof org.cidarlab.eugene.dom.Device) {
						org.cidarlab.eugene.dom.Device groupDevice = (org.cidarlab.eugene.dom.Device) groupElement;
						List<NamedElement> components = groupDevice.getComponentList();
						for (int k = 0; k < components.size(); k++) {
							NamedElement componentElement = components.get(k);
							org.cidarlab.eugene.dom.Device componentDevice = (org.cidarlab.eugene.dom.Device) componentElement;
							String name = componentElement.getName();

							NetlistNode node = this.getNetlistNodeByGateName(name);

							String o = "";
							try {
								o = componentDevice.getOrientations(j).toString();
							} catch (EugeneException e) {
								e.printStackTrace();
							}

							if (o.contains(Rules.S_REVERSE)) {
								placement.setDirection(false);
								try {
									org.cidarlab.eugene.dom.Device reverse = DeviceUtils.flipAndInvert(componentDevice);
									groupElement = reverse;
								} catch (EugeneException e) {
									e.printStackTrace();
								}
							}

							List<String> parts = new ArrayList<>();
							for (NamedElement part : componentDevice.getComponentList()) {
								parts.add(part.getName());
							}
							org.cellocad.cello2.results.placing.placement.Component component = new org.cellocad.cello2.results.placing.placement.Component(parts,true,false);
							component.setDirection(true);
							component.setNode(node.getName());
							component.setName(parts.get(parts.size()-1) + "_" + String.valueOf(k));
							group.addComponent(component);
						}
					}
				}
			}
		}
	}

	/**
	 *  Returns the Logger for the <i>Eugene</i> algorithm
	 *
	 *  @return the logger for the <i>Eugene</i> algorithm
	 */
	@Override
	protected Logger getLogger() {
		return Eugene.logger;
	}

	private static final Logger logger = LogManager.getLogger(Eugene.class);

	/**
	 * Getter for <i>maxPlacements</i>
	 * @return value of <i>maxPlacements</i>
	 */
	protected Integer getMaxPlacements() {
		return this.maxPlacements;
	}

	/**
	 * Setter for <i>maxPlacements</i>
	 * @param maxPlacements the value to set <i>maxPlacements</i>
	 */
	protected void setMaxPlacements(final Integer maxPlacements) {
		this.maxPlacements = maxPlacements;
	}

	/**
	 * Getter for <i>splitTandemPromoters</i>
	 * @return value of <i>splitTandemPromoters</i>
	 */
	public Boolean getSplitTandemPromoters() {
		return splitTandemPromoters;
	}

	/**
	 * Setter for <i>splitTandemPromoters</i>
	 * @param splitTandemPromoters the value to set <i>splitTandemPromoters</i>
	 */
	public void setSplitTandemPromoters(Boolean splitTandemPromoters) {
		this.splitTandemPromoters = splitTandemPromoters;
	}

	/**
	 * Getter for <i>eugeneResults</i>
	 * @return value of <i>eugeneResults</i>
	 */
	protected EugeneArray getEugeneResults() {
		return eugeneResults;
	}

	/**
	 * Setter for <i>eugeneResults</i>
	 * @param eugeneResults the value to set <i>eugeneResults</i>
	 */
	protected void setEugeneResults(final EugeneArray eugeneResults) {
		this.eugeneResults = eugeneResults;
	}

	/**
	 * Getter for <i>eugeneScript</i>
	 * @return value of <i>eugeneScript</i>
	 */
	protected String getEugeneScript() {
		return eugeneScript;
	}

	/**
	 * Setter for <i>eugeneScript</i>
	 * @param eugeneScript the value to set <i>eugeneScript</i>
	 */
	protected void setEugeneScript(final String eugeneScript) {
		this.eugeneScript = eugeneScript;
	}

	/**
	 * Getter for <i>eugeneScriptFilename</i>
	 * @return value of <i>eugeneScriptFilename</i>
	 */
	protected String getEugeneScriptFilename() {
		return eugeneScriptFilename;
	}

	/**
	 * Setter for <i>eugeneScriptFilename</i>
	 * @param eugeneScriptFilename the value to set <i>eugeneScriptFilename</i>
	 */
	protected void setEugeneScriptFilename(final String eugeneScriptFilename) {
		this.eugeneScriptFilename = eugeneScriptFilename;
	}

	/**
	 * @return the deviceMap
	 */
	public Map<NetlistNode,Devices> getDevicesMap() {
		return devicesMap;
	}

	/**
	 * @param deviceMap the deviceMap to set
	 */
	public void setDevicesMap(Map<NetlistNode,Devices> devicesMap) {
		this.devicesMap = devicesMap;
	}

	/**
	 * Getter for <i>groups</i>
	 * @return value of <i>groups</i>
	 */
	public CObjectCollection<Devices> getGroups() {
		return groups;
	}

	/**
	 * Setter for <i>groups</i>
	 * @param groups the value to set <i>groups</i>
	 */
	public void setGroups(CObjectCollection<Devices> groups) {
		this.groups = groups;
	}

	/**
	 * Getter for <i>gates</i>
	 * @return value of <i>gates</i>
	 */
	protected CObjectCollection<Gate> getGates() {
		return gates;
	}

	/**
	 * Setter for <i>gates</i>
	 * @param gates the value to set <i>gates</i>
	 */
	protected void setGates(final CObjectCollection<Gate> gates) {
		this.gates = gates;
	}

	/**
	 * Getter for <i>parts</i>
	 * @return value of <i>parts</i>
	 */
	protected CObjectCollection<Part> getParts() {
		return parts;
	}

	/**
	 * Setter for <i>parts</i>
	 * @param parts the value to set <i>parts</i>
	 */
	protected void setParts(final CObjectCollection<Part> parts) {
		this.parts = parts;
	}

	/*
	 * InputSensors
	 */
	/**
	 * Getter for <i>sensors</i>
	 * @return value of <i>sensors</i>
	 */
	public CObjectCollection<InputSensor> getInputSensors() {
		return sensors;
	}

	/**
	 * Setter for <i>sensors</i>
	 * @param sensors the value to set <i>sensors</i>
	 */
	protected void setInputSensors(final CObjectCollection<InputSensor> sensors) {
		this.sensors = sensors;
	}

	/**
	 * Getter for <i>reporters</i>
	 * @return value of <i>reporters</i>
	 */
	public CObjectCollection<OutputReporter> getOutputReporters() {
		return reporters;
	}

	/**
	 * Setter for <i>reporters</i>
	 * @param reporters the value to set <i>reporters</i>
	 */
	protected void setOutputReporters(final CObjectCollection<OutputReporter> reporters) {
		this.reporters = reporters;
	}

	/**
	 * Getter for <i>rules</i>
	 * @return value of <i>rules</i>
	 */
	public Rules getRules() {
		return rules;
	}

	/**
	 * Setter for <i>rules</i>
	 * @param rules the value to set rules
	 */
	public void setRules(Rules rules) {
		this.rules = rules;
	}

	/**
	 * Getter for <i>outputReporterOption</i>
	 * @return value of <i>outputReporterOption</i>
	 */
	public String getOutputReporterOption() {
		return outputReporterOption;
	}

	/**
	 * Setter for <i>outputReporterOption</i>
	 * @param outputReporterOption the value to set outputReporterOption
	 */
	public void setOutputReporterOption(String outputReporterOption) {
		this.outputReporterOption = outputReporterOption;
	}

	/**
	 * Getter for <i>inputSensorOption</i>
	 * @return value of <i>inputSensorOption</i>
	 */
	public String getInputSensorOption() {
		return inputSensorOption;
	}

	/**
	 * Setter for <i>inputSensorOption</i>
	 * @param inputSensorOption the value to set inputSensorOption
	 */
	public void setInputSensorOption(String inputSensorOption) {
		this.inputSensorOption = inputSensorOption;
	}

	private static String S_NONE = "none";
	private static String S_WITH_CIRCUIT = "with_circuit";
	private static String S_SEPARATE = "separate";
	private static final String[] ValidPlacementOptions =
		{
			S_NONE,
			S_WITH_CIRCUIT,
			S_SEPARATE
		};

	private Integer maxPlacements;
	private Boolean splitTandemPromoters;
	private String outputReporterOption;
	private String inputSensorOption;
	private EugeneArray eugeneResults;
	private String eugeneScript;
	private String eugeneScriptFilename;
	private Map<NetlistNode,Devices> devicesMap;
	private CObjectCollection<Devices> groups;
	private CObjectCollection<Gate> gates;
	private CObjectCollection<Part> parts;
	private CObjectCollection<InputSensor> sensors;
	private CObjectCollection<OutputReporter> reporters;
	private Rules rules;

	static private String S_PROMOTER = "promoter";
}

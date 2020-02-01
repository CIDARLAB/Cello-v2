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
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cellocad.cello2.common.CObjectCollection;
import org.cellocad.cello2.common.Utils;
import org.cellocad.cello2.common.graph.algorithm.SinkBFS;
import org.cellocad.cello2.placing.algorithm.PLAlgorithm;
import org.cellocad.cello2.placing.algorithm.Eugene.data.DNAPlotLibUtils;
import org.cellocad.cello2.placing.algorithm.Eugene.data.Devices;
import org.cellocad.cello2.placing.algorithm.Eugene.data.EugeneDataUtils;
import org.cellocad.cello2.placing.algorithm.Eugene.data.EugeneNetlistData;
import org.cellocad.cello2.placing.algorithm.Eugene.data.EugeneNetlistEdgeData;
import org.cellocad.cello2.placing.algorithm.Eugene.data.EugeneNetlistNodeData;
import org.cellocad.cello2.placing.algorithm.Eugene.data.structure.EugeneDevice;
import org.cellocad.cello2.placing.algorithm.Eugene.data.structure.EugeneObject;
import org.cellocad.cello2.placing.algorithm.Eugene.data.structure.EugenePart;
import org.cellocad.cello2.placing.algorithm.Eugene.data.structure.EugeneTemplate;
import org.cellocad.cello2.placing.algorithm.Eugene.data.ucf.CircuitRules;
import org.cellocad.cello2.placing.algorithm.Eugene.data.ucf.ContainerSpecification;
import org.cellocad.cello2.placing.algorithm.Eugene.data.ucf.DeviceRules;
import org.cellocad.cello2.placing.algorithm.Eugene.data.ucf.EugeneRules;
import org.cellocad.cello2.placing.algorithm.Eugene.data.ucf.Gate;
import org.cellocad.cello2.placing.algorithm.Eugene.data.ucf.GeneticLocation;
import org.cellocad.cello2.placing.algorithm.Eugene.data.ucf.InputSensor;
import org.cellocad.cello2.placing.algorithm.Eugene.data.ucf.OutputDevice;
import org.cellocad.cello2.placing.algorithm.Eugene.data.ucf.Part;
import org.cellocad.cello2.placing.runtime.environment.PLArgString;
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
 * The Eugene class implements the <i>Eugene</i> algorithm in the <i>placing</i>
 * stage.
 *
 * @author Timothy Jones
 *
 * @date 2018-06-06
 *
 */
public class Eugene extends PLAlgorithm {

	/**
	 * Returns the <i>EugeneNetlistNodeData</i> of the <i>node</i>
	 *
	 * @param node a node within the <i>netlist</i> of this instance
	 * @return the <i>EugeneNetlistNodeData</i> instance if it exists, null
	 *         otherwise
	 */
	protected EugeneNetlistNodeData getEugeneNetlistNodeData(NetlistNode node) {
		EugeneNetlistNodeData rtn = null;
		rtn = (EugeneNetlistNodeData) node.getNetlistNodeData();
		return rtn;
	}

	/**
	 * Returns the <i>EugeneNetlistEdgeData</i> of the <i>edge</i>
	 *
	 * @param edge an edge within the <i>netlist</i> of this instance
	 * @return the <i>EugeneNetlistEdgeData</i> instance if it exists, null
	 *         otherwise
	 */
	protected EugeneNetlistEdgeData getEugeneNetlistEdgeData(NetlistEdge edge) {
		EugeneNetlistEdgeData rtn = null;
		rtn = (EugeneNetlistEdgeData) edge.getNetlistEdgeData();
		return rtn;
	}

	/**
	 * Returns the <i>EugeneNetlistData</i> of the <i>netlist</i>
	 *
	 * @param netlist the netlist of this instance
	 * @return the <i>EugeneNetlistData</i> instance if it exists, null otherwise
	 */
	protected EugeneNetlistData getEugeneNetlistData(Netlist netlist) {
		EugeneNetlistData rtn = null;
		rtn = (EugeneNetlistData) netlist.getNetlistData();
		return rtn;
	}

	/**
	 * Gets the Constraint data from the NetlistConstraintFile
	 */
	@Override
	protected void getConstraintFromNetlistConstraintFile() {

	}

	/**
	 * Gets the data from the UCF
	 */
	@Override
	protected void getDataFromUCF() {
		this.setGates(EugeneDataUtils.getGates(this.getTargetData()));
		this.setParts(EugeneDataUtils.getParts(this.getTargetData()));
		this.setInputSensors(EugeneDataUtils.getInputSensors(this.getTargetData()));
		this.setOutputDevices(EugeneDataUtils.getOutputReporters(this.getTargetData()));
		this.setCircuitRules(EugeneDataUtils.getCircuitRules(this.getTargetData()));
		this.setDeviceRules(EugeneDataUtils.getDeviceRules(this.getTargetData()));
		this.setGeneticLocations(EugeneDataUtils.getGeneticLocations(this.getTargetData()));
		this.setContainerSpecifications(EugeneDataUtils.getContainerSpecifications(this.getTargetData()));
	}

	/**
	 * Set parameter(s) value(s) of the algorithm
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
	}

	/**
	 * Validate parameter value of the algorithm
	 */
	@Override
	protected void validateParameterValues() {
		if (this.getMaxPlacements() == null || this.getMaxPlacements() <= 0)
			this.setMaxPlacements(5);
	}

	private void setDevices() {
		SinkBFS<NetlistNode, NetlistEdge, Netlist> BFS = new SinkBFS<NetlistNode, NetlistEdge, Netlist>(
				this.getNetlist());
		NetlistNode node = null;
		node = BFS.getNextVertex();
		while (node != null) {
			Collection<EugeneDevice> devices = EugeneUtils.getDevices(node, this.getGates(), this.getOutputDevices());
			this.getDevicesMap().put(node, devices);
			node = BFS.getNextVertex();
		}
	}

	private Collection<String> getPartTypes() {
		Set<String> rtn = new HashSet<String>();
		Set<String> temp = new HashSet<String>();
		for (Collection<EugeneDevice> devices : this.getDevicesMap().values()) {
			Iterator<EugeneDevice> it = devices.iterator();
			while (it.hasNext()) {
				EugeneDevice d = it.next();
				temp.addAll(EugeneUtils.getPartTypes(d, this.getParts()));
			}
		}
		Iterator<String> it = temp.iterator();
		while (it.hasNext()) {
			String str = EugeneUtils.getPartTypeDefinition(it.next());
			rtn.add(str);
		}
		return rtn;
	}

	private Collection<String> getPartDefinitions() {
		Set<String> rtn = new HashSet<String>();
		for (Collection<EugeneDevice> devices : this.getDevicesMap().values()) {
			Iterator<EugeneDevice> it = devices.iterator();
			while (it.hasNext()) {
				EugeneDevice d = it.next();
				rtn.addAll(EugeneUtils.getPartDefinitions(d, this.getParts()));
			}
		}
		for (int i = 0; i < this.getNetlist().getNumVertex(); i++) {
			NetlistNode node = this.getNetlist().getVertexAtIdx(i);
			CObjectCollection<Part> inputs = EugeneUtils.getInputs(node, this.getInputSensors(), this.getGates(), this.getParts());
			for (Part p : inputs) {
				rtn.add(EugeneUtils.getPartDefinition(p));
			}
		}
		return rtn;
	}

	private Collection<String> getDeviceDefinitions() {
		Collection<String> rtn = new ArrayList<String>();
		for (Collection<EugeneDevice> devices : this.getDevicesMap().values()) {
			Iterator<EugeneDevice> it = devices.iterator();
			while (it.hasNext()) {
				EugeneDevice d = it.next();
				rtn.add(d.getDevice());
			}
		}
		return rtn;
	}

	private Collection<String> getLocationSpecifications() {
		Collection<String> rtn = new ArrayList<>();
		rtn.add(EugeneUtils.getPartTypeDefinition(S_FENCEPOST));
		for (int i = 1; i < this.getGeneticLocations().size(); i++) {
			GeneticLocation l = this.getGeneticLocations().get(i);
			rtn.add(String.format("%s %s();", S_FENCEPOST, l.getName()));
		}
		return rtn;
	}

	private Collection<String> getDeviceRuleSpecifications(EugeneDevice device, NetlistNode node) {
		Collection<String> rtn = new ArrayList<>();
		Collection<EugeneDevice> subs = new ArrayList<>();
		String str = "";
		str += String.format("Rule %sRules ( ON %s:", device.getName(), device.getName());
		str += Utils.getNewLine();
		Collection<String> rules = new HashSet<>();
		CObjectCollection<Part> inputs = EugeneUtils.getInputs(node, this.getInputSensors(), this.getGates(),
				this.getParts());
		int i = device.getIdx();
		for (EugeneObject o : device.getComponents()) {
			if (o instanceof EugenePart) {
				for (String rule : this.getDeviceRules().getRulesByObjectName(o.getName())) {
					rules.add(Utils.getTabCharacter() + rule);
				}
			}
			if (o instanceof EugeneDevice) {
				subs.add((EugeneDevice) o);
			}
			if (o instanceof EugeneTemplate) {
				Part input = inputs.get(i);
				i++;
				rules.add(Utils.getTabCharacter() + EugeneRules.S_CONTAINS + " " + input.getName());
			}
		}
		str += String.join(" " + EugeneRules.S_AND + Utils.getNewLine(), rules);
		str += Utils.getNewLine();
		str += ");" + Utils.getNewLine();
		rtn.add(str);
		for (EugeneDevice d : subs) {
			rtn.addAll(getDeviceRuleSpecifications(d, node));
		}
		return rtn;
	}

	private Collection<String> getDeviceRuleSpecifications() {
		Collection<String> rtn = new ArrayList<>();
		for (int i = 0; i < this.getNetlist().getNumVertex(); i++) {
			NetlistNode node = this.getNetlist().getVertexAtIdx(i);
			Collection<EugeneDevice> devices = this.getDevicesMap().get(node);
			int j = 0;
			for (EugeneDevice d : devices) {
				d.setIdx(j);
				for (EugeneObject o : d.getComponents()) {
					if (o instanceof EugeneTemplate) {
						j++;
					}
				}
			}
			for (EugeneDevice d : devices) {
				rtn.addAll(this.getDeviceRuleSpecifications(d, node));
			}
		}
		return rtn;
	}

	private Collection<String> getProducts() {
		Collection<String> rtn = new ArrayList<>();
		for (Collection<EugeneDevice> devices : this.getDevicesMap().values()) {
			for (EugeneDevice d : devices) {
				String str = String.format("%s_devices = product(%s);", d.getName(), d.getName());
				rtn.add(str);
			}
		}
		return rtn;
	}

	private String getCircuitDeclaration() {
		String rtn = "";
		for (Collection<EugeneDevice> devices : this.getDevicesMap().values()) {
			for (EugeneDevice d : devices) {
				rtn += String.format("Device %sDevice();", d.getName());
				rtn += Utils.getNewLine();
			}
		}
		rtn += Utils.getNewLine();
		rtn += "Device circuit();";
		rtn += Utils.getNewLine() + Utils.getNewLine();
		return rtn;
	}

	private String getCircuitInstantiation() {
		String rtn = "";
		rtn += "Device circuit(";
		// TODO get markers
		for (Collection<EugeneDevice> devices : this.getDevicesMap().values()) {
			for (EugeneDevice d : devices) {
				rtn += Utils.getNewLine();
				rtn += Utils.getTabCharacter();
				rtn += String.format("%sDevice", d.getName());
				rtn += ",";
			}
		}
		for (int i = 1; i < this.getGeneticLocations().size(); i++) {
			GeneticLocation l = this.getGeneticLocations().get(i);
			rtn += Utils.getNewLine();
			rtn += Utils.getTabCharacter();
			rtn += String.format("%s", l.getName());
			rtn += ",";
		}
		rtn = rtn.substring(0, rtn.length() - 1);
		rtn += Utils.getNewLine();
		rtn += ");";
		rtn += Utils.getNewLine();
		return rtn;
	}

	private String getResults() {
		String rtn = "";
		rtn += "Array allResults;";
		rtn += Utils.getNewLine() + Utils.getNewLine();
		int j = 0;
		final String fmt = "for(num i%d = 0; i%d < sizeof(%s_devices); i%d = i%d + 1) {";
		for (Collection<EugeneDevice> devices : this.getDevicesMap().values()) {
			for (EugeneDevice d : devices) {
				j++;
				rtn += String.format(fmt, j, j, d.getName(), j, j);
				rtn += Utils.getNewLine();
			}
		}
		rtn += Utils.getNewLine();
		j = 0;
		for (Collection<EugeneDevice> devices : this.getDevicesMap().values()) {
			for (EugeneDevice d : devices) {
				j++;
				rtn += String.format("%sDevice = %s_devices[i%d];", d.getName(), d.getName(), j);
				rtn += Utils.getNewLine();
			}
		}
		rtn += Utils.getNewLine();
		rtn += this.getCircuitInstantiation();
		rtn += Utils.getNewLine() + Utils.getNewLine();
		rtn += "result = permute(circuit);";
		rtn += Utils.getNewLine() + Utils.getNewLine();
		rtn += "allResults = allResults + result;";
		rtn += Utils.getNewLine() + Utils.getNewLine();
		for (Collection<EugeneDevice> devices : this.getDevicesMap().values()) {
			for (int i = 0; i < devices.size(); i++) {
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
	 * Perform preprocessing
	 */
	@Override
	protected void preprocessing() {
		LSResultNetlistUtils.setVertexTypeUsingLSResult(this.getNetlist());
		this.setDevicesMap(new HashMap<NetlistNode, Collection<EugeneDevice>>());

		// devices
		this.setDevices();

		// device names
		Collection<String> deviceNames = new ArrayList<>();
		for (Collection<EugeneDevice> devices : this.getDevicesMap().values()) {
			for (EugeneDevice d : devices) {
				deviceNames.add(d.getName());
			}
		}
		Collection<String> fenceposts = new ArrayList<>();
		for (int i = 1; i < this.getGeneticLocations().size(); i++) {
			GeneticLocation l = this.getGeneticLocations().get(i);
			fenceposts.add(l.getName());
		}

		logInfo("building Eugene input script");

		String script = "";

		// part types
		script += this.getBlock(this.getPartTypes());
		// part sequences
		script += this.getBlock(this.getPartDefinitions());
		// location specifications
		script += this.getBlock(this.getLocationSpecifications());
		// device definitions
		script += this.getBlock(this.getDeviceDefinitions());
		// device rules
		script += this.getBlock(this.getDeviceRuleSpecifications());
		// products
		script += this.getBlock(this.getProducts());
		// circuit definition
		script += this.getCircuitDeclaration();
		// circuit rules
		script += this.getCircuitRules().filter(deviceNames, fenceposts);
		// results
		script += this.getResults();

		this.setEugeneScript(script);
		Utils.writeToFile(script, this.getEugeneScriptFilename());
	}

	/**
	 * Run the (core) algorithm
	 */
	@Override
	protected void run() {
		logInfo("running Eugene");
		try {
			org.cidarlab.eugene.Eugene eugene = new org.cidarlab.eugene.Eugene();

			File cruft = new File(Utils.getWorkingDirectory() + Utils.getFileSeparator() + "exports");
			(new File(cruft.getPath() + Utils.getFileSeparator() + "pigeon")).delete();
			cruft.delete();

			EugeneCollection ec = eugene.executeScript(this.getEugeneScript());
			this.setEugeneResults((EugeneArray) ec.get("allResults"));
		} catch (EugeneException e) {
			e.printStackTrace();
		}
	}

	private NetlistNode getNetlistNodeByGateName(String name) {
		NetlistNode rtn = null;
		name = name.replaceAll("^unit_", "");
		name = name.replaceAll("__SPLIT_[0-9]+$", "");
		name = name.replaceAll("_device$", "");
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
	 * Perform postprocessing
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

			Placement placement = new Placement(true, false);
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
					PlacementGroup group = new PlacementGroup(true, false);
					group.setName(groupElement.getName());
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
								// FIXME: part orientations not respected
								o = componentDevice.getOrientations(0).toString();
							} catch (EugeneException e) {
								e.printStackTrace();
							}

							if (o.contains(EugeneRules.S_REVERSE)) {
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
							org.cellocad.cello2.results.placing.placement.Component component = new org.cellocad.cello2.results.placing.placement.Component(
									parts, true, false);
							component.setDirection(true);
							component.setNode(node.getName());
							component.setName(parts.get(parts.size() - 1) + "_" + String.valueOf(k));
							group.addComponent(component);
						}
					}
				}
			}
		}
		// DNAPlotLib
		logInfo("generating dnaplotlib figures");
		String outputDir = this.getRuntimeEnv().getOptionValue(PLArgString.OUTPUTDIR);
		File file = null;
		List<String> designs = DNAPlotLibUtils.getDNADesigns(this.getNetlist());
		String designsFilename = outputDir + Utils.getFileSeparator() + "dpl_dna_designs.csv";
		file = new File(designsFilename);
		DNAPlotLibUtils.writeCSV(designs, file);
		List<String> parts = DNAPlotLibUtils.getPartInformation(this.getNetlist(), this.getParts(), this.getGates());
		String partsFilename = outputDir + Utils.getFileSeparator() + "dpl_part_information.csv";
		file = new File(partsFilename);
		DNAPlotLibUtils.writeCSV(parts, file);
		List<String> reg = DNAPlotLibUtils.getRegulatoryInformation(this.getNetlist(), this.getParts(),
				this.getGates());
		String regFilename = outputDir + Utils.getFileSeparator() + "dpl_regulatory_information.csv";
		file = new File(regFilename);
		DNAPlotLibUtils.writeCSV(reg, file);
		String params;
		try {
			params = Utils.getResourceAsString("plot_parameters.csv");
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		String paramsFilename = outputDir + Utils.getFileSeparator() + "plot_parameters.csv";
		Utils.writeToFile(params, paramsFilename);
		String fmt = "%s -W ignore %s -params %s -parts %s -designs %s -regulation %s -output %s";
		String output = outputDir + Utils.getFileSeparator() + this.getNetlist().getName() + "_dpl";
		String cmd = String.format(fmt, PLArgString.PYTHONENV, "library_plot.py", partsFilename, designsFilename,
				regFilename, output);
		Utils.executeAndWaitForCommand(cmd + ".pdf");
		Utils.executeAndWaitForCommand(cmd + ".png");
	}

	/**
	 * Returns the Logger for the <i>Eugene</i> algorithm
	 *
	 * @return the logger for the <i>Eugene</i> algorithm
	 */
	@Override
	protected Logger getLogger() {
		return Eugene.logger;
	}

	private static final Logger logger = LogManager.getLogger(Eugene.class);

	/**
	 * Getter for <i>maxPlacements</i>
	 * 
	 * @return value of <i>maxPlacements</i>
	 */
	protected Integer getMaxPlacements() {
		return this.maxPlacements;
	}

	/**
	 * Setter for <i>maxPlacements</i>
	 * 
	 * @param maxPlacements the value to set <i>maxPlacements</i>
	 */
	protected void setMaxPlacements(final Integer maxPlacements) {
		this.maxPlacements = maxPlacements;
	}

	/**
	 * Getter for <i>eugeneResults</i>
	 * 
	 * @return value of <i>eugeneResults</i>
	 */
	protected EugeneArray getEugeneResults() {
		return eugeneResults;
	}

	/**
	 * Setter for <i>eugeneResults</i>
	 * 
	 * @param eugeneResults the value to set <i>eugeneResults</i>
	 */
	protected void setEugeneResults(final EugeneArray eugeneResults) {
		this.eugeneResults = eugeneResults;
	}

	/**
	 * Getter for <i>eugeneScript</i>
	 * 
	 * @return value of <i>eugeneScript</i>
	 */
	protected String getEugeneScript() {
		return eugeneScript;
	}

	/**
	 * Setter for <i>eugeneScript</i>
	 * 
	 * @param eugeneScript the value to set <i>eugeneScript</i>
	 */
	protected void setEugeneScript(final String eugeneScript) {
		this.eugeneScript = eugeneScript;
	}

	/**
	 * Getter for <i>eugeneScriptFilename</i>
	 * 
	 * @return value of <i>eugeneScriptFilename</i>
	 */
	protected String getEugeneScriptFilename() {
		return eugeneScriptFilename;
	}

	/**
	 * Getter for <i>devicesMap</i>
	 *
	 * @return value of <i>devicesMap</i>
	 */
	public Map<NetlistNode, Collection<EugeneDevice>> getDevicesMap() {
		return devicesMap;
	}

	/**
	 * Setter for <i>devicesMap</i>
	 *
	 * @param devicesMap the value to set <i>devicesMap</i>
	 */
	public void setDevicesMap(Map<NetlistNode, Collection<EugeneDevice>> devicesMap) {
		this.devicesMap = devicesMap;
	}

	/**
	 * Setter for <i>eugeneScriptFilename</i>
	 * 
	 * @param eugeneScriptFilename the value to set <i>eugeneScriptFilename</i>
	 */
	protected void setEugeneScriptFilename(final String eugeneScriptFilename) {
		this.eugeneScriptFilename = eugeneScriptFilename;
	}

	/**
	 * Getter for <i>groupsMap</i>
	 * 
	 * @return value of <i>groupsMap</i>
	 */
	public Map<ContainerSpecification, Devices> getGroupsMap() {
		return groupsMap;
	}

	/**
	 * Setter for <i>groupsMap</i>
	 * 
	 * @param groupsMap the value to set <i>groupsMap</i>
	 */
	public void setGroupsMap(Map<ContainerSpecification, Devices> groupsMap) {
		this.groupsMap = groupsMap;
	}

	/**
	 * Getter for <i>gates</i>
	 * 
	 * @return value of <i>gates</i>
	 */
	protected CObjectCollection<Gate> getGates() {
		return gates;
	}

	/**
	 * Setter for <i>gates</i>
	 * 
	 * @param gates the value to set <i>gates</i>
	 */
	protected void setGates(final CObjectCollection<Gate> gates) {
		this.gates = gates;
	}

	/**
	 * Getter for <i>parts</i>
	 * 
	 * @return value of <i>parts</i>
	 */
	protected CObjectCollection<Part> getParts() {
		return parts;
	}

	/**
	 * Setter for <i>parts</i>
	 * 
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
	 * 
	 * @return value of <i>sensors</i>
	 */
	public CObjectCollection<InputSensor> getInputSensors() {
		return sensors;
	}

	/**
	 * Setter for <i>sensors</i>
	 * 
	 * @param sensors the value to set <i>sensors</i>
	 */
	protected void setInputSensors(final CObjectCollection<InputSensor> sensors) {
		this.sensors = sensors;
	}

	/**
	 * Getter for <i>devices</i>
	 * 
	 * @return value of <i>devices</i>
	 */
	public CObjectCollection<OutputDevice> getOutputDevices() {
		return outputDevices;
	}

	/**
	 * Setter for <i>devices</i>
	 * 
	 * @param devices the value to set <i>devices</i>
	 */
	protected void setOutputDevices(final CObjectCollection<OutputDevice> devices) {
		this.outputDevices = devices;
	}

	/**
	 * Getter for <i>circuitRules</i>
	 * 
	 * @return value of <i>circuitRules</i>
	 */
	public CircuitRules getCircuitRules() {
		return circuitRules;
	}

	/**
	 * Setter for <i>circuitRules</i>
	 * 
	 * @param rules the value to set rules
	 */
	public void setCircuitRules(CircuitRules circuitRules) {
		this.circuitRules = circuitRules;
	}

	/**
	 * Getter for <i>deviceRules</i>
	 *
	 * @return value of <i>deviceRules</i>
	 */
	public DeviceRules getDeviceRules() {
		return deviceRules;
	}

	/**
	 * Setter for <i>deviceRules</i>
	 *
	 * @param deviceRules the value to set <i>deviceRules</i>
	 */
	public void setDeviceRules(DeviceRules deviceRules) {
		this.deviceRules = deviceRules;
	}

	/**
	 * Getter for <i>geneticLocations</i>
	 *
	 * @return value of <i>geneticLocations</i>
	 */
	public CObjectCollection<GeneticLocation> getGeneticLocations() {
		return geneticLocations;
	}

	/**
	 * Setter for <i>geneticLocations</i>
	 *
	 * @param geneticLocations the value to set <i>geneticLocations</i>
	 */
	public void setGeneticLocations(CObjectCollection<GeneticLocation> geneticLocations) {
		this.geneticLocations = geneticLocations;
	}

	/**
	 * Getter for <i>containerSpecifications</i>
	 * 
	 * @return value of <i>containerSpecifications</i>
	 */
	public CObjectCollection<ContainerSpecification> getContainerSpecifications() {
		return containerSpecifications;
	}

	/**
	 * Setter for <i>containerSpecifications</i>
	 * 
	 * @param containerSpecifications the value to set
	 *                                <i>containerSpecifications</i>
	 */
	public void setContainerSpecifications(CObjectCollection<ContainerSpecification> containerSpecifications) {
		this.containerSpecifications = containerSpecifications;
	}

	private Integer maxPlacements;
	private EugeneArray eugeneResults;
	private String eugeneScript;
	private String eugeneScriptFilename;
	private Map<NetlistNode, Collection<EugeneDevice>> devicesMap;
	private Map<ContainerSpecification, Devices> groupsMap;
	private CObjectCollection<Gate> gates;
	private CObjectCollection<Part> parts;
	private CObjectCollection<InputSensor> sensors;
	private CObjectCollection<OutputDevice> outputDevices;
	private CircuitRules circuitRules;
	private DeviceRules deviceRules;
	private CObjectCollection<GeneticLocation> geneticLocations;
	private CObjectCollection<ContainerSpecification> containerSpecifications;

	static private String S_FENCEPOST = "fencepost";
}

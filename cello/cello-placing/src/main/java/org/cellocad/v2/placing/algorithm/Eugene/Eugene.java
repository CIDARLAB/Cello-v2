/**
 * Copyright (C) 2018-2020 Boston University (BU)
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
package org.cellocad.v2.placing.algorithm.Eugene;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
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
import org.cellocad.v2.common.CObjectCollection;
import org.cellocad.v2.common.CelloException;
import org.cellocad.v2.common.Utils;
import org.cellocad.v2.common.graph.algorithm.SinkBFS;
import org.cellocad.v2.common.target.data.data.CircuitRules;
import org.cellocad.v2.common.target.data.data.DeviceRules;
import org.cellocad.v2.common.target.data.data.Gate;
import org.cellocad.v2.common.target.data.data.GeneticLocation;
import org.cellocad.v2.common.target.data.data.InputSensor;
import org.cellocad.v2.common.target.data.data.Part;
import org.cellocad.v2.common.target.data.data.StructureDevice;
import org.cellocad.v2.common.target.data.data.StructureObject;
import org.cellocad.v2.common.target.data.data.StructureTemplate;
import org.cellocad.v2.placing.algorithm.PLAlgorithm;
import org.cellocad.v2.placing.algorithm.Eugene.data.DNAPlotLibUtils;
import org.cellocad.v2.placing.algorithm.Eugene.data.EugeneNetlistData;
import org.cellocad.v2.placing.algorithm.Eugene.data.EugeneNetlistEdgeData;
import org.cellocad.v2.placing.algorithm.Eugene.data.EugeneNetlistNodeData;
import org.cellocad.v2.placing.algorithm.Eugene.target.data.EugeneTargetDataUtils;
import org.cellocad.v2.placing.algorithm.Eugene.target.data.data.EugeneDevice;
import org.cellocad.v2.placing.runtime.environment.PLArgString;
import org.cellocad.v2.placing.target.data.PLTargetDataInstance;
import org.cellocad.v2.results.logicSynthesis.LSResultsUtils;
import org.cellocad.v2.results.logicSynthesis.netlist.LSResultNetlistUtils;
import org.cellocad.v2.results.netlist.Netlist;
import org.cellocad.v2.results.netlist.NetlistEdge;
import org.cellocad.v2.results.netlist.NetlistNode;
import org.cellocad.v2.results.netlist.NetlistUtils;
import org.cellocad.v2.results.netlist.data.ResultNetlistNodeData;
import org.cellocad.v2.results.placing.placement.Component;
import org.cellocad.v2.results.placing.placement.Placement;
import org.cellocad.v2.results.placing.placement.PlacementGroup;
import org.cellocad.v2.results.placing.placement.Placements;
import org.cidarlab.eugene.dom.Device;
import org.cidarlab.eugene.dom.NamedElement;
import org.cidarlab.eugene.dom.imp.container.EugeneArray;
import org.cidarlab.eugene.dom.imp.container.EugeneCollection;
import org.cidarlab.eugene.exception.EugeneException;

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
     *
     * @throws CelloException
     */
    @Override
    protected void getDataFromUCF() throws CelloException {
        this.setTargetDataInstance(new PLTargetDataInstance(this.getTargetData()));
        this.setCircuitRules(EugeneTargetDataUtils.getCircuitRules(this.getTargetData()));
        this.setDeviceRules(EugeneTargetDataUtils.getDeviceRules(this.getTargetData()));
        this.setGeneticLocations(EugeneTargetDataUtils.getGeneticLocations(this.getTargetData()));
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
            Collection<StructureDevice> devices = EugeneUtils.getDevices(node, this.getTargetDataInstance().getGates(),
                    this.getTargetDataInstance().getOutputDevices());
            this.getDevicesMap().put(node, devices);
            for (StructureDevice d : devices) {
                this.getDeviceNameNetlistNodeMap().put(d.getName(), node);
            }
            node = BFS.getNextVertex();
        }
    }

    /**
	 * Get a Eugene <code>PartType</code> declaration for each part type in the
	 * circuit.
	 *
	 * @return The Eugene <code>PartType</code> definitions.
	 */
	private Collection<String> getPartTypeDeclarations() {
        Set<String> rtn = new HashSet<String>();
        Set<String> temp = new HashSet<String>();
        for (Collection<StructureDevice> devices : this.getDevicesMap().values()) {
            Iterator<StructureDevice> it = devices.iterator();
            while (it.hasNext()) {
                StructureDevice d = it.next();
                temp.addAll(EugeneUtils.getPartTypes(d, this.getTargetDataInstance().getParts()));
            }
        }
        for (int i = 0; i < this.getNetlist().getNumVertex(); i++) {
            NetlistNode node = this.getNetlist().getVertexAtIdx(i);
            ResultNetlistNodeData data = node.getResultNetlistNodeData();
            String deviceName = data.getDeviceName();
            Part p = null;
            if (!LSResultsUtils.isAllInput(node) && !LSResultsUtils.isAllOutput(node)) {
                Gate gate = this.getTargetDataInstance().getGates().findCObjectByName(deviceName);
                String output = gate.getStructure().getOutputs().get(0);
                p = this.getTargetDataInstance().getParts().findCObjectByName(output);
            }
            if (LSResultsUtils.isAllInput(node)) {
                InputSensor sensor = this.getTargetDataInstance().getInputSensors().findCObjectByName(deviceName);
                String name = sensor.getStructure().getOutputs().get(0);
                p = this.getTargetDataInstance().getParts().findCObjectByName(name);
            }
            if (p != null)
                temp.add(p.getPartType());
        }
        Iterator<String> it = temp.iterator();
        while (it.hasNext()) {
            String str = EugeneUtils.getPartTypeDefinition(it.next());
            rtn.add(str);
        }
        return rtn;
    }

	/**
	 * Get a Eugene part definition, e.g.
	 * <code>promoter pAmtR(.SEQUENCE("CTTG..."));</code>, for every part in the
	 * circuit.
	 * 
	 * @return A collection of Eugene part definitions, one for each part in the
	 *         circuit.
	 */
    private Collection<String> getPartDefinitions() {
        Set<String> rtn = new HashSet<String>();
        for (Collection<StructureDevice> devices : this.getDevicesMap().values()) {
            Iterator<StructureDevice> it = devices.iterator();
            while (it.hasNext()) {
                StructureDevice d = it.next();
                rtn.addAll(EugeneUtils.getPartDefinitions(d, this.getTargetDataInstance().getParts()));
            }
        }
        for (int i = 0; i < this.getNetlist().getNumVertex(); i++) {
            NetlistNode node = this.getNetlist().getVertexAtIdx(i);
			CObjectCollection<Part> inputs = EugeneUtils.getInputs(node, this.getTargetDataInstance());
            for (Part p : inputs) {
                rtn.add(EugeneUtils.getPartDefinition(p));
            }
        }
        return rtn;
    }

	/**
	 * Get a Eugene <code>Device</code> definition for each device in the circuit.
	 * 
	 * @return A collection of all device definitions.
	 */
    private Collection<String> getDeviceDefinitions() {
        Collection<String> rtn = new ArrayList<String>();
        for (Collection<StructureDevice> devices : this.getDevicesMap().values()) {
            Iterator<StructureDevice> it = devices.iterator();
            while (it.hasNext()) {
                StructureDevice d = it.next();
                EugeneDevice e = new EugeneDevice(d);
                rtn.add(e.toString());
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

	private String getDeviceRuleDefinitions(StructureDevice device, NetlistNode node) {
		String rtn = "";
		// all part inputs incident on node
		CObjectCollection<Part> nodeInputs = EugeneUtils.getInputs(node, this.getTargetDataInstance());
		// inputs only for particular device within node
		CObjectCollection<Part> deviceInputs = new CObjectCollection<>();
		int i = device.getIdx();
		for (StructureObject o : device.getComponents()) {
			if (o instanceof StructureTemplate) {
				Part input = nodeInputs.get(i);
				i++;
				deviceInputs.add(input);
			}
		}
		rtn = this.getDeviceRules().filter(device, deviceInputs);
        return rtn;
    }

	/**
	 * For every device in the circuit, get a Eugene device rule definition.
	 * 
	 * Example:
	 * 
	 * <pre>
	 * {@code
	 * Rule A1_AmtRRules ( ON A1_AmtR:
	 * 	CONTAINS pTet AND
	 * 	ALL_FORWARD
	 * );}
	 * </pre>
	 * 
	 * @return A collection of Eugene device rules.
	 */
	private Collection<String> getDeviceRuleDefinitions() {
        Collection<String> rtn = new ArrayList<>();
        for (int i = 0; i < this.getNetlist().getNumVertex(); i++) {
            NetlistNode node = this.getNetlist().getVertexAtIdx(i);
            Collection<StructureDevice> devices = this.getDevicesMap().get(node);
            int j = 0;
            for (StructureDevice d : devices) {
                d.setIdx(j);
                for (StructureObject o : d.getComponents()) {
                    if (o instanceof StructureTemplate) {
                        j++;
                    }
                }
            }
            for (StructureDevice d : devices) {
				rtn.add(this.getDeviceRuleDefinitions(d, node));
            }
        }
        return rtn;
    }

    private Collection<String> getProducts() {
        Collection<String> rtn = new ArrayList<>();
        for (Collection<StructureDevice> devices : this.getDevicesMap().values()) {
            for (StructureDevice d : devices) {
                String str = String.format("%s_devices = product(%s);", d.getName(), d.getName());
                rtn.add(str);
            }
        }
        return rtn;
    }

    private String getCircuitDeclaration() {
        String rtn = "";
        for (Collection<StructureDevice> devices : this.getDevicesMap().values()) {
            for (StructureDevice d : devices) {
                rtn += String.format("Device %s();", EugeneUtils.getDeviceDeviceName(d.getName()));
                rtn += Utils.getNewLine();
            }
        }
        rtn += Utils.getNewLine();
        rtn += "Device circuit();";
        rtn += Utils.getNewLine() + Utils.getNewLine();
        return rtn;
    }

	private String getCircuitDefinition() {
        String rtn = "";
        rtn += "Device circuit(";
        for (Collection<StructureDevice> devices : this.getDevicesMap().values()) {
            for (StructureDevice d : devices) {
                rtn += Utils.getNewLine();
                rtn += Utils.getTabCharacter();
                rtn += String.format("%s", EugeneUtils.getDeviceDeviceName(d.getName()));
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

    private String getResultsDefinition() {
        String rtn = "";
        rtn += "Array allResults;";
        rtn += Utils.getNewLine() + Utils.getNewLine();
        int j = 0;
        // final String fmt = "for(num i%d = 0; i%d < sizeof(%s_devices); i%d = i%d + 1) {";
        final String fmt = "for(num i%d = 0; i%d < 1; i%d = i%d + 1) {";
        for (Collection<StructureDevice> devices : this.getDevicesMap().values()) {
            for (StructureDevice d : devices) {
                j++;
                // rtn += String.format(fmt, j, j, d.getName(), j, j);
                rtn += String.format(fmt, j, j, j, j);
                rtn += Utils.getNewLine();
            }
        }
        rtn += Utils.getNewLine();
        j = 0;
        for (Collection<StructureDevice> devices : this.getDevicesMap().values()) {
            for (StructureDevice d : devices) {
                j++;
                rtn += String.format("%sDevice = %s_devices[i%d];", d.getName(), d.getName(), j);
                rtn += Utils.getNewLine();
            }
        }
        rtn += Utils.getNewLine();
		rtn += this.getCircuitDefinition();
        rtn += Utils.getNewLine() + Utils.getNewLine();
        rtn += "result = permute(circuit);";
        rtn += Utils.getNewLine() + Utils.getNewLine();
        rtn += "allResults = allResults + result;";
        rtn += Utils.getNewLine() + Utils.getNewLine();
        for (Collection<StructureDevice> devices : this.getDevicesMap().values()) {
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
        this.setDevicesMap(new HashMap<NetlistNode, Collection<StructureDevice>>());
        this.setDeviceNameNetlistNodeMap(new HashMap<String, NetlistNode>());

        // devices
        this.setDevices();

        // device names
        Collection<String> deviceNames = new ArrayList<>();
        for (Collection<StructureDevice> devices : this.getDevicesMap().values()) {
            for (StructureDevice d : devices) {
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
		script += this.getBlock(this.getPartTypeDeclarations());
        // part sequences
        script += this.getBlock(this.getPartDefinitions());
        // location specifications
        script += this.getBlock(this.getLocationSpecifications());
        // device definitions
        script += this.getBlock(this.getDeviceDefinitions());
        // device rules
		script += this.getBlock(this.getDeviceRuleDefinitions());
        // products
        script += this.getBlock(this.getProducts());
		// circuit declaration
        script += this.getCircuitDeclaration();
        // circuit rules
        script += this.getCircuitRules().filter(deviceNames, fenceposts);
        // results
        script += this.getResultsDefinition();

        this.setEugeneScript(script);
        Utils.writeToFile(script, this.getEugeneScriptFilename());
    }

    /**
     * Run the (core) algorithm
     *
     * @throws CelloException
     */
    @Override
    protected void run() throws CelloException {
        logInfo("running Eugene");
        try {
            org.cidarlab.eugene.Eugene eugene = new org.cidarlab.eugene.Eugene();

            File cruft = new File(Utils.getWorkingDirectory() + Utils.getFileSeparator() + "exports");
            (new File(cruft.getPath() + Utils.getFileSeparator() + "pigeon")).delete();
            cruft.delete();

            EugeneCollection ec = eugene.executeScript(this.getEugeneScript());
            this.setEugeneResults((EugeneArray) ec.get("allResults"));
        } catch (EugeneException e) {
            throw new CelloException(e);
        }
    }

    private void generateRNASeqPlots() throws CelloException {
        String python = this.getRuntimeEnv().getOptionValue(PLArgString.PYTHONENV);
        String inputFilePath = this.getRuntimeEnv().getOptionValue(PLArgString.INPUTNETLIST);
        String netlistFile = this.getRuntimeEnv().getOptionValue(PLArgString.OUTPUTNETLIST);
        if (netlistFile == null) {
            netlistFile = "";
            netlistFile += this.getRuntimeEnv().getOptionValue(PLArgString.OUTPUTDIR);
            netlistFile += Utils.getFileSeparator();
            netlistFile += Utils.getFilename(inputFilePath);
            netlistFile += "_outputNetlist_placing";
            netlistFile += ".json";
        }
        NetlistUtils.writeJSONForNetlist(this.getNetlist(), netlistFile);
        String fmt = "%s %s -u %s -a %s -l %s -n %s -i %s -x %s -o %s";
        Path dir;
        try {
            dir = Files.createTempDirectory("cello_");
        } catch (IOException e) {
            throw new CelloException("Unable to create temporary directory.", e);
        }
        String rnaseq;
        try {
            rnaseq = Utils.getResourceAsString("rnaseq.py");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        String rnaseqFilename = dir.toString() + Utils.getFileSeparator() + "rnaseq.py";
        Utils.writeToFile(rnaseq, rnaseqFilename);
        String userConstraintsFile = this.getRuntimeEnv().getOptionValue(PLArgString.USERCONSTRAINTSFILE);
        String inputSensorFile = this.getRuntimeEnv().getOptionValue(PLArgString.INPUTSENSORFILE);
        String outputDeviceFile = this.getRuntimeEnv().getOptionValue(PLArgString.OUTPUTDEVICEFILE);
        String outputDir = this.getRuntimeEnv().getOptionValue(PLArgString.OUTPUTDIR);
        String inputFilename = this.getNetlist().getInputFilename();
        String filename = Utils.getFilename(inputFilename);
        String activityFile = outputDir + Utils.getFileSeparator() + filename + "_activity.csv";
        String logicFile = outputDir + Utils.getFileSeparator() + filename + "_logic.csv";
        String cmd = String.format(fmt, python, rnaseqFilename, userConstraintsFile, activityFile, logicFile,
                netlistFile, inputSensorFile, outputDeviceFile,
                outputDir + Utils.getFileSeparator() + "rnaseq_" + filename);
        Utils.executeAndWaitForCommand(cmd);
    }

    private void generateDNAPlotLibPlots() throws CelloException {
        String outputDir = this.getRuntimeEnv().getOptionValue(PLArgString.OUTPUTDIR);
        File file = null;
		List<String> designs = DNAPlotLibUtils.getDNADesigns(this.getNetlist(), this.getTargetDataInstance());
        String designsFilename = outputDir + Utils.getFileSeparator() + "dpl_dna_designs.csv";
        file = new File(designsFilename);
        DNAPlotLibUtils.writeCSV(designs, file);
		List<String> parts = DNAPlotLibUtils.getPartInformation(this.getNetlist(), this.getTargetDataInstance());
        String partsFilename = outputDir + Utils.getFileSeparator() + "dpl_part_information.csv";
        file = new File(partsFilename);
        DNAPlotLibUtils.writeCSV(parts, file);
		List<String> reg = DNAPlotLibUtils.getRegulatoryInformation(this.getNetlist(), this.getTargetDataInstance());
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
        Path dir;
        try {
            dir = Files.createTempDirectory("cello_");
        } catch (IOException e) {
            throw new CelloException("Unable to create temporary directory.", e);
        }
        String libraryPlot;
        try {
            libraryPlot = Utils.getResourceAsString("library_plot.py");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        String libraryPlotFilename = dir.toString() + Utils.getFileSeparator() + "library_plot.py";
        Utils.writeToFile(libraryPlot, libraryPlotFilename);
        String fmt = "%s -W ignore %s -params %s -parts %s -designs %s -regulation %s -output %s";
        String output = outputDir + Utils.getFileSeparator() + this.getNetlist().getName() + "_dpl";
        String python = this.getRuntimeEnv().getOptionValue(PLArgString.PYTHONENV);
        String cmd = String.format(fmt, python, libraryPlotFilename, paramsFilename, partsFilename, designsFilename,
                regFilename, output);
        Utils.executeAndWaitForCommand(cmd + ".pdf");
        Utils.executeAndWaitForCommand(cmd + ".png");
    }

    /**
     * Perform postprocessing
     *
     * @throws CelloException
     */
    @Override
    protected void postprocessing() throws CelloException {
        logInfo("processing Eugene output");

        Placements placements = new Placements();
        this.getNetlist().getResultNetlistData().setPlacements(placements);

        EugeneArray results = this.getEugeneResults();

        if (results == null) {
            throw new CelloException("Error with Eugene results!");
        }

        for (int i = 0; i < results.getElements().size(); i++) {
            if (i >= this.getMaxPlacements())
                break;

            Placement placement = new Placement(true, false);
            placements.addPlacement(placement);

            NamedElement placementElement = null;

            // placement
            try {
                placementElement = results.getElement(i);
            } catch (EugeneException e) {
                e.printStackTrace();
            }

            if (placementElement instanceof Device) {
                Device placementDevice = (Device) placementElement;
                List<List<Device>> deviceGroups = new ArrayList<>();
                List<NamedElement> circuitElements = placementDevice.getComponentList();
                List<Device> deviceGroup = null;
                for (int j = 0; j < circuitElements.size(); j++) {
                    NamedElement circuitElement = circuitElements.get(j);
                    if (deviceGroup == null) {
                        deviceGroup = new ArrayList<>();
                        deviceGroups.add(deviceGroup);
                    }
                    if (circuitElement instanceof Device) {
                        Device device = (Device) circuitElement;
                        deviceGroup.add(device);
                    } else {
                        deviceGroup = null;
                    }
                }
                for (int j = 0; j < deviceGroups.size(); j++) {
                    PlacementGroup group = new PlacementGroup(true, false);
                    placement.addPlacementGroup(group);
                    deviceGroup = deviceGroups.get(j);
                    for (int k = 0; k < deviceGroup.size(); k++) {
                        Device componentDevice = deviceGroup.get(k);
                        String name = EugeneUtils.getDeviceBaseName(componentDevice.getName());
                        NetlistNode node = this.getDeviceNameNetlistNodeMap().get(name);
//						String o = "";
//						try {
//							// FIXME: part orientations not respected
//							o = componentDevice.getOrientations(0).toString();
//						} catch (EugeneException e) {
//							e.printStackTrace();
//						}
//
//						if (o.contains(EugeneRules.S_REVERSE)) {
//							placement.setDirection(false);
//							try {
//								Device reverse = DeviceUtils.flipAndInvert(componentDevice);
//								groupElement = reverse;
//							} catch (EugeneException e) {
//								e.printStackTrace();
//							}
//						}
                        List<String> parts = new ArrayList<>();
                        for (NamedElement part : componentDevice.getComponentList()) {
                            parts.add(part.getName());
                        }
                        Component component = new Component(parts, true, false);
                        component.setDirection(true);
                        component.setNode(node.getName());
                        component.setName(String.format("Group%d_Device%d", j, k));
                        group.addComponent(component);
                    }
                }
            }
        }
        // DNAPlotLib
        logInfo("generating dnaplotlib figures");
        this.generateDNAPlotLibPlots();
        // this.generateRNASeqPlots();
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
    public Map<NetlistNode, Collection<StructureDevice>> getDevicesMap() {
        return devicesMap;
    }

    /**
     * Setter for <i>devicesMap</i>
     *
     * @param devicesMap the value to set <i>devicesMap</i>
     */
    public void setDevicesMap(Map<NetlistNode, Collection<StructureDevice>> devicesMap) {
        this.devicesMap = devicesMap;
    }

    /**
     * @return the deviceNameNetlistNodeMap
     */
    public Map<String, NetlistNode> getDeviceNameNetlistNodeMap() {
        return deviceNameNetlistNodeMap;
    }

    /**
     * @param deviceNameNetlistNodeMap the deviceNameNetlistNodeMap to set
     */
    public void setDeviceNameNetlistNodeMap(Map<String, NetlistNode> deviceNameNetlistNodeMap) {
        this.deviceNameNetlistNodeMap = deviceNameNetlistNodeMap;
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
     * Getter for <i>targetDataInstance</i>.
     *
     * @return value of targetDataInstance
     */
    protected PLTargetDataInstance getTargetDataInstance() {
        return targetDataInstance;
    }

    /**
     * Setter for <i>targetDataInstance</i>.
     *
     * @param targetDataInstance the targetDataInstance to set
     */
    protected void setTargetDataInstance(PLTargetDataInstance targetDataInstance) {
        this.targetDataInstance = targetDataInstance;
    }

    private PLTargetDataInstance targetDataInstance;

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

    private Integer maxPlacements;
    private EugeneArray eugeneResults;
    private String eugeneScript;
    private String eugeneScriptFilename;
    private Map<NetlistNode, Collection<StructureDevice>> devicesMap;
    private Map<String, NetlistNode> deviceNameNetlistNodeMap;
    private CircuitRules circuitRules;
    private DeviceRules deviceRules;
    private CObjectCollection<GeneticLocation> geneticLocations;

    static private String S_FENCEPOST = "fencepost";
}

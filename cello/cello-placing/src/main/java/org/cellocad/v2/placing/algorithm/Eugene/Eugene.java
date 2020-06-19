/*
 * Copyright (C) 2018-2020 Boston University (BU)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package org.cellocad.v2.placing.algorithm.Eugene;

import com.fasterxml.jackson.core.JsonProcessingException;
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
import org.cellocad.v2.common.Utils;
import org.cellocad.v2.common.exception.CelloException;
import org.cellocad.v2.common.graph.algorithm.MyBFS;
import org.cellocad.v2.common.runtime.environment.ArgString;
import org.cellocad.v2.common.target.data.data.AssignableDevice;
import org.cellocad.v2.common.target.data.data.CircuitRules;
import org.cellocad.v2.common.target.data.data.DeviceRules;
import org.cellocad.v2.common.target.data.data.Gate;
import org.cellocad.v2.common.target.data.data.GeneticLocation;
import org.cellocad.v2.common.target.data.data.Input;
import org.cellocad.v2.common.target.data.data.InputSensor;
import org.cellocad.v2.common.target.data.data.Part;
import org.cellocad.v2.common.target.data.data.StructureDevice;
import org.cellocad.v2.common.target.data.data.StructureObject;
import org.cellocad.v2.common.target.data.data.StructureTemplate;
import org.cellocad.v2.placing.algorithm.PLAlgorithm;
import org.cellocad.v2.placing.algorithm.Eugene.data.EugeneNetlistData;
import org.cellocad.v2.placing.algorithm.Eugene.data.EugeneNetlistEdgeData;
import org.cellocad.v2.placing.algorithm.Eugene.data.EugeneNetlistNodeData;
import org.cellocad.v2.placing.algorithm.Eugene.target.data.EugeneTargetDataUtils;
import org.cellocad.v2.placing.algorithm.Eugene.target.data.data.EugeneDevice;
import org.cellocad.v2.placing.target.data.PLTargetDataInstance;
import org.cellocad.v2.results.common.Result;
import org.cellocad.v2.results.logicSynthesis.LSResultsUtils;
import org.cellocad.v2.results.logicSynthesis.netlist.LSResultNetlistUtils;
import org.cellocad.v2.results.netlist.Netlist;
import org.cellocad.v2.results.netlist.NetlistEdge;
import org.cellocad.v2.results.netlist.NetlistNode;
import org.cellocad.v2.results.netlist.NetlistUtils;
import org.cellocad.v2.results.netlist.data.ResultNetlistNodeData;
import org.cellocad.v2.results.placing.DnaPlotLibUtils;
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
 * The implementation of the <i>Eugene</i> algorithm in the <i>placing</i> stage.
 *
 * @author Timothy Jones
 * @date 2018-06-06
 */
public class Eugene extends PLAlgorithm {

  /**
   * Returns the {@link EugeneNetlistNodeData} of the given node.
   *
   * @param node A node within the netlist of this instance.
   * @return The {@link EugeneNetlistNodeData} instance if it exists, null otherwise.
   */
  protected EugeneNetlistNodeData getEugeneNetlistNodeData(final NetlistNode node) {
    EugeneNetlistNodeData rtn = null;
    rtn = (EugeneNetlistNodeData) node.getNetlistNodeData();
    return rtn;
  }

  /**
   * Returns the {@link EugeneNetlistEdgeData} of the given edge.
   *
   * @param edge An edge within the netlist of this instance.
   * @return The {@link EugeneNetlistEdgeData} instance if it exists, null otherwise.
   */
  protected EugeneNetlistEdgeData getEugeneNetlistEdgeData(final NetlistEdge edge) {
    EugeneNetlistEdgeData rtn = null;
    rtn = (EugeneNetlistEdgeData) edge.getNetlistEdgeData();
    return rtn;
  }

  /**
   * Returns the {@link EugeneNetlistData} of the given netlist.
   *
   * @param netlist The netlist of this instance.
   * @return The {@link EugeneNetlistData} instance if it exists, null otherwise.
   */
  protected EugeneNetlistData getEugeneNetlistData(final Netlist netlist) {
    EugeneNetlistData rtn = null;
    rtn = (EugeneNetlistData) netlist.getNetlistData();
    return rtn;
  }

  /** Gets the constraint data from the netlist constraint file. */
  @Override
  protected void getConstraintFromNetlistConstraintFile() {}

  /**
   * Gets the data from the UCF.
   *
   * @throws CelloException Unable to get data from UCF.
   */
  @Override
  protected void getDataFromUcf() throws CelloException {
    setTargetDataInstance(new PLTargetDataInstance(getTargetData()));
    try {
      setCircuitRules(EugeneTargetDataUtils.getCircuitRules(getTargetData()));
    } catch (JsonProcessingException e) {
      throw new CelloException("Unable to parse circuit rules.");
    }
    setDeviceRules(EugeneTargetDataUtils.getDeviceRules(getTargetData()));
  }

  /** Set parameter values of the algorithm. */
  @Override
  protected void setParameterValues() {
    final String outputDir = getRuntimeEnv().getOptionValue(ArgString.OUTPUTDIR);
    final String inputFilename = getNetlist().getInputFilename();
    final String filename =
        outputDir
            + Utils.getFileSeparator()
            + Utils.getFilename(inputFilename)
            + "_eugeneScript.eug";
    setEugeneScriptFilename(filename);

    Boolean present = false;

    present = getAlgorithmProfile().getIntParameter("MaxPlacements").getFirst();
    if (present) {
      setMaxPlacements(getAlgorithmProfile().getIntParameter("MaxPlacements").getSecond());
    }
  }

  /** Validate parameter values of the algorithm. */
  @Override
  protected void validateParameterValues() {
    if (getMaxPlacements() == null || getMaxPlacements() <= 0) {
      setMaxPlacements(5);
    }
  }

  private void setDevices() {
    final MyBFS<NetlistNode, NetlistEdge, Netlist> BFS = new MyBFS<>(getNetlist());
    NetlistNode node = null;
    node = BFS.getNextVertex();
    while (node != null) {
      final Collection<StructureDevice> devices =
          EugeneUtils.getDevices(
              node, getTargetDataInstance().getGates(), getTargetDataInstance().getOutputDevices());
      getDevicesMap().put(node, devices);
      for (final StructureDevice d : devices) {
        getDeviceNameNetlistNodeMap().put(d.getName(), node);
      }
      node = BFS.getNextVertex();
    }
  }

  /**
   * Get a Eugene <code>PartType</code> declaration for each part type in the circuit.
   *
   * @return The Eugene <code>PartType</code> definitions.
   */
  private Collection<String> getPartTypeDeclarations() {
    final Set<String> rtn = new HashSet<>();
    final Set<String> temp = new HashSet<>();
    for (final Collection<StructureDevice> devices : getDevicesMap().values()) {
      final Iterator<StructureDevice> it = devices.iterator();
      while (it.hasNext()) {
        final StructureDevice d = it.next();
        temp.addAll(EugeneUtils.getPartTypes(d, getTargetDataInstance().getParts()));
      }
    }
    for (int i = 0; i < getNetlist().getNumVertex(); i++) {
      final NetlistNode node = getNetlist().getVertexAtIdx(i);
      final ResultNetlistNodeData data = node.getResultNetlistNodeData();
      final String deviceName = data.getDeviceName();
      Part p = null;
      if (!LSResultsUtils.isAllInput(node) && !LSResultsUtils.isAllOutput(node)) {
        final Gate gate = getTargetDataInstance().getGates().findCObjectByName(deviceName);
        final String output = gate.getStructure().getOutputs().get(0);
        p = getTargetDataInstance().getParts().findCObjectByName(output);
      }
      if (LSResultsUtils.isAllInput(node)) {
        final InputSensor sensor =
            getTargetDataInstance().getInputSensors().findCObjectByName(deviceName);
        final String name = sensor.getStructure().getOutputs().get(0);
        p = getTargetDataInstance().getParts().findCObjectByName(name);
      }
      if (p != null) {
        temp.add(p.getPartType());
      }
    }
    final Iterator<String> it = temp.iterator();
    while (it.hasNext()) {
      final String str = EugeneUtils.getPartTypeDefinition(it.next());
      rtn.add(str);
    }
    // Just add these anyway in case of fixed placement
    final String scar = EugeneUtils.getPartTypeDefinition(Part.S_SCAR);
    rtn.add(scar);
    final String spacer = EugeneUtils.getPartTypeDefinition(Part.S_SPACER);
    rtn.add(spacer);
    final String terminator = EugeneUtils.getPartTypeDefinition(Part.S_TERMINATOR);
    rtn.add(terminator);
    return rtn;
  }

  /**
   * Get a Eugene part definition, e.g. <code>promoter pAmtR(.SEQUENCE("CTTG..."));</code>, for
   * every part in the circuit.
   *
   * @return A collection of Eugene part definitions, one for each part in the circuit.
   */
  private Collection<String> getPartDefinitions() {
    final Set<String> rtn = new HashSet<>();
    for (final Collection<StructureDevice> devices : getDevicesMap().values()) {
      final Iterator<StructureDevice> it = devices.iterator();
      while (it.hasNext()) {
        final StructureDevice d = it.next();
        rtn.addAll(EugeneUtils.getPartDefinitions(d, getTargetDataInstance().getParts()));
      }
    }
    for (int i = 0; i < getNetlist().getNumVertex(); i++) {
      final NetlistNode node = getNetlist().getVertexAtIdx(i);
      final CObjectCollection<Part> inputs = EugeneUtils.getInputs(node, getTargetDataInstance());
      for (final Part p : inputs) {
        rtn.add(EugeneUtils.getPartDefinition(p));
      }
    }
    // Just add these anyway in case of fixed placement
    for (Part part : this.getTargetDataInstance().getParts()) {
      if (part.getPartType().equals(Part.S_SCAR)
          || part.getPartType().equals(Part.S_TERMINATOR)
          || part.getPartType().equals(Part.S_SPACER)) {
        rtn.add(EugeneUtils.getPartDefinition(part));
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
    final Collection<String> rtn = new ArrayList<>();
    for (final NetlistNode node : getDevicesMap().keySet()) {
      Collection<Input> inputs = new ArrayList<>();
      Map<Input, Part> map = EugeneUtils.getInputsMap(node, this.getTargetDataInstance());
      for (int i = 0; i < node.getNumInEdge(); i++) {
        NetlistEdge e = node.getInEdgeAtIdx(i);
        Input input = e.getResultNetlistEdgeData().getInput();
        inputs.add(input);
      }
      for (final StructureDevice device : getDevicesMap().get(node)) {
        final EugeneDevice e = new EugeneDevice(device, map);
        rtn.add(e.toString());
      }
    }
    //    for (final Collection<StructureDevice> devices : getDevicesMap().values()) {
    //      final Iterator<StructureDevice> it = devices.iterator();
    //      while (it.hasNext()) {
    //        final StructureDevice d = it.next();
    //        final EugeneDevice e = new EugeneDevice(d);
    //        rtn.add(e.toString());
    //      }
    //    }
    return rtn;
  }

  private Collection<String> getLocationSpecifications() {
    final Collection<String> rtn = new ArrayList<>();
    rtn.add(EugeneUtils.getPartTypeDefinition(Eugene.S_FENCEPOST));
    for (int i = 0; i < this.getTargetDataInstance().getGeneticLocations().size(); i++) {
      final GeneticLocation l = this.getTargetDataInstance().getGeneticLocations().get(i);
      rtn.add(String.format("%s %s();", Eugene.S_FENCEPOST, l.getName()));
    }
    return rtn;
  }

  private String getDeviceRuleDefinitions(final StructureDevice device, final NetlistNode node) {
    String rtn = "";
    // all part inputs incident on node
    final CObjectCollection<Part> nodeInputs = EugeneUtils.getInputs(node, getTargetDataInstance());
    // inputs only for particular device within node
    final CObjectCollection<Part> deviceInputs = new CObjectCollection<>();
    int i = device.getIdx();
    for (final StructureObject o : device.getComponents()) {
      if (o instanceof StructureTemplate) {
        final Part input = nodeInputs.get(i);
        i++;
        deviceInputs.add(input);
      }
    }
    rtn = getDeviceRules().filter(device, deviceInputs);
    return rtn;
  }

  /**
   * For every device in the circuit, get a Eugene device rule definition.
   *
   * <p>Example:
   *
   * <pre>{@code
   * Rule A1_AmtRRules ( ON A1_AmtR:
   *     CONTAINS pTet AND
   *     ALL_FORWARD
   * );
   * }</pre>
   *
   * @return A collection of Eugene device rules.
   */
  private Collection<String> getDeviceRuleDefinitions() {
    final Collection<String> rtn = new ArrayList<>();
    for (int i = 0; i < getNetlist().getNumVertex(); i++) {
      final NetlistNode node = getNetlist().getVertexAtIdx(i);
      final Collection<StructureDevice> devices = getDevicesMap().get(node);
      int j = 0;
      for (final StructureDevice d : devices) {
        d.setIdx(j);
        for (final StructureObject o : d.getComponents()) {
          if (o instanceof StructureTemplate) {
            j++;
          }
        }
      }
      for (final StructureDevice d : devices) {
        rtn.add(this.getDeviceRuleDefinitions(d, node));
      }
    }
    return rtn;
  }

  private Collection<String> getProducts() {
    final Collection<String> rtn = new ArrayList<>();
    for (final Collection<StructureDevice> devices : getDevicesMap().values()) {
      for (final StructureDevice d : devices) {
        final String str = String.format("%s_devices = product(%s);", d.getName(), d.getName());
        rtn.add(str);
      }
    }
    return rtn;
  }

  private String getCircuitDeclaration() {
    String rtn = "";
    for (final Collection<StructureDevice> devices : getDevicesMap().values()) {
      for (final StructureDevice d : devices) {
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
    for (final Collection<StructureDevice> devices : getDevicesMap().values()) {
      for (final StructureDevice d : devices) {
        rtn += Utils.getNewLine();
        rtn += Utils.getTabCharacter();
        rtn += String.format("%s", EugeneUtils.getDeviceDeviceName(d.getName()));
        rtn += ",";
      }
    }
    for (int i = 0; i < this.getTargetDataInstance().getGeneticLocations().size(); i++) {
      final GeneticLocation l = this.getTargetDataInstance().getGeneticLocations().get(i);
      rtn += Utils.getNewLine();
      rtn += Utils.getTabCharacter();
      rtn += String.format("%s", l.getName());
      rtn += ",";
    }
    for (final String obj : this.getCircuitRules().getAcceptedFixedObjects()) {
      GeneticLocation l = this.getTargetDataInstance().getGeneticLocations().findCObjectByName(obj);
      Part p = this.getTargetDataInstance().getParts().findCObjectByName(obj);
      //      if (l != null) {
      //        rtn += Utils.getNewLine();
      //        rtn += Utils.getTabCharacter();
      //        rtn += String.format("%s", l.getName());
      //        rtn += ",";
      //      }
      if (p != null) {
        rtn += Utils.getNewLine();
        rtn += Utils.getTabCharacter();
        rtn += String.format("%s", p.getPartType());
        rtn += ",";
      }
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
    final String fmt = "for(num i%d = 0; i%d < sizeof(%s_devices); i%d = i%d + 1) {";
    for (final Collection<StructureDevice> devices : getDevicesMap().values()) {
      for (final StructureDevice d : devices) {
        j++;
        rtn += String.format(fmt, j, j, d.getName(), j, j);
        rtn += Utils.getNewLine();
      }
    }
    rtn += Utils.getNewLine();
    j = 0;
    for (final Collection<StructureDevice> devices : getDevicesMap().values()) {
      for (final StructureDevice d : devices) {
        j++;
        rtn += String.format("%sDevice = %s_devices[i%d];", d.getName(), d.getName(), j);
        rtn += Utils.getNewLine();
      }
    }
    rtn += Utils.getNewLine();
    rtn += getCircuitDefinition();
    rtn += Utils.getNewLine() + Utils.getNewLine();
    rtn += "result = permute(circuit);";
    rtn += Utils.getNewLine() + Utils.getNewLine();
    rtn += "allResults = allResults + result;";
    rtn += Utils.getNewLine() + Utils.getNewLine();
    for (final Collection<StructureDevice> devices : getDevicesMap().values()) {
      for (int i = 0; i < devices.size(); i++) {
        rtn += "}";
        rtn += Utils.getNewLine();
      }
    }
    return rtn;
  }

  private String getBlock(final Collection<String> str) {
    String rtn = "";
    rtn += String.join(Utils.getNewLine(), str);
    rtn += Utils.getNewLine() + Utils.getNewLine();
    return rtn;
  }

  protected void initEdges() {
    for (int i = 0; i < getNetlist().getNumVertex(); i++) {
      final NetlistNode node = getNetlist().getVertexAtIdx(i);
      final String name = node.getResultNetlistNodeData().getDeviceName();
      AssignableDevice device = null;
      device = this.getTargetDataInstance().getInputSensors().findCObjectByName(name);
      if (device == null) {
        device = this.getTargetDataInstance().getOutputDevices().findCObjectByName(name);
      }
      if (device == null) {
        device = this.getTargetDataInstance().getGates().findCObjectByName(name);
      }
      node.getResultNetlistNodeData().setDevice(device);
      if (node.getNumInEdge() > device.getStructure().getInputs().size()) {
        throw new RuntimeException("Device structure does not have enough inputs.");
      }
      for (int j = 0; j < node.getNumInEdge(); j++) {
        final NetlistEdge e = node.getInEdgeAtIdx(j);
        final Input input = device.getStructure().getInputs().get(j);
        e.getResultNetlistEdgeData().setInput(input);
      }
    }
  }

  /** Perform preprocessing. */
  @Override
  protected void preprocessing() {
    initEdges();
    LSResultNetlistUtils.setVertexTypeUsingLSResult(getNetlist());
    setDevicesMap(new HashMap<NetlistNode, Collection<StructureDevice>>());
    setDeviceNameNetlistNodeMap(new HashMap<String, NetlistNode>());

    // devices
    setDevices();

    // device names
    final Collection<String> deviceNames = new ArrayList<>();
    for (final Collection<StructureDevice> devices : getDevicesMap().values()) {
      for (final StructureDevice d : devices) {
        deviceNames.add(d.getName());
      }
    }
    final Collection<String> fenceposts = new ArrayList<>();
    for (int i = 0; i < this.getTargetDataInstance().getGeneticLocations().size(); i++) {
      final GeneticLocation l = this.getTargetDataInstance().getGeneticLocations().get(i);
      fenceposts.add(l.getName());
    }

    logInfo("building Eugene input script");

    String script = "";

    // part types
    script += getBlock(getPartTypeDeclarations());
    // part sequences
    script += getBlock(getPartDefinitions());
    // location specifications
    script += getBlock(getLocationSpecifications());
    // device definitions
    script += getBlock(getDeviceDefinitions());
    // device rules
    script += getBlock(this.getDeviceRuleDefinitions());
    // products
    script += getBlock(getProducts());
    // circuit declaration
    script += getCircuitDeclaration();
    // circuit rules
    script += getCircuitRules().filter(deviceNames, this.getTargetDataInstance());
    // results
    script += getResultsDefinition();

    setEugeneScript(script);
    Utils.writeToFile(script, getEugeneScriptFilename());
  }

  /**
   * Run the (core) algorithm.
   *
   * @throws CelloException Unable to run the (core) algorithm.
   */
  @Override
  protected void run() throws CelloException {
    logInfo("running Eugene");
    try {
      final org.cidarlab.eugene.Eugene eugene = new org.cidarlab.eugene.Eugene();

      final File cruft =
          new File(Utils.getWorkingDirectory() + Utils.getFileSeparator() + "exports");
      new File(cruft.getPath() + Utils.getFileSeparator() + "pigeon").delete();
      cruft.delete();

      final EugeneCollection ec = eugene.executeScript(getEugeneScript());
      setEugeneResults((EugeneArray) ec.get("allResults"));
    } catch (final EugeneException e) {
      throw new CelloException("Error with Eugene.", e);
    }
  }

  @SuppressWarnings("unused")
  private void generateRnaSeqPlots() throws CelloException {
    final String python = getRuntimeEnv().getOptionValue(ArgString.PYTHONENV);
    final String inputFilePath = getRuntimeEnv().getOptionValue(ArgString.INPUTNETLIST);
    String netlistFile = getRuntimeEnv().getOptionValue(ArgString.OUTPUTNETLIST);
    if (netlistFile == null) {
      netlistFile = "";
      netlistFile += getRuntimeEnv().getOptionValue(ArgString.OUTPUTDIR);
      netlistFile += Utils.getFileSeparator();
      netlistFile += Utils.getFilename(inputFilePath);
      netlistFile += "_outputNetlist_placing";
      netlistFile += ".json";
    }
    NetlistUtils.writeJsonForNetlist(getNetlist(), netlistFile);
    final String fmt = "%s %s -u %s -a %s -l %s -n %s -i %s -x %s -o %s";
    Path dir;
    try {
      dir = Files.createTempDirectory("cello_");
    } catch (final IOException e) {
      throw new CelloException("Unable to create temporary directory.", e);
    }
    String rnaseq;
    try {
      rnaseq = Utils.getResourceAsString("rnaseq.py");
    } catch (final IOException e) {
      throw new RuntimeException(e);
    }
    final String rnaseqFilename = dir.toString() + Utils.getFileSeparator() + "rnaseq.py";
    Utils.writeToFile(rnaseq, rnaseqFilename);
    final String userConstraintsFile =
        getRuntimeEnv().getOptionValue(ArgString.USERCONSTRAINTSFILE);
    final String inputSensorFile = getRuntimeEnv().getOptionValue(ArgString.INPUTSENSORFILE);
    final String outputDeviceFile = getRuntimeEnv().getOptionValue(ArgString.OUTPUTDEVICEFILE);
    final String outputDir = getRuntimeEnv().getOptionValue(ArgString.OUTPUTDIR);
    final String inputFilename = getNetlist().getInputFilename();
    final String filename = Utils.getFilename(inputFilename);
    final String activityFile = outputDir + Utils.getFileSeparator() + filename + "_activity.csv";
    final String logicFile = outputDir + Utils.getFileSeparator() + filename + "_logic.csv";
    final String cmd =
        String.format(
            fmt,
            python,
            rnaseqFilename,
            userConstraintsFile,
            activityFile,
            logicFile,
            netlistFile,
            inputSensorFile,
            outputDeviceFile,
            outputDir + Utils.getFileSeparator() + "rnaseq_" + filename);
    Utils.executeAndWaitForCommand(cmd);
  }

  private void generateDnaPlotLibPlots() throws CelloException {
    final String outputDir = getRuntimeEnv().getOptionValue(ArgString.OUTPUTDIR);
    File file = null;
    final List<String> designs =
        DnaPlotLibUtils.getDnaDesigns(getNetlist(), getTargetDataInstance());
    final String designsFilename = outputDir + Utils.getFileSeparator() + "dpl_dna_designs.csv";
    file = new File(designsFilename);
    DnaPlotLibUtils.writeCSV(designs, file);
    final List<String> parts =
        DnaPlotLibUtils.getPartsInformation(getNetlist(), getTargetDataInstance());
    final String partsFilename = outputDir + Utils.getFileSeparator() + "dpl_part_information.csv";
    file = new File(partsFilename);
    DnaPlotLibUtils.writeCSV(parts, file);
    final List<String> reg =
        DnaPlotLibUtils.getRegulatoryInformation(getNetlist(), getTargetDataInstance());
    final String regFilename =
        outputDir + Utils.getFileSeparator() + "dpl_regulatory_information.csv";
    file = new File(regFilename);
    DnaPlotLibUtils.writeCSV(reg, file);
    String params;
    try {
      params = Utils.getResourceAsString("plot_parameters.csv");
    } catch (final IOException e) {
      throw new RuntimeException(e);
    }
    final String paramsFilename = outputDir + Utils.getFileSeparator() + "plot_parameters.csv";
    Utils.writeToFile(params, paramsFilename);
    Path dir;
    try {
      dir = Files.createTempDirectory("cello_");
    } catch (final IOException e) {
      throw new CelloException("Unable to create temporary directory.", e);
    }
    String libraryPlot;
    try {
      libraryPlot = Utils.getResourceAsString("library_plot.py");
    } catch (final IOException e) {
      throw new RuntimeException(e);
    }
    final String libraryPlotFilename =
        dir.toString() + Utils.getFileSeparator() + "library_plot.py";
    Utils.writeToFile(libraryPlot, libraryPlotFilename);
    final String fmt = "%s -W ignore %s -params %s -parts %s -designs %s -regulation %s -output %s";
    final String output = outputDir + Utils.getFileSeparator() + getNetlist().getName() + "_dpl";
    final String python = getRuntimeEnv().getOptionValue(ArgString.PYTHONENV);
    final String cmd =
        String.format(
            fmt,
            python,
            libraryPlotFilename,
            paramsFilename,
            partsFilename,
            designsFilename,
            regFilename,
            output);

    Utils.executeAndWaitForCommand(cmd + ".pdf");
    Utils.executeAndWaitForCommand(cmd + ".png");
    File pdf = new File(output + ".pdf");
    File png = new File(output + ".png");
    Result pdfResult =
        new Result("dnaplotlib", "placing", "The sequence diagram generated by dnaplotlib.", pdf);
    Result pngResult =
        new Result("dnaplotlib", "placing", "The sequence diagram generated by dnaplotlib.", png);
    try {
      this.getResults().addResult(pdfResult);
      this.getResults().addResult(pngResult);
    } catch (IOException e) {
      throw new CelloException("Unable to write result.");
    }
  }

  /**
   * Perform postprocessing.
   *
   * @throws CelloException Unable to perform postprocessing.
   */
  @Override
  protected void postprocessing() throws CelloException {
    logInfo("processing Eugene output");

    final Placements placements = new Placements();
    getNetlist().getResultNetlistData().setPlacements(placements);

    final EugeneArray results = getEugeneResults();

    if (results == null) {
      throw new CelloException("Error with Eugene results!");
    }

    for (int i = 0; i < results.getElements().size(); i++) {
      if (i >= getMaxPlacements()) {
        break;
      }

      final Placement placement = new Placement(true, false);
      placements.addPlacement(placement);

      NamedElement placementElement = null;

      // placement
      try {
        placementElement = results.getElement(i);
      } catch (final EugeneException e) {
        e.printStackTrace();
      }

      if (placementElement instanceof Device) {
        final Device placementDevice = (Device) placementElement;
        final List<List<NamedElement>> deviceGroups = new ArrayList<>();
        final List<NamedElement> circuitElements = placementDevice.getComponentList();
        List<NamedElement> deviceGroup = null;
        for (int j = 0; j < circuitElements.size(); j++) {
          final NamedElement circuitElement = circuitElements.get(j);
          if (circuitElement.toString().contains(S_FENCEPOST)) {
            deviceGroup = new ArrayList<>();
            deviceGroups.add(deviceGroup);
          } else if (circuitElement instanceof Device
              || circuitElement.toString().contains(Part.S_SCAR) || circuitElement.toString().contains(Part.S_TERMINATOR) || circuitElement.toString().contains(Part.S_SPACER)) {
            deviceGroup.add(circuitElement);
          }
        }
        for (int j = 0; j < deviceGroups.size(); j++) {
          final PlacementGroup group = new PlacementGroup(true, false);
          placement.addPlacementGroup(group);
          deviceGroup = deviceGroups.get(j);
          List<String> parts = null;
          Component component;
          for (int k = 0; k < deviceGroup.size(); k++) {
            final NamedElement componentElement = deviceGroup.get(k);
            if (componentElement instanceof Device) {
              final Device componentDevice = (Device) componentElement;
              final String name = EugeneUtils.getDeviceBaseName(componentDevice.getName());
              final NetlistNode node = getDeviceNameNetlistNodeMap().get(name);
              parts = new ArrayList<>();
              for (final NamedElement part : componentDevice.getComponentList()) {
                parts.add(part.getName());
              }
              component = new Component(parts, true, false);
              component.setDirection(true);
              component.setNode(node.getName());
              component.setName(String.format("Group%d_Object%d", j, k));
              group.addComponent(component);
            } else if (componentElement instanceof org.cidarlab.eugene.dom.Part) {
              if (componentElement.toString().contains(Part.S_SCAR)) {
                parts = new ArrayList<>();
                parts.add(componentElement.getName());
                component = new Component(parts, true, false);
                component.setDirection(true);
                component.setNode(null);
                component.setName(String.format("Group%d_Object%d", j, k));
                group.addComponent(component);
              } else {
                if (componentElement.toString().contains(Part.S_TERMINATOR)) {
                  parts = new ArrayList<>();
                  parts.add(componentElement.getName());
                  if ((k + 1) == deviceGroup.size()) {
                    component = new Component(parts, true, false);
                    component.setDirection(true);
                    component.setNode(null);
                    component.setName(String.format("Group%d_Object%d", j, k));
                    group.addComponent(component);
                  }
                }
                if (componentElement.toString().contains(Part.S_SPACER)) {
                  parts.add(componentElement.getName());
                  component = new Component(parts, true, false);
                  component.setDirection(true);
                  component.setNode(null);
                  component.setName(String.format("Group%d_Object%d", j, k));
                  group.addComponent(component);
                }
              }
            }
          }
        }
      }
    }
    // DNAPlotLib
    logInfo("generating dnaplotlib figures");
    generateDnaPlotLibPlots();
    // this.generateRNASeqPlots();
  }

  /**
   * Returns the {@link Logger} for the <i>Eugene</i> algorithm.
   *
   * @return The {@link Logger} for the <i>Eugene</i> algorithm.
   */
  @Override
  protected Logger getLogger() {
    return Eugene.logger;
  }

  private static final Logger logger = LogManager.getLogger(Eugene.class);

  /**
   * Getter for {@code maxPlacements}.
   *
   * @return The value of {@code maxPlacements}.
   */
  protected Integer getMaxPlacements() {
    return maxPlacements;
  }

  /**
   * Setter for {@code maxPlacements}.
   *
   * @param maxPlacements The value to set {@code maxPlacements}.
   */
  protected void setMaxPlacements(final Integer maxPlacements) {
    this.maxPlacements = maxPlacements;
  }

  /**
   * Getter for {@code eugeneResults}.
   *
   * @return The value of {@code eugeneResults}.
   */
  protected EugeneArray getEugeneResults() {
    return eugeneResults;
  }

  /**
   * Setter for {@code eugeneResults}.
   *
   * @param eugeneResults The value to set {@code eugeneResults}.
   */
  protected void setEugeneResults(final EugeneArray eugeneResults) {
    this.eugeneResults = eugeneResults;
  }

  /**
   * Getter for {@code eugeneScript}.
   *
   * @return The value of {@code eugeneScript}.
   */
  protected String getEugeneScript() {
    return eugeneScript;
  }

  /**
   * Setter for {@code eugeneScript}.
   *
   * @param eugeneScript The value to set {@code eugeneScript}.
   */
  protected void setEugeneScript(final String eugeneScript) {
    this.eugeneScript = eugeneScript;
  }

  /**
   * Getter for {@code eugeneScriptFilename}.
   *
   * @return The value of {@code eugeneScriptFilename}.
   */
  protected String getEugeneScriptFilename() {
    return eugeneScriptFilename;
  }

  /**
   * Getter for {@code devicesMap}.
   *
   * @return The value of {@code devicesMap}.
   */
  public Map<NetlistNode, Collection<StructureDevice>> getDevicesMap() {
    return devicesMap;
  }

  /**
   * Setter for {@code devicesMap}.
   *
   * @param devicesMap The value to set {@code devicesMap}.
   */
  public void setDevicesMap(final Map<NetlistNode, Collection<StructureDevice>> devicesMap) {
    this.devicesMap = devicesMap;
  }

  /**
   * Getter for {@code deviceNameNetlistNodeMap}.
   *
   * @return The value of {@code deviceNameNetlistNodeMap}.
   */
  public Map<String, NetlistNode> getDeviceNameNetlistNodeMap() {
    return deviceNameNetlistNodeMap;
  }

  /**
   * Setter for {@code deviceNameNetlistNodeMap}.
   *
   * @param deviceNameNetlistNodeMap The value to set {@code deviceNameNetlistNodeMap}.
   */
  public void setDeviceNameNetlistNodeMap(final Map<String, NetlistNode> deviceNameNetlistNodeMap) {
    this.deviceNameNetlistNodeMap = deviceNameNetlistNodeMap;
  }

  /**
   * Setter for {@code eugeneScriptFilename}.
   *
   * @param eugeneScriptFilename The value to set {@code eugeneScriptFilename}.
   */
  protected void setEugeneScriptFilename(final String eugeneScriptFilename) {
    this.eugeneScriptFilename = eugeneScriptFilename;
  }

  /**
   * Getter for {@code targetDataInstance}.
   *
   * @return The value of {@code targetDataInstance}.
   */
  protected PLTargetDataInstance getTargetDataInstance() {
    return targetDataInstance;
  }

  /**
   * Setter for {@code targetDataInstance}.
   *
   * @param targetDataInstance The targetDataInstance to set.
   */
  protected void setTargetDataInstance(final PLTargetDataInstance targetDataInstance) {
    this.targetDataInstance = targetDataInstance;
  }

  private PLTargetDataInstance targetDataInstance;

  /**
   * Getter for {@code circuitRules}.
   *
   * @return The value of {@code circuitRules}.
   */
  public CircuitRules getCircuitRules() {
    return circuitRules;
  }

  /**
   * Setter for {@code circuitRules}.
   *
   * @param circuitRules The value to set {@code circuitRules}.
   */
  public void setCircuitRules(final CircuitRules circuitRules) {
    this.circuitRules = circuitRules;
  }

  /**
   * Getter for {@code deviceRules}.
   *
   * @return The value of {@code deviceRules}.
   */
  public DeviceRules getDeviceRules() {
    return deviceRules;
  }

  /**
   * Setter for {@code deviceRules}.
   *
   * @param deviceRules The value to set {@code deviceRules}.
   */
  public void setDeviceRules(final DeviceRules deviceRules) {
    this.deviceRules = deviceRules;
  }

  private Integer maxPlacements;
  private EugeneArray eugeneResults;
  private String eugeneScript;
  private String eugeneScriptFilename;
  private Map<NetlistNode, Collection<StructureDevice>> devicesMap;
  private Map<String, NetlistNode> deviceNameNetlistNodeMap;
  private CircuitRules circuitRules;
  private DeviceRules deviceRules;

  private static String S_FENCEPOST = "fencepost";
}

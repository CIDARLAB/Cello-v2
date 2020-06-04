/*
 * Copyright (C) 2017-2020 Massachusetts Institute of Technology (MIT), Boston University (BU)
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

package org.cellocad.v2.technologyMapping.algorithm.SimulatedAnnealing;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cellocad.v2.common.CObjectCollection;
import org.cellocad.v2.common.Utils;
import org.cellocad.v2.common.exception.CelloException;
import org.cellocad.v2.common.netlistConstraint.data.NetlistConstraint;
import org.cellocad.v2.common.runtime.environment.ArgString;
import org.cellocad.v2.common.target.data.data.AssignableDevice;
import org.cellocad.v2.common.target.data.data.Gate;
import org.cellocad.v2.common.target.data.data.Input;
import org.cellocad.v2.common.target.data.data.InputSensor;
import org.cellocad.v2.common.target.data.data.OutputDevice;
import org.cellocad.v2.results.common.Result;
import org.cellocad.v2.results.logicSynthesis.LSResultsUtils;
import org.cellocad.v2.results.logicSynthesis.logic.LSLogicEvaluation;
import org.cellocad.v2.results.logicSynthesis.netlist.LSResultNetlistUtils;
import org.cellocad.v2.results.netlist.Netlist;
import org.cellocad.v2.results.netlist.NetlistEdge;
import org.cellocad.v2.results.netlist.NetlistNode;
import org.cellocad.v2.results.netlist.data.ResultNetlistNodeData;
import org.cellocad.v2.results.technologyMapping.CytometryPlotUtils;
import org.cellocad.v2.results.technologyMapping.ResponsePlotUtils;
import org.cellocad.v2.results.technologyMapping.TMResultsUtils;
import org.cellocad.v2.results.technologyMapping.activity.TMActivityEvaluation;
import org.cellocad.v2.results.technologyMapping.cytometry.TMCytometryEvaluation;
import org.cellocad.v2.technologyMapping.algorithm.TMAlgorithm;
import org.cellocad.v2.technologyMapping.algorithm.SimulatedAnnealing.data.assignment.GateManager;
import org.cellocad.v2.technologyMapping.algorithm.SimulatedAnnealing.data.score.ScoreUtils;
import org.cellocad.v2.technologyMapping.algorithm.SimulatedAnnealing.data.toxicity.TMToxicityEvaluation;
import org.cellocad.v2.technologyMapping.algorithm.SimulatedAnnealing.results.SimulatedAnnealingResultsUtils;
import org.cellocad.v2.technologyMapping.target.data.TMTargetDataInstance;
import org.json.simple.JSONObject;

/**
 * The implementation of the <i>SimulatedAnnealing</i> algorithm in the <i>technologyMapping</i>
 * stage.
 *
 * @author Vincent Mirian
 * @author Timothy Jones
 * @date 2018-05-21
 */
public class SimulatedAnnealing extends TMAlgorithm {

  /** Gets the constraint data from the netlist constraint file. */
  @Override
  protected void getConstraintFromNetlistConstraintFile() {
    final NetlistConstraint constraint = getNetlistConstraint();
    String type = "";
    // input constraints
    type = "input_constraints";
    final Map<String, String> inputMap = new HashMap<>();
    for (int i = 0; i < constraint.getNumJsonObject(type); i++) {
      final JSONObject jObj = constraint.getJsonObjectAtIdx(type, i);
      final JSONObject map = (JSONObject) jObj.get("sensor_map");
      for (final Object obj : map.keySet()) {
        final String key = (String) obj;
        final String value = (String) map.get(obj);
        inputMap.put(key, value);
      }
    }
    setInputMap(inputMap);
    // ouptut constraints
    type = "output_constraints";
    final Map<String, String> outputMap = new HashMap<>();
    for (int i = 0; i < constraint.getNumJsonObject(type); i++) {
      final JSONObject jObj = constraint.getJsonObjectAtIdx(type, i);
      final JSONObject map = (JSONObject) jObj.get("reporter_map");
      for (final Object obj : map.keySet()) {
        final String key = (String) obj;
        final String value = (String) map.get(obj);
        outputMap.put(key, value);
      }
    }
    setOutputMap(outputMap);
  }

  /**
   * Gets the data from the UCF.
   *
   * @throws CelloException Unable to get data from the UCF.
   */
  @Override
  protected void getDataFromUcf() throws CelloException {
    final TMTargetDataInstance tdi = new TMTargetDataInstance(getTargetData());
    setTargetDataInstance(tdi);
  }

  /** Set parameter values of the algorithm. */
  @Override
  protected void setParameterValues() {}

  /** Validate parameter values of the algorithm. */
  @Override
  protected void validateParameterValues() {}

  /** Perform preprocessing. */
  protected void setTruthTable() {
    LSResultNetlistUtils.setVertexTypeUsingLSResult(getNetlist());
    setLSLogicEvaluation(new LSLogicEvaluation(getNetlist()));
    logInfo(getLSLogicEvaluation().toString());
  }

  protected void assignInputNodes() {
    // assign input
    final CObjectCollection<NetlistNode> inputNodes =
        LSResultsUtils.getPrimaryInputNodes(getNetlist());
    final Map<String, String> inputMap = getInputMap();
    final Iterator<InputSensor> it = getTargetDataInstance().getInputSensors().iterator();
    for (int i = 0; i < inputNodes.size(); i++) {
      final NetlistNode node = inputNodes.get(i);
      final ResultNetlistNodeData data = node.getResultNetlistNodeData();
      if (!it.hasNext()) {
        throw new RuntimeException(
            "Not enough input sensors in the library to cover the netlist inputs.");
      }
      InputSensor sensor = null;
      if (inputMap.containsKey(node.getName())) {
        final String value = inputMap.get(node.getName());
        sensor = getTargetDataInstance().getInputSensors().findCObjectByName(value);
      }
      while (sensor == null) {
        final InputSensor temp = it.next();
        if (!inputMap.containsValue(temp.getName())) {
          sensor = temp;
        }
      }
      data.setDevice(sensor);
    }
  }

  protected void assignOutputNodes() {
    // assign output
    final CObjectCollection<NetlistNode> outputNodes =
        LSResultsUtils.getPrimaryOutputNodes(getNetlist());
    final Map<String, String> outputMap = getOutputMap();
    final Iterator<OutputDevice> it = getTargetDataInstance().getOutputDevices().iterator();
    for (int i = 0; i < outputNodes.size(); i++) {
      final NetlistNode node = outputNodes.get(i);
      final ResultNetlistNodeData data = node.getResultNetlistNodeData();
      if (!it.hasNext()) {
        throw new RuntimeException(
            "Not enough output reporters in the library to cover the netlist outputs.");
      }
      OutputDevice reporter = null;
      if (outputMap.containsKey(node.getName())) {
        final String value = outputMap.get(node.getName());
        reporter = getTargetDataInstance().getOutputDevices().findCObjectByName(value);
      }
      while (reporter == null) {
        final OutputDevice temp = it.next();
        if (!outputMap.containsValue(temp.getName())) {
          reporter = temp;
        }
      }
      data.setDevice(reporter);
    }
  }

  protected void assignNodes() {
    // assign random gates
    final GateManager GM = getGateManager();
    final Netlist netlist = getNetlist();
    for (int i = 0; i < netlist.getNumVertex(); i++) {
      final NetlistNode node = netlist.getVertexAtIdx(i);
      if (LSResultsUtils.isPrimary(node) || LSResultsUtils.isInputOutput(node)) {
        continue;
      }
      final ResultNetlistNodeData data = node.getResultNetlistNodeData();
      final Gate gate = GM.getRandomGateFromUnassignedGroup();
      if (gate == null) {
        throw new RuntimeException("Gate assignment error!");
      }
      data.setDevice(gate);
      GM.setAssignedGate(gate);
    }
  }

  @Override
  protected void preprocessing() throws CelloException {
    random = new Random(SimulatedAnnealing.L_SEED);
    // GateManager
    setGateManager(new GateManager(getTargetDataInstance().getGates()));
    // truth table
    setTruthTable();
  }

  private void swap(final NetlistNode nA, final Gate gA, final NetlistNode nB, final Gate gB) {
    if (nA == null && nB != null) {
      getGateManager().setUnassignedGate(gB);
      nB.getResultNetlistNodeData().setDevice(gA);
      getGateManager().setAssignedGate(gA);
    }
    if (nA != null && nB == null) {
      getGateManager().setUnassignedGate(gA);
      nA.getResultNetlistNodeData().setDevice(gB);
      getGateManager().setAssignedGate(gB);
    }
    if (nA != null && nB != null) {
      nA.getResultNetlistNodeData().setDevice(gB);
      nB.getResultNetlistNodeData().setDevice(gA);
    }
  }

  private NetlistNode getRandomNode() {
    NetlistNode rtn = null;
    while (rtn == null) {
      final int rand = random(0, getNetlist().getNumVertex() - 1);
      final NetlistNode temp = getNetlist().getVertexAtIdx(rand);
      if (!LSResultsUtils.isAllOutput(temp) && !LSResultsUtils.isAllInput(temp)) {
        rtn = temp;
      }
    }
    return rtn;
  }

  /**
   * Run the (core) algorithm.
   *
   * @throws CelloException Unable to run the (core) algorithm.
   */
  @Override
  protected void run() throws CelloException {
    logDebug("Running the (core) algorithm.");

    // input node assignment
    assignInputNodes();
    // output node assignment
    assignOutputNodes();
    // logic node assignment
    assignNodes();
    updateNetlist();

    setTMActivityEvaluation(new TMActivityEvaluation(getNetlist(), getLSLogicEvaluation()));
    setTMToxicityEvaluation(new TMToxicityEvaluation(getNetlist(), getTMActivityEvaluation()));

    // evaluate
    for (int j = 0; j < STEPS + T0_STEPS; ++j) {
      final Double logTemperature = LOGMAX - j * LOGINC;
      Double temperature = Math.pow(10, logTemperature);

      if (j >= STEPS) {
        temperature = 0.0;
      }

      final Double before =
          ScoreUtils.score(getNetlist(), getLSLogicEvaluation(), getTMActivityEvaluation());

      NetlistNode nodeA = null;
      Gate gateA = getGateManager().getRandomGateFromUnassignedGroup();
      if (gateA == null) {
        nodeA = getRandomNode();
        gateA = (Gate) nodeA.getResultNetlistNodeData().getDevice();
      }
      NetlistNode nodeB = null;
      Gate gateB = null;
      do {
        nodeB = getRandomNode();
      } while (nodeB == nodeA);
      gateB = (Gate) nodeB.getResultNetlistNodeData().getDevice();

      swap(nodeA, gateA, nodeB, gateB);

      // evaluate
      final TMActivityEvaluation tmae =
          new TMActivityEvaluation(getNetlist(), getLSLogicEvaluation());
      final Double after = ScoreUtils.score(getNetlist(), getLSLogicEvaluation(), tmae);

      // toxicity
      final TMToxicityEvaluation tmte =
          new TMToxicityEvaluation(getNetlist(), getTMActivityEvaluation());
      if (getTMToxicityEvaluation().getMinimumGrowth() < SimulatedAnnealing.D_GROWTH_THRESHOLD) {
        if (tmte.getMinimumGrowth() > getTMToxicityEvaluation().getMinimumGrowth()) {
          setTMToxicityEvaluation(tmte);
          setTMActivityEvaluation(tmae);
          continue;
        } else {
          // undo
          swap(nodeA, gateB, nodeB, gateA);
          continue;
        }
      } else if (tmte.getMinimumGrowth() < SimulatedAnnealing.D_GROWTH_THRESHOLD) {
        // undo
        swap(nodeA, gateB, nodeB, gateA);
        continue;
      }

      // accept or reject
      final Double probability = Math.exp((after - before) / temperature); // e^b
      final Double ep = Math.random();

      if (ep < probability) {
        // accept
        setTMToxicityEvaluation(tmte);
        setTMActivityEvaluation(tmae);
      } else {
        // undo
        swap(nodeA, gateB, nodeB, gateA);
      }
    }
  }

  /** Copy the gate assignements to the netlist. */
  protected void updateNetlist() {
    for (int i = 0; i < getNetlist().getNumVertex(); i++) {
      final NetlistNode node = getNetlist().getVertexAtIdx(i);
      final AssignableDevice device = node.getResultNetlistNodeData().getDevice();
      if (device != null) {
        node.getResultNetlistNodeData().setDeviceName(device.getName());
      }
      final int num = node.getNumInEdge();
      if (node.getNumInEdge() > device.getStructure().getInputs().size()) {
        throw new RuntimeException("Device structure does not have enough inputs.");
      }
      for (int j = 0; j < num; j++) {
        final NetlistEdge e = node.getInEdgeAtIdx(j);
        final Input input = device.getStructure().getInputs().get(j);
        e.getResultNetlistEdgeData().setInput(input);
      }
    }
  }

  protected void writeLogicResult(final String outputFile) throws CelloException {
    File logicFile;
    try {
      logicFile =
          LSResultsUtils.writeCsvForLSLogicEvaluation(
              getLSLogicEvaluation(), outputFile + "_logic.csv");
    } catch (IOException e) {
      throw new CelloException("Unable to write CSV file for truth table.", e);
    }
    Result logicResult =
        new Result("logic", "technologyMapping", "The truth table for the circuit.", logicFile);
    try {
      this.getResults().addResult(logicResult);
    } catch (IOException e) {
      throw new CelloException("Unable to write metadata for truth table result.");
    }
  }

  protected void writeToxicityResult(final String outputFile) throws CelloException {
    File toxicityFile;
    try {
      toxicityFile =
          SimulatedAnnealingResultsUtils.writeCsvForTMToxicityEvaluation(
              getTMToxicityEvaluation(), outputFile + "_toxicity.csv");
    } catch (IOException e) {
      throw new CelloException("Unable to write CSV file for toxicity.", e);
    }
    Result toxicityResult =
        new Result("toxicity", "technologyMapping", "The toxicity for the circuit.", toxicityFile);
    try {
      this.getResults().addResult(toxicityResult);
    } catch (IOException e) {
      throw new CelloException("Unable to write metadata for toxicity result.");
    }
  }

  protected void writeActivityResult(final String outputFile) throws CelloException {
    File activityFile;
    try {
      activityFile =
          TMResultsUtils.writeCsvForTMActivityEvaluation(
              getTMActivityEvaluation(), outputFile + "_activity.csv");
    } catch (IOException e) {
      throw new CelloException("Unable to write CSV file for activity.", e);
    }
    Result activityResult =
        new Result("activity", "technologyMapping", "The activity for the circuit.", activityFile);
    try {
      this.getResults().addResult(activityResult);
    } catch (IOException e) {
      throw new CelloException("Unable to write metadata for activity result.");
    }
  }

  /**
   * Perform postprocessing.
   *
   * @throws CelloException Unable to perform postprocessing.
   */
  @Override
  protected void postprocessing() throws CelloException {
    updateNetlist();
    final String inputFilename = getNetlist().getInputFilename();
    final String filename = Utils.getFilename(inputFilename);
    final String outputDir = getRuntimeEnv().getOptionValue(ArgString.OUTPUTDIR);
    final String outputFile = outputDir + Utils.getFileSeparator() + filename;
    // logic
    writeLogicResult(outputFile);
    // cytometry
    setTMCytometryEvaluation(new TMCytometryEvaluation());
    // toxicity
    setTMToxicityEvaluation(new TMToxicityEvaluation(getNetlist(), getTMActivityEvaluation()));
    writeToxicityResult(outputFile);
    logInfo(getTMToxicityEvaluation().toString());
    // activity
    writeActivityResult(outputFile);
    logInfo(getTMActivityEvaluation().toString());
    for (int i = 0; i < getNetlist().getNumVertex(); i++) {
      final NetlistNode node = getNetlist().getVertexAtIdx(i);
      final AssignableDevice gate = node.getResultNetlistNodeData().getDevice();
      if (gate != null) {
        String str = "";
        str += String.format("Node: %-5s", node.getName());
        str += Utils.getTabCharacter();
        str += String.format("Type: %-10s", node.getResultNetlistNodeData().getNodeType());
        str += Utils.getTabCharacter();
        str += String.format("Gate: %-10s", gate.getName());
        str += Utils.getTabCharacter();
        logInfo(str);
      }
    }
    logInfo(
        String.format(
            "Score: %.2f",
            ScoreUtils.score(getNetlist(), getLSLogicEvaluation(), getTMActivityEvaluation())));
    // plots
    logInfo("Generating plots");
    ResponsePlotUtils.generatePlots(
        getNetlist(),
        getLSLogicEvaluation(),
        getTMActivityEvaluation(),
        getRuntimeEnv(),
        this.getResults());
    CytometryPlotUtils.generatePlots(
        getNetlist(),
        getLSLogicEvaluation(),
        getTMActivityEvaluation(),
        getRuntimeEnv(),
        this.getResults());
  }

  /**
   * Returns the {@link Logger} for the <i>SimulatedAnnealing</i> algorithm.
   *
   * @return The {@link Logger} for the <i>SimulatedAnnealing</i> algorithm.
   */
  @Override
  protected Logger getLogger() {
    return SimulatedAnnealing.logger;
  }

  private static final Logger logger = LogManager.getLogger(SimulatedAnnealing.class);

  /**
   * Getter for {@code targetDataInstance}.
   *
   * @return The value of {@code targetDataInstance}.
   */
  protected TMTargetDataInstance getTargetDataInstance() {
    return targetDataInstance;
  }

  /**
   * Setter for {@code targetDataInstance}.
   *
   * @param targetDataInstance The targetDataInstance to set.
   */
  protected void setTargetDataInstance(final TMTargetDataInstance targetDataInstance) {
    this.targetDataInstance = targetDataInstance;
  }

  private TMTargetDataInstance targetDataInstance;

  /**
   * Getter for {@code inputMap}.
   *
   * @return The value of {@code inputMap}.
   */
  protected Map<String, String> getInputMap() {
    return inputMap;
  }

  /**
   * Setter for {@code inputMap}.
   *
   * @param inputMap The value to set {@code inputMap}.
   */
  protected void setInputMap(final Map<String, String> inputMap) {
    this.inputMap = inputMap;
  }

  private Map<String, String> inputMap;

  /**
   * Getter for {@code outputMap}.
   *
   * @return The value of {@code outputMap}.
   */
  protected Map<String, String> getOutputMap() {
    return outputMap;
  }

  /**
   * Setter for {@code outputMap}.
   *
   * @param outputMap The value to set {@code outputMap}.
   */
  protected void setOutputMap(final Map<String, String> outputMap) {
    this.outputMap = outputMap;
  }

  private Map<String, String> outputMap;

  /**
   * Setter for {@code unitConversion}.
   *
   * @param unitConversion The value to set {@code unitConversion}.
   */
  protected void setUnitConversion(final Double unitConversion) {
    this.unitConversion = unitConversion;
  }

  /**
   * Getter for {@code unitConversion}.
   *
   * @return The value of {@code unitConversion}.
   */
  public Double getUnitConversion() {
    return unitConversion;
  }

  private Double unitConversion;

  /*
   * GateManager
   */
  protected void setGateManager(final GateManager gateManager) {
    this.gateManager = gateManager;
  }

  public GateManager getGateManager() {
    return gateManager;
  }

  private GateManager gateManager;

  /*
   * LSLogicEvaluation
   */
  protected void setLSLogicEvaluation(final LSLogicEvaluation lsle) {
    this.lsle = lsle;
  }

  public LSLogicEvaluation getLSLogicEvaluation() {
    return lsle;
  }

  private LSLogicEvaluation lsle;

  /*
   * TMActivityEvaluation
   */
  /**
   * Getter for {@code tmae}.
   *
   * @return The value of {@code tmae}.
   */
  public TMActivityEvaluation getTMActivityEvaluation() {
    return tmae;
  }

  /**
   * Setter for {@code tmte}.
   *
   * @param tmte The value to set {@code tmte}.
   */
  protected void setTMToxicityEvaluation(final TMToxicityEvaluation tmte) {
    this.tmte = tmte;
  }

  private TMToxicityEvaluation tmte;

  /*
   * TMToxicityEvaluation
   */
  /**
   * Setter for {@code tmae}.
   *
   * @param tmae The value to set {@code tmae}.
   */
  protected void setTMActivityEvaluation(final TMActivityEvaluation tmae) {
    this.tmae = tmae;
  }

  /**
   * Getter for {@code tmte}.
   *
   * @return The value of {@code tmte}.
   */
  public TMToxicityEvaluation getTMToxicityEvaluation() {
    return tmte;
  }

  private TMActivityEvaluation tmae;

  /**
   * Getter for {@code tmce}.
   *
   * @return The value of {@code tmce}.
   */
  public TMCytometryEvaluation getTMCytometryEvaluation() {
    return tmce;
  }

  /**
   * Setter for {@code tmce}.
   *
   * @param tmce The value to set {@code tmce}.
   */
  protected void setTMCytometryEvaluation(final TMCytometryEvaluation tmce) {
    this.tmce = tmce;
  }

  private TMCytometryEvaluation tmce;

  /*
   * Random
   */
  private int random(final int min, final int max) {
    int rtn = 0;
    final Random random = getRandom();
    rtn = random.nextInt(max - min + 1) + min;
    return rtn;
  }

  private Random getRandom() {
    return random;
  }

  private Random random;
  private static long L_SEED = 21;
  private static final double D_GROWTH_THRESHOLD = 0.75;

  private static final Double MAXTEMP = 100.0;
  private static final Double MINTEMP = 0.001;
  private static final Integer STEPS = 500;
  private static final Double LOGMAX = Math.log10(MAXTEMP);
  private static final Double LOGMIN = Math.log10(MINTEMP);
  private static final Double LOGINC = (LOGMAX - LOGMIN) / STEPS;
  private static final Integer T0_STEPS = 100;
}

/**
 * Copyright (C) 2017-2018
 * Massachusetts Institute of Technology (MIT)
 * Boston University (BU)
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
package org.cellocad.cello2.technologyMapping.algorithm.SimulatedAnnealing;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cellocad.cello2.common.CObjectCollection;
import org.cellocad.cello2.common.CelloException;
import org.cellocad.cello2.common.Utils;
import org.cellocad.cello2.common.netlistConstraint.data.NetlistConstraint;
import org.cellocad.cello2.common.runtime.environment.RuntimeEnv;
import org.cellocad.cello2.results.logicSynthesis.LSResultsUtils;
import org.cellocad.cello2.results.logicSynthesis.logic.LSLogicEvaluation;
import org.cellocad.cello2.results.logicSynthesis.netlist.LSResultNetlistUtils;
import org.cellocad.cello2.results.netlist.Netlist;
import org.cellocad.cello2.results.netlist.NetlistEdge;
import org.cellocad.cello2.results.netlist.NetlistNode;
import org.cellocad.cello2.results.technologyMapping.TMResultsUtils;
import org.cellocad.cello2.results.technologyMapping.activity.TMActivityEvaluation;
import org.cellocad.cello2.results.technologyMapping.activity.signal.SensorSignals;
import org.cellocad.cello2.results.technologyMapping.cytometry.TMCytometryEvaluation;
import org.cellocad.cello2.technologyMapping.algorithm.TMAlgorithm;
import org.cellocad.cello2.technologyMapping.algorithm.SimulatedAnnealing.data.SimulatedAnnealingDataUtils;
import org.cellocad.cello2.technologyMapping.algorithm.SimulatedAnnealing.data.SimulatedAnnealingNetlistNodeData;
import org.cellocad.cello2.technologyMapping.algorithm.SimulatedAnnealing.data.assignment.GateManager;
import org.cellocad.cello2.technologyMapping.algorithm.SimulatedAnnealing.data.evaluation.Evaluator;
import org.cellocad.cello2.technologyMapping.algorithm.SimulatedAnnealing.data.evaluation.TandemEvaluator;
import org.cellocad.cello2.technologyMapping.algorithm.SimulatedAnnealing.data.score.ScoreUtils;
import org.cellocad.cello2.technologyMapping.algorithm.SimulatedAnnealing.data.toxicity.TMToxicityEvaluation;
import org.cellocad.cello2.technologyMapping.algorithm.SimulatedAnnealing.data.ucf.Assignable;
import org.cellocad.cello2.technologyMapping.algorithm.SimulatedAnnealing.data.ucf.CasetteParts;
import org.cellocad.cello2.technologyMapping.algorithm.SimulatedAnnealing.data.ucf.Gate;
import org.cellocad.cello2.technologyMapping.algorithm.SimulatedAnnealing.data.ucf.InputSensor;
import org.cellocad.cello2.technologyMapping.algorithm.SimulatedAnnealing.data.ucf.OutputDevice;
import org.cellocad.cello2.technologyMapping.algorithm.SimulatedAnnealing.data.ucf.Parameter;
import org.cellocad.cello2.technologyMapping.algorithm.SimulatedAnnealing.data.ucf.ResponseFunctionVariable;
import org.cellocad.cello2.technologyMapping.algorithm.SimulatedAnnealing.results.ResponsePlots;
import org.cellocad.cello2.technologyMapping.algorithm.SimulatedAnnealing.results.SimulatedAnnealingResultsUtils;
import org.cellocad.cello2.technologyMapping.runtime.environment.TMArgString;
import org.json.simple.JSONObject;

/**
 * The SimulatedAnnealing class implements the <i>SimulatedAnnealing</i>
 * algorithm in the <i>technologyMapping</i> stage.
 *
 * @author Vincent Mirian
 * @author Timothy Jones
 *
 * @date 2018-05-21
 *
 */
public class SimulatedAnnealing extends TMAlgorithm {

	/**
	 *  Gets the Constraint data from the NetlistConstraintFile
	 */
	@Override
	protected void getConstraintFromNetlistConstraintFile() {
		NetlistConstraint constraint = this.getNetlistConstraint();
		String type = "";
		// input constraints
		type = "input_constraints";
		Map<String, String> inputMap = new HashMap<>();
		for (int i = 0; i < constraint.getNumJSONObject(type); i++) {
			JSONObject jObj = constraint.getJSONObjectAtIdx(type, i);
			JSONObject map = (JSONObject) jObj.get("sensor_map");
			for (Object obj : map.keySet()) {
				String key = (String) obj;
				String value = (String) map.get(obj);
				inputMap.put(key, value);
			}
		}
		this.setInputMap(inputMap);
		// ouptut constraints
		type = "output_constraints";
		Map<String, String> outputMap = new HashMap<>();
		for (int i = 0; i < constraint.getNumJSONObject(type); i++) {
			JSONObject jObj = constraint.getJSONObjectAtIdx(type, i);
			JSONObject map = (JSONObject) jObj.get("reporter_map");
			for (Object obj : map.keySet()) {
				String key = (String) obj;
				String value = (String) map.get(obj);
				outputMap.put(key, value);
			}
		}
		this.setOutputMap(outputMap);
	}

	/**
	 * Gets the data from the UCF
	 */
	@Override
	protected void getDataFromUCF() {
		this.setGates(SimulatedAnnealingDataUtils.getGates(this.getTargetData()));
		this.setInputSensors(SimulatedAnnealingDataUtils.getInputSensors(this.getTargetData()));
		this.setOutputReporters(SimulatedAnnealingDataUtils.getOutputDevices(this.getTargetData()));
		this.setUnitConversion(SimulatedAnnealingDataUtils.getUnitConversion(this.getTargetData()));
	}

	/**
	 * Set parameter(s) value(s) of the algorithm
	 */
	@Override
	protected void setParameterValues() {
	}

	/**
	 * Validate parameter value of the algorithm
	 */
	@Override
	protected void validateParameterValues() {
		this.setComputeTandemEfficiency(
				this.getAlgorithmProfile().getBooleanParameter("ComputeTandemEfficiency").getSecond());
	}

	/**
	 * Perform preprocessing
	 */
	protected void setTruthTable() {
		LSResultNetlistUtils.setVertexTypeUsingLSResult(this.getNetlist());
		this.setLSLogicEvaluation(new LSLogicEvaluation(this.getNetlist()));
		this.logInfo(this.getLSLogicEvaluation().toString());
	}

	protected void assignInputNodes() {
		// assign input
		CObjectCollection<NetlistNode> inputNodes = LSResultsUtils.getPrimaryInputNodes(this.getNetlist());
		Map<String, String> inputMap = this.getInputMap();
		Iterator<InputSensor> it = this.getInputSensors().iterator();
		for (int i = 0; i < inputNodes.size(); i++) {
			NetlistNode node = inputNodes.get(i);
			SimulatedAnnealingNetlistNodeData data = SimulatedAnnealingUtils.getSimulatedAnnealingNetlistNodeData(node);
			if (!it.hasNext()) {
				throw new RuntimeException("Not enough input sensors in the library to cover the netlist inputs.");
			}
			InputSensor sensor = null;
			if (inputMap.containsKey(node.getName())) {
				String value = inputMap.get(node.getName());
				sensor = this.getInputSensors().findCObjectByName(value);
			}
			while (sensor == null) {
				InputSensor temp = it.next();
				if (!inputMap.containsValue(temp.getName())) {
					sensor = temp;
				}
			}
			data.setGate(sensor);
		}
	}

	protected void assignOutputNodes() {
		// assign output
		CObjectCollection<NetlistNode> outputNodes = LSResultsUtils.getPrimaryOutputNodes(this.getNetlist());
		Map<String,String> outputMap = this.getOutputMap();
		Iterator<OutputDevice> it = this.getOutputReporters().iterator();
		for (int i = 0; i < outputNodes.size(); i++) {
			NetlistNode node = outputNodes.get(i);
			SimulatedAnnealingNetlistNodeData data = SimulatedAnnealingUtils.getSimulatedAnnealingNetlistNodeData(node);
			if (!it.hasNext()) {
				throw new RuntimeException("Not enough output reporters in the library to cover the netlist outputs.");
			}
			OutputDevice reporter = null;
			if (outputMap.containsKey(node.getName())) {
				String value = outputMap.get(node.getName());
				reporter = this.getOutputReporters().findCObjectByName(value);
			}
			while (reporter == null) {
				OutputDevice temp = it.next();
				if (!outputMap.containsValue(temp.getName())) {
					reporter = temp;
				}
			}
			data.setGate(reporter);
		}
	}

	protected void assignNodes() {
		// assign random gates
		GateManager GM = this.getGateManager();
		Netlist netlist = this.getNetlist();
		for (int i = 0; i < netlist.getNumVertex(); i++) {
			NetlistNode node = netlist.getVertexAtIdx(i);
			if (LSResultsUtils.isPrimary(node) || LSResultsUtils.isInputOutput(node)) {
				continue;
			}
			SimulatedAnnealingNetlistNodeData data = SimulatedAnnealingUtils.getSimulatedAnnealingNetlistNodeData(node);
			Gate gate = GM.getRandomGateFromUnassignedGroup();
			if (gate == null) {
				throw new RuntimeException("Gate assignment error!");
			}
			data.setGate(gate);
			GM.setAssignedGate(gate);
		}
	}

	@Override
	protected void preprocessing() throws CelloException {
		this.random = new Random(L_SEED);
		// GateManager
		this.setGateManager(new GateManager(this.getGates()));
		// truth table
		this.setTruthTable();
		// input node assignment
		this.assignInputNodes();
		// output node assignment
		this.assignOutputNodes();
		// logic node assignment
		// this.assignNodes();
		// sensor signals
		this.setSensorSignals(this.getNetlist());
		// activity evaluation
		this.setTMActivityEvaluation(
				new TMActivityEvaluation(this.getNetlist(), this.getSensorSignals(), this.getLSLogicEvaluation()));
		// init edge idx
		if (this.getComputeTandemEfficiency())
			SimulatedAnnealingUtils.initInEdgeIdx(this.getNetlist());
	}

	private Integer getNumTandemPair() {
		Integer rtn = 0;
		for (int i = 0; i < this.getNetlist().getNumVertex(); i++) {
			NetlistNode node = this.getNetlist().getVertexAtIdx(i);
			if (node.getNumInEdge() == 2 && !LSResultsUtils.isAllInput(node) && !LSResultsUtils.isAllOutput(node)) {
				rtn++;
			}
		}
		return rtn;
	}

	private Integer getNumSwappableGate() {
		Integer rtn = 0;
		for (int i = 0; i < this.getNetlist().getNumVertex(); i++) {
			NetlistNode node = this.getNetlist().getVertexAtIdx(i);
			if (!LSResultsUtils.isAllInput(node) && !LSResultsUtils.isAllOutput(node)) {
				rtn++;
			}
		}
		return rtn;
	}

	private NetlistNode getRandomNodeWithTandemPair() {
		NetlistNode rtn = null;
		Double r = Math.random() * this.getNumTandemPair();
		Integer n = r.intValue() + 1;
		int k = 0;
		for (int i = 0; i < this.getNetlist().getNumVertex(); i++) {
			NetlistNode node = this.getNetlist().getVertexAtIdx(i);
			if (!LSResultsUtils.isAllInput(node) && !LSResultsUtils.isAllOutput(node) && node.getNumInEdge() == 2) {
				k++;
			}
			if (k == n) {
				rtn = node;
				break;
			}
		}
		return rtn;
	}

	private void swapGate(SimulatedAnnealingNetlistNodeData data, Gate a, Gate b) {
		GateManager GM = this.getGateManager();
		GM.setUnassignedGate(a);
		data.setGate(b);
		GM.setAssignedGate(b);
	}

	private void swapTandemOrder(NetlistNode node) {
		if (node.getNumInEdge() != 2) {
			throw new RuntimeException("Error with swap.");
		}
		NetlistEdge e1 = node.getInEdgeAtIdx(0);
		NetlistEdge e2 = node.getInEdgeAtIdx(1);
		int i1 = node.getIdx();
		int i2 = node.getIdx();
		e1.setIdx(i2);
		e2.setIdx(i1);
	}

	/**
	 * Run the (core) algorithm
	 */
	@Override
	protected void run() {

		Double MAXTEMP = 100.0;
		Double MINTEMP = 0.001;

		Integer STEPS = 500;

		Double LOGMAX = Math.log10(MAXTEMP);
		Double LOGMIN = Math.log10(MINTEMP);

		Double LOGINC = (LOGMAX - LOGMIN) / STEPS;

		Integer T0_STEPS = 100;

		this.assignNodes();

		Double numTandem = this.getNumTandemPair().doubleValue();
		Double numSwappable = this.getNumSwappableGate().doubleValue();
		Double thresh = numTandem / (numTandem + numSwappable);

		// evaluate
		for (int j = 0; j < STEPS + T0_STEPS; ++j) {
			Double LOGTEMP = LOGMAX - j * LOGINC;
			Double TEMP = Math.pow(10, LOGTEMP);

			if (j >= STEPS) {
				TEMP = 0.0;
			}

			Evaluator eval = null;
			eval = new Evaluator(this.getNetlist(), this.getTMActivityEvaluation(), this.getUnitConversion());
			eval.evaluate();
			this.setTMToxicityEvaluation(new TMToxicityEvaluation(this.getNetlist(), this.getTMActivityEvaluation()));

			Double before = ScoreUtils.score(this.getNetlist(), this.getLSLogicEvaluation(),
					this.getTMActivityEvaluation());
			Double after = before;

			Boolean reject = false;
			Boolean tandemSwap = false;

			GateManager GM = this.getGateManager();
			Netlist netlist = this.getNetlist();

			// get random node
			NetlistNode node = null;
			while (node == null) {
				int rand = random(0, netlist.getNumVertex() - 1);
				NetlistNode temp = netlist.getVertexAtIdx(rand);
				if (!LSResultsUtils.isAllOutput(temp) && !LSResultsUtils.isAllInput(temp)) {
					node = temp;
				}
			}
			SimulatedAnnealingNetlistNodeData data = SimulatedAnnealingUtils.getSimulatedAnnealingNetlistNodeData(node);

			// TODO: gate swap or input order swap
			// DONE 0. init edge idx
			// DONE 1. get number of input pairs (NOR gates) and number of gates (for
			// probablity normalization)
			// DONE 2. compute probability of swapping gate or swapping input order
			// DONE 3. get random number and decide
			// DONE 4. if gate swap, proceed as usual. if input swap, set edge idx
			// DONE? 5. compute circuit output using edge idx
			// DONE a. get edges sorted by idx (maps to promoter order)
			// DONE? b. Evaluator
			// 6. do rule check either way (new code needed for check against edge order)
			// DONE 7. write parts in netlist (needs to be there if we chose promoter order
			// in
			// this stage)
			// 7. read parts in Eugene stage

			Double r = Math.random();
			if (r < thresh && this.getComputeTandemEfficiency()) {
				tandemSwap = true;
			}

			Gate original = null;
			Gate candidate = null;
			NetlistNode swapNode = null;

			if (!tandemSwap) {
				// get random gate
				original = (Gate) data.getGate();
				candidate = GM.getRandomGateFromUnassignedGroup();
				if (candidate == null) {
					throw new RuntimeException("Gate assignment error!");
				}
				// set gate
				this.swapGate(data, original, candidate);
			} else {
				swapNode = this.getRandomNodeWithTandemPair();
				this.swapTandemOrder(swapNode);
			}

			// evaluate
			TMActivityEvaluation tempActivity = new TMActivityEvaluation(this.getNetlist(), this.getSensorSignals(),
					this.getLSLogicEvaluation());

			if (this.getComputeTandemEfficiency())
				eval = new TandemEvaluator(this.getNetlist(), tempActivity, this.getUnitConversion());
			else
				eval = new Evaluator(this.getNetlist(), tempActivity, this.getUnitConversion());

			eval.evaluate();
			after = ScoreUtils.score(this.getNetlist(), this.getLSLogicEvaluation(), tempActivity);

			// toxicity
			TMToxicityEvaluation tempToxicity = new TMToxicityEvaluation(this.getNetlist(),
					this.getTMActivityEvaluation());
			if (this.getTMToxicityEvaluation().getMinimumGrowth() < D_GROWTH_THRESHOLD) {
				if (tempToxicity.getMinimumGrowth() > this.getTMToxicityEvaluation().getMinimumGrowth()) {
					this.setTMToxicityEvaluation(tmte);
					this.setTMActivityEvaluation(tempActivity);
					continue;
				} else {
					reject = true;
				}
			}
			if (tempToxicity.getMinimumGrowth() < D_GROWTH_THRESHOLD) {
				reject = true;
			}

			if (reject) {
				if (!tandemSwap)
					this.swapGate(data, candidate, original);
				else
					this.swapTandemOrder(swapNode);
				continue;
			}

			// TODO: rule check

			// accept or reject
			Double probability = Math.exp((after - before) / TEMP); // e^b
			Double ep = Math.random();

			if (ep < probability) {
				this.setTMActivityEvaluation(tempActivity);
			} else {
				if (!tandemSwap)
					this.swapGate(data, candidate, original);
				else
					this.swapTandemOrder(swapNode);
			}

		}

	}

	/**
	 * Copy the gate assignements to the netlist
	 */
	protected void updateNetlist(final Netlist netlist) {
		for (int i = 0; i < netlist.getNumVertex(); i++) {
			NetlistNode node = netlist.getVertexAtIdx(i);
			Assignable gate = SimulatedAnnealingUtils.getSimulatedAnnealingNetlistNodeData(node).getGate();
			if (gate != null) {
				node.getResultNetlistNodeData().setGateType(gate.getName());
			}
			if (this.getComputeTandemEfficiency()) {
				Collection<String> parts = new ArrayList<>();
				for (NetlistEdge e : SimulatedAnnealingUtils.getInEdgeSortedByIdx(node)) {
					NetlistNode src = e.getSrc();
					Assignable g = SimulatedAnnealingUtils.getSimulatedAnnealingNetlistNodeData(src).getGate();
					if (g instanceof Gate) {
						parts.add(((Gate) g).getGateParts().getPromoter());
					}
					if (g instanceof InputSensor) {
						parts.add(((InputSensor) g).getPromoter());
					}
				}
				if (gate != null && gate instanceof Gate) {
					Gate g = (Gate) gate;
					for (int j = 0; j < g.getResponseFunction().getNumVariable(); j++) {
						ResponseFunctionVariable var = g.getResponseFunction().getVariableAtIdx(j);
						CasetteParts cassette = var.getCasetteParts();
						for (int k = 0; k < cassette.getNumParts(); k++) {
							parts.add(cassette.getPartAtIdx(k).getName());
						}
					}
				}
			}
		}
	}

	/**
	 * Perform postprocessing
	 */
	@Override
	protected void postprocessing() {
		updateNetlist(this.getNetlist());
		String inputFilename = this.getNetlist().getInputFilename();
		String filename = Utils.getFilename(inputFilename);
		String outputDir = this.getRuntimeEnv().getOptionValue(TMArgString.OUTPUTDIR);
		String outputFile = outputDir + Utils.getFileSeparator() + filename;
		// logic
		LSResultsUtils.writeCSVForLSLogicEvaluation(this.getLSLogicEvaluation(), outputFile + "_logic.csv");
		// cytometry
		this.setTMCytometryEvaluation(new TMCytometryEvaluation());
		// toxicity
		this.setTMToxicityEvaluation(new TMToxicityEvaluation(this.getNetlist(), this.getTMActivityEvaluation()));
		SimulatedAnnealingResultsUtils.writeCSVForTMToxicityEvaluation(this.getTMToxicityEvaluation(),
				outputFile + "_toxicity.csv");
		this.logInfo(this.getTMToxicityEvaluation().toString());
		// activity
		TMResultsUtils.writeCSVForTMActivityEvaluation(this.getTMActivityEvaluation(), outputFile + "_activity.csv");
		this.logInfo(this.getTMActivityEvaluation().toString());
		for (int i = 0; i < this.getNetlist().getNumVertex(); i++) {
			NetlistNode node = this.getNetlist().getVertexAtIdx(i);
			Assignable gate = SimulatedAnnealingUtils.getSimulatedAnnealingNetlistNodeData(node).getGate();
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
		logInfo(String.format("Score: %.2f", ScoreUtils.score(this.getNetlist(),this.getLSLogicEvaluation(),this.getTMActivityEvaluation())));
		// plots
		logInfo("Generating plots");
		RuntimeEnv runEnv = this.getRuntimeEnv();
		new ResponsePlots(this.getNetlist(), this.getLSLogicEvaluation(), this.getTMActivityEvaluation(), runEnv);
	}

	/**
	 * Returns the Logger for the <i>SimulatedAnnealing</i> algorithm
	 * 
	 * @return the logger for the <i>SimulatedAnnealing</i> algorithm
	 */
	@Override
	protected Logger getLogger() {
		return SimulatedAnnealing.logger;
	}

	private static final Logger logger = LogManager.getLogger(SimulatedAnnealing.class);

	protected void setComputeTandemEfficiency(final Boolean computeTandemEfficiency) {
		this.computeTandemEfficiency = computeTandemEfficiency;
	}

	public Boolean getComputeTandemEfficiency() {
		return this.computeTandemEfficiency;
	}

	private Boolean computeTandemEfficiency;

	/*
	 * Gate
	 */
	protected void setGates(final CObjectCollection<Gate> gates) {
		this.gates = gates;
	}

	public CObjectCollection<Gate> getGates() {
		return this.gates;
	}

	private CObjectCollection<Gate> gates;

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

	private CObjectCollection<InputSensor> sensors;

	/**
	 * Getter for <i>reporters</i>
	 * 
	 * @return value of <i>reporters</i>
	 */
	public CObjectCollection<OutputDevice> getOutputReporters() {
		return reporters;
	}

	/**
	 * Setter for <i>reporters</i>
	 * 
	 * @param reporters the value to set <i>reporters</i>
	 */
	protected void setOutputReporters(final CObjectCollection<OutputDevice> reporters) {
		this.reporters = reporters;
	}

	private CObjectCollection<OutputDevice> reporters;

	/**
	 * Getter for <i>inputMap</i>
	 * 
	 * @return value of <i>inputMap</i>
	 */
	protected Map<String, String> getInputMap() {
		return inputMap;
	}

	/**
	 * Setter for <i>inputMap</i>
	 * 
	 * @param inputMap the value to set <i>inputMap</i>
	 */
	protected void setInputMap(final Map<String, String> inputMap) {
		this.inputMap = inputMap;
	}

	private Map<String, String> inputMap;

	/**
	 * Getter for <i>outputMap</i>
	 * 
	 * @return value of <i>outputMap</i>
	 */
	protected Map<String, String> getOutputMap() {
		return outputMap;
	}

	/**
	 * Setter for <i>outputMap</i>
	 * 
	 * @param outputMap the value to set <i>outputMap</i>
	 */
	protected void setOutputMap(final Map<String, String> outputMap) {
		this.outputMap = outputMap;
	}

	private Map<String, String> outputMap;

	/**
	 * @param unitConversion the unitConversion to set
	 */
	protected void setUnitConversion(final Double unitConversion) {
		this.unitConversion = unitConversion;
	}

	/**
	 * @return the unitConversion
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
		return this.gateManager;
	}

	private GateManager gateManager;

	/*
	 * LSLogicEvaluation
	 */
	protected void setLSLogicEvaluation(final LSLogicEvaluation lsle) {
		this.lsle = lsle;
	}

	public LSLogicEvaluation getLSLogicEvaluation() {
		return this.lsle;
	}

	private LSLogicEvaluation lsle;

	/**
	 * Getter for <i>signals</i>
	 * 
	 * @return value of <i>signals</i>
	 */
	protected SensorSignals<NetlistNode> getSensorSignals() {
		return signals;
	}

	/**
	 * Setter for <i>signals</i>
	 * 
	 * @param netlist the Netlist used to set <i>signals</i>
	 * @throws CelloException
	 */
	protected void setSensorSignals(final Netlist netlist) throws CelloException {
		List<NetlistNode> inputs = LSResultsUtils.getPrimaryInputNodes(netlist);
		SensorSignals<NetlistNode> signals = new SensorSignals<NetlistNode>(inputs);
		for (int i = 0; i < inputs.size(); i++) {
			NetlistNode node = inputs.get(i);
			InputSensor sensor = (InputSensor) SimulatedAnnealingUtils.getSimulatedAnnealingNetlistNodeData(node)
					.getGate();
			Parameter hi = sensor.getParameterValueByName(InputSensor.S_HI);
			Parameter lo = sensor.getParameterValueByName(InputSensor.S_LO);
			String fmt = "Error with %s %s.";
			if (hi == null || hi.getValue() == null)
				throw new CelloException(String.format(fmt, InputSensor.class.getName(), InputSensor.S_HI));
			if (lo == null || lo.getValue() == null)
				throw new CelloException(String.format(fmt, InputSensor.class.getName(), InputSensor.S_LO));
			signals.setHighActivitySignal(node, hi.getValue());
			signals.setLowActivitySignal(node, lo.getValue());
		}
		this.signals = signals;
	}

	/**
	 * Setter for <i>signals</i>
	 * 
	 * @param signals the value to set <i>signals</i>
	 */
	protected void setSensorSignals(final SensorSignals<NetlistNode> signals) {
		this.signals = signals;
	}

	private SensorSignals<NetlistNode> signals;

	/*
	 * TMActivityEvaluation
	 */
	/**
	 * Getter for <i>tmae</i>
	 * 
	 * @return value of <i>tmae</i>
	 */
	public TMActivityEvaluation getTMActivityEvaluation() {
		return tmae;
	}

	/**
	 * Setter for <i>tmte</i>
	 * 
	 * @param tmte the value to set <i>tmte</i>
	 */
	protected void setTMToxicityEvaluation(final TMToxicityEvaluation tmte) {
		this.tmte = tmte;
	}

	private TMToxicityEvaluation tmte;

	/*
	 * TMToxicityEvaluation
	 */
	/**
	 * Setter for <i>tmae</i>
	 * 
	 * @param tmae the value to set <i>tmae</i>
	 */
	protected void setTMActivityEvaluation(final TMActivityEvaluation tmae) {
		this.tmae = tmae;
	}

	/**
	 * Setter for <i>tmae</i>
	 * 
	 * @param tmte the value to set <i>tmae</i>
	 */
	protected void setTMActivityEvaluation(final Netlist netlist, final SensorSignals<NetlistNode> signals,
			final LSLogicEvaluation lsle) {
		updateNetlist(netlist);
		tmae = new TMActivityEvaluation(netlist, signals, lsle);
	}

	/**
	 * Getter for <i>tmte</i>
	 * 
	 * @return value of <i>tmte</i>
	 */
	public TMToxicityEvaluation getTMToxicityEvaluation() {
		return tmte;
	}

	private TMActivityEvaluation tmae;

	/**
	 * Getter for <i>tmce</i>
	 * 
	 * @return value of <i>tmce</i>
	 */
	public TMCytometryEvaluation getTMCytometryEvaluation() {
		return tmce;
	}

	/**
	 * Setter for <i>tmce</i>
	 * 
	 * @param tmce the value to set <i>tmce</i>
	 */
	protected void setTMCytometryEvaluation(final TMCytometryEvaluation tmce) {
		this.tmce = tmce;
	}

	private TMCytometryEvaluation tmce;

	/*
	 * Random
	 */
	private int random(int min, int max) {
		int rtn = 0;
		Random random = this.getRandom();
		rtn = random.nextInt(max - min + 1) + min;
		return rtn;
	}

	private Random getRandom() {
		return this.random;
	}

	private Random random;
	private static long L_SEED = 21;
	private static final double D_GROWTH_THRESHOLD = 0.75;

}

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

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cellocad.cello2.common.CObjectCollection;
import org.cellocad.cello2.common.Utils;
import org.cellocad.cello2.common.netlistConstraint.data.NetlistConstraint;
import org.cellocad.cello2.common.graph.algorithm.Tarjan;
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
import org.cellocad.cello2.technologyMapping.algorithm.SimulatedAnnealing.data.SimulatedAnnealingNetlistData;
import org.cellocad.cello2.technologyMapping.algorithm.SimulatedAnnealing.data.SimulatedAnnealingNetlistEdgeData;
import org.cellocad.cello2.technologyMapping.algorithm.SimulatedAnnealing.data.SimulatedAnnealingNetlistNodeData;
import org.cellocad.cello2.technologyMapping.algorithm.SimulatedAnnealing.data.assignment.GateManager;
import org.cellocad.cello2.technologyMapping.algorithm.SimulatedAnnealing.data.evaluation.NetlistEvaluator;
import org.cellocad.cello2.technologyMapping.algorithm.SimulatedAnnealing.data.score.ScoreUtils;
import org.cellocad.cello2.technologyMapping.algorithm.SimulatedAnnealing.data.toxicity.TMToxicityEvaluation;
import org.cellocad.cello2.technologyMapping.algorithm.SimulatedAnnealing.data.ucf.Assignable;
import org.cellocad.cello2.technologyMapping.algorithm.SimulatedAnnealing.data.ucf.Gate;
import org.cellocad.cello2.technologyMapping.algorithm.SimulatedAnnealing.data.ucf.InputSensor;
import org.cellocad.cello2.technologyMapping.algorithm.SimulatedAnnealing.data.ucf.OutputReporter;
import org.cellocad.cello2.technologyMapping.algorithm.SimulatedAnnealing.results.SimulatedAnnealingResultsUtils;
import org.cellocad.cello2.technologyMapping.runtime.environment.TMArgString;
import org.json.simple.JSONObject;

/**
 * The SimulatedAnnealing class implements the <i>SimulatedAnnealing</i> algorithm in the <i>technologyMapping</i> stage.
 *
 * @author Vincent Mirian
 * @author Timothy Jones
 *
 * @date 2018-05-21
 *
 */
public class SimulatedAnnealing extends TMAlgorithm{

	/**
	 *  Returns the <i>SimulatedAnnealingNetlistNodeData</i> of the <i>node</i>
	 *
	 *  @param node a node within the <i>netlist</i> of this instance
	 *  @return the <i>SimulatedAnnealingNetlistNodeData</i> instance if it exists, null otherwise
	 */
	protected SimulatedAnnealingNetlistNodeData getSimulatedAnnealingNetlistNodeData(NetlistNode node){
		SimulatedAnnealingNetlistNodeData rtn = null;
		rtn = (SimulatedAnnealingNetlistNodeData) node.getNetlistNodeData();
		return rtn;
	}

	/**
	 *  Returns the <i>SimulatedAnnealingNetlistEdgeData</i> of the <i>edge</i>
	 *
	 *  @param edge an edge within the <i>netlist</i> of this instance
	 *  @return the <i>SimulatedAnnealingNetlistEdgeData</i> instance if it exists, null otherwise
	 */
	protected SimulatedAnnealingNetlistEdgeData getSimulatedAnnealingNetlistEdgeData(NetlistEdge edge){
		SimulatedAnnealingNetlistEdgeData rtn = null;
		rtn = (SimulatedAnnealingNetlistEdgeData) edge.getNetlistEdgeData();
		return rtn;
	}

	/**
	 *  Returns the <i>SimulatedAnnealingNetlistData</i> of the <i>netlist</i>
	 *
	 *  @param netlist the netlist of this instance
	 *  @return the <i>SimulatedAnnealingNetlistData</i> instance if it exists, null otherwise
	 */
	protected SimulatedAnnealingNetlistData getSimulatedAnnealingNetlistData(Netlist netlist){
		SimulatedAnnealingNetlistData rtn = null;
		rtn = (SimulatedAnnealingNetlistData) netlist.getNetlistData();
		return rtn;
	}

	/**
	 *  Gets the Constraint data from the NetlistConstraintFile
	 */
	@Override
	protected void getConstraintFromNetlistConstraintFile() {
		NetlistConstraint constraint = this.getNetlistConstraint();
		String type = "";
		// input constraints
		type = "input_constraints";
		Map<String,String> inputMap = new HashMap<>();
		for (int i = 0; i < constraint.getNumJSONObject(type); i++) {
			JSONObject jObj = constraint.getJSONObjectAtIdx(type,i);
			JSONObject map = (JSONObject) jObj.get("sensor_map");
			for (Object obj : map.keySet()) {
				String key = (String) obj;
				String value = (String) map.get(obj);
				inputMap.put(key,value);
			}
		}
		this.setInputMap(inputMap);
		// ouptut constraints
		type = "output_constraints";
		Map<String,String> outputMap = new HashMap<>();
		for (int i = 0; i < constraint.getNumJSONObject(type); i++) {
			JSONObject jObj = constraint.getJSONObjectAtIdx(type,i);
			JSONObject map = (JSONObject) jObj.get("reporter_map");
			for (Object obj : map.keySet()) {
				String key = (String) obj;
				String value = (String) map.get(obj);
				outputMap.put(key,value);
			}
		}
		this.setOutputMap(outputMap);
	}

	/**
	 *  Gets the data from the UCF
	 */
	@Override
	protected void getDataFromUCF() {
		this.setGates(SimulatedAnnealingDataUtils.getGates(this.getTargetData()));
		this.setInputSensors(SimulatedAnnealingDataUtils.getInputSensors(this.getTargetData()));
		this.setOutputReporters(SimulatedAnnealingDataUtils.getOutputReporters(this.getTargetData()));
		this.setUnitConversion(SimulatedAnnealingDataUtils.getUnitConversion(this.getTargetData()));
	}

	/**
	 *  Set parameter(s) value(s) of the algorithm
	 */
	@Override
	protected void setParameterValues() {
	}

	/**
	 *  Validate parameter value of the algorithm
	 */
	@Override
	protected void validateParameterValues() {

	}

	/**
	 *  Perform preprocessing
	 */
	protected void setTruthTable() {
		LSResultNetlistUtils.setVertexTypeUsingLSResult(this.getNetlist());
		Tarjan<NetlistNode, NetlistEdge, Netlist> tarjan = new Tarjan<NetlistNode, NetlistEdge, Netlist>(this.getNetlist());
		CObjectCollection<NetlistNode> component = null;
		while ((component = tarjan.getNextComponent()) != null) {
			if (component.size() <= 1)
				continue;
			for (int i = 0; i < component.size(); i++) {
				NetlistNode node = component.get(i);
			}
		}		
		this.setLSLogicEvaluation(new LSLogicEvaluation(this.getNetlist()));
		this.logInfo(this.getLSLogicEvaluation().toString());
	}

	protected void assignInputNodes() {
		// assign input
		CObjectCollection<NetlistNode> inputNodes = LSResultsUtils.getPrimaryInputNodes(this.getNetlist());
		Map<String,String> inputMap = this.getInputMap();
		Iterator<InputSensor> it = this.getInputSensors().iterator();
		for (int i = 0; i < inputNodes.size(); i++) {
			NetlistNode node = inputNodes.get(i);
			SimulatedAnnealingNetlistNodeData data = this.getSimulatedAnnealingNetlistNodeData(node);
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
		Iterator<OutputReporter> it = this.getOutputReporters().iterator();
		for (int i = 0; i < outputNodes.size(); i++) {
			NetlistNode node = outputNodes.get(i);
			SimulatedAnnealingNetlistNodeData data = this.getSimulatedAnnealingNetlistNodeData(node);
			if (!it.hasNext()) {
				throw new RuntimeException("Not enough output reporters in the library to cover the netlist outputs.");
			}
			OutputReporter reporter = null;
			if (outputMap.containsKey(node.getName())) {
				String value = outputMap.get(node.getName());
				reporter = this.getOutputReporters().findCObjectByName(value);
			}
			while (reporter == null) {
				OutputReporter temp = it.next();
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
			SimulatedAnnealingNetlistNodeData data = this.getSimulatedAnnealingNetlistNodeData(node);
			Gate gate = GM.getRandomGateFromUnassignedGroup();
			if (gate == null) {
				throw new RuntimeException("Gate assignment error!");
			}
			data.setGate(gate);
			GM.setAssignedGate(gate);
		}
	}

	@Override
	protected void preprocessing() {
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
		this.setTMActivityEvaluation(new TMActivityEvaluation(this.getNetlist(),this.getSensorSignals(),this.getLSLogicEvaluation()));
	}

	/**
	 *  Run the (core) algorithm
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

		// evaluate
		for (int j = 0; j < STEPS + T0_STEPS; ++j) {
			Double LOGTEMP = LOGMAX - j * LOGINC;
			Double TEMP = Math.pow(10, LOGTEMP);

			if (j >= STEPS) {
				TEMP = 0.0;
			}

			NetlistEvaluator eval = null;
			eval = new NetlistEvaluator(this.getNetlist(),this.getTMActivityEvaluation(),this.getUnitConversion());
			eval.evaluate();
			this.setTMToxicityEvaluation(new TMToxicityEvaluation(this.getNetlist(),this.getTMActivityEvaluation()));

			Double before = ScoreUtils.score(this.getNetlist(),this.getLSLogicEvaluation(),this.getTMActivityEvaluation());

			GateManager GM = this.getGateManager();
			Netlist netlist = this.getNetlist();

			// get random node
			NetlistNode node = null;
			while (node == null) {
				int rand = random(0,netlist.getNumVertex() - 1);
				NetlistNode temp = netlist.getVertexAtIdx(rand);
				if (!LSResultsUtils.isAllOutput(temp) && !LSResultsUtils.isAllInput(temp)) {
					node = temp;
				}
			}
			SimulatedAnnealingNetlistNodeData data = this.getSimulatedAnnealingNetlistNodeData(node);

			// get random gate
			Gate original = (Gate)data.getGate();
			Gate candidate = GM.getRandomGateFromUnassignedGroup();
			if (candidate == null) {
				throw new RuntimeException("Gate assignment error!");
			}
			// set gate
			GM.setUnassignedGate(original);
			data.setGate(candidate);
			GM.setAssignedGate(candidate);

			// evaluate
			TMActivityEvaluation tempActivity = new TMActivityEvaluation(this.getNetlist(),
			                                                     this.getSensorSignals(),
			                                                     this.getLSLogicEvaluation());
			eval = new NetlistEvaluator(this.getNetlist(),tempActivity,this.getUnitConversion());
			eval.evaluate();
			Double after = ScoreUtils.score(this.getNetlist(),this.getLSLogicEvaluation(),tempActivity);

			// toxicity
			TMToxicityEvaluation tempToxicity = new TMToxicityEvaluation(this.getNetlist(),this.getTMActivityEvaluation());
			if (this.getTMToxicityEvaluation().getMinimumGrowth() < D_GROWTH_THRESHOLD) {
				if (tempToxicity.getMinimumGrowth() > this.getTMToxicityEvaluation().getMinimumGrowth()) {
					this.setTMToxicityEvaluation(tmte);
					this.setTMActivityEvaluation(tempActivity);
					continue;
				}
				else {
					// undo
					GM.setUnassignedGate(candidate);
					data.setGate(original);
					GM.setAssignedGate(original);
					continue;
				}
			}
			else if (tempToxicity.getMinimumGrowth() < D_GROWTH_THRESHOLD) {
				// undo
				GM.setUnassignedGate(candidate);
				data.setGate(original);
				GM.setAssignedGate(original);
				continue;
			}

			// accept or reject
			Double probability = Math.exp( (after-before) / TEMP ); // e^b
			Double ep = Math.random();

			if (ep < probability) {
				this.setTMActivityEvaluation(tempActivity);
			} else {
				// undo
				GM.setUnassignedGate(candidate);
				data.setGate(original);
				GM.setAssignedGate(original);
			}

		}

	}

	/**
	 * Copy the gate assignements to the netlist
	 */
	protected void updateNetlist(final Netlist netlist) {
		for(int i = 0; i < netlist.getNumVertex(); i++) {
			NetlistNode node = netlist.getVertexAtIdx(i);
			Assignable gate = this.getSimulatedAnnealingNetlistNodeData(node).getGate();
			if (gate != null) {
				node.getResultNetlistNodeData().setGateType(gate.getName());
			}
		}
	}

	/**
	 *  Perform postprocessing
	 */
	@Override
	protected void postprocessing() {
		updateNetlist(this.getNetlist());
		String inputFilename = this.getNetlist().getInputFilename();
		String filename = Utils.getFilename(inputFilename);
		String outputDir = this.getRuntimeEnv().getOptionValue(TMArgString.OUTPUTDIR);
		String outputFile = outputDir + Utils.getFileSeparator() + filename;
		// logic
		LSResultsUtils.writeCSVForLSLogicEvaluation(this.getLSLogicEvaluation(),outputFile + "_logic.csv");
		// cytometry
		this.setTMCytometryEvaluation(new TMCytometryEvaluation());
		//toxicity
		this.setTMToxicityEvaluation(new TMToxicityEvaluation(this.getNetlist(),this.getTMActivityEvaluation()));
		SimulatedAnnealingResultsUtils.writeCSVForTMToxicityEvaluation(this.getTMToxicityEvaluation(),outputFile + "_toxicity.csv");
		this.logInfo(this.getTMToxicityEvaluation().toString());
		//activity
		TMResultsUtils.writeCSVForTMActivityEvaluation(this.getTMActivityEvaluation(),outputFile + "_activity.csv");
		this.logInfo(this.getTMActivityEvaluation().toString());
		for (int i = 0; i < this.getNetlist().getNumVertex(); i++) {
			NetlistNode node = this.getNetlist().getVertexAtIdx(i);
			Assignable gate = this.getSimulatedAnnealingNetlistNodeData(node).getGate();
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
	}


	/**
	 *  Returns the Logger for the <i>SimulatedAnnealing</i> algorithm
	 *
	 *  @return the logger for the <i>SimulatedAnnealing</i> algorithm
	 */
	@Override
	protected Logger getLogger() {
		return SimulatedAnnealing.logger;
	}

	private static final Logger logger = LogManager.getLogger(SimulatedAnnealing.class);

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

	private CObjectCollection<InputSensor> sensors;

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

	private CObjectCollection<OutputReporter> reporters;

	/**
	 * Getter for <i>inputMap</i>
	 * @return value of <i>inputMap</i>
	 */
	protected Map<String,String> getInputMap() {
		return inputMap;
	}

	/**
	 * Setter for <i>inputMap</i>
	 * @param inputMap the value to set <i>inputMap</i>
	 */
	protected void setInputMap(final Map<String,String> inputMap) {
		this.inputMap = inputMap;
	}

	private Map<String,String> inputMap;

	/**
	 * Getter for <i>outputMap</i>
	 * @return value of <i>outputMap</i>
	 */
	protected Map<String, String> getOutputMap() {
		return outputMap;
	}

	/**
	 * Setter for <i>outputMap</i>
	 * @param outputMap the value to set <i>outputMap</i>
	 */
	protected void setOutputMap(final Map<String, String> outputMap) {
		this.outputMap = outputMap;
	}

	private Map<String,String> outputMap;

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
	 * @return value of <i>signals</i>
	 */
	protected SensorSignals<NetlistNode> getSensorSignals() {
		return signals;
	}

	/**
	 * Setter for <i>signals</i>
	 * @param netlist the Netlist used to set <i>signals</i>
	 */
	protected void setSensorSignals(final Netlist netlist) {
		List<NetlistNode> inputs = LSResultsUtils.getPrimaryInputNodes(netlist);
		SensorSignals<NetlistNode> signals = new SensorSignals<NetlistNode>(inputs);
		for (int i = 0; i < inputs.size(); i++) {
			NetlistNode node = inputs.get(i);
			InputSensor sensor = (InputSensor) this.getSimulatedAnnealingNetlistNodeData(node).getGate();
			signals.setHighActivitySignal(node, sensor.getHighSignal());
			signals.setLowActivitySignal(node, sensor.getLowSignal());
		}
		this.signals = signals;
	}

	/**
	 * Setter for <i>signals</i>
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
	 * @return value of <i>tmae</i>
	 */
	public TMActivityEvaluation getTMActivityEvaluation() {
		return tmae;
	}

	/**
	 * Setter for <i>tmte</i>
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
	 * @param tmae the value to set <i>tmae</i>
	 */
	protected void setTMActivityEvaluation(final TMActivityEvaluation tmae) {
		this.tmae = tmae;
	}

	/**
	 * Setter for <i>tmae</i>
	 * @param tmte the value to set <i>tmae</i>
	 */
	protected void setTMActivityEvaluation(final Netlist netlist, final SensorSignals<NetlistNode> signals, final LSLogicEvaluation lsle) {
		updateNetlist(netlist);
		tmae = new TMActivityEvaluation(netlist,signals,lsle);
	}

	/**
	 * Getter for <i>tmte</i>
	 * @return value of <i>tmte</i>
	 */
	public TMToxicityEvaluation getTMToxicityEvaluation() {
		return tmte;
	}

	private TMActivityEvaluation tmae;

	/**
	 * Getter for <i>tmce</i>
	 * @return value of <i>tmce</i>
	 */
	public TMCytometryEvaluation getTMCytometryEvaluation() {
		return tmce;
	}

	/**
	 * Setter for <i>tmce</i>
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

	private Random getRandom(){
		return this.random;
	}

	private Random random;
	private static long L_SEED = 29;
	private static final double D_GROWTH_THRESHOLD = 0.75;
}

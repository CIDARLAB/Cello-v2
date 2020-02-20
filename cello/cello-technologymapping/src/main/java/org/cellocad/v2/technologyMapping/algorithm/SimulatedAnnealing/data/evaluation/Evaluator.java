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
package org.cellocad.v2.technologyMapping.algorithm.SimulatedAnnealing.data.evaluation;

import java.util.ArrayList;
import java.util.List;

import org.cellocad.v2.common.CelloException;
import org.cellocad.v2.common.Utils;
import org.cellocad.v2.common.graph.algorithm.SinkDFS;
import org.cellocad.v2.common.target.data.component.AssignableDevice;
import org.cellocad.v2.common.target.data.model.EvaluationContext;
import org.cellocad.v2.common.target.data.model.Function;
import org.cellocad.v2.common.target.data.model.FunctionType;
import org.cellocad.v2.results.logicSynthesis.LSResults;
import org.cellocad.v2.results.netlist.Netlist;
import org.cellocad.v2.results.netlist.NetlistEdge;
import org.cellocad.v2.results.netlist.NetlistNode;
import org.cellocad.v2.results.technologyMapping.activity.TMActivityEvaluation;
import org.cellocad.v2.results.technologyMapping.activity.activitytable.Activity;
import org.cellocad.v2.results.technologyMapping.activity.activitytable.ActivityTable;
import org.cellocad.v2.technologyMapping.algorithm.SimulatedAnnealing.data.SimulatedAnnealingNetlistNodeData;

/**
 * The Evaluator class evaluates the activity of a netlist used within the <i>SimulatedAnnealing</i> algorithm class of the <i>technologyMapping</i> stage.
 *
 * @author Timothy Jones
 *
 * @date 2018-07-17
 *
 */
public class Evaluator {
	
	private void init() {
	}
	
	public Evaluator(Netlist netlist, TMActivityEvaluation tmae, Double conversion) {
		init();
		if (!netlist.isValid()) {
			throw new RuntimeException("netlist is not valid!");
		}
		this.netlist = netlist;
		this.tmae = tmae;
		this.conversion = conversion;
	}
	
	/**
	 *  Returns a List of Double representation of the input values for NetlistNode defined by parameter <i>node</i>
	 *  at the activity defined by parameter <i>state</i>
	 *  
	 *  @param node the NetlistNode
	 *  @param activity the activity
	 *  @return a List of Double representation of the input values for NetlistNode defined by parameter <i>node</i>
	 *  at the activity defined by parameter <i>state</i>
	 */
	private List<Double> getInputActivity(final NetlistNode node, final Activity<NetlistNode> activity) {
		List<Double> rtn = new ArrayList<Double>();
		for (int i = 0; i < node.getNumInEdge(); i++) {
			NetlistNode inputNode = node.getInEdgeAtIdx(i).getSrc();
			ActivityTable<NetlistNode, NetlistNode> activityTable = this.getTMActivityEvaluation().getActivityTable(inputNode);
			activityTable.getActivityOutput(activity);
			Activity<NetlistNode> outputActivity = activityTable.getActivityOutput(activity);
			if (outputActivity.getNumActivityPosition() != 1) {
				throw new RuntimeException("Invalid number of output(s)!");
			}
			rtn.add(outputActivity.getActivity(inputNode));
		}
		return rtn;
	}
	
	/**
	 *  Returns the evaluation for a Primary Input for NetlistNode defined by parameter <i>node</i>
	 *  at the state defined by parameter <i>state</i>
	 *  
	 *  @param node the NetlistNode
	 *  @param state the state
	 *  @return the evaluation for a Primary Input for NetlistNode defined by parameter <i>node</i>
	 *  at the state defined by parameter <i>state</i>
	 */
	private Double computePrimaryInput(final NetlistNode node, final Activity<NetlistNode> activity) {
		Double rtn = null;
		List<Double> inputList = this.getInputActivity(node, activity);
		if (inputList.size() == 0) {
			rtn = activity.getActivity(node);
		}
		return rtn;
	}
	
	/**
	 *  Returns the evaluation for a Primary Output for NetlistNode defined by parameter <i>node</i>
	 *  at the activity defined by parameter <i>activity</i>
	 *  
	 *  @param node the NetlistNode
	 *  @param state the state
	 *  @return the evaluation for a Primary Output for NetlistNode defined by parameter <i>node</i>
	 *  at the activity defined by parameter <i>activity</i>
	 */
	private Double computePrimaryOutput(final NetlistNode node, final Activity<NetlistNode> activity) {
		Double rtn = null;
		List<Double> inputList = this.getInputActivity(node, activity);
		if (inputList.size() == 1) {
			rtn = inputList.get(0) * this.getConversion();
		}
		if (inputList.size() > 1) {
			Double sum = 0.0;
			for (Double input : inputList) {
				sum += input;
			}
			rtn = sum * this.getConversion();
		}
		return rtn;
	}
	
	/**
	 * Returns the activity evaluation for a NetlistNode defined by parameter
	 * <i>node</i> at the state defined by parameter <i>state</i>
	 * 
	 * @param node  the NetlistNode
	 * @param state the state
	 * @return the evaluation for an XNOR NodeType for NetlistNode defined by
	 *         parameter <i>node</i> at the state defined by parameter <i>state</i>
	 * @throws CelloException
	 */	
	private Double computeActivity(final NetlistNode node, final Activity<NetlistNode> activity) throws CelloException {
		Double rtn = null;
		List<Double> inputList = this.getTMActivityEvaluation().getInputActivity(node, activity);
		if (inputList.size() >= 1) {
			SimulatedAnnealingNetlistNodeData data = (SimulatedAnnealingNetlistNodeData) node.getNetlistNodeData();
			AssignableDevice d = data.getGate();
			Function function = d.getModel().getFunctionByName(FunctionType.S_RESPONSEFUNCTION);
			EvaluationContext ec = new EvaluationContext();
			ec.setNode(node);
			rtn = function.evaluate(ec).doubleValue();
		}
		return rtn;
	}
	
	/**
	 * Evaluates the truth table for the NetlistNode defined by parameter
	 * <i>node</i>
	 * 
	 * @param node the NetlistNode
	 * @throws CelloException
	 */
	private void evaluateActivityTable(final NetlistNode node,
			final ActivityTable<NetlistNode, NetlistNode> activityTable) throws CelloException {
		Double result = null;
		final String nodeType = node.getResultNetlistNodeData().getNodeType();
		for (int i = 0; i < activityTable.getNumActivities(); i ++) {
			Activity<NetlistNode> inputActivity = activityTable.getStateAtIdx(i);
			Activity<NetlistNode> outputActivity = activityTable.getActivityOutput(inputActivity);
			if (outputActivity.getNumActivityPosition() != 1) {
				throw new RuntimeException("Invalid number of output(s)!");
			}
			switch (nodeType) {
				case LSResults.S_PRIMARYINPUT:{
					result = this.computePrimaryInput(node, inputActivity);
					break;
				}
				case LSResults.S_PRIMARYOUTPUT:{
					result = this.computePrimaryOutput(node, inputActivity);
					break;
				}
				default:{
					result = computeActivity(node, inputActivity);
					break;
				}
			}
			Utils.isNullRuntimeException(result, "result");
			if (!outputActivity.setActivity(node, result)) {
				throw new RuntimeException("Node does not exist");
			}
		}
	}
	
	/**
	 * Evaluates the Netlist defined by parameter <i>netlist</i> and stores the
	 * result in the TMActivityEvaluation defined by parameter <i>tmae</i>
	 * 
	 * @param netlist the Netlist
	 * @param tmae    the TMActivityEvaluation
	 * @throws CelloException
	 */
	public void evaluate() throws CelloException {
		SinkDFS<NetlistNode, NetlistEdge, Netlist> DFS = new SinkDFS<NetlistNode, NetlistEdge, Netlist>(this.getNetlist());
		NetlistNode node = null;
		node = DFS.getNextVertex();
		while (node != null) {
			ActivityTable<NetlistNode,NetlistNode> activityTable = this.getTMActivityEvaluation().getActivityTable(node);
			evaluateActivityTable(node,activityTable);
			node = DFS.getNextVertex();
		}
	}
	
	/**
	 * Getter for <i>tmae</i>
	 * @return value of <i>tmae</i>
	 */
	private TMActivityEvaluation getTMActivityEvaluation() {
		return tmae;
	}

	/**
	 * Getter for <i>netlist</i>
	 * @return value of <i>netlist</i>
	 */
	private Netlist getNetlist() {
		return netlist;
	}
	
	/**
	 * @return the conversion
	 */
	private Double getConversion() {
		return conversion;
	}

	private TMActivityEvaluation tmae;
	private Netlist netlist;
	private Double conversion;

}

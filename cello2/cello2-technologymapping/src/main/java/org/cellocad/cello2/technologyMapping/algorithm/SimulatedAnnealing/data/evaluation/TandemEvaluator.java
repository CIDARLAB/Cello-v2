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
package org.cellocad.cello2.technologyMapping.algorithm.SimulatedAnnealing.data.evaluation;

import java.util.List;

import org.cellocad.cello2.results.netlist.Netlist;
import org.cellocad.cello2.results.netlist.NetlistEdge;
import org.cellocad.cello2.results.netlist.NetlistNode;
import org.cellocad.cello2.results.technologyMapping.activity.TMActivityEvaluation;
import org.cellocad.cello2.results.technologyMapping.activity.activitytable.Activity;
import org.cellocad.cello2.results.technologyMapping.activity.activitytable.ActivityTable;
import org.cellocad.cello2.technologyMapping.algorithm.SimulatedAnnealing.SimulatedAnnealingUtils;
import org.cellocad.cello2.technologyMapping.algorithm.SimulatedAnnealing.data.SimulatedAnnealingNetlistNodeData;
import org.cellocad.cello2.technologyMapping.algorithm.SimulatedAnnealing.data.ucf.Gate;
import org.cellocad.cello2.technologyMapping.algorithm.SimulatedAnnealing.data.ucf.Parameter;
import org.cellocad.cello2.technologyMapping.algorithm.SimulatedAnnealing.data.ucf.ResponseFunction;

/**
 * The TandemEvaluator class evaluates the activity of a netlist used within the
 * <i>SimulatedAnnealing</i> algorithm class of the <i>technologyMapping</i>
 * stage.
 *
 * @author Timothy Jones
 *
 * @date 2019-08-12
 *
 */
public class TandemEvaluator extends Evaluator {

	public TandemEvaluator(Netlist netlist, TMActivityEvaluation tmae, Double conversion) {
		super(netlist, tmae, conversion);
	}

	protected Double getOutputActivity(final NetlistNode node, final Activity<NetlistNode> activity) {
		Double rtn = null;
		ActivityTable<NetlistNode, NetlistNode> activityTable = this.getTMActivityEvaluation().getActivityTable(node);
		activityTable.getActivityOutput(activity);
		Activity<NetlistNode> outputActivity = activityTable.getActivityOutput(activity);
		if (outputActivity.getNumActivityPosition() != 1) {
			throw new RuntimeException("Invalid number of output(s)!");
		}
		rtn = outputActivity.getActivity(node);
		return rtn;
	}

	@Override
	protected Double getInputActivity(final NetlistNode node, final Activity<NetlistNode> activity) {
		Double rtn = null;
		List<NetlistEdge> inputEdgeList = SimulatedAnnealingUtils.getInEdgeSortedByIdx(node);
		if (inputEdgeList.size() == 2) {
			NetlistEdge e2 = inputEdgeList.get(1);
			NetlistNode n2 = e2.getSrc();
			NetlistEdge e1 = inputEdgeList.get(0);
			NetlistNode n1 = e1.getSrc();

			Double y2 = this.getTMActivityEvaluation().getOutputActivity(n2, activity);
			Double y1 = this.getTMActivityEvaluation().getOutputActivity(n1, activity);
			Double input = this.getInputActivity(n2, activity);

			SimulatedAnnealingNetlistNodeData data = (SimulatedAnnealingNetlistNodeData) n2.getNetlistNodeData();
			Gate gate = (Gate) data.getGate();
			ResponseFunction function = gate.getResponseFunction();
			String equation = function.getTandemEfficiencyFactor();
			MathEval eval = new MathEval();
			for (int i = 0; i < function.getNumParameter(); i++) {
				Parameter param = function.getParameterAtIdx(i);
				eval.setConstant(param.getName(), param.getValue());
			}
			eval.setVariable(function.getVariableByName("x").getName(), input);
			Double c = eval.evaluate(equation);

			rtn = y2 + c * y1;
		} else if (inputEdgeList.size() == 1) {
			NetlistEdge e = inputEdgeList.get(0);
			NetlistNode n = e.getSrc();
			rtn = this.getTMActivityEvaluation().getOutputActivity(n, activity);
		} else {
			throw new RuntimeException("Error with tandem evaluation.");
		}
		return rtn;
	}

	@Override
	protected Double computeActivity(final NetlistNode node, final Activity<NetlistNode> activity) {
		Double rtn = null;

		SimulatedAnnealingNetlistNodeData data = (SimulatedAnnealingNetlistNodeData) node.getNetlistNodeData();
		Gate gate = (Gate) data.getGate();
		ResponseFunction function = gate.getResponseFunction();

		Double input = 0.0;

		String equation = function.getEquation();

		MathEval eval = new MathEval();
		for (int i = 0; i < function.getNumParameter(); i++) {
			Parameter param = function.getParameterAtIdx(i);
			eval.setConstant(param.getName(), param.getValue());
		}
		eval.setVariable(function.getVariableByName("x").getName(), input);
		rtn = eval.evaluate(equation);
		return rtn;
	}

}

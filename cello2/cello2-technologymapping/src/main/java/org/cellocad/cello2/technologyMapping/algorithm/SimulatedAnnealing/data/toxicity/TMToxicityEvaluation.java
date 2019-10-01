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
package org.cellocad.cello2.technologyMapping.algorithm.SimulatedAnnealing.data.toxicity;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.cellocad.cello2.common.CObject;
import org.cellocad.cello2.common.Utils;
import org.cellocad.cello2.common.graph.algorithm.SinkDFS;
import org.cellocad.cello2.results.logicSynthesis.LSResultsUtils;
import org.cellocad.cello2.results.netlist.Netlist;
import org.cellocad.cello2.results.netlist.NetlistEdge;
import org.cellocad.cello2.results.netlist.NetlistNode;
import org.cellocad.cello2.results.technologyMapping.activity.TMActivityEvaluation;
import org.cellocad.cello2.results.technologyMapping.activity.activitytable.Activities;
import org.cellocad.cello2.results.technologyMapping.activity.activitytable.Activity;
import org.cellocad.cello2.results.technologyMapping.activity.activitytable.ActivityTable;
import org.cellocad.cello2.technologyMapping.algorithm.SimulatedAnnealing.data.SimulatedAnnealingNetlistNodeData;
import org.cellocad.cello2.technologyMapping.algorithm.SimulatedAnnealing.data.toxicity.toxicitytable.Toxicity;
import org.cellocad.cello2.technologyMapping.algorithm.SimulatedAnnealing.data.toxicity.toxicitytable.ToxicityTable;
import org.cellocad.cello2.technologyMapping.algorithm.SimulatedAnnealing.data.ucf.Gate;
import org.cellocad.cello2.technologyMapping.algorithm.SimulatedAnnealing.data.ucf.ResponseFunction;
import org.cellocad.cello2.technologyMapping.algorithm.SimulatedAnnealing.data.ucf.ResponseFunctionVariable;

/**
 * The TMToxicityEvaluation class evaluates the toxicity of a netlist used within the <i>SimulatedAnnealing</i> algorithm class of the <i>technologyMapping</i> stage.
 *
 * @author Timothy Jones
 *
 * @date 2019-01-29
 *
 */
public class TMToxicityEvaluation extends CObject{

	/**
	 * Initialize class members
	 */
	private void init() {
		this.toxicitytables = new HashMap<NetlistNode, ToxicityTable<NetlistNode, NetlistNode>>();
	}

	/**
	 * Initializes a newly created TMToxicityEvaluation using the Netlist defined by parameter <i>netlist</i>
	 *
	 * @param netlist the Netlist
	 */
	public TMToxicityEvaluation(final Netlist netlist, final TMActivityEvaluation tmae) {
		this.init();
		if (!netlist.isValid()) {
			throw new RuntimeException("netlist is not valid!");
		}
		this.setTMActivityEvaluation(tmae);
		Activities<NetlistNode> activities = tmae.getActivities();
		List<NetlistNode> outputNodes = new ArrayList<NetlistNode>();
		for(int i = 0; i < netlist.getNumVertex(); i++) {
			NetlistNode node = netlist.getVertexAtIdx(i);
			if (LSResultsUtils.isPrimaryInput(node)
			    ||
			    LSResultsUtils.isPrimaryOutput(node)) {
				continue;
			}
			outputNodes.clear();
			outputNodes.add(node);
			ToxicityTable<NetlistNode, NetlistNode> toxicityTable = new ToxicityTable<NetlistNode, NetlistNode>(activities, outputNodes);
			this.getToxicityTables().put(node, toxicityTable);
		}
		this.evaluate(netlist);
	}

	/**
	 * Interpolates the toxicity for the NetlistNode defined by parameter <i>node</i> at input <i>input</i>
	 *
	 * @param node the NetlistNode
	 * @param input the input
	 */
	private Double interpolateToxicity(final NetlistNode node, final Double input) {
		Double rtn = null;
		SimulatedAnnealingNetlistNodeData data = (SimulatedAnnealingNetlistNodeData) node.getNetlistNodeData();
		Gate gate = (Gate) data.getGate();
		ResponseFunction rf = gate.getResponseFunction();
		ResponseFunctionVariable var = rf.getVariableAtIdx(0);
		org.cellocad.cello2.technologyMapping.algorithm.SimulatedAnnealing.data.ucf.Toxicity toxicity = var.getToxicity();
		int a = 0;
		int b = toxicity.getNumInput() - 1;
		for (int i = 0; i < toxicity.getNumInput(); i++) {
			if ((toxicity.getInputAtIdx(i) < input)
			    &&
			    (Math.abs(toxicity.getInputAtIdx(i) - input) < Math.abs(toxicity.getInputAtIdx(a) - input))) {
				a = i;
			}
			else if ((toxicity.getInputAtIdx(i) > input)
			         &&
			         (Math.abs(toxicity.getInputAtIdx(i) - input) < Math.abs(toxicity.getInputAtIdx(b) - input))) {
				b = i;
			}
		}
		if (a == b) {
			rtn = toxicity.getGrowthAtIdx(a);
		}
		else {
			double r = (input - toxicity.getInputAtIdx(a)) / (toxicity.getInputAtIdx(b) - toxicity.getInputAtIdx(a));
			rtn = toxicity.getGrowthAtIdx(a) + r*(toxicity.getGrowthAtIdx(b) - toxicity.getGrowthAtIdx(a));
		}
		return rtn;
	}

	/**
	 * Evaluates the toxicity table for the NetlistNode defined by parameter <i>node</i>
	 *
	 * @param node the NetlistNode
	 */
	private void evaluateToxicityTable(final NetlistNode node) {
		ToxicityTable<NetlistNode,NetlistNode> toxicityTable = this.getToxicityTables().get(node);
		for (int i = 0; i < toxicityTable.getNumActivities(); i++) {
			Activity<NetlistNode> inputActivity = toxicityTable.getActivityAtIdx(i);
			Toxicity<NetlistNode> outputToxicity = toxicityTable.getToxicityOutput(inputActivity);
			double input = 0.0;
			for (int j = 0; j < node.getNumInEdge(); j++) {
				NetlistEdge edge = node.getInEdgeAtIdx(j);
				NetlistNode src = edge.getSrc();
				ActivityTable<NetlistNode,NetlistNode> table = this.getTMActivityEvaluation().getActivityTable(src);
				Double value = table.getActivityOutput(inputActivity).getActivity(src);
				input += value;
			}
			Double toxicity = this.interpolateToxicity(node,input);
			if (toxicity > D_MAXGROWTH)
				toxicity = D_MAXGROWTH;
			if (toxicity < D_MINGROWTH)
				toxicity = D_MINGROWTH;
			outputToxicity.setToxicity(node,toxicity);
		}
	}

	/**
	 * Evaluates the Netlist defined by parameter <i>netlist</i>
	 *
	 * @param netlist the Netlist
	 */
	protected void evaluate(final Netlist netlist){
		SinkDFS<NetlistNode, NetlistEdge, Netlist> DFS = new SinkDFS<NetlistNode, NetlistEdge, Netlist>(netlist);
		NetlistNode node = null;
		while ((node = DFS.getNextVertex()) != null) {
			if (LSResultsUtils.isPrimaryInput(node)
			    ||
			    LSResultsUtils.isPrimaryOutput(node)) {
				continue;
			}
			evaluateToxicityTable(node);
		}
	}

	public Double getGrowth(final Activity<NetlistNode> activity) {
		Double rtn = D_MAXGROWTH;
		for(NetlistNode node : this.getToxicityTables().keySet()) {
			ToxicityTable<NetlistNode,NetlistNode> table = this.getToxicityTables().get(node);
			Toxicity<NetlistNode> toxicity = table.getToxicityOutput(activity);
			Double value = toxicity.getToxicity(node);
			rtn *= value;
		}
		if (rtn < D_MINGROWTH)
			rtn = D_MINGROWTH;
		return rtn;
	}

	// public Double getMinimumGrowth() {
	// 	Double rtn = D_MAXGROWTH;
	// 	for (NetlistNode node : this.getToxicityTables().keySet()) {
	// 		ToxicityTable<NetlistNode,NetlistNode> toxicitytable = this.getToxicityTable(node);
	// 		for (int i = 0; i < toxicitytable.getNumActivities(); i++) {
	// 			Activity<NetlistNode> input = toxicitytable.getActivityAtIdx(i);
	// 			Toxicity<NetlistNode> output = toxicitytable.getToxicityOutput(input);
	// 			rtn = Math.min(rtn,output.getToxicity(node));
	// 		}
	// 	}
	// 	return rtn;
	// }

	public Double getMinimumGrowth() {
		Double rtn = D_MAXGROWTH;
		Activities<NetlistNode> activities = this.getTMActivityEvaluation().getActivities();
		for (int i = 0; i < activities.getNumActivities(); i++) {
			Activity<NetlistNode> activity = activities.getActivityAtIdx(i);
			rtn = Math.min(rtn,this.getGrowth(activity));
		}
		return rtn;
	}

	protected Map<NetlistNode, ToxicityTable<NetlistNode, NetlistNode>> getToxicityTables(){
		return this.toxicitytables;
	}

	/**
	 * Returns the toxicityTable of NetlistNode defined by parameter <i>node</i>
	 *
	 * @param node the NetlistNode
	 * @return the truthTable of NetlistNode defined by parameter <i>node</i>
	 */
	public ToxicityTable<NetlistNode, NetlistNode> getToxicityTable(final NetlistNode node){
		ToxicityTable<NetlistNode, NetlistNode> rtn = null;
		rtn = this.getToxicityTables().get(node);
		return rtn;
	}

	/**
	 * Getter for <i>tmae</i>
	 * @return value of <i>tmae</i>
	 */
	public TMActivityEvaluation getTMActivityEvaluation() {
		return tmae;
	}

	/**
	 * Setter for <i>tmae</i>
	 * @param tmae the value to set <i>tmae</i>
	 */
	protected void setTMActivityEvaluation(final TMActivityEvaluation tmae) {
		this.tmae = tmae;
	}

	public String toString() {
		String rtn = "";
		rtn += Utils.getNewLine();
		rtn += S_HEADER + Utils.getNewLine();
		rtn += "TMToxicityEvaluation" + Utils.getNewLine();
		rtn += S_HEADER + Utils.getNewLine();
		for (NetlistNode node : this.getToxicityTables().keySet()) {
			rtn += String.format("%-15s",node.getName()) + Utils.getTabCharacter();
			ToxicityTable<NetlistNode,NetlistNode> toxicitytable = this.getToxicityTable(node);
			for (int i = 0; i < toxicitytable.getNumActivities(); i++) {
				Activity<NetlistNode> input = toxicitytable.getActivityAtIdx(i);
				Toxicity<NetlistNode> output = toxicitytable.getToxicityOutput(input);
				rtn += String.format("%.2f",output.getToxicity(node)) + Utils.getTabCharacter();
			}
			rtn += Utils.getNewLine();
		}
		rtn += S_HEADER + Utils.getNewLine();
		rtn += String.format("%-15s","") + Utils.getTabCharacter();
		Activities<NetlistNode> activities = this.getTMActivityEvaluation().getActivities();
		for (int i = 0; i < activities.getNumActivities(); i++) {
			Activity<NetlistNode> activity = activities.getActivityAtIdx(i);
			rtn += String.format("%.2f",this.getGrowth(activity)) + Utils.getTabCharacter();
		}
		rtn += Utils.getNewLine();
		rtn += S_HEADER + Utils.getNewLine();
		return rtn;
	}

	/**
	 *  Writes this instance in CSV format to the writer defined by parameter <i>os</i> with the delimiter equivalent to the parameter <i>delimiter</i>
	 *  @param delimiter the delimiter
	 *  @param os the writer
	 *  @throws IOException If an I/O error occurs
	 */
	public void writeCSV(String delimiter, Writer os) throws IOException {
		String str = "";
		for (NetlistNode node : this.getToxicityTables().keySet()) {
			str += node.getName();
			ToxicityTable<NetlistNode,NetlistNode> toxicitytable = this.getToxicityTable(node);
			for (int i = 0; i < toxicitytable.getNumActivities(); i++) {
				Activity<NetlistNode> input = toxicitytable.getActivityAtIdx(i);
				Toxicity<NetlistNode> output = toxicitytable.getToxicityOutput(input);
				str += delimiter;
				str += String.format("%.2f",output.getToxicity(node));
			}
			str += Utils.getNewLine();
		}
		os.write(str);
	}

	private static final String S_HEADER = "--------------------------------------------";
	private static final double D_MAXGROWTH = 1.00;
	private static final double D_MINGROWTH = 0.01;

	private Map<NetlistNode,ToxicityTable<NetlistNode,NetlistNode>> toxicitytables;
	private TMActivityEvaluation tmae;

}

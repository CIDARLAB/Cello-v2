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
package org.cellocad.v2.results.technologyMapping.activity;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.cellocad.v2.common.CObjectCollection;
import org.cellocad.v2.common.Utils;
import org.cellocad.v2.common.graph.algorithm.SinkDFS;
import org.cellocad.v2.common.target.data.model.EvaluationContext;
import org.cellocad.v2.results.logicSynthesis.LSResultsUtils;
import org.cellocad.v2.results.logicSynthesis.logic.LSLogicEvaluation;
import org.cellocad.v2.results.netlist.Netlist;
import org.cellocad.v2.results.netlist.NetlistEdge;
import org.cellocad.v2.results.netlist.NetlistNode;
import org.cellocad.v2.results.technologyMapping.activity.activitytable.Activities;
import org.cellocad.v2.results.technologyMapping.activity.activitytable.Activity;
import org.cellocad.v2.results.technologyMapping.activity.activitytable.ActivityTable;

/**
 * 
 *
 * @author Timothy Jones
 *
 * @date 2018-05-24
 *
 */
public class TMActivityEvaluation {
	
	/**
	 *  Initialize class members
	 */
	private void init() {
		this.activitytables = new HashMap<NetlistNode, ActivityTable<NetlistNode, NetlistNode>>();
	}
	
	/**
	 *  Initializes a newly created LSLogicEvaluation using the Netlist defined by parameter <i>netlist</i>
	 *  
	 *  @param netlist the Netlist
	 */
	public TMActivityEvaluation(Netlist netlist, LSLogicEvaluation lsle) {
		this.init();
		if (!netlist.isValid()) {
			throw new RuntimeException("netlist is not valid!");
		}
		CObjectCollection<NetlistNode> inputNodes = LSResultsUtils.getPrimaryInputNodes(netlist);
		Activities<NetlistNode> activities = new Activities<NetlistNode>(inputNodes, lsle.getStates());
		this.setActivities(activities);
		List<NetlistNode> outputNodes = new ArrayList<NetlistNode>();
		for(int i = 0; i < netlist.getNumVertex(); i++) {
			NetlistNode node = netlist.getVertexAtIdx(i);
			outputNodes.clear();
			outputNodes.add(node);
			ActivityTable<NetlistNode, NetlistNode> activityTable = new ActivityTable<NetlistNode, NetlistNode>(activities, outputNodes);
			this.getActivityTables().put(node, activityTable);
		}
		this.evaluate(netlist);
	}
	
	/**
	 *  Returns a List of Double representation of the input values for NetlistNode defined by parameter <i>node</i>
	 *  at the state defined by parameter <i>state</i>
	 *  
	 *  @param node the NetlistNode
	 *  @param activity the activity
	 *  @return a List of Double representation of the input values for NetlistNode defined by parameter <i>node</i>
	 *  at the activity defined by parameter <i>activity</i>
	 */
	public List<Double> getInputActivity(final NetlistNode node, final Activity<NetlistNode> activity) {
		List<Double> rtn = new ArrayList<Double>();
		for (int i = 0; i < node.getNumInEdge(); i++) {
			NetlistNode inputNode = node.getInEdgeAtIdx(i).getSrc();
			ActivityTable<NetlistNode, NetlistNode> activityTable = this.getActivityTables().get(inputNode);
			activityTable.getActivityOutput(activity);
			Activity<NetlistNode> outputActivity = activityTable.getActivityOutput(activity);
			if (outputActivity.getNumActivityPosition() != 1) {
				throw new RuntimeException("Invalid number of output(s)!");
			}
			rtn.add(outputActivity.getActivity(inputNode));
		}
		return rtn;
	}

	private void evaluateActivityTable(final NetlistNode node, final EvaluationContext ec) {
		Double result = null;
		ec.setNode(node);
		// ec.setState(state);
		ActivityTable<NetlistNode, NetlistNode> activityTable = this.getActivityTables().get(node);
		for (int i = 0; i < activityTable.getNumActivities(); i++) {
			Activity<NetlistNode> inputActivity = activityTable.getStateAtIdx(i);
			Activity<NetlistNode> outputActivity = activityTable.getActivityOutput(inputActivity);
			if (outputActivity.getNumActivityPosition() != 1) {
				throw new RuntimeException("Invalid number of output(s)!");
			}
			Utils.isNullRuntimeException(result, "result");
			if (!outputActivity.setActivity(node, result)) {
				throw new RuntimeException("Node does not exist");
			}
		}
	}

	/**
	 * Evaluates the Netlist defined by parameter <i>netlist</i>
	 * 
	 * @param netlist the Netlist
	 */
	protected void evaluate(Netlist netlist) {
		SinkDFS<NetlistNode, NetlistEdge, Netlist> DFS = new SinkDFS<NetlistNode, NetlistEdge, Netlist>(netlist);
		NetlistNode node = null;
		EvaluationContext ec = new EvaluationContext();
		node = DFS.getNextVertex();
		while (node != null) {
			evaluateActivityTable(node, ec);
			node = DFS.getNextVertex();
		}
	}

	protected Map<NetlistNode,ActivityTable<NetlistNode,NetlistNode>> getActivityTables(){
		return this.activitytables;
	}

	protected void setActivities(Activities<NetlistNode> activities){
		this.activities = activities;
	}
	
	/**
	 *  Getter for <i>states</i>
	 *  @return the states of this instance
	 */
	public Activities<NetlistNode> getActivities(){
		return this.activities;
	}
	
	/**
	 *  Returns the truthTable of NetlistNode defined by parameter <i>node</i>
	 *  
	 *  @param node the NetlistNode
	 *  @return the truthTable of NetlistNode defined by parameter <i>node</i>
	 */
	public ActivityTable<NetlistNode, NetlistNode> getActivityTable(final NetlistNode node){
		ActivityTable<NetlistNode, NetlistNode> rtn = null;
		rtn = this.getActivityTables().get(node);
		return rtn;
	}
	
	public String toString() {
		String rtn = "";
		rtn += Utils.getNewLine();
		rtn += S_HEADER + Utils.getNewLine();
		rtn += "TMActivityEvaluation" + Utils.getNewLine();
		rtn += S_HEADER + Utils.getNewLine();
		for (NetlistNode node : this.getActivityTables().keySet()) {
			rtn += String.format("%-15s",node.getName()) + Utils.getTabCharacter();
			ActivityTable<NetlistNode,NetlistNode> activitytable = this.getActivityTables().get(node);
			for (int i = 0; i < activitytable.getNumActivities(); i++) {
				Activity<NetlistNode> input = activitytable.getStateAtIdx(i);
				Activity<NetlistNode> output = activitytable.getActivityOutput(input);
				rtn += String.format("%.4f", output.getActivity(node)) + Utils.getTabCharacter();
			}
			rtn += Utils.getNewLine();
		}
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
		for (NetlistNode node : this.getActivityTables().keySet()) {
			str += node.getName();
			ActivityTable<NetlistNode,NetlistNode> activitytable = this.getActivityTable(node);
			for (int i = 0; i < activitytable.getNumActivities(); i++) {
				Activity<NetlistNode> input = activitytable.getStateAtIdx(i);
				Activity<NetlistNode> output = activitytable.getActivityOutput(input);
				str += delimiter;
				str += String.format("%1.5e",output.getActivity(node));
			}
			str += Utils.getNewLine();
		}
		os.write(str);
	}

	private static final String S_HEADER = "--------------------------------------------";


	private Map<NetlistNode, ActivityTable<NetlistNode, NetlistNode>> activitytables;
	private Activities<NetlistNode> activities;
}

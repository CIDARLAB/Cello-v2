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
package results.technologyMapping.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import common.CObjectCollection;
import common.Utils;
import common.graph.algorithm.BFS;
import results.logicSynthesis.LSResults;
import results.logicSynthesis.LSResultsUtils;
import results.logicSynthesis.logic.LSLogicEvaluation;
import results.logicSynthesis.logic.truthtable.State;
import results.logicSynthesis.logic.truthtable.TruthTable;
import results.netlist.Netlist;
import results.netlist.NetlistEdge;
import results.netlist.NetlistNode;
import results.technologyMapping.activity.activitytable.Activities;
import results.technologyMapping.activity.activitytable.Activity;
import results.technologyMapping.activity.activitytable.ActivityTable;
import results.technologyMapping.activity.signal.SensorSignals;

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
	public TMActivityEvaluation (Netlist netlist, SensorSignals<NetlistNode> sensorSignals, LSLogicEvaluation lsle) {
		this.init();
		if (!netlist.isValid()) {
			throw new RuntimeException("netlist is not valid!");
		}
		CObjectCollection<NetlistNode> inputNodes = LSResultsUtils.getPrimaryInputNodes(netlist);
		Activities<NetlistNode> activities = new Activities<NetlistNode>(inputNodes, lsle.getStates(), sensorSignals);
		this.setActivities(activities);
		List<NetlistNode> outputNodes = new ArrayList<NetlistNode>();
		for(int i = 0; i < netlist.getNumVertex(); i++) {
			NetlistNode node = netlist.getVertexAtIdx(i);
			outputNodes.clear();
			outputNodes.add(node);
			ActivityTable<NetlistNode, NetlistNode> activityTable = new ActivityTable<NetlistNode, NetlistNode>(activities, outputNodes);
			this.getActivityTables().put(node, activityTable);
		}
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

	protected Map<NetlistNode,ActivityTable<NetlistNode,NetlistNode>> getActivityTables(){
		return this.activitytables;
	}

	protected void setActivities(Activities<NetlistNode> activities){
		this.activities = activities;
	}
	
	/**
	 *  Getter for <i>activities</i>
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
				Activity<NetlistNode> input = activitytable.getActivityAtIdx(i);
				Activity<NetlistNode> output = activitytable.getActivityOutput(input);
				rtn += String.format("%.4f", output.getActivity(node)) + Utils.getTabCharacter();
			}
			rtn += Utils.getNewLine();
		}
		rtn += S_HEADER + Utils.getNewLine();
		return rtn;
	}
	
	private static final String S_HEADER = "--------------------------------------------";


	private Map<NetlistNode, ActivityTable<NetlistNode, NetlistNode>> activitytables;
	private Activities<NetlistNode> activities;
}

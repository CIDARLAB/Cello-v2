/**
 * Copyright (C) 2017 Massachusetts Institute of Technology (MIT)
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
package org.cellocad.cello2.results.logicSynthesis.logic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.cellocad.cello2.common.CObjectCollection;
import org.cellocad.cello2.common.Utils;
import org.cellocad.cello2.common.graph.algorithm.SinkDFS;
import org.cellocad.cello2.results.logicSynthesis.LSResults;
import org.cellocad.cello2.results.logicSynthesis.LSResultsUtils;
import org.cellocad.cello2.results.logicSynthesis.logic.truthtable.State;
import org.cellocad.cello2.results.logicSynthesis.logic.truthtable.States;
import org.cellocad.cello2.results.logicSynthesis.logic.truthtable.TruthTable;
import org.cellocad.cello2.results.netlist.Netlist;
import org.cellocad.cello2.results.netlist.NetlistEdge;
import org.cellocad.cello2.results.netlist.NetlistNode;

/**
 * The LSLogicEvaluation class is class evaluating the logic of a netlist in the <i>logicSynthesis</i> stage.
 * 
 * @author Vincent Mirian
 * 
 * @date 2018-05-21
 *
 */
public class LSLogicEvaluation {
	
	/**
	 *  Initialize class members
	 */
	private void init() {
		this.truthtables = new HashMap<NetlistNode, TruthTable<NetlistNode, NetlistNode>>();
	}
	
	/**
	 *  Initializes a newly created LSLogicEvaluation using the Netlist defined by parameter <i>netlist</i>
	 *  
	 *  @param netlist the Netlist
	 */
	public LSLogicEvaluation (Netlist netlist) {
		this.init();
		if (!netlist.isValid()) {
			throw new RuntimeException("netlist is not valid!");
		}
		CObjectCollection<NetlistNode> inputNodes = LSResultsUtils.getPrimaryInputNodes(netlist);
		Boolean One = new Boolean(true);
		Boolean Zero = new Boolean(false);
		States<NetlistNode> states = new States<NetlistNode>(inputNodes, One, Zero);
		this.setStates(states);
		List<NetlistNode> outputNodes = new ArrayList<NetlistNode>();
		for(int i = 0; i < netlist.getNumVertex(); i++) {
			NetlistNode node = netlist.getVertexAtIdx(i);
			outputNodes.clear();
			outputNodes.add(node);
			TruthTable<NetlistNode, NetlistNode> truthTable = new TruthTable<NetlistNode, NetlistNode>(states, outputNodes);
			this.getTruthTables().put(node, truthTable);
		}
		this.evaluate(netlist);
	}

	/**
	 *  Returns a Boolean representation of the evaluation of the NodeType defined by <i>nodeType</i>
	 *  with input defined by parameters <i>inputs</i>
	 *  
	 *  @param inputs a List of inputs
	 *  @param nodeType the NodeType
	 *  @return a Boolean representation of the evaluation of the NodeType defined by <i>nodeType</i>
	 *  with input defined by parameters <i>inputs</i>
	 */
	private Boolean computeLogic(final List<Boolean> inputs, final String nodeType) {
		Boolean rtn = inputs.get(0);
		for (int i = 1; i < inputs.size(); i++) {
			Boolean value = inputs.get(i);
			switch (nodeType) {
				case LSResults.S_AND:{
					rtn = rtn && value;
					break;
				}
				case LSResults.S_OR:{
					rtn = rtn || value;
					break;
				}
				case LSResults.S_XOR:{
					rtn = rtn ^ value;
					break;
				}
				default:{
					throw new RuntimeException("Unknown nodeType");
				}
			}
		}
		return rtn;
	}

	/**
	 *  Returns a List of Boolean representation of the input values for NetlistNode defined by parameter <i>node</i>
	 *  at the state defined by parameter <i>state</i>
	 *  
	 *  @param node the NetlistNode
	 *  @param state the state
	 *  @return a List of Boolean representation of the input values for NetlistNode defined by parameter <i>node</i>
	 *  at the state defined by parameter <i>state</i>
	 */
	private List<Boolean> getInputLogic(final NetlistNode node, final State<NetlistNode> state) {
		List<Boolean> rtn = new ArrayList<Boolean>();
		for (int i = 0; i < node.getNumInEdge(); i++) {
			NetlistNode inputNode = node.getInEdgeAtIdx(i).getSrc();
			TruthTable<NetlistNode, NetlistNode> truthTable = this.getTruthTables().get(inputNode);
			truthTable.getStateOutput(state);
			State<NetlistNode> outputState = truthTable.getStateOutput(state);
			if (outputState.getNumStatePosition() != 1) {
				throw new RuntimeException("Invalid number of output(s)!");
			}
			rtn.add(outputState.getState(inputNode));
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
	private Boolean computePrimaryInput(final NetlistNode node, final State<NetlistNode> state) {
		Boolean rtn = null;
		List<Boolean> inputList = this.getInputLogic(node, state);
		if (inputList.size() == 0) {
			rtn = state.getState(node);
		}
		return rtn;
	}
	
	/**
	 *  Returns the evaluation for a Primary Output for NetlistNode defined by parameter <i>node</i>
	 *  at the state defined by parameter <i>state</i>
	 *  
	 *  @param node the NetlistNode
	 *  @param state the state
	 *  @return the evaluation for a Primary Output for NetlistNode defined by parameter <i>node</i>
	 *  at the state defined by parameter <i>state</i>
	 */
	private Boolean computePrimaryOutput(final NetlistNode node, final State<NetlistNode> state) {
		Boolean rtn = null;
		List<Boolean> inputList = this.getInputLogic(node, state);
		if (inputList.size() == 1) {
			rtn = inputList.get(0);
		}
		return rtn;
	}
	
	/**
	 *  Returns the evaluation for a NOT NodeType for NetlistNode defined by parameter <i>node</i>
	 *  at the state defined by parameter <i>state</i>
	 *  
	 *  @param node the NetlistNode
	 *  @param state the state
	 *  @return the evaluation for a NOT NodeType for NetlistNode defined by parameter <i>node</i>
	 *  at the state defined by parameter <i>state</i>
	 */
	private Boolean computeNOT(final NetlistNode node, final State<NetlistNode> state) {
		Boolean rtn = null;
		List<Boolean> inputList = this.getInputLogic(node, state);
		if (inputList.size() == 1) {
			rtn = inputList.get(0);
			rtn = !(rtn);
		}
		return rtn;
	}
	
	/**
	 *  Returns the evaluation for an AND NodeType for NetlistNode defined by parameter <i>node</i>
	 *  at the state defined by parameter <i>state</i>
	 *  
	 *  @param node the NetlistNode
	 *  @param state the state
	 *  @return the evaluation for an AND NodeType for NetlistNode defined by parameter <i>node</i>
	 *  at the state defined by parameter <i>state</i>
	 */
	private Boolean computeAND(final NetlistNode node, final State<NetlistNode> state) {
		Boolean rtn = null;
		List<Boolean> inputList = this.getInputLogic(node, state);
		if (inputList.size() > 1) {
			rtn = this.computeLogic(inputList, LSResults.S_AND);
		}
		return rtn;
	}
	
	/**
	 *  Returns the evaluation for an NAND NodeType for NetlistNode defined by parameter <i>node</i>
	 *  at the state defined by parameter <i>state</i>
	 *  
	 *  @param node the NetlistNode
	 *  @param state the state
	 *  @return the evaluation for an NAND NodeType for NetlistNode defined by parameter <i>node</i>
	 *  at the state defined by parameter <i>state</i>
	 */
	private Boolean computeNAND(final NetlistNode node, final State<NetlistNode> state) {
		Boolean rtn = null;
		List<Boolean> inputList = this.getInputLogic(node, state);
		if (inputList.size() > 1) {
			rtn = this.computeLogic(inputList, LSResults.S_AND);
			rtn = !(rtn);
		}
		return rtn;
	}

	/**
	 *  Returns the evaluation for an OR NodeType for NetlistNode defined by parameter <i>node</i>
	 *  at the state defined by parameter <i>state</i>
	 *  
	 *  @param node the NetlistNode
	 *  @param state the state
	 *  @return the evaluation for an OR NodeType for NetlistNode defined by parameter <i>node</i>
	 *  at the state defined by parameter <i>state</i>
	 */
	private Boolean computeOR(final NetlistNode node, final State<NetlistNode> state) {
		Boolean rtn = null;
		List<Boolean> inputList = this.getInputLogic(node, state);
		if (inputList.size() > 1) {
			rtn = this.computeLogic(inputList, LSResults.S_OR);
		}
		return rtn;
	}
	
	/**
	 *  Returns the evaluation for an NOR NodeType for NetlistNode defined by parameter <i>node</i>
	 *  at the state defined by parameter <i>state</i>
	 *  
	 *  @param node the NetlistNode
	 *  @param state the state
	 *  @return the evaluation for an NOR NodeType for NetlistNode defined by parameter <i>node</i>
	 *  at the state defined by parameter <i>state</i>
	 */
	private Boolean computeNOR(final NetlistNode node, final State<NetlistNode> state) {
		Boolean rtn = null;
		List<Boolean> inputList = this.getInputLogic(node, state);
		if (inputList.size() > 1) {
			rtn = this.computeLogic(inputList, LSResults.S_OR);
			rtn = !(rtn);
		}
		return rtn;
	}
	
	/**
	 *  Returns the evaluation for an XOR NodeType for NetlistNode defined by parameter <i>node</i>
	 *  at the state defined by parameter <i>state</i>
	 *  
	 *  @param node the NetlistNode
	 *  @param state the state
	 *  @return the evaluation for an XOR NodeType for NetlistNode defined by parameter <i>node</i>
	 *  at the state defined by parameter <i>state</i>
	 */
	private Boolean computeXOR(final NetlistNode node, final State<NetlistNode> state) {
		Boolean rtn = null;
		List<Boolean> inputList = this.getInputLogic(node, state);
		if (inputList.size() > 1) {
			rtn = this.computeLogic(inputList, LSResults.S_XOR);
		}
		return rtn;
	}
	
	/**
	 *  Returns the evaluation for an XNOR NodeType for NetlistNode defined by parameter <i>node</i>
	 *  at the state defined by parameter <i>state</i>
	 *  
	 *  @param node the NetlistNode
	 *  @param state the state
	 *  @return the evaluation for an XNOR NodeType for NetlistNode defined by parameter <i>node</i>
	 *  at the state defined by parameter <i>state</i>
	 */	
	private Boolean computeXNOR(final NetlistNode node, final State<NetlistNode> state) {
		Boolean rtn = null;
		List<Boolean> inputList = this.getInputLogic(node, state);
		if (inputList.size() > 1) {
			rtn = this.computeLogic(inputList, LSResults.S_XOR);
			rtn = !(rtn);
		}
		return rtn;
	}
	
	/**
	 *  Evaluates the truth table for the NetlistNode defined by parameter <i>node</i>
	 *  
	 *  @param node the NetlistNode
	 */
	private void evaluateTruthTable(final NetlistNode node) {
		Boolean result = null;
		final String nodeType = node.getResultNetlistNodeData().getNodeType();
		TruthTable<NetlistNode, NetlistNode> truthTable = this.getTruthTables().get(node);
		for (int i = 0; i < truthTable.getNumStates(); i ++) {
			State<NetlistNode> inputState = truthTable.getStateAtIdx(i);
			State<NetlistNode> outputState = truthTable.getStateOutput(inputState);
			if (outputState.getNumStatePosition() != 1) {
				throw new RuntimeException("Invalid number of output(s)!");
			}
			switch (nodeType) {
				case LSResults.S_PRIMARYINPUT:{
					result = this.computePrimaryInput(node, inputState);
					break;
				}
				case LSResults.S_PRIMARYOUTPUT:{
					result = this.computePrimaryOutput(node, inputState);
					break;
				}
				case LSResults.S_INPUT:{
					continue;
				}
				case LSResults.S_OUTPUT:{
					continue;
				}
				case LSResults.S_NOT:{
					result = this.computeNOT(node, inputState);
					break;
				}
				case LSResults.S_AND:{
					result = this.computeAND(node, inputState);
					break;
				}
				case LSResults.S_NAND:{
					result = this.computeNAND(node, inputState);
					break;
				}
				case LSResults.S_OR:{
					result = this.computeOR(node, inputState);
					break;
				}
				case LSResults.S_NOR:{
					result = this.computeNOR(node, inputState);
					break;
				}
				case LSResults.S_XOR:{
					result = this.computeXOR(node, inputState);
					break;
				}
				case LSResults.S_XNOR:{
					result = this.computeXNOR(node, inputState);
					break;
				}
				default:{
					throw new RuntimeException("Unknown nodeType");
				}
			}
			Utils.isNullRuntimeException(result, "result");
			if (!outputState.setState(node, result)) {
				throw new RuntimeException("Node does not exist");
			}
		}
	}
	
	/**
	 *  Evaluates the Netlist defined by parameter <i>netlist</i>
	 *  
	 *  @param netlist the Netlist
	 */
	protected void evaluate(Netlist netlist){
		SinkDFS<NetlistNode, NetlistEdge, Netlist> DFS = new SinkDFS<NetlistNode, NetlistEdge, Netlist>(netlist);
		NetlistNode node = null;
		node = DFS.getNextVertex();
		while (node != null) {
			evaluateTruthTable(node);
			node = DFS.getNextVertex();
		}
	}

	protected Map<NetlistNode, TruthTable<NetlistNode, NetlistNode>> getTruthTables(){
		return this.truthtables;
	}

	protected void setStates(States<NetlistNode> states){
		this.states = states;
	}
	
	/**
	 *  Getter for <i>states</i>
	 *  @return the states of this instance
	 */
	public States<NetlistNode> getStates(){
		return this.states;
	}
	
	/**
	 *  Returns the truthTable of NetlistNode defined by parameter <i>node</i>
	 *  
	 *  @param node the NetlistNode
	 *  @return the truthTable of NetlistNode defined by parameter <i>node</i>
	 */
	public TruthTable<NetlistNode, NetlistNode> getTruthTable(final NetlistNode node){
		TruthTable<NetlistNode, NetlistNode> rtn = null;
		rtn = this.getTruthTables().get(node);
		return rtn;
	}
	
	public String toString() {
		String rtn = "";
		rtn += Utils.getNewLine();
		rtn += S_HEADER + Utils.getNewLine();
		rtn += "LSLogicEvaluation" + Utils.getNewLine();
		rtn += S_HEADER + Utils.getNewLine();
		for (NetlistNode node : this.getTruthTables().keySet()) {
			rtn += String.format("%-15s",node.getName()) + Utils.getTabCharacter();
			TruthTable<NetlistNode,NetlistNode> truthtable = this.getTruthTables().get(node);
			for (int i = 0; i < truthtable.getNumStates(); i++) {
				State<NetlistNode> input = truthtable.getStateAtIdx(i);
				State<NetlistNode> output = truthtable.getStateOutput(input);
				rtn += output.getState(node) + Utils.getTabCharacter();
			}
			rtn += Utils.getNewLine();
		}
		rtn += S_HEADER + Utils.getNewLine();
		return rtn;
	}
	
	private static final String S_HEADER = "--------------------------------------------";

	private Map<NetlistNode, TruthTable<NetlistNode, NetlistNode>> truthtables;
	private States<NetlistNode> states;
}

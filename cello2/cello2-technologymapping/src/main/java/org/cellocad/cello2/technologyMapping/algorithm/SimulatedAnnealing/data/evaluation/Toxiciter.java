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
package org.cellocad.cello2.technologyMapping.algorithm.SimulatedAnnealing.data.evaluation;

import java.util.HashMap;
import java.util.Map;

import org.cellocad.cello2.common.graph.algorithm.BFS;
import org.cellocad.cello2.common.netlist.Netlist;
import org.cellocad.cello2.common.netlist.NetlistEdge;
import org.cellocad.cello2.common.netlist.NetlistNode;
import org.cellocad.cello2.results.logicSynthesis.logic.truthtable.State;
import org.cellocad.cello2.results.logicSynthesis.logic.truthtable.States;

/**
 * The Toxiciter class the toxicity of a netlist used within the <i>SimulatedAnnealing</i> algorithm class of the <i>technologyMapping</i> stage.
 * 
 * @author Vincent Mirian
 * 
 * @date 2018-05-21
 *
 */
public class Toxiciter {

	private void init() {
		this.toxTable = new HashMap<State<NetlistNode>, Double>();
	}
	
	public Toxiciter (Netlist netlist, States<NetlistNode> states) {
		this.init();
		if (!netlist.isValid()) {
			throw new RuntimeException("netlist is not valid!");
		}
		this.setStates(states);
		this.evaluate(netlist);
	}

	protected void evaluate(Netlist netlist){
		States<NetlistNode> states = this.getStates();
		for (int i = 0; i < states.getNumStates(); i++) {
			State<NetlistNode> state = states.getStateAtIdx(i);
			for (int j = 0; j < netlist.getNumVertex(); j++) {
				
			}
		}
		BFS<NetlistNode, NetlistEdge, Netlist> BFS = new BFS<NetlistNode, NetlistEdge, Netlist>(netlist);
		NetlistNode node = null;
		node = BFS.getNextVertex();
		while (node != null) {
			//TODO:
			//evaluateToxicity(node);
			node = BFS.getNextVertex();
		}
	}

	protected Map<State<NetlistNode>, Double> getToxTable(){
		return this.toxTable;
	}
	
	public Double getToxicityAtState(final State<NetlistNode> state){
		Double rtn = null;
		rtn = this.getToxTable().get(state);
		return rtn;
	}
	
	protected void setStates(States<NetlistNode> states){
		this.states = states;
	}

	public States<NetlistNode> getStates(){
		return this.states;
	}
	
	private Map<State<NetlistNode>, Double> toxTable;
	private States<NetlistNode> states;
	
}

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
package org.cellocad.v2.results.logicSynthesis.logic.truthtable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.cellocad.v2.common.CObject;

/**
 * The TruthTable class represents a truth table within the <i>SimulatedAnnealing</i> algorithm class of the <i>technologyMapping</i> stage.
 * @param Input input type index
 * @param Output output type index
 * 
 * @author Vincent Mirian
 * 
 * @date 2018-05-21
 *
 */
public class TruthTable <Input, Output> extends CObject{

	/**
	 *  Initialize class members
	 */
	private void init() {
		truthTableMap = new HashMap<State<Input>, State<Output>>();
		states = new ArrayList<State<Input>>();
	}

	/**
	 *  Initializes a newly created TruthTable with the list of inputs defined by parameter <i>inputs</i>
	 *  a list of outputs defined by parameter <i>outputs</i>.
	 *  
	 *  @param inputs the List of inputs
	 *  @param outputs the List of outputs
	 */
	public TruthTable(final List<State<Input>> inputs, final List<Output> outputs) {
		init();
		for (int i = 0; i < inputs.size(); i ++) {
			State<Input> InputState = inputs.get(i);
			State<Output> OutputState = new State<Output>(outputs, InputState.getOne(), InputState.getZero());
			this.getTruthTableMap().put(InputState, OutputState);
			this.getStates().add(InputState);
		}
	}

	/**
	 *  Initializes a newly created TruthTable with the list of states defined by parameter <i>states</i>
	 *  a list of outputs defined by parameter <i>outputs</i>.
	 *  
	 *  @param states the List of states
	 *  @param outputs the List of outputs
	 */
	public TruthTable(final States<Input> states, final List<Output> outputs) {
		init();
		for (int i = 0; i < states.getNumStates(); i ++) {
			State<Input> InputState = states.getStateAtIdx(i);
			State<Output> OutputState = new State<Output>(outputs, InputState.getOne(), InputState.getZero());
			this.getTruthTableMap().put(InputState, OutputState);
			this.getStates().add(InputState);
		}
	}

	/*public TruthTable(final List<Input> inputs, final List<Output> outputs, Boolean One, Boolean Zero) {
		init();
		Double result = Math.pow(2,inputs.size());
		int size = result.intValue();
		for (int i = 0; i < size; i ++) {
			State<Input> InputState = new State<Input>(inputs, One, Zero, i);
			State<Output> OutputState = new State<Output>(outputs);
			this.getTruthTableMap().put(InputState, OutputState);
			this.getStates().add(InputState);
		}
	}*/

	/*
	 * StateMap
	 */
	/**
	 *  Getter for <i>truthTableMap</i>
	 *  @return the truthTableMap of this instance
	 */
	protected Map<State<Input>, State<Output>> getTruthTableMap() {
		return truthTableMap;
	}

	/**
	 *  Getter for <i>states</i>
	 *  @return the states of this instance
	 */
	protected List<State<Input>> getStates() {
		return states;
	}

	/**
	 * Returns the output state for the state defined by parameter <i>state</i>
	 * 
	 * @param state the input state
	 * @return the output state for the state defined by parameter <i>state</i>, otherwise null
	 */
	public State<Output> getStateOutput(final State<Input> state){
		State<Output> rtn = null;
		rtn = this.getTruthTableMap().get(state);
		return rtn;
	}

	/**
	 * Returns the State<Input> at the specified position in this instance.
	 * 
	 * @param index index of the State<Input> to return
	 * @return if the index is within the bounds (0 <= bounds < this.getNumStates()), returns the State<Input> at the specified position in this instance, otherwise null
	 */
	public State<Input> getStateAtIdx(final int index){
		State<Input> rtn = null;
		if (
				(0 <= index)
				&&
				(index < this.getNumStates())
				) {
			rtn = this.getStates().get(index);
		}
		return rtn;
	}

	/**
	 * Returns the number of states in this instance.
	 * 
	 * @return the number of states in this instance.
	 */
	public int getNumStates() {
		return this.getStates().size();
	}

	List<State<Input>> states;
	Map <State<Input>, State<Output>> truthTableMap;
}
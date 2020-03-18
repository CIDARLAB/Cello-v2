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
package org.cellocad.v2.technologyMapping.algorithm.SimulatedAnnealing.data.toxicity.toxicitytable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.cellocad.v2.results.logicSynthesis.logic.truthtable.State;
import org.cellocad.v2.results.logicSynthesis.logic.truthtable.States;

/**
 * The ToxicityTable class represents table of states within the <i>SimulatedAnnealing</i> algorithm class of the <i>technologyMapping</i> stage.
 * @param Input input type index
 * @param Output output type index
 *
 * @author Timothy Jones
 *
 * @date 2019-02-03
 *
 */
public class ToxicityTable<Input,Output> {

	/**
	 * Initialize class members
	 */
	private void init() {
		toxicityTableMap = new HashMap<State<Input>, Toxicity<Output>>();
		states = new ArrayList<State<Input>>();
	}

	/**
	 * Initializes a newly created ToxicityTable with the list of inputs defined by parameter <i>inputs</i>
	 * a list of outputs defined by parameter <i>outputs</i>.
	 *
	 * @param inputs the List of inputs
	 * @param outputs the List of outputs
	 */
	public ToxicityTable(final List<State<Input>> inputs, final List<Output> outputs) {
		this.init();
		for (int i = 0; i < inputs.size(); i ++) {
			State<Input> InputState = inputs.get(i);
			Toxicity<Output> OutputToxicity = new Toxicity<Output>(outputs);
			this.getToxicityTableMap().put(InputState, OutputToxicity);
			this.getStates().add(InputState);
		}
	}

	/**
	 * Initializes a newly created ToxicityTable with the list of states defined by parameter <i>states</i>
	 * a list of outputs defined by parameter <i>outputs</i>.
	 *
	 * @param states the List of states
	 * @param outputs the List of outputs
	 */
	public ToxicityTable(final States<Input> states, final List<Output> outputs) {
		init();
		for (int i = 0; i < states.getNumStates(); i++) {
			State<Input> InputState = states.getStateAtIdx(i);
			Toxicity<Output> OutputToxicity = new Toxicity<Output>(outputs);
			this.getToxicityTableMap().put(InputState, OutputToxicity);
			this.getStates().add(InputState);
		}
	}

	/*
	 * ToxicityMap
	 */
	/**
	 * Getter for <i>toxicityTableMap</i>
	 * @return the toxicityTableMap of this instance
	 */
	protected Map<State<Input>, Toxicity<Output>> getToxicityTableMap() {
		return toxicityTableMap;
	}

	/**
	 * Getter for <i>states</i>
	 * @return the states of this instance
	 */
	protected List<State<Input>> getStates() {
		return states;
	}

	/**
	 * Returns the output toxicity for the activity defined by parameter <i>activity</i>
	 *
	 * @param state the input activity
	 * @return the output toxicity for the activity defined by parameter <i>activity</i>, otherwise null
	 */
	public Toxicity<Output> getToxicityOutput(final State<Input> state){
		Toxicity<Output> rtn = null;
		rtn = this.getToxicityTableMap().get(state);
		return rtn;
	}

	/**
	 * Returns the Activity<Input> at the specified position in this instance.
	 *
	 * @param index index of the Activity<Input> to return
	 * @return if the index is within the bounds (0 <= bounds < this.getNumActivities()), returns the Activity<Input> at the specified position in this instance, otherwise null
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
	Map<State<Input>, Toxicity<Output>> toxicityTableMap;
}

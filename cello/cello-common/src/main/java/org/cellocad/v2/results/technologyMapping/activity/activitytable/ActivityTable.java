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
package org.cellocad.v2.results.technologyMapping.activity.activitytable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.cellocad.v2.results.logicSynthesis.logic.truthtable.State;
import org.cellocad.v2.results.logicSynthesis.logic.truthtable.States;

/**
 * The ActivityTable class represents a mapping between node state and node
 * activity.
 * 
 * @param Input  input type index
 * @param Output output type index
 *
 * @author Timothy Jones
 *
 * @date 2018-05-24
 *
 */
public class ActivityTable<Input, Output> {

	/**
	 *  Initialize class members
	 */
	private void init() {
		activityTableMap = new HashMap<State<Input>, Activity<Output>>();
		states = new ArrayList<State<Input>>();
	}

	/**
	 *  Initializes a newly created ActivityTable with the list of inputs defined by parameter <i>inputs</i>
	 *  a list of outputs defined by parameter <i>outputs</i>.
	 *  
	 *  @param inputs the List of inputs
	 *  @param outputs the List of outputs
	 */
	public ActivityTable(final List<State<Input>> inputs, final List<Output> outputs) {
		init();
		for (int i = 0; i < inputs.size(); i ++) {
			State<Input> InputActivity = inputs.get(i);
			Activity<Output> OutputActivity = new Activity<Output>(outputs);
			this.getActivityTableMap().put(InputActivity, OutputActivity);
			this.getStates().add(InputActivity);
		}
	}

	/**
	 * Initializes a newly created ActivityTable with the list of states defined by
	 * parameter <i>states</i> a list of outputs defined by parameter
	 * <i>outputs</i>.
	 * 
	 * @param states  the List of states
	 * @param outputs the List of outputs
	 */
	public ActivityTable(final States<Input> states, final List<Output> outputs) {
		init();
		for (int i = 0; i < states.getNumStates(); i++) {
			State<Input> InputState = states.getStateAtIdx(i);
			Activity<Output> OutputActivity = new Activity<Output>(outputs);
			this.getActivityTableMap().put(InputState, OutputActivity);
			this.getStates().add(InputState);
		}
	}

	/*
	 * ActivityMap
	 */
	/**
	 *  Getter for <i>activityTableMap</i>
	 *  @return the activityTableMap of this instance
	 */
	protected Map<State<Input>, Activity<Output>> getActivityTableMap() {
		return activityTableMap;
	}

	/**
	 * Getter for <i>states</i>
	 * 
	 * @return the states of this instance
	 */
	protected List<State<Input>> getStates() {
		return states;
	}

	/**
	 * Returns the output activity for the activity defined by parameter <i>activity</i>
	 * 
	 * @param activity the input activity
	 * @return the output activity for the activity defined by parameter <i>activity</i>, otherwise null
	 */
	public Activity<Output> getActivityOutput(final State<Input> state) {
		Activity<Output> rtn = null;
		rtn = this.getActivityTableMap().get(state);
		return rtn;
	}

	/**
	 * Returns the State<Input> at the specified position in this instance.
	 * 
	 * @param index index of the State<Input> to return
	 * @return if the index is within the bounds (0 <= bounds <
	 *         this.getNumStates()), returns the State<Input> at the specified
	 *         position in this instance, otherwise null
	 */
	public State<Input> getStateAtIdx(final int index) {
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
	Map<State<Input>, Activity<Output>> activityTableMap;
}

/**
 * Copyright (C) 2017 Boston University (BU)
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
import java.util.List;

import org.cellocad.v2.results.logicSynthesis.logic.truthtable.State;
import org.cellocad.v2.results.logicSynthesis.logic.truthtable.States;
import org.cellocad.v2.results.technologyMapping.activity.signal.SensorSignals;

/**
 * The Activity class contains all the state of a netlist used within the <i>SimulatedAnnealing</i> algorithm class of the <i>technologyMapping</i> stage.
 *
 * @author Timothy Jones
 *
 * @date 2018-05-23
 *
 */
public class Activities <T>{

	/**
	 *  Initialize class members
	 */
	private void init() {
		activities = new ArrayList<Activity<T>>();
	}

	public Activities(final List<T> inputs, final States<T> states, final SensorSignals<T> signals) {
		init();
		for (int i = 0; i < states.getNumStates(); i++) {
			State<T> state = states.getStateAtIdx(i);
			Activity<T> activity = new Activity<T>(inputs, state, signals);
			this.getActivities().add(activity);
		}
	}

	/**
	 *  Getter for <i>activities</i>
	 *  @return the states of this instance
	 */
	protected List<Activity<T>> getActivities() {
		return activities;
	}
	
	/**
	 * Returns the Activity<T> at the specified position in this instance.
	 * 
	 * @param index index of the Activity<T> to return
	 * @return if the index is within the bounds (0 <= bounds < this.getNumActivities()), returns the Activity<T> at the specified position in this instance, otherwise null
	 */
	public Activity<T> getActivityAtIdx(final int index){
		Activity<T> rtn = null;
		if (
				(0 <= index)
				&&
				(index < this.getNumActivities())
				) {
			rtn = this.getActivities().get(index);
		}
		return rtn;
	}
	
	/**
	 * Returns the number of Activity<T> in this instance.
	 * 
	 * @return the number of Activity<T> in this instance.
	 */
	public int getNumActivities() {
		return this.getActivities().size();
	}

	List<Activity<T>> activities;

}

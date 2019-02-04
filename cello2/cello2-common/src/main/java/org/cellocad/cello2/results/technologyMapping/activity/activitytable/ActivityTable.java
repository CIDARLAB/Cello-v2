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
package org.cellocad.cello2.results.technologyMapping.activity.activitytable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The ActivityTable class represents table of activities within the <i>SimulatedAnnealing</i> algorithm class of the <i>technologyMapping</i> stage.
 * @param Input input type index
 * @param Output output type index
 *
 * @author Timothy Jones
 *
 * @date 2018-05-24
 *
 */
public class ActivityTable<Input,Output> {

	/**
	 *  Initialize class members
	 */
	private void init() {
		activityTableMap = new HashMap<Activity<Input>, Activity<Output>>();
		activities = new ArrayList<Activity<Input>>();
	}

	/**
	 *  Initializes a newly created ActivityTable with the list of inputs defined by parameter <i>inputs</i>
	 *  a list of outputs defined by parameter <i>outputs</i>.
	 *  
	 *  @param inputs the List of inputs
	 *  @param outputs the List of outputs
	 */
	public ActivityTable(final List<Activity<Input>> inputs, final List<Output> outputs) {
		init();
		for (int i = 0; i < inputs.size(); i ++) {
			Activity<Input> InputActivity = inputs.get(i);
			Activity<Output> OutputActivity = new Activity<Output>(outputs);
			this.getActivityTableMap().put(InputActivity, OutputActivity);
			this.getActivities().add(InputActivity);
		}
	}

	/**
	 *  Initializes a newly created ActivityTable with the list of activities defined by parameter <i>activities</i>
	 *  a list of outputs defined by parameter <i>outputs</i>.
	 *  
	 *  @param activities the List of activities
	 *  @param outputs the List of outputs
	 */
	public ActivityTable(final Activities<Input> activities, final List<Output> outputs) {
		init();
		for (int i = 0; i < activities.getNumActivities(); i ++) {
			Activity<Input> InputActivity = activities.getActivityAtIdx(i);
			Activity<Output> OutputActivity = new Activity<Output>(outputs);
			this.getActivityTableMap().put(InputActivity, OutputActivity);
			this.getActivities().add(InputActivity);
		}
	}

	/*
	 * ActivityMap
	 */
	/**
	 *  Getter for <i>activityTableMap</i>
	 *  @return the activityTableMap of this instance
	 */
	protected Map<Activity<Input>, Activity<Output>> getActivityTableMap() {
		return activityTableMap;
	}

	/**
	 *  Getter for <i>activities</i>
	 *  @return the activities of this instance
	 */
	protected List<Activity<Input>> getActivities() {
		return activities;
	}

	/**
	 * Returns the output activity for the activity defined by parameter <i>activity</i>
	 * 
	 * @param activity the input activity
	 * @return the output activity for the activity defined by parameter <i>activity</i>, otherwise null
	 */
	public Activity<Output> getActivityOutput(final Activity<Input> activity){
		Activity<Output> rtn = null;
		rtn = this.getActivityTableMap().get(activity);
		return rtn;
	}

	/**
	 * Returns the Activity<Input> at the specified position in this instance.
	 * 
	 * @param index index of the Activity<Input> to return
	 * @return if the index is within the bounds (0 <= bounds < this.getNumActivities()), returns the Activity<Input> at the specified position in this instance, otherwise null
	 */
	public Activity<Input> getActivityAtIdx(final int index){
		Activity<Input> rtn = null;
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
	 * Returns the number of activities in this instance.
	 * 
	 * @return the number of activities in this instance.
	 */
	public int getNumActivities() {
		return this.getActivities().size();
	}

	List<Activity<Input>> activities;
	Map<Activity<Input>, Activity<Output>> activityTableMap;
}

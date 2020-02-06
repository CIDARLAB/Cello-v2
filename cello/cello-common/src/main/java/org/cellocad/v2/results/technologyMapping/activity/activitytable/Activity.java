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

import org.cellocad.v2.common.CObject;
import org.cellocad.v2.common.Pair;
import org.cellocad.v2.results.logicSynthesis.logic.truthtable.State;
import org.cellocad.v2.results.technologyMapping.activity.signal.SensorSignals;

/**
 * The Activity class contains the activity of a netlist used within the <i>SimulatedAnnealing</i> algorithm class of the <i>technologyMapping</i> stage.
 * @param T type index
 * 
 * @author Timothy Jones
 * 
 * @date 2018-05-22
 *
 */
public class Activity <T> extends CObject{

	/**
	 *  Initialize class members
	 */
	private void init() {
		activityEntry = new ArrayList<Pair<T,Double>>();
		activityEntryMap = new HashMap<T,Double>();
	}
	
	/**
	 *  Initializes a newly created Activity with the list of types defined by parameter <i>nodes</i>
	 *  and value defined by parameter <i>value</i>.
	 *  
	 *  @param nodes the List of types
	 *  @param value the value
	 */
	public Activity(final List<T> nodes, final double value) {
		init();
		for (int i = 0; i < nodes.size(); i++) {
			T node = nodes.get(i);
			Pair<T,Double> pair = new Pair<T,Double>(node, value);
			this.getActivity().add(pair);
			this.getActivityMap().put(node, value);
		}
	}

	/**
	 *  Initializes a newly created Activity with the list of types defined by parameter <i>nodes</i>
	 *  
	 *  @param nodes the List of types
	 */
	public Activity(final List<T> nodes) {
		this(nodes, 0.0);
	}
	
	public Activity(final List<T> inputs, final State<T> state, final SensorSignals<T> signals) {
		init();
		for (int i = 0; i < inputs.size(); i++) {
			T node = inputs.get(i);
			Boolean logic = state.getState(node);
			Double value = null;
			if (logic == state.getZero()) {
				value = signals.getLowActivitySignal(node);
			}
			else if (logic == state.getOne()) {
				value = signals.getHighActivitySignal(node);
			}
			else {
				throw new RuntimeException("Error with State.");
			}
			Pair<T,Double> pair = new Pair<T,Double>(node, value);
			this.getActivity().add(0, pair);
			this.getActivityMap().put(node, value);
		}
	}
	
	/*
	 * Activity
	 */
	/**
	 *  Getter for <i>activityEntry</i>
	 *  @return the activityEntry of this instance
	 */
	protected List<Pair<T,Double>> getActivity() {
		return activityEntry;
	}
	
	/**
	 * Returns the Pair<T,Double> at the specified position in this instance.
	 * 
	 * @param index index of the Pair<T,Double> to return
	 * @return if the index is within the bounds (0 <= bounds < this.getNumActivityPosition()), returns the Pair<T,Boolean> at the specified position in this instance, otherwise null
	 */
	protected Pair<T,Double> getActivityPositionAtIdx(final int index){
		Pair<T,Double> rtn = null;
		if (
				(0 <= index)
				&&
				(index < this.getNumActivityPosition())
				) {
			rtn = this.getActivity().get(index);
		}
		return rtn;
	}
	
	/**
	 * Returns the number of Pair<T,Double> in this instance.
	 * 
	 * @return the number of Pair<T,Double> in this instance.
	 */
	public int getNumActivityPosition() {
		return this.getActivity().size();
	}
	
	/*
	 * ActivityMap
	 */
	/**
	 *  Getter for <i>activityEntryMap</i>
	 *  @return the activityEntryMap of this instance
	 */
	protected Map<T,Double> getActivityMap() {
		return activityEntryMap;
	}

	/**
	 *  Returns the activity of <i>node</i>
	 *  @return the activity of <i>node</i> if the node exists, null otherwise
	 */
	public Double getActivity(final T node){
		Double rtn = null;
		rtn = this.getActivityMap().get(node);
		return rtn;
	}

	/**
	 *  Returns true if the <i>node</i> exists in this instance, then assigns the Double <i>value</i> to the <i>node</i>
	 *  
	 *  @param node the node
	 *  @param value the value
	 *  @return true if the node exists in this instance, false otherwise
	 */
	public boolean setActivity(final T node, final Double value){
		boolean rtn = false;
		for (int i = 0; i < this.getNumActivityPosition(); i ++) {
			Pair<T,Double> position = this.getActivityPositionAtIdx(i);
			if (position.getFirst().equals(node)) {
				position.setSecond(value);
				this.getActivityMap().put(node, value);
				rtn = true;
			}
		}
		return rtn;
	}

	private List<Pair<T,Double>> activityEntry;
	private Map<T,Double> activityEntryMap;
	
}

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
package org.cellocad.cello2.results.logicSynthesis.logic.truthtable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * The State class contains all the state of a netlist used within the <i>SimulatedAnnealing</i> algorithm class of the <i>technologyMapping</i> stage.
 * @param T type index
 * 
 * @author Vincent Mirian
 * 
 * @date 2018-05-21
 *
 */
public class States <T>{

	/**
	 *  Initialize class members
	 */
	private void init() {
		states = new ArrayList<State<T>>();
	}

	/**
	 *  Initializes a newly created States with the list of inputs defined by parameter <i>inputs</i>
	 *  the True value defined by parameter <i>One</i>,
	 *  the false value defined by parameter <i>Zero</i>,.
	 *  
	 *  @param inputs the List of inputs
	 *  @param One the True value
	 *  @param Zero the False value
	 */
	public States(final List<T> inputs, final Boolean One, final Boolean Zero) {
		init();
		this.setOne(One);
		this.setZero(Zero);
		Double result = Math.pow(2,inputs.size());
		int size = result.intValue();
		for (int i = 0; i < size; i ++) {
			State<T> InputState = new State<T>(inputs, One, Zero, i);
			this.getStates().add(InputState);
		}
	}

	/**
	 *  Getter for <i>states</i>
	 *  @return the states of this instance
	 */
	protected List<State<T>> getStates() {
		return states;
	}

	/**
	 * Returns the State<T> at the specified position in this instance.
	 * 
	 * @param index index of the State<T> to return
	 * @return if the index is within the bounds (0 <= bounds < this.getNumStates()), returns the State<T> at the specified position in this instance, otherwise null
	 */
	public State<T> getStateAtIdx(final int index){
		State<T> rtn = null;
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
	 * Returns the number of State<T> in this instance.
	 * 
	 * @return the number of State<T> in this instance.
	 */
	public int getNumStates() {
		return this.getStates().size();
	}

	/**
	 * Removes the <i>states</i> in this instance.
	 * 
	 * @param stats the collection of State<T> to remove.
	 * @return true if this instance is changed as a result of the call.
	 */
	public Boolean removeStates(final Collection<State<T>> states) {
		Iterator<State<T>> it = states.iterator();
		while (it.hasNext()) {
			State<T> state = it.next();
			this.getStates().remove(state);
		}
		return true;
	}
	
	/*
	 * On
	 */
	/**
	 *  Getter for <i>bOne</i>
	 *  @return the bOne of this instance
	 */
	public Boolean getOne() {
		return bOne;
	}
	/**
	 *  Setter for <i>bOne</i>
	 *  @param One the value to set <i>bOne</i>
	 */
	protected void setOne(Boolean One) {
		bOne = One;
	}
	
	/*
	 * Off
	 */
	/**
	 *  Getter for <i>bZero</i>
	 *  @return the bZero of this instance
	 */
	public Boolean getZero() {
		return bZero;
	}
	/**
	 *  Setter for <i>bZero</i>
	 *  @param Zero the value to set <i>bZero</i>
	 */
	protected void setZero(Boolean Zero) {
		bZero = Zero;
	}
	
	List<State<T>> states;
	private Boolean bOne;
	private Boolean bZero;
}

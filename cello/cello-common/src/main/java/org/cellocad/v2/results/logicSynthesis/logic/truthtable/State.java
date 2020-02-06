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
import org.cellocad.v2.common.Pair;

/**
 * The State class contains a state of a netlist used within the <i>SimulatedAnnealing</i> algorithm class of the <i>technologyMapping</i> stage.
 * @param T type index
 * 
 * @author Vincent Mirian
 * 
 * @date 2018-05-21
 *
 */
public class State <T> extends CObject{

	/**
	 *  Initialize class members
	 */
	private void init() {
		stateEntry = new ArrayList<Pair<T,Boolean>>();
		stateEntryMap = new HashMap<T,Boolean>();
	}
	
	/**
	 *  Initializes a newly created State with the list of types defined by parameter <i>nodes</i>
	 *  the True value defined by parameter <i>One</i>,
	 *  the false value defined by parameter <i>Zero</i>,
	 *  and value defined by parameter <i>value</i>.
	 *  
	 *  @param nodes the List of types
	 *  @param One the True value
	 *  @param Zero the False value
	 *  @param value the value
	 */
	public State(final List<T> nodes, final Boolean One, final Boolean Zero, final int value) {
		init();
		this.setOne(One);
		this.setZero(Zero);
		Boolean bValue = null;
		int tempValue = value;
		for (int i = 0; i < nodes.size(); i ++) {
			T node = nodes.get(i);
			int temp = tempValue & 1;
			if (temp == 0) {
				bValue = Zero;
			}
			else { //else if (temp == 1) 
				bValue = One;
			}
			tempValue = tempValue >> 1;
			Pair<T,Boolean> pair = new Pair<T,Boolean>(node, bValue);
			this.getState().add(0, pair);
			this.getStateMap().put(node, bValue);
		}
	}

	/**
	 *  Initializes a newly created State with the list of types defined by parameter <i>nodes</i>
	 *  the True value defined by parameter <i>One</i>,
	 *  the false value defined by parameter <i>Zero</i>.
	 *  
	 *  @param nodes the List of types
	 *  @param One the True value
	 *  @param Zero the False value
	 */
	public State(final List<T> nodes, final Boolean One, final Boolean Zero) {
		this(nodes, One, Zero, 0);
	}
	
	/*
	 * State
	 */
	/**
	 *  Getter for <i>stateEntry</i>
	 *  @return the stateEntry of this instance
	 */
	protected List<Pair<T,Boolean>> getState() {
		return stateEntry;
	}
	
	/**
	 * Returns the Pair<T,Boolean> at the specified position in this instance.
	 * 
	 * @param index index of the Pair<T,Boolean> to return
	 * @return if the index is within the bounds (0 <= bounds < this.getNumStatePosition()), returns the Pair<T,Boolean> at the specified position in this instance, otherwise null
	 */
	protected Pair<T,Boolean> getStatePositionAtIdx(final int index){
		Pair<T,Boolean> rtn = null;
		if (
				(0 <= index)
				&&
				(index < this.getNumStatePosition())
				) {
			rtn = this.getState().get(index);
		}
		return rtn;
	}
	
	/**
	 * Returns the number of Pair<T,Boolean> in this instance.
	 * 
	 * @return the number of Pair<T,Boolean> in this instance.
	 */
	public int getNumStatePosition() {
		return this.getState().size();
	}
	
	/*
	 * StateMap
	 */
	/**
	 *  Getter for <i>stateEntryMap</i>
	 *  @return the stateEntryMap of this instance
	 */
	protected Map<T,Boolean> getStateMap() {
		return stateEntryMap;
	}

	/**
	 *  Returns the state of <i>node</i>
	 *  @return the state of <i>node</i> if the node exists, null otherwise
	 */
	public Boolean getState(final T node){
		Boolean rtn = null;
		rtn = this.getStateMap().get(node);
		return rtn;
	}

	/**
	 *  Returns true if the <i>node</i> exists in this instance, then assigns the Boolean <i>value</i> to the <i>node</i>
	 *  
	 *  @param node the node
	 *  @param value the value
	 *  @return true if the node exists in this instance, false otherwise
	 */
	public boolean setState(final T node, final Boolean value){
		boolean rtn = false;
		for (int i = 0; i < this.getNumStatePosition(); i ++) {
			Pair<T,Boolean> position = this.getStatePositionAtIdx(i);
			if (position.getFirst().equals(node)) {
				position.setSecond(value);
				this.getStateMap().put(node, value);
				rtn = true;
			}
		}
		return rtn;
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

	private List<Pair<T,Boolean>> stateEntry;
	private Map<T,Boolean> stateEntryMap;
	private Boolean bOne;
	private Boolean bZero;
	
}

/**
 * Copyright (C) 2018 Boston Univeristy (BU)
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
package org.cellocad.cello2.common.graph.algorithm;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import org.cellocad.cello2.common.CObject;
import org.cellocad.cello2.common.CObjectCollection;
import org.cellocad.cello2.common.graph.AbstractEdge;
import org.cellocad.cello2.common.graph.AbstractGraph;
import org.cellocad.cello2.common.graph.AbstractVertex;

/**
 * The Tarjan class is a class performing Tarjan's strongly connected components algorithm on an <i>AbstractGraph</i>.
 * @param <V> the type of the AbstractVertex
 * @param <E> the type of the AbstractEdge
 * @param <G> the type of the AbstractGraph
 *
 *
 * @author Timothy Jones
 *
 * @date 2019-01-08
 *
 */
public class Tarjan<V extends AbstractVertex<E>, E extends AbstractEdge<V>, G extends AbstractGraph<V,E>> extends CObject {
	
	private void init() {
		this.setIndex(0);
		stack = new Stack<>();
		indexMap = new HashMap<>();
		lowlinkMap = new HashMap<>();
		components = new Stack<>();
	}
	
	private void reset() {
		this.setIndex(0);
		this.getStack().clear();
		this.getIndexMap().clear();
		this.getLowlinkMap().clear();
		this.getComponents().clear();
	}

	private void strongConnect(V v) {
		// index map
		if (this.getIndexMap().containsKey(v)) {
			this.getIndexMap().replace(v,this.getIndex());
		} else {
			this.getIndexMap().put(v,this.getIndex());
		}
		// lowlink map
		if (this.getLowlinkMap().containsKey(v)) {
			this.getLowlinkMap().replace(v,this.getIndex());
		} else {
			this.getLowlinkMap().put(v,this.getIndex());
		}
		// index
		this.setIndex(this.getIndex() + 1);
		// stack
		stack.push(v);
		// successors
		for (int i = 0; i < v.getNumOutEdge(); i++) {
			E e = v.getOutEdgeAtIdx(i);
			for (int j = 0; j < e.getNumDst(); j++) {
				V w = e.getDstAtIdx(j);
				if (!this.getLowlinkMap().containsKey(w)) {
					strongConnect(w);
					int lowlink = Math.min(this.getLowlinkMap().get(v),this.getLowlinkMap().get(w));
					this.getLowlinkMap().replace(v,lowlink);
				}
				else if (stack.contains(w)) {
					int lowlink = Math.min(this.getLowlinkMap().get(v),this.getIndexMap().get(w));
					this.getLowlinkMap().replace(v,lowlink);
				}
			}
		}
		if (this.getLowlinkMap().get(v) == this.getIndexMap().get(v)) {
			CObjectCollection<V> component = new CObjectCollection<>();
			V w = null;
			do {
				w = stack.pop();
				component.add(w);
			} while (!w.equals(v));
			this.getComponents().push(component);
		}
	}

	private void doTarjan() {
		G g = this.getGraph();
		for (int i = 0; i < g.getNumVertex(); i++) {
			V v = g.getVertexAtIdx(i);
			if (!this.getIndexMap().containsKey(v)) {
				this.strongConnect(v);
			}
		}
	}
	
	/**
	 *  Initializes a newly created Tarjan
	 */
	public Tarjan() {
		init();
	}

	/**
	 *  Initializes a newly created Tarjan with an AbstractGraph defined by parameter <i>g</i>
	 */
	public Tarjan(final G g) {
		init();
		this.setGraph(g);
	}

	/**
	 *  Setter for the instance's AbstractGraph
	 *  @param g the AbstractGraph
	 */
	public void setGraph(final G g) {
		reset();
		this.graph = g;
		this.doTarjan();
	}

	/**
	 *  Getter for the instance's AbstractGraph
	 *  @return the AbstractGraph of this instance
	 */
	public G getGraph() {
		return this.graph;
	}
	
	/**
	 * Getter for <i>index</i>
	 * @return value of <i>index</i>
	 */
	private Integer getIndex() {
		return index;
	}

	/**
	 * Setter for <i>index</i>
	 * @param index the value to set <i>index</i>
	 */
	private void setIndex(final Integer index) {
		this.index = index;
	}

	/**
	 * Getter for <i>stack</i>
	 * @return value of <i>stack</i>
	 */
	private Stack<V> getStack() {
		return stack;
	}

	/**
	 * Getter for <i>indexMap</i>
	 * @return value of <i>indexMap</i>
	 */
	private Map<V, Integer> getIndexMap() {
		return indexMap;
	}

	/**
	 * Getter for <i>lowlinkMap</i>
	 * @return value of <i>lowlinkMap</i>
	 */
	private Map<V, Integer> getLowlinkMap() {
		return lowlinkMap;
	}

	/**
	 * Getter for <i>components</i>
	 * @return value of <i>components</i>
	 */
	private Stack<CObjectCollection<V>> getComponents() {
		return components;
	}

	/**
	 *  Returns the next AbstractVertex of this instance's AbstractGraph when performing Tarjan's algorithm
	 *  @return the next AbstractVertex of this instance's AbstractGraph when performing Tarjan's algorithm
	 */
	public CObjectCollection<V> getNextComponent() {
		CObjectCollection<V> rtn = null;
		if (!this.getComponents().isEmpty())
			rtn = this.getComponents().pop();
		return rtn;
	}

	/*
	 * HashCode
	 */
	/**
	 *  Returns a hash code value for the object.
	 *  @return a hash code value for this object.
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((components == null) ? 0 : components.hashCode());
		result = prime * result + ((graph == null) ? 0 : graph.hashCode());
		return result;
	}

	/*
	 * Equals
	 */
	/**
	 *  Indicates whether some other object is "equal to" this one.
	 *  
	 *  @param obj the object to compare with.
	 *  @return true if this object is the same as the obj argument; false otherwise.
	 */
	@Override
	public boolean equals(final Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		if (!(obj instanceof Tarjan<?,?,?>)) {
			return false;
		}
		Tarjan<?,?,?> other = (Tarjan<?,?,?>) obj;
		if (components == null) {
			if (other.components != null)
				return false;
		} else if (!components.equals(other.components))
			return false;
		if (graph == null) {
			if (other.graph != null)
				return false;
		} else if (!graph.equals(other.graph))
			return false;
		return true;
	}

	private G graph;
	private Integer index;
	private Stack<V> stack;
	private Map<V,Integer> indexMap;
	private Map<V,Integer> lowlinkMap;
	private Stack<CObjectCollection<V>> components;
}

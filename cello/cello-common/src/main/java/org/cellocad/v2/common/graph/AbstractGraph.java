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
package org.cellocad.v2.common.graph;

import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import org.cellocad.v2.common.CObject;
import org.cellocad.v2.common.CObjectCollection;
import org.cellocad.v2.common.Utils;

/**
 * The AbstractGraph class is a class representing a graph consisting of: 1) node(s) of type <i>AbstractVertex</i>, and, 2) edge(s) of type <i>AbstractEdge</i>
 * @param <V> the type of the AbstractVertex
 * @param <E> the type of the AbstractEdge
 * 
 * @author Vincent Mirian
 * 
 * @date Nov 15, 2017
 *
 */
abstract public class AbstractGraph<V extends AbstractVertex<E>, E extends AbstractEdge<V>> extends CObject {

	private void init(){
		this.vertices = new CObjectCollection<V>();
		this.edges = new CObjectCollection<E>();		
	}
	
	/**
	 *  Clear the contents of the AbstractGraph
	 */
	public void clear(){
		this.vertices.clear();
		this.edges.clear();
	}

	/**
	 *  Initializes a newly created AbstractGraph 
	 */
	public AbstractGraph(){
		init();
	}

	/**
	 *  Adds the AbstractVertex defined by parameter <i>v</i> to the source node of the AbstractEdge defined by parameter <i>e</i>
	 *  
	 *  @param v the AbstractVertex
	 *  @param e the AbstractEdge
	 */
	protected void addVertexToSrc(final V v, final E e) {
		e.setSrc(v);
	}

	/**
	 *  Adds the AbstractVertex defined by parameter <i>v</i> to the destination node of the AbstractEdge defined by parameter <i>e</i>
	 *  
	 *  @param v the AbstractVertex
	 *  @param e the AbstractEdge
	 */
	protected void addVertexToDst(final V v, final E e){
		e.addDst(v);
	}

	/**
	 *  Adds the AbstractEdge defined by parameter <i>e</i> to the InEdge(s) of the AbstractVertex defined by parameter <i>v</i>
	 *  
	 *  @param v the AbstractVertex
	 *  @param e the AbstractEdge
	 */
	protected void addEdgeToInEdge(final V v, final E e) {
		v.addInEdge(e);
	}

	/**
	 *  Adds the AbstractEdge defined by parameter <i>e</i> to the OutEdge(s) of the AbstractVertex defined by parameter <i>v</i>
	 *  
	 *  @param v the AbstractVertex
	 *  @param e the AbstractEdge
	 */
	protected void addEdgeToOutEdge(final V v, final E e){
		v.addOutEdge(e);
	}

	/**
	 *  Return a newly created AbstractVertex with its contents set to those of parameter <i>other</i>.
	 *  
	 *  @param other the other AbstractVertex
	 *  @return a newly created AbstractVertex with its contents set to those of parameter <i>other</i>.
	 */
	public abstract V createV(final V other);

	/**
	 *  Return a newly created AbstractEdge with its contents set to those of parameter <i>other</i>.
	 *  
	 *  @param other the other AbstractEdge
	 *  @return a newly created AbstractEdge with its contents set to those of parameter <i>other</i>.
	 */
	public abstract E createE(final E other);
	
	/*@SuppressWarnings("unchecked")
	private E createE(E other) {
		E rtn = null;
		try {
			Class<? extends EdgeTemplate<?>> Class = (Class<? extends EdgeTemplate<?>>) other.getClass();
			Constructor<? extends EdgeTemplate<?>> ClassConstructor;
			ClassConstructor = Class.getDeclaredConstructor(Class);
			rtn = (E) ClassConstructor.newInstance(other);
		} catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
			ex.printStackTrace();
		}
		return rtn;
	}
	
	@SuppressWarnings("unchecked")
	private V createV(V other) {
		V rtn = null;
		try {
			Class<? extends VertexTemplate<?>> Class = (Class<? extends VertexTemplate<?>>) other.getClass();
			Constructor<? extends VertexTemplate<?>> ClassConstructor;
			ClassConstructor = Class.getDeclaredConstructor(Class);
			rtn = (V) ClassConstructor.newInstance(other);
		} catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
			ex.printStackTrace();
		}
		return rtn;
	}*/

	/**
	 *  Initializes a newly created AbstractGraph with its node(s) and edge(s) cloned from those of parameter <i>other</i>.
	 *  
	 *  @param other the other AbstractGraph
	 */
	public AbstractGraph(final AbstractGraph<V, E> other){
		super(other);
		init();
		Map<V, V> mapVertexOtherThis = new HashMap<V, V>();
		Map<E, E> mapEdgeOtherThis = new HashMap<E, E>();
		// copy Vertex
		for (int i = 0; i < other.getNumVertex(); i++){
			V v = other.getVertexAtIdx(i);
			V vertex = createV(v);
			vertex.clearInEdge();
			vertex.clearOutEdge();
			mapVertexOtherThis.put(v, vertex);
			this.addVertex(vertex);
		}
		// copy Edge
		for (int i = 0; i < other.getNumEdge(); i++){
			E e = other.getEdgeAtIdx(i);
			E edge = createE(e);
			mapEdgeOtherThis.put(e, edge);
			this.addEdge(edge);
		}
		// for each Vertex:
		for (int i = 0; i < other.getNumVertex(); i++){
			V v = other.getVertexAtIdx(i);
			V vertex = mapVertexOtherThis.get(v);
			assert (vertex != null);
			// set outEdge for Vertex
			for (int j = 0; j < v.getNumOutEdge(); j ++){
				E e = v.getOutEdgeAtIdx(j);
				E edge = mapEdgeOtherThis.get(e);
				assert (edge != null);
				//vertex.addOutEdge(edge);		
				addEdgeToOutEdge(vertex, edge);		
			}
			// set inEdge for Vertex
			for (int j = 0; j < v.getNumInEdge(); j ++){
				E e = v.getInEdgeAtIdx(j);
				E edge = mapEdgeOtherThis.get(e);
				assert (edge != null);
				//vertex.addInEdge(edge);
				addEdgeToInEdge(vertex, edge);
			}			
		}
		// for each Edge:
		for (int i = 0; i < other.getNumEdge(); i++){
			E e = other.getEdgeAtIdx(i);
			E edge = mapEdgeOtherThis.get(e);
			assert (edge != null);
			// set src for Edge
			{
				V v = e.getSrc();
				V vertex = mapVertexOtherThis.get(v);
				assert (vertex != null);
				//edge.setSrc(vertex);
				addVertexToSrc(vertex, edge);
			}
			// set dst for Edge
			{
				for (int j = 0; j < e.getNumDst(); j++) {
					V v = e.getDstAtIdx(j);
					V vertex = mapVertexOtherThis.get(v);
					assert (vertex != null);
					//edge.setDst(vertex);
					addVertexToDst(vertex, edge);					
				}
			}
		}
	}
	
	/*
	 * Vertices
	 */
	/**
	 *  Adds the AbstractVertex defined by parameter <i>vertex</i> to this instance. 
	 *  
	 *  @param vertex a non-null instance of type AbstractVertex
	 */
	public void addVertex(final V vertex){
		if (vertex != null){
			vertices.add(vertex);
		}
	}
	
	/**
	 *  Removes the AbstractVertex defined by parameter <i>vertex</i> from this instance
	 *  
	 *  @param vertex a non-null instance of type AbstractVertex
	 */
	public void removeVertex(final V vertex){
		if (vertex != null){
			vertices.remove(vertex);
		}
	}

	/**
	 * Returns the AbstractVertex at the specified position in this instance.
	 * 
	 * @param index index of the AbstractVertex to return
	 * @return if the index is within the bounds (0 <= bounds < this.getNumVertex()), returns the AbstractVertex at the specified position in this instance, otherwise null
	 */
	public V getVertexAtIdx(int index){
		V rtn = null;
		if (
				(index >= 0) &&
				(index < this.getNumVertex())
			){
			rtn = vertices.get(index);	
		}
		return rtn;
	}

	/**
	 *  Returns the first occurrence of the AbstractVertex with its name equivalent to parameter <i>name</i>.
	 *  
	 *  @param name name of the AbstractVertex to return
	 *  @return the first occurrence of the AbstractVertex with its name equivalent to parameter <i>name</i>.
	 */
	public V getVertexByName(final String name){
		V rtn = null;
		rtn = vertices.findCObjectByName(name);
		return rtn;
	}
	
	/**
	 * Returns the number of AbstractVertex in this instance.
	 * 
	 * @return the number of AbstractVertex in this instance.
	 */
	public int getNumVertex(){
		int rtn = vertices.size();
		return rtn;
	}

	/*
	 * Edges
	 */
	/**
	 *  Adds the AbstractEdge defined by parameter <i>edge</i> to this instance
	 *  
	 *  @param edge a non-null instance of type AbstractEdge
	 */
	public void addEdge(final E edge){
		if (edge != null){
			edges.add(edge);
		}
	}
	
	/**
	 *  Removes the AbstractEdge defined by parameter <i>edge</i> from this instance
	 *  
	 *  @param edge a non-null instance of type AbstractEdge
	 */
	public void removeEdge(final E edge){
		if (edge != null){
			edges.remove(edge);
		}
	}

	/**
	 * Returns the AbstractEdge at the specified position in this instance.
	 * 
	 * @param index index of the AbstractEdge to return
	 * @return if the index is within the bounds (0 <= bounds < this.getNumEdge()), returns the AbstractEdge at the specified position in this instance, otherwise null
	 */
	public E getEdgeAtIdx(int index){
		E rtn = null;
		if (
				(index >= 0) &&
				(index < this.getNumEdge())
			){
			rtn = edges.get(index);	
		} 
		return rtn;
	}

	/**
	 *  Returns the first occurrence of the AbstractEdge with its name equivalent to parameter <i>name</i>.
	 *  
	 *  @param name name of the AbstractEdge to return
	 *  @return the first occurrence of the AbstractEdge with its name equivalent to parameter <i>name</i>.
	 */
	public E getEdgeByName(final String name){
		E rtn = null;
		rtn = edges.findCObjectByName(name);
		return rtn;
	}
	
	/**
	 * Returns the number of AbstractEdge in this instance.
	 * 
	 * @return the number of AbstractEdge in this instance.
	 */
	public int getNumEdge(){
		int rtn = edges.size();
		return rtn;
	}

	/*
	 * is valid?
	 */
	/**
	 *  Returns a boolean flag signifying the validity of this instance
	 *  @return true if the instance is valid; false otherwise.
	 */
	@Override
	public boolean isValid(){
		boolean rtn = true;
		V v = null;
		E e = null;
		// parent is valid
		rtn = rtn && super.isValid();
		/*
		 *  for each vertex, ensure that:
		 *  1) vertex is valid
		 *  2) the in/out edges are in graph
		 */
		for (int i = 0; rtn && (i < vertices.size()); i++){
			v = this.getVertexAtIdx(i);
			// 1) vertex is valid
			rtn = rtn && v.isValid();
			// 2) the in/out edges are in graph
			for (int j = 0; j < v.getNumOutEdge(); j++){
				e = v.getOutEdgeAtIdx(j);
				rtn = rtn && edges.contains(e);
			}
			for (int j = 0; j < v.getNumInEdge(); j++){
				e = v.getInEdgeAtIdx(j);
				rtn = rtn && edges.contains(e);
			}
		}
		/*
		 *  for each edge, ensure that:
		 *  1) edge is valid
		 *  2) the src/dst vertices are in graph
		 */
		for (int i = 0; rtn && (i < edges.size()); i++){
			e = this.getEdgeAtIdx(i);
			// 1) edge is valid
			rtn = rtn && e.isValid();
			// 2) the src/dst vertices are in graph
			v = e.getSrc();
			rtn = rtn && vertices.contains(v);
			for (int j = 0; j < e.getNumDst(); j++) {
				v = e.getDstAtIdx(j);
				rtn = rtn && vertices.contains(v);	
			}
		}
		return rtn;
	}	

	/*
	 * dot file
	 */
	
	/**
	 * Returns a string containing the header in DOT (graph description language) format of this instance
	 * 
	 * @return a string containing the header in DOT (graph description language) format of this instance
	 */
	protected String getDotHeader(){
		String rtn = "";
		rtn += "digraph ";
		rtn += this.getName();
		rtn += " {";
		rtn += Utils.getNewLine();
		rtn += "label=\"";
		rtn += this.getName();
		rtn += "\"";
		rtn += Utils.getNewLine();
		rtn += "rankdir=\"LR\"";
		rtn += Utils.getNewLine();
		rtn += "remincross=true";
		rtn += Utils.getNewLine();
		return rtn;
	}
	
	/**
	 * Returns a string containing the footer in DOT (graph description language) format of this instance
	 * 
	 * @return a string containing the footer in DOT (graph description language) format of this instance
	 */
	protected String getDotFooter(){
		String rtn = "";
		rtn += "}";
		rtn += System.lineSeparator();
		return rtn;
	}

	/**
	 * Returns a string that prefixes the vertices declaration
	 * 
	 * @return a string that prefixes the vertices declaration
	 */
	protected String getDotVerticesPrefix() {
		return "";
	}

	/**
	 * Returns a string that appends the vertices declaration
	 * 
	 * @return a string that appends the vertices declaration
	 */
	protected String getDotVerticesPostfix() {
		return "";
	}

	/**
	 * Returns a string that prefixes the edges declaration
	 * 
	 * @return a string that prefixes the edges declaration
	 */
	protected String getDotEdgesPrefix() {
		return "";
	}

	/**
	 * Returns a string that appends the vertices declaration
	 * 
	 * @return a string that appends the vertices declaration
	 */
	protected String getDotEdgesPostfix() {
		return "";
	}

	/**
	 * Writes this instance in DOT (graph description language) format to the writer defined by parameter <i>os</i>
	 * 
	 * @param os the writer
	 * @throws IOException If an I/O error occurs
	 */
	public void printDot(final Writer os) throws IOException{
		os.write(this.getDotHeader());
		os.write(this.getDotVerticesPrefix());
		for (int i = 0; i < vertices.size(); i++){
			this.getVertexAtIdx(i).printDot(os);
		}
		os.write(this.getDotVerticesPostfix());
		os.write(this.getDotEdgesPrefix());
		for (int i = 0; i < edges.size(); i++){
			this.getEdgeAtIdx(i).printDot(os);
		}
		os.write(this.getDotEdgesPostfix());
		os.write(this.getDotFooter());		
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
		result = prime * result + ((edges == null) ? 0 : edges.hashCode());
		result = prime * result + ((vertices == null) ? 0 : vertices.hashCode());
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
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		if (!(obj instanceof AbstractGraph<?, ?>)) {
			return false;
		}
		AbstractGraph<?, ?> other = (AbstractGraph<?, ?>) obj;
		if (edges == null) {
			if (other.edges != null)
				return false;
		} else if (!edges.equals(other.edges))
			return false;
		if (vertices == null) {
			if (other.vertices != null)
				return false;
		} else if (!vertices.equals(other.vertices))
			return false;
		return true;
	}

	/*
	 * toString
	 */
	/**
	 * Returns a string representing the node(s) of this instance
	 * 
	 * @return a string representing the node(s) of this instance
	 */
	protected String getVerticesToString() {
		String rtn = "";
		for (int i = 0; i < this.getNumVertex(); i ++) {
			rtn = rtn + Utils.getTabCharacterRepeat(2);
			V vertex = this.getVertexAtIdx(i);
			rtn = rtn + vertex.getName();
			rtn = rtn + ",";
			rtn = rtn + Utils.getNewLine();
		}
		return rtn;
	}
	
	/**
	 * Returns a string representing the edge(s) of this instance
	 * 
	 * @return a string representing the edge(s) of this instance
	 */
	protected String getEdgesToString() {
		String rtn = "";
		for (int i = 0; i < this.getNumEdge(); i ++) {
			rtn = rtn + Utils.getTabCharacterRepeat(2);
			E edge = this.getEdgeAtIdx(i);
			rtn = rtn + edge.getName();
			rtn = rtn + ",";
			rtn = rtn + Utils.getNewLine();
		}
		return rtn;
	}

	/**
	 *  Returns a string representation of the object.
	 *  @return a string representation of the object.
	 */
	@Override
	public String toString() {
		String rtn = "";
		String superStr = "";
		rtn = rtn + "[ ";
		rtn = rtn + Utils.getNewLine();
		// name
		rtn = rtn + this.getEntryToString("name", this.getName());
		// vertex
		rtn = rtn + Utils.getTabCharacter();
		rtn = rtn + "vertices = ";
		rtn = rtn + Utils.getNewLine();
		rtn = rtn + Utils.getTabCharacter();
		rtn = rtn + "{";
		rtn = rtn + Utils.getNewLine();
		rtn = rtn + this.getVerticesToString();
		rtn = rtn + Utils.getTabCharacter();
		rtn = rtn + "}";
		rtn = rtn + Utils.getNewLine();
		// edge
		rtn = rtn + Utils.getTabCharacter();
		rtn = rtn + "edges = ";
		rtn = rtn + Utils.getNewLine();
		rtn = rtn + Utils.getTabCharacter();
		rtn = rtn + "{";
		rtn = rtn + Utils.getNewLine();
		rtn = rtn + this.getEdgesToString();
		rtn = rtn + Utils.getTabCharacter();
		rtn = rtn + "}";
		rtn = rtn + Utils.getNewLine();		
		// toString
		rtn = rtn + Utils.getTabCharacter();
		rtn = rtn + "toString() = ";
		rtn = rtn + Utils.getNewLine();
		superStr = super.toString();
		superStr = Utils.addIndent(1, superStr);
		rtn = rtn + superStr;
		rtn = rtn + Utils.getNewLine();
		// end
		rtn = rtn + "]";
		return rtn;
	}

	private CObjectCollection<V> vertices;
	private CObjectCollection<E> edges;
}

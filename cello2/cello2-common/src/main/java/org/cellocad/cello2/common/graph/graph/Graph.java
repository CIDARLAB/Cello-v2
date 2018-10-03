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
package org.cellocad.cello2.common.graph.graph;


/**
 * The Graph class is a class representing a graph consisting of: 1) node(s) of type <i>Vertex</i>, and, 2) edge(s) of type <i>Edge</i>
 * 
 * @author Vincent Mirian
 * 
 * @date Oct 26, 2017
 *
 */
public class Graph extends GraphTemplate<Vertex, Edge>{

	/**
	 *  Return a newly created Vertex with its contents set to those of parameter <i>other</i>.
	 *  
	 *  @param other the other Vertex
	 *  @return a newly created Vertex with its contents set to those of parameter <i>other</i>.
	 */
	@Override
	public Vertex createV(Vertex other) {
		Vertex rtn = null;
		rtn = new Vertex(other);
		return rtn;
	}

	/**
	 *  Return a newly created Edge with its contents set to those of parameter <i>other</i>.
	 *  
	 *  @param other the other Edge
	 *  @return a newly created Edge with its contents set to those of parameter <i>other</i>.
	 */
	@Override
	public Edge createE(Edge other) {
		Edge rtn = null;
		rtn = new Edge(other);
		return rtn;
	}
	
	/**
	 * Returns a string that prefix the vertices declaration
	 * 
	 * @return a string that prefix the vertices declaration
	 */
	protected String getDotVerticesPrefix() {
		return "";
	}

}

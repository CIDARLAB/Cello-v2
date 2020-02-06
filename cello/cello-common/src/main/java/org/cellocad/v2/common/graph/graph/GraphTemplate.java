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
package org.cellocad.v2.common.graph.graph;

import org.cellocad.v2.common.graph.AbstractGraph;

/**
 * The GraphTemplate class is a class representing a graph consisting of: 1) node(s) of type <i>VertexTemplate</i>, and, 2) edge(s) of type <i>EdgeTemplate</i>
 * @param <V> the type of the VertexTemplate
 * @param <E> the type of the EdgeTemplate
 * 
 * @author Vincent Mirian
 * 
 * @date Nov 2, 2017
 *
 */
public abstract class GraphTemplate<V extends VertexTemplate<E>, E extends EdgeTemplate<V>> extends AbstractGraph<V,E>{

	/**
	 *  Initializes a newly created GraphTemplate 
	 */
	public GraphTemplate(){
		super();
	}
	
	/**
	 *  Initializes a newly created GraphTemplate with its node(s) and edge(s) cloned from those of parameter <i>other</i>.
	 *  
	 *  @param other the other GraphTemplate
	 */
	public GraphTemplate(final GraphTemplate<V, E> other){
		super(other);
	}
}

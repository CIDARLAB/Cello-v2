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
package common.graph.hypergraph;

import common.graph.AbstractEdge;

/**
 * The HyperEdgeTemplate class is a class representing the edge(s) of a <i>HyperGraphTemplate</i>.
 * @param <T> the type of the HyperVertexTemplate
 * 
 * @author Vincent Mirian
 * 
 * @date Nov 14, 2017
 *
 */
public class HyperEdgeTemplate<T extends HyperVertexTemplate<?>> extends AbstractEdge<T> {

	/**
	 *  Initializes a newly created HyperEdgeTemplate 
	 */
	public HyperEdgeTemplate(){
		super();
	}

	/**
	 *  Initializes a newly created HyperEdgeTemplate with its source node defined by parameter <i>Src</i>
	 *  @param Src the source node
	 */
	public HyperEdgeTemplate(final T Src) {
		super(Src);
    }

	/**
	 *  Initializes a newly created HyperEdgeTemplate with its contents set to those of <i>other</i>.
	 *  
	 *  @param other the other HyperEdgeTemplate
	 */
	public HyperEdgeTemplate(final HyperEdgeTemplate<T> other) {
		super(other);
    }
	
}

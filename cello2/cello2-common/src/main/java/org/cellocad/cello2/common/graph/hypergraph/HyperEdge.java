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
package org.cellocad.cello2.common.graph.hypergraph;

/**
 * The HyperEdge class is a class representing the edge(s) of a <i>HyperGraph</i>.
 * 
 * @author Vincent Mirian
 * 
 * @date Nov 15, 2017
 *
 */
public class HyperEdge extends HyperEdgeTemplate<HyperVertex>{

	/**
	 *  Initializes a newly created HyperEdge 
	 */
	public HyperEdge(){
        super();
	}

	/**
	 *  Initializes a newly created HyperEdge with its source node defined by parameter <i>Src</i>
	 *  @param Src the source node
	 */
	public HyperEdge(final HyperVertex Src) {
        super(Src);
    }

	/**
	 *  Initializes a newly created HyperEdge with its contents set to those of <i>other</i>.
	 *  
	 *  @param other the other HyperEdge
	 */
	public HyperEdge(final HyperEdge other) {
		super(other);
    }
}

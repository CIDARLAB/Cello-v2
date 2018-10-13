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
 * The HyperGraph class is a class representing a hypergraph consisting of: 1) node(s) of type <i>HyperVertex</i>, and, 2) edge(s) of type <i>HyperEdge</i>
 * 
 * @author Vincent Mirian
 * 
 * @date Nov 15, 2017
 *
 */
public class HyperGraph extends HyperGraphTemplate<HyperVertex, HyperEdge>{

	@Override
	/**
	 *  Return a newly created HyperVertex with its contents set to those of parameter <i>other</i>.
	 *  
	 *  @param other the other HyperVertex
	 *  @return a newly created HyperVertex with its contents set to those of parameter <i>other</i>.
	 */
	public HyperVertex createV(HyperVertex other) {
		HyperVertex rtn = null;
		rtn = new HyperVertex(other);
		return rtn;
	}

	@Override
	/**
	 *  Return a newly created HyperEdge with its contents set to those of parameter <i>other</i>.
	 *  
	 *  @param other the other HyperEdge
	 *  @return a newly created HyperEdge with its contents set to those of parameter <i>other</i>.
	 */
	public HyperEdge createE(HyperEdge other) {
		HyperEdge rtn = null;
		rtn = new HyperEdge(other);
		return rtn;
	}

}

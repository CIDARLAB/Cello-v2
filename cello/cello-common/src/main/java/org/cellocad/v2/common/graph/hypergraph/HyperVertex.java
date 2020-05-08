/*
 * Copyright (C) 2017 Massachusetts Institute of Technology (MIT)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package org.cellocad.v2.common.graph.hypergraph;

/**
 * The HyperVertex class is a class representing the node(s) of a <i>HyperGraph</i>.
 *
 * @author Vincent Mirian
 * @date Nov 15, 2017
 */
public class HyperVertex extends HyperVertexTemplate<HyperEdge> {

  /** Initializes a newly created {@link HyperVertex}. */
  public HyperVertex() {
    super();
  }

  /**
   * Initializes a newly created {@link HyperVertex} with its contents set to those of parameter
   * {@code other}.
   *
   * @param other The other HyperVertex.
   */
  public HyperVertex(final HyperVertex other) {
    super(other);
  }

  /**
   * Adds this instance to the source node of the HyperEdge defined by parameter {@code e}.
   *
   * @param e The HyperEdge.
   */
  @Override
  protected void addMeToSrc(final HyperEdge e) {
    e.setSrc(this);
  }

  /**
   * Adds this instance to the destination node of the HyperEdge defined by parameter {@code e}.
   *
   * @param e The HyperEdge.
   */
  @Override
  protected void addMeToDst(final HyperEdge e) {
    e.addDst(this);
  }

  /**
   * Return a newly created HyperEdge with its contents set to those of parameter {@code e}.
   *
   * @param e The other HyperEdge.
   * @return A newly created HyperEdge with its contents set to those of parameter {@code e}.
   */
  @Override
  public HyperEdge createT(final HyperEdge e) {
    HyperEdge rtn = null;
    rtn = new HyperEdge(e);
    return rtn;
  }
}

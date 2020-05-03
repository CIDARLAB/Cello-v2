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

package org.cellocad.v2.common.graph.graph;

/**
 * The Vertex class is a class representing the node(s) of a <i>Graph</i>.
 *
 * @author Vincent Mirian
 *
 * @date Oct 26, 2017
 */

public class Vertex extends VertexTemplate<Edge> {

  /**
   * Initializes a newly created {@link Vertex}.
   */
  public Vertex() {
    super();
  }

  /**
   * Initializes a newly created {@link Vertex} with its contents set to those of parameter
   * {@code other}.
   *
   * @param other The other Vertex.
   */
  public Vertex(final Vertex other) {
    super(other);
  }

  /**
   * Adds this instance to the source node of the Edge defined by parameter {@code e}.
   *
   * @param e The Edge.
   */
  @Override
  protected void addMeToSrc(final Edge e) {
    e.setSrc(this);
  }

  /**
   * Adds this instance to the destination node of the Edge defined by parameter {@code e}.
   *
   * @param e The Edge.
   */
  @Override
  protected void addMeToDst(final Edge e) {
    e.setDst(this);
  }

  /**
   * Return a newly created Edge with its contents set to those of parameter {@code e}.
   *
   * @param e The other Edge.
   * @return A newly created Edge with its contents set to those of parameter {@code e}.
   */
  @Override
  public Edge createT(final Edge e) {
    Edge rtn = null;
    rtn = new Edge(e);
    return rtn;
  }

}

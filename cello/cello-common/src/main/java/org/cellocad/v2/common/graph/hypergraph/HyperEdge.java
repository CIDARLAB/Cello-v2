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
 * A representation of the edge(s) of a {@link HyperGraph}.
 *
 * @author Vincent Mirian
 *
 * @date Nov 15, 2017
 */
public class HyperEdge extends HyperEdgeTemplate<HyperVertex> {

  /**
   * Initializes a newly created {@link HyperEdge}.
   */
  public HyperEdge() {
    super();
  }

  /**
   * Initializes a newly created {@link HyperEdge} with its source node defined by parameter
   * {@code src}.
   *
   * @param src The source node.
   */
  public HyperEdge(final HyperVertex src) {
    super(src);
  }

  /**
   * Initializes a newly created {@link HyperEdge} with its contents set to those of parameter
   * {@code other}.
   *
   * @param other The other HyperEdge.
   */
  public HyperEdge(final HyperEdge other) {
    super(other);
  }

}

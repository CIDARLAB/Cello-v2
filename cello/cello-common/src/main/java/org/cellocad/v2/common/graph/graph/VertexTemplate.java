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

import org.cellocad.v2.common.graph.AbstractVertex;

/**
 * The VertexTemplate class is a class representing the node(s) of a <i>GraphTemplate</i>.
 *
 * @param <T> the type of the EdgeTemplate.
 * @author Vincent Mirian
 * @date Nov 2, 2017
 */
public abstract class VertexTemplate<T extends EdgeTemplate<?>> extends AbstractVertex<T> {

  /** Initializes a newly created {@link VertexTemplate}. */
  public VertexTemplate() {
    super();
  }

  /**
   * Initializes a newly created {@link VertexTemplate} with its contents set to those of parameter
   * {@code other}.
   *
   * @param other The other VertexTemplate.
   */
  public VertexTemplate(final VertexTemplate<T> other) {
    super(other);
  }

  /*
   * toString
   */
  /**
   * Returns a string representation of the parameter {@code edge}.
   *
   * @return A string representation of the parameter {@code edge}.
   */
  @Override
  protected String getEdgeTemplateToString(final T edge) {
    String rtn = "";
    rtn = rtn + "[";
    rtn = rtn + " name = ";
    rtn = rtn + edge.getName();
    rtn = rtn + ",";
    rtn = rtn + " src = ";
    rtn = rtn + edge.getSrc().getName();
    rtn = rtn + ",";
    rtn = rtn + " dst = ";
    rtn = rtn + edge.getDst().getName();
    rtn = rtn + "]";
    return rtn;
  }
}

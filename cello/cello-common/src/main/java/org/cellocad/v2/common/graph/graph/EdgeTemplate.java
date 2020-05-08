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

import org.cellocad.v2.common.Utils;
import org.cellocad.v2.common.graph.AbstractEdge;

/**
 * The EdgeTemplate class is a class representing the edge(s) of a <i>GraphTemplate</i>.
 *
 * @param <T> the type of the VertexTemplate.
 * @author Vincent Mirian
 * @date Nov 2, 2017
 */
public class EdgeTemplate<T extends VertexTemplate<?>> extends AbstractEdge<T> {

  /** Initializes a newly created {@link EdgeTemplate}. */
  public EdgeTemplate() {
    super();
    setSrc(null);
    this.setDst(null);
  }

  /**
   * Initializes a newly created {@link EdgeTemplate} with its source node defined by parameter
   * {@code Src}.
   *
   * @param src The source node.
   */
  public EdgeTemplate(final T src) {
    super(src);
    this.setDst(null);
  }

  /**
   * Initializes a newly created {@link EdgeTemplate} with its contents set to those of parameter
   * {@code other}.
   *
   * @param other The other EdgeTemplate.
   */
  public EdgeTemplate(final EdgeTemplate<T> other) {
    super(other);
  }

  /**
   * Setter for the instance's destination node.
   *
   * @param dst The destination node.
   */
  public void setDst(final T dst) {
    this.addDst(dst);
  }

  /**
   * Getter for the instance's destination node.
   *
   * @return The destination node of this instance.
   */
  public T getDst() {
    return getDstAtIdx(0);
  }

  /**
   * Adds the destination node defined by parameter {@code dst} to the destination node list, that
   * is reduced to <u>a</u> destination node in this class, of this instance.
   *
   * @param dst a non-null destination node.
   */
  @Override
  public void addDst(final T dst) {
    if (dst != null) {
      if (getNumDst() == 0) {
        getMyDst().add(dst);
      } else {
        getMyDst().set(0, dst);
      }
      assert getNumDst() == 1;
    }
  }

  /*
   * toString
   */
  /**
   * Returns a string representation of the object.
   *
   * @return A string representation of the object.
   */
  @Override
  public String toString() {
    String rtn = "";
    rtn = rtn + "[ ";
    rtn = rtn + Utils.getNewLine();
    // name
    rtn = rtn + this.getEntryToString("name", getName());
    // src
    rtn = rtn + this.getEntryToString("src", getSrc().getName());
    // dst
    rtn = rtn + this.getEntryToString("dst", this.getDst().getName());
    // toString
    rtn = rtn + Utils.getTabCharacter();
    rtn = rtn + "toString() = ";
    rtn = rtn + Utils.getNewLine();
    String superStr = "";
    superStr = super.toString();
    superStr = Utils.addIndent(1, superStr);
    rtn = rtn + superStr;
    rtn = rtn + ",";
    rtn = rtn + Utils.getNewLine();
    // end
    rtn = rtn + "]";
    return rtn;
  }
}

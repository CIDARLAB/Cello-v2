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

package org.cellocad.v2.common.graph;

import java.io.IOException;
import java.io.Writer;
import org.cellocad.v2.common.CObject;
import org.cellocad.v2.common.CObjectCollection;
import org.cellocad.v2.common.Utils;

/**
 * The AbstractEdge class is a class representing the edge(s) of a <i>AbstractGraph</i>.
 *
 * @param <T> the type of the {@link AbstractVertex}.
 *
 * @author Vincent Mirian
 *
 * @date Nov 15, 2017
 */

public abstract class AbstractEdge<T extends AbstractVertex<?>> extends CObject {

  private void init() {
    this.dst = new CObjectCollection<>();
  }

  /**
   * Initializes a newly created {@link AbstractEdge} with an empty list of destination nodes, and,
   * a source node equivalent to null.
   */
  public AbstractEdge() {
    this.init();
    this.setSrc(null);
  }

  /**
   * Initializes a newly created {@link AbstractEdge} with an empty list of destination nodes, and,
   * a source node equivalent to parameter {@code src}.
   */
  public AbstractEdge(final T src) {
    this();
    this.setSrc(src);
  }

  /**
   * Initializes a newly created {@link AbstractEdge} with its contents set to those of parameter
   * {@code other}.
   *
   * @param other The other {@link AbstractEdge}.
   */
  public AbstractEdge(final AbstractEdge<T> other) {
    super(other);
    this.init();
    /*
     * this.setSrc(other.getSrc()); this.getMyDst().addAll(other.getMyDst());.
     */
  }

  /**
   * Setter for the instance's source node.
   *
   * @param src The source node.
   */
  public void setSrc(final T src) {
    this.src = src;
  }

  /**
   * Getter for the instance's source node.
   *
   * @return The source node of this instance.
   */
  public T getSrc() {
    return this.src;
  }

  /**
   * Appends the node, <i>dst</i>, to the end of the destination node list.
   *
   * @param dst a non-null node to append to end of the destination node list.
   */
  public void addDst(final T dst) {
    if (dst != null) {
      this.getMyDst().add(dst);
    }
  }

  /**
   * Returns the node at the specified position in the destination node list of this instance.
   *
   * @param index The index of the destination to return.
   * @return If the index is within the bounds (0 <= bounds < this.getNumDst()), return the node at
   *         the specified position in the destination node list of this instance, otherwise null.
   */
  public T getDstAtIdx(final int index) {
    T rtn = null;
    if (index >= 0 && index < this.getNumDst()) {
      rtn = this.getMyDst().get(index);
    }
    return rtn;
  }

  /**
   * Returns the number of destination nodes in the destination node list of this instance.
   *
   * @return The number of destination nodes in the destination node list of this instance.
   */
  public int getNumDst() {
    final int rtn = this.getMyDst().size();
    return rtn;
  }

  /**
   * Returns true if the edge contains a destination node defined by parameter {@code node},
   * otherwise false.
   *
   * @return True if the edge contains a destination node defined by parameter {@code node},
   *         otherwise false.
   */
  public boolean hasDst(final T node) {
    final boolean rtn = this.getMyDst().contains(node);
    return rtn;
  }

  /**
   * Returns the first occurrence of the destination node with name defined by parameter
   * {@code name}.
   *
   * @param name name of the element to return.
   * @return The first occurrence of the destination node with name defined by parameter
   *         {@code name} if an element exists, null otherwise.
   */
  public T hasDstWithName(final String name) {
    final T rtn = this.getMyDst().findCObjectByName(name);
    return rtn;
  }

  /*
   * dot file
   */
  /**
   * Returns a string containing this instance in DOT (graph description language) format.
   *
   * @return A string containing this instance in DOT (graph description language) format.
   */
  protected String getData() {
    String rtn = "";
    final T src = this.getSrc();
    final int numDst = this.getNumDst();
    if (src != null && numDst > 0) {
      for (int i = 0; i < numDst; i++) {
        rtn += "\"";
        rtn += src.getName();
        rtn += "\" -> \"";
        rtn += this.getDstAtIdx(i).getName();
        rtn += "\"";
        rtn += Utils.getNewLine();
      }
    }
    return rtn;
  }

  /**
   * Writes this instance in DOT (graph description language) format to the writer defined by
   * parameter {@code os}.
   *
   * @param os The writer.
   * @throws IOException If an I/O error occurs.
   */
  public void printDot(final Writer os) throws IOException {
    os.write(this.getData());
  }

  /*
   * HashCode
   */
  /**
   * Returns a hash code value for the object.
   *
   * @return A hash code value for this object.
   */
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result;
    for (int i = 0; i < this.getNumDst(); i++) {
      final T dst = this.getDstAtIdx(i);
      result = prime * result + (dst == null ? 0 : dst.getName().hashCode());
    }
    result = prime * result + (src == null ? 0 : src.getName().hashCode());
    return result;
  }

  /*
   * Equals
   */
  /**
   * Indicates whether some other object is "equal to" this one.
   *
   * @param obj The object to compare with.
   * @return True if this object is the same as the obj argument; false otherwise.
   */
  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (!super.equals(obj)) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    if (!(obj instanceof AbstractEdge<?>)) {
      return false;
    }
    final AbstractEdge<?> other = (AbstractEdge<?>) obj;
    if (this.getMyDst() != other.getMyDst()) {
      return false;
    }
    if (this.getSrc() != other.getSrc()) {
      return false;
    }
    for (int i = 0; i < this.getNumDst(); i++) {
      final AbstractVertex<?> dst = this.getDstAtIdx(i);
      if (dst == null) {
        if (other.getDstAtIdx(i) != null) {
          return false;
        }
      } else {
        if (other.getDstAtIdx(i) != null) {
          if (!dst.getName().equals(other.getDstAtIdx(i).getName())) {
            return false;
          }
        } else {
          return false;
        }
      }
    }
    if (src == null) {
      if (other.src != null) {
        return false;
      }
    } else {
      if (other.src != null) {
        if (!src.getName().equals(other.src.getName())) {
          return false;
        }
      } else {
        return false;
      }
    }
    return true;
  }

  /*
   * toString
   */
  /**
   * Returns a string representation of the instance's destination node list.
   *
   * @return A string representation of the instance's destination node list.
   */
  protected String getDstToString() {
    String rtn = "";
    final int numDst = this.getNumDst();
    for (int i = 0; i < numDst; i++) {
      rtn = rtn + Utils.getTabCharacterRepeat(2);
      rtn = rtn + this.getDstAtIdx(i).getName();
      rtn = rtn + ",";
      rtn = rtn + Utils.getNewLine();
    }
    return rtn;
  }

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
    rtn = rtn + this.getEntryToString("src", src.getName());
    // dst
    rtn = rtn + Utils.getTabCharacter();
    rtn = rtn + "dst = ";
    rtn = rtn + Utils.getNewLine();
    rtn = rtn + Utils.getTabCharacter();
    rtn = rtn + "{";
    rtn = rtn + Utils.getNewLine();
    rtn = rtn + this.getDstToString();
    rtn = rtn + Utils.getTabCharacter();
    rtn = rtn + "}";
    rtn = rtn + Utils.getNewLine();
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

  /*
   * dst
   */
  /**
   * Getter for the instance's destination node list.
   *
   * @return The destination node list of this instance.
   */
  protected CObjectCollection<T> getMyDst() {
    return this.dst;
  }

  /*
   * Members of class
   */
  private T src;
  private CObjectCollection<T> dst;

}

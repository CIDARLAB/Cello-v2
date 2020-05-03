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
 * The AbstractVertex class is a class representing the node(s) of a <i>AbstractGraph</i>.
 *
 * @param <T> the type of the {@link AbstractEdge}.
 *
 * @author Vincent Mirian
 *
 * @date Nov 15, 2017
 */
public abstract class AbstractVertex<T extends AbstractEdge<?>> extends CObject {

  private void init() {
    this.inEdges = new CObjectCollection<>();
    this.outEdges = new CObjectCollection<>();
  }

  private void reset() {
    this.setVertexType(VertexType.NONE);
    this.setVertexColor(VertexColor.WHITE);
    this.setVertexDiscovery(VertexDiscovery.UNVISITED);
    this.inEdges.clear();
    this.outEdges.clear();
  }

  /**
   * Initializes a newly created {@link AbstractVertex} with:<br>
   * its {@code vertexType} set to {@code NONE}, its {@code vertexColor} set to {@code WHITE}, its
   * {@code vertexDiscovery} set to {@code UNVISITED}, an empty list of InEdge(s), and, an empty
   * list of OutEdge(s).
   */
  public AbstractVertex() {
    init();
    reset();
  }

  /**
   * Adds this instance to the source node of the {@link AbstractEdge} defined by parameter
   * {@code e}.
   *
   * @param e The {@link AbstractEdge}.
   */
  protected abstract void addMeToSrc(T e);

  /**
   * Adds this instance to the destination node of the {@link AbstractEdge} defined by parameter
   * {@code e}.
   *
   * @param e The {@link AbstractEdge}.
   */
  protected abstract void addMeToDst(T e);

  /**
   * Return a newly created {@link AbstractEdge} object with its contents set to those of parameter
   * {@code e}.
   *
   * @param e The other {@link AbstractEdge}.
   * @return A newly created {@link AbstractEdge} object with its contents set to those of parameter
   *         {@code e}.
   */
  public abstract T createT(T e);

  /*
   * @SuppressWarnings("unchecked") private T createT(T other) { T rtn = null; try { Class<? extends
   * AbstractEdge<?>> EdgeClass = (Class<? extends AbstractEdge<?>>) other.getClass(); Constructor<?
   * extends AbstractEdge<?>> EdgeClassConstructor; EdgeClassConstructor =
   * EdgeClass.getDeclaredConstructor(EdgeClass); rtn = (T) EdgeClassConstructor.newInstance(other);
   * } catch (NoSuchMethodException | SecurityException | InstantiationException |
   * IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
   * ex.printStackTrace(); } return rtn; }
   */

  /**
   * Initializes a newly created {@link AbstractVertex} with its contents set to those of parameter
   * {@code other}. The AbstractEdge of InEdge(s) and OutEdge(s) are cloned.
   *
   * @param other The other {@link AbstractVertex}.
   */
  public AbstractVertex(final AbstractVertex<T> other) {
    super(other);
    init();
    /*
     * // outEdge for (int i = 0; i < other.getNumOutEdge(); i++) { T eOther =
     * other.getOutEdgeAtIdx(i); T e = createT(eOther); this.addOutEdge(e); //e.setSrc(this);
     * this.addMeToSrc(e); } // inEdge for (int i = 0; i < other.getNumInEdge(); i++) { T eOther =
     * other.getInEdgeAtIdx(i); T e = createT(eOther); this.addInEdge(e); //e.setDst(this);
     * this.addMeToDst(e); }.
     */
  }

  /*
   * VertexType
   */
  /**
   * Describes the type or direction of a vertex, e.g. source or sink.
   */
  public enum VertexType {
    /**
     * None.
     */
    NONE,
    /**
     * Source.
     */
    SOURCE,
    /**
     * Sink.
     */
    SINK
  }

  /**
   * Setter for the {@code vertexType}.
   *
   * @param vertexType The value to set {@code vertexType}.
   */
  public void setVertexType(final VertexType vertexType) {
    this.vertexType = vertexType;
  }

  /**
   * Getter for {@code vertexType}.
   *
   * @return The value of {@code vertexType}.
   */
  public VertexType getVertexType() {
    return this.vertexType;
  }

  /*
   * VertexColor
   */
  /**
   * Describes the color of a vertex.
   */
  public enum VertexColor {
    /**
     * Black.
     */
    BLACK,
    /**
     * Grey.
     */
    GREY,
    /**
     * White.
     */
    WHITE
  }

  /**
   * Setter for{@code vertexColor}.
   *
   * @param vertexColor The value to set {@code vertexColor}.
   */
  public void setVertexColor(final VertexColor vertexColor) {
    this.vertexColor = vertexColor;
  }

  /**
   * Getter for {@code vertexColor}.
   *
   * @return The value of {@code vertexColor}.
   */
  public VertexColor getVertexColor() {
    return this.vertexColor;
  }

  /*
   * VertexDiscovery
   */
  /**
   * Describes the discovery status of a vertex, e.g. visited, touched, or unvisited.
   */
  public enum VertexDiscovery {
    /**
     * Visited.
     */
    VISITED,
    /**
     * Touched.
     */
    TOUCHED,
    /**
     * Unvisited.
     */
    UNVISITED
  }

  /**
   * Setter for {@code vertexDiscovery}.
   *
   * @param vertexDiscovery The value to set {@code vertexDiscovery}.
   */
  public void setVertexDiscovery(final VertexDiscovery vertexDiscovery) {
    this.vertexDiscovery = vertexDiscovery;
  }

  /**
   * Getter for {@code VertexDiscovery}.
   *
   * @return The value of {@code vertexDiscovery}.
   */
  public VertexDiscovery getVertexDiscovery() {
    return this.vertexDiscovery;
  }

  /*
   * InEdge
   */
  /**
   * Adds the {@link AbstractEdge} defined by parameter {@code edge} to the InEdge(s) of this
   * instance.
   *
   * @param edge An {@link AbstractEdge} object.
   */
  public void addInEdge(final T edge) {
    if (edge != null) {
      inEdges.add(edge);
    }
  }

  /**
   * Removes the {@link AbstractEdge} defined by parameter {@code edge} from the InEdge(s) of this
   * instance.
   *
   * @param edge An {@link AbstractEdge} object.
   */
  public void removeInEdge(final T edge) {
    if (edge != null) {
      inEdges.remove(edge);
    }
  }

  /**
   * Returns the {@link AbstractEdge} at the specified position in the InEdge(s) of this instance.
   *
   * @param index The index of the {@link AbstractEdge} object to return.
   * @return If the index is within the bounds (0 <= bounds < this.getNumInEdge()), returns the
   *         {@link AbstractEdge} at the specified position in the InEdge(s) of this instance,
   *         otherwise null.
   */
  public T getInEdgeAtIdx(final int index) {
    T rtn = null;
    if (index >= 0 && index < this.getNumInEdge()) {
      rtn = inEdges.get(index);
    }
    return rtn;
  }

  /**
   * Returns the number of AbstractEdge in the InEdge(s) of this instance.
   *
   * @return The number of AbstractEdge in the InEdge(s) of this instance.
   */
  public int getNumInEdge() {
    final int rtn = inEdges.size();
    return rtn;
  }

  /**
   * Returns true if the vertex contains the inedge defined by parameter {@code edge}, otherwise
   * false.
   *
   * @return True if the vertex contains the inedge defined by parameter {@code edge}, otherwise
   *         false.
   */
  public boolean hasInEdge(final T edge) {
    final boolean rtn = inEdges.contains(edge);
    return rtn;
  }

  /**
   * Returns the first occurrence of the inedge with name defined by parameter {@code name}.
   *
   * @param name name of the element to return.
   * @return The first occurrence of the inedge with name defined by parameter {@code name} if an
   *         element exists, null otherwise.
   */
  public T hasInEdgeWithName(final String name) {
    final T rtn = inEdges.findCObjectByName(name);
    return rtn;
  }

  /**
   * Removes all of the {@link AbstractEdge} from the InEdge(s) of this instance.
   */
  public void clearInEdge() {
    inEdges.clear();
  }

  /*
   * OutEdge
   */
  /**
   * Adds the {@link AbstractEdge} defined by parameter {@code edge} to the OutEdge(s) of this
   * instance.
   *
   * @param edge An {@link AbstractEdge} object.
   */
  public void addOutEdge(final T edge) {
    if (edge != null) {
      outEdges.add(edge);
    }
  }

  /**
   * Removes the {@link AbstractEdge} defined by parameter {@code edge} from the OutEdge(s) of this
   * instance.
   *
   * @param edge An {@link AbstractEdge} object.
   */
  public void removeOutEdge(final T edge) {
    if (edge != null) {
      outEdges.remove(edge);
    }
  }

  /**
   * Returns the {@link AbstractEdge} at the specified position in the OutEdge(s) of this instance.
   *
   * @param index The index of the {@link AbstractEdge} object to return.
   * @return If the index is within the bounds (0 <= bounds < this.getNumOutEdge()), returns the
   *         {@link AbstractEdge} at the specified position in the OutEdge(s) of this instance,
   *         otherwise null.
   */
  public T getOutEdgeAtIdx(final int index) {
    T rtn = null;
    if (index >= 0 && index < this.getNumOutEdge()) {
      rtn = outEdges.get(index);
    }
    return rtn;
  }

  /**
   * Returns the number of AbstractEdge in the OutEdge(s) of this instance.
   *
   * @return The number of AbstractEdge in the OutEdge(s) of this instance.
   */
  public int getNumOutEdge() {
    final int rtn = outEdges.size();
    return rtn;
  }

  /**
   * Returns true if the vertex contains the outedge defined by parameter {@code edge}, otherwise
   * false.
   *
   * @return True if the vertex contains the outedge defined by parameter {@code edge}, otherwise
   *         false.
   */
  public boolean hasOutEdge(final T edge) {
    final boolean rtn = outEdges.contains(edge);
    return rtn;
  }

  /**
   * Returns the first occurrence of the outedge with name defined by parameter {@code name}.
   *
   * @param name name of the element to return.
   * @return The first occurrence of the outedge with name defined by parameter {@code name} if an
   *         element exists, null otherwise.
   */
  public T hasOutEdgeWithName(final String name) {
    final T rtn = outEdges.findCObjectByName(name);
    return rtn;
  }

  /**
   * Removes all of the {@link AbstractEdge} from the OutEdge(s) of this instance.
   */
  public void clearOutEdge() {
    outEdges.clear();
  }

  /*
   * dot file
   */
  /**
   * Returns a string representing the shape of this instance in DOT (graph description language)
   * format.
   *
   * @return A string representing the shape of this instance in DOT (graph description language)
   *         format.
   */
  protected String getShape() {
    String rtn = "";
    rtn += "circle";
    return rtn;
  }

  /**
   * Returns a string containing this instance in DOT (graph description language) format.
   *
   * @return A string containing this instance in DOT (graph description language) format.
   */
  protected String getData() {
    String rtn = "\"";
    rtn += getName();
    rtn += "\" [shape=";
    rtn += this.getShape();
    rtn += ", label=\"";
    rtn += getName();
    rtn += "\"]";
    rtn += Utils.getNewLine();
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
    // final int prime = 31;
    final int result = super.hashCode();
    /*
     * result = prime * result + ((inEdges == null) ? 0 : inEdges.hashCode()); result = prime *
     * result + ((outEdges == null) ? 0 : outEdges.hashCode()); result = prime * result +
     * ((vertexColor == null) ? 0 : vertexColor.hashCode()); result = prime * result +
     * ((vertexDiscovery == null) ? 0 : vertexDiscovery.hashCode()); result = prime * result +
     * ((vertexType == null) ? 0 : vertexType.hashCode());.
     */
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
    if (!(obj instanceof AbstractVertex<?>)) {
      return false;
    }
    final AbstractVertex<?> other = (AbstractVertex<?>) obj;
    if (inEdges == null) {
      if (other.inEdges != null) {
        return false;
      }
    } else if (!inEdges.equals(other.inEdges)) {
      return false;
    }
    if (outEdges == null) {
      if (other.outEdges != null) {
        return false;
      }
    } else if (!outEdges.equals(other.outEdges)) {
      return false;
    }
    if (vertexColor != other.vertexColor) {
      return false;
    }
    if (vertexDiscovery != other.vertexDiscovery) {
      return false;
    }
    if (vertexType != other.vertexType) {
      return false;
    }
    return true;
  }

  /*
   * toString
   */
  /**
   * Returns a string representing the {@link AbstractEdge} defined by parameter {@code edge}.
   *
   * @param edge The {@link AbstractEdge}.
   * @return A string representing the {@link AbstractEdge} defined by parameter {@code edge}.
   */
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
    for (int i = 0; i < edge.getNumDst(); i++) {
      rtn = rtn + edge.getDstAtIdx(i).getName();
      rtn = rtn + ", ";
    }
    rtn = rtn + "]";
    return rtn;
  }

  /**
   * Returns a string representing the InEdge(s) of this instance.
   *
   * @return A string representing the InEdge(s) of this instance.
   */
  protected String getInEdgesToString() {
    String rtn = "";
    // inEdge
    for (int i = 0; i < this.getNumInEdge(); i++) {
      rtn = rtn + Utils.getTabCharacterRepeat(2);
      final T edge = this.getInEdgeAtIdx(i);
      rtn = rtn + this.getEdgeTemplateToString(edge);
      rtn = rtn + ",";
      rtn = rtn + Utils.getNewLine();
    }
    return rtn;
  }

  /**
   * Returns a string representing the OutEdge(s) of this instance.
   *
   * @return A string representing the OutEdge(s) of this instance.
   */
  protected String getOutEdgesToString() {
    String rtn = "";
    // outEdge
    for (int i = 0; i < this.getNumOutEdge(); i++) {
      rtn = rtn + Utils.getTabCharacterRepeat(2);
      final T edge = this.getOutEdgeAtIdx(i);
      rtn = rtn + this.getEdgeTemplateToString(edge);
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
    // vertexType
    rtn = rtn + this.getEntryToString("vertexType", vertexType.toString());
    // vertexColor
    rtn = rtn + this.getEntryToString("vertexColor", vertexColor.toString());
    // vertexDiscovery
    rtn = rtn + this.getEntryToString("vertexDiscovery", vertexDiscovery.toString());
    // inEdges
    rtn = rtn + Utils.getTabCharacter();
    rtn = rtn + "inEdges = ";
    rtn = rtn + Utils.getNewLine();
    rtn = rtn + Utils.getTabCharacter();
    rtn = rtn + "{";
    rtn = rtn + Utils.getNewLine();
    rtn = rtn + this.getInEdgesToString();
    rtn = rtn + Utils.getTabCharacter();
    rtn = rtn + "}";
    rtn = rtn + Utils.getNewLine();
    // outEdges
    rtn = rtn + Utils.getTabCharacter();
    rtn = rtn + "outEdges = ";
    rtn = rtn + Utils.getNewLine();
    rtn = rtn + Utils.getTabCharacter();
    rtn = rtn + "{";
    rtn = rtn + Utils.getNewLine();
    rtn = rtn + this.getOutEdgesToString();
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
    rtn = rtn + Utils.getNewLine();
    // end
    rtn = rtn + "]";
    return rtn;
  }

  private VertexType vertexType;
  private VertexColor vertexColor;
  private VertexDiscovery vertexDiscovery;
  private CObjectCollection<T> inEdges;
  private CObjectCollection<T> outEdges;

}

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

package org.cellocad.v2.common.graph.algorithm;

import java.util.Stack;
import org.cellocad.v2.common.CObject;
import org.cellocad.v2.common.graph.AbstractEdge;
import org.cellocad.v2.common.graph.AbstractGraph;
import org.cellocad.v2.common.graph.AbstractVertex;
import org.cellocad.v2.common.graph.AbstractVertex.VertexDiscovery;
import org.cellocad.v2.common.graph.AbstractVertex.VertexType;

/**
 * The DFS class is class performing depth-first search (DFS) on an <i>AbstractGraph</i>.
 *
 * @param <V> the type of the {@link AbstractVertex}.
 * @param <E> the type of the {@link AbstractEdge}.
 * @param <G> the type of the {@link AbstractGraph}.
 *
 * @author Vincent Mirian
 *
 * @date Nov 1, 2017
 */
public class DFS<V extends AbstractVertex<E>, E extends AbstractEdge<V>, G extends AbstractGraph<V, E>>
    extends CObject {

  private void init() {
    dfs = new Stack<>();
  }

  private void reset() {
    this.getDFS().clear();
  }

  private void doDFS() {
    final G g = this.getGraph();
    final Stack<V> stack = new Stack<>();
    // initialize VertexDiscovery
    // get Source Vertex
    for (int i = 0; i < g.getNumVertex(); i++) {
      final V v = g.getVertexAtIdx(i);
      if (v.getVertexType() == VertexType.SOURCE) {
        this.getDFS().push(v);
        stack.push(v);
      }
      v.setVertexDiscovery(VertexDiscovery.UNVISITED);
    }
    // doDFS
    while (!stack.isEmpty()) {
      final V v = stack.pop();
      // skip if SINK
      if (v.getVertexType() == VertexType.SINK) {
        v.setVertexDiscovery(VertexDiscovery.VISITED);
      }
      // skip if VISITED
      if (v.getVertexDiscovery() == VertexDiscovery.VISITED) {
        continue;
      }
      v.setVertexDiscovery(VertexDiscovery.VISITED);
      for (int i = 0; i < v.getNumOutEdge(); i++) {
        final E e = v.getOutEdgeAtIdx(i);
        for (int j = 0; j < e.getNumDst(); j++) {
          final V dst = e.getDstAtIdx(j);
          if (dst.getVertexDiscovery() == VertexDiscovery.UNVISITED) {
            this.getDFS().push(dst);
            stack.push(dst);
          }
        }
      }
    }
  }

  private Stack<V> getDFS() {
    return this.dfs;
  }

  /**
   * Initializes a newly created {@link DFS}.
   */
  public DFS() {
    init();
  }

  /**
   * Initializes a newly created {@link DFS} with an AbstractGraph defined by parameter {@code g}.
   */
  public DFS(final G g) {
    init();
    this.setGraph(g);
  }

  /**
   * Setter for the instance's AbstractGraph.
   *
   * @param g The {@link AbstractGraph}.
   */
  public void setGraph(final G g) {
    reset();
    this.graph = g;
    this.doDFS();
  }

  /**
   * Getter for the instance's AbstractGraph.
   *
   * @return The {@link AbstractGraph} of this instance.
   */
  public G getGraph() {
    return this.graph;
  }

  /**
   * Returns the next AbstractVertex of this instance's AbstractGraph when performing the
   * depth-first search (DFS) algorithm.
   *
   * @return The next AbstractVertex of this instance's AbstractGraph when performing the
   *         depth-first search (DFS) algorithm.
   */
  public V getNextVertex() {
    V rtn = null;
    if (!this.getDFS().isEmpty()) {
      rtn = this.getDFS().pop();
    }
    return rtn;
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
    result = prime * result + (dfs == null ? 0 : dfs.hashCode());
    result = prime * result + (graph == null ? 0 : graph.hashCode());
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
    if (!(obj instanceof DFS<?, ?, ?>)) {
      return false;
    }
    final DFS<?, ?, ?> other = (DFS<?, ?, ?>) obj;
    if (dfs == null) {
      if (other.dfs != null) {
        return false;
      }
    } else if (!dfs.equals(other.dfs)) {
      return false;
    }
    if (graph == null) {
      if (other.graph != null) {
        return false;
      }
    } else if (!graph.equals(other.graph)) {
      return false;
    }
    return true;
  }

  private G graph;
  private Stack<V> dfs;

}

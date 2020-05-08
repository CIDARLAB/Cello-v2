/*
 * Copyright (C) 2018 Boston University (BU)
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

package org.cellocad.v2.technologyMapping.algorithm.SimulatedAnnealing.data.toxicity.toxicitytable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.cellocad.v2.common.CObject;
import org.cellocad.v2.common.Pair;
import org.cellocad.v2.common.target.data.data.GateToxicity;

/**
 * The GateToxicity class contains the toxicity of a netlist used within the
 * <i>SimulatedAnnealing</i> algorithm class of the <i>technologyMapping</i> stage.
 *
 * @param T type index.
 * @author Timothy Jones
 * @date 2019-02-03
 */
public class Toxicity<T> extends CObject {

  /** Initialize class members. */
  private void init() {
    toxicityEntry = new ArrayList<>();
    toxicityEntryMap = new HashMap<>();
  }

  /**
   * Initializes a newly created {@link GateToxicity} with the list of types defined by parameter
   * {@code nodes} and value defined by parameter {@code value}.
   *
   * @param nodes The List of types.
   */
  public Toxicity(final List<T> nodes) {
    init();
    for (int i = 0; i < nodes.size(); i++) {
      final T node = nodes.get(i);
      final Double toxicity = new Double(1.0);
      final Pair<T, Double> pair = new Pair<>(node, toxicity);
      this.getToxicity().add(pair);
      this.getToxicityMap().put(node, toxicity);
    }
  }

  /*
   * GateToxicity
   */
  /**
   * Getter for {@code toxicityEntry}.
   *
   * @return The toxicityEntry of this instance.
   */
  protected List<Pair<T, Double>> getToxicity() {
    return toxicityEntry;
  }

  /**
   * Gets the relative growth associated with a particular node.
   *
   * @return The relative growth associated with a particular node.
   */
  public Double getToxicity(final T node) {
    Double rtn = null;
    rtn = this.getToxicityMap().get(node);
    return rtn;
  }

  /**
   * Returns the {@link Pair} at the specified position in this instance.
   *
   * @param index The index of the {@link Pair} object to return.
   * @return If the index is within the bounds {@code (0 <= bounds <
   *     this.getNumToxicityPosition())}, returns the {@link Pair} at the specified position in this
   *     instance, otherwise null.
   */
  protected Pair<T, Double> getToxicityPositionAtIdx(final int index) {
    Pair<T, Double> rtn = null;
    if (0 <= index && index < this.getNumToxicityPosition()) {
      rtn = this.getToxicity().get(index);
    }
    return rtn;
  }

  /**
   * Returns the number of {@link Pair} objects in this instance.
   *
   * @return The number of {@link Pair} objects in this instance.
   */
  public int getNumToxicityPosition() {
    return this.getToxicity().size();
  }

  /*
   * ToxicityMap
   */
  /**
   * Getter for {@code toxicityEntryMap}.
   *
   * @return The toxicityEntryMap of this instance.
   */
  protected Map<T, Double> getToxicityMap() {
    return toxicityEntryMap;
  }

  /**
   * Returns true if the node exists in this instance, then assigns the Histogram <i>value</i> to
   * the <i>node</i>.
   *
   * @param node The node.
   * @param value The value.
   * @return True if the node exists in this instance, false otherwise.
   */
  public boolean setToxicity(final T node, final Double value) {
    boolean rtn = false;
    for (int i = 0; i < this.getNumToxicityPosition(); i++) {
      final Pair<T, Double> position = this.getToxicityPositionAtIdx(i);
      if (position.getFirst().equals(node)) {
        position.setSecond(value);
        this.getToxicityMap().put(node, value);
        rtn = true;
      }
    }
    return rtn;
  }

  private List<Pair<T, Double>> toxicityEntry;
  private Map<T, Double> toxicityEntryMap;
}

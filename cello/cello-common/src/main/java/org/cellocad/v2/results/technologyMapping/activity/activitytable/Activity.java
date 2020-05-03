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

package org.cellocad.v2.results.technologyMapping.activity.activitytable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.cellocad.v2.common.CObject;
import org.cellocad.v2.common.Pair;

/**
 * The activity of a netlist used within the <i>SimulatedAnnealing</i> algorithm of the
 * <i>technologyMapping</i> stage.
 *
 * @param <T> The object type with which the activity is associated.
 *
 * @author Timothy Jones
 *
 * @date 2018-05-22
 */
public class Activity<T> extends CObject {

  /**
   * Initialize class members.
   */
  private void init() {
    activityEntry = new ArrayList<>();
    activityEntryMap = new HashMap<>();
  }

  /**
   * Initializes a newly created {@link Activity} with the list of types defined by parameter
   * {@code nodes} and value defined by parameter {@code value}.
   *
   * @param nodes The List of types.
   * @param value The value.
   */
  public Activity(final List<T> nodes, final double value) {
    this.init();
    for (int i = 0; i < nodes.size(); i++) {
      final T node = nodes.get(i);
      final Pair<T, Double> pair = new Pair<>(node, value);
      this.getActivity().add(pair);
      this.getActivityMap().put(node, value);
    }
  }

  /**
   * Initializes a newly created {@link Activity} with the list of types defined by parameter
   * {@code nodes}.
   *
   * @param nodes The List of types.
   */
  public Activity(final List<T> nodes) {
    this(nodes, 0.0);
  }

  /*
   * Activity
   */
  /**
   * Getter for {@code activityEntry}.
   *
   * @return The value of {@code activityEntry}.
   */
  protected List<Pair<T, Double>> getActivity() {
    return activityEntry;
  }

  /**
   * Returns the activity of the given node.
   *
   * @return The activity of the given node if the node exists, null otherwise.
   */
  public Double getActivity(final T node) {
    Double rtn = null;
    rtn = this.getActivityMap().get(node);
    return rtn;
  }

  /**
   * Returns the {@link Pair} object at the specified position in this instance.
   *
   * @param index The index of the {@link Pair} object to return.
   * @return If the index is within the bounds
   *         {@code (0 <= bounds < this.getNumActivityPosition())}, returns the {@link Pair} object
   *         at the specified position in this instance, otherwise null.
   */
  protected Pair<T, Double> getActivityPositionAtIdx(final int index) {
    Pair<T, Double> rtn = null;
    if (0 <= index && index < this.getNumActivityPosition()) {
      rtn = this.getActivity().get(index);
    }
    return rtn;
  }

  /**
   * Returns the number of {@link Pair} objects in this instance.
   *
   * @return The number of {@link Pair} objects in this instance.
   */
  public int getNumActivityPosition() {
    return this.getActivity().size();
  }

  /*
   * ActivityMap
   */
  /**
   * Getter for {@code activityEntryMap}.
   *
   * @return The value of {@link activityEntryMap}.
   */
  protected Map<T, Double> getActivityMap() {
    return activityEntryMap;
  }

  /**
   * Returns true if the given node exists in this instance, then assigns the {@link Double}
   * {@code value} to the node.
   *
   * @param node  The node.
   * @param value The value.
   * @return True if the node exists in this instance, false otherwise.
   */
  public boolean setActivity(final T node, final Double value) {
    boolean rtn = false;
    for (int i = 0; i < this.getNumActivityPosition(); i++) {
      final Pair<T, Double> position = this.getActivityPositionAtIdx(i);
      if (position.getFirst().equals(node)) {
        position.setSecond(value);
        this.getActivityMap().put(node, value);
        rtn = true;
      }
    }
    return rtn;
  }

  private List<Pair<T, Double>> activityEntry;
  private Map<T, Double> activityEntryMap;

}

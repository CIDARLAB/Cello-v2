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

package org.cellocad.v2.results.logicSynthesis.logic.truthtable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.cellocad.v2.common.CObject;
import org.cellocad.v2.common.Pair;

/**
 * The State class contains a state of a netlist used within the <i>SimulatedAnnealing</i> algorithm
 * class of the <i>technologyMapping</i> stage.
 *
 * @param T type index.
 *
 * @author Vincent Mirian
 *
 * @date 2018-05-21
 */
public class State<T> extends CObject {

  /**
   * Initialize class members.
   */
  private void init() {
    stateEntry = new ArrayList<>();
    stateEntryMap = new HashMap<>();
  }

  /**
   * Initializes a newly created {@link State} with the list of types defined by parameter
   * {@code nodes} the True value defined by parameter {@code one}, the false value defined by
   * parameter {@code zero}, and value defined by parameter {@code value}.
   *
   * @param nodes The List of types.
   * @param one   The True value.
   * @param zero  The False value.
   * @param value The value.
   */
  public State(final List<T> nodes, final Boolean one, final Boolean zero, final int value) {
    init();
    this.setOne(one);
    this.setZero(zero);
    Boolean boolValue = null;
    int tempValue = value;
    for (int i = 0; i < nodes.size(); i++) {
      final T node = nodes.get(i);
      final int temp = tempValue & 1;
      if (temp == 0) {
        boolValue = zero;
      } else { // else if (temp == 1)
        boolValue = one;
      }
      tempValue = tempValue >> 1;
      final Pair<T, Boolean> pair = new Pair<>(node, boolValue);
      this.getState().add(0, pair);
      this.getStateMap().put(node, boolValue);
    }
  }

  /**
   * Initializes a newly created {@link State} with the list of types defined by parameter
   * {@code nodes} the True value defined by parameter {@code one}, the false value defined by
   * parameter {@code zero}.
   *
   * @param nodes The List of types.
   * @param one   The True value.
   * @param zero  The False value.
   */
  public State(final List<T> nodes, final Boolean one, final Boolean zero) {
    this(nodes, one, zero, 0);
  }

  /*
   * State
   */
  /**
   * Getter for {@code stateEntry}.
   *
   * @return The stateEntry of this instance.
   */
  protected List<Pair<T, Boolean>> getState() {
    return stateEntry;
  }

  /**
   * Returns the state of {@code node}.
   *
   * @return The state of {@code node} if the node exists, null otherwise.
   */
  public Boolean getState(final T node) {
    Boolean rtn = null;
    rtn = this.getStateMap().get(node);
    return rtn;
  }

  /**
   * Returns the {@link Pair} at the specified position in this instance.
   *
   * @param index The index of the {@link Pair} object to return.
   * @return If the index is within the bounds {@code (0 <= bounds < this.getNumStatePosition())},
   *         returns the {@link Pair} at the specified position in this instance, otherwise null.
   */
  protected Pair<T, Boolean> getStatePositionAtIdx(final int index) {
    Pair<T, Boolean> rtn = null;
    if (0 <= index && index < this.getNumStatePosition()) {
      rtn = this.getState().get(index);
    }
    return rtn;
  }

  /**
   * Returns the number of {@link Pair} objects in this instance.
   *
   * @return The number of {@link Pair} objects in this instance.
   */
  public int getNumStatePosition() {
    return this.getState().size();
  }

  /*
   * StateMap
   */
  /**
   * Getter for {@code stateEntryMap}.
   *
   * @return The value of {@code stateEntryMap}.
   */
  protected Map<T, Boolean> getStateMap() {
    return stateEntryMap;
  }

  /**
   * Returns true if the {@code node} exists in this instance, then assigns the Boolean
   * {@code value} to the {@code node}.
   *
   * @param node  The node.
   * @param value The value.
   * @return True if the node exists in this instance, false otherwise.
   */
  public boolean setState(final T node, final Boolean value) {
    boolean rtn = false;
    for (int i = 0; i < this.getNumStatePosition(); i++) {
      final Pair<T, Boolean> position = this.getStatePositionAtIdx(i);
      if (position.getFirst().equals(node)) {
        position.setSecond(value);
        this.getStateMap().put(node, value);
        rtn = true;
      }
    }
    return rtn;
  }

  /*
   * On
   */
  /**
   * Getter for {@code bOne}.
   *
   * @return The bOne of this instance.
   */
  public Boolean getOne() {
    return bOne;
  }

  /**
   * Setter for {@code bOne}.
   *
   * @param one The value to set {@code bOne}.
   */
  protected void setOne(final Boolean one) {
    bOne = one;
  }

  /*
   * Off
   */
  /**
   * Getter for {@code bZero}.
   *
   * @return The bZero of this instance.
   */
  public Boolean getZero() {
    return bZero;
  }

  /**
   * Setter for {@code bZero}.
   *
   * @param zero The value to set {@code bZero}.
   */
  protected void setZero(final Boolean zero) {
    bZero = zero;
  }

  private List<Pair<T, Boolean>> stateEntry;
  private Map<T, Boolean> stateEntryMap;
  private Boolean bOne;
  private Boolean bZero;

}

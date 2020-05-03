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
import java.util.List;

/**
 * The State class contains all the state of a netlist used within the <i>SimulatedAnnealing</i>
 * algorithm class of the <i>technologyMapping</i> stage.
 *
 * @param T type index.
 *
 * @author Vincent Mirian
 *
 * @date 2018-05-21
 */
public class States<T> {

  /**
   * Initialize class members.
   */
  private void init() {
    states = new ArrayList<>();
  }

  /**
   * Initializes a newly created {@link States} with the list of inputs defined by parameter
   * {@code inputs} the True value defined by parameter {@code one}, the false value defined by
   * parameter {@code zero}.
   *
   * @param inputs The List of inputs.
   * @param one    The True value.
   * @param zero   The False value.
   */
  public States(final List<T> inputs, final Boolean one, final Boolean zero) {
    init();
    this.setOne(one);
    this.setZero(zero);
    final Double result = Math.pow(2, inputs.size());
    final int size = result.intValue();
    for (int i = 0; i < size; i++) {
      final State<T> InputState = new State<>(inputs, one, zero, i);
      this.getStates().add(InputState);
    }
  }

  /**
   * Getter for {@code states}.
   *
   * @return The states of this instance.
   */
  protected List<State<T>> getStates() {
    return states;
  }

  /**
   * Returns the {@code State} object at the specified position in this instance.
   *
   * @param index The index of the {@link State} object to return.
   * @return If the index is within the bounds {@code (0 <= bounds < this.getNumStates())}, returns
   *         the {@link State} object at the specified position in this instance, otherwise null.
   */
  public State<T> getStateAtIdx(final int index) {
    State<T> rtn = null;
    if (0 <= index && index < this.getNumStates()) {
      rtn = this.getStates().get(index);
    }
    return rtn;
  }

  /**
   * Returns the number of {@link State} objects in this instance.
   *
   * @return The number of {@link State} objects in this instance.
   */
  public int getNumStates() {
    return this.getStates().size();
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

  List<State<T>> states;
  private Boolean bOne;
  private Boolean bZero;

}

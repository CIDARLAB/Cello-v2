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
import org.cellocad.v2.results.logicSynthesis.logic.truthtable.State;
import org.cellocad.v2.results.logicSynthesis.logic.truthtable.States;

/**
 * The ActivityTable class represents a mapping between node state and node activity.
 *
 * @param Input input type index.
 * @param Output output type index.
 * @author Timothy Jones
 * @date 2018-05-24
 */
public class ActivityTable<InputT, OutputT> {

  /** Initialize class members. */
  private void init() {
    activityTableMap = new HashMap<>();
    states = new ArrayList<>();
  }

  /**
   * Initializes a newly created {@link ActivityTable} with the list of inputs defined by parameter
   * {@code inputs} a list of outputs defined by parameter {@code outputs}.
   *
   * @param inputs The List of inputs.
   * @param outputs The List of outputs.
   */
  public ActivityTable(final List<State<InputT>> inputs, final List<OutputT> outputs) {
    init();
    for (int i = 0; i < inputs.size(); i++) {
      final State<InputT> InputActivity = inputs.get(i);
      final Activity<OutputT> OutputActivity = new Activity<>(outputs);
      this.getActivityTableMap().put(InputActivity, OutputActivity);
      this.getStates().add(InputActivity);
    }
  }

  /**
   * Initializes a newly created {@link ActivityTable} with the list of states defined by parameter
   * {@code states} a list of outputs defined by parameter {@code outputs}.
   *
   * @param states The List of states.
   * @param outputs The List of outputs.
   */
  public ActivityTable(final States<InputT> states, final List<OutputT> outputs) {
    init();
    for (int i = 0; i < states.getNumStates(); i++) {
      final State<InputT> InputState = states.getStateAtIdx(i);
      final Activity<OutputT> OutputActivity = new Activity<>(outputs);
      this.getActivityTableMap().put(InputState, OutputActivity);
      this.getStates().add(InputState);
    }
  }

  /*
   * ActivityMap
   */
  /**
   * Getter for {@code activityTableMap}.
   *
   * @return The activityTableMap of this instance.
   */
  protected Map<State<InputT>, Activity<OutputT>> getActivityTableMap() {
    return activityTableMap;
  }

  /**
   * Getter for {@code states}.
   *
   * @return The states of this instance.
   */
  protected List<State<InputT>> getStates() {
    return states;
  }

  /**
   * Returns the output activity for the activity defined by parameter {@code activity}.
   *
   * @return The output activity for the activity defined by parameter {@code activity}, otherwise
   *     null.
   */
  public Activity<OutputT> getActivityOutput(final State<InputT> state) {
    Activity<OutputT> rtn = null;
    rtn = this.getActivityTableMap().get(state);
    return rtn;
  }

  /**
   * Returns the State<Input> at the specified position in this instance.
   *
   * @param index The index of the {@link State} object to return.
   * @return If the index is within the bounds (0 <= bounds < this.getNumStates()), returns the
   *     State<Input> at the specified position in this instance, otherwise null.
   */
  public State<InputT> getStateAtIdx(final int index) {
    State<InputT> rtn = null;
    if (0 <= index && index < this.getNumStates()) {
      rtn = this.getStates().get(index);
    }
    return rtn;
  }

  /**
   * Returns the number of states in this instance.
   *
   * @return The number of states in this instance.
   */
  public int getNumStates() {
    return this.getStates().size();
  }

  List<State<InputT>> states;
  Map<State<InputT>, Activity<OutputT>> activityTableMap;
}

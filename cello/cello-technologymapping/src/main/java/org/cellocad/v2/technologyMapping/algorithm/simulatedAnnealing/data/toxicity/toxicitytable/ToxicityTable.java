/*
 * Copyright (C) 2018 Boston University (BU)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions: The above copyright notice and this
 * permission notice shall be included in all copies or substantial portions of the Software. THE
 * SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT
 * LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package org.cellocad.v2.technologyMapping.algorithm.simulatedAnnealing.data.toxicity.toxicitytable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.cellocad.v2.results.logicSynthesis.logic.truthtable.State;
import org.cellocad.v2.results.logicSynthesis.logic.truthtable.States;

/**
 * A lookup table for toxicity. Input objects (probably primary input netlist nodes) define the
 * input columns of the truth table, the truth table output column is associated with an output
 * object type. Rows are of type {@link State}.
 *
 * @param <InputT>  The input object type.
 * @param <OutputT> The output object type.
 *
 * @author Timothy Jones
 *
 * @date 2019-02-03
 */
public class ToxicityTable<InputT, OutputT> {

  /**
   * Initialize class members.
   */
  private void init() {
    toxicityTableMap = new HashMap<>();
    states = new ArrayList<>();
  }

  /**
   * Initializes a newly created {@link ToxicityTable} with the list of inputs defined by parameter
   * {@code inputs} a list of outputs defined by parameter {@code outputs}.
   *
   * @param inputs  The List of inputs.
   * @param outputs The List of outputs.
   */
  public ToxicityTable(final List<State<InputT>> inputs, final List<OutputT> outputs) {
    this.init();
    for (int i = 0; i < inputs.size(); i++) {
      final State<InputT> InputState = inputs.get(i);
      final Toxicity<OutputT> OutputToxicity = new Toxicity<>(outputs);
      this.getToxicityTableMap().put(InputState, OutputToxicity);
      this.getStates().add(InputState);
    }
  }

  /**
   * Initializes a newly created {@link ToxicityTable} with the list of states defined by parameter
   * {@code states} a list of outputs defined by parameter {@code outputs}.
   *
   * @param states  The List of states.
   * @param outputs The List of outputs.
   */
  public ToxicityTable(final States<InputT> states, final List<OutputT> outputs) {
    init();
    for (int i = 0; i < states.getNumStates(); i++) {
      final State<InputT> InputState = states.getStateAtIdx(i);
      final Toxicity<OutputT> OutputToxicity = new Toxicity<>(outputs);
      this.getToxicityTableMap().put(InputState, OutputToxicity);
      this.getStates().add(InputState);
    }
  }

  /*
   * ToxicityMap
   */
  /**
   * Getter for {@code toxicityTableMap}.
   *
   * @return The toxicityTableMap of this instance.
   */
  protected Map<State<InputT>, Toxicity<OutputT>> getToxicityTableMap() {
    return toxicityTableMap;
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
   * Returns the output toxicity for the activity defined by parameter {@code activity}.
   *
   * @param state The input activity.
   * @return The output toxicity for the activity defined by parameter {@code activity}, otherwise
   *         null.
   */
  public Toxicity<OutputT> getToxicityOutput(final State<InputT> state) {
    Toxicity<OutputT> rtn = null;
    rtn = this.getToxicityTableMap().get(state);
    return rtn;
  }

  /**
   * Returns the Activity<Input> at the specified position in this instance.
   *
   * @param index The index of the {@link Activity} object to return.
   * @return If the index is within the bounds (0 <= bounds < this.getNumActivities()), returns the
   *         Activity<Input> at the specified position in this instance, otherwise null.
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
  Map<State<InputT>, Toxicity<OutputT>> toxicityTableMap;

}

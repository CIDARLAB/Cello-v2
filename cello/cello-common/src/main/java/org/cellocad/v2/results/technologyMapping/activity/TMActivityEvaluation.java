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

package org.cellocad.v2.results.technologyMapping.activity;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.cellocad.v2.common.CelloException;
import org.cellocad.v2.common.Utils;
import org.cellocad.v2.common.graph.algorithm.BFS;
import org.cellocad.v2.common.target.data.data.EvaluationContext;
import org.cellocad.v2.common.target.data.data.FunctionType;
import org.cellocad.v2.results.logicSynthesis.logic.LSLogicEvaluation;
import org.cellocad.v2.results.logicSynthesis.logic.truthtable.State;
import org.cellocad.v2.results.logicSynthesis.logic.truthtable.States;
import org.cellocad.v2.results.netlist.Netlist;
import org.cellocad.v2.results.netlist.NetlistEdge;
import org.cellocad.v2.results.netlist.NetlistNode;
import org.cellocad.v2.results.technologyMapping.activity.activitytable.Activity;
import org.cellocad.v2.results.technologyMapping.activity.activitytable.ActivityTable;

/**
 * The complete activity evaluation of a netlist.
 *
 * @author Timothy Jones
 *
 * @date 2018-05-24
 */
public class TMActivityEvaluation {

  /**
   * Initialize class members.
   */
  private void init() {
    activitytables = new HashMap<>();
  }

  /**
   * Initializes a newly created {@link LSLogicEvaluation} using the {@link Netlist} defined by
   * parameter {@code netlist}.
   *
   * @param netlist A netlist.
   * @throws CelloException Unable to initialize object.
   */
  public TMActivityEvaluation(final Netlist netlist, final LSLogicEvaluation lsle)
      throws CelloException {
    init();
    if (!netlist.isValid()) {
      throw new RuntimeException("netlist is not valid!");
    }
    setStates(lsle.getStates());
    final List<NetlistNode> outputNodes = new ArrayList<>();
    for (int i = 0; i < netlist.getNumVertex(); i++) {
      final NetlistNode node = netlist.getVertexAtIdx(i);
      outputNodes.clear();
      outputNodes.add(node);
      final ActivityTable<NetlistNode, NetlistNode> activityTable =
          new ActivityTable<>(states, outputNodes);
      getActivityTables().put(node, activityTable);
    }
    evaluate(netlist);
  }

  /**
   * Returns a List of Double representation of the input values for NetlistNode defined by
   * parameter {@code node} at the state defined by parameter {@code state}.
   *
   * @param node     The {@link NetlistNode}.
   * @param activity The activity.
   * @return A List of Double representation of the input values for NetlistNode defined by
   *         parameter {@code node} at the activity defined by parameter {@code activity}.
   */
  public List<Double> getInputActivity(final NetlistNode node, final State<NetlistNode> activity) {
    final List<Double> rtn = new ArrayList<>();
    for (int i = 0; i < node.getNumInEdge(); i++) {
      final NetlistNode inputNode = node.getInEdgeAtIdx(i).getSrc();
      final ActivityTable<NetlistNode, NetlistNode> activityTable =
          getActivityTables().get(inputNode);
      activityTable.getActivityOutput(activity);
      final Activity<NetlistNode> outputActivity = activityTable.getActivityOutput(activity);
      if (outputActivity.getNumActivityPosition() != 1) {
        throw new RuntimeException("Invalid number of output(s)!");
      }
      rtn.add(outputActivity.getActivity(inputNode));
    }
    return rtn;
  }

  private void evaluateActivityTable(final NetlistNode node, final EvaluationContext ec)
      throws CelloException {
    ec.setNode(node);
    final ActivityTable<NetlistNode, NetlistNode> activityTable = getActivityTables().get(node);
    for (int i = 0; i < activityTable.getNumStates(); i++) {
      final State<NetlistNode> inputState = activityTable.getStateAtIdx(i);
      final Activity<NetlistNode> outputActivity = activityTable.getActivityOutput(inputState);
      ec.setState(inputState);
      final Double result = node.getResultNetlistNodeData().getDevice().getModel()
          .getFunctionByName(FunctionType.S_RESPONSEFUNCTION).evaluate(ec).doubleValue();
      if (outputActivity.getNumActivityPosition() != 1) {
        throw new RuntimeException("Invalid number of output(s)!");
      }
      Utils.isNullRuntimeException(result, "result");
      if (!outputActivity.setActivity(node, result)) {
        throw new RuntimeException("Node does not exist");
      }
    }
  }

  /**
   * Evaluates the activity of a netlist.
   *
   * @param netlist A netlist.
   * @throws CelloException Unable to evaluate activity.
   */
  protected void evaluate(final Netlist netlist) throws CelloException {
    final BFS<NetlistNode, NetlistEdge, Netlist> BFS = new BFS<>(netlist);
    NetlistNode node = null;
    final EvaluationContext ec = new EvaluationContext();
    node = BFS.getNextVertex();
    while (node != null) {
      evaluateActivityTable(node, ec);
      node = BFS.getNextVertex();
    }
  }

  protected Map<NetlistNode, ActivityTable<NetlistNode, NetlistNode>> getActivityTables() {
    return activitytables;
  }

  /**
   * Setter for {@code states}.
   *
   * @param states The states.
   */
  protected void setStates(final States<NetlistNode> states) {
    this.states = states;
  }

  /**
   * Getter for {@code states}.
   *
   * @return The states of this instance.
   */
  public States<NetlistNode> getStates() {
    return states;
  }

  /**
   * Returns the truthTable of NetlistNode defined by parameter {@code node}.
   *
   * @param node The {@link NetlistNode}.
   * @return The truthTable of NetlistNode defined by parameter {@code node}.
   */
  public ActivityTable<NetlistNode, NetlistNode> getActivityTable(final NetlistNode node) {
    ActivityTable<NetlistNode, NetlistNode> rtn = null;
    rtn = getActivityTables().get(node);
    return rtn;
  }

  @Override
  public String toString() {
    String rtn = "";
    rtn += Utils.getNewLine();
    rtn += TMActivityEvaluation.S_HEADER + Utils.getNewLine();
    rtn += "TMActivityEvaluation" + Utils.getNewLine();
    rtn += TMActivityEvaluation.S_HEADER + Utils.getNewLine();
    for (final NetlistNode node : getActivityTables().keySet()) {
      rtn += String.format("%-15s", node.getName()) + Utils.getTabCharacter();
      final ActivityTable<NetlistNode, NetlistNode> activityTable = getActivityTables().get(node);
      for (int i = 0; i < activityTable.getNumStates(); i++) {
        final State<NetlistNode> input = activityTable.getStateAtIdx(i);
        final Activity<NetlistNode> output = activityTable.getActivityOutput(input);
        rtn += String.format("%.4f", output.getActivity(node)) + Utils.getTabCharacter();
      }
      rtn += Utils.getNewLine();
    }
    rtn += TMActivityEvaluation.S_HEADER + Utils.getNewLine();
    return rtn;
  }

  /**
   * Writes this instance in CSV format to the writer defined by parameter {@code os} with the
   * delimiter equivalent to the parameter {@code delimiter}.
   *
   * @param delimiter The delimiter.
   * @param os        The writer.
   * @throws IOException If an I/O error occurs.
   */
  public void writeCSV(final String delimiter, final Writer os) throws IOException {
    String str = "";
    for (final NetlistNode node : getActivityTables().keySet()) {
      str += node.getName();
      final ActivityTable<NetlistNode, NetlistNode> activityTable = getActivityTable(node);
      for (int i = 0; i < activityTable.getNumStates(); i++) {
        final State<NetlistNode> input = activityTable.getStateAtIdx(i);
        final Activity<NetlistNode> output = activityTable.getActivityOutput(input);
        str += delimiter;
        str += String.format("%1.5e", output.getActivity(node));
      }
      str += Utils.getNewLine();
    }
    os.write(str);
  }

  private static final String S_HEADER = "--------------------------------------------";

  private Map<NetlistNode, ActivityTable<NetlistNode, NetlistNode>> activitytables;
  private States<NetlistNode> states;

}

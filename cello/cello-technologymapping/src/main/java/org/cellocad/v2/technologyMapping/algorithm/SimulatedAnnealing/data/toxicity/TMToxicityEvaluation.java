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

package org.cellocad.v2.technologyMapping.algorithm.SimulatedAnnealing.data.toxicity;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.cellocad.v2.common.CObject;
import org.cellocad.v2.common.Utils;
import org.cellocad.v2.common.exception.CelloException;
import org.cellocad.v2.common.graph.algorithm.SinkDFS;
import org.cellocad.v2.common.target.data.data.EvaluationContext;
import org.cellocad.v2.results.logicSynthesis.LSResultsUtils;
import org.cellocad.v2.results.logicSynthesis.logic.truthtable.State;
import org.cellocad.v2.results.logicSynthesis.logic.truthtable.States;
import org.cellocad.v2.results.netlist.Netlist;
import org.cellocad.v2.results.netlist.NetlistEdge;
import org.cellocad.v2.results.netlist.NetlistNode;
import org.cellocad.v2.results.technologyMapping.activity.TMActivityEvaluation;
import org.cellocad.v2.technologyMapping.algorithm.SimulatedAnnealing.data.toxicity.toxicitytable.Toxicity;
import org.cellocad.v2.technologyMapping.algorithm.SimulatedAnnealing.data.toxicity.toxicitytable.ToxicityTable;

/**
 * The TMToxicityEvaluation class evaluates the toxicity of a netlist used within the
 * <i>SimulatedAnnealing</i> algorithm class of the <i>technologyMapping</i> stage.
 *
 * @author Timothy Jones
 * @date 2019-01-29
 */
public class TMToxicityEvaluation extends CObject {

  /** Initialize class members. */
  private void init() {
    toxicitytables = new HashMap<>();
  }

  /**
   * Initializes a toxicity evaluation with a netlist and activity evaluation.
   *
   * @param netlist The {@link Netlist}.
   * @throws CelloException Unable to initialize the object.
   */
  public TMToxicityEvaluation(final Netlist netlist, final TMActivityEvaluation tmae)
      throws CelloException {
    init();
    if (!netlist.isValid()) {
      throw new RuntimeException("netlist is not valid!");
    }
    setTMActivityEvaluation(tmae);
    final States<NetlistNode> states = tmae.getStates();
    final List<NetlistNode> outputNodes = new ArrayList<>();
    for (int i = 0; i < netlist.getNumVertex(); i++) {
      final NetlistNode node = netlist.getVertexAtIdx(i);
      if (LSResultsUtils.isPrimaryInput(node) || LSResultsUtils.isPrimaryOutput(node)) {
        continue;
      }
      outputNodes.clear();
      outputNodes.add(node);
      final ToxicityTable<NetlistNode, NetlistNode> toxicityTable =
          new ToxicityTable<>(states, outputNodes);
      getToxicityTables().put(node, toxicityTable);
    }
    evaluate(netlist);
  }

  /**
   * Evaluates the toxicity table for a node.
   *
   * @param node A node.
   * @param ec An evaluation context.
   * @throws CelloException Unable to evaluate toxicity table.
   */
  private void evaluateToxicityTable(final NetlistNode node, final EvaluationContext ec)
      throws CelloException {
    ec.setNode(node);
    final ToxicityTable<NetlistNode, NetlistNode> toxicityTable = getToxicityTables().get(node);
    for (int i = 0; i < toxicityTable.getNumStates(); i++) {
      final State<NetlistNode> inputState = toxicityTable.getStateAtIdx(i);
      final Toxicity<NetlistNode> outputToxicity = toxicityTable.getToxicityOutput(inputState);
      ec.setState(inputState);
      Double result =
          node.getResultNetlistNodeData()
              .getDevice()
              .getModel()
              .getFunctionByName("toxicity")
              .evaluate(ec)
              .doubleValue();
      if (result > TMToxicityEvaluation.D_MAXGROWTH) {
        result = TMToxicityEvaluation.D_MAXGROWTH;
      }
      if (result < TMToxicityEvaluation.D_MINGROWTH) {
        result = TMToxicityEvaluation.D_MINGROWTH;
      }
      outputToxicity.setToxicity(node, result);
    }
  }

  /**
   * Evaluates the toxicity for a netlist.
   *
   * @param netlist A netlist.
   * @throws CelloException Unable to evaluate the toxicity for the netlist.
   */
  protected void evaluate(final Netlist netlist) throws CelloException {
    final SinkDFS<NetlistNode, NetlistEdge, Netlist> DFS = new SinkDFS<>(netlist);
    NetlistNode node = null;
    final EvaluationContext ec = new EvaluationContext();
    while ((node = DFS.getNextVertex()) != null) {
      if (LSResultsUtils.isPrimaryInput(node) || LSResultsUtils.isPrimaryOutput(node)) {
        continue;
      }
      evaluateToxicityTable(node, ec);
    }
  }

  /**
   * Gets the predicted relative growth of the organism at the given state.
   *
   * @param state A state.
   * @return The relative growth.
   */
  public Double getGrowth(final State<NetlistNode> state) {
    Double rtn = TMToxicityEvaluation.D_MAXGROWTH;
    for (final NetlistNode node : getToxicityTables().keySet()) {
      final ToxicityTable<NetlistNode, NetlistNode> table = getToxicityTables().get(node);
      final Toxicity<NetlistNode> toxicity = table.getToxicityOutput(state);
      final Double value = toxicity.getToxicity(node);
      rtn *= value;
    }
    if (rtn < TMToxicityEvaluation.D_MINGROWTH) {
      rtn = TMToxicityEvaluation.D_MINGROWTH;
    }
    return rtn;
  }

  // public Double getMinimumGrowth() {
  // Double rtn = D_MAXGROWTH;
  // for (NetlistNode node : this.getToxicityTables().keySet()) {
  // ToxicityTable<NetlistNode,NetlistNode> toxicitytable =
  // this.getToxicityTable(node);
  // for (int i = 0; i < toxicitytable.getNumActivities(); i++) {
  // Activity<NetlistNode> input = toxicitytable.getActivityAtIdx(i);
  // GateToxicity<NetlistNode> output = toxicitytable.getToxicityOutput(input);
  // rtn = Math.min(rtn,output.getToxicity(node));
  // }
  // }
  // return rtn;
  // }

  /**
   * Gets the minimum relative growth over all states of the circuit.
   *
   * @return The mimimum relative growth over all states of the circuit.
   */
  public Double getMinimumGrowth() {
    Double rtn = TMToxicityEvaluation.D_MAXGROWTH;
    final States<NetlistNode> states = getTMActivityEvaluation().getStates();
    for (int i = 0; i < states.getNumStates(); i++) {
      final State<NetlistNode> state = states.getStateAtIdx(i);
      rtn = Math.min(rtn, getGrowth(state));
    }
    return rtn;
  }

  protected Map<NetlistNode, ToxicityTable<NetlistNode, NetlistNode>> getToxicityTables() {
    return toxicitytables;
  }

  /**
   * Returns the toxicityTable of NetlistNode defined by parameter {@code node}.
   *
   * @param node The {@link NetlistNode}.
   * @return The truthTable of NetlistNode defined by parameter {@code node}.
   */
  public ToxicityTable<NetlistNode, NetlistNode> getToxicityTable(final NetlistNode node) {
    ToxicityTable<NetlistNode, NetlistNode> rtn = null;
    rtn = getToxicityTables().get(node);
    return rtn;
  }

  /**
   * Getter for {@code tmae}.
   *
   * @return The value of {@code tmae}.
   */
  public TMActivityEvaluation getTMActivityEvaluation() {
    return tmae;
  }

  /**
   * Setter for {@code tmae}.
   *
   * @param tmae The value to set {@code tmae}.
   */
  protected void setTMActivityEvaluation(final TMActivityEvaluation tmae) {
    this.tmae = tmae;
  }

  @Override
  public String toString() {
    String rtn = "";
    rtn += Utils.getNewLine();
    rtn += TMToxicityEvaluation.S_HEADER + Utils.getNewLine();
    rtn += "TMToxicityEvaluation" + Utils.getNewLine();
    rtn += TMToxicityEvaluation.S_HEADER + Utils.getNewLine();
    for (final NetlistNode node : getToxicityTables().keySet()) {
      rtn += String.format("%-15s", node.getName()) + Utils.getTabCharacter();
      final ToxicityTable<NetlistNode, NetlistNode> toxicityTable = getToxicityTable(node);
      for (int i = 0; i < toxicityTable.getNumStates(); i++) {
        final State<NetlistNode> input = toxicityTable.getStateAtIdx(i);
        final Toxicity<NetlistNode> output = toxicityTable.getToxicityOutput(input);
        rtn += String.format("%.2f", output.getToxicity(node)) + Utils.getTabCharacter();
      }
      rtn += Utils.getNewLine();
    }
    rtn += TMToxicityEvaluation.S_HEADER + Utils.getNewLine();
    rtn += String.format("%-15s", "") + Utils.getTabCharacter();
    final States<NetlistNode> states = getTMActivityEvaluation().getStates();
    for (int i = 0; i < states.getNumStates(); i++) {
      final State<NetlistNode> state = states.getStateAtIdx(i);
      rtn += String.format("%.2f", getGrowth(state)) + Utils.getTabCharacter();
    }
    rtn += Utils.getNewLine();
    rtn += TMToxicityEvaluation.S_HEADER + Utils.getNewLine();
    return rtn;
  }

  /**
   * Writes this instance in CSV format to the writer defined by parameter {@code os} with the
   * delimiter equivalent to the parameter {@code delimiter}.
   *
   * @param delimiter The delimiter.
   * @param os The writer.
   * @throws IOException If an I/O error occurs.
   */
  public void writeCSV(final String delimiter, final Writer os) throws IOException {
    String str = "";
    for (final NetlistNode node : getToxicityTables().keySet()) {
      str += node.getName();
      final ToxicityTable<NetlistNode, NetlistNode> toxicityTable = getToxicityTable(node);
      for (int i = 0; i < toxicityTable.getNumStates(); i++) {
        final State<NetlistNode> input = toxicityTable.getStateAtIdx(i);
        final Toxicity<NetlistNode> output = toxicityTable.getToxicityOutput(input);
        str += delimiter;
        str += String.format("%.2f", output.getToxicity(node));
      }
      str += Utils.getNewLine();
    }
    os.write(str);
  }

  private static final String S_HEADER = "--------------------------------------------";
  private static final double D_MAXGROWTH = 1.00;
  private static final double D_MINGROWTH = 0.01;

  private Map<NetlistNode, ToxicityTable<NetlistNode, NetlistNode>> toxicitytables;
  private TMActivityEvaluation tmae;
}

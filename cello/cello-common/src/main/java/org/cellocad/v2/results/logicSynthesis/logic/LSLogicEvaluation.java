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

package org.cellocad.v2.results.logicSynthesis.logic;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.cellocad.v2.common.CObjectCollection;
import org.cellocad.v2.common.Utils;
import org.cellocad.v2.common.graph.algorithm.BFS;
import org.cellocad.v2.results.logicSynthesis.LSResults;
import org.cellocad.v2.results.logicSynthesis.LSResultsUtils;
import org.cellocad.v2.results.logicSynthesis.logic.truthtable.State;
import org.cellocad.v2.results.logicSynthesis.logic.truthtable.States;
import org.cellocad.v2.results.logicSynthesis.logic.truthtable.TruthTable;
import org.cellocad.v2.results.netlist.Netlist;
import org.cellocad.v2.results.netlist.NetlistEdge;
import org.cellocad.v2.results.netlist.NetlistNode;

/**
 * A Boolean logic evaluation of a netlist in the <i>logicSynthesis</i> stage.
 *
 * @author Vincent Mirian
 *
 * @date 2018-05-21
 */
public class LSLogicEvaluation {

  /**
   * Initialize class members.
   */
  private void init() {
    truthtables = new HashMap<>();
  }

  /**
   * Initializes a newly created {@link LSLogicEvaluation} using the Netlist defined by parameter
   * {@code netlist}.
   *
   * @param netlist The {@link Netlist}.
   */
  public LSLogicEvaluation(final Netlist netlist) {
    init();
    if (!netlist.isValid()) {
      throw new RuntimeException("netlist is not valid!");
    }
    final CObjectCollection<NetlistNode> inputNodes = LSResultsUtils.getPrimaryInputNodes(netlist);
    final Boolean One = new Boolean(true);
    final Boolean Zero = new Boolean(false);
    final States<NetlistNode> states = new States<>(inputNodes, One, Zero);
    setStates(states);
    final List<NetlistNode> outputNodes = new ArrayList<>();
    for (int i = 0; i < netlist.getNumVertex(); i++) {
      final NetlistNode node = netlist.getVertexAtIdx(i);
      outputNodes.clear();
      outputNodes.add(node);
      final TruthTable<NetlistNode, NetlistNode> truthTable = new TruthTable<>(states, outputNodes);
      getTruthTables().put(node, truthTable);
    }
    evaluate(netlist);
  }

  /**
   * Returns a Boolean representation of the evaluation of the NodeType defined by <i>nodeType</i>
   * with input defined by parameters <i>inputs</i>.
   *
   * @param inputs   a List of inputs.
   * @param nodeType The NodeType.
   * @return A Boolean representation of the evaluation of the NodeType defined by <i>nodeType</i>
   *         with input defined by parameters <i>inputs</i>.
   */
  private Boolean computeLogic(final List<Boolean> inputs, final String nodeType) {
    Boolean rtn = inputs.get(0);
    for (int i = 1; i < inputs.size(); i++) {
      final Boolean value = inputs.get(i);
      switch (nodeType) {
        case LSResults.S_AND: {
          rtn = rtn && value;
          break;
        }
        case LSResults.S_OR: {
          rtn = rtn || value;
          break;
        }
        case LSResults.S_XOR: {
          rtn = rtn ^ value;
          break;
        }
        default: {
          throw new RuntimeException("Unknown nodeType");
        }
      }
    }
    return rtn;
  }

  /**
   * Returns a List of Boolean representation of the input values for NetlistNode defined by
   * parameter {@code node} at the state defined by parameter {@code state}.
   *
   * @param node  The {@link NetlistNode}.
   * @param state The state.
   * @return A List of Boolean representation of the input values for NetlistNode defined by
   *         parameter {@code node} at the state defined by parameter {@code state}.
   */
  private List<Boolean> getInputLogic(final NetlistNode node, final State<NetlistNode> state) {
    final List<Boolean> rtn = new ArrayList<>();
    for (int i = 0; i < node.getNumInEdge(); i++) {
      final NetlistNode inputNode = node.getInEdgeAtIdx(i).getSrc();
      final TruthTable<NetlistNode, NetlistNode> truthTable = getTruthTables().get(inputNode);
      truthTable.getStateOutput(state);
      final State<NetlistNode> outputState = truthTable.getStateOutput(state);
      if (outputState.getNumStatePosition() != 1) {
        throw new RuntimeException("Invalid number of output(s)!");
      }
      rtn.add(outputState.getState(inputNode));
    }
    return rtn;
  }

  /**
   * Returns the evaluation for a Primary Input for NetlistNode defined by parameter {@code node} at
   * the state defined by parameter {@code state}.
   *
   * @param node  The {@link NetlistNode}.
   * @param state The state.
   * @return The evaluation for a Primary Input for NetlistNode defined by parameter {@code node} at
   *         the state defined by parameter {@code state}.
   */
  private Boolean computePrimaryInput(final NetlistNode node, final State<NetlistNode> state) {
    Boolean rtn = null;
    final List<Boolean> inputList = getInputLogic(node, state);
    if (inputList.size() == 0) {
      rtn = state.getState(node);
    }
    return rtn;
  }

  /**
   * Returns the evaluation for a Primary Output for NetlistNode defined by parameter {@code node}
   * at the state defined by parameter {@code state}.
   *
   * @param node  The {@link NetlistNode}.
   * @param state The state.
   * @return The evaluation for a Primary Output for NetlistNode defined by parameter {@code node}
   *         at the state defined by parameter {@code state}.
   */
  private Boolean computePrimaryOutput(final NetlistNode node, final State<NetlistNode> state) {
    Boolean rtn = null;
    final List<Boolean> inputList = getInputLogic(node, state);
    if (inputList.size() == 1) {
      rtn = inputList.get(0);
    }
    if (inputList.size() > 1) {
      rtn = computeOR(node, state);
    }
    return rtn;
  }

  /**
   * Returns the evaluation for a NOT NodeType for NetlistNode defined by parameter {@code node} at
   * the state defined by parameter {@code state}.
   *
   * @param node  The {@link NetlistNode}.
   * @param state The state.
   * @return The evaluation for a NOT NodeType for NetlistNode defined by parameter {@code node} at
   *         the state defined by parameter {@code state}.
   */
  private Boolean computeNOT(final NetlistNode node, final State<NetlistNode> state) {
    Boolean rtn = null;
    final List<Boolean> inputList = getInputLogic(node, state);
    if (inputList.size() == 1) {
      rtn = inputList.get(0);
      rtn = !rtn;
    }
    return rtn;
  }

  /**
   * Returns the evaluation for an AND NodeType for NetlistNode defined by parameter {@code node} at
   * the state defined by parameter {@code state}.
   *
   * @param node  The {@link NetlistNode}.
   * @param state The state.
   * @return The evaluation for an AND NodeType for NetlistNode defined by parameter {@code node} at
   *         the state defined by parameter {@code state}.
   */
  private Boolean computeAND(final NetlistNode node, final State<NetlistNode> state) {
    Boolean rtn = null;
    final List<Boolean> inputList = getInputLogic(node, state);
    if (inputList.size() > 1) {
      rtn = computeLogic(inputList, LSResults.S_AND);
    }
    return rtn;
  }

  /**
   * Returns the evaluation for an NAND NodeType for NetlistNode defined by parameter {@code node}
   * at the state defined by parameter {@code state}.
   *
   * @param node  The {@link NetlistNode}.
   * @param state The state.
   * @return The evaluation for an NAND NodeType for NetlistNode defined by parameter {@code node}
   *         at the state defined by parameter {@code state}.
   */
  private Boolean computeNand(final NetlistNode node, final State<NetlistNode> state) {
    Boolean rtn = null;
    final List<Boolean> inputList = getInputLogic(node, state);
    if (inputList.size() > 1) {
      rtn = computeLogic(inputList, LSResults.S_AND);
      rtn = !rtn;
    }
    return rtn;
  }

  /**
   * Returns the evaluation for an OR NodeType for NetlistNode defined by parameter {@code node} at
   * the state defined by parameter {@code state}.
   *
   * @param node  The {@link NetlistNode}.
   * @param state The state.
   * @return The evaluation for an OR NodeType for NetlistNode defined by parameter {@code node} at
   *         the state defined by parameter {@code state}.
   */
  private Boolean computeOR(final NetlistNode node, final State<NetlistNode> state) {
    Boolean rtn = null;
    final List<Boolean> inputList = getInputLogic(node, state);
    if (inputList.size() > 1) {
      rtn = computeLogic(inputList, LSResults.S_OR);
    }
    return rtn;
  }

  /**
   * Returns the evaluation for an NOR NodeType for NetlistNode defined by parameter {@code node} at
   * the state defined by parameter {@code state}.
   *
   * @param node  The {@link NetlistNode}.
   * @param state The state.
   * @return The evaluation for an NOR NodeType for NetlistNode defined by parameter {@code node} at
   *         the state defined by parameter {@code state}.
   */
  private Boolean computeNOR(final NetlistNode node, final State<NetlistNode> state) {
    Boolean rtn = null;
    final List<Boolean> inputList = getInputLogic(node, state);
    if (inputList.size() > 1) {
      rtn = computeLogic(inputList, LSResults.S_OR);
      rtn = !rtn;
    }
    return rtn;
  }

  /**
   * Returns the evaluation for an XOR NodeType for NetlistNode defined by parameter {@code node} at
   * the state defined by parameter {@code state}.
   *
   * @param node  The {@link NetlistNode}.
   * @param state The state.
   * @return The evaluation for an XOR NodeType for NetlistNode defined by parameter {@code node} at
   *         the state defined by parameter {@code state}.
   */
  private Boolean computeXOR(final NetlistNode node, final State<NetlistNode> state) {
    Boolean rtn = null;
    final List<Boolean> inputList = getInputLogic(node, state);
    if (inputList.size() > 1) {
      rtn = computeLogic(inputList, LSResults.S_XOR);
    }
    return rtn;
  }

  /**
   * Returns the evaluation for an XNOR NodeType for NetlistNode defined by parameter {@code node}
   * at the state defined by parameter {@code state}.
   *
   * @param node  The {@link NetlistNode}.
   * @param state The state.
   * @return The evaluation for an XNOR NodeType for NetlistNode defined by parameter {@code node}
   *         at the state defined by parameter {@code state}.
   */
  private Boolean computeXnor(final NetlistNode node, final State<NetlistNode> state) {
    Boolean rtn = null;
    final List<Boolean> inputList = getInputLogic(node, state);
    if (inputList.size() > 1) {
      rtn = computeLogic(inputList, LSResults.S_XOR);
      rtn = !rtn;
    }
    return rtn;
  }

  /**
   * Evaluates the truth table for the NetlistNode defined by parameter {@code node}.
   *
   * @param node The {@link NetlistNode}.
   */
  private void evaluateTruthTable(final NetlistNode node) {
    Boolean result = null;
    final String nodeType = node.getResultNetlistNodeData().getNodeType();
    final TruthTable<NetlistNode, NetlistNode> truthTable = getTruthTables().get(node);
    for (int i = 0; i < truthTable.getNumStates(); i++) {
      final State<NetlistNode> inputState = truthTable.getStateAtIdx(i);
      final State<NetlistNode> outputState = truthTable.getStateOutput(inputState);
      if (outputState.getNumStatePosition() != 1) {
        throw new RuntimeException("Invalid number of output(s)!");
      }
      switch (nodeType) {
        case LSResults.S_PRIMARYINPUT: {
          result = computePrimaryInput(node, inputState);
          break;
        }
        case LSResults.S_PRIMARYOUTPUT: {
          result = computePrimaryOutput(node, inputState);
          break;
        }
        case LSResults.S_INPUT: {
          continue;
        }
        case LSResults.S_OUTPUT: {
          continue;
        }
        case LSResults.S_NOT: {
          result = computeNOT(node, inputState);
          break;
        }
        case LSResults.S_AND: {
          result = computeAND(node, inputState);
          break;
        }
        case LSResults.S_NAND: {
          result = computeNand(node, inputState);
          break;
        }
        case LSResults.S_OR: {
          result = computeOR(node, inputState);
          break;
        }
        case LSResults.S_NOR: {
          result = computeNOR(node, inputState);
          break;
        }
        case LSResults.S_XOR: {
          result = computeXOR(node, inputState);
          break;
        }
        case LSResults.S_XNOR: {
          result = computeXnor(node, inputState);
          break;
        }
        default: {
          throw new RuntimeException("Unknown nodeType");
        }
      }
      Utils.isNullRuntimeException(result, "result");
      if (!outputState.setState(node, result)) {
        throw new RuntimeException("Node does not exist");
      }
    }
  }

  /**
   * Evaluates the Netlist defined by parameter {@code netlist}.
   *
   * @param netlist The {@link Netlist}.
   */
  protected void evaluate(final Netlist netlist) {
    final BFS<NetlistNode, NetlistEdge, Netlist> BFS = new BFS<>(netlist);
    NetlistNode node = null;
    node = BFS.getNextVertex();
    while (node != null) {
      evaluateTruthTable(node);
      node = BFS.getNextVertex();
    }
  }

  protected Map<NetlistNode, TruthTable<NetlistNode, NetlistNode>> getTruthTables() {
    return truthtables;
  }

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
  public TruthTable<NetlistNode, NetlistNode> getTruthTable(final NetlistNode node) {
    TruthTable<NetlistNode, NetlistNode> rtn = null;
    rtn = getTruthTables().get(node);
    return rtn;
  }

  @Override
  public String toString() {
    String rtn = "";
    rtn += Utils.getNewLine();
    rtn += LSLogicEvaluation.S_HEADER + Utils.getNewLine();
    rtn += "LSLogicEvaluation" + Utils.getNewLine();
    rtn += LSLogicEvaluation.S_HEADER + Utils.getNewLine();
    for (final NetlistNode node : getTruthTables().keySet()) {
      rtn += String.format("%-15s", node.getName()) + Utils.getTabCharacter();
      final TruthTable<NetlistNode, NetlistNode> truthtable = getTruthTables().get(node);
      for (int i = 0; i < truthtable.getNumStates(); i++) {
        final State<NetlistNode> input = truthtable.getStateAtIdx(i);
        final State<NetlistNode> output = truthtable.getStateOutput(input);
        rtn += output.getState(node) + Utils.getTabCharacter();
      }
      rtn += Utils.getNewLine();
    }
    rtn += LSLogicEvaluation.S_HEADER + Utils.getNewLine();
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
    for (final NetlistNode node : getTruthTables().keySet()) {
      str += node.getName();
      final TruthTable<NetlistNode, NetlistNode> truthtable = getTruthTable(node);
      for (int i = 0; i < truthtable.getNumStates(); i++) {
        final State<NetlistNode> input = truthtable.getStateAtIdx(i);
        final State<NetlistNode> output = truthtable.getStateOutput(input);
        str += delimiter;
        str += String.format("%s", output.getState(node));
      }
      str += Utils.getNewLine();
    }
    os.write(str);
  }

  private static final String S_HEADER = "--------------------------------------------";

  private Map<NetlistNode, TruthTable<NetlistNode, NetlistNode>> truthtables;
  private States<NetlistNode> states;

}

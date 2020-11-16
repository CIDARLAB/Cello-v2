/*
 * Copyright (C) 2020 Boston University (BU)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package org.cellocad.v2.technologyMapping.algorithm.SimulatedAnnealing.data.feature;

import java.util.Collection;
import org.cellocad.v2.common.exception.CelloException;
import org.cellocad.v2.common.exception.NotImplementedException;
import org.cellocad.v2.common.target.data.data.FixedParameter;
import org.cellocad.v2.common.target.data.data.Function;
import org.cellocad.v2.common.target.data.data.FunctionType;
import org.cellocad.v2.common.target.data.data.Model;
import org.cellocad.v2.common.target.data.data.Variable;
import org.cellocad.v2.results.logicSynthesis.LSResultsUtils;
import org.cellocad.v2.results.logicSynthesis.logic.LSLogicEvaluation;
import org.cellocad.v2.results.logicSynthesis.logic.truthtable.State;
import org.cellocad.v2.results.logicSynthesis.logic.truthtable.TruthTable;
import org.cellocad.v2.results.netlist.Netlist;
import org.cellocad.v2.results.netlist.NetlistNode;
import org.cellocad.v2.results.technologyMapping.activity.TMActivityEvaluation;
import org.cellocad.v2.results.technologyMapping.activity.activitytable.Activity;
import org.cellocad.v2.results.technologyMapping.activity.activitytable.ActivityTable;

/**
 * Checks whether all input levels are within ON/OFF thresholds specified by gates.
 *
 * @author Timothy Jones
 * @date 2020-07-01
 */
public class ThresholdUtils {

  /**
   * Get the number of nodes with activity outputs that violate the thresholds (if specified).
   *
   * @param netlist A netlist.
   * @param tmae The activity evaluation of the netlist.
   * @param lsle The logic evaluation of the netlist.
   * @return The number of nodes with activity outputs that violate the thresholds (if specified).
   * @throws CelloException Unable to get thresholds.
   */
  public static Integer getNumberNodesViolatingThreshold(
      final Netlist netlist, final TMActivityEvaluation tmae, final LSLogicEvaluation lsle)
      throws CelloException {
    Integer rtn = 0;
    for (int i = 0; i < netlist.getNumVertex(); i++) {
      NetlistNode node = netlist.getVertexAtIdx(i);
      if (LSResultsUtils.isAllInput(node) || LSResultsUtils.isAllOutput(node)) {
        continue;
      }
      Boolean b =
          inputsWithinThreshold(node, tmae.getActivityTable(node), lsle.getTruthTable(node));
      if (!b) {
        rtn++;
      }
    }
    return rtn;
  }

  /**
   * Checks whether input levels for ON (OFF) states are above (below) the gates ON (OFF) threshold,
   * if specified.
   *
   * @param netlist A netlist.
   * @param tmae The activity evaluation of the netlist.
   * @param lsle The logic evaluation of the netlist.
   * @return Whether input levels for ON (OFF) states are above (below) the gates ON (OFF)
   *     threshold, if specified.
   * @throws CelloException Unable to get thresholds.
   */
  public static Boolean inputsWithinThreshold(
      final Netlist netlist, final TMActivityEvaluation tmae, final LSLogicEvaluation lsle)
      throws CelloException {
    Boolean rtn = true;
    for (int i = 0; i < netlist.getNumVertex(); i++) {
      NetlistNode node = netlist.getVertexAtIdx(i);
      if (LSResultsUtils.isAllInput(node) || LSResultsUtils.isAllOutput(node)) {
        continue;
      }
      Boolean b =
          inputsWithinThreshold(node, tmae.getActivityTable(node), lsle.getTruthTable(node));
      if (!b) {
        return false;
      }
    }
    return rtn;
  }

  /**
   * Checks whether input levels for ON (OFF) states are above (below) the gates ON (OFF) threshold,
   * if specified.
   *
   * @param node A netlist node.
   * @param activityTable The activity table of the node.
   * @param truthTable The truth table of the node.
   * @return Whether input levels for ON (OFF) states are above (below) the gates ON (OFF)
   *     threshold, if specified.
   * @throws CelloException Unable to get thresholds.
   */
  public static Boolean inputsWithinThreshold(
      final NetlistNode node,
      final ActivityTable<NetlistNode, NetlistNode> activityTable,
      final TruthTable<NetlistNode, NetlistNode> truthTable)
      throws CelloException {
    Boolean rtn = true;
    Model m = node.getResultNetlistNodeData().getDevice().getModel();
    Function f = m.getFunctionByName(FunctionType.S_RESPONSEFUNCTION);
    Collection<Variable> variables = f.getVariables();
    if (variables.size() > 1) {
      throw new NotImplementedException(
          "Unable to check thresholds for functions with more than one input.");
    }
    FixedParameter onThresholdParameter = (FixedParameter) m.getParameterByName("on_threshold");
    Double onThreshold = null;
    if (onThresholdParameter != null) {
      onThreshold = onThresholdParameter.evaluate(null).doubleValue();
    }
    FixedParameter offThresholdParameter = (FixedParameter) m.getParameterByName("off_threshold");
    Double offThreshold = null;
    if (offThresholdParameter != null) {
      offThreshold = offThresholdParameter.evaluate(null).doubleValue();
    }
    for (int i = 0; i < activityTable.getNumStates(); i++) {
      State<NetlistNode> state = activityTable.getStateAtIdx(i);
      State<NetlistNode> outputState = truthTable.getStateOutput(state);
      Boolean b = outputState.getState(node).equals(outputState.getOne());
      Activity<NetlistNode> outputActivity = activityTable.getActivityOutput(state);
      if (b && onThreshold != null) {
        if (outputActivity.getActivity(node) < onThreshold) {
          rtn = false;
        }
      } else if (offThreshold != null) {
        if (outputActivity.getActivity(node) > offThreshold) {
          rtn = false;
        }
      }
    }
    return rtn;
  }
}

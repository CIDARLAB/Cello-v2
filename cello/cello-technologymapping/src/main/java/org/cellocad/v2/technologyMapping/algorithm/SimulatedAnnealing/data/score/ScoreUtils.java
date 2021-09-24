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

package org.cellocad.v2.technologyMapping.algorithm.SimulatedAnnealing.data.score;

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
 * Utility methods for computing the scores of netlists.
 *
 * @author Timothy Jones
 * @date 2018-07-16
 */
public class ScoreUtils {

  /**
   * Compute the score of a netlist using its logic and activity evaluations.
   *
   * @param netlist A netlist.
   * @param lsle The logic evaluation of the netlist.
   * @param tmae The activity evaluation of the netlist.
   * @return The score of a netlist using its logic and activity evaluations.
   */
  public static Double score(
      final Netlist netlist, final LSLogicEvaluation lsle, final TMActivityEvaluation tmae) {
    if (!netlist.isValid()) {
      throw new RuntimeException("netlist is not valid!");
    }
    Double rtn = Double.MAX_VALUE;
    for (int i = 0; i < netlist.getNumVertex(); i++) {
      final NetlistNode node = netlist.getVertexAtIdx(i);
      if (LSResultsUtils.isPrimaryOutput(node)) {
        final Double score = ScoreUtils.getOnOffRatio(node, lsle, tmae);
        if (score < rtn) {
          rtn = score;
        }
      }
    }
    return rtn;
  }

  private static Double getOnOffRatio(
      final NetlistNode node, final LSLogicEvaluation lsle, final TMActivityEvaluation tmae) {
    Double rtn = null;

    Double on = Double.MAX_VALUE;
    Double off = Double.MIN_VALUE;

    final TruthTable<NetlistNode, NetlistNode> truthTable = lsle.getTruthTable(node);
    final ActivityTable<NetlistNode, NetlistNode> activityTable = tmae.getActivityTable(node);

    for (int i = 0; i < truthTable.getNumStates(); i++) {
      final State<NetlistNode> inputState = truthTable.getStateAtIdx(i);
      final State<NetlistNode> outputState = truthTable.getStateOutput(inputState);
      final Activity<NetlistNode> outputActivity = activityTable.getActivityOutput(inputState);
      final Boolean l = outputState.getState(node);
      final Double a = outputActivity.getActivity(node);
      if (l == true && on > a) {
        on = a;
      } else if (l == false && off < a) {
        off = a;
      }
    }

    rtn = on / off;

    return rtn;
  }
}

/*
 * Copyright (C) 2020 Boston University (BU)
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
import org.cellocad.v2.common.Utils;
import org.cellocad.v2.results.logicSynthesis.logic.truthtable.TruthTable;
import org.cellocad.v2.results.logicSynthesis.netlist.LSResultNetlistUtils;
import org.cellocad.v2.results.netlist.Netlist;
import org.cellocad.v2.results.netlist.NetlistNode;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.junit.Test;

/**
 * Integration tests for {@link LSLogicEvaluation}.
 *
 * @author Timothy Jones
 * @date 2020-03-07
 */
public class LSLogicEvaluationIT {

  private static Netlist getNetlist(final String filename) throws IOException, ParseException {
    Netlist rtn = null;
    JSONParser parser = new JSONParser();
    String str = Utils.getResourceAsString(filename);
    JSONObject jsonObj = (JSONObject) parser.parse(str);
    rtn = new Netlist(jsonObj);
    LSResultNetlistUtils.setVertexTypeUsingLSResult(rtn);
    return rtn;
  }

  /**
   * Test {@link LSLogicEvaluation} for correctness using mock netlist.
   *
   * @throws ParseException Unable to parse netlist.
   * @throws IOException Unable to read netlist.
   */
  @Test
  public void LSLogicEvaluation_NetlistForAndGateUsingNotAndNor_ShouldHaveCorrectTruthTable()
      throws IOException, ParseException {
    Netlist netlist = getNetlist("and_LogicOnly.json");
    LSLogicEvaluation lsle = new LSLogicEvaluation(netlist);
    NetlistNode out = netlist.getVertexByName("out");
    TruthTable<NetlistNode, NetlistNode> tt = lsle.getTruthTable(out);
    final Boolean b0 = lsle.getStates().getZero();
    final Boolean b1 = lsle.getStates().getOne();
    assert (tt.getStateOutput(tt.getStateAtIdx(0)).getState(out).equals(b0)
        && tt.getStateOutput(tt.getStateAtIdx(1)).getState(out).equals(b0)
        && tt.getStateOutput(tt.getStateAtIdx(2)).getState(out).equals(b0)
        && tt.getStateOutput(tt.getStateAtIdx(3)).getState(out).equals(b1));
  }

  /**
   * Test {@link LSLogicEvaluation} for correctness using mock netlist.
   *
   * @throws ParseException Unable to parse netlist.
   * @throws IOException Unable to read netlist.
   */
  @Test
  public void LSLogicEvaluation_NetlistForA1CircuitUsingNotAndNor_ShouldHaveCorrectTruthTable()
      throws IOException, ParseException {
    Netlist netlist = getNetlist("A1_LogicOnly.json");
    LSLogicEvaluation lsle = new LSLogicEvaluation(netlist);
    NetlistNode out = netlist.getVertexByName("out");
    TruthTable<NetlistNode, NetlistNode> tt = lsle.getTruthTable(out);
    final Boolean b0 = lsle.getStates().getZero();
    final Boolean b1 = lsle.getStates().getOne();
    assert (tt.getStateOutput(tt.getStateAtIdx(0)).getState(out).equals(b1)
        && tt.getStateOutput(tt.getStateAtIdx(1)).getState(out).equals(b1)
        && tt.getStateOutput(tt.getStateAtIdx(2)).getState(out).equals(b0)
        && tt.getStateOutput(tt.getStateAtIdx(3)).getState(out).equals(b0)
        && tt.getStateOutput(tt.getStateAtIdx(4)).getState(out).equals(b0)
        && tt.getStateOutput(tt.getStateAtIdx(5)).getState(out).equals(b0)
        && tt.getStateOutput(tt.getStateAtIdx(6)).getState(out).equals(b0)
        && tt.getStateOutput(tt.getStateAtIdx(7)).getState(out).equals(b1));
  }
}

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

package org.cellocad.v2.common.target.data.data;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.cellocad.v2.common.Utils;
import org.cellocad.v2.common.exception.CelloException;
import org.cellocad.v2.common.target.data.TargetData;
import org.cellocad.v2.common.target.data.TargetDataInstance;
import org.cellocad.v2.results.logicSynthesis.LSResultsUtils;
import org.cellocad.v2.results.logicSynthesis.logic.truthtable.State;
import org.cellocad.v2.results.netlist.Netlist;
import org.cellocad.v2.results.netlist.NetlistEdge;
import org.cellocad.v2.results.netlist.NetlistNode;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Tests for {@link UnivariateLookupTableFunction}.
 *
 * @author Timothy Jones
 * @date 2020-02-21
 */
public class UnivariateLookupTableFunctionTest {

  /**
   * Load library files for the tests.
   *
   * @throws IOException Unable to load resources.
   * @throws ParseException Unable to parse resources.
   * @throws CelloException Unable to instantiate supporting classes.
   */
  @SuppressWarnings("unchecked")
  @BeforeClass
  public static void init() throws IOException, ParseException, CelloException {
    JSONParser parser = new JSONParser();
    String str;
    JSONArray jsonTop = new JSONArray();
    str = Utils.getResourceAsString("lib/ucf/Bth/Bth1C1G1T1.UCF.json");
    jsonTop.addAll((Collection<Object>) parser.parse(str));
    str = Utils.getResourceAsString("lib/input/Bth/Bth1C1G1T1.input.json");
    jsonTop.addAll((Collection<Object>) parser.parse(str));
    str = Utils.getResourceAsString("lib/output/Bth/Bth1C1G1T1.output.json");
    jsonTop.addAll((Collection<Object>) parser.parse(str));
    TargetData td = new TargetData(jsonTop);
    tdi = new TargetDataInstance(td);
    str = Utils.getResourceAsString("and_GateAssignmentUsingBth1C1G1T1.json");
    JSONObject jsonObj = (JSONObject) parser.parse(str);
    netlist = new Netlist(jsonObj);
    for (int i = 0; i < netlist.getNumVertex(); i++) {
      NetlistNode node = netlist.getVertexAtIdx(i);
      String deviceName = node.getResultNetlistNodeData().getDeviceName();
      AssignableDevice device = null;
      if (LSResultsUtils.isAllInput(node)) {
        device = tdi.getInputSensors().findCObjectByName(deviceName);
      } else if (LSResultsUtils.isAllOutput(node)) {
        device = tdi.getOutputDevices().findCObjectByName(deviceName);
      } else {
        device = tdi.getGates().findCObjectByName(deviceName);
      }
      node.getResultNetlistNodeData().setDevice(device);
      int num = node.getNumInEdge();
      for (int j = 0; j < num; j++) {
        NetlistEdge e = node.getInEdgeAtIdx(j);
        Input input = device.getStructure().getInputs().get(j);
        e.getResultNetlistEdgeData().setInput(input);
      }
    }
  }

  @Test
  public void evaluate_MockData_ShouldBeWithinTolerance() throws CelloException {
    NetlistNode node = netlist.getVertexByName("$49");
    NetlistNode a = netlist.getVertexByName("a");
    NetlistNode b = netlist.getVertexByName("b");
    List<NetlistNode> inputs = new ArrayList<>();
    inputs.add(a);
    inputs.add(b);
    State<NetlistNode> state = new State<>(inputs, true, false, 1);
    EvaluationContext ec = new EvaluationContext();
    ec.setNode(node);
    ec.setState(state);
    AssignableDevice device = node.getResultNetlistNodeData().getDevice();
    UnivariateLookupTableFunction f =
        (UnivariateLookupTableFunction) device.getModel().getFunctionByName("toxicity");
    assert (Math.abs(f.evaluate(ec).doubleValue() - 0.885636) < TOLERANCE);
  }

  private static TargetDataInstance tdi;
  private static Netlist netlist;

  private static Double TOLERANCE = 1e-3;
}

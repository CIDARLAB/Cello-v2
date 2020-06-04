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

package org.cellocad.v2.results.technologyMapping;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.cellocad.v2.common.Utils;
import org.cellocad.v2.common.exception.CelloException;
import org.cellocad.v2.common.runtime.environment.ArgString;
import org.cellocad.v2.common.runtime.environment.RuntimeEnv;
import org.cellocad.v2.common.target.data.data.AnalyticFunction;
import org.cellocad.v2.common.target.data.data.AssignableDevice;
import org.cellocad.v2.common.target.data.data.EvaluationContext;
import org.cellocad.v2.common.target.data.data.Function;
import org.cellocad.v2.common.target.data.data.FunctionType;
import org.cellocad.v2.common.target.data.data.Gate;
import org.cellocad.v2.common.target.data.data.Model;
import org.cellocad.v2.common.target.data.data.Variable;
import org.cellocad.v2.results.common.Result;
import org.cellocad.v2.results.common.Results;
import org.cellocad.v2.results.logicSynthesis.LSResultsUtils;
import org.cellocad.v2.results.logicSynthesis.logic.LSLogicEvaluation;
import org.cellocad.v2.results.logicSynthesis.logic.truthtable.State;
import org.cellocad.v2.results.logicSynthesis.logic.truthtable.TruthTable;
import org.cellocad.v2.results.netlist.Netlist;
import org.cellocad.v2.results.netlist.NetlistEdge;
import org.cellocad.v2.results.netlist.NetlistNode;
import org.cellocad.v2.results.netlist.data.ResultNetlistNodeData;
import org.cellocad.v2.results.technologyMapping.activity.TMActivityEvaluation;
import org.cellocad.v2.results.technologyMapping.activity.activitytable.Activity;
import org.cellocad.v2.results.technologyMapping.activity.activitytable.ActivityTable;

/**
 * Utility methods for generating response plots.
 *
 * @author Timothy Jones
 * @date 2020-01-30
 */
public class ResponsePlotUtils {

  private static String getPlotFilename(final NetlistNode node) {
    String rtn = null;
    final String gateType = node.getResultNetlistNodeData().getDeviceName();
    rtn = String.format(ResponsePlotUtils.S_PREFIX + "%s_%s.png", node.getName(), gateType);
    return rtn;
  }

  private static String getPlotScriptFilename(final NetlistNode node) {
    String rtn = null;
    final String gateType = node.getResultNetlistNodeData().getDeviceName();
    rtn = String.format(ResponsePlotUtils.S_PREFIX + "%s_%s.py", node.getName(), gateType);
    return rtn;
  }

  private static String getColor(final Color color) {
    String rtn = null;
    final int r = color.getRed();
    final int g = color.getGreen();
    final int b = color.getBlue();
    rtn = String.format("#%02x%02x%02x", r, g, b);
    return rtn;
  }

  private static List<Double> getXData() {
    final List<Double> rtn = new ArrayList<>();
    final Double d =
        (Math.log10(ResponsePlotUtils.D_XMAX) - Math.log10(ResponsePlotUtils.D_XMIN))
            / ResponsePlotUtils.I_NUM;
    final Double xmin = Math.log10(ResponsePlotUtils.D_XMIN);
    for (int i = 0; i <= ResponsePlotUtils.I_NUM; i++) {
      rtn.add(Math.pow(10, xmin + d * i));
    }
    return rtn;
  }

  private static List<Double> getYData(final NetlistNode node, final List<Double> x)
      throws CelloException {
    final List<Double> rtn = new ArrayList<>();
    final Model m = node.getResultNetlistNodeData().getDevice().getModel();
    final Function f = m.getFunctionByName(FunctionType.S_RESPONSEFUNCTION);
    // FIXME generalize to UnivariateLookupTableFunction
    final AnalyticFunction a = (AnalyticFunction) f;
    final EvaluationContext ec = new EvaluationContext();
    ec.setNode(node);
    final Map<Variable, Double> value = new HashMap<>();
    if (f.getVariables().size() > 1) {
      throw new RuntimeException("Function is not univariate.");
    }
    for (int i = 0; i < x.size(); i++) {
      final Double xi = x.get(i);
      value.put(a.getVariables().get(0), xi);
      rtn.add(a.evaluate(ec, value).doubleValue());
    }
    return rtn;
  }

  private static String getDoubleList(final List<Double> x) {
    String rtn = "";
    for (final Double n : x) {
      rtn += String.format("%e,", n);
    }
    return rtn;
  }

  private static List<Integer> getHiIdx(final NetlistNode node, final LSLogicEvaluation lsle) {
    final List<Integer> rtn = new ArrayList<>();
    final TruthTable<NetlistNode, NetlistNode> tt = lsle.getTruthTable(node);
    for (int i = 0; i < tt.getNumStates(); i++) {
      final State<NetlistNode> input = tt.getStateAtIdx(i);
      final State<NetlistNode> output = tt.getStateOutput(input);
      final Boolean s = output.getState(node);
      if (s.equals(output.getOne())) {
        rtn.add(i);
      }
    }
    return rtn;
  }

  private static List<Integer> getLoIdx(final NetlistNode node, final LSLogicEvaluation lsle) {
    final List<Integer> rtn = new ArrayList<>();
    final TruthTable<NetlistNode, NetlistNode> tt = lsle.getTruthTable(node);
    for (int i = 0; i < tt.getNumStates(); i++) {
      final State<NetlistNode> input = tt.getStateAtIdx(i);
      final State<NetlistNode> output = tt.getStateOutput(input);
      final Boolean s = output.getState(node);
      if (s.equals(output.getZero())) {
        rtn.add(i);
      }
    }
    return rtn;
  }

  private static List<Double> getHiLoXData(
      final NetlistNode node, final List<Integer> idx, final TMActivityEvaluation tmae) {
    final List<Double> rtn = new ArrayList<>();
    for (final int i : idx) {
      Double d = 0.0;
      for (int j = 0; j < node.getNumInEdge(); j++) {
        final NetlistEdge e = node.getInEdgeAtIdx(j);
        final NetlistNode src = e.getSrc();
        final ActivityTable<NetlistNode, NetlistNode> at = tmae.getActivityTable(src);
        final State<NetlistNode> input = at.getStateAtIdx(i);
        final Activity<NetlistNode> output = at.getActivityOutput(input);
        final Double a = output.getActivity(src);
        d += a;
      }
      rtn.add(d);
    }
    return rtn;
  }

  private static List<Double> getHiLoYData(
      final NetlistNode node, final List<Integer> idx, final TMActivityEvaluation tmae) {
    final List<Double> rtn = new ArrayList<>();
    final ActivityTable<NetlistNode, NetlistNode> at = tmae.getActivityTable(node);
    for (final int i : idx) {
      final State<NetlistNode> input = at.getStateAtIdx(i);
      final Activity<NetlistNode> output = at.getActivityOutput(input);
      final Double a = output.getActivity(node);
      rtn.add(a);
    }
    return rtn;
  }

  private static String getPlotScript(
      final NetlistNode node,
      final LSLogicEvaluation lsle,
      final TMActivityEvaluation tmae,
      final String dir)
      throws CelloException {
    String rtn = null;
    // template
    try {
      rtn = Utils.getResourceAsString("response_plot.py");
    } catch (final IOException e) {
      throw new RuntimeException(e);
    }
    // gate
    final ResultNetlistNodeData data = node.getResultNetlistNodeData();
    final AssignableDevice a = data.getDevice();
    if (!(a instanceof Gate)) {
      throw new RuntimeException("Not a gate.");
    }
    final Gate gate = (Gate) a;
    // data
    final List<Double> x = ResponsePlotUtils.getXData();
    final List<Double> y = ResponsePlotUtils.getYData(node, x);
    // hi & lo
    final List<Integer> hi = ResponsePlotUtils.getHiIdx(node, lsle);
    final List<Integer> lo = ResponsePlotUtils.getLoIdx(node, lsle);
    // output
    final String output = dir + Utils.getFileSeparator() + ResponsePlotUtils.getPlotFilename(node);
    // string replace
    rtn = rtn.replace(ResponsePlotUtils.S_XMIN, String.format("%e", ResponsePlotUtils.D_XMIN));
    rtn = rtn.replace(ResponsePlotUtils.S_XMAX, String.format("%e", ResponsePlotUtils.D_XMAX));
    rtn = rtn.replace(ResponsePlotUtils.S_YMIN, String.format("%e", ResponsePlotUtils.D_YMIN));
    rtn = rtn.replace(ResponsePlotUtils.S_YMAX, String.format("%e", ResponsePlotUtils.D_YMAX));
    rtn =
        rtn.replace(
            ResponsePlotUtils.S_XDATA, String.join(",", ResponsePlotUtils.getDoubleList(x)));
    rtn =
        rtn.replace(
            ResponsePlotUtils.S_YDATA, String.join(",", ResponsePlotUtils.getDoubleList(y)));
    rtn = rtn.replace(ResponsePlotUtils.S_COLOR, ResponsePlotUtils.getColor(gate.getColor()));
    rtn =
        rtn.replace(
            ResponsePlotUtils.S_HIX,
            ResponsePlotUtils.getDoubleList(ResponsePlotUtils.getHiLoXData(node, hi, tmae)));
    rtn =
        rtn.replace(
            ResponsePlotUtils.S_HIY,
            ResponsePlotUtils.getDoubleList(ResponsePlotUtils.getHiLoYData(node, hi, tmae)));
    rtn =
        rtn.replace(
            ResponsePlotUtils.S_LOX,
            ResponsePlotUtils.getDoubleList(ResponsePlotUtils.getHiLoXData(node, lo, tmae)));
    rtn =
        rtn.replace(
            ResponsePlotUtils.S_LOY,
            ResponsePlotUtils.getDoubleList(ResponsePlotUtils.getHiLoYData(node, lo, tmae)));
    rtn = rtn.replace(ResponsePlotUtils.S_OUTPUTFILE, output);
    return rtn;
  }

  private static String getPlotCommand(final RuntimeEnv runEnv, final String file) {
    String rtn = null;
    final String python = runEnv.getOptionValue(ArgString.PYTHONENV);
    rtn = String.format("%s %s", python, file);
    return rtn;
  }

  private static void generatePlot(
      final NetlistNode node,
      final LSLogicEvaluation lsle,
      final TMActivityEvaluation tmae,
      final RuntimeEnv runEnv,
      final Results results)
      throws CelloException {
    final String outDir = runEnv.getOptionValue(ArgString.OUTPUTDIR);
    // script
    final String script = ResponsePlotUtils.getPlotScript(node, lsle, tmae, outDir);
    final String scriptFilename =
        outDir + Utils.getFileSeparator() + ResponsePlotUtils.getPlotScriptFilename(node);
    Utils.writeToFile(script, scriptFilename);
    // plot
    final String cmd = ResponsePlotUtils.getPlotCommand(runEnv, scriptFilename);
    Utils.executeAndWaitForCommand(cmd);
    final File file =
        new File(outDir + Utils.getFileSeparator() + Utils.getFilename(scriptFilename) + ".png");
    if (file.exists()) {
      final Result result =
          new Result(
              "response_plot",
              "technologyMapping",
              "The response plot for node " + node.getName() + ".",
              file);
      try {
        results.addResult(result);
      } catch (IOException e) {
        throw new CelloException("Unable to write result.", e);
      }
    }
  }

  /**
   * Generate the response plots for all non-primary nodes in a netlist.
   *
   * @param netlist A netlist.
   * @param lsle The logic evaluation of the netlist.
   * @param tmae The activity evaluation of the netlist.
   * @param runEnv The runtime environment that contains the output directory.
   * @param results The results.
   * @throws CelloException Unable to generate the response plots.
   */
  public static void generatePlots(
      final Netlist netlist,
      final LSLogicEvaluation lsle,
      final TMActivityEvaluation tmae,
      final RuntimeEnv runEnv,
      final Results results)
      throws CelloException {
    for (int i = 0; i < netlist.getNumVertex(); i++) {
      final NetlistNode node = netlist.getVertexAtIdx(i);
      if (LSResultsUtils.isAllInput(node) || LSResultsUtils.isAllOutput(node)) {
        continue;
      }
      ResponsePlotUtils.generatePlot(node, lsle, tmae, runEnv, results);
    }
  }

  private static Double D_XMIN = 1e-3;
  private static Double D_XMAX = 1e2;
  private static Double D_YMIN = 1e-3;
  private static Double D_YMAX = 1e2;
  private static Integer I_NUM = 100;

  private static String S_PREFIX = "response_plot_";

  private static String S_NONCE = "##NONCE##21##";
  private static String S_XMIN = ResponsePlotUtils.S_NONCE + "XMIN" + ResponsePlotUtils.S_NONCE;
  private static String S_XMAX = ResponsePlotUtils.S_NONCE + "XMAX" + ResponsePlotUtils.S_NONCE;
  private static String S_YMIN = ResponsePlotUtils.S_NONCE + "YMIN" + ResponsePlotUtils.S_NONCE;
  private static String S_YMAX = ResponsePlotUtils.S_NONCE + "YMAX" + ResponsePlotUtils.S_NONCE;
  private static String S_XDATA = ResponsePlotUtils.S_NONCE + "XDATA" + ResponsePlotUtils.S_NONCE;
  private static String S_YDATA = ResponsePlotUtils.S_NONCE + "YDATA" + ResponsePlotUtils.S_NONCE;
  private static String S_COLOR = ResponsePlotUtils.S_NONCE + "COLOR" + ResponsePlotUtils.S_NONCE;
  private static String S_HIX = ResponsePlotUtils.S_NONCE + "HIX" + ResponsePlotUtils.S_NONCE;
  private static String S_HIY = ResponsePlotUtils.S_NONCE + "HIY" + ResponsePlotUtils.S_NONCE;
  private static String S_LOX = ResponsePlotUtils.S_NONCE + "LOX" + ResponsePlotUtils.S_NONCE;
  private static String S_LOY = ResponsePlotUtils.S_NONCE + "LOY" + ResponsePlotUtils.S_NONCE;
  private static String S_OUTPUTFILE =
      ResponsePlotUtils.S_NONCE + "OUTPUTFILE" + ResponsePlotUtils.S_NONCE;
}

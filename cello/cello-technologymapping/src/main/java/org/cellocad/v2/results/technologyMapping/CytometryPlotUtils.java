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

package org.cellocad.v2.results.technologyMapping;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.cellocad.v2.common.CelloException;
import org.cellocad.v2.common.Pair;
import org.cellocad.v2.common.Utils;
import org.cellocad.v2.common.runtime.environment.ArgString;
import org.cellocad.v2.common.runtime.environment.RuntimeEnv;
import org.cellocad.v2.common.target.data.data.AssignableDevice;
import org.cellocad.v2.common.target.data.data.BivariateLookupTableFunction;
import org.cellocad.v2.common.target.data.data.EvaluationContext;
import org.cellocad.v2.common.target.data.data.FunctionType;
import org.cellocad.v2.common.target.data.data.Gate;
import org.cellocad.v2.common.target.data.data.Variable;
import org.cellocad.v2.results.logicSynthesis.LSResultsUtils;
import org.cellocad.v2.results.logicSynthesis.logic.LSLogicEvaluation;
import org.cellocad.v2.results.logicSynthesis.logic.truthtable.State;
import org.cellocad.v2.results.logicSynthesis.logic.truthtable.States;
import org.cellocad.v2.results.netlist.Netlist;
import org.cellocad.v2.results.netlist.NetlistNode;
import org.cellocad.v2.results.netlist.data.ResultNetlistNodeData;
import org.cellocad.v2.results.technologyMapping.activity.TMActivityEvaluation;

/**
 * Utilities for generating cytometry plots for each gate.
 *
 * @author Timothy Jones
 * @date 2020-05-25
 */
public class CytometryPlotUtils {

  private static String getPlotFilename(final NetlistNode node) {
    String rtn = null;
    final String gateType = node.getResultNetlistNodeData().getDeviceName();
    rtn = String.format(S_PREFIX + "%s_%s.png", node.getName(), gateType);
    return rtn;
  }

  private static String getPlotScriptFilename(final NetlistNode node) {
    String rtn = null;
    final String gateType = node.getResultNetlistNodeData().getDeviceName();
    rtn = String.format(S_PREFIX + "%s_%s.py", node.getName(), gateType);
    return rtn;
  }

  private static List<Double> getXData(final NetlistNode node) {
    List<Double> rtn = new ArrayList<>();
    AssignableDevice a = node.getResultNetlistNodeData().getDevice();
    Gate g = (Gate) a;
    BivariateLookupTableFunction c =
        (BivariateLookupTableFunction) g.getModel().getFunctionByName("cytometry");
    rtn = c.getYDataAtIdx(0);
    return rtn;
  }

  private static List<Double> getYData(
      final NetlistNode node,
      final TMActivityEvaluation tmae,
      final EvaluationContext ec,
      final State<NetlistNode> state)
      throws CelloException {
    List<Double> rtn = new ArrayList<>();
    AssignableDevice a = node.getResultNetlistNodeData().getDevice();
    Gate g = (Gate) a;
    BivariateLookupTableFunction c =
        (BivariateLookupTableFunction) g.getModel().getFunctionByName("cytometry");
    Variable x = c.getVariables().findCObjectByName("x");
    ec.setNode(node);
    ec.setState(state);
    final Double result =
        node.getResultNetlistNodeData()
            .getDevice()
            .getModel()
            .getFunctionByName(FunctionType.S_INPUTCOMPOSITION)
            .evaluate(ec)
            .doubleValue();
    Pair<Variable, Double> map = new Pair<>(x, result);
    rtn = c.evaluate(map);
    return rtn;
  }

  private static String getDoubleList(final List<Double> x) {
    String rtn = "";
    for (final Double n : x) {
      rtn += String.format("%e,", n);
    }
    return rtn;
  }

  private static String getPlotScript(
      final NetlistNode node,
      final LSLogicEvaluation lsle,
      final TMActivityEvaluation tmae,
      final EvaluationContext ec,
      final String dir)
      throws CelloException {
    String rtn = null;
    // template
    try {
      rtn = Utils.getResourceAsString(S_TEMPLATE);
    } catch (final IOException e) {
      throw new RuntimeException(e);
    }
    // gate
    final ResultNetlistNodeData data = node.getResultNetlistNodeData();
    final AssignableDevice a = data.getDevice();
    if (!(a instanceof Gate)) {
      throw new RuntimeException("Not a gate.");
    }
    // output
    final String output = dir + Utils.getFileSeparator() + getPlotFilename(node);
    // string replace
    rtn = rtn.replace(S_XMIN, String.format("%e", D_XMIN));
    rtn = rtn.replace(S_XMAX, String.format("%e", D_XMAX));
    States<NetlistNode> states = tmae.getStates();
    rtn = rtn.replace(S_NUMPLOTS, String.format("%d", states.getNumStates()));
    String plots = "";
    for (int i = 0; i < states.getNumStates(); i++) {
      State<NetlistNode> state = states.getStateAtIdx(i);
      final List<Double> x = getXData(node);
      final List<Double> y = getYData(node, tmae, ec, state);
      final String fmt = "ax[%d].plot([%s], [%s])" + Utils.getNewLine();
      final String xArr = String.join(",", getDoubleList(x));
      final String yArr = String.join(",", getDoubleList(y));
      String plot = String.format(fmt, i, xArr, yArr);
      plots += plot;
    }
    rtn = rtn.replace(S_PLOTS, plots);
    rtn = rtn.replace(S_OUTPUTFILE, output);
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
      final EvaluationContext ec,
      final RuntimeEnv runEnv)
      throws CelloException {
    final String outDir = runEnv.getOptionValue(ArgString.OUTPUTDIR);
    // script
    final String script = getPlotScript(node, lsle, tmae, ec, outDir);
    final String scriptFilename = outDir + Utils.getFileSeparator() + getPlotScriptFilename(node);
    Utils.writeToFile(script, scriptFilename);
    // plot
    final String cmd = getPlotCommand(runEnv, scriptFilename);
    Utils.executeAndWaitForCommand(cmd);
  }

  /**
   * Generate the response plots for all non-primary nodes in a netlist.
   *
   * @param netlist A netlist.
   * @param lsle The logic evaluation of the netlist.
   * @param tmae The activity evaluation of the netlist.
   * @param runEnv The runtime environment that contains the output directory.
   * @throws CelloException Unable to generate the response plots.
   */
  public static void generatePlots(
      final Netlist netlist,
      final LSLogicEvaluation lsle,
      final TMActivityEvaluation tmae,
      final RuntimeEnv runEnv)
      throws CelloException {
    for (int i = 0; i < netlist.getNumVertex(); i++) {
      final NetlistNode node = netlist.getVertexAtIdx(i);
      if (LSResultsUtils.isAllInput(node) || LSResultsUtils.isAllOutput(node)) {
        continue;
      }
      EvaluationContext ec = new EvaluationContext();
      generatePlot(node, lsle, tmae, ec, runEnv);
    }
  }

  private static final Double D_XMIN = 1e-3;
  private static final Double D_XMAX = 1e2;

  private static final String S_PREFIX = "cytometry_plot_";
  private static final String S_TEMPLATE = "cytometry_plot.py";

  private static final String S_NONCE = "##NONCE##21##";
  private static final String S_NUMPLOTS = S_NONCE + "NUM_PLOTS" + S_NONCE;
  private static final String S_XMIN = S_NONCE + "XMIN" + S_NONCE;
  private static final String S_XMAX = S_NONCE + "XMAX" + S_NONCE;
  private static final String S_PLOTS = S_NONCE + "PLOTS" + S_NONCE;
  private static final String S_OUTPUTFILE = S_NONCE + "OUTPUTFILE" + S_NONCE;
}

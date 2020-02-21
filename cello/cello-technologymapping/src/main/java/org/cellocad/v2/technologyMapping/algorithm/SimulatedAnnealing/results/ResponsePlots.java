/**
 * Copyright (C) 2020 Boston University (BU)
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:

 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.

 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package org.cellocad.v2.technologyMapping.algorithm.SimulatedAnnealing.results;

import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.cellocad.MIT.dnacompiler.MathEval;
import org.cellocad.MIT.dnacompiler.ResponseFunction;
import org.cellocad.v2.common.Utils;
import org.cellocad.v2.common.runtime.environment.RuntimeEnv;
import org.cellocad.v2.common.target.data.component.AssignableDevice;
import org.cellocad.v2.common.target.data.component.Gate;
import org.cellocad.v2.common.target.data.model.FixedParameter;
import org.cellocad.v2.results.logicSynthesis.LSResultsUtils;
import org.cellocad.v2.results.logicSynthesis.logic.LSLogicEvaluation;
import org.cellocad.v2.results.logicSynthesis.logic.truthtable.State;
import org.cellocad.v2.results.logicSynthesis.logic.truthtable.TruthTable;
import org.cellocad.v2.results.netlist.Netlist;
import org.cellocad.v2.results.netlist.NetlistEdge;
import org.cellocad.v2.results.netlist.NetlistNode;
import org.cellocad.v2.results.technologyMapping.activity.TMActivityEvaluation;
import org.cellocad.v2.results.technologyMapping.activity.activitytable.Activity;
import org.cellocad.v2.results.technologyMapping.activity.activitytable.ActivityTable;
import org.cellocad.v2.technologyMapping.algorithm.SimulatedAnnealing.SimulatedAnnealingUtils;
import org.cellocad.v2.technologyMapping.algorithm.SimulatedAnnealing.data.SimulatedAnnealingNetlistNodeData;
import org.cellocad.v2.technologyMapping.runtime.environment.TMArgString;

/**
 *
 *
 * @author Timothy Jones
 *
 * @date 2020-01-30
 *
 */
public class ResponsePlots {

	public static String getPlotFilename(final NetlistNode node) {
		String rtn = null;
		String gateType = node.getResultNetlistNodeData().getDeviceName();
		rtn = String.format(S_PREFIX + "%s_%s.png", node.getName(), gateType);
		return rtn;
	}

	private static String getPlotScriptFilename(final NetlistNode node) {
		String rtn = null;
		String gateType = node.getResultNetlistNodeData().getDeviceName();
		rtn = String.format(S_PREFIX + "%s_%s.py", node.getName(), gateType);
		return rtn;
	}

	private static String getColor(Color color) {
		String rtn = null;
		int r = color.getRed();
		int g = color.getGreen();
		int b = color.getBlue();
		rtn = String.format("#%02x%02x%02x", r, g, b);
		return rtn;
	}

	private static List<Double> getXData() {
		List<Double> rtn = new ArrayList<Double>();
		Double d = (Math.log10(D_XMAX) - Math.log10(D_XMIN)) / I_NUM;
		Double xmin = Math.log10(D_XMIN);
		for (int i = 0; i <= I_NUM; i++) {
			rtn.add(Math.pow(10, xmin + d * i));
		}
		return rtn;
	}

	private static List<Double> getYData(final ResponseFunction rf, List<Double> x) {
		List<Double> rtn = new ArrayList<Double>();
		String equation = rf.getEquation();
		MathEval eval = new MathEval();
		for (int i = 0; i < rf.getNumParameter(); i++) {
			FixedParameter param = rf.getParameterAtIdx(i);
			eval.setConstant(param.getName(), param.getValue());
		}
		for (int i = 0; i < x.size(); i++) {
			Double xi = x.get(i);
			eval.setVariable(rf.getVariableByName("x").getName(), xi);
			rtn.add(eval.evaluate(equation));
		}
		return rtn;
	}

	private static String getDoubleList(List<Double> x) {
		String rtn = "";
		for (Double n : x) {
			rtn += String.format("%e,", n);
		}
		return rtn;
	}

	private static List<Integer> getHiIdx(final NetlistNode node, final LSLogicEvaluation lsle) {
		List<Integer> rtn = new ArrayList<>();
		TruthTable<NetlistNode, NetlistNode> tt = lsle.getTruthTable(node);
		for (int i = 0; i < tt.getNumStates(); i++) {
			State<NetlistNode> input = tt.getStateAtIdx(i);
			State<NetlistNode> output = tt.getStateOutput(input);
			Boolean s = output.getState(node);
			if (s.equals(output.getOne())) {
				rtn.add(i);
			}
		}
		return rtn;
	}

	private static List<Integer> getLoIdx(final NetlistNode node, final LSLogicEvaluation lsle) {
		List<Integer> rtn = new ArrayList<>();
		TruthTable<NetlistNode, NetlistNode> tt = lsle.getTruthTable(node);
		for (int i = 0; i < tt.getNumStates(); i++) {
			State<NetlistNode> input = tt.getStateAtIdx(i);
			State<NetlistNode> output = tt.getStateOutput(input);
			Boolean s = output.getState(node);
			if (s.equals(output.getZero())) {
				rtn.add(i);
			}
		}
		return rtn;
	}

	private static List<Double> getHiLoXData(final NetlistNode node, final List<Integer> idx,
			final TMActivityEvaluation tmae) {
		List<Double> rtn = new ArrayList<>();
		for (int i : idx) {
			Double d = 0.0;
			for (int j = 0; j < node.getNumInEdge(); j++) {
				NetlistEdge e = node.getInEdgeAtIdx(j);
				NetlistNode src = e.getSrc();
				ActivityTable<NetlistNode, NetlistNode> at = tmae.getActivityTable(src);
				State<NetlistNode> input = at.getStateAtIdx(i);
				Activity<NetlistNode> output = at.getActivityOutput(input);
				Double a = output.getActivity(src);
				d += a;
			}
			rtn.add(d);
		}
		return rtn;
	}

	private static List<Double> getHiLoYData(final NetlistNode node, final List<Integer> idx,
			final TMActivityEvaluation tmae) {
		List<Double> rtn = new ArrayList<>();
		ActivityTable<NetlistNode, NetlistNode> at = tmae.getActivityTable(node);
		for (int i : idx) {
			State<NetlistNode> input = at.getStateAtIdx(i);
			Activity<NetlistNode> output = at.getActivityOutput(input);
			Double a = output.getActivity(node);
			rtn.add(a);
		}
		return rtn;
	}

	private static String getPlotScript(final NetlistNode node, final LSLogicEvaluation lsle,
			final TMActivityEvaluation tmae, final String dir) {
		String rtn = null;
		// template
		try {
			rtn = Utils.getResourceAsString("response_plot.py");
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		// gate
		SimulatedAnnealingNetlistNodeData data = SimulatedAnnealingUtils.getSimulatedAnnealingNetlistNodeData(node);
		AssignableDevice a = data.getGate();
		if (!(a instanceof Gate))
			throw new RuntimeException("Not a gate.");
		Gate gate = (Gate) a;
		ResponseFunction rf = gate.getResponseFunction();
		// data
		List<Double> x = getXData();
		List<Double> y = getYData(rf, x);
		// hi & lo
		List<Integer> hi = getHiIdx(node, lsle);
		List<Integer> lo = getLoIdx(node, lsle);
		// output
		String output = dir + Utils.getFileSeparator() + getPlotFilename(node);
		// string replace
		rtn = rtn.replace(S_XMIN, String.format("%e", D_XMIN));
		rtn = rtn.replace(S_XMAX, String.format("%e", D_XMAX));
		rtn = rtn.replace(S_YMIN, String.format("%e", D_YMIN));
		rtn = rtn.replace(S_YMAX, String.format("%e", D_YMAX));
		rtn = rtn.replace(S_XDATA, String.join(",", getDoubleList(x)));
		rtn = rtn.replace(S_YDATA, String.join(",", getDoubleList(y)));
		rtn = rtn.replace(S_COLOR, getColor(gate.getColor()));
		rtn = rtn.replace(S_HIX, getDoubleList(getHiLoXData(node, hi, tmae)));
		rtn = rtn.replace(S_HIY, getDoubleList(getHiLoYData(node, hi, tmae)));
		rtn = rtn.replace(S_LOX, getDoubleList(getHiLoXData(node, lo, tmae)));
		rtn = rtn.replace(S_LOY, getDoubleList(getHiLoYData(node, lo, tmae)));
		rtn = rtn.replace(S_OUTPUTFILE, output);
		return rtn;
	}

	private static String getPlotCommand(final RuntimeEnv runEnv, final String file) {
		String rtn = null;
		String python = runEnv.getOptionValue(TMArgString.PYTHONENV);
		rtn = String.format("%s %s", python, file);
		return rtn;
	}

	private static void generatePlot(final NetlistNode node, final LSLogicEvaluation lsle,
			final TMActivityEvaluation tmae, final RuntimeEnv runEnv) {
		String outDir = runEnv.getOptionValue(TMArgString.OUTPUTDIR);
		// script
		String script = getPlotScript(node, lsle, tmae, outDir);
		String scriptFilename = outDir + Utils.getFileSeparator() + getPlotScriptFilename(node);
		Utils.writeToFile(script, scriptFilename);
		// plot
		String cmd = getPlotCommand(runEnv, scriptFilename);
		Utils.executeAndWaitForCommand(cmd);
	}

	public ResponsePlots(final Netlist netlist, final LSLogicEvaluation lsle, final TMActivityEvaluation tmae,
			final RuntimeEnv runEnv) {
		for (int i = 0; i < netlist.getNumVertex(); i++) {
			NetlistNode node = netlist.getVertexAtIdx(i);
			if (LSResultsUtils.isAllInput(node) || LSResultsUtils.isAllOutput(node))
				continue;
			generatePlot(node, lsle, tmae, runEnv);
		}
	}

	private static Double D_XMIN = 1e-3;
	private static Double D_XMAX = 1e2;
	private static Double D_YMIN = 1e-3;
	private static Double D_YMAX = 1e2;
	private static Integer I_NUM = 100;

	private static String S_PREFIX = "response_plot_";

	private static String S_NONCE = "##NONCE##21##";
	private static String S_XMIN = S_NONCE + "XMIN" + S_NONCE;
	private static String S_XMAX = S_NONCE + "XMAX" + S_NONCE;
	private static String S_YMIN = S_NONCE + "YMIN" + S_NONCE;
	private static String S_YMAX = S_NONCE + "YMAX" + S_NONCE;
	private static String S_XDATA = S_NONCE + "XDATA" + S_NONCE;
	private static String S_YDATA = S_NONCE + "YDATA" + S_NONCE;
	private static String S_COLOR = S_NONCE + "COLOR" + S_NONCE;
	private static String S_HIX = S_NONCE + "HIX" + S_NONCE;
	private static String S_HIY = S_NONCE + "HIY" + S_NONCE;
	private static String S_LOX = S_NONCE + "LOX" + S_NONCE;
	private static String S_LOY = S_NONCE + "LOY" + S_NONCE;
	private static String S_OUTPUTFILE = S_NONCE + "OUTPUTFILE" + S_NONCE;

}

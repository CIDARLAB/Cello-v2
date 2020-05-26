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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.apache.commons.math3.ml.distance.EuclideanDistance;
import org.cellocad.v2.common.CelloException;
import org.cellocad.v2.common.Pair;
import org.cellocad.v2.common.profile.ProfileUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 * A bivariate lookup table function.
 *
 * @author Timothy Jones
 * @date 2020-02-21
 */
public class BivariateLookupTableFunction extends LookupTableFunction {

  private void init() {
    vars = new Variable[2];
  }

  @SuppressWarnings("unchecked")
  private void parseTable(final JSONObject jsonObj) throws CelloException {
    final JSONArray jArr = (JSONArray) jsonObj.get(LookupTableFunction.S_TABLE);
    if (jArr == null) {
      final String fmt = "Invalid %s specification: %s.";
      final String str =
          String.format(
              fmt, BivariateLookupTableFunction.class.getSimpleName(), jsonObj.toString());
      throw new CelloException(str);
    }
    if (jArr.size() == 0) {
      return;
    }
    // examine structure
    final JSONObject template = (JSONObject) jArr.get(0);
    for (final Variable var : getVariables()) {
      final Object entry = template.get(var.getName());
      if (entry instanceof Double) {
        vars[0] = var;
      } else if (entry instanceof JSONArray) {
        vars[1] = var;
        // instantiate table
        final int n = jArr.size();
        final int m = ((JSONArray) entry).size();
        xdata = new Double[n];
        ydata = new Double[n][m];
        zdata = new Double[n][m];
      }
    }
    // build table
    for (int i = 0; i < jArr.size(); i++) {
      final JSONObject entry = (JSONObject) jArr.get(i);
      xdata[i] = ProfileUtils.getDouble(entry, vars[0].getName());
      final JSONArray y = (JSONArray) entry.get(vars[1].getName());
      final JSONArray z = (JSONArray) entry.get(LookupTableFunction.S_OUTPUT);
      ydata[i] = (Double[]) y.toArray(ydata[i]);
      zdata[i] = (Double[]) z.toArray(zdata[i]);
    }
  }

  private void parseLookupTableFunction(final JSONObject jsonObj) throws CelloException {
    parseTable(jsonObj);
  }

  /**
   * Initializes a newly created {@link BivariateLookupTableFunction} with the given JSON data.
   *
   * @param jsonObj The JSON data associated with the {@link BivariateLookupTableFunction} object.
   * @throws CelloException Unable to initialize {@link BivariateLookupTableFunction}.
   */
  public BivariateLookupTableFunction(final JSONObject jsonObj) throws CelloException {
    super(jsonObj);
    init();
    parseLookupTableFunction(jsonObj);
  }

  @Override
  public boolean isValid() {
    boolean rtn = super.isValid();
    rtn = rtn && xdata != null;
    rtn = rtn && ydata != null;
    rtn = rtn && zdata != null;
    return rtn;
  }

  private static Pair<Integer, Integer> argNearestPair(final Double[] p, final Double x) {
    Pair<Integer, Integer> rtn = null;
    if (p == null || p.length == 0) {
      throw new RuntimeException("Input array is null.");
    }
    if (p.length == 1) {
      rtn = new Pair<>(0, 0);
    } else {
      final int w = p.length / 2;
      if (x > p[w - 1]) {
        if (x < p[w]) {
          rtn = new Pair<>(w - 1, w);
        } else {
          rtn = BivariateLookupTableFunction.argNearestPair(Arrays.copyOfRange(p, w, p.length), x);
          rtn.setFirst(rtn.getFirst() + w);
          rtn.setSecond(rtn.getSecond() + w);
        }
      } else {
        rtn = BivariateLookupTableFunction.argNearestPair(Arrays.copyOfRange(p, 0, w), x);
      }
    }
    return rtn;
  }

  @Override
  public Number evaluate(final EvaluationContext ec) throws CelloException {
    final String fmt = "Cannot evaluate %s in scalar context.";
    throw new CelloException(
        String.format(fmt, BivariateLookupTableFunction.class.getSimpleName()));
  }

  /**
   * Evaluates this instance with the given variable mapping.
   *
   * @param value A variable mapping.
   * @return The list of results.
   */
  public List<Double> evaluate(final Pair<Variable, Double> value) {
    List<Double> rtn = null;
    final Variable var = value.getFirst();
    if (var == vars[0]) {
      final Double x = value.getSecond();
      final Pair<Integer, Integer> xbar = BivariateLookupTableFunction.argNearestPair(xdata, x);
      final Double w1 = Math.abs(xdata[xbar.getFirst()] - x);
      final Double w2 = Math.abs(xdata[xbar.getSecond()] - x);
      if (w1 < 1e-8) {
        rtn = Arrays.asList(zdata[xbar.getFirst()]);
      } else if (w2 < 1e-8) {
        rtn = Arrays.asList(zdata[xbar.getSecond()]);
      } else {
        rtn = new ArrayList<>();
        for (int i = 0; i < ydata[xbar.getFirst()].length; i++) {
          rtn.add(
              (zdata[xbar.getFirst()][i] / w1 + zdata[xbar.getSecond()][i] / w2)
                  / (1.0 / w1 + 1.0 / w2));
        }
      }
    } else {
      throw new UnsupportedOperationException("Not implemented.");
    }
    return rtn;
  }

  /**
   * Evaluates this instance with the given variable mapping.
   *
   * @param value A variable mapping.
   * @return The result.
   */
  public Double evaluate(final Map<Variable, Double> value) {
    Double rtn = null;
    final Variable vi = vars[0];
    final Double x = value.get(vi);
    final Variable vj = vars[1];
    final Double y = value.get(vj);
    final Pair<Integer, Integer> xbar = BivariateLookupTableFunction.argNearestPair(xdata, x);
    final Pair<Integer, Integer> ybar =
        BivariateLookupTableFunction.argNearestPair(ydata[xbar.getFirst()], y);
    final List<Double> px = new ArrayList<>();
    final List<Double> py = new ArrayList<>();
    final List<Double> pz = new ArrayList<>();
    px.add(xdata[xbar.getFirst()]);
    px.add(xdata[xbar.getFirst()]);
    py.add(ydata[xbar.getFirst()][ybar.getFirst()]);
    py.add(ydata[xbar.getFirst()][ybar.getSecond()]);
    pz.add(zdata[xbar.getFirst()][ybar.getFirst()]);
    pz.add(zdata[xbar.getFirst()][ybar.getSecond()]);
    if (xbar.getFirst() != xbar.getSecond()) {
      final Pair<Integer, Integer> ybar2 =
          BivariateLookupTableFunction.argNearestPair(ydata[xbar.getSecond()], y);
      px.add(xdata[xbar.getSecond()]);
      px.add(xdata[xbar.getSecond()]);
      py.add(ydata[xbar.getSecond()][ybar2.getFirst()]);
      py.add(ydata[xbar.getSecond()][ybar2.getSecond()]);
      pz.add(zdata[xbar.getSecond()][ybar2.getFirst()]);
      pz.add(zdata[xbar.getSecond()][ybar2.getSecond()]);
    }
    final EuclideanDistance eu = new EuclideanDistance();
    Double top = 0.0;
    Double bot = 0.0;
    for (int i = 0; i < px.size(); i++) {
      final double[] a = new double[] {px.get(i), py.get(i)};
      final double[] b = new double[] {x, y};
      final Double d = eu.compute(a, b);
      if (d < 1e-12) {
        top = pz.get(i);
        bot = 1.0;
        break;
      }
      final Double w = 1.0 / d;
      top += pz.get(i) * w;
      bot += w;
    }
    rtn = top / bot;
    return rtn;
  }

  /**
   * Get the Y data at the specified index.
   *
   * @param idx The index.
   * @return The Y data at the specified index.
   */
  public List<Double> getYDataAtIdx(final Integer idx) {
    List<Double> rtn = new ArrayList<>();
    rtn = Arrays.asList(this.ydata[idx]);
    return rtn;
  }

  private Variable[] vars;
  private Double[] xdata;
  private Double[][] ydata;
  private Double[][] zdata;
}

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
package org.cellocad.v2.common.target.data.model;

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
 *
 *
 * @author Timothy Jones
 *
 * @date 2020-02-21
 *
 */
public class BivariateLookupTableFunction extends LookupTableFunction {

	private void init() {
		this.vars = new Variable[2];
	}

	@SuppressWarnings("unchecked")
	private void parseTable(JSONObject JObj) throws CelloException {
		JSONArray jArr = (JSONArray) JObj.get(LookupTableFunction.S_TABLE);
		if (jArr == null) {
			String fmt = "Invalid %s specification: %s.";
			String str = String.format(fmt, BivariateLookupTableFunction.class.getSimpleName(), JObj.toString());
			throw new CelloException(str);
		}
		if (jArr.size() == 0)
			return;
		// examine structure
		JSONObject template = (JSONObject) jArr.get(0);
		for (Variable var : this.getVariables()) {
			Object entry = template.get(var.getName());
			if (entry instanceof Double) {
				vars[0] = var;
			} else if (entry instanceof JSONArray) {
				vars[1] = var;
				// instantiate table
				int n = jArr.size();
				int m = ((JSONArray) entry).size();
				this.X = new Double[n];
				this.Y = new Double[n][m];
				this.Z = new Double[n][m];
			}
		}
		// build table
		for (int i = 0; i < jArr.size(); i++) {
			JSONObject entry = (JSONObject) jArr.get(i);
			this.X[i] = ProfileUtils.getDouble(entry, vars[0].getName());
			JSONArray y = (JSONArray) entry.get(vars[1].getName());
			JSONArray z = (JSONArray) entry.get(S_OUTPUT);
			this.Y[i] = (Double[]) y.toArray(this.Y[i]);
			this.Z[i] = (Double[]) z.toArray(this.Z[i]);
		}
	}

	private void parseLookupTableFunction(JSONObject JObj) throws CelloException {
		this.parseTable(JObj);
	}

	public BivariateLookupTableFunction(JSONObject jObj) throws CelloException {
		super(jObj);
		this.init();
		this.parseLookupTableFunction(jObj);
	}

	@Override
	public boolean isValid() {
		boolean rtn = super.isValid();
		rtn = rtn && (this.X != null);
		rtn = rtn && (this.Y != null);
		rtn = rtn && (this.Z != null);
		return rtn;
	}

	@Override
	public Number evaluate(final EvaluationContext ec) throws CelloException {
		String fmt = "Cannot evaluate %s in scalar context.";
		throw new CelloException(String.format(fmt, BivariateLookupTableFunction.class.getSimpleName()));
	}

	private static Pair<Integer, Integer> argNearestPair(final Double[] p, final Double x) {
		Pair<Integer, Integer> rtn = null;
		if (p == null || p.length == 0)
			throw new RuntimeException("Input array is null.");
		if (p.length == 1) {
			rtn = new Pair<>(0, 0);
		} else {
			int w = p.length / 2;
			if (x > p[w - 1]) {
				if (x < p[w]) {
					rtn = new Pair<>(w - 1, w);
				} else {
					rtn = argNearestPair(Arrays.copyOfRange(p, w, p.length), x);
					rtn.setFirst(rtn.getFirst() + w);
					rtn.setSecond(rtn.getSecond() + w);
				}
			} else {
				rtn = argNearestPair(Arrays.copyOfRange(p, 0, w), x);
			}
		}
		return rtn;
	}

	public List<Double> evaluate(final Pair<Variable, Double> value) {
		List<Double> rtn = null;
		Variable var = value.getFirst();
		if (var == vars[0]) {
			Double x = value.getSecond();
			Pair<Integer, Integer> xbar = argNearestPair(X, x);
			Double w1 = Math.abs(X[xbar.getFirst()] - x);
			Double w2 = Math.abs(X[xbar.getSecond()] - x);
			if (w1 < 1e-8) {
				rtn = Arrays.asList(Z[xbar.getFirst()]);
			} else if (w2 < 1e-8) {
				rtn = Arrays.asList(Z[xbar.getSecond()]);
			} else {
				rtn = new ArrayList<>();
				for (int i = 0; i < Y[xbar.getFirst()].length; i++) {
					rtn.add((Z[xbar.getFirst()][i] / w1 + Z[xbar.getSecond()][i] / w2) / (1.0 / w1 + 1.0 / w2));
				}
			}
		} else {
			throw new UnsupportedOperationException("Not implemented.");
		}
		return rtn;
	}

	public Double evaluate(final Map<Variable, Double> value) {
		Double rtn = null;
		Variable vi = vars[0];
		Double x = value.get(vi);
		Variable vj = vars[1];
		Double y = value.get(vj);
		Pair<Integer, Integer> xbar = argNearestPair(X, x);
		Pair<Integer, Integer> ybar = argNearestPair(Y[xbar.getFirst()], y);
		List<Double> px = new ArrayList<>();
		List<Double> py = new ArrayList<>();
		List<Double> pz = new ArrayList<>();
		px.add(X[xbar.getFirst()]);
		px.add(X[xbar.getFirst()]);
		py.add(Y[xbar.getFirst()][ybar.getFirst()]);
		py.add(Y[xbar.getFirst()][ybar.getSecond()]);
		pz.add(Z[xbar.getFirst()][ybar.getFirst()]);
		pz.add(Z[xbar.getFirst()][ybar.getSecond()]);
		if (xbar.getFirst() != xbar.getSecond()) {
			Pair<Integer, Integer> ybar2 = argNearestPair(Y[xbar.getSecond()], y);
			px.add(X[xbar.getSecond()]);
			px.add(X[xbar.getSecond()]);
			py.add(Y[xbar.getSecond()][ybar2.getFirst()]);
			py.add(Y[xbar.getSecond()][ybar2.getSecond()]);
			pz.add(Z[xbar.getSecond()][ybar2.getFirst()]);
			pz.add(Z[xbar.getSecond()][ybar2.getSecond()]);
		}
		EuclideanDistance eu = new EuclideanDistance();
		Double top = 0.0;
		Double bot = 0.0;
		for (int i = 0; i < px.size(); i++) {
			double[] a = new double[] { px.get(i), py.get(i) };
			double[] b = new double[] { x, y };
			Double d = eu.compute(a, b);
			if (d < 1e-12) {
				top = pz.get(i);
				bot = 1.0;
				break;
			}
			Double w = 1.0 / d;
			top += pz.get(i) * w;
			bot += w;
		}
		rtn = top / bot;
		return rtn;
	}

	private Variable[] vars;
	private Double[] X;
	private Double[][] Y;
	private Double[][] Z;

}

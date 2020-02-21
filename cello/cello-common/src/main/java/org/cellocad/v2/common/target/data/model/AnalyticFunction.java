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

import org.cellocad.v2.common.CObjectCollection;
import org.cellocad.v2.common.CelloException;
import org.cellocad.v2.common.profile.ProfileUtils;
import org.cellocad.v2.results.logicSynthesis.logic.truthtable.State;
import org.cellocad.v2.results.netlist.NetlistNode;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.mariuszgromada.math.mxparser.Expression;

/**
 *
 *
 * @author Timothy Jones
 *
 * @date 2020-02-21
 *
 */
public final class AnalyticFunction extends Function {

	private void init() {
		this.parameters = new CObjectCollection<>();
	}

	private void parseEquation(final JSONObject JObj) {
		String value = ProfileUtils.getString(JObj, S_EQUATION);
		this.equation = value;
	}

	private void parseParameters(final JSONObject JObj) {
		CObjectCollection<Parameter> parameters = this.getParameters();
		JSONArray jArr = (JSONArray) JObj.get(S_PARAMETERS);
		if (jArr == null)
			return;
		for (int i = 0; i < jArr.size(); i++) {
			JSONObject jObj = (JSONObject) jArr.get(i);
			Parameter e = null;
			if (jObj.containsKey(Reference.S_MAP)) {
				e = new ParameterReference(jObj);
			} else {
				e = new FixedParameter(jObj);
			}
			if (e != null && e.isValid())
				parameters.add(e);
		}
	}

	private void parseFunction(JSONObject jObj) {
		this.parseEquation(jObj);
		this.parseParameters(jObj);
	}

	public AnalyticFunction(JSONObject jObj) {
		super(jObj);
		this.init();
		this.parseFunction(jObj);
	}

	@Override
	public boolean isValid() {
		boolean rtn = super.isValid();
		rtn = rtn && (this.getEquation() != null);
		return rtn;
	}

	@Override
	public Number evaluate(EvaluationContext ec) throws CelloException {
		Double rtn = null;
		Expression expr = new Expression(this.getEquation().replace("$", "_"));
		for (Parameter p : this.getParameters()) {
			expr.defineArgument(p.getName(), p.evaluate(ec).doubleValue());
		}
		for (Variable v : this.getVariables()) {
			expr.defineArgument(v.getName(), v.evaluate(ec).doubleValue());
		}
		NetlistNode node = ec.getNode();
		if (expr.getExpressionString().contains(S_STATE)) {
			State<NetlistNode> state = ec.getState();
			Boolean nodeState = state.getState(node);
			if (nodeState == null)
				throw new CelloException("Node state undefined.");
			Double q = nodeState.equals(state.getOne()) ? 1.0 : 0.0;
			expr.defineArgument(S_STATE, q);
		}
		rtn = expr.calculate();
		return rtn;
	}

	private String getEquation() {
		return equation;
	}

	private CObjectCollection<Parameter> getParameters() {
		return parameters;
	}

	private String equation;
	private CObjectCollection<Parameter> parameters;

	public static final String S_EQUATION = "equation";
	public static final String S_PARAMETERS = "parameters";
	private static final String S_STATE = "_STATE";

}

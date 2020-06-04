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

import java.util.Map;
import org.cellocad.v2.common.CObjectCollection;
import org.cellocad.v2.common.exception.CelloException;
import org.cellocad.v2.common.profile.ProfileUtils;
import org.cellocad.v2.results.logicSynthesis.logic.truthtable.State;
import org.cellocad.v2.results.netlist.NetlistNode;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.mariuszgromada.math.mxparser.Expression;

/**
 * A function that can be evaluated analytically.
 *
 * @author Timothy Jones
 * @date 2020-02-21
 */
public final class AnalyticFunction extends Function {

  private void init() {
    parameters = new CObjectCollection<>();
  }

  private void parseEquation(final JSONObject jsonObj) {
    final String value = ProfileUtils.getString(jsonObj, AnalyticFunction.S_EQUATION);
    equation = value;
  }

  private void parseParameters(final JSONObject jsonObj) {
    final CObjectCollection<Parameter> parameters = getParameters();
    final JSONArray jArr = (JSONArray) jsonObj.get(AnalyticFunction.S_PARAMETERS);
    if (jArr == null) {
      return;
    }
    for (int i = 0; i < jArr.size(); i++) {
      final JSONObject jObj = (JSONObject) jArr.get(i);
      Parameter e = null;
      if (jObj.containsKey(Reference.S_MAP)) {
        e = new ParameterReference(jObj);
      } else {
        e = new FixedParameter(jObj);
      }
      if (e != null && e.isValid()) {
        parameters.add(e);
      }
    }
  }

  private void parseFunction(final JSONObject jObj) {
    parseEquation(jObj);
    parseParameters(jObj);
  }

  /**
   * Initializes a newly created {@link AnalyticFunction} object from the given JSON data.
   *
   * @param jsonObj The JSON data associated with the function.
   */
  public AnalyticFunction(final JSONObject jsonObj) {
    super(jsonObj);
    init();
    parseFunction(jsonObj);
  }

  @Override
  public boolean isValid() {
    boolean rtn = super.isValid();
    rtn = rtn && getEquation() != null;
    return rtn;
  }

  @Override
  public Number evaluate(final EvaluationContext ec) throws CelloException {
    Double rtn = null;
    final Expression expr = new Expression(getEquation().replace("$", "_"));
    for (final Parameter p : getParameters()) {
      expr.defineArgument(p.getName(), p.evaluate(ec).doubleValue());
    }
    for (final Variable v : getVariables()) {
      expr.defineArgument(v.getName(), v.evaluate(ec).doubleValue());
    }
    final NetlistNode node = ec.getNode();
    if (expr.getExpressionString().contains(AnalyticFunction.S_STATE)) {
      final State<NetlistNode> state = ec.getState();
      final Boolean nodeState = state.getState(node);
      if (nodeState == null) {
        throw new CelloException("Node state undefined.");
      }
      final Double q = nodeState.equals(state.getOne()) ? 1.0 : 0.0;
      expr.defineArgument(AnalyticFunction.S_STATE, q);
    }
    rtn = expr.calculate();
    return rtn;
  }

  /**
   * Evaluate the function within the given context, and with the given variable map.
   *
   * @param ec The {@link EvaluationContext}.
   * @param value The variable map.
   * @return The result of the evaluation.
   * @throws CelloException Unable to evaluate the function.
   */
  public Number evaluate(final EvaluationContext ec, final Map<Variable, Double> value)
      throws CelloException {
    Double rtn = null;
    final Expression expr = new Expression(getEquation().replace("$", "_"));
    for (final Parameter p : getParameters()) {
      expr.defineArgument(p.getName(), p.evaluate(ec).doubleValue());
    }
    for (final Variable v : value.keySet()) {
      expr.defineArgument(v.getName(), value.get(v));
    }
    final NetlistNode node = ec.getNode();
    if (expr.getExpressionString().contains(AnalyticFunction.S_STATE)) {
      final State<NetlistNode> state = ec.getState();
      final Boolean nodeState = state.getState(node);
      if (nodeState == null) {
        throw new CelloException("Node state undefined.");
      }
      final Double q = nodeState.equals(state.getOne()) ? 1.0 : 0.0;
      expr.defineArgument(AnalyticFunction.S_STATE, q);
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

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

import org.cellocad.v2.common.CObjectCollection;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 * A base class for an evaluatable function.
 *
 * @author Timothy Jones
 * @date 2020-02-11
 */
public abstract class Function extends Evaluatable {

  private void init() {
    variables = new CObjectCollection<>();
  }

  private void parseVariables(final JSONObject jsonObj) {
    final CObjectCollection<Variable> variables = getVariables();
    final JSONArray jArr = (JSONArray) jsonObj.get(Function.S_VARIABLES);
    if (jArr == null) {
      return;
    }
    for (int i = 0; i < jArr.size(); i++) {
      final JSONObject variableObj = (JSONObject) jArr.get(i);
      final Variable variable = new Variable(variableObj);
      if (variable.isValid()) {
        variables.add(variable);
      }
    }
  }

  private void parseFunction(final JSONObject jObj) {
    parseVariables(jObj);
  }

  /**
   * Initializes a newly created {@link Function} object with the given JSON data.
   *
   * @param jsonObj The JSON data corresponding to the {@link Function} object.
   */
  public Function(final JSONObject jsonObj) {
    super(jsonObj);
    init();
    parseFunction(jsonObj);
  }

  /**
   * Getter for {@code variables}.
   *
   * @return The value of {@code variables}.
   */
  public CObjectCollection<Variable> getVariables() {
    return variables;
  }

  private CObjectCollection<Variable> variables;

  public static final String S_VARIABLES = "variables";
}

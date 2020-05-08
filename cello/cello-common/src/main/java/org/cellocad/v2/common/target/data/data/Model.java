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

import java.util.HashMap;
import java.util.Map;
import org.cellocad.v2.common.CObject;
import org.cellocad.v2.common.CObjectCollection;
import org.cellocad.v2.common.profile.ProfileUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 * A representation of a gate model.
 *
 * @author Timothy Jones
 * @date 2020-02-11
 */
public class Model extends CObject {

  private void init() {
    functions = new HashMap<>();
    parameters = new CObjectCollection<>();
  }

  private void parseName(final JSONObject jsonObj) {
    final String value = ProfileUtils.getString(jsonObj, Model.S_NAME);
    setName(value);
  }

  private void parseParameters(final JSONObject jsonObj) {
    final CObjectCollection<FixedParameter> parameters = getParameters();
    final JSONArray jArr = (JSONArray) jsonObj.get(Model.S_PARAMETERS);
    for (int i = 0; i < jArr.size(); i++) {
      final JSONObject jObj = (JSONObject) jArr.get(i);
      final FixedParameter parameter = new FixedParameter(jObj);
      parameters.add(parameter);
    }
  }

  private void parseModel(final JSONObject jsonObj) {
    init();
    parseName(jsonObj);
    parseParameters(jsonObj);
  }

  public Model(final JSONObject jsonObj) {
    parseModel(jsonObj);
  }

  @Override
  public boolean isValid() {
    boolean rtn = super.isValid();
    rtn = rtn && getName() != null;
    return rtn;
  }

  /*
   * Function
   */

  public void addFunction(final String name, final Function function) {
    getFunctions().put(name, function);
  }

  /**
   * Returns the {@link Function} with name equivalent to parameter {@code name}.
   *
   * @param name The name of the {@link Function} to return.
   * @return The element with name equivalent to parameter {@code name}.
   */
  public Function getFunctionByName(final String name) {
    return getFunctions().get(name);
  }

  /**
   * Getter for {@code functions}.
   *
   * @return The value of {@code functions}.
   */
  private Map<String, Function> getFunctions() {
    return functions;
  }

  private Map<String, Function> functions;

  /*
   * FixedParameter
   */

  public FixedParameter getParameterByName(final String name) {
    return getParameters().findCObjectByName(name);
  }

  /**
   * Gets the parameter at the given index.
   *
   * @param index An index.
   * @return The parameter at the given index.
   */
  public FixedParameter getParameterAtIdx(final int index) {
    FixedParameter rtn = null;
    if (0 <= index && index < getNumParameter()) {
      rtn = getParameters().get(index);
    }
    return rtn;
  }

  /**
   * Returns the number of {@link FixedParameter} objects in this instance.
   *
   * @return The number of {@link FixedParameter} objects in this instance.
   */
  public int getNumParameter() {
    return getParameters().size();
  }

  /**
   * Getter for {@code parameters}.
   *
   * @return The value of {@code parameters}.
   */
  private CObjectCollection<FixedParameter> getParameters() {
    return parameters;
  }

  private CObjectCollection<FixedParameter> parameters;

  static final String S_NAME = "name";
  public static final String S_FUNCTIONS = "functions";
  static final String S_PARAMETERS = "parameters";
}

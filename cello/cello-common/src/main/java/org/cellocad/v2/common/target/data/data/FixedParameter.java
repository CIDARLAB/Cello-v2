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

import org.cellocad.v2.common.profile.ProfileUtils;
import org.json.simple.JSONObject;

/**
 * A representation of a numerical parameter with a constant, fixed value.
 *
 * @author Timothy Jones
 *
 * @date 2020-01-30
 */
public class FixedParameter extends Parameter {

  private void init() {
  }

  private void parseValue(final JSONObject jsonObj) {
    final Double value = ProfileUtils.getDouble(jsonObj, FixedParameter.S_VALUE);
    this.value = value;
  }

  private void parseFixedParameter(final JSONObject jObj) {
    parseName(jObj);
    parseValue(jObj);
  }

  /**
   * Initializes a newly created {@link FixedParameter} with the given JSON data.
   *
   * @param jsonObj The JSON data associated with the parameter.
   */
  public FixedParameter(final JSONObject jsonObj) {
    super(jsonObj);
    init();
    parseFixedParameter(jsonObj);
  }

  @Override
  public Number evaluate(final EvaluationContext ce) {
    return getValue();
  }

  @Override
  public boolean isValid() {
    boolean rtn = super.isValid();
    rtn = rtn && getName() != null;
    rtn = rtn && getValue() != null;
    return rtn;
  }

  private Double getValue() {
    return value;
  }

  private Double value;

  private static final String S_VALUE = "value";

}

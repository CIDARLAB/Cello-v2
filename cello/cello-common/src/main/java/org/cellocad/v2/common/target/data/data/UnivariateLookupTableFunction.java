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

import com.google.common.collect.ImmutableTable;
import com.google.common.collect.ImmutableTable.Builder;
import com.google.common.collect.Table;
import org.apache.commons.math3.ml.distance.EuclideanDistance;
import org.cellocad.v2.common.exception.CelloException;
import org.cellocad.v2.common.profile.ProfileUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 * A univariate lookup table function.
 *
 * @author Timothy Jones
 * @date 2020-02-21
 */
public class UnivariateLookupTableFunction extends LookupTableFunction {

  private void init() {}

  private void parseTable(final JSONObject jsonObj) throws CelloException {
    final Builder<Integer, String, Double> builder = ImmutableTable.builder();
    final JSONObject functionObj = (JSONObject) jsonObj.get(LookupTableFunction.S_TABLE);
    if (functionObj == null) {
      final String fmt = "Invalid %s specification: %s.";
      throw new CelloException(
          String.format(fmt, UnivariateLookupTableFunction.class.getName(), jsonObj.toString()));
    }
    for (final Object key : functionObj.keySet()) {
      final String name = ProfileUtils.getString(key);
      if (getVariables().findCObjectByName(name) == null
          && !name.equals(LookupTableFunction.S_OUTPUT)) {
        final String fmt = "Invalid table key: %s.";
        throw new CelloException(String.format(fmt, name));
      }
      final JSONArray jArr = (JSONArray) functionObj.get(key);
      for (int i = 0; i < jArr.size(); i++) {
        final Double value = (Double) jArr.get(i);
        builder.put(i, name, value);
      }
    }
    table = builder.build();
  }

  private void parseLookupTableFunction(final JSONObject jsonObj) throws CelloException {
    parseTable(jsonObj);
  }

  /**
   * Initializes a newly created {@link UnivariateLookupTableFunction} with the given JSON data.
   *
   * @param jsonObj The JSON data associated with the function.
   * @throws CelloException Unable to initialize function.
   */
  public UnivariateLookupTableFunction(final JSONObject jsonObj) throws CelloException {
    super(jsonObj);
    init();
    parseLookupTableFunction(jsonObj);
  }

  @Override
  public boolean isValid() {
    boolean rtn = super.isValid();
    rtn = rtn && getTable() != null;
    return rtn;
  }

  @Override
  public Number evaluate(final EvaluationContext ec) throws CelloException {
    Double rtn = null;
    final double[] input = new double[getVariables().size()];
    for (int i = 0; i < getVariables().size(); i++) {
      final Variable v = getVariables().get(i);
      final String name = v.getName();
      final Double value = v.evaluate(ec).doubleValue();
      if (!getTable().containsColumn(name)) {
        throw new RuntimeException(String.format("Missing column %s.", name));
      }
      input[i] = value;
    }
    // stupid two-point average interpolation
    Integer argMinA = -1;
    Double minA = Double.MAX_VALUE;
    Integer argMinB = -1;
    Double minB = Double.MAX_VALUE;
    for (final Integer row : getTable().rowKeySet()) {
      final double[] record = new double[getVariables().size()];
      for (int i = 0; i < getVariables().size(); i++) {
        final Variable v = getVariables().get(i);
        final Double d = getTable().get(row, v.getName());
        record[i] = d;
      }
      final EuclideanDistance eu = new EuclideanDistance();
      final Double distance = eu.compute(input, record);
      if (distance < minA) {
        argMinB = argMinA;
        minB = minA;
        argMinA = row;
        minA = distance;
      } else if (distance < minB) {
        argMinB = row;
        minB = distance;
      }
    }
    final Double valueA = getTable().get(argMinA, LookupTableFunction.S_OUTPUT);
    final Double valueB = getTable().get(argMinB, LookupTableFunction.S_OUTPUT);
    rtn = (valueA * minA + valueB * minB) / (minA + minB);
    return rtn;
  }

  private Table<Integer, String, Double> getTable() {
    return table;
  }

  private Table<Integer, String, Double> table;
}

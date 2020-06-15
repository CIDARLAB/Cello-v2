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

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.cellocad.v2.common.Pair;
import org.cellocad.v2.common.Utils;
import org.cellocad.v2.common.exception.CelloException;
import org.cellocad.v2.common.target.data.data.BivariateLookupTableFunction;
import org.cellocad.v2.common.target.data.data.Variable;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Tests for {@link BivariateLookupTableFunction}.
 *
 * @author Timothy Jones
 * @date 2020-02-21
 */
public class BivariateLookupTableFunctionTest {

  /**
   * Environment setup for tests.
   *
   * @throws IOException Unable to read resources.
   * @throws ParseException Unable to parse JSON in resources.
   * @throws CelloException Unable to instantiate supporting classes.
   */
  @BeforeClass
  public static void init() throws IOException, ParseException, CelloException {
    JSONParser parser = new JSONParser();
    String str = Utils.getResourceAsString("2d.json");
    JSONObject jsonTop = (JSONObject) parser.parse(str);
    lut = new BivariateLookupTableFunction(jsonTop);
  }

  @Test
  public void evaluate_MockData_ShouldBeExpectedScalar1() throws CelloException {
    Map<Variable, Double> args = new HashMap<>();
    args.put(lut.getVariables().findCObjectByName("x"), 1.5);
    args.put(lut.getVariables().findCObjectByName("bin"), 1.5);
    assertEquals(lut.evaluate(args), 5.0, TOLERANCE);
  }

  @Test
  public void evaluate_MockData_ShouldBeExpectedScalar2() throws CelloException {
    Map<Variable, Double> args = new HashMap<>();
    args.put(lut.getVariables().findCObjectByName("x"), 3.0);
    args.put(lut.getVariables().findCObjectByName("bin"), 3.0);
    assertEquals(lut.evaluate(args), 8.0, TOLERANCE);
  }

  @Test
  public void evaluate_MockData_ShouldBeExpectedScalar3() throws CelloException {
    Map<Variable, Double> args = new HashMap<>();
    args.put(lut.getVariables().findCObjectByName("x"), 4.0);
    args.put(lut.getVariables().findCObjectByName("bin"), 4.0);
    assertEquals(lut.evaluate(args), 8.0, TOLERANCE);
  }

  @Test
  public void evaluate_MockData_ShouldBeExpectedVector1() throws CelloException {
    Variable v = lut.getVariables().findCObjectByName("x");
    Pair<Variable, Double> args = new Pair<>(v, 1.0);
    List<Double> result = lut.evaluate(args);
    assertEquals(result.get(0), 4.0, TOLERANCE);
    assertEquals(result.get(1), 5.0, TOLERANCE);
    assertEquals(result.get(2), 6.0, TOLERANCE);
  }

  @Test
  public void evaluate_MockData_ShouldBeExpectedVector2() throws CelloException {
    Variable v = lut.getVariables().findCObjectByName("x");
    Pair<Variable, Double> args = new Pair<>(v, 1.25);
    List<Double> result = lut.evaluate(args);
    assertEquals(result.get(0), 4.25, TOLERANCE);
    assertEquals(result.get(1), 5.25, TOLERANCE);
    assertEquals(result.get(2), 6.25, TOLERANCE);
  }

  private static BivariateLookupTableFunction lut;

  private static Double TOLERANCE = 1e-8;
}

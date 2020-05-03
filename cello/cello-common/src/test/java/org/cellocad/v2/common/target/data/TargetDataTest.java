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

package org.cellocad.v2.common.target.data;

import java.io.IOException;
import org.cellocad.v2.common.Utils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Tests for {@link TargetData}.
 *
 * @author Timothy Jones
 *
 * @date 2020-02-18
 */
public class TargetDataTest {

  /**
   * Loads sample target data.
   * 
   * @throws IOException    Unable to load target data.
   * @throws ParseException Unable to parse target data.
   */
  @BeforeClass
  public static void init() throws IOException, ParseException {
    String str = Utils.getResourceAsString("lib/ucf/Bth/Bth1C1G1T1.UCF.json");
    JSONParser parser = new JSONParser();
    JSONArray jsonTop = (JSONArray) parser.parse(str);
    td = new TargetData(jsonTop);
  }

  @Test
  public void getNumJsonObject_Foobar_ShouldReturn0() {
    assert (td.getNumJsonObject("foobar") == 0);
  }

  @Test
  public void getNumJsonObject_Functions_ShouldReturn9() {
    assert (td.getNumJsonObject("functions") == 9);
  }

  @Test
  public void getJsonObjectAtIdx_Foobar0_ShouldReturnNull() {
    assert (td.getJsonObjectAtIdx("foobar", 0) == null);
  }

  @Test
  public void getJsonObjectAtIdx_Functions10_ShouldReturnNull() {
    assert (td.getJsonObjectAtIdx("functions", 10) == null);
  }

  @Test
  public void getJsonObjectAtIdx_Functions0_ShouldReturnJsonObject() {
    assert (td.getJsonObjectAtIdx("functions", 0) instanceof JSONObject);
  }

  private static TargetData td;

}

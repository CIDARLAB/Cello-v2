/*
 * Copyright (C) 2017 Massachusetts Institute of Technology (MIT)
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

package org.cellocad.v2.logicSynthesis.algorithm.Yosys;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.cellocad.v2.results.logicSynthesis.LSResults;

/**
 * The YosysUtils class is class with utility methods for the <i>Yosys</i> instances.
 *
 * @author Vincent Mirian
 *
 * @date 2018-05-21
 */
public class YosysUtils {

  /**
   * ValidGates: Array of Strings containing Valid Gates for the <i>Yosys</i> algorithm.
   */
  public static final String[] ValidGates =
      {LSResults.S_NOT, LSResults.S_AND, LSResults.S_NAND, LSResults.S_OR, LSResults.S_NOR,
          LSResults.S_XOR, LSResults.S_XNOR, LSResults.S_ANDNOT, LSResults.S_ORNOT,};

  /**
   * Returns a boolean flag signifying that the parameter {@code gate} is a valid gate.
   *
   * @return True if the parameter {@code gate} is a valid gate, otherwise false.
   */
  public static boolean isValidGates(final String gate) {
    boolean rtn = false;
    final List<String> validGates = new ArrayList<>(Arrays.asList(YosysUtils.ValidGates));
    rtn = validGates.contains(gate);
    return rtn;
  }

  /**
   * Returns a string representing a valid result for the <i>logicSynthesis</i> stage using the
   * parameter {@code type}.
   *
   * @param type The type to validate.
   * @return A string representing a valid result for the <i>logicSynthesis</i> stage using the
   *         parameter {@code type}.
   */
  protected static String getNodeType(final String type) {
    String rtn = type;
    if (rtn.length() >= 2 && rtn.substring(0, 2).equals(YosysUtils.S_TYPE_PREFIX)) {
      rtn = rtn.substring(2);
    }
    if (rtn.length() >= 1 && rtn.substring(rtn.length() - 1).equals(YosysUtils.S_TYPE_POSTFIX)) {
      rtn = rtn.substring(0, rtn.length() - 1);
    }
    if (!YosysUtils.isValidGates(rtn)) {
      rtn = type;
    }
    return rtn;
  }

  private static String S_TYPE_PREFIX = "$_";
  private static String S_TYPE_POSTFIX = "_";

}

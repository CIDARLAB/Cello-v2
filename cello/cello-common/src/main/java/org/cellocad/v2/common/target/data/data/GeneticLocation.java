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

import org.cellocad.v2.common.CObject;
import org.cellocad.v2.common.profile.ProfileUtils;
import org.json.simple.JSONObject;

/**
 * A genetic location object in the target data.
 *
 * @author Timothy Jones
 * @date 2020-01-13
 */
public class GeneticLocation extends CObject {

  private void init() {}

  private void parseSymbol(final JSONObject jsonObj) {
    final String str = ProfileUtils.getString(jsonObj, GeneticLocation.S_SYMBOL);
    setName(str);
  }

  private void parseLocus(final JSONObject jsonObj) {
    final Integer z = ProfileUtils.getInteger(jsonObj, GeneticLocation.S_LOCUS);
    locus = z;
  }

  private void parseLocation(final JSONObject jsonObj) {
    parseSymbol(jsonObj);
    parseLocus(jsonObj);
  }

  public GeneticLocation(final JSONObject jsonObj) {
    init();
    parseLocation(jsonObj);
  }

  /**
   * Getter for {@code locus}.
   *
   * @return The value of {@code locus}.
   */
  public Integer getLocus() {
    return locus;
  }

  private Integer locus;

  private static final String S_SYMBOL = "symbol";
  private static final String S_LOCUS = "locus";
}

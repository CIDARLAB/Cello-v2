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

import org.cellocad.v2.common.CelloException;
import org.cellocad.v2.common.profile.ProfileUtils;
import org.cellocad.v2.results.netlist.NetlistNode;
import org.json.simple.JSONObject;

/**
 * A free variable.
 *
 * @author Timothy Jones
 * @date 2020-02-12
 */
public class Variable extends Evaluatable {

  private void init() {}

  private void parseMap(final JSONObject jsonObj) {
    final String value = ProfileUtils.getString(jsonObj, Reference.S_MAP);
    map = value;
  }

  private void parseVariable(final JSONObject jsonObj) {
    parseName(jsonObj);
    parseMap(jsonObj);
  }

  /**
   * Initializes a newly created {@link Variable} with the given JSON data.
   *
   * @param jsonObj The JSON data associated with the variable.
   */
  public Variable(final JSONObject jsonObj) {
    super(jsonObj);
    init();
    parseVariable(jsonObj);
  }

  @Override
  public Number evaluate(final EvaluationContext ec) throws CelloException {
    Number rtn = null;
    final NetlistNode node = ec.getNode();
    final Evaluatable e = ec.dereference(getMap());
    if (e == null) {
      throw new RuntimeException("Dereference failed.");
    }
    rtn = e.evaluate(ec);
    ec.setNode(node);
    return rtn;
  }

  @Override
  public boolean isValid() {
    boolean rtn = super.isValid();
    rtn = rtn && getName() != null;
    return rtn;
  }

  private String getMap() {
    return map;
  }

  private String map;
}

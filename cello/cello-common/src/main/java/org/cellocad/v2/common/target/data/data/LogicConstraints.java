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

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import org.cellocad.v2.common.CObject;
import org.cellocad.v2.common.profile.ProfileUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 * Represents the logic constraints section of the target data.
 *
 * @author Timothy Jones
 * @date 2020-03-30
 */
public class LogicConstraints extends CObject {

  public static final String S_AVAILABLE_GATES = "available_gates";
  public static final String S_TYPE = "type";
  public static final String S_MAX_INSTANCES = "max_instances";

  private Map<String, Integer> gates;

  private void init() {
    gates = new HashMap<>();
  }

  private void parseAvailableGates(final JSONObject jsonObj) {
    final JSONArray jArr = (JSONArray) jsonObj.get(LogicConstraints.S_AVAILABLE_GATES);
    for (int i = 0; i < jArr.size(); i++) {
      final JSONObject o = (JSONObject) jArr.get(i);
      final String type = ProfileUtils.getString(o, LogicConstraints.S_TYPE);
      final Object value = o.get(LogicConstraints.S_MAX_INSTANCES);
      if (value instanceof Boolean && (Boolean) value) {
        gates.put(type, Integer.MAX_VALUE);
      } else {
        final Integer max = ProfileUtils.getInteger(value);
        gates.put(type, max);
      }
    }
  }

  private void parseGate(final JSONObject jsonObj) {
    parseAvailableGates(jsonObj);
  }

  /**
   * Initializes a newly created {@link LogicConstraints} object with the given JSON data.
   *
   * @param jsonObj The JSON data associated with the {@link LogicConstraints} object.
   */
  public LogicConstraints(final JSONObject jsonObj) {
    super();
    init();
    parseGate(jsonObj);
  }

  public Collection<String> getAvailableGates() {
    final Collection<String> rtn = gates.keySet();
    return rtn;
  }

  /**
   * Get the maximum instances of the given gate type.
   *
   * @param gateType A gate type.
   * @return The maximum instances of the given gate type.
   */
  public Integer getMaxInstances(final String gateType) {
    Integer rtn = null;
    rtn = gates.get(gateType);
    return rtn;
  }
}

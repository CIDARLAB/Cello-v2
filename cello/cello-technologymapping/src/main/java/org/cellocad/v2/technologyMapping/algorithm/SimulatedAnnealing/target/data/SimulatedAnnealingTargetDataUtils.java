/*
 * Copyright (C) 2020 Boston University (BU)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package org.cellocad.v2.technologyMapping.algorithm.SimulatedAnnealing.target.data;

import org.cellocad.v2.common.target.data.TargetData;
import org.cellocad.v2.common.target.data.data.DeviceRules;
import org.json.simple.JSONObject;

/**
 * Utilities for the target data in the <i>SimulatedAnnealing</i> algorithm.
 *
 * @author Timothy Jones
 * @date 2020-06-10
 */
public class SimulatedAnnealingTargetDataUtils {
  /**
   * Get the device rules contained in the target data.
   *
   * @param td The {@link TargetData}.
   * @return The device rules contained in the target data.
   */
  public static DeviceRules getDeviceRules(final TargetData td) {
    DeviceRules rtn = null;
    final JSONObject jObj = td.getJsonObjectAtIdx(S_DEVICERULES, 0);
    rtn = new DeviceRules(jObj);
    return rtn;
  }

  private static String S_DEVICERULES = "device_rules";
}

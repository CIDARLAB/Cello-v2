/*
 * Copyright (C) 2018 Boston University (BU)
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

package org.cellocad.v2.placing.algorithm.Eugene.target.data;

import org.cellocad.v2.common.CObjectCollection;
import org.cellocad.v2.common.target.data.TargetData;
import org.cellocad.v2.common.target.data.data.CircuitRules;
import org.cellocad.v2.common.target.data.data.DeviceRules;
import org.cellocad.v2.common.target.data.data.GeneticLocation;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 * The SimulatedAnnealingDataUtils is class with utility methods for the data used in the
 * <i>SimulatedAnnealing</i> algorithm.
 *
 * @author Timothy Jones
 *
 * @date 2018-05-21
 */
public class EugeneTargetDataUtils {

  /**
   * Get the circuit rules contained in the target data.
   * 
   * @param td The {@link TargetData}.
   * @return The circuit rules contained in the target data.
   */
  public static CircuitRules getCircuitRules(final TargetData td) {
    CircuitRules rtn = null;
    final JSONObject jObj = td.getJsonObjectAtIdx(EugeneTargetDataUtils.S_CIRCUITRULES, 0);
    rtn = new CircuitRules(jObj);
    return rtn;
  }

  /**
   * Get the device rules contained in the target data.
   * 
   * @param td The {@link TargetData}.
   * @return The device rules contained in the target data.
   */
  public static DeviceRules getDeviceRules(final TargetData td) {
    DeviceRules rtn = null;
    final JSONObject jObj = td.getJsonObjectAtIdx(EugeneTargetDataUtils.S_DEVICERULES, 0);
    rtn = new DeviceRules(jObj);
    return rtn;
  }

  /**
   * Get the genetic locations objects in the target data.
   * 
   * @param td The target data.
   * @return The genetic locations objects in the target data.
   */
  public static CObjectCollection<GeneticLocation> getGeneticLocations(final TargetData td) {
    final CObjectCollection<GeneticLocation> rtn = new CObjectCollection<>();
    final JSONObject obj = td.getJsonObjectAtIdx(EugeneTargetDataUtils.S_GENETICLOCATIONS, 0);
    final JSONArray locations = (JSONArray) obj.get("locations");
    for (int i = 0; i < locations.size(); i++) {
      final JSONObject location = (JSONObject) locations.get(i);
      final GeneticLocation l = new GeneticLocation(location);
      rtn.add(l);
    }
    return rtn;
  }

  private static String S_CIRCUITRULES = "circuit_rules";
  private static String S_DEVICERULES = "device_rules";
  private static String S_GENETICLOCATIONS = "genetic_locations";

}

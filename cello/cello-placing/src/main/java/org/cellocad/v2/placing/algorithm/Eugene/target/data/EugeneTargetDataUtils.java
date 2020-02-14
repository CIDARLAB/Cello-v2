/**
 * Copyright (C) 2018 Boston University (BU)
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:

 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.

 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package org.cellocad.v2.placing.algorithm.Eugene.target.data;

import org.cellocad.v2.common.CObjectCollection;
import org.cellocad.v2.common.target.data.TargetData;
import org.cellocad.v2.common.target.data.data.GeneticLocation;
import org.cellocad.v2.common.target.data.placing.CircuitRules;
import org.cellocad.v2.common.target.data.placing.DeviceRules;
import org.json.simple.JSONObject;

/**
 * The SimulatedAnnealingDataUtils is class with utility methods for the data used in the <i>SimulatedAnnealing</i> algorithm.
 * 
 * @author Timothy Jones
 * 
 * @date 2018-05-21
 *
 */
public class EugeneTargetDataUtils {
	
	static public CircuitRules getCircuitRules(final TargetData td){
		CircuitRules rtn = null;
		JSONObject jObj = td.getJSONObjectAtIdx(EugeneTargetDataUtils.S_CIRCUITRULES, 0);
		rtn = new CircuitRules(jObj);
		return rtn;
	}
	
	/**
	 * @param targetData
	 * @return
	 */
	public static DeviceRules getDeviceRules(TargetData td) {
		DeviceRules rtn = null;
		JSONObject jObj = td.getJSONObjectAtIdx(EugeneTargetDataUtils.S_DEVICERULES, 0);
		rtn = new DeviceRules(jObj);
		return rtn;
	}
	
	static public CObjectCollection<GeneticLocation> getGeneticLocations(final TargetData td) {
		CObjectCollection<GeneticLocation> rtn = new CObjectCollection<>();
		for (int i = 0; i < td.getNumJSONObject(S_GENETICLOCATIONS); i++) {
			JSONObject jObj = td.getJSONObjectAtIdx(S_GENETICLOCATIONS, i);
			GeneticLocation l = new GeneticLocation(jObj);
			rtn.add(l);
		}
		return rtn;
	}

	private static String S_CIRCUITRULES = "circuit_rules";
	private static String S_DEVICERULES = "device_rules";
	private static String S_GENETICLOCATIONS = "genetic_locations";

}

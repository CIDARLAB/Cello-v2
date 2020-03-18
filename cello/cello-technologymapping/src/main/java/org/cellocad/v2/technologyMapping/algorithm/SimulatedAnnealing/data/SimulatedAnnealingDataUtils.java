/**
 * Copyright (C) 2018 Massachusetts Institute of Technology (MIT), Boston University (BU)
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
package org.cellocad.v2.technologyMapping.algorithm.SimulatedAnnealing.data;

import org.cellocad.v2.common.target.data.TargetData;
import org.cellocad.v2.common.target.data.data.LocationSequences;
import org.json.simple.JSONObject;

/**
 * The SimulatedAnnealingDataUtils is class with utility methods for the data
 * used in the <i>SimulatedAnnealing</i> algorithm.
 * 
 * @author Vincent Mirian
 * @author Timothy Jones
 * 
 * @date 2018-05-21
 *
 */
public class SimulatedAnnealingDataUtils {

	static public LocationSequences getLocationSequences(TargetData td) {
		LocationSequences rtn = null;
		JSONObject jObj = td.getJSONObjectAtIdx(SimulatedAnnealingDataUtils.S_LOCATIONSEQUENCES, 0);
		rtn = new LocationSequences(jObj);
		return rtn;
	}

	static public Double getUnitConversion(TargetData td) {
		Double rtn = null;
		try {
			LocationSequences locations = getLocationSequences(td);
			rtn = locations.getOutputModuleLocations().get(0).getUnitConversion();
		} catch (Exception e) {
			rtn = 1.0;
		}
		return rtn;
	}

	private static String S_LOCATIONSEQUENCES = "location_sequences";
}

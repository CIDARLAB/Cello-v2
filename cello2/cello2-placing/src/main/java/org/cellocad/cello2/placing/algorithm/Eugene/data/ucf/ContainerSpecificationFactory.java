/**
 * Copyright (C) 2019 Boston University (BU)
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
package org.cellocad.cello2.placing.algorithm.Eugene.data.ucf;

import java.util.Arrays;
import java.util.Collection;

import org.cellocad.cello2.common.profile.ProfileUtils;
import org.cellocad.cello2.results.logicSynthesis.LSResultsUtils;
import org.json.simple.JSONObject;

/**
 * 
 *
 * @author Timothy Jones
 *
 * @date 2019-07-22
 *
 */
public class ContainerSpecificationFactory {

	public ContainerSpecification getContainerSpecification(JSONObject jObj) {
		ContainerSpecification rtn = null;
		String name = ProfileUtils.getString(jObj, "name");
		String type = ProfileUtils.getString(jObj, "type");
		String gateType = ProfileUtils.getString(jObj, "gate_type");
		Collection<String> gateTypes = Arrays.asList(gateType.split(","));
		for (String str : gateTypes) {
			if ( !LSResultsUtils.isValidNodeTypes(str) ) {
				throw new RuntimeException("Unrecognized node type in container specification.");
			}
		}
		Double copyNumber = 1.0;
		Double temp = ProfileUtils.getDouble(jObj, "copy_number");
		if (temp != null)
			copyNumber = temp;
		if (type.equals("landing_pad")) {
			rtn = new LandingPadSpecification(name, gateTypes, copyNumber);
		}
		if (type.equals("plasmid")) {
			String backbone = ProfileUtils.getString(jObj, "backbone");
			rtn = new PlasmidSpecification(name, gateTypes, copyNumber, backbone);
		}
		return rtn;
	}
}

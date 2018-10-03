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
package technologyMapping.algorithm.SimulatedAnnealing.data.ucf;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import common.CObject;
import common.CObjectCollection;

/**
 * 
 *
 * @author Timothy Jones
 *
 * @date 2018-08-06
 *
 */
public class GeneticLocations extends CObject {
	
	private void parseOutputModuleLocation(final JSONObject jObj) {
		JSONArray jArr = (JSONArray) jObj.get("output_module_location");
		for (int i = 0; i < jArr.size(); i++) {
			JSONObject obj = (JSONObject) jArr.get(i);
			OutputModuleLocation location = new OutputModuleLocation(obj);
			this.getOutputModuleLocations().add(location);
		}
	}
	
	private void init() {
		this.oml = new CObjectCollection<OutputModuleLocation>();
	}
	
	public GeneticLocations(final JSONObject jobj) {
		this.init();
		this.parseOutputModuleLocation(jobj);
	}
	
	/**
	 * @return the oml
	 */
	public CObjectCollection<OutputModuleLocation> getOutputModuleLocations() {
		return oml;
	}

	private CObjectCollection<OutputModuleLocation> oml;

}

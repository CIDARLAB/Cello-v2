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
package org.cellocad.v2.export.algorithm.SBOL.data.ucf;

import org.cellocad.v2.common.CObjectCollection;
import org.cellocad.v2.common.profile.ProfileUtils;
import org.cellocad.v2.export.algorithm.SBOL.data.Device;
import org.json.simple.JSONObject;

/**
 * The OutputReporter is class representing an output reporter for the gate
 * assignment in the <i>SimulatedAnnealing</i> algorithm.
 *
 * @author Timothy Jones
 *
 * @date 2018-06-29
 *
 */
public class OutputDevice extends Device {

	private void init() {
	}

	private void parseName(final JSONObject JObj) {
		String value = ProfileUtils.getString(JObj, "name");
		this.setName(value);
	}

	private void parseUri(final JSONObject JObj) {
		String value = ProfileUtils.getString(JObj, "uri");
		this.setUri(value);
	}

	private void parseOutputReporter(final JSONObject jObj, CObjectCollection<Part> parts) {
		this.parseName(jObj);
		this.parseUri(jObj);
	}

	public OutputDevice(final JSONObject jObj, CObjectCollection<Part> parts) {
		this.init();
		this.parseOutputReporter(jObj, parts);
	}

	@Override
	public boolean isValid() {
		boolean rtn = super.isValid();
		rtn = rtn && (this.getName() != null);
		return rtn;
	}

}

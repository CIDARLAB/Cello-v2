/**
 * Copyright (C) 2020 Boston University (BU)
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
package org.cellocad.v2.common.target.data.model;

import org.cellocad.v2.common.CObject;
import org.cellocad.v2.common.profile.ProfileUtils;
import org.cellocad.v2.common.target.data.component.AssignableDevice;
import org.json.simple.JSONObject;

/**
 *
 *
 * @author Timothy Jones
 *
 * @date 2020-02-11
 *
 */
public class Input extends CObject {

	private void init() {
	}

	private void parseName(final JSONObject jObj) {
		String value = ProfileUtils.getString(jObj, S_NAME);
		this.setName(value);
	}

	private void parsePartType(final JSONObject jObj) {
		String value = ProfileUtils.getString(jObj, S_PARTTYPE);
		this.partType = value;
	}

	private void parseInput(final JSONObject JObj) {
		this.parseName(JObj);
		this.parsePartType(JObj);
	}

	public Input(final JSONObject jObj) {
		this.init();
		this.parseInput(jObj);
	}

	@Override
	public boolean isValid() {
		boolean rtn = super.isValid();
		rtn = rtn && (this.getName() != null);
		return rtn;
	}

	/**
	 * Getter for <i>partType</i>.
	 *
	 * @return value of partType
	 */
	public String getPartType() {
		return partType;
	}

	private String partType;

	/**
	 * Getter for <i>device</i>.
	 *
	 * @return value of device
	 */
	public AssignableDevice getDevice() {
		return device;
	}

	/**
	 * Setter for <i>device</i>.
	 *
	 * @param device the device to set
	 */
	public void setDevice(AssignableDevice device) {
		this.device = device;
	}

	private AssignableDevice device;

	private static final String S_NAME = "name";
	private static final String S_PARTTYPE = "part_type";

}

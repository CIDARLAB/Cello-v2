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
package org.cellocad.v2.common.target.data.data;

import org.cellocad.v2.common.profile.ProfileUtils;
import org.json.simple.JSONObject;

/**
 * 
 *
 * @author Timothy Jones
 *
 * @date 2018-05-22
 *
 */
public class MeasurementStandard {
	

	private void parseSignalCarriertUnits(final JSONObject jobj) {
		String value = ProfileUtils.getString(jobj, S_SIGNALCARRIERUNITS);
		this.setSignalCarrierUnits(value);
	}
	
	private void init() {
		this.signalCarrierUnits = ""; 
	}
	
	public MeasurementStandard(final JSONObject jobj) {
		this.init();
		this.parseSignalCarriertUnits(jobj);
	}

	private final void setSignalCarrierUnits(String signalCarrierUnits) {
		this.signalCarrierUnits = signalCarrierUnits;
	}

	public String getSignalCarrierUnits() {
		return signalCarrierUnits;
	}
	
	private String signalCarrierUnits;

	private static final String S_SIGNALCARRIERUNITS = "signal_carrier_units";

}

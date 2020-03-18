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

import org.cellocad.v2.common.CObject;
import org.cellocad.v2.common.profile.ProfileUtils;
import org.json.simple.JSONObject;

/**
 * 
 *
 * @author Timothy Jones
 *
 * @date 2018-08-06
 *
 */
public class OutputModuleLocation extends CObject {

	private void parseLocationName(final JSONObject jobj) {
		String value = ProfileUtils.getString(jobj, S_LOCATIONNAME);
		this.setLocationName(value);
	}
	
	private void parseStart(final JSONObject jobj) {
		Integer value = ProfileUtils.getInteger(jobj, S_BPSTART);
		this.setStart(value);
	}
	
	private void parseEnd(final JSONObject jobj) {
		Integer value = ProfileUtils.getInteger(jobj, S_BPEND);
		this.setEnd(value);
	}
	
	private void parseUnitConversion(final JSONObject jobj) {
		Double value = ProfileUtils.getDouble(jobj, S_UNITCONVERSION);
		this.setUnitConversion(value);
	}
	
	private void init() {
		this.setLocationName(""); 
		this.setStart(0);
		this.setEnd(0);
		this.setUnitConversion(0.0);
	}
	
	public OutputModuleLocation(final JSONObject jobj) {
		this.init();
		this.parseLocationName(jobj);
		this.parseStart(jobj);
		this.parseEnd(jobj);
		this.parseUnitConversion(jobj);
	}

	/**
	 * @param locationName the locationName to set
	 */
	private void setLocationName(final String locationName) {
		this.locationName = locationName;
	}
	
	/**
	 * @return the locationName
	 */
	public String getLocationName() {
		return locationName;
	}

	private String locationName;

	/**
	 * @param start the start to set
	 */
	private void setStart(final Integer start) {
		this.start = start;
	}
	
	/**
	 * @return the start
	 */
	public Integer getStart() {
		return start;
	}
	
	private Integer start;

	/**
	 * @param end the end to set
	 */
	private void setEnd(final Integer end) {
		this.end = end;
	}
	
	/**
	 * @return the end
	 */
	public Integer getEnd() {
		return end;
	}

	private Integer end;
	
	/**
	 * @param unitConversion the unitConversion to set
	 */
	private void setUnitConversion(final Double unitConversion) {
		this.unitConversion = unitConversion;
	}
	
	/**
	 * @return the unitConversion
	 */
	public Double getUnitConversion() {
		return unitConversion;
	}
	
	private Double unitConversion;

	private static final String S_LOCATIONNAME = "location_name";
	private static final String S_BPSTART = "bp_start";
	private static final String S_BPEND = "bp_end";
	private static final String S_UNITCONVERSION = "unit_conversion";

}

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
package org.cellocad.v2.placing.algorithm.Eugene.data.ucf;

import org.cellocad.v2.common.CObject;
import org.cellocad.v2.common.profile.ProfileUtils;
import org.json.simple.JSONObject;

/**
 *
 *
 * @author Timothy Jones
 *
 * @date 2020-01-13
 *
 */
public class GeneticLocation extends CObject {

	private void init() {
	}

	private void parseSymbol(JSONObject jObj) {
		String str = ProfileUtils.getString(jObj, "symbol");
		this.setName(str);
	}

	private void parseLocus(JSONObject jObj) {
		Integer z = ProfileUtils.getInteger(jObj, "locus");
		this.locus = z;
	}

	private void parseSequence(JSONObject jObj) {
		String str = ProfileUtils.getString(jObj, "sequence");
		this.sequence = str;
	}

	private void parseLocation(JSONObject jObj) {
		this.parseSymbol(jObj);
		this.parseLocus(jObj);
		this.parseSequence(jObj);
	}

	public GeneticLocation(JSONObject jObj) {
		this.init();
		this.parseLocation(jObj);
	}
	
	/**
	 * Getter for <i>locus</i>
	 *
	 * @return value of <i>locus</i>
	 */
	public Integer getLocus() {
		return locus;
	}

	/**
	 * Getter for <i>sequence</i>
	 *
	 * @return value of <i>sequence</i>
	 */
	public String getSequence() {
		return sequence;
	}

	private Integer locus;
	private String sequence;

}

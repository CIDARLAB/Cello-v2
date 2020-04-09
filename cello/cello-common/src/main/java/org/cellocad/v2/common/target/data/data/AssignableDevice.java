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

import org.json.simple.JSONObject;

/**
 * 
 *
 * @author Timothy Jones
 *
 * @date 2018-06-29
 *
 */
public abstract class AssignableDevice extends DNAComponent {

	public AssignableDevice(final JSONObject jObj) {
		super(jObj);
	}

	@Override
	public boolean isValid() {
		boolean rtn = super.isValid();
		rtn = rtn && (this.getModel() != null);
		rtn = rtn && (this.getStructure() != null);
		return rtn;
	}

	/**
	 * Getter for <i>model</i>.
	 *
	 * @return value of model
	 */
	public Model getModel() {
		return model;
	}

	/**
	 * Setter for <i>model</i>.
	 *
	 * @param model the model to set
	 */
	public void setModel(final Model model) {
		this.model = model;
	}

	private Model model;

	/**
	 * Getter for <i>structure</i>.
	 *
	 * @return value of structure
	 */
	public Structure getStructure() {
		return structure;
	}

	/**
	 * Setter for <i>structure</i>.
	 *
	 * @param structure the structure to set
	 */
	public void setStructure(Structure structure) {
		this.structure = structure;
	}

	private Structure structure;

	public static final String S_MODEL = "model";
	public static final String S_STRUCTURE = "structure";

}

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
package org.cellocad.v2.common.target.data.component;

import org.cellocad.v2.common.target.data.model.OutputDeviceModel;
import org.cellocad.v2.common.target.data.structure.OutputDeviceStructure;
import org.json.simple.JSONObject;

/**
 * 
 *
 * @author Timothy Jones
 *
 * @date 2018-06-29
 *
 */
public class OutputDevice extends AssignableDevice {
	
	private void init() {
	}
	
	private void parseOutputDevice(final JSONObject jObj) {
	}
	
	public OutputDevice(final JSONObject jObj) {
		super(jObj);
		this.init();
		this.parseOutputDevice(jObj);
	}
	
	/**
	 * Getter for <i>outputDeviceModel</i>.
	 *
	 * @return value of outputDeviceModel
	 */
	public OutputDeviceModel getOutputDeviceModel() {
		return outputDeviceModel;
	}

	/**
	 * Setter for <i>outputDeviceModel</i>.
	 *
	 * @param outputDeviceModel the outputDeviceModel to set
	 */
	public void setOutputDeviceModel(OutputDeviceModel outputDeviceModel) {
		this.outputDeviceModel = outputDeviceModel;
	}

	private OutputDeviceModel outputDeviceModel;

	/**
	 * Getter for <i>outputDeviceStructure</i>.
	 *
	 * @return value of <i>outputDeviceStructure</i>
	 */
	public OutputDeviceStructure getOutputDeviceStructure() {
		return outputDeviceStructure;
	}

	/**
	 * Setter for <i>outputDeviceStructure</i>.
	 *
	 * @param outputDeviceStructure the value to set <i>outputDeviceStructure</i>
	 */
	public void setOutputDeviceStructure(final OutputDeviceStructure outputDeviceStructure) {
		this.outputDeviceStructure = outputDeviceStructure;
	}

	private OutputDeviceStructure outputDeviceStructure;
	
}

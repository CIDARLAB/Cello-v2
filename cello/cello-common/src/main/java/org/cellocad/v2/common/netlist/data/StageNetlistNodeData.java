/**
 * Copyright (C) 2017-2020
 * Massachusetts Institute of Technology (MIT)
 * Boston University (BU)
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
package org.cellocad.v2.common.netlist.data;

import java.io.IOException;
import java.io.Writer;

import org.cellocad.v2.common.CObject;
import org.cellocad.v2.common.target.data.component.AssignableDevice;

/**
 * The StageNetlistNodeData class is the base class for all StageNetlistNodeData
 * classes using the Poros framework.
 * 
 * @author Vincent Mirian
 * @author Timothy Jones
 * 
 * @date Dec 10, 2017
 *
 */
public abstract class StageNetlistNodeData extends CObject {

	/**
	 *  Writes this instance in JSON format to the writer defined by parameter <i>os</i> with the number of indents equivalent to the parameter <i>indent</i>
	 *  @param indent the number of indents
	 *  @param os the writer
	 *  @throws IOException If an I/O error occurs
	 */
	public abstract void writeJSON(int indent, Writer os) throws IOException;

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
	public void setDevice(final AssignableDevice device) {
		this.device = device;
	}

	private AssignableDevice device;

}

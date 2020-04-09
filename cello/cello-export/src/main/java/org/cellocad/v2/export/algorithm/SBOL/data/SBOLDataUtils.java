/**
 * Copyright (C) 2017-2019
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
package org.cellocad.v2.export.algorithm.SBOL.data;

import org.cellocad.v2.common.target.data.data.DNAComponent;
import org.cellocad.v2.common.target.data.data.Gate;
import org.cellocad.v2.common.target.data.data.InputSensor;
import org.cellocad.v2.common.target.data.data.OutputDevice;
import org.cellocad.v2.common.target.data.data.Part;

/**
 * The SimulatedAnnealingDataUtils is class with utility methods for the data
 * used in the <i>SimulatedAnnealing</i> algorithm.
 *
 * @author Vincent Mirian
 *
 * @date 2018-05-21
 *
 */
public class SBOLDataUtils {

	static public String getDNASequence(final DNAComponent component) {
		String rtn = "";
		if (component instanceof Part) {
			Part part = (Part) component;
			rtn = part.getDNASequence();
		}
		if (component instanceof Gate) {
			Gate gate = (Gate) component;
			rtn = getDNASequence(gate);
		}
		if (component instanceof InputSensor) {
			InputSensor sensor = (InputSensor) component;
			rtn = getDNASequence(sensor);
		}
		if (component instanceof OutputDevice) {
			OutputDevice reporter = (OutputDevice) component;
			rtn = getDNASequence(reporter);
		}
		return rtn;
	}

}

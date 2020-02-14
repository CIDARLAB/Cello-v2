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
package org.cellocad.v2.common.target.data;

import org.cellocad.v2.common.CObjectCollection;
import org.cellocad.v2.common.target.data.component.Gate;
import org.cellocad.v2.common.target.data.component.InputSensor;
import org.cellocad.v2.common.target.data.component.OutputDevice;
import org.cellocad.v2.common.target.data.model.Function;
import org.cellocad.v2.common.target.data.model.Model;
import org.cellocad.v2.common.target.data.model.Structure;

/**
 *
 *
 * @author Timothy Jones
 *
 * @date 2020-02-14
 *
 */
public class TargetDataInstance {

	public TargetDataInstance(final TargetData td) {
		CObjectCollection<Function> functions = TargetDataUtils.getFunctions(td);
		CObjectCollection<Model> models = TargetDataUtils.getModels(td, functions);
		CObjectCollection<Structure> structures = TargetDataUtils.getStructures(td);
		this.gates = TargetDataUtils.getGates(td, models, structures);
		this.inputSensors = TargetDataUtils.getInputSensors(td, models, structures);
		this.outputDevices = TargetDataUtils.getOutputDevices(td, models, structures);
	}

	/**
	 * Getter for <i>gates</i>.
	 *
	 * @return value of gates
	 */
	public CObjectCollection<Gate> getGates() {
		return gates;
	}

	private CObjectCollection<Gate> gates;

	/**
	 * Getter for <i>inputSensors</i>.
	 *
	 * @return value of inputSensors
	 */
	public CObjectCollection<InputSensor> getInputSensors() {
		return inputSensors;
	}

	public void setInputSensors(CObjectCollection<InputSensor> inputSensors) {
		this.inputSensors = inputSensors;
	}

	private CObjectCollection<InputSensor> inputSensors;

	/**
	 * Getter for <i>outputDevices</i>.
	 *
	 * @return value of outputDevices
	 */
	public CObjectCollection<OutputDevice> getOutputDevices() {
		return outputDevices;
	}

	private CObjectCollection<OutputDevice> outputDevices;

}

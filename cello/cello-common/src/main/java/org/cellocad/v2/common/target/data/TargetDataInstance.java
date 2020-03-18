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

import org.cellocad.v2.common.CObject;
import org.cellocad.v2.common.CObjectCollection;
import org.cellocad.v2.common.CelloException;
import org.cellocad.v2.common.target.data.component.AssignableDevice;
import org.cellocad.v2.common.target.data.component.Gate;
import org.cellocad.v2.common.target.data.component.InputSensor;
import org.cellocad.v2.common.target.data.component.OutputDevice;
import org.cellocad.v2.common.target.data.component.Part;
import org.cellocad.v2.common.target.data.model.Function;
import org.cellocad.v2.common.target.data.model.Model;
import org.cellocad.v2.common.target.data.model.Structure;

/**
 * The {@code TargetDataInstance} class is a represetation of the target data in
 * which all objects have been instantiated and linked, e.g. each gate is
 * accessible as a {@link Gate} object and the model to which the gate refers is
 * accessible as a {@link Model} object.
 *
 * @author Timothy Jones
 *
 * @date 2020-02-14
 *
 */
public class TargetDataInstance extends CObject {

	public TargetDataInstance(final TargetData td) throws CelloException {
		CObjectCollection<Function> functions = TargetDataUtils.getFunctions(td);
		CObjectCollection<Model> models = TargetDataUtils.getModels(td, functions);
		CObjectCollection<Structure> structures = TargetDataUtils.getStructures(td);
		this.parts = TargetDataUtils.getParts(td);
		this.gates = TargetDataUtils.getGates(td, models, structures);
		this.inputSensors = TargetDataUtils.getInputSensors(td, models, structures);
		this.outputDevices = TargetDataUtils.getOutputDevices(td, models, structures);
	}

	@Override
	public boolean isValid() {
		boolean rtn = super.isValid();
		rtn = rtn && (this.getParts() != null && this.getParts().isValid());
		rtn = rtn && (this.getGates() != null && this.getGates().isValid());
		rtn = rtn && (this.getInputSensors() != null && this.getInputSensors().isValid());
		rtn = rtn && (this.getOutputDevices() != null && this.getOutputDevices().isValid());
		return rtn;
	}

	public AssignableDevice getAssignableDeviceByName(final String name) {
		Gate g = this.getGates().findCObjectByName(name);
		if (g != null)
			return g;
		InputSensor s = this.getInputSensors().findCObjectByName(name);
		if (s != null)
			return s;
		OutputDevice o = this.getOutputDevices().findCObjectByName(name);
		if (o != null)
			return o;
		return null;
	}

	/**
	 * Getter for <i>parts</i>.
	 *
	 * @return value of parts
	 */
	public CObjectCollection<Part> getParts() {
		return parts;
	}

	private CObjectCollection<Part> parts;

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

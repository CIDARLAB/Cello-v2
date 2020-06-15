/*
 * Copyright (C) 2020 Boston University (BU)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package org.cellocad.v2.common.target.data;

import org.cellocad.v2.common.CObject;
import org.cellocad.v2.common.CObjectCollection;
import org.cellocad.v2.common.exception.CelloException;
import org.cellocad.v2.common.target.data.data.AssignableDevice;
import org.cellocad.v2.common.target.data.data.Function;
import org.cellocad.v2.common.target.data.data.Gate;
import org.cellocad.v2.common.target.data.data.GeneticLocation;
import org.cellocad.v2.common.target.data.data.InputSensor;
import org.cellocad.v2.common.target.data.data.LogicConstraints;
import org.cellocad.v2.common.target.data.data.Model;
import org.cellocad.v2.common.target.data.data.OutputDevice;
import org.cellocad.v2.common.target.data.data.Part;
import org.cellocad.v2.common.target.data.data.Structure;

/**
 * A representation of the target data in which all objects have been instantiated and linked, e.g.
 * each gate is accessible as a {@link Gate} object and the model to which the gate refers is
 * accessible as a {@link Model} object. Compare with {@link TargetData} in which all library
 * objects are parsed only at the JSON level.
 *
 * @author Timothy Jones
 * @date 2020-02-14
 */
public class TargetDataInstance extends CObject {

  private final LogicConstraints logicConstraints;
  private final CObjectCollection<Part> parts;
  private final CObjectCollection<Gate> gates;
  private final CObjectCollection<InputSensor> inputSensors;
  private final CObjectCollection<OutputDevice> outputDevices;
  private final CObjectCollection<GeneticLocation> geneticLocations;

  /**
   * Initializes a target data instance with target data.
   *
   * @param td Target data.
   * @throws CelloException Unable to instantiate target data instance.
   */
  public TargetDataInstance(final TargetData td) throws CelloException {
    logicConstraints = TargetDataUtils.getLogicConstraints(td);
    final CObjectCollection<Function> functions = TargetDataUtils.getFunctions(td);
    final CObjectCollection<Model> models = TargetDataUtils.getModels(td, functions);
    final CObjectCollection<Structure> structures = TargetDataUtils.getStructures(td);
    parts = TargetDataUtils.getParts(td);
    gates = TargetDataUtils.getGates(td, models, structures);
    inputSensors = TargetDataUtils.getInputSensors(td, models, structures);
    outputDevices = TargetDataUtils.getOutputDevices(td, models, structures);
    geneticLocations = TargetDataUtils.getGeneticLocations(td);
  }

  @Override
  public boolean isValid() {
    boolean rtn = super.isValid();
    rtn = rtn && getLogicConstraints() != null && getLogicConstraints().isValid();
    rtn = rtn && getParts() != null && getParts().isValid();
    rtn = rtn && getGates() != null && getGates().isValid();
    rtn = rtn && getInputSensors() != null && getInputSensors().isValid();
    rtn = rtn && getOutputDevices() != null && getOutputDevices().isValid();
    return rtn;
  }

  /**
   * Get an assignable device by name.
   *
   * @param name The name of the device.
   * @return The assignable device, if it exists, otherwise null.
   */
  public AssignableDevice getAssignableDeviceByName(final String name) {
    final Gate g = getGates().findCObjectByName(name);
    if (g != null) {
      return g;
    }
    final InputSensor s = getInputSensors().findCObjectByName(name);
    if (s != null) {
      return s;
    }
    final OutputDevice o = getOutputDevices().findCObjectByName(name);
    if (o != null) {
      return o;
    }
    return null;
  }

  /**
   * Getter for {@code logicConstraints}.
   *
   * @return The value of {@code logicConstraints}.
   */
  public LogicConstraints getLogicConstraints() {
    return logicConstraints;
  }

  /**
   * Getter for {@code parts}.
   *
   * @return The value of {@code parts}.
   */
  public CObjectCollection<Part> getParts() {
    return parts;
  }

  /**
   * Getter for {@code gates}.
   *
   * @return The value of {@code gates}.
   */
  public CObjectCollection<Gate> getGates() {
    return gates;
  }

  /**
   * Getter for {@code inputSensors}.
   *
   * @return The value of {@code inputSensors}.
   */
  public CObjectCollection<InputSensor> getInputSensors() {
    return inputSensors;
  }

  /**
   * Getter for {@code outputDevices}.
   *
   * @return The value of {@code outputDevices}.
   */
  public CObjectCollection<OutputDevice> getOutputDevices() {
    return outputDevices;
  }

  /**
   * Getter for {@code geneticLocations}.
   *
   * @return The value of {@code geneticLocations}.
   */
  public CObjectCollection<GeneticLocation> getGeneticLocations() {
    return geneticLocations;
  }
}

/*
 * Copyright (C) 2020 Massachusetts Institute of Technology (MIT), Boston University (BU)
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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Iterator;
import org.cellocad.v2.common.CObjectCollection;
import org.cellocad.v2.common.CelloException;
import org.cellocad.v2.common.Utils;
import org.cellocad.v2.common.profile.ProfileUtils;
import org.cellocad.v2.common.runtime.environment.RuntimeEnv;
import org.cellocad.v2.common.target.data.data.AnalyticFunction;
import org.cellocad.v2.common.target.data.data.AssignableDevice;
import org.cellocad.v2.common.target.data.data.BivariateLookupTableFunction;
import org.cellocad.v2.common.target.data.data.Function;
import org.cellocad.v2.common.target.data.data.Gate;
import org.cellocad.v2.common.target.data.data.InputSensor;
import org.cellocad.v2.common.target.data.data.LogicConstraints;
import org.cellocad.v2.common.target.data.data.LookupTableFunction;
import org.cellocad.v2.common.target.data.data.Model;
import org.cellocad.v2.common.target.data.data.OutputDevice;
import org.cellocad.v2.common.target.data.data.Part;
import org.cellocad.v2.common.target.data.data.Structure;
import org.cellocad.v2.common.target.data.data.UnivariateLookupTableFunction;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * The TargetDataUtils class is class with utility methods for <i>TargetData</i> instances.
 *
 * @author Vincent Mirian
 * @author Timothy Jones
 *
 * @date Nov 22, 2017
 */
public class TargetDataUtils {

  private static final String S_PARTS = "parts";
  private static final String S_FUNCTIONS = "functions";
  private static final String S_MODELS = "models";
  private static final String S_STRUCTURES = "structures";
  private static final String S_GATES = "gates";
  private static final String S_INPUTSENSORS = "input_sensors";
  private static final String S_OUTPUTDEVICES = "output_devices";
  private static final String S_LOGIC_CONSTRAINTS = "logic_constraints";

  /**
   * Get all the parts from the target data.
   * 
   * @param td The target data.
   * @return The parts.
   */
  public static final CObjectCollection<Part> getParts(final TargetData td) {
    final CObjectCollection<Part> rtn = new CObjectCollection<>();
    for (int i = 0; i < td.getNumJsonObject(TargetDataUtils.S_PARTS); i++) {
      final JSONObject jObj = td.getJsonObjectAtIdx(TargetDataUtils.S_PARTS, i);
      final Part part = new Part(jObj);
      rtn.add(part);
    }
    return rtn;
  }

  /**
   * Get all the functions from the target data.
   * 
   * @param td The target data.
   * @return The functions.
   * @throws CelloException Unable to get functions.
   */
  public static final CObjectCollection<Function> getFunctions(final TargetData td)
      throws CelloException {
    final CObjectCollection<Function> rtn = new CObjectCollection<>();
    for (int i = 0; i < td.getNumJsonObject(TargetDataUtils.S_FUNCTIONS); i++) {
      final JSONObject jObj = td.getJsonObjectAtIdx(TargetDataUtils.S_FUNCTIONS, i);
      Function function;
      if (jObj.containsKey(AnalyticFunction.S_EQUATION)) {
        function = new AnalyticFunction(jObj);
      } else if (jObj.containsKey(LookupTableFunction.S_TABLE)) {
        final JSONArray variables = (JSONArray) jObj.get(Function.S_VARIABLES);
        if (variables.size() == 1) {
          function = new UnivariateLookupTableFunction(jObj);
        } else if (variables.size() == 2) {
          function = new BivariateLookupTableFunction(jObj);
        } else {
          final String fmt = "Bad variable count: %s.";
          throw new CelloException(String.format(fmt, jObj.toString()));
        }
      } else {
        final String fmt = "Invalid %s specification: %s";
        throw new CelloException(String.format(fmt, Function.class.getName(), jObj.toString()));
      }
      rtn.add(function);
    }
    return rtn;
  }

  private static void attachFunctions(final JSONObject jObj, final Model model,
      final CObjectCollection<Function> functions) {
    final JSONObject obj = (JSONObject) jObj.get(Model.S_FUNCTIONS);
    final Iterator<?> it = obj.keySet().iterator();
    while (it.hasNext()) {
      final String name = (String) it.next();
      final String value = ProfileUtils.getString(obj, name);
      final Function function = functions.findCObjectByName(value);
      model.addFunction(name, function);
    }
  }

  /**
   * Get all the models from the target data.
   * 
   * @param td        The target data.
   * @param functions The functions used in the models.
   * @return The models.
   */
  public static final CObjectCollection<Model> getModels(final TargetData td,
      final CObjectCollection<Function> functions) {
    final CObjectCollection<Model> rtn = new CObjectCollection<>();
    for (int i = 0; i < td.getNumJsonObject(TargetDataUtils.S_MODELS); i++) {
      final JSONObject jObj = td.getJsonObjectAtIdx(TargetDataUtils.S_MODELS, i);
      final Model model = new Model(jObj);
      TargetDataUtils.attachFunctions(jObj, model, functions);
      rtn.add(model);
    }
    return rtn;
  }

  /**
   * Get all the structures from the target data.
   * 
   * @param td The target data.
   * @return The structures.
   * @throws CelloException Unable to load the structures.
   */
  public static final CObjectCollection<Structure> getStructures(final TargetData td)
      throws CelloException {
    final CObjectCollection<Structure> rtn = new CObjectCollection<>();
    for (int i = 0; i < td.getNumJsonObject(TargetDataUtils.S_STRUCTURES); i++) {
      final JSONObject jObj = td.getJsonObjectAtIdx(TargetDataUtils.S_STRUCTURES, i);
      final Structure structure = new Structure(jObj);
      rtn.add(structure);
    }
    return rtn;
  }

  private static void attachModel(final JSONObject jObj, final AssignableDevice d,
      final CObjectCollection<Model> models) {
    final String name = ProfileUtils.getString(jObj, AssignableDevice.S_MODEL);
    final Model model = models.findCObjectByName(name);
    d.setModel(model);
  }

  private static void attachStructure(final JSONObject jObj, final AssignableDevice d,
      final CObjectCollection<Structure> structures) {
    final String name = ProfileUtils.getString(jObj, AssignableDevice.S_STRUCTURE);
    final Structure structure = structures.findCObjectByName(name);
    d.setStructure(structure);
  }

  /**
   * Get all the gates from the target data.
   * 
   * @param td         The target data.
   * @param models     The models used in the gates.
   * @param structures The structures used in the gates.
   * @return The gates.
   */
  public static final CObjectCollection<Gate> getGates(final TargetData td,
      final CObjectCollection<Model> models, final CObjectCollection<Structure> structures) {
    final CObjectCollection<Gate> rtn = new CObjectCollection<>();
    for (int i = 0; i < td.getNumJsonObject(TargetDataUtils.S_GATES); i++) {
      final JSONObject jObj = td.getJsonObjectAtIdx(TargetDataUtils.S_GATES, i);
      final Gate gate = new Gate(jObj);
      // model
      TargetDataUtils.attachModel(jObj, gate, models);
      // structure
      TargetDataUtils.attachStructure(jObj, gate, structures);
      rtn.add(gate);
    }
    return rtn;
  }

  /**
   * Get all the input sensors from the target data.
   * 
   * @param td         The target data.
   * @param models     The models used in the sensors.
   * @param structures The structures used in the sensors.
   * @return The input sensors.
   */
  public static final CObjectCollection<InputSensor> getInputSensors(final TargetData td,
      final CObjectCollection<Model> models, final CObjectCollection<Structure> structures) {
    final CObjectCollection<InputSensor> rtn = new CObjectCollection<>();
    for (int i = 0; i < td.getNumJsonObject(TargetDataUtils.S_INPUTSENSORS); i++) {
      final JSONObject jObj = td.getJsonObjectAtIdx(TargetDataUtils.S_INPUTSENSORS, i);
      final InputSensor sensor = new InputSensor(jObj);
      // model
      TargetDataUtils.attachModel(jObj, sensor, models);
      // structure
      TargetDataUtils.attachStructure(jObj, sensor, structures);
      rtn.add(sensor);
    }
    return rtn;
  }

  /**
   * Get all the output devices from the target data.
   * 
   * @param td         The target data.
   * @param models     The models used in the output devices.
   * @param structures The structures used in the output devices.
   * @return The output devices.
   */
  public static final CObjectCollection<OutputDevice> getOutputDevices(final TargetData td,
      final CObjectCollection<Model> models, final CObjectCollection<Structure> structures) {
    final CObjectCollection<OutputDevice> rtn = new CObjectCollection<>();
    for (int i = 0; i < td.getNumJsonObject(TargetDataUtils.S_OUTPUTDEVICES); i++) {
      final JSONObject jObj = td.getJsonObjectAtIdx(TargetDataUtils.S_OUTPUTDEVICES, i);
      final OutputDevice device = new OutputDevice(jObj);
      // model
      TargetDataUtils.attachModel(jObj, device, models);
      // structure
      TargetDataUtils.attachStructure(jObj, device, structures);
      rtn.add(device);
    }
    return rtn;
  }

  /**
   * Get the logic constraints section of the target data.
   *
   * @param td The target data.
   * @return The logic constraints.
   */
  public static LogicConstraints getLogicConstraints(final TargetData td) {
    LogicConstraints rtn = null;
    final JSONObject jObj = td.getJsonObjectAtIdx(TargetDataUtils.S_LOGIC_CONSTRAINTS, 0);
    rtn = new LogicConstraints(jObj);
    return rtn;
  }

  private static JSONArray getJsonArrayFromFile(final String file) {
    JSONArray rtn = null;
    // get File
    final File f = new File(file);
    Reader reader = null;
    // Create File Reader
    try {
      reader = new FileReader(f);
    } catch (final FileNotFoundException e) {
      throw new RuntimeException("Error with file: " + file);
    }
    // Create JSON object from File Reader
    final JSONParser parser = new JSONParser();
    try {
      rtn = (JSONArray) parser.parse(reader);
    } catch (final IOException e) {
      throw new RuntimeException("File IO Exception for: " + file + ".");
    } catch (final ParseException e) {
      throw new RuntimeException("Parser Exception for: " + file + ".");
    }
    try {
      reader.close();
    } catch (final IOException e) {
      throw new RuntimeException("Error with file: " + file);
    }
    return rtn;
  }

  /**
   * Initializes a newly created {@link TargetData} using the RuntimeEnv, <i>runEnv</i>, and strings
   * referencing command line arguments.
   *
   * @param runEnv                    The RuntimeEnv.
   * @param userConstraintsFileOption The string referencing command line argument for the User
   *                                  Constraints File.
   * @param inputSensorFileOption     The string referencing command line argument for the Input
   *                                  Sensor file.
   * @param outputDeviceFileOption    The string referencing command line argument for the Output
   *                                  Reporter file.
   * @return The TargetData if created successfully, otherwise null.
   */
  @SuppressWarnings("unchecked")
  public static final TargetData getTargetTargetData(final RuntimeEnv runEnv,
      final String userConstraintsFileOption, final String inputSensorFileOption,
      final String outputDeviceFileOption) {
    Utils.isNullRuntimeException(runEnv, "runEnv");
    Utils.isNullRuntimeException(userConstraintsFileOption, "userConstraintsFileOption");
    TargetData rtn = null;
    JSONArray jsonTop = null;
    // get User Constraints File
    final String userConstraintsFileName = runEnv.getOptionValue(userConstraintsFileOption);
    final JSONArray userConstraintsJson =
        TargetDataUtils.getJsonArrayFromFile(userConstraintsFileName);
    // get Input Sensor File
    final String inputSensorFileName = runEnv.getOptionValue(inputSensorFileOption);
    final JSONArray inputSensorJson = TargetDataUtils.getJsonArrayFromFile(inputSensorFileName);
    // get Output Device File
    final String outputDeviceFileName = runEnv.getOptionValue(outputDeviceFileOption);
    final JSONArray outputDeviceJson = TargetDataUtils.getJsonArrayFromFile(outputDeviceFileName);
    // combine Json
    jsonTop = new JSONArray();
    jsonTop.addAll(userConstraintsJson);
    jsonTop.addAll(inputSensorJson);
    jsonTop.addAll(outputDeviceJson);
    // Create TargetData object
    rtn = new TargetData(jsonTop);
    return rtn;
  }

}

/**
 * Copyright (C) 2020
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
import org.cellocad.v2.common.target.data.component.AssignableDevice;
import org.cellocad.v2.common.target.data.component.Gate;
import org.cellocad.v2.common.target.data.component.InputSensor;
import org.cellocad.v2.common.target.data.component.OutputDevice;
import org.cellocad.v2.common.target.data.component.Part;
import org.cellocad.v2.common.target.data.model.AnalyticFunction;
import org.cellocad.v2.common.target.data.model.BivariateLookupTableFunction;
import org.cellocad.v2.common.target.data.model.Function;
import org.cellocad.v2.common.target.data.model.LookupTableFunction;
import org.cellocad.v2.common.target.data.model.Model;
import org.cellocad.v2.common.target.data.model.Structure;
import org.cellocad.v2.common.target.data.model.UnivariateLookupTableFunction;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * The TargetDataUtils class is class with utility methods for <i>TargetData</i>
 * instances.
 * 
 * @author Vincent Mirian
 * @author Timothy Jones
 * 
 * @date Nov 22, 2017
 *
 */
public class TargetDataUtils {

	public static final CObjectCollection<Part> getParts(final TargetData td) {
		CObjectCollection<Part> rtn = new CObjectCollection<Part>();
		for (int i = 0; i < td.getNumJSONObject(S_PARTS); i++) {
			JSONObject jObj = td.getJSONObjectAtIdx(S_PARTS, i);
			Part part = new Part(jObj);
			rtn.add(part);
		}
		return rtn;
	}

	public static final CObjectCollection<Function> getFunctions(final TargetData td) throws CelloException {
		CObjectCollection<Function> rtn = new CObjectCollection<Function>();
		for (int i = 0; i < td.getNumJSONObject(S_FUNCTIONS); i++) {
			JSONObject jObj = td.getJSONObjectAtIdx(S_FUNCTIONS, i);
			Function function;
			if (jObj.containsKey(AnalyticFunction.S_EQUATION))
				function = new AnalyticFunction(jObj);
			else if (jObj.containsKey(LookupTableFunction.S_TABLE)) {
				JSONArray variables = (JSONArray) jObj.get(Function.S_VARIABLES);
				if (variables.size() == 1)
					function = new UnivariateLookupTableFunction(jObj);
				else if (variables.size() == 2)
					function = new BivariateLookupTableFunction(jObj);
				else {
					String fmt = "Bad variable count: %s.";
					throw new CelloException(String.format(fmt, jObj.toString()));
				}
			} else {
				String fmt = "Invalid %s specification: %s";
				throw new CelloException(String.format(fmt, Function.class.getName(), jObj.toString()));
			}
			rtn.add(function);
		}
		return rtn;
	}

	private static void attachFunctions(final JSONObject jObj, final Model model,
			final CObjectCollection<Function> functions) {
		JSONObject obj = (JSONObject) jObj.get(Model.S_FUNCTIONS);
		Iterator<?> it = obj.keySet().iterator();
		while (it.hasNext()) {
			String name = (String) it.next();
			String value = ProfileUtils.getString(obj, name);
			Function function = functions.findCObjectByName(value);
			model.addFunction(name, function);
		}
	}

	public static final CObjectCollection<Model> getModels(final TargetData td,
			final CObjectCollection<Function> functions) {
		CObjectCollection<Model> rtn = new CObjectCollection<Model>();
		for (int i = 0; i < td.getNumJSONObject(S_MODELS); i++) {
			JSONObject jObj = td.getJSONObjectAtIdx(S_MODELS, i);
			Model model = new Model(jObj);
			attachFunctions(jObj, model, functions);
			rtn.add(model);
		}
		return rtn;
	}

	public static final CObjectCollection<Structure> getStructures(final TargetData td) throws CelloException {
		CObjectCollection<Structure> rtn = new CObjectCollection<Structure>();
		for (int i = 0; i < td.getNumJSONObject(S_STRUCTURES); i++) {
			JSONObject jObj = td.getJSONObjectAtIdx(S_STRUCTURES, i);
			Structure structure = new Structure(jObj);
			rtn.add(structure);
		}
		return rtn;
	}

	private static void attachModel(final JSONObject jObj, final AssignableDevice d,
			final CObjectCollection<Model> models) {
		String name = ProfileUtils.getString(jObj, AssignableDevice.S_MODEL);
		Model model = models.findCObjectByName(name);
		d.setModel(model);
	}

	private static void attachStructure(final JSONObject jObj, final AssignableDevice d,
			final CObjectCollection<Structure> structures) {
		String name = ProfileUtils.getString(jObj, AssignableDevice.S_STRUCTURE);
		Structure structure = structures.findCObjectByName(name);
		d.setStructure(structure);
	}

	public static final CObjectCollection<Gate> getGates(final TargetData td, final CObjectCollection<Model> models,
			final CObjectCollection<Structure> structures) {
		CObjectCollection<Gate> rtn = new CObjectCollection<Gate>();
		for (int i = 0; i < td.getNumJSONObject(S_GATES); i++) {
			JSONObject jObj = td.getJSONObjectAtIdx(S_GATES, i);
			Gate gate = new Gate(jObj);
			// model
			attachModel(jObj, gate, models);
			// structure
			attachStructure(jObj, gate, structures);
			rtn.add(gate);
		}
		return rtn;
	}

	public static final CObjectCollection<InputSensor> getInputSensors(final TargetData td,
			final CObjectCollection<Model> models, final CObjectCollection<Structure> structures) {
		CObjectCollection<InputSensor> rtn = new CObjectCollection<InputSensor>();
		for (int i = 0; i < td.getNumJSONObject(S_INPUTSENSORS); i++) {
			JSONObject jObj = td.getJSONObjectAtIdx(S_INPUTSENSORS, i);
			InputSensor sensor = new InputSensor(jObj);
			// model
			attachModel(jObj, sensor, models);
			// structure
			attachStructure(jObj, sensor, structures);
			rtn.add(sensor);
		}
		return rtn;
	}

	public static final CObjectCollection<OutputDevice> getOutputDevices(final TargetData td,
			final CObjectCollection<Model> models, final CObjectCollection<Structure> structures) {
		CObjectCollection<OutputDevice> rtn = new CObjectCollection<OutputDevice>();
		for (int i = 0; i < td.getNumJSONObject(S_OUTPUTDEVICES); i++) {
			JSONObject jObj = td.getJSONObjectAtIdx(S_OUTPUTDEVICES, i);
			OutputDevice device = new OutputDevice(jObj);
			// model
			attachModel(jObj, device, models);
			// structure
			attachStructure(jObj, device, structures);
			rtn.add(device);
		}
		return rtn;
	}

	private static JSONArray getJsonArrayFromFile(final String file) {
		JSONArray rtn = null;
		// get File
		File f = new File(file);
		Reader reader = null;
		// Create File Reader
		try {
			reader = new FileReader(f);
		} catch (FileNotFoundException e) {
			throw new RuntimeException("Error with file: " + file);
		}
		// Create JSON object from File Reader
		JSONParser parser = new JSONParser();
		try {
			rtn = (JSONArray) parser.parse(reader);
		} catch (IOException e) {
			throw new RuntimeException("File IO Exception for: " + file + ".");
		} catch (ParseException e) {
			throw new RuntimeException("Parser Exception for: " + file + ".");
		}
		try {
			reader.close();
		} catch (IOException e) {
			throw new RuntimeException("Error with file: " + file);
		}
		return rtn;
	}

	/**
	 * Initializes a newly created TargetData using the RuntimeEnv, <i>runEnv</i>,
	 * and strings referencing command line arguments.
	 * 
	 * @param runEnv                    the RuntimeEnv
	 * @param userConstraintsFileOption the string referencing command line argument
	 *                                  for the User Constraints File
	 * @param inputSensorFileOption     the string referencing command line argument
	 *                                  for the Input Sensor file
	 * @param outputDeviceFileOption    the string referencing command line argument
	 *                                  for the Output Reporter file
	 * @return the TargetData if created successfully, otherwise null
	 */
	@SuppressWarnings("unchecked")
	public static final TargetData getTargetTargetData(final RuntimeEnv runEnv, final String userConstraintsFileOption,
			final String inputSensorFileOption, final String outputDeviceFileOption) {
		Utils.isNullRuntimeException(runEnv, "runEnv");
		Utils.isNullRuntimeException(userConstraintsFileOption, "userConstraintsFileOption");
		TargetData rtn = null;
		JSONArray jsonTop = null;
		// get User Constraints File
		String userConstraintsFileName = runEnv.getOptionValue(userConstraintsFileOption);
		JSONArray userConstraintsJson = getJsonArrayFromFile(userConstraintsFileName);
		// get Input Sensor File
		String inputSensorFileName = runEnv.getOptionValue(inputSensorFileOption);
		JSONArray inputSensorJson = getJsonArrayFromFile(inputSensorFileName);
		// get Output Device File
		String outputDeviceFileName = runEnv.getOptionValue(outputDeviceFileOption);
		JSONArray outputDeviceJson = getJsonArrayFromFile(outputDeviceFileName);
		// combine Json
		jsonTop = new JSONArray();
		jsonTop.addAll(userConstraintsJson);
		jsonTop.addAll(inputSensorJson);
		jsonTop.addAll(outputDeviceJson);
		// Create TargetData object
	    rtn = new TargetData(jsonTop);
	    return rtn;
	}

	private static final String S_PARTS = "parts";
	private static final String S_FUNCTIONS = "functions";
	private static final String S_MODELS = "models";
	private static final String S_STRUCTURES = "structures";
	private static final String S_GATES = "gates";
	private static final String S_INPUTSENSORS = "input_sensors";
	private static final String S_OUTPUTDEVICES = "output_devices";

}

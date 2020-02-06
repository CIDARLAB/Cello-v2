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

import org.cellocad.v2.common.Utils;
import org.cellocad.v2.common.runtime.environment.RuntimeEnv;
import org.json.simple.JSONArray;
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
final public class TargetDataUtils {

	static private JSONArray getJsonArrayFromFile(final String file) {
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
	static public TargetData getTargetTargetData(final RuntimeEnv runEnv, final String userConstraintsFileOption,
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
}

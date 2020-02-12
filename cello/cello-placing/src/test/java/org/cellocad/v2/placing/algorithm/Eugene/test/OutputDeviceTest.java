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
package org.cellocad.v2.placing.algorithm.Eugene.test;

import java.io.IOException;

import org.cellocad.v2.common.Utils;
import org.cellocad.v2.common.target.data.data.OutputDevice;
import org.cellocad.v2.common.target.data.data.OutputDeviceStructure;
import org.cellocad.v2.common.target.data.data.StructureDevice;
import org.cellocad.v2.placing.algorithm.Eugene.target.data.data.EugeneDevice;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.junit.Test;

/**
 *
 *
 * @author Timothy Jones
 *
 * @date 2020-01-29
 *
 */
public class OutputDeviceTest {

	@Test
	public void test() throws IOException, ParseException {
		String deviceString = Utils.getResourceAsString("output_device.json");
		String structureString = Utils.getResourceAsString("output_device_structure.json");
		JSONParser parser = new JSONParser();
		JSONObject deviceJson = (JSONObject) parser.parse(deviceString);
		JSONObject structureJson = (JSONObject) parser.parse(structureString);
		OutputDeviceStructure structure = new OutputDeviceStructure(structureJson);
		OutputDevice device = new OutputDevice(deviceJson);
		device.setOutputDeviceStructure(structure);
		structure.setOutputDevice(device);
		String str = "";
		for (StructureDevice d : structure.getDevices()) {
			EugeneDevice e = new EugeneDevice(d);
			str += e.toString();
		}
		assert (str.equals(S_REF));
	}

	private static String S_REF = "Device YFP_reporter_a(\n" + "    promoter,\n" + "    YFP_cassette\n" + ");\n"
			+ "Device YFP_reporter_b(\n" + "    promoter,\n" + "    YFP_cassette\n" + ");\n";

}

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

import java.io.IOException;
import java.util.Collection;

import org.cellocad.v2.common.CelloException;
import org.cellocad.v2.common.Utils;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 *
 * @author Timothy Jones
 *
 * @date 2020-02-18
 *
 */
public class TargetDataInstanceTest {

	@SuppressWarnings("unchecked")
	@BeforeClass
	public static void getJsonTargetData() throws IOException, ParseException, CelloException {
		JSONParser parser = new JSONParser();
		String str;
		JSONArray jsonTop = new JSONArray();
		str = Utils.getResourceAsString("lib/ucf/Bth/Bth1C1G1T1.UCF.json");
		jsonTop.addAll((Collection<Object>) parser.parse(str));
		str = Utils.getResourceAsString("lib/input/Bth/Bth1C1G1T1.input.json");
		jsonTop.addAll((Collection<Object>) parser.parse(str));
		str = Utils.getResourceAsString("lib/output/Bth/Bth1C1G1T1.output.json");
		jsonTop.addAll((Collection<Object>) parser.parse(str));
		TargetData td = new TargetData(jsonTop);
		tdi = new TargetDataInstance(td);
	}

	@Test
	public void getGates_None_SizeShouldBe7() {
		assert (tdi.getGates().size() == 7);
	}

	@Test
	public void getParts_None_SizeShouldBe40() {
		assert (tdi.getParts().size() == 40);
	}

	@Test
	public void getInputSensors_None_SizeShouldBe3() {
		assert (tdi.getInputSensors().size() == 3);
	}

	@Test
	public void getOutputDevices_None_SizeShouldBe1() {
		assert (tdi.getOutputDevices().size() == 1);
	}

	private static TargetDataInstance tdi;

}

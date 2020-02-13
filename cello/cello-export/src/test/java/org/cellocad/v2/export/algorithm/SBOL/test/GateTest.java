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
package org.cellocad.v2.export.algorithm.SBOL.test;

import java.io.IOException;

import org.cellocad.v2.common.Utils;
import org.cellocad.v2.common.target.data.component.Gate;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.junit.Test;

/**
 *
 *
 * @author Timothy Jones
 *
 * @date 2020-01-28
 *
 */
public class GateTest {

	@Test
	public void test() throws ParseException, IOException {
		String str = Utils.getResourceAsString("gate.json");
		JSONObject jsonTop = null;
		JSONParser parser = new JSONParser();
		jsonTop = (JSONObject) parser.parse(str);
		Gate gate = new Gate(jsonTop);
		assert (gate.isValid());
		assert (gate.getRegulator().equals("AmeR"));
		assert (gate.getGroup().equals("AmeR"));
		assert (gate.getName().equals("F1_AmeR"));
		assert (gate.getGateType().equals("NOR"));
		assert (gate.getSystem().equals("TetR"));
		assert (gate.getColor().getRed() == 111);
		assert (gate.getColor().getGreen() == 205);
		assert (gate.getColor().getBlue() == 225);
	}

}

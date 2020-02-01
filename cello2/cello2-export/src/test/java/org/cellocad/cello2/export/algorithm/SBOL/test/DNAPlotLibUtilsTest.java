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
package org.cellocad.cello2.export.algorithm.SBOL.test;

import java.io.IOException;
import java.util.List;

import org.cellocad.cello2.common.CObjectCollection;
import org.cellocad.cello2.common.Utils;
import org.cellocad.cello2.common.target.data.TargetData;
import org.cellocad.cello2.export.algorithm.SBOL.data.DNAPlotLibUtils;
import org.cellocad.cello2.export.algorithm.SBOL.data.SBOLDataUtils;
import org.cellocad.cello2.export.algorithm.SBOL.data.ucf.Gate;
import org.cellocad.cello2.export.algorithm.SBOL.data.ucf.Part;
import org.cellocad.cello2.results.netlist.Netlist;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.junit.Before;
import org.junit.Test;

/**
 *
 *
 * @author Timothy Jones
 *
 * @date 2020-01-28
 *
 */
public class DNAPlotLibUtilsTest {

	private static boolean initIsDone = false;

	@Before
	public void init() throws IOException, ParseException {
		if (initIsDone)
			return;
		String str = null;
		JSONArray jArr = null;
		JSONObject jObj = null;
		JSONParser parser = new JSONParser();
		str = Utils.getResourceAsString("and_netlist.json");
		jObj = (JSONObject) parser.parse(str);
		netlist = new Netlist(jObj);
		str = Utils.getResourceAsString("Eco1C1G1T1.UCF.json");
		jArr = (JSONArray) parser.parse(str);
		targetData = new TargetData(jArr);
		parts = SBOLDataUtils.getParts(targetData);
		gates = SBOLDataUtils.getGates(targetData);
		initIsDone = true;
	}

	@Test
	public void testGetDNADesigns() {
		List<String> designs = DNAPlotLibUtils.getDNADesigns(netlist);
		// System.out.println(String.join(Utils.getNewLine(), designs));
	}

	@Test
	public void testGetRegulatoryInformation() {
		List<String> reg = DNAPlotLibUtils.getRegulatoryInformation(netlist, gates);
		// System.out.println(String.join(Utils.getNewLine(), reg));
	}

	@Test
	public void testPartInformation() {
		List<String> part = DNAPlotLibUtils.getPartInformation(netlist, parts, gates);
		// System.out.println(String.join(Utils.getNewLine(), part));
	}

	private static Netlist netlist;
	private static TargetData targetData;
	private static CObjectCollection<Part> parts;
	private static CObjectCollection<Gate> gates;

}

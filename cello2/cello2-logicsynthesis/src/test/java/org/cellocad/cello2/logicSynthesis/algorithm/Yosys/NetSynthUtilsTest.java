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
package org.cellocad.cello2.logicSynthesis.algorithm.Yosys;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.apache.commons.io.FileUtils;
import org.cellocad.cello2.common.Utils;
import org.cellocad.cello2.common.target.data.TargetData;
import org.cellocad.cello2.logicSynthesis.algorithm.Yosys.data.YosysDataUtils;
import org.cellocad.cello2.results.netlist.Netlist;
import org.cellocad.cello2.results.netlist.NetlistUtils;
import org.json.JSONException;
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
 * @date 2020-01-31
 *
 */
public class NetSynthUtilsTest {

	private static boolean initIsDone = false;

	@Before
	public void init() throws IOException, ParseException {
		if (initIsDone)
			return;
		String str = null;
		JSONArray jArr = null;
		JSONObject jObj = null;
		JSONParser parser = new JSONParser();
		str = Utils.getResourceAsString("xor_netlist.json");
		jObj = (JSONObject) parser.parse(str);
		netlist = new Netlist(jObj);
		str = Utils.getResourceAsString("Eco1C1G1T1.UCF.json");
		jArr = (JSONArray) parser.parse(str);
		targetData = new TargetData(jArr);
		initIsDone = true;
	}

	@Test
	public void testEscapeSpecialCharacters() {
		String str = "$50";
		str = NetSynthUtils.escapeSpecialCharacters(str);
		assert (str.equals("\\$50"));
	}

	@Test
	public void testGetVerilog() {
		String str = "";
		str = NetSynthUtils.getVerilog(netlist);
	}

	@Test
	public void testGetNetSynthNetlist() throws JSONException, IOException {
		JSONArray motifs = YosysDataUtils.getMotifLibrary(targetData);
		Path dir = Files.createTempDirectory("Cello2_");
		Netlist n = NetSynthUtils.getNetSynthNetlist(netlist, motifs, dir.toString());
		String output = dir.toString() + Utils.getFileSeparator() + netlist.getInputFilename() + "_netSynth.json";
		NetlistUtils.writeJSONForNetlist(n, output);
		FileUtils.deleteDirectory(dir.toFile());
	}

	private static Netlist netlist;
	private static TargetData targetData;

}

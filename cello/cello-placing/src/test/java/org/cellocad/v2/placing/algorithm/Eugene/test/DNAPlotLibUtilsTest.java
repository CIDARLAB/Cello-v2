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

import java.io.File;
import java.util.List;

import org.cellocad.v2.common.CObjectCollection;
import org.cellocad.v2.common.CelloException;
import org.cellocad.v2.common.Utils;
import org.cellocad.v2.common.target.data.TargetData;
import org.cellocad.v2.common.target.data.TargetDataUtils;
import org.cellocad.v2.placing.algorithm.Eugene.data.DNAPlotLibUtils;
import org.cellocad.v2.placing.algorithm.Eugene.data.EugeneDataUtils;
import org.cellocad.v2.placing.algorithm.Eugene.data.ucf.Gate;
import org.cellocad.v2.placing.algorithm.Eugene.data.ucf.Part;
import org.cellocad.v2.placing.runtime.environment.PLArgString;
import org.cellocad.v2.placing.runtime.environment.PLRuntimeEnv;
import org.cellocad.v2.results.netlist.Netlist;
import org.cellocad.v2.results.netlist.NetlistUtils;
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
	public void init() throws CelloException {
		if (initIsDone)
			return;
		String[] args = { "-" + PLArgString.INPUTNETLIST, Utils.getResource("and_placed_netlist.json").getFile(),
				"-" + PLArgString.USERCONSTRAINTSFILE, Utils.getResource("Eco1C1G1T1.UCF.json").getFile(),
				"-" + PLArgString.INPUTSENSORFILE, Utils.getResource("Eco1C1G1T1.input.json").getFile(),
				"-" + PLArgString.OUTPUTDEVICEFILE, Utils.getResource("Eco1C1G1T1.output.json").getFile(),
				"-" + PLArgString.ALGORITHMNAME, "Eugene" };
		PLRuntimeEnv runEnv = new PLRuntimeEnv(args);
		runEnv.setName("placing");
		// InputFile
		String inputFilePath = runEnv.getOptionValue(PLArgString.INPUTNETLIST);
		File inputFile = new File(inputFilePath);
		if (!(inputFile.exists() && !inputFile.isDirectory())) {
			throw new CelloException("Input file does not exist!");
		}
		// Read Netlist
		netlist = NetlistUtils.getNetlist(runEnv, PLArgString.INPUTNETLIST);
		if (!netlist.isValid()) {
			throw new CelloException("Netlist is invalid!");
		}
		// get TargetData
		td = TargetDataUtils.getTargetTargetData(runEnv, PLArgString.USERCONSTRAINTSFILE,
				PLArgString.INPUTSENSORFILE, PLArgString.OUTPUTDEVICEFILE);
		if (!td.isValid()) {
			throw new CelloException("TargetData is invalid!");
		}
		parts = EugeneDataUtils.getParts(td);
		gates = EugeneDataUtils.getGates(td);
		initIsDone = true;
	}

	@Test
	public void testGetDNADesigns() {
		List<String> designs = DNAPlotLibUtils.getDNADesigns(netlist);
		assert (designs.size() == 2);
		// System.out.println(String.join(Utils.getNewLine(), designs));
	}

	@Test
	public void testGetRegulatoryInformation() {
		List<String> reg = DNAPlotLibUtils.getRegulatoryInformation(netlist, parts, gates);
		assert (reg.size() == 4);
		// System.out.println(String.join(Utils.getNewLine(), reg));
	}

	@Test
	public void testPartInformation() {
		List<String> part = DNAPlotLibUtils.getPartInformation(netlist, parts, gates);
		assert (part.size() == 20);
		assert (part.get(2).equals("YFP_cassette,UserDefined,25,5,,,0.00;0.00;0.00,,,,,"));
		// System.out.println(String.join(Utils.getNewLine(), part));
	}

	private static Netlist netlist;
	private static TargetData td;
	private static CObjectCollection<Part> parts;
	private static CObjectCollection<Gate> gates;

}

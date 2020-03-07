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
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.apache.commons.io.FileUtils;
import org.cellocad.v2.common.CelloException;
import org.cellocad.v2.common.Utils;
import org.cellocad.v2.common.netlistConstraint.data.NetlistConstraint;
import org.cellocad.v2.common.netlistConstraint.data.NetlistConstraintUtils;
import org.cellocad.v2.common.stage.Stage;
import org.cellocad.v2.common.stage.StageUtils;
import org.cellocad.v2.common.target.data.TargetData;
import org.cellocad.v2.common.target.data.TargetDataUtils;
import org.cellocad.v2.placing.runtime.PLRuntimeObject;
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
 * @date 2020-01-09
 *
 */
public class EugeneTest {

	private static boolean initIsDone = false;

	@Before
	public void init() throws CelloException, IOException {
		if (initIsDone)
			return;
		Path dir = Files.createTempDirectory("cello_");
		this.output = dir.toFile();
		String[] args = { "-" + PLArgString.INPUTNETLIST, Utils.getResource("and_netlist.json").getFile(),
				"-" + PLArgString.USERCONSTRAINTSFILE, Utils.getResource("lib/ucf/Eco/Eco1C1G1T1.UCF.json").getFile(),
				"-" + PLArgString.INPUTSENSORFILE, Utils.getResource("lib/input/Eco/Eco1C1G1T1.input.json").getFile(),
				"-" + PLArgString.OUTPUTDEVICEFILE,
				Utils.getResource("lib/output/Eco/Eco1C1G1T1.output.json").getFile(),
				"-" + PLArgString.ALGORITHMNAME, "Eugene",
				"-" + PLArgString.OUTPUTDIR, dir.toString(), "-" + PLArgString.PYTHONENV, "python" };
		PLRuntimeEnv runEnv = new PLRuntimeEnv(args);
		runEnv.setName("placing");
		// InputFile
		String inputFilePath = runEnv.getOptionValue(PLArgString.INPUTNETLIST);
		File inputFile = new File(inputFilePath);
		if (!(inputFile.exists() && !inputFile.isDirectory())) {
			throw new CelloException("Input file does not exist!");
		}
		// Read Netlist
		Netlist netlist = NetlistUtils.getNetlist(runEnv, PLArgString.INPUTNETLIST);
		if (!netlist.isValid()) {
			throw new CelloException("Netlist is invalid!");
		}
		// get Stage
		Stage stage = StageUtils.getStage(runEnv, PLArgString.ALGORITHMNAME);
		stage.setName("placing");
		String stageName = runEnv.getOptionValue(PLArgString.STAGENAME);
		if (stageName != null) {
			stage.setName(stageName);
		}
		// get TargetData
		TargetData td = TargetDataUtils.getTargetTargetData(runEnv, PLArgString.USERCONSTRAINTSFILE,
				PLArgString.INPUTSENSORFILE, PLArgString.OUTPUTDEVICEFILE);
		if (!td.isValid()) {
			throw new CelloException("TargetData is invalid!");
		}
		// NetlistConstraint
		NetlistConstraint netlistConstraint = NetlistConstraintUtils.getNetlistConstraintData(runEnv,
				PLArgString.NETLISTCONSTRAINTFILE);
		if (netlistConstraint == null) {
			netlistConstraint = new NetlistConstraint();
		}
		// Execute
		PLRuntimeObject PL = new PLRuntimeObject(stage, td, netlistConstraint, netlist, runEnv);
		PL.setName("placing");
		this.PL = PL;
		initIsDone = true;
	}

	@Test
	public void test() throws CelloException, IOException {
		this.PL.execute();
		FileUtils.deleteDirectory(this.output);
	}

	private PLRuntimeObject PL;
	private File output;

}

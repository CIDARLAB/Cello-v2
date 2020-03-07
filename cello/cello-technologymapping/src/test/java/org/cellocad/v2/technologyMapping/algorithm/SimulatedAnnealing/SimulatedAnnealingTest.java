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
package org.cellocad.v2.technologyMapping.algorithm.SimulatedAnnealing;

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
import org.cellocad.v2.results.netlist.Netlist;
import org.cellocad.v2.results.netlist.NetlistUtils;
import org.cellocad.v2.technologyMapping.runtime.TMRuntimeObject;
import org.cellocad.v2.technologyMapping.runtime.environment.TMArgString;
import org.cellocad.v2.technologyMapping.runtime.environment.TMRuntimeEnv;
import org.junit.Before;
import org.junit.Test;

/**
 *
 *
 * @author Timothy Jones
 *
 * @date 2020-01-30
 *
 */
public class SimulatedAnnealingTest {

	private static boolean initIsDone = false;

	@Before
	public void init() throws CelloException, IOException {
		if (initIsDone)
			return;
		Path dir = Files.createTempDirectory("cello_");
		this.output = dir.toFile();
		String[] args = { "-" + TMArgString.INPUTNETLIST, Utils.getResource("and_netlist.json").getFile(),
				"-" + TMArgString.USERCONSTRAINTSFILE, Utils.getResource("lib/ucf/Eco/Eco1C1G1T1.UCF.json").getFile(),
				"-" + TMArgString.INPUTSENSORFILE, Utils.getResource("lib/input/Eco/Eco1C1G1T1.input.json").getFile(),
				"-" + TMArgString.OUTPUTDEVICEFILE,
				Utils.getResource("lib/output/Eco/Eco1C1G1T1.output.json").getFile(),
				"-" + TMArgString.ALGORITHMNAME, "SimulatedAnnealing", "-" + TMArgString.PYTHONENV, "python",
				"-" + TMArgString.OUTPUTDIR, dir.toString(), "-" + TMArgString.LOGFILENAME,
				dir.toString() + Utils.getFileSeparator() + "log.log" };
		TMRuntimeEnv runEnv = new TMRuntimeEnv(args);
		runEnv.setName("technologyMapping");
		// InputFile
		String inputFilePath = runEnv.getOptionValue(TMArgString.INPUTNETLIST);
		File inputFile = new File(inputFilePath);
		if (!(inputFile.exists() && !inputFile.isDirectory())) {
			throw new CelloException("Input file does not exist!");
		}
		// Read Netlist
		Netlist netlist = NetlistUtils.getNetlist(runEnv, TMArgString.INPUTNETLIST);
		if (!netlist.isValid()) {
			throw new CelloException("Netlist is invalid!");
		}
		// get Stage
		Stage stage = StageUtils.getStage(runEnv, TMArgString.ALGORITHMNAME);
		stage.setName("technologyMapping");
		String stageName = runEnv.getOptionValue(TMArgString.STAGENAME);
		if (stageName != null) {
			stage.setName(stageName);
		}
		// get TargetData
		TargetData td = TargetDataUtils.getTargetTargetData(runEnv, TMArgString.USERCONSTRAINTSFILE,
				TMArgString.INPUTSENSORFILE, TMArgString.OUTPUTDEVICEFILE);
		if (!td.isValid()) {
			throw new CelloException("TargetData is invalid!");
		}
		// NetlistConstraint
		NetlistConstraint netlistConstraint = NetlistConstraintUtils.getNetlistConstraintData(runEnv,
				TMArgString.NETLISTCONSTRAINTFILE);
		if (netlistConstraint == null) {
			netlistConstraint = new NetlistConstraint();
		}
		// Execute
		TMRuntimeObject TM = new TMRuntimeObject(stage, td, netlistConstraint, netlist, runEnv);
		TM.setName("technologyMapiing");
		this.TM = TM;
		initIsDone = true;
	}

	@Test
	public void test() throws CelloException, IOException {
		this.TM.execute();
		FileUtils.deleteDirectory(this.output);
	}

	private TMRuntimeObject TM;
	private File output;

}

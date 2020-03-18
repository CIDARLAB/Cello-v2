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
package org.cellocad.v2.logicSynthesis.algorithm.Yosys;

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
import org.cellocad.v2.logicSynthesis.runtime.LSRuntimeObject;
import org.cellocad.v2.logicSynthesis.runtime.environment.LSArgString;
import org.cellocad.v2.logicSynthesis.runtime.environment.LSRuntimeEnv;
import org.cellocad.v2.results.netlist.Netlist;
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
public class YosysIT {

	private static boolean initIsDone = false;

	@Before
	public void init() throws CelloException, IOException {
		if (initIsDone)
			return;
		Path dir = Files.createTempDirectory("cello_");
		this.output = dir.toFile();
		String[] args = { "-" + LSArgString.INPUTNETLIST, Utils.getResource("and.v").getFile(),
				"-" + LSArgString.USERCONSTRAINTSFILE, Utils.getResource("lib/ucf/Bth/Bth1C1G1T1.UCF.json").getFile(),
				"-" + LSArgString.INPUTSENSORFILE, Utils.getResource("lib/input/Bth/Bth1C1G1T1.input.json").getFile(),
				"-" + LSArgString.OUTPUTDEVICEFILE,
				Utils.getResource("lib/output/Bth/Bth1C1G1T1.output.json").getFile(),
				"-" + LSArgString.ALGORITHMNAME, "Yosys", "-" + LSArgString.OUTPUTDIR, dir.toString(),
				"-" + LSArgString.PYTHONENV, "python" };
		LSRuntimeEnv runEnv = new LSRuntimeEnv(args);
		runEnv.setName("logicSynthesis");
		// InputFile
		String inputFilePath = runEnv.getOptionValue(LSArgString.INPUTNETLIST);
		File inputFile = new File(inputFilePath);
		if (!(inputFile.exists() && !inputFile.isDirectory())) {
			throw new CelloException("Input file does not exist!");
		}
		// Read Netlist
		Netlist netlist = new Netlist();
		// Input from User
		netlist.setInputFilename(inputFilePath);
		// get Stage
		Stage stage = StageUtils.getStage(runEnv, LSArgString.ALGORITHMNAME);
		stage.setName("logicSynthesis");
		String stageName = runEnv.getOptionValue(LSArgString.STAGENAME);
		if (stageName != null) {
			stage.setName(stageName);
		}
		// get TargetData
		TargetData td = TargetDataUtils.getTargetTargetData(runEnv, LSArgString.USERCONSTRAINTSFILE,
				LSArgString.INPUTSENSORFILE, LSArgString.OUTPUTDEVICEFILE);
		if (!td.isValid()) {
			throw new CelloException("TargetData is invalid!");
		}
		// NetlistConstraint
		NetlistConstraint netlistConstraint = NetlistConstraintUtils.getNetlistConstraintData(runEnv,
				LSArgString.NETLISTCONSTRAINTFILE);
		if (netlistConstraint == null) {
			netlistConstraint = new NetlistConstraint();
		}
		// Execute
		LSRuntimeObject LS = new LSRuntimeObject(stage, td, netlistConstraint, netlist, runEnv);
		LS.setName("logicSynthesis");
		this.LS = LS;
		initIsDone = true;
	}

	@Test
	public void Yosys_Bth1C1G1T1_ShouldReturn() throws CelloException, IOException {
		this.LS.execute();
		FileUtils.deleteDirectory(this.output);
	}

	private LSRuntimeObject LS;
	private File output;

}

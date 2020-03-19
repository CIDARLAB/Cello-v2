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
package org.cellocad.v2.export.algorithm.SBOL;

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
import org.cellocad.v2.export.runtime.EXRuntimeObject;
import org.cellocad.v2.export.runtime.environment.EXArgString;
import org.cellocad.v2.export.runtime.environment.EXRuntimeEnv;
import org.cellocad.v2.results.netlist.Netlist;
import org.cellocad.v2.results.netlist.NetlistUtils;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 *
 * @author Timothy Jones
 *
 * @date 2020-02-25
 *
 */
public class SBOLTest {

	@BeforeClass
	public static void init() throws IOException, CelloException {
		Path dir = Files.createTempDirectory("cello_");
		output = dir.toFile();
		String[] args = { "-" + EXArgString.INPUTNETLIST, Utils.getResource("and_netlist.json").getFile(),
				"-" + EXArgString.USERCONSTRAINTSFILE, Utils.getResource("lib/ucf/Eco/Eco1C1G1T1.UCF.json").getFile(),
				"-" + EXArgString.INPUTSENSORFILE, Utils.getResource("lib/input/Eco/Eco1C1G1T1.input.json").getFile(),
				"-" + EXArgString.OUTPUTDEVICEFILE,
				Utils.getResource("lib/output/Eco/Eco1C1G1T1.output.json").getFile(),
				"-" + EXArgString.ALGORITHMNAME, "SBOL", "-" + EXArgString.OUTPUTDIR, dir.toString(),
				"-" + EXArgString.PYTHONENV, "python" };
		EXRuntimeEnv runEnv = new EXRuntimeEnv(args);
		runEnv.setName("export");
		// Read Netlist
		Netlist netlist = NetlistUtils.getNetlist(runEnv, EXArgString.INPUTNETLIST);
		// get Stage
		Stage stage = StageUtils.getStage(runEnv, EXArgString.ALGORITHMNAME);
		stage.setName("export");
		String stageName = runEnv.getOptionValue(EXArgString.STAGENAME);
		if (stageName != null) {
			stage.setName(stageName);
		}
		// get TargetData
		TargetData td = TargetDataUtils.getTargetTargetData(runEnv, EXArgString.USERCONSTRAINTSFILE,
				EXArgString.INPUTSENSORFILE, EXArgString.OUTPUTDEVICEFILE);
		// NetlistConstraint
		NetlistConstraint netlistConstraint = NetlistConstraintUtils.getNetlistConstraintData(runEnv,
				EXArgString.NETLISTCONSTRAINTFILE);
		if (netlistConstraint == null) {
			netlistConstraint = new NetlistConstraint();
		}
		// Execute
		EX = new EXRuntimeObject(stage, td, netlistConstraint, netlist, runEnv);
		EX.setName("export");
	}

	@Test
	public void execute_None_ShouldReturn() throws CelloException, IOException {
		EX.execute();
		FileUtils.deleteDirectory(output);
	}

	private static EXRuntimeObject EX;
	private static File output;

}

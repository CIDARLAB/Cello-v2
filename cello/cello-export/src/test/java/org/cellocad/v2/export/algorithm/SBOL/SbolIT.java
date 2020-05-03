/*
 * Copyright (C) 2020 Boston University (BU)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
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
import org.cellocad.v2.common.runtime.environment.ArgString;
import org.cellocad.v2.common.stage.Stage;
import org.cellocad.v2.common.stage.StageUtils;
import org.cellocad.v2.common.stage.runtime.environment.StageArgString;
import org.cellocad.v2.common.target.data.TargetData;
import org.cellocad.v2.common.target.data.TargetDataUtils;
import org.cellocad.v2.export.runtime.EXRuntimeObject;
import org.cellocad.v2.export.runtime.environment.EXRuntimeEnv;
import org.cellocad.v2.results.common.Results;
import org.cellocad.v2.results.netlist.Netlist;
import org.cellocad.v2.results.netlist.NetlistUtils;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Integration test for {@link Sbol}.
 *
 * @author Timothy Jones
 *
 * @date 2020-02-25
 */
public class SbolIT {

  /**
   * Environment setup for tests.
   * 
   * @throws IOException    Unable to read resources.
   * @throws CelloException Unable to instantiate supporting classes.
   */
  @BeforeClass
  public static void init() throws IOException, CelloException {
    final Path dir = Files.createTempDirectory("cello_");
    SbolIT.output = dir.toFile();
    final String[] args =
        {"-" + ArgString.INPUTNETLIST, Utils.getResource("and_SC1C1G1T1_PL.netlist.json").getFile(),
            "-" + ArgString.USERCONSTRAINTSFILE,
            Utils.getResource("lib/ucf/SC/SC1C1G1T1.UCF.json").getFile(),
            "-" + ArgString.INPUTSENSORFILE,
            Utils.getResource("lib/input/SC/SC1C1G1T1.input.json").getFile(),
            "-" + ArgString.OUTPUTDEVICEFILE,
            Utils.getResource("lib/output/SC/SC1C1G1T1.output.json").getFile(),
            "-" + StageArgString.ALGORITHMNAME, "SBOL", "-" + ArgString.OUTPUTDIR, dir.toString(),
            "-" + ArgString.PYTHONENV, "python"};
    final EXRuntimeEnv runEnv = new EXRuntimeEnv(args);
    runEnv.setName("export");
    // Read Netlist
    final Netlist netlist = NetlistUtils.getNetlist(runEnv, ArgString.INPUTNETLIST);
    // get Stage
    final Stage stage = StageUtils.getStage(runEnv, StageArgString.ALGORITHMNAME);
    stage.setName("export");
    final String stageName = runEnv.getOptionValue(StageArgString.STAGENAME);
    if (stageName != null) {
      stage.setName(stageName);
    }
    // get TargetData
    final TargetData td = TargetDataUtils.getTargetTargetData(runEnv, ArgString.USERCONSTRAINTSFILE,
        ArgString.INPUTSENSORFILE, ArgString.OUTPUTDEVICEFILE);
    // NetlistConstraint
    NetlistConstraint netlistConstraint =
        NetlistConstraintUtils.getNetlistConstraintData(runEnv, ArgString.NETLISTCONSTRAINTFILE);
    if (netlistConstraint == null) {
      netlistConstraint = new NetlistConstraint();
    }
    // Results
    final File outputDir = new File(runEnv.getOptionValue(ArgString.OUTPUTDIR));
    final Results results = new Results(outputDir);
    // Execute
    SbolIT.EX = new EXRuntimeObject(stage, td, netlistConstraint, netlist, results, runEnv);
    SbolIT.EX.setName("export");
  }

  @Test
  public void execute_None_ShouldReturn() throws CelloException, IOException {
    SbolIT.EX.execute();
    FileUtils.deleteDirectory(SbolIT.output);
  }

  private static EXRuntimeObject EX;
  private static File output;

}

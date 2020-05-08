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
import org.cellocad.v2.common.runtime.environment.ArgString;
import org.cellocad.v2.common.stage.Stage;
import org.cellocad.v2.common.stage.StageUtils;
import org.cellocad.v2.common.stage.runtime.environment.StageArgString;
import org.cellocad.v2.common.target.data.TargetData;
import org.cellocad.v2.common.target.data.TargetDataUtils;
import org.cellocad.v2.results.common.Results;
import org.cellocad.v2.results.netlist.Netlist;
import org.cellocad.v2.results.netlist.NetlistUtils;
import org.cellocad.v2.technologyMapping.runtime.TMRuntimeObject;
import org.cellocad.v2.technologyMapping.runtime.environment.TMRuntimeEnv;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Integration test for the {@link SimulatedAnnealing} algorithm.
 *
 * @author Timothy Jones
 *
 * @date 2020-01-30
 */
public class SimulatedAnnealingIT {

  /**
   * Environment setup for tests.
   * 
   * @throws CelloException Unable to initialize supporting classes.
   * @throws IOException    Unable to load resources.
   */
  @BeforeClass
  public static void init() throws CelloException, IOException {
    final Path dir = Files.createTempDirectory("cello_");
    output = dir.toFile();
    final String[] args = {"-" + ArgString.INPUTNETLIST,
        Utils.getResource("and_netlist.json").getFile(), "-" + ArgString.USERCONSTRAINTSFILE,
        Utils.getResource("lib/ucf/Eco/Eco1C1G1T1.UCF.json").getFile(),
        "-" + ArgString.INPUTSENSORFILE,
        Utils.getResource("lib/input/Eco/Eco1C1G1T1.input.json").getFile(),
        "-" + ArgString.OUTPUTDEVICEFILE,
        Utils.getResource("lib/output/Eco/Eco1C1G1T1.output.json").getFile(),
        "-" + StageArgString.ALGORITHMNAME, "SimulatedAnnealing", "-" + ArgString.PYTHONENV,
        "python", "-" + ArgString.OUTPUTDIR, dir.toString(), "-" + ArgString.LOGFILENAME,
        dir.toString() + Utils.getFileSeparator() + "log.log"};
    final TMRuntimeEnv runEnv = new TMRuntimeEnv(args);
    runEnv.setName("technologyMapping");
    // InputFile
    final String inputFilePath = runEnv.getOptionValue(ArgString.INPUTNETLIST);
    final File inputFile = new File(inputFilePath);
    if (!(inputFile.exists() && !inputFile.isDirectory())) {
      throw new CelloException("Input file does not exist!");
    }
    // Read Netlist
    final Netlist netlist = NetlistUtils.getNetlist(runEnv, ArgString.INPUTNETLIST);
    if (!netlist.isValid()) {
      throw new CelloException("Netlist is invalid!");
    }
    // get Stage
    final Stage stage = StageUtils.getStage(runEnv, StageArgString.ALGORITHMNAME);
    stage.setName("technologyMapping");
    final String stageName = runEnv.getOptionValue(StageArgString.STAGENAME);
    if (stageName != null) {
      stage.setName(stageName);
    }
    // get TargetData
    final TargetData td = TargetDataUtils.getTargetTargetData(runEnv, ArgString.USERCONSTRAINTSFILE,
        ArgString.INPUTSENSORFILE, ArgString.OUTPUTDEVICEFILE);
    if (!td.isValid()) {
      throw new CelloException("TargetData is invalid!");
    }
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
    tm = new TMRuntimeObject(stage, td, netlistConstraint, netlist, results, runEnv);
    tm.setName("technologyMapiing");
  }

  @Test
  public void SimulatedAnenaling_MockDesign_ShouldReturn() throws CelloException, IOException {
    tm.execute();
    FileUtils.deleteDirectory(output);
  }

  private static TMRuntimeObject tm;
  private static File output;

}

/*
 * Copyright (C) 2018-2020 Boston University (BU)
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

package org.cellocad.v2.export.runtime;

import java.io.File;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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
import org.cellocad.v2.export.runtime.environment.EXRuntimeEnv;
import org.cellocad.v2.results.common.Results;
import org.cellocad.v2.results.netlist.Netlist;
import org.cellocad.v2.results.netlist.NetlistUtils;

/**
 * The Main class is the executable class for the <i>export</i> stage.
 *
 * @author Timothy Jones
 *
 * @date 2018-06-04
 */
public class Main {

  /**
   * The <i>main</i> method is the executable for the <i>export</i> stage.
   *
   * @param args command line argument(s).
   */
  public static void main(final String[] args) throws CelloException {
    final EXRuntimeEnv runEnv = new EXRuntimeEnv(args);
    runEnv.setName("export");
    // Setup Logger
    Main.setupLogger(runEnv);
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
    stage.setName("export");
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
    final EXRuntimeObject EX =
        new EXRuntimeObject(stage, td, netlistConstraint, netlist, results, runEnv);
    EX.setName("export");
    EX.execute();
    // Write Netlist
    String outputFilename = runEnv.getOptionValue(ArgString.OUTPUTNETLIST);
    if (outputFilename == null) {
      outputFilename = "";
      outputFilename += runEnv.getOptionValue(ArgString.OUTPUTDIR);
      outputFilename += Utils.getFileSeparator();
      outputFilename += Utils.getFilename(inputFilePath);
      outputFilename += "_outputNetlist";
      outputFilename += ".json";
    }
    NetlistUtils.writeJsonForNetlist(netlist, outputFilename);
  }

  /**
   * Setup the logger using the EXRuntimeEnv defined by parameter {@code runEnv}.
   *
   * @param runEnv The EXRuntimeEnv.
   */
  protected static void setupLogger(final EXRuntimeEnv runEnv) {
    String logfile = runEnv.getOptionValue(ArgString.LOGFILENAME);
    if (logfile == null) {
      logfile = "log.log";
    }
    logfile = runEnv.getOptionValue(ArgString.OUTPUTDIR) + Utils.getFileSeparator() + logfile;
    // the logger will write to the specified file
    System.setProperty("logfile.name", logfile);
    Main.logger = LogManager.getLogger(Main.class);
  }

  /**
   * Returns the {@link Logger}.
   *
   * @return The {@link Logger}.
   */
  protected static Logger getLogger() {
    return Main.logger;
  }

  private static Logger logger;

}

/*
 * Copyright (C) 2017-2020 Massachusetts Institute of Technology (MIT), Boston University (BU)
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

package org.cellocad.v2.DNACompiler.runtime;

import java.io.File;
import java.io.IOException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cellocad.v2.DNACompiler.common.DNACompilerUtils;
import org.cellocad.v2.DNACompiler.runtime.environment.DNACompilerRuntimeEnv;
import org.cellocad.v2.common.Utils;
import org.cellocad.v2.common.application.ApplicationConfiguration;
import org.cellocad.v2.common.application.ApplicationUtils;
import org.cellocad.v2.common.exception.CelloException;
import org.cellocad.v2.common.file.dot.utils.DotUtils;
import org.cellocad.v2.common.netlistConstraint.data.NetlistConstraint;
import org.cellocad.v2.common.netlistConstraint.data.NetlistConstraintUtils;
import org.cellocad.v2.common.runtime.environment.ArgString;
import org.cellocad.v2.common.stage.Stage;
import org.cellocad.v2.common.target.data.TargetData;
import org.cellocad.v2.common.target.data.TargetDataUtils;
import org.cellocad.v2.export.runtime.EXRuntimeObject;
import org.cellocad.v2.logicSynthesis.runtime.LSRuntimeObject;
import org.cellocad.v2.placing.runtime.PLRuntimeObject;
import org.cellocad.v2.results.common.Results;
import org.cellocad.v2.results.common.ResultsUtils;
import org.cellocad.v2.results.logicSynthesis.LSResultsStats;
import org.cellocad.v2.results.netlist.Netlist;
import org.cellocad.v2.results.netlist.NetlistUtils;
import org.cellocad.v2.results.partitioning.block.PTBlockNetlist;
import org.cellocad.v2.technologyMapping.runtime.TMRuntimeObject;

/**
 * The Main class is the executable class for the <i>DNACompiler</i> application.
 *
 * @author Vincent Mirian
 * @author Timothy Jones
 * @date 2018-05-21
 */
public class Main {

  /**
   * The <i>printPartitioningGraphs</i> prints the partitioning graph of netlist defined by
   * parameter {@code myNetlist} using the DNACompilerRuntimeEnv defined by parameter {@code
   * runEnv}.
   *
   * @param runEnv The DNACompilerRuntimeEnv.
   * @param myNetlist The {@link Netlist}.
   * @throws CelloException an error in printing the partitioning graph.
   */
  protected static void printPartitioningGraphs(
      final DNACompilerRuntimeEnv runEnv, final Netlist myNetlist) throws CelloException {
    final String outputDir = runEnv.getOptionValue(ArgString.OUTPUTDIR) + Utils.getFileSeparator();
    final PTBlockNetlist ptBlockNetlist = new PTBlockNetlist(myNetlist);
    Netlist netlist = null;
    for (int i = 0; i < ptBlockNetlist.getNetlistFONum(); i++) {
      netlist = ptBlockNetlist.getNetlistFOAtIdx(i);
      final File dotFile = new File(outputDir + netlist.getName() + ".dot");
      NetlistUtils.writeDotFileForGraph(netlist, dotFile.getAbsolutePath());
      DotUtils.dot2pdf(dotFile);
    }
    // TODO:
    // netlist = ptBlockNetlist.getClusterRepeatedEdgesNetlist();
    // NetlistUtils.writeDotFileForGraph(netlist,
    // outputDir+netlist.getName()+".dot");
    // netlist = ptBlockNetlist.getClusterNetlist();
    // NetlistUtils.writeDotFileForGraph(netlist,
    // outputDir+netlist.getName()+".dot");
    netlist = ptBlockNetlist.getVirtualLargeNetlistFO();
    final File dotFile = new File(outputDir + netlist.getName() + ".dot");
    NetlistUtils.writeDotFileForGraph(netlist, dotFile.getAbsolutePath());
    DotUtils.dot2pdf(dotFile);
  }

  /**
   * The executable for the <i>DNACompiler</i> application.
   *
   * @param args Command line argument(s).
   * @throws IOException If an I/O error occurs.
   */
  public static void main(final String[] args) throws CelloException {
    /*
     * Preparation
     */
    // RuntimeEnv
    final DNACompilerRuntimeEnv runEnv = new DNACompilerRuntimeEnv(args);
    runEnv.setName("DNACompiler");
    if (!runEnv.isValid()) {
      throw new RuntimeException("DNACompilerRuntimeEnv is invalid!");
    }
    /*
     * Setup Logger
     */
    Main.setupLogger(runEnv);
    /*
     * Other
     */
    // ApplicationConfiguration
    ApplicationConfiguration appCfg;
    try {
      appCfg =
          ApplicationUtils.getApplicationConfiguration(
              runEnv, ArgString.OPTIONS, DNACompilerUtils.getApplicationConfiguration());
    } catch (final IOException e) {
      throw new RuntimeException("Error with application configuration file.");
    }
    if (!appCfg.isValid()) {
      throw new RuntimeException("ApplicationConfiguration is invalid!");
    }
    // get TargetData
    final TargetData td =
        TargetDataUtils.getTargetTargetData(
            runEnv,
            ArgString.USERCONSTRAINTSFILE,
            ArgString.INPUTSENSORFILE,
            ArgString.OUTPUTDEVICEFILE);
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
    /*
     * Get InputFile from user
     */
    // InputFile
    final String inputFilePath = runEnv.getOptionValue(ArgString.INPUTNETLIST);
    final File inputFile = new File(inputFilePath);
    if (!(inputFile.exists() && !inputFile.isDirectory())) {
      throw new CelloException("Input file does not exist!");
    }
    // Netlist
    Netlist netlist = new Netlist();
    // Input from User
    netlist.setInputFilename(inputFilePath);
    /*
     * Stages
     */
    Stage currentStage = null;
    /*
     * logicSynthesis
     */
    currentStage = appCfg.getStageByName("logicSynthesis");
    final LSRuntimeObject LS =
        new LSRuntimeObject(currentStage, td, netlistConstraint, netlist, results, runEnv);
    LS.execute();
    // Write netlist
    Main.writeJsonForNetlist(runEnv, netlist, inputFilePath);
    ResultsUtils.writeNetlistResults(LS.getName(), outputDir, netlist, results);
    Main.getLogger().info(LSResultsStats.getLogicSynthesisStats(netlist));
    // logicOptimization
    // currentStage = appCfg.getStageByName("logicOptimization");
    // LORuntimeObject LO = new LORuntimeObject(currentStage, td, netlistConstraint,
    // netlist, runEnv);
    // LO.execute();
    // File loDotFile = new File(outputDir, netlist.getName() +
    // "_logicOptimization" + ".dot");
    // NetlistUtils.writeDotFileForGraph(netlist, loDotFile.getAbsolutePath());
    // Dot2Pdf.dot2pdf(loDotFile);
    // Main.getLogger().info(LOResultsStats.getLogicOptimizationStats(netlist));
    // clustering
    // currentStage = appCfg.getStageByName("clustering");
    // CLRuntimeObject CL = new CLRuntimeObject(currentStage, td, netlistConstraint,
    // netlist, runEnv);
    // CL.execute();
    // partitioning
    // currentStage = appCfg.getStageByName("partitioning");
    // PTRuntimeObject PT = new PTRuntimeObject(currentStage, td, netlistConstraint,
    // netlist, runEnv);
    // PT.execute();
    // Main.printPartitioningGraphs(runEnv, netlist);
    // Main.getLogger().info(PTResultsStats.getPartitioningStats(netlist));
    // netlist = new PTBlockNetlist(netlist).getVirtualLargeNetlistFO();
    // Write netlist
    // Main.writeJsonForNetlist(runEnv, netlist, inputFilePath);
    /*
     * technologyMapping
     */
    currentStage = appCfg.getStageByName("technologyMapping");
    final TMRuntimeObject TM =
        new TMRuntimeObject(currentStage, td, netlistConstraint, netlist, results, runEnv);
    TM.execute();
    // Write netlist
    Main.writeJsonForNetlist(runEnv, netlist, inputFilePath);
    ResultsUtils.writeNetlistResults(TM.getName(), outputDir, netlist, results);
    /*
     * placing
     */
    currentStage = appCfg.getStageByName("placing");
    final PLRuntimeObject PL =
        new PLRuntimeObject(currentStage, td, netlistConstraint, netlist, results, runEnv);
    PL.execute();
    // Write netlist
    Main.writeJsonForNetlist(runEnv, netlist, inputFilePath);
    ResultsUtils.writeNetlistResults(PL.getName(), outputDir, netlist, results);
    /*
     * export.
     */
    currentStage = appCfg.getStageByName("export");
    final EXRuntimeObject EX =
        new EXRuntimeObject(currentStage, td, netlistConstraint, netlist, results, runEnv);
    EX.execute();
    // Write netlist
    Main.writeJsonForNetlist(runEnv, netlist, inputFilePath);
    ResultsUtils.writeNetlistResults(EX.getName(), outputDir, netlist, results);
  }

  protected static void writeJsonForNetlist(
      final DNACompilerRuntimeEnv runEnv, final Netlist netlist, final String inputFilePath) {
    String outputNetlistFilePath = null;
    outputNetlistFilePath = runEnv.getOptionValue(ArgString.OUTPUTNETLIST);
    if (outputNetlistFilePath == null) {
      outputNetlistFilePath = "";
      outputNetlistFilePath += runEnv.getOptionValue(ArgString.OUTPUTDIR);
      outputNetlistFilePath += Utils.getFileSeparator();
      outputNetlistFilePath += Utils.getFilename(inputFilePath);
      outputNetlistFilePath += "_outputNetlist";
      outputNetlistFilePath += ".json";
    }
    NetlistUtils.writeJsonForNetlist(netlist, outputNetlistFilePath);
  }

  /**
   * Setup the logger using the DNACompilerRuntimeEnv defined by parameter {@code runEnv}.
   *
   * @param runEnv The DNACompilerRuntimeEnv.
   */
  protected static void setupLogger(final DNACompilerRuntimeEnv runEnv) {
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

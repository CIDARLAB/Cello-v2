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

package org.cellocad.v2.logicSynthesis.algorithm.Yosys;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.StringTokenizer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cellocad.v2.common.ExecCommand;
import org.cellocad.v2.common.Utils;
import org.cellocad.v2.common.exception.CelloException;
import org.cellocad.v2.common.runtime.environment.ArgString;
import org.cellocad.v2.logicSynthesis.algorithm.LSAlgorithm;
import org.cellocad.v2.logicSynthesis.algorithm.Yosys.data.YosysDataUtils;
import org.cellocad.v2.logicSynthesis.algorithm.Yosys.data.YosysNetlistData;
import org.cellocad.v2.logicSynthesis.algorithm.Yosys.data.YosysNetlistEdgeData;
import org.cellocad.v2.logicSynthesis.algorithm.Yosys.data.YosysNetlistNodeData;
import org.cellocad.v2.logicSynthesis.netlist.OutputOrTransform;
import org.cellocad.v2.logicSynthesis.target.data.LSTargetDataInstance;
import org.cellocad.v2.results.logicSynthesis.LSResultsUtils;
import org.cellocad.v2.results.netlist.Netlist;
import org.cellocad.v2.results.netlist.NetlistEdge;
import org.cellocad.v2.results.netlist.NetlistNode;
import org.json.JSONException;
import org.json.simple.JSONArray;

/**
 * The implementation of the <i>Yosys</i> algorithm in the <i>logicSynthesis</i> stage.
 *
 * @author Vincent Mirian
 * @author Timothy Jones
 * @date 2018-05-21
 */
public class Yosys extends LSAlgorithm {

  private LSTargetDataInstance targetDataInstance;
  private String yosysScriptFilename;
  private String yosysEdifFilename;
  private String yosysJsonFilename;
  private String yosysDotFilename;
  private String yosysExec;

  private static String S_HEADER_FOOTER = "+-----------------------------------------------------";
  private static String S_HEADER_LINE_PREFIX = "|";
  private static String S_RESULT = "    RESULTS";
  private static int S_TAB_NUM = 3;
  private static String S_ABC_RESULT = "ABC RESULTS";
  private static String S_GATES_DELIM = ",";
  private static boolean B_CLEANUP = false;

  /**
   * Returns the {@link YosysNetlistNodeData} of the given node.
   *
   * @param node A node within the netlist of this instance.
   * @return The {@link YosysNetlistNodeData} instance if it exists, null otherwise.
   */
  protected YosysNetlistNodeData getYosysNetlistNodeData(final NetlistNode node) {
    YosysNetlistNodeData rtn = null;
    rtn = (YosysNetlistNodeData) node.getNetlistNodeData();
    return rtn;
  }

  /**
   * Returns the {@link YosysNetlistEdgeData} of the given edge.
   *
   * @param edge An edge within the netlist of this instance.
   * @return The {@link YosysNetlistEdgeData} instance if it exists, null otherwise.
   */
  protected YosysNetlistEdgeData getYosysNetlistEdgeData(final NetlistEdge edge) {
    YosysNetlistEdgeData rtn = null;
    rtn = (YosysNetlistEdgeData) edge.getNetlistEdgeData();
    return rtn;
  }

  /**
   * Returns the {@link YosysNetlistData} of the given netlist.
   *
   * @param netlist The netlist of this instance.
   * @return The {@link YosysNetlistData} instance if it exists, null otherwise.
   */
  protected YosysNetlistData getYosysNetlistData(final Netlist netlist) {
    YosysNetlistData rtn = null;
    rtn = (YosysNetlistData) netlist.getNetlistData();
    return rtn;
  }

  /** Gets the constraint data from the netlist constraint file. */
  @Override
  protected void getConstraintFromNetlistConstraintFile() {}

  /**
   * Gets the data from the UCF.
   *
   * @throws CelloException Unable to get data from UCF.
   */
  @Override
  protected void getDataFromUcf() throws CelloException {
    final LSTargetDataInstance tdi = new LSTargetDataInstance(getTargetData());
    setTargetDataInstance(tdi);
  }

  /** Set parameter values of the algorithm. */
  @Override
  protected void setParameterValues() {
    Boolean present = true;
    present = getAlgorithmProfile().getBooleanParameter("NetSynth").getFirst();
    if (present) {
      setNetSynth(getAlgorithmProfile().getBooleanParameter("NetSynth").getSecond());
    }
  }

  /** Validate parameter value for <i>Gates</i>. */
  protected void validateGatesParameterValues() {
    final List<String> validGates = new ArrayList<>(Arrays.asList(YosysUtils.ValidGates));
    final StringTokenizer strtok = new StringTokenizer(getGates(), Yosys.S_GATES_DELIM);
    while (strtok.hasMoreTokens()) {
      final String token = strtok.nextToken();
      if (!validGates.contains(token)) {
        logError(token + " is not a valid value for parameter Gates!");
        Utils.exit(-1);
      }
    }
  }

  /** Validate parameter values of the algorithm. */
  @Override
  protected void validateParameterValues() {
    validateGatesParameterValues();
  }

  private String getGates() {
    String rtn = null;
    final Collection<String> types = new ArrayList<>();
    for (final String str : getTargetDataInstance().getLogicConstraints().getAvailableGates()) {
      if (LSResultsUtils.isValidNodeTypes(str)) {
        types.add(str);
      }
    }
    rtn = String.join(",", types);
    return rtn;
  }

  /** Perform preprocessing. */
  @Override
  protected void preprocessing() {
    final String outputDir = getRuntimeEnv().getOptionValue(ArgString.OUTPUTDIR);
    final String inputFilename = getNetlist().getInputFilename();
    final String filename = Utils.getFilename(inputFilename);
    setYosysScriptFilename(outputDir + Utils.getFileSeparator() + filename + ".ys");
    setYosysDotFilename(outputDir + Utils.getFileSeparator() + filename + "_yosys.dot");
    setYosysEdifFilename(outputDir + Utils.getFileSeparator() + filename + ".edif");
    setYosysJsonFilename(outputDir + Utils.getFileSeparator() + filename + ".json");
    // exec
    String exec = "";
    exec += "yosys";
    if (Utils.isWin()) {
      exec += ".exe";
    }
    exec += " -s ";
    setYosysExec(exec);
    // create Yosys script
    String script = "";
    // read_verilog
    script += "read_verilog ";
    script += inputFilename;
    script += Utils.getNewLine();
    // flatten
    script += "flatten";
    script += Utils.getNewLine();
    // splitnets
    script += "splitnets -ports";
    script += Utils.getNewLine();
    // hierarchy
    script += "hierarchy -auto-top";
    script += Utils.getNewLine();
    // proc
    script += "proc";
    script += Utils.getNewLine();
    // techmap
    script += "techmap";
    script += Utils.getNewLine();
    // opt
    script += "opt";
    script += Utils.getNewLine();
    // abc
    script += "abc -g ";
    script += getGates();
    script += Utils.getNewLine();
    // opt
    script += "opt";
    script += Utils.getNewLine();
    // hierarchy
    script += "hierarchy -auto-top";
    script += Utils.getNewLine();
    // opt_clean
    script += "opt_clean -purge";
    script += Utils.getNewLine();
    // show
    script += "show -format pdf -prefix ";
    script += outputDir;
    script += Utils.getFileSeparator();
    script += filename;
    script += "_yosys";
    script += Utils.getNewLine();
    // write
    script += "write_edif ";
    script += getYosysEdifFilename();
    script += Utils.getNewLine();
    // write
    script += "write_json ";
    script += getYosysJsonFilename();
    script += Utils.getNewLine();
    // write Yosys script
    try {
      final OutputStream outputStream = new FileOutputStream(getYosysScriptFilename());
      final Writer outputStreamWriter = new OutputStreamWriter(outputStream);
      outputStreamWriter.write(script);
      outputStreamWriter.close();
      outputStream.close();
    } catch (final IOException e) {
      e.printStackTrace();
    }
  }

  /** Run the (core) algorithm. */
  @Override
  protected void run() {
    final ExecCommand proc =
        Utils.executeAndWaitForCommand(getYosysExec() + getYosysScriptFilename());
    this.getResults(proc);
    logInfo(proc.getOutput());
    logInfo(proc.getError());
  }

  /**
   * Perform postprocessing.
   *
   * @throws CelloException Unable to perform postprocessing.
   */
  @Override
  protected void postprocessing() throws CelloException {
    // YosysEdifUtils.convertEdifToNetlist(this, this.getYosysEdifFilename(),
    // this.getNetlist());
    YosysJsonUtils.getNetlistFromYosysJsonFile(this, getYosysJsonFilename(), getNetlist());
    // delete
    if (Yosys.B_CLEANUP) {
      Utils.deleteFilename(getYosysDotFilename());
      Utils.deleteFilename(getYosysEdifFilename());
      Utils.deleteFilename(getYosysJsonFilename());
      Utils.deleteFilename(getYosysScriptFilename());
    }
    if (getNetSynth()) {
      final String outputDir = getRuntimeEnv().getOptionValue(ArgString.OUTPUTDIR);
      final JSONArray motifs = YosysDataUtils.getMotifLibrary(getTargetData());
      Netlist n = null;
      try {
        n = NetSynthUtils.getNetSynthNetlist(getNetlist(), motifs, outputDir);
      } catch (JSONException | IOException e) {
        throw new CelloException(e);
      }
      getNetlist().clear();
      for (int i = 0; i < n.getNumVertex(); i++) {
        final NetlistNode node = n.getVertexAtIdx(i);
        getNetlist().addVertex(node);
      }
      for (int i = 0; i < n.getNumEdge(); i++) {
        final NetlistEdge node = n.getEdgeAtIdx(i);
        getNetlist().addEdge(node);
      }
    } else {
      new OutputOrTransform(getNetlist());
    }
  }

  /**
   * Setter for {@code netSynth}.
   *
   * @param value The value to set {@code netSynth}.
   */
  protected void setNetSynth(final Boolean value) {
    netSynth = value;
  }

  /**
   * Getter for {@code netSynth}.
   *
   * @return The value of {@code netSynth}.
   */
  protected Boolean getNetSynth() {
    return netSynth;
  }

  private Boolean netSynth;

  /**
   * Returns the {@link Logger} for the <i>Yosys</i> algorithm.
   *
   * @return The {@link Logger} for the <i>Yosys</i> algorithm.
   */
  @Override
  protected Logger getLogger() {
    return Yosys.logger;
  }

  private static final Logger logger = LogManager.getLogger(Yosys.class);

  /*
   * Log
   */
  /** Logs the Result header. */
  protected void logResultHeader() {
    logInfo(Yosys.S_HEADER_FOOTER);
    logInfo(
        Yosys.S_HEADER_LINE_PREFIX + Utils.getTabCharacterRepeat(Yosys.S_TAB_NUM) + Yosys.S_RESULT);
    logInfo(Yosys.S_HEADER_FOOTER);
  }

  /**
   * Logs the Results from the execution of the Yosys tool by the process defined in parameter
   * {@code proc}.
   *
   * @param proc The process.
   */
  protected void getResults(final ExecCommand proc) {
    logResultHeader();
    final StringTokenizer strtok = new StringTokenizer(proc.getOutput(), Utils.getNewLine());
    while (strtok.hasMoreTokens()) {
      final String token = strtok.nextToken();
      if (token.contains(Yosys.S_ABC_RESULT)) {
        logInfo(token.replaceFirst(Yosys.S_ABC_RESULT, "").trim());
      }
    }
    logInfo(Yosys.S_HEADER_FOOTER);
  }

  /*
   * Getter and Setter
   */
  /**
   * Setter for {@code yosysScriptFilename}.
   *
   * @param str The value to set {@code yosysScriptFilename}.
   */
  protected void setYosysScriptFilename(final String str) {
    yosysScriptFilename = str;
  }

  /**
   * Getter for {@code yosysScriptFilename}.
   *
   * @return The value of {@code yosysScriptFilename}.
   */
  protected String getYosysScriptFilename() {
    return yosysScriptFilename;
  }

  /**
   * Setter for {@code yosysEdifFilename}.
   *
   * @param str The value to set {@code yosysEdifFilename}.
   */
  protected void setYosysEdifFilename(final String str) {
    yosysEdifFilename = str;
  }

  /**
   * Getter for {@code yosysEdifFilename}.
   *
   * @return The value of {@code yosysEdifFilename}.
   */
  protected String getYosysEdifFilename() {
    return yosysEdifFilename;
  }

  /**
   * Setter for {@code yosysJsonFilename}.
   *
   * @param str The value to set {@code yosysJsonFilename}.
   */
  protected void setYosysJsonFilename(final String str) {
    yosysJsonFilename = str;
  }

  /**
   * Getter for {@code yosysJsonFilename}.
   *
   * @return The value of {@code yosysJsonFilename}.
   */
  protected String getYosysJsonFilename() {
    return yosysJsonFilename;
  }

  /**
   * Setter for {@code yosysDotFilename}.
   *
   * @param str The value to set {@code yosysDotFilename}.
   */
  protected void setYosysDotFilename(final String str) {
    yosysDotFilename = str;
  }

  /**
   * Getter for {@code yosysDotFilename}.
   *
   * @return The value of {@code yosysDotFilename}.
   */
  protected String getYosysDotFilename() {
    return yosysDotFilename;
  }

  /**
   * Setter for {@code yosysExec}.
   *
   * @param str The value to set {@code yosysExec}.
   */
  protected void setYosysExec(final String str) {
    yosysExec = str;
  }

  /**
   * Getter for {@code yosysExec}.
   *
   * @return The value of {@code yosysExec}.
   */
  protected String getYosysExec() {
    return yosysExec;
  }

  /**
   * Getter for {@code targetDataInstance}.
   *
   * @return The value of {@code targetDataInstance}.
   */
  protected LSTargetDataInstance getTargetDataInstance() {
    return targetDataInstance;
  }

  /**
   * Setter for {@code targetDataInstance}.
   *
   * @param targetDataInstance The targetDataInstance to set.
   */
  protected void setTargetDataInstance(final LSTargetDataInstance targetDataInstance) {
    this.targetDataInstance = targetDataInstance;
  }
}

/*
 * Copyright (C) 2017 Massachusetts Institute of Technology (MIT)
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

package org.cellocad.v2.partitioning.algorithm.GPCC_SCIP_BASE;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cellocad.v2.common.CelloException;
import org.cellocad.v2.common.Utils;
import org.cellocad.v2.common.runtime.environment.ArgString;
import org.cellocad.v2.partitioning.algorithm.GPCC_BASE.GpccBase;
import org.cellocad.v2.partitioning.algorithm.GPCC_SCIP_BASE.data.GpccScipBaseNetlistData;
import org.cellocad.v2.partitioning.algorithm.GPCC_SCIP_BASE.data.GpccScipBaseNetlistEdgeData;
import org.cellocad.v2.partitioning.algorithm.GPCC_SCIP_BASE.data.GpccScipBaseNetlistNodeData;
import org.cellocad.v2.results.netlist.Netlist;
import org.cellocad.v2.results.netlist.NetlistEdge;
import org.cellocad.v2.results.netlist.NetlistNode;

/**
 * The implementation of the <i>GPCC_SCIP_BASE</i> algorithm in the <i>partitioning</i> stage.
 *
 * @author Vincent Mirian
 *
 * @date 2018-05-21
 */
public class GpccScipBase extends GpccBase {

  /**
   * Returns the {@link GpccScipBaseNetlistNodeData} of the given node.
   *
   * @param node A node within the netlist of this instance.
   * @return The {@link GpccScipBaseNetlistNodeData} instance if it exists, null otherwise.
   */
  protected GpccScipBaseNetlistNodeData getGpccScipBaseNetlistNodeData(final NetlistNode node) {
    GpccScipBaseNetlistNodeData rtn = null;
    rtn = (GpccScipBaseNetlistNodeData) node.getNetlistNodeData();
    return rtn;
  }

  /**
   * Returns the {@link GpccScipBaseNetlistEdgeData} of the given edge.
   *
   * @param edge An edge within the netlist of this instance.
   * @return The {@link GpccScipBaseNetlistEdgeData} instance if it exists, null otherwise.
   */
  protected GpccScipBaseNetlistEdgeData getGpccScipBaseNetlistEdgeData(final NetlistEdge edge) {
    GpccScipBaseNetlistEdgeData rtn = null;
    rtn = (GpccScipBaseNetlistEdgeData) edge.getNetlistEdgeData();
    return rtn;
  }

  /**
   * Returns the {@link GpccScipBaseNetlistData} of the given netlist.
   *
   * @param netlist The netlist of this instance.
   * @return The {@link GpccScipBaseNetlistData} instance if it exists, null otherwise.
   */
  protected GpccScipBaseNetlistData getGpccScipBaseNetlistData(final Netlist netlist) {
    GpccScipBaseNetlistData rtn = null;
    rtn = (GpccScipBaseNetlistData) netlist.getNetlistData();
    return rtn;
  }

  /**
   * Gets the constraint data from the netlist constraint file.
   */
  @Override
  protected void getConstraintFromNetlistConstraintFile() {

  }

  /**
   * Gets the data from the UCF.
   */
  @Override
  protected void getDataFromUcf() {
    super.getDataFromUcf();
  }

  /**
   * Set parameter values of the algorithm.
   */
  @Override
  protected void setParameterValues() {
    super.setParameterValues();
  }

  /**
   * Validate parameter values of the algorithm.
   */
  @Override
  protected void validateParameterValues() {
    super.validateParameterValues();
  }

  /**
   * Write SCIP Script File.
   */
  private void writeScipScriptFile() {
    // create SCIP script
    String script = "";
    final int numVertex = getNetlist().getNumVertex();
    double ratio = numVertex / 1000.0;
    if (ratio > 1) {
      ratio = 1.0;
    }
    // read test.lp display problem
    script += "read ";
    script += getScipConstraintFilename();
    // script += " display problem";
    script += Utils.getNewLine();
    // aggressive
    script += "set heuristics emphasis aggr";
    script += Utils.getNewLine();
    // checkcurvature
    script += "set constraints quadratic checkcurvature false";
    script += Utils.getNewLine();
    // replacebinaryprod
    script += "set constraints quadratic replacebinaryprod 0";
    script += Utils.getNewLine();
    // presolving inttobinary maxrounds
    script += "set presolving inttobinary maxrounds ";
    int scale = 0;
    scale = 10;
    scale = (int) Math.ceil(ratio * scale);
    if (scale < 5) {
      scale = 5;
    }
    script += scale;
    script += Utils.getNewLine();
    // limits solutions
    script += "set limits solutions ";
    scale = 7;
    scale = (int) Math.ceil(ratio * scale);
    if (scale < 1) {
      scale = 1;
    }
    // TODO
    // script += scale;
    script += 1;
    script += Utils.getNewLine();
    // limits time
    /*
     * script += "set limits time ";//259200 = 72 hours scale = 3600; script += (int) scale +
     * (Math.round(ratio * scale)); // script += (int) (Math.round(ratio * ratio * scale) +
     * Math.round(ratio * 250)); script += Utils.getNewLine();
     */
    // limits nodes
    /*
     * script += "set limits nodes "; scale = 30; scale = (int) Math.ceil(ratio * scale); if (scale
     * < 7) { scale = 7; } script += scale; script += Utils.getNewLine();.
     */
    // optimize
    script += "optimize";
    script += Utils.getNewLine();
    // display solution
    // script += "display solution";
    // script += Utils.getNewLine();
    // write solution
    script += "write solution ";
    script += getScipOutputFilename();
    script += Utils.getNewLine();
    // quit
    script += "quit";
    // write SCIP script
    try {
      final OutputStream outputStream = new FileOutputStream(getScipScriptFilename());
      final Writer outputStreamWriter = new OutputStreamWriter(outputStream);
      outputStreamWriter.write(script);
      outputStreamWriter.close();
      outputStream.close();
    } catch (final IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * Setup Exec.
   */
  private void setupExec() {
    // exec
    String exec = "";
    exec += "scip -b ";
    setScipExec(exec);
  }

  /**
   * Perform preprocessing.
   * 
   * @throws CelloException Unable to perform preprocessing.
   */
  @Override
  protected void preprocessing() throws CelloException {
    super.preprocessing();
    // prepare file
    final String outputDir = getRuntimeEnv().getOptionValue(ArgString.OUTPUTDIR);
    final String inputFilename = getNetlist().getInputFilename();
    final String filename = Utils.getFilename(inputFilename);
    setScipConstraintFilename(
        outputDir + Utils.getFileSeparator() + filename + "_SCIPConstraint.lp");
    setScipScriptFilename(outputDir + Utils.getFileSeparator() + filename + "_SCIPScript");
    setScipOutputFilename(outputDir + Utils.getFileSeparator() + filename + "_SCIPOutput");
    setPartitionDotFile(outputDir + Utils.getFileSeparator() + filename + "_SCIP.dot");
    // SCIPConstraint
    try {
      GpccScipBaseConstraintUtils.writeScipConstraintFile(this, getScipConstraintFilename());
    } catch (IOException e) {
      throw new CelloException(e);
    }
    // exec
    setupExec();
    // SCIPScript
    writeScipScriptFile();
  }

  /**
   * Run the (core) algorithm.
   */
  @Override
  protected void run() {

  }

  /**
   * Perform postprocessing.
   */
  @Override
  protected void postprocessing() {
    super.postprocessing();
  }

  /**
   * Setter for {@code scipConstraintFilename}.
   *
   * @param str The value to set {@code scipConstraintFilename}.
   */
  protected void setScipConstraintFilename(final String str) {
    scipConstraintFilename = str;
  }

  /**
   * Getter for {@code scipConstraintFilename}.
   *
   * @return The value of {@code scipConstraintFilename}.
   */
  protected String getScipConstraintFilename() {
    return scipConstraintFilename;
  }

  /**
   * Setter for {@code scipScriptFilename}.
   *
   * @param str The value to set {@code scipScriptFilename}.
   */
  protected void setScipScriptFilename(final String str) {
    scipScriptFilename = str;
  }

  /**
   * Getter for {@code scipScriptFilename}.
   *
   * @return The value of {@code scipScriptFilename}.
   */
  protected String getScipScriptFilename() {
    return scipScriptFilename;
  }

  /**
   * Setter for {@code scipOutputFilename}.
   *
   * @param str The value to set {@code scipOutputFilename}.
   */
  protected void setScipOutputFilename(final String str) {
    scipOutputFilename = str;
  }

  /**
   * Getter for {@code scipOutputFilename}.
   *
   * @return The value of {@code scipOutputFilename}.
   */
  protected String getScipOutputFilename() {
    return scipOutputFilename;
  }

  /**
   * Setter for {@code partitionDot}.
   *
   * @param str The value to set {@code partitionDot}.
   */
  protected void setPartitionDotFile(final String str) {
    partitionDot = str;
  }

  /**
   * Getter for {@code partitionDot}.
   *
   * @return The value of {@code partitionDot}.
   */
  protected String getPartitionDotFile() {
    return partitionDot;
  }

  private String partitionDot;

  /**
   * Setter for {@code scipExec}.
   *
   * @param str The value to set {@code scipExec}.
   */
  protected void setScipExec(final String str) {
    scipExec = str;
  }

  /**
   * Getter for {@code scipExec}.
   *
   * @return The value of {@code scipExec}.
   */
  protected String getScipExec() {
    return scipExec;
  }

  private String scipConstraintFilename;
  private String scipScriptFilename;
  private String scipOutputFilename;
  private String scipExec;

  /**
   * Returns the {@link Logger} for the <i>GPCC_SCIP_BASE</i> algorithm.
   *
   * @return The {@link Logger} for the <i>GPCC_SCIP_BASE</i> algorithm.
   */
  @Override
  protected Logger getLogger() {
    return GpccScipBase.logger;
  }

  private static final Logger logger = LogManager.getLogger(GpccScipBase.class);

  protected static String S_HEADER_FOOTER =
      "+-----------------------------------------------------";
  protected static String S_HEADER_LINE_PREFIX = "|";
  protected static String S_RESULT = "    RESULTS";
  protected static int S_TAB_NUM = 3;
  /*
   * private static String S_STDOUT = "    STDOUT"; private static String S_STDERR = "    STDERR";
   * private static String S_SOLUTIONSTATUS = "solution status:"; private static String S_INFEASIBLE
   * = "infeasible"; private static String S_ASSIGNTAG = "(obj:0)"; private static boolean B_CLEANUP
   * = false;
   */

}

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

package org.cellocad.v2.partitioning.algorithm.GPCC_SUGARM_BASE;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import jp.kobe_u.sugar.SugarMain;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cellocad.v2.common.CelloException;
import org.cellocad.v2.common.ExecCommand;
import org.cellocad.v2.common.Utils;
import org.cellocad.v2.common.runtime.environment.ArgString;
import org.cellocad.v2.partitioning.algorithm.GPCC_BASE.GPCC_BASE;
import org.cellocad.v2.partitioning.algorithm.GPCC_SUGARM_BASE.data.GPCC_SUGARM_BASENetlistData;
import org.cellocad.v2.partitioning.algorithm.GPCC_SUGARM_BASE.data.GPCC_SUGARM_BASENetlistEdgeData;
import org.cellocad.v2.partitioning.algorithm.GPCC_SUGARM_BASE.data.GPCC_SUGARM_BASENetlistNodeData;
import org.cellocad.v2.partitioning.common.Block;
import org.cellocad.v2.partitioning.common.Move;
import org.cellocad.v2.partitioning.common.Partition;
import org.cellocad.v2.partitioning.common.PartitionUtils;
import org.cellocad.v2.partitioning.netlist.PTNetlistNode;
import org.cellocad.v2.partitioning.netlist.PTNetlistUtils;
import org.cellocad.v2.results.netlist.Netlist;
import org.cellocad.v2.results.netlist.NetlistEdge;
import org.cellocad.v2.results.netlist.NetlistNode;

/**
 * The implementation of the <i>GPCC_SUGARM_BASE</i> algorithm in the <i>partitioning</i> stage.
 *
 * @author Vincent Mirian
 * @date 2018-05-21
 */
public class GPCC_SUGARM_BASE extends GPCC_BASE {

  /**
   * Returns the {@link GPCC_SUGARM_BASENetlistNodeData} of the given node.
   *
   * @param node A node within the netlist of this instance.
   * @return The {@link GPCC_SUGARM_BASENetlistNodeData} instance if it exists, null otherwise.
   */
  protected GPCC_SUGARM_BASENetlistNodeData getGpccSugarMBaseNetlistNodeData(
      final NetlistNode node) {
    GPCC_SUGARM_BASENetlistNodeData rtn = null;
    rtn = (GPCC_SUGARM_BASENetlistNodeData) node.getNetlistNodeData();
    return rtn;
  }

  /**
   * Returns the {@link GPCC_SUGARM_BASENetlistEdgeData} of the given edge.
   *
   * @param edge An edge within the netlist of this instance.
   * @return The {@link GPCC_SUGARM_BASENetlistEdgeData} instance if it exists, null otherwise.
   */
  protected GPCC_SUGARM_BASENetlistEdgeData getGpccSugarMBaseNetlistEdgeData(
      final NetlistEdge edge) {
    GPCC_SUGARM_BASENetlistEdgeData rtn = null;
    rtn = (GPCC_SUGARM_BASENetlistEdgeData) edge.getNetlistEdgeData();
    return rtn;
  }

  /**
   * Returns the {@link GPCC_SUGARM_BASENetlistData} of the given netlist.
   *
   * @param netlist The netlist of this instance.
   * @return The {@link GPCC_SUGARM_BASENetlistData} instance if it exists, null otherwise.
   */
  protected GPCC_SUGARM_BASENetlistData getGpccSugarMBaseNetlistData(final Netlist netlist) {
    GPCC_SUGARM_BASENetlistData rtn = null;
    rtn = (GPCC_SUGARM_BASENetlistData) netlist.getNetlistData();
    return rtn;
  }

  /** Gets the constraint data from the netlist constraint file. */
  @Override
  protected void getConstraintFromNetlistConstraintFile() {}

  /** Gets the data from the UCF. */
  @Override
  protected void getDataFromUcf() {
    super.getDataFromUcf();
  }

  /** Set parameter values of the algorithm. */
  @Override
  protected void setParameterValues() {
    super.setParameterValues();
    Boolean present = true;
    present = getAlgorithmProfile().getIntParameter("LowerBoundValue").getFirst();
    if (present) {
      setLowerBoundValue(getAlgorithmProfile().getIntParameter("LowerBoundValue").getSecond());
    }
    present = getAlgorithmProfile().getIntParameter("UpperBoundValue").getFirst();
    if (present) {
      setUpperBoundValue(getAlgorithmProfile().getIntParameter("UpperBoundValue").getSecond());
    }
    present = getAlgorithmProfile().getIntParameter("Attempts").getFirst();
    if (present) {
      setAttempts(getAlgorithmProfile().getIntParameter("Attempts").getSecond());
    }
    present = getAlgorithmProfile().getIntParameter("Iteration").getFirst();
    if (present) {
      setIteration(getAlgorithmProfile().getIntParameter("Iteration").getSecond());
    }
  }

  /** Validate parameter values of the algorithm. */
  @Override
  protected void validateParameterValues() {
    super.validateParameterValues();
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
    setCspFilename(outputDir + Utils.getFileSeparator() + filename + "_SUGARM.csp");
    setCnfFilename(outputDir + Utils.getFileSeparator() + filename + "_SUGARM.cnf");
    setMapFilename(outputDir + Utils.getFileSeparator() + filename + "_SUGARM.map");
    setOutFilename(outputDir + Utils.getFileSeparator() + filename + "_SUGARM.out");
    setPartitionDotFile(outputDir + Utils.getFileSeparator() + filename + "_SUGARM.dot");
    // exec
    String exec = "";
    exec += "minisat ";
    setExec(exec);
  }

  /**
   * Run the (core) algorithm.
   *
   * @throws CelloException Unable to write file.
   */
  @Override
  protected void run() throws CelloException {
    int temp = 0;
    final int attempts = getAttempts();
    final int iteration = getIteration();
    // lower
    int lower = 1;
    temp = getLowerBoundValue();
    if (temp > lower && temp >= 0) {
      lower = temp;
    }
    // upper
    int upper = getBlockList().size();
    temp = getUpperBoundValue();
    if (temp < upper && temp >= 0) {
      upper = temp;
    }
    // other
    int currentBlockNum = 0;
    int bestBlockNum = -1;
    final GPCC_SUGARM_BASEPWriter writer =
        new GPCC_SUGARM_BASEPWriter(this, getCspFilename(), currentBlockNum);
    // TODO: open window of binary search
    // TODO: enable random point in range
    // binary search for optimal numBlocks
    int numAttempts = 0;
    int numIteration = 0;
    while (lower <= upper // binary search
        &&
        // attempts
        (attempts < 0 || attempts >= 0 && numAttempts < attempts)
        &&
        // iteration
        (iteration < 0 || iteration >= 0 && numIteration < iteration)) {
      // get middle point
      currentBlockNum = Double.valueOf(Math.floor((lower + upper) / 2.0)).intValue();
      writer.setLowerBoundBlocks(currentBlockNum);
      writer.setUpperBoundBlocks(currentBlockNum);
      logInfo(GPCC_SUGARM_BASE.S_HEADER_FOOTER);
      logInfo("Attempting with " + currentBlockNum + " blocks.");
      logInfo(GPCC_SUGARM_BASE.S_HEADER_FOOTER);
      // write info
      try {
        writer.write();
      } catch (IOException e) {
        throw new CelloException(e);
      }
      // encode
      logInfo("Encoding...");
      final String[] argsEnc = {
        GPCC_SUGARM_BASE.S_V,
        GPCC_SUGARM_BASE.S_V,
        GPCC_SUGARM_BASE.S_ENCODE,
        getCspFilename(),
        getCnfFilename(),
        getMapFilename()
      };
      SugarMain.main(argsEnc);
      // minisat
      logInfo("Solving...");
      final ExecCommand proc =
          Utils.executeAndWaitForCommand(getExec() + getCnfFilename() + " " + getOutFilename());
      logInfo(proc.getOutput());
      // is unsat, adjust lower and upper
      if (isUnsat(proc.getOutput())) {
        lower = currentBlockNum + 1;
      } else {
        bestBlockNum = currentBlockNum;
        upper = currentBlockNum - 1;
        // decode
        decode();
        // assign
        logInfo("Assigning...");
        applyResult();
        final String outputDir = getRuntimeEnv().getOptionValue(ArgString.OUTPUTDIR);
        final Partition P = getPartitioner().getPartition();
        PartitionUtils.writeDotFileForPartition(
            P,
            outputDir
                + Utils.getFileSeparator()
                + P.getName()
                + "_blocks_"
                + currentBlockNum
                + "_final.dot");
        numIteration++;
      }
      numAttempts++;
    }
    if (bestBlockNum < 0) {
      logInfo("GPCC_SUGARM cannot find a solution!");
      Utils.exit(-1);
    }
    logInfo("Using block number is : " + bestBlockNum);
  }

  protected boolean isUnsat(final String str) {
    boolean rtn = false;
    rtn = str.contains(GPCC_SUGARM_BASE.S_UNSATISFIABLE);
    return rtn;
  }

  protected void applyResult() {
    final List<PTNetlistNode> ptCellList = getPTCellList();
    final List<Block> blockList = getBlockList();
    final List<Move> moves = new ArrayList<>();
    Move move = null;
    final Partition partition = getPartitioner().getPartition();
    String line = null;
    final StringTokenizer stk = new StringTokenizer(getOutput(), Utils.getNewLine());
    while (stk.hasMoreElements()) {
      line = stk.nextToken();
      line.trim();
      // status
      if (line.startsWith(GPCC_SUGARM_BASE.S_SOLUTIONMARKER)) {
        logInfo(GPCC_SUGARM_BASE.S_HEADER_LINE_PREFIX + GPCC_SUGARM_BASE.S_SPACE + line);
        if (line.contains(GPCC_SUGARM_BASE.S_UNSATISFIABLE)) {
          logInfo("GPCC_SUGARM cannot find a solution!");
          Utils.exit(-1);
        }
        continue;
      }
      // assignment
      if (line.startsWith(GPCC_SUGARM_BASE.S_ASSIGNMARKER)) {
        int cellId = -1;
        int blockId = -1;
        final StringTokenizer strtok = new StringTokenizer(line);
        String token = null;
        token = strtok.nextToken();
        String assignment = "";
        int prefixIdx = 0;
        int blkIdx = 0;
        if (strtok.hasMoreTokens()) {
          token = strtok.nextToken();
          prefixIdx = token.indexOf(GPCC_SUGARM_BASE.S_ASSIGN + GPCC_SUGARM_BASE.S_CELL);
          blkIdx = token.indexOf(GPCC_SUGARM_BASE.S_BLK);
          if (prefixIdx == 0 && blkIdx > prefixIdx) {
            assignment = token;
          }
        }
        if (assignment.isEmpty()) {
          continue;
        }
        if (strtok.hasMoreTokens()) {
          token = strtok.nextToken();
          if (Math.round(Double.parseDouble(token)) == 1) {
            cellId =
                Integer.parseInt(
                    assignment.substring(
                        GPCC_SUGARM_BASE.S_ASSIGN.length() + GPCC_SUGARM_BASE.S_CELL.length(),
                        blkIdx));
            blockId =
                Integer.parseInt(assignment.substring(blkIdx + GPCC_SUGARM_BASE.S_BLK.length()));
          }
        }
        if (cellId >= 0 && blockId >= 0) {
          final PTNetlistNode ptnode = ptCellList.get(cellId);
          final Block block = blockList.get(blockId);
          move = new Move(ptnode, ptnode.getMyBlock(), block);
          moves.add(move);
        }
        continue;
      }
    }
    // do moves
    partition.doMoves(moves);
  }

  /** Perform decode. */
  protected void decode() {
    // decode
    logInfo("Decoding...");
    // Create a stream to hold the output
    final ByteArrayOutputStream baos = new ByteArrayOutputStream();
    final PrintStream ps = new PrintStream(baos);
    // Save the old System.out!
    final PrintStream old = System.out;
    // Tell Java to use your special stream
    System.setOut(ps);
    // Run Decode
    final String[] argsDec = {
      GPCC_SUGARM_BASE.S_V,
      GPCC_SUGARM_BASE.S_V,
      GPCC_SUGARM_BASE.S_DECODE,
      getOutFilename(),
      getMapFilename()
    };
    SugarMain.main(argsDec);
    // Put things back
    System.out.flush();
    System.setOut(old);
    // Show what happened
    setOutput(baos.toString());
    logInfo(getOutput());
  }

  /** Perform postprocessing. */
  @Override
  protected void postprocessing() {
    super.postprocessing();
    logResultHeader();
    getNetlister().getNetlist();
    final String outputDir = getRuntimeEnv().getOptionValue(ArgString.OUTPUTDIR);
    final Partition P = getPartitioner().getPartition();
    PartitionUtils.writeDotFileForPartition(
        P, outputDir + Utils.getFileSeparator() + P.getName() + "_final.dot");
    // delete
    if (GPCC_SUGARM_BASE.B_CLEANUP) {
      Utils.deleteFilename(getCspFilename());
      Utils.deleteFilename(getCnfFilename());
      Utils.deleteFilename(getMapFilename());
      Utils.deleteFilename(getOutFilename());
    }
    PTNetlistUtils.writeDotFileForPartition(getNetlister().getPTNetlist(), getPartitionDotFile());
  }

  /** Logs the Result header. */
  protected void logResultHeader() {
    logInfo(GPCC_SUGARM_BASE.S_HEADER_FOOTER);
    logInfo(
        GPCC_SUGARM_BASE.S_HEADER_LINE_PREFIX
            + Utils.getTabCharacterRepeat(GPCC_SUGARM_BASE.S_TAB_NUM)
            + GPCC_SUGARM_BASE.S_RESULT);
    logInfo(GPCC_SUGARM_BASE.S_HEADER_FOOTER);
  }

  /**
   * Setter for {@code cspFilename}.
   *
   * @param str The value to set {@code cspFilename}.
   */
  protected void setCspFilename(final String str) {
    cspFilename = str;
  }

  /**
   * Getter for {@code cspFilename}.
   *
   * @return The value of {@code cspFilename}.
   */
  protected String getCspFilename() {
    return cspFilename;
  }

  private String cspFilename;

  /**
   * Setter for {@code cnfFilename}.
   *
   * @param str The value to set {@code cnfFilename}.
   */
  protected void setCnfFilename(final String str) {
    cnfFilename = str;
  }

  /**
   * Getter for {@code cnfFilename}.
   *
   * @return The value of {@code cnfFilename}.
   */
  protected String getCnfFilename() {
    return cnfFilename;
  }

  private String cnfFilename;

  /**
   * Setter for {@code outFilename}.
   *
   * @param str The value to set {@code outFilename}.
   */
  protected void setOutFilename(final String str) {
    outFilename = str;
  }

  /**
   * Getter for {@code outFilename}.
   *
   * @return The value of {@code outFilename}.
   */
  protected String getOutFilename() {
    return outFilename;
  }

  private String outFilename;

  /**
   * Setter for {@code mapFilename}.
   *
   * @param str The value to set {@code mapFilename}.
   */
  protected void setMapFilename(final String str) {
    mapFilename = str;
  }

  /**
   * Getter for {@code mapFilename}.
   *
   * @return The value of {@code mapFilename}.
   */
  protected String getMapFilename() {
    return mapFilename;
  }

  private String mapFilename;

  /**
   * Setter for {@code exec}.
   *
   * @param str The value to set {@code exec}.
   */
  protected void setExec(final String str) {
    exec = str;
  }

  /**
   * Getter for {@code exec}.
   *
   * @return The value of {@code exec}.
   */
  protected String getExec() {
    return exec;
  }

  private String exec;

  /**
   * Setter for {@code output}.
   *
   * @param str The value to set {@code output}.
   */
  protected void setOutput(final String str) {
    output = str;
  }

  /**
   * Getter for {@code output}.
   *
   * @return The value of {@code output}.
   */
  protected String getOutput() {
    return output;
  }

  private String output;

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
   * Setter for {@code lowerBoundValue}.
   *
   * @param value The value to set {@code lowerBoundValue}.
   */
  protected void setLowerBoundValue(final int value) {
    lowerBoundValue = value;
  }

  /**
   * Getter for {@code lowerBoundValue}.
   *
   * @return The value of {@code lowerBoundValue}.
   */
  protected int getLowerBoundValue() {
    return lowerBoundValue;
  }

  private int lowerBoundValue;

  /**
   * Setter for {@code upperBoundValue}.
   *
   * @param value The value to set {@code upperBoundValue}.
   */
  protected void setUpperBoundValue(final int value) {
    upperBoundValue = value;
  }

  /**
   * Getter for {@code upperBoundValue}.
   *
   * @return The value of {@code upperBoundValue}.
   */
  protected int getUpperBoundValue() {
    return upperBoundValue;
  }

  private int upperBoundValue;

  /**
   * Setter for {@code attempts}.
   *
   * @param value The value to set {@code attempts}.
   */
  protected void setAttempts(final int value) {
    attempts = value;
  }

  /**
   * Getter for {@code attempts}.
   *
   * @return The value of {@code attempts}.
   */
  protected int getAttempts() {
    return attempts;
  }

  private int attempts;

  /**
   * Setter for {@code iteration}.
   *
   * @param value The value to set {@code iteration}.
   */
  protected void setIteration(final int value) {
    iteration = value;
  }

  /**
   * Getter for {@code iteration}.
   *
   * @return The value of {@code iteration}.
   */
  protected int getIteration() {
    return iteration;
  }

  private int iteration;

  /**
   * Returns the {@link Logger} for the <i>GPCC_SUGARM_BASE</i> algorithm.
   *
   * @return The {@link Logger} for the <i>GPCC_SUGARM_BASE</i> algorithm.
   */
  @Override
  protected Logger getLogger() {
    return GPCC_SUGARM_BASE.logger;
  }

  private static final Logger logger = LogManager.getLogger(GPCC_SUGARM_BASE.class);

  private static boolean B_CLEANUP = false;
  static String S_ENCODE = "-encode";
  static String S_DECODE = "-decode";
  static String S_ASSIGNMARKER = "a ";
  static String S_SOLUTIONMARKER = "s ";
  static String S_UNSATISFIABLE = "UNSATISFIABLE";

  static String S_MINIMIZE = "Minimize";
  static String S_SUBJECTTO = "Subject To";
  static String S_BINARY = "Binary";
  static String S_END = "End";
  static String S_OBJECTIVE = "Objective";
  static String S_OBJECTIVEEQUATION = "ObjectiveEquation";
  static String S_CAPACITY = "Capacity";
  protected static String S_ASSIGN = "Assign";
  protected static String S_CELL = "C";
  protected static String S_BLK = "BLK";
  static String S_TO = "TO";
  static String S_UPPER = "UPPER";
  static String S_LOWER = "LOWER";
  static String S_NUMEDGE = "numEdge";
  static String S_HEADER = "\\*************************************";
  static String S_COMMENT = "\\";
  static String S_SQUARE_BRACKET_OPEN = "[";
  static String S_SQUARE_BRACKET_CLOSE = "]";
  static String S_EQUAL = "=";
  static String S_ONE = "1";
  static String S_ADDITION = "+";
  static String S_MULTIPLICATION = "*";
  static String S_COLON = ":";
  protected static String S_SPACE = " ";
  static String S_UNDERSCORE = "_";
  protected static String S_HEADER_FOOTER =
      "+-----------------------------------------------------";
  protected static String S_HEADER_LINE_PREFIX = "|";
  protected static String S_RESULT = "    RESULTS";
  protected static String S_V = "-v";
  protected static int S_TAB_NUM = 3;
}

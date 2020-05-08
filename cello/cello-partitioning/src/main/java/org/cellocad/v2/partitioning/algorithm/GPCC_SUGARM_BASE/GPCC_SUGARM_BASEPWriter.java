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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Set;
import org.cellocad.v2.common.Utils;
import org.cellocad.v2.partitioning.algorithm.GPCC_BASE.GPCC_BASE;
import org.cellocad.v2.partitioning.algorithm.GPCC_BASE.GPCC_BASE_PWriter;
import org.cellocad.v2.partitioning.profile.Capacity;

/**
 * The GPCC_SUGARM_PWriter class implements the problem writer for the <i>GPCC_SUGARM_BASE</i>
 * algorithm.
 *
 * @author Vincent Mirian
 * @date 2018-05-21
 */
public class GPCC_SUGARM_BASEPWriter extends GPCC_BASE_PWriter {

  @Override
  protected String getEdgeEquationCoefficient(final int coefficient) {
    String rtn = "";
    rtn += Integer.toString(coefficient) + GPCC_SUGARM_BASEPWriter.S_SPACE;
    rtn +=
        GPCC_SUGARM_BASEPWriter.S_PARENTHESIS_OPEN
            + GPCC_SUGARM_BASEPWriter.S_MULTIPLICATION
            + GPCC_SUGARM_BASEPWriter.S_SPACE;
    return rtn;
  }

  @Override
  protected String getEdgeEquationBetweenBlocks() {
    return GPCC_SUGARM_BASEPWriter.S_SPACE;
  }

  @Override
  protected String getEdgeEquationAfterBlocks() {
    return GPCC_SUGARM_BASEPWriter.S_PARENTHESIS_CLOSE + GPCC_SUGARM_BASEPWriter.S_SPACE;
  }

  @Override
  protected String getEdgeEquationBeforeTerm() {
    return GPCC_SUGARM_BASEPWriter.S_PARENTHESIS_OPEN
        + GPCC_SUGARM_BASEPWriter.S_MULTIPLICATION
        + GPCC_SUGARM_BASEPWriter.S_SPACE;
  }

  @Override
  protected String getEdgeEquationAfterTerm() {
    return GPCC_SUGARM_BASEPWriter.S_PARENTHESIS_CLOSE + GPCC_SUGARM_BASEPWriter.S_SPACE;
  }

  @Override
  protected String getComment() {
    return GPCC_SUGARM_BASEPWriter.S_COMMENT;
  }

  @Override
  protected String getObjectiveConstraintEdgeName() {
    return GPCC_SUGARM_BASEPWriter.S_OBJECTIVE
        + GPCC_SUGARM_BASEPWriter.S_SPACE
        + GPCC_SUGARM_BASEPWriter.S_MINIMIZE
        + GPCC_SUGARM_BASEPWriter.S_SPACE
        + GPCC_SUGARM_BASEPWriter.S_NUMEDGE;
  }

  @Override
  protected String getObjectiveConstraintEdgeBeforeTotalEdges() {
    return GPCC_SUGARM_BASEPWriter.S_PARENTHESIS_OPEN
        + GPCC_SUGARM_BASEPWriter.S_DOMAIN
        + GPCC_SUGARM_BASEPWriter.S_SPACE
        + GPCC_SUGARM_BASEPWriter.S_EDGE
        + GPCC_SUGARM_BASEPWriter.S_SPACE
        + GPCC_SUGARM_BASEPWriter.S_ZERO
        + GPCC_SUGARM_BASEPWriter.S_SPACE;
  }

  @Override
  protected String getObjectiveConstraintEdgeAfterTotalEdges() {
    String rtn = "";
    rtn +=
        GPCC_SUGARM_BASEPWriter.S_SPACE + GPCC_SUGARM_BASEPWriter.S_PARENTHESIS_CLOSE + Utils.getNewLine();
    rtn +=
        GPCC_SUGARM_BASEPWriter.S_PARENTHESIS_OPEN
            + GPCC_SUGARM_BASEPWriter.S_INT
            + GPCC_SUGARM_BASEPWriter.S_SPACE
            + GPCC_SUGARM_BASEPWriter.S_NUMEDGE
            + GPCC_SUGARM_BASEPWriter.S_SPACE
            + GPCC_SUGARM_BASEPWriter.S_EDGE
            + GPCC_SUGARM_BASEPWriter.S_PARENTHESIS_CLOSE
            + Utils.getNewLine();
    rtn +=
        GPCC_SUGARM_BASEPWriter.S_PARENTHESIS_OPEN
            + GPCC_SUGARM_BASEPWriter.S_EQUAL
            + GPCC_SUGARM_BASEPWriter.S_SPACE
            + GPCC_SUGARM_BASEPWriter.S_PARENTHESIS_OPEN
            + GPCC_SUGARM_BASEPWriter.S_ADDITION
            + GPCC_SUGARM_BASEPWriter.S_SPACE
            + GPCC_SUGARM_BASEPWriter.S_NUMEDGE;
    return rtn;
  }

  @Override
  protected String getObjectiveConstraintEdgeBeforeEqn() {
    return GPCC_SUGARM_BASEPWriter.S_SPACE;
  }

  @Override
  protected String getObjectiveConstraintEdgeAfterEqn() {
    return GPCC_SUGARM_BASEPWriter.S_SPACE;
  }

  @Override
  protected String getObjectiveConstraintEdgeBeforeEndTotalEdges() {
    return GPCC_SUGARM_BASEPWriter.S_PARENTHESIS_CLOSE + GPCC_SUGARM_BASEPWriter.S_SPACE;
  }

  @Override
  protected String getObjectiveConstraintEdgeAfterEndTotalEdges() {
    return GPCC_SUGARM_BASEPWriter.S_PARENTHESIS_CLOSE + Utils.getNewLine();
  }

  @Override
  protected String getAssignmentVariablePrefix() {
    return GPCC_SUGARM_BASEPWriter.S_BINARYDOMAIN + Utils.getNewLine();
  }

  @Override
  protected String getAssignmentVariablePreAssign() {
    String rtn = "";
    rtn += GPCC_SUGARM_BASEPWriter.S_PARENTHESIS_OPEN;
    rtn += GPCC_SUGARM_BASEPWriter.S_INT + GPCC_SUGARM_BASEPWriter.S_SPACE;
    return rtn;
  }

  @Override
  protected String getAssignmentVariablePostAssign() {
    String rtn = "";
    rtn += GPCC_SUGARM_BASEPWriter.S_SPACE + GPCC_SUGARM_BASEPWriter.S_BINARY;
    rtn += GPCC_SUGARM_BASEPWriter.S_PARENTHESIS_CLOSE;
    rtn += Utils.getNewLine();
    return rtn;
  }

  @Override
  protected String getAssignmentVariablePostfix() {
    return "";
  }

  @Override
  protected String getEnd() {
    String rtn = "";
    rtn += getComment();
    rtn += GPCC_SUGARM_BASEPWriter.S_END;
    rtn += Utils.getNewLine();
    return rtn;
  }

  @Override
  protected String getConstraintsBlockCapacityLowerBeforeEqn(final Capacity c) {
    String rtn = "";
    rtn +=
        GPCC_SUGARM_BASEPWriter.S_PARENTHESIS_OPEN
            + c.getLowerBoundType().toString()
            + GPCC_SUGARM_BASEPWriter.S_SPACE
            + c.getLowerBound()
            + GPCC_SUGARM_BASEPWriter.S_SPACE;
    rtn +=
        GPCC_SUGARM_BASEPWriter.S_PARENTHESIS_OPEN
            + GPCC_SUGARM_BASEPWriter.S_ADDITION
            + GPCC_SUGARM_BASEPWriter.S_SPACE;
    return rtn;
  }

  @Override
  protected String getConstraintsBlockCapacityLowerAfterEqn(final Capacity c) {
    String rtn = "";
    rtn += GPCC_SUGARM_BASEPWriter.S_PARENTHESIS_CLOSE;
    rtn += GPCC_SUGARM_BASEPWriter.S_PARENTHESIS_CLOSE + Utils.getNewLine();
    return rtn;
  }

  @Override
  protected String getConstraintsBlockCapacityUpperBeforeEqn(final Capacity c) {
    String rtn = "";
    rtn +=
        GPCC_SUGARM_BASEPWriter.S_PARENTHESIS_OPEN
            + c.getUpperBoundType().toString()
            + GPCC_SUGARM_BASEPWriter.S_SPACE
            + GPCC_SUGARM_BASEPWriter.S_PARENTHESIS_OPEN
            + GPCC_SUGARM_BASEPWriter.S_ADDITION
            + GPCC_SUGARM_BASEPWriter.S_SPACE;
    return rtn;
  }

  @Override
  protected String getConstraintsBlockCapacityUpperAfterEqn(final Capacity c) {
    String rtn = "";
    rtn += GPCC_SUGARM_BASEPWriter.S_PARENTHESIS_CLOSE + GPCC_SUGARM_BASEPWriter.S_SPACE;
    rtn += c.getUpperBound();
    rtn += GPCC_SUGARM_BASEPWriter.S_PARENTHESIS_CLOSE + Utils.getNewLine();
    return rtn;
  }

  @Override
  protected String getConstraintsAssignmentPrefix() {
    String rtn = "";
    rtn +=
        GPCC_SUGARM_BASEPWriter.S_PARENTHESIS_OPEN
            + GPCC_SUGARM_BASEPWriter.S_EQUAL
            + GPCC_SUGARM_BASEPWriter.S_SPACE
            + GPCC_SUGARM_BASEPWriter.S_ONE
            + GPCC_SUGARM_BASEPWriter.S_SPACE;
    rtn += GPCC_SUGARM_BASEPWriter.S_PARENTHESIS_OPEN + GPCC_SUGARM_BASEPWriter.S_ADDITION;
    return rtn;
  }

  @Override
  protected String getConstraintsAssignmentPostfix() {
    String rtn = "";
    rtn +=
        GPCC_SUGARM_BASEPWriter.S_PARENTHESIS_CLOSE
            + GPCC_SUGARM_BASEPWriter.S_SPACE
            + GPCC_SUGARM_BASEPWriter.S_PARENTHESIS_CLOSE;
    return rtn;
  }

  @Override
  protected String getConstraintsInterBlockCapacityLowerBeforeEqn(final Capacity c) {
    String rtn = "";
    rtn +=
        GPCC_SUGARM_BASEPWriter.S_PARENTHESIS_OPEN
            + c.getLowerBoundType().toString()
            + GPCC_SUGARM_BASEPWriter.S_SPACE
            + c.getLowerBound()
            + GPCC_SUGARM_BASEPWriter.S_SPACE;
    rtn +=
        GPCC_SUGARM_BASEPWriter.S_PARENTHESIS_OPEN
            + GPCC_SUGARM_BASEPWriter.S_ADDITION
            + GPCC_SUGARM_BASEPWriter.S_SPACE;
    return rtn;
  }

  @Override
  protected String getConstraintsInterBlockCapacityLowerAfterEqn(final Capacity c) {
    String rtn = "";
    rtn += GPCC_SUGARM_BASEPWriter.S_PARENTHESIS_CLOSE;
    rtn += GPCC_SUGARM_BASEPWriter.S_PARENTHESIS_CLOSE + Utils.getNewLine();
    return rtn;
  }

  @Override
  protected String getConstraintsInterBlockCapacityUpperBeforeEqn(final Capacity c) {
    String rtn = "";
    rtn +=
        GPCC_SUGARM_BASEPWriter.S_PARENTHESIS_OPEN
            + c.getUpperBoundType().toString()
            + GPCC_SUGARM_BASEPWriter.S_SPACE
            + GPCC_SUGARM_BASEPWriter.S_PARENTHESIS_OPEN
            + GPCC_SUGARM_BASEPWriter.S_ADDITION
            + GPCC_SUGARM_BASEPWriter.S_SPACE;
    return rtn;
  }

  @Override
  protected String getConstraintsInterBlockCapacityUpperAfterEqn(final Capacity c) {
    String rtn = "";
    rtn += GPCC_SUGARM_BASEPWriter.S_PARENTHESIS_CLOSE + GPCC_SUGARM_BASEPWriter.S_SPACE;
    rtn += c.getUpperBound();
    rtn += GPCC_SUGARM_BASEPWriter.S_PARENTHESIS_CLOSE + Utils.getNewLine();
    return rtn;
  }

  @Override
  protected String getConstraintsOutputConnectionsCapacityPrefix(final int blockIdx) {
    final String rtn = "";
    /*
     * rtn += S_PARENTHESIS_OPEN + S_INT + S_SPACE +
     * this.getConstraintsOutputConnectionsCapacityVariable(blockIdx); rtn += S_SPACE + S_ZERO +
     * S_SPACE + this.getConstraintsOutputConnectionsCapacityBlockMaxConnections(blockIdx); rtn +=
     * S_PARENTHESIS_CLOSE + Utils.getNewLine();
     */
    return rtn;
  }

  @Override
  protected String getConstraintsInputConnectionsCapacityPrefix(final int blockIdx) {
    final String rtn = "";
    /*
     * rtn += S_PARENTHESIS_OPEN + S_INT + S_SPACE +
     * this.getConstraintsInputConnectionsCapacityVariable(blockIdx); rtn += S_SPACE + S_ZERO +
     * S_SPACE + this.getConstraintsInputConnectionsCapacityBlockMaxConnections(blockIdx); rtn +=
     * S_PARENTHESIS_CLOSE + Utils.getNewLine();
     */
    return rtn;
  }

  @Override
  protected String getConstraintsInOutConnectionsCapacityPrefix(final int blockIdx) {
    final String rtn = "";
    /*
     * rtn += S_PARENTHESIS_OPEN + S_INT + S_SPACE +
     * this.getConstraintsInputConnectionsCapacityVariable(blockIdx); rtn += S_SPACE + S_ZERO +
     * S_SPACE + this.getConstraintsInputConnectionsCapacityBlockMaxConnections(blockIdx); rtn +=
     * S_PARENTHESIS_CLOSE + Utils.getNewLine();
     */
    return rtn;
  }

  @Override
  protected String getInterBlockEqn(final int srcBlockIdx, final int dstBlockIdx) {
    String rtn = "";
    rtn += getConstraintsConnectionsCapacityCellList(true, srcBlockIdx, dstBlockIdx);
    return rtn;
  }

  // TODO: make abstract
  protected void writeConstraintsObjectiveNumBlocks() {
    String constraint = "";
    String constraintHeader = "";
    String equ = "";
    final List<List<Set<Integer>>> blockCapacityCellList = getGpccBase().getBlockCapacityCellList();
    constraintHeader +=
        getComment()
            + GPCC_SUGARM_BASEPWriter.S_OBJECTIVE
            + GPCC_SUGARM_BASEPWriter.S_SPACE
            + "NumBlocks"
            + Utils.getNewLine();
    constraintHeader +=
        "("
            + GPCC_SUGARM_BASEPWriter.S_INT
            + GPCC_SUGARM_BASEPWriter.S_SPACE
            + "NumBlocks "
            + getLowerBoundBlocks()
            + GPCC_SUGARM_BASEPWriter.S_SPACE
            + getUpperBoundBlocks()
            + ")"
            + Utils.getNewLine();
    constraintHeader +=
        "("
            + GPCC_SUGARM_BASEPWriter.S_OBJECTIVE
            + GPCC_SUGARM_BASEPWriter.S_SPACE
            + "minimize NumBlocks)"
            + Utils.getNewLine();
    //
    for (int blockIdx = 0; blockIdx < blockCapacityCellList.size(); blockIdx++) {
      final List<Set<Integer>> cellList = blockCapacityCellList.get(blockIdx);
      final Set<Integer> allCells = union(cellList);
      equ = getConstraintsBlockCapacityCellList(blockIdx, allCells);
      constraint += " ( if ( < 0 ";
      constraint += " ( + " + equ + ") )";
      constraint += " 1 0 )";
    }
    constraint = "( <= (+ " + constraint + " ) NumBlocks )";
    constraint += Utils.getNewLine();
    constraint = constraintHeader + constraint;
    // write script
    Utils.appendToFile(constraint, getFilename());
  }

  @Override
  public void writeContent() {
    if (getContents() == null) {
      writeAssignmentVariable();
      // this.writeConstraintsObjectiveEdge();
      writeConstraintsBlockCapacity();
      writeConstraintsBlockInputConnectionsCapacity();
      writeConstraintsBlockOutputConnectionsCapacity();
      writeConstraintsBlockInOutConnectionsCapacity();
      writeConstraintsAssignment();
      writeConstraintsInterBlockCapacity();
      try {
        final File file = new File(getFilename());
        final FileInputStream fis = new FileInputStream(file);
        final byte[] data = new byte[(int) file.length()];
        fis.read(data);
        setContents(new String(data, "UTF-8"));
        fis.close();
      } catch (final IOException e) {
        e.printStackTrace();
      }
    } else {
      Utils.appendToFile(getContents(), getFilename());
    }
    writeConstraintsObjectiveNumBlocks();
  }

  GPCC_SUGARM_BASEPWriter(final GPCC_BASE gpcc, final String filename, final int upperbound) {
    super(gpcc, filename);
    setLowerBoundBlocks(0);
    setUpperBoundBlocks(upperbound);
    setContents(null);
  }

  /**
   * Setter for {@code lowerBoundBlocks}.
   *
   * @param lowerBoundBlocks The value to set {@code lowerBoundBlocks}.
   */
  public void setLowerBoundBlocks(final int lowerBoundBlocks) {
    this.lowerBoundBlocks = lowerBoundBlocks;
  }

  /**
   * Getter for {@code lowerBoundBlocks}.
   *
   * @return The value of {@code lowerBoundBlocks}.
   */
  protected int getLowerBoundBlocks() {
    return lowerBoundBlocks;
  }

  private int lowerBoundBlocks;

  /**
   * Setter for {@code upperBoundBlocks}.
   *
   * @param upperBoundBlocks The value to set {@code upperBoundBlocks}.
   */
  public void setUpperBoundBlocks(final int upperBoundBlocks) {
    this.upperBoundBlocks = upperBoundBlocks;
  }

  /**
   * Getter for {@code upperBoundBlocks}.
   *
   * @return The value of {@code upperBoundBlocks}.
   */
  protected int getUpperBoundBlocks() {
    return upperBoundBlocks;
  }

  private int upperBoundBlocks;

  // TODO: save file for next iteration
  // reduce time
  /**
   * Setter for {@code contents}.
   *
   * @param contents The value to set {@code contents}.
   */
  protected void setContents(final String contents) {
    this.contents = contents;
  }

  /**
   * Getter for {@code contents}.
   *
   * @return The value of {@code contents}.
   */
  protected String getContents() {
    return contents;
  }

  private String contents;

  static String S_TO = "TO";
  static String S_UPPER = "UPPER";
  static String S_LOWER = "LOWER";
  static String S_NUMEDGE = "numEdge";
  static String S_EDGE = "edge";
  static String S_OBJECTIVE = "objective";
  static String S_MINIMIZE = "minimize";
  static String S_ADDITION = "+";
  static String S_MULTIPLICATION = "*";
  static String S_EQUAL = "=";
  static String S_PARENTHESIS_OPEN = "(";
  static String S_PARENTHESIS_CLOSE = ")";
  static String S_SPACE = " ";
  static String S_INT = "int";
  static String S_DOMAIN = "domain";
  static String S_COMMENT = ";";
  static String S_BINARY = "b";
  static String S_ZERO = "0";
  static String S_ONE = "1";
  static String S_END = "End";
  static String S_BINARYRANGE =
      GPCC_SUGARM_BASEPWriter.S_ZERO + GPCC_SUGARM_BASEPWriter.S_SPACE + GPCC_SUGARM_BASEPWriter.S_ONE;
  static String S_BINARYDOMAIN =
      GPCC_SUGARM_BASEPWriter.S_PARENTHESIS_OPEN
          + GPCC_SUGARM_BASEPWriter.S_DOMAIN
          + GPCC_SUGARM_BASEPWriter.S_SPACE
          + GPCC_SUGARM_BASEPWriter.S_BINARY
          + GPCC_SUGARM_BASEPWriter.S_SPACE
          + GPCC_SUGARM_BASEPWriter.S_BINARYRANGE
          + GPCC_SUGARM_BASEPWriter.S_PARENTHESIS_CLOSE;
}

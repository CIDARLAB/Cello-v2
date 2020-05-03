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

package org.cellocad.v2.partitioning.algorithm.gpccSugarMBase;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Set;
import org.cellocad.v2.common.Utils;
import org.cellocad.v2.partitioning.algorithm.gpccBase.GpccBase;
import org.cellocad.v2.partitioning.algorithm.gpccBase.GpccBasePWriter;
import org.cellocad.v2.partitioning.profile.Capacity;

/**
 * The GPCC_SUGARM_PWriter class implements the problem writer for the <i>GPCC_SUGARM_BASE</i>
 * algorithm.
 *
 * @author Vincent Mirian
 *
 * @date 2018-05-21
 */
public class GpccSugarMPWriter extends GpccBasePWriter {

  @Override
  protected String getEdgeEquationCoefficient(final int coefficient) {
    String rtn = "";
    rtn += Integer.toString(coefficient) + GpccSugarMPWriter.S_SPACE;
    rtn += GpccSugarMPWriter.S_PARENTHESIS_OPEN + GpccSugarMPWriter.S_MULTIPLICATION
        + GpccSugarMPWriter.S_SPACE;
    return rtn;
  }

  @Override
  protected String getEdgeEquationBetweenBlocks() {
    return GpccSugarMPWriter.S_SPACE;
  }

  @Override
  protected String getEdgeEquationAfterBlocks() {
    return GpccSugarMPWriter.S_PARENTHESIS_CLOSE + GpccSugarMPWriter.S_SPACE;
  }

  @Override
  protected String getEdgeEquationBeforeTerm() {
    return GpccSugarMPWriter.S_PARENTHESIS_OPEN + GpccSugarMPWriter.S_MULTIPLICATION
        + GpccSugarMPWriter.S_SPACE;
  }

  @Override
  protected String getEdgeEquationAfterTerm() {
    return GpccSugarMPWriter.S_PARENTHESIS_CLOSE + GpccSugarMPWriter.S_SPACE;
  }

  @Override
  protected String getComment() {
    return GpccSugarMPWriter.S_COMMENT;
  }

  @Override
  protected String getObjectiveConstraintEdgeName() {
    return GpccSugarMPWriter.S_OBJECTIVE + GpccSugarMPWriter.S_SPACE + GpccSugarMPWriter.S_MINIMIZE
        + GpccSugarMPWriter.S_SPACE + GpccSugarMPWriter.S_NUMEDGE;
  }

  @Override
  protected String getObjectiveConstraintEdgeBeforeTotalEdges() {
    return GpccSugarMPWriter.S_PARENTHESIS_OPEN + GpccSugarMPWriter.S_DOMAIN
        + GpccSugarMPWriter.S_SPACE + GpccSugarMPWriter.S_EDGE + GpccSugarMPWriter.S_SPACE
        + GpccSugarMPWriter.S_ZERO + GpccSugarMPWriter.S_SPACE;
  }

  @Override
  protected String getObjectiveConstraintEdgeAfterTotalEdges() {
    String rtn = "";
    rtn += GpccSugarMPWriter.S_SPACE + GpccSugarMPWriter.S_PARENTHESIS_CLOSE + Utils.getNewLine();
    rtn += GpccSugarMPWriter.S_PARENTHESIS_OPEN + GpccSugarMPWriter.S_INT
        + GpccSugarMPWriter.S_SPACE + GpccSugarMPWriter.S_NUMEDGE + GpccSugarMPWriter.S_SPACE
        + GpccSugarMPWriter.S_EDGE + GpccSugarMPWriter.S_PARENTHESIS_CLOSE + Utils.getNewLine();
    rtn += GpccSugarMPWriter.S_PARENTHESIS_OPEN + GpccSugarMPWriter.S_EQUAL
        + GpccSugarMPWriter.S_SPACE + GpccSugarMPWriter.S_PARENTHESIS_OPEN
        + GpccSugarMPWriter.S_ADDITION + GpccSugarMPWriter.S_SPACE + GpccSugarMPWriter.S_NUMEDGE;
    return rtn;
  }

  @Override
  protected String getObjectiveConstraintEdgeBeforeEqn() {
    return GpccSugarMPWriter.S_SPACE;
  }

  @Override
  protected String getObjectiveConstraintEdgeAfterEqn() {
    return GpccSugarMPWriter.S_SPACE;
  }

  @Override
  protected String getObjectiveConstraintEdgeBeforeEndTotalEdges() {
    return GpccSugarMPWriter.S_PARENTHESIS_CLOSE + GpccSugarMPWriter.S_SPACE;
  }

  @Override
  protected String getObjectiveConstraintEdgeAfterEndTotalEdges() {
    return GpccSugarMPWriter.S_PARENTHESIS_CLOSE + Utils.getNewLine();
  }

  @Override
  protected String getAssignmentVariablePrefix() {
    return GpccSugarMPWriter.S_BINARYDOMAIN + Utils.getNewLine();
  }

  @Override
  protected String getAssignmentVariablePreAssign() {
    String rtn = "";
    rtn += GpccSugarMPWriter.S_PARENTHESIS_OPEN;
    rtn += GpccSugarMPWriter.S_INT + GpccSugarMPWriter.S_SPACE;
    return rtn;

  }

  @Override
  protected String getAssignmentVariablePostAssign() {
    String rtn = "";
    rtn += GpccSugarMPWriter.S_SPACE + GpccSugarMPWriter.S_BINARY;
    rtn += GpccSugarMPWriter.S_PARENTHESIS_CLOSE;
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
    rtn += GpccSugarMPWriter.S_END;
    rtn += Utils.getNewLine();
    return rtn;
  }

  @Override
  protected String getConstraintsBlockCapacityLowerBeforeEqn(final Capacity c) {
    String rtn = "";
    rtn += GpccSugarMPWriter.S_PARENTHESIS_OPEN + c.getLowerBoundType().toString()
        + GpccSugarMPWriter.S_SPACE + c.getLowerBound() + GpccSugarMPWriter.S_SPACE;
    rtn += GpccSugarMPWriter.S_PARENTHESIS_OPEN + GpccSugarMPWriter.S_ADDITION
        + GpccSugarMPWriter.S_SPACE;
    return rtn;
  }

  @Override
  protected String getConstraintsBlockCapacityLowerAfterEqn(final Capacity c) {
    String rtn = "";
    rtn += GpccSugarMPWriter.S_PARENTHESIS_CLOSE;
    rtn += GpccSugarMPWriter.S_PARENTHESIS_CLOSE + Utils.getNewLine();
    return rtn;
  }

  @Override
  protected String getConstraintsBlockCapacityUpperBeforeEqn(final Capacity c) {
    String rtn = "";
    rtn += GpccSugarMPWriter.S_PARENTHESIS_OPEN + c.getUpperBoundType().toString()
        + GpccSugarMPWriter.S_SPACE + GpccSugarMPWriter.S_PARENTHESIS_OPEN
        + GpccSugarMPWriter.S_ADDITION + GpccSugarMPWriter.S_SPACE;
    return rtn;
  }

  @Override
  protected String getConstraintsBlockCapacityUpperAfterEqn(final Capacity c) {
    String rtn = "";
    rtn += GpccSugarMPWriter.S_PARENTHESIS_CLOSE + GpccSugarMPWriter.S_SPACE;
    rtn += c.getUpperBound();
    rtn += GpccSugarMPWriter.S_PARENTHESIS_CLOSE + Utils.getNewLine();
    return rtn;
  }

  @Override
  protected String getConstraintsAssignmentPrefix() {
    String rtn = "";
    rtn += GpccSugarMPWriter.S_PARENTHESIS_OPEN + GpccSugarMPWriter.S_EQUAL
        + GpccSugarMPWriter.S_SPACE + GpccSugarMPWriter.S_ONE + GpccSugarMPWriter.S_SPACE;
    rtn += GpccSugarMPWriter.S_PARENTHESIS_OPEN + GpccSugarMPWriter.S_ADDITION;
    return rtn;
  }

  @Override
  protected String getConstraintsAssignmentPostfix() {
    String rtn = "";
    rtn += GpccSugarMPWriter.S_PARENTHESIS_CLOSE + GpccSugarMPWriter.S_SPACE
        + GpccSugarMPWriter.S_PARENTHESIS_CLOSE;
    return rtn;
  }

  @Override
  protected String getConstraintsInterBlockCapacityLowerBeforeEqn(final Capacity c) {
    String rtn = "";
    rtn += GpccSugarMPWriter.S_PARENTHESIS_OPEN + c.getLowerBoundType().toString()
        + GpccSugarMPWriter.S_SPACE + c.getLowerBound() + GpccSugarMPWriter.S_SPACE;
    rtn += GpccSugarMPWriter.S_PARENTHESIS_OPEN + GpccSugarMPWriter.S_ADDITION
        + GpccSugarMPWriter.S_SPACE;
    return rtn;
  }

  @Override
  protected String getConstraintsInterBlockCapacityLowerAfterEqn(final Capacity c) {
    String rtn = "";
    rtn += GpccSugarMPWriter.S_PARENTHESIS_CLOSE;
    rtn += GpccSugarMPWriter.S_PARENTHESIS_CLOSE + Utils.getNewLine();
    return rtn;

  }

  @Override
  protected String getConstraintsInterBlockCapacityUpperBeforeEqn(final Capacity c) {
    String rtn = "";
    rtn += GpccSugarMPWriter.S_PARENTHESIS_OPEN + c.getUpperBoundType().toString()
        + GpccSugarMPWriter.S_SPACE + GpccSugarMPWriter.S_PARENTHESIS_OPEN
        + GpccSugarMPWriter.S_ADDITION + GpccSugarMPWriter.S_SPACE;
    return rtn;
  }

  @Override
  protected String getConstraintsInterBlockCapacityUpperAfterEqn(final Capacity c) {
    String rtn = "";
    rtn += GpccSugarMPWriter.S_PARENTHESIS_CLOSE + GpccSugarMPWriter.S_SPACE;
    rtn += c.getUpperBound();
    rtn += GpccSugarMPWriter.S_PARENTHESIS_CLOSE + Utils.getNewLine();
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
    constraintHeader += getComment() + GpccSugarMPWriter.S_OBJECTIVE + GpccSugarMPWriter.S_SPACE
        + "NumBlocks" + Utils.getNewLine();
    constraintHeader += "(" + GpccSugarMPWriter.S_INT + GpccSugarMPWriter.S_SPACE + "NumBlocks "
        + getLowerBoundBlocks() + GpccSugarMPWriter.S_SPACE + getUpperBoundBlocks() + ")"
        + Utils.getNewLine();
    constraintHeader += "(" + GpccSugarMPWriter.S_OBJECTIVE + GpccSugarMPWriter.S_SPACE
        + "minimize NumBlocks)" + Utils.getNewLine();
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

  GpccSugarMPWriter(final GpccBase gpcc, final String filename, final int upperbound) {
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
      GpccSugarMPWriter.S_ZERO + GpccSugarMPWriter.S_SPACE + GpccSugarMPWriter.S_ONE;
  static String S_BINARYDOMAIN = GpccSugarMPWriter.S_PARENTHESIS_OPEN + GpccSugarMPWriter.S_DOMAIN
      + GpccSugarMPWriter.S_SPACE + GpccSugarMPWriter.S_BINARY + GpccSugarMPWriter.S_SPACE
      + GpccSugarMPWriter.S_BINARYRANGE + GpccSugarMPWriter.S_PARENTHESIS_CLOSE;

}

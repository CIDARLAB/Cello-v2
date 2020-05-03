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

package org.cellocad.v2.partitioning.algorithm.gpccBase;

import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.cellocad.v2.common.Utils;
import org.cellocad.v2.partitioning.common.Block;
import org.cellocad.v2.partitioning.common.InterBlock;
import org.cellocad.v2.partitioning.common.Partition;
import org.cellocad.v2.partitioning.profile.Capacity;
import org.cellocad.v2.results.logicSynthesis.LSResultsUtils;
import org.cellocad.v2.results.netlist.NetlistNode;
import org.cellocad.v2.results.netlist.NetlistNodeUtils;

/**
 * The implementation of the problem writer for the <i>GPCC_BASE</i> algorithm.
 *
 * @author Vincent Mirian
 *
 * @date 2018-05-21
 */
public abstract class GpccBasePWriter {

  /**
   * Returns a aggregated set of integers from the list defined by parameter {@code listSets}.
   *
   * @param listSets a list of sets containing integers.
   * @return A aggregated set of integers from the list defined by parameter {@code listSets}.
   */
  protected Set<Integer> union(final List<Set<Integer>> listSets) {
    final Set<Integer> rtn = new HashSet<>();
    for (int j = 0; j < listSets.size(); j++) {
      rtn.addAll(listSets.get(j));
    }
    return rtn;
  }

  /**
   * Returns a string representation of the assignment variable of cell defined by parameter
   * {@code cell} to the block defined by parameter {@code block}.
   *
   * @param cell  An integer representing the cell.
   * @param block An integer representing the block.
   * @return A string representation of the assignment variable of cell defined by parameter
   *         {@code cell} to the block defined by parameter {@code block}.
   */
  protected String assignInt(final int cell, final int block) {
    String rtn = "";
    rtn += GpccBasePWriter.S_ASSIGN;
    rtn += GpccBasePWriter.S_CELL;
    rtn += cell;
    rtn += GpccBasePWriter.S_BLK;
    rtn += block;
    return rtn;
  }

  /**
   * Returns a string representation the total number of edges.
   *
   * @return A string representation the total number of edges.
   */
  public String getConstraintsObjectiveTotalEdges() {
    String rtn = "";
    int totalEdges = 0;
    final List<NetlistNode> nodeList = getGpccBase().getCellList();
    final int size = nodeList.size();
    for (int i = 0; i < size; i++) {
      for (int j = 0; j < size; j++) {
        final NetlistNode src = nodeList.get(i);
        final NetlistNode dst = nodeList.get(j);
        totalEdges += NetlistNodeUtils.numNetlistEdgesBetween(src, dst);
      }
    }
    rtn += totalEdges;
    return rtn;
  }

  /**
   * Returns a string representation with the Edge Equation coefficient.
   *
   * @return A string representation with the Edge Equation coefficient.
   */
  protected abstract String getEdgeEquationCoefficient(int coefficient);

  /**
   * Returns a string used in the Edge Equation between Blocks.
   *
   * @return A string used in the Edge Equation between Blocks.
   */
  protected abstract String getEdgeEquationBetweenBlocks();

  /**
   * Returns a string used in the Edge Equation After Blocks.
   *
   * @return A string used in the Edge Equation After Blocks.
   */
  protected abstract String getEdgeEquationAfterBlocks();

  /**
   * Returns a string used in the Edge Equation Before Term.
   *
   * @return A string used in the Edge Equation Before Term.
   */
  protected abstract String getEdgeEquationBeforeTerm();

  /**
   * Returns a string used in the Edge Equation After Term.
   *
   * @return A string used in the Edge Equation After Term.
   */
  protected abstract String getEdgeEquationAfterTerm();

  /**
   * Returns a string representing the Edge Equation.
   *
   * @param srcCells    source cells.
   * @param dstCells    dstCells cells.
   * @param nodeList    node list.
   * @param srcBlockIdx source block index.
   * @param dstBlockIdx destination block index.
   * @return A string representing the Edge Equation.
   */
  protected String getEdgeEquation(final Set<Integer> srcCells, final Set<Integer> dstCells,
      final List<NetlistNode> nodeList, final int srcBlockIdx, final int dstBlockIdx) {
    String rtn = "";
    // for each source cell
    final Iterator<Integer> it0 = srcCells.iterator();
    while (it0.hasNext()) {
      final int j = it0.next();
      // for each destination cell
      final Iterator<Integer> it1 = dstCells.iterator();
      while (it1.hasNext()) {
        final int k = it1.next();
        String term = "";
        /*
         * check the connections between the cells for that block add the coefficient for that
         * weight.
         */
        final NetlistNode src = nodeList.get(j);
        final NetlistNode dst = nodeList.get(k);
        int coefficient = 0;
        coefficient += NetlistNodeUtils.numNetlistEdgesBetween(src, dst);
        if (coefficient > 0) {
          term += getEdgeEquationCoefficient(coefficient);
          term += assignInt(j, srcBlockIdx);
          term += getEdgeEquationBetweenBlocks();
          term += assignInt(k, dstBlockIdx);
          term += getEdgeEquationAfterBlocks();
        }
        // if term is not empty add to objective function
        if (!term.isEmpty()) {
          rtn += getEdgeEquationBeforeTerm();
          rtn += term;
          rtn += getEdgeEquationAfterTerm();
        }
      }
    }
    return rtn;
  }

  /**
   * Returns a string representing a start of comment.
   *
   * @return A string representing a start of comment.
   */
  protected abstract String getComment();

  /**
   * Returns a string representing the Objective Constraint Edge Name.
   *
   * @return A string representing the Objective Constraint Edge Name.
   */
  protected abstract String getObjectiveConstraintEdgeName();

  /**
   * Returns a string representing the Objective Constraint Edge Before Total Edges.
   *
   * @return A string representing the Objective Constraint Edge Before Total Edges.
   */
  protected abstract String getObjectiveConstraintEdgeBeforeTotalEdges();

  /**
   * Returns a string representing the Objective Constraint Edge After Total Edges.
   *
   * @return A string representing the Objective Constraint Edge After Total Edges.
   */
  protected abstract String getObjectiveConstraintEdgeAfterTotalEdges();

  /**
   * Returns a string representing the Objective Constraint Edge Before Equation.
   *
   * @return A string representing the Objective Constraint Edge Before Equation.
   */
  protected abstract String getObjectiveConstraintEdgeBeforeEqn();

  /**
   * Returns a string representing the Objective Constraint Edge After Equation.
   *
   * @return A string representing the Objective Constraint Edge After Equation.
   */
  protected abstract String getObjectiveConstraintEdgeAfterEqn();

  /**
   * Returns a string representing the Objective Constraint Edge Before End Total Edges.
   *
   * @return A string representing the Objective Constraint Edge Before End Total Edges.
   */
  protected abstract String getObjectiveConstraintEdgeBeforeEndTotalEdges();

  /**
   * Returns a string representing the Objective Constraint Edge After End Total Edges.
   *
   * @return A string representing the Objective Constraint Edge After End Total Edges.
   */
  protected abstract String getObjectiveConstraintEdgeAfterEndTotalEdges();

  /**
   * Writes the Objective Constraint Edge.
   */
  protected void writeConstraintsObjectiveEdge() {
    String constraint = "";
    String equ = "";
    String name = "";
    final List<List<Set<Integer>>> blockCapacityCellList = getGpccBase().getBlockCapacityCellList();
    final List<NetlistNode> nodeList = getGpccBase().getCellList();
    final int sizeI = blockCapacityCellList.size();
    if (sizeI != 0) {
      final String totalEdges = getConstraintsObjectiveTotalEdges();
      name += getObjectiveConstraintEdgeName();
      constraint += getComment() + name + Utils.getNewLine();
      constraint += getObjectiveConstraintEdgeBeforeTotalEdges();
      constraint += totalEdges;
      constraint += getObjectiveConstraintEdgeAfterTotalEdges();
      // for each block
      for (int i = 0; i < sizeI; i++) {
        final List<Set<Integer>> allCellList = blockCapacityCellList.get(i);
        final Set<Integer> cellList = union(allCellList);
        equ = getEdgeEquation(cellList, cellList, nodeList, i, i);
        if (!equ.isEmpty()) {
          constraint += getObjectiveConstraintEdgeBeforeEqn();
          constraint += equ;
          constraint += getObjectiveConstraintEdgeAfterEqn();
        }
      }
      constraint += getObjectiveConstraintEdgeBeforeEndTotalEdges();
      constraint += totalEdges;
      constraint += getObjectiveConstraintEdgeAfterEndTotalEdges();
    }
    // write script
    Utils.appendToFile(constraint, getFilename());
  }

  /**
   * Writes the Header.
   */
  protected void writeHeader() {
    String constraint = "";
    constraint += getComment() + getFilename() + Utils.getNewLine();
    // write script
    Utils.appendToFile(constraint, getFilename());
  }

  /**
   * Returns a string representing the Assignment Variable Prefix.
   *
   * @return A string representing the Assignment Variable Prefix.
   */
  protected abstract String getAssignmentVariablePrefix();

  /**
   * Returns a string representing the Assignment Variable PreAssign.
   *
   * @return A string representing the Assignment Variable PreAssign.
   */
  protected abstract String getAssignmentVariablePreAssign();

  /**
   * Returns a string representing the Assignment Variable PostAssign.
   *
   * @return A string representing the Assignment Variable PostAssign.
   */
  protected abstract String getAssignmentVariablePostAssign();

  /**
   * Returns a string representing the Assignment Variable Postfix.
   *
   * @return A string representing the Assignment Variable Postfix.
   */
  protected abstract String getAssignmentVariablePostfix();

  /**
   * Writes the Assignment Variable.
   */
  protected void writeAssignmentVariable() {
    String constraint = "";
    final List<Set<Integer>> cellBlockList = getGpccBase().getCellBlockList();
    constraint += getAssignmentVariablePrefix();
    for (int i = 0; i < cellBlockList.size(); i++) {
      final int size = cellBlockList.get(i).size();
      if (size == 0) {
        continue;
      }
      final Iterator<Integer> it = cellBlockList.get(i).iterator();
      while (it.hasNext()) {
        final int blkId = it.next();
        constraint += getAssignmentVariablePreAssign();
        constraint += assignInt(i, blkId);
        constraint += getAssignmentVariablePostAssign();
      }
    }
    constraint += getAssignmentVariablePostfix();
    // write script
    Utils.appendToFile(constraint, getFilename());
  }

  /**
   * Returns a string representing the End.
   *
   * @return A string representing the End.
   */
  protected abstract String getEnd();

  /**
   * Writes the End.
   */
  protected void writeEnd() {
    String constraint = "";
    constraint += getEnd();
    // write script
    Utils.appendToFile(constraint, getFilename());
  }

  /**
   * Returns a string representing the Block Capacity Header for Block index defined by parameter
   * {@code blockIdx} and capacity index defined by parameter {@code capacityIdx}.
   *
   * @param blockIdx    The block index.
   * @param capacityIdx The capacity index.
   * @return A string representing the Block Capacity Header.
   */
  protected String getConstraintsBlockCapacityHeader(final int blockIdx, final int capacityIdx) {
    String rtn = "";
    // header
    rtn += Utils.getTabCharacter();
    rtn += GpccBasePWriter.S_BLK + GpccBasePWriter.S_SPACE;
    rtn += blockIdx + GpccBasePWriter.S_SPACE;
    rtn += GpccBasePWriter.S_CAPACITY + GpccBasePWriter.S_SPACE;
    rtn += capacityIdx;
    return rtn;
  }

  /**
   * Returns a string representing the Block Capacity CellList for Block index defined by parameter
   * {@code blockIdx} and set of integer defined by cellList <i>cellList</i>.
   *
   * @param blockIdx The block index.
   * @param cellList The cellList.
   * @return A string representing the Block Capacity CellList.
   */
  protected String getConstraintsBlockCapacityCellList(final int blockIdx,
      final Set<Integer> cellList) {
    String rtn = "";
    int cellIdx = 0;
    final Iterator<Integer> it = cellList.iterator();
    while (it.hasNext()) {
      if (cellIdx != 0) {
        rtn += GpccBasePWriter.S_SPACE;
      }
      final int cellId = it.next();
      rtn += assignInt(cellId, blockIdx);
      cellIdx++;
    }
    return rtn;
  }

  /**
   * Returns a string representing the Block Capacity Lower Before Eqn.
   *
   * @param c The Capacity.
   * @return A string representing the Block Capacity Lower Before Eqn.
   */
  protected abstract String getConstraintsBlockCapacityLowerBeforeEqn(Capacity c);

  /**
   * Returns a string representing the Block Capacity Lower After Eqn.
   *
   * @param c The Capacity.
   * @return A string representing the Block Capacity Lower After Eqn.
   */
  protected abstract String getConstraintsBlockCapacityLowerAfterEqn(Capacity c);

  /**
   * Returns a string representing the Block Capacity Upper Before Eqn.
   *
   * @param c The Capacity.
   * @return A string representing the Block Capacity Upper Before Eqn.
   */
  protected abstract String getConstraintsBlockCapacityUpperBeforeEqn(Capacity c);

  /**
   * Returns a string representing the Block Capacity Upper After Eqn.
   *
   * @param c The Capacity.
   * @return A string representing the Block Capacity Upper After Eqn.
   */
  protected abstract String getConstraintsBlockCapacityUpperAfterEqn(Capacity c);

  /**
   * Writes the Block Capacity.
   */
  protected void writeConstraintsBlockCapacity() {
    String equ = "";
    final List<List<Set<Integer>>> blockCapacityCellList = getGpccBase().getBlockCapacityCellList();
    for (int blockIdx = 0; blockIdx < blockCapacityCellList.size(); blockIdx++) {
      String constraint = "";
      final List<Set<Integer>> allCellList = blockCapacityCellList.get(blockIdx);
      final int size = allCellList.size();
      if (size == 0) {
        continue;
      }
      final Block block = getGpccBase().getBlockList().get(blockIdx);
      for (int capacityIdx = 0; capacityIdx < block.getNumCapacity(); capacityIdx++) {
        final Capacity capacity = block.getCapacityAtIdx(capacityIdx);
        final Set<Integer> cellList = allCellList.get(capacityIdx);
        // header
        constraint += getComment() + getConstraintsBlockCapacityHeader(blockIdx, capacityIdx)
            + Utils.getNewLine();
        equ = getConstraintsBlockCapacityCellList(blockIdx, cellList);
        // identifier lower
        constraint +=
            getComment() + Utils.getTabCharacter() + GpccBasePWriter.S_LOWER + Utils.getNewLine();
        constraint += getConstraintsBlockCapacityLowerBeforeEqn(capacity);
        constraint += equ;
        constraint += getConstraintsBlockCapacityLowerAfterEqn(capacity);
        // identifier upper
        constraint +=
            getComment() + Utils.getTabCharacter() + GpccBasePWriter.S_UPPER + Utils.getNewLine();
        constraint += getConstraintsBlockCapacityUpperBeforeEqn(capacity);
        constraint += equ;
        constraint += getConstraintsBlockCapacityUpperAfterEqn(capacity);
      }
      // write script
      Utils.appendToFile(constraint, getFilename());
    }
  }

  /**
   * Returns a string representing the Block Input Connection Capacity CellList for cell defined by
   * parameter {@code cellID} and block defined by parameter {@code blockIdx}.
   *
   * @param cellId   The cell index.
   * @param blockIdx The block index.
   * @return A string representing the Block Input Connection Capacity CellList.
   */
  // input to cellId in BlockIdx
  protected String getConstraintsBlockInputConnectionCapacityCellList(final int cellId,
      final int blockIdx) {
    String rtn = "";
    final List<NetlistNode> nodeList = getGpccBase().getCellList();
    final List<Set<Integer>> cellBlockList = getGpccBase().getCellBlockList();
    final NetlistNode node = nodeList.get(cellId);
    // get input connections
    for (int i = 0; i < node.getNumInEdge(); i++) {
      final NetlistNode srcNode = node.getInEdgeAtIdx(i).getSrc();
      if (LSResultsUtils.isAllInput(srcNode) || LSResultsUtils.isAllOutput(srcNode)) {
        continue;
      }
      final int srcNodeIdx = nodeList.indexOf(srcNode);
      // block list of otherNode
      final Set<Integer> blockSet = cellBlockList.get(srcNodeIdx);
      final Iterator<Integer> bkiter = blockSet.iterator();
      while (bkiter.hasNext()) {
        final int srcBlockIdx = bkiter.next();
        // not assigned to the same block
        if (srcBlockIdx == blockIdx) {
          continue;
        }
        rtn += assignInt(srcNodeIdx, srcBlockIdx);
        rtn += " ";
      }
    }
    return rtn;
  }

  /**
   * Returns a string representing the Block Output Connection Capacity CellList for cell defined by
   * parameter {@code cellID} and block defined by parameter {@code blockIdx}.
   *
   * @param cellId   The cell index.
   * @param blockIdx The block index.
   * @return A string representing the Block Output Connection Capacity CellList.
   */
  // output from cellId in BlockIdx
  protected String getConstraintsBlockOutputConnectionCapacityCellList(final int cellId,
      final int blockIdx) {
    String rtn = "";
    /*
     * List<NetlistNode> nodeList = this.getGPCC_BASE().getCellList(); List<Set<Integer>>
     * cellBlockList = this.getGPCC_BASE().getCellBlockList(); NetlistNode node =
     * nodeList.get(cellId); // get output connections for (int i = 0; i < node.getNumOutEdge(); i
     * ++) { NetlistNode dstNode = node.getOutEdgeAtIdx(i).getDst(); if
     * (LSResultsUtils.isInput(dstNode) || LSResultsUtils.isOutput(dstNode)) { continue; } int
     * dstNodeIdx = nodeList.indexOf(dstNode); // block list of otherNode Set<Integer> blockSet =
     * cellBlockList.get(dstNodeIdx); Iterator<Integer> bkiter = blockSet.iterator(); while
     * (bkiter.hasNext()) { int dstBlockIdx = bkiter.next(); // not assigned to the same block if
     * (dstBlockIdx == blockIdx) { continue; } rtn += this.AssignInt(dstNodeIdx, dstBlockIdx); rtn
     * += " "; } }.
     */
    rtn += getConstraintsBlockOutputConnectionCapacityCellListDst(cellId, blockIdx, -1);
    return rtn;
  }

  /**
   * Returns a string representing the Block Output Connection Capacity CellList for cell defined by
   * parameter {@code cellID} and block defined by parameter {@code blockIdx} to block defined by
   * parameter {@code dstBlockIdx}.
   *
   * @param cellId      The cell index.
   * @param blockIdx    The block index.
   * @param dstBlockIdx The destination block index.
   * @return A string representing the Block Output Connection Capacity CellList.
   */
  // output from cellId in BlockIdx to DstBlockIdx
  protected String getConstraintsBlockOutputConnectionCapacityCellListDst(final int cellId,
      final int blockIdx, final int dstBlockIdx) {
    String rtn = "";
    final List<NetlistNode> nodeList = getGpccBase().getCellList();
    final List<Set<Integer>> cellBlockList = getGpccBase().getCellBlockList();
    final NetlistNode node = nodeList.get(cellId);
    // get output connections
    for (int i = 0; i < node.getNumOutEdge(); i++) {
      final NetlistNode dstNode = node.getOutEdgeAtIdx(i).getDst();
      if (LSResultsUtils.isAllInput(dstNode) || LSResultsUtils.isAllOutput(dstNode)) {
        continue;
      }
      final int dstNodeIdx = nodeList.indexOf(dstNode);
      // block list of otherNode
      final Set<Integer> blockSet = cellBlockList.get(dstNodeIdx);
      final Iterator<Integer> bkiter = blockSet.iterator();
      while (bkiter.hasNext()) {
        final int myDstBlockIdx = bkiter.next();
        // not assigned to the same block
        if (myDstBlockIdx == blockIdx) {
          continue;
        }
        // flag to skip dstBlockIdx check if negative
        if (dstBlockIdx >= 0 && myDstBlockIdx != dstBlockIdx) {
          continue;
        }
        rtn += assignInt(dstNodeIdx, myDstBlockIdx);
        rtn += " ";
      }
    }
    return rtn;
  }

  /**
   * Returns a string representing the Block Connection Capacity CellList for block defined by
   * parameter {@code blockIdx}, parameter {@code isOutput} defines whether the Connection is for an
   * output or an input.
   *
   * @param isOutput    true if the connection is an output, false otherwise.
   * @param blockIdx    The block index.
   * @param dstBlockIdx The destination block index.
   * @return A string representing the Block Connection Capacity CellList for block defined by
   *         parameter {@code blockIdx},.
   */
  // TODO: make abstract
  protected String getConstraintsConnectionsCapacityCellList(final boolean isOutput,
      final int blockIdx, final int dstBlockIdx) {
    String rtn = "";
    final List<List<Set<Integer>>> blockCapacityCellList = getGpccBase().getBlockCapacityCellList();
    final List<Set<Integer>> allCellList = blockCapacityCellList.get(blockIdx);
    final Set<Integer> cellList = union(allCellList);
    final Iterator<Integer> it = cellList.iterator();
    // for each node in block
    while (it.hasNext()) {
      final int cellId = it.next();
      String dstNodeBlockAssign = "";
      if (isOutput) {
        if (dstBlockIdx < 0) {
          dstNodeBlockAssign =
              getConstraintsBlockOutputConnectionCapacityCellList(cellId, blockIdx);
        } else {
          dstNodeBlockAssign =
              getConstraintsBlockOutputConnectionCapacityCellListDst(cellId, blockIdx, dstBlockIdx);
        }
        dstNodeBlockAssign = "( >  ( + " + dstNodeBlockAssign + ") 0 )";
        // if cellId is in blockIdx
        // and a destination cell is in another block
        // increment output count
        rtn += "( if ( and ";
        rtn += "( > ";
        rtn += assignInt(cellId, blockIdx);
        rtn += GpccBasePWriter.S_SPACE;
        rtn += " 0 )";
        rtn += dstNodeBlockAssign;
        // rtn += ") (add 1 " + dstNodeBlockAssignVar + ") (add 0 " +
        // dstNodeBlockAssignVar + "))";
        rtn += ") 1 0 )";
      } else {
        dstNodeBlockAssign = getConstraintsBlockInputConnectionCapacityCellList(cellId, blockIdx);
        dstNodeBlockAssign = "( + " + dstNodeBlockAssign + ")";
        // if cellId is in blockIdx
        // add the number of input cell in other blocks to count
        rtn += "( if ";
        rtn += "( > ";
        rtn += assignInt(cellId, blockIdx);
        rtn += GpccBasePWriter.S_SPACE;
        rtn += " 0 )";
        // rtn += "(add " + dstNodeBlockAssign + " " + dstNodeBlockAssignVar + ")";
        rtn += dstNodeBlockAssign;
        // rtn += "(add 0 " + dstNodeBlockAssignVar + ") )";
        rtn += " 0 )";
      }
      if (dstNodeBlockAssign.isEmpty()) {
        continue;
      }
    }
    return rtn;
  }

  /**
   * Returns a string representing the Input Connections Capacity Variable for block defined by
   * parameter {@code blockIdx}.
   *
   * @param blockIdx The block index.
   * @return A string representing the Input Connections Capacity Variable for block defined by
   *         parameter {@code blockIdx}.
   */
  protected String getConstraintsInputConnectionsCapacityVariable(final int blockIdx) {
    return GpccBasePWriter.S_BLK + blockIdx + GpccBasePWriter.S_ICC;
  }

  /**
   * Returns a string representing the Output Connections Capacity Variable for block defined by
   * parameter {@code blockIdx}.
   *
   * @param blockIdx The block index.
   * @return A string representing the Output Connections Capacity Variable for block defined by
   *         parameter {@code blockIdx}.
   */
  protected String getConstraintsOutputConnectionsCapacityVariable(final int blockIdx) {
    return GpccBasePWriter.S_BLK + blockIdx + GpccBasePWriter.S_OCC;
  }

  /**
   * Returns a string representing the Input Connections Block Max Connections for block defined by
   * parameter {@code blockIdx}.
   *
   * @param blockIdx The block index.
   * @return A string representing the Input Connections Block Max Connections for block defined by
   *         parameter {@code blockIdx}.
   */
  protected String getConstraintsInputConnectionsCapacityBlockMaxConnections(final int blockIdx) {
    String rtn = "";
    int max = 0;
    final Set<Integer> cellList = union(getGpccBase().getBlockCapacityCellList().get(blockIdx));
    final List<NetlistNode> nodeList = getGpccBase().getCellList();
    final Iterator<Integer> it = cellList.iterator();
    while (it.hasNext()) {
      final NetlistNode node = nodeList.get(it.next());
      for (int i = 0; i < node.getNumInEdge(); i++) {
        final NetlistNode srcNode = node.getInEdgeAtIdx(i).getSrc();
        if (LSResultsUtils.isAllInput(srcNode) || LSResultsUtils.isAllOutput(srcNode)) {
          continue;
        }
        max++;
      }
    }
    rtn += max;
    return rtn;
  }

  /**
   * Returns a string representing the Output Connections Block Max Connections for block defined by
   * parameter {@code blockIdx}.
   *
   * @param blockIdx The block index.
   * @return A string representing the Output Connections Block Max Connections for block defined by
   *         parameter {@code blockIdx}.
   */
  protected String getConstraintsOutputConnectionsCapacityBlockMaxConnections(final int blockIdx) {
    String rtn = "";
    int max = 0;
    final Set<Integer> cellList = union(getGpccBase().getBlockCapacityCellList().get(blockIdx));
    final List<NetlistNode> nodeList = getGpccBase().getCellList();
    final Iterator<Integer> it = cellList.iterator();
    while (it.hasNext()) {
      final NetlistNode node = nodeList.get(it.next());
      for (int i = 0; i < node.getNumOutEdge(); i++) {
        final NetlistNode dstNode = node.getOutEdgeAtIdx(i).getDst();
        if (LSResultsUtils.isAllInput(dstNode) || LSResultsUtils.isAllOutput(dstNode)) {
          continue;
        }
        max++;
      }
    }
    rtn += max;
    return rtn;
  }

  /**
   * Returns a string representing the Output Connections Capacity Prefix for block defined by
   * parameter {@code blockIdx}.
   *
   * @param blockIdx The block index.
   * @return A string representing the Output Connections Capacity Prefix for block defined by
   *         parameter {@code blockIdx}.
   */
  protected abstract String getConstraintsOutputConnectionsCapacityPrefix(int blockIdx);

  /**
   * Returns a string representing the Input Connections Capacity Prefix for block defined by
   * parameter {@code blockIdx}.
   *
   * @param blockIdx The block index.
   * @return A string representing the Input Connections Capacity Prefix for block defined by
   *         parameter {@code blockIdx}.
   */
  protected abstract String getConstraintsInputConnectionsCapacityPrefix(int blockIdx);

  /**
   * Writes the Block Connections Capacity.
   *
   * @param isOutput true if the connection is an output, false otherwise.
   */
  protected void writeConstraintsBlockConnectionsCapacity(final boolean isOutput) {
    String equ = "";
    final List<List<Set<Integer>>> blockCapacityCellList = getGpccBase().getBlockCapacityCellList();
    for (int blockIdx = 0; blockIdx < blockCapacityCellList.size(); blockIdx++) {
      String constraint = "";
      final List<Set<Integer>> allCellList = blockCapacityCellList.get(blockIdx);
      final int size = allCellList.size();
      if (size == 0) {
        continue;
      }
      final Block block = getGpccBase().getBlockList().get(blockIdx);
      Capacity capacity = null;
      for (int capacityIdx = 0; capacityIdx < block.getNumCapacity(); capacityIdx++) {
        if (isOutput) {
          capacity = block.getOutputConnectionsCapacityAtIdx(capacityIdx);
        } else {
          capacity = block.getInputConnectionsCapacityAtIdx(capacityIdx);
        }
        if (capacity == null) {
          continue;
        }
        // header
        constraint +=
            getComment() + getConstraintsBlockCapacityHeader(blockIdx, 0) + Utils.getNewLine();
        if (isOutput) {
          constraint += getConstraintsOutputConnectionsCapacityPrefix(blockIdx);
        } else {
          constraint += getConstraintsInputConnectionsCapacityPrefix(blockIdx);
        }
        equ = getConstraintsConnectionsCapacityCellList(isOutput, blockIdx, -1);
        // identifier lower
        constraint +=
            getComment() + Utils.getTabCharacter() + GpccBasePWriter.S_LOWER + Utils.getNewLine();
        constraint += getConstraintsBlockCapacityLowerBeforeEqn(capacity);
        constraint += equ;
        constraint += getConstraintsBlockCapacityLowerAfterEqn(capacity);
        // identifier upper
        constraint +=
            getComment() + Utils.getTabCharacter() + GpccBasePWriter.S_UPPER + Utils.getNewLine();
        constraint += getConstraintsBlockCapacityUpperBeforeEqn(capacity);
        constraint += equ;
        constraint += getConstraintsBlockCapacityUpperAfterEqn(capacity);

      }
      // write script
      Utils.appendToFile(constraint, getFilename());
    }
  }

  /**
   * Writes the Block Output Connections Capacity.
   */
  protected void writeConstraintsBlockOutputConnectionsCapacity() {
    writeConstraintsBlockConnectionsCapacity(true);
  }

  /**
   * Writes the Block Input Connections Capacity.
   */
  protected void writeConstraintsBlockInputConnectionsCapacity() {
    writeConstraintsBlockConnectionsCapacity(false);
  }

  protected abstract String getConstraintsInOutConnectionsCapacityPrefix(int blockIdx);

  /**
   * Writes the Block InOut Connections Capacity.
   */
  protected void writeConstraintsBlockInOutConnectionsCapacity() {
    String equIn = "";
    String equOut = "";
    String equ = "";
    final List<List<Set<Integer>>> blockCapacityCellList = getGpccBase().getBlockCapacityCellList();
    for (int blockIdx = 0; blockIdx < blockCapacityCellList.size(); blockIdx++) {
      String constraint = "";
      final List<Set<Integer>> allCellList = blockCapacityCellList.get(blockIdx);
      final int size = allCellList.size();
      if (size == 0) {
        continue;
      }
      final Block block = getGpccBase().getBlockList().get(blockIdx);
      Capacity capacity = null;
      for (int capacityIdx = 0; capacityIdx < block.getNumCapacity(); capacityIdx++) {
        capacity = block.getInOutConnectionsCapacityAtIdx(capacityIdx);
        if (capacity == null) {
          continue;
        }
        // header
        constraint +=
            getComment() + getConstraintsBlockCapacityHeader(blockIdx, 0) + Utils.getNewLine();
        // inout
        constraint += getConstraintsInOutConnectionsCapacityPrefix(blockIdx);
        equIn = getConstraintsConnectionsCapacityCellList(false, blockIdx, -1);
        equOut = getConstraintsConnectionsCapacityCellList(true, blockIdx, -1);
        equ = equIn + GpccBasePWriter.S_SPACE + equOut;
        // identifier lower
        constraint +=
            getComment() + Utils.getTabCharacter() + GpccBasePWriter.S_LOWER + Utils.getNewLine();
        constraint += getConstraintsBlockCapacityLowerBeforeEqn(capacity);
        constraint += equ;
        constraint += getConstraintsBlockCapacityLowerAfterEqn(capacity);
        // identifier upper
        constraint +=
            getComment() + Utils.getTabCharacter() + GpccBasePWriter.S_UPPER + Utils.getNewLine();
        constraint += getConstraintsBlockCapacityUpperBeforeEqn(capacity);
        constraint += equ;
        constraint += getConstraintsBlockCapacityUpperAfterEqn(capacity);
      }
      // write script
      Utils.appendToFile(constraint, getFilename());
    }
  }

  /**
   * Returns a string representing the Assignment Prefix.
   *
   * @return A string representing the Assignment Prefix.
   */
  protected abstract String getConstraintsAssignmentPrefix();

  /**
   * Returns a string representing the Assignment Postfix.
   *
   * @return A string representing the Assignment Postfix.
   */
  protected abstract String getConstraintsAssignmentPostfix();

  /**
   * Writes the Assignment.
   */
  protected void writeConstraintsAssignment() {
    String constraint = "";
    final List<Set<Integer>> cellBlockList = getGpccBase().getCellBlockList();
    for (int i = 0; i < cellBlockList.size(); i++) {
      final int size = cellBlockList.get(i).size();
      if (size == 0) {
        continue;
      }
      // header
      constraint += getComment() + GpccBasePWriter.S_ASSIGN + GpccBasePWriter.S_SPACE
          + GpccBasePWriter.S_CELL;
      constraint += i;
      constraint += Utils.getNewLine();
      // compute
      constraint += getConstraintsAssignmentPrefix();
      final Iterator<Integer> it = cellBlockList.get(i).iterator();
      while (it.hasNext()) {
        constraint += GpccBasePWriter.S_SPACE;
        final int blkId = it.next();
        constraint += assignInt(i, blkId);
      }
      constraint += getConstraintsAssignmentPostfix();
      constraint += Utils.getNewLine();
    }
    // write script
    Utils.appendToFile(constraint, getFilename());
  }

  /**
   * Returns a string representing the InterBlock Capacity Header for source block defined by
   * parameter {@code srcBlockIdx}, destination block defined by parameter {@code dstBlockIdx}, and,
   * capacity defined by parameter {@code capacityIdx}.
   *
   * @return A string representing the InterBlock Capacity Header.
   */
  private String getConstraintsInterBlockCapacityHeader(final int srcBlockIdx,
      final int dstBlockIdx, final int capacityIdx) {
    String rtn = "";
    // header
    rtn += Utils.getTabCharacter();
    rtn += GpccBasePWriter.S_BLK + GpccBasePWriter.S_SPACE;
    rtn += srcBlockIdx + GpccBasePWriter.S_SPACE;
    rtn += GpccBasePWriter.S_TO + GpccBasePWriter.S_SPACE + GpccBasePWriter.S_BLK
        + GpccBasePWriter.S_SPACE;
    rtn += dstBlockIdx + GpccBasePWriter.S_SPACE;
    rtn += GpccBasePWriter.S_CAPACITY + GpccBasePWriter.S_SPACE;
    rtn += capacityIdx;
    return rtn;
  }

  /**
   * Returns a string representing the InterBlock Capacity Lower Before Eqn of capacity defined by
   * parameter {@code c}.
   *
   * @return A string representing the InterBlock Capacity Lower Before Eqn of capacity defined by
   *         parameter {@code c}.
   */
  protected abstract String getConstraintsInterBlockCapacityLowerBeforeEqn(Capacity c);

  /**
   * Returns a string representing the InterBlock Capacity Lower After Eqn of capacity defined by
   * parameter {@code c}.
   *
   * @return A string representing the InterBlock Capacity Lower After Eqn of capacity defined by
   *         parameter {@code c}.
   */
  protected abstract String getConstraintsInterBlockCapacityLowerAfterEqn(Capacity c);

  /**
   * Returns a string representing the InterBlock Capacity Upper Before Eqn of capacity defined by
   * parameter {@code c}.
   *
   * @return A string representing the InterBlock Capacity Upper Before Eqn of capacity defined by
   *         parameter {@code c}.
   */
  protected abstract String getConstraintsInterBlockCapacityUpperBeforeEqn(Capacity c);

  /**
   * Returns a string representing the InterBlock Capacity Upper After Eqn of capacity defined by
   * parameter {@code c}.
   *
   * @return A string representing the InterBlock Capacity Upper After Eqn of capacity defined by
   *         parameter {@code c}.
   */
  protected abstract String getConstraintsInterBlockCapacityUpperAfterEqn(Capacity c);

  /**
   * Returns a string representing the InterBlock Equ for source block defined by parameter
   * {@code srcBlockIdx}, and, destination block defined by parameter {@code dstBlockIdx}.
   *
   * @param srcBlockIdx The source block.
   * @param dstBlockIdx The destination block.
   * @return A string representing the InterBlock Equ for source block defined by parameter
   *         {@code srcBlockIdx}, and, destination block defined by parameter {@code dstBlockIdx}.
   */
  protected abstract String getInterBlockEqn(int srcBlockIdx, int dstBlockIdx);

  /**
   * Writes the InterBlock Capacity.
   */
  protected void writeConstraintsInterBlockCapacity() {
    String equ = "";
    final List<Block> blockList = getGpccBase().getBlockList();
    final Partition P = getGpccBase().getPartitioner().getPartition();
    for (int interblockIdx = 0; interblockIdx < P.getNumInterBlock(); interblockIdx++) {
      String constraint = "";
      final InterBlock interblock = P.getInterBlockAtIdx(interblockIdx);
      final Block srcBlock = interblock.getSrcBlock();
      final Block dstBlock = interblock.getDstBlock();
      final int srcBlockIdx = blockList.indexOf(srcBlock);
      final int dstBlockIdx = blockList.indexOf(dstBlock);
      for (int capacityIdx = 0; capacityIdx < interblock.getNumCapacity(); capacityIdx++) {
        final Capacity capacity = interblock.getCapacityAtIdx(capacityIdx);
        // header
        constraint += getComment()
            + getConstraintsInterBlockCapacityHeader(srcBlockIdx, dstBlockIdx, capacityIdx)
            + Utils.getNewLine();
        equ = getInterBlockEqn(srcBlockIdx, dstBlockIdx);
        // identifier lower
        constraint +=
            getComment() + Utils.getTabCharacter() + GpccBasePWriter.S_LOWER + Utils.getNewLine();
        constraint += getConstraintsInterBlockCapacityLowerBeforeEqn(capacity);
        constraint += equ;
        constraint += getConstraintsInterBlockCapacityLowerAfterEqn(capacity);
        // identifier upper
        constraint +=
            getComment() + Utils.getTabCharacter() + GpccBasePWriter.S_UPPER + Utils.getNewLine();
        constraint += getConstraintsInterBlockCapacityUpperBeforeEqn(capacity);
        constraint += equ;
        constraint += getConstraintsInterBlockCapacityUpperAfterEqn(capacity);
      }
      // write script
      Utils.appendToFile(constraint, getFilename());
    }
  }

  /**
   * Writes the content.
   */
  public abstract void writeContent();

  /**
   * Write.
   */
  public void write() throws IOException {
    Utils.deleteFilename(getFilename());
    Utils.createFile(getFilename());
    writeHeader();
    writeContent();
    writeEnd();
  }

  /**
   * Initializes a newly created {@link GpccBasePWriter} with a {@link GpccBase} defined by
   * parameter {@code gpcc}, and filename defined by parameter {@code filename}.
   */
  protected GpccBasePWriter(final GpccBase gpcc, final String filename) {
    setGpccBase(gpcc);
    setFilename(filename);
  }

  /**
   * Setter for {@code filename}.
   *
   * @param filename The value to set {@code filename}.
   */
  protected void setFilename(final String filename) {
    this.filename = filename;
  }

  /**
   * Getter for {@code filename}.
   *
   * @return The value of {@code filename}.
   */
  protected String getFilename() {
    return filename;
  }

  private String filename;

  /**
   * Setter for {@code myGpccBase}.
   *
   * @param myGpccBase The value to set {@code myGpccBase}.
   */
  protected void setGpccBase(final GpccBase myGpccBase) {
    this.myGpccBase = myGpccBase;
  }

  /**
   * Getter for {@code myGpccBase}.
   *
   * @return The value of {@code myGpccBase}.
   */
  protected GpccBase getGpccBase() {
    return myGpccBase;
  }

  private GpccBase myGpccBase;

  static String S_TO = "TO";
  static String S_UPPER = "UPPER";
  static String S_LOWER = "LOWER";
  static String S_NUMEDGE = "numEdge";
  static String S_EDGE = "edge";
  static String S_ADDITION = "+";
  static String S_MULTIPLICATION = "*";
  static String S_EQUAL = "=";
  static String S_OBJECTIVE = "objective";
  static String S_MINIMIZE = "minimize";
  static String S_MAXIMIZE = "maximize";
  static String S_COMMENT = ";";
  static String S_END = GpccBasePWriter.S_COMMENT + "End";
  static String S_ASSIGN = "Assign";
  static String S_CAPACITY = "Capacity";
  static String S_CELL = "C";
  static String S_BLK = "BLK";
  static String S_OCC = "OCC";
  static String S_ICC = "ICC";
  static String S_PARENTHESIS_OPEN = "(";
  static String S_PARENTHESIS_CLOSE = ")";
  static String S_SPACE = " ";
  static String S_INT = "int";
  static String S_DOMAIN = "domain";
  static String S_BINARY = "b";
  static String S_ZERO = "0";
  static String S_ONE = "1";
  static String S_BINARYRANGE =
      GpccBasePWriter.S_ZERO + GpccBasePWriter.S_SPACE + GpccBasePWriter.S_ONE;
  static String S_BINARYDOMAIN = GpccBasePWriter.S_PARENTHESIS_OPEN + GpccBasePWriter.S_DOMAIN
      + GpccBasePWriter.S_SPACE + GpccBasePWriter.S_BINARY + GpccBasePWriter.S_SPACE
      + GpccBasePWriter.S_BINARYRANGE + GpccBasePWriter.S_PARENTHESIS_CLOSE;

}

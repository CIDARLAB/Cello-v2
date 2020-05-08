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

import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.cellocad.v2.common.Utils;
import org.cellocad.v2.common.constraint.LowerBoundType;
import org.cellocad.v2.partitioning.common.Block;
import org.cellocad.v2.partitioning.common.InterBlock;
import org.cellocad.v2.partitioning.common.Partition;
import org.cellocad.v2.partitioning.profile.Capacity;
import org.cellocad.v2.results.netlist.NetlistNode;
import org.cellocad.v2.results.netlist.NetlistNodeUtils;

/**
 * Utility methods for the <i>GPCC_SCIP_BASE</i> instances.
 *
 * @author Vincent Mirian
 * @date 2018-05-21
 */
public class GPCC_SCIP_BASEConstraintUtils {

  /**
   * Returns a aggregated set of integers from the list defined by parameter {@code listSets}.
   *
   * @param listSets a list of sets containing integers.
   * @return A aggregated set of integers from the list defined by parameter {@code listSets}.
   */
  protected static Set<Integer> union(final List<Set<Integer>> listSets) {
    final Set<Integer> rtn = new HashSet<>();
    for (int j = 0; j < listSets.size(); j++) {
      rtn.addAll(listSets.get(j));
    }
    return rtn;
  }

  /*
   * *****************************************************************************
   * ************************ Write SCIP Constraint File
   *****************************************************************************************************/
  /**
   * Write SCIP Objective Function.
   *
   * @param scip scip instance.
   * @param filename filename.
   */
  private static void writeObjectiveFunction(final GPCC_SCIP_BASE scip, final String filename) {
    String constraint = "";
    constraint += GPCC_SCIP_BASEConstraintUtils.S_MINIMIZE;
    constraint += Utils.getNewLine();
    constraint += Utils.getTabCharacter();
    constraint += GPCC_SCIP_BASEConstraintUtils.S_OBJECTIVE;
    constraint += GPCC_SCIP_BASEConstraintUtils.S_IDENTIFIERSEPARATOR;
    constraint += GPCC_SCIP_BASEConstraintUtils.S_NUMEDGE;
    constraint += Utils.getNewLine();
    // write SCIP script
    Utils.appendToFile(constraint, filename);
  }

  /**
   * Write SCIP Objective Function.
   *
   * @param scip Scip instance.
   * @param cell The {@link NetlistNode}.
   * @param block The Block.
   */
  protected static String assign(
      final GPCC_SCIP_BASE scip, final NetlistNode cell, final Block block) {
    String rtn = "";
    final int celli = scip.getCellList().indexOf(cell);
    final int blocki = scip.getBlockList().indexOf(block);
    if (celli == -1 || blocki == -1) {
      throw new RuntimeException("Error with values!");
    }
    rtn +=
        GPCC_SCIP_BASEConstraintUtils.assignInt(
            scip.getCellList().indexOf(cell), scip.getBlockList().indexOf(block));
    return rtn;
  }

  /**
   * Returns a string representation of the assignment variable of cell defined by parameter {@code
   * cell} to the block defined by parameter {@code block}.
   *
   * @param cell An integer representing the cell.
   * @param block An integer representing the block.
   * @return A string representation of the assignment variable of cell defined by parameter {@code
   *     cell} to the block defined by parameter {@code block}.
   */
  protected static String assignInt(final int cell, final int block) {
    String rtn = "";
    rtn += GPCC_SCIP_BASEConstraintUtils.S_ASSIGN;
    rtn += GPCC_SCIP_BASEConstraintUtils.S_CELL;
    rtn += cell;
    rtn += GPCC_SCIP_BASEConstraintUtils.S_BLK;
    rtn += block;
    return rtn;
  }

  /**
   * Returns a string representing the Block Capacity Header for Block index defined by parameter
   * {@code blockIdx} and capacity index defined by parameter {@code capacityIdx}.
   *
   * @param blockIdx The block index.
   * @param capacityIdx The capacity index.
   * @return A string representing the Block Capacity Header.
   */
  private static String getConstraintsBlockCapacityHeader(
      final int blockIdx, final int capacityIdx) {
    String rtn = "";
    // header
    rtn += Utils.getTabCharacter();
    rtn += GPCC_SCIP_BASEConstraintUtils.S_HEADER;
    rtn += Utils.getNewLine();
    rtn += Utils.getTabCharacter();
    rtn += GPCC_SCIP_BASEConstraintUtils.S_COMMENT;
    rtn += Utils.getTabCharacterRepeat(2);
    rtn += GPCC_SCIP_BASEConstraintUtils.S_BLK + GPCC_SCIP_BASEConstraintUtils.S_SPACE;
    rtn += blockIdx + GPCC_SCIP_BASEConstraintUtils.S_SPACE;
    rtn += GPCC_SCIP_BASEConstraintUtils.S_CAPACITY + GPCC_SCIP_BASEConstraintUtils.S_SPACE;
    rtn += capacityIdx;
    rtn += Utils.getNewLine();
    rtn += Utils.getTabCharacter();
    rtn += GPCC_SCIP_BASEConstraintUtils.S_HEADER;
    rtn += Utils.getNewLine();
    return rtn;
  }

  /**
   * Returns a string representing the Block Capacity Identifier for Block index defined by
   * parameter {@code blockIdx} and capacity index defined by parameter {@code capacityIdx}.
   *
   * @param blockIdx The block index.
   * @param capacityIdx The capacity index.
   * @return A string representing the Block Capacity Identifier.
   */
  private static String getConstraintsBlockCapacityIdentifier(
      final int blockIdx, final int capacityIdx) {
    String rtn = "";
    // identifier
    rtn += Utils.getTabCharacter();
    rtn += GPCC_SCIP_BASEConstraintUtils.S_BLK + GPCC_SCIP_BASEConstraintUtils.S_UNDERSCORE;
    rtn += blockIdx + GPCC_SCIP_BASEConstraintUtils.S_UNDERSCORE;
    rtn += GPCC_SCIP_BASEConstraintUtils.S_CAPACITY + GPCC_SCIP_BASEConstraintUtils.S_UNDERSCORE;
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
  private static String getConstraintsBlockCapacityCellList(
      final int blockIdx, final Set<Integer> cellList) {
    String rtn = "";
    final int size = cellList.size();
    int cellIdx = 0;
    final Iterator<Integer> it = cellList.iterator();
    while (it.hasNext()) {
      final int cellId = it.next();
      rtn += GPCC_SCIP_BASEConstraintUtils.assignInt(cellId, blockIdx);
      if (cellIdx < size - 1) {
        rtn +=
            GPCC_SCIP_BASEConstraintUtils.S_SPACE
                + GPCC_SCIP_BASEConstraintUtils.S_ADDITION
                + GPCC_SCIP_BASEConstraintUtils.S_SPACE;
      }
      cellIdx++;
    }
    return rtn;
  }

  /**
   * Writes the Block Capacity.
   *
   * @param scip scip instance.
   * @param filename filename.
   */
  private static void writeConstraintsBlockCapacity(
      final GPCC_SCIP_BASE scip, final String filename) {
    String equ = "";
    final List<List<Set<Integer>>> blockCapacityCellList = scip.getBlockCapacityCellList();
    for (int blockIdx = 0; blockIdx < blockCapacityCellList.size(); blockIdx++) {
      String constraint = "";
      final List<Set<Integer>> allCellList = blockCapacityCellList.get(blockIdx);
      final int size = allCellList.size();
      if (size == 0) {
        continue;
      }
      final Block block = scip.getBlockList().get(blockIdx);
      for (int capacityIdx = 0; capacityIdx < block.getNumCapacity(); capacityIdx++) {
        final Capacity capacity = block.getCapacityAtIdx(capacityIdx);
        final Set<Integer> cellList = allCellList.get(capacityIdx);
        // header
        constraint +=
            GPCC_SCIP_BASEConstraintUtils.getConstraintsBlockCapacityHeader(blockIdx, capacityIdx);
        // identifier lower
        constraint +=
            GPCC_SCIP_BASEConstraintUtils.getConstraintsBlockCapacityIdentifier(
                blockIdx, capacityIdx);
        constraint += GPCC_SCIP_BASEConstraintUtils.S_UNDERSCORE;
        constraint += GPCC_SCIP_BASEConstraintUtils.S_LOWER;
        constraint += GPCC_SCIP_BASEConstraintUtils.S_IDENTIFIERSEPARATOR;
        equ = GPCC_SCIP_BASEConstraintUtils.getConstraintsBlockCapacityCellList(blockIdx, cellList);
        constraint += equ;
        // in LP format: >, >=, => is equivalent to =>
        // capacity lower bound symbol is reduced to => (flipped)
        constraint +=
            GPCC_SCIP_BASEConstraintUtils.S_SPACE
                + LowerBoundType.getStringFlip(capacity.getLowerBoundType())
                + GPCC_SCIP_BASEConstraintUtils.S_SPACE
                + capacity.getLowerBound();
        constraint += Utils.getNewLine();
        // identifier upper
        constraint +=
            GPCC_SCIP_BASEConstraintUtils.getConstraintsBlockCapacityIdentifier(
                blockIdx, capacityIdx);
        constraint += GPCC_SCIP_BASEConstraintUtils.S_UNDERSCORE;
        constraint += GPCC_SCIP_BASEConstraintUtils.S_UPPER;
        constraint += GPCC_SCIP_BASEConstraintUtils.S_IDENTIFIERSEPARATOR;
        constraint += equ;
        // rtn += writeConstraintsBlockCapacityCellList(blockIdx, cellList);
        // in LP format: <, <=, =< is equivalent to <=
        // capacity upper bound symbol is reduced to <, thus substract one from value
        constraint +=
            GPCC_SCIP_BASEConstraintUtils.S_SPACE
                + capacity.getUpperBoundType().toString()
                + GPCC_SCIP_BASEConstraintUtils.S_SPACE
                + (capacity.getUpperBound() - 1);
        constraint += Utils.getNewLine();
      }
      // write SCIP script
      Utils.appendToFile(constraint, filename);
    }
  }

  /**
   * Writes the Block Connections Capacity.
   *
   * @param scip scip instance.
   * @param filename filename.
   */
  @SuppressWarnings("unused")
  private static void writeConstraintsBlockConnectionsCapacity(
      final GPCC_SCIP_BASE scip, final String filename) {
    String equ = "";
    final List<List<Set<Integer>>> blockCapacityCellList = scip.getBlockCapacityCellList();
    for (int blockIdx = 0; blockIdx < blockCapacityCellList.size(); blockIdx++) {
      String constraint = "";
      final List<Set<Integer>> allCellList = blockCapacityCellList.get(blockIdx);
      final int size = allCellList.size();
      if (size == 0) {
        continue;
      }
      final Block block = scip.getBlockList().get(blockIdx);
      Capacity capacity = null;
      for (int capacityIdx = 0; capacityIdx < block.getNumCapacity(); capacityIdx++) {
        capacity = block.getInOutConnectionsCapacityAtIdx(capacityIdx);
        if (capacity == null) {
          continue;
        }
        // get inout eqn
        // TODO: input and output
        final Set<Integer> cellList = allCellList.get(capacityIdx);
        // header
        constraint +=
            GPCC_SCIP_BASEConstraintUtils.getConstraintsBlockCapacityHeader(blockIdx, capacityIdx);
        // identifier lower
        constraint +=
            GPCC_SCIP_BASEConstraintUtils.getConstraintsBlockCapacityIdentifier(
                blockIdx, capacityIdx);
        constraint += GPCC_SCIP_BASEConstraintUtils.S_UNDERSCORE;
        constraint += GPCC_SCIP_BASEConstraintUtils.S_LOWER;
        constraint += GPCC_SCIP_BASEConstraintUtils.S_IDENTIFIERSEPARATOR;
        equ = GPCC_SCIP_BASEConstraintUtils.getConstraintsBlockCapacityCellList(blockIdx, cellList);
        constraint += equ;
        // in LP format: >, >=, => is equivalent to =>
        // capacity lower bound symbol is reduced to => (flipped)
        constraint +=
            GPCC_SCIP_BASEConstraintUtils.S_SPACE
                + LowerBoundType.getStringFlip(capacity.getLowerBoundType())
                + GPCC_SCIP_BASEConstraintUtils.S_SPACE
                + capacity.getLowerBound();
        constraint += Utils.getNewLine();
        // identifier upper
        constraint +=
            GPCC_SCIP_BASEConstraintUtils.getConstraintsBlockCapacityIdentifier(
                blockIdx, capacityIdx);
        constraint += GPCC_SCIP_BASEConstraintUtils.S_UNDERSCORE;
        constraint += GPCC_SCIP_BASEConstraintUtils.S_UPPER;
        constraint += GPCC_SCIP_BASEConstraintUtils.S_IDENTIFIERSEPARATOR;
        constraint += equ;
        // rtn += writeConstraintsBlockCapacityCellList(blockIdx, cellList);
        // in LP format: <, <=, =< is equivalent to <=
        // capacity upper bound symbol is reduced to <, thus substract one from value
        constraint +=
            GPCC_SCIP_BASEConstraintUtils.S_SPACE
                + capacity.getUpperBoundType().toString()
                + GPCC_SCIP_BASEConstraintUtils.S_SPACE
                + (capacity.getUpperBound() - 1);
        constraint += Utils.getNewLine();
        // write SCIP script
        Utils.appendToFile(constraint, filename);
      }
    }
  }

  /**
   * Writes the Assignment.
   *
   * @param scip scip instance.
   * @param filename filename.
   */
  private static void writeConstraintsAssignment(final GPCC_SCIP_BASE scip, final String filename) {
    String constraint = "";
    final List<Set<Integer>> cellBlockList = scip.getCellBlockList();
    for (int i = 0; i < cellBlockList.size(); i++) {
      final int size = cellBlockList.get(i).size();
      if (size == 0) {
        continue;
      }
      // header
      constraint += Utils.getTabCharacter();
      constraint += GPCC_SCIP_BASEConstraintUtils.S_HEADER;
      constraint += Utils.getNewLine();
      constraint += Utils.getTabCharacter();
      constraint += GPCC_SCIP_BASEConstraintUtils.S_COMMENT;
      constraint += Utils.getTabCharacterRepeat(2);
      constraint +=
          GPCC_SCIP_BASEConstraintUtils.S_ASSIGN + " " + GPCC_SCIP_BASEConstraintUtils.S_CELL;
      constraint += i;
      constraint += Utils.getNewLine();
      constraint += Utils.getTabCharacter();
      constraint += GPCC_SCIP_BASEConstraintUtils.S_HEADER;
      constraint += Utils.getNewLine();
      // identifier
      constraint += Utils.getTabCharacter();
      constraint +=
          GPCC_SCIP_BASEConstraintUtils.S_ASSIGN
              + GPCC_SCIP_BASEConstraintUtils.S_CELL
              + GPCC_SCIP_BASEConstraintUtils.S_UNDERSCORE;
      constraint += i;
      constraint += GPCC_SCIP_BASEConstraintUtils.S_IDENTIFIERSEPARATOR;
      final Iterator<Integer> it = cellBlockList.get(i).iterator();
      int j = 0;
      while (it.hasNext()) {
        final int blkId = it.next();
        constraint += GPCC_SCIP_BASEConstraintUtils.assignInt(i, blkId);
        if (j < size - 1) {
          constraint +=
              GPCC_SCIP_BASEConstraintUtils.S_SPACE
                  + GPCC_SCIP_BASEConstraintUtils.S_ADDITION
                  + GPCC_SCIP_BASEConstraintUtils.S_SPACE;
        }
        j++;
      }
      constraint +=
          GPCC_SCIP_BASEConstraintUtils.S_SPACE
              + GPCC_SCIP_BASEConstraintUtils.S_EQUAL
              + GPCC_SCIP_BASEConstraintUtils.S_SPACE
              + GPCC_SCIP_BASEConstraintUtils.S_ONE;
      constraint += Utils.getNewLine();
    }
    // write SCIP script
    Utils.appendToFile(constraint, filename);
  }

  /**
   * Returns a string representing the InterBlock Capacity Header for Source Block index defined by
   * parameter {@code srcBlockIdx}, Destination Block index defined by parameter {@code
   * dstBlockIdx}, and capacity index defined by parameter {@code capacityIdx}.
   *
   * @param srcBlockIdx The source block index.
   * @param dstBlockIdx The destination block index.
   * @param capacityIdx The capacity index.
   * @return A string representing the InterBlock Capacity Header.
   */
  private static String getConstraintsInterBlockCapacityHeader(
      final int srcBlockIdx, final int dstBlockIdx, final int capacityIdx) {
    String rtn = "";
    // header
    rtn += Utils.getTabCharacter();
    rtn += GPCC_SCIP_BASEConstraintUtils.S_HEADER;
    rtn += Utils.getNewLine();
    rtn += Utils.getTabCharacter();
    rtn += GPCC_SCIP_BASEConstraintUtils.S_COMMENT;
    rtn += Utils.getTabCharacterRepeat(2);
    rtn += GPCC_SCIP_BASEConstraintUtils.S_BLK + GPCC_SCIP_BASEConstraintUtils.S_SPACE;
    rtn += srcBlockIdx + GPCC_SCIP_BASEConstraintUtils.S_SPACE;
    rtn +=
        GPCC_SCIP_BASEConstraintUtils.S_TO
            + GPCC_SCIP_BASEConstraintUtils.S_SPACE
            + GPCC_SCIP_BASEConstraintUtils.S_BLK
            + GPCC_SCIP_BASEConstraintUtils.S_SPACE;
    rtn += dstBlockIdx + GPCC_SCIP_BASEConstraintUtils.S_SPACE;
    rtn += GPCC_SCIP_BASEConstraintUtils.S_CAPACITY + GPCC_SCIP_BASEConstraintUtils.S_SPACE;
    rtn += capacityIdx;
    rtn += Utils.getNewLine();
    rtn += Utils.getTabCharacter();
    rtn += GPCC_SCIP_BASEConstraintUtils.S_HEADER;
    rtn += Utils.getNewLine();
    return rtn;
  }

  /**
   * Returns a string representing the InterBlock Capacity Header for Source Block index defined by
   * parameter {@code srcBlockIdx}, Destination Block index defined by parameter {@code
   * dstBlockIdx}, and capacity index defined by parameter {@code capacityIdx}.
   *
   * @param srcBlockIdx The source block index.
   * @param dstBlockIdx The destination block index.
   * @param capacityIdx The capacity index.
   * @return A string representing the InterBlock Capacity Header.
   */
  private static String getConstraintsInterBlockCapacityIdentifier(
      final int srcBlockIdx, final int dstBlockIdx, final int capacityIdx) {
    String rtn = "";
    // identifier
    rtn += Utils.getTabCharacter();
    rtn += GPCC_SCIP_BASEConstraintUtils.S_BLK + GPCC_SCIP_BASEConstraintUtils.S_UNDERSCORE;
    rtn += srcBlockIdx + GPCC_SCIP_BASEConstraintUtils.S_UNDERSCORE;
    rtn +=
        GPCC_SCIP_BASEConstraintUtils.S_TO
            + GPCC_SCIP_BASEConstraintUtils.S_UNDERSCORE
            + GPCC_SCIP_BASEConstraintUtils.S_BLK
            + GPCC_SCIP_BASEConstraintUtils.S_UNDERSCORE;
    rtn += dstBlockIdx;
    rtn +=
        GPCC_SCIP_BASEConstraintUtils.S_UNDERSCORE
            + GPCC_SCIP_BASEConstraintUtils.S_CAPACITY
            + GPCC_SCIP_BASEConstraintUtils.S_UNDERSCORE;
    rtn += capacityIdx;
    return rtn;
  }

  /**
   * Returns a string representing the Edge Equation.
   *
   * @param srcCells source cells.
   * @param dstCells dstCells cells.
   * @param nodeList node list.
   * @param srcBlockIdx source block index.
   * @param dstBlockIdx destination block index.
   * @return A string representing the Edge Equation.
   */
  private static String getEdgeEquation(
      final Set<Integer> srcCells,
      final Set<Integer> dstCells,
      final List<NetlistNode> nodeList,
      final int srcBlockIdx,
      final int dstBlockIdx) {
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
          term += Integer.toString(coefficient) + GPCC_SCIP_BASEConstraintUtils.S_SPACE;
          term += GPCC_SCIP_BASEConstraintUtils.assignInt(j, srcBlockIdx);
          term +=
              GPCC_SCIP_BASEConstraintUtils.S_SPACE
                  + GPCC_SCIP_BASEConstraintUtils.S_MULTIPLICATION
                  + GPCC_SCIP_BASEConstraintUtils.S_SPACE;
          term += GPCC_SCIP_BASEConstraintUtils.assignInt(k, dstBlockIdx);
        }
        // if term is not empty add to objective function
        if (!term.isEmpty()) {
          if (!rtn.isEmpty()) {
            rtn +=
                GPCC_SCIP_BASEConstraintUtils.S_SPACE
                    + GPCC_SCIP_BASEConstraintUtils.S_ADDITION
                    + GPCC_SCIP_BASEConstraintUtils.S_SPACE;
          }
          rtn +=
              GPCC_SCIP_BASEConstraintUtils.S_SPACE
                  + GPCC_SCIP_BASEConstraintUtils.S_SQUARE_BRACKET_OPEN
                  + GPCC_SCIP_BASEConstraintUtils.S_SPACE;
          rtn += term;
          rtn +=
              GPCC_SCIP_BASEConstraintUtils.S_SPACE
                  + GPCC_SCIP_BASEConstraintUtils.S_SQUARE_BRACKET_CLOSE
                  + GPCC_SCIP_BASEConstraintUtils.S_SPACE;
        }
      }
    }
    return rtn;
  }

  /**
   * Writes the InterBlock Capacity.
   *
   * @param scip scip instance.
   * @param filename filename.
   */
  private static void writeConstraintsInterBlockCapacity(
      final GPCC_SCIP_BASE scip, final String filename) {
    String equ = "";
    final List<Block> blockList = scip.getBlockList();
    final List<List<Set<Integer>>> blockCapacityCellList = scip.getBlockCapacityCellList();
    final List<NetlistNode> nodeList = scip.getCellList();
    final Partition P = scip.getPartitioner().getPartition();
    for (int interblockIdx = 0; interblockIdx < P.getNumInterBlock(); interblockIdx++) {
      String constraint = "";
      final InterBlock interblock = P.getInterBlockAtIdx(interblockIdx);
      final Block srcBlock = interblock.getSrcBlock();
      final Block dstBlock = interblock.getDstBlock();
      final int srcBlockIdx = blockList.indexOf(srcBlock);
      final int dstBlockIdx = blockList.indexOf(dstBlock);
      final Set<Integer> srcCells =
          GPCC_SCIP_BASEConstraintUtils.union(blockCapacityCellList.get(srcBlockIdx));
      final Set<Integer> dstCells =
          GPCC_SCIP_BASEConstraintUtils.union(blockCapacityCellList.get(dstBlockIdx));
      for (int capacityIdx = 0; capacityIdx < interblock.getNumCapacity(); capacityIdx++) {
        final Capacity capacity = interblock.getCapacityAtIdx(capacityIdx);
        // header
        constraint +=
            GPCC_SCIP_BASEConstraintUtils.getConstraintsInterBlockCapacityHeader(
                srcBlockIdx, dstBlockIdx, capacityIdx);
        // identifier lower
        constraint +=
            GPCC_SCIP_BASEConstraintUtils.getConstraintsInterBlockCapacityIdentifier(
                srcBlockIdx, dstBlockIdx, capacityIdx);
        constraint += GPCC_SCIP_BASEConstraintUtils.S_UNDERSCORE;
        constraint += GPCC_SCIP_BASEConstraintUtils.S_LOWER;
        constraint += GPCC_SCIP_BASEConstraintUtils.S_IDENTIFIERSEPARATOR;
        equ =
            GPCC_SCIP_BASEConstraintUtils.getEdgeEquation(
                srcCells, dstCells, nodeList, srcBlockIdx, dstBlockIdx);
        constraint += equ;
        // in LP format: >, >=, => is equivalent to =>
        // capacity lower bound symbol is reduced to => (flipped)
        constraint +=
            GPCC_SCIP_BASEConstraintUtils.S_SPACE
                + LowerBoundType.getStringFlip(capacity.getLowerBoundType())
                + GPCC_SCIP_BASEConstraintUtils.S_SPACE
                + capacity.getLowerBound();
        constraint += Utils.getNewLine();
        // identifier upper
        constraint +=
            GPCC_SCIP_BASEConstraintUtils.getConstraintsInterBlockCapacityIdentifier(
                srcBlockIdx, dstBlockIdx, capacityIdx);
        constraint += GPCC_SCIP_BASEConstraintUtils.S_UNDERSCORE;
        constraint += GPCC_SCIP_BASEConstraintUtils.S_UPPER;
        constraint += GPCC_SCIP_BASEConstraintUtils.S_IDENTIFIERSEPARATOR;
        constraint += equ;
        // in LP format: <, <=, =< is equivalent to <=
        // capacity upper bound symbol is reduced to <, thus substract one from value
        constraint +=
            GPCC_SCIP_BASEConstraintUtils.S_SPACE
                + capacity.getUpperBoundType().toString()
                + GPCC_SCIP_BASEConstraintUtils.S_SPACE
                + (capacity.getUpperBound() - 1);
        constraint += Utils.getNewLine();
      }
      // write SCIP script
      Utils.appendToFile(constraint, filename);
    }
  }

  /**
   * Returns a string representation the total number of edges.
   *
   * @param scip scip instance.
   * @return A string representation the total number of edges.
   */
  public static String getConstraintsObjectiveTotalEdges(final GPCC_SCIP_BASE scip) {
    String rtn = "";
    int totalEdges = 0;
    final List<NetlistNode> nodeList = scip.getCellList();
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
   * Writes Objective.
   *
   * @param scip scip instance.
   * @param filename filename.
   */
  private static void writeConstraintsObjective(final GPCC_SCIP_BASE scip, final String filename) {
    String constraint = "";
    String equ = "";
    final List<List<Set<Integer>>> blockCapacityCellList = scip.getBlockCapacityCellList();
    final List<NetlistNode> nodeList = scip.getCellList();
    final int sizeI = blockCapacityCellList.size();
    if (sizeI != 0) {
      // header
      constraint += Utils.getTabCharacter();
      constraint += GPCC_SCIP_BASEConstraintUtils.S_HEADER;
      constraint += Utils.getNewLine();
      constraint += Utils.getTabCharacter();
      constraint += GPCC_SCIP_BASEConstraintUtils.S_COMMENT;
      constraint += Utils.getTabCharacterRepeat(2);
      constraint += GPCC_SCIP_BASEConstraintUtils.S_OBJECTIVE;
      constraint += Utils.getNewLine();
      constraint += Utils.getTabCharacter();
      constraint += GPCC_SCIP_BASEConstraintUtils.S_HEADER;
      constraint += Utils.getNewLine();
      // identifier
      constraint += Utils.getTabCharacter();
      constraint += GPCC_SCIP_BASEConstraintUtils.S_OBJECTIVEEQUATION;
      constraint += GPCC_SCIP_BASEConstraintUtils.S_IDENTIFIERSEPARATOR;
      constraint += GPCC_SCIP_BASEConstraintUtils.S_NUMEDGE;
      // for each block
      for (int i = 0; i < sizeI; i++) {
        final List<Set<Integer>> allCellList = blockCapacityCellList.get(i);
        final Set<Integer> cellList = GPCC_SCIP_BASEConstraintUtils.union(allCellList);
        equ = GPCC_SCIP_BASEConstraintUtils.getEdgeEquation(cellList, cellList, nodeList, i, i);
        if (!equ.isEmpty()) {
          constraint +=
              GPCC_SCIP_BASEConstraintUtils.S_SPACE
                  + GPCC_SCIP_BASEConstraintUtils.S_ADDITION
                  + GPCC_SCIP_BASEConstraintUtils.S_SPACE;
        }
        constraint += equ;
      }
      constraint +=
          GPCC_SCIP_BASEConstraintUtils.S_EQUAL
              + GPCC_SCIP_BASEConstraintUtils.S_SPACE
              + GPCC_SCIP_BASEConstraintUtils.getConstraintsObjectiveTotalEdges(scip);
      constraint += Utils.getNewLine();
    }
    // write SCIP script
    Utils.appendToFile(constraint, filename);
  }

  /**
   * Writes Subject To.
   *
   * @param scip scip instance.
   * @param filename filename.
   */
  private static void writeSubjectTo(final GPCC_SCIP_BASE scip, final String filename) {
    String constraint = "";
    constraint += GPCC_SCIP_BASEConstraintUtils.S_SUBJECTTO;
    constraint += Utils.getNewLine();
    // write SCIP script
    Utils.appendToFile(constraint, filename);
  }

  /**
   * Writes Constraints.
   *
   * @param scip scip instance.
   * @param filename filename.
   */
  private static void writeConstraints(final GPCC_SCIP_BASE scip, final String filename) {
    GPCC_SCIP_BASEConstraintUtils.writeSubjectTo(scip, filename);
    GPCC_SCIP_BASEConstraintUtils.writeConstraintsBlockCapacity(scip, filename);
    // GPCCSCIPBaseConstraintUtils.writeConstraintsBlockConnectionsCapacity(scip,
    // filename);
    GPCC_SCIP_BASEConstraintUtils.writeConstraintsAssignment(scip, filename);
    GPCC_SCIP_BASEConstraintUtils.writeConstraintsInterBlockCapacity(scip, filename);
    GPCC_SCIP_BASEConstraintUtils.writeConstraintsObjective(scip, filename);
  }

  /**
   * Writes Binary.
   *
   * @param scip scip instance.
   * @param filename filename.
   */
  private static void writeBinary(final GPCC_SCIP_BASE scip, final String filename) {
    String constraint = "";
    constraint += GPCC_SCIP_BASEConstraintUtils.S_BINARY;
    constraint += Utils.getNewLine();
    final List<Set<Integer>> cellBlockList = scip.getCellBlockList();
    for (int i = 0; i < cellBlockList.size(); i++) {
      final int size = cellBlockList.get(i).size();
      if (size == 0) {
        continue;
      }
      final Iterator<Integer> it = cellBlockList.get(i).iterator();
      while (it.hasNext()) {
        final int blkId = it.next();
        constraint += Utils.getTabCharacter();
        constraint += GPCC_SCIP_BASEConstraintUtils.assignInt(i, blkId);
        constraint += Utils.getNewLine();
      }
    }
    // write SCIP script
    Utils.appendToFile(constraint, filename);
  }

  /**
   * Writes the End.
   *
   * @param scip scip instance.
   * @param filename filename.
   */
  private static void writeEnd(final GPCC_SCIP_BASE scip, final String filename) {
    String constraint = "";
    constraint += GPCC_SCIP_BASEConstraintUtils.S_END;
    constraint += Utils.getNewLine();
    // write SCIP script
    Utils.appendToFile(constraint, filename);
  }

  /**
   * Writes SCIP Constraint File.
   *
   * @param scip scip instance.
   * @param filename filename.
   * @throws IOException Unable to create file.
   */
  public static void writeScipConstraintFile(final GPCC_SCIP_BASE scip, final String filename)
      throws IOException {
    Utils.deleteFilename(filename);
    Utils.createFile(filename);
    GPCC_SCIP_BASEConstraintUtils.writeObjectiveFunction(scip, filename);
    GPCC_SCIP_BASEConstraintUtils.writeConstraints(scip, filename);
    /*
     * this.writeBounds(); this.writeGeneral();.
     */
    GPCC_SCIP_BASEConstraintUtils.writeBinary(scip, filename);
    GPCC_SCIP_BASEConstraintUtils.writeEnd(scip, filename);
  }

  private static String S_MINIMIZE = "Minimize";
  private static String S_SUBJECTTO = "Subject To";
  private static String S_BINARY = "Binary";
  private static String S_END = "End";
  private static String S_OBJECTIVE = "Objective";
  private static String S_OBJECTIVEEQUATION = "ObjectiveEquation";
  private static String S_CAPACITY = "Capacity";
  private static String S_ASSIGN = "Assign";
  private static String S_CELL = "C";
  private static String S_BLK = "BLK";
  private static String S_TO = "TO";
  private static String S_UPPER = "UPPER";
  private static String S_LOWER = "LOWER";
  private static String S_NUMEDGE = "numEdge";
  private static String S_HEADER = "\\*************************************";
  private static String S_COMMENT = "\\";
  private static String S_SQUARE_BRACKET_OPEN = "[";
  private static String S_SQUARE_BRACKET_CLOSE = "]";
  private static String S_EQUAL = "=";
  private static String S_ONE = "1";
  private static String S_ADDITION = "+";
  private static String S_MULTIPLICATION = "*";
  private static String S_COLON = ":";
  private static String S_SPACE = " ";
  private static String S_UNDERSCORE = "_";
  private static String S_IDENTIFIERSEPARATOR =
      GPCC_SCIP_BASEConstraintUtils.S_COLON + GPCC_SCIP_BASEConstraintUtils.S_SPACE;
}

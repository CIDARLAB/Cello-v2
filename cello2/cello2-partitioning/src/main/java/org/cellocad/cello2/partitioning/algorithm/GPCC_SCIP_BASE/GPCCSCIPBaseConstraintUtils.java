/**
 * Copyright (C) 2017 Massachusetts Institute of Technology (MIT)
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:

 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.

 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package org.cellocad.cello2.partitioning.algorithm.GPCC_SCIP_BASE;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.cellocad.cello2.common.Utils;
import org.cellocad.cello2.common.constraint.LowerBoundType;
import org.cellocad.cello2.partitioning.common.Block;
import org.cellocad.cello2.partitioning.common.InterBlock;
import org.cellocad.cello2.partitioning.common.Partition;
import org.cellocad.cello2.partitioning.profile.Capacity;
import org.cellocad.cello2.results.netlist.NetlistNode;
import org.cellocad.cello2.results.netlist.NetlistNodeUtils;

/**
 * The GPCCSCIPBaseConstraintUtils class is class with utility methods for the <i>GPCC_SCIP_BASE</i> instances.
 * 
 * @author Vincent Mirian
 * 
 * @date 2018-05-21
 *
 */
public class GPCCSCIPBaseConstraintUtils {

	/**
	 *  Returns a aggregated set of integers from the list defined by parameter <i>listSets</i>
	 *  
	 *  @param listSets a list of sets containing integers
	 *  @return a aggregated set of integers from the list defined by parameter <i>listSets</i>
	 */
	protected static Set<Integer> union(List<Set<Integer>> listSets){
		Set<Integer> rtn = new HashSet<Integer>();
		for (int j = 0; j < listSets.size(); j++) {
			rtn.addAll(listSets.get(j));
		}
		return rtn;
	}
	/* *****************************************************************************************************
	 *  Write SCIP Constraint File
	 * *****************************************************************************************************/
	/**
	 *  Write SCIP Objective Function
	 *  
	 *  @param scip scip instance
	 *  @param filename filename
	 */
	static private void writeObjectiveFunction(GPCC_SCIP_BASE scip, String filename) {
		String constraint = "";
		constraint += S_MINIMIZE;
		constraint += Utils.getNewLine();
		constraint += Utils.getTabCharacter();
		constraint += S_OBJECTIVE;
		constraint += S_IDENTIFIERSEPARATOR;
		constraint += S_NUMEDGE;
		constraint += Utils.getNewLine();
		// write SCIP script
		Utils.appendToFile(constraint, filename);		
	}

	/**
	 *  Write SCIP Objective Function
	 *  
	 *  @param scip scip instance
	 *  @param Cell the NetlistNode
	 *  @param Block the Block
	 */
	static protected String Assign(GPCC_SCIP_BASE scip, final NetlistNode Cell, final Block Block) {
		String rtn = "";
		int celli = scip.getCellList().indexOf(Cell);
		int blocki = scip.getBlockList().indexOf(Block);
		if ((celli == -1) ||
			(blocki== -1)) {
			throw new RuntimeException("Error with values!");
		}
		rtn += GPCCSCIPBaseConstraintUtils.AssignInt(scip.getCellList().indexOf(Cell), scip.getBlockList().indexOf(Block));
		return rtn;
	}

	/**
	 *  Returns a string representation of the assignment variable of cell defined by parameter <i>Cell</i>
	 *  to the block defined by parameter <i>Block</i>
	 *  
	 *  @param Cell an integer representing the cell
	 *  @param Block an integer representing the block
	 *  @return a string representation of the assignment variable of cell defined by parameter <i>Cell</i>
	 *  to the block defined by parameter <i>Block</i>
	 */
	static protected String AssignInt(final int Cell, final int Block) {
		String rtn = "";
		rtn += S_ASSIGN;
		rtn += S_CELL;
		rtn += Cell;
		rtn += S_BLK;
		rtn += Block;
		return rtn;
	}
	
	/**
	 *  Returns a string representing the Block Capacity Header for Block index defined by parameter <i>blockIdx</i>
	 *  and capacity index defined by parameter <i>capacityIdx</i>
	 *  
	 *  @param blockIdx the block index
	 *  @param capacityIdx the capacity index
	 *  @return a string representing the Block Capacity Header
	 */
	static private String getConstraintsBlockCapacityHeader(int blockIdx, int capacityIdx) {
		String rtn = "";
		// header
		rtn += Utils.getTabCharacter();
		rtn += S_HEADER;
		rtn += Utils.getNewLine();
		rtn += Utils.getTabCharacter();
		rtn += S_COMMENT;
		rtn += Utils.getTabCharacterRepeat(2);
		rtn += S_BLK + S_SPACE;
		rtn += blockIdx + S_SPACE;
		rtn += S_CAPACITY + S_SPACE;
		rtn += capacityIdx;
		rtn += Utils.getNewLine();
		rtn += Utils.getTabCharacter();
		rtn += S_HEADER;
		rtn += Utils.getNewLine();
		return rtn;		
	}
	/**
	 *  Returns a string representing the Block Capacity Identifier for Block index defined by parameter <i>blockIdx</i>
	 *  and capacity index defined by parameter <i>capacityIdx</i>
	 *  
	 *  @param blockIdx the block index
	 *  @param capacityIdx the capacity index
	 *  @return a string representing the Block Capacity Identifier
	 */
	static private String getConstraintsBlockCapacityIdentifier(int blockIdx, int capacityIdx) {
		String rtn = "";
		// identifier
		rtn += Utils.getTabCharacter();
		rtn += S_BLK + S_UNDERSCORE;
		rtn += blockIdx + S_UNDERSCORE;
		rtn += S_CAPACITY + S_UNDERSCORE;
		rtn += capacityIdx;
		return rtn;		
	}

	/**
	 *  Returns a string representing the Block Capacity CellList for Block index defined by parameter <i>blockIdx</i>
	 *  and set of integer defined by cellList <i>cellList</i>
	 *  
	 *  @param blockIdx the block index
	 *  @param cellList the cellList
	 *  @return a string representing the Block Capacity CellList
	 */
	static private String getConstraintsBlockCapacityCellList(int blockIdx, Set<Integer> cellList) {
		String rtn = "";
		int size = cellList.size();
		int cellIdx = 0;
		Iterator<Integer> it = cellList.iterator();
		while (it.hasNext()) {
			int cellId = it.next();
			rtn += AssignInt(cellId, blockIdx);
			if (cellIdx < size - 1) {
				rtn += S_SPACE + S_ADDITION + S_SPACE;
			}
			cellIdx++;
		}
		return rtn;
	}
	
	/**
	 *  Writes the Block Capacity
	 *  
	 *  @param scip scip instance
	 *  @param filename filename
	 */
	static private void writeConstraintsBlockCapacity(GPCC_SCIP_BASE scip, String filename) {
		String equ = "";
		List<List<Set<Integer>>> blockCapacityCellList = scip.getBlockCapacityCellList();
		for (int blockIdx = 0; blockIdx < blockCapacityCellList.size(); blockIdx++) {
			String constraint = "";
			List<Set<Integer>> allCellList = blockCapacityCellList.get(blockIdx);
			int size = allCellList.size();
			if (size == 0) {
				continue;
			}
			Block block = scip.getBlockList().get(blockIdx);
			for (int capacityIdx = 0; capacityIdx < block.getNumCapacity(); capacityIdx++) {
				Capacity capacity = block.getCapacityAtIdx(capacityIdx);
				Set<Integer> cellList = allCellList.get(capacityIdx);
				// header
				constraint += GPCCSCIPBaseConstraintUtils.getConstraintsBlockCapacityHeader(blockIdx, capacityIdx);
				// identifier lower
				constraint += GPCCSCIPBaseConstraintUtils.getConstraintsBlockCapacityIdentifier(blockIdx, capacityIdx);
				constraint += S_UNDERSCORE;
				constraint += S_LOWER;
				constraint += S_IDENTIFIERSEPARATOR;
				equ = GPCCSCIPBaseConstraintUtils.getConstraintsBlockCapacityCellList(blockIdx, cellList);
				constraint += equ;
				// in LP format: >, >=, => is equivalent to =>
				// capacity lower bound symbol is reduced to => (flipped)
				constraint += S_SPACE + LowerBoundType.getStringFlip(capacity.getLowerBoundType()) + S_SPACE + capacity.getLowerBound();
				constraint += Utils.getNewLine();
				// identifier upper
				constraint += GPCCSCIPBaseConstraintUtils.getConstraintsBlockCapacityIdentifier(blockIdx, capacityIdx);
				constraint += S_UNDERSCORE;
				constraint += S_UPPER;
				constraint += S_IDENTIFIERSEPARATOR;
				constraint += equ;
				//rtn += writeConstraintsBlockCapacityCellList(blockIdx, cellList);
				// in LP format: <, <=, =< is equivalent to <=
				// capacity upper bound symbol is reduced to <, thus substract one from value
				constraint += S_SPACE + capacity.getUpperBoundType().toString() + S_SPACE + (capacity.getUpperBound() - 1);
				constraint += Utils.getNewLine();				
			}
			// write SCIP script
			Utils.appendToFile(constraint, filename);
		}
	}

	/**
	 *  Writes the Block Connections Capacity
	 *  
	 *  @param scip scip instance
	 *  @param filename filename
	 */
	static private void writeConstraintsBlockConnectionsCapacity(GPCC_SCIP_BASE scip, String filename) {
		String equ = "";
		List<List<Set<Integer>>> blockCapacityCellList = scip.getBlockCapacityCellList();
		for (int blockIdx = 0; blockIdx < blockCapacityCellList.size(); blockIdx++) {
			String constraint = "";
			List<Set<Integer>> allCellList = blockCapacityCellList.get(blockIdx);
			int size = allCellList.size();
			if (size == 0) {
				continue;
			}
			Block block = scip.getBlockList().get(blockIdx);
			Capacity capacity = null;
			for (int capacityIdx = 0; capacityIdx < block.getNumCapacity(); capacityIdx++) {
				capacity = block.getInOutConnectionsCapacityAtIdx(capacityIdx);
				if (capacity == null) {
					continue;
				}
				// get inout eqn				
				// TODO: input and output
				Set<Integer> cellList = allCellList.get(capacityIdx);
				// header
				constraint += GPCCSCIPBaseConstraintUtils.getConstraintsBlockCapacityHeader(blockIdx, capacityIdx);
				// identifier lower
				constraint += GPCCSCIPBaseConstraintUtils.getConstraintsBlockCapacityIdentifier(blockIdx, capacityIdx);
				constraint += S_UNDERSCORE;
				constraint += S_LOWER;
				constraint += S_IDENTIFIERSEPARATOR;
				equ = GPCCSCIPBaseConstraintUtils.getConstraintsBlockCapacityCellList(blockIdx, cellList);
				constraint += equ;
				// in LP format: >, >=, => is equivalent to =>
				// capacity lower bound symbol is reduced to => (flipped)
				constraint += S_SPACE + LowerBoundType.getStringFlip(capacity.getLowerBoundType()) + S_SPACE + capacity.getLowerBound();
				constraint += Utils.getNewLine();
				// identifier upper
				constraint += GPCCSCIPBaseConstraintUtils.getConstraintsBlockCapacityIdentifier(blockIdx, capacityIdx);
				constraint += S_UNDERSCORE;
				constraint += S_UPPER;
				constraint += S_IDENTIFIERSEPARATOR;
				constraint += equ;
				//rtn += writeConstraintsBlockCapacityCellList(blockIdx, cellList);
				// in LP format: <, <=, =< is equivalent to <=
				// capacity upper bound symbol is reduced to <, thus substract one from value
				constraint += S_SPACE + capacity.getUpperBoundType().toString() + S_SPACE + (capacity.getUpperBound() - 1);
				constraint += Utils.getNewLine();				
				// write SCIP script
				Utils.appendToFile(constraint, filename);
			}
		}
	}
	
	/**
	 *  Writes the Assignment
	 *  
	 *  @param scip scip instance
	 *  @param filename filename
	 */	
	static private void writeConstraintsAssignment(GPCC_SCIP_BASE scip, String filename) {
		String constraint = "";
		List<Set<Integer>> cellBlockList = scip.getCellBlockList();
		for (int i = 0; i < cellBlockList.size(); i++) {
			int size = cellBlockList.get(i).size();
			if (size == 0) {
				continue;
			}
			// header
			constraint += Utils.getTabCharacter();
			constraint += S_HEADER;
			constraint += Utils.getNewLine();
			constraint += Utils.getTabCharacter();
			constraint += S_COMMENT;
			constraint += Utils.getTabCharacterRepeat(2);
			constraint += S_ASSIGN + " " + S_CELL;
			constraint += i;
			constraint += Utils.getNewLine();
			constraint += Utils.getTabCharacter();
			constraint += S_HEADER;
			constraint += Utils.getNewLine();
			// identifier
			constraint += Utils.getTabCharacter();
			constraint += S_ASSIGN + S_CELL + S_UNDERSCORE;
			constraint += i;
			constraint += S_IDENTIFIERSEPARATOR;
			Iterator<Integer> it = cellBlockList.get(i).iterator();
			int j = 0;
			while(it.hasNext()){
				int blkId = it.next();
				constraint += AssignInt(i, blkId);
				if (j < size -1) {
					constraint += S_SPACE + S_ADDITION + S_SPACE;
				}
				j++;
			}
			constraint += S_SPACE + S_EQUAL + S_SPACE + S_ONE;			
			constraint += Utils.getNewLine();
		}		
		// write SCIP script
		Utils.appendToFile(constraint, filename);
	}
	
	/**
	 *  Returns a string representing the InterBlock Capacity Header for Source Block index defined by parameter <i>srcBlockIdx</i>,
	 *  Destination Block index defined by parameter <i>dstBlockIdx</i>,
	 *  and capacity index defined by parameter <i>capacityIdx</i>
	 *  
	 *  @param srcBlockIdx the source block index
	 *  @param dstBlockIdx the destination block index
	 *  @param capacityIdx the capacity index
	 *  @return a string representing the InterBlock Capacity Header
	 */
	static private String getConstraintsInterBlockCapacityHeader(int srcBlockIdx, int dstBlockIdx, int capacityIdx) {
		String rtn = "";
		// header
		rtn += Utils.getTabCharacter();
		rtn += S_HEADER;
		rtn += Utils.getNewLine();
		rtn += Utils.getTabCharacter();
		rtn += S_COMMENT;
		rtn += Utils.getTabCharacterRepeat(2);
		rtn += S_BLK + S_SPACE;
		rtn += srcBlockIdx + S_SPACE;
		rtn += S_TO + S_SPACE + S_BLK + S_SPACE;
		rtn += dstBlockIdx + S_SPACE;
		rtn += S_CAPACITY + S_SPACE;
		rtn += capacityIdx;
		rtn += Utils.getNewLine();
		rtn += Utils.getTabCharacter();
		rtn += S_HEADER;
		rtn += Utils.getNewLine();
		return rtn;		
	}

	/**
	 *  Returns a string representing the InterBlock Capacity Header for Source Block index defined by parameter <i>srcBlockIdx</i>,
	 *  Destination Block index defined by parameter <i>dstBlockIdx</i>,
	 *  and capacity index defined by parameter <i>capacityIdx</i>
	 *  
	 *  @param srcBlockIdx the source block index
	 *  @param dstBlockIdx the destination block index
	 *  @param capacityIdx the capacity index
	 *  @return a string representing the InterBlock Capacity Header
	 */
	static private String getConstraintsInterBlockCapacityIdentifier(int srcBlockIdx, int dstBlockIdx, int capacityIdx) {
		String rtn = "";
		// identifier
		rtn += Utils.getTabCharacter();
		rtn += S_BLK + S_UNDERSCORE;
		rtn += srcBlockIdx + S_UNDERSCORE;
		rtn += S_TO + S_UNDERSCORE + S_BLK + S_UNDERSCORE;
		rtn += dstBlockIdx;
		rtn += S_UNDERSCORE + S_CAPACITY + S_UNDERSCORE;
		rtn += capacityIdx;
		return rtn;
	}

	/**
	 *  Returns a string representing the Edge Equation
	 *  
	 *  @param srcCells source cells
	 *  @param dstCells dstCells cells
	 *  @param nodeList node list
	 *  @param srcBlockIdx source block index
	 *  @param dstBlockIdx destination block index
	 *  @return a string representing the Edge Equation
	 */
	static private String getEdgeEquation(
			Set<Integer> srcCells,
			Set<Integer> dstCells,
			List<NetlistNode> nodeList,
			int srcBlockIdx,
			int dstBlockIdx
			) {
		String rtn = "";
		// for each source cell
		Iterator<Integer> it0 = srcCells.iterator();
		while(it0.hasNext()){
			int j = it0.next();
			// for each destination cell
			Iterator<Integer> it1 = dstCells.iterator();
			while(it1.hasNext()){
				int k = it1.next();
				String term = "";
				/*
				 * check the connections between the cells for that block
				 * add the coefficient for that weight
				 */
				NetlistNode src = nodeList.get(j);
				NetlistNode dst = nodeList.get(k);
				int coefficient = 0;
				coefficient += NetlistNodeUtils.numNetlistEdgesBetween(src, dst);
				if (coefficient > 0) {
					term += Integer.toString(coefficient) + S_SPACE;
					term += GPCCSCIPBaseConstraintUtils.AssignInt(j, srcBlockIdx);
					term += S_SPACE + S_MULTIPLICATION + S_SPACE;
					term += GPCCSCIPBaseConstraintUtils.AssignInt(k, dstBlockIdx);					
				}
				// if term is not empty add to objective function
				if (!term.isEmpty()) {
					if (!rtn.isEmpty()) {
						rtn += S_SPACE + S_ADDITION + S_SPACE;
					}
					rtn += S_SPACE + S_SQUARE_BRACKET_OPEN + S_SPACE;
					rtn += term;				
					rtn += S_SPACE + S_SQUARE_BRACKET_CLOSE + S_SPACE;
				}
			}
		}
		return rtn;
	}

	/**
	 *  Writes the InterBlock Capacity
	 *  
	 *  @param scip scip instance
	 *  @param filename filename
	 */
	static private void writeConstraintsInterBlockCapacity(GPCC_SCIP_BASE scip, String filename) {
		String equ = "";
		List<Block> blockList = scip.getBlockList();
		List<List<Set<Integer>>> blockCapacityCellList = scip.getBlockCapacityCellList();
		List<NetlistNode> nodeList = scip.getCellList();
		Partition P = scip.getPartitioner().getPartition();
		for (int interblockIdx = 0; interblockIdx < P.getNumInterBlock(); interblockIdx++) {
			String constraint = "";
			InterBlock interblock = P.getInterBlockAtIdx(interblockIdx);
			Block srcBlock = interblock.getSrcBlock();
			Block dstBlock = interblock.getDstBlock();
			int srcBlockIdx = blockList.indexOf(srcBlock);
			int dstBlockIdx = blockList.indexOf(dstBlock);
			Set<Integer> srcCells = GPCCSCIPBaseConstraintUtils.union(blockCapacityCellList.get(srcBlockIdx));
			Set<Integer> dstCells = GPCCSCIPBaseConstraintUtils.union(blockCapacityCellList.get(dstBlockIdx));
			for (int capacityIdx = 0; capacityIdx < interblock.getNumCapacity(); capacityIdx++) {
				Capacity capacity = interblock.getCapacityAtIdx(capacityIdx);
				// header
				constraint += GPCCSCIPBaseConstraintUtils.getConstraintsInterBlockCapacityHeader(srcBlockIdx,dstBlockIdx,capacityIdx);
				// identifier lower
				constraint += GPCCSCIPBaseConstraintUtils.getConstraintsInterBlockCapacityIdentifier(srcBlockIdx,dstBlockIdx,capacityIdx);
				constraint += S_UNDERSCORE;
				constraint += S_LOWER;
				constraint += S_IDENTIFIERSEPARATOR;
				equ = GPCCSCIPBaseConstraintUtils.getEdgeEquation(srcCells, dstCells, nodeList, srcBlockIdx, dstBlockIdx);
				constraint += equ;
				// in LP format: >, >=, => is equivalent to =>
				// capacity lower bound symbol is reduced to => (flipped)
				constraint += S_SPACE + LowerBoundType.getStringFlip(capacity.getLowerBoundType()) + S_SPACE + capacity.getLowerBound();
				constraint += Utils.getNewLine();
				// identifier upper
				constraint += GPCCSCIPBaseConstraintUtils.getConstraintsInterBlockCapacityIdentifier(srcBlockIdx,dstBlockIdx,capacityIdx);
				constraint += S_UNDERSCORE;
				constraint += S_UPPER;
				constraint += S_IDENTIFIERSEPARATOR;
				constraint += equ;
				// in LP format: <, <=, =< is equivalent to <=
				// capacity upper bound symbol is reduced to <, thus substract one from value
				constraint += S_SPACE + capacity.getUpperBoundType().toString() + S_SPACE + (capacity.getUpperBound() - 1);
				constraint += Utils.getNewLine();		
			}
			// write SCIP script
			Utils.appendToFile(constraint, filename);
		}
	}
	
	/**
	 *  Returns a string representation the total number of edges
	 *  
	 *  @param scip scip instance
	 *  @return a string representation the total number of edges
	 */
	static public String getConstraintsObjectiveTotalEdges(GPCC_SCIP_BASE scip) {
		String rtn = "";
		int totalEdges = 0;
		List<NetlistNode> nodeList = scip.getCellList();
		int size = nodeList.size();
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				NetlistNode src = nodeList.get(i);
				NetlistNode dst = nodeList.get(j);
				totalEdges += NetlistNodeUtils.numNetlistEdgesBetween(src, dst);
			}
		}
		rtn += totalEdges;
		return rtn;
	}

	/**
	 *  Writes Objective
	 *  
	 *  @param scip scip instance
	 *  @param filename filename
	 */	
	static private void writeConstraintsObjective(GPCC_SCIP_BASE scip, String filename) {
		String constraint = "";
		String equ = "";
		List<List<Set<Integer>>> blockCapacityCellList = scip.getBlockCapacityCellList();
		List<NetlistNode> nodeList = scip.getCellList();
		int sizeI = blockCapacityCellList.size();
		if (sizeI != 0) {
			// header
			constraint += Utils.getTabCharacter();
			constraint += S_HEADER;
			constraint += Utils.getNewLine();
			constraint += Utils.getTabCharacter();
			constraint += S_COMMENT;
			constraint += Utils.getTabCharacterRepeat(2);
			constraint += S_OBJECTIVE;
			constraint += Utils.getNewLine();
			constraint += Utils.getTabCharacter();
			constraint += S_HEADER;
			constraint += Utils.getNewLine();
			// identifier
			constraint += Utils.getTabCharacter();
			constraint += S_OBJECTIVEEQUATION;
			constraint += S_IDENTIFIERSEPARATOR;
			constraint += S_NUMEDGE;
			// for each block
			for (int i = 0; i < sizeI; i++) {
				List<Set<Integer>> allCellList = blockCapacityCellList.get(i);
				Set<Integer> cellList = GPCCSCIPBaseConstraintUtils.union(allCellList);
				equ = GPCCSCIPBaseConstraintUtils.getEdgeEquation(cellList, cellList, nodeList, i, i);
				if (!equ.isEmpty()) {
					constraint += S_SPACE + S_ADDITION + S_SPACE;
				}
				constraint += equ;
			}
			constraint += S_EQUAL + S_SPACE + GPCCSCIPBaseConstraintUtils.getConstraintsObjectiveTotalEdges(scip);
			constraint += Utils.getNewLine();
		}
		// write SCIP script
		Utils.appendToFile(constraint, filename);
	}

	/**
	 *  Writes Subject To
	 *  
	 *  @param scip scip instance
	 *  @param filename filename
	 */	
	static private void writeSubjectTo(GPCC_SCIP_BASE scip, String filename) {
		String constraint = "";
		constraint += S_SUBJECTTO;
		constraint += Utils.getNewLine();
		// write SCIP script
		Utils.appendToFile(constraint, filename);
	}

	/**
	 *  Writes Constraints
	 *  
	 *  @param scip scip instance
	 *  @param filename filename
	 */	
	static private void writeConstraints(GPCC_SCIP_BASE scip, String filename) {
		GPCCSCIPBaseConstraintUtils.writeSubjectTo(scip, filename);
		GPCCSCIPBaseConstraintUtils.writeConstraintsBlockCapacity(scip, filename);
		// GPCCSCIPBaseConstraintUtils.writeConstraintsBlockConnectionsCapacity(scip, filename);
		GPCCSCIPBaseConstraintUtils.writeConstraintsAssignment(scip, filename);
		GPCCSCIPBaseConstraintUtils.writeConstraintsInterBlockCapacity(scip, filename);
		GPCCSCIPBaseConstraintUtils.writeConstraintsObjective(scip, filename);
	}

	/**
	 *  Writes Binary
	 *  
	 *  @param scip scip instance
	 *  @param filename filename
	 */	
	static private void writeBinary(GPCC_SCIP_BASE scip, String filename) {
		String constraint = "";
		constraint += S_BINARY;
		constraint += Utils.getNewLine();
		List<Set<Integer>> cellBlockList = scip.getCellBlockList();
		for (int i = 0; i < cellBlockList.size(); i++) {
			int size = cellBlockList.get(i).size();
			if (size == 0) {
				continue;
			}
			Iterator<Integer> it = cellBlockList.get(i).iterator();
			while(it.hasNext()){
				int blkId = it.next();
				constraint += Utils.getTabCharacter();
				constraint += AssignInt(i, blkId);
				constraint += Utils.getNewLine();
			}
		}
		// write SCIP script
		Utils.appendToFile(constraint, filename);
	}
	
	/**
	 *  Writes the End
	 *  
	 *  @param scip scip instance
	 *  @param filename filename
	 */	
	static private void writeEnd(GPCC_SCIP_BASE scip, String filename) {
		String constraint = "";
		constraint += S_END;
		constraint += Utils.getNewLine();
		// write SCIP script
		Utils.appendToFile(constraint, filename);
	}
	
	/**
	 *  Writes SCIP Constraint File
	 *  
	 *  @param scip scip instance
	 *  @param filename filename
	 */	
	static public void writeSCIPConstraintFile(GPCC_SCIP_BASE scip, String filename) {
		Utils.deleteFilename(filename);
		Utils.createFile(filename);
		GPCCSCIPBaseConstraintUtils.writeObjectiveFunction(scip, filename);
		GPCCSCIPBaseConstraintUtils.writeConstraints(scip, filename);
		/*this.writeBounds();
		this.writeGeneral();*/
		GPCCSCIPBaseConstraintUtils.writeBinary(scip, filename);
		GPCCSCIPBaseConstraintUtils.writeEnd(scip, filename);
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
	private static String S_IDENTIFIERSEPARATOR = S_COLON + S_SPACE;
	
}

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
package partitioning.algorithm.GPCC_BASE;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import common.Utils;
import partitioning.common.Block;
import partitioning.common.InterBlock;
import partitioning.common.Partition;
import partitioning.profile.Capacity;
import results.logicSynthesis.LSResultsUtils;
import results.netlist.NetlistNode;
import results.netlist.NetlistNodeUtils;

/**
 * The GPCC_BASE_PWriter class implements the problem writer for the <i>GPCC_BASE</i> algorithm.
 * 
 * @author Vincent Mirian
 * 
 * @date 2018-05-21
 *
 */
abstract public class GPCC_BASE_PWriter {
	
	/**
	 *  Returns a aggregated set of integers from the list defined by parameter <i>listSets</i>
	 *  
	 *  @param listSets a list of sets containing integers
	 *  @return a aggregated set of integers from the list defined by parameter <i>listSets</i>
	 */
	protected Set<Integer> union(List<Set<Integer>> listSets){
		Set<Integer> rtn = new HashSet<Integer>();
		for (int j = 0; j < listSets.size(); j++) {
			rtn.addAll(listSets.get(j));
		}
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
	protected String AssignInt(final int Cell, final int Block) {
		String rtn = "";
		rtn += S_ASSIGN;
		rtn += S_CELL;
		rtn += Cell;
		rtn += S_BLK;
		rtn += Block;
		return rtn;
	}
	
	/**
	 *  Returns a string representation the total number of edges
	 *  
	 *  @return a string representation the total number of edges
	 */
	public String getConstraintsObjectiveTotalEdges() {
		String rtn = "";
		int totalEdges = 0;
		List<NetlistNode> nodeList = this.getGPCC_BASE().getCellList();
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
	 *  Returns a string representation with the Edge Equation coefficient
	 *  
	 *  @return a string representation with the Edge Equation coefficient
	 */
	abstract protected String getEdgeEquationCoefficient(int coefficient);
	
	/**
	 *  Returns a string used in the Edge Equation between Blocks
	 *  
	 *  @return a string used in the Edge Equation between Blocks
	 */
	abstract protected String getEdgeEquationBetweenBlocks();
	
	/**
	 *  Returns a string used in the Edge Equation After Blocks
	 *  
	 *  @return a string used in the Edge Equation After Blocks
	 */
	abstract protected String getEdgeEquationAfterBlocks();
	
	/**
	 *  Returns a string used in the Edge Equation Before Term
	 *  
	 *  @return a string used in the Edge Equation Before Term
	 */
	abstract protected String getEdgeEquationBeforeTerm();
	
	/**
	 *  Returns a string used in the Edge Equation After Term
	 *  
	 *  @return a string used in the Edge Equation After Term
	 */
	abstract protected String getEdgeEquationAfterTerm();

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
	protected String getEdgeEquation(
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
					term += this.getEdgeEquationCoefficient(coefficient);
					term += this.AssignInt(j, srcBlockIdx);
					term += this.getEdgeEquationBetweenBlocks();
					term += this.AssignInt(k, dstBlockIdx);	
					term += this.getEdgeEquationAfterBlocks();		
				}
				// if term is not empty add to objective function
				if (!term.isEmpty()) {
					rtn += this.getEdgeEquationBeforeTerm();
					rtn += term;
					rtn += this.getEdgeEquationAfterTerm();
				}
			}
		}
		return rtn;
	}
		
	/**
	 *  Returns a string representing a start of comment 
	 *  
	 *  @return a string representing a start of comment
	 */
	abstract protected String getComment();
	
	
	/**
	 *  Returns a string representing the Objective Constraint Edge Name 
	 *  
	 *  @return a string representing the Objective Constraint Edge Name 
	 */
	abstract protected String getObjectiveConstraintEdgeName();
	
	/**
	 *  Returns a string representing the Objective Constraint Edge Before Total Edges
	 *  
	 *  @return a string representing the Objective Constraint Edge Before Total Edges
	 */
	abstract protected String getObjectiveConstraintEdgeBeforeTotalEdges();
	
	/**
	 *  Returns a string representing the Objective Constraint Edge After Total Edges
	 *  
	 *  @return a string representing the Objective Constraint Edge After Total Edges
	 */
	abstract protected String getObjectiveConstraintEdgeAfterTotalEdges();
	
	/**
	 *  Returns a string representing the Objective Constraint Edge Before Equation
	 *  
	 *  @return a string representing the Objective Constraint Edge Before Equation
	 */
	abstract protected String getObjectiveConstraintEdgeBeforeEqn();
	
	/**
	 *  Returns a string representing the Objective Constraint Edge After Equation
	 *  
	 *  @return a string representing the Objective Constraint Edge After Equation
	 */
	abstract protected String getObjectiveConstraintEdgeAfterEqn();
	
	/**
	 *  Returns a string representing the Objective Constraint Edge Before End Total Edges
	 *  
	 *  @return a string representing the Objective Constraint Edge Before End Total Edges
	 */
	abstract protected String getObjectiveConstraintEdgeBeforeEndTotalEdges();
	
	/**
	 *  Returns a string representing the Objective Constraint Edge After End Total Edges
	 *  
	 *  @return a string representing the Objective Constraint Edge After End Total Edges
	 */
	abstract protected String getObjectiveConstraintEdgeAfterEndTotalEdges();
	
	/**
	 *  Writes the Objective Constraint Edge
	 */	
	protected void writeConstraintsObjectiveEdge() {
		String constraint = "";
		String equ = "";
		String name = "";
		List<List<Set<Integer>>> blockCapacityCellList = this.getGPCC_BASE().getBlockCapacityCellList();
		List<NetlistNode> nodeList = this.getGPCC_BASE().getCellList();
		int sizeI = blockCapacityCellList.size();
		if (sizeI != 0) {
			String totalEdges = this.getConstraintsObjectiveTotalEdges();
			name += getObjectiveConstraintEdgeName();
			constraint += this.getComment() + name  + Utils.getNewLine();
			constraint += this.getObjectiveConstraintEdgeBeforeTotalEdges();
			constraint += totalEdges;
			constraint += this.getObjectiveConstraintEdgeAfterTotalEdges();
			// for each block
			for (int i = 0; i < sizeI; i++) {
				List<Set<Integer>> allCellList = blockCapacityCellList.get(i);
				Set<Integer> cellList = this.union(allCellList);
				equ = this.getEdgeEquation(cellList, cellList, nodeList, i, i);
				if (!equ.isEmpty()) {
					constraint += this.getObjectiveConstraintEdgeBeforeEqn();
					constraint += equ;
					constraint += this.getObjectiveConstraintEdgeAfterEqn();
				}
			}
			constraint += this.getObjectiveConstraintEdgeBeforeEndTotalEdges();
			constraint += totalEdges;
			constraint += this.getObjectiveConstraintEdgeAfterEndTotalEdges();
		}
		// write script
		Utils.appendToFile(constraint, this.getFilename());
	}

	/**
	 *  Writes the Header
	 */	
	protected void writeHeader() {
		String constraint = "";
		constraint += this.getComment() + this.getFilename() + Utils.getNewLine();
		// write script
		Utils.appendToFile(constraint, this.getFilename());
	}
	
	/**
	 *  Returns a string representing the Assignment Variable Prefix
	 *  
	 *  @return a string representing the Assignment Variable Prefix
	 */
	abstract protected String getAssignmentVariablePrefix();
	/**
	 *  Returns a string representing the Assignment Variable PreAssign
	 *  
	 *  @return a string representing the Assignment Variable PreAssign
	 */
	abstract protected String getAssignmentVariablePreAssign();
	/**
	 *  Returns a string representing the Assignment Variable PostAssign
	 *  
	 *  @return a string representing the Assignment Variable PostAssign
	 */
	abstract protected String getAssignmentVariablePostAssign();
	/**
	 *  Returns a string representing the Assignment Variable Postfix
	 *  
	 *  @return a string representing the Assignment Variable Postfix
	 */
	abstract protected String getAssignmentVariablePostfix();

	/**
	 *  Writes the Assignment Variable
	 */	
	protected void writeAssignmentVariable() {
		String constraint = "";
		List<Set<Integer>> cellBlockList = this.getGPCC_BASE().getCellBlockList();
		constraint += this.getAssignmentVariablePrefix();
		for (int i = 0; i < cellBlockList.size(); i++) {
			int size = cellBlockList.get(i).size();
			if (size == 0) {
				continue;
			}
			Iterator<Integer> it = cellBlockList.get(i).iterator();
			while(it.hasNext()){
				int blkId = it.next();
				constraint += this.getAssignmentVariablePreAssign();
				constraint += AssignInt(i, blkId);
				constraint += this.getAssignmentVariablePostAssign();
			}
		}
		constraint += this.getAssignmentVariablePostfix();
		// write script
		Utils.appendToFile(constraint, this.getFilename());
	}
	
	/**
	 *  Returns a string representing the End
	 *  
	 *  @return a string representing the End
	 */
	abstract protected String getEnd();

	/**
	 *  Writes the End
	 */	
	protected void writeEnd() {
		String constraint = "";
		constraint += this.getEnd();
		// write script
		Utils.appendToFile(constraint, this.getFilename());
	}

	/**
	 *  Returns a string representing the Block Capacity Header for Block index defined by parameter <i>blockIdx</i>
	 *  and capacity index defined by parameter <i>capacityIdx</i>
	 *  
	 *  @param blockIdx the block index
	 *  @param capacityIdx the capacity index
	 *  @return a string representing the Block Capacity Header
	 */
	protected String getConstraintsBlockCapacityHeader(int blockIdx, int capacityIdx) {
		String rtn = "";
		// header
		rtn += Utils.getTabCharacter();
		rtn += S_BLK + S_SPACE;
		rtn += blockIdx + S_SPACE;
		rtn += S_CAPACITY + S_SPACE;
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
	protected String getConstraintsBlockCapacityCellList(int blockIdx, Set<Integer> cellList) {
		String rtn = "";
		int cellIdx = 0;
		Iterator<Integer> it = cellList.iterator();
		while (it.hasNext()) {
			if (cellIdx != 0) {
				rtn += S_SPACE;
			}
			int cellId = it.next();
			rtn += this.AssignInt(cellId, blockIdx);
			cellIdx++;
		}
		return rtn;
	}

	/**
	 *  Returns a string representing the Block Capacity Lower Before Eqn
	 *  
	 *  @param c the Capacity
	 *  @return a string representing the Block Capacity Lower Before Eqn
	 */
	abstract protected String getConstraintsBlockCapacityLowerBeforeEqn(Capacity c);
	/**
	 *  Returns a string representing the Block Capacity Lower After Eqn
	 *  
	 *  @param c the Capacity
	 *  @return a string representing the Block Capacity Lower After Eqn
	 */
	abstract protected String getConstraintsBlockCapacityLowerAfterEqn(Capacity c);
	/**
	 *  Returns a string representing the Block Capacity Upper Before Eqn
	 *  
	 *  @param c the Capacity
	 *  @return a string representing the Block Capacity Upper Before Eqn
	 */
	abstract protected String getConstraintsBlockCapacityUpperBeforeEqn(Capacity c);
	/**
	 *  Returns a string representing the Block Capacity Upper After Eqn
	 *  
	 *  @param c the Capacity
	 *  @return a string representing the Block Capacity Upper After Eqn
	 */
	abstract protected String getConstraintsBlockCapacityUpperAfterEqn(Capacity c);

	/**
	 *  Writes the Block Capacity
	 */
	protected void writeConstraintsBlockCapacity() {
		String equ = "";
		List<List<Set<Integer>>> blockCapacityCellList = this.getGPCC_BASE().getBlockCapacityCellList();
		for (int blockIdx = 0; blockIdx < blockCapacityCellList.size(); blockIdx++) {
			String constraint = "";
			List<Set<Integer>> allCellList = blockCapacityCellList.get(blockIdx);
			int size = allCellList.size();
			if (size == 0) {
				continue;
			}
			Block block = this.getGPCC_BASE().getBlockList().get(blockIdx);
			for (int capacityIdx = 0; capacityIdx < block.getNumCapacity(); capacityIdx++) {
				Capacity capacity = block.getCapacityAtIdx(capacityIdx);
				Set<Integer> cellList = allCellList.get(capacityIdx);
				// header
				constraint += this.getComment() + this.getConstraintsBlockCapacityHeader(blockIdx, capacityIdx) + Utils.getNewLine();
				equ = this.getConstraintsBlockCapacityCellList(blockIdx, cellList);
				// identifier lower
				constraint += this.getComment() + Utils.getTabCharacter() + S_LOWER + Utils.getNewLine();
				constraint += this.getConstraintsBlockCapacityLowerBeforeEqn(capacity);
				constraint += equ;
				constraint += this.getConstraintsBlockCapacityLowerAfterEqn(capacity);
				// identifier upper
				constraint += this.getComment() + Utils.getTabCharacter() + S_UPPER + Utils.getNewLine();
				constraint += getConstraintsBlockCapacityUpperBeforeEqn(capacity);
				constraint += equ;
				constraint += this.getConstraintsBlockCapacityUpperAfterEqn(capacity);	
			}
			// write script
			Utils.appendToFile(constraint, this.getFilename());
		}
	}

	/**
	 *  Returns a string representing the Block Input Connection Capacity CellList for cell defined by parameter <i>cellID</i>
	 *  and block defined by parameter <i>blockIdx</i>
	 *  
	 *  @param cellId the cell index
	 *  @param blockIdx the block index
	 *  @return a string representing the Block Input Connection Capacity CellList
	 */
	// input to cellId in BlockIdx
	protected String getConstraintsBlockInputConnectionCapacityCellList(int cellId, int blockIdx) {
		String rtn = "";
		List<NetlistNode> nodeList = this.getGPCC_BASE().getCellList();
		List<Set<Integer>> cellBlockList = this.getGPCC_BASE().getCellBlockList();
		NetlistNode node = nodeList.get(cellId);
		// get input connections
		for (int i = 0; i < node.getNumInEdge(); i ++) {
			NetlistNode srcNode = node.getInEdgeAtIdx(i).getSrc();
			if (LSResultsUtils.isAllInput(srcNode) || LSResultsUtils.isAllOutput(srcNode)) {
				continue;
			}
			int srcNodeIdx = nodeList.indexOf(srcNode);
			// block list of otherNode
			Set<Integer> blockSet = cellBlockList.get(srcNodeIdx);
			Iterator<Integer> bkiter = blockSet.iterator();
			while (bkiter.hasNext()) {
				int srcBlockIdx = bkiter.next();
				// not assigned to the same block
				if (srcBlockIdx == blockIdx) {
					continue;
				}
				rtn += this.AssignInt(srcNodeIdx, srcBlockIdx);
				rtn += " ";
			}
		}
		return rtn;
	}

	/**
	 *  Returns a string representing the Block Output Connection Capacity CellList for cell defined by parameter <i>cellID</i>
	 *  and block defined by parameter <i>blockIdx</i>
	 *  
	 *  @param cellId the cell index
	 *  @param blockIdx the block index
	 *  @return a string representing the Block Output Connection Capacity CellList
	 */
	// output from cellId in BlockIdx
	protected String getConstraintsBlockOutputConnectionCapacityCellList(int cellId, int blockIdx) {
		String rtn = "";
		/*List<NetlistNode> nodeList = this.getGPCC_BASE().getCellList();
		List<Set<Integer>> cellBlockList = this.getGPCC_BASE().getCellBlockList();
		NetlistNode node = nodeList.get(cellId);
		// get output connections
		for (int i = 0; i < node.getNumOutEdge(); i ++) {
			NetlistNode dstNode = node.getOutEdgeAtIdx(i).getDst();
			if (LSResultsUtils.isInput(dstNode) || LSResultsUtils.isOutput(dstNode)) {
				continue;
			}
			int dstNodeIdx = nodeList.indexOf(dstNode);
			// block list of otherNode
			Set<Integer> blockSet = cellBlockList.get(dstNodeIdx);
			Iterator<Integer> bkiter = blockSet.iterator();
			while (bkiter.hasNext()) {
				int dstBlockIdx = bkiter.next();
				// not assigned to the same block
				if (dstBlockIdx == blockIdx) {
					continue;
				}
				rtn += this.AssignInt(dstNodeIdx, dstBlockIdx);
				rtn += " ";
			}
		}*/
		rtn += this.getConstraintsBlockOutputConnectionCapacityCellListDst(cellId, blockIdx, -1);
		return rtn;
	}

	/**
	 *  Returns a string representing the Block Output Connection Capacity CellList for cell defined by parameter <i>cellID</i>
	 *  and block defined by parameter <i>blockIdx</i>
	 *  to block defined by parameter <i>dstBlockIdx</i>
	 *  
	 *  @param cellId the cell index
	 *  @param blockIdx the block index
	 *  @param dstBlockIdx the destination block index
	 *  @return a string representing the Block Output Connection Capacity CellList
	 */
	// output from cellId in BlockIdx to DstBlockIdx
	protected String getConstraintsBlockOutputConnectionCapacityCellListDst(int cellId, int blockIdx, int dstBlockIdx) {
		String rtn = "";
		List<NetlistNode> nodeList = this.getGPCC_BASE().getCellList();
		List<Set<Integer>> cellBlockList = this.getGPCC_BASE().getCellBlockList();
		NetlistNode node = nodeList.get(cellId);
		// get output connections
		for (int i = 0; i < node.getNumOutEdge(); i ++) {
			NetlistNode dstNode = node.getOutEdgeAtIdx(i).getDst();
			if (LSResultsUtils.isAllInput(dstNode) || LSResultsUtils.isAllOutput(dstNode)) {
				continue;
			}
			int dstNodeIdx = nodeList.indexOf(dstNode);
			// block list of otherNode
			Set<Integer> blockSet = cellBlockList.get(dstNodeIdx);
			Iterator<Integer> bkiter = blockSet.iterator();
			while (bkiter.hasNext()) {
				int myDstBlockIdx = bkiter.next();
				// not assigned to the same block
				if (myDstBlockIdx == blockIdx) {
					continue;
				}
				// flag to skip dstBlockIdx check if negative
				if ((dstBlockIdx >= 0) && (myDstBlockIdx != dstBlockIdx)) {
					continue;
				}
				rtn += this.AssignInt(dstNodeIdx, myDstBlockIdx);
				rtn += " ";
			}
		}
		return rtn;
	}
	
	/**
	 *  Returns a string representing the Block Connection Capacity CellList for block defined by parameter <i>blockIdx</i>,
	 *  parameter <i>isOutput</i> defines whether the Connection is for an output or an input
	 *  
	 *  @param isOutput true if the connection is an output, false otherwise
	 *  @param blockIdx the block index
	 *  @param dstBlockIdx the destination block index
	 *  @return a string representing the Block Connection Capacity CellList for block defined by parameter <i>blockIdx</i>,
	 */
	// TODO: make abstract
	protected String getConstraintsConnectionsCapacityCellList(boolean isOutput, int blockIdx, int dstBlockIdx) {
		String rtn = "";
		List<List<Set<Integer>>> blockCapacityCellList = this.getGPCC_BASE().getBlockCapacityCellList();
		List<Set<Integer>> allCellList = blockCapacityCellList.get(blockIdx);
		Set<Integer> cellList = this.union(allCellList);
		Iterator<Integer> it = cellList.iterator();
		// for each node in block
		while (it.hasNext()) {
			int cellId = it.next();
			String dstNodeBlockAssign = ""; 
			String dstNodeBlockAssignVar = "";
			if (isOutput) {
				if (dstBlockIdx < 0) {
					dstNodeBlockAssign = this.getConstraintsBlockOutputConnectionCapacityCellList(cellId, blockIdx);
				}
				else {
					dstNodeBlockAssign = this.getConstraintsBlockOutputConnectionCapacityCellListDst(cellId, blockIdx, dstBlockIdx);
				}
				dstNodeBlockAssignVar = "";//this.getConstraintsOutputConnectionsCapacityVariable(blockIdx);
				dstNodeBlockAssign = "( >  ( + " + dstNodeBlockAssign + ") 0 )";
				// if cellId is in blockIdx
				// and a destination cell is in another block
				// increment output count
				rtn += "( if ( and ";
				rtn += "( > ";
				rtn += this.AssignInt(cellId, blockIdx);
				rtn += S_SPACE;
				rtn += " 0 )";
				rtn += dstNodeBlockAssign;
				//rtn += ") (add 1 " + dstNodeBlockAssignVar + ") (add 0 " + dstNodeBlockAssignVar + "))";
				rtn += ") 1 0 )";
			}
			else {
				dstNodeBlockAssign = this.getConstraintsBlockInputConnectionCapacityCellList(cellId, blockIdx);
				dstNodeBlockAssignVar = "";//this.getConstraintsInputConnectionsCapacityVariable(blockIdx);
				dstNodeBlockAssign = "( + " + dstNodeBlockAssign + ")";
				// if cellId is in blockIdx
				// add the number of input cell in other blocks to count
				rtn += "( if ";
				rtn += "( > ";
				rtn += this.AssignInt(cellId, blockIdx);
				rtn += S_SPACE;
				rtn += " 0 )";
				//rtn += "(add " + dstNodeBlockAssign + " "  + dstNodeBlockAssignVar + ")";
				rtn += dstNodeBlockAssign;
				//rtn += "(add 0 "  + dstNodeBlockAssignVar + ") )";
				rtn += " 0 )";
			}
			if (dstNodeBlockAssign.isEmpty()) {
				continue;
			}
		}
		return rtn;
	}

	/**
	 *  Returns a string representing the Input Connections Capacity Variable for block defined by parameter <i>blockIdx</i>
	 *  
	 *  @param blockIdx the block index
	 *  @return a string representing the Input Connections Capacity Variable for block defined by parameter <i>blockIdx</i>
	 */
	protected String getConstraintsInputConnectionsCapacityVariable(int blockIdx) {
		return S_BLK + blockIdx + S_ICC;
	}

	/**
	 *  Returns a string representing the Output Connections Capacity Variable for block defined by parameter <i>blockIdx</i>
	 *  
	 *  @param blockIdx the block index
	 *  @return a string representing the Output Connections Capacity Variable for block defined by parameter <i>blockIdx</i>
	 */
	protected String getConstraintsOutputConnectionsCapacityVariable(int blockIdx) {
		return S_BLK + blockIdx + S_OCC;
	}

	/**
	 *  Returns a string representing the Input Connections Block Max Connections for block defined by parameter <i>blockIdx</i>
	 *  
	 *  @param blockIdx the block index
	 *  @return a string representing the Input Connections Block Max Connections for block defined by parameter <i>blockIdx</i>
	 */
	protected String getConstraintsInputConnectionsCapacityBlockMaxConnections(int blockIdx) {
		String rtn = "";
		int max = 0;
		Set<Integer> cellList = this.union(this.getGPCC_BASE().getBlockCapacityCellList().get(blockIdx));
		List<NetlistNode> nodeList = this.getGPCC_BASE().getCellList();
		Iterator<Integer> it = cellList.iterator();
		while (it.hasNext()) {
			NetlistNode node = nodeList.get(it.next());
			for (int i = 0; i < node.getNumInEdge(); i ++) {
				NetlistNode srcNode = node.getInEdgeAtIdx(i).getSrc();
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
	 *  Returns a string representing the Output Connections Block Max Connections for block defined by parameter <i>blockIdx</i>
	 *  
	 *  @param blockIdx the block index
	 *  @return a string representing the Output Connections Block Max Connections for block defined by parameter <i>blockIdx</i>
	 */
	protected String getConstraintsOutputConnectionsCapacityBlockMaxConnections(int blockIdx) {
		String rtn = "";
		int max = 0;
		Set<Integer> cellList = this.union(this.getGPCC_BASE().getBlockCapacityCellList().get(blockIdx));
		List<NetlistNode> nodeList = this.getGPCC_BASE().getCellList();
		Iterator<Integer> it = cellList.iterator();
		while (it.hasNext()) {
			NetlistNode node = nodeList.get(it.next());
			for (int i = 0; i < node.getNumOutEdge(); i ++) {
				NetlistNode dstNode = node.getOutEdgeAtIdx(i).getDst();
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
	 *  Returns a string representing the Output Connections Capacity Prefix for block defined by parameter <i>blockIdx</i>
	 *  
	 *  @param blockIdx the block index
	 *  @return a string representing the Output Connections Capacity Prefix for block defined by parameter <i>blockIdx</i>
	 */
	abstract protected String getConstraintsOutputConnectionsCapacityPrefix(int blockIdx);
	/**
	 *  Returns a string representing the Input Connections Capacity Prefix for block defined by parameter <i>blockIdx</i>
	 *  
	 *  @param blockIdx the block index
	 *  @return a string representing the Input Connections Capacity Prefix for block defined by parameter <i>blockIdx</i>
	 */
	abstract protected String getConstraintsInputConnectionsCapacityPrefix(int blockIdx);

	/**
	 *  Writes the Block Connections Capacity
	 *  
	 *  @param isOutput true if the connection is an output, false otherwise
	 */	
	protected void writeConstraintsBlockConnectionsCapacity(boolean isOutput) {
		String equ = "";
		List<List<Set<Integer>>> blockCapacityCellList = this.getGPCC_BASE().getBlockCapacityCellList();
		for (int blockIdx = 0; blockIdx < blockCapacityCellList.size(); blockIdx++) {
			String constraint = "";
			List<Set<Integer>> allCellList = blockCapacityCellList.get(blockIdx);
			int size = allCellList.size();
			if (size == 0) {
				continue;
			}
			Block block = this.getGPCC_BASE().getBlockList().get(blockIdx);
			Capacity capacity = null;
			for (int capacityIdx = 0; capacityIdx < block.getNumCapacity(); capacityIdx++) {
				if (isOutput) {
					capacity = block.getOutputConnectionsCapacityAtIdx(capacityIdx);
				}
				else {
					capacity = block.getInputConnectionsCapacityAtIdx(capacityIdx);
				}
				if (capacity == null) {
					continue;
				}
				// header
				constraint += this.getComment() + this.getConstraintsBlockCapacityHeader(blockIdx, 0) + Utils.getNewLine();
				if (isOutput) {
					constraint += this.getConstraintsOutputConnectionsCapacityPrefix(blockIdx);
				}
				else {
					constraint += this.getConstraintsInputConnectionsCapacityPrefix(blockIdx);
				}
				equ = this.getConstraintsConnectionsCapacityCellList(isOutput, blockIdx, -1);
				// identifier lower
				constraint += this.getComment() + Utils.getTabCharacter() + S_LOWER + Utils.getNewLine();
				constraint += this.getConstraintsBlockCapacityLowerBeforeEqn(capacity);
				constraint += equ;
				constraint += this.getConstraintsBlockCapacityLowerAfterEqn(capacity);
				// identifier upper
				constraint += this.getComment() + Utils.getTabCharacter() + S_UPPER + Utils.getNewLine();
				constraint += getConstraintsBlockCapacityUpperBeforeEqn(capacity);
				constraint += equ;
				constraint += this.getConstraintsBlockCapacityUpperAfterEqn(capacity);

			}
			// write script
			Utils.appendToFile(constraint, this.getFilename());
		}
	}

	/**
	 *  Writes the Block Output Connections Capacity
	 */	
	protected void writeConstraintsBlockOutputConnectionsCapacity() {
		this.writeConstraintsBlockConnectionsCapacity(true);
	}

	/**
	 *  Writes the Block Input Connections Capacity
	 */	
	protected void writeConstraintsBlockInputConnectionsCapacity() {
		this.writeConstraintsBlockConnectionsCapacity(false);
	}

	abstract protected String getConstraintsInOutConnectionsCapacityPrefix(int blockIdx);

	/**
	 *  Writes the Block InOut Connections Capacity
	 */	
	protected void writeConstraintsBlockInOutConnectionsCapacity() {
		String equIn = "";
		String equOut = "";
		String equ = "";
		List<List<Set<Integer>>> blockCapacityCellList = this.getGPCC_BASE().getBlockCapacityCellList();
		for (int blockIdx = 0; blockIdx < blockCapacityCellList.size(); blockIdx++) {
			String constraint = "";
			List<Set<Integer>> allCellList = blockCapacityCellList.get(blockIdx);
			int size = allCellList.size();
			if (size == 0) {
				continue;
			}
			Block block = this.getGPCC_BASE().getBlockList().get(blockIdx);
			Capacity capacity = null;
			for (int capacityIdx = 0; capacityIdx < block.getNumCapacity(); capacityIdx++) {
				capacity = block.getInOutConnectionsCapacityAtIdx(capacityIdx);
				if (capacity == null) {
					continue;
				}
				// header
				constraint += this.getComment() + this.getConstraintsBlockCapacityHeader(blockIdx, 0) + Utils.getNewLine();
				// inout
				constraint += this.getConstraintsInOutConnectionsCapacityPrefix(blockIdx);
				equIn = this.getConstraintsConnectionsCapacityCellList(false, blockIdx, -1);
				equOut = this.getConstraintsConnectionsCapacityCellList(true, blockIdx, -1);
				equ = equIn + S_SPACE + equOut;
				// identifier lower
				constraint += this.getComment() + Utils.getTabCharacter() + S_LOWER + Utils.getNewLine();
				constraint += this.getConstraintsBlockCapacityLowerBeforeEqn(capacity);
				constraint += equ;
				constraint += this.getConstraintsBlockCapacityLowerAfterEqn(capacity);
				// identifier upper
				constraint += this.getComment() + Utils.getTabCharacter() + S_UPPER + Utils.getNewLine();
				constraint += getConstraintsBlockCapacityUpperBeforeEqn(capacity);
				constraint += equ;
				constraint += this.getConstraintsBlockCapacityUpperAfterEqn(capacity);
			}
			// write script
			Utils.appendToFile(constraint, this.getFilename());
		}
	}
	
	/**
	 *  Returns a string representing the Assignment Prefix
	 *  
	 *  @return a string representing the Assignment Prefix
	 */
	abstract protected String getConstraintsAssignmentPrefix();

	/**
	 *  Returns a string representing the Assignment Postfix
	 *  
	 *  @return a string representing the Assignment Postfix
	 */
	abstract protected String getConstraintsAssignmentPostfix();

	/**
	 *  Writes the Assignment
	 */	
	protected void writeConstraintsAssignment() {
		String constraint = "";
		List<Set<Integer>> cellBlockList = this.getGPCC_BASE().getCellBlockList();
		for (int i = 0; i < cellBlockList.size(); i++) {
			int size = cellBlockList.get(i).size();
			if (size == 0) {
				continue;
			}
			// header
			constraint += this.getComment() + S_ASSIGN + S_SPACE + S_CELL;
			constraint += i;
			constraint += Utils.getNewLine();
			// compute
			constraint += this.getConstraintsAssignmentPrefix();
			Iterator<Integer> it = cellBlockList.get(i).iterator();
			while(it.hasNext()){
				constraint += S_SPACE;
				int blkId = it.next();
				constraint += this.AssignInt(i, blkId);
			}
			constraint += this.getConstraintsAssignmentPostfix();			
			constraint += Utils.getNewLine();
		}		
		// write script
		Utils.appendToFile(constraint, this.getFilename());
	}

	/**
	 *  Returns a string representing the InterBlock Capacity Header for source block defined by parameter <i>srcBlockIdx</i>,
	 *  destination block defined by parameter <i>dstBlockIdx</i>, and,
	 *  capacity defined by parameter <i>capacityIdx</i>
	 *  
	 *  @return a string representing the InterBlock Capacity Header
	 */
	private String getConstraintsInterBlockCapacityHeader(int srcBlockIdx, int dstBlockIdx, int capacityIdx) {
		String rtn = "";
		// header
		rtn += Utils.getTabCharacter();
		rtn += S_BLK + S_SPACE;
		rtn += srcBlockIdx + S_SPACE;
		rtn += S_TO + S_SPACE + S_BLK + S_SPACE;
		rtn += dstBlockIdx + S_SPACE;
		rtn += S_CAPACITY + S_SPACE;
		rtn += capacityIdx;
		return rtn;		
	}

	/**
	 *  Returns a string representing the InterBlock Capacity Lower Before Eqn of capacity defined by parameter <i>c</i>
	 *  
	 *  @return a string representing the InterBlock Capacity Lower Before Eqn of capacity defined by parameter <i>c</i>
	 */
	abstract protected String getConstraintsInterBlockCapacityLowerBeforeEqn(Capacity c);

	/**
	 *  Returns a string representing the InterBlock Capacity Lower After Eqn of capacity defined by parameter <i>c</i>
	 *  
	 *  @return a string representing the InterBlock Capacity Lower After Eqn of capacity defined by parameter <i>c</i>
	 */
	abstract protected String getConstraintsInterBlockCapacityLowerAfterEqn(Capacity c);

	/**
	 *  Returns a string representing the InterBlock Capacity Upper Before Eqn of capacity defined by parameter <i>c</i>
	 *  
	 *  @return a string representing the InterBlock Capacity Upper Before Eqn of capacity defined by parameter <i>c</i>
	 */
	abstract protected String getConstraintsInterBlockCapacityUpperBeforeEqn(Capacity c);

	/**
	 *  Returns a string representing the InterBlock Capacity Upper After Eqn of capacity defined by parameter <i>c</i>
	 *  
	 *  @return a string representing the InterBlock Capacity Upper After Eqn of capacity defined by parameter <i>c</i>
	 */
	abstract protected String getConstraintsInterBlockCapacityUpperAfterEqn(Capacity c);
	
	/**
	 *  Returns a string representing the InterBlock Equ for source block defined by parameter <i>srcBlockIdx</i>, and,
	 *  destination block defined by parameter <i>dstBlockIdx</i>
	 *  
	 *  @param srcBlockIdx the source block
	 *  @param dstBlockIdx the destination block
	 *  @return a string representing the InterBlock Equ for source block defined by parameter <i>srcBlockIdx</i>, and,
	 *  destination block defined by parameter <i>dstBlockIdx</i>
	 */
	abstract protected String getInterBlockEqn(int srcBlockIdx, int dstBlockIdx);

	/**
	 *  Writes the InterBlock Capacity
	 */	
	protected void writeConstraintsInterBlockCapacity() {
		String equ = "";
		List<Block> blockList = this.getGPCC_BASE().getBlockList();
		Partition P = this.getGPCC_BASE().getPartitioner().getPartition();
		for (int interblockIdx = 0; interblockIdx < P.getNumInterBlock(); interblockIdx++) {
			String constraint = "";
			InterBlock interblock = P.getInterBlockAtIdx(interblockIdx);
			Block srcBlock = interblock.getSrcBlock();
			Block dstBlock = interblock.getDstBlock();
			int srcBlockIdx = blockList.indexOf(srcBlock);
			int dstBlockIdx = blockList.indexOf(dstBlock);
			for (int capacityIdx = 0; capacityIdx < interblock.getNumCapacity(); capacityIdx++) {
				Capacity capacity = interblock.getCapacityAtIdx(capacityIdx);
				// header
				constraint += this.getComment() + this.getConstraintsInterBlockCapacityHeader(srcBlockIdx,dstBlockIdx,capacityIdx) + Utils.getNewLine();
				equ = this.getInterBlockEqn(srcBlockIdx, dstBlockIdx);
				// identifier lower
				constraint += this.getComment() + Utils.getTabCharacter() + S_LOWER + Utils.getNewLine();
				constraint += this.getConstraintsInterBlockCapacityLowerBeforeEqn(capacity);
				constraint += equ;
				constraint += this.getConstraintsInterBlockCapacityLowerAfterEqn(capacity);
				// identifier upper
				constraint += this.getComment() + Utils.getTabCharacter() + S_UPPER + Utils.getNewLine();
				constraint += this.getConstraintsInterBlockCapacityUpperBeforeEqn(capacity);
				constraint += equ;
				constraint += this.getConstraintsInterBlockCapacityUpperAfterEqn(capacity);
			}
			// write script
			Utils.appendToFile(constraint, this.getFilename());
		}
	}
	
	/**
	 *  Writes the content
	 */	
	abstract public void writeContent();

	/**
	 *  Write
	 */	
	public void write() {
		Utils.deleteFilename(this.getFilename());
		Utils.createFile(this.getFilename());
		this.writeHeader();
		this.writeContent();
		this.writeEnd();
	}

	/**
	 *  Initializes a newly created GPCC_BASE_PWriter with a GPCC_BASE defined by parameter<i>gpcc</i>,
	 *  and filename defined by parameter <i>filename</i>
	 */
	protected GPCC_BASE_PWriter(GPCC_BASE gpcc, String filename){
		this.setGPCC_BASE(gpcc);
		this.setFilename(filename);
	}

	/**
	 * Setter for <i>filename</i>
	 * @param filename the value to set <i>filename</i>
	*/
	protected void setFilename(final String filename) {
		this.filename = filename;
	}

	/**
	 * Getter for <i>filename</i>
	 * @return value of <i>filename</i>
	*/
	protected String getFilename() {
		return this.filename;
	}
	
	private String filename;
	
	/**
	 * Setter for <i>myGPCC_BASE</i>
	 * @param myGPCC_BASE the value to set <i>myGPCC_BASE</i>
	*/
	protected void setGPCC_BASE(final GPCC_BASE myGPCC_BASE) {
		this.myGPCC_BASE = myGPCC_BASE;
	}

	/**
	 * Getter for <i>myGPCC_BASE</i>
	 * @return value of <i>myGPCC_BASE</i>
	*/
	protected GPCC_BASE getGPCC_BASE() {
		return this.myGPCC_BASE;
	}
	
	private GPCC_BASE myGPCC_BASE;
	
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
	static String S_END = S_COMMENT + "End";
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
	static String S_BINARYRANGE = S_ZERO + S_SPACE + S_ONE;
	static String S_BINARYDOMAIN = S_PARENTHESIS_OPEN + S_DOMAIN + S_SPACE + S_BINARY + S_SPACE + S_BINARYRANGE + S_PARENTHESIS_CLOSE;
	
}

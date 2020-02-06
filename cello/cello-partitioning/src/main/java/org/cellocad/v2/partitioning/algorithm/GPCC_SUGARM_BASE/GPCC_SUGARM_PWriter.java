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
 * The GPCC_SUGARM_PWriter class implements the problem writer for the <i>GPCC_SUGARM_BASE</i> algorithm.
 * 
 * @author Vincent Mirian
 * 
 * @date 2018-05-21
 *
 */
public class GPCC_SUGARM_PWriter extends GPCC_BASE_PWriter{

	protected String getEdgeEquationCoefficient(int coefficient) {
		String rtn = "";
		rtn += Integer.toString(coefficient) + S_SPACE;
		rtn += S_PARENTHESIS_OPEN + S_MULTIPLICATION + S_SPACE;
		return rtn;
	}
	
	protected String getEdgeEquationBetweenBlocks() {
		return S_SPACE;
	}
	 
	protected String getEdgeEquationAfterBlocks() {
		return S_PARENTHESIS_CLOSE + S_SPACE;
	}
	
	protected String getEdgeEquationBeforeTerm() {
		return S_PARENTHESIS_OPEN + S_MULTIPLICATION + S_SPACE;
	};
	
	protected String getEdgeEquationAfterTerm() {
		return S_PARENTHESIS_CLOSE + S_SPACE;
	}		

	protected String getComment() {
		return S_COMMENT;
	}
	
	protected String getObjectiveConstraintEdgeName() {
		return S_OBJECTIVE + S_SPACE + S_MINIMIZE + S_SPACE + S_NUMEDGE;
	}

	protected String getObjectiveConstraintEdgeBeforeTotalEdges() {
		return S_PARENTHESIS_OPEN + S_DOMAIN + S_SPACE + S_EDGE + S_SPACE + S_ZERO + S_SPACE;
	}
	protected String getObjectiveConstraintEdgeAfterTotalEdges() {
		String rtn = "";
		rtn += S_SPACE + S_PARENTHESIS_CLOSE + Utils.getNewLine();
		rtn += S_PARENTHESIS_OPEN + S_INT + S_SPACE + S_NUMEDGE + S_SPACE + S_EDGE + S_PARENTHESIS_CLOSE + Utils.getNewLine();
		rtn += S_PARENTHESIS_OPEN + S_EQUAL + S_SPACE + S_PARENTHESIS_OPEN + S_ADDITION + S_SPACE + S_NUMEDGE;
		return rtn;
	}
	
	protected String getObjectiveConstraintEdgeBeforeEqn() {
		return S_SPACE;
	}
	protected String getObjectiveConstraintEdgeAfterEqn() {
		return S_SPACE;
	}
	
	protected String getObjectiveConstraintEdgeBeforeEndTotalEdges() {
		return S_PARENTHESIS_CLOSE + S_SPACE;
	}
	protected String getObjectiveConstraintEdgeAfterEndTotalEdges() {
		return S_PARENTHESIS_CLOSE + Utils.getNewLine();
	}
	

	protected String getAssignmentVariablePrefix() {
		return S_BINARYDOMAIN + Utils.getNewLine();
	}
	protected String getAssignmentVariablePreAssign() {
		String rtn = "";
		rtn += S_PARENTHESIS_OPEN;
		rtn += S_INT + S_SPACE;
		return rtn;
		
	}
	protected String getAssignmentVariablePostAssign() {
		String rtn = "";
		rtn += S_SPACE + S_BINARY;
		rtn += S_PARENTHESIS_CLOSE;
		rtn += Utils.getNewLine();
		return rtn;
	}
	protected String getAssignmentVariablePostfix() {
		return "";
	}

	protected String getEnd() {
		String rtn = "";
		rtn += this.getComment();
		rtn += S_END;
		rtn += Utils.getNewLine();
		return rtn;
	}
	

	protected String getConstraintsBlockCapacityLowerBeforeEqn(Capacity c) {
		String rtn = "";
		rtn += S_PARENTHESIS_OPEN + c.getLowerBoundType().toString() + S_SPACE + c.getLowerBound() + S_SPACE;
		rtn += S_PARENTHESIS_OPEN + S_ADDITION + S_SPACE;
		return rtn;
	}
	protected String getConstraintsBlockCapacityLowerAfterEqn(Capacity c) {
		String rtn = "";
		rtn += S_PARENTHESIS_CLOSE;
		rtn += S_PARENTHESIS_CLOSE + Utils.getNewLine();
		return rtn;
	}
	protected String getConstraintsBlockCapacityUpperBeforeEqn(Capacity c) {
		String rtn = "";
		rtn += S_PARENTHESIS_OPEN + c.getUpperBoundType().toString() + S_SPACE + S_PARENTHESIS_OPEN + S_ADDITION + S_SPACE;
		return rtn;
	}
	
	protected String getConstraintsBlockCapacityUpperAfterEqn(Capacity c) {
		String rtn = "";
		rtn += S_PARENTHESIS_CLOSE + S_SPACE;
		rtn += c.getUpperBound();
		rtn += S_PARENTHESIS_CLOSE + Utils.getNewLine();
		return rtn;
	}
	
	protected String getConstraintsAssignmentPrefix() {
		String rtn = "";
		rtn += S_PARENTHESIS_OPEN + S_EQUAL + S_SPACE + S_ONE + S_SPACE;
		rtn += S_PARENTHESIS_OPEN + S_ADDITION;	
		return rtn;
	}
	
	protected String getConstraintsAssignmentPostfix() {
		String rtn = "";
		rtn += S_PARENTHESIS_CLOSE + S_SPACE + S_PARENTHESIS_CLOSE;	
		return rtn;
	}
	
	protected String getConstraintsInterBlockCapacityLowerBeforeEqn(Capacity c) {
		String rtn = "";
		rtn += S_PARENTHESIS_OPEN + c.getLowerBoundType().toString() + S_SPACE + c.getLowerBound() + S_SPACE;
		rtn += S_PARENTHESIS_OPEN + S_ADDITION + S_SPACE;
		return rtn;
	}
	protected String getConstraintsInterBlockCapacityLowerAfterEqn(Capacity c) {
		String rtn = "";
		rtn += S_PARENTHESIS_CLOSE;
		rtn += S_PARENTHESIS_CLOSE + Utils.getNewLine();
		return rtn;
		
	}
	protected String getConstraintsInterBlockCapacityUpperBeforeEqn(Capacity c) {
		String rtn = "";
		rtn += S_PARENTHESIS_OPEN + c.getUpperBoundType().toString() + S_SPACE + S_PARENTHESIS_OPEN + S_ADDITION + S_SPACE;
		return rtn;
	}
	protected String getConstraintsInterBlockCapacityUpperAfterEqn(Capacity c) {
		String rtn = "";
		rtn += S_PARENTHESIS_CLOSE + S_SPACE;
		rtn += c.getUpperBound();
		rtn += S_PARENTHESIS_CLOSE + Utils.getNewLine();	
		return rtn;
	}

	protected String getConstraintsOutputConnectionsCapacityPrefix(int blockIdx) {
		String rtn = "";
		/*rtn += S_PARENTHESIS_OPEN + S_INT + S_SPACE + this.getConstraintsOutputConnectionsCapacityVariable(blockIdx);
		rtn += S_SPACE + S_ZERO + S_SPACE + this.getConstraintsOutputConnectionsCapacityBlockMaxConnections(blockIdx);
		rtn += S_PARENTHESIS_CLOSE + Utils.getNewLine();*/
		return rtn;
	}

	protected String getConstraintsInputConnectionsCapacityPrefix(int blockIdx) {
		String rtn = "";
		/*rtn += S_PARENTHESIS_OPEN + S_INT + S_SPACE + this.getConstraintsInputConnectionsCapacityVariable(blockIdx);
		rtn += S_SPACE + S_ZERO + S_SPACE + this.getConstraintsInputConnectionsCapacityBlockMaxConnections(blockIdx);
		rtn += S_PARENTHESIS_CLOSE + Utils.getNewLine();*/
		return rtn;
	}
	
	protected String getConstraintsInOutConnectionsCapacityPrefix(int blockIdx) {
		String rtn = "";
		/*rtn += S_PARENTHESIS_OPEN + S_INT + S_SPACE + this.getConstraintsInputConnectionsCapacityVariable(blockIdx);
		rtn += S_SPACE + S_ZERO + S_SPACE + this.getConstraintsInputConnectionsCapacityBlockMaxConnections(blockIdx);
		rtn += S_PARENTHESIS_CLOSE + Utils.getNewLine();*/
		return rtn;
	}


	protected String getInterBlockEqn(int srcBlockIdx, int dstBlockIdx) {
		String rtn = "";
		rtn += this.getConstraintsConnectionsCapacityCellList(true, srcBlockIdx, dstBlockIdx);
		return rtn;
	}

	//TODO: make abstract
	protected void writeConstraintsObjectiveNumBlocks() {
		String constraint = "";
		String constraintHeader = "";
		String equ = "";
		List<List<Set<Integer>>> blockCapacityCellList = this.getGPCC_BASE().getBlockCapacityCellList();
		constraintHeader += this.getComment() + S_OBJECTIVE + S_SPACE + "NumBlocks" + Utils.getNewLine();
		constraintHeader += "(" + S_INT + S_SPACE + "NumBlocks " + this.getLowerBoundBlocks() + S_SPACE + this.getUpperBoundBlocks() + ")" + Utils.getNewLine();
		constraintHeader += "(" + S_OBJECTIVE + S_SPACE + "minimize NumBlocks)" + Utils.getNewLine();
		// 
		for (int blockIdx = 0; blockIdx < blockCapacityCellList.size(); blockIdx++) {
			List<Set<Integer>> cellList = blockCapacityCellList.get(blockIdx);
			Set<Integer> allCells = this.union(cellList);
			equ = this.getConstraintsBlockCapacityCellList(blockIdx, allCells);
			constraint += " ( if ( < 0 ";
			constraint += " ( + " + equ + ") )";
			constraint += " 1 0 )";
		}
		constraint = "( <= (+ " + constraint + " ) NumBlocks )";
		constraint += Utils.getNewLine();
		constraint = constraintHeader + constraint;
		// write script
		Utils.appendToFile(constraint, this.getFilename());
	}

	public void writeContent() {
		if (this.getContents() == null) {
			this.writeAssignmentVariable();
			// this.writeConstraintsObjectiveEdge();
			this.writeConstraintsBlockCapacity();
			this.writeConstraintsBlockInputConnectionsCapacity();
			this.writeConstraintsBlockOutputConnectionsCapacity();
			this.writeConstraintsBlockInOutConnectionsCapacity();
			this.writeConstraintsAssignment();
			this.writeConstraintsInterBlockCapacity();
			try {
				File file = new File(this.getFilename());
				FileInputStream fis = new FileInputStream(file);
				byte[] data = new byte[(int) file.length()];
				fis.read(data);
				this.setContents(new String(data, "UTF-8"));
				fis.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		else {
			Utils.appendToFile(this.getContents(), this.getFilename());
		}
		this.writeConstraintsObjectiveNumBlocks();
	}
	
	GPCC_SUGARM_PWriter(GPCC_BASE gpcc, String filename, int upperbound) {
		super(gpcc, filename);
		this.setLowerBoundBlocks(0);
		this.setUpperBoundBlocks(upperbound);
		this.setContents(null);
	}

	/**
	 * Setter for <i>lowerBoundBlocks</i>
	 * @param lowerBoundBlocks the value to set <i>lowerBoundBlocks</i>
	*/
	public void setLowerBoundBlocks(final int lowerBoundBlocks) {
		this.lowerBoundBlocks = lowerBoundBlocks;
	}

	/**
	 * Getter for <i>lowerBoundBlocks</i>
	 * @return value of <i>lowerBoundBlocks</i>
	*/
	protected int getLowerBoundBlocks() {
		return this.lowerBoundBlocks;
	}
	
	private int lowerBoundBlocks;
	
	/**
	 * Setter for <i>upperBoundBlocks</i>
	 * @param upperBoundBlocks the value to set <i>upperBoundBlocks</i>
	*/
	public void setUpperBoundBlocks(final int upperBoundBlocks) {
		this.upperBoundBlocks = upperBoundBlocks;
	}

	/**
	 * Getter for <i>upperBoundBlocks</i>
	 * @return value of <i>upperBoundBlocks</i>
	*/
	protected int getUpperBoundBlocks() {
		return this.upperBoundBlocks;
	}
	
	private int upperBoundBlocks;
	
	//TODO: save file for next iteration
	// reduce time
	/**
	 * Setter for <i>contents</i>
	 * @param contents the value to set <i>contents</i>
	*/
	protected void setContents(final String contents) {
		this.contents = contents;
	}

	/**
	 * Getter for <i>contents</i>
	 * @return value of <i>contents</i>
	*/
	protected String getContents() {
		return this.contents;
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
	static String S_BINARYRANGE = S_ZERO + S_SPACE + S_ONE;
	static String S_BINARYDOMAIN = S_PARENTHESIS_OPEN + S_DOMAIN + S_SPACE + S_BINARY + S_SPACE + S_BINARYRANGE + S_PARENTHESIS_CLOSE;
}

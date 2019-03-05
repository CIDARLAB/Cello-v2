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
package org.cellocad.cello2.partitioning.algorithm.GPCC_SUGARM_BASE;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cellocad.cello2.common.ExecCommand;
import org.cellocad.cello2.common.Utils;
import org.cellocad.cello2.partitioning.algorithm.GPCC_BASE.GPCC_BASE;
import org.cellocad.cello2.partitioning.algorithm.GPCC_SUGARM_BASE.data.GPCC_SUGARM_BASENetlistData;
import org.cellocad.cello2.partitioning.algorithm.GPCC_SUGARM_BASE.data.GPCC_SUGARM_BASENetlistEdgeData;
import org.cellocad.cello2.partitioning.algorithm.GPCC_SUGARM_BASE.data.GPCC_SUGARM_BASENetlistNodeData;
import org.cellocad.cello2.partitioning.common.Block;
import org.cellocad.cello2.partitioning.common.Move;
import org.cellocad.cello2.partitioning.common.Partition;
import org.cellocad.cello2.partitioning.common.PartitionUtils;
import org.cellocad.cello2.partitioning.netlist.PTNetlistNode;
import org.cellocad.cello2.partitioning.netlist.PTNetlistUtils;
import org.cellocad.cello2.partitioning.runtime.environment.PTArgString;
import org.cellocad.cello2.results.netlist.Netlist;
import org.cellocad.cello2.results.netlist.NetlistEdge;
import org.cellocad.cello2.results.netlist.NetlistNode;

import jp.kobe_u.sugar.SugarMain;

/**
 * The GPCC_SUGARM_BASE class implements the <i>GPCC_SUGARM_BASE</i> algorithm in the <i>partitioning</i> stage.
 * 
 * @author Vincent Mirian
 * 
 * @date 2018-05-21
 *
 */
public class GPCC_SUGARM_BASE extends GPCC_BASE{

	/**
	 *  Returns the <i>GPCC_SUGARM_BASENetlistNodeData</i> of the <i>node</i>
	 *
	 *  @param node a node within the <i>netlist</i> of this instance
	 *  @return the <i>GPCC_SUGARM_BASENetlistNodeData</i> instance if it exists, null otherwise
	 */
	protected GPCC_SUGARM_BASENetlistNodeData getGPCC_SUGARM_BASENetlistNodeData(NetlistNode node){
		GPCC_SUGARM_BASENetlistNodeData rtn = null;
		rtn = (GPCC_SUGARM_BASENetlistNodeData) node.getNetlistNodeData();
		return rtn;
	}

	/**
	 *  Returns the <i>GPCC_SUGARM_BASENetlistEdgeData</i> of the <i>edge</i>
	 *
	 *  @param edge an edge within the <i>netlist</i> of this instance
	 *  @return the <i>GPCC_SUGARM_BASENetlistEdgeData</i> instance if it exists, null otherwise
	 */
	protected GPCC_SUGARM_BASENetlistEdgeData getGPCC_SUGARM_BASENetlistEdgeData(NetlistEdge edge){
		GPCC_SUGARM_BASENetlistEdgeData rtn = null;
		rtn = (GPCC_SUGARM_BASENetlistEdgeData) edge.getNetlistEdgeData();
		return rtn;
	}

	/**
	 *  Returns the <i>GPCC_SUGARM_BASENetlistData</i> of the <i>netlist</i>
	 *
	 *  @param netlist the netlist of this instance
	 *  @return the <i>GPCC_SUGARM_BASENetlistData</i> instance if it exists, null otherwise
	 */
	protected GPCC_SUGARM_BASENetlistData getGPCC_SUGARM_BASENetlistData(Netlist netlist){
		GPCC_SUGARM_BASENetlistData rtn = null;
		rtn = (GPCC_SUGARM_BASENetlistData) netlist.getNetlistData();
		return rtn;
	}

	/**
	 *  Gets the Constraint data from the NetlistConstraintFile
	 */
	@Override
	protected void getConstraintFromNetlistConstraintFile() {

	}

	/**
	 *  Gets the data from the UCF
	 */
	@Override
	protected void getDataFromUCF() {
		super.getDataFromUCF();
	}

	/**
	 *  Set parameter(s) value(s) of the algorithm
	 */
	@Override
	protected void setParameterValues() {
		super.setParameterValues();
		Boolean present = true;
		present = this.getAlgorithmProfile().getIntParameter("LowerBoundValue").getFirst();
		if (present) {
			this.setLowerBoundValue(this.getAlgorithmProfile().getIntParameter("LowerBoundValue").getSecond());
		}
		present = this.getAlgorithmProfile().getIntParameter("UpperBoundValue").getFirst();
		if (present) {
			this.setUpperBoundValue(this.getAlgorithmProfile().getIntParameter("UpperBoundValue").getSecond());
		}
		present = this.getAlgorithmProfile().getIntParameter("Attempts").getFirst();
		if (present) {
			this.setAttempts(this.getAlgorithmProfile().getIntParameter("Attempts").getSecond());
		}
		present = this.getAlgorithmProfile().getIntParameter("Iteration").getFirst();
		if (present) {
			this.setIteration(this.getAlgorithmProfile().getIntParameter("Iteration").getSecond());
		}
	}

	/**
	 *  Validate parameter value of the algorithm
	 */
	@Override
	protected void validateParameterValues() {
		super.validateParameterValues();
	}

	/**
	 *  Perform preprocessing
	 */
	@Override
	protected void preprocessing() {
		super.preprocessing();
		// prepare file
		String outputDir = this.getRuntimeEnv().getOptionValue(PTArgString.OUTPUTDIR);
		String inputFilename = this.getNetlist().getInputFilename();
		String filename = Utils.getFilename(inputFilename);
		this.setCSPFilename(outputDir + Utils.getFileSeparator() + filename + "_SUGARM.csp");
		this.setCNFFilename(outputDir + Utils.getFileSeparator() + filename + "_SUGARM.cnf");
		this.setMAPFilename(outputDir + Utils.getFileSeparator() + filename + "_SUGARM.map");
		this.setOUTFilename(outputDir + Utils.getFileSeparator() + filename + "_SUGARM.out");
		this.setPartitionDotFile(outputDir + Utils.getFileSeparator() + filename + "_SUGARM.dot");	
		// exec
		String exec = "";
		exec += "minisat ";
		this.setExec(exec);
	}

	/**
	 *  Run the (core) algorithm
	 */
	@Override
	protected void run() {
		int temp = 0;
		int numAttempts = 0;
		int attempts = this.getAttempts();
		int numIteration = 0;
		int iteration = this.getIteration();
		// lower
		int lower = 1;
		temp = this.getLowerBoundValue();
		if ((temp > lower) && (temp >= 0)) {
			lower = temp;
		}
		// upper
		int upper = this.getBlockList().size();
		temp = this.getUpperBoundValue();
		if ((temp < upper) && (temp >= 0)) {
			upper = temp;
		}
		// other
		int currentBlockNum = 0;
		int bestBlockNum = -1;
		GPCC_SUGARM_PWriter writer = new GPCC_SUGARM_PWriter(this, this.getCSPFilename(), currentBlockNum);
		// TODO: open window of binary search
		// TODO: enable random point in range
		// binary search for optimal numBlocks
		while (
				// binary search
				(lower <= upper) &&
				// attempts
				((attempts < 0) || (attempts >= 0) && (numAttempts < attempts)) &&
				// iteration
				((iteration < 0) || (iteration >= 0) && (numIteration < iteration))
				) {
			// get middle point
			currentBlockNum = Double.valueOf(Math.floor((lower + upper)/2.0)).intValue();
			writer.setLowerBoundBlocks(currentBlockNum);
			writer.setUpperBoundBlocks(currentBlockNum);
			this.logInfo(S_HEADER_FOOTER);
			this.logInfo("Attempting with " + currentBlockNum + " blocks.");
			this.logInfo(S_HEADER_FOOTER);
			// write info
			writer.write();
			// encode
			this.logInfo("Encoding...");
			String[] argsEnc = {S_V, S_V, S_ENCODE, this.getCSPFilename(), this.getCNFFilename(), this.getMAPFilename()};
			SugarMain.main(argsEnc);
			// minisat
			this.logInfo("Solving...");
			ExecCommand proc = Utils.executeAndWaitForCommand(this.getExec() + this.getCNFFilename() + " " + this.getOUTFilename());
			this.logInfo(proc.getOutput());
			// is unsat, adjust lower and upper
			if (isUNSAT(proc.getOutput())) {
				lower = currentBlockNum + 1; 
			}
			else {
				bestBlockNum = currentBlockNum;
				upper = currentBlockNum - 1;
				// decode
				this.decode();
				// assign
				this.logInfo("Assigning...");
				this.applyResult();
				String outputDir = this.getRuntimeEnv().getOptionValue(PTArgString.OUTPUTDIR);
				Partition P = this.getPartitioner().getPartition();
				PartitionUtils.writeDotFileForPartition(P, outputDir+Utils.getFileSeparator()+P.getName()+ "_blocks_" + currentBlockNum + "_final.dot");
				numIteration++;
			}
			numAttempts++;
		}
		if (bestBlockNum < 0) {
			this.logInfo("GPCC_SUGARM cannot find a solution!");
			Utils.exit(-1);
		}
		this.logInfo("Using block number is : " + bestBlockNum);
	}

	protected boolean isUNSAT(String str) {
		boolean rtn = false;
		rtn = str.contains(S_UNSATISFIABLE);
		return rtn;
	}
	
	protected void applyResult() {
		List<PTNetlistNode> ptCellList = this.getPTCellList();
		List<Block> blockList = this.getBlockList();
		List<Move> moves = new ArrayList<Move>();
		Move move = null;
		Partition partition = this.getPartitioner().getPartition();
		String line = null;
		StringTokenizer stk = new StringTokenizer(this.getOutput(), Utils.getNewLine());
		while (stk.hasMoreElements()) {
			line = stk.nextToken();
			line.trim();
			// status
			if (line.startsWith(S_SOLUTIONMARKER)) {
				this.logInfo(S_HEADER_LINE_PREFIX + S_SPACE + line);
				if (line.contains(S_UNSATISFIABLE)) {
					this.logInfo("GPCC_SUGARM cannot find a solution!");
					Utils.exit(-1);
				}
				continue;
			}
			// assignment
			if (line.startsWith(S_ASSIGNMARKER)) {
				int cellId = -1;
				int blockId = -1;
				StringTokenizer strtok = new StringTokenizer(line);
				String token = null;
				token = strtok.nextToken();
				String Assignment = "";
				int prefixIdx = 0;
				int blkIdx = 0;
				if (strtok.hasMoreTokens()) {
					token = strtok.nextToken();
					prefixIdx = token.indexOf(S_ASSIGN + S_CELL);
					blkIdx = token.indexOf(S_BLK);
					if ((prefixIdx == 0) && (blkIdx > prefixIdx)){
						Assignment = token;
					}
				}
				if (Assignment.isEmpty()) {
					continue;
				}
				if (strtok.hasMoreTokens()) {
					token = strtok.nextToken();
					if ((Math.round(Double.parseDouble(token))) == 1) {
						cellId = Integer.parseInt(Assignment.substring(S_ASSIGN.length() + S_CELL.length(), blkIdx));
						blockId = Integer.parseInt(Assignment.substring(blkIdx + S_BLK.length()));
					}
				}
				if ((cellId >= 0) && (blockId >= 0)) {
					PTNetlistNode ptnode = ptCellList.get(cellId);
					Block block = blockList.get(blockId);
					move = new Move(ptnode, ptnode.getMyBlock(), block);
					moves.add(move);
				}
				continue;
			}
		}
	    // do moves
	    partition.doMoves(moves);
	}
	
	/**
	 *  Perform decode
	 */
	protected void decode() {
		// decode
		this.logInfo("Decoding...");
		// Create a stream to hold the output
	    ByteArrayOutputStream baos = new ByteArrayOutputStream();
	    PrintStream ps = new PrintStream(baos);
	    // Save the old System.out!
	    PrintStream old = System.out;
	    // Tell Java to use your special stream
	    System.setOut(ps);
	    // Run Decode
		String[] argsDec = {S_V, S_V, S_DECODE, this.getOUTFilename(), this.getMAPFilename()};
		SugarMain.main(argsDec);
	    // Put things back
	    System.out.flush();
	    System.setOut(old);
	    // Show what happened
	    this.setOutput(baos.toString());
	    this.logInfo(this.getOutput());
	}
	
	/**
	 *  Perform postprocessing
	 */
	@Override
	protected void postprocessing() {
		super.postprocessing();
		this.logResultHeader();
		this.getNetlister().getNetlist();
		String outputDir = this.getRuntimeEnv().getOptionValue(PTArgString.OUTPUTDIR);
		Partition P = this.getPartitioner().getPartition();
		PartitionUtils.writeDotFileForPartition(P, outputDir+Utils.getFileSeparator()+P.getName()+"_final.dot");
		// delete
		if (B_CLEANUP) {
			Utils.deleteFilename(this.getCSPFilename());
			Utils.deleteFilename(this.getCNFFilename());
			Utils.deleteFilename(this.getMAPFilename());
			Utils.deleteFilename(this.getOUTFilename());
		}
		PTNetlistUtils.writeDotFileForPartition(this.getNetlister().getPTNetlist(), this.getPartitionDotFile());
	}

	/**
	 *  Logs the Result header
	 */
	protected void logResultHeader() {
		this.logInfo(S_HEADER_FOOTER);
		this.logInfo(S_HEADER_LINE_PREFIX + Utils.getTabCharacterRepeat(S_TAB_NUM) + S_RESULT);
		this.logInfo(S_HEADER_FOOTER);
	}

	/**
	 * Setter for <i>CSPFilename</i>
	 * @param str the value to set <i>CSPFilename</i>
	*/
	protected void setCSPFilename(final String str) {
		this.CSPFilename = str;
	}

	/**
	 * Getter for <i>CSPFilename</i>
	 * @return value of <i>CSPFilename</i>
	*/
	protected String getCSPFilename() {
		return this.CSPFilename;
	}
	
	private String CSPFilename;
	
	/**
	 * Setter for <i>CNFFilename</i>
	 * @param str the value to set <i>CNFFilename</i>
	*/
	protected void setCNFFilename(final String str) {
		this.CNFFilename = str;
	}

	/**
	 * Getter for <i>CNFFilename</i>
	 * @return value of <i>CNFFilename</i>
	*/
	protected String getCNFFilename() {
		return this.CNFFilename;
	}
	
	private String CNFFilename;

	/**
	 * Setter for <i>OUTFilename</i>
	 * @param str the value to set <i>OUTFilename</i>
	*/
	protected void setOUTFilename(final String str) {
		this.OUTFilename = str;
	}

	/**
	 * Getter for <i>OUTFilename</i>
	 * @return value of <i>OUTFilename</i>
	*/
	protected String getOUTFilename() {
		return this.OUTFilename;
	}
	
	private String OUTFilename;
	
	/**
	 * Setter for <i>MAPFilename</i>
	 * @param str the value to set <i>MAPFilename</i>
	*/
	protected void setMAPFilename(final String str) {
		this.MAPFilename = str;
	}

	/**
	 * Getter for <i>MAPFilename</i>
	 * @return value of <i>MAPFilename</i>
	*/
	protected String getMAPFilename() {
		return this.MAPFilename;
	}
	
	private String MAPFilename;

	/**
	 * Setter for <i>Exec</i>
	 * @param str the value to set <i>Exec</i>
	*/
	protected void setExec(final String str) {
		this.Exec = str;
	}

	/**
	 * Getter for <i>Exec</i>
	 * @return value of <i>Exec</i>
	*/
	protected String getExec() {
		return this.Exec;
	}
	
	private String Exec;
	
	/**
	 * Setter for <i>Output</i>
	 * @param str the value to set <i>Output</i>
	*/
	protected void setOutput(final String str) {
		this.Output = str;
	}

	/**
	 * Getter for <i>Output</i>
	 * @return value of <i>Output</i>
	*/
	protected String getOutput() {
		return this.Output;
	}
	
	private String Output;
	
	/**
	 * Setter for <i>partitionDot</i>
	 * @param str the value to set <i>partitionDot</i>
	*/
	protected void setPartitionDotFile(final String str) {
		this.partitionDot = str;
	}

	/**
	 * Getter for <i>partitionDot</i>
	 * @return value of <i>partitionDot</i>
	*/
	protected String getPartitionDotFile() {
		return this.partitionDot;
	}

	private String partitionDot;
	
	/**
	 * Setter for <i>LowerBoundValue</i>
	 * @param value the value to set <i>LowerBoundValue</i>
	*/
	protected void setLowerBoundValue(final int value){
		this.LowerBoundValue = value;
	}

	/**
	 * Getter for <i>LowerBoundValue</i>
	 * @return value of <i>LowerBoundValue</i>
	*/
	protected int getLowerBoundValue(){
		return this.LowerBoundValue;
	}

	private int LowerBoundValue;

	/**
	 * Setter for <i>UpperBoundValue</i>
	 * @param value the value to set <i>UpperBoundValue</i>
	*/
	protected void setUpperBoundValue(final int value){
		this.UpperBoundValue = value;
	}

	/**
	 * Getter for <i>UpperBoundValue</i>
	 * @return value of <i>UpperBoundValue</i>
	*/
	protected int getUpperBoundValue(){
		return this.UpperBoundValue;
	}

	private int UpperBoundValue;

	/**
	 * Setter for <i>Attempts</i>
	 * @param value the value to set <i>Attempts</i>
	*/
	protected void setAttempts(final int value){
		this.Attempts = value;
	}

	/**
	 * Getter for <i>Attempts</i>
	 * @return value of <i>Attempts</i>
	*/
	protected int getAttempts(){
		return this.Attempts;
	}

	private int Attempts;

	/**
	 * Setter for <i>Iteration</i>
	 * @param value the value to set <i>Iteration</i>
	*/
	protected void setIteration(final int value){
		this.Iteration = value;
	}

	/**
	 * Getter for <i>Iteration</i>
	 * @return value of <i>Iteration</i>
	*/
	protected int getIteration(){
		return this.Iteration;
	}

	private int Iteration;


	/**
	 *  Returns the Logger for the <i>GPCC_SUGARM_BASE</i> algorithm
	 *
	 *  @return the logger for the <i>GPCC_SUGARM_BASE</i> algorithm
	 */
	@Override
	protected Logger getLogger() {
		return GPCC_SUGARM_BASE.logger;
	}
	
	private static final Logger logger = LogManager.getLogger(GPCC_SUGARM_BASE.class);

	static private boolean B_CLEANUP = false;
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
	static protected String S_ASSIGN = "Assign";
	static protected String S_CELL = "C";
	static protected String S_BLK = "BLK";
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
	static protected String S_SPACE = " ";
	static String S_UNDERSCORE = "_";
	protected static String S_HEADER_FOOTER = "+-----------------------------------------------------";
	protected static String S_HEADER_LINE_PREFIX = "|";
	protected static String S_RESULT = "    RESULTS";
	protected static String S_V = "-v";
	static protected int S_TAB_NUM = 3;
}

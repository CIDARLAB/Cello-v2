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
package org.cellocad.v2.partitioning.algorithm.GPCC_SCIP_BASE;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cellocad.v2.common.Utils;
import org.cellocad.v2.partitioning.algorithm.GPCC_BASE.GPCC_BASE;
import org.cellocad.v2.partitioning.algorithm.GPCC_SCIP_BASE.data.GPCC_SCIP_BASENetlistData;
import org.cellocad.v2.partitioning.algorithm.GPCC_SCIP_BASE.data.GPCC_SCIP_BASENetlistEdgeData;
import org.cellocad.v2.partitioning.algorithm.GPCC_SCIP_BASE.data.GPCC_SCIP_BASENetlistNodeData;
import org.cellocad.v2.partitioning.runtime.environment.PTArgString;
import org.cellocad.v2.results.netlist.Netlist;
import org.cellocad.v2.results.netlist.NetlistEdge;
import org.cellocad.v2.results.netlist.NetlistNode;

/**
 * The GPCC_SCIP_BASE class implements the <i>GPCC_SCIP_BASE</i> algorithm in the <i>partitioning</i> stage.
 * 
 * @author Vincent Mirian
 * 
 * @date 2018-05-21
 *
 */
public class GPCC_SCIP_BASE extends GPCC_BASE{

	/**
	 *  Returns the <i>GPCC_SCIP_BASENetlistNodeData</i> of the <i>node</i>
	 *
	 *  @param node a node within the <i>netlist</i> of this instance
	 *  @return the <i>GPCC_SCIP_BASENetlistNodeData</i> instance if it exists, null otherwise
	 */
	protected GPCC_SCIP_BASENetlistNodeData getGPCC_SCIP_BASENetlistNodeData(NetlistNode node){
		GPCC_SCIP_BASENetlistNodeData rtn = null;
		rtn = (GPCC_SCIP_BASENetlistNodeData) node.getNetlistNodeData();
		return rtn;
	}

	/**
	 *  Returns the <i>GPCC_SCIP_BASENetlistEdgeData</i> of the <i>edge</i>
	 *
	 *  @param edge an edge within the <i>netlist</i> of this instance
	 *  @return the <i>GPCC_SCIP_BASENetlistEdgeData</i> instance if it exists, null otherwise
	 */
	protected GPCC_SCIP_BASENetlistEdgeData getGPCC_SCIP_BASENetlistEdgeData(NetlistEdge edge){
		GPCC_SCIP_BASENetlistEdgeData rtn = null;
		rtn = (GPCC_SCIP_BASENetlistEdgeData) edge.getNetlistEdgeData();
		return rtn;
	}

	/**
	 *  Returns the <i>GPCC_SCIP_BASENetlistData</i> of the <i>netlist</i>
	 *
	 *  @param netlist the netlist of this instance
	 *  @return the <i>GPCC_SCIP_BASENetlistData</i> instance if it exists, null otherwise
	 */
	protected GPCC_SCIP_BASENetlistData getGPCC_SCIP_BASENetlistData(Netlist netlist){
		GPCC_SCIP_BASENetlistData rtn = null;
		rtn = (GPCC_SCIP_BASENetlistData) netlist.getNetlistData();
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
	}

	/**
	 *  Validate parameter value of the algorithm
	 */
	@Override
	protected void validateParameterValues() {
		super.validateParameterValues();
	}
	/**
	 *  Write SCIP Script File
	 */
	private void writeSCIPScriptFile() {
		// create SCIP script
		String script = "";
		int scale = 0;
		int numVertex = this.getNetlist().getNumVertex();
		double ratio = numVertex/1000.0;
		if (ratio > 1) {
			ratio = 1.0;
		}
		// read test.lp display problem
		script += "read ";
		script += this.getSCIPConstraintFilename();
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
		script += "set limits time ";//259200 = 72 hours
		scale = 3600;
		script += (int) scale + (Math.round(ratio * scale));
		// script += (int) (Math.round(ratio * ratio * scale) + Math.round(ratio * 250));
		script += Utils.getNewLine();
		*/
		// limits nodes
		/*script += "set limits nodes ";
		scale = 30;
		scale = (int) Math.ceil(ratio * scale);
		if (scale < 7) {
			scale = 7;
		}
		script += scale;
		script += Utils.getNewLine();*/
		// optimize
		script += "optimize";
		script += Utils.getNewLine();
		// display solution
		// script += "display solution";
		// script += Utils.getNewLine();
		// write solution
		script += "write solution ";
		script += this.getSCIPOutputFilename();
		script += Utils.getNewLine();
		// quit
		script += "quit";
		// write SCIP script
		try {
			OutputStream outputStream = new FileOutputStream(this.getSCIPScriptFilename());
			Writer outputStreamWriter = new OutputStreamWriter(outputStream);
			outputStreamWriter.write(script);
			outputStreamWriter.close();
			outputStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 *  Setup Exec
	 */
	private void setupExec() {
		// exec
		String exec = "";
		exec += "scip -b ";
		this.setSCIPExec(exec);
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
		this.setSCIPConstraintFilename(outputDir + Utils.getFileSeparator() + filename + "_SCIPConstraint.lp");
		this.setSCIPScriptFilename(outputDir + Utils.getFileSeparator() + filename + "_SCIPScript");
		this.setSCIPOutputFilename(outputDir + Utils.getFileSeparator() + filename + "_SCIPOutput");	
		this.setPartitionDotFile(outputDir + Utils.getFileSeparator() + filename + "_SCIP.dot");	
		// SCIPConstraint
		GPCCSCIPBaseConstraintUtils.writeSCIPConstraintFile(this, this.getSCIPConstraintFilename());
		// exec
		this.setupExec();
		// SCIPScript
		this.writeSCIPScriptFile();
	}

	/**
	 *  Run the (core) algorithm
	 */
	@Override
	protected void run() {

	}

	/**
	 *  Perform postprocessing
	 */
	@Override
	protected void postprocessing() {
		super.postprocessing();
	}
	/**
	 * Setter for <i>scipConstraintFilename</i>
	 * @param str the value to set <i>scipConstraintFilename</i>
	*/
	protected void setSCIPConstraintFilename(final String str) {
		this.scipConstraintFilename = str;
	}

	/**
	 * Getter for <i>scipConstraintFilename</i>
	 * @return value of <i>scipConstraintFilename</i>
	*/
	protected String getSCIPConstraintFilename() {
		return this.scipConstraintFilename;
	}
	
	/**
	 * Setter for <i>scipScriptFilename</i>
	 * @param str the value to set <i>scipScriptFilename</i>
	*/
	protected void setSCIPScriptFilename(final String str) {
		this.scipScriptFilename = str;
	}

	/**
	 * Getter for <i>scipScriptFilename</i>
	 * @return value of <i>scipScriptFilename</i>
	*/
	protected String getSCIPScriptFilename() {
		return this.scipScriptFilename;
	}

	/**
	 * Setter for <i>scipOutputFilename</i>
	 * @param str the value to set <i>scipOutputFilename</i>
	*/
	protected void setSCIPOutputFilename(final String str) {
		this.scipOutputFilename = str;
	}

	/**
	 * Getter for <i>scipOutputFilename</i>
	 * @return value of <i>scipOutputFilename</i>
	*/
	protected String getSCIPOutputFilename() {
		return this.scipOutputFilename;
	}
	
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
	 * Setter for <i>scipExec</i>
	 * @param str the value to set <i>scipExec</i>
	*/
	protected void setSCIPExec(final String str) {
		this.scipExec = str;
	}

	/**
	 * Getter for <i>scipExec</i>
	 * @return value of <i>scipExec</i>
	*/
	protected String getSCIPExec() {
		return this.scipExec;
	}
	
	private String scipConstraintFilename;
	private String scipScriptFilename;
	private String scipOutputFilename;
	private String scipExec;

	/**
	 *  Returns the Logger for the <i>GPCC_SCIP_BASE</i> algorithm
	 *
	 *  @return the logger for the <i>GPCC_SCIP_BASE</i> algorithm
	 */
	@Override
	protected Logger getLogger() {
		return GPCC_SCIP_BASE.logger;
	}
	
	private static final Logger logger = LogManager.getLogger(GPCC_SCIP_BASE.class);

	protected static String S_HEADER_FOOTER = "+-----------------------------------------------------";
	protected static String S_HEADER_LINE_PREFIX = "|";
	protected static String S_RESULT = "    RESULTS";
	static private String S_STDOUT = "    STDOUT";
	static private String S_STDERR = "    STDERR";
	static protected int S_TAB_NUM = 3;
	static private String S_SOLUTIONSTATUS = "solution status:";
	static private String S_INFEASIBLE = "infeasible";
	static private String S_ASSIGNTAG = "(obj:0)";
	static private boolean B_CLEANUP = false;
}

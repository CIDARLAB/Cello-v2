/**
 * Copyright (C) 2020
 * Massachusetts Institute of Technology (MIT)
 * Boston University (BU)
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
package org.cellocad.cello2.logicSynthesis.algorithm.Yosys;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cellocad.cello2.common.CelloException;
import org.cellocad.cello2.common.ExecCommand;
import org.cellocad.cello2.common.Utils;
import org.cellocad.cello2.logicSynthesis.algorithm.LSAlgorithm;
import org.cellocad.cello2.logicSynthesis.algorithm.Yosys.data.YosysDataUtils;
import org.cellocad.cello2.logicSynthesis.algorithm.Yosys.data.YosysNetlistData;
import org.cellocad.cello2.logicSynthesis.algorithm.Yosys.data.YosysNetlistEdgeData;
import org.cellocad.cello2.logicSynthesis.algorithm.Yosys.data.YosysNetlistNodeData;
import org.cellocad.cello2.logicSynthesis.netlist.OutputOrTransform;
import org.cellocad.cello2.logicSynthesis.runtime.environment.LSArgString;
import org.cellocad.cello2.results.netlist.Netlist;
import org.cellocad.cello2.results.netlist.NetlistEdge;
import org.cellocad.cello2.results.netlist.NetlistNode;
import org.json.JSONException;
import org.json.simple.JSONArray;

/**
 * The Yosys class implements the <i>Yosys</i> algorithm in the <i>logicSynthesis</i> stage.
 * 
 * @author Vincent Mirian
 * 
 * @date 2018-05-21
 *
 */
public class Yosys extends LSAlgorithm{

	/**
	 *  Returns the <i>YosysNetlistNodeData</i> of the <i>node</i>
	 *
	 *  @param node a node within the <i>netlist</i> of this instance
	 *  @return the <i>YosysNetlistNodeData</i> instance if it exists, null otherwise
	 */
	protected YosysNetlistNodeData getYosysNetlistNodeData(NetlistNode node){
		YosysNetlistNodeData rtn = null;
		rtn = (YosysNetlistNodeData) node.getNetlistNodeData();
		return rtn;
	}

	/**
	 *  Returns the <i>YosysNetlistEdgeData</i> of the <i>edge</i>
	 *
	 *  @param edge an edge within the <i>netlist</i> of this instance
	 *  @return the <i>YosysNetlistEdgeData</i> instance if it exists, null otherwise
	 */
	protected YosysNetlistEdgeData getYosysNetlistEdgeData(NetlistEdge edge){
		YosysNetlistEdgeData rtn = null;
		rtn = (YosysNetlistEdgeData) edge.getNetlistEdgeData();
		return rtn;
	}

	/**
	 *  Returns the <i>YosysNetlistData</i> of the <i>netlist</i>
	 *
	 *  @param netlist the netlist of this instance
	 *  @return the <i>YosysNetlistData</i> instance if it exists, null otherwise
	 */
	protected YosysNetlistData getYosysNetlistData(Netlist netlist){
		YosysNetlistData rtn = null;
		rtn = (YosysNetlistData) netlist.getNetlistData();
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

	}

	/**
	 *  Set parameter(s) value(s) of the algorithm
	 */
	@Override
	protected void setParameterValues() {
		Boolean present = true;
		present = this.getAlgorithmProfile().getStringParameter("Gates").getFirst();
		if (present) {
			this.setGates(this.getAlgorithmProfile().getStringParameter("Gates").getSecond());
		}
		present = this.getAlgorithmProfile().getBooleanParameter("NetSynth").getFirst();
		if (present) {
			this.setNetSynth(this.getAlgorithmProfile().getBooleanParameter("NetSynth").getSecond());
		}
	}


	/**
	 *  Validate parameter value for <i>Gates</i>
	 */
	protected void validateGatesParameterValues() {
		List<String> validGates = new ArrayList<String>(Arrays.asList(YosysUtils.ValidGates));
		StringTokenizer strtok = new StringTokenizer(this.getGates(), Yosys.S_GATES_DELIM);
		while(strtok.hasMoreTokens()) {
			String token = strtok.nextToken();
			if (!validGates.contains(token)) {
				this.logError(token + " is not a valid value for parameter Gates!");
				Utils.exit(-1);
			}
		}
	}
	
	/**
	 *  Validate parameter value of the algorithm
	 */
	@Override
	protected void validateParameterValues() {
		this.validateGatesParameterValues();
	}

	/**
	 *  Perform preprocessing
	 */
	@Override
	protected void preprocessing() {
		String outputDir = this.getRuntimeEnv().getOptionValue(LSArgString.OUTPUTDIR);
		String inputFilename = this.getNetlist().getInputFilename();
		String filename = Utils.getFilename(inputFilename);
		this.setYosysScriptFilename(outputDir + Utils.getFileSeparator() + filename + ".ys");
		this.setYosysDotFilename(outputDir + Utils.getFileSeparator() + filename + "_yosys.dot");
		this.setYosysEdifFilename(outputDir + Utils.getFileSeparator() + filename + ".edif");
		this.setYosysJSONFilename(outputDir + Utils.getFileSeparator() + filename + ".json");
		// exec
		String exec = "";
		exec += "yosys";
		if (Utils.isWin()) {
			exec += ".exe";
		}
		exec += " -s ";
		this.setYosysExec(exec);
		// create Yosys script
		String script = "";
		// read_verilog
		script += "read_verilog ";
		script += inputFilename;
		script += Utils.getNewLine();
		// flatten
		script += "flatten";
		script += Utils.getNewLine();
		// splitnets
		script += "splitnets -ports";
		script += Utils.getNewLine();
		// hierarchy
		script += "hierarchy -auto-top";
		script += Utils.getNewLine();
		// proc
		script += "proc";
		script += Utils.getNewLine();
		// techmap
		script += "techmap";
		script += Utils.getNewLine();
		// opt
		script += "opt";
		script += Utils.getNewLine();
		// abc
		script += "abc -g ";
		script += this.getGates();
		script += Utils.getNewLine();
		// opt
		script += "opt";
		script += Utils.getNewLine();
		// hierarchy
		script += "hierarchy -auto-top";
		script += Utils.getNewLine();
		// show
		script += "show -format pdf -prefix ";
		script += outputDir;
		script += Utils.getFileSeparator();
		script += filename;
		script += "_yosys";
		script += Utils.getNewLine();
		// write
		script += "write_edif ";
		script += this.getYosysEdifFilename();
		script += Utils.getNewLine();
		// write
		script += "write_json ";
		script += this.getYosysJSONFilename();
		script += Utils.getNewLine();
		// write Yosys script
		try {
			OutputStream outputStream = new FileOutputStream(this.getYosysScriptFilename());
			Writer outputStreamWriter = new OutputStreamWriter(outputStream);
			outputStreamWriter.write(script);
			outputStreamWriter.close();
			outputStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 *  Run the (core) algorithm
	 */
	@Override
	protected void run() {
		ExecCommand proc = Utils.executeAndWaitForCommand(this.getYosysExec() + this.getYosysScriptFilename());
		this.getResults(proc);
		this.logInfo(proc.getOutput());
		this.logInfo(proc.getError());
	}

	/**
	 * Perform postprocessing
	 * 
	 * @throws CelloException
	 */
	@Override
	protected void postprocessing() throws CelloException {
		//YosysEdifUtils.convertEdifToNetlist(this, this.getYosysEdifFilename(), this.getNetlist());
		YosysJSONUtils.getNetlistFromYosysJSONFile(this, this.getYosysJSONFilename(), this.getNetlist());
		// delete
		if (Yosys.B_CLEANUP) {
			Utils.deleteFilename(this.getYosysDotFilename());
			Utils.deleteFilename(this.getYosysEdifFilename());
			Utils.deleteFilename(this.getYosysJSONFilename());
			Utils.deleteFilename(this.getYosysScriptFilename());
		}
		if (this.getNetSynth()) {
			String outputDir = this.getRuntimeEnv().getOptionValue(LSArgString.OUTPUTDIR);
			JSONArray motifs = YosysDataUtils.getMotifLibrary(this.getTargetData());
			Netlist n = null;
			try {
				n = NetSynthUtils.getNetSynthNetlist(this.getNetlist(), motifs, outputDir);
			} catch (JSONException | IOException e) {
				throw new CelloException(e);
			}
			this.getNetlist().clear();
			for (int i = 0; i < n.getNumVertex(); i++) {
				NetlistNode node = n.getVertexAtIdx(i);
				this.getNetlist().addVertex(node);
			}
			for (int i = 0; i < n.getNumEdge(); i++) {
				NetlistEdge node = n.getEdgeAtIdx(i);
				this.getNetlist().addEdge(node);
			}
		} else {
			new OutputOrTransform(this.getNetlist());
		}
	}

	/**
	 * Setter for <i>Gates</i>
	 * @param value the value to set <i>Gates</i>
	*/
	protected void setGates(final String value){
		this.Gates = value;
	}

	/**
	 * Getter for <i>Gates</i>
	 * @return value of <i>Gates</i>
	*/
	protected String getGates(){
		return this.Gates;
	}

	private String Gates;

	/**
	 * Setter for <i>NetSynth</i>
	 * 
	 * @param value the value to set <i>NetSynth</i>
	 */
	protected void setNetSynth(final Boolean value) {
		this.NetSynth = value;
	}

	/**
	 * Getter for <i>NetSynth</i>
	 * 
	 * @return value of <i>NetSynth</i>
	 */
	protected Boolean getNetSynth() {
		return this.NetSynth;
	}

	private Boolean NetSynth;


	/**
	 *  Returns the Logger for the <i>Yosys</i> algorithm
	 *
	 *  @return the logger for the <i>Yosys</i> algorithm
	 */
	@Override
	protected Logger getLogger() {
		return Yosys.logger;
	}
	
	private static final Logger logger = LogManager.getLogger(Yosys.class);
	
	/*
	 * Log
	 */
	/**
	 *  Logs the Result header
	 */
	protected void logResultHeader() {
		this.logInfo(Yosys.S_HEADER_FOOTER);
		this.logInfo(Yosys.S_HEADER_LINE_PREFIX + Utils.getTabCharacterRepeat(Yosys.S_TAB_NUM) + Yosys.S_RESULT);
		this.logInfo(Yosys.S_HEADER_FOOTER);
	}

	/**
	 *  Logs the Results from the execution of the Yosys tool by the process defined in parameter <i>proc</i>
	 *  
	 *  @param proc the process
	 */
	protected void getResults(ExecCommand proc) {
		this.logResultHeader();
		StringTokenizer strtok = new StringTokenizer(proc.getOutput(), Utils.getNewLine());
		while(strtok.hasMoreTokens()) {
			String token = strtok.nextToken();
		    if (token.contains(Yosys.S_ABC_RESULT)) {
			    this.logInfo(token.replaceFirst(Yosys.S_ABC_RESULT, "").trim());
		    }
		}
		this.logInfo(Yosys.S_HEADER_FOOTER);
	}
	
	/*
	 * Getter and Setter
	 */
	/**
	 * Setter for <i>yosysScriptFilename</i>
	 * @param str the value to set <i>yosysScriptFilename</i>
	*/
	protected void setYosysScriptFilename(final String str) {
		this.yosysScriptFilename = str;
	}

	/**
	 * Getter for <i>yosysScriptFilename</i>
	 * @return value of <i>yosysScriptFilename</i>
	*/
	protected String getYosysScriptFilename() {
		return this.yosysScriptFilename;
	}

	/**
	 * Setter for <i>yosysEdifFilename</i>
	 * @param str the value to set <i>yosysEdifFilename</i>
	*/
	protected void setYosysEdifFilename(final String str) {
		this.yosysEdifFilename = str;
	}

	/**
	 * Getter for <i>yosysEdifFilename</i>
	 * @return value of <i>yosysEdifFilename</i>
	*/
	protected String getYosysEdifFilename() {
		return this.yosysEdifFilename;
	}

	/**
	 * Setter for <i>yosysJSONFilename</i>
	 * @param str the value to set <i>yosysJSONFilename</i>
	*/
	protected void setYosysJSONFilename(final String str) {
		this.yosysJSONFilename = str;
	}

	/**
	 * Getter for <i>yosysJSONFilename</i>
	 * @return value of <i>yosysJSONFilename</i>
	*/
	protected String getYosysJSONFilename() {
		return this.yosysJSONFilename;
	}

	/**
	 * Setter for <i>yosysDotFilename</i>
	 * @param str the value to set <i>yosysDotFilename</i>
	*/
	protected void setYosysDotFilename(final String str) {
		this.yosysDotFilename = str;
	}

	/**
	 * Getter for <i>yosysDotFilename</i>
	 * @return value of <i>yosysDotFilename</i>
	*/
	protected String getYosysDotFilename() {
		return this.yosysDotFilename;
	}

	/**
	 * Setter for <i>yosysExec</i>
	 * @param str the value to set <i>yosysExec</i>
	*/
	protected void setYosysExec(final String str) {
		this.yosysExec = str;
	}

	/**
	 * Getter for <i>yosysExec</i>
	 * @return value of <i>yosysExec</i>
	*/
	protected String getYosysExec() {
		return this.yosysExec;
	}

	private String yosysScriptFilename;
	private String yosysEdifFilename;
	private String yosysJSONFilename;
	private String yosysDotFilename;
	private String yosysExec;
	static private String S_HEADER_FOOTER = "+-----------------------------------------------------";
	static private String S_HEADER_LINE_PREFIX = "|";
	static private String S_RESULT = "    RESULTS";
	static private int S_TAB_NUM = 3;
	static private String S_ABC_RESULT = "ABC RESULTS";
	static private String S_GATES_DELIM = ",";
	static private boolean B_CLEANUP = false;
}

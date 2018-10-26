/**
 * Copyright (C) 2018 Boston Univeristy (BU)
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
package org.cellocad.cello2.technologyMapping.algorithm.DSGRN;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cellocad.cello2.common.Utils;
import org.cellocad.cello2.results.logicSynthesis.LSResultsUtils;
import org.cellocad.cello2.results.logicSynthesis.logic.LSLogicEvaluation;
import org.cellocad.cello2.results.logicSynthesis.logic.truthtable.State;
import org.cellocad.cello2.results.logicSynthesis.logic.truthtable.States;
import org.cellocad.cello2.results.logicSynthesis.logic.truthtable.TruthTable;
import org.cellocad.cello2.results.logicSynthesis.netlist.LSResultNetlistUtils;
import org.cellocad.cello2.results.netlist.Netlist;
import org.cellocad.cello2.results.netlist.NetlistEdge;
import org.cellocad.cello2.results.netlist.NetlistNode;
import org.cellocad.cello2.technologyMapping.algorithm.TMAlgorithm;
import org.cellocad.cello2.technologyMapping.algorithm.DSGRN.data.DSGRNNetlistData;
import org.cellocad.cello2.technologyMapping.algorithm.DSGRN.data.DSGRNNetlistEdgeData;
import org.cellocad.cello2.technologyMapping.algorithm.DSGRN.data.DSGRNNetlistNodeData;
import org.cellocad.cello2.technologyMapping.common.TMUtils;
import org.cellocad.cello2.technologyMapping.runtime.environment.TMArgString;

/**
 * The DSGRN class implements the <i>DSGRN</i> algorithm in the <i>technologyMapping</i> stage.
 * 
 * @author Timothy Jones
 * 
 * @date 2018-10-15
 *
 */
public class DSGRN extends TMAlgorithm{

	/**
	 *  Returns the <i>DSGRNNetlistNodeData</i> of the <i>node</i>
	 *
	 *  @param node a node within the <i>netlist</i> of this instance
	 *  @return the <i>DSGRNNetlistNodeData</i> instance if it exists, null otherwise
	 */
	protected DSGRNNetlistNodeData getDSGRNNetlistNodeData(NetlistNode node){
		DSGRNNetlistNodeData rtn = null;
		rtn = (DSGRNNetlistNodeData) node.getNetlistNodeData();
		return rtn;
	}

	/**
	 *  Returns the <i>DSGRNNetlistEdgeData</i> of the <i>edge</i>
	 *
	 *  @param edge an edge within the <i>netlist</i> of this instance
	 *  @return the <i>DSGRNNetlistEdgeData</i> instance if it exists, null otherwise
	 */
	protected DSGRNNetlistEdgeData getDSGRNNetlistEdgeData(NetlistEdge edge){
		DSGRNNetlistEdgeData rtn = null;
		rtn = (DSGRNNetlistEdgeData) edge.getNetlistEdgeData();
		return rtn;
	}

	/**
	 *  Returns the <i>DSGRNNetlistData</i> of the <i>netlist</i>
	 *
	 *  @param netlist the netlist of this instance
	 *  @return the <i>DSGRNNetlistData</i> instance if it exists, null otherwise
	 */
	protected DSGRNNetlistData getDSGRNNetlistData(Netlist netlist){
		DSGRNNetlistData rtn = null;
		rtn = (DSGRNNetlistData) netlist.getNetlistData();
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
	}

	/**
	 *  Validate parameter value of the algorithm
	 */
	@Override
	protected void validateParameterValues() {
		
	}

	/**
	 *  Perform preprocessing
	 */
	protected void setTruthTable() {
		LSResultNetlistUtils.setVertexTypeUsingLSResult(this.getNetlist());
		this.setLSLogicEvaluation(new LSLogicEvaluation(this.getNetlist()));
		this.logInfo(this.getLSLogicEvaluation().toString());
	}
	
	private String getDSGRNScript(String network, String template) {
		String rtn = "";
		BufferedReader bf;
		try {
			bf = new BufferedReader(new FileReader(template));
		} catch (FileNotFoundException e) {
			throw new RuntimeException("Error with file.");
		}
		String line = "";
		String fps = this.getDSGRNFixedPoints();
		while (line != null) {
			try {
				if ((line = bf.readLine()) != null) {
					String str = line;
					str = str.replaceAll("##NONCE##21##NETWORKFILE##21##NONCE",this.getDSGRNNetworkFilename());
					str = str.replaceAll("##NONCE##21##DOTFILENAME##21##NONCE",this.getNetlist().getName()+"_DSGRN.dot");
					str = str.replaceAll("##NONCE##21##OUTPUTDIR##21##NONCE",this.getRuntimeEnv().getOptionValue(TMArgString.OUTPUTDIR));
					str = str.replaceAll("##NONCE##21##FIXEDPOINTS##21##NONCE",fps);
					str = str.replaceAll("##NONCE##21##OUTPUTFILENAME##21##NONCE",this.getNetlist().getName()+"_DSGRN_inequalities.json");
					rtn += str;
					rtn += Utils.getNewLine();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		try {
			bf.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return rtn;
	}

	private String getDSGRNNetwork() {
		String rtn = "";
		for (int i = 0; i < this.getNetlist().getNumVertex(); i++) {
			NetlistNode node = this.getNetlist().getVertexAtIdx(i);
			rtn += node.getName();
			rtn += S_DSGRN_SEPARATOR;
			if (LSResultsUtils.isPrimaryInput(node)) {
				rtn += node.getName();
				rtn += S_DSGRN_SEPARATOR;
				rtn += S_DSGRN_TERMINATOR;
				rtn += Utils.getNewLine();
			}
			else {
				for (int j = 0; j < node.getNumInEdge(); j++) {
					NetlistNode src = node.getInEdgeAtIdx(j).getSrc();
					rtn += S_DSGRN_PARENTHESIS_OPEN;
					if (!LSResultsUtils.isPrimaryInput(src))
						rtn += S_DSGRN_REPRESSION;
					rtn += src.getName();
					rtn += S_DSGRN_PARENTHESIS_CLOSE;
				}
				rtn += S_DSGRN_SEPARATOR;
				rtn += S_DSGRN_TERMINATOR;
				rtn += Utils.getNewLine();
				if (LSResultsUtils.isPrimaryOutput(node)) {
					String dummy = "Dummy_" + node.getName();
					rtn += dummy;
					rtn += S_DSGRN_SEPARATOR;
					rtn += S_DSGRN_PARENTHESIS_OPEN;
					rtn += dummy;
					rtn += S_DSGRN_PARENTHESIS_CLOSE;
					rtn += S_DSGRN_PARENTHESIS_OPEN;
					rtn += node.getName();
					rtn += S_DSGRN_PARENTHESIS_CLOSE;
					rtn += S_DSGRN_SEPARATOR;
					rtn += S_DSGRN_TERMINATOR;
					rtn += Utils.getNewLine();
				}
			}
		}
		return rtn;
	}

	private String getDSGRNFixedPoints() {
		String rtn = "";
		States<NetlistNode> states = this.getLSLogicEvaluation().getStates();
		for (int j = 0; j < states.getNumStates(); j++) {
			State<NetlistNode> state = states.getStateAtIdx(j);
			rtn += S_PY_DICT_OPEN;
			for (int i = 0; i < this.getNetlist().getNumVertex(); i++) {
				NetlistNode node = this.getNetlist().getVertexAtIdx(i);
				TruthTable<NetlistNode,NetlistNode> table = this.getLSLogicEvaluation().getTruthTable(node);
				State<NetlistNode> output = table.getStateOutput(table.getStateAtIdx(j));
				if (LSResultsUtils.isPrimaryInput(node) || LSResultsUtils.isPrimaryOutput(node)) {
					rtn += S_PY_QUOT_OPEN;
					rtn += node.getName();
					rtn += S_PY_QUOT_CLOSE;
					rtn += S_PY_DICT_SEPARATOR;
					rtn += S_PY_LIST_OPEN;
					if (output.getState(node).equals(output.getOne())) {
						rtn += String.valueOf(node.getNumOutEdge() + 1);
						rtn += S_PY_DELIMITER;
						rtn += String.valueOf(node.getNumOutEdge() + 1);
					} else {
						rtn += "0";
						rtn += S_PY_DELIMITER;
						rtn += "0";
					}
					rtn += S_PY_LIST_CLOSE;
					rtn += S_PY_DELIMITER;
				}
			}
			rtn += S_PY_DICT_CLOSE;
			rtn += S_PY_DELIMITER;
		}
		return rtn;
	}

	@Override
	protected void preprocessing() {
		// truth table
		this.setTruthTable();
		// 
		String outputDir = this.getRuntimeEnv().getOptionValue(TMArgString.OUTPUTDIR);
		String inputFilename = this.getNetlist().getInputFilename();
		String filename = Utils.getFilename(inputFilename);
		// network
		this.setDSGRNNetworkFilename(outputDir + Utils.getFileSeparator() + filename + "_DSGRN_Network.txt");
		String network = this.getDSGRNNetwork();
		Utils.writeToFile(network,this.getDSGRNNetworkFilename());
		// script
		this.setDSGRNScriptFilename(outputDir + Utils.getFileSeparator() + filename + "_DSGRN_Script.py");
		String[] pathPrefix = {TMUtils.getResourcesFilepath(),"algorithms","DSGRN"};
		String templateFilename = Utils.getPath(pathPrefix) + Utils.getFileSeparator() + "dsgrn-script.py";
		String script = this.getDSGRNScript(network,templateFilename);
		Utils.writeToFile(script,this.getDSGRNScriptFilename());
		// exec
		String cmd = "";
		cmd += this.getRuntimeEnv().getOptionValue(TMArgString.PYTHONENV);
		cmd += " ";
		cmd += this.getDSGRNScriptFilename();
		Utils.executeAndWaitForCommand(cmd);
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
		
	}


	/**
	 *  Returns the Logger for the <i>DSGRN</i> algorithm
	 *
	 *  @return the logger for the <i>DSGRN</i> algorithm
	 */
	@Override
	protected Logger getLogger() {
		return DSGRN.logger;
	}

	/**
	 * Setter for <i>lsle</i>
	 * @param lsle the value to set <i>lsle</i>
	 */
	protected void setLSLogicEvaluation(final LSLogicEvaluation lsle) {
		this.lsle = lsle;
	}
	
	/**
	 * Getter for <i>lsle</i>
	 * @return value of <i>lsle</i>
	 */
	public LSLogicEvaluation getLSLogicEvaluation() {
		return this.lsle;
	}

	/**
	 * Setter for <i>dsgrnNetworkFilename</i>
	 * @param str the value to set <i>dsgrnNetworkFilename</i>
	 */
	protected void setDSGRNNetworkFilename(final String str) {
		this.dsgrnNetworkFilename = str;
	}

	/**
	 * Getter for <i>dsgrnNetworkFilename</i>
	 * @return value of <i>dsgrnNetworkFilename</i>
	 */
	protected String getDSGRNNetworkFilename() {
		return this.dsgrnNetworkFilename;
	}

	/**
	 * Setter for <i>dsgrnScriptFilename</i>
	 * @param str the value to set <i>dsgrnScriptFilename</i>
	 */
	protected void setDSGRNScriptFilename(final String str) {
		this.dsgrnScriptFilename = str;
	}

	/**
	 * Getter for <i>dsgrnScriptFilename</i>
	 * @return value of <i>dsgrnScriptFilename</i>
	 */
	protected String getDSGRNScriptFilename() {
		return this.dsgrnScriptFilename;
	}
	
	private static final Logger logger = LogManager.getLogger(DSGRN.class.getSimpleName());

	private LSLogicEvaluation lsle;
	private String dsgrnNetworkFilename;
	private String dsgrnScriptFilename;
	static private String S_PY_DICT_OPEN = "{";
	static private String S_PY_DICT_CLOSE = "}";
	static private String S_PY_LIST_OPEN = "[";
	static private String S_PY_LIST_CLOSE = "]";
	static private String S_PY_DELIMITER = ",";
	static private String S_PY_DICT_SEPARATOR = ":";
	static private String S_PY_QUOT_OPEN = "\"";
	static private String S_PY_QUOT_CLOSE = "\"";
	static private String S_DSGRN_SEPARATOR = " : ";
	static private String S_DSGRN_TERMINATOR = "E";
	static private String S_DSGRN_PARENTHESIS_OPEN = "(";
	static private String S_DSGRN_PARENTHESIS_CLOSE = ")";
	static private String S_DSGRN_REPRESSION = "~";
}

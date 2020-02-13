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
package org.cellocad.v2.technologyMapping.algorithm.Cello_JY_TP;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cellocad.MIT.dnacompiler.DNACompiler;
import org.cellocad.v2.common.CObjectCollection;
import org.cellocad.v2.common.Utils;
import org.cellocad.v2.common.file.csv.utils.CSVReader;
import org.cellocad.v2.common.file.csv.utils.CSVRecord;
import org.cellocad.v2.common.target.data.TargetData;
import org.cellocad.v2.constraint.technologyMapping.Cello_JY_TP.Cello_JY_TP_Constraint;
import org.cellocad.v2.data.technologyMapping.Input;
import org.cellocad.v2.data.technologyMapping.Output;
import org.cellocad.v2.data.technologyMapping.QuorumPair;
import org.cellocad.v2.results.logicSynthesis.LSResultsUtils;
import org.cellocad.v2.results.netlist.Netlist;
import org.cellocad.v2.results.netlist.NetlistEdge;
import org.cellocad.v2.results.netlist.NetlistNode;
import org.cellocad.v2.results.partitioning.block.PTBlockNetlist;
import org.cellocad.v2.technologyMapping.algorithm.TMAlgorithm;
import org.cellocad.v2.technologyMapping.algorithm.Cello_JY_TP.data.Cello_JY_TPNetlistData;
import org.cellocad.v2.technologyMapping.algorithm.Cello_JY_TP.data.Cello_JY_TPNetlistEdgeData;
import org.cellocad.v2.technologyMapping.algorithm.Cello_JY_TP.data.Cello_JY_TPNetlistNodeData;
import org.cellocad.v2.technologyMapping.runtime.environment.TMArgString;

/**
 * The Cello_JY_TP class implements the <i>Cello_JY_TP</i> algorithm in the <i>technologyMapping</i> stage.
 * 
 * @author Vincent Mirian
 * 
 * @date 2018-05-21
 *
 */
public class Cello_JY_TP extends TMAlgorithm{

	/**
	 *  Returns the <i>Cello_JY_TPNetlistNodeData</i> of the <i>node</i>
	 *
	 *  @param node a node within the <i>netlist</i> of this instance
	 *  @return the <i>Cello_JY_TPNetlistNodeData</i> instance if it exists, null otherwise
	 */
	protected Cello_JY_TPNetlistNodeData getCello_JY_TPNetlistNodeData(NetlistNode node){
		Cello_JY_TPNetlistNodeData rtn = null;
		rtn = (Cello_JY_TPNetlistNodeData) node.getNetlistNodeData();
		return rtn;
	}

	/**
	 *  Returns the <i>Cello_JY_TPNetlistEdgeData</i> of the <i>edge</i>
	 *
	 *  @param edge an edge within the <i>netlist</i> of this instance
	 *  @return the <i>Cello_JY_TPNetlistEdgeData</i> instance if it exists, null otherwise
	 */
	protected Cello_JY_TPNetlistEdgeData getCello_JY_TPNetlistEdgeData(NetlistEdge edge){
		Cello_JY_TPNetlistEdgeData rtn = null;
		rtn = (Cello_JY_TPNetlistEdgeData) edge.getNetlistEdgeData();
		return rtn;
	}

	/**
	 *  Returns the <i>Cello_JY_TPNetlistData</i> of the <i>netlist</i>
	 *
	 *  @param netlist the netlist of this instance
	 *  @return the <i>Cello_JY_TPNetlistData</i> instance if it exists, null otherwise
	 */
	protected Cello_JY_TPNetlistData getCello_JY_TPNetlistData(Netlist netlist){
		Cello_JY_TPNetlistData rtn = null;
		rtn = (Cello_JY_TPNetlistData) netlist.getNetlistData();
		return rtn;
	}

	/**
	 *  Gets the Constraint data from the NetlistConstraintFile
	 */
	protected void writeToFile(Cello_JY_TP_Constraint constraint, String file) {
		try {
			OutputStream outputStream = new FileOutputStream(file);
			Writer outputStreamWriter = new OutputStreamWriter(outputStream);
			outputStreamWriter.write(constraint.getData());
			outputStreamWriter.close();
			outputStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
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
		quorum = new CObjectCollection<QuorumPair>();
		inducers = new CObjectCollection<Input>();
		outputs = new CObjectCollection<Output>();
		TargetData td = this.getTargetData();
		// quorum
		for (int i = 0; i < td.getNumJSONObject(S_QUORUMPAIR); i++) {
			QuorumPair qp = new QuorumPair(td.getJSONObjectAtIdx(S_QUORUMPAIR, i));
			this.getQuorum().add(qp);
		}
		// inducer
		for (int i = 0; i < td.getNumJSONObject(S_INDUCERS); i++) {
			Input input = new Input(td.getJSONObjectAtIdx(S_INDUCERS, i));
			this.getInducers().add(input);
		}
		// output
		for (int i = 0; i < td.getNumJSONObject(S_OUTPUT); i++) {
			Output output = new Output(td.getJSONObjectAtIdx(S_OUTPUT, i));
			this.getOutputs().add(output);
		}
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
	@Override
	protected void preprocessing() {
		assignedNodes = new CObjectCollection<NetlistNode>();
	}

	/**
	 *  Returns string representation of output defined by parameter <i>output</i>
	 *  
	 *  @param output the output
	 *  @return string representation of input defined by parameter <i>input</i>
	 */
	private String getOutputString(Output output) {
		String rtn = "";
		rtn += output.getName() + Utils.getTabCharacter() + output.getDNA();
		rtn += Utils.getNewLine();
		return rtn;
	}

	/**
	 *  Returns string representation of the output for Netlist defined by parameter <i>netlist</i>
	 *  
	 *  @param netlist the Netlist
	 *  @return string representation of the output for Netlist defined by parameter <i>netlist</i>
	 */
	private String getOutputString(Netlist netlist) {
		String rtn = "";
		// output (temp)
		CObjectCollection<Output> outputs = new CObjectCollection<Output>();
		outputs.addAll(this.getOutputs());
		// outputs
		CObjectCollection<NetlistNode> nodes = LSResultsUtils.getPrimaryOutputNodes(netlist);
		if (nodes.size() > outputs.size()) {
			throw new RuntimeException("Error with Primary Outputs!");
		}
		// outputs
		for (int i = 0; i < nodes.size(); i++) {
			NetlistNode node = this.getAssignedNodes().findCObjectByName(nodes.get(i).getName());
			if (node != null) {
				// inducer is assigned
				String outputName = node.getResultNetlistNodeData().getDevice();
				// get inducer
				Output output = outputs.findCObjectByName(outputName);
				// remove from temp
				outputs.remove(output);
				rtn += getOutputString(output);
			}
		}
		// print unassigned
		for (int i = 0; i < outputs.size(); i++) {
			Output output = outputs.get(i);
			rtn = getOutputString(output) + rtn;
		}
		return rtn;
	}

	/**
	 *  Returns string representation of the quorum for Netlist defined by parameter <i>netlist</i>
	 *  
	 *  @param netlist the Netlist
	 *  @param quorums the quorums
	 *  @return string representation of the quorum for Netlist defined by parameter <i>netlist</i>
	 */
	private String getQuorumAssignString(Netlist netlist, CObjectCollection<QuorumPair> quorums) {
		String rtn = "";
		// quorum input
		CObjectCollection<NetlistNode> nodes = LSResultsUtils.getOutputNodes(netlist);
		if (nodes.size() > quorums.size()) {
			throw new RuntimeException("Error with Outputs!");
		}
		// translate to trueNetlist
		CObjectCollection<NetlistNode> nodesNetlist = new CObjectCollection<NetlistNode>();
		for (int i = 0; i < nodes.size(); i++) {
			// assign output quorum
			NetlistNode node = this.getNetlist().getVertexByName(nodes.get(i).getName());
			if (node == null) {
				throw new RuntimeException("Error with Outputs!");
			}
			nodesNetlist.add(node);
		}
		// quorums
		int index = 0;
		// get all output nodes
		while(!nodesNetlist.isEmpty()) {
			// assign output quorum
			NetlistNode node = this.getNetlist().getVertexByName(nodesNetlist.get(0).getName());
			if (node == null) {
				throw new RuntimeException("Error with Outputs!");
			}
			// get quorum
			QuorumPair quorum = quorums.get(index);
			Output qOutput = quorum.getOutput();
			rtn += getOutputString(qOutput);
			// assign
			node.getResultNetlistNodeData().setDevice(qOutput.getName());
			// get all output nodes from this gate
			nodesNetlist.remove(node);
			index++;			
		}
		return rtn;
	}
	
	/**
	 *  Write Output
	 */
	private void writeOutput(Netlist netlist, CObjectCollection<QuorumPair> quorums) {
		String content = "";
		// inducers
		content += this.getOutputString(netlist);
		// quorum
		content += this.getQuorumAssignString(netlist, quorums);
		Utils.writeToFile(content, this.getOutput());
	}

	/**
	 *  Returns string representation of input defined by parameter <i>input</i>
	 *  
	 *  @param input the input
	 *  @return string representation of input defined by parameter <i>input</i>
	 */
	private String getInputString(Input input) {
		String rtn = "";
		rtn += input.getName() + Utils.getTabCharacter() + input.getOffREU() + Utils.getTabCharacter() + input.getONREU() + Utils.getTabCharacter() + input.getK() + Utils.getTabCharacter();
		rtn += input.getN() + Utils.getTabCharacter() + input.getA() + Utils.getTabCharacter() + input.getB() + Utils.getTabCharacter() + input.getDNA();
		rtn += Utils.getNewLine();
		return rtn;
	}

	/**
	 *  Returns string representation of the inducers for Netlist defined by parameter <i>netlist</i>
	 *  
	 *  @param netlist the Netlist
	 *  @return string representation of the inducers for Netlist defined by parameter <i>netlist</i>
	 */
	private String getInducerString(Netlist netlist) {
		String rtn = "";
		// TODO: if global edit lines below
		// inducers (temp)
		CObjectCollection<Input> inducers = new CObjectCollection<Input>();
		inducers.addAll(this.getInducers());
		// inducers input
		CObjectCollection<NetlistNode> nodes = LSResultsUtils.getPrimaryInputNodes(netlist);
		if (nodes.size() > inducers.size()) {
			throw new RuntimeException("Error with Primary Inputs!");
		}
		// inducers
		for (int i = 0; i < nodes.size(); i++) {
			NetlistNode node = this.getAssignedNodes().findCObjectByName(nodes.get(i).getName());
			if (node != null) {
				// inducer is assigned
				String inducerName = node.getResultNetlistNodeData().getDevice();
				// get inducer
				Input inducer = inducers.findCObjectByName(inducerName);
				// remove from temp
				inducers.remove(inducer);
				rtn += getInputString(inducer);
			}
		}
		// print unassigned
		for (int i = 0; i < inducers.size(); i++) {
			Input inducer = inducers.get(i);
			rtn = getInputString(inducer) + rtn;
		}
		return rtn;
	}
	
	/**
	 *  Returns string representation of the quorum for Netlist defined by parameter <i>netlist</i>
	 *  
	 *  @param netlist the Netlist
	 *  @param quorums the quorums
	 *  @return string representation of the quorum for Netlist defined by parameter <i>netlist</i>
	 */
	private String getQuorumString(Netlist netlist, CObjectCollection<QuorumPair> quorums) {
		String rtn = "";
		// quorum input
		CObjectCollection<NetlistNode> nodes = LSResultsUtils.getInputNodes(netlist);
		if (nodes.size() > quorums.size()) {
			throw new RuntimeException("Error with Inputs!");
		}
		// quorums
		for (int i = 0; i < nodes.size(); i++) {
			NetlistNode node = nodes.get(i);
			// inducer is assigned
			String quorumName = this.getNetlist().getVertexByName(node.getName()).getResultNetlistNodeData().getDevice();
			// get quorum
			QuorumPair quorum = this.getQuorum().findCObjectByName(QuorumPair.getQuorumPairName(quorumName));
			// remove from quorum
			quorums.remove(quorum);
			// print
			rtn += getInputString(quorum.getInput());
		}
		return rtn;
	}
	
	/**
	 *  Write Input
	 *  
	 *  @param netlist the Netlist
	 *  @param quorums the quorums
	 */
	private void writeInput(Netlist netlist, CObjectCollection<QuorumPair> quorums) {
		String content = "";
		// inducers
		content += this.getInducerString(netlist);
		// quorum
		content += this.getQuorumString(netlist, quorums);
		Utils.writeToFile(content, this.getInput());
	}
	

	/**
	 *  Write IO of Netlist defined by parameter <i>netlist</i>
	 *  
	 *  @param netlist the Netlist
	 */
	protected void writeIO(Netlist netlist) {
		CObjectCollection<QuorumPair> quorums = new CObjectCollection<QuorumPair>();
		quorums.addAll(this.getQuorum());
		// write input
		this.writeInput(netlist, quorums);
		// write output
		this.writeOutput(netlist, quorums);		
	}

	/**
	 *  Returns string representing NodeType for NetlistNode defined by parameter <i>node</i>
	 *  
	 *  @param node NetlistNode
	 *  @return string representing NodeType for NetlistNode defined by parameter <i>node</i>
	 */
	private String getNodeType(NetlistNode node) {
		String rtn = "";
		if (LSResultsUtils.isAllInput(node)) {
			rtn += "INPUT";
		}
		else if (LSResultsUtils.isAllOutput(node)) {
			rtn += "OUTPUT";
		}
		else {
			rtn += node.getResultNetlistNodeData().getNodeType();
		}
		return rtn;
	}
	/**
	 *  Write Circuit
	 */
	protected void writeCircuit(Netlist netlist) {
		this.nodeMap = new HashMap<Integer, NetlistNode>();
		Map<Integer, NetlistNode> nodeMap = this.getNodeMap();
		Map<NetlistNode, Integer> nodeInvMap = new HashMap<NetlistNode, Integer>();
		CObjectCollection<NetlistNode> assignedNodes = this.getAssignedNodes();
		final String NONCE_ASSIGN = "NONCE_ASSIGN_";
		String file = "";
		String edge = "";
		int numedge = 0;
		// nodes
		int size = 0;
		size = netlist.getNumVertex();
		file += size + Utils.getNewLine();
		for (int i = 0; i < size; i++) {
			NetlistNode node = netlist.getVertexAtIdx(i);
			nodeMap.put(i, node);
			nodeInvMap.put(node, i);
			// index
			file += i + Cello_JY_TP.S_COMMA;
			//name
			String name = "";
			// primaries
			if (LSResultsUtils.isPrimary(node)) {
				String nodeName = node.getName();
				NetlistNode nodeRef = assignedNodes.findCObjectByName(nodeName);
				if (nodeRef != null) {
					name += NONCE_ASSIGN + nodeRef.getResultNetlistNodeData().getDevice();
				}
			}
			// non-primary
			else if (LSResultsUtils.isInputOutput(node)) {
				String nodeName = node.getName();
				NetlistNode nodeRef = this.getNetlist().getVertexByName(nodeName);
				if (!nodeRef.getResultNetlistNodeData().getDevice().isEmpty()) {
					name += NONCE_ASSIGN + nodeRef.getResultNetlistNodeData().getDevice();
				}
				else {
					throw new RuntimeException("Error!");
				}
			}
			// other
			if (name.isEmpty()) {
				name += node.getName();
			}
			file += name + Cello_JY_TP.S_COMMA;
			// type
			file += this.getNodeType(node) + Cello_JY_TP.S_COMMA;
			file += Utils.getNewLine();
		}
		// edges
		Iterator <Map.Entry <Integer, NetlistNode>> iter = nodeMap.entrySet().iterator();
		while(iter.hasNext()) {
			Map.Entry<Integer, NetlistNode> entry = iter.next();
			Integer first = entry.getKey();
			NetlistNode node = entry.getValue();
			size = node.getNumInEdge();
			numedge += size;
			for (int i = 0; i < size; i++) {
				Integer second = nodeInvMap.get(node.getInEdgeAtIdx(i).getSrc());
				// first
				edge += first + Cello_JY_TP.S_COMMA;
				// second
				edge += second + Cello_JY_TP.S_COMMA;
				edge += Utils.getNewLine();
			}
		}
		file += numedge;
		file += Utils.getNewLine();
		file += edge;
		try {
			OutputStream outputStream = new FileOutputStream(this.getInputFilename());
			Writer outputStreamWriter = new OutputStreamWriter(outputStream);
			outputStreamWriter.write(file);			
			outputStreamWriter.close();
			outputStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 *  Read Circuit
	 */
	protected void readCircuit() {
		//read from CSV file
		CSVReader csvReader = new CSVReader(this.getOutputFilename(), ",");
		CSVRecord csvRecord = null;
		while ((csvRecord = csvReader.getNextRecord()) != null) {
			if (csvRecord.getNumFields() < 2) {
				throw new RuntimeException("Error!\n");
			}
			int index = Integer.parseInt(csvRecord.getFieldAtIdx(0));
			String gate = csvRecord.getFieldAtIdx(1);
			String Promoter = "";
			if (csvRecord.getNumFields() > 2) {
				Promoter += ":" + csvRecord.getFieldAtIdx(2);
			}
			NetlistNode node = this.getNetlist().getVertexByName(this.getNodeMap().get(index).getName());
			// assign inputs from output quorum
			if (LSResultsUtils.isOutput(node)) {
				String matchingName = PTBlockNetlist.getMatchingVIONodeName(node.getName());
				NetlistNode matchingNode = this.getNetlist().getVertexByName(matchingName);
				String gateName = QuorumPair.getMatchingName(node.getResultNetlistNodeData().getDevice());
				matchingNode.getResultNetlistNodeData().setDevice(gateName);
				this.getAssignedNodes().add(matchingNode);
			}
			node.getResultNetlistNodeData().setDevice(gate + Promoter);
			this.getAssignedNodes().add(node);
		}
		// TODO: for debugging
		/*if (false) {
			Utils.deleteFilename(this.getInput());
			Utils.deleteFilename(this.getOutput());
			Utils.deleteFilename(this.getInputFilename());
			Utils.deleteFilename(this.getOutputFilename());
		}*/
	}
	

	/**
	 *  Perform preprocessing
	 */
	protected CObjectCollection<Netlist> sort(){
		CObjectCollection<Netlist> rtn = new CObjectCollection<Netlist>();
		PTBlockNetlist ptBlockNetlist = new PTBlockNetlist(this.getNetlist());
		CObjectCollection<NetlistNode> inputs = new CObjectCollection<NetlistNode>();
		inputs.addAll(LSResultsUtils.getPrimaryInputNodes(this.getNetlist()));
		// ensure progress (loop invariant)
		int previousSize = -1;
		while((rtn.size() != ptBlockNetlist.getNetlistFONum()) && (previousSize != rtn.size())){
			previousSize = rtn.size();
			for (int i = 0; i < ptBlockNetlist.getNetlistFONum(); i++) {
				Netlist netlist = ptBlockNetlist.getNetlistFOAtIdx(i);
				if (rtn.contains(netlist)) {
					continue;
				}
				CObjectCollection<NetlistNode> netlistPInput = LSResultsUtils.getAllInputNodes(netlist);
				// contains All
				boolean containsAll = true;
				for (int j = 0; j < netlistPInput.size(); j++) {
					containsAll = containsAll && (inputs.findCObjectByName(netlistPInput.get(j).getName()) != null);
				}
				if (containsAll) {
					rtn.add(netlist);
					//add to inputs
					CObjectCollection<NetlistNode> nodes = LSResultsUtils.getOutputNodes(netlist);
					for (int j = 0; j < nodes.size(); j++) {
						String matchingName = PTBlockNetlist.getMatchingVIONodeName(nodes.get(j).getName());
						NetlistNode matchNode = this.getNetlist().getVertexByName(matchingName);
						if (matchNode == null) {
							throw new RuntimeException("Match Node error!");
						}
						// add if not present
						if (inputs.findCObjectByName(matchingName) == null) {
							inputs.add(matchNode);
						}
					}
				}
			}
		}
		if (rtn.size() != ptBlockNetlist.getNetlistFONum()) {
			throw new RuntimeException("");			
		}
		return rtn;	
	}

	/**
	 *  Run the (core) algorithm
	 */
	@Override
	protected void run() {
		String outputDir = this.getRuntimeEnv().getOptionValue(TMArgString.OUTPUTDIR) + Utils.getFileSeparator();
		String UCFFile = this.getRuntimeEnv().getOptionValue(TMArgString.USERCONSTRAINTSFILE)
				+ Utils.getFileSeparator();
		String[] path = {"algorithms", "Cello_JY_TP", "files", "default_histogram.txt"};
		String [] optList;
		String file = "";
		DNACompiler cello = new DNACompiler();
		// assume feed forward for limited book keeping and assumptions
		CObjectCollection<Netlist> sortedNetlist = sort();
		// for each circuit get repressor assignment
		// do book keeping of input and output (quorum signals)
		for (int i = 0; i < sortedNetlist.size(); i++) {
			this.setInput(outputDir + S_INPUT+i);
			this.setOutput(outputDir + S_OUTPUT+i);
			// write input
			this.setInputFilename(outputDir + Cello_JY_TP.S_INPUTFILE+i);
			// write output
			this.setOutputFilename(outputDir + Cello_JY_TP.S_OUTPUTFILE+i);
			// netlist
			Netlist netlist = sortedNetlist.get(i);
			// write IO
			this.writeIO(netlist);
			// write circuit
			this.writeCircuit(netlist);
			// run Cello
			File  f = new File(outputDir + "Cello_JY_TP" + Utils.getFileSeparator() + netlist.getName() + Utils.getFileSeparator());
			f.mkdirs();
			String options = "";
			options += " -output_genes ";
			options += this.getOutput();
			options += " -input_promoters ";
			options += this.getInput();
			options += " -UCF " + UCFFile;//EcoJS4ib_120117_model_up_JS_cytometry_all.UCF.json ";
			options += " -plasmid false -tpmodel true -nA 1 -jobID Cello_JY_TP -output_directory " + outputDir + "Cello_JY_TP" + Utils.getFileSeparator() + netlist.getName() + Utils.getFileSeparator();
			//options += " -input_values ";
			//options += this.getInputValues();
			options += " -assignment_algorithm sim_annealing ";
			options += " -vinny_filename " + this.getInputFilename();
			options += " -vinny_outfilename " + this.getOutputFilename();
			options += " -default_histogram ";
			options += Utils.getPathFile(path);
			optList = options.split(" ");
			cello.run(optList);
			// read circuit
			this.readCircuit();
			// do book keeping of input and output (quorum signals)
			// available input/output
			options = options.replace(" -assignment_algorithm sim_annealing ", " -assignment_algorithm reload ");
			options = options.replace(" -plasmid false ", " -plasmid true ");
			options += " -reload " + f.toString() + Utils.getFileSeparator() + "logic_circuit.txt ";
			optList = options.split(" ");
			file += "mvn -f ~/PostDoc/SD2/Software/sandbox/workspace/cello/pom.xml -PCelloMain -Dexec.args=\"";
			file += options;
			file += "\"" + Utils.getNewLine();
			/*cello.run(optList);*/
		}
		Utils.appendToFile(file, outputDir + "script.sh");
	}

	/**
	 *  Perform postprocessing
	 */
	@Override
	protected void postprocessing() {
	}

	/**
	 *  Returns the Logger for the <i>Cello_JY_TP</i> algorithm
	 *
	 *  @return the logger for the <i>Cello_JY_TP</i> algorithm
	 */
	@Override
	protected Logger getLogger() {
		return Cello_JY_TP.logger;
	}
	
	private static final Logger logger = LogManager.getLogger(Cello_JY_TP.class);
	
	/**
	 * Setter for <i>input</i>
	 * @param str the value to set <i>input</i>
	*/
	protected void setInput(final String str) {
		this.input = str;
	}

	/**
	 * Getter for <i>input</i>
	 * @return value of <i>input</i>
	*/
	protected String getInput() {
		return this.input;
	}

	private String input;
	
	/**
	 * Setter for <i>output</i>
	 * @param str the value to set <i>output</i>
	*/
	protected void setOutput(final String str) {
		this.output = str;
	}

	/**
	 * Getter for <i>output</i>
	 * @return value of <i>output</i>
	*/
	protected String getOutput() {
		return this.output;
	}

	private String output;

	/**
	 * Setter for <i>inputValues</i>
	 * @param str the value to set <i>inputValues</i>
	*/
	protected void setInputValues(final String str) {
		this.inputValues = str;
	}

	/**
	 * Getter for <i>inputValues</i>
	 * @return value of <i>inputValues</i>
	*/
	protected String getInputValues() {
		return this.inputValues;
	}

	private String inputValues;
	
	/**
	 * Getter for <i>nodeMap</i>
	 * @return value of <i>nodeMap</i>
	*/
	protected Map<Integer, NetlistNode> getNodeMap() {
		return this.nodeMap;
	}

	private Map<Integer, NetlistNode> nodeMap;
	
	/**
	 * Setter for <i>inputFilename</i>
	 * @param str the value to set <i>inputFilename</i>
	*/
	protected void setInputFilename(final String str) {
		this.inputFilename = str;
	}

	/**
	 * Getter for <i>inputFilename</i>
	 * @return value of <i>inputFilename</i>
	*/
	protected String getInputFilename() {
		return this.inputFilename;
	}

	private String inputFilename;
	
	/**
	 * Setter for <i>outputFilename</i>
	 * @param str the value to set <i>outputFilename</i>
	*/
	protected void setOutputFilename(final String str) {
		this.outputFilename = str;
	}

	/**
	 * Getter for <i>outputFilename</i>
	 * @return value of <i>outputFilename</i>
	*/
	protected String getOutputFilename() {
		return this.outputFilename;
	}

	private String outputFilename;

	/**
	 * Getter for <i>quorum</i>
	 * @return value of <i>quorum</i>
	*/
	protected CObjectCollection<QuorumPair> getQuorum() {
		return this.quorum;
	}
	
	/**
	 * Getter for <i>inducers</i>
	 * @return value of <i>inducers</i>
	*/
	protected CObjectCollection<Input> getInducers() {
		return this.inducers;
	}
	
	/**
	 * Getter for <i>outputs</i>
	 * @return value of <i>outputs</i>
	*/
	protected CObjectCollection<Output> getOutputs() {
		return this.outputs;
	}

	private CObjectCollection<QuorumPair> quorum;
	private CObjectCollection<Input> inducers;
	private CObjectCollection<Output> outputs;
	
	/**
	 * Getter for <i>assignedNodes</i>
	 * @return value of <i>assignedNodes</i>
	*/
	protected CObjectCollection<NetlistNode> getAssignedNodes() {
		return this.assignedNodes;
	}
	
	private CObjectCollection<NetlistNode> assignedNodes;

	static private String S_INPUT = "input";
	static private String S_OUTPUT = "output";
	static private String S_INPUTFILE = "circuit";
	static private String S_OUTPUTFILE = "circuit_output";
	static private String S_INDUCERS = "inducers";
	static private String S_QUORUMPAIR = "quorum_pair";
	static private String S_COMMA = ",";
}

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
package org.cellocad.cello2.partitioning.algorithm.HMetis;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cellocad.cello2.common.Utils;
import org.cellocad.cello2.partitioning.algorithm.PTAlgorithm;
import org.cellocad.cello2.partitioning.algorithm.HMetis.data.HMetisNetlistData;
import org.cellocad.cello2.partitioning.algorithm.HMetis.data.HMetisNetlistEdgeData;
import org.cellocad.cello2.partitioning.algorithm.HMetis.data.HMetisNetlistNodeData;
import org.cellocad.cello2.partitioning.common.Block;
import org.cellocad.cello2.partitioning.common.Move;
import org.cellocad.cello2.partitioning.common.Netlister;
import org.cellocad.cello2.partitioning.common.Partition;
import org.cellocad.cello2.partitioning.common.Partitioner;
import org.cellocad.cello2.partitioning.netlist.PTNetlist;
import org.cellocad.cello2.partitioning.netlist.PTNetlistEdge;
import org.cellocad.cello2.partitioning.netlist.PTNetlistEdgeUtils;
import org.cellocad.cello2.partitioning.netlist.PTNetlistNode;
import org.cellocad.cello2.partitioning.netlist.PTNetlistNodeUtils;
import org.cellocad.cello2.partitioning.netlist.PTNetlistUtils;
import org.cellocad.cello2.partitioning.runtime.environment.PTArgString;
import org.cellocad.cello2.results.logicSynthesis.LSResultsUtils;
import org.cellocad.cello2.results.netlist.Netlist;
import org.cellocad.cello2.results.netlist.NetlistEdge;
import org.cellocad.cello2.results.netlist.NetlistNode;

/**
 * The HMetis class implements the <i>HMetis</i> algorithm in the <i>partitioning</i> stage.
 * 
 * @author Vincent Mirian
 * 
 * @date 2018-05-21
 *
 */
public class HMetis extends PTAlgorithm{

	private void init() {
		integerVertexMap = new HashMap<Integer, PTNetlistNode>();
		vertexIntegerMap = new HashMap<PTNetlistNode, Integer>();
	}

	/**
	 *  Initializes a newly created HMetis
	 */
	public HMetis() {
		this.init();
	}

	/**
	 *  Creates the file path for the HMetis Input File
	 */
	protected void createHMetisInFilePath() {
		String file = "";
		file += this.getRuntimeEnv().getOptionValue(PTArgString.OUTPUTDIR);
		file += Utils.getFileSeparator();
		file += "TEMP_GRAPH_FILE.hgr";
		this.setHMetisInFile(file);
	}

	/**
	 *  Creates the file path for the HMetis Output File
	 */
	protected void createHMetisOutFilePath() {
		String file = "";
		file += "TEMP_GRAPH_FILE.hgr.part.";
		file += this.getPartitioner().getPartition().getNumBlock();
		this.setHMetisOutFile(file);	
	}

	/**
	 *  Adds the content to the HMetis Input File
	 */
	protected void addContentHMetisInFilePath() {
		PTNetlist ptNetlist = this.getNetlister().getPTNetlist();
		String newline = System.lineSeparator();
		Map<PTNetlistNode, Integer> vertexIntegerMap = this.getVertexIntegerMap();
		List<String> fileContent = new ArrayList<String>();
		// vertices
		PTNetlistNode src = null;
		PTNetlistNode dst = null;
		Integer srcInteger = null;
		Integer dstInteger = null;
		int temp = 0;
		for (int i = 0; i < ptNetlist.getNumEdge(); i++){
			PTNetlistEdge edge = ptNetlist.getEdgeAtIdx(i);
			if (PTNetlistEdgeUtils.isEdgeConnectedToPrimary(edge)) {
				continue;
			}
			temp ++;
			src = edge.getSrc();
			dst = edge.getDst();
			srcInteger = vertexIntegerMap.get(src);
			dstInteger = vertexIntegerMap.get(dst);
			fileContent.add(srcInteger + " " + dstInteger + newline);
		}
		try {
			OutputStream outputStream = new FileOutputStream(this.getHMetisInFile());
			Writer outputStreamWriter = new OutputStreamWriter(outputStream);
			// Format of Hypergraph Input File
			// header lines are: [number of edges] [number of vertices] 
			//		subsequent lines give each edge, one edge per line
			outputStreamWriter.write(temp + " " + vertexIntegerMap.size() + newline);
			for (int i = 0; i < fileContent.size(); i++) {
				outputStreamWriter.write(fileContent.get(i));
			}			
			outputStreamWriter.close();
			outputStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 *  Creates the the HMetis Execution command
	 */
	protected void createHMetisExec() {
		String cmd = "";
		cmd += "hmetis ";
		cmd += this.getHMetisInFile();
		cmd += " ";
		cmd += this.getPartitioner().getPartition().getNumBlock();
		// UBfactor=1, Nruns=10, CType=1, RType=1, Vcycle=3, Reconst=0, dbglvl=0
		cmd += " 1 10 1 1 3 0 0";
		this.setHMetisExec(cmd);
	}

	/**
	 *  Creates the file path for the Partition Dot File
	 */
	protected void createPartitionDotFilePath() {
		String file = "";
		file += this.getRuntimeEnv().getOptionValue(PTArgString.OUTPUTDIR);
		file += Utils.getFileSeparator();
		file += Utils.getFilename(this.getNetlist().getInputFilename());
		file += "_hmetis.dot";
		this.setPartitionDotFile(file);
	}

	/**
	 *  Translates the result from HMetis application to the Netlist
	 */
	protected void applyResult() {
		List<Move> moves = new ArrayList<Move>();
		Move move = null;
		Partition partition = this.getPartitioner().getPartition();
	    File resultFile = new File(this.getHMetisOutFile());
	    Map<Integer, PTNetlistNode> integerVertexMap = this.getIntegerVertexMap();
	    Reader resultReader = null;
		// Create File Reader
		try {
			resultReader = new FileReader(resultFile);
		} catch (FileNotFoundException e) {
			throw new RuntimeException("Error with file: " + resultFile);
		}
		// Read and assign move
		BufferedReader resultBufferedReader = new BufferedReader(resultReader);
		try {
			String line = null;
			int i = 1;
			while ((line = resultBufferedReader.readLine()) != null) {
				PTNetlistNode ptnode = integerVertexMap.get(i);
				int blockIdx = Utils.getInteger(line.trim());
				Block block = partition.getBlockAtIdx(blockIdx);
				move = new Move(ptnode, ptnode.getMyBlock(), block);
				moves.add(move);
				i++;
			}
		} catch (IOException e) {
			throw new RuntimeException("Error with file: " + resultFile);
		}
		// close
	    try {
	    	resultReader.close();
		} catch (IOException e) {
			throw new RuntimeException("Error with file: " + resultFile);
		}
	    // do moves
	    partition.doMoves(moves);
	}

	/**
	 *  Returns the <i>HMetisNetlistNodeData</i> of the <i>node</i>
	 *
	 *  @param node a node within the <i>netlist</i> of this instance
	 *  @return the <i>HMetisNetlistNodeData</i> instance if it exists, null otherwise
	 */
	protected HMetisNetlistNodeData getHMetisNetlistNodeData(NetlistNode node){
		HMetisNetlistNodeData rtn = null;
		rtn = (HMetisNetlistNodeData) node.getNetlistNodeData();
		return rtn;
	}

	/**
	 *  Returns the <i>HMetisNetlistEdgeData</i> of the <i>edge</i>
	 *
	 *  @param edge an edge within the <i>netlist</i> of this instance
	 *  @return the <i>HMetisNetlistEdgeData</i> instance if it exists, null otherwise
	 */
	protected HMetisNetlistEdgeData getHMetisNetlistEdgeData(NetlistEdge edge){
		HMetisNetlistEdgeData rtn = null;
		rtn = (HMetisNetlistEdgeData) edge.getNetlistEdgeData();
		return rtn;
	}

	/**
	 *  Returns the <i>HMetisNetlistData</i> of the <i>netlist</i>
	 *
	 *  @param netlist the netlist of this instance
	 *  @return the <i>HMetisNetlistData</i> instance if it exists, null otherwise
	 */
	protected HMetisNetlistData getHMetisNetlistData(Netlist netlist){
		HMetisNetlistData rtn = null;
		rtn = (HMetisNetlistData) netlist.getNetlistData();
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
	@Override
	protected void preprocessing() {
		Netlist netlist = this.getNetlist();
		this.setNetlister(new Netlister(netlist));
		//this.setPartitioner(new Partitioner(this.getTargetData()));
		int nblocks = netlist.getNumVertex() - LSResultsUtils.getPrimaryInputOutputNodes(netlist).size();
		nblocks = ((Double)Math.ceil((double)nblocks/9)).intValue();
		this.setPartitioner(new Partitioner(nblocks));
		// Map Nodes to integers
		Map<Integer, PTNetlistNode> integerVertexMap = this.getIntegerVertexMap();
		Map<PTNetlistNode, Integer> vertexIntegerMap = this.getVertexIntegerMap();
		PTNetlist ptnetlist = this.getNetlister().getPTNetlist();
		int temp = 1;
		for (int i = 0; i < ptnetlist.getNumVertex(); i++) {
			PTNetlistNode node = ptnetlist.getVertexAtIdx(i);
			if (PTNetlistNodeUtils.isPrimary(node)) {
				continue;
			}
			vertexIntegerMap.put(node, new Integer(temp));
			integerVertexMap.put(new Integer(temp), node);
			temp++;
		}
		// create path to HMetisInFile
		this.createHMetisInFilePath();
		// create path to HMetisOutFile
		this.createHMetisOutFilePath();
		// create contents of HMetisInFile
		this.addContentHMetisInFilePath();
		// create HMetisExec
		this.createHMetisExec();
		// create path to PartitionDotFile
		this.createPartitionDotFilePath();		
	}

	/**
	 *  Run the (core) algorithm
	 */
	@Override
	protected void run() {
		Utils.executeAndWaitForCommand(this.getHMetisExec());
	}

	/**
	 *  Perform postprocessing
	 */
	@Override
	protected void postprocessing() {
		this.applyResult();
		this.getNetlister().getNetlist();
		Utils.deleteFilename(this.getHMetisInFile());
		Utils.deleteFilename(this.getHMetisOutFile());
		PTNetlistUtils.writeDotFileForPartition(this.getNetlister().getPTNetlist(), this.getPartitionDotFile());
	}


	/**
	 *  Returns the Logger for the <i>HMetis</i> algorithm
	 *
	 *  @return the logger for the <i>HMetis</i> algorithm
	 */
	@Override
	protected Logger getLogger() {
		return HMetis.logger;
	}
	
	private static final Logger logger = LogManager.getLogger(HMetis.class);

	/*
	 * hMetisInFile
	 */
	/**
	 * Setter for <i>hMetisInFile</i>
	 * @param str the value to set <i>hMetisInFile</i>
	*/
	protected void setHMetisInFile(final String str) {
		this.hMetisInFile = str;
	}

	/**
	 * Getter for <i>hMetisInFile</i>
	 * @return value of <i>hMetisInFile</i>
	*/
	protected String getHMetisInFile() {
		return this.hMetisInFile;
	}

	private String hMetisInFile;
	
	/*
	 * hMetisOutFile
	 */
	/**
	 * Setter for <i>hMetisOutFile</i>
	 * @param str the value to set <i>hMetisOutFile</i>
	*/
	protected void setHMetisOutFile(final String str) {
		this.hMetisOutFile = str;
	}

	/**
	 * Getter for <i>hMetisOutFile</i>
	 * @return value of <i>hMetisOutFile</i>
	*/
	protected String getHMetisOutFile() {
		return this.hMetisOutFile;
	}

	private String hMetisOutFile;
	
	/*
	 * PartitionDot
	 */
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
	
	/*
	 * hMetisExec
	 */
	/**
	 * Setter for <i>hMetisExec</i>
	 * @param str the value to set <i>hMetisExec</i>
	*/
	protected void setHMetisExec(final String str) {
		this.hMetisExec = str;
	}

	/**
	 * Getter for <i>hMetisExec</i>
	 * @return value of <i>hMetisExec</i>
	*/
	protected String getHMetisExec() {
		return this.hMetisExec;
	}

	private String hMetisExec;
	
	/*
	 * Netlister
	 */
	/**
	 * Setter for <i>netlister</i>
	 * @param netlister the value to set <i>netlister</i>
	*/
	protected void setNetlister(final Netlister netlister) {
		this.netlister = netlister;
	}

	/**
	 * Getter for <i>netlister</i>
	 * @return value of <i>netlister</i>
	*/
	protected Netlister getNetlister() {
		return this.netlister;
	}
	
	private Netlister netlister;
	
	/*
	 * Partitioner
	 */
	/**
	 * Setter for <i>partitioner</i>
	 * @param partitioner the value to set <i>partitioner</i>
	*/
	protected void setPartitioner(final Partitioner partitioner) {
		this.partitioner = partitioner;
	}

	/**
	 * Getter for <i>partitioner</i>
	 * @return value of <i>partitioner</i>
	*/
	protected Partitioner getPartitioner() {
		return this.partitioner;
	}
	
	private Partitioner partitioner;
	
	/*
	 * integerVertexMap
	 */
	private Map<Integer, PTNetlistNode> integerVertexMap;
	
	private Map<Integer, PTNetlistNode> getIntegerVertexMap(){
		return this.integerVertexMap;
	}
	
	/*
	 * vertexIntegerMap
	 */
	private Map<PTNetlistNode, Integer> vertexIntegerMap;
	
	private Map<PTNetlistNode, Integer> getVertexIntegerMap(){
		return this.vertexIntegerMap;
	}
}

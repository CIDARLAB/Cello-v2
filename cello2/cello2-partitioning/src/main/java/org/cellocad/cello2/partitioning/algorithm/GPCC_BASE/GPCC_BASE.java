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
package org.cellocad.cello2.partitioning.algorithm.GPCC_BASE;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cellocad.cello2.common.CObject;
import org.cellocad.cello2.common.CObjectCollection;
import org.cellocad.cello2.common.constraint.Weight;
import org.cellocad.cello2.partitioning.algorithm.PTAlgorithm;
import org.cellocad.cello2.partitioning.algorithm.GPCC_BASE.data.GPCC_BASENetlistData;
import org.cellocad.cello2.partitioning.algorithm.GPCC_BASE.data.GPCC_BASENetlistEdgeData;
import org.cellocad.cello2.partitioning.algorithm.GPCC_BASE.data.GPCC_BASENetlistNodeData;
import org.cellocad.cello2.partitioning.common.Block;
import org.cellocad.cello2.partitioning.common.Netlister;
import org.cellocad.cello2.partitioning.common.Partition;
import org.cellocad.cello2.partitioning.common.Partitioner;
import org.cellocad.cello2.partitioning.netlist.PTNetlist;
import org.cellocad.cello2.partitioning.netlist.PTNetlistNode;
import org.cellocad.cello2.results.logicSynthesis.LSResultsUtils;
import org.cellocad.cello2.results.netlist.Netlist;
import org.cellocad.cello2.results.netlist.NetlistEdge;
import org.cellocad.cello2.results.netlist.NetlistNode;

/**
 * The GPCC_BASE class implements the <i>GPCC_BASE</i> algorithm in the <i>partitioning</i> stage.
 * 
 * @author Vincent Mirian
 * 
 * @date 2018-05-21
 *
 */
public class GPCC_BASE extends PTAlgorithm{
		
	/**
	 *  Initializes class members
	 */
	private void init() {
		this.cellList = new ArrayList<NetlistNode>();
		this.ptCellList = new ArrayList<PTNetlistNode>();
		this.blockList = new ArrayList<Block>();
		this.blockCapacityCellList = new ArrayList<List<Set<Integer>>>();
		this.cellBlockList = new ArrayList<Set<Integer>>();
	}

	/**
	 *  Initializes a newly created GPCC_BASE
	 */
	public GPCC_BASE() {
		this.init();
	}
	
	/**
	 *  Returns the <i>GPCC_BASENetlistNodeData</i> of the <i>node</i>
	 *
	 *  @param node a node within the <i>netlist</i> of this instance
	 *  @return the <i>GPCC_BASENetlistNodeData</i> instance if it exists, null otherwise
	 */
	protected GPCC_BASENetlistNodeData getGPCC_BASENetlistNodeData(NetlistNode node){
		GPCC_BASENetlistNodeData rtn = null;
		rtn = (GPCC_BASENetlistNodeData) node.getNetlistNodeData();
		return rtn;
	}

	/**
	 *  Returns the <i>GPCC_BASENetlistEdgeData</i> of the <i>edge</i>
	 *
	 *  @param edge an edge within the <i>netlist</i> of this instance
	 *  @return the <i>GPCC_BASENetlistEdgeData</i> instance if it exists, null otherwise
	 */
	protected GPCC_BASENetlistEdgeData getGPCC_BASENetlistEdgeData(NetlistEdge edge){
		GPCC_BASENetlistEdgeData rtn = null;
		rtn = (GPCC_BASENetlistEdgeData) edge.getNetlistEdgeData();
		return rtn;
	}

	/**
	 *  Returns the <i>GPCC_BASENetlistData</i> of the <i>netlist</i>
	 *
	 *  @param netlist the netlist of this instance
	 *  @return the <i>GPCC_BASENetlistData</i> instance if it exists, null otherwise
	 */
	protected GPCC_BASENetlistData getGPCC_BASENetlistData(Netlist netlist){
		GPCC_BASENetlistData rtn = null;
		rtn = (GPCC_BASENetlistData) netlist.getNetlistData();
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
	 *  set Weights for PTNetlistNode
	 */
	private void setPTNetlistNodeWeights() {
		PTNetlist ptnetlist = this.getNetlister().getPTNetlist();
		CObjectCollection<CObject> units = this.getPartitioner().getPartition().getBlockCapacityUnits();
		Weight nodeWeight = null;
		for (int i = 0; i < ptnetlist.getNumVertex(); i++) {
			PTNetlistNode node = ptnetlist.getVertexAtIdx(i);
			String nodeType = node.getNodeType();
			if (units.findCObjectByName(nodeType) != null) {
				nodeWeight = node.getMyWeight();
				nodeWeight.incWeight(nodeType, 1);
				nodeWeight.resetUnits(units);
			}
		}
	}
	/**
	 *  Relate Data Structure
	 */
	private void relateDataStructure() {
		List<PTNetlistNode> ptCellList = this.getPTCellList();
		List<Block> blockList = this.getBlockList();
		List<List<Set<Integer>>> blockCapacityCellList = this.getBlockCapacityCellList();
		List<Set<Integer>> cellBlockList = this.getCellBlockList();
		Block block = null;
		PTNetlistNode node = null;
		for (int i = 0; i < ptCellList.size(); i++) {
			node = ptCellList.get(i);
			for (int j = 0; j < blockList.size(); j++) {
				block = blockList.get(j);
				for (int k = 0; k < block.getNumCapacity(); k++) {
					if (block.getCapacityAtIdx(k).canFit(node.getMyWeight())) {
						cellBlockList.get(i).add(j);
						blockCapacityCellList.get(j).get(k).add(i);						
					}
				}
			}
			if (cellBlockList.get(i).size() == 0) {
				throw new RuntimeException("GPCC_BASE: Can't assign " + node.getName() + " to block.");
			}
		}
	}
	
	/**
	 *  Initialize Data Structure
	 */
	private void initDataStructure() {
		List<NetlistNode> cellList = this.getCellList();
		List<PTNetlistNode> ptCellList = this.getPTCellList();
		List<Block> blockList = this.getBlockList();
		List<List<Set<Integer>>> blockCapacityCellList = this.getBlockCapacityCellList();
		List<Set<Integer>> cellBlockList = this.getCellBlockList();
		cellList.clear();
		ptCellList.clear();
		blockList.clear();
		blockCapacityCellList.clear();
		cellBlockList.clear();
		Netlist netlist = this.getNetlist();
		PTNetlist ptnetlist = this.getNetlister().getPTNetlist();
		for (int i = 0; i < netlist.getNumVertex(); i++) {
			NetlistNode node = netlist.getVertexAtIdx(i);
			if (LSResultsUtils.isPrimary(node)) {
				continue;
			}
			PTNetlistNode ptnode = ptnetlist.getVertexByName(node.getName());
			if (ptnode == null) {
				throw new RuntimeException("Can't find PTNode " + node.getName());
			}
			cellList.add(node);
			ptCellList.add(ptnode);
			cellBlockList.add(new HashSet<Integer>());
		}
		Partition partition = this.getPartitioner().getPartition();
		for (int i = 0; i < partition.getNumBlock(); i++) {
			Block blk = partition.getBlockAtIdx(i);
			blockList.add(blk);
			blockCapacityCellList.add(new ArrayList<Set<Integer>>());
			for (int j = 0; j < blk.getNumCapacity(); j++) {
				blockCapacityCellList.get(i).add(new HashSet<Integer>());
			}
		}
		// relationship
		this.relateDataStructure();
	}

	/**
	 *  Initializes preprocessing
	 */
	protected void initPreprocessing() {
		Netlist netlist = this.getNetlist();
		this.setNetlister(new Netlister(netlist));
		this.setPartitioner(new Partitioner(this.getTargetData()));
		// weights for PTNetlistNode
		this.setPTNetlistNodeWeights();
		// prepare data structure
		this.initDataStructure();
		// info
		this.logInfo("Number of Blocks in Partitioner: " + this.getPartitioner().getPartition().getNumBlock());
	}

	/**
	 *  Perform preprocessing
	 */
	@Override
	protected void preprocessing() {
		this.initPreprocessing();
	}

	/**
	 *  Run the (core) algorithm
	 */
	@Override
	protected void run() {
		throw new RuntimeException("Cannot execute GPCC_BASE!");
	}

	/**
	 *  Perform postprocessing
	 */
	@Override
	protected void postprocessing() {
		
	}
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
	public Partitioner getPartitioner() {
		return this.partitioner;
	}
	
	private Partitioner partitioner;

	/**
	 * Getter for <i>ptCellList</i>
	 * @return value of <i>ptCellList</i>
	*/
	protected List<PTNetlistNode> getPTCellList(){
		return this.ptCellList;
	}

	/**
	 * Getter for <i>cellList</i>
	 * @return value of <i>cellList</i>
	*/
	public List<NetlistNode> getCellList(){
		return this.cellList;
	}

	/**
	 * Getter for <i>blockList</i>
	 * @return value of <i>blockList</i>
	*/
	public List<Block> getBlockList(){
		return this.blockList;
	}

	/**
	 * Getter for <i>blockCapacityCellList</i>
	 * @return value of <i>blockCapacityCellList</i>
	*/
	public List<List<Set<Integer>>> getBlockCapacityCellList(){
		return this.blockCapacityCellList;
	}

	/**
	 * Getter for <i>cellBlockList</i>
	 * @return value of <i>cellBlockList</i>
	*/
	public List<Set<Integer>> getCellBlockList(){
		return this.cellBlockList;
	}

	// index of PTNetlistNode
	private List<PTNetlistNode> ptCellList;
	// index of NetlistNode
	private List<NetlistNode> cellList;
	// index of Block
	private List<Block> blockList;
	// index by block is from blockList
	private List<List<Set<Integer>>> blockCapacityCellList;
	// index by Cell id from cellList
	// Set contains the index of block from blockList
	private List<Set<Integer>> cellBlockList;

	/**
	 *  Returns the Logger for the <i>GPCC_BASE</i> algorithm
	 *
	 *  @return the logger for the <i>GPCC_BASE</i> algorithm
	 */
	@Override
	protected Logger getLogger() {
		return GPCC_BASE.logger;
	}
	
	private static final Logger logger = LogManager.getLogger(GPCC_BASE.class.getSimpleName());
}

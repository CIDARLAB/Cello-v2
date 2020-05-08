/*
 * Copyright (C) 2017 Massachusetts Institute of Technology (MIT)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package org.cellocad.v2.partitioning.algorithm.GPCC_BASE;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cellocad.v2.common.CObject;
import org.cellocad.v2.common.CObjectCollection;
import org.cellocad.v2.common.CelloException;
import org.cellocad.v2.common.constraint.Weight;
import org.cellocad.v2.partitioning.algorithm.PTAlgorithm;
import org.cellocad.v2.partitioning.algorithm.GPCC_BASE.data.GpccBaseNetlistData;
import org.cellocad.v2.partitioning.algorithm.GPCC_BASE.data.GpccBaseNetlistEdgeData;
import org.cellocad.v2.partitioning.algorithm.GPCC_BASE.data.GpccBaseNetlistNodeData;
import org.cellocad.v2.partitioning.common.Block;
import org.cellocad.v2.partitioning.common.Netlister;
import org.cellocad.v2.partitioning.common.Partition;
import org.cellocad.v2.partitioning.common.Partitioner;
import org.cellocad.v2.partitioning.netlist.PTNetlist;
import org.cellocad.v2.partitioning.netlist.PTNetlistNode;
import org.cellocad.v2.results.logicSynthesis.LSResultsUtils;
import org.cellocad.v2.results.netlist.Netlist;
import org.cellocad.v2.results.netlist.NetlistEdge;
import org.cellocad.v2.results.netlist.NetlistNode;

/**
 * The implementation of the <i>GPCC_BASE</i> algorithm in the <i>partitioning</i> stage.
 *
 * @author Vincent Mirian
 * @date 2018-05-21
 */
public class GpccBase extends PTAlgorithm {

  /** Initializes class members. */
  private void init() {
    cellList = new ArrayList<>();
    ptCellList = new ArrayList<>();
    blockList = new ArrayList<>();
    blockCapacityCellList = new ArrayList<>();
    cellBlockList = new ArrayList<>();
  }

  /** Initializes a newly created {@link GpccBase}. */
  public GpccBase() {
    init();
  }

  /**
   * Returns the {@link GpccBaseNetlistNodeData} of the given node.
   *
   * @param node A node within the netlist of this instance.
   * @return The {@link GpccBaseNetlistNodeData} instance if it exists, null otherwise.
   */
  protected GpccBaseNetlistNodeData getGpccBaseNetlistNodeData(final NetlistNode node) {
    GpccBaseNetlistNodeData rtn = null;
    rtn = (GpccBaseNetlistNodeData) node.getNetlistNodeData();
    return rtn;
  }

  /**
   * Returns the {@link GpccBaseNetlistEdgeData} of the given edge.
   *
   * @param edge An edge within the netlist of this instance.
   * @return The {@link GpccBaseNetlistEdgeData} instance if it exists, null otherwise.
   */
  protected GpccBaseNetlistEdgeData getGpccBaseNetlistEdgeData(final NetlistEdge edge) {
    GpccBaseNetlistEdgeData rtn = null;
    rtn = (GpccBaseNetlistEdgeData) edge.getNetlistEdgeData();
    return rtn;
  }

  /**
   * Returns the {@link GpccBaseNetlistData} of the given netlist.
   *
   * @param netlist The netlist of this instance.
   * @return The {@link GpccBaseNetlistData} instance if it exists, null otherwise.
   */
  protected GpccBaseNetlistData getGpccBaseNetlistData(final Netlist netlist) {
    GpccBaseNetlistData rtn = null;
    rtn = (GpccBaseNetlistData) netlist.getNetlistData();
    return rtn;
  }

  /** Gets the constraint data from the netlist constraint file. */
  @Override
  protected void getConstraintFromNetlistConstraintFile() {}

  /** Gets the data from the UCF. */
  @Override
  protected void getDataFromUcf() {}

  /** Set parameter values of the algorithm. */
  @Override
  protected void setParameterValues() {}

  /** Validate parameter values of the algorithm. */
  @Override
  protected void validateParameterValues() {}

  /** set Weights for PTNetlistNode. */
  private void setPTNetlistNodeWeights() {
    final PTNetlist ptnetlist = getNetlister().getPTNetlist();
    final CObjectCollection<CObject> units =
        getPartitioner().getPartition().getBlockCapacityUnits();
    Weight nodeWeight = null;
    for (int i = 0; i < ptnetlist.getNumVertex(); i++) {
      final PTNetlistNode node = ptnetlist.getVertexAtIdx(i);
      final String nodeType = node.getNodeType();
      if (units.findCObjectByName(nodeType) != null) {
        nodeWeight = node.getMyWeight();
        nodeWeight.incWeight(nodeType, 1);
        nodeWeight.resetUnits(units);
      }
    }
  }

  /** Relate Data Structure. */
  private void relateDataStructure() {
    final List<PTNetlistNode> ptCellList = getPTCellList();
    final List<Block> blockList = getBlockList();
    final List<List<Set<Integer>>> blockCapacityCellList = getBlockCapacityCellList();
    final List<Set<Integer>> cellBlockList = getCellBlockList();
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

  /** Initialize Data Structure. */
  private void initDataStructure() {
    final List<NetlistNode> cellList = getCellList();
    final List<PTNetlistNode> ptCellList = getPTCellList();
    final List<Block> blockList = getBlockList();
    final List<List<Set<Integer>>> blockCapacityCellList = getBlockCapacityCellList();
    final List<Set<Integer>> cellBlockList = getCellBlockList();
    cellList.clear();
    ptCellList.clear();
    blockList.clear();
    blockCapacityCellList.clear();
    cellBlockList.clear();
    final Netlist netlist = getNetlist();
    final PTNetlist ptnetlist = getNetlister().getPTNetlist();
    for (int i = 0; i < netlist.getNumVertex(); i++) {
      final NetlistNode node = netlist.getVertexAtIdx(i);
      if (LSResultsUtils.isPrimary(node)) {
        continue;
      }
      final PTNetlistNode ptnode = ptnetlist.getVertexByName(node.getName());
      if (ptnode == null) {
        throw new RuntimeException("Can't find PTNode " + node.getName());
      }
      cellList.add(node);
      ptCellList.add(ptnode);
      cellBlockList.add(new HashSet<Integer>());
    }
    final Partition partition = getPartitioner().getPartition();
    for (int i = 0; i < partition.getNumBlock(); i++) {
      final Block blk = partition.getBlockAtIdx(i);
      blockList.add(blk);
      blockCapacityCellList.add(new ArrayList<Set<Integer>>());
      for (int j = 0; j < blk.getNumCapacity(); j++) {
        blockCapacityCellList.get(i).add(new HashSet<Integer>());
      }
    }
    // relationship
    relateDataStructure();
  }

  /** Initializes preprocessing. */
  protected void initPreprocessing() {
    final Netlist netlist = getNetlist();
    setNetlister(new Netlister(netlist));
    setPartitioner(new Partitioner(getTargetData()));
    // weights for PTNetlistNode
    setPTNetlistNodeWeights();
    // prepare data structure
    initDataStructure();
    // info
    logInfo("Number of Blocks in Partitioner: " + getPartitioner().getPartition().getNumBlock());
  }

  /**
   * Perform preprocessing.
   *
   * @throws CelloException Unable to perform preprocessing.
   */
  @Override
  protected void preprocessing() throws CelloException {
    initPreprocessing();
  }

  /**
   * Run the (core) algorithm.
   *
   * @throws CelloException Unable to run the (core) algorithm.
   */
  @Override
  protected void run() throws CelloException {
    throw new RuntimeException("Cannot execute GPCC_BASE!");
  }

  /** Perform postprocessing. */
  @Override
  protected void postprocessing() {}

  /*
   * Netlister
   */
  /**
   * Setter for {@code netlister}.
   *
   * @param netlister The value to set {@code netlister}.
   */
  protected void setNetlister(final Netlister netlister) {
    this.netlister = netlister;
  }

  /**
   * Getter for {@code netlister}.
   *
   * @return The value of {@code netlister}.
   */
  protected Netlister getNetlister() {
    return netlister;
  }

  private Netlister netlister;

  /*
   * Partitioner
   */
  /**
   * Setter for {@code partitioner}.
   *
   * @param partitioner The value to set {@code partitioner}.
   */
  protected void setPartitioner(final Partitioner partitioner) {
    this.partitioner = partitioner;
  }

  /**
   * Getter for {@code partitioner}.
   *
   * @return The value of {@code partitioner}.
   */
  public Partitioner getPartitioner() {
    return partitioner;
  }

  private Partitioner partitioner;

  /**
   * Getter for {@code ptCellList}.
   *
   * @return The value of {@code ptCellList}.
   */
  protected List<PTNetlistNode> getPTCellList() {
    return ptCellList;
  }

  /**
   * Getter for {@code cellList}.
   *
   * @return The value of {@code cellList}.
   */
  public List<NetlistNode> getCellList() {
    return cellList;
  }

  /**
   * Getter for {@code blockList}.
   *
   * @return The value of {@code blockList}.
   */
  public List<Block> getBlockList() {
    return blockList;
  }

  /**
   * Getter for {@code blockCapacityCellList}.
   *
   * @return The value of {@code blockCapacityCellList}.
   */
  public List<List<Set<Integer>>> getBlockCapacityCellList() {
    return blockCapacityCellList;
  }

  /**
   * Getter for {@code cellBlockList}.
   *
   * @return The value of {@code cellBlockList}.
   */
  public List<Set<Integer>> getCellBlockList() {
    return cellBlockList;
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
   * Returns the {@link Logger} for the <i>GPCC_BASE</i> algorithm.
   *
   * @return The {@link Logger} for the <i>GPCC_BASE</i> algorithm.
   */
  @Override
  protected Logger getLogger() {
    return GpccBase.logger;
  }

  private static final Logger logger = LogManager.getLogger(GpccBase.class);
}

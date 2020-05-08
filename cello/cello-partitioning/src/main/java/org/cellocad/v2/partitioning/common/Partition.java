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

package org.cellocad.v2.partitioning.common;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.cellocad.v2.common.CObject;
import org.cellocad.v2.common.CObjectCollection;
import org.cellocad.v2.common.Utils;
import org.cellocad.v2.common.profile.DerivedProfile;
import org.cellocad.v2.common.profile.ProfileObject;
import org.cellocad.v2.partitioning.netlist.PTNetlistEdge;
import org.cellocad.v2.partitioning.netlist.PTNetlistNode;
import org.cellocad.v2.partitioning.profile.BlockProfile;
import org.cellocad.v2.partitioning.profile.Capacity;
import org.cellocad.v2.partitioning.profile.CapacityProfile;
import org.cellocad.v2.partitioning.profile.InterBlockProfile;
import org.cellocad.v2.partitioning.profile.PartitionProfile;
import org.cellocad.v2.results.logicSynthesis.LSResultsUtils;

/**
 * A partition.
 *
 * @author Vincent Mirian
 * @date Oct 26, 2017
 */
public class Partition extends DerivedProfile<PartitionProfile> {

  private void init() {
    blocks = new CObjectCollection<>();
    blockCapacityUnits = new CObjectCollection<>();
    blockCapacity = new CObjectCollection<>();
    interblocks = new ArrayList<>();
    interblockCapacityUnits = new CObjectCollection<>();
    interblockCapacity = new CObjectCollection<>();
  }

  private void initBlockCapacityUnits() {
    final PartitionProfile PProfile = getProfile();
    ProfileObject unit = null;
    CObject capacityObj = null;
    for (int i = 0; i < PProfile.getNumBlockCapacityUnits(); i++) {
      unit = PProfile.getBlockCapacityUnitsAtIdx(i);
      capacityObj = new CObject(unit);
      blockCapacityUnits.add(capacityObj);
    }
  }

  private void initBlockCapacity() {
    final PartitionProfile PProfile = getProfile();
    CapacityProfile cp = null;
    Capacity capacity = null;
    for (int i = 0; i < PProfile.getNumBlockCapacity(); i++) {
      cp = PProfile.getBlockCapacityAtIdx(i);
      capacity = new Capacity(cp, blockCapacityUnits);
      blockCapacity.add(capacity);
    }
  }

  private void initBlocks() {
    final PartitionProfile PProfile = getProfile();
    // initBlocks
    Block block = null;
    BlockProfile bp = null;
    for (int i = 0; i < PProfile.getNumBlockProfile(); i++) {
      bp = PProfile.getBlockProfileAtIdx(i);
      block = new Block(bp, blockCapacity, blockCapacityUnits);
      addBlock(block);
    }
  }

  private void initBlockInformation() {
    initBlockCapacityUnits();
    initBlockCapacity();
    initBlocks();
  }

  private void initInterBlockCapacityUnits() {
    final PartitionProfile PProfile = getProfile();
    ProfileObject unit = null;
    CObject capacityObj = null;
    for (int i = 0; i < PProfile.getNumInterBlockCapacityUnits(); i++) {
      unit = PProfile.getInterBlockCapacityUnitsAtIdx(i);
      capacityObj = new CObject(unit);
      interblockCapacityUnits.add(capacityObj);
    }
  }

  private void initInterBlockCapacity() {
    final PartitionProfile PProfile = getProfile();
    CapacityProfile cp = null;
    Capacity capacity = null;
    for (int i = 0; i < PProfile.getNumInterBlockCapacity(); i++) {
      cp = PProfile.getInterBlockCapacityAtIdx(i);
      capacity = new Capacity(cp, interblockCapacityUnits);
      interblockCapacity.add(capacity);
    }
  }

  private void initInterBlocks() {
    final PartitionProfile PProfile = getProfile();
    // initBlocks
    InterBlock interblock = null;
    InterBlockProfile ibp = null;
    for (int i = 0; i < PProfile.getNumInterBlockProfile(); i++) {
      ibp = PProfile.getInterBlockProfileAtIdx(i);
      interblock = new InterBlock(ibp, blocks, interblockCapacity, blockCapacityUnits);
      addInterBlock(interblock);
    }
  }

  private void initInterBlocksInformation() {
    initInterBlockCapacityUnits();
    initInterBlockCapacity();
    initInterBlocks();
  }

  /**
   * Initializes a newly created {@link Partition}.
   *
   * @param pProfile A {@link PartitionProfile}.
   */
  public Partition(final PartitionProfile pProfile) {
    super(pProfile);
    init();
    initBlockInformation();
    initInterBlocksInformation();
  }

  /*
   * Capacity Units
   */
  public CObjectCollection<CObject> getBlockCapacityUnits() {
    return blockCapacityUnits;
  }

  /*
   * Blocks
   */
  private void addBlock(final Block block) {
    if (block != null) {
      block.setIdx(getNumBlock());
      blocks.add(block);
    }
  }

  /*
   * private void removeBlock(final Block block){ if (block != null){ blocks.remove(block); } }
   */

  /**
   * Gets the block at the specified index.
   *
   * @param index An index.
   * @return The block at the specified index.
   */
  public Block getBlockAtIdx(final int index) {
    Block rtn = null;
    if (index >= 0 && index < getNumBlock()) {
      rtn = blocks.get(index);
    }
    return rtn;
  }

  public int getNumBlock() {
    final int rtn = blocks.size();
    return rtn;
  }

  private boolean blockExists(final Block block) {
    final boolean rtn = block != null && blocks.contains(block);
    return rtn;
  }

  /*
   * InterBlocks
   */
  private void addInterBlock(final InterBlock interblock) {
    if (interblock != null) {
      interblock.setIdx(getNumInterBlock());
      interblocks.add(interblock);
    }
  }

  /**
   * Gets the {@link InterBlock} at the given index.
   *
   * @param index An index.
   * @return The {@link InterBlock} at the given index.
   */
  public InterBlock getInterBlockAtIdx(final int index) {
    InterBlock rtn = null;
    if (index >= 0 && index < getNumInterBlock()) {
      rtn = interblocks.get(index);
    }
    return rtn;
  }

  public int getNumInterBlock() {
    final int rtn = interblocks.size();
    return rtn;
  }

  /*
   * Move
   */
  /**
   * Perform a set of moves.
   *
   * @param moves A list of moves.
   * @return Whether the moves were performed.
   */
  public boolean doMoves(final List<Move> moves) {
    boolean rtn = true;
    Move move;
    final Iterator<Move> movesIt = moves.iterator();
    while (rtn && movesIt.hasNext()) {
      move = movesIt.next();
      rtn = rtn && doMove(move);
    }
    return rtn;
  }

  private boolean doMove(final Move move) {
    boolean rtn = false;
    final boolean moveIsValid = move.isValid();
    final PTNetlistNode node = move.getPNode();
    final Block srcBlock = move.getSrcBlock();
    final Block dstBlock = move.getDstBlock();
    final boolean srcExist = srcBlock == null || blockExists(srcBlock);
    final boolean dstExist = dstBlock == null || blockExists(dstBlock);
    rtn = srcExist && dstExist && moveIsValid;
    if (rtn) {
      if (srcBlock != null) {
        srcBlock.removePNode(node);
        node.setMyBlock(null);
        assert node.getMyBlock() == null;
      }
      if (dstBlock != null) {
        dstBlock.addPNode(node);
        node.setMyBlock(dstBlock);
        assert node.getMyBlock() == dstBlock;
      }
    }
    return rtn;
  }

  /*
   * dot file
   */
  protected String getDotHeader() {
    String rtn = "";
    rtn += "digraph ";
    rtn += getName();
    rtn += " {";
    rtn += System.lineSeparator();
    rtn += "rankdir=\"LR\"";
    rtn += System.lineSeparator();
    return rtn;
  }

  protected String getDotFooter() {
    String rtn = "";
    rtn += "}";
    rtn += System.lineSeparator();
    return rtn;
  }

  protected String getDotSubgraphHeader(final Block block) {
    String rtn = "";
    // cluster header
    rtn += "subgraph cluster";
    rtn += block.getIdx();
    rtn += " {";
    rtn += System.lineSeparator();
    return rtn;
  }

  protected String getDotSubgraphFooter() {
    return getDotFooter();
  }

  protected String getRank(final String value) {
    return "{rank = same; " + value + "};";
  }

  /**
   * Write the DOT file to the writer.
   *
   * @param os A writer.
   * @throws IOException Unable to write.
   */
  public void printDot(final Writer os) throws IOException {
    os.write(getDotHeader());
    String input = "";
    String output = "";
    final Set<PTNetlistNode> nodes = new HashSet<>();
    final Set<PTNetlistEdge> edges = new HashSet<>();
    // for each block
    for (int i = 0; i < getNumBlock(); i++) {
      final Block block = getBlockAtIdx(i);
      // nodes
      for (int j = 0; j < block.getNumPNode(); j++) {
        final PTNetlistNode node = block.getPNodeAtIdx(j);
        nodes.add(node);
        // edges
        for (int k = 0; k < node.getNumInEdge(); k++) {
          final PTNetlistEdge edge = node.getInEdgeAtIdx(k);
          edges.add(edge);
          nodes.add(edge.getSrc());
        }
        // edges
        for (int k = 0; k < node.getNumOutEdge(); k++) {
          final PTNetlistEdge edge = node.getOutEdgeAtIdx(k);
          edges.add(edge);
          nodes.add(edge.getDst());
        }
      }
    }
    // for nodes
    final Iterator<PTNetlistNode> niter = nodes.iterator();
    while (niter.hasNext()) {
      final PTNetlistNode node = niter.next();
      node.printDot(os);
      if (LSResultsUtils.isAllInput(node.getNetlistNode())) {
        if (!input.isEmpty()) {
          input += ",";
        }
        input += "\"" + node.getName() + "\"";
      }
      if (LSResultsUtils.isAllOutput(node.getNetlistNode())) {
        if (!output.isEmpty()) {
          output += ",";
        }
        output += "\"" + node.getName() + "\"";
      }
    }
    // for edges
    final Iterator<PTNetlistEdge> eiter = edges.iterator();
    while (eiter.hasNext()) {
      final PTNetlistEdge edge = eiter.next();
      edge.printDot(os);
    }
    os.write(getRank(input) + Utils.getNewLine());
    os.write(getRank(output) + Utils.getNewLine());
    os.write(getDotFooter());
  }

  /*
   * HashCode
   */
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + (blocks == null ? 0 : blocks.hashCode());
    result = prime * result + (blockCapacityUnits == null ? 0 : blockCapacityUnits.hashCode());
    result = prime * result + (blockCapacity == null ? 0 : blockCapacity.hashCode());
    return result;
  }

  /*
   * Equals
   */
  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (!super.equals(obj)) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final Partition other = (Partition) obj;
    if (blocks == null) {
      if (other.blocks != null) {
        return false;
      }
    } else if (!blocks.equals(other.blocks)) {
      return false;
    }
    if (blockCapacityUnits == null) {
      if (other.blockCapacityUnits != null) {
        return false;
      }
    } else if (!blockCapacityUnits.equals(other.blockCapacityUnits)) {
      return false;
    }
    if (blockCapacity == null) {
      if (other.blockCapacity != null) {
        return false;
      }
    } else if (!blockCapacity.equals(other.blockCapacity)) {
      return false;
    }
    return true;
  }

  /*
   * toString
   */
  protected String getBlockToString() {
    String rtn = "";
    rtn = rtn + blocks.toString();
    rtn = Utils.addIndent(1, rtn);
    return rtn;
  }

  @Override
  public String toString() {
    String rtn = "";
    rtn = rtn + "[ ";
    rtn = rtn + Utils.getNewLine();
    // name
    rtn = rtn + this.getEntryToString("name", getName());
    // profile
    rtn = rtn + this.getEntryToString("profile", getProfile().getName());
    // blocks
    rtn = rtn + Utils.getTabCharacter();
    rtn = rtn + "blocks = ";
    rtn = rtn + Utils.getNewLine();
    rtn = rtn + Utils.getTabCharacter();
    rtn = rtn + "{";
    rtn = rtn + Utils.getNewLine();
    rtn = rtn + getBlockToString();
    rtn = rtn + Utils.getTabCharacter();
    rtn = rtn + "}";
    rtn = rtn + Utils.getNewLine();
    // toString
    rtn = rtn + Utils.getTabCharacter();
    rtn = rtn + "toString() = ";
    rtn = rtn + Utils.getNewLine();
    String superStr = "";
    superStr = super.toString();
    superStr = Utils.addIndent(1, superStr);
    rtn = rtn + superStr;
    rtn = rtn + ",";
    rtn = rtn + Utils.getNewLine();
    // end
    rtn = rtn + "]";
    return rtn;
  }

  private CObjectCollection<Block> blocks;
  private CObjectCollection<CObject> blockCapacityUnits;
  private CObjectCollection<Capacity> blockCapacity;
  private List<InterBlock> interblocks;
  private CObjectCollection<CObject> interblockCapacityUnits;
  private CObjectCollection<Capacity> interblockCapacity;
}

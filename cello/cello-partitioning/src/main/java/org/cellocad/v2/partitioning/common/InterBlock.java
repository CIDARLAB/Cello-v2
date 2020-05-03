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

import org.cellocad.v2.common.CObject;
import org.cellocad.v2.common.CObjectCollection;
import org.cellocad.v2.common.Utils;
import org.cellocad.v2.common.constraint.Weight;
import org.cellocad.v2.partitioning.netlist.PTNetlistEdge;
import org.cellocad.v2.partitioning.profile.Capacity;
import org.cellocad.v2.partitioning.profile.CapacityCollection;
import org.cellocad.v2.partitioning.profile.InterBlockProfile;

/**
 * An inter block.
 * 
 * @author Vincent Mirian
 *
 * @date Oct 26, 2017
 */
public class InterBlock extends CapacityCollection<InterBlockProfile> {

  private void init() {
    edges = new CObjectCollection<>();
  }

  /**
   * Initializes a newly created {@link InterBlock} object.
   * 
   * @param ibp           An {@link InterBlockProfile} object.
   * @param blocks        A collection of {@link Block} objects.
   * @param capacity      A collection of {@link Capacity} objects.
   * @param capacityUnits A collection of {@link CObject} objects.
   */
  public InterBlock(final InterBlockProfile ibp, final CObjectCollection<Block> blocks,
      final CObjectCollection<Capacity> capacity, final CObjectCollection<CObject> capacityUnits) {
    super(ibp, capacity);
    init();
    Utils.isNullRuntimeException(capacityUnits, "CapacityUnits");
    myWeight = new Weight(capacityUnits, capacityUnits);
    setSrcDst(ibp, blocks);
  }

  /*
   * myWeight
   */
  private Weight getMyWeight() {
    return myWeight;
  }

  /*
   * Evaluate
   */
  /**
   * Whether this instance can fit.
   * 
   * @return Whether this instance can fit.
   */
  public boolean canFit() {
    boolean rtn = false;
    rtn = this.canFit(getMyWeight());
    return rtn;
  }

  /**
   * Whether this instance can fit using the given weight.
   * 
   * @return Whether this instance can fit using the given weight.
   */
  @Override
  public boolean canFit(final Weight wObj) {
    boolean rtn = false;
    if (wObj != null) {
      final Weight wObjTemp = new Weight(wObj);
      wObjTemp.inc(getMyWeight());
      rtn = super.canFit(wObjTemp);
    }
    return rtn;
  }

  /**
   * Whether this instance is overflow.
   * 
   * @return Whether this instance is overflow.
   */
  public boolean isOverflow() {
    boolean rtn = false;
    rtn = this.isOverflow(getMyWeight());
    return rtn;
  }

  /**
   * Whether this instance is overflow using the given weight.
   * 
   * @param wObj A {@link Weight} object.
   * @return Whether this instance is overflow using the given weight.
   */
  @Override
  public boolean isOverflow(final Weight wObj) {
    boolean rtn = false;
    if (wObj != null) {
      final Weight wObjTemp = new Weight(wObj);
      wObjTemp.inc(getMyWeight());
      rtn = super.isOverflow(wObjTemp);
    }
    return rtn;
  }

  /**
   * Whether this instance is underflow.
   * 
   * @return Whether this instance is underflow.
   */
  public boolean isUnderflow() {
    boolean rtn = false;
    rtn = this.isUnderflow(getMyWeight());
    return rtn;
  }

  /**
   * Whether this instance is underflow using the given weight.
   * 
   * @param wObj A {@link Weight} object.
   * @return Whether this instance is underflow using the given weight.
   */
  @Override
  public boolean isUnderflow(final Weight wObj) {
    boolean rtn = false;
    if (wObj != null) {
      final Weight wObjTemp = new Weight(wObj);
      wObjTemp.inc(getMyWeight());
      rtn = super.isUnderflow(wObjTemp);
    }
    return rtn;
  }

  /*
   * PEdge
   */
  /**
   * Adds the given {@link PTNetlistEdge} to this instance.
   * 
   * @param edge A {@link PTNetlistEdge} object.
   */
  public void addPEdge(final PTNetlistEdge edge) {
    if (edge != null) {
      edges.add(edge);
      myWeight.inc(edge.getMyWeight());
    }
  }

  /**
   * Removes the given {@link PTNetlistEdge} from this instance.
   * 
   * @param edge A {@link PTNetlistEdge} object.
   */
  public void removePEdge(final PTNetlistEdge edge) {
    if (edge != null && contains(edge)) {
      edges.remove(edge);
      myWeight.dec(edge.getMyWeight());
    }
  }

  /**
   * Get the {@link PTNetlistEdge} at the given index.
   * 
   * @param index An index.
   * @return The {@link PTNetlistEdge} at the given index.
   */
  public PTNetlistEdge getPEdgeAtIdx(final int index) {
    PTNetlistEdge rtn = null;
    if (index >= 0 && index < getNumPEdge()) {
      rtn = edges.get(index);
    }
    return rtn;
  }

  public int getNumPEdge() {
    final int rtn = edges.size();
    return rtn;
  }

  private boolean contains(final PTNetlistEdge edge) {
    final boolean rtn = edge != null && edges.contains(edge);
    return rtn;
  }

  /*
   * Src/Dst
   */
  private void setSrcDst(final InterBlockProfile ibp, final CObjectCollection<Block> blocks) {
    Block block = null;
    String blockName = null;
    blockName = ibp.getSource().getName();
    block = blocks.findCObjectByName(blockName);
    if (block == null) {
      throw new RuntimeException("Source block not found: " + blockName);
    }
    setSrcBlock(block);
    blockName = ibp.getDestination().getName();
    block = blocks.findCObjectByName(blockName);
    if (block == null) {
      throw new RuntimeException("Destination block not found: " + blockName);
    }
    setDstBlock(block);
  }

  /*
   * Src Block
   */
  public Block getSrcBlock() {
    return srcBlock;
  }

  private void setSrcBlock(final Block block) {
    srcBlock = block;
  }

  private Block srcBlock;

  /*
   * Dst Block
   */
  public Block getDstBlock() {
    return dstBlock;
  }

  private void setDstBlock(final Block block) {
    dstBlock = block;
  }

  private Block dstBlock;

  /*
   * HashCode
   */
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + (edges == null ? 0 : edges.hashCode());
    result = prime * result + (myWeight == null ? 0 : myWeight.hashCode());
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
    final InterBlock other = (InterBlock) obj;
    if (edges == null) {
      if (other.edges != null) {
        return false;
      }
    } else if (!edges.equals(other.edges)) {
      return false;
    }
    if (myWeight == null) {
      if (other.myWeight != null) {
        return false;
      }
    } else if (!myWeight.equals(other.myWeight)) {
      return false;
    }
    return true;
  }

  private CObjectCollection<PTNetlistEdge> edges;
  private final Weight myWeight;

}

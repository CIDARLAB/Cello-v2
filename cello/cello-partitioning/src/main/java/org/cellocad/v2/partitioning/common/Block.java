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
import org.cellocad.v2.common.CObject;
import org.cellocad.v2.common.CObjectCollection;
import org.cellocad.v2.common.Utils;
import org.cellocad.v2.common.constraint.Weight;
import org.cellocad.v2.partitioning.netlist.PTNetlist;
import org.cellocad.v2.partitioning.netlist.PTNetlistEdge;
import org.cellocad.v2.partitioning.netlist.PTNetlistNode;
import org.cellocad.v2.partitioning.profile.BlockProfile;
import org.cellocad.v2.partitioning.profile.Capacity;
import org.cellocad.v2.partitioning.profile.CapacityCollection;
import org.cellocad.v2.partitioning.profile.CapacityProfile;

/**
 * A partition block.
 *
 * @author Vincent Mirian
 * @date Oct 26, 2017
 */
public class Block extends CapacityCollection<BlockProfile> {

  private void init() {
    nodes = new CObjectCollection<>();
    outputConnectionsCapacity = new CObjectCollection<>();
    inputConnectionsCapacity = new CObjectCollection<>();
    inoutConnectionsCapacity = new CObjectCollection<>();
  }

  private void initOutputConnectionsCapacity(
      final BlockProfile bp, final CObjectCollection<Capacity> ccp) {
    Utils.isNullRuntimeException(ccp, "CapacityCollectionProfile");
    Utils.isNullRuntimeException(bp, "BlockProfile");
    init();
    CapacityProfile cp = null;
    Capacity capacity = null;
    for (int i = 0; i < bp.getNumOutputConnectionsCapacity(); i++) {
      cp = bp.getOutputConnectionsCapacityAtIdx(i);
      capacity = ccp.findCObjectByName(cp.getName());
      Utils.isNullRuntimeException(capacity, "Capacity");
      addOutputConnectionsCapacity(capacity);
    }
  }

  private void initInputConnectionsCapacity(
      final BlockProfile bp, final CObjectCollection<Capacity> ccp) {
    Utils.isNullRuntimeException(ccp, "CapacityCollectionProfile");
    Utils.isNullRuntimeException(bp, "BlockProfile");
    init();
    CapacityProfile cp = null;
    Capacity capacity = null;
    for (int i = 0; i < bp.getNumInputConnectionsCapacity(); i++) {
      cp = bp.getInputConnectionsCapacityAtIdx(i);
      capacity = ccp.findCObjectByName(cp.getName());
      Utils.isNullRuntimeException(capacity, "Capacity");
      addInputConnectionsCapacity(capacity);
    }
  }

  private void initInOutConnectionsCapacity(
      final BlockProfile bp, final CObjectCollection<Capacity> ccp) {
    Utils.isNullRuntimeException(ccp, "CapacityCollectionProfile");
    Utils.isNullRuntimeException(bp, "BlockProfile");
    init();
    CapacityProfile cp = null;
    Capacity capacity = null;
    for (int i = 0; i < bp.getNumInOutConnectionsCapacity(); i++) {
      cp = bp.getInOutConnectionsCapacityAtIdx(i);
      capacity = ccp.findCObjectByName(cp.getName());
      Utils.isNullRuntimeException(capacity, "Capacity");
      addInOutConnectionsCapacity(capacity);
    }
  }

  /**
   * Initializes a newly created {@link Block} object.
   *
   * @param bp A {@link BlockProfile}.
   * @param capacity A collection of {@link Capacity} objects.
   * @param capacityUnits A collection of {@link CObject} objects corresponding to capacity units.
   */
  public Block(
      final BlockProfile bp,
      final CObjectCollection<Capacity> capacity,
      final CObjectCollection<CObject> capacityUnits) {
    super(bp, capacity);
    init();
    Utils.isNullRuntimeException(capacityUnits, "CapacityUnits");
    myWeight = new Weight(capacityUnits, capacityUnits);
    initOutputConnectionsCapacity(bp, capacity);
    initInputConnectionsCapacity(bp, capacity);
    initInOutConnectionsCapacity(bp, capacity);
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
   * Whether the block can fit.
   *
   * @return A boolean representing wether the block can fit.
   */
  public boolean canFit() {
    boolean rtn = false;
    rtn = this.canFit(getMyWeight());
    return rtn;
  }

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
   * Whether the block is overflow.
   *
   * @return A boolean representing wether the block is overflow.
   */
  public boolean isOverflow() {
    boolean rtn = false;
    rtn = this.isOverflow(getMyWeight());
    return rtn;
  }

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
   * Wheter the block is underflow.
   *
   * @return A boolean representing wether the block is underflow.
   */
  public boolean isUnderflow() {
    boolean rtn = false;
    rtn = this.isUnderflow(getMyWeight());
    return rtn;
  }

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
   * PNode
   */
  /**
   * Add the given {@link PTNetlistNode} object to this instance.
   *
   * @param node A {@link PTNetlistNode} object.
   */
  public void addPNode(final PTNetlistNode node) {
    if (node != null) {
      nodes.add(node);
      myWeight.inc(node.getMyWeight());
    }
  }

  /**
   * Remove the given {@link PTNetlistNode} object to this instance.
   *
   * @param node A {@link PTNetlistNode} object.
   */
  public void removePNode(final PTNetlistNode node) {
    if (node != null && contains(node)) {
      nodes.remove(node);
      myWeight.dec(node.getMyWeight());
    }
  }

  /**
   * Get the {@link PTNetlistNode} object at the specified index if it exists, otherwise null.
   *
   * @param index The index.
   * @return The {@link PTNetlistNode} object at the specified index if it exists, otherwise null.
   */
  public PTNetlistNode getPNodeAtIdx(final int index) {
    PTNetlistNode rtn = null;
    if (index >= 0 && index < getNumPNode()) {
      rtn = nodes.get(index);
    }
    return rtn;
  }

  public int getNumPNode() {
    final int rtn = nodes.size();
    return rtn;
  }

  private boolean contains(final PTNetlistNode node) {
    final boolean rtn = node != null && nodes.contains(node);
    return rtn;
  }

  /*
   * dot file
   */
  /**
   * Converts this instance into a {@link PTNetlist} object.
   *
   * @return The {@link PTNetlist} object corresponding to this instance.
   */
  public PTNetlist convertToPTNetlist() {
    final PTNetlist rtn = new PTNetlist();
    for (int i = 0; i < getNumPNode(); i++) {
      final PTNetlistNode node = getPNodeAtIdx(i);
      rtn.addVertex(node);
      // outedges
      for (int j = 0; j < node.getNumOutEdge(); j++) {
        final PTNetlistEdge edge = node.getOutEdgeAtIdx(j);
        final PTNetlistNode other = edge.getDst();
        if (contains(other)) {
          rtn.addEdge(edge);
        }
      }
      // inedges
      for (int j = 0; j < node.getNumInEdge(); j++) {
        final PTNetlistEdge edge = node.getInEdgeAtIdx(j);
        final PTNetlistNode other = edge.getSrc();
        if (contains(other)) {
          rtn.addEdge(edge);
        }
      }
    }
    return rtn;
  }

  public void printDot(final Writer os) throws IOException {
    final PTNetlist ptNetlist = convertToPTNetlist();
    ptNetlist.printDot(os);
  }

  /*
   * HashCode
   */
  /*
   * @Override public int hashCode() { final int prime = 31; int result = super.hashCode(); result =
   * prime * result + ((nodes == null) ? 0 : nodes.hashCode()); result = prime * result + ((myWeight
   * == null) ? 0 : myWeight.hashCode()); return result; }
   */

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
    final Block other = (Block) obj;
    if (nodes == null) {
      if (other.nodes != null) {
        return false;
      }
    } else if (!nodes.equals(other.nodes)) {
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

  /*
   * toString
   */
  protected String getNodesToString() {
    String rtn = "";
    for (int i = 0; i < getNumPNode(); i++) {
      rtn = rtn + Utils.getTabCharacterRepeat(2);
      final PTNetlistNode node = getPNodeAtIdx(i);
      rtn = rtn + node.getName();
      rtn = rtn + ",";
      rtn = rtn + Utils.getNewLine();
    }
    return rtn;
  }

  @Override
  public String toString() {
    String rtn = "";
    rtn = rtn + "[ ";
    rtn = rtn + Utils.getNewLine();
    // name
    rtn = rtn + this.getEntryToString("name", getName());
    // nodes
    rtn = rtn + Utils.getTabCharacter();
    rtn = rtn + "nodes = ";
    rtn = rtn + Utils.getNewLine();
    rtn = rtn + Utils.getTabCharacter();
    rtn = rtn + "{";
    rtn = rtn + Utils.getNewLine();
    rtn = rtn + getNodesToString();
    rtn = rtn + Utils.getTabCharacter();
    rtn = rtn + "}";
    rtn = rtn + Utils.getNewLine();
    // Weight
    rtn = rtn + Utils.getTabCharacter();
    rtn = rtn + "myWeight = ";
    rtn = rtn + Utils.getNewLine();
    String indentStr = "";
    indentStr = getMyWeight().toString();
    indentStr = Utils.addIndent(1, indentStr);
    rtn = rtn + Utils.getTabCharacter();
    rtn = rtn + Utils.getNewLine();
    // toString
    rtn = rtn + Utils.getTabCharacter();
    rtn = rtn + "toString() = ";
    rtn = rtn + Utils.getNewLine();
    indentStr = super.toString();
    indentStr = Utils.addIndent(1, indentStr);
    rtn = rtn + indentStr;
    rtn = rtn + ",";
    rtn = rtn + Utils.getNewLine();
    // end
    rtn = rtn + "]";
    return rtn;
  }

  /*
   * Output
   */
  /**
   * Adds the Capacity defined by parameter {@code c} to <i>outputConnectionsCapacity</i>.
   *
   * @param c A {@link Capacity} object.
   */
  public void addOutputConnectionsCapacity(final Capacity c) {
    if (c != null) {
      getOutputConnectionsCapacity().add(c);
    }
  }

  /**
   * Returns the Capacity at the specified position in <i>outputConnectionsCapacity</i>.
   *
   * @param index The index of the {@link Capacity} object to return.
   * @return If the index is within the bounds (0 <= bounds <
   *     this.getNumOutputConnectionsCapacity()), return the Capacity at the specified position in
   *     <i>outputConnectionsCapacity</i>, otherwise null.
   */
  public Capacity getOutputConnectionsCapacityAtIdx(final int index) {
    Capacity rtn = null;
    if (0 <= index && index < getNumOutputConnectionsCapacity()) {
      rtn = getOutputConnectionsCapacity().get(index);
    }
    return rtn;
  }

  /**
   * Returns the number of Capacity in <i>outputConnectionsCapacity</i>.
   *
   * @return The number of Capacity in <i>outputConnectionsCapacity</i>.
   */
  public int getNumOutputConnectionsCapacity() {
    return getOutputConnectionsCapacity().size();
  }

  /**
   * Getter for {@code outputConnectionsCapacity}.
   *
   * @return The value of {@code outputConnectionsCapacity}.
   */
  public CObjectCollection<Capacity> getOutputConnectionsCapacity() {
    return outputConnectionsCapacity;
  }

  /*
   * Input
   */
  /**
   * Adds the Capacity defined by parameter {@code c} to <i>inputConnectionsCapacity</i>.
   *
   * @param c A {@link Capacity} object.
   */
  public void addInputConnectionsCapacity(final Capacity c) {
    if (c != null) {
      getInputConnectionsCapacity().add(c);
    }
  }

  /**
   * Returns the Capacity at the specified position in <i>inputConnectionsCapacity</i>.
   *
   * @param index The index of the {@link Capacity} object to return.
   * @return If the index is within the bounds (0 <= bounds <
   *     this.getNumInputConnectionsCapacity()), return the Capacity at the specified position in
   *     <i>inputConnectionsCapacity</i>, otherwise null.
   */
  public Capacity getInputConnectionsCapacityAtIdx(final int index) {
    Capacity rtn = null;
    if (0 <= index && index < getNumInputConnectionsCapacity()) {
      rtn = getInputConnectionsCapacity().get(index);
    }
    return rtn;
  }

  /**
   * Returns the number of Capacity in <i>inputConnectionsCapacity</i>.
   *
   * @return The number of Capacity in <i>inputConnectionsCapacity</i>.
   */
  public int getNumInputConnectionsCapacity() {
    return getInputConnectionsCapacity().size();
  }

  /**
   * Getter for {@code inputConnectionsCapacity}.
   *
   * @return The value of {@code inputConnectionsCapacity}.
   */
  public CObjectCollection<Capacity> getInputConnectionsCapacity() {
    return inputConnectionsCapacity;
  }

  /*
   * InOut
   */
  /**
   * Adds the Capacity defined by parameter {@code c} to <i>inoutConnectionsCapacity</i>.
   *
   * @param c A {@link Capacity} object.
   */
  public void addInOutConnectionsCapacity(final Capacity c) {
    if (c != null) {
      getInOutConnectionsCapacity().add(c);
    }
  }

  /**
   * Returns the Capacity at the specified position in <i>inoutConnectionsCapacity</i>.
   *
   * @param index The index of the {@link Capacity} object to return.
   * @return If the index is within the bounds (0 <= bounds <
   *     this.getNumInOutConnectionsCapacity()), return the Capacity at the specified position in
   *     <i>inoutConnectionsCapacity</i>, otherwise null.
   */
  public Capacity getInOutConnectionsCapacityAtIdx(final int index) {
    Capacity rtn = null;
    if (0 <= index && index < getNumInOutConnectionsCapacity()) {
      rtn = getInOutConnectionsCapacity().get(index);
    }
    return rtn;
  }

  /**
   * Returns the number of Capacity in <i>inoutConnectionsCapacity</i>.
   *
   * @return The number of Capacity in <i>inoutConnectionsCapacity</i>.
   */
  public int getNumInOutConnectionsCapacity() {
    return getInOutConnectionsCapacity().size();
  }

  /**
   * Getter for {@code inoutConnectionsCapacity}.
   *
   * @return The value of {@code inoutConnectionsCapacity}.
   */
  public CObjectCollection<Capacity> getInOutConnectionsCapacity() {
    return inoutConnectionsCapacity;
  }

  private CObjectCollection<PTNetlistNode> nodes;
  private final Weight myWeight;
  private CObjectCollection<Capacity> outputConnectionsCapacity;
  private CObjectCollection<Capacity> inputConnectionsCapacity;
  private CObjectCollection<Capacity> inoutConnectionsCapacity;
}

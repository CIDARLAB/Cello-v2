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

package org.cellocad.v2.partitioning.netlist;

import java.util.HashSet;
import java.util.Set;
import org.cellocad.v2.common.Utils;
import org.cellocad.v2.common.constraint.Weight;
import org.cellocad.v2.common.graph.graph.VertexTemplate;
import org.cellocad.v2.partitioning.common.Block;
import org.cellocad.v2.results.netlist.NetlistNode;

/**
 * A netlist node for the <i>partitioning</i> stage.
 *
 * @author Vincent Mirian
 * @date Oct 26, 2017
 */
public class PTNetlistNode extends VertexTemplate<PTNetlistEdge> {

  private void init() {
    setMyBlock(null);
    setLocked(false);
    myWeight = new Weight();
    setPlaceLock(false);
    blockPlaceLock = "";
  }

  public PTNetlistNode() {
    super();
    init();
  }

  /**
   * Initializes a newly created {@link PTNetlistNode}.
   *
   * @param other The node to copy.
   */
  public PTNetlistNode(final PTNetlistNode other) {
    super(other);
    final Block block = other.getMyBlock();
    setMyBlock(block);
    setLocked(other.getLocked());
    if (block != null) {
      block.addPNode(this);
    }
    setMyWeight(other.getMyWeight());
  }

  @Override
  protected void addMeToSrc(final PTNetlistEdge e) {
    e.setSrc(this);
  }

  @Override
  protected void addMeToDst(final PTNetlistEdge e) {
    e.setDst(this);
  }

  @Override
  public PTNetlistEdge createT(final PTNetlistEdge e) {
    PTNetlistEdge rtn = null;
    rtn = new PTNetlistEdge(e);
    return rtn;
  }

  /*
   * Block
   */
  /**
   * Set the block of this instance.
   *
   * @param block The block to set.
   */
  public void setMyBlock(final Block block) {
    if (!getLocked()) {
      myBlock = block;
    }
  }

  public Block getMyBlock() {
    return myBlock;
  }

  /*
   * lock
   */
  public void setLocked(final boolean locked) {
    this.locked = locked;
  }

  public void enableLocked() {
    setLocked(true);
  }

  public void disableLocked() {
    setLocked(false);
  }

  public void toggleLocked() {
    setLocked(!getLocked());
  }

  public boolean getLocked() {
    return locked;
  }

  /*
   * blockPlaceLock
   */
  /**
   * Set a lock on the block with the given name.
   *
   * @param block A name of a block.
   */
  public void setMyBlockLock(final String block) {
    if (block != null) {
      blockPlaceLock = block;
    }
  }

  public String getMyBlockLock() {
    return blockPlaceLock;
  }

  /*
   * PlaceLock
   */
  public void setPlaceLock(final boolean locked) {
    placeLock = locked;
  }

  public void enablePlaceLock() {
    setPlaceLock(true);
  }

  public void disablePlaceLock() {
    setPlaceLock(false);
  }

  public void togglePlaceLock() {
    setPlaceLock(!getLocked());
  }

  public boolean getPlaceLock() {
    return placeLock;
  }

  /*
   * Weight
   */
  protected void setMyWeight(final Weight w) {
    myWeight = w;
  }

  public Weight getMyWeight() {
    return myWeight;
  }

  /*
   * is valid?
   */
  @Override
  public boolean isValid() {
    boolean rtn = true;
    // parent is valid
    rtn = rtn && super.isValid();
    rtn = rtn && (getLocked() && getMyBlock() != null || !getLocked());
    rtn = rtn && getMyWeight().isValid();
    return rtn;
  }

  /*
   * NodeType
   */
  /**
   * Setter for {@code nodeType}.
   *
   * @param nodeType The value to set {@code nodeType}.
   */
  public void setNodeType(final String nodeType) {
    this.nodeType = nodeType;
  }

  /**
   * Getter for {@code nodeType}.
   *
   * @return The nodeType of this instance.
   */
  public String getNodeType() {
    return nodeType;
  }

  private String nodeType;

  /*
   * DOT
   */
  /**
   * Returns a string representing the shape of this instance in DOT (graph description language)
   * format.
   *
   * @return A string representing the shape of this instance in DOT (graph description language)
   *     format.
   */
  @Override
  protected String getShape() {
    String rtn = super.getShape();
    final Block block = getMyBlock();
    if (block == null) {
      rtn = "octagon";
    } else {
      rtn = "none";
    }
    return rtn;
  }

  /**
   * Returns a string containing this instance in DOT (graph description language) format.
   *
   * @return A string containing this instance in DOT (graph description language) format.
   */
  // TODO: hacked
  @Override
  protected String getData() {
    String rtn = "";
    final PTNetlistNode srcNode = this;
    String startPoint = "\"" + srcNode.getName() + "\"";
    final Block srcBlock = srcNode.getMyBlock();
    Block dstBlock = null;
    int srcBlockIdx = -1;
    if (srcBlock != null) {
      srcBlockIdx = srcBlock.getIdx();
    }
    // block start
    if (srcBlockIdx != -1) {
      rtn += "subgraph cluster";
      rtn += srcBlockIdx;
      rtn += " {" + Utils.getNewLine();
      rtn += "rank=same";
      rtn += Utils.getNewLine();
      rtn += "label=\"";
      rtn += srcBlock.getName();
      rtn += "\";";
      rtn += Utils.getNewLine();
    }
    // node info
    rtn += "\"";
    rtn += srcNode.getName();
    rtn += "\" [shape=";
    rtn += srcNode.getShape();
    rtn += ", label=\"";
    if (srcBlockIdx == -1) {
      rtn += srcNode.getName();
    }
    // image
    if (srcBlockIdx != -1) {
      rtn += "\",  width=.5 height=.5 fixedsize=true image=\"";
      rtn += srcNode.getNetlistNode().getResultNetlistNodeData().getNodeType();
      rtn += ".png";
    }
    rtn += "\"]";
    rtn += Utils.getNewLine();
    // add point for fanout
    // TODO: hack this should be in a hypergraph Netlist
    // connected to different blocks
    final Set<Block> set = new HashSet<>();
    for (int i = 0; i < srcNode.getNumOutEdge(); i++) {
      final PTNetlistNode dstNode = srcNode.getOutEdgeAtIdx(i).getDst();
      final Block otherBlock = dstNode.getMyBlock();
      set.add(otherBlock);
    }
    if (set.size() > 1) {
      startPoint = "\"" + getName() + "Point\"";
      rtn += startPoint + " [ shape=point ]";
      rtn += Utils.getNewLine();
      rtn += "\"" + getName() + "\" -> \"" + getName() + "Point\":w";
      rtn += Utils.getNewLine();
    }
    // end block
    if (srcBlockIdx != -1) {
      rtn += "}";
      rtn += Utils.getNewLine();
    }
    // edges
    set.clear();
    for (int i = 0; i < getNumOutEdge(); i++) {
      final PTNetlistNode dstNode = getOutEdgeAtIdx(i).getDst();
      dstBlock = dstNode.getMyBlock();
      int dstBlockIdx = -1;
      if (dstBlock != null) {
        dstBlockIdx = dstBlock.getIdx();
      }
      if (dstBlockIdx != -1 && set.contains(dstBlock)) {
        continue;
      }
      set.add(dstBlock);
      rtn += startPoint;
      rtn += " -> \"";
      rtn += dstNode.getName();
      rtn += "\"";
      if (srcBlockIdx != dstBlockIdx) {
        if (srcBlockIdx >= 0 || dstBlockIdx >= 0) {
          rtn += "[";
          if (srcBlockIdx >= 0) {
            rtn += "ltail=cluster" + srcBlockIdx;
          }
          if (srcBlockIdx >= 0 && dstBlockIdx >= 0) {
            rtn += ",";
          }
          if (dstBlockIdx >= 0) {
            rtn += "lhead=cluster" + dstBlockIdx;
          }
          rtn += ",style=dashed";
          rtn += "];";
        }
      }
      rtn += Utils.getNewLine();
    }
    return rtn;
  }

  /*
   * HashCode
   */
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + (locked ? 1231 : 1237);
    result = prime * result + (myBlock == null ? 0 : myBlock.getName().hashCode());
    result = prime * result + (myWeight == null ? 0 : myWeight.hashCode());
    result = prime * result + (placeLock ? 1231 : 1237);
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
    final PTNetlistNode other = (PTNetlistNode) obj;
    if (locked != other.locked) {
      return false;
    }
    if (getMyBlock() != other.getMyBlock()) {
      return false;
    }
    if (myWeight == null) {
      if (other.myWeight != null) {
        return false;
      }
    } else if (!myWeight.equals(other.myWeight)) {
      return false;
    }
    if (placeLock != other.placeLock) {
      return false;
    }
    return true;
  }

  /*
   * toString
   */
  @Override
  public String toString() {
    String rtn = "";
    rtn = rtn + "[ ";
    rtn = rtn + Utils.getNewLine();
    // name
    rtn = rtn + this.getEntryToString("name", getName());
    // myBlock
    String block = "NOT ASSIGNED";
    if (getMyBlock() != null) {
      block = getMyBlock().getName();
    }
    rtn = rtn + this.getEntryToString("myBlock", block);
    // locked
    rtn = rtn + this.getEntryToString("locked", locked);
    // placeLock
    if (getPlaceLock()) {
      rtn = rtn + this.getEntryToString("placeLock", blockPlaceLock);
    }
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

  /**
   * Setter for {@code netlistNode}.
   *
   * @param netlistNode The value to set {@code netlistNode}.
   */
  public void setNetlistNode(final NetlistNode netlistNode) {
    this.netlistNode = netlistNode;
  }

  /**
   * Getter for {@code netlistNode}.
   *
   * @return The value of {@code netlistNode}.
   */
  public NetlistNode getNetlistNode() {
    return netlistNode;
  }

  private NetlistNode netlistNode;

  private Block myBlock;
  private boolean locked;
  private Weight myWeight;
  private boolean placeLock;
  private String blockPlaceLock;
}

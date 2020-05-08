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
import org.cellocad.v2.partitioning.netlist.PTNetlistNode;

/**
 * A representation of a move operation.
 *
 * @author Vincent Mirian
 * @date Oct 26, 2017
 */
public class Move extends CObject {

  /** Initializes a newly created {@link Move}. */
  public Move() {
    setPNode(null);
    setSrcBlock(null);
    setDstBlock(null);
  }

  /**
   * Initializes a newly created {@link Move}.
   *
   * @param node The node to move.
   * @param srcBlock The source block.
   * @param dstBlock The destination block.
   */
  public Move(final PTNetlistNode node, final Block srcBlock, final Block dstBlock) {
    setPNode(node);
    setSrcBlock(srcBlock);
    setDstBlock(dstBlock);
  }

  /*
   * null source block means not assigned null destination block means remove/no assignment
   */

  public void setPNode(final PTNetlistNode node) {
    this.node = node;
  }

  public PTNetlistNode getPNode() {
    return node;
  }

  public void setSrcBlock(final Block srcBlock) {
    this.srcBlock = srcBlock;
  }

  public Block getSrcBlock() {
    return srcBlock;
  }

  public void setDstBlock(final Block dstBlock) {
    this.dstBlock = dstBlock;
  }

  public Block getDstBlock() {
    return dstBlock;
  }

  /*
   * Undo
   */

  /** Invert the move. */
  public void makeUndo() {
    final Block Src = getSrcBlock();
    final Block Dst = getDstBlock();
    setSrcBlock(Dst);
    setDstBlock(Src);
  }

  public Move createUndo(final Move move) {
    final Move rtn = new Move(move.getPNode(), move.getDstBlock(), move.getSrcBlock());
    return rtn;
  }

  /*
   * is valid?
   */
  @Override
  public boolean isValid() {
    boolean rtn = false;
    // parent is valid
    rtn = super.isValid();
    // other
    final PTNetlistNode node = getPNode();
    final Block srcBlock = getSrcBlock();
    // node is not null
    rtn = rtn && node != null;
    if (rtn) {
      // node and srcBlock are identical
      rtn = rtn && node.getMyBlock() == srcBlock;
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
    result = prime * result + (dstBlock == null ? 0 : dstBlock.hashCode());
    result = prime * result + (node == null ? 0 : node.hashCode());
    result = prime * result + (srcBlock == null ? 0 : srcBlock.hashCode());
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
    final Move other = (Move) obj;
    if (dstBlock == null) {
      if (other.dstBlock != null) {
        return false;
      }
    } else if (!dstBlock.equals(other.dstBlock)) {
      return false;
    }
    if (node == null) {
      if (other.node != null) {
        return false;
      }
    } else if (!node.equals(other.node)) {
      return false;
    }
    if (srcBlock == null) {
      if (other.srcBlock != null) {
        return false;
      }
    } else if (!srcBlock.equals(other.srcBlock)) {
      return false;
    }
    return true;
  }

  /*
   * toString
   */
  @Override
  public String toString() {
    return "Move [node="
        + node
        + ", srcBlock="
        + srcBlock.getName()
        + ", dstBlock="
        + dstBlock.getName()
        + "]";
  }

  private PTNetlistNode node;
  private Block srcBlock;
  private Block dstBlock;
}

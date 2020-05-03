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

import org.cellocad.v2.common.Utils;
import org.cellocad.v2.common.constraint.Weight;
import org.cellocad.v2.common.graph.graph.EdgeTemplate;
import org.cellocad.v2.results.netlist.NetlistEdge;

/**
 * A netlist edge in the <i>partitioning</i> stage.
 * 
 * @author Vincent Mirian
 *
 * @date Oct 27, 2017
 */
public class PTNetlistEdge extends EdgeTemplate<PTNetlistNode> {

  private void init() {
    myWeight = new Weight();
  }

  /**
   * Initializes a newly created {@link PTNetlistEdge}.
   */
  public PTNetlistEdge() {
    super();
    setSrc(null);
    setDst(null);
    init();
  }

  /**
   * Initializes a newly created {@link PTNetlistEdge}.
   *
   * @param src A source node.
   * @param dst A destination node.
   */
  public PTNetlistEdge(final PTNetlistNode src, final PTNetlistNode dst) {
    this();
    setSrc(src);
    setDst(dst);
  }

  /**
   * Initializes a newly created {@link PTNetlistEdge}.
   *
   * @param other The other edge to copy.
   */
  public PTNetlistEdge(final PTNetlistEdge other) {
    super(other);
    setSrc(other.getSrc());
    setDst(other.getDst());
    setMyWeight(other.getMyWeight());
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
   * HashCode
   */
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
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
    final PTNetlistEdge other = (PTNetlistEdge) obj;
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
  @Override
  public String toString() {
    String rtn = "";
    rtn = rtn + "[ ";
    rtn = rtn + Utils.getNewLine();
    // name
    rtn = rtn + this.getEntryToString("name", getName());
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
   * dot file
   */
  /**
   * Returns a string containing this instance in DOT (graph description language) format.
   *
   * @return A string containing this instance in DOT (graph description language) format.
   */
  @Override
  protected String getData() {
    String rtn = "";
    /*
     * PTNetlistNode src = this.getSrc(); PTNetlistNode dst = null; Block srcBlock = null; Block
     * dstBlock = null; srcBlock = src.getMyBlock(); int srcBlockIdx = -1; if (srcBlock != null) {
     * srcBlockIdx = srcBlock.getIdx(); } // name String srcName = src.getName(); Set<Block> set =
     * new HashSet<Block>(); for (int i = 0; i < src.getNumOutEdge(); i++) { PTNetlistNode dstNode =
     * src.getOutEdgeAtIdx(i).getDst(); set.add(dstNode.getMyBlock()); } if (set.size() > 1) {
     * srcName = "\"" + srcName + "Point\":e"; } else { srcName = "\"" + srcName + "\""; } int
     * numDst = this.getNumDst(); if (src != null && numDst > 0) { for (int i = 0; i < numDst; i ++)
     * { dst = this.getDstAtIdx(i); dstBlock = dst.getMyBlock(); int dstBlockIdx = -1; if (dstBlock
     * != null) { dstBlockIdx = dstBlock.getIdx(); } rtn += srcName; rtn += " -> \""; rtn +=
     * dst.getName(); rtn += "\""; if ((srcBlockIdx >= 0) || (dstBlockIdx >= 0)){ rtn += "["; if
     * (srcBlockIdx >= 0) { rtn += "ltail=cluster" + srcBlockIdx; } if ((srcBlockIdx >= 0) &&
     * (dstBlockIdx >= 0)) { rtn += ","; } if (dstBlockIdx >= 0) { rtn += "lhead=cluster" +
     * dstBlockIdx; } rtn += "];"; } rtn += Utils.getNewLine(); } }.
     */
    rtn = "";
    return rtn;
  }

  private Weight myWeight;

  /**
   * Setter for {@code netlistEdge}.
   *
   * @param netlistEdge The value to set {@code netlistEdge}.
   */
  public void setNetlistEdge(final NetlistEdge netlistEdge) {
    this.netlistEdge = netlistEdge;
  }

  /**
   * Getter for {@code netlistEdge}.
   *
   * @return The value of {@code netlistEdge}.
   */
  public NetlistEdge getNetlistEdge() {
    return netlistEdge;
  }

  private NetlistEdge netlistEdge;

}

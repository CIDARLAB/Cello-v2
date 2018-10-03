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
package partitioning.netlist;

import java.util.HashSet;
import java.util.Set;

import common.Utils;
import common.constraint.Weight;
import common.graph.graph.VertexTemplate;
import partitioning.common.Block;
import results.netlist.NetlistNode;


/**
 * @author Vincent Mirian
 * 
 * @date Oct 26, 2017
 *
 */
public class PTNetlistNode extends VertexTemplate<PTNetlistEdge> {

	private void init() {
		this.setMyBlock(null);
		this.setLocked(false);
		this.myWeight = new Weight();
		this.setPlaceLock(false);
		this.blockPlaceLock = "";
	}
	
	public PTNetlistNode(){
		super();
		init();
	}
	
	public PTNetlistNode(final PTNetlistNode other){
		super(other);
		Block block = other.getMyBlock(); 
		this.setMyBlock(block);
		this.setLocked(other.getLocked());
		if (block != null)
			block.addPNode(this);
        this.setMyWeight(other.getMyWeight());
	}

	@Override
	protected void addMeToSrc(final PTNetlistEdge e) {
		e.setSrc(this);
	}

	@Override
	protected void addMeToDst(final PTNetlistEdge e){
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
	public void setMyBlock(final Block block){
		if (!this.getLocked()){
			this.myBlock = block;
		}
	}
		
	public Block getMyBlock(){
		return this.myBlock;
	}
	
	/*
	 * lock
	 */
	public void setLocked(boolean locked){
		this.locked = locked;
	}
	
	public void enableLocked(){
		this.setLocked(true);
	}

	public void disableLocked(){
		this.setLocked(false);
	}

	public void toggleLocked(){
		this.setLocked(!this.getLocked());
	}
		
	public boolean getLocked(){
		return this.locked;
	}

	/*
	 * blockPlaceLock
	 */
	public void setMyBlockLock(final String block){
		if (block != null){
			this.blockPlaceLock = block;
		}
	}
		
	public String getMyBlockLock(){
		return this.blockPlaceLock;
	}
	
	/*
	 * PlaceLock
	 */
	public void setPlaceLock(boolean locked){
		this.placeLock = locked;
	}
	
	public void enablePlaceLock(){
		this.setPlaceLock(true);
	}

	public void disablePlaceLock(){
		this.setPlaceLock(false);
	}

	public void togglePlaceLock(){
		this.setPlaceLock(!this.getLocked());
	}
		
	public boolean getPlaceLock(){
		return this.placeLock;
	}

	/*
	 * Weight
	 */
	protected void setMyWeight(final Weight w){
		this.myWeight = w;
	}
	
	public Weight getMyWeight(){
		return this.myWeight;
	}
	
	/*
	 * is valid?
	 */
	public boolean isValid(){
		boolean rtn = true;
		// parent is valid
		rtn = rtn && super.isValid();
		rtn = rtn && ((this.getLocked() && (this.getMyBlock() != null)) || (!this.getLocked()));
		rtn = rtn && this.getMyWeight().isValid();
		return rtn;
	}

	/*
	 * NodeType
	 */
	/**
	 *  Setter for <i>nodeType</i>
	 *  @param nodeType the value to set <i>nodeType</i>
	 */
	public void setNodeType(String nodeType) {
		this.nodeType = nodeType;
	}

	/**
	 *  Getter for <i>nodeType</i>
	 *  @return the nodeType of this instance
	 */
	public String getNodeType() {
		return this.nodeType;
	}
	
	private String nodeType;
	
	/*
	 * DOT
	 */
	/**
	 * Returns a string representing the shape of this instance in DOT (graph description language) format
	 * 
	 * @return a string representing the shape of this instance in DOT (graph description language) format
	 */
	@Override
	protected String getShape() {
		String rtn = super.getShape();
		Block block = this.getMyBlock();
		if (block == null) {
			rtn = "octagon";
		}
		else {
			rtn = "none";
		}
		return rtn;
	}

	/**
	 * Returns a string containing this instance in DOT (graph description language) format
	 * 
	 * @return a string containing this instance in DOT (graph description language) format
	 */
	//TODO: hacked
	protected String getData(){
		String rtn = "";
		PTNetlistNode srcNode = this;
		String startPoint = "\"" + srcNode.getName() + "\"";
		Block srcBlock = srcNode.getMyBlock();
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
		Set<Block> set = new HashSet<Block>();
		for (int i = 0; i < srcNode.getNumOutEdge(); i++) {
			PTNetlistNode dstNode = srcNode.getOutEdgeAtIdx(i).getDst();
			Block otherBlock = dstNode.getMyBlock();
			set.add(otherBlock);
		}
		if (set.size() > 1) {
			startPoint = "\"" + this.getName() + "Point\"";
			rtn += startPoint + " [ shape=point ]";
			rtn += Utils.getNewLine();
			rtn += "\"" + this.getName() + "\" -> \"" + this.getName() + "Point\":w";
			rtn += Utils.getNewLine();
		}
		// end block
		if (srcBlockIdx != -1) {
			rtn += "}";
			rtn += Utils.getNewLine();
		}
		// edges
		set.clear();
		for (int i = 0; i < this.getNumOutEdge(); i++) {
			PTNetlistNode dstNode = this.getOutEdgeAtIdx(i).getDst();
			dstBlock = dstNode.getMyBlock();
			int dstBlockIdx = -1;
			if (dstBlock != null) {
				dstBlockIdx = dstBlock.getIdx();
			}
			if ((dstBlockIdx != -1) && set.contains(dstBlock)) {
				continue;
			}
			set.add(dstBlock);
			rtn += startPoint;
			rtn += " -> \"";
			rtn += dstNode.getName();
			rtn += "\"";
			if (srcBlockIdx != dstBlockIdx) {
				if ((srcBlockIdx >= 0) || (dstBlockIdx >= 0)){
					rtn += "[";
					if (srcBlockIdx >= 0) {
						rtn += "ltail=cluster" + srcBlockIdx;
					}
					if ((srcBlockIdx >= 0) && (dstBlockIdx >= 0)) {
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
		result = prime * result + ((myBlock == null) ? 0 : myBlock.getName().hashCode());
		result = prime * result + ((myWeight == null) ? 0 : myWeight.hashCode());
		result = prime * result + (placeLock ? 1231 : 1237);
		return result;
	}

	/*
	 * Equals
	 */
	@Override
	public boolean equals(final Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		PTNetlistNode other = (PTNetlistNode) obj;
		if (locked != other.locked)
			return false;
		if (this.getMyBlock() != other.getMyBlock())
			return false;
		if (myWeight == null) {
			if (other.myWeight != null)
				return false;
		} else if (!myWeight.equals(other.myWeight))
			return false;
		if (placeLock != other.placeLock)
			return false;
		return true;
	}
	
	/*
	 * toString
	 */
	@Override
	public String toString() {
		String rtn = "";
		String indentStr = "";
		rtn = rtn + "[ ";
		rtn = rtn + Utils.getNewLine();
		// name
		rtn = rtn + this.getEntryToString("name", this.getName());
		// myBlock
		String block = "NOT ASSIGNED";
		if (this.getMyBlock() != null) {
			block = this.getMyBlock().getName();
		}
		rtn = rtn + this.getEntryToString("myBlock", block);
		// locked
		rtn = rtn + this.getEntryToString("locked", locked);
		// placeLock
		if (this.getPlaceLock()) {
			rtn = rtn + this.getEntryToString("placeLock", blockPlaceLock);	
		}
		// Weight
		rtn = rtn + Utils.getTabCharacter();
		rtn = rtn + "myWeight = ";
		rtn = rtn + Utils.getNewLine();
		indentStr = this.getMyWeight().toString();
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
	 * Setter for <i>netlistNode</i>
	 * @param netlistNode the value to set <i>netlistNode</i>
	*/
	public void setNetlistNode(final NetlistNode netlistNode) {
		this.netlistNode = netlistNode;
	}

	/**
	 * Getter for <i>netlistNode</i>
	 * @return value of <i>netlistNode</i>
	*/
	public NetlistNode getNetlistNode() {
		return this.netlistNode;
	}

	private NetlistNode netlistNode;
	
	private Block myBlock;
	private boolean locked;
	private Weight myWeight;
	private boolean placeLock;
	private String blockPlaceLock;
}

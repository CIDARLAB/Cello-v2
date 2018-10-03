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
package partitioning.common;


import common.CObject;
import common.CObjectCollection;
import common.Utils;
import common.constraint.Weight;
import partitioning.netlist.PTNetlistEdge;
import partitioning.profile.Capacity;
import partitioning.profile.CapacityCollection;
import partitioning.profile.InterBlockProfile;

/**
 * @author Vincent Mirian
 * 
 * @date Oct 26, 2017
 *
 */
public class InterBlock extends CapacityCollection<InterBlockProfile> {

	private void init(){
		this.edges = new CObjectCollection<PTNetlistEdge>();
	}
	
	public InterBlock(InterBlockProfile IBP, CObjectCollection<Block> blocks, CObjectCollection<Capacity> capacity, CObjectCollection<CObject> capacityUnits){
		super(IBP, capacity);
		init();
		Utils.isNullRuntimeException(capacityUnits, "CapacityUnits");
		myWeight = new Weight(capacityUnits, capacityUnits);
		this.setSrcDst(IBP, blocks);
	}
	
	/*
	 * myWeight
	 */	
	private Weight getMyWeight() {
		return this.myWeight;
	}
	
	/*
	 * Evaluate
	 */	
	public boolean canFit () {
		boolean rtn = false;
		rtn = this.canFit(this.getMyWeight());
		return rtn;
	}
	
	public boolean isOverflow () {
		boolean rtn = false;
		rtn = this.isOverflow(this.getMyWeight());
		return rtn;
	}
	
	public boolean isUnderflow () {
		boolean rtn = false;
		rtn = this.isUnderflow(this.getMyWeight());
		return rtn;
	}
	
	@Override
	public boolean canFit (final Weight wObj) {
		boolean rtn = false;
		if (wObj != null) {
			Weight wObjTemp = new Weight(wObj);
			wObjTemp.inc(this.getMyWeight());
			rtn = super.canFit(wObjTemp);
		}
		return rtn;
	}

	@Override
	public boolean isOverflow (final Weight wObj) {
		boolean rtn = false;
		if (wObj != null) {
			Weight wObjTemp = new Weight(wObj);
			wObjTemp.inc(this.getMyWeight());
			rtn = super.isOverflow(wObjTemp);
		}
		return rtn;
	}

	@Override
	public boolean isUnderflow (final Weight wObj) {
		boolean rtn = false;
		if (wObj != null) {
			Weight wObjTemp = new Weight(wObj);
			wObjTemp.inc(this.getMyWeight());
			rtn = super.isUnderflow(wObjTemp);
		}
		return rtn;
	}
	
	/*
	 * PEdge
	 */
	public void addPEdge(final PTNetlistEdge edge){
		if (edge != null){
			edges.add(edge);
			myWeight.inc(edge.getMyWeight());
		}
	}
	
	public void removePEdge(final PTNetlistEdge edge){
		if ((edge != null) && this.PEdgeExists(edge)) {
			edges.remove(edge);
			myWeight.dec(edge.getMyWeight());
		}
	}
	
	public PTNetlistEdge getPEdgeAtIdx(int index){
		PTNetlistEdge rtn = null;
		if (
				(index >= 0) &&
				(index < this.getNumPEdge())
			){
			rtn = edges.get(index);	
		} 
		return rtn;
	}
	
	public int getNumPEdge(){
		int rtn = edges.size();
		return rtn;
	}
	
	private boolean PEdgeExists(final PTNetlistEdge edge){
		boolean rtn = (edge != null) && (edges.contains(edge));
		return rtn;
	}
	/*
	 * Src/Dst
	 */
	private void setSrcDst(InterBlockProfile IBP, CObjectCollection<Block> blocks){
		Block block = null;
		String blockName = null;
		blockName = IBP.getSource().getName();
		block = blocks.findCObjectByName(blockName);
		if (block == null) {
			throw new RuntimeException ("Source block not found: " + blockName);
		}
		this.setSrcBlock(block);
		blockName = IBP.getDestination().getName();
		block = blocks.findCObjectByName(blockName);
		if (block == null) {
			throw new RuntimeException ("Destination block not found: " + blockName);
		}
		this.setDstBlock(block);
	}
	/*
	 * Src Block
	 */
	public Block getSrcBlock() {
		return this.srcBlock;
	}
	
	private void setSrcBlock(Block block) {
		this.srcBlock = block;
	}
	
	private Block srcBlock;

	/*
	 * Dst Block
	 */
	public Block getDstBlock() {
		return this.dstBlock;
	}
	
	private void setDstBlock(Block block) {
		this.dstBlock = block;
	}
	
	private Block dstBlock;
	
	
	/*
	 * HashCode
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((edges == null) ? 0 : edges.hashCode());
		result = prime * result + ((myWeight == null) ? 0 : myWeight.hashCode());
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
		InterBlock other = (InterBlock) obj;
		if (edges == null) {
			if (other.edges != null)
				return false;
		} else if (!edges.equals(other.edges))
			return false;
		if (myWeight == null) {
			if (other.myWeight != null)
				return false;
		} else if (!myWeight.equals(other.myWeight))
			return false;
		return true;
	}
	
	private CObjectCollection<PTNetlistEdge> edges;
	private Weight myWeight;

}

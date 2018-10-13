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
package org.cellocad.cello2.partitioning.common;

import java.io.IOException;
import java.io.Writer;

import org.cellocad.cello2.common.CObject;
import org.cellocad.cello2.common.CObjectCollection;
import org.cellocad.cello2.common.Utils;
import org.cellocad.cello2.common.constraint.Weight;
import org.cellocad.cello2.partitioning.netlist.PTNetlist;
import org.cellocad.cello2.partitioning.netlist.PTNetlistEdge;
import org.cellocad.cello2.partitioning.netlist.PTNetlistNode;
import org.cellocad.cello2.partitioning.profile.BlockProfile;
import org.cellocad.cello2.partitioning.profile.Capacity;
import org.cellocad.cello2.partitioning.profile.CapacityCollection;
import org.cellocad.cello2.partitioning.profile.CapacityProfile;

/**
 * @author Vincent Mirian
 * 
 * @date Oct 26, 2017
 *
 */
public class Block extends CapacityCollection<BlockProfile> {

	private void init(){
		this.nodes = new CObjectCollection<PTNetlistNode>();
		outputConnectionsCapacity = new CObjectCollection<Capacity>();
		inputConnectionsCapacity = new CObjectCollection<Capacity>();
		inoutConnectionsCapacity = new CObjectCollection<Capacity>();
	}

	private void initOutputConnectionsCapacity(BlockProfile BP, CObjectCollection<Capacity> CCP) {
		Utils.isNullRuntimeException(CCP, "CapacityCollectionProfile");
		Utils.isNullRuntimeException(BP, "BlockProfile");
		init();
		CapacityProfile CP = null;
		Capacity capacity = null;
		for (int i = 0; i < BP.getNumOutputConnectionsCapacity(); i ++) {
			CP = BP.getOutputConnectionsCapacityAtIdx(i);
			capacity = CCP.findCObjectByName(CP.getName());
			Utils.isNullRuntimeException(capacity, "Capacity");
			this.addOutputConnectionsCapacity(capacity);
		}
	}

	private void initInputConnectionsCapacity(BlockProfile BP, CObjectCollection<Capacity> CCP) {
		Utils.isNullRuntimeException(CCP, "CapacityCollectionProfile");
		Utils.isNullRuntimeException(BP, "BlockProfile");
		init();
		CapacityProfile CP = null;
		Capacity capacity = null;
		for (int i = 0; i < BP.getNumInputConnectionsCapacity(); i ++) {
			CP = BP.getInputConnectionsCapacityAtIdx(i);
			capacity = CCP.findCObjectByName(CP.getName());
			Utils.isNullRuntimeException(capacity, "Capacity");
			this.addInputConnectionsCapacity(capacity);
		}
	}

	private void initInOutConnectionsCapacity(BlockProfile BP, CObjectCollection<Capacity> CCP) {
		Utils.isNullRuntimeException(CCP, "CapacityCollectionProfile");
		Utils.isNullRuntimeException(BP, "BlockProfile");
		init();
		CapacityProfile CP = null;
		Capacity capacity = null;
		for (int i = 0; i < BP.getNumInOutConnectionsCapacity(); i ++) {
			CP = BP.getInOutConnectionsCapacityAtIdx(i);
			capacity = CCP.findCObjectByName(CP.getName());
			Utils.isNullRuntimeException(capacity, "Capacity");
			this.addInOutConnectionsCapacity(capacity);
		}
	}

	
	public Block(BlockProfile BP, CObjectCollection<Capacity> capacity, CObjectCollection<CObject> capacityUnits){
		super(BP, capacity);
		init();
		Utils.isNullRuntimeException(capacityUnits, "CapacityUnits");
		myWeight = new Weight(capacityUnits, capacityUnits);
		this.initOutputConnectionsCapacity(BP, capacity);
		this.initInputConnectionsCapacity(BP, capacity);
		this.initInOutConnectionsCapacity(BP, capacity);
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
	 * PNode
	 */
	public void addPNode(final PTNetlistNode node){
		if (node != null){
			nodes.add(node);
			myWeight.inc(node.getMyWeight());
		}
	}
	
	public void removePNode(final PTNetlistNode node){
		if ((node != null) && this.PNodeExists(node)) {
			nodes.remove(node);
			myWeight.dec(node.getMyWeight());
		}
	}
	
	public PTNetlistNode getPNodeAtIdx(int index){
		PTNetlistNode rtn = null;
		if (
				(index >= 0) &&
				(index < this.getNumPNode())
			){
			rtn = nodes.get(index);	
		} 
		return rtn;
	}
	
	public int getNumPNode(){
		int rtn = nodes.size();
		return rtn;
	}
	
	private boolean PNodeExists(final PTNetlistNode node){
		boolean rtn = (node != null) && (nodes.contains(node));
		return rtn;
	}

	/*
	 * dot file
	 */
	public PTNetlist convertToPTNetlist(){
		PTNetlist rtn = new PTNetlist();
		for (int i = 0; i < this.getNumPNode(); i++){
			PTNetlistNode node = this.getPNodeAtIdx(i);
			rtn.addVertex(node);
			// outedges
			for (int j = 0; j < node.getNumOutEdge(); j ++){
				PTNetlistEdge edge = node.getOutEdgeAtIdx(j);
				PTNetlistNode other = edge.getDst();
				if (this.PNodeExists(other)){
					rtn.addEdge(edge);
				}
			}
			// inedges
			for (int j = 0; j < node.getNumInEdge(); j ++){
				PTNetlistEdge edge = node.getInEdgeAtIdx(j);
				PTNetlistNode other = edge.getSrc();
				if (this.PNodeExists(other)){
					rtn.addEdge(edge);
				}
			}
		}
		return rtn;
	}
	
	public void printDot(final Writer os) throws IOException{
		PTNetlist ptNetlist = this.convertToPTNetlist();
		ptNetlist.printDot(os);
	}
	
	/*
	 * HashCode
	 */
	/*@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((nodes == null) ? 0 : nodes.hashCode());
		result = prime * result + ((myWeight == null) ? 0 : myWeight.hashCode());
		return result;
	}*/

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
		Block other = (Block) obj;
		if (nodes == null) {
			if (other.nodes != null)
				return false;
		} else if (!nodes.equals(other.nodes))
			return false;
		if (myWeight == null) {
			if (other.myWeight != null)
				return false;
		} else if (!myWeight.equals(other.myWeight))
			return false;
		return true;
	}

	/*
	 * toString
	 */
	protected String getNodesToString() {
		String rtn = "";
		for (int i = 0; i < this.getNumPNode(); i ++) {
			rtn = rtn + Utils.getTabCharacterRepeat(2);
			PTNetlistNode node = this.getPNodeAtIdx(i);
			rtn = rtn + node.getName();
			rtn = rtn + ",";
			rtn = rtn + Utils.getNewLine();
		}
		return rtn;
	}
	
	@Override
	public String toString() {
		String rtn = "";
		String indentStr = "";
		rtn = rtn + "[ ";
		rtn = rtn + Utils.getNewLine();
		// name
		rtn = rtn + this.getEntryToString("name", this.getName());
		// nodes
		rtn = rtn + Utils.getTabCharacter();
		rtn = rtn + "nodes = ";
		rtn = rtn + Utils.getNewLine();
		rtn = rtn + Utils.getTabCharacter();
		rtn = rtn + "{";
		rtn = rtn + Utils.getNewLine();
		rtn = rtn + this.getNodesToString();
		rtn = rtn + Utils.getTabCharacter();
		rtn = rtn + "}";
		rtn = rtn + Utils.getNewLine();	
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

	/*
	 * Output
	 */
	/**
	 *  Adds the Capacity defined by parameter <i>c</i> to <i>outputConnectionsCapacity</i>
	 *  
	 *  @param c a non-null instance of type Capacity
	 */
	public void addOutputConnectionsCapacity(final Capacity c) {
		if (c != null) {
			this.getOutputConnectionsCapacity().add(c);
		}
	}

	/**
	 * Returns the Capacity at the specified position in <i>outputConnectionsCapacity</i>
	 * 
	 * @param index index of the Capacity to return
	 * @return if the index is within the bounds (0 <= bounds < this.getNumOutputConnectionsCapacity()), return the Capacity at the specified position in <i>outputConnectionsCapacity</i>, otherwise null
	 */
	public Capacity getOutputConnectionsCapacityAtIdx(int index) {
		Capacity rtn = null;
		if (
				(0 <= index) &&
				(index < this.getNumOutputConnectionsCapacity())) {
			rtn = this.getOutputConnectionsCapacity().get(index);
		}		
		return rtn;
	}

	/**
	 * Returns the number of Capacity in <i>outputConnectionsCapacity</i>
	 * 
	 * @return the number of Capacity in <i>outputConnectionsCapacity</i>
	 */
	public int getNumOutputConnectionsCapacity() {
		return this.getOutputConnectionsCapacity().size();
	}
	
	/**
	 * Getter for <i>outputConnectionsCapacity</i>
	 * @return value of <i>outputConnectionsCapacity</i>
	*/
	public CObjectCollection<Capacity> getOutputConnectionsCapacity() {
		return this.outputConnectionsCapacity;
	}

	/*
	 * Input
	 */
	/**
	 *  Adds the Capacity defined by parameter <i>c</i> to <i>inputConnectionsCapacity</i>
	 *  
	 *  @param c a non-null instance of type Capacity
	 */
	public void addInputConnectionsCapacity(final Capacity c) {
		if (c != null) {
			this.getInputConnectionsCapacity().add(c);
		}
	}

	/**
	 * Returns the Capacity at the specified position in <i>inputConnectionsCapacity</i>
	 * 
	 * @param index index of the Capacity to return
	 * @return if the index is within the bounds (0 <= bounds < this.getNumInputConnectionsCapacity()), return the Capacity at the specified position in <i>inputConnectionsCapacity</i>, otherwise null
	 */
	public Capacity getInputConnectionsCapacityAtIdx(int index) {
		Capacity rtn = null;
		if (
				(0 <= index) &&
				(index < this.getNumInputConnectionsCapacity())) {
			rtn = this.getInputConnectionsCapacity().get(index);
		}		
		return rtn;
	}

	/**
	 * Returns the number of Capacity in <i>inputConnectionsCapacity</i>
	 * 
	 * @return the number of Capacity in <i>inputConnectionsCapacity</i>
	 */
	public int getNumInputConnectionsCapacity() {
		return this.getInputConnectionsCapacity().size();
	}
	
	/**
	 * Getter for <i>inputConnectionsCapacity</i>
	 * @return value of <i>inputConnectionsCapacity</i>
	*/
	public CObjectCollection<Capacity> getInputConnectionsCapacity() {
		return this.inputConnectionsCapacity;
	}

	/*
	 * InOut
	 */
	/**
	 *  Adds the Capacity defined by parameter <i>c</i> to <i>inoutConnectionsCapacity</i>
	 *  
	 *  @param c a non-null instance of type Capacity
	 */
	public void addInOutConnectionsCapacity(final Capacity c) {
		if (c != null) {
			this.getInOutConnectionsCapacity().add(c);
		}
	}

	/**
	 * Returns the Capacity at the specified position in <i>inoutConnectionsCapacity</i>
	 * 
	 * @param index index of the Capacity to return
	 * @return if the index is within the bounds (0 <= bounds < this.getNumInOutConnectionsCapacity()), return the Capacity at the specified position in <i>inoutConnectionsCapacity</i>, otherwise null
	 */
	public Capacity getInOutConnectionsCapacityAtIdx(int index) {
		Capacity rtn = null;
		if (
				(0 <= index) &&
				(index < this.getNumInOutConnectionsCapacity())) {
			rtn = this.getInOutConnectionsCapacity().get(index);
		}		
		return rtn;
	}

	/**
	 * Returns the number of Capacity in <i>inoutConnectionsCapacity</i>
	 * 
	 * @return the number of Capacity in <i>inoutConnectionsCapacity</i>
	 */
	public int getNumInOutConnectionsCapacity() {
		return this.getInOutConnectionsCapacity().size();
	}

	/**
	 * Getter for <i>inoutConnectionsCapacity</i>
	 * @return value of <i>inoutConnectionsCapacity</i>
	*/
	public CObjectCollection<Capacity> getInOutConnectionsCapacity() {
		return this.inoutConnectionsCapacity;
	}
	
	private CObjectCollection<PTNetlistNode> nodes;
	private Weight myWeight;
	private CObjectCollection<Capacity> outputConnectionsCapacity;
	private CObjectCollection<Capacity> inputConnectionsCapacity;
	private CObjectCollection<Capacity> inoutConnectionsCapacity;
}

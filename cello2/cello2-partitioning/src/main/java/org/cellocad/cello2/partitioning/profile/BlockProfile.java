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
package org.cellocad.cello2.partitioning.profile;

import org.cellocad.cello2.common.CObjectCollection;
import org.cellocad.cello2.common.Utils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;


/**
 * @author Vincent Mirian
 * 
 * @date Oct 27, 2017
 *
 */
public class BlockProfile extends CapacityCollectionProfile {
	
	private void parseOutputConnectionsCapacity(final JSONObject JObj,
			final CObjectCollection<CapacityProfile> Capacity){
		// Capacity
		JSONArray jsonArr = (JSONArray) JObj.get("output_connections_capacity");
		if (jsonArr != null) {
			for (int i = 0; i < jsonArr.size(); i++){
	    	    Object jsonObj = (Object) jsonArr.get(i);
	    	    if (Utils.isString(jsonObj)) {
	    	    	String capacityName = (String) jsonObj;
	    	    	CapacityProfile capacity = Capacity.findCObjectByName(capacityName);
	    	    	if (capacity != null) {
	    	    		this.addOutputConnectionsCapacity(capacity);
	    	    	}
	        	    else {
	        	    	throw new RuntimeException(capacityName + " not found.");
	        	    }
	    	    }
			}
		}
	}

	private void parseInputConnectionsCapacity(final JSONObject JObj,
			final CObjectCollection<CapacityProfile> Capacity){
		// Capacity
		JSONArray jsonArr = (JSONArray) JObj.get("input_connections_capacity");
		if (jsonArr != null) {
			for (int i = 0; i < jsonArr.size(); i++){
	    	    Object jsonObj = (Object) jsonArr.get(i);
	    	    if (Utils.isString(jsonObj)) {
	    	    	String capacityName = (String) jsonObj;
	    	    	CapacityProfile capacity = Capacity.findCObjectByName(capacityName);
	    	    	if (capacity != null) {
	    	    		this.addInputConnectionsCapacity(capacity);
	    	    	}
	        	    else {
	        	    	throw new RuntimeException(capacityName + " not found.");
	        	    }
	    	    }
			}
		}
	}
	
	private void parseInOutConnectionsCapacity(final JSONObject JObj,
			final CObjectCollection<CapacityProfile> Capacity){
		// Capacity
		JSONArray jsonArr = (JSONArray) JObj.get("inout_connections_capacity");
		if (jsonArr != null) {
			if ((this.getOutputConnectionsCapacity().size() > 0) || (this.getInputConnectionsCapacity().size() > 0)) {
				throw new RuntimeException("Conflict: inout_connections_capacity for block name " + this.getName());
			}
			for (int i = 0; i < jsonArr.size(); i++){
	    	    Object jsonObj = (Object) jsonArr.get(i);
	    	    if (Utils.isString(jsonObj)) {
	    	    	String capacityName = (String) jsonObj;
	    	    	CapacityProfile capacity = Capacity.findCObjectByName(capacityName);
	    	    	if (capacity != null) {
	    	    		this.addInOutConnectionsCapacity(capacity);
	    	    	}
	        	    else {
	        	    	throw new RuntimeException(capacityName + " not found.");
	        	    }
	    	    }
			}
		}
	}

	private void init (){
		this.inputConnectionsCapacity = new CObjectCollection<CapacityProfile>();
		this.outputConnectionsCapacity = new CObjectCollection<CapacityProfile>();
		this.inoutConnectionsCapacity = new CObjectCollection<CapacityProfile>();
	}
	
	public BlockProfile(final JSONObject JObj,
			final CObjectCollection<CapacityProfile> Capacity){
		super(JObj, Capacity);
		this.init();
		this.parseOutputConnectionsCapacity(JObj, Capacity);
		this.parseInputConnectionsCapacity(JObj, Capacity);
		this.parseInOutConnectionsCapacity(JObj, Capacity);
	}

	/*
	 * Input
	 */
	/**
	 *  Adds the CapacityProfile defined by parameter <i>c</i> to <i>inputConnectionsCapacity</i>
	 *  
	 *  @param c a non-null instance of type CapacityProfile
	 */
	public void addInputConnectionsCapacity(final CapacityProfile c) {
		if (c != null) {
			this.getInputConnectionsCapacity().add(c);
		}
	}

	/**
	 * Returns the CapacityProfile at the specified position in <i>inputConnectionsCapacity</i>
	 * 
	 * @param index index of the CapacityProfile to return
	 * @return if the index is within the bounds (0 <= bounds < this.getNumInputConnectionsCapacity()), return the CapacityProfile at the specified position in <i>inputConnectionsCapacity</i>, otherwise null
	 */
	public CapacityProfile getInputConnectionsCapacityAtIdx(int index) {
		CapacityProfile rtn = null;
		if (
				(0 <= index) &&
				(index < this.getNumInputConnectionsCapacity())) {
			rtn = this.getInputConnectionsCapacity().get(index);
		}		
		return rtn;
	}

	/**
	 * Returns the number of CapacityProfile in <i>inputConnectionsCapacity</i>
	 * 
	 * @return the number of CapacityProfile in <i>inputConnectionsCapacity</i>
	 */
	public int getNumInputConnectionsCapacity() {
		return this.getInputConnectionsCapacity().size();
	}
	
	/**
	 * Getter for <i>inputConnectionsCapacity</i>
	 * @return value of <i>inputConnectionsCapacity</i>
	*/
	public CObjectCollection<CapacityProfile> getInputConnectionsCapacity() {
		return this.inputConnectionsCapacity;
	}
	
	private CObjectCollection<CapacityProfile> inputConnectionsCapacity;

	/*
	 * Output
	 */
	/**
	 *  Adds the CapacityProfile defined by parameter <i>c</i> to <i>outputConnectionsCapacity</i>
	 *  
	 *  @param c a non-null instance of type CapacityProfile
	 */
	public void addOutputConnectionsCapacity(final CapacityProfile c) {
		if (c != null) {
			this.getOutputConnectionsCapacity().add(c);
		}
	}

	/**
	 * Returns the CapacityProfile at the specified position in <i>outputConnectionsCapacity</i>
	 * 
	 * @param index index of the CapacityProfile to return
	 * @return if the index is within the bounds (0 <= bounds < this.getNumOutputConnectionsCapacity()), return the CapacityProfile at the specified position in <i>outputConnectionsCapacity</i>, otherwise null
	 */
	public CapacityProfile getOutputConnectionsCapacityAtIdx(int index) {
		CapacityProfile rtn = null;
		if (
				(0 <= index) &&
				(index < this.getNumOutputConnectionsCapacity())) {
			rtn = this.getOutputConnectionsCapacity().get(index);
		}		
		return rtn;
	}

	/**
	 * Returns the number of CapacityProfile in <i>outputConnectionsCapacity</i>
	 * 
	 * @return the number of CapacityProfile in <i>outputConnectionsCapacity</i>
	 */
	public int getNumOutputConnectionsCapacity() {
		return this.getOutputConnectionsCapacity().size();
	}

	/**
	 * Getter for <i>outputConnectionsCapacity</i>
	 * @return value of <i>outputConnectionsCapacity</i>
	*/
	public CObjectCollection<CapacityProfile> getOutputConnectionsCapacity() {
		return this.outputConnectionsCapacity;
	}
	
	private CObjectCollection<CapacityProfile> outputConnectionsCapacity;

	
	/*
	 * InOut
	 */
	/**
	 *  Adds the CapacityProfile defined by parameter <i>c</i> to <i>inoutConnectionsCapacity</i>
	 *  
	 *  @param c a non-null instance of type CapacityProfile
	 */
	public void addInOutConnectionsCapacity(final CapacityProfile c) {
		if (c != null) {
			this.getInOutConnectionsCapacity().add(c);
		}
	}

	/**
	 * Returns the CapacityProfile at the specified position in <i>inoutConnectionsCapacity</i>
	 * 
	 * @param index index of the CapacityProfile to return
	 * @return if the index is within the bounds (0 <= bounds < this.getNumInOutConnectionsCapacity()), return the CapacityProfile at the specified position in <i>inoutConnectionsCapacity</i>, otherwise null
	 */
	public CapacityProfile getInOutConnectionsCapacityAtIdx(int index) {
		CapacityProfile rtn = null;
		if (
				(0 <= index) &&
				(index < this.getNumInOutConnectionsCapacity())) {
			rtn = this.getInOutConnectionsCapacity().get(index);
		}		
		return rtn;
	}

	/**
	 * Returns the number of CapacityProfile in <i>inoutConnectionsCapacity</i>
	 * 
	 * @return the number of CapacityProfile in <i>inoutConnectionsCapacity</i>
	 */
	public int getNumInOutConnectionsCapacity() {
		return this.getInOutConnectionsCapacity().size();
	}
	
	/**
	 * Getter for <i>inoutConnectionsCapacity</i>
	 * @return value of <i>inoutConnectionsCapacity</i>
	*/
	public CObjectCollection<CapacityProfile> getInOutConnectionsCapacity() {
		return this.inoutConnectionsCapacity;
	}
	
	private CObjectCollection<CapacityProfile> inoutConnectionsCapacity;
	
}

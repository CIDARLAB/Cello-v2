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
package org.cellocad.v2.partitioning.profile;

import org.cellocad.v2.common.CObjectCollection;
import org.cellocad.v2.common.Utils;
import org.cellocad.v2.common.profile.ProfileObject;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 * @author Vincent Mirian
 * 
 * @date Oct 27, 2017
 *
 */
public class PartitionProfile extends ProfileObject {

	private void init(){
		blockCapacityUnits = new CObjectCollection<ProfileObject>();
		blockCapacity = new CObjectCollection<CapacityProfile>();
		interBlockCapacityUnits = new CObjectCollection<ProfileObject>();
		interBlockCapacity = new CObjectCollection<CapacityProfile>();
		blocks = new CObjectCollection<BlockProfile>();
		interBlocks = new CObjectCollection<InterBlockProfile>();
	}
	
	public PartitionProfile(final JSONObject JObj){
		super(JObj);
		init();
		this.parse(JObj);
		assert(this.isValid());
	}
	
	/*
	 * Parse
	 */
	private void parseCapacityUnits(final JSONObject JObj, final CObjectCollection<ProfileObject> CapacityUnits){
		// CapacityUnits
		JSONArray jsonArr = (JSONArray) JObj.get("Capacity_Units");
		if (jsonArr != null) {
			for (int i = 0; i < jsonArr.size(); i++){
				Object jObj = jsonArr.get(i);
				if (Utils.isString(jObj)) {
					String name = (String) jObj;
					ProfileObject unit = new ProfileObject();
					unit.setName(name);
					unit.setIdx(CapacityUnits.size());
					CapacityUnits.add(unit);				
				}
			}
		}
	}
	
	private void parseCapacity(final JSONObject JObj,
			final CObjectCollection<ProfileObject> CapacityUnits,
			final CObjectCollection<CapacityProfile> Capacity){
		// Capacity
		JSONArray jsonArr = (JSONArray) JObj.get("Capacity");
		if (jsonArr != null) {
			for (int i = 0; i < jsonArr.size(); i++){
				JSONObject jsonObj = (JSONObject) jsonArr.get(i);
				CapacityProfile capacity = new CapacityProfile(jsonObj, CapacityUnits);
				Capacity.add(capacity);
			}
		}
	}

	private void parseBlocks(final JSONObject JObj){
		// blocks
		JSONArray jsonArr = (JSONArray) JObj.get("Blocks");
		if (jsonArr == null) {
			throw new RuntimeException("Blocks not specified for " + this.getName() + ".");
		}
		for (int i = 0; i < jsonArr.size(); i++){
			JSONObject jsonObj = (JSONObject) jsonArr.get(i);
			BlockProfile BP = new BlockProfile(jsonObj, blockCapacity);
			blocks.add(BP);
		}
	}

	private void parseInterBlocks(final JSONObject JObj){
		// interBlocks
		JSONArray jsonArr = (JSONArray) JObj.get("InterBlocks");
		for (int i = 0; i < jsonArr.size(); i++){
			JSONObject jsonObj = (JSONObject) jsonArr.get(i);
			InterBlockProfile IBP = new InterBlockProfile(jsonObj, blocks, interBlockCapacity);
			interBlocks.add(IBP);
		}
	}

	private void parseBlocksInformation(final JSONObject JObj){
		// blocks
		JSONObject jsonObj = (JSONObject) JObj.get("Blocks");
		if (jsonObj == null) {
			throw new RuntimeException("Blocks not specified for " + this.getName() + ".");
		}
		// capacityUnits
		this.parseCapacityUnits(jsonObj, blockCapacityUnits);
		// capacity
		this.parseCapacity(jsonObj, blockCapacityUnits, blockCapacity);
		// blocks
		this.parseBlocks(jsonObj);
	}
	
	private void parseInterBlocksInformation(final JSONObject JObj){
		// interBlocks
		JSONObject jsonObj = (JSONObject) JObj.get("InterBlocks");
		if (jsonObj != null) {
			// capacityUnits
			this.parseCapacityUnits(jsonObj, interBlockCapacityUnits);
			// capacity
			this.parseCapacity(jsonObj, interBlockCapacityUnits, interBlockCapacity);
			// interBlocks
			this.parseInterBlocks(jsonObj);
		}
	}
		
	private void parse(final JSONObject JObj){
		// name
		// parseName(JObj);
		// blocks
		parseBlocksInformation(JObj);
		// interBlocks
		parseInterBlocksInformation(JObj);
	}

	/*
	 * CapacityUnits
	 */
	private ProfileObject getCapacityUnitsAtIdx(final CObjectCollection<ProfileObject> collection, int index) {
		ProfileObject rtn = null;
		if (
				(index >= 0) &&
				(index < this.getCapacityUnitsSize(collection))
			){
			rtn = collection.get(index);	
		} 
		return rtn;
	}
	
	private int getCapacityUnitsSize(final CObjectCollection<ProfileObject> collection) {
		int rtn = 0;
		if (collection != null) {
			rtn = collection.size();
		}
		return rtn;
	}

	/*
	 * Capacity
	 */
	private CapacityProfile getCapacityAtIdx(final CObjectCollection<CapacityProfile> collection, int index) {
		CapacityProfile rtn = null;
		if (
				(index >= 0) &&
				(index < this.getCapacitySize(collection))
			){
			rtn = collection.get(index);	
		} 
		return rtn;
	}
	
	private int getCapacitySize(final CObjectCollection<CapacityProfile> collection) {
		int rtn = 0;
		if (collection != null) {
			rtn = collection.size();
		}
		return rtn;
	}
	
	/*
	 * Block
	 */
	public BlockProfile getBlockProfileAtIdx(int index){
		BlockProfile rtn = null;
		if (
				(index >= 0) &&
				(index < this.getNumBlockProfile())
			){
			rtn = blocks.get(index);	
		}
		return rtn;
	}
	
	public int getNumBlockProfile(){
		int rtn = blocks.size();
		return rtn;
	}
	
	public CapacityProfile getBlockCapacityAtIdx(int index){
		return this.getCapacityAtIdx(this.blockCapacity, index);
	}
	
	public int getNumBlockCapacity(){
		return this.getCapacitySize(this.blockCapacity);
	}
	
	public ProfileObject getBlockCapacityUnitsAtIdx(int index){
		return this.getCapacityUnitsAtIdx(this.blockCapacityUnits, index);
	}
	
	public int getNumBlockCapacityUnits(){
		return this.getCapacityUnitsSize(this.blockCapacityUnits);
	}

	public CObjectCollection<ProfileObject> getBlockCapacityUnits(){
		return this.blockCapacityUnits;
	}
	
	/*
	 * InterBlock
	 */	
	public InterBlockProfile getInterBlockProfileAtIdx(int index){
		InterBlockProfile rtn = null;
		if (
				(index >= 0) &&
				(index < this.getNumInterBlockProfile())
			){
			rtn = interBlocks.get(index);	
		}
		return rtn;
	}
	
	public int getNumInterBlockProfile(){
		int rtn = interBlocks.size();
		return rtn;
	}
	
	public CapacityProfile getInterBlockCapacityAtIdx(int index){
		return this.getCapacityAtIdx(this.interBlockCapacity, index);
	}
	
	public int getNumInterBlockCapacity(){
		return this.getCapacitySize(this.interBlockCapacity);
	}
	
	public ProfileObject getInterBlockCapacityUnitsAtIdx(int index){
		return this.getCapacityUnitsAtIdx(this.interBlockCapacityUnits, index);
	}
	
	public int getNumInterBlockCapacityUnits(){
		return this.getCapacityUnitsSize(this.interBlockCapacityUnits);
	}
	
	public CObjectCollection<ProfileObject> getInterBlockCapacityUnits(){
		return this.interBlockCapacityUnits;
	}
	
	/*
	 * isValid
	 */	
	@Override
	public boolean isValid() {
		boolean rtn = false;
		rtn = super.isValid();
		//blocks
		for (int i = 0; (rtn) && (i < this.getNumBlockProfile()); i++) {
			BlockProfile BP = this.getBlockProfileAtIdx(i);
			// block isValid
			rtn = rtn && BP.isValid();
		}
		//interBlocks
		for (int i = 0; (rtn) && (i < this.getNumInterBlockProfile()); i++) {
			InterBlockProfile IBP = this.getInterBlockProfileAtIdx(i);
			// interblock isValid
			rtn = rtn && IBP.isValid();
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
		result = prime * result + ((blockCapacity == null) ? 0 : blockCapacity.hashCode());
		result = prime * result + ((blockCapacityUnits == null) ? 0 : blockCapacityUnits.hashCode());
		result = prime * result + ((blocks == null) ? 0 : blocks.hashCode());
		result = prime * result + ((interBlockCapacity == null) ? 0 : interBlockCapacity.hashCode());
		result = prime * result + ((interBlockCapacityUnits == null) ? 0 : interBlockCapacityUnits.hashCode());
		result = prime * result + ((interBlocks == null) ? 0 : interBlocks.hashCode());
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
		PartitionProfile other = (PartitionProfile) obj;
		if (blockCapacity == null) {
			if (other.blockCapacity != null)
				return false;
		} else if (!blockCapacity.equals(other.blockCapacity))
			return false;
		if (blockCapacityUnits == null) {
			if (other.blockCapacityUnits != null)
				return false;
		} else if (!blockCapacityUnits.equals(other.blockCapacityUnits))
			return false;
		if (blocks == null) {
			if (other.blocks != null)
				return false;
		} else if (!blocks.equals(other.blocks))
			return false;
		if (interBlockCapacity == null) {
			if (other.interBlockCapacity != null)
				return false;
		} else if (!interBlockCapacity.equals(other.interBlockCapacity))
			return false;
		if (interBlockCapacityUnits == null) {
			if (other.interBlockCapacityUnits != null)
				return false;
		} else if (!interBlockCapacityUnits.equals(other.interBlockCapacityUnits))
			return false;
		if (interBlocks == null) {
			if (other.interBlocks != null)
				return false;
		} else if (!interBlocks.equals(other.interBlocks))
			return false;
		return true;
	}

	private CObjectCollection<ProfileObject> blockCapacityUnits;
	private CObjectCollection<CapacityProfile> blockCapacity;
	private CObjectCollection<ProfileObject> interBlockCapacityUnits;
	private CObjectCollection<CapacityProfile> interBlockCapacity;
	private CObjectCollection<BlockProfile> blocks;
	private CObjectCollection<InterBlockProfile> interBlocks;
}

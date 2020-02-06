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
import org.cellocad.v2.common.profile.ProfileUtils;
import org.json.simple.JSONObject;

/**
 * @author Vincent Mirian
 * 
 * @date Nov 6, 2017
 *
 */
public class InterBlockProfile extends CapacityCollectionProfile {
	
	public InterBlockProfile(final JSONObject JObj,
			final CObjectCollection<BlockProfile> Blocks,
			final CObjectCollection<CapacityProfile> Capacity){
		super(JObj, Capacity);
		//parse
		this.parse(JObj, Blocks);
	}
	
	/*
	 * Parse
	 */
	private void parseSource(final JSONObject JObj, final CObjectCollection<BlockProfile> Blocks){
		String sourceName = ProfileUtils.getString(JObj, "source");
		if (sourceName != null) {
			BlockProfile source = Blocks.findCObjectByName(sourceName);
			if (source == null) {
    	    	throw new RuntimeException(sourceName + " not found.");
			}
			this.setSource(source);
		}
		else {
	    	throw new RuntimeException("Source not specified for " + this.getName() + ".");
		}
	}
	
	private void parseDestination(final JSONObject JObj, final CObjectCollection<BlockProfile> Blocks){
		String destinationName = ProfileUtils.getString(JObj, "destination");
		if (destinationName != null) {
			BlockProfile destination = Blocks.findCObjectByName(destinationName);
			if (destination == null) {
    	    	throw new RuntimeException(destinationName + " not found.");
			}
			this.setDestination(destination);
		}
		else {
	    	throw new RuntimeException("Destination not specified for " + this.getName() + ".");
		}
	}
	
	private void parse(final JSONObject JObj, final CObjectCollection<BlockProfile> Blocks){
		// source
		parseSource(JObj, Blocks);
		// destination
		parseDestination(JObj, Blocks);
	}

	/*
	 * Getter and Setter
	 */

	private void setSource(final BlockProfile BProfile) {
		this.source = BProfile;
	}
	
	public BlockProfile getSource() {
		return this.source;
	}
	
	private void setDestination(final BlockProfile BProfile) {
		this.destination = BProfile;
	}
	
	public BlockProfile getDestination() {
		return this.destination;
	}
	
	/*
	 * isValid
	 */
	@Override
	public boolean isValid() {
		boolean rtn = false;
		rtn = super.isValid();
		rtn = rtn && (this.getSource() != null);
		rtn = rtn && (this.getDestination() != null);
		return rtn;
	}
	
	/*
	 * HashCode
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((destination == null) ? 0 : destination.hashCode());
		result = prime * result + ((source == null) ? 0 : source.hashCode());
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
		InterBlockProfile other = (InterBlockProfile) obj;
		if (destination == null) {
			if (other.destination != null)
				return false;
		} else if (!destination.equals(other.destination))
			return false;
		if (source == null) {
			if (other.source != null)
				return false;
		} else if (!source.equals(other.source))
			return false;
		return true;
	}

	private BlockProfile source;
	private BlockProfile destination;
}

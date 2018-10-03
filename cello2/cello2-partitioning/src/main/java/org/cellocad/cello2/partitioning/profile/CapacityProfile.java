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
import org.cellocad.cello2.common.constraint.LowerBoundType;
import org.cellocad.cello2.common.constraint.UpperBoundType;
import org.cellocad.cello2.common.profile.ProfileObject;
import org.cellocad.cello2.common.profile.ProfileUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 * @author Vincent Mirian
 * 
 * @date Nov 6, 2017
 *
 */
public class CapacityProfile extends ProfileObject{
	
	private void reset() {
		this.setLowerBound(0);
		this.setLowerBoundType(LowerBoundType.GREATER_THAN_OR_EQUAL);
		this.setUpperBound(1);
		this.setUpperBoundType(UpperBoundType.LESS_THAN_OR_EQUAL);
	}

	private void init() {
		this.myCapacityUnits = new CObjectCollection<ProfileObject>();
	}
	
	CapacityProfile(final JSONObject JObj, final CObjectCollection<ProfileObject> CapacityUnits){
		super(JObj);
		init();
		reset();
		this.capacityUnits = CapacityUnits;
		// parse
		this.parse(JObj);
	}
	
	/*
	 * Parse
	 */
	private void parseUnits(final JSONObject JObj){
		JSONArray jsonArr = (JSONArray) JObj.get("units");
		if (jsonArr == null) {
	    	throw new RuntimeException("Units not specified for capacity " + this.getName());
		}
		for (int i = 0; i < jsonArr.size(); i++) {
    	    Object jsonObj = (Object) jsonArr.get(i);
    	    if (Utils.isString(jsonObj)) {
    	    	String unitName = (String) jsonObj;
    	    	ProfileObject unit = capacityUnits.findCObjectByName(unitName);
    	    	if (unit != null) {
    	    		this.addCapacityUnit(unit);
    	    	}
        	    else {
        	    	throw new RuntimeException(unitName + " not found.");
        	    }
    	    }
		}
	}
	
	private void parseLowerBound(final JSONObject JObj){
		Integer IntegerValue = ProfileUtils.getInteger(JObj, "lower_bound");
		if (IntegerValue != null) {
			int value = IntegerValue.intValue();
			this.setLowerBound(value);
		}
	}
	
	private void parseLowerBoundType(final JSONObject JObj){
		String StringValue = ProfileUtils.getString(JObj, "lower_bound_type");
		if (StringValue != null) {
			LowerBoundType boundType = LowerBoundType.getBoundType(StringValue);
			if (boundType != null) {
				this.setLowerBoundType(boundType);
			}
		}
	}
	
	private void parseUpperBound(final JSONObject JObj){
		Integer IntegerValue = ProfileUtils.getInteger(JObj, "upper_bound");
		if (IntegerValue == null) {
	    	throw new RuntimeException("UpperBound not specified for capacity " + this.getName());
		}
		if (IntegerValue != null) {
			int value = IntegerValue.intValue();
			this.setUpperBound(value);
		}
	}
	
	private void parseUpperBoundType(final JSONObject JObj){
		String StringValue = ProfileUtils.getString(JObj, "upper_bound_type");
		if (StringValue != null) {
			UpperBoundType boundType = UpperBoundType.getBoundType(StringValue);
			if (boundType != null) {
				this.setUpperBoundType(boundType);
			}
		}
	}
	
	private void parse(final JSONObject JObj){
		// name
		// parseName(JObj);
		// units
		parseUnits(JObj);
		// lowerBound
		parseLowerBound(JObj);
		// lowerBoundType
		parseLowerBoundType(JObj);
		// upperBound
		parseUpperBound(JObj);
		// UpperBoundType
		parseUpperBoundType(JObj);
	}

	/*
	 * capacityUnits
	 */
	private void addCapacityUnit(final ProfileObject pObject) {
		if (pObject != null) {
			this.myCapacityUnits.add(pObject);
		}
	}

	public ProfileObject getCapacityUnitAtIdx(int index) {
		ProfileObject rtn = null;
		if (
				(0 <= index) &&
				(index < this.getNumCapacityUnit())) {
			rtn = this.myCapacityUnits.get(index);
		}		
		return rtn;
	}
	
	public int getNumCapacityUnit() {
		return this.myCapacityUnits.size();
	}
	
	public CObjectCollection<ProfileObject> getCapacityUnits() {
		return this.myCapacityUnits;
	}
	
	public CObjectCollection<ProfileObject> getAllCapacityUnits() {
		return this.capacityUnits;
	}

	/*
	 * Getter and Setter
	 */
	private void reduce() {
		if (this.getLowerBoundType() == LowerBoundType.GREATER_THAN) {
			this.setLowerBoundType(LowerBoundType.GREATER_THAN_OR_EQUAL);
			this.setLowerBound(this.getLowerBound() + 1);
		}
		if (this.getUpperBoundType() == UpperBoundType.LESS_THAN_OR_EQUAL) {
			this.setUpperBoundType(UpperBoundType.LESS_THAN);
			this.setUpperBound(this.getUpperBound() + 1);
		}
	}
	
	private void setLowerBound(int lowerBound) {
		this.lowerBound = lowerBound;
		this.reduce();
	}
	
	public int getLowerBound() {
		return this.lowerBound;
	}
	
	private void setLowerBoundType(final LowerBoundType type) {
		this.lowerBoundType = type;
	}
	
	public LowerBoundType getLowerBoundType() {
		return this.lowerBoundType;
	}
		
	private void setUpperBound(int upperBound) {
		this.upperBound = upperBound;
	}
	
	public int getUpperBound() {
		return this.upperBound;
	}
	
	private void setUpperBoundType(final UpperBoundType type) {
		this.upperBoundType = type;
		this.reduce();
	}
	
	public UpperBoundType getUpperBoundType() {
		return this.upperBoundType;
	}

	/*
	 * isValid
	 */
	@Override
	public boolean isValid() {
		boolean rtn = false;
		rtn = super.isValid();
		for (int i = 0; (rtn) && (i < this.getNumCapacityUnit()); i++) {
			ProfileObject PObj = this.getCapacityUnitAtIdx(i);
			rtn = rtn && PObj.isValid();
		}
		rtn = rtn && (this.getLowerBound() < this.getUpperBound());
		rtn = rtn && (this.getLowerBoundType() == LowerBoundType.GREATER_THAN_OR_EQUAL);
		rtn = rtn && (this.getUpperBoundType() == UpperBoundType.LESS_THAN);
		return rtn;
	}
	

	/*
	 * HashCode
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((capacityUnits == null) ? 0 : capacityUnits.hashCode());
		result = prime * result + lowerBound;
		result = prime * result + ((lowerBoundType == null) ? 0 : lowerBoundType.hashCode());
		result = prime * result + ((myCapacityUnits == null) ? 0 : myCapacityUnits.hashCode());
		result = prime * result + upperBound;
		result = prime * result + ((upperBoundType == null) ? 0 : upperBoundType.hashCode());
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
		CapacityProfile other = (CapacityProfile) obj;
		if (capacityUnits == null) {
			if (other.capacityUnits != null)
				return false;
		} else if (!capacityUnits.equals(other.capacityUnits))
			return false;
		if (lowerBound != other.lowerBound)
			return false;
		if (lowerBoundType != other.lowerBoundType)
			return false;
		if (myCapacityUnits == null) {
			if (other.myCapacityUnits != null)
				return false;
		} else if (!myCapacityUnits.equals(other.myCapacityUnits))
			return false;
		if (upperBound != other.upperBound)
			return false;
		if (upperBoundType != other.upperBoundType)
			return false;
		return true;
	}

	private final CObjectCollection<ProfileObject> capacityUnits;
	private CObjectCollection<ProfileObject> myCapacityUnits;
	private int lowerBound;
	private LowerBoundType lowerBoundType;
	private int upperBound;
	private UpperBoundType upperBoundType;
}

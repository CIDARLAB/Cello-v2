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
package org.cellocad.cello2.common.constraint;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.cellocad.cello2.common.CObject;
import org.cellocad.cello2.common.CObjectCollection;
import org.cellocad.cello2.common.Utils;

/**
 * @author Vincent Mirian
 * 
 * @date Nov 7, 2017
 *
 */
public class Weight extends CObject {

	private void init() {
		weightMap = new HashMap<String, Integer>();
		myUnits = new Units();
	}
	
	public Weight() {
		super();
		init();
		this.setTotal(0);
	}
	
	public Weight(final Weight other) {
		super(other);
		init();
		weightMap = new HashMap<String, Integer>(other.weightMap);
		myUnits = new Units(other.myUnits);
		this.setTotal(other.getTotal());
	}
	
	public Weight(final CObjectCollection<CObject> units, final CObjectCollection<CObject> allUnits) {
		this();
		init();
		this.getUnits().resetWithCObject(units, allUnits);
	}
	
	/*
	 * units
	 */
	public Units getUnits() {
		return this.myUnits;
	}
	
	public void resetUnits(final CObjectCollection<CObject> allUnits) {
		CObjectCollection<CObject> units = new CObjectCollection<CObject>();
		Iterator<Map.Entry<String, Integer>> it = this.weightMap.entrySet().iterator();
		CObject cObj = null;
		while (it.hasNext()) {
		    Map.Entry<String, Integer> pair = it.next();
		    cObj = new CObject();
		    cObj.setName(pair.getKey());
		    units.add(cObj);
		}
		this.getUnits().resetWithCObject(units, allUnits);
	}

	
	/*
	 * weights
	 */
	public void setWeight(final String Unit, int value) {
		this.weightMap.put(Unit, new Integer(value));
	}
	
	public void setWeight(final String Unit, Integer value) {
		this.weightMap.put(Unit, new Integer(value));
	}
	
	public Integer getWeight(final String Unit) {
		Integer rtn = null;
		rtn = this.weightMap.get(Unit);
		return rtn;
	}
	
	public void removeWeight(final String Unit) {
		this.weightMap.remove(Unit);
	}
	
	public void addWeight(final String Unit) {
		this.setWeight(Unit, 0);
	}

	public void incWeight(final String Unit) {
		this.incWeight(Unit, 1);
	}

	public void incWeight(final String Unit, int value) {
		Integer amount = this.getWeight(Unit);
		if (amount == null) {
			this.addWeight(Unit);
			amount = this.getWeight(Unit);
		}
		amount = new Integer(amount.intValue() + 1);
		this.setWeight(Unit, amount);
		this.incTotal(value);
	}
	
	public void decWeight(final String Unit) {
		this.decWeight(Unit, 1);
	}
	
	public void decWeight(final String Unit, int value) {
		Integer amount = this.getWeight(Unit);
		if (amount == null) {
			this.addWeight(Unit);
			amount = this.getWeight(Unit);
		}
		amount = new Integer(amount.intValue() - 1);
		this.setWeight(Unit, amount);
		this.decTotal(value);
	}
	
	/*
	 * total
	 */

	public void dec(final Weight wObj) {
		boolean valid = false;
		valid = (this.isValid() && wObj.isValid());
		valid = valid && (this.getUnits().doUnitsExist(wObj.getUnits()));
		if (valid) {
			this.decTotal(wObj.getTotal());
		}
	}

	public void inc(final Weight wObj) {
		boolean valid = false;
		valid = (this.isValid() && wObj.isValid());
		valid = valid && (this.getUnits().doUnitsExist(wObj.getUnits()));
		if (valid) {
			this.incTotal(wObj.getTotal());
		}
	}
	
	public int getTotal() {
		return this.total;
	}
	
	private void setTotal(int total) {
		this.total = total;
	}
	
	private void incTotal(int value) {
		this.setTotal(this.getTotal() + value);
	}
	
	private void decTotal(int value) {
		this.setTotal(this.getTotal() - value);
	}
	
	/*
	 * isValid
	 */
	@Override
	public boolean isValid() {
		boolean rtn = false;
		rtn = super.isValid();
		rtn = rtn && (this.getTotal() >= 0);
		rtn = rtn && (this.getUnits().isValid());
		return rtn;
	}
	
	/*
	 * HashCode
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + total;
		result = prime * result + ((weightMap == null) ? 0 : weightMap.hashCode());
		result = prime * result + ((myUnits == null) ? 0 : myUnits.hashCode());
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
		Weight other = (Weight) obj;
		if (total != other.total)
			return false;
		if (weightMap == null) {
			if (other.weightMap != null)
				return false;
		} else if (!weightMap.equals(other.weightMap))
			return false;
		if (myUnits == null) {
			if (other.myUnits != null)
				return false;
		} else if (!myUnits.equals(other.myUnits))
			return false;
		return true;
	}

	/*
	 * toString
	 */
	protected String getWeightsToString() {
		String rtn = "";
		Iterator<Map.Entry<String, Integer>> it = this.weightMap.entrySet().iterator();
		while (it.hasNext()) {
		    Map.Entry<String, Integer> pair = it.next();
		    rtn = rtn + Utils.getTabCharacterRepeat(2);
		    rtn = rtn + pair.getValue();
		    rtn = rtn + " ";
			rtn = rtn + pair.getKey();
			rtn = rtn + ",";
			rtn = rtn + Utils.getNewLine();
		}
		return rtn;
	}
	
	@Override
	public String toString() {
		String rtn = "";
		String superStr = "";
		rtn = rtn + "[ ";
		rtn = rtn + Utils.getNewLine();
		// weights
		rtn = rtn + Utils.getTabCharacter();
		rtn = rtn + "weights = ";
		rtn = rtn + Utils.getNewLine();
		rtn = rtn + Utils.getTabCharacter();
		rtn = rtn + "{";
		rtn = rtn + Utils.getNewLine();
		rtn = rtn + this.getWeightsToString();
		rtn = rtn + Utils.getTabCharacter();
		rtn = rtn + "}";
		rtn = rtn + Utils.getNewLine();
		// myUnits
		rtn = rtn + this.getEntryToString("myUnits", this.getUnits().getUnitsToString());
		// total
		rtn = rtn + this.getEntryToString("total", this.getTotal());
		// toString
		rtn = rtn + Utils.getTabCharacter();
		rtn = rtn + "toString() = ";
		rtn = rtn + Utils.getNewLine();
		superStr = super.toString();
		superStr = Utils.addIndent(1, superStr);
		rtn = rtn + superStr;
		rtn = rtn + ",";
		rtn = rtn + Utils.getNewLine();
		// end
		rtn = rtn + "]";
		return rtn;
	}

	private Map<String, Integer> weightMap;
	private Units myUnits;
	private int total;
}

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

import org.cellocad.cello2.common.CObject;
import org.cellocad.cello2.common.CObjectCollection;
import org.cellocad.cello2.common.Utils;
import org.cellocad.cello2.common.constraint.EvaluateResult;
import org.cellocad.cello2.common.constraint.LowerBoundType;
import org.cellocad.cello2.common.constraint.Units;
import org.cellocad.cello2.common.constraint.UpperBoundType;
import org.cellocad.cello2.common.constraint.Weight;
import org.cellocad.cello2.common.profile.DerivedProfile;

/**
 * @author Vincent Mirian
 * 
 * @date Nov 7, 2017
 *
 */

// TODO: Capacity is a constraint, make constraint engine 
public class Capacity extends DerivedProfile<CapacityProfile> {
	
	public Capacity(final CapacityProfile CP, final CObjectCollection<CObject> capacityUnits) {
		super(CP);
		Utils.isNullRuntimeException(capacityUnits, "CapacityUnits");
		this.setLowerBound(CP.getLowerBound());
		this.setLowerBoundType(CP.getLowerBoundType());
		this.setUpperBound(CP.getUpperBound());
		this.setUpperBoundType(CP.getUpperBoundType());
		myUnits = new Units(CP.getCapacityUnits(), capacityUnits);
	}
	
	/*
	 * units
	 */
	private Units getUnits() {
		return this.myUnits;
	}
	
	/*
	 * Evaluate
	 */
	private EvaluateResult evaluate(final Weight wObj) {
		EvaluateResult rtn = EvaluateResult.ERROR;
		boolean valid = false;
		valid = (this.isValid() && wObj.isValid());
		valid = valid && (this.getUnits().doUnitsExist(wObj.getUnits()));
		if (valid) {
			int total = wObj.getTotal();			
			if (total < this.getLowerBound()) {
				rtn = EvaluateResult.UNDERFLOW;
			}
			else if (this.getUpperBound() <= total) {
				rtn = EvaluateResult.OVERFLOW;
			}
			else {
				rtn = EvaluateResult.OK;
			}
		}
		return rtn;
	}
	
	public boolean canFit (final Weight wObj) {
		boolean rtn = false;
		EvaluateResult result = this.evaluate(wObj);
		rtn = (result == EvaluateResult.OK);
		return rtn;
	}

	public boolean isOverflow (final Weight wObj) {
		boolean rtn = false;
		EvaluateResult result = this.evaluate(wObj);
		rtn = (result == EvaluateResult.OVERFLOW);
		return rtn;
	}

	public boolean isUnderflow (final Weight wObj) {
		boolean rtn = false;
		EvaluateResult result = this.evaluate(wObj);
		rtn = (result == EvaluateResult.UNDERFLOW);
		return rtn;
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
	}
	
	public int getLowerBound() {
		return this.lowerBound;
	}
	
	private void setLowerBoundType(final LowerBoundType type) {
		this.lowerBoundType = type;
		this.reduce();
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
		rtn = rtn && (this.getLowerBound() < this.getUpperBound());
		rtn = rtn && (this.getLowerBoundType() == LowerBoundType.GREATER_THAN_OR_EQUAL);
		rtn = rtn && (this.getUpperBoundType() == UpperBoundType.LESS_THAN);
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
		result = prime * result + lowerBound;
		result = prime * result + ((lowerBoundType == null) ? 0 : lowerBoundType.hashCode());
		result = prime * result + upperBound;
		result = prime * result + ((upperBoundType == null) ? 0 : upperBoundType.hashCode());
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
		Capacity other = (Capacity) obj;
		if (lowerBound != other.lowerBound)
			return false;
		if (lowerBoundType != other.lowerBoundType)
			return false;
		if (upperBound != other.upperBound)
			return false;
		if (upperBoundType != other.upperBoundType)
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
	@Override
	public String toString() {
		String rtn = "";
		String superStr = "";
		rtn = rtn + "[ ";
		rtn = rtn + Utils.getNewLine();
		// name
		rtn = rtn + this.getEntryToString("name", this.getName());
		// equation
		rtn = rtn + Utils.getTabCharacter();
		rtn = rtn + "Equation: ";
		rtn = rtn + this.getLowerBound();
		rtn = rtn + " ";
		rtn = rtn + this.getLowerBoundType().toString();
		rtn = rtn + this.getUnits().getUnitsToString();
		rtn = rtn + " ";
		rtn = rtn + this.getUpperBoundType().toString();
		rtn = rtn + " ";
		rtn = rtn + this.getUpperBound();
		rtn = rtn + Utils.getNewLine();	
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

	private int lowerBound;
	private LowerBoundType lowerBoundType;
	private int upperBound;
	private UpperBoundType upperBoundType;
	private Units myUnits;
}

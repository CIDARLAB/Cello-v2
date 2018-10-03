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

import java.util.Arrays;

import org.cellocad.cello2.common.CObject;
import org.cellocad.cello2.common.CObjectCollection;
import org.cellocad.cello2.common.Utils;
import org.cellocad.cello2.common.profile.ProfileObject;

/**
 * @author Vincent Mirian
 * 
 * @date Nov 7, 2017
 *
 */

/*
 * Mapping of valid units from all potential units
 */
public class Units extends CObject {

	private void initUnitsEnables(int size) {
		this.UnitsEnables = new boolean[size];
	}

	private void copyUnitsEnables(final boolean[] array) {
		this.UnitsEnables = Arrays.copyOf(array, array.length);
	}
	
	public Units() {
		super();
		this.disableUnitsEnabled();
		this.initUnitsEnables(0);
	}
	
	public Units(Units other) {
		super(other);
		Utils.isNullRuntimeException(other, "Other");
		this.setUnitsEnabled(other.getUnitsEnabled());
		this.copyUnitsEnables(other.UnitsEnables);
		this.setProfileUnits(other.getProfileUnits());
	}
	
	public Units(final CObjectCollection<ProfileObject> units, final CObjectCollection<CObject> allUnits) {
		this();
		resetWithProfileObject(units, allUnits);
	}
	
	public void resetWithProfileObject(final CObjectCollection<ProfileObject> units, final CObjectCollection<CObject> allUnits) {
		Utils.isNullRuntimeException(units, "Units");
		CObjectCollection<CObject> unitsTemp = new CObjectCollection<CObject>();
		ProfileObject PObj = null;
		CObject cObj = null;
		for (int i = 0; i < units.size(); i ++ ) {
			PObj = units.get(i);
			cObj = new CObject(PObj);
			unitsTemp.add(cObj);
		}
		this.resetWithCObject(unitsTemp, allUnits);
	}
	
	public void resetWithCObject(final CObjectCollection<CObject> units, final CObjectCollection<CObject> allUnits) {
		Utils.isNullRuntimeException(units, "Units");
		Utils.isNullRuntimeException(allUnits, "AllUnits");
		this.setProfileUnits(allUnits);
		int size = allUnits.size();
		this.initUnitsEnables(size);
		for (int i = 0; i < units.size(); i++ ) {
			String unitName = units.get(i).getName();
			CObject unit = allUnits.findCObjectByName(unitName);
			if (unit == null) {
				throw new RuntimeException(unitName + "does not exist.");				
			}
			int loc = unit.getIdx();
			if (size <= loc) {
				throw new RuntimeException("Invalid index for unit: " + unit.getName() + ".");
			}
			this.UnitsEnables[loc] = true;
		}
		this.enableUnitsEnabled();
	}
	
	/*
	 * Units
	 */
	protected CObjectCollection<CObject> getProfileUnits() {
		return this.allUnits;
	}

	private void setProfileUnits(CObjectCollection<CObject> units) {
		this.allUnits = units;
	}
	
	private boolean getUnitsEnabled() {
		return this.UnitsEnabled;
	}

	private void setUnitsEnabled(boolean b) {
		this.UnitsEnabled = b;
	}

	private void enableUnitsEnabled() {
		this.setUnitsEnabled(true);
	}
	
	private void disableUnitsEnabled() {
		this.setUnitsEnabled(false);
	}

	/*
	 * doUnitsAlign
	 */
	public boolean doUnitsAlign(Units other) {
		boolean rtn = false;
		int size = this.UnitsEnables.length;
		rtn = (size == other.UnitsEnables.length) && (UnitsEnabled) && (other.UnitsEnabled);
		for (int i = 0; (rtn) && (i < size); i++) {
			boolean result = this.UnitsEnables[i] ^ other.UnitsEnables[i];
			result = (result == false);
			rtn = rtn && result;
		}
		return rtn;
	}

	/*
	 * doUnitsAlign
	 */
	public boolean doUnitsExist(Units other) {
		boolean rtn = false;
		int size = this.UnitsEnables.length;
		rtn = (size == other.UnitsEnables.length) && (UnitsEnabled) && (other.UnitsEnabled);
		for (int i = 0; (rtn) && (i < size); i++) {
			if (other.UnitsEnables[i]) {
				rtn = rtn && (this.UnitsEnables[i]);
			}
		}
		return rtn;
	}
	
	/*
	 * isValid
	 */
	@Override
	public boolean isValid() {
		boolean rtn = false;
		boolean state = false;
		rtn = super.isValid();
		state = (this.getUnitsEnabled()) && (this.UnitsEnables.length > 0);
		state = state || ((!this.getUnitsEnabled()) && (this.UnitsEnables.length == 0));
		rtn = rtn && state;
		return rtn;
	}
		
	/*
	 * HashCode
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + (UnitsEnabled ? 1231 : 1237);
		result = prime * result + Arrays.hashCode(UnitsEnables);
		result = prime * result + ((allUnits == null) ? 0 : allUnits.hashCode());
		return result;
	}

	/*
	 * Equals
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		Units other = (Units) obj;
		if (UnitsEnabled != other.UnitsEnabled)
			return false;
		if (!Arrays.equals(UnitsEnables, other.UnitsEnables))
			return false;
		if (allUnits == null) {
			if (other.allUnits != null)
				return false;
		} else if (!allUnits.equals(other.allUnits))
			return false;
		return true;
	}

	/*
	 * toString
	 */
	public String getUnitsToString() {
		String rtn = "";
		if (this.getUnitsEnabled()) {
			for (int i = 0; i < this.UnitsEnables.length; i++) {
				if (UnitsEnables[i] == false) {
					continue;
				}
				CObject PObj = allUnits.findCObjectByIdx(i);
				assert(PObj != null);
				rtn = rtn + " ";
				rtn = rtn + PObj.getName();
				rtn = rtn + ",";
			}
		}
		return rtn;
	}
	
	@Override
	public String toString() {
		String rtn = "";
		String superStr = "";
		rtn = rtn + "[ ";
		rtn = rtn + Utils.getNewLine();
		// units
		rtn = rtn + Utils.getTabCharacter();
		rtn = rtn + "myUnits = ";
		rtn = rtn + "{";
		rtn = rtn + this.getUnitsToString();
		rtn = rtn + " ";
		rtn = rtn + "}";
		rtn = rtn + Utils.getNewLine();
		// UnitsEnabled
		rtn = rtn + this.getEntryToString("UnitsEnabled", this.getUnitsEnabled());
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

	boolean UnitsEnabled;
	private boolean[] UnitsEnables;
	private CObjectCollection<CObject> allUnits;
}

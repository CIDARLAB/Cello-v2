/*
 * Copyright (C) 2017 Massachusetts Institute of Technology (MIT)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package org.cellocad.v2.common.constraint;

import java.util.Arrays;
import org.cellocad.v2.common.CObject;
import org.cellocad.v2.common.CObjectCollection;
import org.cellocad.v2.common.Utils;
import org.cellocad.v2.common.profile.ProfileObject;

/**
 * Units.
 * 
 * @author Vincent Mirian
 *
 * @date Nov 7, 2017
 */

/*
 * Mapping of valid units from all potential units
 */
public class Units extends CObject {

  private void initUnitsEnables(final int size) {
    unitsEnables = new boolean[size];
  }

  private void copyUnitsEnables(final boolean[] array) {
    unitsEnables = Arrays.copyOf(array, array.length);
  }

  /**
   * Initializes a newly created {@link Units}.
   */
  public Units() {
    super();
    disableUnitsEnabled();
    initUnitsEnables(0);
  }

  /**
   * Initializes a newly created {@link Units}.
   *
   * @param other Other.
   */
  public Units(final Units other) {
    super(other);
    Utils.isNullRuntimeException(other, "Other");
    setUnitsEnabled(other.getUnitsEnabled());
    copyUnitsEnables(other.unitsEnables);
    setProfileUnits(other.getProfileUnits());
  }

  public Units(final CObjectCollection<ProfileObject> units,
      final CObjectCollection<CObject> allUnits) {
    this();
    resetWithProfileObject(units, allUnits);
  }

  /**
   * Resets this instance with a profile object.
   * 
   * @param units    Units.
   * @param allUnits All units.
   */
  public void resetWithProfileObject(final CObjectCollection<ProfileObject> units,
      final CObjectCollection<CObject> allUnits) {
    Utils.isNullRuntimeException(units, "Units");
    final CObjectCollection<CObject> unitsTemp = new CObjectCollection<>();
    ProfileObject profileObj = null;
    CObject obj = null;
    for (int i = 0; i < units.size(); i++) {
      profileObj = units.get(i);
      obj = new CObject(profileObj);
      unitsTemp.add(obj);
    }
    resetWithCObject(unitsTemp, allUnits);
  }

  /**
   * Resets with {@link CObject}.
   * 
   * @param units    Units.
   * @param allUnits All units.
   */
  public void resetWithCObject(final CObjectCollection<CObject> units,
      final CObjectCollection<CObject> allUnits) {
    Utils.isNullRuntimeException(units, "Units");
    Utils.isNullRuntimeException(allUnits, "AllUnits");
    setProfileUnits(allUnits);
    final int size = allUnits.size();
    initUnitsEnables(size);
    for (int i = 0; i < units.size(); i++) {
      final String unitName = units.get(i).getName();
      final CObject unit = allUnits.findCObjectByName(unitName);
      if (unit == null) {
        throw new RuntimeException(unitName + "does not exist.");
      }
      final int loc = unit.getIdx();
      if (size <= loc) {
        throw new RuntimeException("Invalid index for unit: " + unit.getName() + ".");
      }
      unitsEnables[loc] = true;
    }
    enableUnitsEnabled();
  }

  /*
   * Units
   */
  protected CObjectCollection<CObject> getProfileUnits() {
    return allUnits;
  }

  private void setProfileUnits(final CObjectCollection<CObject> units) {
    allUnits = units;
  }

  private boolean getUnitsEnabled() {
    return unitsEnabled;
  }

  private void setUnitsEnabled(final boolean b) {
    unitsEnabled = b;
  }

  private void enableUnitsEnabled() {
    setUnitsEnabled(true);
  }

  private void disableUnitsEnabled() {
    setUnitsEnabled(false);
  }

  /*
   * doUnitsAlign
   */
  /**
   * Check if these units align with others.
   * 
   * @param other Another {@link Units} object.
   * @return Whether units align.
   */
  public boolean doUnitsAlign(final Units other) {
    boolean rtn = false;
    final int size = unitsEnables.length;
    rtn = size == other.unitsEnables.length && unitsEnabled && other.unitsEnabled;
    for (int i = 0; rtn && i < size; i++) {
      boolean result = unitsEnables[i] ^ other.unitsEnables[i];
      result = result == false;
      rtn = rtn && result;
    }
    return rtn;
  }

  /*
   * doUnitsAlign
   */
  /**
   * Check existence.
   * 
   * @param other Other units.
   * @return Existence flag.
   */
  public boolean doUnitsExist(final Units other) {
    boolean rtn = false;
    final int size = unitsEnables.length;
    rtn = size == other.unitsEnables.length && unitsEnabled && other.unitsEnabled;
    for (int i = 0; rtn && i < size; i++) {
      if (other.unitsEnables[i]) {
        rtn = rtn && unitsEnables[i];
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
    state = getUnitsEnabled() && unitsEnables.length > 0;
    state = state || !getUnitsEnabled() && unitsEnables.length == 0;
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
    result = prime * result + (unitsEnabled ? 1231 : 1237);
    result = prime * result + Arrays.hashCode(unitsEnables);
    result = prime * result + (allUnits == null ? 0 : allUnits.hashCode());
    return result;
  }

  /*
   * Equals
   */
  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (!super.equals(obj)) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final Units other = (Units) obj;
    if (unitsEnabled != other.unitsEnabled) {
      return false;
    }
    if (!Arrays.equals(unitsEnables, other.unitsEnables)) {
      return false;
    }
    if (allUnits == null) {
      if (other.allUnits != null) {
        return false;
      }
    } else if (!allUnits.equals(other.allUnits)) {
      return false;
    }
    return true;
  }

  /*
   * toString
   */
  /**
   * Get a string representation of a {@link Units} instance.
   * 
   * @return The string representation.
   */
  public String getUnitsToString() {
    String rtn = "";
    if (getUnitsEnabled()) {
      for (int i = 0; i < unitsEnables.length; i++) {
        if (unitsEnables[i] == false) {
          continue;
        }
        final CObject PObj = allUnits.findCObjectByIdx(i);
        assert PObj != null;
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
    rtn = rtn + "[ ";
    rtn = rtn + Utils.getNewLine();
    // units
    rtn = rtn + Utils.getTabCharacter();
    rtn = rtn + "myUnits = ";
    rtn = rtn + "{";
    rtn = rtn + getUnitsToString();
    rtn = rtn + " ";
    rtn = rtn + "}";
    rtn = rtn + Utils.getNewLine();
    // UnitsEnabled
    rtn = rtn + this.getEntryToString("UnitsEnabled", getUnitsEnabled());
    // toString
    rtn = rtn + Utils.getTabCharacter();
    rtn = rtn + "toString() = ";
    rtn = rtn + Utils.getNewLine();
    String superStr = "";
    superStr = super.toString();
    superStr = Utils.addIndent(1, superStr);
    rtn = rtn + superStr;
    rtn = rtn + ",";
    rtn = rtn + Utils.getNewLine();
    // end
    rtn = rtn + "]";
    return rtn;
  }

  boolean unitsEnabled;
  private boolean[] unitsEnables;
  private CObjectCollection<CObject> allUnits;

}

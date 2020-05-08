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

package org.cellocad.v2.partitioning.profile;

import org.cellocad.v2.common.CObjectCollection;
import org.cellocad.v2.common.Utils;
import org.cellocad.v2.common.constraint.LowerBoundType;
import org.cellocad.v2.common.constraint.UpperBoundType;
import org.cellocad.v2.common.profile.ProfileObject;
import org.cellocad.v2.common.profile.ProfileUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 * A capacity profile.
 *
 * @author Vincent Mirian
 * @date Nov 6, 2017
 */
public class CapacityProfile extends ProfileObject {

  private void reset() {
    setLowerBound(0);
    setLowerBoundType(LowerBoundType.GREATER_THAN_OR_EQUAL);
    setUpperBound(1);
    setUpperBoundType(UpperBoundType.LESS_THAN_OR_EQUAL);
  }

  private void init() {
    myCapacityUnits = new CObjectCollection<>();
  }

  CapacityProfile(final JSONObject jsnObj, final CObjectCollection<ProfileObject> capacityUnits) {
    super(jsnObj);
    init();
    reset();
    this.capacityUnits = capacityUnits;
    // parse
    parse(jsnObj);
  }

  /*
   * Parse
   */
  private void parseUnits(final JSONObject jsonObj) {
    final JSONArray jsonArr = (JSONArray) jsonObj.get("units");
    if (jsonArr == null) {
      throw new RuntimeException("Units not specified for capacity " + getName());
    }
    for (int i = 0; i < jsonArr.size(); i++) {
      final Object unitsObj = jsonArr.get(i);
      if (Utils.isString(unitsObj)) {
        final String unitName = (String) unitsObj;
        final ProfileObject unit = capacityUnits.findCObjectByName(unitName);
        if (unit != null) {
          addCapacityUnit(unit);
        } else {
          throw new RuntimeException(unitName + " not found.");
        }
      }
    }
  }

  private void parseLowerBound(final JSONObject jsonObj) {
    final Integer IntegerValue = ProfileUtils.getInteger(jsonObj, "lower_bound");
    if (IntegerValue != null) {
      final int value = IntegerValue.intValue();
      setLowerBound(value);
    }
  }

  private void parseLowerBoundType(final JSONObject jsonObj) {
    final String StringValue = ProfileUtils.getString(jsonObj, "lower_bound_type");
    if (StringValue != null) {
      final LowerBoundType boundType = LowerBoundType.getBoundType(StringValue);
      if (boundType != null) {
        setLowerBoundType(boundType);
      }
    }
  }

  private void parseUpperBound(final JSONObject jsonObj) {
    final Integer IntegerValue = ProfileUtils.getInteger(jsonObj, "upper_bound");
    if (IntegerValue == null) {
      throw new RuntimeException("UpperBound not specified for capacity " + getName());
    }
    if (IntegerValue != null) {
      final int value = IntegerValue.intValue();
      setUpperBound(value);
    }
  }

  private void parseUpperBoundType(final JSONObject jsonObj) {
    final String StringValue = ProfileUtils.getString(jsonObj, "upper_bound_type");
    if (StringValue != null) {
      final UpperBoundType boundType = UpperBoundType.getBoundType(StringValue);
      if (boundType != null) {
        setUpperBoundType(boundType);
      }
    }
  }

  private void parse(final JSONObject jsonObj) {
    // name
    // parseName(JObj);
    // units
    parseUnits(jsonObj);
    // lowerBound
    parseLowerBound(jsonObj);
    // lowerBoundType
    parseLowerBoundType(jsonObj);
    // upperBound
    parseUpperBound(jsonObj);
    // UpperBoundType
    parseUpperBoundType(jsonObj);
  }

  /*
   * capacityUnits
   */
  private void addCapacityUnit(final ProfileObject pObject) {
    if (pObject != null) {
      myCapacityUnits.add(pObject);
    }
  }

  /**
   * Get the capacity unit at the given index.
   *
   * @param index An index.
   * @return The capacity unit at the given index.
   */
  public ProfileObject getCapacityUnitAtIdx(final int index) {
    ProfileObject rtn = null;
    if (0 <= index && index < getNumCapacityUnit()) {
      rtn = myCapacityUnits.get(index);
    }
    return rtn;
  }

  public int getNumCapacityUnit() {
    return myCapacityUnits.size();
  }

  public CObjectCollection<ProfileObject> getCapacityUnits() {
    return myCapacityUnits;
  }

  public CObjectCollection<ProfileObject> getAllCapacityUnits() {
    return capacityUnits;
  }

  /*
   * Getter and Setter
   */
  private void reduce() {
    if (getLowerBoundType() == LowerBoundType.GREATER_THAN) {
      setLowerBoundType(LowerBoundType.GREATER_THAN_OR_EQUAL);
      setLowerBound(getLowerBound() + 1);
    }
    if (getUpperBoundType() == UpperBoundType.LESS_THAN_OR_EQUAL) {
      setUpperBoundType(UpperBoundType.LESS_THAN);
      setUpperBound(getUpperBound() + 1);
    }
  }

  private void setLowerBound(final int lowerBound) {
    this.lowerBound = lowerBound;
    reduce();
  }

  public int getLowerBound() {
    return lowerBound;
  }

  private void setLowerBoundType(final LowerBoundType type) {
    lowerBoundType = type;
  }

  public LowerBoundType getLowerBoundType() {
    return lowerBoundType;
  }

  private void setUpperBound(final int upperBound) {
    this.upperBound = upperBound;
  }

  public int getUpperBound() {
    return upperBound;
  }

  private void setUpperBoundType(final UpperBoundType type) {
    upperBoundType = type;
    reduce();
  }

  public UpperBoundType getUpperBoundType() {
    return upperBoundType;
  }

  /*
   * isValid
   */
  @Override
  public boolean isValid() {
    boolean rtn = false;
    rtn = super.isValid();
    for (int i = 0; rtn && i < getNumCapacityUnit(); i++) {
      final ProfileObject PObj = getCapacityUnitAtIdx(i);
      rtn = rtn && PObj.isValid();
    }
    rtn = rtn && getLowerBound() < getUpperBound();
    rtn = rtn && getLowerBoundType() == LowerBoundType.GREATER_THAN_OR_EQUAL;
    rtn = rtn && getUpperBoundType() == UpperBoundType.LESS_THAN;
    return rtn;
  }

  /*
   * HashCode
   */
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + (capacityUnits == null ? 0 : capacityUnits.hashCode());
    result = prime * result + lowerBound;
    result = prime * result + (lowerBoundType == null ? 0 : lowerBoundType.hashCode());
    result = prime * result + (myCapacityUnits == null ? 0 : myCapacityUnits.hashCode());
    result = prime * result + upperBound;
    result = prime * result + (upperBoundType == null ? 0 : upperBoundType.hashCode());
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
    final CapacityProfile other = (CapacityProfile) obj;
    if (capacityUnits == null) {
      if (other.capacityUnits != null) {
        return false;
      }
    } else if (!capacityUnits.equals(other.capacityUnits)) {
      return false;
    }
    if (lowerBound != other.lowerBound) {
      return false;
    }
    if (lowerBoundType != other.lowerBoundType) {
      return false;
    }
    if (myCapacityUnits == null) {
      if (other.myCapacityUnits != null) {
        return false;
      }
    } else if (!myCapacityUnits.equals(other.myCapacityUnits)) {
      return false;
    }
    if (upperBound != other.upperBound) {
      return false;
    }
    if (upperBoundType != other.upperBoundType) {
      return false;
    }
    return true;
  }

  private final CObjectCollection<ProfileObject> capacityUnits;
  private CObjectCollection<ProfileObject> myCapacityUnits;
  private int lowerBound;
  private LowerBoundType lowerBoundType;
  private int upperBound;
  private UpperBoundType upperBoundType;
}

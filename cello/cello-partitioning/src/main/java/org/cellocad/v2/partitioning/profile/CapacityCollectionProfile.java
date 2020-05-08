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
import org.cellocad.v2.common.profile.ProfileObject;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 * A capacity collection profile.
 *
 * @author Vincent Mirian
 * @date Nov 7, 2017
 */
public class CapacityCollectionProfile extends ProfileObject {

  private void init() {
    myCapacity = new CObjectCollection<>();
  }

  /**
   * Initializes a newly created {@link CapacityCollectionProfile}.
   *
   * @param jsonObj The JSON data associated with this instance.
   * @param capacity A collection of {@link CapacityProfile} objects.
   */
  public CapacityCollectionProfile(
      final JSONObject jsonObj, final CObjectCollection<CapacityProfile> capacity) {
    super(jsonObj);
    this.capacity = capacity;
    init();
    // parse
    parse(jsonObj);
  }

  /*
   * Parse
   */
  private void parseCapacity(final JSONObject jsonObj) {
    final JSONArray jsonArr = (JSONArray) jsonObj.get("capacity");
    if (jsonArr != null) {
      for (int i = 0; i < jsonArr.size(); i++) {
        final Object capacityObj = jsonArr.get(i);
        if (Utils.isString(capacityObj)) {
          final String capacityName = (String) capacityObj;
          final CapacityProfile capacity = this.capacity.findCObjectByName(capacityName);
          if (capacity != null) {
            addCapacity(capacity);
          } else {
            throw new RuntimeException(capacityName + " not found.");
          }
        }
      }
    }
  }

  private void parse(final JSONObject jsonObj) {
    // name
    // parseName(JObj);
    // capacity
    parseCapacity(jsonObj);
  }

  /*
   * capacity
   */
  private void addCapacity(final CapacityProfile pObject) {
    if (pObject != null) {
      myCapacity.add(pObject);
    }
  }

  /**
   * Get the capacity profile at the given index.
   *
   * @param index An index.
   * @return The capacity profile at the given index.
   */
  public CapacityProfile getCapacityAtIdx(final int index) {
    CapacityProfile rtn = null;
    if (0 <= index && index < getNumCapacity()) {
      rtn = myCapacity.get(index);
    }
    return rtn;
  }

  public int getNumCapacity() {
    return myCapacity.size();
  }

  /*
   * isValid
   */
  @Override
  public boolean isValid() {
    boolean rtn = false;
    rtn = super.isValid();
    for (int i = 0; rtn && i < getNumCapacity(); i++) {
      final CapacityProfile CP = getCapacityAtIdx(i);
      rtn = rtn && CP.isValid();
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
    result = prime * result + (capacity == null ? 0 : capacity.hashCode());
    result = prime * result + (myCapacity == null ? 0 : myCapacity.hashCode());
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
    final CapacityCollectionProfile other = (CapacityCollectionProfile) obj;
    if (capacity == null) {
      if (other.capacity != null) {
        return false;
      }
    } else if (!capacity.equals(other.capacity)) {
      return false;
    }
    if (myCapacity == null) {
      if (other.myCapacity != null) {
        return false;
      }
    } else if (!myCapacity.equals(other.myCapacity)) {
      return false;
    }
    return true;
  }

  private final CObjectCollection<CapacityProfile> capacity;
  private CObjectCollection<CapacityProfile> myCapacity;
}

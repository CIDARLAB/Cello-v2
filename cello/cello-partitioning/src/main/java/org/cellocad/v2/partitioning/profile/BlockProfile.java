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
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 * A profile of a partition block.
 * 
 * @author Vincent Mirian
 *
 * @date Oct 27, 2017
 */
public class BlockProfile extends CapacityCollectionProfile {

  private void parseOutputConnectionsCapacity(final JSONObject jsonObj,
      final CObjectCollection<CapacityProfile> capacities) {
    // Capacity
    final JSONArray jsonArr = (JSONArray) jsonObj.get("output_connections_capacity");
    if (jsonArr != null) {
      for (int i = 0; i < jsonArr.size(); i++) {
        final Object capacityObj = jsonArr.get(i);
        if (Utils.isString(capacityObj)) {
          final String capacityName = (String) capacityObj;
          final CapacityProfile capacity = capacities.findCObjectByName(capacityName);
          if (capacity != null) {
            addOutputConnectionsCapacity(capacity);
          } else {
            throw new RuntimeException(capacityName + " not found.");
          }
        }
      }
    }
  }

  private void parseInputConnectionsCapacity(final JSONObject jsonObj,
      final CObjectCollection<CapacityProfile> capacities) {
    // Capacity
    final JSONArray jsonArr = (JSONArray) jsonObj.get("input_connections_capacity");
    if (jsonArr != null) {
      for (int i = 0; i < jsonArr.size(); i++) {
        final Object capacityObj = jsonArr.get(i);
        if (Utils.isString(capacityObj)) {
          final String capacityName = (String) capacityObj;
          final CapacityProfile capacity = capacities.findCObjectByName(capacityName);
          if (capacity != null) {
            addInputConnectionsCapacity(capacity);
          } else {
            throw new RuntimeException(capacityName + " not found.");
          }
        }
      }
    }
  }

  private void parseInOutConnectionsCapacity(final JSONObject jsonObj,
      final CObjectCollection<CapacityProfile> capacities) {
    // Capacity
    final JSONArray jsonArr = (JSONArray) jsonObj.get("inout_connections_capacity");
    if (jsonArr != null) {
      if (getOutputConnectionsCapacity().size() > 0 || getInputConnectionsCapacity().size() > 0) {
        throw new RuntimeException(
            "Conflict: inout_connections_capacity for block name " + getName());
      }
      for (int i = 0; i < jsonArr.size(); i++) {
        final Object capacityObj = jsonArr.get(i);
        if (Utils.isString(capacityObj)) {
          final String capacityName = (String) capacityObj;
          final CapacityProfile capacity = capacities.findCObjectByName(capacityName);
          if (capacity != null) {
            addInOutConnectionsCapacity(capacity);
          } else {
            throw new RuntimeException(capacityName + " not found.");
          }
        }
      }
    }
  }

  private void init() {
    inputConnectionsCapacity = new CObjectCollection<>();
    outputConnectionsCapacity = new CObjectCollection<>();
    inoutConnectionsCapacity = new CObjectCollection<>();
  }

  /**
   * Initializes a newly created {@link BlockProfile} object.
   * 
   * @param jsonObj  The {@link JSONObject} corresponding to the block profile.
   * @param capacity A collection of {@link CapacityProfile} objects.
   */
  public BlockProfile(final JSONObject jsonObj, final CObjectCollection<CapacityProfile> capacity) {
    super(jsonObj, capacity);
    init();
    parseOutputConnectionsCapacity(jsonObj, capacity);
    parseInputConnectionsCapacity(jsonObj, capacity);
    parseInOutConnectionsCapacity(jsonObj, capacity);
  }

  /*
   * Input
   */
  /**
   * Adds the {@link CapacityProfile} object defined by parameter {@code c} to
   * {@code inputConnectionsCapacity}.
   *
   * @param c A {@link CapacityProfile} object.
   */
  public void addInputConnectionsCapacity(final CapacityProfile c) {
    if (c != null) {
      getInputConnectionsCapacity().add(c);
    }
  }

  /**
   * Returns the {@link CapacityProfile} object at the specified position in
   * {@code inputConnectionsCapacity}.
   *
   * @param index The index of the {@link CapacityProfile} object to return.
   * @return If the index is within the bounds
   *         {@code (0 <= bounds < this.getNumInputConnectionsCapacity())}, return the
   *         {@link CapacityProfile} object at the specified position in
   *         {@code inputConnectionsCapacity}, otherwise null.
   */
  public CapacityProfile getInputConnectionsCapacityAtIdx(final int index) {
    CapacityProfile rtn = null;
    if (0 <= index && index < getNumInputConnectionsCapacity()) {
      rtn = getInputConnectionsCapacity().get(index);
    }
    return rtn;
  }

  /**
   * Returns the number of {@link CapacityProfile} objects in {@code inputConnectionsCapacity}.
   *
   * @return The number of {@link CapacityProfile} objects in {@code inputConnectionsCapacity}.
   */
  public int getNumInputConnectionsCapacity() {
    return getInputConnectionsCapacity().size();
  }

  /**
   * Getter for {@code inputConnectionsCapacity}.
   *
   * @return The value of {@code inputConnectionsCapacity}.
   */
  public CObjectCollection<CapacityProfile> getInputConnectionsCapacity() {
    return inputConnectionsCapacity;
  }

  private CObjectCollection<CapacityProfile> inputConnectionsCapacity;

  /*
   * Output
   */
  /**
   * Adds the {@link CapacityProfile} object defined by parameter {@code c} to
   * {@code outputConnectionsCapacity}.
   *
   * @param c A {@link CapacityProfile} object.
   */
  public void addOutputConnectionsCapacity(final CapacityProfile c) {
    if (c != null) {
      getOutputConnectionsCapacity().add(c);
    }
  }

  /**
   * Returns the {@link CapacityProfile} object at the specified position in
   * {@code outputConnectionsCapacity}.
   *
   * @param index The index of the {@link CapacityProfile} object to return.
   * @return If the index is within the bounds
   *         {@code (0 <= bounds < this.getNumOutputConnectionsCapacity())}, return the
   *         {@link CapacityProfile} object at the specified position in
   *         {@code outputConnectionsCapacity}, otherwise null.
   */
  public CapacityProfile getOutputConnectionsCapacityAtIdx(final int index) {
    CapacityProfile rtn = null;
    if (0 <= index && index < getNumOutputConnectionsCapacity()) {
      rtn = getOutputConnectionsCapacity().get(index);
    }
    return rtn;
  }

  /**
   * Returns the number of {@link CapacityProfile} objects in {@code outputConnectionsCapacity}.
   *
   * @return The number of {@link CapacityProfile} objects in {@code outputConnectionsCapacity}.
   */
  public int getNumOutputConnectionsCapacity() {
    return getOutputConnectionsCapacity().size();
  }

  /**
   * Getter for {@code outputConnectionsCapacity}.
   *
   * @return The value of {@code outputConnectionsCapacity}.
   */
  public CObjectCollection<CapacityProfile> getOutputConnectionsCapacity() {
    return outputConnectionsCapacity;
  }

  private CObjectCollection<CapacityProfile> outputConnectionsCapacity;

  /*
   * InOut
   */
  /**
   * Adds the {@link CapacityProfile} object defined by parameter {@code c} to
   * {@code inoutConnectionsCapacity}.
   *
   * @param c A {@link CapacityProfile} object.
   */
  public void addInOutConnectionsCapacity(final CapacityProfile c) {
    if (c != null) {
      getInOutConnectionsCapacity().add(c);
    }
  }

  /**
   * Returns the {@link CapacityProfile} object at the specified position in
   * {@code inoutConnectionsCapacity}.
   *
   * @param index The index of the {@link CapacityProfile} object to return.
   * @return If the index is within the bounds
   *         {@code (0 <= bounds < this.getNumInOutConnectionsCapacity())}, return the
   *         {@link CapacityProfile} at the specified position in {@code inoutConnectionsCapacity},
   *         otherwise null.
   */
  public CapacityProfile getInOutConnectionsCapacityAtIdx(final int index) {
    CapacityProfile rtn = null;
    if (0 <= index && index < getNumInOutConnectionsCapacity()) {
      rtn = getInOutConnectionsCapacity().get(index);
    }
    return rtn;
  }

  /**
   * Returns the number of {@link CapacityProfile} objects in {@code inoutConnectionsCapacity}.
   *
   * @return The number of {@link CapacityProfile} objects in {@code inoutConnectionsCapacity}.
   */
  public int getNumInOutConnectionsCapacity() {
    return getInOutConnectionsCapacity().size();
  }

  /**
   * Getter for {@code inoutConnectionsCapacity}.
   *
   * @return The value of {@code inoutConnectionsCapacity}.
   */
  public CObjectCollection<CapacityProfile> getInOutConnectionsCapacity() {
    return inoutConnectionsCapacity;
  }

  private CObjectCollection<CapacityProfile> inoutConnectionsCapacity;

}

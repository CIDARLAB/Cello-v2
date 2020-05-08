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
import org.cellocad.v2.common.constraint.Weight;
import org.cellocad.v2.common.profile.DerivedProfile;

/**
 * A collection of {@link CapacityCollectionProfile} objects.
 *
 * @author Vincent Mirian
 * @date Nov 7, 2017
 */
public class CapacityCollection<T extends CapacityCollectionProfile> extends DerivedProfile<T> {

  private void init() {
    myCapacity = new CObjectCollection<>();
  }

  /**
   * Initializes a newly created {@link CapacityCollection} object.
   *
   * @param ccp A capacity collection profile.
   * @param capacities A collection of {@link Capacity} objects.
   */
  public CapacityCollection(final T ccp, final CObjectCollection<Capacity> capacities) {
    super(ccp);
    Utils.isNullRuntimeException(ccp, "CapacityCollectionProfile");
    Utils.isNullRuntimeException(capacities, "Capacities");
    init();
    CapacityProfile cp = null;
    Capacity capacity = null;
    for (int i = 0; i < ccp.getNumCapacity(); i++) {
      cp = ccp.getCapacityAtIdx(i);
      capacity = capacities.findCObjectByName(cp.getName());
      Utils.isNullRuntimeException(capacity, "Capacity");
      this.addCapacity(capacity);
    }
  }

  /*
   * Evaluate
   */
  /**
   * Whether this instance can fit using the given weight.
   *
   * @param wObj A {@link Weight}.
   * @return Whether this instance can fit using the given weight.
   */
  public boolean canFit(final Weight wObj) {
    boolean rtn = false;
    final int size = this.getNumCapacity();
    rtn = size == 0;
    for (int i = 0; !rtn && i < size; i++) {
      final Capacity c = this.getCapacityAtIdx(i);
      rtn = rtn || c.canFit(wObj);
    }
    return rtn;
  }

  /**
   * Whether this instance is overflow using the given weight.
   *
   * @param wObj A {@link Weight}.
   * @return Whether this instance is overflow using the given weight.
   */
  public boolean isOverflow(final Weight wObj) {
    boolean rtn = false;
    final int size = this.getNumCapacity();
    rtn = size == 0;
    for (int i = 0; !rtn && i < size; i++) {
      final Capacity c = this.getCapacityAtIdx(i);
      rtn = rtn || c.isOverflow(wObj);
    }
    return rtn;
  }

  /**
   * Whether this instance is underflow using the given weight.
   *
   * @param wObj A {@link Weight}.
   * @return Whether this instance is underflow using the given weight.
   */
  public boolean isUnderflow(final Weight wObj) {
    boolean rtn = false;
    final int size = this.getNumCapacity();
    rtn = size == 0;
    for (int i = 0; !rtn && i < size; i++) {
      final Capacity c = this.getCapacityAtIdx(i);
      rtn = rtn || c.isUnderflow(wObj);
    }
    return rtn;
  }

  /*
   * capacity
   */
  /**
   * Adds the given {@link Capacity} object to this instance.
   *
   * @param c A {@link Capacity} object.
   */
  public void addCapacity(final Capacity c) {
    if (c != null) {
      this.myCapacity.add(c);
    }
  }

  /**
   * Gets the {@link Capacity} object at the given index.
   *
   * @param index An index.
   * @return The {@link Capacity} object at the given index.
   */
  public Capacity getCapacityAtIdx(final int index) {
    Capacity rtn = null;
    if (0 <= index && index < this.getNumCapacity()) {
      rtn = myCapacity.get(index);
    }
    return rtn;
  }

  public int getNumCapacity() {
    return myCapacity.size();
  }

  private CObjectCollection<Capacity> myCapacity;
}

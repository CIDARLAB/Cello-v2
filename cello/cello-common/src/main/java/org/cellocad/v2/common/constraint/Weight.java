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

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.cellocad.v2.common.CObject;
import org.cellocad.v2.common.CObjectCollection;
import org.cellocad.v2.common.Utils;

/**
 * A weight.
 * 
 * @author Vincent Mirian
 *
 * @date Nov 7, 2017
 */
public class Weight extends CObject {

  private void init() {
    weightMap = new HashMap<>();
    myUnits = new Units();
  }

  /**
   * Initializes a newly created {@link Weight}.
   */
  public Weight() {
    super();
    init();
    setTotal(0);
  }

  /**
   * Initializes a newly created {@link Weight}.
   *
   * @param other Another weight.
   */
  public Weight(final Weight other) {
    super(other);
    init();
    weightMap = new HashMap<>(other.weightMap);
    myUnits = new Units(other.myUnits);
    setTotal(other.getTotal());
  }

  /**
   * Initializes a newly created {@link Weight}.
   *
   * @param units    Units.
   * @param allUnits All units.
   */
  public Weight(final CObjectCollection<CObject> units, final CObjectCollection<CObject> allUnits) {
    this();
    init();
    getUnits().resetWithCObject(units, allUnits);
  }

  /*
   * units
   */
  public Units getUnits() {
    return myUnits;
  }

  /**
   * Reset units.
   * 
   * @param allUnits All units.
   */
  public void resetUnits(final CObjectCollection<CObject> allUnits) {
    final CObjectCollection<CObject> units = new CObjectCollection<>();
    final Iterator<Map.Entry<String, Integer>> it = weightMap.entrySet().iterator();
    CObject obj = null;
    while (it.hasNext()) {
      final Map.Entry<String, Integer> pair = it.next();
      obj = new CObject();
      obj.setName(pair.getKey());
      units.add(obj);
    }
    getUnits().resetWithCObject(units, allUnits);
  }

  /*
   * weights
   */
  public void setWeight(final String unit, final int value) {
    weightMap.put(unit, new Integer(value));
  }

  /**
   * Set weight.
   * 
   * @param unit  Unit.
   * @param value Value.
   */
  public void setWeight(final String unit, final Integer value) {
    weightMap.put(unit, new Integer(value));
  }

  /**
   * Get weight.
   * 
   * @param unit Unit.
   * @return Weight.
   */
  public Integer getWeight(final String unit) {
    Integer rtn = null;
    rtn = weightMap.get(unit);
    return rtn;
  }

  public void removeWeight(final String unit) {
    weightMap.remove(unit);
  }

  public void addWeight(final String unit) {
    this.setWeight(unit, 0);
  }

  public void incWeight(final String unit) {
    this.incWeight(unit, 1);
  }

  /**
   * Increment weight.
   * 
   * @param unit  Unit.
   * @param value Value.
   */
  public void incWeight(final String unit, final int value) {
    Integer amount = getWeight(unit);
    if (amount == null) {
      addWeight(unit);
      amount = getWeight(unit);
    }
    amount = new Integer(amount.intValue() + 1);
    this.setWeight(unit, amount);
    incTotal(value);
  }

  public void decWeight(final String unit) {
    this.decWeight(unit, 1);
  }

  /**
   * Decrement.
   * 
   * @param unit  Unit.
   * @param value Value.
   */
  public void decWeight(final String unit, final int value) {
    Integer amount = getWeight(unit);
    if (amount == null) {
      addWeight(unit);
      amount = getWeight(unit);
    }
    amount = new Integer(amount.intValue() - 1);
    this.setWeight(unit, amount);
    decTotal(value);
  }

  /*
   * total
   */

  /**
   * Decrement.
   * 
   * @param wObj A weight.
   */
  public void dec(final Weight wObj) {
    boolean valid = false;
    valid = isValid() && wObj.isValid();
    valid = valid && getUnits().doUnitsExist(wObj.getUnits());
    if (valid) {
      decTotal(wObj.getTotal());
    }
  }

  /**
   * Increment.
   * 
   * @param wObj A weight.
   */
  public void inc(final Weight wObj) {
    boolean valid = false;
    valid = isValid() && wObj.isValid();
    valid = valid && getUnits().doUnitsExist(wObj.getUnits());
    if (valid) {
      incTotal(wObj.getTotal());
    }
  }

  public int getTotal() {
    return total;
  }

  private void setTotal(final int total) {
    this.total = total;
  }

  private void incTotal(final int value) {
    setTotal(getTotal() + value);
  }

  private void decTotal(final int value) {
    setTotal(getTotal() - value);
  }

  /*
   * isValid
   */
  @Override
  public boolean isValid() {
    boolean rtn = false;
    rtn = super.isValid();
    rtn = rtn && getTotal() >= 0;
    rtn = rtn && getUnits().isValid();
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
    result = prime * result + (weightMap == null ? 0 : weightMap.hashCode());
    result = prime * result + (myUnits == null ? 0 : myUnits.hashCode());
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
    final Weight other = (Weight) obj;
    if (total != other.total) {
      return false;
    }
    if (weightMap == null) {
      if (other.weightMap != null) {
        return false;
      }
    } else if (!weightMap.equals(other.weightMap)) {
      return false;
    }
    if (myUnits == null) {
      if (other.myUnits != null) {
        return false;
      }
    } else if (!myUnits.equals(other.myUnits)) {
      return false;
    }
    return true;
  }

  /*
   * toString
   */
  protected String getWeightsToString() {
    String rtn = "";
    final Iterator<Map.Entry<String, Integer>> it = weightMap.entrySet().iterator();
    while (it.hasNext()) {
      final Map.Entry<String, Integer> pair = it.next();
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
    rtn = rtn + "[ ";
    rtn = rtn + Utils.getNewLine();
    // weights
    rtn = rtn + Utils.getTabCharacter();
    rtn = rtn + "weights = ";
    rtn = rtn + Utils.getNewLine();
    rtn = rtn + Utils.getTabCharacter();
    rtn = rtn + "{";
    rtn = rtn + Utils.getNewLine();
    rtn = rtn + getWeightsToString();
    rtn = rtn + Utils.getTabCharacter();
    rtn = rtn + "}";
    rtn = rtn + Utils.getNewLine();
    // myUnits
    rtn = rtn + this.getEntryToString("myUnits", getUnits().getUnitsToString());
    // total
    rtn = rtn + this.getEntryToString("total", getTotal());
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

  private Map<String, Integer> weightMap;
  private Units myUnits;
  private int total;

}

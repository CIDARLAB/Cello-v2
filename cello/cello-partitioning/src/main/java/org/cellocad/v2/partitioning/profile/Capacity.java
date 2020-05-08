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

import org.cellocad.v2.common.CObject;
import org.cellocad.v2.common.CObjectCollection;
import org.cellocad.v2.common.Utils;
import org.cellocad.v2.common.constraint.EvaluateResult;
import org.cellocad.v2.common.constraint.LowerBoundType;
import org.cellocad.v2.common.constraint.Units;
import org.cellocad.v2.common.constraint.UpperBoundType;
import org.cellocad.v2.common.constraint.Weight;
import org.cellocad.v2.common.profile.DerivedProfile;

/**
 * A description of the capacity of a partitioned object.
 *
 * @author Vincent Mirian
 * @date Nov 7, 2017
 */

// TODO: Capacity is a constraint, make constraint engine
public class Capacity extends DerivedProfile<CapacityProfile> {

  /**
   * Initializes a newly created {@link Capacity}.
   *
   * @param cp A {@link CapacityProfile}.
   * @param capacityUnits A collection of {@link CObject}.
   */
  public Capacity(final CapacityProfile cp, final CObjectCollection<CObject> capacityUnits) {
    super(cp);
    Utils.isNullRuntimeException(capacityUnits, "CapacityUnits");
    setLowerBound(cp.getLowerBound());
    setLowerBoundType(cp.getLowerBoundType());
    setUpperBound(cp.getUpperBound());
    setUpperBoundType(cp.getUpperBoundType());
    myUnits = new Units(cp.getCapacityUnits(), capacityUnits);
  }

  /*
   * units
   */
  private Units getUnits() {
    return myUnits;
  }

  /*
   * Evaluate
   */
  private EvaluateResult evaluate(final Weight wObj) {
    EvaluateResult rtn = EvaluateResult.ERROR;
    boolean valid = false;
    valid = isValid() && wObj.isValid();
    valid = valid && getUnits().doUnitsExist(wObj.getUnits());
    if (valid) {
      final int total = wObj.getTotal();
      if (total < getLowerBound()) {
        rtn = EvaluateResult.UNDERFLOW;
      } else if (getUpperBound() <= total) {
        rtn = EvaluateResult.OVERFLOW;
      } else {
        rtn = EvaluateResult.OK;
      }
    }
    return rtn;
  }

  /**
   * Whether this instance can fit using the given weight.
   *
   * @param wObj A weight.
   * @return Whether this instance can fit using the given weight.
   */
  public boolean canFit(final Weight wObj) {
    boolean rtn = false;
    final EvaluateResult result = evaluate(wObj);
    rtn = result == EvaluateResult.OK;
    return rtn;
  }

  /**
   * Whether this instance is overflow using the given weight.
   *
   * @param wObj A weight.
   * @return Whether this instance is overflow using the given weight.
   */
  public boolean isOverflow(final Weight wObj) {
    boolean rtn = false;
    final EvaluateResult result = evaluate(wObj);
    rtn = result == EvaluateResult.OVERFLOW;
    return rtn;
  }

  /**
   * Whether this instance is underflow using the given weight.
   *
   * @param wObj A weight.
   * @return Whether this instance is underflow using the given weight.
   */
  public boolean isUnderflow(final Weight wObj) {
    boolean rtn = false;
    final EvaluateResult result = evaluate(wObj);
    rtn = result == EvaluateResult.UNDERFLOW;
    return rtn;
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
  }

  public int getLowerBound() {
    return lowerBound;
  }

  private void setLowerBoundType(final LowerBoundType type) {
    lowerBoundType = type;
    reduce();
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
    rtn = rtn && getLowerBound() < getUpperBound();
    rtn = rtn && getLowerBoundType() == LowerBoundType.GREATER_THAN_OR_EQUAL;
    rtn = rtn && getUpperBoundType() == UpperBoundType.LESS_THAN;
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
    result = prime * result + lowerBound;
    result = prime * result + (lowerBoundType == null ? 0 : lowerBoundType.hashCode());
    result = prime * result + upperBound;
    result = prime * result + (upperBoundType == null ? 0 : upperBoundType.hashCode());
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
    final Capacity other = (Capacity) obj;
    if (lowerBound != other.lowerBound) {
      return false;
    }
    if (lowerBoundType != other.lowerBoundType) {
      return false;
    }
    if (upperBound != other.upperBound) {
      return false;
    }
    if (upperBoundType != other.upperBoundType) {
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
  @Override
  public String toString() {
    String rtn = "";
    rtn = rtn + "[ ";
    rtn = rtn + Utils.getNewLine();
    // name
    rtn = rtn + this.getEntryToString("name", getName());
    // equation
    rtn = rtn + Utils.getTabCharacter();
    rtn = rtn + "Equation: ";
    rtn = rtn + getLowerBound();
    rtn = rtn + " ";
    rtn = rtn + getLowerBoundType().toString();
    rtn = rtn + getUnits().getUnitsToString();
    rtn = rtn + " ";
    rtn = rtn + getUpperBoundType().toString();
    rtn = rtn + " ";
    rtn = rtn + getUpperBound();
    rtn = rtn + Utils.getNewLine();
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

  private int lowerBound;
  private LowerBoundType lowerBoundType;
  private int upperBound;
  private UpperBoundType upperBoundType;
  private final Units myUnits;
}

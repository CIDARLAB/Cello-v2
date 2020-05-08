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

/**
 * A representation of a lower bound type.
 *
 * @author Vincent Mirian
 * @date Nov 7, 2017
 */
public enum LowerBoundType {
  GREATER_THAN_OR_EQUAL,
  GREATER_THAN; // , EQUAL;

  /**
   * Get the {@link LowerBoundType} from the given string.
   *
   * @param str A string representation of a {@link LowerBoundType} object.
   * @return The {@link LowerBoundType} object.
   */
  public static LowerBoundType getBoundType(final String str) {
    LowerBoundType rtn = null;
    switch (str) {
      case "greater_than_or_equal":
        rtn = GREATER_THAN_OR_EQUAL;
        break;
      case "greater_than":
        rtn = GREATER_THAN;
        break;
      default:
        rtn = null;
        break;
    }
    return rtn;
  }

  /**
   * Get a string representation of the operator associated with the given {@link LowerBoundType}
   * object.
   *
   * @param lb A {@link LowerBoundType} object.
   * @return A string representation of the operator associated with the given {@link
   *     LowerBoundType} object.
   */
  public static String getStringFlip(final LowerBoundType lb) {
    String rtn = null;
    switch (lb) {
      case GREATER_THAN_OR_EQUAL:
        rtn = "=>";
        break;
      case GREATER_THAN:
        rtn = ">";
        break;
      default:
        rtn = null;
        break;
    }
    return rtn;
  }

  @Override
  public String toString() {
    String rtn = null;
    switch (this) {
      case GREATER_THAN_OR_EQUAL:
        rtn = "<=";
        break;
      case GREATER_THAN:
        rtn = "<";
        break;
      default:
        rtn = null;
        break;
    }
    return rtn;
  }
}

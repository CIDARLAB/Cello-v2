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
 * Objects representing upper bound types.
 * 
 * @author Vincent Mirian
 *
 * @date Nov 7, 2017
 */
public enum UpperBoundType {
  LESS_THAN, LESS_THAN_OR_EQUAL;// , EQUAL;

  /**
   * Get the {@link UpperBoundType} corresponding to the given string.
   * 
   * @param str A string representing an upper bound type.
   * @return The {@link UpperBoundType} corresponding to the given string.
   */
  public static UpperBoundType getBoundType(final String str) {
    UpperBoundType rtn = null;
    switch (str) {
      case "less_than": {
        rtn = LESS_THAN;
        break;
      }
      case "less_than_or_equal": {
        rtn = LESS_THAN_OR_EQUAL;
        break;
      }
      /*
       * case "equal":{ rtn = EQUAL; break; }
       */
      default: {
        rtn = null;
        break;
      }
    }
    return rtn;
  }

  @Override
  public String toString() {
    String rtn = null;
    switch (this) {
      case LESS_THAN_OR_EQUAL: {
        rtn = "<=";
        break;
      }
      case LESS_THAN: {
        rtn = "<";
        break;
      }
      /*
       * case "equal":{ rtn = EQUAL; break; }
       */
      default: {
        rtn = null;
        break;
      }
    }
    return rtn;
  }

}

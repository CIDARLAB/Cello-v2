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

package org.cellocad.v2.common.algorithm.data;

import org.cellocad.v2.common.CObject;
import org.cellocad.v2.common.profile.AlgorithmProfile;

/**
 * The NetlistEdgeDataFactory class is a NetlistEdgeData factory.
 *
 * @param <T> the type of NetlistEdgeData.
 *
 * @author Vincent Mirian
 *
 * @date Dec 15, 2017
 */
public abstract class NetlistEdgeDataFactory<T extends NetlistEdgeData> extends CObject {

  /**
   * Returns the {@link NetlistEdgeData} object that has the same name as the parameter {@code name}
   * within the {@link NetlistEdgeDataFactory}.
   *
   * @param name string used for searching the NetlistEdgeDataFactory.
   * @return The {@link NetlistEdgeData} instance if the {@link NetlistEdgeData} type exists within
   *         the {@link NetlistEdgeDataFactory}, otherwise null.
   */
  protected abstract T getNetlistEdgeData(final String name);

  /**
   * Returns the NetlistEdgeData that has the same name as the parameter {@code algProfile} within
   * the NetlistEdgeDataFactory.
   *
   * @param algProfile AlgorithmProfile used for searching the NetlistEdgeDataFactory.
   * @return The {@link NetlistEdgeData} instance if the {@link NetlistEdgeData} type exists within
   *         the {@link NetlistEdgeDataFactory}, otherwise null.
   */
  public T getNetlistEdgeData(final AlgorithmProfile algProfile) {
    T rtn = null;
    if (algProfile != null) {
      final String name = algProfile.getName();
      rtn = getNetlistEdgeData(name);
      if (rtn != null) {
        rtn.setName(algProfile.getName());
      }
    }
    return rtn;
  }

}

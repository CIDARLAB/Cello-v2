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

package org.cellocad.v2.logicSynthesis.algorithm.data;

import org.cellocad.v2.common.algorithm.data.NetlistEdgeDataFactory;
import org.cellocad.v2.logicSynthesis.algorithm.Yosys.data.YosysNetlistEdgeData;

/**
 * The LSNetlistEdgeDataFactory is a NetlistEdgeData factory for the <i>logicSynthesis</i> stage.
 *
 * @author Vincent Mirian
 * @date 2018-05-21
 */
public class LSNetlistEdgeDataFactory extends NetlistEdgeDataFactory<LSNetlistEdgeData> {

  /**
   * Returns the {@link LSNetlistEdgeData} object that has the same name as the parameter {@code
   * name} within the {@link LSNetlistEdgeDataFactory}.
   *
   * @param name string used for searching this instance.
   * @return The {@link LSNetlistEdgeData} instance if the {@link LSNetlistEdgeData} type exists
   *     within the {@link LSNetlistEdgeDataFactory}, otherwise null.
   */
  @Override
  protected LSNetlistEdgeData getNetlistEdgeData(final String name) {
    LSNetlistEdgeData rtn = null;
    if (name.equals("Yosys")) {
      rtn = new YosysNetlistEdgeData();
    }
    return rtn;
  }
}

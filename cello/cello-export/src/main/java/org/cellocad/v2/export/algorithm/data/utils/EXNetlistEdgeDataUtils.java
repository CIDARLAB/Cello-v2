/*
 * Copyright (C) 2018 Boston University (BU)
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

package org.cellocad.v2.export.algorithm.data.utils;

import org.cellocad.v2.common.profile.AlgorithmProfile;
import org.cellocad.v2.export.algorithm.data.EXNetlistEdgeData;
import org.cellocad.v2.export.algorithm.data.EXNetlistEdgeDataFactory;
import org.cellocad.v2.results.netlist.Netlist;
import org.cellocad.v2.results.netlist.NetlistEdge;

/**
 * The EXNetlistEdgeDataUtils class is class with utility methods for EXNetlistEdgeData instances in
 * the <i>export</i> stage.
 *
 * @author Timothy Jones
 * @date 2018-06-04
 */
public class EXNetlistEdgeDataUtils {

  /**
   * Resets the algorithm data, where the algorithm is defined by parameter {@code algProfile}, for
   * all edges in the netlist instance defined by parameter {@code netlist}.
   *
   * @param netlist The {@link Netlist}.
   * @param algProfile The {@link AlgorithmProfile}.
   */
  public static void resetNetlistEdgeData(
      final Netlist netlist, final AlgorithmProfile algProfile) {
    for (int i = 0; i < netlist.getNumEdge(); i++) {
      final NetlistEdge edge = netlist.getEdgeAtIdx(i);
      EXNetlistEdgeDataUtils.resetNetlistEdgeData(edge, algProfile);
    }
  }

  /**
   * Resets the algorithm data, where the algorithm is defined by parameter {@code algProfile}, for
   * a NetlistEdge instance defined by parameter {@code edge}.
   *
   * @param edge The {@link NetlistEdge}.
   * @param algProfile The {@link AlgorithmProfile}.
   */
  public static void resetNetlistEdgeData(
      final NetlistEdge edge, final AlgorithmProfile algProfile) {
    final EXNetlistEdgeDataFactory EXFactory = new EXNetlistEdgeDataFactory();
    final EXNetlistEdgeData data = EXFactory.getNetlistEdgeData(algProfile);
    edge.setNetlistEdgeData(data);
  }
}

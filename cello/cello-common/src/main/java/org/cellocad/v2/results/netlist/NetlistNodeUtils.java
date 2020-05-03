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

package org.cellocad.v2.results.netlist;

import java.util.ArrayList;
import java.util.List;
import org.cellocad.v2.common.Utils;

/**
 * The NetlistNodeUtils class is class with utility methods for <i>NetlistNode</i> instances.
 *
 * @author Vincent Mirian
 *
 * @date 2018-05-21
 */
public class NetlistNodeUtils {

  /**
   * Returns the number of {@link NetlistEdge} objects connecting a source {@link NetlistNode},
   * {@code src}, to a destination {@link NetlistNode}, {@code dst}.
   *
   * @param src The source NetlistNode.
   * @param dst The destination NetlistNode.
   * @return The number of {@link NetlistEdge} objects connecting a source {@link NetlistNode},
   *         {@code src}, to a destination {@link NetlistNode}, {@code dst}.
   * @throws RuntimeException Any of the parameters are null.
   */
  public static int numNetlistEdgesBetween(final NetlistNode src, final NetlistNode dst) {
    Utils.isNullRuntimeException(src, "src");
    Utils.isNullRuntimeException(dst, "dst");
    int rtn = 0;
    rtn += NetlistNodeUtils.getNetlistEdgesBetween(src, dst).size();
    return rtn;
  }

  /**
   * Returns a list of NetlistEdges connecting the a source {@link NetlistNode}, {@code src}, to a
   * destination {@link NetlistNode}, {@code dst}.
   *
   * @param src The source NetlistNode.
   * @param dst The destination NetlistNode.
   * @return A list of NetlistEdges connecting the a source {@link NetlistNode}, {@code src}, to a
   *         destination {@link NetlistNode}, {@code dst}.
   * @throws RuntimeException Any of the parameters are null.
   */
  private static List<NetlistEdge> getNetlistEdgesBetween(final NetlistNode src,
      final NetlistNode dst) {
    Utils.isNullRuntimeException(src, "src");
    Utils.isNullRuntimeException(dst, "dst");
    final List<NetlistEdge> rtn = new ArrayList<>();
    for (int i = 0; i < dst.getNumInEdge(); i++) {
      final NetlistEdge edge = dst.getInEdgeAtIdx(i);
      if (edge.getSrc() == src) {
        rtn.add(edge);
      }
    }
    return rtn;
  }

}

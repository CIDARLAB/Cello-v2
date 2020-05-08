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

package org.cellocad.v2.partitioning.algorithm.GPCC_SUGARM_BASE.data;

import java.io.IOException;
import java.io.Writer;
import org.cellocad.v2.partitioning.algorithm.GPCC_BASE.data.GPCC_BASENetlistData;

/**
 * The data for a netlist used within the <i>GPCC_SUGARM_BASE</i> algorithm of the
 * <i>partitioning</i> stage.
 *
 * @author Vincent Mirian
 *
 * @date 2018-05-21
 */
public class GPCC_SUGARM_BASENetlistData extends GPCC_BASENetlistData {

  private void setDefault() {
  }

  /**
   * Initializes a newly created {@link GPCC_SUGARM_BASENetlistData}.
   */
  public GPCC_SUGARM_BASENetlistData() {
    super();
    setDefault();
  }

  /**
   * Writes this instance in JSON format to the writer defined by parameter {@code os} with the
   * number of indents equivalent to the parameter {@code indent}.
   *
   * @param indent The number of indents.
   * @param os     The writer.
   * @throws IOException If an I/O error occurs.
   */
  @Override
  public void writeJson(final int indent, final Writer os) throws IOException {

  }

}

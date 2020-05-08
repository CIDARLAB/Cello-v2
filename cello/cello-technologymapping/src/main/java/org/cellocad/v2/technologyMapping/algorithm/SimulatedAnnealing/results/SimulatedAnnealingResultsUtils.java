/*
 * Copyright (C) 2019 Boston Univeristy (BU)
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

package org.cellocad.v2.technologyMapping.algorithm.SimulatedAnnealing.results;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import org.cellocad.v2.common.Utils;
import org.cellocad.v2.technologyMapping.algorithm.SimulatedAnnealing.data.toxicity.TMToxicityEvaluation;

/**
 * The NetlistUtils class is a class with utility methods for the result of the
 * <i>SimulatedAnnealing</i> algorithm.
 *
 * @author Timothy Jones
 *
 * @date 2019-02-14
 */
public class SimulatedAnnealingResultsUtils {

  /**
   * Writes the toxicity evaluation defined by parameter {@code tmte} to file defined by
   * {@code filename}.
   *
   * @param tmte     The toxicity evaluation.
   * @param filename The file to write the toxicity evaluation.
   * @throws RuntimeException Any of the parameters are null.
   */
  public static void writeCsvForTMToxicityEvaluation(final TMToxicityEvaluation tmte,
      final String filename) {
    Utils.isNullRuntimeException(tmte, "tmte");
    Utils.isNullRuntimeException(filename, "filename");
    try {
      final OutputStream outputStream = new FileOutputStream(filename);
      final Writer outputStreamWriter = new OutputStreamWriter(outputStream);
      tmte.writeCSV(",", outputStreamWriter);
      outputStreamWriter.close();
      outputStream.close();
    } catch (final IOException e) {
      e.printStackTrace();
    }
  }

}

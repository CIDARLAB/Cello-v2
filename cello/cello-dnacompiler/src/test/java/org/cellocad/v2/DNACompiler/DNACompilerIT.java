/*
 * Copyright (C) 2020 Boston University (BU)
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

package org.cellocad.v2.DNACompiler;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.cellocad.v2.DNACompiler.runtime.Main;
import org.cellocad.v2.DNACompiler.runtime.environment.DNACompilerArgString;
import org.cellocad.v2.common.CelloException;
import org.cellocad.v2.common.Utils;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Integration tests for {@link DnaCompiler}.
 *
 * @author Timothy Jones
 * @date 2020-02-25
 */
public class DNACompilerIT {

  /**
   * Environment setup for tests.
   *
   * @throws IOException Unable to read resources.
   * @throws CelloException Unable to instantiate supporting classes.
   */
  @BeforeClass
  public static void init() throws IOException, CelloException {
    final Path dir = Files.createTempDirectory("cello_");
    args =
        new String[] {
          "-" + DNACompilerArgString.INPUTNETLIST,
          Utils.getResource("and.v").getFile(),
          "-" + DNACompilerArgString.USERCONSTRAINTSFILE,
          Utils.getResource("lib/ucf/SC/SC1C1G1T1.UCF.json").getFile(),
          "-" + DNACompilerArgString.INPUTSENSORFILE,
          Utils.getResource("lib/input/SC/SC1C1G1T1.input.json").getFile(),
          "-" + DNACompilerArgString.OUTPUTDEVICEFILE,
          Utils.getResource("lib/output/SC/SC1C1G1T1.output.json").getFile(),
          "-" + DNACompilerArgString.OUTPUTDIR,
          dir.toString(),
          "-" + DNACompilerArgString.PYTHONENV,
          "python"
        };
  }

  @Test
  public void main_AndGateVerilogWithReferenceLibrary_ShouldReturn()
      throws CelloException, IOException {
    Main.main(args);
  }

  private static String[] args;
}

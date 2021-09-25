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

package org.cellocad.v2.logicSynthesis.algorithm.Yosys;

import java.io.IOException;
import java.nio.file.Files;
import org.cellocad.v2.common.Utils;
import org.cellocad.v2.common.exception.CelloException;
import org.cellocad.v2.common.runtime.environment.ArgString;
import org.cellocad.v2.common.stage.runtime.environment.StageArgString;
import org.cellocad.v2.logicSynthesis.runtime.Main;
import org.junit.Test;

/**
 * Integration test for the {@link Yosys} algorithm.
 *
 * @author Timothy Jones
 * @date 2020-01-09
 */
public class YosysIT {

  private static String[] getArguments(
      final String netlist, final String prefix, final String library) throws IOException {
    String[] rtn = null;
    rtn =
        new String[] {
          "-" + StageArgString.INPUTNETLIST,
          Utils.getResource(netlist).getFile(),
          "-" + StageArgString.USERCONSTRAINTSFILE,
          Utils.getResource("lib/ucf/" + prefix + "/" + library + ".UCF.json").getFile(),
          "-" + StageArgString.INPUTSENSORFILE,
          Utils.getResource("lib/input/" + prefix + "/" + library + ".input.json").getFile(),
          "-" + StageArgString.OUTPUTDEVICEFILE,
          Utils.getResource("lib/output/" + prefix + "/" + library + ".output.json").getFile(),
          "-" + StageArgString.ALGORITHMNAME,
          "Yosys",
          "-" + ArgString.OUTPUTDIR,
          Files.createTempDirectory("cello_").toString(),
          "-" + ArgString.PYTHONENV,
          "python"
        };
    return rtn;
  }

  @Test
  public void main_AndGateWithBth1C1G1T1Library_ShouldReturn() throws CelloException, IOException {
    final String[] args = getArguments("and.v", "Bth", "Bth1C1G1T1");
    Main.main(args);
  }

  @Test
  public void main_A1CircuitWithEco1C1G1T1Library_ShouldReturn()
      throws CelloException, IOException {
    final String[] args = getArguments("0xA1.v", "Eco", "Eco1C1G1T1");
    Main.main(args);
  }
}

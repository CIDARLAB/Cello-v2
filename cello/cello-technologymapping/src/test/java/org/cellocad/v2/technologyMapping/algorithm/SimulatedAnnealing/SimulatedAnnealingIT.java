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

package org.cellocad.v2.technologyMapping.algorithm.SimulatedAnnealing;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.cellocad.v2.common.CelloException;
import org.cellocad.v2.common.Utils;
import org.cellocad.v2.common.runtime.environment.ArgString;
import org.cellocad.v2.common.stage.runtime.environment.StageArgString;
import org.cellocad.v2.technologyMapping.runtime.Main;
import org.junit.Test;

/**
 * Integration test for the {@link SimulatedAnnealing} algorithm.
 *
 * @author Timothy Jones
 * @date 2020-01-30
 */
public class SimulatedAnnealingIT {

  @Test
  public void main_AndNetlistWithEco1C1G1T1Library_ShouldReturn()
      throws CelloException, IOException {
    final Path dir = Files.createTempDirectory("cello_");
    final String[] args = {
      "-" + ArgString.INPUTNETLIST,
      Utils.getResource("and_netlist.json").getFile(),
      "-" + ArgString.USERCONSTRAINTSFILE,
      Utils.getResource("lib/ucf/Eco/Eco1C1G1T1.UCF.json").getFile(),
      "-" + ArgString.INPUTSENSORFILE,
      Utils.getResource("lib/input/Eco/Eco1C1G1T1.input.json").getFile(),
      "-" + ArgString.OUTPUTDEVICEFILE,
      Utils.getResource("lib/output/Eco/Eco1C1G1T1.output.json").getFile(),
      "-" + StageArgString.ALGORITHMNAME,
      "SimulatedAnnealing",
      "-" + ArgString.PYTHONENV,
      "python",
      "-" + ArgString.OUTPUTDIR,
      dir.toString()
    };
    Main.main(args);
  }
}

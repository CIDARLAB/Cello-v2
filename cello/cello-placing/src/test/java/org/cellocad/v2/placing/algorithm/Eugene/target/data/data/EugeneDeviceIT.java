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

package org.cellocad.v2.placing.algorithm.Eugene.target.data.data;

import java.io.IOException;
import org.cellocad.v2.common.CelloException;
import org.cellocad.v2.common.Utils;
import org.cellocad.v2.common.target.data.data.OutputDevice;
import org.cellocad.v2.common.target.data.data.Structure;
import org.cellocad.v2.common.target.data.data.StructureDevice;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.junit.Test;

/**
 * Integration test for {@link EugeneDevice}.
 *
 * @author Timothy Jones
 * @date 2020-01-09
 */
public class EugeneDeviceIT {

  @Test
  public void EugeneDevice_MockStructure_ShouldReturnExpectedString()
      throws IOException, ParseException, CelloException {
    String str = Utils.getResourceAsString("structure.json");
    final JSONParser parser = new JSONParser();
    final JSONObject obj = (JSONObject) parser.parse(str);
    final Structure structure = new Structure(obj);
    str = "";
    for (final StructureDevice d : structure.getDevices()) {
      final EugeneDevice e = new EugeneDevice(d);
      str += e.toString();
    }
    final String S_REF =
        "cassette P1_PhlF_a_cassette();\n"
            + "Device P1_PhlF_a(\n"
            + "    promoter,\n"
            + "    P1_PhlF_a_cassette\n"
            + ");\n"
            + "cassette P1_PhlF_b_cassette();\n"
            + "Device P1_PhlF_b(\n"
            + "    promoter,\n"
            + "    P1_PhlF_b_cassette\n"
            + ");\n";
    assert str.equals(S_REF);
  }

  @Test
  public void EugeneDevice_MockOutputDevice_ShouldReturnExpectedString()
      throws IOException, ParseException, CelloException {
    final String deviceString = Utils.getResourceAsString("output_device.json");
    final String structureString = Utils.getResourceAsString("output_device_structure.json");
    final JSONParser parser = new JSONParser();
    final JSONObject deviceJson = (JSONObject) parser.parse(deviceString);
    final JSONObject structureJson = (JSONObject) parser.parse(structureString);
    final Structure structure = new Structure(structureJson);
    final OutputDevice device = new OutputDevice(deviceJson);
    device.setStructure(structure);
    String str = "";
    for (final StructureDevice d : structure.getDevices()) {
      final EugeneDevice e = new EugeneDevice(d);
      str += e.toString();
    }
    final String S_REF =
        "Device YFP_reporter(\n"
            + "    promoter,\n"
            + "    promoter,\n"
            + "    YFP_cassette\n"
            + ");\n";
    assert str.equals(S_REF);
  }
}

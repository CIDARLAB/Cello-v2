/*
 * Copyright (C) 2019 Boston University (BU)
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

package org.cellocad.v2.technologyMapping.common;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import org.cellocad.v2.common.CObjectCollection;
import org.cellocad.v2.common.exception.CelloException;
import org.cellocad.v2.common.target.data.TargetDataInstance;
import org.cellocad.v2.common.target.data.data.Gate;
import org.cellocad.v2.common.target.data.data.Input;
import org.cellocad.v2.common.target.data.data.InputSensor;
import org.cellocad.v2.common.target.data.data.Part;
import org.cellocad.v2.results.logicSynthesis.LSResultsUtils;
import org.cellocad.v2.results.netlist.NetlistEdge;
import org.cellocad.v2.results.netlist.NetlistNode;

/**
 * Utility methods for the <i>technologyMapping</i> stage.
 *
 * @author Timothy Jones
 * @date 2019-02-20
 */
public class TMUtils {

  /**
   * Get the version of this project.
   *
   * @return The version of this project.
   * @throws CelloException Unable to get the version.
   */
  public static String getVersion() throws CelloException {
    String rtn = null;
    final Properties properties = new Properties();
    try {
      properties.load(
          TMUtils.class.getClassLoader().getResourceAsStream("cello-technologymapping.properties"));
    } catch (IOException e) {
      throw new CelloException("Unable to get version.");
    }
    rtn = properties.getProperty("org.cellocad.v2.cello-technologymapping.version");
    return rtn;
  }

  /**
   * Gets the location of a resource as a {@link URL} object.
   *
   * @param resource A resource name.
   * @return The resource location as a {@link URL} object.
   */
  public static URL getResource(final String resource) {
    URL rtn = null;
    rtn = TMUtils.class.getClassLoader().getResource(resource);
    return rtn;
  }

  /**
   * Reads the given resource as a string.
   *
   * @param resource A resource name.
   * @return The given resource as a string.
   * @throws IOException Unable to load the given resource.
   */
  public static String getResourceAsString(final String resource) throws IOException {
    String rtn = "";
    final InputStream is = TMUtils.getResource(resource).openStream();
    final InputStreamReader isr = new InputStreamReader(is);
    final BufferedReader br = new BufferedReader(isr);
    final StringBuffer sb = new StringBuffer();
    String line;
    while ((line = br.readLine()) != null) {
      sb.append(line);
    }
    br.close();
    isr.close();
    is.close();
    rtn = sb.toString();
    return rtn;
  }

  /**
   * Get the {@link Part} objects that act as inputs to the given node.
   *
   * @param node A {@link NetlistNode}.
   * @param tdi The {@link TargetDataInstance} that describes the parts.
   * @return A collection of {@link Part} objects that act as inputs to the given node.
   */
  public static CObjectCollection<Part> getInputParts(
      final NetlistNode node, final TargetDataInstance tdi) {
    CObjectCollection<Part> rtn = new CObjectCollection<>();
    for (int i = 0; i < node.getNumInEdge(); i++) {
      final NetlistEdge e = node.getInEdgeAtIdx(i);
      final NetlistNode src = e.getSrc();
      String input = "";
      final String gateType = src.getResultNetlistNodeData().getDeviceName();
      if (LSResultsUtils.isAllInput(src)) {
        final InputSensor sensor = tdi.getInputSensors().findCObjectByName(gateType);
        input = sensor.getStructure().getOutputs().get(0);
      } else {
        final Gate gate = tdi.getGates().findCObjectByName(gateType);
        if (gate == null) {
          new RuntimeException("Unknown gate.");
        }
        input = gate.getStructure().getOutputs().get(0);
      }
      final Part part = tdi.getParts().findCObjectByName(input);
      rtn.add(part);
    }
    return rtn;
  }

  /**
   * Get the {@link Part} objects that act as inputs to the given node.
   *
   * @param node A {@link NetlistNode}.
   * @param tdi The {@link TargetDataInstance} that describes the parts.
   * @return A collection of {@link Part} objects that act as inputs to the given node.
   */
  public static Map<Input, Part> getInputs(final NetlistNode node, final TargetDataInstance tdi) {
    Map<Input, Part> rtn = new HashMap<>();
    for (int i = 0; i < node.getNumInEdge(); i++) {
      final NetlistEdge e = node.getInEdgeAtIdx(i);
      final NetlistNode src = e.getSrc();
      Input input = e.getResultNetlistEdgeData().getInput();
      String inputPartName = "";
      final String gateType = src.getResultNetlistNodeData().getDevice().getName();
      if (LSResultsUtils.isAllInput(src)) {
        final InputSensor sensor = tdi.getInputSensors().findCObjectByName(gateType);
        inputPartName = sensor.getStructure().getOutputs().get(0);
      } else {
        final Gate gate = tdi.getGates().findCObjectByName(gateType);
        if (gate == null) {
          new RuntimeException("Unknown gate.");
        }
        inputPartName = gate.getStructure().getOutputs().get(0);
      }
      final Part part = tdi.getParts().findCObjectByName(inputPartName);
      rtn.put(input, part);
    }
    return rtn;
  }
}

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

package org.cellocad.v2.placing.algorithm.Eugene;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.cellocad.v2.common.CObjectCollection;
import org.cellocad.v2.common.target.data.TargetDataInstance;
import org.cellocad.v2.common.target.data.data.Gate;
import org.cellocad.v2.common.target.data.data.Input;
import org.cellocad.v2.common.target.data.data.InputSensor;
import org.cellocad.v2.common.target.data.data.OutputDevice;
import org.cellocad.v2.common.target.data.data.Part;
import org.cellocad.v2.common.target.data.data.StructureDevice;
import org.cellocad.v2.common.target.data.data.StructureObject;
import org.cellocad.v2.common.target.data.data.StructurePart;
import org.cellocad.v2.common.target.data.data.StructureTemplate;
import org.cellocad.v2.results.logicSynthesis.LSResultsUtils;
import org.cellocad.v2.results.netlist.NetlistEdge;
import org.cellocad.v2.results.netlist.NetlistNode;

/**
 * Utility methods for the <i>Eugene</i> algorithm in the <i>placing</i> stage.
 *
 * @author Timothy Jones
 * @date 2019-05-29
 */
public class EugeneUtils {

  /**
   * Get the name of a device to be used in a Eugene program.
   *
   * @param name The name of the device.
   * @return The name of a device to be used in a Eugene program.
   */
  public static String getDeviceDeviceName(final String name) {
    String rtn = null;
    rtn = name + "Device";
    return rtn;
  }

  /**
   * Get the base name of a device that was used in a Eugene program.
   *
   * @param name The name of a device that was used in a Eugene program.
   * @return The base name.
   */
  public static String getDeviceBaseName(final String name) {
    String rtn = null;
    rtn = name.replaceAll("Device$", "");
    return rtn;
  }

  /**
   * Get a Eugene {@code PartType} definition for the given Cello part type.
   *
   * <p>Example:
   *
   * <pre>
   * getPartTypeDefinition("promoter"); // returns "PartType promoter;"
   * </pre>
   *
   * @param type A Ceello part type, e.g. {@code promoter}.
   * @return A Eugene {@code PartType} definition for the given Cello part type.
   */
  public static String getPartTypeDefinition(final String type) {
    String rtn = "";
    rtn = String.format("PartType %s;", type);
    return rtn;
  }

  /**
   * Get all the part types of the parts that appear in the given device.
   *
   * @param device A {@link StructureDevice}.
   * @param parts The {@link Part} objects that describe the part names in the given device.
   * @return All the part types of the parts that appear in the given device.
   */
  public static Set<String> getPartTypes(
      final StructureDevice device, final CObjectCollection<Part> parts) {
    final Set<String> rtn = new HashSet<>();
    for (final StructureObject o : device.getComponents()) {
      if (o instanceof StructurePart) {
        final Part p = parts.findCObjectByName(o.getName());
        if (p != null) {
          rtn.add(p.getPartType());
        }
      }
      if (o instanceof StructureTemplate) {
        final StructureTemplate t = (StructureTemplate) o;
        rtn.add(t.getInput().getPartType());
      }
      if (o instanceof StructureDevice) {
        final StructureDevice d = (StructureDevice) o;
        rtn.addAll(EugeneUtils.getPartTypes(d, parts));
      }
    }
    return rtn;
  }

  /**
   * Get the Eugene part definition for the given Cello {@link Part}.
   *
   * @param part A {@link Part}.
   * @return The Eugene part definition for the given Cello {@link Part}.
   */
  public static String getPartDefinition(final Part part) {
    String rtn = "";
    final String type = part.getPartType();
    final String name = part.getName();
    final String seq = part.getDnaSequence();
    rtn = String.format("%s %s(.SEQUENCE(\"%s\"));", type, name, seq);
    return rtn;
  }

  /**
   * Get all the part definitions for the parts that appear in the given device.
   *
   * @param device A {@link StructureDevice}.
   * @param parts The {@link Part} objects that describe the part names in the given device.
   * @return
   */
  public static Set<String> getPartDefinitions(
      final StructureDevice device, final CObjectCollection<Part> parts) {
    final Set<String> rtn = new HashSet<>();
    for (final StructureObject o : device.getComponents()) {
      if (o instanceof StructurePart) {
        final Part p = parts.findCObjectByName(o.getName());
        if (p != null) {
          rtn.add(EugeneUtils.getPartDefinition(p));
        }
      }
      if (o instanceof StructureDevice) {
        final StructureDevice d = (StructureDevice) o;
        rtn.addAll(EugeneUtils.getPartDefinitions(d, parts));
      }
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
  public static Map<Input, Part> getInputsMap(
      final NetlistNode node, final TargetDataInstance tdi) {
    Map<Input, Part> rtn = new HashMap<>();
    for (int i = 0; i < node.getNumInEdge(); i++) {
      final NetlistEdge e = node.getInEdgeAtIdx(i);
      final NetlistNode src = e.getSrc();
      Input input = e.getResultNetlistEdgeData().getInput();
      String inputPartName = "";
      final String gateType = src.getResultNetlistNodeData().getDeviceName();
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

  /**
   * Get the {@link Part} objects that act as inputs to the given node.
   *
   * @param node A {@link NetlistNode}.
   * @param tdi The {@link TargetDataInstance} that describes the parts.
   * @return A collection of {@link Part} objects that act as inputs to the given node.
   */
  public static CObjectCollection<Part> getInputs(
      final NetlistNode node, final TargetDataInstance tdi) {
    final CObjectCollection<Part> rtn = new CObjectCollection<>();
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
   * Obtain the StructureDevice objects associated with a given node. If more device objects are
   * specified in a gate than there are inputs to the node, the extra devices will be discarded.
   *
   * @param node The {@link NetlistNode}.
   * @param gates The Gate objects.
   * @param sensors The InputSensor objects.
   * @param reporters The OutputReporter objects.
   * @return A collection of StructureDevice objects associated with the NetlistNode.
   */
  static Collection<StructureDevice> getDevices(
      final NetlistNode node,
      final TargetDataInstance tdi) {
    final Collection<StructureDevice> rtn = new ArrayList<>();
    final String gateType = node.getResultNetlistNodeData().getDeviceName();
    final Integer num = node.getNumInEdge();
    final Gate gate = tdi.getGates().findCObjectByName(gateType);
    final OutputDevice reporter = tdi.getOutputDevices().findCObjectByName(gateType);
    if (reporter != null) {
      final Collection<StructureDevice> devices = reporter.getStructure().getDevices();
      Integer i = 0;
      for (final StructureDevice d : devices) {
        final StructureDevice e = new StructureDevice(d);
        final Collection<StructureTemplate> inputs = new ArrayList<>();
        for (final StructureObject o : e.getComponents()) {
          if (o instanceof StructureTemplate) {
            i++;
            final StructureTemplate t = (StructureTemplate) o;
            if (i <= num) {
              continue;
            }
            inputs.add(t);
          }
        }
        for (final StructureTemplate t : inputs) {
          e.getComponents().remove(t);
        }
        for (final StructureObject o : e.getComponents()) {
          if (o instanceof StructureTemplate) {
            rtn.add(e);
            break;
          }
        }
      }
    }
    if (gate != null) {
      final Collection<StructureDevice> devices = gate.getStructure().getDevices();
      Integer i = 0;
      Map<Input, Part> inputMap = getInputsMap(node, tdi);
      for (final StructureDevice d : devices) {
        final StructureDevice e = new StructureDevice(d);
        final Collection<StructureTemplate> inputs = new ArrayList<>();
        final Collection<StructureTemplate> empty = new ArrayList<>();
        for (final StructureObject o : e.getComponents()) {
          if (o instanceof StructureTemplate) {
            i++;
            final StructureTemplate t = (StructureTemplate) o;
            for (Input input : inputMap.keySet()) {
              if (input.getName().equals(o.getName())) {
                inputs.add(t);
              }
            }
          }
        }
        for (final StructureObject o : e.getComponents()) {
          if (o instanceof StructureTemplate) {
            final StructureTemplate t = (StructureTemplate) o;
            if (!inputs.contains(t)) {
              empty.add(t);
            }
          }
        }
        for (StructureTemplate t : empty) {
          e.getComponents().remove(t);
        }
        for (final StructureObject o : e.getComponents()) {
          if (o instanceof StructureTemplate) {
            rtn.add(e);
            break;
          }
        }
      }
    }
    return rtn;
  }
}

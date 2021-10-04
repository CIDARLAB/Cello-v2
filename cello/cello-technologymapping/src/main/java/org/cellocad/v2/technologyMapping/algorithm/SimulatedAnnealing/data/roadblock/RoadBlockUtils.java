/*
 * Copyright (C) 2020 Boston University (BU)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package org.cellocad.v2.technologyMapping.algorithm.SimulatedAnnealing.data.roadblock;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.lang3.StringUtils;
import org.cellocad.v2.common.CObjectCollection;
import org.cellocad.v2.common.target.data.TargetDataInstance;
import org.cellocad.v2.common.target.data.data.DeviceRules;
import org.cellocad.v2.common.target.data.data.Input;
import org.cellocad.v2.common.target.data.data.Part;
import org.cellocad.v2.common.target.data.data.StructureDevice;
import org.cellocad.v2.common.target.data.data.StructureObject;
import org.cellocad.v2.common.target.data.data.StructureTemplate;
import org.cellocad.v2.results.netlist.Netlist;
import org.cellocad.v2.results.netlist.NetlistEdge;
import org.cellocad.v2.results.netlist.NetlistNode;
import org.cellocad.v2.technologyMapping.common.TMUtils;

/**
 * Tests for roadblocking.
 *
 * @author Timothy Jones
 * @date 2020-06-07
 */
public class RoadBlockUtils {

  /**
   * Gets the number of roadblocked nodes in a netlist.
   *
   * @param netlist The netlist.
   * @return The number of roadblocked nodes in a netlist.
   */
  public static Integer getNumberOfRoadBlockedNodes(
      final Netlist netlist, final DeviceRules rules, final TargetDataInstance tdi) {
    Integer rtn = 0;
    for (int i = 0; i < netlist.getNumVertex(); i++) {
      NetlistNode node = netlist.getVertexAtIdx(i);
      if (isNodeRoadBlocked(node, rules, tdi)) {
        rtn++;
      }
    }
    return rtn;
  }

  /**
   * Tests whether any node in the given netlist has a roadblocking set of inputs.
   *
   * @param netlist The netlist.
   * @return Whether any node in the given netlist has a roadblocking set of inputs.
   */
  public static Boolean isNetlistRoadBlocked(
      final Netlist netlist, final DeviceRules rules, final TargetDataInstance tdi) {
    Boolean rtn = false;
    for (int i = 0; i < netlist.getNumVertex(); i++) {
      NetlistNode node = netlist.getVertexAtIdx(i);
      rtn = isNodeRoadBlocked(node, rules, tdi);
      if (rtn) {
        break;
      }
    }
    return rtn;
  }

  private static void addDownstreamNodesToCollection(
      final NetlistNode node, Collection<NetlistNode> nodes) {
    for (int i = 0; i < node.getNumOutEdge(); i++) {
      NetlistEdge e = node.getOutEdgeAtIdx(i);
      NetlistNode dst = e.getDst();
      nodes.add(dst);
    }
  }

  /**
   * Tests whether a node as well as its downstream neighbors are roadblocked.
   *
   * @param node The node.
   * @param rules The rules.
   * @param tdi The target data instance.
   * @return Whether a node as well as its downstream neighbors are roadblocked.
   */
  public static Boolean areNodeAndDownstreamNeighborsRoadBlocked(
      final NetlistNode node, final DeviceRules rules, final TargetDataInstance tdi) {
    Boolean rtn = false;
    Collection<NetlistNode> nodes = new ArrayList<>();
    nodes.add(node);
    addDownstreamNodesToCollection(node, nodes);
    for (NetlistNode n : nodes) {
      if (isNodeRoadBlocked(n, rules, tdi)) {
        rtn = true;
        break;
      }
    }
    return rtn;
  }

  private static Boolean isRoadBlockable(final StructureDevice device) {
    Boolean rtn = false;
    Integer consecutiveInputsSeen = 0;
    // TODO Lazy implementation. This assumes all inputs are at the beginning.
    for (StructureObject obj : device.getComponents()) {
      if (obj instanceof StructureTemplate) {
        consecutiveInputsSeen++;
      } else {
        break;
      }
    }
    if (consecutiveInputsSeen > 1) {
      rtn = true;
    }
    return rtn;
  }

  /**
   * Tests whether a node has a roadblocking set of inputs.
   *
   * @param node The node.
   * @param rules The rules.
   * @param tdi The target data instance.
   * @return Whether a node has a roadblocking set of inputs.
   */
  public static Boolean isNodeRoadBlocked(
      final NetlistNode node, final DeviceRules rules, final TargetDataInstance tdi) {
    Boolean rtn = false;
    if (node.getNumInEdge() < 2) {
      return false;
    }
    Collection<StructureDevice> devices =
        node.getResultNetlistNodeData().getDevice().getStructure().getDevices();
    Map<Input, Part> inputMap = TMUtils.getInputs(node, tdi);
    for (StructureDevice device : devices) {
      if (!isRoadBlockable(device)) {
        continue;
      }
      List<Input> inputs = new ArrayList<>();
      for (StructureObject obj : device.getComponents()) {
        if (obj instanceof StructureTemplate) {
          for (Input input : inputMap.keySet()) {
            if (input.getName().equals(obj.getName())) {
              inputs.add(input);
            }
          }
        }
      }
      CObjectCollection<Part> parts = new CObjectCollection<>();
      parts.addAll(inputMap.values());
      String rule = rules.filter(device, parts);
      if (StringUtils.countMatches(rule, "STARTSWITH") > 1) {
        return true;
      }
      for (int i = 0; i < inputs.size(); i++) {
        Input input = inputs.get(i);
        // startswith
        Pattern r = Pattern.compile("STARTSWITH " + inputMap.get(input).getName(), Pattern.DOTALL);
        Matcher m = r.matcher(rule);
        if (i > 0 && m.matches()) {
          return true;
        }
        // before
        r = Pattern.compile(".*" + inputMap.get(input).getName() + " BEFORE ([A-Za-z0-9]+).*", Pattern.DOTALL);
        m = r.matcher(rule);
        if (m.matches()) {
          for (int j = 0; j < inputs.size(); j++) {
            if (i == j) {
              continue;
            }
            Input other = inputs.get(j);
            if (inputMap.get(other).getName().equals(m.group(1)) && j < i) {
              return true;
            }
          }
        }
        // after
        r = Pattern.compile(".*" + inputMap.get(input).getName() + " AFTER ([A-Za-z0-9]+).*", Pattern.DOTALL);
        m = r.matcher(rule);
        if (m.matches()) {
          for (int j = 0; j < inputs.size(); j++) {
            if (i == j) {
              continue;
            }
            Input other = inputs.get(j);
            if (inputMap.get(other).getName().equals(m.group(1)) && j > i) {
              return true;
            }
          }
        }
      }
    }
    return rtn;
  }
}

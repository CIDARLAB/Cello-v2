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

package org.cellocad.v2.logicSynthesis.algorithm.Yosys;

import edu.byu.ece.edif.core.EdifCellInstance;
import edu.byu.ece.edif.core.EdifEnvironment;
import edu.byu.ece.edif.core.EdifNet;
import edu.byu.ece.edif.core.EdifPortRef;
import edu.byu.ece.edif.util.parse.EdifParser;
import edu.byu.ece.edif.util.parse.ParseException;
import java.io.FileNotFoundException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import org.cellocad.v2.common.Utils;
import org.cellocad.v2.common.graph.AbstractVertex.VertexType;
import org.cellocad.v2.results.logicSynthesis.LSResults;
import org.cellocad.v2.results.netlist.Netlist;
import org.cellocad.v2.results.netlist.NetlistEdge;
import org.cellocad.v2.results.netlist.NetlistNode;

/**
 * The YosysEdifUtils class is class with utility methods for the <i>Yosys</i> instances.
 *
 * @author Vincent Mirian
 * @date 2018-05-21
 */
public class YosysEdifUtils {

  /*
   * EDIF
   */
  /**
   * Translates the EDIF file referenced by parameter {@code filename} into the Netlist in parameter
   * {@code netlist} using the <i>Yosys</i> algorithm instance.
   *
   * @param yosys The <i>Yosys</i> algorithm instance.
   * @param filename The EDIF file.
   * @param netlist The netlist.
   * @throws RuntimeException if: <br>
   *     Any of the parameters are null<br>
   *     Error accessing {@code filename}<br>
   *     Error parsing {@code filename}<br>
   *     .
   */
  public static void convertEdifToNetlist(
      final Yosys yosys, final String filename, final Netlist netlist) {
    Utils.isNullRuntimeException(yosys, "yosys");
    Utils.isNullRuntimeException(filename, "filename");
    Utils.isNullRuntimeException(netlist, "netlist");
    EdifEnvironment edifEnv = null;
    try {
      edifEnv = EdifParser.translate(filename);
    } catch (FileNotFoundException | ParseException e) {
      e.printStackTrace();
    }
    final EdifCellInstance top = edifEnv.getTopDesign().getTopCellInstance();
    netlist.setName(top.getOldName());
    final Map<String, NetlistNode> map = new HashMap<>();
    final Collection<EdifNet> nets = top.getCellType().getNetList();
    EdifCellInstance srcCell = null;
    EdifCellInstance dstCell = null;
    NetlistNode srcNode = null;
    NetlistNode dstNode = null;
    String type = null;
    for (final EdifNet net : nets) {
      // Top Input
      if (YosysEdifUtils.hasTopInput(net)) {
        // Top PortRef
        for (final EdifPortRef topPortRef : net.getInputPortRefs()) {
          if (topPortRef.isTopLevelPortRef()) {
            srcNode =
                YosysEdifUtils.getNode(
                    topPortRef.getSingleBitPort().getPortName(),
                    LSResults.S_PRIMARYINPUT,
                    map,
                    netlist);
            srcNode.setVertexType(VertexType.SOURCE);
            // Other Input/Output PortRef
            for (final EdifPortRef otherPortRef : net.getPortRefList()) {
              if (topPortRef == otherPortRef) {
                continue;
              }
              final EdifCellInstance cell = otherPortRef.getCellInstance();
              // generic
              if (cell != null) {
                assert otherPortRef.getPort().isInput();
                type = YosysUtils.getNodeType(cell.getCellType().getOldName());
                dstNode = YosysEdifUtils.getNode(cell.getName(), type, map, netlist);
                dstNode.setVertexType(VertexType.NONE);
              } else { // Top output
                assert otherPortRef.getPort().isOutput();
                dstNode =
                    YosysEdifUtils.getNode(
                        otherPortRef.getSingleBitPort().getPortName(),
                        LSResults.S_PRIMARYOUTPUT,
                        map,
                        netlist);
                dstNode.setVertexType(VertexType.SINK);
              }
              // setEdge
              YosysEdifUtils.setEdge(srcNode, dstNode, net, netlist);
            }
          }
        }
      } else if (YosysEdifUtils.hasTopOutput(net)) { // Top Output
        // Top PortRef
        for (final EdifPortRef topPortRef : net.getOutputPortRefs()) {
          if (topPortRef.isTopLevelPortRef()) {
            dstNode =
                YosysEdifUtils.getNode(
                    topPortRef.getSingleBitPort().getPortName(),
                    LSResults.S_PRIMARYOUTPUT,
                    map,
                    netlist);
            dstNode.setVertexType(VertexType.SINK);
            // Other Output PortRef
            for (final EdifPortRef otherPortRef : net.getOutputPortRefs()) {
              if (topPortRef == otherPortRef) {
                continue;
              }
              final EdifCellInstance cell = otherPortRef.getCellInstance();
              // generic
              assert otherPortRef.getPort().isOutput();
              type = YosysUtils.getNodeType(cell.getCellType().getOldName());
              srcNode = YosysEdifUtils.getNode(cell.getName(), type, map, netlist);
              srcNode.setVertexType(VertexType.NONE);
              // setEdge
              YosysEdifUtils.setEdge(srcNode, dstNode, net, netlist);
            }
          }
        }
      } else { // Other
        // Outputs
        for (final EdifPortRef outputPortRef : net.getOutputPortRefs()) {
          srcCell = outputPortRef.getCellInstance();
          // create vertex if not present
          type = YosysUtils.getNodeType(srcCell.getCellType().getOldName());
          srcNode = YosysEdifUtils.getNode(srcCell.getName(), type, map, netlist);
          srcNode.setVertexType(VertexType.NONE);
          for (final EdifPortRef inputPortRef : net.getInputPortRefs()) {
            dstCell = inputPortRef.getCellInstance();
            // create vertex if not present
            type = YosysUtils.getNodeType(dstCell.getCellType().getOldName());
            dstNode = YosysEdifUtils.getNode(dstCell.getName(), type, map, netlist);
            dstNode.setVertexType(VertexType.NONE);
            // setEdge
            YosysEdifUtils.setEdge(srcNode, dstNode, net, netlist);
          }
        }
      }
    }
  }

  /**
   * Initializes a NetlistEdge in the Netlist defined by parameter {@code netlist} with: attributes
   * defined by parameter {@code net}, source node defined by parameter {@code src}, and,
   * destination node defined by parameter {@code dst}.
   *
   * @param src The source node.
   * @param dst The destination node.
   * @param net The attributes.
   * @param netlist The {@link Netlist}.
   */
  protected static void setEdge(
      final NetlistNode src, final NetlistNode dst, final EdifNet net, final Netlist netlist) {
    final NetlistEdge edge = new NetlistEdge(src, dst);
    edge.setName(net.getOldName());
    src.addOutEdge(edge);
    dst.addInEdge(edge);
    netlist.addEdge(edge);
  }

  /**
   * Assigns the {@link NetlistNode} with its name defined by parameter {@code name} with result for
   * the <i>logicSynthesis</i> stage defined by parameter {@code type}. If the {@link NetlistNode}
   * with its name defined by parameter {@code name} is not present in the translation bookeeper
   * defined by parameter {@code S_MAP}, then initialize a new {@link NetlistNode} and insert it
   * into the {@link Netlist} defined by parameter {@code netlist}. Returns the assigned or
   * initialized {@link NetlistNode}.
   *
   * @param name The name of the {@link NetlistNode}.
   * @param type The result for the <i>logicSynthesis</i> stage.
   * @param map The translation bookeeper.
   * @param netlist The {@link Netlist}.
   * @return The assigned or initialized {@link NetlistNode}.
   */
  protected static NetlistNode getNode(
      final String name,
      final String type,
      final Map<String, NetlistNode> map,
      final Netlist netlist) {
    NetlistNode rtn = null;
    rtn = map.get(name);
    if (rtn == null) {
      rtn = new NetlistNode();
      rtn.setName(name);
      rtn.getResultNetlistNodeData().setNodeType(type);
      netlist.addVertex(rtn);
      map.put(name, rtn);
    }
    return rtn;
  }

  /**
   * Returns a boolean flag signifying that the parameter {@code net} is connected to an input port.
   *
   * @param net The Net.
   * @return True if the parameter {@code net} is connected to an input port, otherwise false.
   */
  protected static boolean hasTopInput(final EdifNet net) {
    boolean rtn = false;
    for (final EdifPortRef portRef : net.getInputPortRefs()) {
      rtn = rtn || portRef.isTopLevelPortRef();
    }
    return rtn;
  }

  /**
   * Returns a boolean flag signifying that the parameter {@code net} is connected to an output
   * port.
   *
   * @param net The Net.
   * @return True if the parameter {@code net} is connected to an output port, otherwise false.
   */
  protected static boolean hasTopOutput(final EdifNet net) {
    boolean rtn = false;
    for (final EdifPortRef portRef : net.getOutputPortRefs()) {
      rtn = rtn || portRef.isTopLevelPortRef();
    }
    return rtn;
  }
}

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

package org.cellocad.v2.placing.algorithm.eugene.data;

import java.awt.Color;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.cellocad.v2.common.CelloException;
import org.cellocad.v2.common.Utils;
import org.cellocad.v2.common.target.data.TargetDataInstance;
import org.cellocad.v2.common.target.data.data.AssignableDevice;
import org.cellocad.v2.common.target.data.data.Gate;
import org.cellocad.v2.common.target.data.data.Part;
import org.cellocad.v2.common.target.data.data.Structure;
import org.cellocad.v2.common.target.data.data.StructureDevice;
import org.cellocad.v2.common.target.data.data.StructureObject;
import org.cellocad.v2.common.target.data.data.StructurePart;
import org.cellocad.v2.common.target.data.data.StructureTemplate;
import org.cellocad.v2.results.logicSynthesis.LSResultsUtils;
import org.cellocad.v2.results.netlist.Netlist;
import org.cellocad.v2.results.netlist.NetlistEdge;
import org.cellocad.v2.results.netlist.NetlistNode;
import org.cellocad.v2.results.placing.placement.Component;
import org.cellocad.v2.results.placing.placement.Placement;
import org.cellocad.v2.results.placing.placement.PlacementGroup;
import org.cellocad.v2.results.placing.placement.Placements;

/**
 * Utility methods for generating dnaplotlib plots for Cello designs.
 *
 * @author Timothy Jones
 *
 * @date 2020-01-28
 */
public class DnaPlotLibUtils {

  public static void writeCSV(final Collection<String> records, final File file) {
    final String str = String.join(Utils.getNewLine(), records);
    Utils.writeToFile(str, file.getAbsolutePath());
  }

  /**
   * Gets the given {@link Color} object as an RGB string for use with dnaplotlib.
   * 
   * @param color A {@link Color} object.
   * @return The given {@link Color} object as an RGB string for use with dnaplotlib.
   */
  public static String getRgb(final Color color) {
    String rtn = null;
    final Double r = color.getRed() / 255.0;
    final Double g = color.getGreen() / 255.0;
    final Double b = color.getBlue() / 255.0;
    rtn = String.format("%.2f;%.2f;%.2f", r, g, b);
    return rtn;
  }

  /**
   * Gets the given Cello part type as a dnaplotlib-compatible part type string.
   * 
   * @param type A Cello part type, e.g. {@code promoter} or {@code rbs}.
   * @return A dnaplotlib-compatible part type string if one exists, otherwise null.
   */
  public static String getPartType(final String type) {
    String rtn = null;
    if (type.equalsIgnoreCase("promoter")) {
      rtn = DnaPlotLibUtils.S_PROMOTER;
    } else if (type.equalsIgnoreCase("ribozyme")) {
      rtn = DnaPlotLibUtils.S_RIBOZYME;
    } else if (type.equalsIgnoreCase("rbs")) {
      rtn = DnaPlotLibUtils.S_RBS;
    } else if (type.equalsIgnoreCase("cds")) {
      rtn = DnaPlotLibUtils.S_CDS;
    } else if (type.equalsIgnoreCase("terminator")) {
      rtn = DnaPlotLibUtils.S_TERMINATOR;
    }
    return rtn;
  }

  private static Collection<String> getFlattenedPartList(final StructureDevice device)
      throws CelloException {
    final Collection<String> rtn = new ArrayList<>();
    for (final StructureObject o : device.getComponents()) {
      if (o instanceof StructureTemplate) {
        throw new CelloException("Cannot flatten with unreferenced input parts.");
      }
      if (o instanceof StructurePart) {
        rtn.add(o.getName());
      }
      if (o instanceof StructureDevice) {
        rtn.addAll(DnaPlotLibUtils.getFlattenedPartList((StructureDevice) o));
      }
    }
    return rtn;
  }

  private static Collection<String> unNestDevice(final String object, final Component component,
      final Netlist netlist, final TargetDataInstance tdi) throws CelloException {
    final Collection<String> rtn = new ArrayList<>();
    final NetlistNode node = netlist.getVertexByName(component.getNode());
    final String deviceName = node.getResultNetlistNodeData().getDeviceName();
    final AssignableDevice device = tdi.getAssignableDeviceByName(deviceName);
    final Structure structure = device.getStructure();
    final StructureDevice sd = structure.getDeviceByName(object);
    if (sd != null) {
      rtn.addAll(DnaPlotLibUtils.getFlattenedPartList(sd));
    } else {
      rtn.add(object);
    }
    return rtn;
  }

  /**
   * Gets a list of CSV records representing the Cello designs in dnaplotlib format.
   * 
   * @param netlist The netlist containing the designs.
   * @param tdi     The target data instance containing the data used in the designs.
   * @return A list of CSV records representing the Cello designs in dnaplotlib format.
   * @throws CelloException Unable to generate the Cello designs in dnaplotlib format.
   */
  public static List<String> getDnaDesigns(final Netlist netlist, final TargetDataInstance tdi)
      throws CelloException {
    final List<String> rtn = new ArrayList<>();
    rtn.add("design_name,parts,");
    final Placements placements = netlist.getResultNetlistData().getPlacements();
    for (int i = 0; i < placements.getNumPlacement(); i++) {
      final Collection<String> design = new ArrayList<>();
      final Placement placement = placements.getPlacementAtIdx(i);
      String name = placement.getName();
      if (name.isEmpty()) {
        name = netlist.getName() + String.valueOf(i);
      }
      design.add(name);
      for (int j = 0; j < placement.getNumPlacementGroup(); j++) {
        final PlacementGroup group = placement.getPlacementGroupAtIdx(j);
        for (int k = 0; k < group.getNumComponent(); k++) {
          final Component component = group.getComponentAtIdx(k);
          for (int l = 0; l < component.getNumPart(); l++) {
            final String obj = component.getPartAtIdx(l);
            final Collection<String> parts =
                DnaPlotLibUtils.unNestDevice(obj, component, netlist, tdi);
            if (parts.size() == 0) {
              design.add(obj);
            } else {
              design.addAll(parts);
            }
          }
        }
        if (j + 1 < placement.getNumPlacementGroup()) {
          final String pad = String.format("%s%d", DnaPlotLibUtils.S_NONCEPAD, j);
          design.add(pad);
        }
      }
      final String record = String.join(",", design);
      rtn.add(record);
    }
    return rtn;
  }

  /**
   * Gets a list of CSV records representing the part information for Cello designs in dnaplotlib
   * format.
   * 
   * @param netlist The netlist containing the designs.
   * @param tdi     The target data instance containing the data used in the designs.
   * @return A list of CSV records representing the part information for Cello designs in dnaplotlib
   *         format.
   * @throws CelloException Unable to generate part information.
   */
  public static List<String> getPartInformation(final Netlist netlist, final TargetDataInstance tdi)
      throws CelloException {
    final List<String> rtn = new ArrayList<>();
    final List<String> specified = new ArrayList<>();
    rtn.add(
        "part_name,type,x_extent,y_extent,start_pad,end_pad,color,hatch,arrowhead_height,arrowhead_length,linestyle,linewidth");
    final Placements placements = netlist.getResultNetlistData().getPlacements();
    for (int i = 0; i < placements.getNumPlacement(); i++) {
      final Placement placement = placements.getPlacementAtIdx(i);
      for (int j = 0; j < placement.getNumPlacementGroup(); j++) {
        final PlacementGroup group = placement.getPlacementGroupAtIdx(j);
        for (int k = 0; k < group.getNumComponent(); k++) {
          final Component component = group.getComponentAtIdx(k);
          final String n = component.getNode();
          final NetlistNode node = netlist.getVertexByName(n);
          for (int l = 0; l < component.getNumPart(); l++) {
            final String obj = component.getPartAtIdx(l);
            final Collection<String> deviceParts =
                DnaPlotLibUtils.unNestDevice(obj, component, netlist, tdi);
            if (deviceParts.size() == 0) {
              deviceParts.add(obj);
            }
            for (final String p : deviceParts) {
              if (specified.contains(p)) {
                continue;
              } else {
                specified.add(p);
              }
              final Part part = tdi.getParts().findCObjectByName(p);
              String rgb = "0.0;0.0;0.0";
              String type = DnaPlotLibUtils.getPartType(part.getPartType());
              String x = "";
              String y = "";
              if (part.getPartType().equals("promoter")) {
                for (int m = 0; m < node.getNumInEdge(); m++) {
                  final NetlistEdge e = node.getInEdgeAtIdx(m);
                  final NetlistNode src = e.getSrc();
                  if (LSResultsUtils.isAllInput(src)) {
                    continue;
                  }
                  final String gateType = src.getResultNetlistNodeData().getDeviceName();
                  final Gate gate = tdi.getGates().findCObjectByName(gateType);
                  if (!gate.getStructure().getOutputs().get(0).equals(p)) {
                    continue;
                  }
                  final Color color = gate.getColor();
                  rgb = DnaPlotLibUtils.getRgb(color);
                }
              } else {
                final String gateType = node.getResultNetlistNodeData().getDeviceName();
                final Gate gate = tdi.getGates().findCObjectByName(gateType);
                if (gate != null) {
                  final Color color = gate.getColor();
                  rgb = DnaPlotLibUtils.getRgb(color);
                }
                if (LSResultsUtils.isPrimaryOutput(node)) {
                  type = DnaPlotLibUtils.S_USERDEFINED;
                  rgb = DnaPlotLibUtils.getRgb(Color.BLACK);
                  x = String.valueOf(25);
                  y = String.valueOf(5);
                }
              }
              rtn.add(String.format("%s,%s,%s,%s,,,%s,,,,,", p, type, x, y, rgb));
            }
          }
        }
        if (j + 1 < placement.getNumPlacementGroup()) {
          final String pad = String.format("%s%d,%s,30,,,,1.00;1.00;1.00,,,,,",
              DnaPlotLibUtils.S_NONCEPAD, j, DnaPlotLibUtils.S_USERDEFINED);
          rtn.add(pad);
        }

      }
    }
    return rtn;
  }

  /**
   * Gets a list of CSV records representing the regulatory information for Cello designs in
   * dnaplotlib format.
   * 
   * @param netlist The netlist containing the designs.
   * @param tdi     The target data instance containing the data used in the designs.
   * @return A list of CSV records representing the regulatory information for Cello designs in
   *         dnaplotlib format.
   * @throws CelloException Unable to generate regulatory information.
   */
  public static List<String> getRegulatoryInformation(final Netlist netlist,
      final TargetDataInstance tdi) throws CelloException {
    final List<String> rtn = new ArrayList<>();
    final Set<String> specified = new HashSet<>();
    rtn.add("from_partname,type,to_partname,arrowhead_length,linestyle,linewidth,color");
    // TAG hardcoded
    final Placements placements = netlist.getResultNetlistData().getPlacements();
    for (int i = 0; i < placements.getNumPlacement(); i++) {
      if (i == 1) {
        break;
      }
      final Placement placement = placements.getPlacementAtIdx(i);
      for (int j = 0; j < placement.getNumPlacementGroup(); j++) {
        final PlacementGroup group = placement.getPlacementGroupAtIdx(j);
        for (int k = 0; k < group.getNumComponent(); k++) {
          final org.cellocad.v2.results.placing.placement.Component component =
              group.getComponentAtIdx(k);
          final String n = component.getNode();
          final NetlistNode node = netlist.getVertexByName(n);
          if (LSResultsUtils.isAllInput(node) || LSResultsUtils.isAllOutput(node)) {
            continue;
          }
          final String gateType = node.getResultNetlistNodeData().getDeviceName();
          final Gate gate = tdi.getGates().findCObjectByName(gateType);
          final Structure gs = gate.getStructure();
          final Color color = gate.getColor();
          for (int l = 0; l < component.getNumPart(); l++) {
            final String obj = component.getPartAtIdx(l);
            final Collection<String> deviceParts =
                DnaPlotLibUtils.unNestDevice(obj, component, netlist, tdi);
            if (deviceParts.size() == 0) {
              deviceParts.add(obj);
            }
            for (final String p : deviceParts) {
              final Part part = tdi.getParts().findCObjectByName(p);
              if (part.getPartType().equals("cds")) {
                final Collection<String> fields = new ArrayList<>();
                fields.add(part.getName());
                fields.add(DnaPlotLibUtils.S_REPRESSION);
                fields.add(gs.getOutputs().get(0));
                fields.add(String.valueOf(3));
                fields.add("-");
                fields.add("");
                fields.add(DnaPlotLibUtils.getRgb(color));
                final String str = String.join(",", fields);
                specified.add(str);
              }
            }
          }
        }
      }
    }
    rtn.addAll(specified);
    return rtn;
  }

  private static String S_PROMOTER = "Promoter";
  private static String S_RIBOZYME = "Ribozyme";
  private static String S_RBS = "RBS";
  private static String S_CDS = "CDS";
  private static String S_TERMINATOR = "Terminator";
  private static String S_REPRESSION = "Repression";
  private static String S_USERDEFINED = "UserDefined";
  private static String S_NONCEPAD = "_NONCE_PAD";

}

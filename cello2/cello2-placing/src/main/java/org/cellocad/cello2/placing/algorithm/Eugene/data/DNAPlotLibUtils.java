/**
 * Copyright (C) 2020 Boston University (BU)
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:

 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.

 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package org.cellocad.cello2.placing.algorithm.Eugene.data;

import java.awt.Color;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.cellocad.cello2.common.CObjectCollection;
import org.cellocad.cello2.common.Utils;
import org.cellocad.cello2.placing.algorithm.Eugene.data.ucf.CasetteParts;
import org.cellocad.cello2.placing.algorithm.Eugene.data.ucf.Gate;
import org.cellocad.cello2.placing.algorithm.Eugene.data.ucf.GateParts;
import org.cellocad.cello2.placing.algorithm.Eugene.data.ucf.Part;
import org.cellocad.cello2.results.logicSynthesis.LSResultsUtils;
import org.cellocad.cello2.results.netlist.Netlist;
import org.cellocad.cello2.results.netlist.NetlistEdge;
import org.cellocad.cello2.results.netlist.NetlistNode;
import org.cellocad.cello2.results.placing.placement.Placement;
import org.cellocad.cello2.results.placing.placement.PlacementGroup;
import org.cellocad.cello2.results.placing.placement.Placements;

/**
 *
 *
 * @author Timothy Jones
 *
 * @date 2020-01-28
 *
 */
public class DNAPlotLibUtils {

	public static void writeCSV(final Collection<String> records, final File file) {
		String str = String.join(Utils.getNewLine(), records);
		Utils.writeToFile(str, file.getAbsolutePath());
	}

	public static String getRGB(Color color) {
		String rtn = null;
		Double r = color.getRed() / 255.0;
		Double g = color.getGreen() / 255.0;
		Double b = color.getBlue() / 255.0;
		rtn = String.format("%.2f;%.2f;%.2f", r, g, b);
		return rtn;
	}

	public static String getPartType(String type) {
		String rtn = null;
		if (type.equalsIgnoreCase("promoter")) {
			rtn = S_PROMOTER;
		}
		else if (type.equalsIgnoreCase("ribozyme")) {
			rtn = S_RIBOZYME;
		}
		else if (type.equalsIgnoreCase("rbs")) {
			rtn = S_RBS;
		}
		else if (type.equalsIgnoreCase("cds")) {
			rtn = S_CDS;
		}
		else if (type.equalsIgnoreCase("terminator")) {
			rtn = S_TERMINATOR;
		}
		return rtn;
	}

	public static List<String> getDNADesigns(final Netlist netlist) {
		List<String> rtn = new ArrayList<>();
		rtn.add("design_name,parts,");
		Placements placements = netlist.getResultNetlistData().getPlacements();
		for (int i = 0; i < placements.getNumPlacement(); i++) {
			Collection<String> design = new ArrayList<>();
			Placement placement = placements.getPlacementAtIdx(i);
			String name = placement.getName();
			if (name.isEmpty()) {
				name = netlist.getName() + String.valueOf(i);
			}
			design.add(name);
			for (int j = 0; j < placement.getNumPlacementGroup(); j++) {
				PlacementGroup group = placement.getPlacementGroupAtIdx(j);
				for (int k = 0; k < group.getNumComponent(); k++) {
					org.cellocad.cello2.results.placing.placement.Component component = group.getComponentAtIdx(k);
					for (int l = 0; l < component.getNumPart(); l++) {
						String part = component.getPartAtIdx(l);
						design.add(part);
					}
				}
				if ((j + 1) < placement.getNumPlacementGroup()) {
					String pad = String.format("%s%d", S_NONCEPAD, j);
					design.add(pad);
				}
			}
			String record = String.join(",", design);
			rtn.add(record);
		}
		return rtn;
	}

	public static List<String> getPartInformation(Netlist netlist, final CObjectCollection<Part> parts,
			final CObjectCollection<Gate> gates) {
		List<String> rtn = new ArrayList<>();
		rtn.add("part_name,type,x_extent,y_extent,start_pad,end_pad,color,hatch,arrowhead_height,arrowhead_length,linestyle,linewidth");
		Placements placements = netlist.getResultNetlistData().getPlacements();
		for (int i = 0; i < placements.getNumPlacement(); i++) {
			Placement placement = placements.getPlacementAtIdx(i);
			for (int j = 0; j < placement.getNumPlacementGroup(); j++) {
				PlacementGroup group = placement.getPlacementGroupAtIdx(j);
				for (int k = 0; k < group.getNumComponent(); k++) {
					org.cellocad.cello2.results.placing.placement.Component component = group.getComponentAtIdx(k);
					String n = component.getNode();
					NetlistNode node = netlist.getVertexByName(n);
					for (int l = 0; l < component.getNumPart(); l++) {
						String p = component.getPartAtIdx(l);
						Part part = parts.findCObjectByName(p);
						String rgb = "0.0;0.0;0.0";
						String type = getPartType(part.getPartType());
						String x = "";
						String y = "";
						if (type.equals(S_PROMOTER)) {
							for (int m = 0; m < node.getNumInEdge(); m++) {
								NetlistEdge e = node.getInEdgeAtIdx(m);
								NetlistNode src = e.getSrc();
								if (LSResultsUtils.isAllInput(src))
									continue;
								String gateType = src.getResultNetlistNodeData().getGateType();
								Gate gate = gates.findCObjectByName(gateType);
								if (!gate.getGateParts().getPromoter().equals(p))
									continue;
								Color color = gate.getColor();
								rgb = getRGB(color);
							}
						}
						else {
							String gateType = node.getResultNetlistNodeData().getGateType();
							Gate gate = gates.findCObjectByName(gateType);
							if (gate != null) {
								Color color = gate.getColor();
								rgb = getRGB(color);
							}
							if (LSResultsUtils.isPrimaryOutput(node)) {
								type = S_USERDEFINED;
								rgb = getRGB(Color.BLACK);
								x = String.valueOf(25);
								y = String.valueOf(5);
							}
						}
						rtn.add(String.format("%s,%s,%s,%s,,,%s,,,,,", p, type, x, y, rgb));
					}
				}
				if ((j + 1) < placement.getNumPlacementGroup()) {
					String pad = String.format("%s%d,%s,30,,,,1.00;1.00;1.00,,,,,", S_NONCEPAD, j, S_USERDEFINED);
					rtn.add(pad);
				}

			}
		}
		return rtn;
	}

	public static List<String> getRegulatoryInformation(Netlist netlist, CObjectCollection<Gate> gates) {
		List<String> rtn = new ArrayList<>();
		rtn.add("from_partname,type,to_partname,arrowhead_length,linestyle,linewidth,color");
		// TAG hardcoded
		for (int i = 0; i < netlist.getNumVertex(); i++) {
			NetlistNode node = netlist.getVertexAtIdx(i);
			String gateType = node.getResultNetlistNodeData().getGateType();
			if (LSResultsUtils.isAllInput(node)) {
				continue;
			} else if (!LSResultsUtils.isAllOutput(node)) {
				Gate gate = gates.findCObjectByName(gateType);
				GateParts parts = gate.getGateParts();
				CasetteParts cassette = parts.getCasetteParts("x");
				Color color = gate.getColor();
				for (int j = 0; j < cassette.getNumParts(); j++) {
					Part part = cassette.getPartAtIdx(j);
					if (part.getPartType().equals("cds")) {
						Collection<String> fields = new ArrayList<>();
						fields.add(part.getName());
						fields.add(S_REPRESSION);
						fields.add(parts.getPromoter());
						fields.add(String.valueOf(3));
						fields.add("-");
						fields.add("");
						fields.add(getRGB(color));
						String str = String.join(",", fields);
						rtn.add(str);
					}
				}
			}
		}
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

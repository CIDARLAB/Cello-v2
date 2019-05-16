/**
 * Copyright (C) 2018 Boston University (BU)
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
package org.cellocad.cello2.export.algorithm.SBOL.data.design;

import java.util.ArrayList;
import java.util.List;

import org.cellocad.cello2.common.CObjectCollection;
import org.cellocad.cello2.export.algorithm.SBOL.data.ucf.Gate;
import org.cellocad.cello2.export.algorithm.SBOL.data.ucf.InputSensor;
import org.cellocad.cello2.export.algorithm.SBOL.data.ucf.OutputReporter;
import org.cellocad.cello2.export.algorithm.SBOL.data.ucf.Part;
import org.cellocad.cello2.results.netlist.Netlist;
import org.cellocad.cello2.results.netlist.NetlistNode;
import org.cellocad.cello2.results.placing.placement.Placement;

/**
 *
 *
 * @author Timothy Jones
 *
 * @date 2018-06-14
 *
 */
public class Designs {

	/**
	 *  Initialize class members
	 */
	private void init() {
		designs = new ArrayList<>();
	}

	/**
	 * @param netlist
	 */
	public Designs(final Netlist netlist,
	               final Boolean Up,
	               final Boolean Down,
	               final CObjectCollection<Part> parts,
	               final CObjectCollection<Gate> gates,
	               final CObjectCollection<InputSensor> sensors,
	               final CObjectCollection<OutputReporter> reporters) {
		init();
		NetlistNode first = netlist.getVertexAtIdx(0);
		Integer num = first.getResultNetlistNodeData().getPlacements().getNumPlacement();
		for (int i = 0; i < num; i++) {
			Design design = new Design();
			Plasmid plasmid = new Plasmid(Up, Down);
			for (int j = 0; j < netlist.getNumVertex(); j++) {
				NetlistNode node = netlist.getVertexAtIdx(j);
				Placement placement = node.getResultNetlistNodeData().getPlacements().getPlacementAtIdx(i);
				TranscriptionalUnit unit = new TranscriptionalUnit(placement,Up,Down,parts,gates,sensors,reporters);
				unit.setDirection(placement.getDirection());
				unit.setIdx(placement.getIdx());
				unit.setName(netlist.getName() + "_" + node.getResultNetlistNodeData().getGateType());
				plasmid.addTranscriptionalUnit(node,unit);
			}
			design.addPlasmid(plasmid);
			this.getDesigns().add(design);
		}
	}

	/**
	 * Returns the Plasmid at the specified position in this instance.
	 *
	 * @param index index of the Plasmid to return
	 * @return if the index is within the bounds (0 <= bounds < this.getNumPlasmids()), returns the Plasmid at the specified position in this instance, otherwise null
	 */
	public Design getDesignAtIdx(int index) {
		Design rtn = null;
		if (
		    (0 <= index)
		    &&
		    (index < this.getNumDesign())
		    ) {
			rtn = this.getDesigns().get(index);
		}
		return rtn;
	}

	/**
	 * Returns the number of Design in this instance.
	 *
	 * @return the number of Design in this instance.
	 */
	public int getNumDesign() {
		return this.getDesigns().size();
	}

	/**
	 *  Getter for <i>designs</i>
	 *  @return the designs of this instance
	 */
	protected List<Design> getDesigns() {
		return designs;
	}

	List<Design> designs;

}

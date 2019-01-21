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
package org.cellocad.cello2.export.algorithm.SBOL.data.plasmid;

import java.util.ArrayList;
import java.util.List;

import org.cellocad.cello2.common.CObjectCollection;
import org.cellocad.cello2.export.algorithm.SBOL.data.ucf.Gate;
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
public class Plasmids {
	
	/**
	 *  Initialize class members
	 */
	private void init() {
		plasmids = new ArrayList<>();
	}
	
	/**
	 * @param netlist
	 */
	public Plasmids(final Netlist netlist,
					final Boolean Up,
					final Boolean Down,
					final CObjectCollection<Part> parts,
					final CObjectCollection<Gate> gates,
					final CObjectCollection<OutputReporter> reporters) {
		init();
		NetlistNode first = netlist.getVertexAtIdx(0);
		Integer num = first.getResultNetlistNodeData().getPlacements().getNumPlacements();
		for (int i = 0; i < num; i++) {
			Plasmid plasmid = new Plasmid(Up, Down);
			for (int j = 0; j < netlist.getNumVertex(); j++) {
				NetlistNode node = netlist.getVertexAtIdx(j);
				Placement placement = node.getResultNetlistNodeData().getPlacements().getPlacementAtIdx(i);
				TranscriptionalUnit unit = new TranscriptionalUnit(placement,Up,Down,parts,gates,reporters);
				unit.setDirection(placement.getDirection());
				unit.setIdx(placement.getIdx());
				unit.setName(netlist.getName() + "_" + node.getResultNetlistNodeData().getGateType());
				plasmid.addTranscriptionalUnit(node,unit);
			}
			this.getPlasmids().add(plasmid);
		}
	}
	
	/**
	 * Returns the Plasmid at the specified position in this instance.
	 * 
	 * @param index index of the Plasmid to return
	 * @return if the index is within the bounds (0 <= bounds < this.getNumPlasmids()), returns the Plasmid at the specified position in this instance, otherwise null
	 */
	public Plasmid getPlasmidAtIdx(int index) {
		Plasmid rtn = null;
		if (
				(0 <= index)
				&&
				(index < this.getNumPlasmids())
				) {
			rtn = this.getPlasmids().get(index);
		}
		return rtn;
	}
	
	/**
	 * Returns the number of Plasmid in this instance.
	 * 
	 * @return the number of Plasmid in this instance.
	 */
	public int getNumPlasmids() {
		return this.getPlasmids().size();
	}
	
	/**
	 *  Getter for <i>plasmids</i>
	 *  @return the plasmids of this instance
	 */
	protected List<Plasmid> getPlasmids() {
		return plasmids;
	}
	
	/*
	 * Up
	 */
	/**
	 * Getter for <i>bUp</i>
	 * @return value of <i>bUp</i>
	 */
	protected Boolean getUp() {
		return bUp;
	}
	/**
	 * Setter for <i>bUp</i>
	 * @param Up the value to set <i>bUp</i>
	 */
	protected void setUp(final Boolean Up) {
		this.bUp = Up;
	}
	/*
	 * Down
	 */
	/**
	 * Getter for <i>bDown</i>
	 * @return value of <i>bDown</i>
	 */
	protected Boolean getDown() {
		return bDown;
	}
	/**
	 * Setter for <i>bDown</i>
	 * @param Down the value to set <i>bDown</i>
	 */
	protected void setDown(final Boolean Down) {
		this.bDown = Down;
	}
	
	List<Plasmid> plasmids;
	private Boolean bUp;
	private Boolean bDown;

}

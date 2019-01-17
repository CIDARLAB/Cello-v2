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
package org.cellocad.cello2.results.placing.placement;

import java.util.ArrayList;
import java.util.List;

import org.cellocad.cello2.common.JSON.JSONUtils;

/**
 * 
 *
 * @author Timothy Jones
 *
 * @date 2018-06-05
 *
 */
public class Placements {
	
	/**
	 *  Initialize class members
	 */
	private void init() {
		placements = new ArrayList<>();
	}
	
	public Placements() {
		init();
	}
	
	/**
	 *  Initializes a newly created Placements with the list of Placement objects defined by parameter <i>placements</i>
	 *  
	 *  @param inputs the List of inputs
	 */
	public Placements(final List<Placement> placements) {
		init();
		this.setPlacements(placements);
	}
	
	/*
	 * 
	 */
	public String toJSON() {
		String rtn = "";
		// Placement
		for (int i = 0; i < this.getNumPlacements(); i++) {
			Placement p = this.getPlacementAtIdx(i);
			rtn += p.toJSON();
		}
		rtn = JSONUtils.addIndent(1, rtn);
		return rtn;
	}
		
	/**
	 * Add a component and its placement index to this instance
	 * @param component add the <i>component</i> to the placement unit
	 * @param index assign placement index <i>index</i> to the placement unit
	 */
	public void addPlacement(Placement placement) {
		this.getPlacements().add(placement);
	}
	
	public int getNumPlacements() {
		return this.getPlacements().size();
	}
	
	public Placement getPlacementAtIdx(final int index) {
		Placement rtn = null;
		rtn = this.getPlacements().get(index);
		return rtn;
	}

	/**
	 * Getter for <i>placements</i>
	 * @return value of <i>placements</i>
	 */
	protected List<Placement> getPlacements() {
		return placements;
	}

	/**
	 * Setter for <i>placements</i>
	 * @param placements the value to set <i>placements</i>
	 */
	protected void setPlacements(final List<Placement> placements) {
		this.placements = placements;
	}

	List<Placement> placements;
}

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

import org.cellocad.cello2.common.CObject;
import org.cellocad.cello2.common.CObjectCollection;
import org.cellocad.cello2.export.algorithm.SBOL.data.ucf.Part;
import org.cellocad.cello2.results.placing.placement.Placement;
import org.sbolstandard.core2.ComponentDefinition;
import org.sbolstandard.core2.SBOLDocument;

/**
 * 
 *
 * @author Timothy Jones
 *
 * @date 2018-06-14
 *
 */
public class TranscriptionalUnit extends CObject {
	
	private void init() {
		unit = new ArrayList<>();
	}
	
	protected void addPart(Part part) {
		unit.add(part);
	}
	
	protected void addPart(int index, Part part) {
		unit.add(index,part);
	}
	
	TranscriptionalUnit(final Placement placement, final Boolean Up, final Boolean Down, CObjectCollection<Part> parts) {
		init();
		if (placement.getDirection() == Down) {
			this.setDirection(Down);
		}
		else {
			this.setDirection(Up);
		}
		for (int i = 0; i < placement.getNumParts(); i++) {
			String name = placement.getPartAtIdx(i);
			Part part = parts.findCObjectByName(name);
			this.addPart(part);
		}
	}
	
	
	/**
	 * Returns the DNA sequence of this instance.
	 * 
	 * @return the DNA sequence of this instance.
	 */
	public String getDNASequence() {
		String rtn = "";
		for (Part part : this.getTranscriptionalUnit()) {
			rtn = rtn + part.getDNASequence();
		}
		return rtn;
	}
	
	/**
	 * Returns the number of parts in this instance.
	 * 
	 * @return the number of parts in this instance.
	 */
	public int getNumParts() {
		return this.getTranscriptionalUnit().size();
	}
	
	/**
	 *  Returns the part at index <i>index</i>
	 *  @return the part at <i>index</i> if the part exists, null otherwise
	 */
	public Part getPartAtIdx(final int index) {
		Part rtn = null;
		if (
				(0 <= index)
				&&
				(index < this.getNumParts())
				) {
			rtn = this.getTranscriptionalUnit().get(index);
		}
		return rtn;
	}
	
	/**
	 * Getter for <i>unit</i>
	 * @return value of <i>unit</i>
	 */
	protected List<Part> getTranscriptionalUnit() {
		return unit;
	}
	
	protected ComponentDefinition getComponentDefinition(SBOLDocument document) {
		ComponentDefinition rtn = null;
		return rtn;
	}

	/**
	 * Getter for <i>name</i>
	 * @return value of <i>name</i>
	 */
	public String getName() {
		return name;
	}

	/**
	 * Setter for <i>name</i>
	 * @param name the value to set <i>name</i>
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Getter for <i>direction</i>
	 * @return value of <i>direction</i>
	 */
	public Boolean getDirection() {
		return direction;
	}
	
	/**
	 * Setter for <i>direction</i>
	 * @param direction the value to set <i>direction</i>
	 */
	protected void setDirection(final Boolean direction) {
		this.direction = direction;
	}

	private String name;
	private Boolean direction;
	List<Part> unit;

}

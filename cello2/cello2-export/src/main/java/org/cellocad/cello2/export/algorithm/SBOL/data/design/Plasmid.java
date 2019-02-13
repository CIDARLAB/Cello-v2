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

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import org.cellocad.cello2.common.CObject;
import org.cellocad.cello2.common.CObjectCollection;
import org.cellocad.cello2.results.netlist.NetlistNode;

/**
 *
 *
 * @author Timothy Jones
 *
 * @date 2018-06-14
 *
 */
public class Plasmid extends CObject {

	private void init() {
		plasmid = new CObjectCollection<>();
		plasmidMap = new HashMap<>();
	}

	public Plasmid(final Boolean Up, final Boolean Down) {
		init();
	}

	protected CObjectCollection<TranscriptionalUnit> getPlasmid() {
		return plasmid;
	}

	public TranscriptionalUnit getTranscriptionalUnitAtIdx(int index) {
		TranscriptionalUnit rtn = null;
		rtn = this.getPlasmid().findCObjectByIdx(index);
		return rtn;
	}

	public int getNumTranscriptionalUnit() {
		return this.getPlasmid().size();
	}

	/**
	 * Getter for <i>plasmidMap</i>
	 * @return value of <i>plasmidMap</i>
	 */
	protected Map<NetlistNode, TranscriptionalUnit> getPlasmidMap() {
		return plasmidMap;
	}

	public TranscriptionalUnit getTranscriptionalUnit(NetlistNode node) {
		TranscriptionalUnit rtn = null;
		rtn = this.getPlasmidMap().get(node);
		return rtn;
	}

	protected void addTranscriptionalUnit(NetlistNode node, TranscriptionalUnit unit) {
		getPlasmid().add(unit);
		getPlasmidMap().put(node,unit);
	}

	protected void addTranscriptionalUnit(int index, NetlistNode node, TranscriptionalUnit unit) {
		getPlasmid().add(index,unit);
		getPlasmidMap().put(node,unit);
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

	/**
	 * Getter for <i>uri</i>
	 * @return value of <i>uri</i>
	 */
	public URI getUri() {
		return uri;
	}

	/**
	 * Setter for <i>uri</i>
	 * @param uri the value to set <i>uri</i>
	 */
	public void setUri(URI uri) {
		this.uri = uri;
	}

	private CObjectCollection<TranscriptionalUnit> plasmid;
	private Map<NetlistNode,TranscriptionalUnit> plasmidMap;
	private Boolean bUp;
	private Boolean bDown;
	private URI uri;

}

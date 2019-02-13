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
import java.util.ArrayList;
import java.util.List;

import org.cellocad.cello2.common.CObject;

/**
 *
 *
 * @author Timothy Jones
 *
 * @date 2018-06-14
 *
 */
public class Design extends CObject {

	private void init() {
		design = new ArrayList<>();
	}

	public Design() {
		init();
	}

	protected List<Plasmid> getPlasmids() {
		return design;
	}

	public Plasmid getPlasmidAtIdx(int index) {
		Plasmid rtn = null;
		if (
		    (0 <= index)
		    &&
		    (index < this.getNumPlasmid())
		    ) {
			rtn = this.getPlasmids().get(index);
		}
		return rtn;
	}

	public int getNumPlasmid() {
		return this.getPlasmids().size();
	}

	protected void addPlasmid(Plasmid plasmid) {
		getPlasmids().add(plasmid);
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

	private List<Plasmid> design;
	private URI uri;

}

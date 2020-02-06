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
package org.cellocad.v2.placing.algorithm.Eugene.data.ucf;

import org.cellocad.v2.common.CObjectCollection;
import org.json.simple.JSONArray;

/**
 * 
 *
 * @author Timothy Jones
 *
 * @date 2018-07-05
 *
 */
public class RegulatoryParts {
	
	private void init() {
		parts = new CObjectCollection<Part>();
	}
	
	private void parseCasetteParts(final JSONArray jObj, CObjectCollection<Part> parts) {
		for (int i = 0; i < jObj.size(); i++) {
			String partName = (String) jObj.get(i);
			Part part = parts.findCObjectByName(partName);
			this.getParts().add(part);
		}
    }
	
	public RegulatoryParts(final JSONArray jobj, CObjectCollection<Part> parts) {
		this.init();
		this.parseCasetteParts(jobj, parts);
	}

	
	/*
	 * Parts
	 */
	private CObjectCollection<Part> getParts(){
		return this.parts;
	}
	
	public Part getPartAtIdx(final int index){
		Part rtn = null;
		if (
				(0 <= index)
				&&
				(index < this.getNumParts())
				) {
			rtn = this.getParts().get(index);
		}
		return rtn;
	}
	
	public int getNumParts(){
		return this.getParts().size();
	}
	
	private CObjectCollection<Part> parts;

}

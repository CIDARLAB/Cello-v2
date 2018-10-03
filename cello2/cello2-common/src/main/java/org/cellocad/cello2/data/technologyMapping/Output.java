/**
 * Copyright (C) 2017 Massachusetts Institute of Technology (MIT)
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
package org.cellocad.cello2.data.technologyMapping;

import org.cellocad.cello2.common.profile.ProfileObject;
import org.cellocad.cello2.common.profile.ProfileUtils;
import org.json.simple.JSONObject;

/**
 * The Output class describes the outputs to a netlist (inducers, quorum signal, molecule)
 * 
 * @author Vincent Mirian
 * 
 * @date 2018-05-21
 *
 */
public class Output extends ProfileObject{

	/**
	 *  Extracts the value of type attribute from the parameter <i>JObj</i>, and sets the name to the extracted value
	 *  
	 *  @param JObj the JavaScript Object Notation (JSON) representation of the ProfileObject Object
	 *  @throws RuntimeException if the attribute is not specified in the parameter <i>JObj</i>
	 */
	protected void parseDNA(final JSONObject JObj){
		// data
		String data = (String) ProfileUtils.getString(JObj, "seq");
		if (data == null) {
	    	throw new RuntimeException("Type not specified!");
		}
		this.setDNA(data);
	}
	
	/**
	 *  Initializes a newly created Output
	 */
	public Output(JSONObject JObj){
		super(JObj);
		this.parseDNA(JObj);
	}

	/**
	 *  Setter for <i>dna</i>
	 *  @param dna the dna to set <i>dna</i>
	 */
	private void setDNA(final String dna){
		this.dna = dna;
	}

	/**
	 *  Getter for <i>dna</i>
	 *  @return the dna of this instance
	 */
	public String getDNA(){
		return this.dna;
	}
	
	private String dna;
}

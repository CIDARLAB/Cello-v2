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
package org.cellocad.v2.data.technologyMapping;

import org.cellocad.v2.common.profile.ProfileObject;
import org.cellocad.v2.common.profile.ProfileUtils;
import org.json.simple.JSONObject;

/**
 * The Inputs class describes the inputs to a netlist (inducers, quorum signal, molecule)
 * 
 * @author Vincent Mirian
 * 
 * @date 2018-05-21
 *
 */
public class Input extends ProfileObject{

	/**
	 *  Extracts the value of type attribute from the parameter <i>JObj</i>, and sets the name to the extracted value
	 *  
	 *  @param JObj the JavaScript Object Notation (JSON) representation of the ProfileObject Object
	 *  @throws RuntimeException if the attribute is not specified in the parameter <i>JObj</i>
	 */
	protected void parseOffREU(final JSONObject JObj){
		// data
		Double data = (Double) ProfileUtils.getDouble(JObj, "off_reu");
		if (data == null) {
	    	throw new RuntimeException("Type not specified!");
		}
		this.setOffREU(data);
	}

	/**
	 *  Extracts the value of type attribute from the parameter <i>JObj</i>, and sets the name to the extracted value
	 *  
	 *  @param JObj the JavaScript Object Notation (JSON) representation of the ProfileObject Object
	 *  @throws RuntimeException if the attribute is not specified in the parameter <i>JObj</i>
	 */
	protected void parseONREU(final JSONObject JObj){
		// data
		Double data = (Double) ProfileUtils.getDouble(JObj, "on_reu");
		if (data == null) {
	    	throw new RuntimeException("Type not specified!");
		}
		this.setONREU(data);
	}

	/**
	 *  Extracts the value of type attribute from the parameter <i>JObj</i>, and sets the name to the extracted value
	 *  
	 *  @param JObj the JavaScript Object Notation (JSON) representation of the ProfileObject Object
	 *  @throws RuntimeException if the attribute is not specified in the parameter <i>JObj</i>
	 */
	protected void parseK(final JSONObject JObj){
		// data
		Double data = (Double) ProfileUtils.getDouble(JObj, "K");
		if (data == null) {
	    	throw new RuntimeException("Type not specified!");
		}
		this.setK(data);
	}

	/**
	 *  Extracts the value of type attribute from the parameter <i>JObj</i>, and sets the name to the extracted value
	 *  
	 *  @param JObj the JavaScript Object Notation (JSON) representation of the ProfileObject Object
	 *  @throws RuntimeException if the attribute is not specified in the parameter <i>JObj</i>
	 */
	protected void parseN(final JSONObject JObj){
		// data
		Double data = (Double) ProfileUtils.getDouble(JObj, "n");
		if (data == null) {
	    	throw new RuntimeException("Type not specified!");
		}
		this.setN(data);
	}

	/**
	 *  Extracts the value of type attribute from the parameter <i>JObj</i>, and sets the name to the extracted value
	 *  
	 *  @param JObj the JavaScript Object Notation (JSON) representation of the ProfileObject Object
	 *  @throws RuntimeException if the attribute is not specified in the parameter <i>JObj</i>
	 */
	protected void parseA(final JSONObject JObj){
		// data
		Double data = (Double) ProfileUtils.getDouble(JObj, "a");
		if (data == null) {
	    	throw new RuntimeException("Type not specified!");
		}
		this.setA(data);
	}

	/**
	 *  Extracts the value of type attribute from the parameter <i>JObj</i>, and sets the name to the extracted value
	 *  
	 *  @param JObj the JavaScript Object Notation (JSON) representation of the ProfileObject Object
	 *  @throws RuntimeException if the attribute is not specified in the parameter <i>JObj</i>
	 */
	protected void parseB(final JSONObject JObj){
		// data
		Double data = (Double) ProfileUtils.getDouble(JObj, "b");
		if (data == null) {
	    	throw new RuntimeException("Type not specified!");
		}
		this.setB(data);
	}

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
	 *  Initializes a newly created Inputs
	 */
	public Input(JSONObject JObj){
		super(JObj);
		this.parseOffREU(JObj);
		this.parseONREU(JObj);
		this.parseK(JObj);
		this.parseN(JObj);
		this.parseA(JObj);
		this.parseB(JObj);
		this.parseDNA(JObj);
	}

	/**
	 *  Setter for <i>off_reu</i>
	 *  @param value to set <i>off_reu</i>
	 */
	private void setOffREU(final double value){
		this.off_reu = value;
	}

	/**
	 *  Getter for <i>off_reu</i>
	 *  @return the off_reu of this instance
	 */
	public double getOffREU(){
		return this.off_reu;
	}

	/**
	 *  Setter for <i>on_reu</i>
	 *  @param value to set <i>on_reu</i>
	 */
	private void setONREU(final double value){
		this.on_reu = value;
	}

	/**
	 *  Getter for <i>on_reu</i>
	 *  @return the on_reu of this instance
	 */
	public double getONREU(){
		return this.on_reu;
	}

	/**
	 *  Setter for <i>K</i>
	 *  @param value to set <i>K</i>
	 */
	private void setK(final double value){
		this.K = value;
	}

	/**
	 *  Getter for <i>K</i>
	 *  @return the K of this instance
	 */
	public double getK(){
		return this.K;
	}

	/**
	 *  Setter for <i>n</i>
	 *  @param value to set <i>n</i>
	 */
	private void setN(final double value){
		this.n = value;
	}

	/**
	 *  Getter for <i>n</i>
	 *  @return the n of this instance
	 */
	public double getN(){
		return this.n;
	}
	
	/**
	 *  Setter for <i>a</i>
	 *  @param value to set <i>a</i>
	 */
	private void setA(final double value){
		this.a = value;
	}

	/**
	 *  Getter for <i>a</i>
	 *  @return the a of this instance
	 */
	public double getA(){
		return this.a;
	}
	
	/**
	 *  Setter for <i>b</i>
	 *  @param value to set <i>b</i>
	 */
	private void setB(final double value){
		this.b = value;
	}

	/**
	 *  Getter for <i>b</i>
	 *  @return the b of this instance
	 */
	public double getB(){
		return this.b;
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
	
	private double off_reu;
	private double on_reu;
	private double K;
	private double n;
	private double a;
	private double b;
	private String dna;
}

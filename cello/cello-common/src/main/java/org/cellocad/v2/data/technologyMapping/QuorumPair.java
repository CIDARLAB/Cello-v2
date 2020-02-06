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
import org.json.simple.JSONObject;

/**
 * The QuorumPair class describes the Quorum Pair (input and output)
 * 
 * @author Vincent Mirian
 * 
 * @date 2018-05-21
 *
 */
public class QuorumPair extends ProfileObject{

	/**
	 *  Extracts the value of type attribute from the parameter <i>JObj</i>, and sets the name to the extracted value
	 *  
	 *  @param JObj the JavaScript Object Notation (JSON) representation of the ProfileObject Object
	 *  @throws RuntimeException if the attribute is not specified in the parameter <i>JObj</i>
	 */
	protected void parseInput(final JSONObject JObj){
		// data
		Input data = new Input((JSONObject) JObj.get("input"));
		this.setInput(data);
	}

	/**
	 *  Extracts the value of type attribute from the parameter <i>JObj</i>, and sets the name to the extracted value
	 *  
	 *  @param JObj the JavaScript Object Notation (JSON) representation of the ProfileObject Object
	 *  @throws RuntimeException if the attribute is not specified in the parameter <i>JObj</i>
	 */
	protected void parseOutput(final JSONObject JObj){
		// data
		Output data = new Output((JSONObject) JObj.get("output"));
		this.setOutput(data);
	}
	
	/**
	 *  Initializes a newly created QuorumPair
	 */
	public QuorumPair(JSONObject JObj){
		super(JObj);
		this.parseInput(JObj);
		this.parseOutput(JObj);
		this.getInput().setName(this.getName()+S_SEPARATOR+S_INPUT);
		this.getOutput().setName(this.getName()+S_SEPARATOR+S_OUTPUT);
	}
	
	/**
	 *  Setter for <i>input</i>
	 *  @param value to set <i>input</i>
	 */
	private void setInput(final Input value){
		this.input = value;
	}

	/**
	 *  Getter for <i>input</i>
	 *  @return the input of this instance
	 */
	public Input getInput(){
		return this.input;
	}
	
	/**
	 *  Setter for <i>output</i>
	 *  @param value to set <i>output</i>
	 */
	private void setOutput(final Output value){
		this.output = value;
	}

	/**
	 *  Getter for <i>output</i>
	 *  @return the output of this instance
	 */
	public Output getOutput(){
		return this.output;
	}

	/**
	 *  Returns matching name of parameter defined by <i>name</i>
	 *  
	 *  @param name name to match
	 *  @return matching name of parameter defined by <i>name</i>
	 */
	public static String getMatchingName(String name) {
		String rtn = "";
		rtn += name;
		if (name.endsWith(S_SEPARATOR+S_OUTPUT)) {
			rtn = name.replace(S_SEPARATOR+S_OUTPUT, S_SEPARATOR+S_INPUT);
		}
		if (name.endsWith(S_SEPARATOR+S_INPUT)) {
			rtn = name.replace(S_SEPARATOR+S_INPUT, S_SEPARATOR+S_OUTPUT);
		}
		return rtn;
	}
	
	/**
	 *  Returns quorum pair name of parameter defined by <i>name</i>
	 *  
	 *  @param name name for quorum pair
	 *  @return matching name of parameter defined by <i>name</i>
	 */
	public static String getQuorumPairName(String name) {
		String rtn = "";
		rtn += name.substring(0, name.lastIndexOf(S_SEPARATOR));
		return rtn;
	}
	
	private Input input;
	private Output output;

	private static String S_INPUT = "input";
	private static String S_OUTPUT = "output";
	private static String S_SEPARATOR = "_";
}

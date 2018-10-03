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
package common.stage;

import org.json.simple.JSONObject;
import common.profile.ProfileObject;
import common.profile.ProfileUtils;

/**
 * The Stage class is a class containing the configuration for a stage.
 * 
 * @author Vincent Mirian
 * 
 * @date Nov 20, 2017
 *
 */
final public class Stage extends ProfileObject{
	
	private void init() {
	}

	/**
	 *  Initializes a newly created Stage with <i>algorithmName</i> set to null
	 */
	public Stage(){
		super();
		init();
	}

	/**
	 *  Initializes a newly created Stage using the parameter <i>JObj</i>.
	 *  
	 *  @param JObj the JavaScript Object Notation (JSON) representation of the Stage Object
	 */
	public Stage(final JSONObject JObj){
		super(JObj);
		init();
		parse(JObj);
	}
	/*
	 * Parse
	 */
	private void parseStageConfiguration(final JSONObject JObj){
    	// parse StageConfiguration
		// algoName
		String algoName = ProfileUtils.getString(JObj, "algorithm_name");
		if (algoName == null) {
			throw new RuntimeException("'algorithm_name' missing for stage configuration " + this.getName() + ".");
		}
		this.setAlgorithmName(algoName);
	}
	
	private void parse(final JSONObject JObj){
		this.parseStageConfiguration(JObj);
	}

	/*
	 * Getter and Setter
	 */
	/**
	 *  Getter for <i>algorithmName</i>
	 *  @return the name of the algorithm for this instance
	 */
	public String getAlgorithmName() {
		return this.algorithmName;
	}

	/**
	 *  Setter for <i>algorithmName</i>
	 *  @param algorithmName the name of the algorithm for this instance
	 */
	public void setAlgorithmName(final String algorithmName) {
		this.algorithmName = algorithmName;
	}
	
	String algorithmName;
}

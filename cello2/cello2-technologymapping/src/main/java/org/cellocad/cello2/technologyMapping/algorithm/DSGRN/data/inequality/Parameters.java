/**
 * Copyright (C) 2018 Boston Univeristy (BU)
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
package org.cellocad.cello2.technologyMapping.algorithm.DSGRN.data.inequality;

import org.cellocad.cello2.common.CObject;
import org.cellocad.cello2.common.CObjectCollection;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 * The Parameters class is a class for storing and accessing a set of Parameter objects.
 *
 * @author Timothy Jones
 *
 * @date 2018-10-29
 *
 */
public class Parameters extends CObject{
	
	private void init() {
		parameters = new CObjectCollection<>();
	}
	
	public Parameters(final JSONArray JArray){
		super();
		init();
		parse(JArray);
	}
	
	private void parse(final JSONArray JArray){
		for (int i = 0; i < JArray.size(); i++) {
			JSONObject JObj = (JSONObject) JArray.get(i);
			Parameter param = new Parameter(JObj);
			this.getParameters().add(param);		
		}
	}
	
	/**
	 *  Returns a Parameter at index, <i>index</i>
	 *  
	 *  @param index index of the Parameter to return
	 *  @return the Parameter if it exists, otherwise null
	 */
	public Parameter getParameterAtIdx(int index) {
		Parameter rtn = null;
		rtn = this.getParameters().get(index);
		return rtn;
	}

	/**
	 *  Returns the number of Parameter
	 *  
	 *  @return the number of Parameter
	 */
	public int getNumParameter() {
		int rtn = 0;
		rtn = this.getParameters().size();
		return rtn;
	}

	/**
	 * Getter for <i>parameters</i>
	 * @return value of <i>parameters</i>
	 */
	public CObjectCollection<Parameter> getParameters() {
		return parameters;
	}
	
	CObjectCollection<Parameter> parameters;

}

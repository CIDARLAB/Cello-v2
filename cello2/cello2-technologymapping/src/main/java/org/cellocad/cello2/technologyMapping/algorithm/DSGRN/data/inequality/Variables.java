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
 * The Variables class is a class for storing and accessing a set of Variable objects.
 *
 * @author Timothy Jones
 *
 * @date 2018-10-29
 *
 */
public class Variables extends CObject{
	
	private void init() {
		variables = new CObjectCollection<>();
	}
	
	public Variables(final String value){
		super();
		init();
		parse(value);
	}
	
	private void parse(final String value){
		String temp = value.substring(1,value.length()-1);
		String[] vars = temp.split(", ");
		for (String v : vars) {
			Variable variable = new Variable(v);
			this.getVariables().add(variable);
		}
	}
	
	/**
	 *  Returns a Variable at index, <i>index</i>
	 *  
	 *  @param index index of the Variable to return
	 *  @return the Variable if it exists, otherwise null
	 */
	public Variable getVariableAtIdx(int index) {
		Variable rtn = null;
		rtn = this.getVariables().get(index);
		return rtn;
	}

	/**
	 *  Returns the number of Variable
	 *  
	 *  @return the number of Variable
	 */
	public int getNumVariable() {
		int rtn = 0;
		rtn = this.getVariables().size();
		return rtn;
	}

	/**
	 * Getter for <i>variables</i>
	 * @return value of <i>variables</i>
	 */
	public CObjectCollection<Variable> getVariables() {
		return variables;
	}
	
	CObjectCollection<Variable> variables;

}

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
package org.cellocad.v2.common.file.csv.utils;

import org.cellocad.v2.common.CObject;

/**
 * The CSVRecord class is containing a record for a CSV file.
 *  
 * @author Vincent Mirian
 * 
 * @date Oct 26, 2017
 *
 */
public class CSVRecord extends CObject{

	/**
	 *  Initializes a newly created CSVReader with a record defined by parameter <i>line</i>, and,
	 *  a delimeter for the fields of the record defined by parameter <i>delimeter</i>.
	 *  
	 *  @param line the record
	 *  @param delimeter the delimeter
	 */
	public CSVRecord(String line, String delimeter) {
		this.setFields(line.split(delimeter));
	}

	/**
	 * Returns the Field at the specified position in this instance.
	 * 
	 * @param index index of the Field to return
	 * @return if the index is within the bounds (0 <= bounds < this.getNumFields()), returns the Field at the specified position in this instance, otherwise null
	 */
	public String getFieldAtIdx(int index){
		String rtn = null;
		if (
				(index >= 0) &&
				(index < this.getNumFields())
			){
			rtn = this.getFields()[index];	
		}
		return rtn;
	}

	/**
	 * Returns the number of Fields in this instance.
	 * 
	 * @return the number of Fields in this instance.
	 */
	public int getNumFields() {
		return this.getFields().length;
	}

	/**
	 *  Setter for <i>fields</i>
	 *  @param fields the fields to set <i>fields</i>
	 */
	public void setFields(String[] fields){
		this.fields = fields;
	}
	
	/**
	 *  Getter for <i>fields</i>
	 *  @return the fields of this instance
	 */
	private String[] getFields(){
		return this.fields;
	}

	private String[] fields;
}

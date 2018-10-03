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
package results.netlist.data;

import java.io.IOException;
import java.io.Writer;

import org.json.simple.JSONObject;

import common.application.data.ApplicationNetlistEdgeData;

/**
 * The ResultNetlistEdgeData class contains all the data for an edge used within the project.
 * 
 * @author Vincent Mirian
 * 
 * @date 2018-05-21
 *
 */
public class ResultNetlistEdgeData extends ApplicationNetlistEdgeData{

	private void setDefault() {
	}

	/**
	 *  Initializes a newly created ResultNetlistEdgeData 
	 */
	public ResultNetlistEdgeData(){
		super();
		this.setDefault();
	}

	/**
	 *  Initializes a newly created ResultNetlistEdgeData with its parameters cloned from those of parameter <i>other</i>.
	 *  
	 *  @param other the other ResultNetlistEdgeData
	 */
	public ResultNetlistEdgeData(ResultNetlistEdgeData other){
		super();
		this.setDefault();
	}

	/**
	 *  Initializes a newly created ResultNetlistEdgeData using the parameter <i>JObj</i>.
	 *  
	 *  @param JObj the JavaScript Object Notation (JSON) representation of the ResultNetlistEdgeData Object
	 */
	public ResultNetlistEdgeData(final JSONObject JObj){
		super();
		this.setDefault();
		this.parse(JObj);
	}
	
	/**
	 *  Writes this instance in JSON format to the writer defined by parameter <i>os</i> with the number of indents equivalent to the parameter <i>indent</i>
	 *  @param indent the number of indents
	 *  @param os the writer
	 *  @throws IOException If an I/O error occurs
	 */
	public void writeJSON(int indent, Writer os) throws IOException{
	}

	/**
	 *  Parses the data attached to this instance
	 *  
	 *  @param JObj the JavaScript Object Notation (JSON) representation of the Project NetlistEdgeData Object
	 */
	public void parse(final JSONObject JObj){
	}

}

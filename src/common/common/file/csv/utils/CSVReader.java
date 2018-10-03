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
package common.file.csv.utils;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import common.CObject;

/**
 * The CSVReader class is class with mechanism for reading a CSV file.
 *  
 * @author Vincent Mirian
 * 
 * @date Oct 26, 2017
 *
 */
public class CSVReader extends CObject{

	/**
	 *  Initializes a newly created CSVReader with a file defined by parameter <i>filename</i>, and,
	 *  a delimeter for the fields of the record defined by parameter <i>delimeter</i>.
	 *  
	 *  @param filename the filename
	 *  @param delimeter the delimeter
	 */
	public CSVReader (String filename, String delimeter) {
		try {
			this.br = new BufferedReader(new FileReader(filename));
			this.setCSVSplitBy(delimeter);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	/**
	 *  Returns the next CSVRecord.
	 *  
	 *  @return the next CSVRecord, if one exists, null otherwise.
	 */
	public CSVRecord getNextRecord() {
		CSVRecord rtn = null;
		String line = "";
		try {
			if ((line = this.getBufferedReader().readLine()) != null) {
				rtn = new CSVRecord(line, this.getCSVSplitBy());
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return rtn;
	}

	/**
	 *  Getter for <i>br</i>
	 *  @return the br of this instance
	 */
	private BufferedReader getBufferedReader(){
		return this.br;
	}
	
	private BufferedReader br;

	/**
	 *  Setter for <i>cvsSplitBy</i>
	 *  @param cvsSplitBy the cvsSplitBy to set <i>cvsSplitBy</i>
	 */
	private void setCSVSplitBy(final String cvsSplitBy){
		this.cvsSplitBy = cvsSplitBy;
	}
	/**
	 *  Getter for <i>cvsSplitBy</i>
	 *  @return the cvsSplitBy of this instance
	 */
	private String getCSVSplitBy(){
		return this.cvsSplitBy;
	}
	
	private String cvsSplitBy;
	
}

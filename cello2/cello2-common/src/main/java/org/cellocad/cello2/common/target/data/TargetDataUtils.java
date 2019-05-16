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
package org.cellocad.cello2.common.target.data;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;

import org.cellocad.cello2.common.Utils;
import org.cellocad.cello2.common.runtime.environment.RuntimeEnv;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * The TargetDataUtils class is class with utility methods for <i>TargetData</i> instances.
 * 
 * @author Vincent Mirian
 * 
 * @date Nov 22, 2017
 *
 */
final public class TargetDataUtils {

	/**
	 *  Initializes a newly created TargetData using the RuntimeEnv, <i>runEnv</i>, and,
	 *  the string referencing command line argument for the TargetData file, <i>targetDataFile</i>.
	 *  
	 *  @param runEnv the RuntimeEnv to extract the TargetData file, <i>targetDataFile</i>
	 *  @param targetDataFile the string referencing command line argument for the TargetData file
	 *  @return the TargetData if created successfully, otherwise null
	 *  @throws RuntimeException if: <br>
	 *  Error accessing <i>targetDataFile</i><br>
	 *  Error parsing <i>targetDataFile</i><br>
	 */
	static public TargetData getTargetTargetData(final RuntimeEnv runEnv, final String targetDataFile){
		Utils.isNullRuntimeException(runEnv, "runEnv");
		Utils.isNullRuntimeException(targetDataFile, "targetDataFile");
		TargetData rtn = null;
		// get Target File
		String targetFilename = runEnv.getOptionValue(targetDataFile);
	    File targetFile = new File(targetFilename);
	    Reader targetReader = null;
	    JSONArray jsonTop = null;
		// Create File Reader
		try {
			targetReader = new FileReader(targetFile);
		} catch (FileNotFoundException e) {
			throw new RuntimeException("Error with file: " + targetFilename);
		}
		// Create JSON object from File Reader
		JSONParser parser = new JSONParser();
        try{
        	jsonTop = (JSONArray) parser.parse(targetReader);
	    } catch (IOException e) {
	        throw new RuntimeException("File IO Exception for: " + targetFilename + ".");
	    } catch (ParseException e) {
	        throw new RuntimeException("Parser Exception for: " + targetFilename + ".");
	    }
		// Create TargetInfo object
	    rtn = new TargetData(jsonTop);
	    try {
			targetReader.close();
		} catch (IOException e) {
			throw new RuntimeException("Error with file: " + targetFilename);
		}
	    return rtn;
	}	
}

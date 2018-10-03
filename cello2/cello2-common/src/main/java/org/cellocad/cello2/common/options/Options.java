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
package org.cellocad.cello2.common.options;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.StringTokenizer;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.cellocad.cello2.common.CObject;
import org.cellocad.cello2.common.Pair;


/**
 * The Options class is the class containing the options for executables (an application and a stage ) in the Poros framework. It is the software representation of the execution control file is used to modify the default execution of an application or a stage.
 * 
 * @author Vincent Mirian
 * 
 * @date Dec 8, 2017
 *
 */
final public class Options extends CObject{

	private void init() {
		this.stageValues = new HashMap<String, String>();
		this.stageArgValues = new HashMap<String, Map <String, String>>();
	}

	/**
	 *  Initializes a newly created Options with its contents equivalent to those of the parameter <i>filename</i>.
	 *  
	 *  @param filename the path to the execution control file
	 */
	public Options(String filename) {
		init();
		Reader in = null;
		try {
			in = new FileReader(filename);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		Iterable<CSVRecord> records = null;
		try {
			records = CSVFormat.DEFAULT.parse(in);
		} catch (IOException e) {
			e.printStackTrace();
		}
		for (CSVRecord record : records) {
			if (record.size() < 2){
				continue;				
			}
		    String arg = record.get(0);
		    String value = record.get(1);
		    if ((arg != null) &&
		    	(value != null))
		    {
		    	// stage param
		    	if (arg.contains(".")) {
		    		StringTokenizer st = new StringTokenizer(arg,".");
		    		String stageName = st.nextToken();
		    		String argumentName = st.nextToken();
		    		Map <String, String> params = this.getStageArgValue().get(stageName);
		    		if (params == null) {
		    			params = new LinkedHashMap<String, String>();
		    			this.getStageArgValue().put(stageName, params);
		    		}
		    		params.put(argumentName, value);		    		
		    	}
		    	// stage name
		    	else {
		    		this.getStageValue().put(arg, value);
		    	}
		    }
		}
	}

	/**
	 *  Returns the value of the stage defined by parameter <i>stage</i>
	 *  
	 *  @param stage the name of the stage
	 *  @return if the parameter <i>stage</i> is present, return the value of the stage defined by parameter <i>stage</i>, otherwise null
	 */
	public String getStageName(String stage) {
		String rtn = null;
		rtn = this.getStageValue().get(stage);
		return rtn;
	}

	/**
	 *  Returns the value of the argument defined by parameter <i>arg</i> for the stage defined by parameter <i>stage</i>
	 *  
	 *  @param stage the name of the stage
	 *  @param arg the name of the argument
	 *  @return if the argument is present in the stage, return the value of the argument defined by parameter <i>arg</i> for the stage defined by parameter <i>stage</i>, otherwise null
	 */
	public String getStageArgValueName(String stage, String arg) {
		String rtn = null;
		Map <String, String> params = this.getStageArgValue().get(stage);
		if (params != null) {
			rtn = params.get(arg);
		}
		return rtn;
	}

	/**
	 *  Returns the number of argument(s) in the stage defined by parameter <i>stage</i>
	 *  
	 *  @param stage the name of the stage
	 *  @return the number of argument(s) in the stage defined by parameter <i>stage</i>
	 */
	public int getNumStageArgValue(String stage) {
		int rtn = 0;
		Map <String, String> params = this.getStageArgValue().get(stage);
		if (params != null) {
			rtn = params.size();	
		}
		return rtn;
	}

	/**
	 *  Returns the value of the argument at the index defined by parameter <i>index</i> for the stage defined by parameter <i>stage</i>
	 *  
	 *  @param stage the name of the stage
	 *  @param index index of the argument
	 *  @return if the index is within the bounds (0 <= bounds < this.getNumStageArgValue(stage)), return a pair instance with the first element containing the name of the argument, and, the second element containing the value of the argument, otherwise return null
	 */
	public Pair<String, String> getStageArgValueAtIdx(String stage, int index) {
		Pair<String, String> rtn = null;
		Map <String, String> params = this.getStageArgValue().get(stage);
		int count = 0;
		Iterator< Map.Entry<String, String>> it = params.entrySet().iterator();
	    while (it.hasNext()) {
	        Map.Entry<String, String> pair = (Map.Entry<String, String>) it.next();
	        String arg = pair.getKey();
	        String value = pair.getValue();
	    	if (index == count) {
	    		rtn = new Pair<String, String>(arg,value);
	    	}
	    	count ++;
	    }
		return rtn;
	}
	
	/*
	 * Getter and Setter
	 */
	private Map <String, String> getStageValue() {
		return this.stageValues;
	}
	
	private Map <String, Map <String, String>> getStageArgValue() {
		return this.stageArgValues;
	}
	
	private Map <String, String> stageValues;
	private Map <String, Map <String, String>> stageArgValues;
}

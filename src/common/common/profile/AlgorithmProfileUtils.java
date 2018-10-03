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
package common.profile;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import common.Pair;
import common.Utils;
import common.options.Options;


/**
 * The AlgorithmProfileUtils class is class with utility methods for <i>AlgorithmProfile</i> instances.
 * 
 * @author Vincent Mirian
 * 
 * @date Dec 9, 2017
 *
 */
final public class AlgorithmProfileUtils {

	/**
	 *  Overrides the AlgorithmProfile instance, <i>AProfile</i>, with the Options instance, <i>options</i>.
	 *  
	 *  @param AProfile AlgorithmProfile instance
	 *  @param options Options instance
	 *  @throws RuntimeException if parameter <i>AProfile</i> is null
	 */
	static public void OverrideWithOptions(final AlgorithmProfile AProfile, final Options options){
		Utils.isNullRuntimeException(AProfile, "AProfile");
		String stageName = AProfile.getStageName();
		if (options != null) {
			for (int i = 0; i < options.getNumStageArgValue(stageName); i++){
				Pair<String, String > argValue = options.getStageArgValueAtIdx(stageName, i);
				String arg = argValue.getFirst();
				String value = argValue.getSecond();
				Boolean obj = null;
				// Boolean
				obj = AProfile.getBooleanParameter(arg).getFirst();
				if (obj.booleanValue()) {
					Boolean tObj = Utils.getBoolean(value);
					if (tObj != null) {
						AProfile.setBooleanParameter(arg, tObj);
					}
				}
				// Byte
				obj = AProfile.getByteParameter(arg).getFirst();
				if (obj.booleanValue()) {
					Byte tObj = Utils.getByte(value);
					if (tObj != null) {
						AProfile.setByteParameter(arg, tObj);
					}
				}
				// Character
				obj = AProfile.getCharParameter(arg).getFirst();
				if (obj.booleanValue()) {
					Character tObj = Utils.getCharacter(value);
					if (tObj != null) {
						AProfile.setCharacterParameter(arg, tObj);
					}
				}
				// Short
				obj = AProfile.getShortParameter(arg).getFirst();
				if (obj.booleanValue()) {
					Short tObj = Utils.getShort(value);
					if (tObj != null) {
						AProfile.setShortParameter(arg, tObj);
					}
				}
				// Integer
				obj = AProfile.getIntParameter(arg).getFirst();
				if (obj.booleanValue()) {
					Integer tObj = Utils.getInteger(value);
					if (tObj != null) {
						AProfile.setIntegerParameter(arg, tObj);
					}
				}
				// Long
				obj = AProfile.getLongParameter(arg).getFirst();
				if (obj.booleanValue()) {
					Long tObj = Utils.getLong(value);
					if (tObj != null) {
						AProfile.setLongParameter(arg, tObj);
					}
				}
				// Float
				obj = AProfile.getFloatParameter(arg).getFirst();
				if (obj.booleanValue()) {
					Float tObj = Utils.getFloat(value);
					if (tObj != null) {
						AProfile.setFloatParameter(arg, tObj);
					}
				}
				// Double
				obj = AProfile.getDoubleParameter(arg).getFirst();
				if (obj.booleanValue()) {
					Double tObj = Utils.getDouble(value);
					if (tObj != null) {
						AProfile.setDoubleParameter(arg, tObj);
					}
				}
				// String
				obj = AProfile.getStringParameter(arg).getFirst();
				if (obj.booleanValue()) {
					String tObj = Utils.getString(value);
					if (tObj != null) {
						AProfile.setStringParameter(arg, tObj);
					}
				}
			}
		}
	}

	/**
	 *  Initializes a newly created AlgorithmProfile using the path to the algorithm configuration file, <i>filename</i>.
	 *  
	 *  @param filename the path to the algorithm configuration file
	 *  @return the AlgorithmProfile if created successfully, otherwise null
	 *  @throws RuntimeException if: <br>
	 *  Error accessing <i>filename</i><br>
	 *  Error parsing <i>filename</i><br>
	 */
	static public AlgorithmProfile getAlgorithmProfile(final String filename){
		AlgorithmProfile rtn = null;
		Reader APReader = null;
		JSONObject jsonTop = null;
		// Create File Reader
		try {
			APReader = new FileReader(filename);
		} catch (FileNotFoundException e) {
			throw new RuntimeException("Error with file: " + filename);
		}
		// Create JSON object from File Reader
		JSONParser parser = new JSONParser();
        try{
        	jsonTop = (JSONObject) parser.parse(APReader);
	    } catch (IOException e) {
	        throw new RuntimeException("File IO Exception for: " + filename + ".");
	    } catch (ParseException e) {
	        throw new RuntimeException("Parser Exception for: " + filename + ".");
	    }
		// Create TargetInfo object
	    rtn = new AlgorithmProfile(jsonTop);
	    try {
	    	APReader.close();
		} catch (IOException e) {
			throw new RuntimeException("Error with file: " + filename);
		}
		
		return rtn;		
	}
}

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
package org.cellocad.cello2.common.profile;

import java.util.HashMap;
import java.util.Map;

import org.cellocad.cello2.common.Pair;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;


/**
 * The AlgorithmProfile class is a class containing the configuration for an algorithm. It is the software representation of the algorithm configuration file.
 * 
 * @author Vincent Mirian
 * 
 * @date Oct 27, 2017
 *
 */
final public class AlgorithmProfile extends ProfileObject {

	/**
	 *  Initializes a newly created AlgorithmProfile using the parameter <i>JObj</i>.
	 *  
	 *  @param JObj the JavaScript Object Notation (JSON) representation of the AlgorithmProfile Object
	 */
	public AlgorithmProfile(final JSONObject JObj){
		super(JObj);
		booleanParameters = new HashMap<String, Boolean>();
		byteParameters = new HashMap<String, Byte>();
		charParameters = new HashMap<String, Character>();
		shortParameters = new HashMap<String, Short>();
		intParameters = new HashMap<String, Integer>();
		longParameters = new HashMap<String, Long>();
		floatParameters = new HashMap<String, Float>();
		doubleParameters = new HashMap<String, Double>();
		stringParameters = new HashMap<String, String>();
		stageName = "";
		this.parse(JObj);
	}
	

	/**
	 *  Getter for <i>stageName</i>
	 *  @return the stageName of this instance
	 */
	public String getStageName(){
		return this.stageName;
	}

	/**
	 *  Setter for <i>stageName</i>
	 *  @param str the name of the stage to set <i>stageName</i>
	 */
	public void setStageName(final String str){
		this.stageName = str;
	}

	/**
	 *  Getter for the boolean parameter with its name equivalent to parameter <i>name</i>. 
	 *   
	 *  @param name the name of the parameter
	 *  @return a pair instance with the first element representing the validity of the second element, and, the second element contains the value of the parameter <i>name</i>. If the parameter is present for this instance, then the value of the first element is true, otherwise false. If the parameter is present for this instance, the value of the second element is the value of the parameter, otherwise null.
	 */
	public Pair<Boolean, Boolean> getBooleanParameter(final String name){
		Boolean value = booleanParameters.get(name);
		boolean first = (value != null);
		Pair<Boolean, Boolean> rtn = new Pair<Boolean, Boolean>(new Boolean(first), value);
		return rtn;
	}
	/**
	 *  Setter for the boolean parameter with its name equivalent to parameter <i>name</i>.
	 *  @param name the name of the parameter
	 *  @param value the value of the parameter
	 */
	public void setBooleanParameter(final String name, Boolean value){
		booleanParameters.put(name, value);
	}
	
	/**
	 *  Getter for the byte parameter with its name equivalent to parameter <i>name</i>. 
	 *   
	 *  @param name the name of the parameter
	 *  @return a pair instance with the first element representing the validity of the second element, and, the second element contains the value of the parameter <i>name</i>. If the parameter is present for this instance, then the value of the first element is true, otherwise false. If the parameter is present for this instance, the value of the second element is the value of the parameter, otherwise null. 
	 */
	public Pair<Boolean, Byte> getByteParameter(final String name){
		Byte value = byteParameters.get(name);
		boolean first = (value != null);
		Pair<Boolean, Byte> rtn = new Pair<Boolean, Byte>(new Boolean(first), value);
		return rtn;
	}
	/**
	 *  Setter for the byte parameter with its name equivalent to parameter <i>name</i>.
	 *  @param name the name of the parameter
	 *  @param value the value of the parameter
	 */
	public void setByteParameter(final String name, Byte value){
		byteParameters.put(name, value);
	}
	
	/**
	 *  Getter for the character parameter with its name equivalent to parameter <i>name</i>. 
	 *   
	 *  @param name the name of the parameter
	 *  @return a pair instance with the first element representing the validity of the second element, and, the second element contains the value of the parameter <i>name</i>. If the parameter is present for this instance, then the value of the first element is true, otherwise false. If the parameter is present for this instance, the value of the second element is the value of the parameter, otherwise null.
	 */
	public Pair<Boolean, Character> getCharParameter(final String name){
		Character value = charParameters.get(name);
		boolean first = (value != null);
		Pair<Boolean, Character> rtn = new Pair<Boolean, Character>(new Boolean(first), value);
		return rtn;
	}
	/**
	 *  Setter for the character parameter with its name equivalent to parameter <i>name</i>.
	 *  @param name the name of the parameter
	 *  @param value the value of the parameter
	 */
	public void setCharacterParameter(final String name, Character value){
		charParameters.put(name, value);
	}
	
	/**
	 *  Getter for the short parameter with its name equivalent to parameter <i>name</i>. 
	 *   
	 *  @param name the name of the parameter
	 *  @return a pair instance with the first element representing the validity of the second element, and, the second element contains the value of the parameter <i>name</i>. If the parameter is present for this instance, then the value of the first element is true, otherwise false. If the parameter is present for this instance, the value of the second element is the value of the parameter, otherwise null.
	 */
	public Pair<Boolean, Short> getShortParameter(final String name){
		Short value = shortParameters.get(name);
		boolean first = (value != null);
		Pair<Boolean, Short> rtn = new Pair<Boolean, Short>(new Boolean(first), value);
		return rtn;
	}
	/**
	 *  Setter for the short parameter with its name equivalent to parameter <i>name</i>.
	 *  @param name the name of the parameter
	 *  @param value the value of the parameter
	 */
	public void setShortParameter(final String name, Short value){
		shortParameters.put(name, value);
	}
	
	/**
	 *  Getter for the integer parameter with its name equivalent to parameter <i>name</i>. 
	 *   
	 *  @param name the name of the parameter
	 *  @return a pair instance with the first element representing the validity of the second element, and, the second element contains the value of the parameter <i>name</i>. If the parameter is present for this instance, then the value of the first element is true, otherwise false. If the parameter is present for this instance, the value of the second element is the value of the parameter, otherwise null.
	 */
	public Pair<Boolean, Integer> getIntParameter(final String name){
		Integer value = intParameters.get(name);
		boolean first = (value != null);
		Pair<Boolean, Integer> rtn = new Pair<Boolean, Integer>(new Boolean(first), value);
		return rtn;
	}
	/**
	 *  Setter for the integer parameter with its name equivalent to parameter <i>name</i>.
	 *  @param name the name of the parameter
	 *  @param value the value of the parameter
	 */
	public void setIntegerParameter(final String name, Integer value){
		intParameters.put(name, value);
	}

	/**
	 *  Getter for the long parameter with its name equivalent to parameter <i>name</i>. 
	 *   
	 *  @param name the name of the parameter
	 *  @return a pair instance with the first element representing the validity of the second element, and, the second element contains the value of the parameter <i>name</i>. If the parameter is present for this instance, then the value of the first element is true, otherwise false. If the parameter is present for this instance, the value of the second element is the value of the parameter, otherwise null.
	 */
	public Pair<Boolean, Long> getLongParameter(final String name){
		Long value = longParameters.get(name);
		boolean first = (value != null);
		Pair<Boolean, Long> rtn = new Pair<Boolean, Long>(new Boolean(first), value);
		return rtn;
	}
	/**
	 *  Setter for the long parameter with its name equivalent to parameter <i>name</i>.
	 *  @param name the name of the parameter
	 *  @param value the value of the parameter
	 */
	public void setLongParameter(final String name, Long value){
		longParameters.put(name, value);
	}

	/**
	 *  Getter for the float parameter with its name equivalent to parameter <i>name</i>. 
	 *   
	 *  @param name the name of the parameter
	 *  @return a pair instance with the first element representing the validity of the second element, and, the second element contains the value of the parameter <i>name</i>. If the parameter is present for this instance, then the value of the first element is true, otherwise false. If the parameter is present for this instance, the value of the second element is the value of the parameter, otherwise null.
	 */
	public Pair<Boolean, Float> getFloatParameter(final String name){
		Float value = floatParameters.get(name);
		boolean first = (value != null);
		Pair<Boolean, Float> rtn = new Pair<Boolean, Float>(new Boolean(first), value);
		return rtn;
	}
	/**
	 *  Setter for the float parameter with its name equivalent to parameter <i>name</i>.
	 *  @param name the name of the parameter
	 *  @param value the value of the parameter
	 */
	public void setFloatParameter(final String name, Float value){
		floatParameters.put(name, value);
	}

	/**
	 *  Getter for the double parameter with its name equivalent to parameter <i>name</i>. 
	 *   
	 *  @param name the name of the parameter
	 *  @return a pair instance with the first element representing the validity of the second element, and, the second element contains the value of the parameter <i>name</i>. If the parameter is present for this instance, then the value of the first element is true, otherwise false. If the parameter is present for this instance, the value of the second element is the value of the parameter, otherwise null.
	 */
	public Pair<Boolean, Double> getDoubleParameter(final String name){
		Double value = doubleParameters.get(name);
		boolean first = (value != null);
		Pair<Boolean, Double> rtn = new Pair<Boolean, Double>(new Boolean(first), value);
		return rtn;
	}
	/**
	 *  Setter for the double parameter with its name equivalent to parameter <i>name</i>.
	 *  @param name the name of the parameter
	 *  @param value the value of the parameter
	 */
	public void setDoubleParameter(final String name, Double value){
		doubleParameters.put(name, value);
	}

	/**
	 *  Getter for the string parameter with its name equivalent to parameter <i>name</i>. 
	 *   
	 *  @param name the name of the parameter
	 *  @return a pair instance with the first element representing the validity of the second element, and, the second element contains the value of the parameter <i>name</i>. If the parameter is present for this instance, then the value of the first element is true, otherwise false. If the parameter is present for this instance, the value of the second element is the value of the parameter, otherwise null.
	 */
	public Pair<Boolean, String> getStringParameter(final String name){
		String value = stringParameters.get(name);
		boolean first = (value != null);
		Pair<Boolean, String> rtn = new Pair<Boolean, String>(new Boolean(first), value);
		return rtn;
	}
	public void setStringParameter(final String name, String value){
		stringParameters.put(name, value);
	}
	
	/*
	 * Parse
	 */

	private void parseParameter(final JSONObject JObj){
		//name
		String name = (String) ProfileUtils.getString(JObj, "name");
		if (name == null){
			throw new RuntimeException("Name not specified for parameter in AlgorithmProfile " + this.getName() + ".");
		}
		//type
		String type = (String) ProfileUtils.getString(JObj, "type");
		if (type == null){
			throw new RuntimeException("Type not specified for parameter " + name + ".");
		}
		// value
		Object value = ProfileUtils.getObject(JObj, "value");
		if (value == null){
			throw new RuntimeException("Value not specified for parameter " + name + ".");
		}
		switch (type) {
	        case BOOLEAN:{
	        	Boolean data = ProfileUtils.getBoolean(JObj, "value");
	        	this.setBooleanParameter(name, data);
	            break;
	        }
	        case BYTE:{
	        	Byte data = ProfileUtils.getByte(JObj, "value");
	        	this.setByteParameter(name, data);
	            break;
	        }
	        case CHAR:{
	        	Character c = ProfileUtils.getCharacter(JObj, "value");
	        	this.setCharacterParameter(name, c);
	            break;
	        }
	        case SHORT:{
	        	Short data = ProfileUtils.getShort(JObj, "value");
	        	this.setShortParameter(name, data);
	            break;
	        }
	        case INT:{
	        	Integer data = ProfileUtils.getInteger(JObj, "value");
	        	this.setIntegerParameter(name, data);
	            break;
	        }
	        case LONG:{
	        	Long data = ProfileUtils.getLong(JObj, "value");
	        	this.setLongParameter(name, data);
	            break;
	        }
	        case FLOAT:{
	        	Float data = ProfileUtils.getFloat(JObj, "value");
	        	this.setFloatParameter(name, data);
	            break;
	        }
	        case DOUBLE:{
	        	Double data = ProfileUtils.getDouble(JObj, "value");
	        	this.setDoubleParameter(name, data);
	            break;
	        }
	        case STRING:{
	        	String data = ProfileUtils.getString(JObj, "value");
	        	this.setStringParameter(name, data);
	            break;
	        }
	        default:{
				throw new RuntimeException("Invalid type for parameter " + name + ".");
	        }
		}
	}
	
	private void parseParameters(final JSONObject JObj){
		JSONArray jsonArr = (JSONArray) JObj.get("parameters");
		for (int i = 0; i < jsonArr.size(); i++){
			JSONObject jsonObj = (JSONObject) jsonArr.get(i);
			parseParameter(jsonObj);
		}
	}

	private void parse(final JSONObject JObj){
		// name
		// parseName(JObj);
		// parameters
		parseParameters(JObj);
	}

	/*
	 * HashCode
	 */
	/**
	 *  Returns a hash code value for the object.
	 *  @return a hash code value for this object.
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((booleanParameters == null) ? 0 : booleanParameters.hashCode());
		result = prime * result + ((byteParameters == null) ? 0 : byteParameters.hashCode());
		result = prime * result + ((charParameters == null) ? 0 : charParameters.hashCode());
		result = prime * result + ((doubleParameters == null) ? 0 : doubleParameters.hashCode());
		result = prime * result + ((floatParameters == null) ? 0 : floatParameters.hashCode());
		result = prime * result + ((intParameters == null) ? 0 : intParameters.hashCode());
		result = prime * result + ((longParameters == null) ? 0 : longParameters.hashCode());
		result = prime * result + ((shortParameters == null) ? 0 : shortParameters.hashCode());
		result = prime * result + ((stringParameters == null) ? 0 : stringParameters.hashCode());
		return result;
	}

	/*
	 * Equals
	 */
	/**
	 *  Indicates whether some other object is "equal to" this one.
	 *  
	 *  @param obj the object to compare with.
	 *  @return true if this object is the same as the obj argument; false otherwise.
	 */
	@Override
	public boolean equals(final Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		AlgorithmProfile other = (AlgorithmProfile) obj;
		if (booleanParameters == null) {
			if (other.booleanParameters != null)
				return false;
		} else if (!booleanParameters.equals(other.booleanParameters))
			return false;
		if (byteParameters == null) {
			if (other.byteParameters != null)
				return false;
		} else if (!byteParameters.equals(other.byteParameters))
			return false;
		if (charParameters == null) {
			if (other.charParameters != null)
				return false;
		} else if (!charParameters.equals(other.charParameters))
			return false;
		if (doubleParameters == null) {
			if (other.doubleParameters != null)
				return false;
		} else if (!doubleParameters.equals(other.doubleParameters))
			return false;
		if (floatParameters == null) {
			if (other.floatParameters != null)
				return false;
		} else if (!floatParameters.equals(other.floatParameters))
			return false;
		if (intParameters == null) {
			if (other.intParameters != null)
				return false;
		} else if (!intParameters.equals(other.intParameters))
			return false;
		if (longParameters == null) {
			if (other.longParameters != null)
				return false;
		} else if (!longParameters.equals(other.longParameters))
			return false;
		if (shortParameters == null) {
			if (other.shortParameters != null)
				return false;
		} else if (!shortParameters.equals(other.shortParameters))
			return false;
		if (stringParameters == null) {
			if (other.stringParameters != null)
				return false;
		} else if (!stringParameters.equals(other.stringParameters))
			return false;
		return true;
	}

	private String stageName;
	private static final String BOOLEAN = "boolean";
	private static final String BYTE = "byte";
	private static final String CHAR = "char";
	private static final String SHORT = "short";
	private static final String INT = "int";
	private static final String LONG = "long";
	private static final String FLOAT = "float";
	private static final String DOUBLE = "double";
	private static final String STRING = "string";
	//private static final String[] parameterNames = {BOOLEAN, BYTE, CHAR, SHORT, INT, LONG, FLOAT, DOUBLE, STRING};
	private Map<String, Boolean> booleanParameters;
	private Map<String, Byte> byteParameters;
	private Map<String, Character> charParameters;
	private Map<String, Short> shortParameters;
	private Map<String, Integer> intParameters;
	private Map<String, Long> longParameters;
	private Map<String, Float> floatParameters;
	private Map<String, Double> doubleParameters;
	private Map<String, String> stringParameters;
}

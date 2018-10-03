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

import org.json.simple.JSONObject;

import common.Utils;

/**
 * The AlgorithmProfileUtils class is class with utility methods for <i>ProfileObject</i> instances.
 * 
 * @author Vincent Mirian
 * 
 * @date Nov 21, 2017
 *
 */
final public class ProfileUtils {

	/**
	 *  Returns an instance of type Boolean containing the contents of parameter <i>value</i>
	 *   
	 *  @param value the Object to cast
	 *  @return if the parameter <i>value</i> is of type Boolean, returns an instance of type Boolean containing the contents of parameter <i>value</i>, otherwise null
	 */
	static public Boolean getBoolean(final Object value) {
		Boolean rtn = null;
		if (
				(value != null)
				&& Utils.isBoolean(value)
				) {
			rtn = (Boolean) value;
		}
		return rtn;
	}

	/**
	 *  Returns an instance of type Byte containing the contents of parameter <i>value</i>
	 *   
	 *  @param value the Object to cast
	 *  @return if the parameter <i>value</i> is of type Long, returns an instance of type Byte containing the contents of parameter <i>value</i>, otherwise null
	 */
	static public Byte getByte(final Object value) {
		Byte rtn = null;
		if (
				(value != null)
				&& Utils.isLong(value)
				) {
			Long temp = (Long) value;
	    	rtn = (Byte) temp.byteValue();
		}
		return rtn;
	}

	/**
	 *  Returns an instance of type Character containing the contents of parameter <i>value</i>
	 *   
	 *  @param value the Object to cast
	 *  @return if the parameter <i>value</i> is of type String, returns an instance of type Character containing the contents of parameter <i>value</i>, otherwise null
	 */
	static public Character getCharacter(final Object value) {
		Character rtn = null;
		if (
				(value != null)
				&& Utils.isString(value)
				) {
	    	String data = (String) value;
	    	if (data.length() > 0)
	    		rtn = data.charAt(0);
		}
		return rtn;
	}

	/**
	 *  Returns an instance of type Short containing the contents of parameter <i>value</i>
	 *   
	 *  @param value the Object to cast
	 *  @return if the parameter <i>value</i> is of type Long, returns an instance of type Short containing the contents of parameter <i>value</i>, otherwise null
	 */
	static public Short getShort(final Object value) {
		Short rtn = null;
		if (
				(value != null)
				&& Utils.isLong(value)
				) {
        	Long temp = (Long) value;
        	rtn = (Short) temp.shortValue();
		}
		return rtn;
	}

	/**
	 *  Returns an instance of type Integer containing the contents of parameter <i>value</i>
	 *   
	 *  @param value the Object to cast
	 *  @return if the parameter <i>value</i> is of type Long, returns an instance of type Integer containing the contents of parameter <i>value</i>, otherwise null
	 */
	static public Integer getInteger(final Object value) {
		Integer rtn = null;
		if (
				(value != null)
				&& Utils.isLong(value)
				) {
        	Long temp = (Long) value;
        	rtn = (Integer) temp.intValue();
		}
		return rtn;
	}

	/**
	 *  Returns an instance of type Long containing the contents of parameter <i>value</i>
	 *   
	 *  @param value the Object to cast
	 *  @return if the parameter <i>value</i> is of type Long, returns an instance of type Long containing the contents of parameter <i>value</i>, otherwise null
	 */
	static public Long getLong(final Object value) {
		Long rtn = null;
		if (
				(value != null)
				&& Utils.isLong(value)
				) {
			rtn = (Long) value;
		}
		return rtn;
	}

	/**
	 *  Returns an instance of type Float containing the contents of parameter <i>value</i>
	 *   
	 *  @param value the Object to cast
	 *  @return if the parameter <i>value</i> is of type Double, returns an instance of type Float containing the contents of parameter <i>value</i>, otherwise null
	 */
	static public Float getFloat(final Object value) {
		Float rtn = null;
		if (
				(value != null)
				&& Utils.isDouble(value)
				) {
        	Double temp = (Double) value;
        	rtn = (Float) temp.floatValue();
		}
		return rtn;
	}

	/**
	 *  Returns an instance of type Double containing the contents of parameter <i>value</i>
	 *   
	 *  @param value the Object to cast
	 *  @return if the parameter <i>value</i> is of type Double, returns an instance of type Double containing the contents of parameter <i>value</i>, otherwise null
	 */
	static public Double getDouble(final Object value) {
		Double rtn = null;
		if (
				(value != null)
				&& Utils.isDouble(value)
				) {
        	rtn = (Double) value;
		}
		return rtn;
	}

	/**
	 *  Returns an instance of type String containing the contents of parameter <i>value</i>
	 *   
	 *  @param value the Object to cast
	 *  @return if the parameter <i>value</i> is of type String, returns an instance of type String containing the contents of parameter <i>value</i>, otherwise null
	 */
	static public String getString(final Object value) {
		String rtn = null;
		if (
				(value != null)
				&& Utils.isString(value)
				) {
        	rtn = (String) value;
		}
		return rtn;
	}

	/**
	 *  Returns an instance of type Boolean containing the value of the attribute's name that is equivalent to parameter <i>member</i> from the JavaScript Object Notation (JSON) representation of the ProfileObject Object (parameter <i>JObj</i>)
	 *   
	 *  @param JObj the JavaScript Object Notation (JSON) representation of the ProfileObject Object
	 *  @param member the name of the attribute
	 *  @return if the attribute is present, then an instance of type Boolean with its contents equivalent to the value of the attribute, otherwise null
	 */
	public static Boolean getBoolean(final JSONObject JObj, final String member) {
		Boolean rtn = null;
		Object value = ProfileUtils.getObject(JObj, member);
		rtn = ProfileUtils.getBoolean(value);
		return rtn;
	}

	/**
	 *  Returns an instance of type Byte containing the value of the attribute's name that is equivalent to parameter <i>member</i> from the JavaScript Object Notation (JSON) representation of the ProfileObject Object (parameter <i>JObj</i>)
	 *   
	 *  @param JObj the JavaScript Object Notation (JSON) representation of the ProfileObject Object
	 *  @param member the name of the attribute
	 *  @return if the attribute is present, then an instance of type Byte with its contents equivalent to the value of the attribute, otherwise null
	 */
	public static Byte getByte(final JSONObject JObj, final String member) {
		Byte rtn = null;
		Object value = ProfileUtils.getObject(JObj, member);
		rtn = ProfileUtils.getByte(value);
		return rtn;
	}

	/**
	 *  Returns an instance of type Character containing the value of the attribute's name that is equivalent to parameter <i>member</i> from the JavaScript Object Notation (JSON) representation of the ProfileObject Object (parameter <i>JObj</i>)
	 *   
	 *  @param JObj the JavaScript Object Notation (JSON) representation of the ProfileObject Object
	 *  @param member the name of the attribute
	 *  @return if the attribute is present, then an instance of type Character with its contents equivalent to the value of the attribute, otherwise null
	 */
	public static Character getCharacter(final JSONObject JObj, final String member) {
		Character rtn = null;
		Object value = ProfileUtils.getObject(JObj, member);
		rtn = ProfileUtils.getCharacter(value);
		return rtn;
	}
	
	/**
	 *  Returns an instance of type Short containing the value of the attribute's name that is equivalent to parameter <i>member</i> from the JavaScript Object Notation (JSON) representation of the ProfileObject Object (parameter <i>JObj</i>)
	 *   
	 *  @param JObj the JavaScript Object Notation (JSON) representation of the ProfileObject Object
	 *  @param member the name of the attribute
	 *  @return if the attribute is present, then an instance of type Short with its contents equivalent to the value of the attribute, otherwise null
	 */
	public static Short getShort(final JSONObject JObj, final String member) {
		Short rtn = null;
		Object value = ProfileUtils.getObject(JObj, member);
		rtn = ProfileUtils.getShort(value);
		return rtn;
	}
	
	/**
	 *  Returns an instance of type Integer containing the value of the attribute's name that is equivalent to parameter <i>member</i> from the JavaScript Object Notation (JSON) representation of the ProfileObject Object (parameter <i>JObj</i>)
	 *   
	 *  @param JObj the JavaScript Object Notation (JSON) representation of the ProfileObject Object
	 *  @param member the name of the attribute
	 *  @return if the attribute is present, then an instance of type Integer with its contents equivalent to the value of the attribute, otherwise null
	 */
	public static Integer getInteger(final JSONObject JObj, final String member) {
		Integer rtn = null;
		Object value = ProfileUtils.getObject(JObj, member);
		rtn = ProfileUtils.getInteger(value);
		return rtn;
	}
	
	/**
	 *  Returns an instance of type Long containing the value of the attribute's name that is equivalent to parameter <i>member</i> from the JavaScript Object Notation (JSON) representation of the ProfileObject Object (parameter <i>JObj</i>)
	 *   
	 *  @param JObj the JavaScript Object Notation (JSON) representation of the ProfileObject Object
	 *  @param member the name of the attribute
	 *  @return if the attribute is present, then an instance of type Long with its contents equivalent to the value of the attribute, otherwise null
	 */
	public static Long getLong(final JSONObject JObj, final String member) {
		Long rtn = null;
		Object value = ProfileUtils.getObject(JObj, member);
		rtn = ProfileUtils.getLong(value);
		return rtn;
	}
	
	/**
	 *  Returns an instance of type Float containing the value of the attribute's name that is equivalent to parameter <i>member</i> from the JavaScript Object Notation (JSON) representation of the ProfileObject Object (parameter <i>JObj</i>)
	 *   
	 *  @param JObj the JavaScript Object Notation (JSON) representation of the ProfileObject Object
	 *  @param member the name of the attribute
	 *  @return if the attribute is present, then an instance of type Float with its contents equivalent to the value of the attribute, otherwise null
	 */
	public static Float getFloat(final JSONObject JObj, final String member) {
		Float rtn = null;
		Object value = ProfileUtils.getObject(JObj, member);
		rtn = ProfileUtils.getFloat(value);
		return rtn;
	}
	
	/**
	 *  Returns an instance of type Double containing the value of the attribute's name that is equivalent to parameter <i>member</i> from the JavaScript Object Notation (JSON) representation of the ProfileObject Object (parameter <i>JObj</i>)
	 *   
	 *  @param JObj the JavaScript Object Notation (JSON) representation of the ProfileObject Object
	 *  @param member the name of the attribute
	 *  @return if the attribute is present, then an instance of type Double with its contents equivalent to the value of the attribute, otherwise null
	 */
	public static Double getDouble(final JSONObject JObj, final String member) {
		Double rtn = null;
		Object value = ProfileUtils.getObject(JObj, member);
		rtn = ProfileUtils.getDouble(value);
		return rtn;
	}
	
	/**
	 *  Returns an instance of type String containing the value of the attribute's name that is equivalent to parameter <i>member</i> from the JavaScript Object Notation (JSON) representation of the ProfileObject Object (parameter <i>JObj</i>)
	 *   
	 *  @param JObj the JavaScript Object Notation (JSON) representation of the ProfileObject Object
	 *  @param member the name of the attribute
	 *  @return if the attribute is present, then an instance of type String with its contents equivalent to the value of the attribute, otherwise null
	 */
	public static String getString(final JSONObject JObj, final String member) {
		String rtn = null;
		Object value = ProfileUtils.getObject(JObj, member);
		rtn = ProfileUtils.getString(value);
		return rtn;
	}
	
	/**
	 *  Returns an instance of type Object containing the value of the attribute's name that is equivalent to parameter <i>member</i> from the JavaScript Object Notation (JSON) representation of the ProfileObject Object (parameter <i>JObj</i>)
	 *   
	 *  @param JObj the JavaScript Object Notation (JSON) representation of the ProfileObject Object
	 *  @param member the name of the attribute
	 *  @return if the attribute is present, then an instance of type Object with its contents equivalent to the value of the attribute, otherwise null
	 */
	public static Object getObject(final JSONObject JObj, final String member) {
		Object rtn = null;
		rtn = (Object) JObj.get(member);
		return rtn;
	}
}

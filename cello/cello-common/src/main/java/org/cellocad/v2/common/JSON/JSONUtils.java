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
package org.cellocad.v2.common.JSON;

import org.cellocad.v2.common.Utils;

/**
 * The JSONUtils class is class with utility methods for building a JSON formatted String.
 * 
 * @author Vincent Mirian
 * 
 * @date Nov 21, 2017
 *
 */

public class JSONUtils {

	/**
	 * Returns a string representing the parameter <i>str</i> indented with parameter <i>numIndent</i> tabulator character(s), but avoids the indentation on the final last line preceeded with a new line.
	 * 
	 * @param numIndent number of indentation(s)
	 * @param str the string to indent
	 * @return a string representing the parameter <i>str</i> indented with parameter <i>numIndent</i> tabulator character(s),but avoids the indentation on the final last line preceeded with a new line.
	 *
	 */
	static public String addIndent(int numIndent, final String str) {
		String rtn = "";
		int index = 0;
		rtn = Utils.addIndent(numIndent, str);
		index = rtn.lastIndexOf(Utils.getNewLine());
		if (index > 0) {
			rtn = rtn.substring(0, index+1);
		}
		return rtn;
	}

	/**
	 * Returns a string with the name of the attribute equivalent to the parameter <i>name</i> in the following JSON format: "<i>name</i>: "
	 * 
	 * @param name the name of the attribute
	 * @return if parameter <i>name</i> is not null, return a string with the name of the attribute equivalent to the parameter <i>name</i> in the following JSON format: "<i>name</i>: ", otherwise "null: "
	 *
	 */
	static private String getKeyEntryToString(final String name) {
		String rtn = "";
		rtn += "\"";
		if (name != null) {
			rtn += name;
		}
		else {
			rtn += "null";
		}
		rtn += "\": ";
		return rtn;	
	}

	/**
	 * Returns a string with the name of the attribute equivalent to the parameter <i>name</i>, and, the value of the attribute equivalent to the parameter <i>value</i> in the following JSON format: "<i>name</i>: <i>value</i>"
	 * 
	 * @param name the name of the attribute
	 * @param value the value of the attribute
	 * @return
	 * if parameter <i>name</i> is not null and parameter <i>value</i> is not null,<br>
	 * return a string in the following JSON format: "<i>name</i>: <i>value</i>",<br>
	 * if parameter <i>name</i> is not null and parameter <i>value</i> is null,<br>
	 * return a string in the following JSON format: "<i>name</i>: null",<br>
	 * if parameter <i>name</i> is null and parameter <i>value</i> is not null,<br>
	 * return a string in the following JSON format: "null: <i>value</i>",<br>
	 * otherwise return a string in the following JSON format: "null: null".
	 *
	 */
	static public String getEntryToString(final String name, final String value) {
		String rtn = "";
		rtn += JSONUtils.getKeyEntryToString(name);
		rtn += JSONUtils.getValueToString(value);
		return rtn;
	}
	
	/**
	 * Returns a string with the value equivalent to the parameter <i>value</i> in the following JSON format: "<i>value</i>"
	 * 
	 * @param value the value of the string
	 * @return
	 * if parameter <i>value</i> is not null,<br>
	 * return a string in the following JSON format: "<i>value</i>",<br>
	 * if parameter <i>value</i> is null,<br>
	 * return a string in the following JSON format: "null",<br>.
	 *
	 */
	static public String getValueToString(final String value) {
		String rtn = "";
		rtn += "\"";
		if (value != null) {
			rtn += value;
		}
		else {
			rtn += "null";
		}
		rtn += "\"";
		rtn += ",";
		rtn += Utils.getNewLine();
		return rtn;
	}

	/**
	 * Returns a string with the name of the attribute equivalent to the parameter <i>name</i>, and, the value of the attribute equivalent to the parameter <i>value</i> in the following JSON format: "<i>name</i>: <i>value</i>"
	 * 
	 * @param name the name of the attribute
	 * @param value the value of the attribute
	 * @return
	 * if parameter <i>name</i> is not null, return a string in the following JSON format: "<i>name</i>: <i>value</i>", otherwise "null: <i>value</i>".
	 *
	 */
	static public String getEntryToString(final String name, int value) {
		String rtn = "";
		rtn += JSONUtils.getKeyEntryToString(name);
		rtn += Integer.toString(value);
		rtn += ",";
		rtn += Utils.getNewLine();
		return rtn;
	}

	/**
	 * Returns a string representing the start entry in JSON format ("{") followed by a new line
	 * 
	 * @return a string representing the start entry in JSON format ("{") followed by a new line
	 *
	 */
	static public String getStartEntryString() {
		String rtn = "";
		rtn += "{";
		rtn += Utils.getNewLine();
		return rtn;
	}

	/**
	 * Returns a string representing the end entry in JSON format ("}") followed by a common (",") and a new line
	 * 
	 * @return a string representing the end entry in JSON format ("}") followed by a common (",") and a new line
	 *
	 */
	static public String getEndEntryString() {
		String rtn = "";
		rtn += "}";
		rtn += ",";
		rtn += Utils.getNewLine();
		return rtn;
	}

	/**
	 * Returns a string with the name of the attribute equivalent to the parameter <i>name</i> followed by a new line and a string for the start array in the following JSON format:<br>
	 * "<i>name</i>: <br>
	 * ["
	 * 
	 * @param name the name of the attribute
	 * @return if parameter <i>name</i> is not null, return a string with the name of the attribute equivalent to the parameter <i>name</i> followed by a new line and a string for the start array in the following JSON format:<br>
	 * "<i>name</i>: <br>
	 * [",
	 * otherwise 
	 * "null: <br>
	 * ["
	 */
	static public String getStartArrayWithMemberString(final String name) {
		String rtn = "";
		rtn += JSONUtils.getKeyEntryToString(name);
		rtn += Utils.getNewLine();
		rtn += JSONUtils.getStartArrayString();
		return rtn;
	}

	/**
	 * Returns a string representing the start array in JSON format ("[") followed by a new line
	 * 
	 * @return a string representing the start array in JSON format ("[") followed by a new line
	 *
	 */
	static public String getStartArrayString() {
		String rtn = "";
		rtn += "[";
		rtn += Utils.getNewLine();
		return rtn;
	}

	/**
	 * Returns a string representing the end array in JSON format ("]") followed by a common (",") and a new line
	 * 
	 * @return a string representing the end array in JSON format ("]") followed by a common (",") and a new line
	 *
	 */
	static public String getEndArrayString() {
		String rtn = "";
		rtn += "]";
		rtn += ",";
		rtn += Utils.getNewLine();
		return rtn;
	}
}

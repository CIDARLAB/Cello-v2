/*
 * Copyright (C) 2017 Massachusetts Institute of Technology (MIT)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package org.cellocad.v2.common.json;

import org.cellocad.v2.common.Utils;

/**
 * Utility methods for building a JSON formatted {@link String}.
 *
 * @author Vincent Mirian
 * @date Nov 21, 2017
 */
public class JsonUtils {

  /**
   * Returns a string representing the parameter {@code str} indented with parameter {@code
   * numIndent} tabulator character(s), but avoids the indentation on the final last line preceeded
   * with a new line.
   *
   * @param numIndent number of indentation(s).
   * @param str The string to indent.
   * @return A string representing the parameter {@code str} indented with parameter {@code
   *     numIndent} tabulator character(s),but avoids the indentation on the final last line
   *     preceeded with a new line.
   */
  public static String addIndent(final int numIndent, final String str) {
    String rtn = "";
    int index = 0;
    rtn = Utils.addIndent(numIndent, str);
    index = rtn.lastIndexOf(Utils.getNewLine());
    if (index > 0) {
      rtn = rtn.substring(0, index + 1);
    }
    return rtn;
  }

  /**
   * Returns a string with the name of the attribute equivalent to the parameter {@code name} in the
   * following JSON format: "<i>name</i>: ".
   *
   * @param name The name of the attribute.
   * @return If parameter {@code name} is not null, return a string with the name of the attribute
   *     equivalent to the parameter {@code name} in the following JSON format: "<i>name</i>: ",
   *     otherwise "null: ".
   */
  private static String getKeyEntryToString(final String name) {
    String rtn = "";
    rtn += "\"";
    if (name != null) {
      rtn += name;
    } else {
      rtn += "null";
    }
    rtn += "\": ";
    return rtn;
  }

  /**
   * Returns a string with the name of the attribute equivalent to the parameter {@code name}, and,
   * the value of the attribute equivalent to the parameter {@code value} in the following JSON
   * format: "<i>name</i>: <i>value</i>".
   *
   * @param name The name of the attribute.
   * @param value The value of the attribute.
   * @return If parameter {@code name} is not null and parameter {@code value} is not null,<br>
   *     . return a string in the following JSON format: "<i>name</i>: <i>value</i>",<br>
   *     if parameter {@code name} is not null and parameter {@code value} is null,<br>
   *     return a string in the following JSON format: "<i>name</i>: null",<br>
   *     if parameter {@code name} is null and parameter {@code value} is not null,<br>
   *     return a string in the following JSON format: "null: <i>value</i>",<br>
   *     otherwise return a string in the following JSON format: "null: null".
   */
  public static String getEntryToString(final String name, final String value) {
    String rtn = "";
    rtn += JsonUtils.getKeyEntryToString(name);
    rtn += JsonUtils.getValueToString(value);
    return rtn;
  }

  /**
   * Returns a string with the name of the attribute equivalent to the parameter {@code name}, and,
   * the value of the attribute equivalent to the parameter {@code value} in the following JSON
   * format: "<i>name</i>: <i>value</i>".
   *
   * @param name The name of the attribute.
   * @param value The value of the attribute.
   * @return If parameter {@code name} is not null, return a string in the following JSON format:
   *     "<i>name</i>: <i>value</i>", otherwise "null: <i>value</i>".
   */
  public static String getEntryToString(final String name, final int value) {
    String rtn = "";
    rtn += JsonUtils.getKeyEntryToString(name);
    rtn += Integer.toString(value);
    rtn += ",";
    rtn += Utils.getNewLine();
    return rtn;
  }

  /**
   * Returns a string with the value equivalent to the parameter {@code value} in the following JSON
   * format: "<i>value</i>".
   *
   * @param value The value of the string.
   * @return If parameter {@code value} is not null,<br>
   *     . return a string in the following JSON format: "<i>value</i>",<br>
   *     if parameter {@code value} is null,<br>
   *     return a string in the following JSON format: "null",<br>
   *     .
   */
  public static String getValueToString(final String value) {
    String rtn = "";
    rtn += "\"";
    if (value != null) {
      rtn += value;
    } else {
      rtn += "null";
    }
    rtn += "\"";
    rtn += ",";
    rtn += Utils.getNewLine();
    return rtn;
  }

  /**
   * Returns a string representing the start entry in JSON format ("{") followed by a new line.
   *
   * @return A string representing the start entry in JSON format ("{") followed by a new line.
   */
  public static String getStartEntryString() {
    String rtn = "";
    rtn += "{";
    rtn += Utils.getNewLine();
    return rtn;
  }

  /**
   * Returns a string representing the end entry in JSON format ("}") followed by a common (",") and
   * a new line.
   *
   * @return A string representing the end entry in JSON format ("}") followed by a common (",") and
   *     a new line.
   */
  public static String getEndEntryString() {
    String rtn = "";
    rtn += "}";
    rtn += ",";
    rtn += Utils.getNewLine();
    return rtn;
  }

  /**
   * Returns a string with the name of the attribute equivalent to the parameter {@code name}
   * followed by a new line and a string for the start array in the following JSON format:<br>
   * . "<i>name</i>: <br>
   * [".
   *
   * @param name The name of the attribute.
   * @return If parameter {@code name} is not null, return a string with the name of the attribute
   *     equivalent to the parameter {@code name} followed by a new line and a string for the start
   *     array in the following JSON format:<br>
   *     . "<i>name</i>: <br>
   *     [", otherwise "null: <br>
   *     [".
   */
  public static String getStartArrayWithMemberString(final String name) {
    String rtn = "";
    rtn += JsonUtils.getKeyEntryToString(name);
    rtn += Utils.getNewLine();
    rtn += JsonUtils.getStartArrayString();
    return rtn;
  }

  /**
   * Returns a string representing the start array in JSON format ("[") followed by a new line.
   *
   * @return A string representing the start array in JSON format ("[") followed by a new line.
   */
  public static String getStartArrayString() {
    String rtn = "";
    rtn += "[";
    rtn += Utils.getNewLine();
    return rtn;
  }

  /**
   * Returns a string representing the end array in JSON format ("]") followed by a common (",") and
   * a new line.
   *
   * @return A string representing the end array in JSON format ("]") followed by a common (",") and
   *     a new line.
   */
  public static String getEndArrayString() {
    String rtn = "";
    rtn += "]";
    rtn += ",";
    rtn += Utils.getNewLine();
    return rtn;
  }
}

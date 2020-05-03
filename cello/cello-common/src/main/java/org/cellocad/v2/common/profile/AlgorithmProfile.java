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

package org.cellocad.v2.common.profile;

import java.util.HashMap;
import java.util.Map;
import org.cellocad.v2.common.Pair;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 * The AlgorithmProfile class is a class containing the configuration for an algorithm. It is the
 * software representation of the algorithm configuration file.
 *
 * @author Vincent Mirian
 *
 * @date Oct 27, 2017
 */
public final class AlgorithmProfile extends ProfileObject {

  /**
   * Initializes a newly created {@link AlgorithmProfile} using the parameter {@code jsonObj}.
   *
   * @param jsonObj The JavaScript Object Notation (JSON) representation of the AlgorithmProfile
   *                Object.
   */
  public AlgorithmProfile(final JSONObject jsonObj) {
    super(jsonObj);
    booleanParameters = new HashMap<>();
    byteParameters = new HashMap<>();
    charParameters = new HashMap<>();
    shortParameters = new HashMap<>();
    intParameters = new HashMap<>();
    longParameters = new HashMap<>();
    floatParameters = new HashMap<>();
    doubleParameters = new HashMap<>();
    stringParameters = new HashMap<>();
    stageName = "";
    parse(jsonObj);
  }

  /**
   * Getter for {@code stageName}.
   *
   * @return The stageName of this instance.
   */
  public String getStageName() {
    return stageName;
  }

  /**
   * Setter for {@code stageName}.
   *
   * @param str The name of the stage to set <i>stageName</i>.
   */
  public void setStageName(final String str) {
    stageName = str;
  }

  /**
   * Getter for the boolean parameter with its name equivalent to parameter {@code name}.
   *
   * @param name The name of the parameter.
   * @return A pair instance with the first element representing the validity of the second element,
   *         and, the second element contains the value of the parameter {@code name}. If the
   *         parameter is present for this instance, then the value of the first element is true,
   *         otherwise false. If the parameter is present for this instance, the value of the second
   *         element is the value of the parameter, otherwise null.
   */
  public Pair<Boolean, Boolean> getBooleanParameter(final String name) {
    final Boolean value = booleanParameters.get(name);
    final boolean first = value != null;
    final Pair<Boolean, Boolean> rtn = new Pair<>(new Boolean(first), value);
    return rtn;
  }

  /**
   * Setter for the boolean parameter with its name equivalent to parameter {@code name}.
   *
   * @param name  The name of the parameter.
   * @param value The value of the parameter.
   */
  public void setBooleanParameter(final String name, final Boolean value) {
    booleanParameters.put(name, value);
  }

  /**
   * Getter for the byte parameter with its name equivalent to parameter {@code name}.
   *
   * @param name The name of the parameter.
   * @return A pair instance with the first element representing the validity of the second element,
   *         and, the second element contains the value of the parameter {@code name}. If the
   *         parameter is present for this instance, then the value of the first element is true,
   *         otherwise false. If the parameter is present for this instance, the value of the second
   *         element is the value of the parameter, otherwise null.
   */
  public Pair<Boolean, Byte> getByteParameter(final String name) {
    final Byte value = byteParameters.get(name);
    final boolean first = value != null;
    final Pair<Boolean, Byte> rtn = new Pair<>(new Boolean(first), value);
    return rtn;
  }

  /**
   * Setter for the byte parameter with its name equivalent to parameter {@code name}.
   *
   * @param name  The name of the parameter.
   * @param value The value of the parameter.
   */
  public void setByteParameter(final String name, final Byte value) {
    byteParameters.put(name, value);
  }

  /**
   * Getter for the character parameter with its name equivalent to parameter {@code name}.
   *
   * @param name The name of the parameter.
   * @return A pair instance with the first element representing the validity of the second element,
   *         and, the second element contains the value of the parameter {@code name}. If the
   *         parameter is present for this instance, then the value of the first element is true,
   *         otherwise false. If the parameter is present for this instance, the value of the second
   *         element is the value of the parameter, otherwise null.
   */
  public Pair<Boolean, Character> getCharParameter(final String name) {
    final Character value = charParameters.get(name);
    final boolean first = value != null;
    final Pair<Boolean, Character> rtn = new Pair<>(new Boolean(first), value);
    return rtn;
  }

  /**
   * Setter for the character parameter with its name equivalent to parameter {@code name}.
   *
   * @param name  The name of the parameter.
   * @param value The value of the parameter.
   */
  public void setCharacterParameter(final String name, final Character value) {
    charParameters.put(name, value);
  }

  /**
   * Getter for the short parameter with its name equivalent to parameter {@code name}.
   *
   * @param name The name of the parameter.
   * @return A pair instance with the first element representing the validity of the second element,
   *         and, the second element contains the value of the parameter {@code name}. If the
   *         parameter is present for this instance, then the value of the first element is true,
   *         otherwise false. If the parameter is present for this instance, the value of the second
   *         element is the value of the parameter, otherwise null.
   */
  public Pair<Boolean, Short> getShortParameter(final String name) {
    final Short value = shortParameters.get(name);
    final boolean first = value != null;
    final Pair<Boolean, Short> rtn = new Pair<>(new Boolean(first), value);
    return rtn;
  }

  /**
   * Setter for the short parameter with its name equivalent to parameter {@code name}.
   *
   * @param name  The name of the parameter.
   * @param value The value of the parameter.
   */
  public void setShortParameter(final String name, final Short value) {
    shortParameters.put(name, value);
  }

  /**
   * Getter for the integer parameter with its name equivalent to parameter {@code name}.
   *
   * @param name The name of the parameter.
   * @return A pair instance with the first element representing the validity of the second element,
   *         and, the second element contains the value of the parameter {@code name}. If the
   *         parameter is present for this instance, then the value of the first element is true,
   *         otherwise false. If the parameter is present for this instance, the value of the second
   *         element is the value of the parameter, otherwise null.
   */
  public Pair<Boolean, Integer> getIntParameter(final String name) {
    final Integer value = intParameters.get(name);
    final boolean first = value != null;
    final Pair<Boolean, Integer> rtn = new Pair<>(new Boolean(first), value);
    return rtn;
  }

  /**
   * Setter for the integer parameter with its name equivalent to parameter {@code name}.
   *
   * @param name  The name of the parameter.
   * @param value The value of the parameter.
   */
  public void setIntegerParameter(final String name, final Integer value) {
    intParameters.put(name, value);
  }

  /**
   * Getter for the long parameter with its name equivalent to parameter {@code name}.
   *
   * @param name The name of the parameter.
   * @return A pair instance with the first element representing the validity of the second element,
   *         and, the second element contains the value of the parameter {@code name}. If the
   *         parameter is present for this instance, then the value of the first element is true,
   *         otherwise false. If the parameter is present for this instance, the value of the second
   *         element is the value of the parameter, otherwise null.
   */
  public Pair<Boolean, Long> getLongParameter(final String name) {
    final Long value = longParameters.get(name);
    final boolean first = value != null;
    final Pair<Boolean, Long> rtn = new Pair<>(new Boolean(first), value);
    return rtn;
  }

  /**
   * Setter for the long parameter with its name equivalent to parameter {@code name}.
   *
   * @param name  The name of the parameter.
   * @param value The value of the parameter.
   */
  public void setLongParameter(final String name, final Long value) {
    longParameters.put(name, value);
  }

  /**
   * Getter for the float parameter with its name equivalent to parameter {@code name}.
   *
   * @param name The name of the parameter.
   * @return A pair instance with the first element representing the validity of the second element,
   *         and, the second element contains the value of the parameter {@code name}. If the
   *         parameter is present for this instance, then the value of the first element is true,
   *         otherwise false. If the parameter is present for this instance, the value of the second
   *         element is the value of the parameter, otherwise null.
   */
  public Pair<Boolean, Float> getFloatParameter(final String name) {
    final Float value = floatParameters.get(name);
    final boolean first = value != null;
    final Pair<Boolean, Float> rtn = new Pair<>(new Boolean(first), value);
    return rtn;
  }

  /**
   * Setter for the float parameter with its name equivalent to parameter {@code name}.
   *
   * @param name  The name of the parameter.
   * @param value The value of the parameter.
   */
  public void setFloatParameter(final String name, final Float value) {
    floatParameters.put(name, value);
  }

  /**
   * Getter for the double parameter with its name equivalent to parameter {@code name}.
   *
   * @param name The name of the parameter.
   * @return A pair instance with the first element representing the validity of the second element,
   *         and, the second element contains the value of the parameter {@code name}. If the
   *         parameter is present for this instance, then the value of the first element is true,
   *         otherwise false. If the parameter is present for this instance, the value of the second
   *         element is the value of the parameter, otherwise null.
   */
  public Pair<Boolean, Double> getDoubleParameter(final String name) {
    final Double value = doubleParameters.get(name);
    final boolean first = value != null;
    final Pair<Boolean, Double> rtn = new Pair<>(new Boolean(first), value);
    return rtn;
  }

  /**
   * Setter for the double parameter with its name equivalent to parameter {@code name}.
   *
   * @param name  The name of the parameter.
   * @param value The value of the parameter.
   */
  public void setDoubleParameter(final String name, final Double value) {
    doubleParameters.put(name, value);
  }

  /**
   * Getter for the string parameter with its name equivalent to parameter {@code name}.
   *
   * @param name The name of the parameter.
   * @return A pair instance with the first element representing the validity of the second element,
   *         and, the second element contains the value of the parameter {@code name}. If the
   *         parameter is present for this instance, then the value of the first element is true,
   *         otherwise false. If the parameter is present for this instance, the value of the second
   *         element is the value of the parameter, otherwise null.
   */
  public Pair<Boolean, String> getStringParameter(final String name) {
    final String value = stringParameters.get(name);
    final boolean first = value != null;
    final Pair<Boolean, String> rtn = new Pair<>(new Boolean(first), value);
    return rtn;
  }

  public void setStringParameter(final String name, final String value) {
    stringParameters.put(name, value);
  }

  /*
   * Parse
   */

  private void parseParameter(final JSONObject jsonObj) {
    // name
    final String name = ProfileUtils.getString(jsonObj, "name");
    if (name == null) {
      throw new RuntimeException(
          "Name not specified for parameter in AlgorithmProfile " + getName() + ".");
    }
    // type
    final String type = ProfileUtils.getString(jsonObj, "type");
    if (type == null) {
      throw new RuntimeException("Type not specified for parameter " + name + ".");
    }
    // value
    final Object value = ProfileUtils.getObject(jsonObj, "value");
    if (value == null) {
      throw new RuntimeException("Value not specified for parameter " + name + ".");
    }
    switch (type) {
      case BOOLEAN: {
        final Boolean data = ProfileUtils.getBoolean(jsonObj, "value");
        setBooleanParameter(name, data);
        break;
      }
      case BYTE: {
        final Byte data = ProfileUtils.getByte(jsonObj, "value");
        setByteParameter(name, data);
        break;
      }
      case CHAR: {
        final Character c = ProfileUtils.getCharacter(jsonObj, "value");
        setCharacterParameter(name, c);
        break;
      }
      case SHORT: {
        final Short data = ProfileUtils.getShort(jsonObj, "value");
        setShortParameter(name, data);
        break;
      }
      case INT: {
        final Integer data = ProfileUtils.getInteger(jsonObj, "value");
        setIntegerParameter(name, data);
        break;
      }
      case LONG: {
        final Long data = ProfileUtils.getLong(jsonObj, "value");
        setLongParameter(name, data);
        break;
      }
      case FLOAT: {
        final Float data = ProfileUtils.getFloat(jsonObj, "value");
        setFloatParameter(name, data);
        break;
      }
      case DOUBLE: {
        final Double data = ProfileUtils.getDouble(jsonObj, "value");
        setDoubleParameter(name, data);
        break;
      }
      case STRING: {
        final String data = ProfileUtils.getString(jsonObj, "value");
        setStringParameter(name, data);
        break;
      }
      default: {
        throw new RuntimeException("Invalid type for parameter " + name + ".");
      }
    }
  }

  private void parseParameters(final JSONObject jsonObj) {
    final JSONArray jsonArr = (JSONArray) jsonObj.get("parameters");
    for (int i = 0; i < jsonArr.size(); i++) {
      final JSONObject paramObj = (JSONObject) jsonArr.get(i);
      parseParameter(paramObj);
    }
  }

  private void parse(final JSONObject jsonObj) {
    // name
    // parseName(JObj);
    // parameters
    parseParameters(jsonObj);
  }

  /*
   * HashCode
   */
  /**
   * Returns a hash code value for the object.
   *
   * @return A hash code value for this object.
   */
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + (booleanParameters == null ? 0 : booleanParameters.hashCode());
    result = prime * result + (byteParameters == null ? 0 : byteParameters.hashCode());
    result = prime * result + (charParameters == null ? 0 : charParameters.hashCode());
    result = prime * result + (doubleParameters == null ? 0 : doubleParameters.hashCode());
    result = prime * result + (floatParameters == null ? 0 : floatParameters.hashCode());
    result = prime * result + (intParameters == null ? 0 : intParameters.hashCode());
    result = prime * result + (longParameters == null ? 0 : longParameters.hashCode());
    result = prime * result + (shortParameters == null ? 0 : shortParameters.hashCode());
    result = prime * result + (stringParameters == null ? 0 : stringParameters.hashCode());
    return result;
  }

  /*
   * Equals
   */
  /**
   * Indicates whether some other object is "equal to" this one.
   *
   * @param obj The object to compare with.
   * @return True if this object is the same as the obj argument; false otherwise.
   */
  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (!super.equals(obj)) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final AlgorithmProfile other = (AlgorithmProfile) obj;
    if (booleanParameters == null) {
      if (other.booleanParameters != null) {
        return false;
      }
    } else if (!booleanParameters.equals(other.booleanParameters)) {
      return false;
    }
    if (byteParameters == null) {
      if (other.byteParameters != null) {
        return false;
      }
    } else if (!byteParameters.equals(other.byteParameters)) {
      return false;
    }
    if (charParameters == null) {
      if (other.charParameters != null) {
        return false;
      }
    } else if (!charParameters.equals(other.charParameters)) {
      return false;
    }
    if (doubleParameters == null) {
      if (other.doubleParameters != null) {
        return false;
      }
    } else if (!doubleParameters.equals(other.doubleParameters)) {
      return false;
    }
    if (floatParameters == null) {
      if (other.floatParameters != null) {
        return false;
      }
    } else if (!floatParameters.equals(other.floatParameters)) {
      return false;
    }
    if (intParameters == null) {
      if (other.intParameters != null) {
        return false;
      }
    } else if (!intParameters.equals(other.intParameters)) {
      return false;
    }
    if (longParameters == null) {
      if (other.longParameters != null) {
        return false;
      }
    } else if (!longParameters.equals(other.longParameters)) {
      return false;
    }
    if (shortParameters == null) {
      if (other.shortParameters != null) {
        return false;
      }
    } else if (!shortParameters.equals(other.shortParameters)) {
      return false;
    }
    if (stringParameters == null) {
      if (other.stringParameters != null) {
        return false;
      }
    } else if (!stringParameters.equals(other.stringParameters)) {
      return false;
    }
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
  // private static final String[] parameterNames = {BOOLEAN, BYTE, CHAR, SHORT,
  // INT, LONG, FLOAT, DOUBLE, STRING};
  private final Map<String, Boolean> booleanParameters;
  private final Map<String, Byte> byteParameters;
  private final Map<String, Character> charParameters;
  private final Map<String, Short> shortParameters;
  private final Map<String, Integer> intParameters;
  private final Map<String, Long> longParameters;
  private final Map<String, Float> floatParameters;
  private final Map<String, Double> doubleParameters;
  private final Map<String, String> stringParameters;

}

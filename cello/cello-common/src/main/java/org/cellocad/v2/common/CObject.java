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

package org.cellocad.v2.common;

/**
 * The CObject class is the base object for all classes within the Poros framework.
 *
 * @author Vincent Mirian
 * @date Oct 26, 2017
 */
public class CObject {

  /**
   * Initializes a newly created {@link CObject} with an empty string as its <i>name</i>, its
   * <i>type</i> equivalent to -1, and, its <i>idx</i> equivalent to -1.
   */
  public CObject() {
    setName("");
    setType(-1);
    setIdx(-1);
  }

  /**
   * Initializes a newly created {@link CObject} with its <i>name</i> set to parameter {@code name},
   * its <i>type</i> set to parameter {@code type}, and, its <i>idx</i> set to parameter {@code
   * idx}.
   *
   * @param name The name of the CObject.
   * @param type The type of the CObject.
   * @param idx The idx of the CObject.
   */
  public CObject(final String name, final int type, final int idx) {
    setName(name);
    setType(type);
    setIdx(idx);
  }

  /**
   * Initializes a newly created {@link CObject} with its contents set to those of parameter {@code
   * other}.
   *
   * @param other The other CObject.
   */
  public CObject(final CObject other) {
    this(other.getName(), other.getType(), other.getIdx());
  }

  /**
   * Creates and returns a copy of this object.
   *
   * @return A clone of this instance.
   */
  @Override
  public CObject clone() {
    CObject rtn;
    rtn = new CObject(this);
    return rtn;
  }

  /**
   * Setter for {@code name}.
   *
   * @param name The name to set <i>name</i>.
   */
  public void setName(final String name) {
    this.name = name;
  }

  /**
   * Getter for {@code name}.
   *
   * @return The name of this instance.
   */
  public String getName() {
    return name;
  }

  /**
   * Setter for {@code type}.
   *
   * @param type The type to set <i>type</i>.
   */
  public void setType(final int type) {
    this.type = type;
  }

  /**
   * Getter for {@code type}.
   *
   * @return The type of this instance.
   */
  public int getType() {
    return type;
  }

  /**
   * Setter for {@code idx}.
   *
   * @param idx The idx to set <i>idx</i>.
   */
  public void setIdx(final int idx) {
    this.idx = idx;
  }

  /**
   * Getter for {@code idx}.
   *
   * @return The idx of this instance.
   */
  public int getIdx() {
    return idx;
  }

  /*
   * is valid?
   */
  /**
   * Returns a boolean flag signifying the validity of this instance.
   *
   * @return True if the instance is valid; false otherwise.
   */
  public boolean isValid() {
    return true;
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
    result = prime * result + idx;
    result = prime * result + (name == null ? 0 : name.hashCode());
    result = prime * result + type;
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
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final CObject other = (CObject) obj;
    if (idx != other.idx) {
      return false;
    }
    if (name == null) {
      if (other.name != null) {
        return false;
      }
    } else if (!name.equals(other.name)) {
      return false;
    }
    if (type != other.type) {
      return false;
    }
    return true;
  }

  /*
   * toString
   */
  /**
   * Returns a string with the <i>name</i> parameter concatenated with the <i>value</i> parameter in
   * the following form without the quotes: "<i>name</i> = <i>value</i>,".
   *
   * @param name The name.
   * @param value The value.
   * @return A string with the <i>name</i> parameter concatenated with the <i>value</i> parameter.
   */
  protected String getEntryToString(final String name, final String value) {
    String rtn = "";
    rtn = rtn + Utils.getTabCharacter();
    rtn = rtn + name;
    rtn = rtn + " = ";
    rtn = rtn + value;
    rtn = rtn + ",";
    rtn = rtn + Utils.getNewLine();
    return rtn;
  }

  /**
   * Returns a string with the <i>name</i> parameter concatenated with the <i>value</i> parameter in
   * the following form without the quotes: "<i>name</i> = <i>value</i>,".
   *
   * @param name The name.
   * @param value The value.
   * @return A string with the <i>name</i> parameter concatenated with the <i>value</i> parameter.
   */
  protected String getEntryToString(final String name, final int value) {
    String rtn = "";
    rtn = rtn + this.getEntryToString(name, Integer.toString(value));
    return rtn;
  }

  /**
   * Returns a string with the <i>name</i> parameter concatenated with the <i>value</i> parameter in
   * the following form without the quotes: "<i>name</i> = <i>value</i>,".
   *
   * @param name The name.
   * @param value The value.
   * @return A string with the <i>name</i> parameter concatenated with the <i>value</i> parameter.
   */
  protected String getEntryToString(final String name, final boolean value) {
    String rtn = "";
    rtn = rtn + this.getEntryToString(name, Boolean.toString(value));
    return rtn;
  }

  /**
   * Returns a string representation of the object.
   *
   * @return A string representation of the object.
   */
  @Override
  public String toString() {
    String rtn = "";
    rtn = rtn + "[ ";
    rtn = rtn + Utils.getNewLine();
    // name
    rtn = rtn + this.getEntryToString("name", name);
    // type
    rtn = rtn + this.getEntryToString("type", type);
    // idx
    rtn = rtn + this.getEntryToString("idx", idx);
    // isValid
    rtn = rtn + this.getEntryToString("isValid()", isValid());
    // className
    rtn = rtn + this.getEntryToString("getClass()", getClass().getName());
    // toString
    rtn = rtn + Utils.getTabCharacter();
    rtn = rtn + "toString() = ";
    rtn = rtn + super.toString();
    rtn = rtn + Utils.getNewLine();
    // end
    rtn = rtn + "]";
    return rtn;
  }

  /*
   * Members of class
   */
  private String name;
  private int type;
  private int idx;
}

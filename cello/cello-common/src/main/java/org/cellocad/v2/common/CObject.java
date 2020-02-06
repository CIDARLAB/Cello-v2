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
package org.cellocad.v2.common;

/**
 * The CObject class is the base object for all classes within the Poros framework.
 *  
 * @author Vincent Mirian
 * 
 * @date Oct 26, 2017
 *
 */
public class CObject {

	/**
	 *  Initializes a newly created CObject with an empty string as its <i>name</i>,
	 *  its <i>type</i> equivalent to -1, and,
	 *  its <i>idx</i> equivalent to -1.
	 */
	public CObject(){
		this.setName("");
        this.setType(-1);
        this.setIdx(-1);
	}
	
	/**
	 *  Initializes a newly created CObject with its <i>name</i> set to parameter <i>name</i>,
	 *  its <i>type</i> set to parameter <i>type</i>, and,
	 *  its <i>idx</i> set to parameter <i>idx</i>.
	 *  
	 *  @param name the name of the CObject
	 *  @param type the type of the CObject
	 *  @param idx the idx of the CObject
	 */
	public CObject(final String name, int type, int idx) {
		this.setName(name);
        this.setType(type);
        this.setIdx(idx);
    }
	
	/**
	 *  Initializes a newly created CObject with its contents set to those of <i>other</i>.
	 *  
	 *  @param other the other CObject
	 */
	public CObject(final CObject other){
		this(other.getName(), other.getType(), other.getIdx());
	}
	
	/**
	 *  Creates and returns a copy of this object.
	 *  @return a clone of this instance.
	 */
	@Override
	public CObject clone(){
		CObject rtn;
		rtn = new CObject(this);
		return rtn;
	}

	/**
	 *  Setter for <i>name</i>
	 *  @param name the name to set <i>name</i>
	 */
	public void setName(final String name){
		this.name = name;
	}

	/**
	 *  Getter for <i>name</i>
	 *  @return the name of this instance
	 */
	public String getName(){
		return this.name;
	}

	/**
	 *  Setter for <i>type</i>
	 *  @param type the type to set <i>type</i>
	 */
	public void setType(int type){
		this.type = type;
	}

	/**
	 *  Getter for <i>type</i>
	 *  @return the type of this instance
	 */
	public int getType(){
		return this.type;
	}

	/**
	 *  Setter for <i>idx</i>
	 *  @param idx the idx to set <i>idx</i>
	 */
	public void setIdx(int idx){
		this.idx = idx;
	}

	/**
	 *  Getter for <i>idx</i>
	 *  @return the idx of this instance
	 */
	public int getIdx(){
		return this.idx;
	}

	/*
	 * is valid?
	 */
	/**
	 *  Returns a boolean flag signifying the validity of this instance
	 *  @return true if the instance is valid; false otherwise.
	 */
	public boolean isValid(){
		return true;
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
		result = prime * result + idx;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + type;
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
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CObject other = (CObject) obj;
		if (idx != other.idx)
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (type != other.type)
			return false;
		return true;
	}
	
	/*
	 * toString
	 */
	/**
	 *  Returns a string with the <i>name</i> parameter concatenated with the <i>value</i> parameter in the following form without the quotes: 
	 *  "<i>name</i> = <i>value</i>,".
	 *  
	 *  @param name the name.
	 *  @param value the value.
	 *  @return a string with the <i>name</i> parameter concatenated with the <i>value</i> parameter.
	 */
	protected String getEntryToString(String name, String value) {
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
	 *  Returns a string with the <i>name</i> parameter concatenated with the <i>value</i> parameter in the following form without the quotes: 
	 *  "<i>name</i> = <i>value</i>,".
	 *  
	 *  @param name the name.
	 *  @param value the value.
	 *  @return a string with the <i>name</i> parameter concatenated with the <i>value</i> parameter.
	 */
	protected String getEntryToString(String name, int value) {
		String rtn = "";
		rtn = rtn + this.getEntryToString(name, Integer.toString(value));
		return rtn;
	}

	/**
	 *  Returns a string with the <i>name</i> parameter concatenated with the <i>value</i> parameter in the following form without the quotes: 
	 *  "<i>name</i> = <i>value</i>,".
	 *  
	 *  @param name the name.
	 *  @param value the value.
	 *  @return a string with the <i>name</i> parameter concatenated with the <i>value</i> parameter.
	 */
	protected String getEntryToString(String name, boolean value) {
		String rtn = "";
		rtn = rtn + this.getEntryToString(name, Boolean.toString(value));
		return rtn;
	}

	/**
	 *  Returns a string representation of the object.
	 *  @return a string representation of the object.
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

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

package org.cellocad.v2.common.file.csv.utils;

import org.cellocad.v2.common.CObject;

/**
 * The CsvRecord class is containing a record for a CSV file.
 *
 * @author Vincent Mirian
 *
 * @date Oct 26, 2017
 */
public class CsvRecord extends CObject {

  /**
   * Initializes a newly created {@link CsvReader} with a record defined by parameter {@code line},
   * and, a delimeter for the fields of the record defined by parameter {@code delimeter}.
   *
   * @param line      The record.
   * @param delimeter The delimeter.
   */
  public CsvRecord(final String line, final String delimeter) {
    setFields(line.split(delimeter));
  }

  /**
   * Returns the field at the specified position in this instance.
   *
   * @param index The index of the field to return.
   * @return If the index is within the bounds (0 <= bounds < this.getNumFields()), returns the
   *         field at the specified position in this instance, otherwise null.
   */
  public String getFieldAtIdx(final int index) {
    String rtn = null;
    if (index >= 0 && index < getNumFields()) {
      rtn = getFields()[index];
    }
    return rtn;
  }

  /**
   * Returns the number of fields in this instance.
   *
   * @return The number of fields in this instance.
   */
  public int getNumFields() {
    return getFields().length;
  }

  /**
   * Setter for {@code fields}.
   *
   * @param fields The value to set {@code fields}.
   */
  public void setFields(final String[] fields) {
    this.fields = fields;
  }

  /**
   * Getter for {@code fields}.
   *
   * @return The value of {@code fields}.
   */
  private String[] getFields() {
    return fields;
  }

  private String[] fields;

}

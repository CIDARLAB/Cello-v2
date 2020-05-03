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

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import org.cellocad.v2.common.CObject;

/**
 * A reader for a CSV file.
 *
 * @author Vincent Mirian
 *
 * @date Oct 26, 2017
 */
public class CsvReader extends CObject {

  /**
   * Initializes a newly created {@link CsvReader} with a file defined by parameter
   * {@code filename}, and, a delimeter for the fields of the record defined by parameter
   * {@code delimeter}.
   *
   * @param filename  The filename.
   * @param delimeter The delimeter.
   */
  public CsvReader(final String filename, final String delimeter) {
    try {
      br = new BufferedReader(new FileReader(filename));
      setCsvSplitBy(delimeter);
    } catch (final FileNotFoundException e) {
      e.printStackTrace();
    }
  }

  /**
   * Returns the next {@link CsvRecord}.
   *
   * @return The next {@link CsvRecord}, if one exists, null otherwise.
   */
  public CsvRecord getNextRecord() {
    CsvRecord rtn = null;
    String line = "";
    try {
      if ((line = getBufferedReader().readLine()) != null) {
        rtn = new CsvRecord(line, getCsvSplitBy());
      }
    } catch (final FileNotFoundException e) {
      e.printStackTrace();
    } catch (final IOException e) {
      e.printStackTrace();
    }
    return rtn;
  }

  /**
   * Getter for {@code br}.
   *
   * @return The br of this instance.
   */
  private BufferedReader getBufferedReader() {
    return br;
  }

  private BufferedReader br;

  /**
   * Setter for {@code csvSplitBy}.
   *
   * @param csvSplitBy The value to set {@link csvSplitBy}.
   */
  private void setCsvSplitBy(final String csvSplitBy) {
    this.csvSplitBy = csvSplitBy;
  }

  /**
   * Getter for {@code csvSplitBy}.
   *
   * @return The value of {@link csvSplitBy}.
   */
  private String getCsvSplitBy() {
    return csvSplitBy;
  }

  private String csvSplitBy;

}

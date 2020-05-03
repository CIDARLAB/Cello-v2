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

package org.cellocad.v2.results.netlist.data;

import java.io.IOException;
import java.io.Writer;
import org.cellocad.v2.common.application.data.ApplicationNetlistEdgeData;
import org.cellocad.v2.common.target.data.data.Input;
import org.json.simple.JSONObject;

/**
 * The data for an edge used within the project.
 *
 * @author Vincent Mirian
 *
 * @date 2018-05-21
 */
public class ResultNetlistEdgeData extends ApplicationNetlistEdgeData {

  private void setDefault() {
  }

  /**
   * Initializes a newly created {@link ResultNetlistEdgeData}.
   */
  public ResultNetlistEdgeData() {
    super();
    setDefault();
  }

  /**
   * Initializes a newly created {@link ResultNetlistEdgeData} with its parameters cloned from those
   * of parameter {@code other}.
   *
   * @param other The other ResultNetlistEdgeData.
   */
  public ResultNetlistEdgeData(final ResultNetlistEdgeData other) {
    super();
    setDefault();
  }

  /**
   * Initializes a newly created {@link ResultNetlistEdgeData} using the parameter {@code jsonObj}.
   *
   * @param jsonObj The JavaScript Object Notation (JSON) representation of the
   *                ResultNetlistEdgeData Object.
   */
  public ResultNetlistEdgeData(final JSONObject jsonObj) {
    super();
    setDefault();
    parse(jsonObj);
  }

  /**
   * Writes this instance in JSON format to the writer defined by parameter {@code os} with the
   * number of indents equivalent to the parameter {@code indent}.
   *
   * @param indent The number of indents.
   * @param os     The writer.
   * @throws IOException If an I/O error occurs.
   */
  @Override
  public void writeJson(final int indent, final Writer os) throws IOException {
  }

  /**
   * Parses the data attached to this instance.
   *
   * @param jsonObj The JavaScript Object Notation (JSON) representation of the Project
   *                NetlistEdgeData Object.
   */
  @Override
  public void parse(final JSONObject jsonObj) {
  }

  /**
   * Getter for {@code input}.
   *
   * @return The value of {@code input}.
   */
  public Input getInput() {
    return input;
  }

  /**
   * Setter for {@code input}.
   *
   * @param input The input to set.
   */
  public void setInput(final Input input) {
    this.input = input;
  }

  private Input input;

}

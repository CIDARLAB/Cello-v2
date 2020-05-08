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
import org.cellocad.v2.common.application.data.ApplicationNetlistData;
import org.cellocad.v2.results.placing.placement.Placement;
import org.cellocad.v2.results.placing.placement.Placements;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 * The data for a netlist used within the project.
 *
 * @author Vincent Mirian
 * @date 2018-05-21
 */
public class ResultNetlistData extends ApplicationNetlistData {

  private void setDefault() {
    setPlacements(new Placements());
  }

  /** Initializes a newly created {@link ResultNetlistData}. */
  public ResultNetlistData() {
    super();
    setDefault();
  }

  /**
   * Initializes a newly created {@link ResultNetlistData} with its parameters cloned from those of
   * parameter {@code other}.
   *
   * @param other The other ResultNetlistData.
   */
  public ResultNetlistData(final ResultNetlistData other) {
    super();
    setDefault();
  }

  /**
   * Initializes a newly created {@link ResultNetlistData} using the parameter {@code JObj}.
   *
   * @param jsonObj The JavaScript Object Notation (JSON) representation of the {@link
   *     ResultNetlistData} object.
   */
  public ResultNetlistData(final JSONObject jsonObj) {
    super();
    setDefault();
    parse(jsonObj);
  }

  /**
   * Writes this instance in JSON format to the writer defined by parameter {@code os} with the
   * number of indents equivalent to the parameter {@code indent}.
   *
   * @param indent The number of indents.
   * @param os The writer.
   * @throws IOException If an I/O error occurs.
   */
  @Override
  public void writeJson(final int indent, final Writer os) throws IOException {
    getPlacements().writeJson(indent, os);
  }

  /*
   * Parse
   */
  private void parsePlacements(final JSONObject jsonObj) {
    JSONArray jsonArr;
    jsonArr = (JSONArray) jsonObj.get("placements");
    if (jsonArr == null) {
      return;
    }
    for (int i = 0; i < jsonArr.size(); i++) {
      final JSONArray placementObj = (JSONArray) jsonArr.get(i);
      final Placement placement = new Placement(placementObj, true, false);
      getPlacements().addPlacement(placement);
    }
  }

  /**
   * Parses the data attached to this instance.
   *
   * @param jsonObj The JavaScript Object Notation (JSON) representation of the Project NetlistData
   *     Object.
   */
  @Override
  public void parse(final JSONObject jsonObj) {
    parsePlacements(jsonObj);
  }

  /*
   * Placements
   */
  /**
   * Setter for {@code placements}.
   *
   * @param placements The value to set {@code placements}.
   */
  public void setPlacements(final Placements placements) {
    this.placements = placements;
  }

  /**
   * Getter for {@code placements}.
   *
   * @return The placements of this instance.
   */
  public Placements getPlacements() {
    return placements;
  }

  private Placements placements;
}

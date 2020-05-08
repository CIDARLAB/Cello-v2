/*
 * Copyright (C) 2018 Boston University (BU)
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

package org.cellocad.v2.results.placing.placement;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import org.cellocad.v2.common.json.JsonUtils;

/**
 * A collection of {@link Placement} objects.
 *
 * @author Timothy Jones
 * @date 2018-06-05
 */
public class Placements {

  /** Initialize class members. */
  private void init() {
    placements = new ArrayList<>();
  }

  public Placements() {
    init();
  }

  public Placements(final List<Placement> placements) {
    init();
    setPlacements(placements);
  }

  /**
   * Writes this instance in JSON format to the writer defined by parameter {@code os} with the
   * number of indents equivalent to the parameter {@code indent}.
   *
   * @param indent The number of indents.
   * @param os The writer.
   * @throws IOException If an I/O error occurs.
   */
  public void writeJson(final int indent, final Writer os) throws IOException {
    String str = null;
    str = JsonUtils.getStartArrayWithMemberString("placements");
    str = JsonUtils.addIndent(indent, str);
    os.write(str);
    for (int i = 0; i < getNumPlacement(); i++) {
      getPlacementAtIdx(i).writeJson(indent + 1, os);
    }
    str = JsonUtils.getEndArrayString();
    str = JsonUtils.addIndent(indent, str);
    os.write(str);
  }

  public void addPlacement(final Placement placement) {
    getPlacements().add(placement);
  }

  public int getNumPlacement() {
    return getPlacements().size();
  }

  /**
   * Gets the placement at the given index.
   *
   * @param index A placement.
   * @return The placement at the given index.
   */
  public Placement getPlacementAtIdx(final int index) {
    Placement rtn = null;
    rtn = getPlacements().get(index);
    return rtn;
  }

  /**
   * Getter for {@code placements}.
   *
   * @return The value of {@code placements}.
   */
  protected List<Placement> getPlacements() {
    return placements;
  }

  /**
   * Setter for {@code placements}.
   *
   * @param placements The value to set {@code placements}.
   */
  protected void setPlacements(final List<Placement> placements) {
    this.placements = placements;
  }

  List<Placement> placements;
}

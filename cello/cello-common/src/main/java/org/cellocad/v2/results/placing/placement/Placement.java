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
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import org.cellocad.v2.common.CObject;
import org.cellocad.v2.common.json.JsonUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 * A placement.
 *
 * @author Timothy Jones
 * @date 2018-06-05
 */
public class Placement extends CObject {

  /** Initialize class members. */
  private void init() {
    placement = new ArrayList<>();
  }

  /** Initializes a newly created {@link Placement}. */
  public Placement(final Boolean up, final Boolean down) {
    init();
    setUp(up);
    setDown(down);
  }

  /**
   * Initializes a newly created {@link Placement} using the parameter {@code jsonObj}.
   *
   * @param jsonObj The JavaScript Object Notation (JSON) representation of the {@link NetlistNode}
   *     object.
   */
  public Placement(final JSONArray jsonObj, final Boolean up, final Boolean down) {
    this(up, down);
    parse(jsonObj);
  }

  /*
   * Parse
   */
  private void parsePlacement(final JSONArray jsonObj) {
    if (jsonObj == null) {
      throw new RuntimeException("Placement is empty!");
    }
    for (int i = 0; i < jsonObj.size(); i++) {
      final JSONObject placementObj = (JSONObject) jsonObj.get(i);
      final PlacementGroup group = new PlacementGroup(placementObj, getUp(), getDown());
      this.addPlacementGroup(group);
    }
  }

  private void parse(final JSONArray jsonObj) {
    parsePlacement(jsonObj);
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
    str = JsonUtils.getStartArrayString();
    str = JsonUtils.addIndent(indent, str);
    os.write(str);
    for (int i = 0; i < getNumPlacementGroup(); i++) {
      getPlacementGroupAtIdx(i).writeJson(indent + 1, os);
    }
    str = JsonUtils.getEndArrayString();
    str = JsonUtils.addIndent(indent, str);
    os.write(str);
  }

  public void addPlacementGroup(final PlacementGroup group) {
    placement.add(group);
  }

  public void addPlacementGroup(final Integer index, final PlacementGroup group) {
    placement.add(index, group);
  }

  /**
   * Gets the placement group at the given index.
   *
   * @param index An index.
   * @return The placement group at the given index.
   */
  public PlacementGroup getPlacementGroupAtIdx(final int index) {
    PlacementGroup rtn = null;
    if (0 <= index && index < getNumPlacementGroup()) {
      rtn = getPlacement().get(index);
    }
    return rtn;
  }

  /**
   * Returns the number of {@link Pair} objects in this instance.
   *
   * @return The number of {@link Pair} objects in this instance.
   */
  public int getNumPlacementGroup() {
    return getPlacement().size();
  }

  /**
   * Getter for {@code placement}.
   *
   * @return The value of {@code placement}.
   */
  protected List<PlacementGroup> getPlacement() {
    return placement;
  }

  /**
   * Getter for {@code position}.
   *
   * @return The value of {@code position}.
   */
  protected Integer getPosition() {
    return getIdx();
  }

  /*
   * Up
   */
  /**
   * Getter for {@code bUp}.
   *
   * @return The value of {@code bUp}.
   */
  protected Boolean getUp() {
    return bUp;
  }

  /**
   * Setter for {@code bUp}.
   *
   * @param up The value to set {@code bUp}.
   */
  protected void setUp(final Boolean up) {
    bUp = up;
  }

  /*
   * Down
   */
  /**
   * Getter for {@code bDown}.
   *
   * @return The value of {@code bDown}.
   */
  protected Boolean getDown() {
    return bDown;
  }

  /**
   * Setter for {@code bDown}.
   *
   * @param down The value to set {@code bDown}.
   */
  protected void setDown(final Boolean down) {
    bDown = down;
  }

  /*
   * Direction
   */
  /**
   * Getter for {@code direction}.
   *
   * @return The value of {@code direction}.
   */
  public Boolean getDirection() {
    return direction;
  }

  /**
   * Setter for {@code direction}.
   *
   * @param direction The value to set {@code direction}.
   */
  public void setDirection(final Boolean direction) {
    this.direction = direction;
  }

  /**
   * Getter for {@code uri}.
   *
   * @return The value of {@code uri}.
   */
  public URI getUri() {
    return uri;
  }

  /**
   * Setter for {@code uri}.
   *
   * @param uri The value to set {@code uri}.
   */
  public void setUri(final URI uri) {
    this.uri = uri;
  }

  private List<PlacementGroup> placement;
  private Boolean bUp;
  private Boolean bDown;
  private Boolean direction;
  private URI uri;
}

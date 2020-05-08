/*
 * Copyright (C) 2019 Boston University (BU)
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
import org.cellocad.v2.common.profile.ProfileUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 * A collection of components in a placement. In transcriptional circuits, a placement corresponds
 * to a set of contiguous transcriptional units that are integrated at one genomic location or on a
 * particular plasmid.
 *
 * @author Timothy Jones
 * @date 2019-05-15
 */
public class PlacementGroup extends CObject {

  /** Initialize class members. */
  private void init() {
    placementGroup = new ArrayList<>();
  }

  /**
   * Initializes a newly created {@link PlacementGroup}.
   *
   * @param up The up direction.
   * @param down The down direction.
   */
  public PlacementGroup(final Boolean up, final Boolean down) {
    init();
    setUp(up);
    setDown(down);
  }

  public PlacementGroup(final JSONObject jsonObj, final Boolean up, final Boolean down) {
    this(up, down);
    parse(jsonObj);
  }

  /*
   * Parse
   */
  private void parsePlacementGroup(final JSONObject jsonObj) {
    final String name = ProfileUtils.getString(jsonObj, "name");
    if (name == null) {
      throw new RuntimeException("'name' missing in placement group!");
    }
    setName(name);
    JSONArray jsonArr;
    jsonArr = (JSONArray) jsonObj.get("components");
    if (jsonArr == null) {
      throw new RuntimeException("'components' missing in placement group!");
    }
    for (int i = 0; i < jsonArr.size(); i++) {
      final JSONObject obj = (JSONObject) jsonArr.get(i);
      final Component component = new Component(obj, getUp(), getDown());
      getPlacementGroup().add(component);
    }
  }

  private void parse(final JSONObject jsonObj) {
    parsePlacementGroup(jsonObj);
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
    str = JsonUtils.getStartEntryString();
    str = JsonUtils.addIndent(indent, str);
    os.write(str);
    str = "";
    str += JsonUtils.getEntryToString("name", getName());
    str += JsonUtils.getStartArrayWithMemberString("components");
    str = JsonUtils.addIndent(indent + 1, str);
    os.write(str);
    for (int i = 0; i < getNumComponent(); i++) {
      getComponentAtIdx(i).writeJson(indent + 2, os);
    }
    str = JsonUtils.getEndArrayString();
    str = JsonUtils.addIndent(indent + 1, str);
    os.write(str);
    str = JsonUtils.getEndEntryString();
    str = JsonUtils.addIndent(indent, str);
    os.write(str);
  }

  /**
   * Add a component to this instance.
   *
   * @param component The component to add.
   */
  public void addComponent(final Component component) {
    getPlacementGroup().add(component);
  }

  protected List<Component> getPlacementGroup() {
    return placementGroup;
  }

  public int getNumComponent() {
    return getPlacementGroup().size();
  }

  /**
   * Gets the component at the given index.
   *
   * @param index An index.
   * @return The component at the given index.
   */
  public Component getComponentAtIdx(final int index) {
    Component rtn = null;
    rtn = getPlacementGroup().get(index);
    return rtn;
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

  private List<Component> placementGroup;
  private Boolean bUp;
  private Boolean bDown;
  private URI uri;
}

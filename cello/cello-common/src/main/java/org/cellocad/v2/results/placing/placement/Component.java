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
 * A component is an object used in the <i>placing</i> stage. It is composed of parts, and many
 * components compose a placement group. For transcriptional circuits, one component is one
 * "transcriptional unit."
 *
 * @author Timothy Jones
 * @date 2019-05-15
 */
public class Component extends CObject {

  private void init() {
    parts = new ArrayList<>();
  }

  protected Component(final Boolean up, final Boolean down) {
    init();
    setUp(up);
    setDown(down);
  }

  public Component(final List<String> parts, final Boolean up, final Boolean down) {
    this(up, down);
    this.parts = parts;
  }

  public Component(final JSONObject jsonObj, final Boolean up, final Boolean down) {
    this(up, down);
    parse(jsonObj);
  }

  /*
   * Parse
   */
  private void parseComponent(final JSONObject jsonObj) {
    final String name = ProfileUtils.getString(jsonObj, "name");
    if (name == null) {
      throw new RuntimeException("'name' missing in Netlist!");
    }
    setName(name);
    final String node = ProfileUtils.getString(jsonObj, "node");
    if (node == null) {
      throw new RuntimeException("'node' missing in Netlist!");
    }
    setNode(node);
    final Integer direction = ProfileUtils.getInteger(jsonObj, "direction");
    if (direction == null) {
      throw new RuntimeException("'direction' missing in Placement!");
    }
    if (direction < 0) {
      setDirection(getDown());
    } else {
      setDirection(getUp());
    }
    JSONArray jsonArr;
    jsonArr = (JSONArray) jsonObj.get("parts");
    if (jsonArr == null) {
      throw new RuntimeException("'parts' missing in Placement!");
    }
    for (int i = 0; i < jsonArr.size(); i++) {
      final String part = (String) jsonArr.get(i);
      getParts().add(part);
    }
  }

  private void parse(final JSONObject jsonObj) {
    parseComponent(jsonObj);
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
    str += JsonUtils.getEntryToString("node", getNode());
    Integer direction = -1;
    if (getDirection().equals(getUp())) {
      direction = 1;
    }
    str += JsonUtils.getEntryToString("direction", direction);
    str += JsonUtils.getStartArrayWithMemberString("parts");
    str = JsonUtils.addIndent(indent + 1, str);
    os.write(str);
    str = "";
    for (int i = 0; i < getNumPart(); i++) {
      str += JsonUtils.getValueToString(getPartAtIdx(i));
    }
    str = JsonUtils.addIndent(indent + 2, str);
    os.write(str);
    str = JsonUtils.getEndArrayString();
    str = JsonUtils.addIndent(indent + 1, str);
    os.write(str);
    str = JsonUtils.getEndEntryString();
    str = JsonUtils.addIndent(indent, str);
    os.write(str);
  }

  public int getNumPart() {
    return getParts().size();
  }

  /**
   * Get the part at the specified index.
   *
   * @param index The index.
   * @return The part at the specified index.
   */
  public String getPartAtIdx(final int index) {
    String rtn = null;
    rtn = getParts().get(index);
    return rtn;
  }

  /**
   * Returns the parts.
   *
   * @return The parts.
   */
  private List<String> getParts() {
    return parts;
  }

  /**
   * Returns the node name to which this component corresponds.
   *
   * @return The node name to which this component corresponds.
   */
  public String getNode() {
    return node;
  }

  /**
   * Setter for {@code node}.
   *
   * @param node The value to set {@code node}.
   */
  public void setNode(final String node) {
    this.node = node;
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

  private String node;
  private List<String> parts;
  private Boolean bUp;
  private Boolean bDown;
  private Boolean direction;
  private URI uri;
}

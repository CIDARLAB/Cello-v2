/*
 * Copyright (C) 2020 Boston University (BU)
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

package org.cellocad.v2.common.target.data.data;

import java.util.ArrayList;
import java.util.List;
import org.cellocad.v2.common.profile.ProfileUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 * A device within a structure.
 *
 * @author Timothy Jones
 * @date 2020-02-11
 */
public class StructureDevice extends StructureObject {

  private void init() {
    components = new ArrayList<>();
  }

  private void parseName(final JSONObject jsonObj) {
    final String value = ProfileUtils.getString(jsonObj, "name");
    setName(value);
  }

  private void parseMapsToVariable(final JSONObject jsonObj) {
    final String value = ProfileUtils.getString(jsonObj, "name");
    mapsToVariable = value;
  }

  private void parseComponents(final JSONObject jsonObj) {
    final JSONArray jArr = (JSONArray) jsonObj.get(StructureDevice.S_COMPONENTS);
    for (int i = 0; i < jArr.size(); i++) {
      final String str = (String) jArr.get(i);
      if (str.startsWith(StructureTemplate.S_PREFIX)) {
        final StructureTemplate t = new StructureTemplate(str);
        getComponents().add(t);
      } else {
        final StructurePart p = new StructurePart();
        p.setName(str);
        getComponents().add(p);
      }
    }
  }

  private void parseGateStructureDevice(final JSONObject jsonObj) {
    parseName(jsonObj);
    parseMapsToVariable(jsonObj);
    parseComponents(jsonObj);
  }

  public StructureDevice(final JSONObject jsonObj) {
    init();
    parseGateStructureDevice(jsonObj);
  }

  /**
   * Initializes a newly created {@link StructureDevice}.
   *
   * @param other Another device.
   */
  public StructureDevice(final StructureDevice other) {
    super(other);
    mapsToVariable = other.getMapsToVariable();
    components = other.getComponents();
  }

  /**
   * Getter for {@code mapsToVariable}.
   *
   * @return The value of {@code mapsToVariable}.
   */
  public String getMapsToVariable() {
    return mapsToVariable;
  }

  /**
   * Getter for {@code components}.
   *
   * @return The value of {@code components}.
   */
  public List<StructureObject> getComponents() {
    return components;
  }

  private String mapsToVariable;
  private List<StructureObject> components;

  private static final String S_COMPONENTS = "components";
}

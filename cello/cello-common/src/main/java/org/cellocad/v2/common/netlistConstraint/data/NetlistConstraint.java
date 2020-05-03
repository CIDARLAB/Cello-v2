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

package org.cellocad.v2.common.netlistConstraint.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.cellocad.v2.common.CObject;
import org.cellocad.v2.common.profile.ProfileUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 * The NetlistConstraint class is a class for managing and accessing the Netlist Constraint data.
 *
 * @author Vincent Mirian
 *
 * @date Nov 21, 2017
 */
public final class NetlistConstraint extends CObject {

  private void init() {
    collectionTypeData = new HashMap<>();
  }

  /**
   * Initializes a newly created {@link NetlistConstraint}.
   */
  public NetlistConstraint() {
    super();
    init();
  }

  /**
   * Initializes a newly created {@link NetlistConstraint} using the parameter {@code JArray}.
   *
   * @param jsonArr The JavaScript Object Notation (JSON) Array representation of the
   *                NetlistConstraint Object.
   */
  public NetlistConstraint(final JSONArray jsonArr) {
    super();
    init();
    parse(jsonArr);
  }

  private void parse(final JSONArray jsonArr) {
    for (int i = 0; i < jsonArr.size(); i++) {
      final JSONObject jsonObj = (JSONObject) jsonArr.get(i);
      final String collection = ProfileUtils.getString(jsonObj, "collection");
      List<JSONObject> temp = getCollectionTypeData().get(collection);
      if (temp == null) {
        temp = new ArrayList<>();
        getCollectionTypeData().put(collection, temp);
      }
      temp.add(jsonObj);
    }
  }

  /**
   * Returns a JSONObject of type {@code type} at index {@code index}.
   *
   * @param type  The type of netlist constraint data.
   * @param index The index of the {@link JSONObject} object to return.
   * @return The {@link JSONObject} if it exists, otherwise null.
   */
  public JSONObject getJsonObjectAtIdx(final String type, final int index) {
    JSONObject rtn = null;
    final List<JSONObject> temp = getCollectionTypeData().get(type);
    if (temp != null) {
      rtn = temp.get(index);
    }
    return rtn;
  }

  /**
   * Returns the number of {@link JSONObject} of type {@code type}.
   *
   * @param type The type of netlist constraint data.
   * @return The number of {@link JSONObject} of type {@code type}.
   */
  public int getNumJsonObject(final String type) {
    int rtn = 0;
    final List<JSONObject> temp = getCollectionTypeData().get(type);
    if (temp != null) {
      rtn = temp.size();
    }
    return rtn;
  }

  private Map<String, List<JSONObject>> getCollectionTypeData() {
    return collectionTypeData;
  }

  Map<String, List<JSONObject>> collectionTypeData;

}

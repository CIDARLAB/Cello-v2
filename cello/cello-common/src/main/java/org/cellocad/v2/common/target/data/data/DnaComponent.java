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

import java.net.URI;
import org.cellocad.v2.common.CObject;
import org.cellocad.v2.common.profile.ProfileUtils;
import org.json.simple.JSONObject;

/**
 * A base class for any DNA-based object: a gate, a part, etc.
 *
 * @author Timothy Jones
 *
 * @date 2020-02-11
 */
public abstract class DnaComponent extends CObject {

  private void init() {
  }

  private void parseName(final JSONObject jsonObj) {
    final String value = ProfileUtils.getString(jsonObj, DnaComponent.S_NAME);
    setName(value);
  }

  private void parseDnaComponent(final JSONObject jsonObj) {
    init();
    parseName(jsonObj);
  }

  public DnaComponent(final JSONObject jObj) {
    parseDnaComponent(jObj);
  }

  @Override
  public boolean isValid() {
    boolean rtn = super.isValid();
    rtn = rtn && getName() != null;
    return rtn;
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

  private URI uri;

  private static final String S_NAME = "name";

}

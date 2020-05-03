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

package org.cellocad.v2.common.profile;

import org.cellocad.v2.common.CObject;
import org.cellocad.v2.common.Utils;

/**
 * A profile derived from a {@link ProfileObject} object.
 *
 * @param <T> The type of the {@link ProfileObject}.
 *
 * @author Vincent Mirian
 *
 * @date Nov 9, 2017
 */
public class DerivedProfile<T extends ProfileObject> extends CObject {

  /**
   * Initializes a newly created {@link DerivedProfile} from the parameter {@code PObject} of type
   * <i>T</i>, with <i>Profile</i> set to parameter {@code PObject}.
   *
   * @param pObject The {@link ProfileObject} of type <i>T</i>.
   * @throws RuntimeException if parameter {@code PObject} is null.
   */
  public DerivedProfile(final T pObject) {
    Utils.isNullRuntimeException(pObject, "PObject");
    this.setProfile(pObject);
    setName(pObject.getName());
  }

  /**
   * Getter for {@code Profile}.
   *
   * @return The Profile of this instance.
   */
  public T getProfile() {
    return this.profile;
  }

  /**
   * Setter for {@code Profile}.
   *
   * @param profile The ProfileObject of type <i>T</i> to set <i>Profile</i>.
   */
  protected void setProfile(final T profile) {
    this.profile = profile;
  }

  private T profile;

}

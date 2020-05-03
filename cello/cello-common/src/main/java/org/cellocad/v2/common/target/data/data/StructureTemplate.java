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

/**
 * A template object in a structure.
 *
 * @author Timothy Jones
 *
 * @date 2020-02-12
 */
public class StructureTemplate extends StructureObject {

  /**
   * Initializes a newly created {@link StructureTemplate}.
   *
   * @param template The template string.
   */
  public StructureTemplate(final String template) {
    if (!template.startsWith(StructureTemplate.S_PREFIX)) {
      throw new RuntimeException("Not a template.");
    }
    final String str = template.substring(1);
    setName(str);
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
   * @param input The value to set {@code input}.
   */
  public void setInput(final Input input) {
    this.input = input;
  }

  private Input input;

  public static final String S_PREFIX = "#";

}

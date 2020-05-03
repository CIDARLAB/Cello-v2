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

package org.cellocad.v2.common.application.data;

import java.io.IOException;
import java.io.Writer;
import org.cellocad.v2.common.CObject;
import org.json.simple.JSONObject;

/**
 * The ApplicationNetlistData class is the base class for all NetlistData classes using the Poros
 * framework.
 *
 * @author Vincent Mirian
 *
 * @date Dec 15, 2017
 */
public abstract class ApplicationNetlistData extends CObject {

  /**
   * Writes this instance in JSON format to the writer defined by parameter {@code os} with the
   * number of indents equivalent to the parameter {@code indent}.
   *
   * @param indent The number of indents.
   * @param os     The writer.
   * @throws IOException If an I/O error occurs.
   */
  public abstract void writeJson(int indent, Writer os) throws IOException;

  /**
   * Parses the data attached to this instance.
   *
   * @param jsonObj The JavaScript Object Notation (JSON) representation of the
   *                {@link ApplicationNetlistData} Object.
   */
  public abstract void parse(final JSONObject jsonObj);

}

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

package org.cellocad.v2.common.target.data.data;

import org.json.simple.JSONObject;

/**
 * A device that can be assigned to a node in a netlist, e.g. a gate, an input sensor, or an output
 * device.
 *
 * @author Timothy Jones
 *
 * @date 2018-06-29
 */
public abstract class AssignableDevice extends DnaComponent {

  public AssignableDevice(final JSONObject jObj) {
    super(jObj);
  }

  @Override
  public boolean isValid() {
    boolean rtn = super.isValid();
    rtn = rtn && getModel() != null;
    rtn = rtn && getStructure() != null;
    return rtn;
  }

  /**
   * Getter for {@code model}.
   *
   * @return The value of {@code model}.
   */
  public Model getModel() {
    return model;
  }

  /**
   * Setter for {@code model}.
   *
   * @param model The model to set.
   */
  public void setModel(final Model model) {
    this.model = model;
  }

  private Model model;

  /**
   * Getter for {@code structure}.
   *
   * @return The value of {@code structure}.
   */
  public Structure getStructure() {
    return structure;
  }

  /**
   * Setter for {@code structure}.
   *
   * @param structure The structure to set.
   */
  public void setStructure(final Structure structure) {
    this.structure = structure;
  }

  private Structure structure;

  public static final String S_MODEL = "model";
  public static final String S_STRUCTURE = "structure";

}

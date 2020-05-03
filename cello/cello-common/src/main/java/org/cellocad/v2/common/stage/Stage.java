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

package org.cellocad.v2.common.stage;

import org.cellocad.v2.common.profile.ProfileObject;
import org.cellocad.v2.common.profile.ProfileUtils;
import org.json.simple.JSONObject;

/**
 * The Stage class is a class containing the configuration for a stage.
 *
 * @author Vincent Mirian
 *
 * @date Nov 20, 2017
 */
public final class Stage extends ProfileObject {

  private void init() {
  }

  /**
   * Initializes a newly created {@link Stage} with <i>algorithmName</i> set to null.
   */
  public Stage() {
    super();
    init();
  }

  /**
   * Initializes a newly created {@link Stage} using the parameter {@code JObj}.
   *
   * @param jsonObj The JavaScript Object Notation (JSON) representation of the Stage Object.
   */
  public Stage(final JSONObject jsonObj) {
    super(jsonObj);
    init();
    parse(jsonObj);
  }

  /*
   * Parse
   */
  private void parseStageConfiguration(final JSONObject jsonObj) {
    // parse StageConfiguration
    // algoName
    final String algoName = ProfileUtils.getString(jsonObj, "algorithm_name");
    if (algoName == null) {
      throw new RuntimeException(
          "'algorithm_name' missing for stage configuration " + getName() + ".");
    }
    setAlgorithmName(algoName);
  }

  private void parse(final JSONObject jsonObj) {
    parseStageConfiguration(jsonObj);
  }

  /*
   * Getter and Setter
   */
  /**
   * Getter for {@code algorithmName}.
   *
   * @return The name of the algorithm for this instance.
   */
  public String getAlgorithmName() {
    return algorithmName;
  }

  /**
   * Setter for {@code algorithmName}.
   *
   * @param algorithmName The name of the algorithm for this instance.
   */
  public void setAlgorithmName(final String algorithmName) {
    this.algorithmName = algorithmName;
  }

  String algorithmName;

}

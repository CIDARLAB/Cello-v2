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

package org.cellocad.v2.common.application;

import org.cellocad.v2.common.CObjectCollection;
import org.cellocad.v2.common.profile.ProfileObject;
import org.cellocad.v2.common.stage.Stage;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 * The ApplicationConfiguration class is a class containing the configuration for an application.
 *
 * @author Vincent Mirian
 * @date Nov 20, 2017
 */
public final class ApplicationConfiguration extends ProfileObject {

  private void init() {
    stages = new CObjectCollection<>();
  }

  /**
   * Initializes a newly created {@link ApplicationConfiguration} using the parameter {@code
   * jsonObj}.
   *
   * @param jsonObj The JavaScript Object Notation (JSON) representation of the {@link
   *     ApplicationConfiguration} object.
   */
  public ApplicationConfiguration(final JSONObject jsonObj) {
    super(jsonObj);
    init();
    parse(jsonObj);
  }

  /*
   * Parse
   */
  private void parseStages(final JSONObject jsonObj) {
    JSONArray jsonArr;
    // parse PartitionProfile
    jsonArr = (JSONArray) jsonObj.get("stages");
    if (jsonArr == null) {
      throw new RuntimeException("'stages' missing in ApplicationInfo!");
    }
    for (int i = 0; i < jsonArr.size(); i++) {
      final JSONObject stageObj = (JSONObject) jsonArr.get(i);
      final Stage S = new Stage(stageObj);
      addStage(S);
    }
  }

  private void parse(final JSONObject jsonObj) {
    parseStages(jsonObj);
  }

  private void addStage(final Stage stage) {
    if (stage != null) {
      getStages().add(stage);
    }
  }

  /**
   * Returns the Stage object with its name equivalent to parameter {@code name}.
   *
   * @param name name of the stage to return.
   * @return Stage instance if the Stage exists, otherwise null.
   */
  public Stage getStageByName(final String name) {
    final Stage rtn = getStages().findCObjectByName(name);
    return rtn;
  }

  /**
   * Returns the Stage at the specified position in this instance.
   *
   * @param index The index of the {@link Stage} object to return.
   * @return If the index is within the bounds (0 <= bounds < this.getNumStage()), returns the Stage
   *     at the specified position in this instance, otherwise null.
   */
  public Stage getStageAtIdx(final int index) {
    Stage rtn = null;
    if (index >= 0 && index < getNumStage()) {
      rtn = getStages().get(index);
    }
    return rtn;
  }

  /**
   * Returns the number of {@link Stage} objects in this instance.
   *
   * @return The number of {@link Stage} objects in this instance.
   */
  public int getNumStage() {
    final int rtn = getStages().size();
    return rtn;
  }

  private CObjectCollection<Stage> getStages() {
    return stages;
  }

  CObjectCollection<Stage> stages;
}

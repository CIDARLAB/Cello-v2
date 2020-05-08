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

import org.cellocad.v2.common.Utils;
import org.cellocad.v2.common.options.Options;
import org.cellocad.v2.common.options.OptionsUtils;
import org.cellocad.v2.common.runtime.environment.RuntimeEnv;
import org.cellocad.v2.common.stage.Stage;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * The ApplicationUtils class is class with utility methods for <i>ApplicationConfiguration</i>
 * instances.
 *
 * @author Vincent Mirian
 * @date Nov 20, 2017
 */
public final class ApplicationUtils {

  /**
   * Overrides the ApplicationConfiguration instance,<i>ac</i>, with the Options
   * instance,<i>options</i>.
   *
   * @param ac ApplicationConfiguration instance.
   * @param options Options instance.
   * @throws RuntimeException if parameter {@code ac} is null.
   */
  public static void overrideWithOptions(final ApplicationConfiguration ac, final Options options) {
    Utils.isNullRuntimeException(ac, "ac");
    if (options != null) {
      for (int i = 0; i < ac.getNumStage(); i++) {
        final Stage stage = ac.getStageAtIdx(i);
        final String stageName = options.getStageName(stage.getName());
        if (stageName != null) {
          stage.setAlgorithmName(stageName);
        }
      }
    }
  }

  /**
   * Initializes a newly created {@link ApplicationConfiguration} using the RuntimeEnv,
   * <i>runEnv</i>, the string referencing command line argument for the Options file,
   * <i>options</i>, and, the path to the application configuration file, <i>projectFilename</i>.
   *
   * @param runEnv The RuntimeEnv to extract the Options file, <i>options</i>.
   * @param options The string referencing command line argument for the Options file.
   * @return The ApplicationConfiguration if created successfully, otherwise null.
   * @throws RuntimeException if: <br>
   *     Any of the parameters are null<br>
   *     Error accessing <i>projectFilename</i><br>
   *     Error parsing <i>projectFilename</i><br>
   *     .
   */
  public static ApplicationConfiguration getApplicationConfiguration(
      final RuntimeEnv runEnv, final String options, final String project) {
    Utils.isNullRuntimeException(runEnv, "runEnv");
    Utils.isNullRuntimeException(options, "options");
    Utils.isNullRuntimeException(project, "projectFilename");
    ApplicationConfiguration rtn = null;
    JSONObject jsonTop = null;
    // Create JSON object from File Reader
    final JSONParser parser = new JSONParser();
    try {
      jsonTop = (JSONObject) parser.parse(project);
    } catch (final ParseException e) {
      throw new RuntimeException("Parser Exception.");
    }
    // Create ApplicationInfo object
    rtn = new ApplicationConfiguration(jsonTop);
    // override with options
    final Options optionsObj = OptionsUtils.getOptions(runEnv, options);
    ApplicationUtils.overrideWithOptions(rtn, optionsObj);
    return rtn;
  }
}

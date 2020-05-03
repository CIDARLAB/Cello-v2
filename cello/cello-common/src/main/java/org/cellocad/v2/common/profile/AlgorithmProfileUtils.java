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

import java.io.IOException;
import org.cellocad.v2.common.Pair;
import org.cellocad.v2.common.Utils;
import org.cellocad.v2.common.options.Options;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * The AlgorithmProfileUtils class is class with utility methods for <i>AlgorithmProfile</i>
 * instances.
 *
 * @author Vincent Mirian
 *
 * @date Dec 9, 2017
 */
public final class AlgorithmProfileUtils {

  /**
   * Overrides the AlgorithmProfile instance, <i>algProfile</i>, with the Options instance,
   * <i>options</i>.
   *
   * @param algProfile An {@link AlgorithmProfile} instance.
   * @param options    An {@link Options} instance.
   * @throws RuntimeException if parameter {@code algProfile} is null.
   */
  public static void overrideWithOptions(final AlgorithmProfile algProfile, final Options options) {
    Utils.isNullRuntimeException(algProfile, "algProfile");
    final String stageName = algProfile.getStageName();
    if (options != null) {
      for (int i = 0; i < options.getNumStageArgValue(stageName); i++) {
        final Pair<String, String> argValue = options.getStageArgValueAtIdx(stageName, i);
        final String arg = argValue.getFirst();
        final String value = argValue.getSecond();
        Boolean obj = null;
        // Boolean
        obj = algProfile.getBooleanParameter(arg).getFirst();
        if (obj.booleanValue()) {
          final Boolean tObj = Utils.getBoolean(value);
          if (tObj != null) {
            algProfile.setBooleanParameter(arg, tObj);
          }
        }
        // Byte
        obj = algProfile.getByteParameter(arg).getFirst();
        if (obj.booleanValue()) {
          final Byte tObj = Utils.getByte(value);
          if (tObj != null) {
            algProfile.setByteParameter(arg, tObj);
          }
        }
        // Character
        obj = algProfile.getCharParameter(arg).getFirst();
        if (obj.booleanValue()) {
          final Character tObj = Utils.getCharacter(value);
          if (tObj != null) {
            algProfile.setCharacterParameter(arg, tObj);
          }
        }
        // Short
        obj = algProfile.getShortParameter(arg).getFirst();
        if (obj.booleanValue()) {
          final Short tObj = Utils.getShort(value);
          if (tObj != null) {
            algProfile.setShortParameter(arg, tObj);
          }
        }
        // Integer
        obj = algProfile.getIntParameter(arg).getFirst();
        if (obj.booleanValue()) {
          final Integer tObj = Utils.getInteger(value);
          if (tObj != null) {
            algProfile.setIntegerParameter(arg, tObj);
          }
        }
        // Long
        obj = algProfile.getLongParameter(arg).getFirst();
        if (obj.booleanValue()) {
          final Long tObj = Utils.getLong(value);
          if (tObj != null) {
            algProfile.setLongParameter(arg, tObj);
          }
        }
        // Float
        obj = algProfile.getFloatParameter(arg).getFirst();
        if (obj.booleanValue()) {
          final Float tObj = Utils.getFloat(value);
          if (tObj != null) {
            algProfile.setFloatParameter(arg, tObj);
          }
        }
        // Double
        obj = algProfile.getDoubleParameter(arg).getFirst();
        if (obj.booleanValue()) {
          final Double tObj = Utils.getDouble(value);
          if (tObj != null) {
            algProfile.setDoubleParameter(arg, tObj);
          }
        }
        // String
        obj = algProfile.getStringParameter(arg).getFirst();
        if (obj.booleanValue()) {
          final String tObj = Utils.getString(value);
          if (tObj != null) {
            algProfile.setStringParameter(arg, tObj);
          }
        }
      }
    }
  }

  /**
   * Initializes a newly created {@link AlgorithmProfile} using the path to the algorithm
   * configuration file, {@code filename}.
   *
   * @param filename The path to the algorithm configuration file.
   * @return The AlgorithmProfile if created successfully, otherwise null.
   * @throws RuntimeException if: <br>
   *                          Error accessing {@code filename}<br>
   *                          Error parsing {@code filename}<br>
   *                          .
   */
  public static AlgorithmProfile getAlgorithmProfile(final String filename) {
    AlgorithmProfile rtn = null;
    JSONObject jsonTop = null;
    String profile = null;
    try {
      profile = Utils.getResourceAsString(filename);
    } catch (final IOException e) {
      throw new RuntimeException("File IO Exception for: " + filename + ".");
    }
    // Create JSON object from File Reader
    final JSONParser parser = new JSONParser();
    try {
      jsonTop = (JSONObject) parser.parse(profile);
    } catch (final ParseException e) {
      throw new RuntimeException("Parser Exception for: " + filename + ".");
    }
    // Create TargetInfo object
    rtn = new AlgorithmProfile(jsonTop);
    return rtn;
  }

}

/*
 * Copyright (C) 2017-2020 Massachusetts Institute of Technology (MIT), Boston University (BU)
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

import java.awt.Color;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.cellocad.v2.common.profile.ProfileUtils;
import org.json.simple.JSONObject;

/**
 * The Gate is class representing a gate for the gate assignment in the <i>SimulatedAnnealing</i>
 * algorithm.
 *
 * @author Vincent Mirian
 * @author Timothy Jones
 * @date 2018-05-21
 */
public class Gate extends AssignableDevice {

  private void init() {}

  private void parseRegulator(final JSONObject jsonObj) {
    final String value = ProfileUtils.getString(jsonObj, Gate.S_REGULATOR);
    setRegulator(value);
  }

  private void parseGroup(final JSONObject jsonObj) {
    final String value = ProfileUtils.getString(jsonObj, Gate.S_GROUP);
    setGroup(value);
  }

  private void parseGateType(final JSONObject jsonObj) {
    final String value = ProfileUtils.getString(jsonObj, Gate.S_GATETYPE);
    setGateType(value);
  }

  private void parseSystem(final JSONObject jsonObj) {
    final String value = ProfileUtils.getString(jsonObj, Gate.S_SYSTEM);
    setSystem(value);
  }

  private void parseColor(final JSONObject jsonObj) {
    final String value = ProfileUtils.getString(jsonObj, Gate.S_COLOR);
    final Pattern pattern = Pattern.compile("[0-9A-Fa-f]{6}");
    final Matcher matcher = pattern.matcher(value);
    if (matcher.matches()) {
      final Color color = Color.decode("0x" + value);
      setColor(color);
    }
  }

  private void parseGate(final JSONObject jObj) {
    parseRegulator(jObj);
    parseGroup(jObj);
    parseGateType(jObj);
    parseSystem(jObj);
    parseColor(jObj);
  }

  /**
   * Initializes a newly created {@link Gate} with the given JSON data.
   *
   * @param jsonObj The JSON data associated with the gate.
   */
  public Gate(final JSONObject jsonObj) {
    super(jsonObj);
    init();
    parseGate(jsonObj);
  }

  @Override
  public boolean isValid() {
    boolean rtn = super.isValid();
    rtn = rtn && getRegulator() != null;
    rtn = rtn && getGroup() != null;
    rtn = rtn && getGateType() != null;
    rtn = rtn && getSystem() != null;
    rtn = rtn && getColor() != null;
    return rtn;
  }

  /*
   * Regulator
   */
  private void setRegulator(final String regulator) {
    this.regulator = regulator;
  }

  public String getRegulator() {
    return regulator;
  }

  private String regulator;

  /*
   * Group
   */
  private void setGroup(final String group) {
    this.group = group;
  }

  public String getGroup() {
    return group;
  }

  private String group;

  /*
   * GateType
   */
  private void setGateType(final String gateType) {
    this.gateType = gateType;
  }

  public String getGateType() {
    return gateType;
  }

  private String gateType;

  /*
   * System
   */
  private void setSystem(final String system) {
    this.system = system;
  }

  public String getSystem() {
    return system;
  }

  private String system;

  /*
   * Color
   */
  private void setColor(final Color color) {
    this.color = color;
  }

  public Color getColor() {
    return color;
  }

  private Color color;

  public static final String S_REGULATOR = "regulator";
  public static final String S_GROUP = "group";
  public static final String S_GATETYPE = "gate_type";
  public static final String S_SYSTEM = "system";
  public static final String S_COLOR = "color";
  public static final String S_MODEL = "model";
  public static final String S_STRUCTURE = "structure";
}

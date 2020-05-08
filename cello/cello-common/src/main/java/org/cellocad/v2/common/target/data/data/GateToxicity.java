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

import java.util.ArrayList;
import java.util.List;
import org.cellocad.v2.common.CObject;
import org.cellocad.v2.common.Pair;
import org.cellocad.v2.common.profile.ProfileUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 * The toxicity data associated with a gate.
 *
 * @author Vincent Mirian
 * @author Timothy Jones
 * @date 2018-05-21
 */
public class GateToxicity extends CObject {

  private void parseName(final JSONObject jsonObj) {
    final String value = ProfileUtils.getString(jsonObj, GateToxicity.S_GATENAME);
    setGateName(value);
  }

  private void parseMapVariable(final JSONObject jsonObj) {
    final String value = ProfileUtils.getString(jsonObj, GateToxicity.S_MAPSTOVARIABLE);
    setMapVariable(value);
  }

  private void parseInput(final JSONObject jsonObj) {
    final JSONArray jArr = (JSONArray) jsonObj.get(GateToxicity.S_INPUT);
    for (int i = 0; i < jArr.size(); i++) {
      final Double input = ((Number) jArr.get(i)).doubleValue();
      getInput().add(input);
    }
  }

  private void parseGrowth(final JSONObject jsonObj) {
    final JSONArray jArr = (JSONArray) jsonObj.get(GateToxicity.S_GROWTH);
    for (int i = 0; i < jArr.size(); i++) {
      final Double growth = ((Number) jArr.get(i)).doubleValue();
      getGrowth().add(growth);
    }
  }

  private void parseInputGrowthPairs() {
    if (getNumInput() != getNumGrowth()) {
      throw new RuntimeException("Error with GateToxicity");
    }
    for (int i = 0; i < getNumInput(); i++) {
      final Double input = getInputAtIdx(i);
      final Double growth = getGrowthAtIdx(i);
      final Pair<Double, Double> pair = new Pair<>(input, growth);
      getInputGrowthPairs().add(pair);
    }
  }

  private void init() {
    input = new ArrayList<>();
    growth = new ArrayList<>();
    inputGrowthPairs = new ArrayList<>();
  }

  private void parseToxicity(final JSONObject jObj) {
    init();
    parseName(jObj);
    parseMapVariable(jObj);
    parseInput(jObj);
    parseGrowth(jObj);
    parseInputGrowthPairs();
  }

  public GateToxicity(final JSONObject jObj) {
    parseToxicity(jObj);
  }

  /*
   * GateName
   */
  private void setGateName(final String gateName) {
    setName(gateName);
  }

  public String getGateName() {
    return getName();
  }

  /*
   * Gate
   */
  public void setGate(final Gate gate) {
    this.gate = gate;
  }

  public Gate getGate() {
    return gate;
  }

  private Gate gate;

  /*
   * MapVariable
   */
  private void setMapVariable(final String mapVariable) {
    this.mapVariable = mapVariable;
  }

  public String getMapVariable() {
    return mapVariable;
  }

  private String mapVariable;

  /*
   * Input
   */
  private List<Double> getInput() {
    return input;
  }

  /**
   * Get the input at the given index.
   *
   * @param index An index.
   * @return The input at the given index.
   */
  public Double getInputAtIdx(final int index) {
    Double rtn = null;
    if (0 <= index && index < getNumInput()) {
      rtn = getInput().get(index);
    }
    return rtn;
  }

  public int getNumInput() {
    return getInput().size();
  }

  private List<Double> input;

  /*
   * Growth
   */
  private List<Double> getGrowth() {
    return growth;
  }

  /**
   * Get the growth at the given index.
   *
   * @param index An index.
   * @return The growth at the given index.
   */
  public Double getGrowthAtIdx(final int index) {
    Double rtn = null;
    if (0 <= index && index < getNumGrowth()) {
      rtn = getGrowth().get(index);
    }
    return rtn;
  }

  public int getNumGrowth() {
    return getGrowth().size();
  }

  private List<Double> growth;

  /*
   * Growth
   */
  private List<Pair<Double, Double>> getInputGrowthPairs() {
    return inputGrowthPairs;
  }

  /**
   * Get the input growth pair at the given index.
   *
   * @param index An index.
   * @return The input growth pair at the given index.
   */
  public Pair<Double, Double> getInputGrowthPairAtIdx(final int index) {
    Pair<Double, Double> rtn = null;
    if (0 <= index && index < getNumInputGrowthPairs()) {
      rtn = getInputGrowthPairs().get(index);
    }
    return rtn;
  }

  public int getNumInputGrowthPairs() {
    return getInputGrowthPairs().size();
  }

  private List<Pair<Double, Double>> inputGrowthPairs;

  private static final String S_GATENAME = "gate_name";
  private static final String S_MAPSTOVARIABLE = "maps_to_variable";
  private static final String S_INPUT = "input";
  private static final String S_GROWTH = "growth";
}

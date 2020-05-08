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

package org.cellocad.v2.common.target.data.data;

import java.util.ArrayList;
import java.util.List;
import org.cellocad.v2.common.CObject;
import org.cellocad.v2.common.profile.ProfileUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 * The CytometryData is class representing the cytometry data for a gate in the gate assignment of
 * the <i>SimulatedAnnealing</i> algorithm.
 *
 * @author Vincent Mirian
 * @date 2018-05-21
 */
public class CytometryData extends CObject {

  private void parseMapVariable(final JSONObject jsonObj) {
    final String value = ProfileUtils.getString(jsonObj, CytometryData.S_MAPSTOVARIABLE);
    setName(value);
    setMapVariable(value);
  }

  private void parseInput(final JSONObject jsonObj) {
    final Double value = ProfileUtils.getDouble(jsonObj, CytometryData.S_INPUT);
    setInput(value);
  }

  private void parseOutputBins(final JSONObject jsonObj) {
    final JSONArray JArr = (JSONArray) jsonObj.get(CytometryData.S_OUTPUTBINS);
    for (int i = 0; i < JArr.size(); i++) {
      getOutputBins().add(((Number) JArr.get(i)).doubleValue());
    }
  }

  private void parseOutputCounts(final JSONObject jsnObj) {
    final JSONArray JArr = (JSONArray) jsnObj.get(CytometryData.S_OUTPUTCOUNTS);
    for (int i = 0; i < JArr.size(); i++) {
      getOutputBins().add(((Number) JArr.get(i)).doubleValue());
    }
  }

  private void parseCytometryData(final JSONObject jObj) {
    parseMapVariable(jObj);
    parseInput(jObj);
    parseOutputBins(jObj);
    parseOutputCounts(jObj);
  }

  private void init() {
    outputBins = new ArrayList<>();
    outputCounts = new ArrayList<>();
  }

  public CytometryData(final JSONObject jobj) {
    init();
    parseCytometryData(jobj);
  }

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
   * input
   */
  private void setInput(final Double input) {
    this.input = input;
  }

  public Double getInput() {
    return input;
  }

  private Double input;

  /*
   * OutputBins
   */
  private List<Double> getOutputBins() {
    return outputBins;
  }

  /**
   * Get the output bins at the given index.
   *
   * @param index An index.
   * @return The output bins at the given index.
   */
  public Double getOutputBinsAtIdx(final int index) {
    Double rtn = null;
    if (0 <= index && index < getNumOutputBins()) {
      rtn = getOutputBins().get(index);
    }
    return rtn;
  }

  public int getNumOutputBins() {
    return getOutputBins().size();
  }

  private List<Double> outputBins;

  /*
   * OutputCounts
   */
  private List<Double> getOutputCounts() {
    return outputCounts;
  }

  /**
   * Get the output counts at the given index.
   *
   * @param index An index.
   * @return The output counts at the given index.
   */
  public Double getOutputCountsAtIdx(final int index) {
    Double rtn = null;
    if (0 <= index && index < getNumOutputCounts()) {
      rtn = getOutputCounts().get(index);
    }
    return rtn;
  }

  public int getNumOutputCounts() {
    return getOutputCounts().size();
  }

  private List<Double> outputCounts;

  private static final String S_MAPSTOVARIABLE = "maps_to_variable";
  private static final String S_INPUT = "input";
  private static final String S_OUTPUTBINS = "output_bins";
  private static final String S_OUTPUTCOUNTS = "output_counts";
}

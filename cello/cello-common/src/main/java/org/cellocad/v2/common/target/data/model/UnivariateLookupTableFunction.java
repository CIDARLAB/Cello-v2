/**
 * Copyright (C) 2020 Boston University (BU)
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:

 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.

 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package org.cellocad.v2.common.target.data.model;

import org.apache.commons.math3.ml.distance.EuclideanDistance;
import org.cellocad.v2.common.CelloException;
import org.cellocad.v2.common.profile.ProfileUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.google.common.collect.ImmutableTable;
import com.google.common.collect.ImmutableTable.Builder;
import com.google.common.collect.Table;

/**
 *
 *
 * @author Timothy Jones
 *
 * @date 2020-02-21
 *
 */
public class UnivariateLookupTableFunction extends LookupTableFunction {

	private void init() {
	}

	private void parseTable(JSONObject JObj) throws CelloException {
		Builder<Integer, String, Double> builder = ImmutableTable.builder();
		JSONObject jObj = (JSONObject) JObj.get(LookupTableFunction.S_TABLE);
		if (jObj == null) {
			String fmt = "Invalid %s specification: %s.";
			throw new CelloException(String.format(fmt, UnivariateLookupTableFunction.class.getName(), JObj.toString()));
		}
		for (Object key : jObj.keySet()) {
			String name = ProfileUtils.getString(key);
			if (this.getVariables().findCObjectByName(name) == null && !name.equals(LookupTableFunction.S_OUTPUT)) {
				String fmt = "Invalid table key: %s.";
				throw new CelloException(String.format(fmt, name));
			}
			JSONArray jArr = (JSONArray) jObj.get(key);
			for (int i = 0; i < jArr.size(); i++) {
				Double value = (Double) jArr.get(i);
				builder.put(i, name, value);
			}
		}
		this.table = builder.build();
	}

	private void parseLookupTableFunction(JSONObject JObj) throws CelloException {
		this.parseTable(JObj);
	}

	public UnivariateLookupTableFunction(JSONObject jObj) throws CelloException {
		super(jObj);
		this.init();
		this.parseLookupTableFunction(jObj);
	}

	@Override
	public boolean isValid() {
		boolean rtn = super.isValid();
		rtn = rtn && (this.getTable() != null);
		return rtn;
	}

	@Override
	public Number evaluate(EvaluationContext ec) throws CelloException {
		Double rtn = null;
		double input[] = new double[this.getVariables().size()];
		for (int i = 0; i < this.getVariables().size(); i++) {
			Variable v = this.getVariables().get(i);
			String name = v.getName();
			Double value = v.evaluate(ec).doubleValue();
			if (!this.getTable().containsColumn(name))
				throw new RuntimeException(String.format("Missing column %s.", name));
			input[i] = value;
		}
		// stupid two-point average interpolation
		Integer argMinA = -1;
		Double minA = Double.MAX_VALUE;
		Integer argMinB = -1;
		Double minB = Double.MAX_VALUE;
		for (Integer row : this.getTable().rowKeySet()) {
			double record[] = new double[this.getVariables().size()];
			for (int i = 0; i < this.getVariables().size(); i++) {
				Variable v = this.getVariables().get(i);
				Double d = this.getTable().get(row, v.getName());
				record[i] = d;
			}
			EuclideanDistance eu = new EuclideanDistance();
			Double distance = eu.compute(input, record);
			if (distance < minA) {
				argMinB = argMinA;
				minB = minA;
				argMinA = row;
				minA = distance;
			} else if (distance < minB) {
				argMinB = row;
				minB = distance;
			}
		}
		Double valueA = this.getTable().get(argMinA, LookupTableFunction.S_OUTPUT);
		Double valueB = this.getTable().get(argMinB, LookupTableFunction.S_OUTPUT);
		rtn = (valueA * minA + valueB * minB) / (minA + minB);
		return rtn;
	}

	private Table<Integer, String, Double> getTable() {
		return table;
	}

	private Table<Integer, String, Double> table;

}

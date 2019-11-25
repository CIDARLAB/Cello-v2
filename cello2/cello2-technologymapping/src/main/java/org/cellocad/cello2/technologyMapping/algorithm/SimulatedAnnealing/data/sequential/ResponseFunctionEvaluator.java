/**
 * Copyright (C) 2019 Boston University (BU)
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
package org.cellocad.cello2.technologyMapping.algorithm.SimulatedAnnealing.data.sequential;

import org.apache.commons.math3.analysis.UnivariateFunction;
import org.cellocad.cello2.technologyMapping.algorithm.SimulatedAnnealing.data.MathEval;
import org.cellocad.cello2.technologyMapping.algorithm.SimulatedAnnealing.data.ucf.ResponseFunction;
import org.cellocad.cello2.technologyMapping.algorithm.SimulatedAnnealing.data.ucf.ResponseFunctionParameter;

/**
 * 
 *
 * @author Timothy Jones
 *
 * @date 2019-11-19
 *
 */
public class ResponseFunctionEvaluator implements UnivariateFunction {

	public ResponseFunctionEvaluator(ResponseFunction rf) {
		this.rf = rf;
	}

	@Override
	public double value(double x) {
		double rtn = 0.0;
		String equation = this.getResponseFunction().getEquation();
		MathEval eval = new MathEval();
		for (int i = 0; i < this.getResponseFunction().getNumParameter(); i++) {
			ResponseFunctionParameter param = this.getResponseFunction().getParameterAtIdx(i);
			eval.setConstant(param.getName(), param.getValue());
		}
		eval.setVariable(this.getResponseFunction().getVariableByName("x").getName(), x);
		rtn = eval.evaluate(equation);
		return rtn;
	}

	/**
	 * Getter for <i>rf</i>
	 * 
	 * @return the rf of this instance
	 */
	private ResponseFunction getResponseFunction() {
		return rf;
	}

	private ResponseFunction rf;

}

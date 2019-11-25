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
import org.apache.commons.math3.util.FastMath;

/**
 * 
 *
 * @author Timothy Jones
 *
 * @date 2019-11-23
 *
 */
public class UnivariateFunctionWithOffset implements UnivariateFunction {

	public UnivariateFunctionWithOffset(UnivariateFunction f, Double offset) {
		this.f = f;
		this.offset = offset;
	}

	@Override
	public double value(double x) {
		return FastMath.abs(f.value(x) + this.getOffset());
	}

	/**
	 * Getter for <i>f</i>
	 * 
	 * @return the f of this instance
	 */
	public UnivariateFunction getUnivariateFunction() {
		return f;
	}

	/**
	 * Getter for <i>offset</i>
	 * 
	 * @return the offset of this instance
	 */
	public Double getOffset() {
		return offset;
	}

	private UnivariateFunction f;
	private Double offset;

}

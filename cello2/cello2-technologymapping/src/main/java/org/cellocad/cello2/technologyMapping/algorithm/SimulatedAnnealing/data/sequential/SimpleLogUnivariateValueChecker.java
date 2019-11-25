/* This file is a modified version of the file:
 * org.apache.commons.math3.optim.univariate.SimpleUnivariateValueChecker.java
 * from the Apache commons-math package, v3.6.1
 * The original license is below. The subsequent license refers to the modifications.
 */
/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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

import org.apache.commons.math3.exception.NotStrictlyPositiveException;
import org.apache.commons.math3.optim.AbstractConvergenceChecker;
import org.apache.commons.math3.optim.univariate.UnivariatePointValuePair;
import org.apache.commons.math3.util.FastMath;

/**
 * Simple implementation of the
 * {@link org.apache.commons.math4.optim.ConvergenceChecker} interface that uses
 * only objective function values.
 *
 * Convergence is considered to have been reached if either the relative
 * difference between the objective function values is smaller than a threshold
 * or if either the absolute difference between the objective function values is
 * smaller than another threshold. <br>
 * The {@link #converged(int,UnivariatePointValuePair,UnivariatePointValuePair)
 * converged} method will also return {@code true} if the number of iterations
 * has been set (see {@link #SimpleUnivariateValueChecker(double,double,int)
 * this constructor}).
 */
public class SimpleLogUnivariateValueChecker extends AbstractConvergenceChecker<UnivariatePointValuePair> {
	/**
	 * If {@link #maxIterationCount} is set to this value, the number of iterations
	 * will never cause
	 * {@link #converged(int,UnivariatePointValuePair,UnivariatePointValuePair)} to
	 * return {@code true}.
	 */
	private static final int ITERATION_CHECK_DISABLED = -1;
	/**
	 * Number of iterations after which the
	 * {@link #converged(int,UnivariatePointValuePair,UnivariatePointValuePair)}
	 * method will return true (unless the check is disabled).
	 */
	private final int maxIterationCount;

	/**
	 * Build an instance with specified thresholds.
	 *
	 * In order to perform only relative checks, the absolute tolerance must be set
	 * to a negative value. In order to perform only absolute checks, the relative
	 * tolerance must be set to a negative value.
	 *
	 * @param relativeThreshold relative tolerance threshold
	 * @param absoluteThreshold absolute tolerance threshold
	 */
	public SimpleLogUnivariateValueChecker(final double relativeThreshold, final double absoluteThreshold) {
		super(relativeThreshold, absoluteThreshold);
		maxIterationCount = ITERATION_CHECK_DISABLED;
	}

	/**
	 * Builds an instance with specified thresholds.
	 *
	 * In order to perform only relative checks, the absolute tolerance must be set
	 * to a negative value. In order to perform only absolute checks, the relative
	 * tolerance must be set to a negative value.
	 *
	 * @param relativeThreshold relative tolerance threshold
	 * @param absoluteThreshold absolute tolerance threshold
	 * @param maxIter           Maximum iteration count.
	 * @throws NotStrictlyPositiveException if {@code maxIter <= 0}.
	 */
	public SimpleLogUnivariateValueChecker(final double relativeThreshold, final double absoluteThreshold,
			final int maxIter) {
		super(relativeThreshold, absoluteThreshold);

		if (maxIter <= 0) {
			throw new NotStrictlyPositiveException(maxIter);
		}
		maxIterationCount = maxIter;
	}

	/**
	 * Check if the optimization algorithm has converged considering the last two
	 * points. This method may be called several time from the same algorithm
	 * iteration with different points. This can be detected by checking the
	 * iteration number at each call if needed. Each time this method is called, the
	 * previous and current point correspond to points with the same role at each
	 * iteration, so they can be compared. As an example, simplex-based algorithms
	 * call this method for all points of the simplex, not only for the best or
	 * worst ones.
	 *
	 * @param iteration Index of current iteration
	 * @param previous  Best point in the previous iteration.
	 * @param current   Best point in the current iteration.
	 * @return {@code true} if the algorithm has converged.
	 */
	@Override
	public boolean converged(final int iteration, final UnivariatePointValuePair previous,
			final UnivariatePointValuePair current) {
		if (maxIterationCount != ITERATION_CHECK_DISABLED && iteration >= maxIterationCount) {
			return true;
		}

		final double p = FastMath.log10(previous.getValue());
		final double c = FastMath.log10(current.getValue());
		final double difference = FastMath.abs(p - c);
		final double size = FastMath.max(FastMath.abs(p), FastMath.abs(c));
		final double rel = FastMath.log10(getRelativeThreshold());
		final double abs = FastMath.log10(getAbsoluteThreshold());
		return difference <= size * rel || difference <= abs;
	}
}

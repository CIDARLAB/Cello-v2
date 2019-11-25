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

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.optim.ConvergenceChecker;
import org.apache.commons.math3.optim.MaxEval;
import org.apache.commons.math3.optim.nonlinear.scalar.GoalType;
import org.apache.commons.math3.optim.univariate.BrentOptimizer;
import org.apache.commons.math3.optim.univariate.SearchInterval;
import org.apache.commons.math3.optim.univariate.UnivariateObjectiveFunction;
import org.apache.commons.math3.optim.univariate.UnivariateOptimizer;
import org.apache.commons.math3.optim.univariate.UnivariatePointValuePair;
import org.apache.commons.math3.random.JDKRandomGenerator;
import org.apache.commons.math3.random.RandomGenerator;
import org.cellocad.cello2.common.Pair;
import org.cellocad.cello2.technologyMapping.algorithm.SimulatedAnnealing.data.ucf.Gate;
import org.cellocad.cello2.technologyMapping.algorithm.SimulatedAnnealing.data.ucf.ResponseFunction;

/**
 * 
 *
 * @author Timothy Jones
 *
 * @date 2019-11-19
 *
 */
public class StabilityScorer {

	public StabilityScorer(Gate gate1, Gate gate2) {
		ResponseFunction rf1 = gate1.getResponseFunction();
		ResponseFunction rf2 = gate2.getResponseFunction();
		this.eval1 = new ResponseFunctionEvaluator(rf1);
		this.eval2 = new ResponseFunctionEvaluator(rf2);
	}

	private static Double getInputFromOutput(final ResponseFunctionEvaluator f, final Double x0) {
		Double rtn = null;
		UnivariateFunction g = new UnivariateFunctionWithOffset(f, -1 * x0);
		ConvergenceChecker<UnivariatePointValuePair> checker = new SimpleLogUnivariateValueChecker(1e-4, 1e-4);
		UnivariateOptimizer o = new BrentOptimizer(1e-4, 1e-4, checker);
		UnivariatePointValuePair result = o.optimize(GoalType.MINIMIZE, new SearchInterval(1e-3, 1e3), new MaxEval(40),
				new UnivariateObjectiveFunction(g));
		rtn = result.getPoint();
		return rtn;
	}

	private List<Pair<Double, Double>> getStablePoints() {
		List<Pair<Double, Double>> rtn = new ArrayList<>();

		List<Double> x = new ArrayList<>();
		x.add(1e-3);
		x.add(1e+3);

		for (Double c : x) {
			Double y1p = 1e-10;
			Double y2p = 1e-10;
			Double y1 = this.getResponseFunctionEvaluator1().value(c);
			Double y2 = this.getResponseFunctionEvaluator2().value(y1);
			while (Math.abs(1.0 - y1 / y1p) > D_TOLERANCE && Math.abs(1.0 - y2 / y2p) > D_TOLERANCE) {
				y1p = y1;
				y2p = y2;
				y1 = this.getResponseFunctionEvaluator1().value(y2);
				y2 = this.getResponseFunctionEvaluator2().value(y1);
			}
			Pair<Double, Double> fp = new Pair<>(y1, y2);
			rtn.add(fp);
		}

		return rtn;
	}

	private Pair<Double, Double> getUnstablePoint(final Pair<Double, Double> guess) {
		Pair<Double, Double> rtn = null;

		Double x1 = getInputFromOutput(this.getResponseFunctionEvaluator1(), guess.getFirst());
		Double x2 = getInputFromOutput(this.getResponseFunctionEvaluator2(), x1);

		Double x1p = 0.0;
		Double x2p = 0.0;

		while (Math.abs(1.0 - x1 / x1p) > D_TOLERANCE && Math.abs(1.0 - x2 / x2p) > D_TOLERANCE) {
			x1p = x1;
			x2p = x2;
			x1 = getInputFromOutput(this.getResponseFunctionEvaluator1(), x2);
			x2 = getInputFromOutput(this.getResponseFunctionEvaluator2(), x1);
		}
		rtn = new Pair<>(x2, x1);

		return rtn;
	}

	private static List<Double> getLogUniformRandom(final double lo, final double hi, final int size) {
		List<Double> rtn = new ArrayList<>();
		SecureRandom random;
		try {
			random = SecureRandom.getInstance("SHA1PRNG");
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException("Unable to generate random seed.");
		}
		RandomGenerator rand = new JDKRandomGenerator();
		rand.setSeed(random.generateSeed(1)[0]);
		for (int i = 0; i < size; i++) {
			Double r = rand.nextDouble();
			r = Math.log10(lo) + r * (Math.log10(hi) - Math.log10(lo));
			r = Math.pow(10, r);
			rtn.add(r);
		}
		return rtn;
	}

	private Double getArea(final Pair<Double, Double> a, final Pair<Double, Double> b, final Double sign) {
		Double rtn = 0.0;

		double d1 = Math.log10(a.getFirst()) - Math.log10(b.getFirst());
		double d2 = Math.log10(b.getSecond()) - Math.log10(a.getSecond());
		int d = (int) (d1 * d2 * D_SAMP);
		if (d < 1)
			return 0.0;

		List<Double> r1 = getLogUniformRandom(b.getFirst(), a.getFirst(), d);
		List<Double> r2 = getLogUniformRandom(a.getSecond(), b.getSecond(), d);

		int c = 0;
		for (int i = 0; i < d; i++) {
			Double ra = r1.get(i);
			Double rb = r2.get(i);
			Double y1 = this.getResponseFunctionEvaluator1().value(rb);
			Double y2 = this.getResponseFunctionEvaluator2().value(ra);
			if ((sign * y1 >= sign * ra) && sign * y2 <= sign * rb)
				c++;
		}

		rtn = Double.valueOf(c) / Double.valueOf(r1.size()) * d1 * d2;

		return rtn;
	}

	public Double score() {
		Double rtn = 0.0;

		List<Pair<Double, Double>> fp = this.getStablePoints();

		Double y1 = Math.sqrt(fp.get(0).getFirst() * fp.get(1).getFirst());
		Double y2 = Math.sqrt(fp.get(0).getSecond() * fp.get(1).getSecond());
		Pair<Double, Double> guess = new Pair<>(y1, y2);

		Pair<Double, Double> u = getUnstablePoint(guess);

		Double a1 = this.getArea(fp.get(0), u, 1.0);
		Double a2 = this.getArea(u, fp.get(1), -1.0);

		rtn = a1 * a2 / (a1 + a2);

		return rtn;
	}

	/**
	 * Getter for <i>eval1</i>
	 * 
	 * @return the eval1 of this instance
	 */
	public ResponseFunctionEvaluator getResponseFunctionEvaluator1() {
		return eval1;
	}

	/**
	 * Getter for <i>eval2</i>
	 * 
	 * @return the eval2 of this instance
	 */
	public ResponseFunctionEvaluator getResponseFunctionEvaluator2() {
		return eval2;
	}

	private static double D_TOLERANCE = 1e-6;
	private static double D_SAMP = 1000;

	private ResponseFunctionEvaluator eval1;
	private ResponseFunctionEvaluator eval2;

}

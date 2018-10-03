/**
 * Copyright (C) 2018 Boston University (BU)
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
package results.technologyMapping.activity.signal;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import common.Pair;

/**
 * 
 *
 * @author Timothy Jones
 *
 * @date 2018-05-24
 *
 */
public class SensorSignals<T> {
	
	private void init() {
		signals = new HashMap<>();
	}
	
	public SensorSignals() {
		init();
	}
	
	public SensorSignals(final List<T> nodes) {
		init();
		for (int i = 0; i < nodes.size(); i++) {
			T node = nodes.get(i);
			this.addNode(node);
		}
	}
	
	public void addNode(final T node) {
		Pair<Double,Double> pair = new Pair<>(0.0,0.0);
		this.getSignals().put(node, pair);
	}
	
	public Double getLowActivitySignal(final T node) {
		return this.getSignals().get(node).getFirst();
	}
	
	public Double getHighActivitySignal(final T node) {
		return this.getSignals().get(node).getSecond();
	}
	
	public void setLowActivitySignal(final T node, final Double value) {
		Pair<Double,Double> pair = this.getSignal(node);
		pair.setFirst(value);
	}
	
	public void setHighActivitySignal(final T node, final Double value) {
		Pair<Double,Double> pair = this.getSignal(node);
		pair.setSecond(value);
	}
	
	protected Pair<Double,Double> getSignal(final T node) {
		Pair<Double,Double> rtn = null;
		rtn = this.getSignals().get(node);
		return rtn;
	}
	
	protected Map<T,Pair<Double,Double>> getSignals() {
		return signals;
	}
	
	private Map<T,Pair<Double,Double>> signals;
}

/**
 * Copyright (C) 2017 Massachusetts Institute of Technology (MIT)
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
package org.cellocad.v2.partitioning.algorithm;

import org.cellocad.v2.common.algorithm.AlgorithmFactory;
import org.cellocad.v2.partitioning.algorithm.GPCC_BASE.GPCC_BASE;
import org.cellocad.v2.partitioning.algorithm.GPCC_SCIP_BASE.GPCC_SCIP_BASE;
import org.cellocad.v2.partitioning.algorithm.GPCC_SUGARM_BASE.GPCC_SUGARM_BASE;
import org.cellocad.v2.partitioning.algorithm.HMetis.HMetis;

/**
 * The PTAlgorithmFactory is an algorithm factory for the <i>partitioning</i> stage. 
 * 
 * @author Vincent Mirian
 * 
 * @date 2018-05-21
 *
 */
public class PTAlgorithmFactory extends AlgorithmFactory<PTAlgorithm>{

	/**
	 *  Returns the PTAlgorithm that has the same name as the parameter <i>name</i> within this instance
	 *  
	 *  @param name string used for searching the PTAlgorithmFactory
	 *  @return PTAlgorithm instance if the PTAlgorithm exists within the PTAlgorithmFactory, otherwise null
	 */
	@Override
	protected PTAlgorithm getAlgorithm(final String name) {
		PTAlgorithm rtn = null;
		if (name.equals("GPCC_SUGARM_BASE")){
			rtn = new GPCC_SUGARM_BASE();
		}
		if (name.equals("GPCC_SCIP_BASE")){
			rtn = new GPCC_SCIP_BASE();
		}
		if (name.equals("GPCC_BASE")){
			rtn = new GPCC_BASE();
		}
		if (name.equals("HMetis")){
			rtn = new HMetis();
		}
		return rtn;
	}
	
}

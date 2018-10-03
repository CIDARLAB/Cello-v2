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
package org.cellocad.cello2.technologyMapping.algorithm.data;

import org.cellocad.cello2.common.algorithm.data.NetlistNodeDataFactory;
import org.cellocad.cello2.technologyMapping.algorithm.Cello_JY_TP.data.Cello_JY_TPNetlistNodeData;
import org.cellocad.cello2.technologyMapping.algorithm.SimulatedAnnealing.data.SimulatedAnnealingNetlistNodeData;

/**
 * The TMNetlistNodeDataFactory is a NetlistNodeData factory for the <i>technologyMapping</i> stage.
 * 
 * @author Vincent Mirian
 * 
 * @date 2018-05-21
 *
 */
public class TMNetlistNodeDataFactory extends NetlistNodeDataFactory<TMNetlistNodeData>{

	/**
	 *  Returns the TMNetlistNodeData that has the same name as the parameter <i>name</i> within the TMNetlistNodeDataFactory
	 *  
	 *  @param name string used for searching this instance
	 *  @return the TMNetlistNodeData instance if the TMNetlistNodeData exists within the TMNetlistNodeDataFactory, otherwise null
	 */
	@Override
	protected TMNetlistNodeData getNetlistNodeData(final String name) {
		TMNetlistNodeData rtn = null;
		if (name.equals("Cello_JY_TP")){
			rtn = new Cello_JY_TPNetlistNodeData();
		}
		if (name.equals("SimulatedAnnealing")){
			rtn = new SimulatedAnnealingNetlistNodeData();
		}
		return rtn;
	}
	
}

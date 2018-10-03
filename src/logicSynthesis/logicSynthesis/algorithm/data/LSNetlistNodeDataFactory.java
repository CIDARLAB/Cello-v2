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
package logicSynthesis.algorithm.data;

import common.algorithm.data.NetlistNodeDataFactory;
import logicSynthesis.algorithm.Yosys.data.YosysNetlistNodeData;

/**
 * The LSNetlistNodeDataFactory is a NetlistNodeData factory for the <i>logicSynthesis</i> stage.
 * 
 * @author Vincent Mirian
 * 
 * @date 2018-05-21
 *
 */
public class LSNetlistNodeDataFactory extends NetlistNodeDataFactory<LSNetlistNodeData>{

	/**
	 *  Returns the LSNetlistNodeData that has the same name as the parameter <i>name</i> within the LSNetlistNodeDataFactory
	 *  
	 *  @param name string used for searching this instance
	 *  @return the LSNetlistNodeData instance if the LSNetlistNodeData exists within the LSNetlistNodeDataFactory, otherwise null
	 */
	@Override
	protected LSNetlistNodeData getNetlistNodeData(final String name) {
		LSNetlistNodeData rtn = null;
		if (name.equals("Yosys")){
			rtn = new YosysNetlistNodeData();
		}
		return rtn;
	}
	
}

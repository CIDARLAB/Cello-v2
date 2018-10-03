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
package partitioning.algorithm.data;

import common.algorithm.data.NetlistDataFactory;
import partitioning.algorithm.GPCC_SUGARM_BASE.data.GPCC_SUGARM_BASENetlistData;
import partitioning.algorithm.GPCC_SCIP_BASE.data.GPCC_SCIP_BASENetlistData;
import partitioning.algorithm.GPCC_BASE.data.GPCC_BASENetlistData;
import partitioning.algorithm.HMetis.data.HMetisNetlistData;

/**
 * The PTNetlistDataFactory class is a NetlistData factory for the <i>partitioning</i> stage.
 * 
 * @author Vincent Mirian
 * 
 * @date 2018-05-21
 *
 */
public class PTNetlistDataFactory extends NetlistDataFactory<PTNetlistData>{

	/**
	 *  Returns the PTNetlistData that has the same name as the parameter <i>name</i> within the PTNetlistDataFactory
	 *  
	 *  @param name string used for searching this instance
	 *  @return the PTNetlistData instance if the PTNetlistData exists within the PTNetlistDataFactory, otherwise null
	 */
	@Override
	protected PTNetlistData getNetlistData(final String name) {
		PTNetlistData rtn = null;
		if (name.equals("GPCC_SUGARM_BASE")){
			rtn = new GPCC_SUGARM_BASENetlistData();
		}
		if (name.equals("GPCC_SCIP_BASE")){
			rtn = new GPCC_SCIP_BASENetlistData();
		}
		if (name.equals("GPCC_BASE")){
			rtn = new GPCC_BASENetlistData();
		}
		if (name.equals("HMetis")){
			rtn = new HMetisNetlistData();
		}
		return rtn;
	}
	
}

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
package common.algorithm.data;

import common.CObject;
import common.profile.AlgorithmProfile;

/**
 * The NetlistEdgeDataFactory class is a NetlistEdgeData factory.
 * @param <T> the type of NetlistEdgeData
 * 
 * @author Vincent Mirian
 * 
 * @date Dec 15, 2017
 *
 */
public abstract class NetlistEdgeDataFactory<T extends NetlistEdgeData> extends CObject{

	/**
	 *  Returns the NetlistEdgeData that has the same name as the parameter <i>name</i> within the NetlistEdgeDataFactory
	 *  
	 *  @param name string used for searching the NetlistEdgeDataFactory
	 *  @return the NetlistEdgeData instance if the NetlistEdgeData exists within the NetlistEdgeDataFactory, otherwise null
	 */
	abstract protected T getNetlistEdgeData(final String name);
	
	/**
	 *  Returns the NetlistEdgeData that has the same name as the parameter <i>AProfile</i> within the NetlistEdgeDataFactory
	 *  
	 *  @param AProfile AlgorithmProfile used for searching the NetlistEdgeDataFactory
	 *  @return the NetlistEdgeData instance if the NetlistEdgeData exists within the NetlistEdgeDataFactory, otherwise null
	 */
	public T getNetlistEdgeData(final AlgorithmProfile AProfile){
		T rtn = null;
		if (AProfile != null){
			String name = AProfile.getName();
			rtn = getNetlistEdgeData(name);
			if (rtn != null) {
				rtn.setName(AProfile.getName());
			}
		}
		return rtn;
	}
}

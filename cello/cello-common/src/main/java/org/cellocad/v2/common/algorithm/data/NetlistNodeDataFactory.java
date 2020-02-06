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
package org.cellocad.v2.common.algorithm.data;

import org.cellocad.v2.common.CObject;
import org.cellocad.v2.common.profile.AlgorithmProfile;

/**
 * The NetlistNodeDataFactory class is a NetlistNodeData factory.
 * @param <T> the type of NetlistNodeData
 * 
 * @author Vincent Mirian
 * 
 * @date Dec 15, 2017
 *
 */
public abstract class NetlistNodeDataFactory<T extends NetlistNodeData> extends CObject{

	/**
	 *  Returns the NetlistNodeData that has the same name as the parameter <i>name</i> within the NetlistNodeDataFactory
	 *  
	 *  @param name string used for searching the NetlistNodeDataFactory
	 *  @return the NetlistNodeData instance if the NetlistNodeData exists within the NetlistNodeDataFactory, otherwise null
	 */
	abstract protected T getNetlistNodeData(final String name);
	
	/**
	 *  Returns the NetlistNodeData that has the same name as the parameter <i>AProfile</i> within the NetlistNodeDataFactory
	 *  
	 *  @param AProfile AlgorithmProfile used for searching the NetlistNodeDataFactory
	 *  @return the NetlistNodeData instance if the NetlistNodeData exists within the NetlistNodeDataFactory, otherwise null
	 */
	public T getNetlistNodeData(final AlgorithmProfile AProfile){
		T rtn = null;
		if (AProfile != null){
			String name = AProfile.getName();
			rtn = getNetlistNodeData(name);
			if (rtn != null) {
				rtn.setName(AProfile.getName());
			}
		}
		return rtn;
	}
}

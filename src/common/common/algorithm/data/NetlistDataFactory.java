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
 * The NetlistDataFactory class is a NetlistData factory.
 * @param <T> the type of NetlistData
 * 
 * @author Vincent Mirian
 * 
 * @date Dec 15, 2017
 *
 */
public abstract class NetlistDataFactory<T extends NetlistData> extends CObject{
	
	/**
	 *  Returns the NetlistData that has the same name as the parameter <i>name</i> within the NetlistDataFactory
	 *  
	 *  @param name string used for searching the NetlistDataFactory
	 *  @return the NetlistData instance if the NetlistData exists within the NetlistDataFactory, otherwise null
	 */
	abstract protected T getNetlistData(final String name);
	
	/**
	 *  Returns the NetlistData that has the same name as the parameter <i>AProfile</i> within the NetlistDataFactory
	 *  
	 *  @param AProfile AlgorithmProfile used for searching the NetlistDataFactory
	 *  @return the NetlistData instance if the NetlistData exists within the NetlistDataFactory, otherwise null
	 */
	public T getNetlistData(final AlgorithmProfile AProfile){
		T rtn = null;
		if (AProfile != null){
			String name = AProfile.getName();
			rtn = getNetlistData(name);
			if (rtn != null) {
				rtn.setName(AProfile.getName());
			}
		}
		return rtn;
	}
}

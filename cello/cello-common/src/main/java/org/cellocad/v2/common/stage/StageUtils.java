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
package org.cellocad.v2.common.stage;

import org.cellocad.v2.common.Utils;
import org.cellocad.v2.common.runtime.environment.RuntimeEnv;

/**
 * The StageUtils class is class with utility methods for <i>Stage</i> instances.
 * 
 * @author Vincent Mirian
 * 
 * @date Nov 20, 2017
 *
 */
final public class StageUtils {

	/**
	 *  Initializes a newly created Stage using the RuntimeEnv, <i>runEnv</i>, and,
	 *  the string referencing command line argument for the algorithm name, <i>algoName</i>.
	 *  
	 *  @param runEnv the RuntimeEnv to extract the algorithm name, <i>algoName</i>
	 *  @param algoName the string referencing command line argument for the algorithm name
	 *  @return the Stage
	 *  @throws RuntimeException if any of the parameters are null
	 */
	static public Stage getStage(final RuntimeEnv runEnv, final String algoName){
		Utils.isNullRuntimeException(runEnv, "runEnv");
		Utils.isNullRuntimeException(algoName, "algoName");
		Stage rtn = new Stage();
		String myAlgoName = runEnv.getOptionValue(algoName);
		rtn.setAlgorithmName(myAlgoName);
	    return rtn;
	}
}

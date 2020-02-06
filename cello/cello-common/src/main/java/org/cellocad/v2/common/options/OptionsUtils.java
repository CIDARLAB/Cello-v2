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
package org.cellocad.v2.common.options;

import org.cellocad.v2.common.Utils;
import org.cellocad.v2.common.runtime.environment.RuntimeEnv;

/**
 * The OptionsUtils class is class with utility methods for <i>Options</i> instances.
 * 
 * @author Vincent Mirian
 * 
 * @date Dec 9, 2017
 *
 */
final public class OptionsUtils {

	/**
	 *  Initializes a newly created Options using the RuntimeEnv, <i>runEnv</i>, and,
	 *  the string referencing command line argument for the Options file, <i>options</i>.
	 *  
	 *  @param runEnv the RuntimeEnv to extract the Options file, <i>options</i>
	 *  @param options the string referencing command line argument for the Options file
	 *  @return the Options if created successfully, otherwise null
	 *  @throws RuntimeException if any of the parameters are null
	 */
	static public Options getOptions(final RuntimeEnv runEnv, final String options){
		Utils.isNullRuntimeException(runEnv, "runEnv");
		Utils.isNullRuntimeException(options, "options");
		Options rtn = null;
		// get Options File
		String optionsFilename = runEnv.getOptionValue(options);
		if (optionsFilename != null) {
			rtn = new Options(optionsFilename);
		}
	    return rtn;
	}
	
}

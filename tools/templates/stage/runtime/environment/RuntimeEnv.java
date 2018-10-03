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
package ##NONCE##21##STAGENAME##21##NONCE.runtime.environment;

//uncomment the line below to add options
//import org.apache.commons.cli.Options;

import common.stage.runtime.environment.StageRuntimeEnv;

/**
 * The ##NONCE##21##STAGEPREFIX##21##NONCERuntimeEnv class is a class for managing and parsing command line argument(s) for the <i>##NONCE##21##STAGENAME##21##NONCE</i> stage.
 * 
 * @author Vincent Mirian
 * 
 * @date Today
 *
 */
public class ##NONCE##21##STAGEPREFIX##21##NONCERuntimeEnv extends StageRuntimeEnv{

	/**
	 *  Initializes a newly created ##NONCE##21##STAGEPREFIX##21##NONCERuntimeEnv with command line argument(s), <i>args</i>.
	 *  
	 *  @param args command line argument(s)
	 */
	public ##NONCE##21##STAGEPREFIX##21##NONCERuntimeEnv(String[] args) {
		super(args);
	}
	
	/**
	 *  Setter for <i>options</i>
	 */
	@Override
	protected void setOptions() {
		super.setOptions();
		// uncomment the line below to add options
		// Options options = this.getOptions();
	}
}

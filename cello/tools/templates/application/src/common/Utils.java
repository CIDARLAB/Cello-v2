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
package org.cellocad.cello2.##NONCE##21##APPNAME##21##NONCE.common;

import common.Utils;

/**
 * The ##NONCE##21##APPNAME##21##NONCEUtils class is class with utility methods for the <i>##NONCE##21##APPNAME##21##NONCE</i> application.
 * 
 * @author Vincent Mirian
 * 
 * @date Today
 *
 */
public class ##NONCE##21##APPNAME##21##NONCEUtils {

	/**
	 * Returns the path of the ClassLoader
	 * 
	 * @return the path of the ClassLoader
	 *
	 */
	static public String getFilepath(){
		String rtn = "";
		rtn = ##NONCE##21##APPNAME##21##NONCEUtils.class.getClassLoader().getResource(".").getPath();
		return rtn;
	}

	/**
	 * Returns the path of the Resources directory for the <i>##NONCE##21##APPNAME##21##NONCE</i> application
	 * 
	 * @return the path of the Resources directory for the <i>##NONCE##21##APPNAME##21##NONCE</i> application
	 *
	 */
	static public String getResourcesFilepath(){
		String rtn = "";
		rtn += ##NONCE##21##APPNAME##21##NONCEUtils.getFilepath();
		rtn += "resources-";
		rtn += "##NONCE##21##APPNAME##21##NONCE";
		rtn += Utils.getFileSeparator();
		return rtn;
	}

	/**
	 * Returns the path to the configuation file for the <i>##NONCE##21##APPNAME##21##NONCE</i> application
	 * 
	 * @return the path to the configuation file for the <i>##NONCE##21##APPNAME##21##NONCE</i> application
	 *
	 */
	static public String getResourcesConfigurationFile(){
		String rtn = "";
		rtn += ##NONCE##21##APPNAME##21##NONCEUtils.getResourcesFilepath();
		rtn += "Configuration.json";
		return rtn;
	}
}

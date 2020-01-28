/**
 * Copyright (C) 2020
 * Massachusetts Institute of Technology (MIT)
 * Boston University (BU)
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
package org.cellocad.cello2.common.runtime.environment;

/**
 * The ArgString class is class containing the string referencing the common
 * command line argument(s) for a stage and an application.
 * 
 * @author Vincent Mirian
 * @author Timothy Jones
 * 
 * @date Nov 17, 2017
 *
 */
public class ArgString {
	
	/*
	 * General
	 */
	/**
	 * String referencing the HELP command line argument
	 */
	final static public String HELP = "help";

	/**
	 * String referencing the INPUTSENSORFILE command line argument
	 */
	final static public String INPUTSENSORFILE = "inputSensorFile";

	/**
	 * String referencing the OUTPUTDEVICEFILE command line argument
	 */
	final static public String OUTPUTDEVICEFILE = "outputDeviceFile";

	/**
	 * String referencing the USERCONSTRAINTSFILE command line argument
	 */
	final static public String USERCONSTRAINTSFILE = "userConstraintsFile";

	/**
	 * String referencing the OPTIONS command line argument
	 */
	final static public String OPTIONS = "options";

	/**
	 * String referencing the OUTPUTDIR command line argument
	 */
	final static public String OUTPUTDIR  = "outputDir";

	/**
	 * String referencing the PYTHONENV command line argument
	 */
	final static public String PYTHONENV = "pythonEnv";

	/**
	 * String referencing the INPUTNETLIST command line argument
	 */
    final static public String INPUTNETLIST = "inputNetlist";

	/**
	 * String referencing the OUTPUTNETLIST command line argument
	 */
    final static public String OUTPUTNETLIST = "outputNetlist";

	/**
	 * String referencing the NETLISTCONSTRAINTFILE command line argument
	 */
	final static public String NETLISTCONSTRAINTFILE = "netlistConstraintFile";
	
	/**
	 * String referencing the LOGFILENAME command line argument
	 */
	final static public String LOGFILENAME = "logFilename";
	
}

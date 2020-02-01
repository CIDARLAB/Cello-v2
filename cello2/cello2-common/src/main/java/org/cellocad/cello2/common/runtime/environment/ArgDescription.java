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
 * The ArgDescription class is class containing the description of the common
 * command line argument(s) for a stage and an application.
 * 
 * @author Vincent Mirian
 * @author Timothy Jones
 * 
 * @date Nov 17, 2017
 *
 */
public class ArgDescription {

	/*
	 * General
	 */
	/**
	 * String representing the HELP command line argument description
	 */
	final static public String HELP_DESCRIPTION = "print this message";

	/**
	 * String representing the INPUTSENSORFILE command line argument description
	 */
	final static public String INPUTSENSORFILE_DESCRIPTION = "path to input sensor file";

	/**
	 * String representing the OUTPUTDEVICEFILE command line argument description
	 */
	final static public String OUTPUTDEVICEFILE_DESCRIPTION = "path to output device file";

	/**
	 * String representing the USERCONSTRAINTSFILE command line argument description
	 */
	final static public String USERCONSTRAINTSFILE_DESCRIPTION = "path to user constraints file";
	
	/**
	 * String representing the OPTIONS command line argument description
	 */
	final static public String OPTIONS_DESCRIPTION = "path to options file";
	
	/**
	 * String representing the OUTPUTDIR command line argument description
	 */
	final static public String OUTPUTDIR_DESCRIPTION = "path of output directory";
	
	/**
	 * String representing the PYTHONENV command line argument description
	 */
	final static public String PYTHONENV_DESCRIPTION = "path to python environment";
	
	/**
	 * String representing the INPUTNETLIST command line argument description
	 */
    final static public String INPUTNETLIST_DESCRIPTION = "path to input netlist file";
	
	/**
	 * String representing the OUTPUTNETLIST command line argument description
	 */
    final static public String OUTPUTNETLIST_DESCRIPTION = "path to output netlist file";
	
	/**
	 * String representing the NETLISTCONSTRAINTFILE command line argument description
	 */
	final static public String NETLISTCONSTRAINTFILE_DESCRIPTION = "path to netlist constraint file";
	
	/**
	 * String representing the LOGFILENAME command line argument
	 */
	final static public String LOGFILENAME_DESCRIPTION = "log filename";
	
}

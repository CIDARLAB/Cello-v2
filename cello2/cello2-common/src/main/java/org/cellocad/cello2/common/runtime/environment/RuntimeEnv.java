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
package org.cellocad.cello2.common.runtime.environment;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.cellocad.cello2.common.CObject;
import org.cellocad.cello2.common.Utils;

/**
 * The RuntimeEnv class is a class for managing and parsing command line argument(s) for a stage and an application.
 * 
 * @author Vincent Mirian
 * 
 * @date Nov 13, 2017
 *
 */
public class RuntimeEnv extends CObject{
	
	private void init(){
		this.parser = new DefaultParser();
		this.options = new Options();		
	}
	
	/**
	 *  Initializes a newly created RuntimeEnv with command line argument(s), <i>args</i>.
	 *  
	 *  @param args command line argument(s)
	 */
	public RuntimeEnv(final String[] args){
		super();
		init();
		this.setOptions();
		try {
			line = parser.parse(this.getOptions(), args);
	    }
	    catch(ParseException e) {
	        // oops, something went wrong
	        System.err.println( "Parsing failed.  Reason: " + e.getMessage() );
	    }
		if (this.hasOption(ArgString.HELP)) {
			this.printHelp();
			Utils.exit(0);
		}
	}
	
	// print help
	/**
	 *  Print the help message of this instance
	 */
	public void printHelp() {
		 HelpFormatter formatter = new HelpFormatter();
		 String name = this.getName();
		 if (name.isEmpty()) {
			 name = "\"EXECUTABLE\"";
		 }
		 formatter.printHelp(name, this.getOptions(), true);
	}

	// getter and setter
	/**
	 *  Getter for <i>options</i>
	 *  @return the options for this instance
	 */
	protected Options getOptions() {
		return this.options;
	}

	/**
	 *  Setter for <i>options</i>
	 */
	protected void setOptions() {
		Options options = this.getOptions();
		options.addOption(this.getHelpOption());
		options.addOption(this.getTargetDataFileOption());
		options.addOption(this.getOptionsOption());
		options.addOption(this.getOutputDirOption());
		options.addOption(this.getPythonEnvOption());
		options.addOption(this.getInputNetlistOption());
		options.addOption(this.getOutputNetlistOption());
		options.addOption(this.getNetlistConstraintFileOption());
		options.addOption(this.getLogFilenameFileOption());
	}

	/*
	 * Options
	 */
	private Option getHelpOption(){
		Option rtn = new Option( ArgString.HELP, false, ArgDescription.HELP_DESCRIPTION);
		return rtn;
	}

	/**
	 *  Getter for the TARGETDATAFILE Option. This option is required.
	 *  @return the TARGETDATAFILE Option for this instance
	 */
	protected Option getTargetDataFileOption(){
		Option rtn = new Option( ArgString.TARGETDATAFILE, true, ArgDescription.TARGETDATAFILE_DESCRIPTION);
		this.makeRequired(rtn);
		return rtn;
	}

	/**
	 *  Getter for the OPTIONS Option.
	 *  @return the OPTIONS Option for this instance
	 */
	private Option getOptionsOption(){
		Option rtn = new Option( ArgString.OPTIONS, true, ArgDescription.OPTIONS_DESCRIPTION);
		return rtn;
	}

	/**
	 *  Getter for the OUTPUTDIR Option.
	 *  @return the OUTPUTDIR Option for this instance
	 */
	protected Option getOutputDirOption(){
		Option rtn = new Option( ArgString.OUTPUTDIR, true, ArgDescription.OUTPUTDIR_DESCRIPTION);
		return rtn;
	}

	/**
	 *  Getter for the PYTHONENV Option.
	 *  @return the PYTHONENV Option for this instance
	 */
	protected Option getPythonEnvOption(){
		Option rtn = new Option( ArgString.PYTHONENV, true, ArgDescription.PYTHONENV_DESCRIPTION);
		return rtn;
	}

	/**
	 *  Getter for the INPUTNETLIST Option. This option is required.
	 *  @return the INPUTNETLIST Option for this instance
	 */
	protected Option getInputNetlistOption(){
		Option rtn = new Option( ArgString.INPUTNETLIST, true, ArgDescription.INPUTNETLIST_DESCRIPTION);
		this.makeRequired(rtn);
		return rtn;
	}

	/**
	 *  Getter for the OUTPUTNETLIST Option.
	 *  @return the OUTPUTNETLIST Option for this instance
	 */
	protected Option getOutputNetlistOption(){
		Option rtn = new Option( ArgString.OUTPUTNETLIST, true, ArgDescription.OUTPUTNETLIST_DESCRIPTION);
		return rtn;
	}

	/**
	 *  Getter for the NETLISTCONSTRAINTFILE Option.
	 *  @return the NETLISTCONSTRAINTFILE Option for this instance
	 */
	protected Option getNetlistConstraintFileOption(){
		Option rtn = new Option( ArgString.NETLISTCONSTRAINTFILE, true, ArgDescription.NETLISTCONSTRAINTFILE_DESCRIPTION);
		return rtn;
	}

	/**
	 *  Getter for the LOGFILENAME Option.
	 *  @return the LOGFILENAME Option for this instance
	 */
	protected Option getLogFilenameFileOption(){
		Option rtn = new Option( ArgString.LOGFILENAME, true, ArgDescription.LOGFILENAME_DESCRIPTION);
		return rtn;
	}

	/**
	 *  Returns the default value for string <i>str</i> that references a command line argument.
	 *  
	 *  @param str the command line argument
	 *  @return the string default value for string <i>str</i> that references a command line argument if command line argument exists,
	 *  otherwise null
	 */
	// get Values
	protected String getDefault(final String str){
		String rtn = null;
		switch (str) {
        case ArgString.OUTPUTDIR:
        	rtn = "";
        	rtn += Utils.getWorkingDirectory();
        	break; 
        case ArgString.PYTHONENV:
        	rtn = "";
        	break;
		}
		return rtn;
	}

	/**
	 *  Returns the value for string <i>str</i> that references a command line argument.
	 *  
	 *  @param str the command line argument
	 *  @return the string value for string <i>str</i> that references a command line argument.
	 */
	public String getOptionValue(final String str){
		String rtn = null;
		rtn = line.getOptionValue(str);
		if (rtn == null) {
			rtn = this.getDefault(str);
		}
		return rtn;
	}

	/**
	 *  Returns the value for char <i>c</i> that references a command line argument.
	 *  
	 *  @param c the command line argument
	 *  @return the string value for char <i>c</i> that references a command line argument.
	 */
	public String getOptionValue(final char c){
		String rtn = null;
		rtn = line.getOptionValue(c);
		return rtn;
	}

	/**
	 *  Returns a boolean flag signifying the string <i>str</i> that references a command line argument is present in this instance.
	 *  
	 *  @param str the command line argument
	 *  @return true if the string <i>str</i> that references a command line argument is present,
	 *  otherwise false
	 */
	public boolean hasOption(final String str){
		boolean rtn = false;
		rtn = line.hasOption(str);
		return rtn;
	}

	/**
	 *  Returns a boolean flag signifying the char <i>c</i> that references a command line argument is present in this instance.
	 *  
	 *  @param c the command line argument
	 *  @return true if the char <i>c</i> that references a command line argument is present,
	 *  otherwise false
	 */
	public boolean hasOption(final char c){
		boolean rtn = false;
		rtn = line.hasOption(c);
		return rtn;
	}

	/**
	 *  Enables the Option <i>arg</i> to be a required command line argument 
	 *  
	 *  @param arg the command line argument
	 */
	protected void makeRequired(final Option arg){
		arg.setRequired(true);		
	}

	/**
	 *  Enables the Option <i>arg</i> to <b>not</b> be a required command line argument 
	 *  
	 *  @param arg the command line argument
	 */
	protected void makeNotRequired(final Option arg){
		arg.setRequired(false);		
	}
	    
	private CommandLineParser parser;
    private CommandLine line;
    private Options options;
}

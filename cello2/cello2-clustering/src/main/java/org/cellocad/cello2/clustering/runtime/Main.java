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
package org.cellocad.cello2.clustering.runtime;

import java.io.File;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cellocad.cello2.clustering.runtime.environment.CLArgString;
import org.cellocad.cello2.clustering.runtime.environment.CLRuntimeEnv;
import org.cellocad.cello2.common.Utils;
import org.cellocad.cello2.common.netlistConstraint.data.NetlistConstraint;
import org.cellocad.cello2.common.netlistConstraint.data.NetlistConstraintUtils;
import org.cellocad.cello2.common.stage.Stage;
import org.cellocad.cello2.common.stage.StageUtils;
import org.cellocad.cello2.common.target.data.TargetData;
import org.cellocad.cello2.common.target.data.TargetDataUtils;
import org.cellocad.cello2.results.netlist.Netlist;
import org.cellocad.cello2.results.netlist.NetlistUtils;

/**
 * The Main class is the executable class for the <i>clustering</i> stage.
 * 
 * @author Vincent Mirian
 * 
 * @date 2018-05-21
 *
 */
public class Main {

	/**
	 * The <i>main</i> method is the executable for the <i>clustering</i> stage.
	 * 
	 * @param args command line argument(s)
	 */
	public static void main(String[] args) {
		CLRuntimeEnv runEnv = new CLRuntimeEnv(args);
		runEnv.setName("clustering");
		// Setup Logger
		Main.setupLogger(runEnv);
		// InputFile
		String inputFilePath = runEnv.getOptionValue(CLArgString.INPUTNETLIST);
		File inputFile = new File(inputFilePath);
		if (!(inputFile.exists() && !inputFile.isDirectory())) {
			throw new RuntimeException("Input file does not exist!");
		}
		// Read Netlist
		Netlist netlist = NetlistUtils.getNetlist(runEnv, CLArgString.INPUTNETLIST);
		if (!netlist.isValid()) {
			throw new RuntimeException("Netlist is invalid!");
		}
		// get Stage
		Stage stage = StageUtils.getStage(runEnv, CLArgString.ALGORITHMNAME);
		stage.setName("clustering");
		String stageName = runEnv.getOptionValue(CLArgString.STAGENAME);
		if (stageName != null) {
			stage.setName(stageName);
		}
		// get TargetData
		TargetData td = TargetDataUtils.getTargetTargetData(runEnv, CLArgString.TARGETDATAFILE);
		if (!td.isValid()) {
			throw new RuntimeException("TargetData is invalid!");
		}
		// NetlistConstraint
		NetlistConstraint netlistConstraint = NetlistConstraintUtils.getNetlistConstraintData(runEnv, CLArgString.NETLISTCONSTRAINTFILE);
		if (netlistConstraint == null) {
			netlistConstraint = new NetlistConstraint();
		}
		// Execute
		CLRuntimeObject CL = new CLRuntimeObject(stage, td, netlistConstraint, netlist, runEnv);
		CL.setName("clustering");
		CL.execute();
		// Write Netlist
		String outputFilename = runEnv.getOptionValue(CLArgString.OUTPUTNETLIST);
		if (outputFilename == null)
		{
			outputFilename = "";
			outputFilename += runEnv.getOptionValue(CLArgString.OUTPUTDIR);
			outputFilename += Utils.getFileSeparator();
			outputFilename += Utils.getFilename(inputFilePath);
			outputFilename += "_outputNetlist";
			outputFilename += ".json";
		}
		NetlistUtils.writeJSONForNetlist(netlist, outputFilename);
	}
	
	/**
	 *  Setup the logger using the CLRuntimeEnv defined by parameter <i>runEnv</i>
	 *
	 *  @param runEnv the CLRuntimeEnv
	 */
	protected static void setupLogger(CLRuntimeEnv runEnv) {
		String logfile = runEnv.getOptionValue(CLArgString.LOGFILENAME);
		if (logfile == null) {
			logfile = "log.log";
		}
		logfile = runEnv.getOptionValue(CLArgString.OUTPUTDIR) + Utils.getFileSeparator() + logfile;
		// the logger will write to the specified file
		System.setProperty("logfile.name", logfile);
		logger = LogManager.getLogger(Main.class);
	}

	/**
	 *  Returns the Logger for the <i>Main</i> class
	 *
	 *  @return the logger for the <i>Main</i> class
	 */
	static protected Logger getLogger() {
		return Main.logger;
	}
	
	private static Logger logger;
}

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
package clustering.runtime;

import java.io.File;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;

import common.Utils;

import results.netlist.Netlist;
import results.netlist.NetlistUtils;
import common.netlistConstraint.data.NetlistConstraint;
import common.netlistConstraint.data.NetlistConstraintUtils;
import common.stage.Stage;
import common.stage.StageUtils;
import common.target.data.TargetData;
import common.target.data.TargetDataUtils;
import clustering.runtime.environment.CLArgString;
import clustering.runtime.environment.CLRuntimeEnv;

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
		String[] path = {Utils.getResourcesFilepath(), "logger", "log4j2.xml"};
		// the logger will write to the specified file
		System.setProperty("logfile.name", logfile);
		LoggerContext context = (org.apache.logging.log4j.core.LoggerContext) LogManager.getContext(false);
		File file = new File(Utils.getPathFile(path));
		// this will force a reconfiguration
		context.setConfigLocation(file.toURI());
	}

	/**
	 *  Returns the Logger for the <i>Main</i> class
	 *
	 *  @return the logger for the <i>Main</i> class
	 */
	static protected Logger getLogger() {
		return Main.logger;
	}
	
	private static final Logger logger = LogManager.getLogger(Main.class.getSimpleName());
}

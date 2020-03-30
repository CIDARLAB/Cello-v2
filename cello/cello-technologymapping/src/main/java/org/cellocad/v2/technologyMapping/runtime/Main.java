/**
 * Copyright (C) 2017-2020
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
package org.cellocad.v2.technologyMapping.runtime;

import java.io.File;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cellocad.v2.common.CelloException;
import org.cellocad.v2.common.Utils;
import org.cellocad.v2.common.netlistConstraint.data.NetlistConstraint;
import org.cellocad.v2.common.netlistConstraint.data.NetlistConstraintUtils;
import org.cellocad.v2.common.runtime.environment.ArgString;
import org.cellocad.v2.common.stage.Stage;
import org.cellocad.v2.common.stage.StageUtils;
import org.cellocad.v2.common.stage.runtime.environment.StageArgString;
import org.cellocad.v2.common.target.data.TargetData;
import org.cellocad.v2.common.target.data.TargetDataUtils;
import org.cellocad.v2.results.common.Results;
import org.cellocad.v2.results.netlist.Netlist;
import org.cellocad.v2.results.netlist.NetlistUtils;
import org.cellocad.v2.technologyMapping.runtime.environment.TMRuntimeEnv;

/**
 * The Main class is the executable class for the <i>technologyMapping</i>
 * stage.
 *
 * @author Vincent Mirian
 * @author Timothy Jones
 *
 * @date 2018-05-21
 *
 */
public class Main {

	/**
	 * The <i>main</i> method is the executable for the <i>technologyMapping</i>
	 * stage.
	 *
	 * @param args command line argument(s)
	 */
	public static void main(String[] args) throws CelloException {
		TMRuntimeEnv runEnv = new TMRuntimeEnv(args);
		runEnv.setName("technologyMapping");
		// Setup Logger
		Main.setupLogger(runEnv);
		// InputFile
		String inputFilePath = runEnv.getOptionValue(ArgString.INPUTNETLIST);
		File inputFile = new File(inputFilePath);
		if (!(inputFile.exists() && !inputFile.isDirectory())) {
			throw new CelloException("Input file does not exist!");
		}
		// Read Netlist
		Netlist netlist = NetlistUtils.getNetlist(runEnv, ArgString.INPUTNETLIST);
		if (!netlist.isValid()) {
			throw new CelloException("Netlist is invalid!");
		}
		// get Stage
		Stage stage = StageUtils.getStage(runEnv, StageArgString.ALGORITHMNAME);
		stage.setName("technologyMapping");
		String stageName = runEnv.getOptionValue(StageArgString.STAGENAME);
		if (stageName != null) {
			stage.setName(stageName);
		}
		// get TargetData
		TargetData td = TargetDataUtils.getTargetTargetData(runEnv, ArgString.USERCONSTRAINTSFILE,
		        ArgString.INPUTSENSORFILE, ArgString.OUTPUTDEVICEFILE);
		if (!td.isValid()) {
			throw new CelloException("TargetData is invalid!");
		}
		// NetlistConstraint
		NetlistConstraint netlistConstraint = NetlistConstraintUtils.getNetlistConstraintData(runEnv,
		        ArgString.NETLISTCONSTRAINTFILE);
		if (netlistConstraint == null) {
			netlistConstraint = new NetlistConstraint();
		}
		// Results
		File outputDir = new File(runEnv.getOptionValue(ArgString.OUTPUTDIR));
		Results results = new Results(outputDir);
		// Execute
		TMRuntimeObject TM = new TMRuntimeObject(stage, td, netlistConstraint, netlist, results, runEnv);
		TM.setName("technologyMapping");
		TM.execute();
		// Write Netlist
		String outputFilename = runEnv.getOptionValue(ArgString.OUTPUTNETLIST);
		if (outputFilename == null) {
			outputFilename = "";
			outputFilename += runEnv.getOptionValue(ArgString.OUTPUTDIR);
			outputFilename += Utils.getFileSeparator();
			outputFilename += Utils.getFilename(inputFilePath);
			outputFilename += "_outputNetlist";
			outputFilename += ".json";
		}
		NetlistUtils.writeJSONForNetlist(netlist, outputFilename);
	}

	/**
	 * Setup the logger using the TMRuntimeEnv defined by parameter <i>runEnv</i>
	 *
	 * @param runEnv the TMRuntimeEnv
	 */
	protected static void setupLogger(TMRuntimeEnv runEnv) {
		String logfile = runEnv.getOptionValue(ArgString.LOGFILENAME);
		if (logfile == null) {
			logfile = "log.log";
		}
		logfile = runEnv.getOptionValue(ArgString.OUTPUTDIR) + Utils.getFileSeparator() + logfile;
		// the logger will write to the specified file
		System.setProperty("logfile.name", logfile);
		logger = LogManager.getLogger(Main.class);
	}

	/**
	 * Returns the Logger for the <i>Main</i> class
	 *
	 * @return the logger for the <i>Main</i> class
	 */
	static protected Logger getLogger() {
		return Main.logger;
	}

	private static Logger logger;

}

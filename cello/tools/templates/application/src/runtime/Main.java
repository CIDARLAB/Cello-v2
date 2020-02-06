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
package org.cellocad.cello2.##NONCE##21##APPNAME##21##NONCE.runtime;

import java.io.File;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;

import org.cellocad.cello2.##NONCE##21##APPNAME##21##NONCE.common.##NONCE##21##APPNAME##21##NONCEUtils;
import org.cellocad.cello2.##NONCE##21##APPNAME##21##NONCE.runtime.environment.##NONCE##21##APPNAME##21##NONCEArgString;
import org.cellocad.cello2.##NONCE##21##APPNAME##21##NONCE.runtime.environment.##NONCE##21##APPNAME##21##NONCERuntimeEnv;
import org.cellocad.cello2.common.Utils;
import org.cellocad.cello2.results.netlist.Netlist;
import org.cellocad.cello2.results.netlist.NetlistUtils;
import org.cellocad.cello2.common.stage.Stage;
import org.cellocad.cello2.common.application.ApplicationConfiguration;
import org.cellocad.cello2.common.application.ApplicationUtils;
import org.cellocad.cello2.common.netlistConstraint.data.NetlistConstraint;
import org.cellocad.cello2.common.netlistConstraint.data.NetlistConstraintUtils;
import org.cellocad.cello2.common.target.data.TargetData;
import org.cellocad.cello2.common.target.data.TargetDataUtils;

/**
 * The Main class is the executable class for the <i>##NONCE##21##APPNAME##21##NONCE</i> application.
 * 
 * @author ##NONCE##21##AUTHORNAME##21##NONCE
 * 
 * @date Today
 *
 */
public class Main {

	/**
	 * The <i>main</i> method is the executable for the <i>##NONCE##21##APPNAME##21##NONCE</i> application.
	 * 
	 * @param args command line argument(s)
	 */
	public static void main(String[] args) {
		/*
		 * Preparation
		 */
		// RuntimeEnv
		##NONCE##21##APPNAME##21##NONCERuntimeEnv runEnv = new ##NONCE##21##APPNAME##21##NONCERuntimeEnv(args);
		runEnv.setName("##NONCE##21##APPNAME##21##NONCE");
		if (!runEnv.isValid()) {
			throw new RuntimeException("##NONCE##21##APPNAME##21##NONCERuntimeEnv is invalid!");			
		}
		/*
		 * Setup Logger
		 */
		Main.setupLogger(runEnv);
		// Netlist
		Netlist netlist = new Netlist();
		// ApplicationConfiguration
		ApplicationConfiguration appCfg = ApplicationUtils.getApplicationConfiguration(runEnv, ##NONCE##21##APPNAME##21##NONCEArgString.OPTIONS, ##NONCE##21##APPNAME##21##NONCEUtils.getResourcesConfigurationFile());
		if (!appCfg.isValid()) {
			throw new RuntimeException("ApplicationConfiguration is invalid!");
		}
		// get TargetData
		TargetData td = TargetDataUtils.getTargetTargetData(runEnv, ##NONCE##21##APPNAME##21##NONCEArgString.TARGETDATAFILE);
		if (!td.isValid()) {
			throw new RuntimeException("TargetData is invalid!");
		}
		// NetlistConstraint
		NetlistConstraint netlistConstraint = NetlistConstraintUtils.getNetlistConstraintData(runEnv, ##NONCE##21##APPNAME##21##NONCEArgString.NETLISTCONSTRAINTFILE);
		if (netlistConstraint == null) {
			netlistConstraint = new NetlistConstraint();
		}
		/*
		 * Get InputFile from user
		 */
		// InputFile
		String inputFilePath = runEnv.getOptionValue(##NONCE##21##APPNAME##21##NONCEArgString.INPUTNETLIST);
		File inputFile = new File(inputFilePath);
		if (!(inputFile.exists() && !inputFile.isDirectory())) {
			throw new RuntimeException("Input file does not exist!");
		}
		// Input from User
		netlist.setInputFilename(inputFilePath);
		/*
		 * Stages
		 */
		Stage currentStage = null;
		/*
		 * Add Stages below
		 */
		/*
		 * Add Stages above
		 */
		/*
		 * Write netlist
		 */
		// Write netlist
		String outputNetlistFilePath = null;
		outputNetlistFilePath = runEnv.getOptionValue(##NONCE##21##APPNAME##21##NONCEArgString.OUTPUTNETLIST);
		if (outputNetlistFilePath == null)
		{
			outputNetlistFilePath = "";
			outputNetlistFilePath += runEnv.getOptionValue(##NONCE##21##APPNAME##21##NONCEArgString.OUTPUTDIR);
			outputNetlistFilePath += Utils.getFileSeparator();
			outputNetlistFilePath += Utils.getFilename(inputFilePath);
			outputNetlistFilePath += "_outputNetlist";
			outputNetlistFilePath += ".json";
		}
		NetlistUtils.writeJSONForNetlist(netlist, outputNetlistFilePath);
	}

	/**
	 *  Setup the logger using the ##NONCE##21##APPNAME##21##NONCERuntimeEnv defined by parameter <i>runEnv</i>
	 *
	 *  @param runEnv the ##NONCE##21##APPNAME##21##NONCERuntimeEnv
	 */
	protected static void setupLogger(##NONCE##21##APPNAME##21##NONCERuntimeEnv runEnv) {
		String logfile = runEnv.getOptionValue(##NONCE##21##APPNAME##21##NONCEArgString.LOGFILENAME);
		if (logfile == null) {
			logfile = "log.log";
		}
		logfile = runEnv.getOptionValue(##NONCE##21##APPNAME##21##NONCEArgString.OUTPUTDIR) + Utils.getFileSeparator() + logfile;
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
	
	private static final Logger logger = LogManager.getLogger(Main.class);
}

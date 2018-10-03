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
package DNACompiler.runtime;
import export.runtime.EXRuntimeObject;
import clustering.runtime.CLRuntimeObject;
import logicOptimization.runtime.LORuntimeObject;
import technologyMapping.runtime.TMRuntimeObject;
import partitioning.runtime.PTRuntimeObject;
import placing.runtime.PLRuntimeObject;
import logicSynthesis.runtime.LSRuntimeObject;

import java.io.File;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;

import DNACompiler.common.DNACompilerUtils;
import DNACompiler.runtime.environment.DNACompilerArgString;
import DNACompiler.runtime.environment.DNACompilerRuntimeEnv;
import common.Utils;
import results.logicOptimization.LOResultsStats;
import results.logicSynthesis.LSResultsStats;
import results.netlist.Netlist;
import results.netlist.NetlistUtils;
import results.partitioning.PTResultsStats;
import results.partitioning.block.PTBlockNetlist;
import common.stage.Stage;
import common.application.ApplicationConfiguration;
import common.application.ApplicationUtils;
import common.netlistConstraint.data.NetlistConstraint;
import common.netlistConstraint.data.NetlistConstraintUtils;
import common.target.data.TargetData;
import common.target.data.TargetDataUtils;

/**
 * The Main class is the executable class for the <i>DNACompiler</i> application.
 * 
 * @author Vincent Mirian
 * 
 * @date 2018-05-21
 *
 */
public class Main {

	/**
	 * The <i>printPartitioningGraphs</i> prints the partitioning graph of netlist defined by parameter <i>myNetlist</i>
	 * using the DNACompilerRuntimeEnv defined by parameter <i>runEnv</i>.
	 * 
	 * @param runEnv the DNACompilerRuntimeEnv
	 * @param myNetlist the Netlist
	 */
	protected static void printPartitioningGraphs(DNACompilerRuntimeEnv runEnv, Netlist myNetlist) {
		String outputDir = runEnv.getOptionValue(DNACompilerArgString.OUTPUTDIR) + Utils.getFileSeparator();
		PTBlockNetlist ptBlockNetlist = new PTBlockNetlist(myNetlist);
		Netlist netlist = null;
		for (int i = 0; i < ptBlockNetlist.getNetlistFONum();i++) {
			netlist = ptBlockNetlist.getNetlistFOAtIdx(i);
			NetlistUtils.writeDotFileForGraph(netlist, outputDir+netlist.getName()+".dot");
		}
		// TODO:
		//netlist = ptBlockNetlist.getClusterRepeatedEdgesNetlist();
		//NetlistUtils.writeDotFileForGraph(netlist, outputDir+netlist.getName()+".dot");
		//netlist = ptBlockNetlist.getClusterNetlist();
		//NetlistUtils.writeDotFileForGraph(netlist, outputDir+netlist.getName()+".dot");
		netlist = ptBlockNetlist.getVirtualLargeNetlistFO();
		NetlistUtils.writeDotFileForGraph(netlist, outputDir+netlist.getName()+".dot");
	}
	
	/**
	 * The <i>main</i> method is the executable for the <i>DNACompiler</i> application.
	 * 
	 * @param args command line argument(s)
	 */
	public static void main(String[] args) {
		/*
		 * Preparation
		 */
		// RuntimeEnv
		DNACompilerRuntimeEnv runEnv = new DNACompilerRuntimeEnv(args);
		runEnv.setName("DNACompiler");
		if (!runEnv.isValid()) {
			throw new RuntimeException("DNACompilerRuntimeEnv is invalid!");			
		}
		/*
		 * Setup Logger
		 */
		Main.setupLogger(runEnv);
		/*
		 * Other
		 */
		// Netlist
		Netlist netlist = new Netlist();
		// ApplicationConfiguration
		ApplicationConfiguration appCfg = ApplicationUtils.getApplicationConfiguration(runEnv, DNACompilerArgString.OPTIONS, DNACompilerUtils.getResourcesConfigurationFile());
		if (!appCfg.isValid()) {
			throw new RuntimeException("ApplicationConfiguration is invalid!");
		}
		// get TargetData
		TargetData td = TargetDataUtils.getTargetTargetData(runEnv, DNACompilerArgString.TARGETDATAFILE);
		if (!td.isValid()) {
			throw new RuntimeException("TargetData is invalid!");
		}
		// NetlistConstraint
		NetlistConstraint netlistConstraint = NetlistConstraintUtils.getNetlistConstraintData(runEnv, DNACompilerArgString.NETLISTCONSTRAINTFILE);
		if (netlistConstraint == null) {
			netlistConstraint = new NetlistConstraint();
		}
		/*
		 * Get InputFile from user
		 */
		// InputFile
		String inputFilePath = runEnv.getOptionValue(DNACompilerArgString.INPUTNETLIST);
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
		String outputDir = runEnv.getOptionValue(DNACompilerArgString.OUTPUTDIR) + Utils.getFileSeparator();
		/*
		 * Add Stages below
		 */
		// logicSynthesis
		currentStage = appCfg.getStageByName("logicSynthesis");
		LSRuntimeObject LS = new LSRuntimeObject(currentStage, td, netlistConstraint, netlist, runEnv);
		LS.execute();
		NetlistUtils.writeDotFileForGraph(netlist, outputDir + netlist.getName()+ "_logicSynthesis" + ".dot");
		Main.getLogger().info(LSResultsStats.getLogicSynthesisStats(netlist));
		// logicOptimization
		currentStage = appCfg.getStageByName("logicOptimization");
		LORuntimeObject LO = new LORuntimeObject(currentStage, td, netlistConstraint, netlist, runEnv);
		//LO.execute();
		//NetlistUtils.writeDotFileForGraph(netlist, outputDir + netlist.getName()+ "_logicOptimization" + ".dot");
		//Main.getLogger().info(LOResultsStats.getLogicOptimizationStats(netlist));
		// clustering
		currentStage = appCfg.getStageByName("clustering");
		CLRuntimeObject CL = new CLRuntimeObject(currentStage, td, netlistConstraint, netlist, runEnv);
		CL.execute();
		// partitioning
		currentStage = appCfg.getStageByName("partitioning");
		PTRuntimeObject PT = new PTRuntimeObject(currentStage, td, netlistConstraint, netlist, runEnv);
		PT.execute();
		Main.printPartitioningGraphs(runEnv, netlist);
		Main.getLogger().info(PTResultsStats.getPartitioningStats(netlist));
		netlist = new PTBlockNetlist(netlist).getVirtualLargeNetlistFO();
		// technologyMapping
		currentStage = appCfg.getStageByName("technologyMapping");
		TMRuntimeObject TM = new TMRuntimeObject(currentStage, td, netlistConstraint, netlist, runEnv);
		TM.execute();
		// placing
		currentStage = appCfg.getStageByName("placing");
		PLRuntimeObject PL = new PLRuntimeObject(currentStage, td, netlistConstraint, netlist, runEnv);
		PL.execute();
		// export
		currentStage = appCfg.getStageByName("export");
		EXRuntimeObject EX = new EXRuntimeObject(currentStage, td, netlistConstraint, netlist, runEnv);
		//EX.execute();
		/*
		 * Add Stages above
		 */
		/*
		 * Write netlist
		 */
		// Write netlist
		String outputNetlistFilePath = null;
		outputNetlistFilePath = runEnv.getOptionValue(DNACompilerArgString.OUTPUTNETLIST);
		if (outputNetlistFilePath == null)
		{
			outputNetlistFilePath = "";
			outputNetlistFilePath += runEnv.getOptionValue(DNACompilerArgString.OUTPUTDIR);
			outputNetlistFilePath += Utils.getFileSeparator();
			outputNetlistFilePath += Utils.getFilename(inputFilePath);
			outputNetlistFilePath += "_outputNetlist";
			outputNetlistFilePath += ".json";
		}
		NetlistUtils.writeJSONForNetlist(netlist, outputNetlistFilePath);
	}

	/**
	 *  Setup the logger using the DNACompilerRuntimeEnv defined by parameter <i>runEnv</i>
	 *
	 *  @param runEnv the DNACompilerRuntimeEnv
	 */
	protected static void setupLogger(DNACompilerRuntimeEnv runEnv) {
		String logfile = runEnv.getOptionValue(DNACompilerArgString.LOGFILENAME);
		if (logfile == null) {
			logfile = "log.log";
		}
		logfile = runEnv.getOptionValue(DNACompilerArgString.OUTPUTDIR) + Utils.getFileSeparator() + logfile;
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

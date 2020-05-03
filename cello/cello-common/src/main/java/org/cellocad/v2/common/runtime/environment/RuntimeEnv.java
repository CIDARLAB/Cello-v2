/*
 * Copyright (C) 2020 Massachusetts Institute of Technology (MIT), Boston University (BU)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package org.cellocad.v2.common.runtime.environment;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.cellocad.v2.common.CObject;
import org.cellocad.v2.common.Utils;

/**
 * The RuntimeEnv class is a class for managing and parsing command line argument(s) for a stage and
 * an application.
 *
 * @author Vincent Mirian
 * @author Timothy Jones
 *
 * @date Nov 13, 2017
 */
public class RuntimeEnv extends CObject {

  private void init() {
    parser = new DefaultParser();
    options = new Options();
  }

  /**
   * Initializes a newly created {@link RuntimeEnv} with command line argument(s), <i>args</i>.
   *
   * @param args command line argument(s).
   */
  public RuntimeEnv(final String[] args) {
    super();
    init();
    setOptions();
    try {
      line = parser.parse(getOptions(), args);
    } catch (final ParseException e) {
      // oops, something went wrong
      System.err.println("Parsing failed.  Reason: " + e.getMessage());
    }
    if (this.hasOption(ArgString.HELP)) {
      printHelp();
      Utils.exit(0);
    }
  }

  // print help
  /**
   * Print the help message of this instance.
   */
  public void printHelp() {
    final HelpFormatter formatter = new HelpFormatter();
    String name = getName();
    if (name.isEmpty()) {
      name = "\"EXECUTABLE\"";
    }
    formatter.printHelp(name, getOptions(), true);
  }

  // getter and setter
  /**
   * Getter for {@code options}.
   *
   * @return The options for this instance.
   */
  protected Options getOptions() {
    return options;
  }

  /**
   * Setter for {@code options}.
   */
  protected void setOptions() {
    final Options options = getOptions();
    options.addOption(getHelpOption());
    options.addOption(getInputSensorFileOption());
    options.addOption(getOutputDeviceFileOption());
    options.addOption(getUserConstraintsFileOption());
    options.addOption(getOptionsOption());
    options.addOption(getOutputDirOption());
    options.addOption(getPythonEnvOption());
    options.addOption(getInputNetlistOption());
    options.addOption(getOutputNetlistOption());
    options.addOption(getNetlistConstraintFileOption());
    options.addOption(getLogFilenameFileOption());
  }

  /*
   * Options
   */
  private Option getHelpOption() {
    final Option rtn = new Option(ArgString.HELP, false, ArgDescription.HELP_DESCRIPTION);
    return rtn;
  }

  /**
   * Getter for the INPUTSENSORFILE Option. This option is required.
   *
   * @return The INPUTSENSORFILE Option for this instance.
   */
  protected Option getInputSensorFileOption() {
    final Option rtn =
        new Option(ArgString.INPUTSENSORFILE, true, ArgDescription.INPUTSENSORFILE_DESCRIPTION);
    makeRequired(rtn);
    return rtn;
  }

  /**
   * Getter for the OUTPUTDEVICEFILE Option. This option is required.
   *
   * @return The OUTPUTDEVICEFILE Option for this instance.
   */
  protected Option getOutputDeviceFileOption() {
    final Option rtn =
        new Option(ArgString.OUTPUTDEVICEFILE, true, ArgDescription.OUTPUTDEVICEFILE_DESCRIPTION);
    makeRequired(rtn);
    return rtn;
  }

  /**
   * Getter for the USERCONSTRAINTSFILE Option. This option is required.
   *
   * @return The USERCONSTRAINTSFILE Option for this instance.
   */
  protected Option getUserConstraintsFileOption() {
    final Option rtn = new Option(ArgString.USERCONSTRAINTSFILE, true,
        ArgDescription.USERCONSTRAINTSFILE_DESCRIPTION);
    makeRequired(rtn);
    return rtn;
  }

  /**
   * Getter for the OPTIONS Option.
   *
   * @return The OPTIONS Option for this instance.
   */
  private Option getOptionsOption() {
    final Option rtn = new Option(ArgString.OPTIONS, true, ArgDescription.OPTIONS_DESCRIPTION);
    return rtn;
  }

  /**
   * Getter for the OUTPUTDIR Option.
   *
   * @return The OUTPUTDIR Option for this instance.
   */
  protected Option getOutputDirOption() {
    final Option rtn = new Option(ArgString.OUTPUTDIR, true, ArgDescription.OUTPUTDIR_DESCRIPTION);
    return rtn;
  }

  /**
   * Getter for the PYTHONENV Option.
   *
   * @return The PYTHONENV Option for this instance.
   */
  protected Option getPythonEnvOption() {
    final Option rtn = new Option(ArgString.PYTHONENV, true, ArgDescription.PYTHONENV_DESCRIPTION);
    return rtn;
  }

  /**
   * Getter for the INPUTNETLIST Option. This option is required.
   *
   * @return The INPUTNETLIST Option for this instance.
   */
  protected Option getInputNetlistOption() {
    final Option rtn =
        new Option(ArgString.INPUTNETLIST, true, ArgDescription.INPUTNETLIST_DESCRIPTION);
    makeRequired(rtn);
    return rtn;
  }

  /**
   * Getter for the OUTPUTNETLIST Option.
   *
   * @return The OUTPUTNETLIST Option for this instance.
   */
  protected Option getOutputNetlistOption() {
    final Option rtn =
        new Option(ArgString.OUTPUTNETLIST, true, ArgDescription.OUTPUTNETLIST_DESCRIPTION);
    return rtn;
  }

  /**
   * Getter for the NETLISTCONSTRAINTFILE Option.
   *
   * @return The NETLISTCONSTRAINTFILE Option for this instance.
   */
  protected Option getNetlistConstraintFileOption() {
    final Option rtn = new Option(ArgString.NETLISTCONSTRAINTFILE, true,
        ArgDescription.NETLISTCONSTRAINTFILE_DESCRIPTION);
    return rtn;
  }

  /**
   * Getter for the LOGFILENAME Option.
   *
   * @return The LOGFILENAME Option for this instance.
   */
  protected Option getLogFilenameFileOption() {
    final Option rtn =
        new Option(ArgString.LOGFILENAME, true, ArgDescription.LOGFILENAME_DESCRIPTION);
    return rtn;
  }

  /**
   * Returns the default value for string <i>str</i> that references a command line argument.
   *
   * @param str The command line argument.
   * @return The string default value for string <i>str</i> that references a command line argument
   *         if command line argument exists, otherwise null.
   */
  // get Values
  protected String getDefault(final String str) {
    String rtn = null;
    switch (str) {
      case ArgString.OUTPUTDIR:
        rtn = "";
        rtn += Utils.getWorkingDirectory();
        break;
      case ArgString.PYTHONENV:
        rtn = "";
        break;
      default:
        break;
    }
    return rtn;
  }

  /**
   * Returns the value for string <i>str</i> that references a command line argument.
   *
   * @param str The command line argument.
   * @return The string value for string <i>str</i> that references a command line argument.
   */
  public String getOptionValue(final String str) {
    String rtn = null;
    rtn = line.getOptionValue(str);
    if (rtn == null) {
      rtn = getDefault(str);
    }
    return rtn;
  }

  /**
   * Returns the value for char <i>c</i> that references a command line argument.
   *
   * @param c The command line argument.
   * @return The string value for char <i>c</i> that references a command line argument.
   */
  public String getOptionValue(final char c) {
    String rtn = null;
    rtn = line.getOptionValue(c);
    return rtn;
  }

  /**
   * Returns a boolean flag signifying the string <i>str</i> that references a command line argument
   * is present in this instance.
   *
   * @param str The command line argument.
   * @return True if the string <i>str</i> that references a command line argument is present,
   *         otherwise false.
   */
  public boolean hasOption(final String str) {
    boolean rtn = false;
    rtn = line.hasOption(str);
    return rtn;
  }

  /**
   * Returns a boolean flag signifying the char <i>c</i> that references a command line argument is
   * present in this instance.
   *
   * @param c The command line argument.
   * @return True if the char <i>c</i> that references a command line argument is present, otherwise
   *         false.
   */
  public boolean hasOption(final char c) {
    boolean rtn = false;
    rtn = line.hasOption(c);
    return rtn;
  }

  /**
   * Enables the Option <i>arg</i> to be a required command line argument.
   *
   * @param arg The command line argument.
   */
  protected void makeRequired(final Option arg) {
    arg.setRequired(true);
  }

  /**
   * Enables the Option <i>arg</i> to <b>not</b> be a required command line argument.
   *
   * @param arg The command line argument.
   */
  protected void makeNotRequired(final Option arg) {
    arg.setRequired(false);
  }

  private CommandLineParser parser;
  private CommandLine line;
  private Options options;

}

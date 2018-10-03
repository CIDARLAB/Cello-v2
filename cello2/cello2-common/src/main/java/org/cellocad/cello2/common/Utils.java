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
package org.cellocad.cello2.common;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Date;
import java.util.Random;

/**
 * The Utils class is class with utility methods for the Poros framework.
 * @author Vincent Mirian
 * 
 * @date Oct 28, 2017
 *
 */
final public class Utils {

	/**
	 * Returns the path of the ClassLoader
	 * 
	 * @return the path of the ClassLoader
	 *
	 */
	static public String getFilepath(){
		String rtn = "";
		rtn = Utils.class.getClassLoader().getResource(".").getPath();
		return rtn;
	}

	/**
	 * Returns the path of the Resources directory
	 * 
	 * @return the path of the Resources directory
	 *
	 */
	static public String getResourcesFilepath(){
		String rtn = "";
		rtn += Utils.getFilepath();
		rtn += "resources-";
		rtn += "common";
		return rtn;
	}

	/**
	 * Returns the filename of the path in parameter <i>name<i>
	 * 
	 * @return the filename of the path in parameter <i>name<i>
	 *
	 */
	static public String getFilename(final String name){
		String rtn = name;
		int index = 0;
		index = rtn.lastIndexOf(Utils.getFileSeparator());
		if (index != -1){
			rtn = rtn.substring(index + 1);
		}
		index = rtn.lastIndexOf(".");
		if ((index != -1) && (rtn.length() > 1)){
			rtn = rtn.substring(0, index);
		}
		return rtn;
	}

	/**
	 * Create the file referenced by <i>filename<i>
	 * 
	 * @return true if the file was created by this method; false if the file could not be created because the named file already exists
	 *
	 */
	static public boolean createFile (String filename) {
		boolean rtn = false;
		try {
		     File file = new File(filename);
		     rtn = file.createNewFile();
		}
		catch (IOException e) {
    		e.printStackTrace();
		}
		return rtn;
	}

	/**
	 * Append String referenced by <i>str</i> to file referenced by <i>filename<i>
	 */	
	static public void appendToFile(String str, String filename) {
		try {
			OutputStream outputStream = new FileOutputStream(filename, true);
			Writer outputStreamWriter = new OutputStreamWriter(outputStream);
			outputStreamWriter.write(str);
			outputStreamWriter.close();
			outputStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Write String referenced by <i>str</i> to file referenced by <i>filename<i>
	 */	
	static public void writeToFile(String str, String filename) {
		try {
			OutputStream outputStream = new FileOutputStream(filename);
			Writer outputStreamWriter = new OutputStreamWriter(outputStream);
			outputStreamWriter.write(str);
			outputStreamWriter.close();
			outputStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Delete the file referenced by <i>name<i>
	 * 
	 * @return true if the file was deleted by this method; false if the file could not be deleted because it did not exist.
	 *
	 */
	static public boolean deleteFilename(final String name){
		boolean rtn = false;
		File file = new File(name);
		rtn = file.delete();
		return rtn;
	}
	
	/**
	 * Returns a boolean flag signifying the Operating System being Mac-based. 
	 * 
	 * @return true if the Operating System is Mac-based, otherwise false.
	 *
	 */
	public static boolean isMac() {
		return (Utils.isMac(Utils.getOS()));
	}
	
	/**
	 * Returns a boolean flag signifying the Operating System being Unix-based. 
	 * 
	 * @return true if the Operating System is Unix-based, otherwise false.
	 *
	 */
	public static boolean isUnix() {
		return (Utils.isUnix(Utils.getOS()));
	}
	
	/**
	 * Returns a string containing the operating system name 
	 * 
	 * @return a string containing the operating system name 
	 *
	 */
	private static String getOS() {
		return System.getProperty("os.name");
	}

	private static boolean isMac(final String OS) {
		return (OS.toLowerCase().indexOf("mac") >= 0);
	}

	private static boolean isUnix(final String OS) {
		return (OS.indexOf("nix") >= 0 || OS.indexOf("nux") >= 0 || OS.indexOf("aix") > 0 );
	}
	
	/**
	 * Executes and waits for the command in parameter <i>cmd</i>.
	 * Returns the ExecCommand instance that executed the command.
	 * 
	 * @param cmd the command
	 * @return the ExecCommand instance that executed the command.
	 *
	 */
	public static ExecCommand executeAndWaitForCommand(final String cmd){
		ExecCommand rtn = null;
		rtn = new ExecCommand(cmd);
		return rtn;
	}

	/**
	 * Returns a string representing the number of milliseconds since January 1, 1970, 00:00:00 GMT.
	 * 
	 * @return a string representing the number of milliseconds since January 1, 1970, 00:00:00 GMT.
	 *
	 */
	static public String getTimeString(){
		String rtn;
        Date date = new Date();
        rtn = String.valueOf(date.getTime());
		return rtn;
	}

	/**
	 * Returns a string representing the working directory
	 * 
	 * @return a string representing the working directory
	 *
	 */
	static public String getWorkingDirectory() {
		String rtn = System.getProperty("user.dir").toString();
		return rtn;
	}
	
	/**
	 * Returns a string representing the file separator on the system
	 * 
	 * @return a string representing the file separator on the system
	 *
	 */
	static public String getFileSeparator() {
		String rtn = System.getProperty("file.separator").toString();
		return rtn;
	}
	
	/**
	 * Returns a string representing the elements in the parameter <i>folders</i> separated by the File Separator of the System
	 * 
	 * @return a string representing the elements in the parameter <i>folders</i> separated by the File Separator of the System
	 *
	 */
	static public String getPath(String[] folders) {
		String rtn = "";
		for (int i = 0; i < folders.length; i++) {
			rtn += folders[i];
			rtn += Utils.getFileSeparator();
		}
		return rtn;
	}
	
	/**
	 * Returns a string representing the elements in the parameter <i>folders</i> separated by the File Separator of the System omitting the last File Separator
	 * 
	 * @return a string representing the elements in the parameter <i>folders</i> separated by the File Separator of the System omitting the last File Separator
	 *
	 */
	static public String getPathFile(String[] folders) {
		String rtn = "";
		rtn = Utils.getPath(folders);
		if (rtn.length() > 0){
			int length = rtn.length();
			String lastChar = rtn.substring(length-1, length);
			if (lastChar.contentEquals(Utils.getFileSeparator())) {
				rtn = rtn.substring(0, length -1);
			}
		}
		return rtn;
	}
	
	/**
	 * Returns a string representing the line separator on the system
	 * 
	 * @return a string representing the line separator on the system
	 *
	 */
	static public String getNewLine() {
		String rtn = System.getProperty("line.separator").toString();
		return rtn;
	}
	
	/**
	 * Returns a string representing the parameter <i>str</i> indented with parameter <i>numIndent</i> tabulator character(s).
	 * 
	 * @param numIndent number of indentation(s)
	 * @param str the string to indent
	 * @return a string representing the parameter <i>str</i> indented with parameter <i>numIndent</i> tabulator character(s).
	 *
	 */
	static public String addIndent(int numIndent, final String str) {
		String rtn = "";
		String replace = "";
		String numTab = Utils.getTabCharacterRepeat(numIndent);
		replace = Utils.getNewLine() + numTab;
		if (!str.isEmpty()) {
			rtn = numTab + str.replace(Utils.getNewLine(), replace);
		}
		return rtn;		
	}
	
	/**
	 * Returns a string representing the tabulator character.
	 * 
	 * @return a string representing the tabulator character.
	 *
	 */
	static public String getTabCharacter() {
		String rtn = "\t";
		return rtn;
	}
	
	/**
	 * Returns a string representing the tabulator character repeated <i>num</i> number of times.
	 * 
	 * @return a string representing the tabulator character repeated <i>num</i> number of times.
	 *
	 */
	static public String getTabCharacterRepeat(int num) {
		String rtn = "";
		String tab = Utils.getTabCharacter();
		for (int i = 0; i < num; i++) {
			rtn = rtn + tab;	
		}		
		return rtn;
	}
	
	/**
	 * Returns a boolean flag signifying that the parameter <i>Obj</i> is an instance of the Boolean class.
	 * 
	 * @return true if Obj is an instance of the Boolean class, otherwise false.
	 *
	 */
	static public boolean isBoolean(final Object Obj) {
		boolean rtn = false;
		rtn = (Obj instanceof Boolean);
		return rtn;
	}

	/**
	 * Returns a boolean flag signifying that the parameter <i>Obj</i> is an instance of the Long class.
	 * 
	 * @return true if Obj is an instance of the Long class, otherwise false.
	 *
	 */
	static public boolean isLong(final Object Obj) {
		boolean rtn = false;
		rtn = (Obj instanceof Long);
		return rtn;
	}

	/**
	 * Returns a boolean flag signifying that the parameter <i>Obj</i> is an instance of the Double class.
	 * 
	 * @return true if Obj is an instance of the Double class, otherwise false.
	 *
	 */
	static public boolean isDouble(final Object Obj) {
		boolean rtn = false;
		rtn = (Obj instanceof Double);
		return rtn;
	}

	/**
	 * Returns a boolean flag signifying that the parameter <i>Obj</i> is an instance of the String class.
	 * 
	 * @return true if Obj is an instance of the String class, otherwise false.
	 *
	 */
	static public boolean isString(final Object Obj) {
		boolean rtn = false;
		rtn = (Obj instanceof String);
		return rtn;
	}

	/**
	 * Returns a boolean flag signifying that the parameter <i>cObj</i> is null.
	 * 
	 * @param cObj Object reference to check
	 * @param name of the cObj, used in RuntimeException print statement
	 * @return true if cObj is null, otherwise false.
	 * @throws RuntimeException if cObj is null.
	 *
	 */
	static public boolean isNullRuntimeException(final Object cObj, String name) {
		boolean rtn = false;
		rtn = (cObj == null);
		if (rtn) {
			throw new RuntimeException(name + " cannot be null!");
		}
		return rtn;
	}


	/**
	 * Returns a Boolean instance where the value is retrieved from the content of parameter <i>str</i>.
	 * 
	 * @param str string where its content is the value
	 * @return null if str is not a valid Boolean value, otherwise an instance with the value equivalent to the parameter <i>str</i> content.
	 *
	 */
	static public Boolean getBoolean(final String str) {
		Boolean rtn = null;
		if (str.equalsIgnoreCase("true")) {
			rtn = new Boolean(true);
		}
		if (str.equalsIgnoreCase("false")) {
			rtn = new Boolean(false);
		}
		return rtn;
	}

	/**
	 * Returns a Byte instance where the value is retrieved from the content of parameter <i>str</i>.
	 * 
	 * @param str string where its content is the value
	 * @return null if str is not a valid Byte value, otherwise an instance with the value equivalent to the parameter <i>str</i> content.
	 *
	 */
	static public Byte getByte(final String str) {
		Byte rtn = null;
		try {
			rtn = Byte.valueOf(str);
		}
		catch(NumberFormatException e) {
			rtn = null;
		}
		return rtn;
	}
	/**
	 * Returns a Character instance where the value is retrieved from the content of parameter <i>str</i>.
	 * 
	 * @param str string where its content is the value
	 * @return null if str is not a valid Character value, otherwise an instance with the value equivalent to the parameter <i>str</i> content.
	 *
	 */
	static public Character getCharacter(final String str) {
		Character rtn = null;
		if (str != null & (str.length() > 0)) {
			rtn = str.charAt(0);
		}
		return rtn;
	}

	/**
	 * Returns a Short instance where the value is retrieved from the content of parameter <i>str</i>.
	 * 
	 * @param str string where its content is the value
	 * @return null if str is not a valid Short value, otherwise an instance with the value equivalent to the parameter <i>str</i> content.
	 *
	 */
	static public Short getShort(final String str) {
		Short rtn = null;
		try {
			rtn = Short.valueOf(str);
		}
		catch(NumberFormatException e) {
			rtn = null;
		}
		return rtn;
	}

	/**
	 * Returns a Integer instance where the value is retrieved from the content of parameter <i>str</i>.
	 * 
	 * @param str string where its content is the value
	 * @return null if str is not a valid Integer value, otherwise an instance with the value equivalent to the parameter <i>str</i> content.
	 *
	 */
	static public Integer getInteger(final String str) {
		Integer rtn = null;
		try {
			rtn = Integer.valueOf(str);
		}
		catch(NumberFormatException e) {
			rtn = null;
		}
		return rtn;
	}

	/**
	 * Returns a Long instance where the value is retrieved from the content of parameter <i>str</i>.
	 * 
	 * @param str string where its content is the value
	 * @return null if str is not a valid Long value, otherwise an instance with the value equivalent to the parameter <i>str</i> content.
	 *
	 */
	static public Long getLong(final String str) {
		Long rtn = null;
		try {
			rtn = Long.valueOf(str);
		}
		catch(NumberFormatException e) {
			rtn = null;
		}
		return rtn;
	}

	/**
	 * Returns a Float instance where the value is retrieved from the content of parameter <i>str</i>.
	 * 
	 * @param str string where its content is the value
	 * @return null if str is not a valid Float value, otherwise an instance with the value equivalent to the parameter <i>str</i> content.
	 *
	 */	
	static public Float getFloat(final String str) {
		Float rtn = null;
		try {
			rtn = Float.valueOf(str);
		}
		catch(NumberFormatException | NullPointerException e) {
			rtn = null;
		}
		return rtn;
	}

	/**
	 * Returns a Double instance where the value is retrieved from the content of parameter <i>str</i>.
	 * 
	 * @param str string where its content is the value
	 * @return null if str is not a valid Double value, otherwise an instance with the value equivalent to the parameter <i>str</i> content.
	 *
	 */	
	static public Double getDouble(final String str) {
		Double rtn = null;
		try {
			rtn = Double.valueOf(str);
		}
		catch(NumberFormatException e) {
			rtn = null;
		}
		return rtn;
	}

	/**
	 * Returns a String instance where the value is retrieved from the content of parameter <i>str</i>.
	 * 
	 * @param str string where its content is the value
	 * @return null if str is not a valid String value, otherwise an instance with the value equivalent to the parameter <i>str</i> content.
	 *
	 */	
	static public String getString(final String str) {
		String rtn = null;
		rtn = str;
		return rtn;
	}
	
	/**
	 * Exits the current executable with exit code equivalent to parameter <i>exit</i>.
	 * 
	 * @param exit exit code
	 *
	 */
	static public void exit(int exit) {
		System.exit(exit);
	}

	/**
	 * Returns a random integer value between a minimum value defined by parameter <i>min</i> and
	 * a maximum value defined by parameter <i>max</i>
	 * 
	 * @param min minimum value
	 * @param max maximum value
	 * @return a random integer value between a minimum value defined by parameter <i>min</i> and a maximum value defined by parameter <i>max</i>
	 *
	 */
	static public int random (int min, int max) {
		int rtn = 0;
		Random random = new Random();
		rtn = random.nextInt(max - min + 1) + min;
		return rtn;
	}
	
	/**
	 * Returns a random integer value between a minimum value defined by parameter <i>min</i> and
	 * a maximum value defined by parameter <i>max</i> using the seed defined by parameter <i>seed</i>
	 * 
	 * @param min minimum value
	 * @param max maximum value
	 * @param seed the seed
	 * @return a random integer value between a minimum value defined by parameter <i>min</i> and a maximum value defined by parameter <i>max</i> using the seed defined by parameter <i>seed</i>
	 *
	 */
	static public int random (int min, int max, long seed) {
		int rtn = 0;
		Random random = new Random(seed);
		rtn = random.nextInt(max - min + 1) + min;
		return rtn;
	}

}

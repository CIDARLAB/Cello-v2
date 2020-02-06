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
package org.cellocad.v2.common;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.concurrent.Semaphore;

/**
 * The ExecCommand class is the class for executing a command using a process within the Poros framework.<br>
 * Reference: <i>https://bjurr.com/runtime-exec-hangs-a-complete-solution/</i>.
 *  
 * @author Vincent Mirian
 * 
 * @date Jan 21, 2018
 *
 */
public class ExecCommand {
	private Semaphore outputSem;
	private String output;
	private Semaphore errorSem;
	private String error;
	private Process p;
	
	private class InputWriter extends Thread {
		private String input;
		
		public InputWriter(String input) {
			this.input = input;
		}
		
		public void run() {
			PrintWriter pw = new PrintWriter(p.getOutputStream());
			pw.println(input);
			pw.flush();
		}
	}
	
	private class OutputReader extends Thread {
		public OutputReader() {
			try {
				outputSem = new Semaphore(1);
				outputSem.acquire();
			}
			catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		public void run() {
			try {
				StringBuffer readBuffer = new StringBuffer();
				BufferedReader isr = new BufferedReader(new InputStreamReader(p
				.getInputStream()));
				String buff = new String();
				while ((buff = isr.readLine()) != null) {
					readBuffer.append(buff + Utils.getNewLine());
					//System.out.println(buff);
				}
				output = readBuffer.toString();
				outputSem.release();
			}
			catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	private class ErrorReader extends Thread {
		public ErrorReader() {
			try {
				errorSem = new Semaphore(1);
				errorSem.acquire();
			}
			catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	
		public void run() {
			try {
				StringBuffer readBuffer = new StringBuffer();
				BufferedReader isr = new BufferedReader(new InputStreamReader(p
				.getErrorStream()));
				String buff = new String();
				while ((buff = isr.readLine()) != null) {
					readBuffer.append(buff + Utils.getNewLine());
				}
				error = readBuffer.toString();
				errorSem.release();
			}
			catch (IOException e) {
				e.printStackTrace();
			}
			/*if (error.length() > 0)
					System.out.println(error);*/
		}
	}
	
	/**
	 *  Initializes a newly created ExecCommand with a command defined by parameter <i>command</i>
	 *  and an input defined by parameter <i>input</i>
	 *  
	 *  @param command the command
	 *  @param input the input
	 */
	public ExecCommand(String command, String input) {
		try {
			p = Runtime.getRuntime().exec(makeArray(command));
			new InputWriter(input).start();
			new OutputReader().start();
			new ErrorReader().start();
			p.waitFor();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 *  Initializes a newly created ExecCommand with a command defined by parameter <i>command</i>
	 *  
	 *  @param command the command
	 */
	public ExecCommand(String command) {
		try {
			p = Runtime.getRuntime().exec(makeArray(command));
			new OutputReader().start();
			new ErrorReader().start();
			p.waitFor();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 *  Returns a string containing the standard output of the command executed by this instance
	 *  
	 *  @return a string containing the standard output of the command executed by this instance
	 */
	public String getOutput() {
		try {
			outputSem.acquire();
		}
		catch (InterruptedException e) {
			e.printStackTrace();
		}
		String value = output;
		outputSem.release();
		return value;
	}
	
	/**
	 *  Returns a string containing the standard error of the command executed by this instance
	 *  
	 *  @return a string containing the standard error of the command executed by this instance
	 */
	public String getError() {
		try {
			errorSem.acquire();
		}
		catch (InterruptedException e) {
			e.printStackTrace();
		}
		String value = error;
		errorSem.release();
		return value;
	}
	
	private String[] makeArray(String command) {
		ArrayList<String> commandArray = new ArrayList<String>();
		String buff = "";
		boolean lookForEnd = false;
		for (int i = 0; i < command.length(); i++) {
			if (lookForEnd) {
				if (command.charAt(i) == '\"') {
				if (buff.length() > 0)
				commandArray.add(buff);
				buff = "";
				lookForEnd = false;
				} else {
				buff += command.charAt(i);
				}
			}
			else {
				if (command.charAt(i) == '\"') {
					lookForEnd = true;
				}
				else if (command.charAt(i) == ' ') {
					if (buff.length() > 0)
						commandArray.add(buff);
					buff = "";
				}
				else {
					buff += command.charAt(i);
				}
			}
		}
		if (buff.length() > 0)
			commandArray.add(buff);			
		String[] array = new String[commandArray.size()];
		for (int i = 0; i < commandArray.size(); i++) {
			array[i] = commandArray.get(i);
		}		
		return array;
	}
}

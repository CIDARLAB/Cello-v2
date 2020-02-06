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
package org.cellocad.v2.partitioning.netlist;

import org.cellocad.v2.results.logicSynthesis.LSResults;

/**
 * The PTNetlistNodeUtils class is class with utility methods for <i>PTNetlistNode</i> instances.
 *  
 * @author Vincent Mirian
 * 
 * @date Jan 21, 2018
 *
 */
public class PTNetlistNodeUtils {

	static public boolean isPrimary(PTNetlistNode node) {
		boolean rtn = false;
		rtn = rtn || PTNetlistNodeUtils.isPrimaryInput(node);
		rtn = rtn || PTNetlistNodeUtils.isPrimaryOutput(node);
		return rtn;
	}
	static public boolean isPrimaryOutput(PTNetlistNode node) {
		boolean rtn = false;
		rtn = rtn || node.getNodeType().equals(LSResults.S_PRIMARYOUTPUT);
		return rtn;
	}
	static public boolean isPrimaryInput(PTNetlistNode node) {
		boolean rtn = false;
		rtn = rtn || node.getNodeType().equals(LSResults.S_PRIMARYINPUT);
		return rtn;
	}

}

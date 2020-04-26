/**
 * Copyright (C) 2019 Boston University (BU)
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
package org.cellocad.v2.common.file.dot.utils;

import java.io.File;

import org.cellocad.v2.common.CelloException;
import org.cellocad.v2.common.ExecCommand;
import org.cellocad.v2.common.Utils;

/**
 * 
 *
 * @author Timothy Jones
 *
 * @date 2019-11-10
 *
 */
public class Dot2Pdf {

	public static File dot2pdf(File file) throws CelloException {
		File rtn = null;
		if (file.isDirectory()) {
			throw new CelloException("Specified file is a directory!");
		}
		String fileName = file.getName();
		String baseName = Utils.getFilename(fileName);
		String parentPath = file.getAbsoluteFile().getParent();
		rtn = new File(parentPath + Utils.getFileSeparator() + baseName + ".png");
		String command = "dot";
		if (Utils.isWin()) {
			command += ".exe";
		}
		command += " -Tpng " + file.getAbsolutePath() + " -o " + rtn.getAbsolutePath();
		ExecCommand proc = Utils.executeAndWaitForCommand(command);
		String error = proc.getError();
		if (!error.equals("")) {
			throw new CelloException(error);
		}
		return rtn;
	}

}

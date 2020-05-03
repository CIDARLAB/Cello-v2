/*
 * Copyright (C) 2019 Boston University (BU)
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

package org.cellocad.v2.common.file.dot.utils;

import java.io.File;
import org.cellocad.v2.common.CelloException;
import org.cellocad.v2.common.ExecCommand;
import org.cellocad.v2.common.Utils;

/**
 * Utility methods for converting DOT files to other formats.
 *
 * @author Timothy Jones
 *
 * @date 2019-11-10
 */
public class DotUtils {

  /**
   * Convert a DOT file to another format.
   * 
   * @param file The DOT file to convert.
   * @return A {@link File} object pointing to the result of the conversion.
   * @throws CelloException Unable to convert DOT file.
   */
  private static File dot2any(final File file, final String type) throws CelloException {
    File rtn = null;
    if (file.isDirectory()) {
      throw new CelloException("Specified file is a directory!");
    }
    final String fileName = file.getName();
    final String baseName = Utils.getFilename(fileName);
    final String parentPath = file.getAbsoluteFile().getParent();
    rtn = new File(parentPath + Utils.getFileSeparator() + baseName + "." + type);
    String command = "dot";
    if (Utils.isWin()) {
      command += ".exe";
    }
    command += " -T" + type + " " + file.getAbsolutePath() + " -o " + rtn.getAbsolutePath();
    final ExecCommand proc = Utils.executeAndWaitForCommand(command);
    final String error = proc.getError();
    if (!error.equals("")) {
      throw new CelloException(error);
    }
    return rtn;
  }

  /**
   * Convert a DOT file to a PDF.
   * 
   * @param file The DOT file to convert.
   * @return A {@link File} object pointing to the PDF file.
   * @throws CelloException Unable to convert DOT file.
   */
  public static File dot2pdf(final File file) throws CelloException {
    File rtn = null;
    rtn = dot2any(file, S_PDF);
    return rtn;
  }

  /**
   * Convert a DOT file to a PNG.
   * 
   * @param file The DOT file to convert.
   * @return A {@link File} object pointing to the PNG file.
   * @throws CelloException Unable to convert DOT file.
   */
  public static File dot2png(final File file) throws CelloException {
    File rtn = null;
    rtn = dot2any(file, S_PNG);
    return rtn;
  }

  private static final String S_PDF = "pdf";
  private static final String S_PNG = "png";

}

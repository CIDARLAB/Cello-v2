/**
 * Copyright (C) 2018 Boston University (BU)
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
package org.cellocad.v2.results.technologyMapping;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

import org.cellocad.v2.common.Utils;
import org.cellocad.v2.results.technologyMapping.activity.TMActivityEvaluation;

/**
 * 
 *
 * @author Timothy Jones
 *
 * @date 2018-05-24
 *
 */
public class TMResultsUtils {

	/**
	 *  Writes the activity evaluation defined by parameter <i>tmae</i> to file defined by <i>filename</i>
	 *
	 *  @param tmae the activity evaluation
	 *  @param filename the file to write the activity evaluation
	 *  @throws RuntimeException if: <br>
	 *  Any of the parameters are null<br>
	 */
	static public void writeCSVForTMActivityEvaluation(final TMActivityEvaluation tmae, final String filename){
		Utils.isNullRuntimeException(tmae, "tmte");
		Utils.isNullRuntimeException(filename, "filename");
		try {
			OutputStream outputStream = new FileOutputStream(filename);
			Writer outputStreamWriter = new OutputStreamWriter(outputStream);
			tmae.writeCSV(",", outputStreamWriter);
			outputStreamWriter.close();
			outputStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}

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

package org.cellocad.v2.clustering.common;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Properties;
import org.cellocad.v2.common.exception.CelloException;

/**
 * Utility methods for the <i>clustering</i> stage.
 *
 * @author Timothy Jones
 * @date 2019-02-20
 */
public class CLUtils {

  /**
   * Get the version of this project.
   *
   * @return The version of this project.
   * @throws CelloException Unable to get the version.
   */
  public static String getVersion() throws CelloException {
    String rtn = null;
    final Properties properties = new Properties();
    try {
      properties.load(
          CLUtils.class.getClassLoader().getResourceAsStream("cello-clustering.properties"));
    } catch (IOException e) {
      throw new CelloException("Unable to get version.");
    }
    rtn = properties.getProperty("org.cellocad.v2.cello-clustering.version");
    return rtn;
  }

  /**
   * Gets the location of a resource as a {@link URL} object.
   *
   * @param resource A resource name.
   * @return The resource location as a {@link URL} object.
   */
  public static URL getResource(final String resource) {
    URL rtn = null;
    rtn = CLUtils.class.getClassLoader().getResource(resource);
    return rtn;
  }

  /**
   * Reads the given resource as a string.
   *
   * @param resource A resource name.
   * @return The given resource as a string.
   * @throws IOException Unable to load the given resource.
   */
  public static String getResourceAsString(final String resource) throws IOException {
    String rtn = "";
    final InputStream is = CLUtils.getResource(resource).openStream();
    final InputStreamReader isr = new InputStreamReader(is);
    final BufferedReader br = new BufferedReader(isr);
    final StringBuffer sb = new StringBuffer();
    String line;
    while ((line = br.readLine()) != null) {
      sb.append(line);
    }
    br.close();
    isr.close();
    is.close();
    rtn = sb.toString();
    return rtn;
  }
}

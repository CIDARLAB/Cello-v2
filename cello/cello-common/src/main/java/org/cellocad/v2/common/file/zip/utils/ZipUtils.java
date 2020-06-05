/*
 * Copyright (C) 2020 Boston University (BU)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package org.cellocad.v2.common.file.zip.utils;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Utilities for ZIP files.
 *
 * @author Timothy Jones
 * @date 2020-06-04
 */
public class ZipUtils {

  /**
   * Zip the files in the given directory, and write to the given output stream.
   *
   * @param os The output stream to write to.
   * @param dir The directory to zip.
   * @throws IOException Unable to walk directory, add zip entry, zip file, or close stream.
   */
  public static void zipDirectory(OutputStream os, Path dir) throws IOException {
    ZipOutputStream zos = new ZipOutputStream(os);
    Iterator<Path> it = Files.walk(dir).iterator();
    while (it.hasNext()) {
      Path path = it.next();
      if (Files.isDirectory(path)) {
        continue;
      }
      ZipEntry entry = new ZipEntry(dir.getParent().relativize(path).toString());
      zos.putNextEntry(entry);
      Files.copy(path, zos);
      zos.closeEntry();
    }
    zos.close();
  }
}

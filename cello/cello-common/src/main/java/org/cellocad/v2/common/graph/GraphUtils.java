/*
 * Copyright (C) 2017 Massachusetts Institute of Technology (MIT)
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

package org.cellocad.v2.common.graph;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.cellocad.v2.common.graph.graph.Edge;
import org.cellocad.v2.common.graph.graph.Graph;
import org.cellocad.v2.common.graph.graph.Vertex;

/**
 * The GraphUtils class is class with utility methods for the for <i>Graph</i> instances.
 *
 * @author Vincent Mirian
 *
 * @date Oct 28, 2017
 */
public class GraphUtils {

  private static Vertex getAndAddIfNotPresent(final String name, final Graph g) {
    Vertex rtn = null;
    rtn = g.getVertexByName(name);
    if (rtn == null) {
      rtn = new Vertex();
      rtn.setName(name);
      g.addVertex(rtn);
    }
    return rtn;
  }

  private static void addVertexEdge(final String src, final String dst, final Graph g) {
    Vertex newSrc = null;
    Vertex newDst = null;
    newSrc = GraphUtils.getAndAddIfNotPresent(src, g);
    newDst = GraphUtils.getAndAddIfNotPresent(dst, g);
    final Edge e = new Edge(newSrc, newDst);
    e.setName(src + "." + dst);
    newSrc.addOutEdge(e);
    newDst.addInEdge(e);
    g.addEdge(e);
  }

  /**
   * Initializes a newly created {@link Graph} using the filename, {@code filename}.
   * {@code filename} is in comma-separated values (CSV) format.
   *
   * @param filename The file.
   */
  public static Graph getGraph(final String filename) {
    // read csv
    final Graph rtn = new Graph();
    Reader in = null;
    try {
      in = new FileReader(filename);
    } catch (final FileNotFoundException e) {
      e.printStackTrace();
    }
    Iterable<CSVRecord> records = null;
    try {
      records = CSVFormat.DEFAULT.parse(in);
    } catch (final IOException e) {
      e.printStackTrace();
    }
    for (final CSVRecord record : records) {
      if (record.size() < 2) {
        continue;
      }
      final String src = record.get(0);
      final String dst = record.get(1);
      if (src != null && dst != null) {
        GraphUtils.addVertexEdge(src, dst, rtn);
      }
    }
    return rtn;
  }

  /**
   * Writes the Graph defined by parameter {@code graph} in DOT (graph description language) format
   * to the file defined by {@code filename}.
   *
   * @param graph    The Graph.
   * @param filename The file.
   */
  public static void writeDotFileForGraph(final Graph graph, final String filename) {
    try {
      final OutputStream outputStream = new FileOutputStream(filename);
      final Writer outputStreamWriter = new OutputStreamWriter(outputStream);
      graph.printDot(outputStreamWriter);
      outputStreamWriter.close();
      outputStream.close();
    } catch (final IOException e) {
      e.printStackTrace();
    }
  }

}

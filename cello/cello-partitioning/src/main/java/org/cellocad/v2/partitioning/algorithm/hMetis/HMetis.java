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

package org.cellocad.v2.partitioning.algorithm.hMetis;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cellocad.v2.common.CelloException;
import org.cellocad.v2.common.Utils;
import org.cellocad.v2.common.file.dot.utils.DotUtils;
import org.cellocad.v2.common.runtime.environment.ArgString;
import org.cellocad.v2.partitioning.algorithm.PTAlgorithm;
import org.cellocad.v2.partitioning.algorithm.hMetis.data.HMetisNetlistData;
import org.cellocad.v2.partitioning.algorithm.hMetis.data.HMetisNetlistEdgeData;
import org.cellocad.v2.partitioning.algorithm.hMetis.data.HMetisNetlistNodeData;
import org.cellocad.v2.partitioning.common.Block;
import org.cellocad.v2.partitioning.common.Move;
import org.cellocad.v2.partitioning.common.Netlister;
import org.cellocad.v2.partitioning.common.Partition;
import org.cellocad.v2.partitioning.common.Partitioner;
import org.cellocad.v2.partitioning.netlist.PTNetlist;
import org.cellocad.v2.partitioning.netlist.PTNetlistEdge;
import org.cellocad.v2.partitioning.netlist.PTNetlistEdgeUtils;
import org.cellocad.v2.partitioning.netlist.PTNetlistNode;
import org.cellocad.v2.partitioning.netlist.PTNetlistNodeUtils;
import org.cellocad.v2.partitioning.netlist.PTNetlistUtils;
import org.cellocad.v2.results.netlist.Netlist;
import org.cellocad.v2.results.netlist.NetlistEdge;
import org.cellocad.v2.results.netlist.NetlistNode;

/**
 * The implementation of the <i>HMetis</i> algorithm in the <i>partitioning</i> stage.
 *
 * @author Vincent Mirian
 *
 * @date 2018-05-21
 */
public class HMetis extends PTAlgorithm {

  private void init() {
    integerVertexMap = new HashMap<>();
    vertexIntegerMap = new HashMap<>();
  }

  /**
   * Initializes a newly created {@link HMetis}.
   */
  public HMetis() {
    init();
  }

  /**
   * Creates the file path for the HMetis Input File.
   */
  protected void createHMetisInFilePath() {
    String file = "";
    file += getRuntimeEnv().getOptionValue(ArgString.OUTPUTDIR);
    file += Utils.getFileSeparator();
    file += "TEMP_GRAPH_FILE.hgr";
    setHMetisInFile(file);
  }

  /**
   * Creates the file path for the HMetis Output File.
   */
  protected void createHMetisOutFilePath() {
    String file = "";
    file += getRuntimeEnv().getOptionValue(ArgString.OUTPUTDIR);
    file += Utils.getFileSeparator();
    file += "TEMP_GRAPH_FILE.hgr.part.";
    file += getPartitioner().getPartition().getNumBlock();
    setHMetisOutFile(file);
  }

  /**
   * Adds the content to the HMetis Input File.
   */
  protected void addContentHMetisInFilePath() {
    final PTNetlist ptNetlist = getNetlister().getPTNetlist();
    final String newline = System.lineSeparator();
    final Map<PTNetlistNode, Integer> vertexIntegerMap = getVertexIntegerMap();
    final List<String> fileContent = new ArrayList<>();
    // vertices
    PTNetlistNode src = null;
    PTNetlistNode dst = null;
    Integer srcInteger = null;
    Integer dstInteger = null;
    int temp = 0;
    for (int i = 0; i < ptNetlist.getNumEdge(); i++) {
      final PTNetlistEdge edge = ptNetlist.getEdgeAtIdx(i);
      if (PTNetlistEdgeUtils.isEdgeConnectedToPrimary(edge)) {
        continue;
      }
      temp++;
      src = edge.getSrc();
      dst = edge.getDst();
      srcInteger = vertexIntegerMap.get(src);
      dstInteger = vertexIntegerMap.get(dst);
      fileContent.add(srcInteger + " " + dstInteger + newline);
    }
    try {
      final OutputStream outputStream = new FileOutputStream(getHMetisInFile());
      final Writer outputStreamWriter = new OutputStreamWriter(outputStream);
      // Format of Hypergraph Input File
      // header lines are: [number of edges] [number of vertices]
      // subsequent lines give each edge, one edge per line
      outputStreamWriter.write(temp + " " + vertexIntegerMap.size() + newline);
      for (int i = 0; i < fileContent.size(); i++) {
        outputStreamWriter.write(fileContent.get(i));
      }
      outputStreamWriter.close();
      outputStream.close();
    } catch (final IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * Creates the the HMetis Execution command.
   */
  protected void createHMetisExec() {
    String cmd = "";
    cmd += "hmetis ";
    cmd += getHMetisInFile();
    cmd += " ";
    cmd += getPartitioner().getPartition().getNumBlock();
    // UBfactor=1, Nruns=10, CType=1, RType=1, Vcycle=3, Reconst=0, dbglvl=0
    cmd += " 1 10 1 1 3 0 0";
    setHMetisExec(cmd);
  }

  /**
   * Creates the file path for the Partition Dot File.
   */
  protected void createPartitionDotFilePath() {
    String file = "";
    file += getRuntimeEnv().getOptionValue(ArgString.OUTPUTDIR);
    file += Utils.getFileSeparator();
    file += Utils.getFilename(getNetlist().getInputFilename());
    file += "_hmetis.dot";
    setPartitionDotFile(file);
  }

  /**
   * Translates the result from HMetis application to the Netlist.
   */
  protected void applyResult() {
    final List<Move> moves = new ArrayList<>();
    Move move = null;
    final Partition partition = getPartitioner().getPartition();
    final File resultFile = new File(getHMetisOutFile());
    final Map<Integer, PTNetlistNode> integerVertexMap = getIntegerVertexMap();
    Reader resultReader = null;
    // Create File Reader
    try {
      resultReader = new FileReader(resultFile);
    } catch (final FileNotFoundException e) {
      throw new RuntimeException("Error with file: " + resultFile);
    }
    // Read and assign move
    final BufferedReader resultBufferedReader = new BufferedReader(resultReader);
    try {
      String line = null;
      int i = 1;
      while ((line = resultBufferedReader.readLine()) != null) {
        final PTNetlistNode ptnode = integerVertexMap.get(i);
        final int blockIdx = Utils.getInteger(line.trim());
        final Block block = partition.getBlockAtIdx(blockIdx);
        move = new Move(ptnode, ptnode.getMyBlock(), block);
        moves.add(move);
        i++;
      }
    } catch (final IOException e) {
      throw new RuntimeException("Error with file: " + resultFile);
    }
    // close
    try {
      resultReader.close();
    } catch (final IOException e) {
      throw new RuntimeException("Error with file: " + resultFile);
    }
    // do moves
    partition.doMoves(moves);
  }

  /**
   * Returns the {@link HMetisNetlistNodeData} of the given node.
   *
   * @param node A node within the netlist of this instance.
   * @return The {@link HMetisNetlistNodeData} instance if it exists, null otherwise.
   */
  protected HMetisNetlistNodeData getHMetisNetlistNodeData(final NetlistNode node) {
    HMetisNetlistNodeData rtn = null;
    rtn = (HMetisNetlistNodeData) node.getNetlistNodeData();
    return rtn;
  }

  /**
   * Returns the {@link HMetisNetlistEdgeData} of the given edge.
   *
   * @param edge An edge within the netlist of this instance.
   * @return The {@link HMetisNetlistEdgeData} instance if it exists, null otherwise.
   */
  protected HMetisNetlistEdgeData getHMetisNetlistEdgeData(final NetlistEdge edge) {
    HMetisNetlistEdgeData rtn = null;
    rtn = (HMetisNetlistEdgeData) edge.getNetlistEdgeData();
    return rtn;
  }

  /**
   * Returns the {@link HMetisNetlistData} of the given netlist.
   *
   * @param netlist The netlist of this instance.
   * @return The {@link HMetisNetlistData} instance if it exists, null otherwise.
   */
  protected HMetisNetlistData getHMetisNetlistData(final Netlist netlist) {
    HMetisNetlistData rtn = null;
    rtn = (HMetisNetlistData) netlist.getNetlistData();
    return rtn;
  }

  /**
   * Gets the constraint data from the netlist constraint file.
   */
  @Override
  protected void getConstraintFromNetlistConstraintFile() {

  }

  /**
   * Gets the data from the UCF.
   */
  @Override
  protected void getDataFromUcf() {

  }

  /**
   * Set parameter values of the algorithm.
   */
  @Override
  protected void setParameterValues() {
  }

  /**
   * Validate parameter values of the algorithm.
   */
  @Override
  protected void validateParameterValues() {

  }

  /**
   * Perform preprocessing.
   */
  @Override
  protected void preprocessing() {
    final Netlist netlist = getNetlist();
    setNetlister(new Netlister(netlist));
    setPartitioner(new Partitioner(getTargetData()));
    // int nblocks = netlist.getNumVertex() -
    // LSResultsUtils.getPrimaryInputOutputNodes(netlist).size();
    // nblocks = ((Double)Math.ceil((double)nblocks/9)).intValue();
    // this.setPartitioner(new Partitioner(nblocks));
    // Map Nodes to integers
    final Map<Integer, PTNetlistNode> integerVertexMap = getIntegerVertexMap();
    final Map<PTNetlistNode, Integer> vertexIntegerMap = getVertexIntegerMap();
    final PTNetlist ptnetlist = getNetlister().getPTNetlist();
    int temp = 1;
    for (int i = 0; i < ptnetlist.getNumVertex(); i++) {
      final PTNetlistNode node = ptnetlist.getVertexAtIdx(i);
      if (PTNetlistNodeUtils.isPrimary(node)) {
        continue;
      }
      vertexIntegerMap.put(node, new Integer(temp));
      integerVertexMap.put(new Integer(temp), node);
      temp++;
    }
    // create path to HMetisInFile
    createHMetisInFilePath();
    // create path to HMetisOutFile
    createHMetisOutFilePath();
    // create contents of HMetisInFile
    addContentHMetisInFilePath();
    // create HMetisExec
    createHMetisExec();
    // create path to PartitionDotFile
    createPartitionDotFilePath();
  }

  /**
   * Run the (core) algorithm.
   */
  @Override
  protected void run() {
    Utils.executeAndWaitForCommand(getHMetisExec());
  }

  /**
   * Perform postprocessing.
   *
   * @throws CelloException Unable to perform postprocessing.
   */
  @Override
  protected void postprocessing() throws CelloException {
    applyResult();
    getNetlister().getNetlist();
    Utils.deleteFilename(getHMetisInFile());
    Utils.deleteFilename(getHMetisOutFile());
    final File dotFile = new File(getPartitionDotFile());
    PTNetlistUtils.writeDotFileForPartition(getNetlister().getPTNetlist(),
        dotFile.getAbsolutePath());
    DotUtils.dot2pdf(dotFile);
  }

  /**
   * Returns the {@link Logger} for the <i>HMetis</i> algorithm.
   *
   * @return The {@link Logger} for the <i>HMetis</i> algorithm.
   */
  @Override
  protected Logger getLogger() {
    return HMetis.logger;
  }

  private static final Logger logger = LogManager.getLogger(HMetis.class);

  /*
   * hMetisInFile
   */
  /**
   * Setter for {@code hMetisInFile}.
   *
   * @param str The value to set {@code hMetisInFile}.
   */
  protected void setHMetisInFile(final String str) {
    hMetisInFile = str;
  }

  /**
   * Getter for {@code hMetisInFile}.
   *
   * @return The value of {@code hMetisInFile}.
   */
  protected String getHMetisInFile() {
    return hMetisInFile;
  }

  private String hMetisInFile;

  /*
   * hMetisOutFile
   */
  /**
   * Setter for {@code hMetisOutFile}.
   *
   * @param str The value to set {@code hMetisOutFile}.
   */
  protected void setHMetisOutFile(final String str) {
    hMetisOutFile = str;
  }

  /**
   * Getter for {@code hMetisOutFile}.
   *
   * @return The value of {@code hMetisOutFile}.
   */
  protected String getHMetisOutFile() {
    return hMetisOutFile;
  }

  private String hMetisOutFile;

  /*
   * PartitionDot
   */
  /**
   * Setter for {@code partitionDot}.
   *
   * @param str The value to set {@code partitionDot}.
   */
  protected void setPartitionDotFile(final String str) {
    partitionDot = str;
  }

  /**
   * Getter for {@code partitionDot}.
   *
   * @return The value of {@code partitionDot}.
   */
  protected String getPartitionDotFile() {
    return partitionDot;
  }

  private String partitionDot;

  /*
   * hMetisExec
   */
  /**
   * Setter for {@code hMetisExec}.
   *
   * @param str The value to set {@code hMetisExec}.
   */
  protected void setHMetisExec(final String str) {
    hMetisExec = str;
  }

  /**
   * Getter for {@code hMetisExec}.
   *
   * @return The value of {@code hMetisExec}.
   */
  protected String getHMetisExec() {
    return hMetisExec;
  }

  private String hMetisExec;

  /*
   * Netlister
   */
  /**
   * Setter for {@code netlister}.
   *
   * @param netlister The value to set {@code netlister}.
   */
  protected void setNetlister(final Netlister netlister) {
    this.netlister = netlister;
  }

  /**
   * Getter for {@code netlister}.
   *
   * @return The value of {@code netlister}.
   */
  protected Netlister getNetlister() {
    return netlister;
  }

  private Netlister netlister;

  /*
   * Partitioner
   */
  /**
   * Setter for {@code partitioner}.
   *
   * @param partitioner The value to set {@code partitioner}.
   */
  protected void setPartitioner(final Partitioner partitioner) {
    this.partitioner = partitioner;
  }

  /**
   * Getter for {@code partitioner}.
   *
   * @return The value of {@code partitioner}.
   */
  protected Partitioner getPartitioner() {
    return partitioner;
  }

  private Partitioner partitioner;

  /*
   * integerVertexMap
   */
  private Map<Integer, PTNetlistNode> integerVertexMap;

  private Map<Integer, PTNetlistNode> getIntegerVertexMap() {
    return integerVertexMap;
  }

  /*
   * vertexIntegerMap
   */
  private Map<PTNetlistNode, Integer> vertexIntegerMap;

  private Map<PTNetlistNode, Integer> getVertexIntegerMap() {
    return vertexIntegerMap;
  }

}

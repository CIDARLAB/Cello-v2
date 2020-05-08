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

package org.cellocad.v2.results.logicSynthesis;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.cellocad.v2.common.CObjectCollection;
import org.cellocad.v2.common.Utils;
import org.cellocad.v2.results.logicSynthesis.logic.LSLogicEvaluation;
import org.cellocad.v2.results.netlist.Netlist;
import org.cellocad.v2.results.netlist.NetlistNode;

/**
 * The LSResultUtils class is class with utility methods for the result of the <i>logicSynthesis</i>
 * stage.
 *
 * @author Vincent Mirian
 * @date 2018-05-21
 */
public class LSResultsUtils {

  /**
   * Returns true if the {@link NetlistNode} defined by parameter {@code node} is a Primary.
   *
   * @param node The {@link NetlistNode}.
   * @return True if the {@link NetlistNode} defined by parameter {@code node} is a Primary, false
   *     otherwise.
   */
  public static boolean isPrimary(final NetlistNode node) {
    boolean rtn = false;
    rtn = rtn || node.getResultNetlistNodeData().getNodeType().equals(LSResults.S_PRIMARYINPUT);
    rtn = rtn || node.getResultNetlistNodeData().getNodeType().equals(LSResults.S_PRIMARYOUTPUT);
    return rtn;
  }

  /**
   * Returns true if the {@link NetlistNode} defined by parameter {@code node} is a PrimaryInput.
   *
   * @param node The {@link NetlistNode}.
   * @return True if the {@link NetlistNode} defined by parameter {@code node} is a PrimaryInput,
   *     false otherwise.
   */
  public static boolean isPrimaryInput(final NetlistNode node) {
    boolean rtn = false;
    rtn = rtn || node.getResultNetlistNodeData().getNodeType().equals(LSResults.S_PRIMARYINPUT);
    return rtn;
  }

  /**
   * Returns true if the {@link NetlistNode} defined by parameter {@code node} is a PrimaryOutput.
   *
   * @param node The {@link NetlistNode}.
   * @return True if the {@link NetlistNode} defined by parameter {@code node} is a PrimaryOutput,
   *     false otherwise.
   */
  public static boolean isPrimaryOutput(final NetlistNode node) {
    boolean rtn = false;
    rtn = rtn || node.getResultNetlistNodeData().getNodeType().equals(LSResults.S_PRIMARYOUTPUT);
    return rtn;
  }

  /**
   * Returns true if the {@link NetlistNode} defined by parameter {@code node} is an Input.
   *
   * @param node The {@link NetlistNode}.
   * @return True if the {@link NetlistNode} defined by parameter {@code node} is an Input, false
   *     otherwise.
   */
  public static boolean isInput(final NetlistNode node) {
    boolean rtn = false;
    rtn = rtn || node.getResultNetlistNodeData().getNodeType().equals(LSResults.S_INPUT);
    return rtn;
  }

  /**
   * Returns true if the {@link NetlistNode} defined by parameter {@code node} is an
   * Input/PrimaryInput.
   *
   * @param node The {@link NetlistNode}.
   * @return True if the {@link NetlistNode} defined by parameter {@code node} is an
   *     Input/PrimaryInput, false otherwise.
   */
  public static boolean isAllInput(final NetlistNode node) {
    boolean rtn = false;
    rtn = rtn || node.getResultNetlistNodeData().getNodeType().equals(LSResults.S_PRIMARYINPUT);
    rtn = rtn || node.getResultNetlistNodeData().getNodeType().equals(LSResults.S_INPUT);
    return rtn;
  }

  /**
   * Returns true if the {@link NetlistNode} defined by parameter {@code node} is an Output.
   *
   * @param node The {@link NetlistNode}.
   * @return True if the {@link NetlistNode} defined by parameter {@code node} is an Output, false
   *     otherwise.
   */
  public static boolean isOutput(final NetlistNode node) {
    boolean rtn = false;
    rtn = rtn || node.getResultNetlistNodeData().getNodeType().equals(LSResults.S_OUTPUT);
    return rtn;
  }

  /**
   * Returns true if the {@link NetlistNode} defined by parameter {@code node} is an
   * Output/PrimaryOutput.
   *
   * @param node The {@link NetlistNode}.
   * @return True if the {@link NetlistNode} defined by parameter {@code node} is an
   *     Output/PrimaryOutput, false otherwise.
   */
  public static boolean isAllOutput(final NetlistNode node) {
    boolean rtn = false;
    rtn = rtn || node.getResultNetlistNodeData().getNodeType().equals(LSResults.S_PRIMARYOUTPUT);
    rtn = rtn || node.getResultNetlistNodeData().getNodeType().equals(LSResults.S_OUTPUT);
    return rtn;
  }

  /**
   * Returns true if the {@link NetlistNode} defined by parameter {@code node} is an Input/Output.
   *
   * @param node The {@link NetlistNode}.
   * @return True if the {@link NetlistNode} defined by parameter {@code node} is an Input/Output,
   *     false otherwise.
   */
  public static boolean isInputOutput(final NetlistNode node) {
    boolean rtn = false;
    rtn = rtn || node.getResultNetlistNodeData().getNodeType().equals(LSResults.S_INPUT);
    rtn = rtn || node.getResultNetlistNodeData().getNodeType().equals(LSResults.S_OUTPUT);
    return rtn;
  }

  /**
   * Returns a {@link CObjectCollection} of {@link NetlistNode} from the {@link Netlist} defined by
   * {@code netlist} with a NodeType defined by {@code nodeType}.
   *
   * @param netlist The {@link Netlist}.
   * @param nodeType The {@link NodeType}.
   * @return A {@link CObjectCollection} of {@link NetlistNode} from the {@link Netlist} defined by
   *     {@code netlist} with a NodeType defined by {@code nodeType}.
   */
  public static CObjectCollection<NetlistNode> getNodeType(
      final Netlist netlist, final String nodeType) {
    CObjectCollection<NetlistNode> rtn = null;
    rtn = new CObjectCollection<>();
    for (int i = 0; i < netlist.getNumVertex(); i++) {
      final NetlistNode node = netlist.getVertexAtIdx(i);
      if (node.getResultNetlistNodeData().getNodeType().equals(nodeType)) {
        rtn.add(node);
      }
    }
    return rtn;
  }

  /**
   * Returns a {@link CObjectCollection} of {@link NetlistNode} from the {@link Netlist} defined by
   * {@code netlist} with a Primary Input NodeType.
   *
   * @param netlist The {@link Netlist}.
   * @return A {@link CObjectCollection} of {@link NetlistNode} from the {@link Netlist} defined by
   *     {@code netlist} with a Primary Input NodeType.
   */
  public static CObjectCollection<NetlistNode> getPrimaryInputNodes(final Netlist netlist) {
    CObjectCollection<NetlistNode> rtn = null;
    rtn = LSResultsUtils.getNodeType(netlist, LSResults.S_PRIMARYINPUT);
    return rtn;
  }

  /**
   * Returns a {@link CObjectCollection} of {@link NetlistNode} from the {@link Netlist} defined by
   * {@code netlist} with a Primary Output NodeType.
   *
   * @param netlist The {@link Netlist}.
   * @return A {@link CObjectCollection} of {@link NetlistNode} from the {@link Netlist} defined by
   *     {@code netlist} with a Primary Output NodeType.
   */
  public static CObjectCollection<NetlistNode> getPrimaryOutputNodes(final Netlist netlist) {
    CObjectCollection<NetlistNode> rtn = null;
    rtn = LSResultsUtils.getNodeType(netlist, LSResults.S_PRIMARYOUTPUT);
    return rtn;
  }

  /**
   * Returns a {@link CObjectCollection} of {@link NetlistNode} from the {@link Netlist} defined by
   * {@code netlist} with a Primary Input/Output NodeType.
   *
   * @param netlist The {@link Netlist}.
   * @return A {@link CObjectCollection} of {@link NetlistNode} from the {@link Netlist} defined by
   *     {@code netlist} with a Primary Input/Output NodeType.
   */
  public static CObjectCollection<NetlistNode> getPrimaryInputOutputNodes(final Netlist netlist) {
    CObjectCollection<NetlistNode> rtn = null;
    rtn = LSResultsUtils.getPrimaryInputNodes(netlist);
    rtn.addAll(LSResultsUtils.getPrimaryOutputNodes(netlist));
    return rtn;
  }

  /**
   * Returns a {@link CObjectCollection} of {@link NetlistNode} from the {@link Netlist} defined by
   * {@code netlist} with an Input NodeType.
   *
   * @param netlist The {@link Netlist}.
   * @return A {@link CObjectCollection} of {@link NetlistNode} from the {@link Netlist} defined by
   *     {@code netlist} with an Input NodeType.
   */
  public static CObjectCollection<NetlistNode> getInputNodes(final Netlist netlist) {
    CObjectCollection<NetlistNode> rtn = null;
    rtn = LSResultsUtils.getNodeType(netlist, LSResults.S_INPUT);
    return rtn;
  }

  /**
   * Returns a {@link CObjectCollection} of {@link NetlistNode} from the {@link Netlist} defined by
   * {@code netlist} with an Output NodeType.
   *
   * @param netlist The {@link Netlist}.
   * @return A {@link CObjectCollection} of {@link NetlistNode} from the {@link Netlist} defined by
   *     {@code netlist} with an Output NodeType.
   */
  public static CObjectCollection<NetlistNode> getOutputNodes(final Netlist netlist) {
    CObjectCollection<NetlistNode> rtn = null;
    rtn = LSResultsUtils.getNodeType(netlist, LSResults.S_OUTPUT);
    return rtn;
  }

  /**
   * Returns a {@link CObjectCollection} of {@link NetlistNode} from the {@link Netlist} defined by
   * {@code netlist} with an Input/Output NodeType.
   *
   * @param netlist The {@link Netlist}.
   * @return A {@link CObjectCollection} of {@link NetlistNode} from the {@link Netlist} defined by
   *     {@code netlist} with an Input/Output NodeType.
   */
  public static CObjectCollection<NetlistNode> getInputOutputNodes(final Netlist netlist) {
    CObjectCollection<NetlistNode> rtn = null;
    rtn = LSResultsUtils.getInputNodes(netlist);
    rtn.addAll(LSResultsUtils.getOutputNodes(netlist));
    return rtn;
  }

  /**
   * Returns a {@link CObjectCollection} of {@link NetlistNode} from the {@link Netlist} defined by
   * {@code netlist} with a Primary Input or Input NodeType.
   *
   * @param netlist The {@link Netlist}.
   * @return A {@link CObjectCollection} of {@link NetlistNode} from the {@link Netlist} defined by
   *     {@code netlist} with a Primary Input or Input NodeType.
   */
  public static CObjectCollection<NetlistNode> getAllInputNodes(final Netlist netlist) {
    CObjectCollection<NetlistNode> rtn = null;
    rtn = LSResultsUtils.getInputNodes(netlist);
    rtn.addAll(LSResultsUtils.getPrimaryInputNodes(netlist));
    return rtn;
  }

  /**
   * Returns a {@link CObjectCollection} of {@link NetlistNode} from the {@link Netlist} defined by
   * {@code netlist} with a Primary Output or Output NodeType.
   *
   * @param netlist The {@link Netlist}.
   * @return A {@link CObjectCollection} of {@link NetlistNode} from the {@link Netlist} defined by
   *     {@code netlist} with a Primary Output or Output NodeType.
   */
  public static CObjectCollection<NetlistNode> getAllOutputNodes(final Netlist netlist) {
    CObjectCollection<NetlistNode> rtn = null;
    rtn = LSResultsUtils.getOutputNodes(netlist);
    rtn.addAll(LSResultsUtils.getPrimaryOutputNodes(netlist));
    return rtn;
  }

  /**
   * Returns a {@link CObjectCollection} of {@link NetlistNode} from the {@link Netlist} defined by
   * {@code netlist} with a Primary Input, Input, Primary Output or Output NodeType.
   *
   * @param netlist The {@link Netlist}.
   * @return A {@link CObjectCollection} of {@link NetlistNode} from the {@link Netlist} defined by
   *     {@code netlist} with a Primary Input, Input, Primary Output or Output NodeType.
   */
  public static CObjectCollection<NetlistNode> getAllInputOutputNodes(final Netlist netlist) {
    CObjectCollection<NetlistNode> rtn = null;
    rtn = LSResultsUtils.getAllInputNodes(netlist);
    rtn.addAll(LSResultsUtils.getAllOutputNodes(netlist));
    return rtn;
  }

  /** ValidNodeTypes: Array of Strings containing Valid NodeType. */
  public static final String[] ValidNodeTypes = {
    LSResults.S_PRIMARYINPUT,
    LSResults.S_PRIMARYOUTPUT,
    LSResults.S_INPUT,
    LSResults.S_OUTPUT,
    LSResults.S_NOT,
    LSResults.S_AND,
    LSResults.S_NAND,
    LSResults.S_OR,
    LSResults.S_NOR,
    LSResults.S_XOR,
    LSResults.S_XNOR
  };

  /**
   * Returns a boolean flag signifying that the parameter {@code nodeType} is a valid NodeType.
   *
   * @return True if the parameter {@code nodeType} is a valid NodeType, otherwise false.
   */
  public static boolean isValidNodeTypes(final String nodeType) {
    boolean rtn = false;
    final List<String> ValidNodeTypes =
        new ArrayList<>(Arrays.asList(LSResultsUtils.ValidNodeTypes));
    rtn = ValidNodeTypes.contains(nodeType);
    return rtn;
  }

  /**
   * Writes the logic evaluation defined by parameter {@code lsle} to file defined by {@code
   * filename}.
   *
   * @param lsle The logic evaluation.
   * @param filename The file to write the activity evaluation.
   * @throws RuntimeException If any of the parameters are null.
   */
  public static void writeCsvForLSLogicEvaluation(
      final LSLogicEvaluation lsle, final String filename) {
    Utils.isNullRuntimeException(lsle, "lsle");
    Utils.isNullRuntimeException(filename, "filename");
    try {
      final OutputStream outputStream = new FileOutputStream(filename);
      final Writer outputStreamWriter = new OutputStreamWriter(outputStream);
      lsle.writeCSV(",", outputStreamWriter);
      outputStreamWriter.close();
      outputStream.close();
    } catch (final IOException e) {
      e.printStackTrace();
    }
  }
}

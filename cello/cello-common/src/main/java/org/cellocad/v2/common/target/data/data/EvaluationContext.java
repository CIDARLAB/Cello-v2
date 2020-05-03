/*
 * Copyright (C) 2020 Boston University (BU)
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

package org.cellocad.v2.common.target.data.data;

import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;
import org.cellocad.v2.common.CelloException;
import org.cellocad.v2.results.logicSynthesis.logic.truthtable.State;
import org.cellocad.v2.results.netlist.NetlistEdge;
import org.cellocad.v2.results.netlist.NetlistNode;
import org.cellocad.v2.results.netlist.data.ResultNetlistEdgeData;

/**
 * A context in which a pointer is evaluated or dereferenced.
 *
 * @author Timothy Jones
 *
 * @date 2020-02-12
 */
public class EvaluationContext {

  private void init() {
    cache = new HashMap<>();
  }

  public EvaluationContext() {
    init();
  }

  private static void isTooShortException(final StringTokenizer st, final String map)
      throws CelloException {
    if (!st.hasMoreTokens()) {
      final String fmt = "%s: '%s' is missing elements.";
      throw new CelloException(String.format(fmt, EvaluationContext.S_INVALID, map));
    }
  }

  private static void isInvalidTokenException(final String map, final String token)
      throws CelloException {
    final String fmt = "%s: '%s', error with '%s'.";
    throw new CelloException(String.format(fmt, EvaluationContext.S_INVALID, map, token));
  }

  private static void isUnsupportedTokenException(final String map, final String token)
      throws CelloException {
    final String fmt = "%s: '%s', error with '%s'.";
    throw new CelloException(String.format(fmt, EvaluationContext.S_UNSUPPORTED, map, token));
  }

  private Evaluatable dereferenceInput(final StringTokenizer st, final String map,
      final NetlistNode node, final Input input) throws CelloException {
    Evaluatable rtn = null;
    NetlistNode src = null;
    for (int i = 0; i < node.getNumInEdge(); i++) {
      final NetlistEdge edge = node.getInEdgeAtIdx(i);
      final ResultNetlistEdgeData data = edge.getResultNetlistEdgeData();
      final Input in = data.getInput();
      if (in.equals(input)) {
        src = edge.getSrc();
        setNode(src);
        break;
      }
    }
    if (src == null) {
      rtn = new NullEvaluatable();
    } else {
      rtn = dereferenceRoot(st, map, src);
    }
    return rtn;
  }

  private Evaluatable dereferenceStructure(final StringTokenizer st, final String map,
      final NetlistNode node, final Structure structure) throws CelloException {
    Evaluatable rtn = null;
    EvaluationContext.isTooShortException(st, map);
    final String token = st.nextToken();
    String name = null;
    switch (token) {
      case Structure.S_INPUTS:
        EvaluationContext.isTooShortException(st, map);
        name = st.nextToken();
        final Input input = structure.getInputs().findCObjectByName(name);
        rtn = dereferenceInput(st, map, node, input);
        break;
      case Structure.S_OUTPUTS:
        EvaluationContext.isUnsupportedTokenException(map, token);
        break;
      case Structure.S_DEVICES:
        EvaluationContext.isUnsupportedTokenException(map, token);
        break;
      default:
        EvaluationContext.isInvalidTokenException(map, token);
    }
    return rtn;
  }

  private Evaluatable dereferenceModel(final StringTokenizer st, final String map,
      final NetlistNode node, final Model model) throws CelloException {
    Evaluatable rtn = null;
    EvaluationContext.isTooShortException(st, map);
    final String token = st.nextToken();
    String name = null;
    switch (token) {
      case Model.S_PARAMETERS:
        EvaluationContext.isTooShortException(st, map);
        name = st.nextToken();
        rtn = model.getParameterByName(name);
        break;
      case Model.S_FUNCTIONS:
        EvaluationContext.isTooShortException(st, map);
        name = st.nextToken();
        rtn = model.getFunctionByName(name);
        break;
      default:
        EvaluationContext.isInvalidTokenException(map, token);
    }
    return rtn;
  }

  private Evaluatable dereferenceRoot(final StringTokenizer st, final String map,
      final NetlistNode node) throws CelloException {
    Evaluatable rtn = null;
    // TODO check cache or originate entry
    EvaluationContext.isTooShortException(st, map);
    final String token = st.nextToken();
    final AssignableDevice d = node.getResultNetlistNodeData().getDevice();
    switch (token) {
      case AssignableDevice.S_MODEL:
        final Model m = d.getModel();
        rtn = dereferenceModel(st, map, node, m);
        break;
      case AssignableDevice.S_STRUCTURE:
        final Structure s = d.getStructure();
        rtn = dereferenceStructure(st, map, node, s);
        break;
      default:
        EvaluationContext.isInvalidTokenException(map, token);
    }
    return rtn;
  }

  /**
   * Dereference the given pointer string.
   * 
   * @param map A pointer string.
   * @return The dereferenced {@link Evaluatable} object.
   * @throws CelloException Unable to dereference the given pointer string.
   */
  public Evaluatable dereference(final String map) throws CelloException {
    Evaluatable rtn = null;
    String str = map;
    if (!str.startsWith(Reference.S_REFCHAR)) {
      final String fmt = "%s: '%s' must begin with '%s'.";
      throw new CelloException(
          String.format(fmt, EvaluationContext.S_INVALID, str, Reference.S_REFCHAR));
    }
    str = str.substring(Reference.S_REFCHAR.length());
    Boolean root = false;
    if (str.startsWith(Reference.S_DELIM)) {
      root = true;
    }
    final StringTokenizer st = new StringTokenizer(str, Reference.S_DELIM);
    if (root) {
      rtn = dereferenceRoot(st, map, getNode());
    } else {
      final String fmt = "%s: '%s'.";
      throw new CelloException(String.format(fmt, EvaluationContext.S_UNSUPPORTED, str));
    }
    return rtn;
  }

  /*
   * NetlistNode
   */

  /**
   * Getter for {@code node}.
   *
   * @return The value of {@code node}.
   */
  public NetlistNode getNode() {
    return node;
  }

  /**
   * Setter for {@code node}.
   *
   * @param node The node to set.
   */
  public void setNode(final NetlistNode node) {
    this.node = node;
  }

  private NetlistNode node;

  /*
   * State
   */

  /**
   * Getter for {@code state}.
   *
   * @return The value of {@code state}.
   */
  public State<NetlistNode> getState() {
    return state;
  }

  /**
   * Setter for {@code state}.
   *
   * @param state The state to set.
   */
  public void setState(final State<NetlistNode> state) {
    this.state = state;
  }

  private State<NetlistNode> state;

  /*
   * Cache
   */

  /**
   * Getter for {@code cache}.
   *
   * @return The value of {@code cache}.
   */
  public Map<String, Number> getCache() {
    return cache;
  }

  private Map<String, Number> cache;

  private static final String S_INVALID = "Invalid reference string";
  private static final String S_UNSUPPORTED = "Unsupported reference string";

}

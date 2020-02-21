/**
 * Copyright (C) 2020 Boston University (BU)
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
package org.cellocad.v2.common.target.data.model;

import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import org.cellocad.v2.common.CelloException;
import org.cellocad.v2.common.netlist.data.StageNetlistEdgeData;
import org.cellocad.v2.common.target.data.component.AssignableDevice;
import org.cellocad.v2.results.logicSynthesis.logic.truthtable.State;
import org.cellocad.v2.results.netlist.NetlistEdge;
import org.cellocad.v2.results.netlist.NetlistNode;

/**
 *
 *
 * @author Timothy Jones
 *
 * @date 2020-02-12
 *
 */
public class EvaluationContext {

	private void init() {
		this.cache = new HashMap<>();
	}

	public EvaluationContext() {
		this.init();
	}

	private static void isTooShortException(final StringTokenizer st, final String map) throws CelloException {
		if (!st.hasMoreTokens()) {
			String fmt = "%s: '%s' is missing elements.";
			throw new CelloException(String.format(fmt, S_INVALID, map));
		}
	}

	private static void isInvalidTokenException(final String map, final String token) throws CelloException {
		String fmt = "%s: '%s', error with '%s'.";
		throw new CelloException(String.format(fmt, S_INVALID, map, token));
	}

	private static void isUnsupportedTokenException(final String map, final String token) throws CelloException {
		String fmt = "%s: '%s', error with '%s'.";
		throw new CelloException(String.format(fmt, S_UNSUPPORTED, map, token));
	}

	private static Evaluatable dereferenceInput(final StringTokenizer st, final String map, final NetlistNode node,
			final Input input)
			throws CelloException {
		Evaluatable rtn = null;
		NetlistNode src = null;
		for (int i = 0; i < node.getNumInEdge(); i++) {
			NetlistEdge edge = node.getInEdgeAtIdx(i);
			StageNetlistEdgeData data = edge.getStageNetlistEdgeData();
			Input in = data.getInput();
			if (in.equals(input)) {
				src = edge.getSrc();
				break;
			}
		}
		if (src == null) {
			String fmt = "Nothing wired to input '%s'.";
			throw new RuntimeException(String.format(fmt, input.getName()));
		}
		rtn = dereferenceRoot(st, map, src);
		return rtn;
	}

	private static Evaluatable dereferenceStructure(final StringTokenizer st, final String map, final NetlistNode node,
			final Structure structure)
			throws CelloException {
		Evaluatable rtn = null;
		isTooShortException(st, map);
		String token = st.nextToken();
		String name = null;
		switch (token) {
		case Structure.S_INPUTS:
			isTooShortException(st, map);
			name = st.nextToken();
			Input input = structure.getInputs().findCObjectByName(name);
			rtn = dereferenceInput(st, map, node, input);
			break;
		case Structure.S_OUTPUTS:
			isUnsupportedTokenException(map, token);
			break;
		case Structure.S_DEVICES:
			isUnsupportedTokenException(map, token);
			break;
		default:
			isInvalidTokenException(map, token);
		}
		return rtn;
	}

	private static Evaluatable dereferenceModel(final StringTokenizer st, final String map, final NetlistNode node,
			final Model model)
			throws CelloException {
		Evaluatable rtn = null;
		isTooShortException(st, map);
		String token = st.nextToken();
		String name = null;
		switch (token) {
		case Model.S_PARAMETERS:
			isTooShortException(st, map);
			name = st.nextToken();
			rtn = model.getParameterByName(name);
			break;
		case Model.S_FUNCTIONS:
			isTooShortException(st, map);
			name = st.nextToken();
			rtn = model.getFunctionByName(name);
			break;
		default:
			isInvalidTokenException(map, token);
		}
		return rtn;
	}

	private static Evaluatable dereferenceRoot(final StringTokenizer st, final String map, final NetlistNode node)
			throws CelloException {
		Evaluatable rtn = null;
		// TODO check cache or originate entry
		isTooShortException(st, map);
		String token = st.nextToken();
		AssignableDevice d = node.getStageNetlistNodeData().getDevice();
		switch (token) {
		case AssignableDevice.S_MODEL:
			Model m = d.getModel();
			rtn = dereferenceModel(st, map, node, m);
			break;
		case AssignableDevice.S_STRUCTURE:
			Structure s = d.getStructure();
			rtn = dereferenceStructure(st, map, node, s);
			break;
		default:
			isInvalidTokenException(map, token);
		}
		return rtn;
	}

	public Evaluatable dereference(final String map) throws CelloException {
		Evaluatable rtn = null;
		String str = map;
		if (!str.startsWith(Reference.S_REFCHAR)) {
			String fmt = "%s: '%s' must begin with '%s'.";
			throw new CelloException(String.format(fmt, S_INVALID, str, Reference.S_REFCHAR));
		}
		str = str.substring(Reference.S_REFCHAR.length());
		Boolean root = false;
		if (str.startsWith(Reference.S_DELIM)) {
			root = true;
		}
		StringTokenizer st = new StringTokenizer(str, Reference.S_DELIM);
		if (root) {
			rtn = dereferenceRoot(st, map, this.getNode());
		} else {
			String fmt = "%s: '%s'.";
			throw new CelloException(String.format(fmt, S_UNSUPPORTED, str));
		}
		return rtn;
	}

	/*
	 * NetlistNode
	 */

	/**
	 * Getter for <i>node</i>.
	 *
	 * @return value of node
	 */
	public NetlistNode getNode() {
		return node;
	}

	/**
	 * Setter for <i>node</i>.
	 *
	 * @param node the node to set
	 */
	public void setNode(NetlistNode node) {
		this.node = node;
	}

	private NetlistNode node;

	/*
	 * State
	 */

	/**
	 * Getter for <i>state</i>.
	 *
	 * @return value of state
	 */
	public State<NetlistNode> getState() {
		return state;
	}

	/**
	 * Setter for <i>state</i>.
	 *
	 * @param state the state to set
	 */
	public void setState(State<NetlistNode> state) {
		this.state = state;
	}

	private State<NetlistNode> state;

	/*
	 * Cache
	 */

	/**
	 * Getter for <i>cache</i>.
	 *
	 * @return value of cache
	 */
	public Map<String, Number> getCache() {
		return cache;
	}

	private Map<String, Number> cache;

	private static final String S_INVALID = "Invalid reference string";
	private static final String S_UNSUPPORTED = "Unsupported reference string";

}

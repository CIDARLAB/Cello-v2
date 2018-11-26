/**
 * Copyright (C) 2018 Boston Univeristy (BU)
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
package org.cellocad.cello2.technologyMapping.algorithm.DSGRN.data.inequality;

import java.util.Arrays;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;
import java.util.StringTokenizer;

import org.cellocad.cello2.common.CObject;
import org.cellocad.cello2.common.CObjectCollection;
import org.cellocad.cello2.results.netlist.Netlist;
import org.cellocad.cello2.results.netlist.NetlistNode;
import org.cellocad.cello2.technologyMapping.algorithm.DSGRN.data.ucf.Gate;
import org.cellocad.cello2.technologyMapping.algorithm.DSGRN.data.ucf.ResponseFunction;
import org.cellocad.cello2.technologyMapping.algorithm.DSGRN.data.ucf.ResponseFunctionVariable;

/**
 *
 *
 * @author Timothy Jones
 *
 * @date 2018-10-29
 *
 */
public class Inequalities extends CObject{
	
	private void init() {
		operatorStack = new Stack<String>();
		outputQueue = new LinkedList<String>();
	}

	private static boolean isNumeric(String str) {
		try {
			Double.parseDouble(str);
		}
		catch(NumberFormatException e) {
		    return false;
		}
		return true;
	}

	public Inequalities(String value) {
		init();
		value = "1 < 2 < 3 < 10 && 5 < 10";
		StringTokenizer st = new StringTokenizer(value);
		while (st.hasMoreTokens()) {
			String token = st.nextToken();
			if (
					token.startsWith("U")
					||
					token.startsWith("T")
					||
					token.startsWith("L")
					)
			{
				this.getOutputQueue().add(token);
			}
			else if (ValidOperatorTypes.contains(token))
			{
				if (token.equals(S_AND)) {
					while (
							!this.getOperatorStack().empty()
							&&
							RelationalOperatorTypes.contains(this.getOperatorStack().peek())
							) {
						String op = this.getOperatorStack().pop();
						this.getOutputQueue().add(op);
					}
				}
				if (
						!this.getOperatorStack().empty()
						&&
						RelationalOperatorTypes.contains(this.getOperatorStack().peek())
						&&
						RelationalOperatorTypes.contains(token)
						) {
					String temp = this.getOutputQueue().peekLast();
					String op = this.getOperatorStack().pop();
					this.getOutputQueue().add(op);
					this.getOutputQueue().add(temp);
					this.getOperatorStack().push(S_AND);
				}
				this.getOperatorStack().push(token);
			}
			else if (isNumeric(token))
			{
				this.getOutputQueue().add(token);
			}
		}
		while (!this.getOperatorStack().empty()) {
			String op = this.getOperatorStack().pop();
			this.getOutputQueue().add(op);
		}
		System.out.println(this.getOutputQueue());
	}
	
	private Double getTokenValue(String token, CObjectCollection<NetlistNode> nodes, CObjectCollection<Gate> gates) {
		Double rtn = null;
		if (isNumeric(token)) {
			rtn = Double.valueOf(token);
		}
		else {
			String temp = token.substring(2,token.length()-1);
			String[] names = temp.split(",");
			NetlistNode first = nodes.findCObjectByName(names[0]);
			NetlistNode second = nodes.findCObjectByName(names[1]);
			if (first != null && second != null) {
				String gateType = first.getResultNetlistNodeData().getGateType();
				Gate gate = gates.findCObjectByName(gateType);
				ResponseFunction rf = gate.getResponseFunction();
				if (token.startsWith("L")) {
					rtn = rf.getParameterValueByName("ymin").getValue();
				}
				else if (token.startsWith("T")) {
					ResponseFunctionVariable var = rf.getVariableByName("x");
					Double off = var.getOffThreshold();
					Double on = var.getOnThreshold();
					rtn = Math.pow(on*off, 1/2);
				}
				else if (token.startsWith("U")) {
					rtn = rf.getParameterValueByName("ymax").getValue();
				}
			}
		}
		return rtn;
	}
	
	public Boolean evaluate(Netlist netlist, CObjectCollection<Gate> gates) {
		Boolean rtn = null;
		CObjectCollection<NetlistNode> nodes = new CObjectCollection<>();
		for (int i = 0; i < netlist.getNumVertex(); i++) {
			NetlistNode node = netlist.getVertexAtIdx(i);
			nodes.add(node);
		}
		Deque<String> outputQueue = new LinkedList<>(this.getOutputQueue());
		Stack<String> temp = new Stack<>();
		Deque<Boolean> queue = new LinkedList<>();
		while (!outputQueue.isEmpty()) {
			String token = outputQueue.pop();
			if (RelationalOperatorTypes.contains(token)) {
				String second = temp.pop();
				String first = temp.pop();
				Double lvalue = this.getTokenValue(first, nodes, gates);
				Double rvalue = this.getTokenValue(second, nodes, gates);
				switch (token) {
				case S_GT: {
					queue.push(lvalue > rvalue);
					break;
				}
				case S_GTEQ: {
					queue.push(lvalue >= rvalue);
					break;
				}
				case S_LT: {
					queue.push(lvalue < rvalue);
					break;
				}
				case S_LTEQ: {
					queue.push(lvalue <= rvalue);
					break;
				}
				case S_EQ: {
					queue.push(lvalue == rvalue);
					break;
				}
				default: {
					throw new RuntimeException("Unsupported operator type.");
				}
				}
			}
			else if (token.equals(S_AND)) {
				Boolean first = queue.pop();
				Boolean second = queue.pop();
				Boolean value = first && second;
				queue.push(value);
			}
			else {
				temp.push(token);
			}
		}
		rtn = queue.pop();
		return rtn;
	}

	/**
	 * Getter for <i>operatorStack</i>
	 * @return value of <i>operatorStack</i>
	 */
	private Stack<String> getOperatorStack() {
		return operatorStack;
	}

	/**
	 * Getter for <i>outputQueue</i>
	 * @return value of <i>outputQueue</i>
	 */
	private Deque<String> getOutputQueue() {
		return outputQueue;
	}

	private Stack<String> operatorStack;
	private Deque<String> outputQueue;
	
	private static final String S_GT = ">";
	private static final String S_GTEQ = ">=";
	private static final String S_LT = "<";
	private static final String S_LTEQ = "<=";
	private static final String S_EQ = "=";
	private static final String S_AND = "&&";
	/**
	 *  ValidOperatorTypes: Array of Strings containing Valid Operator
	 */
	private static final List<String> ValidOperatorTypes = Arrays.asList(
			new String[]
					{
							S_GT,
							S_GTEQ,
							S_LT,
							S_LTEQ,
							S_EQ,
							S_AND
					}
			);
	/**
	 *  RelationalOperatorTypes: Array of Strings containing Valid Operator
	 */
	private static final List<String> RelationalOperatorTypes = Arrays.asList(
			new String[]
					{
							S_GT,
							S_GTEQ,
							S_LT,
							S_LTEQ,
					}
			);

}

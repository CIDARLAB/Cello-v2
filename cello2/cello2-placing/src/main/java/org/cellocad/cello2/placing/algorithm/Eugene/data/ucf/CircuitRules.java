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
package org.cellocad.cello2.placing.algorithm.Eugene.data.ucf;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import org.cellocad.cello2.common.Utils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.logicng.formulas.Formula;
import org.logicng.formulas.FormulaFactory;
import org.logicng.io.parsers.ParserException;
import org.logicng.io.parsers.PropositionalParser;
import org.logicng.transformations.dnf.DNFFactorization;

/**
 * Rules is a class representing the rules for part and gate placement
 * <i>Eugene</i> algorithm.
 * 
 * @author Timothy Jones
 * 
 * @date 2018-08-10
 *
 */
public class CircuitRules {

	private static class Namer {

		public Namer() {
			this.last = "";
		}

		public String next() {
			String rtn = null;
			int len = this.last.length();
			if (this.last.endsWith("Z") || len < 1) {
				rtn = this.last + "A";
			} else {
				String prefix = this.last.substring(0, len - 1);
				char c = this.last.charAt(len - 1);
				rtn = prefix + nextChar(c);
			}
			this.last = rtn;
			return rtn;
		}

		private char nextChar(char c) {
			return (char) (c + 1);
		}

		private String last;
	}

	private void parseBlock(final JSONObject obj, StringBuilder builder, Namer namer) {
		JSONArray rules = (JSONArray) obj.get("rules");
		String op = (String) obj.get("function");
		parseBlocks(rules, builder, namer, op);
	}

	private void parseBlocks(final JSONArray arr, StringBuilder builder, final Namer namer, String op) {
		builder.append("( ");
		char c = '\0';
		if (op.equals("AND")) {
			c = '&';
		} else if (op.equals("OR")) {
			c = '|';
		}
		for (int i = 0; i < arr.size(); i++) {
			Object obj = arr.get(i);
			if (obj instanceof JSONObject) {
				JSONObject jObj = (JSONObject) obj;
				parseBlock(jObj, builder, namer);
			} else if (obj instanceof String) {
				String name = namer.next();
				this.getNames().put(name, (String) obj);
				builder.append(name);
			}
			if (i < arr.size() - 1)
				builder.append(' ' + String.valueOf(c) + ' ');
		}
		builder.append(" )");
	}

	private String buildRule(StringTokenizer st, Map<String, String> names, int num) {
		String rtn = "";
		rtn += "Rule CircuitRule" + String.valueOf(num) + "( ON circuit:" + Utils.getNewLine();
		while (true) {
			if (!st.hasMoreTokens())
				break;
			String t = st.nextToken();
			if (t.equals("|"))
				break;
			if (t.equals("&")) {
				rtn += " " + EugeneRules.S_AND + Utils.getNewLine();
				continue;
			}
			rtn += "    " + names.get(t);
		}
		rtn += Utils.getNewLine() + ");" + Utils.getNewLine();
		return rtn;
	}

	private void buildRules(StringTokenizer st, Map<String, String> names) {
		int i = 0;
		while (st.hasMoreTokens()) {
			this.getRules().add(buildRule(st, names, i));
			i++;
		}
	}

	private void parseRules(final JSONObject jObj) {
		JSONArray jArr = (JSONArray) jObj.get("rules");
		StringBuilder builder = new StringBuilder();
		Namer namer = new Namer();
		parseBlocks(jArr, builder, namer, "AND");
		String expr = builder.toString();
		final FormulaFactory f = new FormulaFactory();
		final PropositionalParser p = new PropositionalParser(f);
		Formula formula;
		try {
			formula = p.parse(expr);
		} catch (ParserException e) {
			throw new RuntimeException(e);
		}
		final DNFFactorization d = new DNFFactorization();
		final Formula r = d.apply(formula, false);
		StringTokenizer st = new StringTokenizer(r.toString());
		buildRules(st, this.getNames());
	}

	private void init() {
		this.rules = new ArrayList<>();
		this.names = new HashMap<>();
	}

	public CircuitRules(final JSONObject jObj) {
		init();
		this.parseRules(jObj);
	}

	public String toString() {
		return String.join(Utils.getNewLine(), this.getRules());
	}

	/*
	 * rules
	 */
	private Collection<String> getRules() {
		return this.rules;
	}

	private Collection<String> rules;

	private Map<String, String> getNames() {
		return this.names;
	}

	private Map<String, String> names;

}

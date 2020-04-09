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
package org.cellocad.v2.common.target.data.data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import org.cellocad.v2.common.CObjectCollection;
import org.cellocad.v2.common.Utils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.logicng.formulas.Formula;
import org.logicng.formulas.FormulaFactory;
import org.logicng.io.parsers.ParserException;
import org.logicng.io.parsers.PropositionalParser;
import org.logicng.transformations.dnf.DNFFactorization;

/**
 *
 *
 * @author Timothy Jones
 *
 * @date 2020-01-13
 *
 */
public class DeviceRules extends AbstractRules {

	private void parseBlock(final JSONObject obj, final StructureDevice device, final CObjectCollection<Part> inputs,
			StringBuilder builder, Namer namer) {
        JSONArray rules = (JSONArray) obj.get(S_RULES);
        String op = this.getOperator((String) obj.get(S_FUNCTION));
        builder.append("(");
        Collection<String> accepted = new ArrayList<>();
		for (StructureObject o : device.getComponents()) {
			if (o instanceof StructurePart) {
				accepted.add(o.getName());
			}
		}
		for (Part p : inputs) {
			accepted.add(p.getName());
		}
        for (int i = 0; i < rules.size(); i++) {
            Object o = rules.get(i);
            if (o instanceof JSONObject) {
                JSONObject j = (JSONObject) o;
				parseBlock(j, device, inputs, builder, namer);
            } else if (o instanceof String) {
                String str = (String) o;
                Collection<String> stuff = EugeneRules.getObjects(str);
                if (!accepted.containsAll(stuff) && !EugeneRules.GlobalOrientationRuleKeywords.contains(str))
                    continue;
                String name = namer.next();
                String rule = str;
                this.getNames().put(name, rule);
                builder.append(name);
            }
            builder.append(op);
        }
        this.trim(builder);
        builder.append(")");
        this.trim(builder);
    }

    /**
     * Build an individual (Eugene) <code>Rule</code> block.
     *
     * @param st    A <code>{@link java.util.StringTokenizer StringTokenizer}</code>
     *              instantiated with the DNF rule set.
     * @param names A map from an object name in a rule to the desired name.
     * @param num   A numeral for the rule used in naming.
     * @return A string representation of the Rule block.
     */
	protected String buildRule(StringTokenizer st, final String name, final int num) {
        String rtn = "";
		String fmt = "Rule %sRule%d( ON %s:";
		rtn += String.format(fmt, name, num, name) + Utils.getNewLine();
        while (st.hasMoreTokens()) {
            String t = st.nextToken();
            if (t.equals("|"))
                break;
            if (t.equals("&")) {
                rtn += " " + EugeneRules.S_AND + Utils.getNewLine();
                continue;
            }
            String rule = this.getNames().get(t);
            rtn += Utils.getTabCharacter() + rule;
        }
        rtn += Utils.getNewLine() + ");" + Utils.getNewLine();
        return rtn;
    }

	private Collection<String> buildRules(StringTokenizer st, String name) {
		Collection<String> rtn = new ArrayList<>();
        int i = 0;
        while (st.hasMoreTokens()) {
			rtn.add(buildRule(st, name, i));
            i++;
        }
		return rtn;
    }

	private Collection<String> parseDeviceRules(final JSONObject jObj, final StructureDevice device,
			final CObjectCollection<Part> inputs) {
		Collection<String> rtn = null;
        JSONObject jArr = (JSONObject) jObj.get(S_RULES);
        StringBuilder builder = new StringBuilder();
        Namer namer = new Namer();
		// inject CONTAINS rules
		for (Part p : inputs) {
			String rule = String.format("%s %s", EugeneRules.S_CONTAINS, p.getName());
			String name = namer.next();
			this.getNames().put(name, rule);
			builder.append(name);
			builder.append(this.getOperator("AND"));
		}
		parseBlock(jArr, device, inputs, builder, namer);
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
		rtn = buildRules(st, device.getName());
		return rtn;
    }

    private void init() {
		this.names = new HashMap<>();
    }

    public DeviceRules(final JSONObject jObj) {
        init();
        this.json = jObj;
    }

	public String filter(final StructureDevice device, final CObjectCollection<Part> inputs) {
		Collection<String> rules = this.parseDeviceRules(this.getJson(), device, inputs);
		return String.join(Utils.getNewLine(), rules);
    }

    /**
     * Getter for <code>names</code>.
     *
     * @return The value of <code>names</code>.
     */
    private Map<String, String> getNames() {
        return this.names;
    }

    private Map<String, String> names;

    /**
     * Getter for <code>json</code>.
     *
     * @return The value of <code>json</code>.
     */
    private JSONObject getJson() {
        return json;
    }

    private JSONObject json;

}

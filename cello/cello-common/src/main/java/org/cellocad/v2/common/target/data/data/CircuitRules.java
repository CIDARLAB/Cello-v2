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
package org.cellocad.v2.common.target.data.data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import org.cellocad.v2.common.Utils;
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
public class CircuitRules extends AbstractRules {

    /**
     * Rename the device in a rule.
     *
     * @param rule    The rule.
     * @param devices The list of devices that should be renamed in the rule.
     * @return The rule with renamed devices.
     */
    private String renameDevices(final String rule, final Collection<String> devices) {
        String rtn = null;
        StringTokenizer st = new StringTokenizer(rule);
        Collection<String> tokens = new ArrayList<>();
        while (st.hasMoreTokens()) {
            String token = st.nextToken();
            if (devices.contains(token)) {
                tokens.add(token + "Device");
            } else {
                tokens.add(token);
            }
        }
        rtn = String.join(" ", tokens);
        return rtn;
    }

    private void parseBlock(final JSONObject obj, final Collection<String> devices, final Collection<String> fenceposts,
            StringBuilder builder, Namer namer) {
        JSONArray rules = (JSONArray) obj.get(S_RULES);
        String op = this.getOperator((String) obj.get(S_FUNCTION));
        builder.append("(");
        Collection<String> accepted = new ArrayList<>();
        accepted.addAll(devices);
        accepted.addAll(fenceposts);
        for (int i = 0; i < rules.size(); i++) {
            Object o = rules.get(i);
            if (o instanceof JSONObject) {
                JSONObject j = (JSONObject) o;
                parseBlock(j, devices, fenceposts, builder, namer);
            } else if (o instanceof String) {
                String str = (String) o;
                Collection<String> stuff = EugeneRules.getObjects(str);
                if (!accepted.containsAll(stuff) && !EugeneRules.GlobalOrientationRuleKeywords.contains(str))
                    continue;
                String name = namer.next();
                String rule = renameDevices(str, devices);
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
     * @param names A map from a name in the DNF to the device name.
     * @param num   A numeral for the rule used in naming.
     * @return A string representation of the Rule block.
     */
    protected String buildRule(StringTokenizer st, Map<String, String> names, int num) {
        String rtn = "";
        rtn += "Rule CircuitRule" + String.valueOf(num) + "( ON circuit:" + Utils.getNewLine();
        while (st.hasMoreTokens()) {
            String t = st.nextToken();
            if (t.equals("|"))
                break;
            if (t.equals("&")) {
                rtn += " " + EugeneRules.S_AND + Utils.getNewLine();
                continue;
            }
            String rule = names.get(t);
            rtn += Utils.getTabCharacter() + rule;
        }
        rtn += Utils.getNewLine() + ");" + Utils.getNewLine();
        return rtn;
    }

    /**
     * Generate all (Eugene) Rule blocks from the DNF.
     *
     * @param st    The
     *              <code>{@link java.util.StringTokenizer StringTokenizer}</code>
     *              instantiated with the DNF rule set.
     * @param names A map from a name in the DNF to the device name.
     */
    private void buildRules(StringTokenizer st, Map<String, String> names) {
        int i = 0;
        while (st.hasMoreTokens()) {
            this.getRules().add(buildRule(st, names, i));
            i++;
        }
    }

    private void parseCircuitRules(final JSONObject jObj, final Collection<String> devices,
            final Collection<String> fenceposts) {
        JSONObject jArr = (JSONObject) jObj.get(S_RULES);
        StringBuilder builder = new StringBuilder();
        Namer namer = new Namer();
        parseBlock(jArr, devices, fenceposts, builder, namer);
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
        this.json = jObj;
    }

    public String filter(Collection<String> devices, Collection<String> fenceposts) {
        this.parseCircuitRules(this.getJson(), devices, fenceposts);
        return String.join(Utils.getNewLine(), this.getRules());
    }

//	public String toString() {
//		return String.join(Utils.getNewLine(), this.getRules());
//	}

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

    /**
     * @return the json
     */
    private JSONObject getJson() {
        return json;
    }

    private JSONObject json;

}

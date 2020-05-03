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
 * The rules associated with part placement inside of a device or gate.
 *
 * @author Timothy Jones
 *
 * @date 2020-01-13
 */
public class DeviceRules extends AbstractRules {

  private void parseBlock(final JSONObject obj, final StructureDevice device,
      final CObjectCollection<Part> inputs, final StringBuilder builder, final Namer namer) {
    final JSONArray rules = (JSONArray) obj.get(AbstractRules.S_RULES);
    final String op = getOperator((String) obj.get(AbstractRules.S_FUNCTION));
    builder.append("(");
    final Collection<String> accepted = new ArrayList<>();
    for (final StructureObject o : device.getComponents()) {
      if (o instanceof StructurePart) {
        accepted.add(o.getName());
      }
    }
    for (final Part p : inputs) {
      accepted.add(p.getName());
    }
    for (int i = 0; i < rules.size(); i++) {
      final Object o = rules.get(i);
      if (o instanceof JSONObject) {
        final JSONObject j = (JSONObject) o;
        parseBlock(j, device, inputs, builder, namer);
      } else if (o instanceof String) {
        final String str = (String) o;
        final Collection<String> stuff = EugeneRules.getObjects(str);
        if (!accepted.containsAll(stuff)
            && !EugeneRules.GlobalOrientationRuleKeywords.contains(str)) {
          continue;
        }
        final String name = namer.next();
        final String rule = str;
        getNames().put(name, rule);
        builder.append(name);
      }
      builder.append(op);
    }
    trim(builder);
    builder.append(")");
    trim(builder);
  }

  /**
   * Build an individual (Eugene) <code>Rule</code> block.
   *
   * @param st    A <code>{@link java.util.StringTokenizer StringTokenizer}</code> instantiated with
   *              the DNF rule set.
   * @param names A map from an object name in a rule to the desired name.
   * @param num   A numeral for the rule used in naming.
   * @return A string representation of the Rule block.
   */
  protected String buildRule(final StringTokenizer st, final String name, final int num) {
    String rtn = "";
    final String fmt = "Rule %sRule%d( ON %s:";
    rtn += String.format(fmt, name, num, name) + Utils.getNewLine();
    while (st.hasMoreTokens()) {
      final String t = st.nextToken();
      if (t.equals("|")) {
        break;
      }
      if (t.equals("&")) {
        rtn += " " + EugeneRules.S_AND + Utils.getNewLine();
        continue;
      }
      final String rule = getNames().get(t);
      rtn += Utils.getTabCharacter() + rule;
    }
    rtn += Utils.getNewLine() + ");" + Utils.getNewLine();
    return rtn;
  }

  private Collection<String> buildRules(final StringTokenizer st, final String name) {
    final Collection<String> rtn = new ArrayList<>();
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
    final JSONObject jArr = (JSONObject) jObj.get(AbstractRules.S_RULES);
    final StringBuilder builder = new StringBuilder();
    final Namer namer = new Namer();
    // inject CONTAINS rules
    for (final Part p : inputs) {
      final String rule = String.format("%s %s", EugeneRules.S_CONTAINS, p.getName());
      final String name = namer.next();
      getNames().put(name, rule);
      builder.append(name);
      builder.append(getOperator("AND"));
    }
    parseBlock(jArr, device, inputs, builder, namer);
    final String expr = builder.toString();
    final FormulaFactory f = new FormulaFactory();
    final PropositionalParser p = new PropositionalParser(f);
    Formula formula;
    try {
      formula = p.parse(expr);
    } catch (final ParserException e) {
      throw new RuntimeException(e);
    }
    final DNFFactorization d = new DNFFactorization();
    final Formula r = d.apply(formula, false);
    final StringTokenizer st = new StringTokenizer(r.toString());
    rtn = buildRules(st, device.getName());
    return rtn;
  }

  private void init() {
    names = new HashMap<>();
  }

  public DeviceRules(final JSONObject jObj) {
    init();
    json = jObj;
  }

  public String filter(final StructureDevice device, final CObjectCollection<Part> inputs) {
    final Collection<String> rules = parseDeviceRules(getJson(), device, inputs);
    return String.join(Utils.getNewLine(), rules);
  }

  /**
   * Getter for {@code names}.
   *
   * @return The value of <code>names</code>.
   */
  private Map<String, String> getNames() {
    return names;
  }

  private Map<String, String> names;

  /**
   * Getter for {@code json}.
   *
   * @return The value of <code>json</code>.
   */
  private JSONObject getJson() {
    return json;
  }

  private final JSONObject json;

}

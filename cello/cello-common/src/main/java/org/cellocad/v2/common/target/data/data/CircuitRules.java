/*
 * Copyright (C) 2018 Boston University (BU)
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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.cellocad.v2.common.Utils;
import org.cellocad.v2.common.collection.CircularLinkedList;
import org.cellocad.v2.common.target.data.TargetDataInstance;
import org.json.simple.JSONObject;
import org.logicng.formulas.Formula;
import org.logicng.formulas.FormulaFactory;
import org.logicng.io.parsers.ParserException;
import org.logicng.io.parsers.PropositionalParser;
import org.logicng.transformations.dnf.DNFFactorization;

/**
 * Rules is a class representing the rules for part and gate placement <i>Eugene</i> algorithm.
 *
 * @author Timothy Jones
 * @date 2018-08-10
 */
public class CircuitRules extends AbstractRules {

  private Map<String, String> names;
  private Collection<String> rules;
  private RuleTree tree;
  private Collection<String> acceptedFixedObjects;

  /**
   * Getter for {@code acceptedFixedObjects}.
   *
   * @return The value of {@code acceptedFixedObjects}.
   */
  public Collection<String> getAcceptedFixedObjects() {
    return acceptedFixedObjects;
  }

  /**
   * Rename the device in a rule.
   *
   * @param rule The rule.
   * @param devices The list of devices that should be renamed in the rule.
   * @return The rule with renamed devices.
   */
  private String renameDevices(final String rule, final Collection<String> devices) {
    String rtn = null;
    final StringTokenizer st = new StringTokenizer(rule);
    final Collection<String> tokens = new ArrayList<>();
    while (st.hasMoreTokens()) {
      final String token = st.nextToken();
      if (devices.contains(token)) {
        tokens.add(token + "Device");
      } else {
        tokens.add(token);
      }
    }
    rtn = String.join(" ", tokens);
    return rtn;
  }

  private void filterBlock(
      final RuleTreeNode node,
      final Collection<String> devices,
      final TargetDataInstance tdi,
      final StringBuilder builder,
      final Namer namer) {
    final String op = node.getFunction().getOperator();
    builder.append("(");
    final Collection<String> accepted = new ArrayList<>();
    accepted.addAll(devices);
    accepted.addAll(acceptedFixedObjects);
    for (GeneticLocation l : tdi.getGeneticLocations()) {
      accepted.add(l.getName());
    }
    if (node instanceof ParentRuleTreeNode) {
      ParentRuleTreeNode parent = (ParentRuleTreeNode) node;
      for (final RuleTreeNode child : parent.getChildren()) {
        filterBlock(child, devices, tdi, builder, namer);
        builder.append(op);
      }
    } else {
      TerminalRuleTreeNode terminal = (TerminalRuleTreeNode) node;
      Boolean keep = isFixedScarPlacementTemplate(terminal, tdi);
      for (final String str : terminal.getRules()) {
        final Collection<String> stuff = EugeneRules.getObjects(str);
        if (!keep
            && !accepted.containsAll(stuff)
            && !EugeneRules.GlobalOrientationRuleKeywords.contains(str)) {
          continue;
        }
        final String name = namer.next();
        final String rule = renameDevices(str, devices);
        getNames().put(name, rule);
        builder.append(name);
        builder.append(op);
      }
    }
    trim(builder);
    builder.append(")");
    trim(builder);
  }

  /**
   * Build an individual (Eugene) <code>Rule</code> block.
   *
   * @param st A <code>{@link java.util.StringTokenizer StringTokenizer}</code> instantiated with
   *     the DNF rule set.
   * @param names A map from a name in the DNF to the device name.
   * @param num A numeral for the rule used in naming.
   * @return A string representation of the Rule block.
   */
  protected String buildRule(
      final Collection<String> devices,
      final StringTokenizer st,
      final Map<String, String> names,
      final int num) {
    String rtn = "";
    rtn += "Rule CircuitRule" + String.valueOf(num) + "( ON circuit:" + Utils.getNewLine();
    for (String device : devices) {
      String rule = "CONTAINS " + device + " AND";
      String newRule = this.renameDevices(rule, devices);
      rtn += Utils.getTabCharacter() + newRule + Utils.getNewLine();
    }
    while (st.hasMoreTokens()) {
      final String t = st.nextToken();
      if (t.equals("|")) {
        break;
      }
      if (t.equals("&")) {
        rtn += " " + EugeneRules.S_AND + Utils.getNewLine();
        continue;
      }
      final String rule = names.get(t);
      rtn += Utils.getTabCharacter() + rule;
    }
    rtn += Utils.getNewLine() + ");" + Utils.getNewLine();
    return rtn;
  }

  /**
   * Generate all (Eugene) Rule blocks from the DNF.
   *
   * @param st The <code>{@link java.util.StringTokenizer StringTokenizer}</code> instantiated with
   *     the DNF rule set.
   * @param names A map from a name in the DNF to the device name.
   */
  private void buildRules(
      final Collection<String> devices, final StringTokenizer st, final Map<String, String> names) {
    int i = 0;
    while (st.hasMoreTokens()) {
      getRules().add(buildRule(devices, st, names, i));
      i++;
    }
  }

  private void filterCircuitRules(
      final RuleTree tree, final Collection<String> devices, final TargetDataInstance tdi) {
    final RuleTreeNode root = tree.getRoot();
    final StringBuilder builder = new StringBuilder();
    final Namer namer = new Namer();
    filterBlock(root, devices, tdi, builder, namer);
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
    buildRules(devices, st, getNames());
  }

  private void init() {
    rules = new ArrayList<>();
    names = new HashMap<>();
    acceptedFixedObjects = new ArrayList<>();
  }

  /**
   * Initializes a newly created {@link CircuitRules}.
   *
   * @param jObj The JSON object.
   * @throws JsonMappingException Unable to map to JSON.
   * @throws JsonProcessingException Unable to convert JSON to object.
   */
  public CircuitRules(final JSONObject jObj) throws JsonMappingException, JsonProcessingException {
    init();
    ObjectMapper mapper = new ObjectMapper();
    tree = mapper.readValue(jObj.toJSONString(), RuleTree.class);
  }

  private class SortBySlot implements Comparator<String> {
    private final Pattern r = Pattern.compile("\\[(\\d+)\\] " + EugeneRules.S_EQUALS + " (\\w+)");

    public int compare(String a, String b) {
      Matcher ma = r.matcher(a);
      ma.matches();
      Matcher mb = r.matcher(b);
      mb.matches();
      return Integer.valueOf(ma.group(1)) - Integer.valueOf(mb.group(1));
    }
  }

  private List<String> getFixedScarPlacementRules(
      final TerminalRuleTreeNode node,
      final Collection<String> devices,
      final TargetDataInstance tdi) {
    List<String> rtn = new ArrayList<>();
    Map<GeneticLocation, Queue<Integer>> slots = new LinkedHashMap<>();
    Map<GeneticLocation, Integer> locationIndices = new HashMap<>();
    Map<GeneticLocation, Integer> lastFilled = new HashMap<>();
    List<String> rules = node.getRules();
    Collections.sort(rules, new SortBySlot());
    Pattern r = Pattern.compile("\\[(\\d+)\\] " + EugeneRules.S_EQUALS + " (\\w+)");
    GeneticLocation loc = null;
    Integer lastIdx = 0;
    for (final String rule : rules) {
      Matcher m = r.matcher(rule);
      m.matches();
      String obj = m.group(2);
      Integer idx = Integer.valueOf(m.group(1));
      GeneticLocation temp = tdi.getGeneticLocations().findCObjectByName(obj);
      for (int i = lastIdx + 1; i < idx; i++) {
        slots.get(loc).add(i);
      }
      lastIdx = idx;
      if (temp != null) {
        loc = temp;
        slots.put(loc, new ArrayDeque<>());
        locationIndices.put(loc, idx);
      }
    }
    List<GeneticLocation> keys = new CircularLinkedList<GeneticLocation>(slots.keySet());
    Iterator<GeneticLocation> it = keys.iterator();
    for (final String device : devices) {
      GeneticLocation key = it.next();
      Queue<Integer> q = slots.get(key);
      Integer last = q.poll();
      lastFilled.put(key, last);
    }
    Integer next = 0;
    for (GeneticLocation key : slots.keySet()) {
      int first = locationIndices.get(key);
      int offset = next - first;
      int last = lastFilled.get(key);
      Integer idx = 0;
      for (final String rule : rules) {
        Matcher m = r.matcher(rule);
        m.matches();
        idx = Integer.valueOf(m.group(1));
        if (idx >= first && idx < last) {
          String newRule =
              rule.replace(
                  "[" + String.valueOf(idx) + "]", "[" + String.valueOf(idx + offset) + "]");
          rtn.add(newRule);
        }
      }
      next = lastFilled.get(key) + 1;
    }
    for (final String rule : rtn) {
      Collection<String> objs = EugeneRules.getObjects(rule);
      Pattern p = Pattern.compile("\\[\\d+\\]");
      for (final String obj : objs) {
        Matcher m = p.matcher(rule);
        if (!m.matches()) {
          this.acceptedFixedObjects.add(obj);
        }
      }
    }
    return rtn;
  }

  private Boolean isFixedScarPlacementTemplate(
      final TerminalRuleTreeNode node, final TargetDataInstance tdi) {
    Boolean rtn = true;
    if (!node.getFunction().equals(RuleTreeFunction.AND)) {
      return rtn;
    }
    for (final String rule : node.getRules()) {
      Pattern r = Pattern.compile("\\[(\\d+)\\] " + EugeneRules.S_EQUALS + " (\\w+)");
      Matcher m = r.matcher(rule);
      if (!m.matches()) {
        rtn = false;
        break;
      }
      String obj = m.group(2);
      GeneticLocation loc = tdi.getGeneticLocations().findCObjectByName(obj);
      Part scar = tdi.getParts().findCObjectByName(obj);
      if (m.group(1).equals("0") && loc == null) {
        // First slot should be a genetic location
        rtn = false;
        break;
      } else if ((scar == null || !scar.getPartType().equals(Part.S_SCAR)) && loc == null) {
        // Other slots can be scar or location
        rtn = false;
        break;
      }
    }
    return rtn;
  }

  /**
   * Get a new rule tree, modified for fixed scar placement, given a collection of devices.
   *
   * @param devices The device names.
   * @param tdi The target data instance.
   * @return
   */
  private RuleTree getFixedScarPlacementRuleTree(
      Collection<String> devices, TargetDataInstance tdi) {
    RuleTree rtn = new RuleTree(tree);
    RuleTreeNode root = rtn.getRoot();
    if (root instanceof TerminalRuleTreeNode) {
      TerminalRuleTreeNode terminal = (TerminalRuleTreeNode) root;
      if (isFixedScarPlacementTemplate(terminal, tdi)) {
        List<String> rules = getFixedScarPlacementRules(terminal, devices, tdi);
        terminal.setRules(rules);
      }
    } else if (root instanceof ParentRuleTreeNode) {
      ParentRuleTreeNode parent = (ParentRuleTreeNode) root;
      for (RuleTreeNode child : parent.getChildren()) {
        if (child instanceof TerminalRuleTreeNode) {
          TerminalRuleTreeNode terminal = (TerminalRuleTreeNode) child;
          if (isFixedScarPlacementTemplate(terminal, tdi)) {
            List<String> rules = getFixedScarPlacementRules(terminal, devices, tdi);
            terminal.setRules(rules);
            break;
          }
        }
      }
    }
    return rtn;
  }

  /**
   * Filter rules for a given collection of devices.
   *
   * @param devices The device names.
   * @param tdi The target data instance.
   * @return The rules.
   */
  public String filter(final Collection<String> devices, final TargetDataInstance tdi) {
    RuleTree tree = getFixedScarPlacementRuleTree(devices, tdi);
    filterCircuitRules(tree, devices, tdi);
    return String.join(Utils.getNewLine(), getRules());
  }

  /*
   * rules
   */
  private Collection<String> getRules() {
    return rules;
  }

  private Map<String, String> getNames() {
    return names;
  }
}

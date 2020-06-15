/*
 * Copyright (C) 2020 Boston University (BU)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package org.cellocad.v2.common.target.data.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSetter;
import java.util.ArrayList;
import java.util.List;

/**
 * A terminal node in a {@link RuleTreeNode}, a node that contains a list of rules and no more
 * groups.
 *
 * @author Timothy Jones
 * @date 2020-06-15
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class TerminalRuleTreeNode extends RuleTreeNode {

  private List<String> rules;

  /** Initializes a newly created {@link TerminalRuleTreeNode}. */
  public TerminalRuleTreeNode() {}

  /**
   * Initializes a newly created {@link TerminalRuleTreeNode}.
   *
   * @param other The other node.
   */
  public TerminalRuleTreeNode(final TerminalRuleTreeNode other) {
    this();
    this.setFunction(other.getFunction());
    this.setRules(new ArrayList<>(other.getRules()));
  }

  /**
   * Getter for {@code rules}.
   *
   * @return The value of {@code rules}.
   */
  public List<String> getRules() {
    return rules;
  }

  /**
   * Setter for {@code rules}.
   *
   * @param rules The value to set {@code rules}.
   */
  @JsonSetter("rules")
  public void setRules(List<String> rules) {
    this.rules = rules;
  }
}

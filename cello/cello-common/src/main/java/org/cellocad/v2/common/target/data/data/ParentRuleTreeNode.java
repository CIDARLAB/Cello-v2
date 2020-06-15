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

import com.fasterxml.jackson.annotation.JsonSetter;
import java.util.ArrayList;
import java.util.Collection;

/**
 * A parent node in a {@link RuleTree}, a node that contains groups of rules.
 *
 * @author Timothy Jones
 * @date 2020-06-15
 */
public class ParentRuleTreeNode extends RuleTreeNode {

  private Collection<RuleTreeNode> children;

  /** Initializes a newly created {@link ParentRuleTreeNode}. */
  public ParentRuleTreeNode() {}

  /**
   * Initializes a newly created {@link ParentRuleTreeNode}.
   *
   * @param other The other node.
   */
  public ParentRuleTreeNode(final ParentRuleTreeNode other) {
    this();
    this.setFunction(other.getFunction());
    this.setChildren(new ArrayList<RuleTreeNode>());
    for (RuleTreeNode node : other.getChildren()) {
      if (node instanceof TerminalRuleTreeNode) {
        TerminalRuleTreeNode terminal = (TerminalRuleTreeNode) node;
        this.getChildren().add(new TerminalRuleTreeNode(terminal));
      } else if (node instanceof ParentRuleTreeNode) {
        ParentRuleTreeNode parent = (ParentRuleTreeNode) node;
        this.getChildren().add(new ParentRuleTreeNode(parent));
      }
    }
  }

  /**
   * Getter for {@code children}.
   *
   * @return The value of {@code children}.
   */
  public Collection<RuleTreeNode> getChildren() {
    return children;
  }

  /**
   * Setter for {@code children}.
   *
   * @param children The value to set {@code children}.
   */
  @JsonSetter("rules")
  public void setChildren(Collection<RuleTreeNode> children) {
    this.children = children;
  }
}

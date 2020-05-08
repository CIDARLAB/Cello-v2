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
import java.util.Arrays;
import java.util.Collection;
import java.util.StringTokenizer;

/**
 * A collection of Eugene-related constants and methods for interacting with them.
 *
 * @author Timothy Jones
 * @date 2020-01-13
 */
public final class EugeneRules {

  /**
   * Get the first occurance of a Eugene keyword in the given rule.
   *
   * @param rule A Eugene rule string.
   * @return The first occurance of a Eugene keyword in the given rule.
   */
  public static String getKeyword(final String rule) {
    String rtn = null;
    final StringTokenizer st = new StringTokenizer(rule);
    while (st.hasMoreTokens()) {
      final String str = st.nextToken();
      if (EugeneRules.ValidRuleKeywords.contains(str.toUpperCase())) {
        rtn = str;
        break;
      }
    }
    return rtn;
  }

  /**
   * Get the non-keyword strings in the given rule.
   *
   * @param rule A Eugene rule.
   * @return A collection of the non-keyword strings in the given rule.
   */
  public static Collection<String> getObjects(final String rule) {
    final Collection<String> rtn = new ArrayList<>();
    final StringTokenizer st = new StringTokenizer(rule);
    while (st.hasMoreTokens()) {
      final String str = st.nextToken();
      if (!EugeneRules.ValidRuleKeywords.contains(str.toUpperCase())) {
        rtn.add(str);
      }
    }
    return rtn;
  }

  /*
   * keywords
   */
  // counting
  public static final String S_CONTAINS = "CONTAINS";
  public static final String S_NOTCONTAINS = "NOTCONTAINS";
  public static final String S_EXACTLY = "EXACTLY";
  public static final String S_NOTEXACTLY = "NOTEXACTLY";
  public static final String S_MORETHAN = "MORETHAN";
  public static final String S_NOTMORETHAN = "NOTMORETHAN";
  public static final String S_SAMECOUNT = "SAME_COUNT";
  public static final String S_WITH = "WITH";
  public static final String S_NOTWITH = "NOTWITH";
  public static final String S_THEN = "THEN";
  // positioning
  public static final String S_STARTSWITH = "STARTSWITH";
  public static final String S_ENDSWITH = "ENDSWITH";
  public static final String S_AFTER = "AFTER";
  public static final String S_ALLAFTER = "ALL_AFTER";
  public static final String S_SOMEAFTER = "SOME_AFTER";
  public static final String S_BEFORE = "BEFORE";
  public static final String S_ALLBEFORE = "ALL_BEFORE";
  public static final String S_SOMEBEFORE = "SOME_BEFORE";
  public static final String S_NEXTTO = "NEXTTO";
  public static final String S_ALLNEXTTO = "ALL_NEXTTO";
  public static final String S_SOMENEXTTO = "SOME_NEXTTO";
  // pairing
  public static final String S_EQUALS = "EQUALS";
  public static final String S_NOTEQUALS = "NOTEQUALS";
  // orientation
  public static final String S_ALLFORWARD = "ALL_FORWARD";
  public static final String S_ALLREVERSE = "ALL_REVERSE";
  public static final String S_FORWARD = "FORWARD";
  public static final String S_REVERSE = "REVERSE";
  public static final String S_SAMEORIENTATION = "SAME_ORIENTATION";
  public static final String S_ALLSAMEORIENTATION = "ALL_SAME_ORIENTATION";
  public static final String S_ALTERNATEORIENTATION = "ALTERNATE_ORIENTATION";
  // interaction
  public static final String S_REPRESSES = "REPRESSES";
  public static final String S_INDUCES = "INDUCES";
  public static final String S_DRIVES = "DRIVES";
  // logic
  public static final String S_NOT = "NOT";
  public static final String S_AND = "AND";
  public static final String S_OR = "OR";

  /** ValidNodeTypes: Array of Strings containing valid rule keyword. */
  public static final Collection<String> ValidRuleKeywords =
      Arrays.asList(
          EugeneRules.S_CONTAINS,
          EugeneRules.S_NOTCONTAINS,
          EugeneRules.S_EXACTLY,
          EugeneRules.S_NOTEXACTLY,
          EugeneRules.S_MORETHAN,
          EugeneRules.S_NOTMORETHAN,
          EugeneRules.S_SAMECOUNT,
          EugeneRules.S_WITH,
          EugeneRules.S_NOTWITH,
          EugeneRules.S_THEN,
          EugeneRules.S_STARTSWITH,
          EugeneRules.S_ENDSWITH,
          EugeneRules.S_AFTER,
          EugeneRules.S_ALLAFTER,
          EugeneRules.S_SOMEAFTER,
          EugeneRules.S_BEFORE,
          EugeneRules.S_ALLBEFORE,
          EugeneRules.S_SOMEBEFORE,
          EugeneRules.S_NEXTTO,
          EugeneRules.S_ALLNEXTTO,
          EugeneRules.S_SOMENEXTTO,
          EugeneRules.S_EQUALS,
          EugeneRules.S_NOTEQUALS,
          EugeneRules.S_ALLFORWARD,
          EugeneRules.S_ALLREVERSE,
          EugeneRules.S_FORWARD,
          EugeneRules.S_REVERSE,
          EugeneRules.S_SAMEORIENTATION,
          EugeneRules.S_ALLSAMEORIENTATION,
          EugeneRules.S_ALTERNATEORIENTATION,
          EugeneRules.S_REPRESSES,
          EugeneRules.S_INDUCES,
          EugeneRules.S_DRIVES,
          EugeneRules.S_NOT,
          EugeneRules.S_AND,
          EugeneRules.S_OR);

  public static final Collection<String> GlobalOrientationRuleKeywords =
      Arrays.asList(
          EugeneRules.S_ALLFORWARD,
          EugeneRules.S_ALLREVERSE,
          EugeneRules.S_ALLSAMEORIENTATION,
          EugeneRules.S_ALTERNATEORIENTATION);
}

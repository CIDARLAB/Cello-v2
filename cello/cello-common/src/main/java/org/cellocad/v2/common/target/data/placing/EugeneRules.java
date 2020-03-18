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
package org.cellocad.v2.common.target.data.placing;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.StringTokenizer;

/**
 *
 *
 * @author Timothy Jones
 *
 * @date 2020-01-13
 *
 */
public final class EugeneRules {

	public static String getKeyword(String rule) {
		String rtn = null;
		StringTokenizer st = new StringTokenizer(rule);
		while (st.hasMoreTokens()) {
			String str = st.nextToken();
			if (ValidRuleKeywords.contains(str.toUpperCase())) {
				rtn = str;
				break;
			}
		}
		return rtn;
	}

	public static Collection<String> getObjects(String rule) {
		Collection<String> rtn = new ArrayList<>();
		StringTokenizer st = new StringTokenizer(rule);
		while (st.hasMoreTokens()) {
			String str = st.nextToken();
			if (!ValidRuleKeywords.contains(str.toUpperCase())) {
				rtn.add(str);
			}
		}
		return rtn;
	}

	/*
	 * keywords
	 */
	// counting
	static public final String S_CONTAINS = "CONTAINS";
	static public final String S_NOTCONTAINS = "NOTCONTAINS";
	static public final String S_EXACTLY = "EXACTLY";
	static public final String S_NOTEXACTLY = "NOTEXACTLY";
	static public final String S_MORETHAN = "MORETHAN";
	static public final String S_NOTMORETHAN = "NOTMORETHAN";
	static public final String S_SAMECOUNT = "SAME_COUNT";
	static public final String S_WITH = "WITH";
	static public final String S_NOTWITH = "NOTWITH";
	static public final String S_THEN = "THEN";
	// positioning
	static public final String S_STARTSWITH = "STARTSWITH";
	static public final String S_ENDSWITH = "ENDSWITH";
	static public final String S_AFTER = "AFTER";
	static public final String S_ALLAFTER = "ALL_AFTER";
	static public final String S_SOMEAFTER = "SOME_AFTER";
	static public final String S_BEFORE = "BEFORE";
	static public final String S_ALLBEFORE = "ALL_BEFORE";
	static public final String S_SOMEBEFORE = "SOME_BEFORE";
	static public final String S_NEXTTO = "NEXTTO";
	static public final String S_ALLNEXTTO = "ALL_NEXTTO";
	static public final String S_SOMENEXTTO = "SOME_NEXTTO";
	// pairing
	static public final String S_EQUALS = "EQUALS";
	static public final String S_NOTEQUALS = "NOTEQUALS";
	// orientation
	static public final String S_ALLFORWARD = "ALL_FORWARD";
	static public final String S_ALLREVERSE = "ALL_REVERSE";
	static public final String S_FORWARD = "FORWARD";
	static public final String S_REVERSE = "REVERSE";
	static public final String S_SAMEORIENTATION = "SAME_ORIENTATION";
	static public final String S_ALLSAMEORIENTATION = "ALL_SAME_ORIENTATION";
	static public final String S_ALTERNATEORIENTATION = "ALTERNATE_ORIENTATION";
	// interaction
	static public final String S_REPRESSES = "REPRESSES";
	static public final String S_INDUCES = "INDUCES";
	static public final String S_DRIVES = "DRIVES";
	// logic
	static public final String S_NOT = "NOT";
	static public final String S_AND = "AND";
	static public final String S_OR = "OR";

	/**
	 * ValidNodeTypes: Array of Strings containing valid rule keyword
	 */
	public static final Collection<String> ValidRuleKeywords = Arrays.asList(S_CONTAINS, S_NOTCONTAINS, S_EXACTLY,
			S_NOTEXACTLY, S_MORETHAN, S_NOTMORETHAN, S_SAMECOUNT, S_WITH, S_NOTWITH, S_THEN, S_STARTSWITH, S_ENDSWITH,
			S_AFTER, S_ALLAFTER, S_SOMEAFTER, S_BEFORE, S_ALLBEFORE, S_SOMEBEFORE, S_NEXTTO, S_ALLNEXTTO, S_SOMENEXTTO,
			S_EQUALS, S_NOTEQUALS, S_ALLFORWARD, S_ALLREVERSE, S_FORWARD, S_REVERSE, S_SAMEORIENTATION,
			S_ALLSAMEORIENTATION, S_ALTERNATEORIENTATION, S_REPRESSES, S_INDUCES, S_DRIVES, S_NOT, S_AND, S_OR);
	public static final Collection<String> GlobalOrientationRuleKeywords = Arrays.asList(S_ALLFORWARD, S_ALLREVERSE,
			S_ALLSAMEORIENTATION, S_ALTERNATEORIENTATION);

}

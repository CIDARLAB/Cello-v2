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

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 * Rules is a class representing the rules for part and gate placement <i>Eugene</i> algorithm.
 * 
 * @author Timothy Jones
 * 
 * @date 2018-08-10
 *
 */
public class Rules {
	
	private void parseGateRules(final JSONObject jObj) {
		JSONArray jArr = (JSONArray) jObj.get("eugene_gate_rules");
		for (int i = 0; i < jArr.size(); i++) {
			String rule = (String) jArr.get(i);
			this.getGateRules().add(rule);
		}
	}
	
	private void parsePartRules(final JSONObject jObj) {
		JSONArray jArr = (JSONArray) jObj.get("eugene_part_rules");
		for (int i = 0; i < jArr.size(); i++) {
			String rule = (String) jArr.get(i);
			this.getPartRules().add(rule);
		}
	}
	
	private void init() {
		this.setGateRules(new ArrayList<String>());
		this.setPartRules(new ArrayList<String>());
	}
	
	public Rules(final JSONObject jObj) {
		init();
		this.parseGateRules(jObj);
		this.parsePartRules(jObj);
	}
	
	/*
	 * gateRules
	 */
	public void setGateRules(final Collection<String> gateRules){
		this.gateRules = gateRules;
	}
	
	public Collection<String> getGateRules(){
		return this.gateRules;
	}
	
	private Collection<String> gateRules;
	
	/*
	 * partRules
	 */
	public void setPartRules(final Collection<String> partRules){
		this.partRules = partRules;
	}
	
	public Collection<String> getPartRules(){
		return this.partRules;
	}
	
	private Collection<String> partRules;
	
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
	 *  ValidNodeTypes: Array of Strings containing valid rule keyword
	 */
	public static final String[] ValidRuleKeywords =
		{
			Rules.S_CONTAINS,
			Rules.S_NOTCONTAINS,
			Rules.S_EXACTLY,
			Rules.S_NOTEXACTLY,
			Rules.S_MORETHAN,
			Rules.S_NOTMORETHAN,
			Rules.S_SAMECOUNT,
			Rules.S_WITH,
			Rules.S_NOTWITH,
			Rules.S_THEN,
			Rules.S_STARTSWITH,
			Rules.S_ENDSWITH,
			Rules.S_AFTER,
			Rules.S_ALLAFTER,
			Rules.S_SOMEAFTER,
			Rules.S_BEFORE,
			Rules.S_ALLBEFORE,
			Rules.S_SOMEBEFORE,
			Rules.S_NEXTTO,
			Rules.S_ALLNEXTTO,
			Rules.S_SOMENEXTTO,
			Rules.S_EQUALS,
			Rules.S_NOTEQUALS,
			Rules.S_ALLFORWARD,
			Rules.S_ALLREVERSE,
			Rules.S_FORWARD,
			Rules.S_REVERSE,
			Rules.S_SAMEORIENTATION,
			Rules.S_ALLSAMEORIENTATION,
			Rules.S_ALTERNATEORIENTATION,
			Rules.S_REPRESSES,
			Rules.S_INDUCES,
			Rules.S_DRIVES,
			Rules.S_NOT,
			Rules.S_AND,
			Rules.S_OR
		};

}

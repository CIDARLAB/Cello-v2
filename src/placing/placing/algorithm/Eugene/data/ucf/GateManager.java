/**
 * Copyright (C) 2017 Massachusetts Institute of Technology (MIT)
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
package placing.algorithm.Eugene.data.ucf;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import common.CObject;
import common.CObjectCollection;

/**
 * The GateManager class is class managing the gate(s) for the gate assignment in the <i>SimulatedAnnealing</i> algorithm.
 * 
 * @author Vincent Mirian
 * 
 * @date 2018-05-21
 *
 */
public class GateManager extends CObject{

	/*
	 * init
	 */
	private void init() {
		this.random = new Random(GateManager.L_SEED);
		this.gates = new CObjectCollection<Gate>();
		this.assignedGates = new CObjectCollection<Gate>();
		this.unassignedGates = new CObjectCollection<Gate>();
		this.gatesMap = new HashMap<String, CObjectCollection<Gate>>();
		this.numAssignedGatesGroupMap = new HashMap<String, Integer>();
		this.numUnassignedGatesGroupMap = new HashMap<String, Integer>();
		this.assignedGatesMap = new HashMap<String, CObjectCollection<Gate>>();
		this.unassignedGatesMap = new HashMap<String, CObjectCollection<Gate>>();
	}

	private void addGroups(final Map<String, CObjectCollection<Gate>> map, final CObjectCollection<Gate> gates) {
		CObjectCollection<Gate> list = null;
		for (int i = 0; i < gates.size(); i ++) {
			Gate gate = gates.get(i);
			String name = gate.getGroupName();
			list = map.get(name);
			if (list == null) {
				list = new CObjectCollection<Gate>();
				map.put(name, list);
			}
		}
	}
	
	private void addNumGroups(final Map<String, Integer> map, final CObjectCollection<Gate> gates) {
		Integer value = 0;
		for (int i = 0; i < gates.size(); i ++) {
			Gate gate = gates.get(i);
			String name = gate.getGroupName();
			value = map.get(name);
			if (value == null) {
				value = new Integer(GateManager.I_ZERO);
				map.put(name, value);
			}
		}
	}

	private void addGatesToGroup(final Map<String, CObjectCollection<Gate>> map, final CObjectCollection<Gate> gates) {
		CObjectCollection<Gate> list = null;
		for (int i = 0; i < gates.size(); i ++) {
			Gate gate = gates.get(i);
			String name = gate.getGroupName();
			list = map.get(name);
			if (list == null) {
				throw new RuntimeException("Group name does not exist!");
			}
			list.add(gate);
		}
	}
	
	private void addNumGatesToGroup(final Map<String, Integer> map, final CObjectCollection<Gate> gates) {
		Integer value = null;
		for (int i = 0; i < gates.size(); i ++) {
			Gate gate = gates.get(i);
			String name = gate.getGroupName();
			value = map.get(name);
			map.put(name, value + 1);
		}
	}
	
	/*
	 * Constructor
	 */
	public GateManager (final CObjectCollection<Gate> gates){
		init();
		this.getGates().addAll(gates);
		this.getUnassignedGates().addAll(gates);
		this.addGroups(this.getGatesMap(), gates);
		this.addNumGroups(this.getNumAssignedGatesGroupMap(), gates);
		this.addNumGroups(this.getNumUnassignedGatesGroupMap(), gates);
		this.addGroups(this.getAssignedGatesMap(), gates);
		this.addGroups(this.getUnassignedGatesMap(), gates);
		this.addGatesToGroup(this.getGatesMap(), gates);
		this.addGatesToGroup(this.getUnassignedGatesMap(), gates);
		this.addNumGatesToGroup(this.getNumUnassignedGatesGroupMap(), gates);
	}

	/*
	 * CObjectCollection
	 */
	private Gate getRandomGate(final CObjectCollection<Gate> gates) {
		Gate rtn = null;
		int size = this.getNumGate(gates);
		if (size > 0) {
			rtn = gates.get(this.random(0, size - 1));
		}
		return rtn;
	}
	
	private int getNumGate(final CObjectCollection<Gate> gates) {
		int rtn = gates.size();
		return rtn;
	}
	/*
	private Gate getGateAtIdx(final CObjectCollection<Gate> gates, final int index) {
		Gate rtn = null;
		int size = this.getNumGate(gates);
		if (
				(0 <= index)
				&&
				(index < size)
				) {
			gates.get(index);
		}			
		return rtn;
	}
	 */
	/*
	 * Setter
	 */
	private boolean contains(final CObjectCollection<Gate> gates, final Gate gate) {
		boolean rtn = false;
		rtn = gates.contains(gate);			
		return rtn;
	}
	
	private boolean contains(final Map<String, CObjectCollection<Gate>> gates, final Gate gate) {
		boolean rtn = false;
		CObjectCollection<Gate> list = gates.get(gate.getGroupName());
		rtn = this.contains(list, gate);
		return rtn;
	}

	private boolean remove(final CObjectCollection<Gate> gates, final Gate gate) {
		boolean rtn = false;
		rtn = gates.remove(gate);			
		return rtn;
	}
	
	private boolean remove(final Map<String, CObjectCollection<Gate>> gates, final Gate gate) {
		boolean rtn = false;
		CObjectCollection<Gate> list = gates.get(gate.getGroupName());
		rtn = this.remove(list, gate);
		return rtn;
	}

	private boolean add(final CObjectCollection<Gate> gates, final Gate gate) {
		boolean rtn = false;
		rtn = gates.add(gate);			
		return rtn;
	}
	
	private boolean add(final Map<String, CObjectCollection<Gate>> gates, final Gate gate) {
		boolean rtn = false;
		CObjectCollection<Gate> list = gates.get(gate.getGroupName());
		rtn = this.add(list, gate);
		return rtn;
	}

	private void inc(final Map<String, Integer> gates, final Gate gate) {
		String name = gate.getGroupName();
		Integer value = gates.get(name);
		if (value == Integer.MAX_VALUE) {
			throw new RuntimeException("MAX_VALUE!");
		}
		gates.put(name, value + 1);
	}
	
	private void dec(final Map<String, Integer> gates, final Gate gate) {
		String name = gate.getGroupName();
		Integer value = gates.get(name);
		if (value == GateManager.I_ZERO) {
			throw new RuntimeException("IS ZERO!");
		}
		gates.put(name, value - 1);
	}
	
	private enum OP {
		LESS_THAN, LESS_THAN_OR_EQUAL, EQUAL, GREATER_THAN_OR_EQUAL, GREATER_THAN;
		
		public static boolean evaluate(int lh, int rh, OP op) {
			boolean rtn = false;
			if (op == LESS_THAN) {
				rtn = lh < rh; 
			}
			if (op == LESS_THAN_OR_EQUAL) {
				rtn = lh <= rh; 
			}
			if (op == EQUAL) {
				rtn = lh == rh; 
			}
			if (op == GREATER_THAN_OR_EQUAL) {
				rtn = lh >= rh; 
			}
			if (op == GREATER_THAN) {
				rtn = lh > rh; 
			}
			return rtn;
		}
		
	}
	
	private String getRandomGroupGTEq(final Map<String, Integer> gates, final int value, final OP op) {
		String rtn = null;
		Set<Map.Entry<String, Integer>> set = gates.entrySet();
		Iterator<Map.Entry<String, Integer>> iter = set.iterator();
		List<String> select = new ArrayList<String>();
		while(iter.hasNext()) {
			Map.Entry<String, Integer> entry = iter.next();
			if (OP.evaluate(entry.getValue(), value, op))
			{
				select.add(entry.getKey());
			}
		}
		int size = select.size();
		if (size > 0) {
			rtn = select.get(this.random(0, size - 1));
		}
		return rtn;
	}
	
	private boolean setGate(
			final CObjectCollection<Gate> a1,
			final Map<String, CObjectCollection<Gate>> a2,
			final Map<String, Integer> a3,
			final CObjectCollection<Gate> b1,
			final Map<String, CObjectCollection<Gate>> b2,
			final Map<String, Integer> b3,
			final Gate gate) {
		boolean rtn = false;
		// must be: 1) unassigned gates, and, 2) unassigned gates by group
		boolean g1 = this.contains(a1, gate);
		boolean g2 = this.contains(a2, gate);
		if (g1 && g2) {
			// remove from: 1) unassigned gates, and, 2) unassigned gates by group
			g1 = this.remove(a1, gate);
			g2 = this.remove(a2, gate);
			this.dec(a3, gate);
			if (!(g1 && g2)) {
				throw new RuntimeException ("Error!");
			}
			// add to: 1) assigned gates, and, 2) assigned gates by group
			g1 = this.add(b1, gate);
			g2 = this.add(b2, gate);
			this.inc(b3, gate);
			if (!(g1 && g2)) {
				throw new RuntimeException ("Error!");
			}			
		}
		return rtn;
	}

	/*
	 * Map/Group
	 */
	private CObjectCollection<Gate> getGatesByGroup(Map<String, CObjectCollection<Gate>> gates, String group) {
		CObjectCollection<Gate> rtn = null;
		rtn = gates.get(group);
		return rtn;
	}
	
	private Gate getRandomGateByGroup(Map<String, CObjectCollection<Gate>> gates, String group) {
		Gate rtn = null;
		CObjectCollection<Gate> list = this.getGatesByGroup(gates, group);
		rtn = this.getRandomGate(list);
		return rtn;
	}
	/*
	private int getNumGateByGroup(Map<String, CObjectCollection<Gate>> gates, String group) {
		int rtn = 0;
		CObjectCollection<Gate> list = this.getGatesByGroup(gates, group);
		rtn = this.getNumGate(list);
		return rtn;
	}
	
	private Gate getGateAtIdxByGroup(Map<String, CObjectCollection<Gate>> gates, String group, int index) {
		Gate rtn = null;
		CObjectCollection<Gate> list = this.getGatesByGroup(gates, group);
		rtn = this.getGateAtIdx(list, index);
		return rtn;
	}
	*/

	/*
	 * Assigned Gates
	 */
	/*
	public Gate getRandomAssignedGate() {
		Gate rtn = null;
		rtn = this.getRandomGate(this.getAssignedGates());
		return rtn;
	}

	public int getNumAssignedGate() {
		int rtn = this.getNumGate(this.getAssignedGates());
		return rtn;
	}
	
	public Gate getAssignedGateAtIdx(final int index) {
		Gate rtn = null;
		rtn = this.getGateAtIdx(this.getAssignedGates(), index);
		return rtn;
	}
	*/
	public Gate getRandomAssignedGateByGroup(final String group) {
		Gate rtn = null;
		rtn = this.getRandomGateByGroup(this.getAssignedGatesMap(), group);
		return rtn;
	}
	/*
	public int getNumAssignedGateByGroup(final String group) {
		int rtn = 0;
		rtn = this.getNumGateByGroup(this.getAssignedGatesMap(), group);
		return rtn;
	}
	
	public Gate getAssignedGateByGroupAtIdx(final String group, final int index) {
		Gate rtn = null;
		rtn = this.getGateAtIdxByGroup(this.getAssignedGatesMap(), group, index);
		return rtn;
	}
	 */
	/*
	 * Unassigned Gates
	 */
	/*
	public Gate getRandomUnassignedGate() {
		Gate rtn = null;
		rtn = this.getRandomGate(this.getUnassignedGates());
		return rtn;
	}

	public int getNumUnassignedGate() {
		int rtn = this.getNumGate(this.getUnassignedGates());
		return rtn;
	}
	
	public Gate getUnassignedGateAtIdx(final int index) {
		Gate rtn = null;
		rtn = this.getGateAtIdx(this.getUnassignedGates(), index);
		return rtn;
	}*/
	
	public Gate getRandomUnassignedGateByGroup(final String group) {
		Gate rtn = null;
		rtn = this.getRandomGateByGroup(this.getUnassignedGatesMap(), group);
		return rtn;
	}
	/*
	public int getNumUnassignedGateByGroup(final String group) {
		int rtn = 0;
		rtn = this.getNumGateByGroup(this.getUnassignedGatesMap(), group);
		return rtn;
	}
	
	public Gate getUnassignedGateByGroupAtIdx(final String group, final int index) {
		Gate rtn = null;
		rtn = this.getGateAtIdxByGroup(this.getUnassignedGatesMap(), group, index);
		return rtn;
	}*/

	/*
	 * Gates
	 */
	/*
	public Gate getRandomGate() {
		Gate rtn = null;
		rtn = this.getRandomGate(this.getGates());
		return rtn;
	}

	public int getNumGate() {
		int rtn = this.getNumGate(this.getGates());
		return rtn;
	}
	
	public Gate getGateAtIdx(final int index) {
		Gate rtn = null;
		rtn = this.getGateAtIdx(this.getGates(), index);
		return rtn;
	}
	
	public Gate getRandomGateByGroup(final String group) {
		Gate rtn = null;
		this.getRandomGateByGroup(this.getGatesMap(), group);
		return rtn;
	}
	
	public int getNumGateByGroup(final String group) {
		int rtn = 0;
		rtn = this.getNumGateByGroup(this.getGatesMap(), group);
		return rtn;
	}
	
	public Gate getGateByGroupAtIdx(final String group, final int index) {
		Gate rtn = null;
		rtn = this.getGateAtIdxByGroup(this.getGatesMap(), group, index);
		return rtn;
	}
	*/
	
	/*
	 * Group Gate Map
	 */
	public Gate getRandomGateFromUnassignedGroup() {
		Gate rtn = null;
		String group = getRandomGroupGTEq(this.getNumAssignedGatesGroupMap(), GateManager.I_ZERO, OP.EQUAL);
		if (group != null) {
			rtn = this.getRandomUnassignedGateByGroup(group);
		}
		return rtn;
	}

	/*
	 * Setter
	 */
	public boolean setAssignedGate(final Gate gate) {
		boolean rtn = false;
		rtn = this.setGate(
				this.getUnassignedGates(),
				this.getUnassignedGatesMap(),
				this.getNumUnassignedGatesGroupMap(),
				this.getAssignedGates(),
				this.getAssignedGatesMap(),
				this.getNumAssignedGatesGroupMap(),
				gate);
		return rtn;
	}

	public boolean setUnassignedGate(final Gate gate) {
		boolean rtn = false;
		rtn = this.setGate(
				this.getAssignedGates(),
				this.getAssignedGatesMap(),
				this.getNumAssignedGatesGroupMap(),
				this.getUnassignedGates(),
				this.getUnassignedGatesMap(),
				this.getNumUnassignedGatesGroupMap(),
				gate);
		return rtn;
	}
	
	/*
	 * Assigned Gates
	 */
	// group, then gates
	private Map<String, CObjectCollection<Gate>> getAssignedGatesMap(){
		return this.assignedGatesMap;
	}

	// assigned gates by group
	private Map<String, CObjectCollection<Gate>> assignedGatesMap;

	private CObjectCollection<Gate> getAssignedGates(){
		return this.assignedGates;
	}
	// assigned gates
	private CObjectCollection<Gate> assignedGates;
	
	/*
	 * Unassigned Gates
	 */
	// group, then gates
	private Map<String, CObjectCollection<Gate>> getUnassignedGatesMap(){
		return this.unassignedGatesMap;
	}
	// unassigned gates by group
	private Map<String, CObjectCollection<Gate>> unassignedGatesMap;

	private CObjectCollection<Gate> getUnassignedGates(){
		return this.unassignedGates;
	}

	// unassigned gates
	private CObjectCollection<Gate> unassignedGates;

	/*
	 * Num Assigned Gates by group
	 */
	private Map<String, Integer> getNumAssignedGatesGroupMap(){
		return this.numAssignedGatesGroupMap;
	}
	
	private Map<String, Integer> numAssignedGatesGroupMap;
	
	/*
	 * Num Unassigned Gates by group
	 */
	private Map<String, Integer> getNumUnassignedGatesGroupMap(){
		return this.numUnassignedGatesGroupMap;
	}
	
	private Map<String, Integer> numUnassignedGatesGroupMap;
	
	/*
	 * Gates
	 */
	// group, then gates
	private Map<String, CObjectCollection<Gate>> getGatesMap(){
		return this.gatesMap;
	}

	private Map<String, CObjectCollection<Gate>> gatesMap;
	
	private CObjectCollection<Gate> getGates(){
		return this.gates;
	}
	
	private CObjectCollection<Gate> gates;
	
	/*
	 * Random
	 */
	private int random(int min, int max) {
		int rtn = 0;
		Random random = this.getRandom();
		rtn = random.nextInt(max - min + 1) + min;
		return rtn;
	}
	
	private Random getRandom(){
		return this.random;
	}
	
	private Random random;
	/*
	 * constants
	 */
	private static int I_ZERO = 0;
	private static long L_SEED = 21;
}

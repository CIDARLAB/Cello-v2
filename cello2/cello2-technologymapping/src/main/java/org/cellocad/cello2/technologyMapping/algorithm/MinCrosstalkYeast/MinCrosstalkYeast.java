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
package org.cellocad.cello2.technologyMapping.algorithm.MinCrosstalkYeast;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cellocad.cello2.common.CObjectCollection;
import org.cellocad.cello2.results.logicSynthesis.LSResultsUtils;
import org.cellocad.cello2.results.logicSynthesis.netlist.LSResultNetlistUtils;
import org.cellocad.cello2.results.netlist.Netlist;
import org.cellocad.cello2.results.netlist.NetlistEdge;
import org.cellocad.cello2.results.netlist.NetlistNode;
import org.cellocad.cello2.technologyMapping.algorithm.TMAlgorithm;
import org.cellocad.cello2.technologyMapping.algorithm.MinCrosstalkYeast.data.MinCrosstalkYeastDataUtils;
import org.cellocad.cello2.technologyMapping.algorithm.MinCrosstalkYeast.data.MinCrosstalkYeastNetlistData;
import org.cellocad.cello2.technologyMapping.algorithm.MinCrosstalkYeast.data.MinCrosstalkYeastNetlistEdgeData;
import org.cellocad.cello2.technologyMapping.algorithm.MinCrosstalkYeast.data.MinCrosstalkYeastNetlistNodeData;
import org.cellocad.cello2.technologyMapping.algorithm.MinCrosstalkYeast.data.assignment.GateManager;
import org.cellocad.cello2.technologyMapping.algorithm.MinCrosstalkYeast.data.score.ScoreUtils;
import org.cellocad.cello2.technologyMapping.algorithm.MinCrosstalkYeast.data.ucf.AbstractPart;
import org.cellocad.cello2.technologyMapping.algorithm.MinCrosstalkYeast.data.ucf.CasetteParts;
import org.cellocad.cello2.technologyMapping.algorithm.MinCrosstalkYeast.data.ucf.Gate;
import org.cellocad.cello2.technologyMapping.algorithm.MinCrosstalkYeast.data.ucf.Part;
import org.cellocad.cello2.technologyMapping.algorithm.MinCrosstalkYeast.data.ucf.PromoterParts;
import org.cellocad.cello2.technologyMapping.algorithm.MinCrosstalkYeast.data.ucf.ResponseFunction;
import org.cellocad.cello2.technologyMapping.algorithm.MinCrosstalkYeast.data.ucf.ResponseFunctionVariable;
import org.cellocad.cello2.technologyMapping.algorithm.MinCrosstalkYeast.data.ucf.TemplatePart;

/**
 * The MinCrosstalkYeast class implements the <i>MinCrosstalkYeast</i> algorithm in the <i>technologyMapping</i> stage.
 * 
 * @author Timothy Jones
 * 
 * @date Today
 *
 */
public class MinCrosstalkYeast extends TMAlgorithm{

	/**
	 *  Returns the <i>MinCrosstalkYeastNetlistNodeData</i> of the <i>node</i>
	 *
	 *  @param node a node within the <i>netlist</i> of this instance
	 *  @return the <i>MinCrosstalkYeastNetlistNodeData</i> instance if it exists, null otherwise
	 */
	protected MinCrosstalkYeastNetlistNodeData getMinCrosstalkYeastNetlistNodeData(NetlistNode node){
		MinCrosstalkYeastNetlistNodeData rtn = null;
		rtn = (MinCrosstalkYeastNetlistNodeData) node.getNetlistNodeData();
		return rtn;
	}

	/**
	 *  Returns the <i>MinCrosstalkYeastNetlistEdgeData</i> of the <i>edge</i>
	 *
	 *  @param edge an edge within the <i>netlist</i> of this instance
	 *  @return the <i>MinCrosstalkYeastNetlistEdgeData</i> instance if it exists, null otherwise
	 */
	protected MinCrosstalkYeastNetlistEdgeData getMinCrosstalkYeastNetlistEdgeData(NetlistEdge edge){
		MinCrosstalkYeastNetlistEdgeData rtn = null;
		rtn = (MinCrosstalkYeastNetlistEdgeData) edge.getNetlistEdgeData();
		return rtn;
	}

	/**
	 *  Returns the <i>MinCrosstalkYeastNetlistData</i> of the <i>netlist</i>
	 *
	 *  @param netlist the netlist of this instance
	 *  @return the <i>MinCrosstalkYeastNetlistData</i> instance if it exists, null otherwise
	 */
	protected MinCrosstalkYeastNetlistData getMinCrosstalkYeastNetlistData(Netlist netlist){
		MinCrosstalkYeastNetlistData rtn = null;
		rtn = (MinCrosstalkYeastNetlistData) netlist.getNetlistData();
		return rtn;
	}

	/**
	 *  Gets the Constraint data from the NetlistConstraintFile
	 */
	@Override
	protected void getConstraintFromNetlistConstraintFile() {

	}

	/**
	 *  Gets the data from the UCF
	 */
	@Override
	protected void getDataFromUCF() {
		this.setGates(MinCrosstalkYeastDataUtils.getGates(this.getTargetData()));
	}

	/**
	 *  Set parameter(s) value(s) of the algorithm
	 */
	@Override
	protected void setParameterValues() {
	}

	/**
	 *  Validate parameter value of the algorithm
	 */
	@Override
	protected void validateParameterValues() {
		
	}

	/**
	 *  Perform preprocessing
	 */
	@Override
	protected void preprocessing() {
		LSResultNetlistUtils.setVertexTypeUsingLSResult(this.getNetlist());
		this.setGateManager(new GateManager(this.getGates()));
	}

	/**
	 *  Run the (core) algorithm
	 */
	@Override
	protected void run() {
		Double MAXTEMP = 100.0;
        Double MINTEMP = 0.001;

        Integer STEPS = 100;

        Double LOGMAX = Math.log10(MAXTEMP);
        Double LOGMIN = Math.log10(MINTEMP);

        Double LOGINC = (LOGMAX - LOGMIN) / STEPS;

        Integer T0_STEPS = 100;
        
		GateManager GM = this.getGateManager();
		
		for (int j = 0; j < this.getNetlist().getNumVertex(); j++) {
			NetlistNode node = this.getNetlist().getVertexAtIdx(j);
			if (LSResultsUtils.isAllInput(node) || LSResultsUtils.isAllOutput(node)) {
				continue;
			}
			Gate gate = GM.getRandomGateFromUnassignedGroup();
			GM.setAssignedGate(gate);
		}

        for (int j = 0; j < STEPS + T0_STEPS; ++j) {

        	Double LOGTEMP = LOGMAX - j * LOGINC;
        	Double TEMP = Math.pow(10, LOGTEMP);

        	if (j >= STEPS) {
        		TEMP = 0.0;
        	}
    		
    		Double before = ScoreUtils.score(GM.getAssignedGates());
        	
        	// get random gate
        	Gate original = GM.getRandomGateFromAssignedGroup();
        	Gate candidate = GM.getRandomGateFromUnassignedGroup();
        	if (candidate == null) {
    			throw new RuntimeException("Gate assignment error!");
    		}
        	
        	// set gate
        	GM.setUnassignedGate(original);
        	GM.setAssignedGate(candidate);
        	
        	Double after = ScoreUtils.score(GM.getAssignedGates());

        	System.out.println(before);
        	System.out.println(after);
        	System.out.println();
        	
        	// accept or reject
        	Double probability = Math.exp( (after-before) / TEMP ); // e^b
        	Double ep = Math.random();

        	if (ep >= probability) {
        		GM.setUnassignedGate(candidate);
            	GM.setAssignedGate(original);
        	}
        }
        
        CObjectCollection<Gate> gates = GM.getAssignedGates();
        
        int i = 0;
		for (int j = 0; j < this.getNetlist().getNumVertex(); j++) {
			NetlistNode node = this.getNetlist().getVertexAtIdx(j);
			if (LSResultsUtils.isAllInput(node) || LSResultsUtils.isAllOutput(node)) {
				continue;
			}
			Gate gate = gates.get(i);
			MinCrosstalkYeastNetlistNodeData data = this.getMinCrosstalkYeastNetlistNodeData(node);
			data.setGate(gate);
			i++;
		}
	}
	
	private List<String> getNodeParts(NetlistNode node) {
		List<String> rtn = new ArrayList<>();
		MinCrosstalkYeastNetlistNodeData data = this.getMinCrosstalkYeastNetlistNodeData(node);
		Gate gate = (Gate) data.getGate();
		// Gate unused = this.getGateManager().getRandomGateFromUnassignedGroup();
		if (gate != null) {
			node.getResultNetlistNodeData().setGateType(gate.getName());
			PromoterParts parts = gate.getGateParts().getPromoterParts();
			int j = 0;
			for (int i = 0; i < parts.getNumParts(); i++) {
				AbstractPart part = parts.getPartAtIdx(i);
				if (part instanceof Part) {
					rtn.add(part.getName());
				}
				else if (part instanceof TemplatePart
						&& 
						((TemplatePart) part).getPartType().equals(S_OPERATOR)) {
					Gate temp = null;
					if (j < node.getNumInEdge()) {
						NetlistNode src = node.getInEdgeAtIdx(j).getSrc();
						temp = (Gate) this.getMinCrosstalkYeastNetlistNodeData(src).getGate();
						j++;
					}
					if (temp == null) {
						temp = this.getGateManager().getRandomGateFromUnassignedGroup();
						this.getGateManager().setAssignedGate(temp);
					}
					String regulates = temp.getGateParts().getRegulates();
					rtn.add(regulates);
				}
				else {
					throw new RuntimeException("Error with part assignment.");
				}
			}
			ResponseFunction rf = gate.getResponseFunction();
			for (int i = 0; i < rf.getNumVariable(); i++) {
				ResponseFunctionVariable var = rf.getVariableAtIdx(i);
				CasetteParts casetteParts = gate.getGateParts().getCasetteParts(var.getName());
				for (j = 0; j < casetteParts.getNumParts(); j++) {
					Part part = casetteParts.getPartAtIdx(j);
					rtn.add(part.getName());
				}
			}
		}
		return rtn;
	}

	/**
	 *  Perform postprocessing
	 */
	@Override
	protected void postprocessing() {
		for (int i = 0; i < this.getNetlist().getNumVertex(); i++) {
			NetlistNode node = this.getNetlist().getVertexAtIdx(i);
			if (LSResultsUtils.isAllInput(node)) {
				continue;
			}
			List<String> parts = this.getNodeParts(node);
			node.getResultNetlistNodeData().setParts(parts);
		}
	}


	/**
	 *  Returns the Logger for the <i>MinCrosstalkYeast</i> algorithm
	 *
	 *  @return the logger for the <i>MinCrosstalkYeast</i> algorithm
	 */
	@Override
	protected Logger getLogger() {
		return MinCrosstalkYeast.logger;
	}
	
	private static final Logger logger = LogManager.getLogger(MinCrosstalkYeast.class.getSimpleName());
	
	/*
	 * Gate
	 */
	protected void setGates(final CObjectCollection<Gate> gates) {
		this.gates = gates;
	}
	
	public CObjectCollection<Gate> getGates() {
		return this.gates;
	}

	private CObjectCollection<Gate> gates;
	
	/*
	 * GateManager
	 */
	protected void setGateManager(final GateManager gateManager) {
		this.gateManager = gateManager;
	}
	
	public GateManager getGateManager() {
		return this.gateManager;
	}
	
	private GateManager gateManager;
	
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
	private static long L_SEED = 21;
	
	private String S_OPERATOR = "operator";
	
}

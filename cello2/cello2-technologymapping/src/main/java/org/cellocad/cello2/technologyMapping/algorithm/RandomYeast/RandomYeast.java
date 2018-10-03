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
package org.cellocad.cello2.technologyMapping.algorithm.RandomYeast;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cellocad.cello2.common.CObjectCollection;
import org.cellocad.cello2.results.logicSynthesis.LSResultsUtils;
import org.cellocad.cello2.results.logicSynthesis.netlist.LSResultNetlistUtils;
import org.cellocad.cello2.results.netlist.Netlist;
import org.cellocad.cello2.results.netlist.NetlistEdge;
import org.cellocad.cello2.results.netlist.NetlistNode;
import org.cellocad.cello2.technologyMapping.algorithm.TMAlgorithm;
import org.cellocad.cello2.technologyMapping.algorithm.RandomYeast.data.RandomYeastDataUtils;
import org.cellocad.cello2.technologyMapping.algorithm.RandomYeast.data.RandomYeastNetlistData;
import org.cellocad.cello2.technologyMapping.algorithm.RandomYeast.data.RandomYeastNetlistEdgeData;
import org.cellocad.cello2.technologyMapping.algorithm.RandomYeast.data.RandomYeastNetlistNodeData;
import org.cellocad.cello2.technologyMapping.algorithm.RandomYeast.data.assignment.GateManager;
import org.cellocad.cello2.technologyMapping.algorithm.RandomYeast.data.ucf.AbstractPart;
import org.cellocad.cello2.technologyMapping.algorithm.RandomYeast.data.ucf.CasetteParts;
import org.cellocad.cello2.technologyMapping.algorithm.RandomYeast.data.ucf.Gate;
import org.cellocad.cello2.technologyMapping.algorithm.RandomYeast.data.ucf.Part;
import org.cellocad.cello2.technologyMapping.algorithm.RandomYeast.data.ucf.PromoterParts;
import org.cellocad.cello2.technologyMapping.algorithm.RandomYeast.data.ucf.ResponseFunction;
import org.cellocad.cello2.technologyMapping.algorithm.RandomYeast.data.ucf.ResponseFunctionVariable;
import org.cellocad.cello2.technologyMapping.algorithm.RandomYeast.data.ucf.TemplatePart;

/**
 * The RandomYeast class implements the <i>RandomYeast</i> algorithm in the <i>technologyMapping</i> stage.
 * 
 * @author Timothy Jones
 * 
 * @date Today
 *
 */
public class RandomYeast extends TMAlgorithm{

	/**
	 *  Returns the <i>RandomYeastNetlistNodeData</i> of the <i>node</i>
	 *
	 *  @param node a node within the <i>netlist</i> of this instance
	 *  @return the <i>RandomYeastNetlistNodeData</i> instance if it exists, null otherwise
	 */
	protected RandomYeastNetlistNodeData getRandomYeastNetlistNodeData(NetlistNode node){
		RandomYeastNetlistNodeData rtn = null;
		rtn = (RandomYeastNetlistNodeData) node.getNetlistNodeData();
		return rtn;
	}

	/**
	 *  Returns the <i>RandomYeastNetlistEdgeData</i> of the <i>edge</i>
	 *
	 *  @param edge an edge within the <i>netlist</i> of this instance
	 *  @return the <i>RandomYeastNetlistEdgeData</i> instance if it exists, null otherwise
	 */
	protected RandomYeastNetlistEdgeData getRandomYeastNetlistEdgeData(NetlistEdge edge){
		RandomYeastNetlistEdgeData rtn = null;
		rtn = (RandomYeastNetlistEdgeData) edge.getNetlistEdgeData();
		return rtn;
	}

	/**
	 *  Returns the <i>RandomYeastNetlistData</i> of the <i>netlist</i>
	 *
	 *  @param netlist the netlist of this instance
	 *  @return the <i>RandomYeastNetlistData</i> instance if it exists, null otherwise
	 */
	protected RandomYeastNetlistData getRandomYeastNetlistData(Netlist netlist){
		RandomYeastNetlistData rtn = null;
		rtn = (RandomYeastNetlistData) netlist.getNetlistData();
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
		this.setGates(RandomYeastDataUtils.getGates(this.getTargetData()));
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
		for (int i = 0; i < this.getNetlist().getNumVertex(); i++) {
			NetlistNode node = this.getNetlist().getVertexAtIdx(i);
			if (LSResultsUtils.isAllInput(node) || LSResultsUtils.isAllOutput(node)) {
				continue;
			}
			Gate gate = this.getGateManager().getRandomGateFromUnassignedGroup();
			this.getGateManager().setAssignedGate(gate);
			for (int j = 0; j < node.getNumOutEdge(); j++) {
				NetlistEdge edge = node.getOutEdgeAtIdx(j);
				RandomYeastNetlistEdgeData data = this.getRandomYeastNetlistEdgeData(edge);
				data.setGate(gate);
			}
			RandomYeastNetlistNodeData data = this.getRandomYeastNetlistNodeData(node);
			data.setGate(gate);
		}
	}
	
	private List<String> getNodeParts(NetlistNode node) {
		List<String> rtn = new ArrayList<>();
		RandomYeastNetlistNodeData data = this.getRandomYeastNetlistNodeData(node);
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
						temp = (Gate) this.getRandomYeastNetlistNodeData(src).getGate();
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
	 *  Returns the Logger for the <i>RandomYeast</i> algorithm
	 *
	 *  @return the logger for the <i>RandomYeast</i> algorithm
	 */
	@Override
	protected Logger getLogger() {
		return RandomYeast.logger;
	}
	
	private static final Logger logger = LogManager.getLogger(RandomYeast.class.getSimpleName());
	
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
	
	private String S_OPERATOR = "operator";
	
}

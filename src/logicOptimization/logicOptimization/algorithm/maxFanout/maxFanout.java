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
package logicOptimization.algorithm.maxFanout;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import common.graph.algorithm.BFS;
import results.netlist.Netlist;
import results.netlist.NetlistEdge;
import results.netlist.NetlistNode;
import logicOptimization.algorithm.LOAlgorithm;
import logicOptimization.algorithm.maxFanout.data.maxFanoutNetlistData;
import logicOptimization.algorithm.maxFanout.data.maxFanoutNetlistEdgeData;
import logicOptimization.algorithm.maxFanout.data.maxFanoutNetlistNodeData;
import results.logicOptimization.LOResults;
import results.logicSynthesis.LSResultsUtils;
import results.logicSynthesis.netlist.LSResultNetlistUtils;

/**
 * The maxFanout class implements the <i>maxFanout</i> algorithm in the <i>logicOptimization</i> stage.
 * 
 * @author Vincent Mirian
 * 
 * @date 2018-05-21
 *
 */
public class maxFanout extends LOAlgorithm{

	/**
	 *  Returns the <i>maxFanoutNetlistNodeData</i> of the <i>node</i>
	 *
	 *  @param node a node within the <i>netlist</i> of this instance
	 *  @return the <i>maxFanoutNetlistNodeData</i> instance if it exists, null otherwise
	 */
	protected maxFanoutNetlistNodeData getmaxFanoutNetlistNodeData(NetlistNode node){
		maxFanoutNetlistNodeData rtn = null;
		rtn = (maxFanoutNetlistNodeData) node.getNetlistNodeData();
		return rtn;
	}

	/**
	 *  Returns the <i>maxFanoutNetlistEdgeData</i> of the <i>edge</i>
	 *
	 *  @param edge an edge within the <i>netlist</i> of this instance
	 *  @return the <i>maxFanoutNetlistEdgeData</i> instance if it exists, null otherwise
	 */
	protected maxFanoutNetlistEdgeData getmaxFanoutNetlistEdgeData(NetlistEdge edge){
		maxFanoutNetlistEdgeData rtn = null;
		rtn = (maxFanoutNetlistEdgeData) edge.getNetlistEdgeData();
		return rtn;
	}

	/**
	 *  Returns the <i>maxFanoutNetlistData</i> of the <i>netlist</i>
	 *
	 *  @param netlist the netlist of this instance
	 *  @return the <i>maxFanoutNetlistData</i> instance if it exists, null otherwise
	 */
	protected maxFanoutNetlistData getmaxFanoutNetlistData(Netlist netlist){
		maxFanoutNetlistData rtn = null;
		rtn = (maxFanoutNetlistData) netlist.getNetlistData();
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

	}

	/**
	 *  Set parameter(s) value(s) of the algorithm
	 */
	@Override
	protected void setParameterValues() {
		Boolean present = true;
		present = this.getAlgorithmProfile().getIntParameter("max").getFirst();
		if (present) {
			this.setmax(this.getAlgorithmProfile().getIntParameter("max").getSecond());
		}
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
		
	}

	/**
	 *  Run the (core) algorithm
	 */
	@Override
	protected void run() {
		Netlist netlist = this.getNetlist();
		boolean inserted = false;
		int max = this.getmax();
		this.logInfo("Max fanout for node: " + max);
		do {
			BFS<NetlistNode, NetlistEdge, Netlist> bfs = new BFS<NetlistNode, NetlistEdge, Netlist>(netlist);
			NetlistNode node = null;
			inserted = false;
			LSResultNetlistUtils.setVertexTypeUsingLSResult(netlist);
			while ((node = bfs.getNextVertex()) != null) {
				if (LSResultsUtils.isAllInput(node) || LSResultsUtils.isAllOutput(node)) {
					continue;
				}
				int fanout = node.getNumOutEdge(); 
				if (fanout > max) {
					this.logInfo("---------------------------------");
					this.logInfo("Duplicating node " +node.getName());
					inserted = true;
					// minus one to keep original
					int numToAdd = ((int) Math.ceil((double)fanout / max)) - 1;
					int numEdgePerNode = fanout/(numToAdd + 1);
					for (int i = 0; i < numToAdd; i ++) {
						NetlistNode duplicate = new NetlistNode(node);
						duplicate.setName(duplicate.getName() + S_DUPLICATE + i);
						this.logInfo("Duplicating node name " + duplicate.getName());
						// add in edges
						for (int j = 0; j < node.getNumInEdge(); j ++) {
							NetlistEdge edge = node.getInEdgeAtIdx(j);
							NetlistNode src = edge.getSrc();
							// make new edge
							NetlistEdge duplicateEdge = new NetlistEdge(edge);
							duplicateEdge.setName(duplicateEdge.getName() + S_DUPLICATE);
							this.logInfo("Creating edge name " + duplicateEdge.getName());
							this.logInfo("Connecting to source node " + src.getName());
							this.logInfo("Connecting to destination node " + duplicate.getName());
							// set edge source
							duplicateEdge.setSrc(src);
							src.addOutEdge(duplicateEdge);
							// set edge destination
							duplicateEdge.setDst(duplicate);
							duplicate.addInEdge(duplicateEdge);
							// add edge to netlist
							netlist.addEdge(duplicateEdge);
						}
						List<NetlistEdge> edgesToRemove = new ArrayList<NetlistEdge>();
						edgesToRemove.clear();
						// add out edges
						for (int j = 0; (j < fanout) && (j < numEdgePerNode); j ++) {
							NetlistEdge edge = node.getOutEdgeAtIdx(j);
							edgesToRemove.add(edge);
							// set edge source to duplicate node
							edge.setSrc(duplicate);
							duplicate.addOutEdge(edge);
							this.logInfo("Seeting source of edge name " + edge.getName() + " to " + duplicate.getName());
						}
						fanout -= numEdgePerNode;
						// remove out edges
						for (int j = 0; j < edgesToRemove.size(); j++) {
							NetlistEdge edge = edgesToRemove.get(j);
							node.removeOutEdge(edge);
							this.logInfo("Removing edge " + edge.getName() + " from node " + node.getName());
						}
						// add node
						netlist.addVertex(duplicate);
					}
				}
			}
		}while(inserted);
		assert(netlist.isValid());
		if (!netlist.isValid()) {
			throw new RuntimeException ("Netlist not valid!");
		}
	}

	/**
	 *  Perform postprocessing
	 */
	@Override
	protected void postprocessing() {
		
	}

	/**
	 * Setter for <i>max</i>
	 * @param value the value to set <i>max</i>
	*/
	protected void setmax(final int value){
		this.max = value;
	}

	/**
	 * Getter for <i>max</i>
	 * @return value of <i>max</i>
	*/
	protected int getmax(){
		return this.max;
	}

	private int max;


	/**
	 *  Returns the Logger for the <i>maxFanout</i> algorithm
	 *
	 *  @return the logger for the <i>maxFanout</i> algorithm
	 */
	@Override
	protected Logger getLogger() {
		return maxFanout.logger;
	}
	
	private static final Logger logger = LogManager.getLogger(maxFanout.class.getSimpleName());
	
	static private final String S_DUPLICATE = "_Duplicate";
}

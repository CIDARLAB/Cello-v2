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
package results.partitioning;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import common.CObjectCollection;
import common.Utils;
import results.logicOptimization.LOResultsStats;
import results.logicSynthesis.LSResults;
import results.logicSynthesis.LSResultsStats;
import results.logicSynthesis.LSResultsUtils;
import results.netlist.Netlist;
import results.netlist.NetlistNode;
import results.partitioning.block.PTBlockNetlist;

/**
 * The PTBlockNetlistStats class is receives the stats from a PTBlockNetlist instance
 * 
 * @author Vincent Mirian
 * 
 * @date 2018-05-21
 *
 */
public class PTResultsStats {
	
	/**
	 * Returns the minimum value between parameters <i>left</i> and <i>right</i>
	 * 
	 * @param left a value
	 * @param right a value
	 */
	private static int min(int left, int right) {
		int rtn = left;
		if (right < left) {
			rtn = right;
		}
		return rtn;
	}
	
	/**
	 * Returns the maximum value between parameters <i>left</i> and <i>right</i>
	 * 
	 * @param left a value
	 * @param right a value
	 */
	private static int max(int left, int right) {
		int rtn = left;
		if (right > left) {
			rtn = right;
		}
		return rtn;
	}
	
	/**
	 * The <i>getPartitioningStats</i> gets the partitioning stats of netlist defined by parameter <i>myNetlist</i>
	 * 
	 * @param myNetlist the Netlist
	 */
	public static String getPartitioningStats(Netlist myNetlist) {
		String rtn = "";
		rtn += Utils.getNewLine();
		rtn += S_HEADER + Utils.getNewLine();
		rtn += "PartitioningStats" + Utils.getNewLine();
		rtn += S_HEADER + Utils.getNewLine();
		PTBlockNetlist ptBlockNetlist = new PTBlockNetlist(myNetlist);
		Netlist netlist = null;
		int numBlocks = ptBlockNetlist.getNetlistFONum();
		rtn += "Number of Blocks: " + numBlocks + Utils.getNewLine();
		// input
		int minInput = Integer.MAX_VALUE;
		int maxInput = Integer.MIN_VALUE;
		int totalInput = 0;
		int minAInput = Integer.MAX_VALUE;
		int maxAInput = Integer.MIN_VALUE;
		int totalAInput = 0;
		int minPInput = Integer.MAX_VALUE;
		int maxPInput = Integer.MIN_VALUE;
		int totalPInput = 0;
		// output
		int minOutput = Integer.MAX_VALUE;
		int maxOutput = Integer.MIN_VALUE;
		int totalOutput = 0;
		int minAOutput = Integer.MAX_VALUE;
		int maxAOutput = Integer.MIN_VALUE;
		int totalAOutput = 0;
		int minPOutput = Integer.MAX_VALUE;
		int maxPOutput = Integer.MIN_VALUE;
		int totalPOutput = 0;
		// gates
		int minGates = Integer.MAX_VALUE;
		int maxGates = Integer.MIN_VALUE;
		int totalGates = 0;
		for (int i = 0; i < numBlocks;i++) {
			netlist = ptBlockNetlist.getNetlistFOAtIdx(i);
			rtn += S_HEADER + Utils.getNewLine();
			rtn += "Block name: " + netlist.getName() + Utils.getNewLine();
			rtn += LSResultsStats.getLogicSynthesisStats(netlist);
			rtn += LOResultsStats.getLogicOptimizationStats(netlist);
			//
			int numTotalNodeBlock = netlist.getNumVertex();
			int numAInputBlock = LSResultsUtils.getAllInputNodes(netlist).size();
			int numAOutputBlock = LSResultsUtils.getAllOutputNodes(netlist).size();
			int numPInputBlock = LSResultsUtils.getPrimaryInputNodes(netlist).size();
			int numPOutputBlock = LSResultsUtils.getPrimaryOutputNodes(netlist).size();
			int numInputBlock = LSResultsUtils.getPrimaryInputNodes(netlist).size();
			int numOutputBlock = LSResultsUtils.getPrimaryOutputNodes(netlist).size();
			int numGatesBlock = numTotalNodeBlock - numAInputBlock - numAOutputBlock;
			// Input
			minInput = PTResultsStats.min(numInputBlock, minInput);
			maxInput = PTResultsStats.max(numInputBlock, maxInput);
			totalInput += numInputBlock;
			// Output
			minOutput = PTResultsStats.min(numOutputBlock, minOutput);
			maxOutput = PTResultsStats.max(numOutputBlock, maxOutput);
			totalOutput += numOutputBlock;	
			// AInput
			minAInput = PTResultsStats.min(numAInputBlock, minAInput);
			maxAInput = PTResultsStats.max(numAInputBlock, maxAInput);
			totalAInput += numAInputBlock;
			// AOutput
			minAOutput = PTResultsStats.min(numAOutputBlock, minAOutput);
			maxAOutput = PTResultsStats.max(numAOutputBlock, maxAOutput);
			totalAOutput += numAOutputBlock;
			// PInput
			minPInput = PTResultsStats.min(numPInputBlock, minPInput);
			maxPInput = PTResultsStats.max(numPInputBlock, maxPInput);
			totalPInput += numPInputBlock;
			// POutput
			minPOutput = PTResultsStats.min(numPOutputBlock, minPOutput);
			maxPOutput = PTResultsStats.max(numPOutputBlock, maxPOutput);
			totalPOutput += numPOutputBlock;	
			// Gates
			minGates = PTResultsStats.min(numGatesBlock, minGates);
			maxGates = PTResultsStats.max(numGatesBlock, maxGates);
			totalGates += numGatesBlock;
			rtn += S_HEADER + Utils.getNewLine();
			rtn += "Quorum signals" + Utils.getNewLine();
			rtn += S_HEADER + Utils.getNewLine();
			// unique signals
			CObjectCollection<NetlistNode> conns;
			Set<NetlistNode> nodes = new HashSet<NetlistNode>();
			Map<Integer,Integer> PID = new HashMap<Integer,Integer>();
			// output
			conns = LSResultsUtils.getNodeType(netlist,LSResults.S_OUTPUT);
			nodes.clear();
			PID.clear();
			for (int j = 0; j < conns.size(); j++) {
				// get unique output
				NetlistNode node = conns.get(j);
				NetlistNode outputNode = node.getInEdgeAtIdx(0).getSrc();
				nodes.add(outputNode);
				// get number of connection to individual blocks
				String inputName = PTBlockNetlist.getMatchingVIONodeName(node.getName());
				NetlistNode inputNode = ptBlockNetlist.getVirtualLargeNetlistFO().getVertexByName(inputName).getOutEdgeAtIdx(0).getDst();
				int pid = inputNode.getResultNetlistNodeData().getPartitionID();
				Integer count = PID.get(pid);
				if (count == null) {
					count = 0;
				}
				count = count + 1;
				PID.put(pid, count);
			}
			rtn += "Number of unique output signals: " + nodes.size() + Utils.getNewLine();
			rtn += "Signals sent to [PID, count]: " + PID.toString() + Utils.getNewLine();
			// input
			conns = LSResultsUtils.getNodeType(netlist,LSResults.S_INPUT);
			nodes.clear();
			PID.clear();
			for (int j = 0; j < conns.size(); j++) {
				NetlistNode node = conns.get(j);
				NetlistNode inputNode = node.getOutEdgeAtIdx(0).getSrc();
				nodes.add(inputNode);
				String outputName = PTBlockNetlist.getMatchingVIONodeName(node.getName());
				NetlistNode oNode = ptBlockNetlist.getVirtualLargeNetlistFO().getVertexByName(outputName).getInEdgeAtIdx(0).getSrc();
				int pid = oNode.getResultNetlistNodeData().getPartitionID();
				Integer count = PID.get(pid);
				if (count == null) {
					count = 0;
				}
				count = count + 1;
				PID.put(pid, count);
			}
			rtn += "Number of unique input signals: " + nodes.size() + Utils.getNewLine();
			rtn += "Signals sent from [PID, count]: " + PID.toString() + Utils.getNewLine();
		}
		rtn += S_HEADER + Utils.getNewLine();
		rtn += "Minimum number of total inputs to a cell: " + minAInput + ", Maximum number of total inputs to a cell: "; 
		rtn += maxAInput + ", Average number of total inputs per cell: " + ((double)totalAInput/numBlocks) + Utils.getNewLine();
		rtn += "Minimum number of primary inputs to a cell: " + minPInput + ", Maximum number of primary inputs to a cell: "; 
		rtn += maxPInput + ", Average number of primary inputs per cell: " + ((double)totalPInput/numBlocks) + Utils.getNewLine();
		rtn += "Minimum number of inputs to a cell: " + minInput + ", Maximum number of inputs to a cell: "; 
		rtn += maxInput + ", Average number of inputs per cell: " + ((double)totalInput/numBlocks) + Utils.getNewLine();
		rtn += S_HEADER + Utils.getNewLine();
		rtn += "Minimum number of total outputs from a cell: " + minAOutput + ", Maximum number of total outputs from a cell: "; 
		rtn += maxAOutput + ", Average number of total outputs per cell: " + ((double)totalAOutput/numBlocks) + Utils.getNewLine();
		rtn += "Minimum number of primary outputs from a cell: " + minPOutput + ", Maximum number of primary outputs from a cell: "; 
		rtn += maxPOutput + ", Average number of primary outputs per cell: " + ((double)totalPOutput/numBlocks) + Utils.getNewLine();
		rtn += "Minimum number of outputs from a cell: " + minOutput + ", Maximum number of outputs from a cell: "; 
		rtn += maxOutput + ", Average number of outputs per cell: " + ((double)totalOutput/numBlocks) + Utils.getNewLine();
		rtn += S_HEADER + Utils.getNewLine();
		rtn += "Total number of gates within all cells: " + totalGates + ", Minimum number of gates within a cell: " + minGates + ", Maximum number of gates within a cell: "; 
		rtn += maxGates + ", Average number of gates per cell: " + ((double)totalGates/numBlocks) + Utils.getNewLine();
		return rtn;
	}

	private static final String S_HEADER = "--------------------------------------------";
}

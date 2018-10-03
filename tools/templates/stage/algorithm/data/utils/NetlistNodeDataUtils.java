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
package ##NONCE##21##STAGENAME##21##NONCE.algorithm.data.utils;

import common.profile.AlgorithmProfile;
import ##NONCE##21##STAGENAME##21##NONCE.algorithm.data.##NONCE##21##STAGEPREFIX##21##NONCENetlistNodeData;
import ##NONCE##21##STAGENAME##21##NONCE.algorithm.data.##NONCE##21##STAGEPREFIX##21##NONCENetlistNodeDataFactory;
import results.netlist.Netlist;
import results.netlist.NetlistNode;


/**
 * The ##NONCE##21##STAGEPREFIX##21##NONCENetlistNodeDataUtils class is class with utility methods for ##NONCE##21##STAGEPREFIX##21##NONCENetlistNodeData instances in the <i>##NONCE##21##STAGENAME##21##NONCE</i> stage.
 * 
 * @author Vincent Mirian
 * 
 * @date Today
 *
 */
public class ##NONCE##21##STAGEPREFIX##21##NONCENetlistNodeDataUtils {

	/**
	 * Resets the algorithm data, where the algorithm is defined by parameter <i>AProfile</i>,
	 * for all nodes in the netlist instance defined by parameter <i>netlist</i>
	 *
	 * @param netlist the Netlist
	 * @param AProfile the AlgorithmProfile
	 *
	 */
	static public void resetNetlistNodeData(Netlist netlist, AlgorithmProfile AProfile){
		for (int i = 0; i < netlist.getNumVertex(); i++) {
			NetlistNode node = netlist.getVertexAtIdx(i);
			##NONCE##21##STAGEPREFIX##21##NONCENetlistNodeDataUtils.resetNetlistNodeData(node, AProfile);
		}
	}

	/**
	 * Resets the algorithm data, where the algorithm is defined by parameter <i>AProfile</i>,
	 * for a NetlistNode instance defined by parameter <i>node</i>
	 *
	 * @param node the NetlistNode
	 * @param AProfile the AlgorithmProfile
	 *
	 */
	static public void resetNetlistNodeData(NetlistNode node, AlgorithmProfile AProfile){
		##NONCE##21##STAGEPREFIX##21##NONCENetlistNodeDataFactory ##NONCE##21##STAGEPREFIX##21##NONCEFactory = new ##NONCE##21##STAGEPREFIX##21##NONCENetlistNodeDataFactory();
		##NONCE##21##STAGEPREFIX##21##NONCENetlistNodeData data = ##NONCE##21##STAGEPREFIX##21##NONCEFactory.getNetlistNodeData(AProfile);
		node.setNetlistNodeData(data);
	}
	
}

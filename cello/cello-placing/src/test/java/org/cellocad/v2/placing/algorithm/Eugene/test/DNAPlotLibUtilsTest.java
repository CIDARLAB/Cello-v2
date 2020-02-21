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
package org.cellocad.v2.placing.algorithm.Eugene.test;

import java.util.List;

import org.cellocad.v2.common.CelloException;
import org.cellocad.v2.common.Utils;
import org.cellocad.v2.common.target.data.TargetData;
import org.cellocad.v2.common.target.data.TargetDataInstance;
import org.cellocad.v2.common.target.data.TargetDataUtils;
import org.cellocad.v2.placing.algorithm.Eugene.data.DNAPlotLibUtils;
import org.cellocad.v2.placing.runtime.environment.PLArgString;
import org.cellocad.v2.placing.runtime.environment.PLRuntimeEnv;
import org.cellocad.v2.results.netlist.Netlist;
import org.cellocad.v2.results.netlist.NetlistUtils;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 *
 * @author Timothy Jones
 *
 * @date 2020-01-28
 *
 */
public class DNAPlotLibUtilsTest {

	@BeforeClass
	public static void init() throws CelloException {
		String[] args = { "-" + PLArgString.INPUTNETLIST, Utils.getResource("and_placed_netlist.json").getFile(),
				"-" + PLArgString.USERCONSTRAINTSFILE, Utils.getResource("Eco1C1G1T1.UCF.json").getFile(),
				"-" + PLArgString.INPUTSENSORFILE, Utils.getResource("Eco1C1G1T1.input.json").getFile(),
				"-" + PLArgString.OUTPUTDEVICEFILE, Utils.getResource("Eco1C1G1T1.output.json").getFile(),
				"-" + PLArgString.ALGORITHMNAME, "Eugene" };
		PLRuntimeEnv runEnv = new PLRuntimeEnv(args);
		runEnv.setName("placing");
		// Read Netlist
		netlist = NetlistUtils.getNetlist(runEnv, PLArgString.INPUTNETLIST);
		// get TargetData
		td = TargetDataUtils.getTargetTargetData(runEnv, PLArgString.USERCONSTRAINTSFILE,
				PLArgString.INPUTSENSORFILE, PLArgString.OUTPUTDEVICEFILE);
		tdi = new TargetDataInstance(td);
	}

	@Test
	public void getDNADesigns_MockNetlist_ShouldReturnTwoDesigns() {
		List<String> designs = DNAPlotLibUtils.getDNADesigns(netlist);
		assert (designs.size() == 2);
	}

	@Test
	public void getDNADesigns_MockNetlist_ShouldReturnExactMatch() {
		List<String> designs = DNAPlotLibUtils.getDNADesigns(netlist);
		assert (designs.get(1).equals(
				"and_placed0,pPhlF,YFP_cassette,_NONCE_PAD0,pTet,RiboJ10,S2,SrpR,ECK120029600,pSrpR,pAmtR,RiboJ53,P3,PhlF,ECK120033737,pTac,BydvJ,A1,AmtR,L3S2P55"));
	}

	@Test
	public void getRegulatoryInformation_MockNetlist_ShouldReturnFourInteractions() {
		List<String> reg = DNAPlotLibUtils.getRegulatoryInformation(netlist, tdi.getParts(), tdi.getGates());
		assert (reg.size() == 4);
	}

	@Test
	public void getRegulatoryInformation_MockNetlist_ShouldReturnExactMatch() {
		List<String> reg = DNAPlotLibUtils.getRegulatoryInformation(netlist, tdi.getParts(), tdi.getGates());
		assert (reg.get(2).equals("AmtR,Repression,pAmtR,3,-,,0.23;0.66;0.88"));
	}

	@Test
	public void getartInformation_MockNetlist_ShouldReturn20Parts() {
		List<String> part = DNAPlotLibUtils.getPartInformation(netlist, tdi.getParts(), tdi.getGates());
		assert (part.size() == 20);
	}

	@Test
	public void getartInformation_MockNetlist_ShouldReturnExactMatch() {
		List<String> part = DNAPlotLibUtils.getPartInformation(netlist, tdi.getParts(), tdi.getGates());
		assert (part.get(2).equals("YFP_cassette,UserDefined,25,5,,,0.00;0.00;0.00,,,,,"));
	}

	private static TargetDataInstance tdi;
	private static Netlist netlist;
	private static TargetData td;

}

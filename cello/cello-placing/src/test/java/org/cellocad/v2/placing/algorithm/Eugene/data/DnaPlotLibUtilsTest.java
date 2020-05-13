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

package org.cellocad.v2.placing.algorithm.Eugene.data;

import java.util.List;
import org.cellocad.v2.common.CelloException;
import org.cellocad.v2.common.Utils;
import org.cellocad.v2.common.runtime.environment.ArgString;
import org.cellocad.v2.common.stage.runtime.environment.StageArgString;
import org.cellocad.v2.common.target.data.TargetData;
import org.cellocad.v2.common.target.data.TargetDataInstance;
import org.cellocad.v2.common.target.data.TargetDataUtils;
import org.cellocad.v2.placing.runtime.environment.PLRuntimeEnv;
import org.cellocad.v2.results.netlist.Netlist;
import org.cellocad.v2.results.netlist.NetlistUtils;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Tests for {@link DnaPlotLibUtils}.
 *
 * @author Timothy Jones
 * @date 2020-01-28
 */
public class DnaPlotLibUtilsTest {

  /**
   * Environment setup for tests.
   *
   * @throws CelloException Unable to instantiate supporting classes.
   */
  @BeforeClass
  public static void init() throws CelloException {
    final String[] args = {
      "-" + ArgString.INPUTNETLIST,
      Utils.getResource("and_placed_netlist.json").getFile(),
      "-" + ArgString.USERCONSTRAINTSFILE,
      Utils.getResource("lib/ucf/Eco/Eco1C1G1T1.UCF.json").getFile(),
      "-" + ArgString.INPUTSENSORFILE,
      Utils.getResource("lib/input/Eco/Eco1C1G1T1.input.json").getFile(),
      "-" + ArgString.OUTPUTDEVICEFILE,
      Utils.getResource("lib/output/Eco/Eco1C1G1T1.output.json").getFile(),
      "-" + StageArgString.ALGORITHMNAME,
      "Eugene"
    };
    final PLRuntimeEnv runEnv = new PLRuntimeEnv(args);
    runEnv.setName("placing");
    // Read Netlist
    DnaPlotLibUtilsTest.netlist = NetlistUtils.getNetlist(runEnv, ArgString.INPUTNETLIST);
    // get TargetData
    DnaPlotLibUtilsTest.td =
        TargetDataUtils.getTargetTargetData(
            runEnv,
            ArgString.USERCONSTRAINTSFILE,
            ArgString.INPUTSENSORFILE,
            ArgString.OUTPUTDEVICEFILE);
    DnaPlotLibUtilsTest.tdi = new TargetDataInstance(DnaPlotLibUtilsTest.td);
  }

  @Test
  public void getDnaDesigns_MockNetlist_ShouldReturnTwoDesigns() throws CelloException {
    final List<String> designs =
        DnaPlotLibUtils.getDnaDesigns(DnaPlotLibUtilsTest.netlist, DnaPlotLibUtilsTest.tdi);
    assert designs.size() == 2;
  }

  @Test
  public void getDnaDesigns_MockNetlist_ShouldReturnExactMatch() throws CelloException {
    final List<String> designs =
        DnaPlotLibUtils.getDnaDesigns(DnaPlotLibUtilsTest.netlist, DnaPlotLibUtilsTest.tdi);
    assert designs.contains(
        "and_placed0,pPhlF,YFP_cassette,_NONCE_PAD,pTet,RiboJ10,S2,SrpR,ECK120029600,pSrpR,pAmtR,RiboJ53,P3,PhlF,ECK120033737,pTac,BydvJ,A1,AmtR,L3S2P55");
  }

  @Test
  public void getRegulatoryInformation_MockNetlist_ShouldReturnFourInteractions()
      throws CelloException {
    final List<String> reg =
        DnaPlotLibUtils.getRegulatoryInformation(
            DnaPlotLibUtilsTest.netlist, DnaPlotLibUtilsTest.tdi);
    assert reg.size() == 4;
  }

  @Test
  public void getRegulatoryInformation_MockNetlist_ShouldReturnExactMatch() throws CelloException {
    final List<String> reg =
        DnaPlotLibUtils.getRegulatoryInformation(
            DnaPlotLibUtilsTest.netlist, DnaPlotLibUtilsTest.tdi);
    assert reg.get(2).equals("AmtR,Repression,pAmtR,3,-,,0.23;0.66;0.88");
  }

  @Test
  public void getPartsInformation_MockNetlist_ShouldReturn20Parts() throws CelloException {
    final List<String> parts =
        DnaPlotLibUtils.getPartsInformation(DnaPlotLibUtilsTest.netlist, DnaPlotLibUtilsTest.tdi);
    assert parts.size() == 20;
  }

  @Test
  public void getPartsInformation_MockNetlist_ShouldReturnExactMatch() throws CelloException {
    final List<String> parts =
        DnaPlotLibUtils.getPartsInformation(DnaPlotLibUtilsTest.netlist, DnaPlotLibUtilsTest.tdi);
    assert parts.contains("YFP_cassette,UserDefined,25,5,,,0.00;0.00;0.00,,,,,");
  }

  private static TargetDataInstance tdi;
  private static Netlist netlist;
  private static TargetData td;
}

/*
 * Copyright (C) 2017 Massachusetts Institute of Technology (MIT)
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

package org.cellocad.v2.partitioning.common;

import org.cellocad.v2.common.Utils;
import org.cellocad.v2.common.target.data.TargetData;
import org.cellocad.v2.partitioning.profile.PartitionProfile;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 * Partitioner.
 * 
 * @author Vincent Mirian
 *
 * @date Oct 27, 2017
 */
public class Partitioner {

  public Partitioner(final TargetData targetData) {
    Utils.isNullRuntimeException(targetData, "targetData");
    getDataFromUCF(targetData);
  }

  /**
   * Initializes a newly created {@link Partitioner}.
   *
   * @param nblocks The number of blocks.
   */
  @SuppressWarnings("unchecked")
  public Partitioner(final int nblocks) {
    Object key = null;
    Object value = null;
    JSONObject jsonObj = null;
    final JSONArray JArr = new JSONArray();
    for (int i = 0; i < nblocks; i++) {
      final String blockName = "block" + i;
      key = "name";
      value = blockName;
      jsonObj = new JSONObject();
      jsonObj.put(key, value);
      JArr.add(jsonObj);
    }
    key = "Blocks";
    value = JArr;
    jsonObj = new JSONObject();
    jsonObj.put(key, value);
    key = "Blocks";
    value = jsonObj;
    jsonObj = new JSONObject();
    jsonObj.put(key, value);
    key = "name";
    value = "PProfile0";
    jsonObj.put(key, value);
    final PartitionProfile PProfile = new PartitionProfile(jsonObj);
    setPartition(new Partition(PProfile));
  }

  protected void getDataFromUCF(final TargetData targetData) {
    JSONObject jsonObj = targetData.getJsonObjectAtIdx("PartitionProfile", 0);
    if (jsonObj == null) {
      throw new RuntimeException("\"PartitionProfile\" not present in UCF!");
    }
    jsonObj = (JSONObject) jsonObj.get("PartitionProfile");
    if (jsonObj == null) {
      throw new RuntimeException("\"PartitionProfile\" not present in UCF!");
    }
    final PartitionProfile PProfile = new PartitionProfile(jsonObj);
    setPartition(new Partition(PProfile));
  }

  protected void setPartition(final Partition partition) {
    this.partition = partition;
  }

  public Partition getPartition() {
    return partition;
  }

  private Partition partition;

}

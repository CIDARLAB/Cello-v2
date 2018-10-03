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
package partitioning.common;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import common.Utils;
import common.target.data.TargetData;
import partitioning.profile.PartitionProfile;

/**
 * @author Vincent Mirian
 * 
 * @date Oct 27, 2017
 *
 */
public class Partitioner {

	public Partitioner(final TargetData targetData){
		Utils.isNullRuntimeException(targetData, "targetData");
		this.getDataFromUCF(targetData);
	}
	
	public Partitioner(final int nblocks){
		Object key = null;
		Object value = null;
		JSONObject jObj = null;
		JSONArray JArr = new JSONArray();
		for (int i = 0; i < nblocks; i++) {
			String blockName = "block" + i;
			key = (Object) "name";
			value = (Object) blockName;
			jObj = new JSONObject();
			jObj.put(key, value);
			JArr.add(jObj);
		}
		key = (Object) "Blocks";
		value = (Object) JArr;
		jObj = new JSONObject();
		jObj.put(key, value);
		key = (Object) "Blocks";
		value = (Object) jObj;
		jObj = new JSONObject();
		jObj.put(key, value);
		key = (Object) "name";
		value = (Object) "PProfile0";
		jObj.put(key, value);
		PartitionProfile PProfile = new PartitionProfile(jObj);
		this.setPartition(new Partition(PProfile));
	}
	
	protected void getDataFromUCF(final TargetData targetData) {
		JSONObject jObj = targetData.getJSONObjectAtIdx("PartitionProfile", 0);
		if (jObj == null) {
			throw new RuntimeException("\"PartitionProfile\" not present in UCF!");
		}
		jObj = (JSONObject) jObj.get("PartitionProfile");
		if (jObj == null) {
			throw new RuntimeException("\"PartitionProfile\" not present in UCF!");
		}
		PartitionProfile PProfile = new PartitionProfile(jObj);
		this.setPartition(new Partition(PProfile));
	}
	
	protected void setPartition(final Partition partition) {
		this.partition = partition;
	}
	
	public Partition getPartition(){
		return this.partition;
	}

	private Partition partition;
}

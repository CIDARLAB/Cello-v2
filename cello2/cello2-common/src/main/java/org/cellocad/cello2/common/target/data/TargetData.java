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
package org.cellocad.cello2.common.target.data;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.cellocad.cello2.common.CObject;
import org.cellocad.cello2.common.profile.ProfileUtils;

/**
 * The TargetData class is a class for managing and accessing the target data.
 * 
 * @author Vincent Mirian
 * 
 * @date Nov 21, 2017
 *
 */
final public class TargetData extends CObject{

	private void init() {
		collectionTypeData = new HashMap< String, List<JSONObject> >();
	}

	/**
	 *  Initializes a newly created TargetData using the parameter <i>JArray</i>.
	 *  
	 *  @param JArray the JavaScript Object Notation (JSON) Array representation of the TargetData Object
	 */
	public TargetData(final JSONArray JArray){
		super();
		init();
		parse(JArray);
	}
	
	private void parse(final JSONArray JArray){
		for (int i = 0; i < JArray.size(); i++) {
			JSONObject JObj = (JSONObject) JArray.get(i);
			String collection = ProfileUtils.getString(JObj, "collection");
			List<JSONObject> temp = this.getCollectionTypeData().get(collection);
			if (temp == null) {
				temp = new ArrayList<JSONObject>();
				this.getCollectionTypeData().put(collection, temp);
			}
			temp.add(JObj);			
		}
	}

	/**
	 *  Returns a JSONObject of type, <i>type</i>, at index, <i>index</i>
	 *  
	 *  @param type the type of target data
	 *  @param index index of the JSONObject to return
	 *  @return the JSONObject if it exists, otherwise null
	 */
	public JSONObject getJSONObjectAtIdx(String type, int index) {
		JSONObject rtn = null;
		List<JSONObject> temp = this.getCollectionTypeData().get(type);
		if (temp != null) {
			rtn = temp.get(index);
		}
		return rtn;
	}

	/**
	 *  Returns the number of JSONObject of type, <i>type</i>
	 *  
	 *  @param type the type of target data
	 *  @return the number of JSONObject of type, <i>type</i>
	 */
	public int getNumJSONObject(String type) {
		int rtn = 0;
		List<JSONObject> temp = this.getCollectionTypeData().get(type);
		if (temp != null) {
			rtn = temp.size();
		}
		return rtn;
	}

	private Map< String, List<JSONObject> > getCollectionTypeData() {
		return this.collectionTypeData;
	}
	
	Map< String, List<JSONObject> > collectionTypeData;
}

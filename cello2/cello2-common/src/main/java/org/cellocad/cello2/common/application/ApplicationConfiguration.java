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
package org.cellocad.cello2.common.application;

import org.cellocad.cello2.common.CObjectCollection;
import org.cellocad.cello2.common.profile.ProfileObject;
import org.cellocad.cello2.common.stage.Stage;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 * The ApplicationConfiguration class is a class containing the configuration for an application.
 * 
 * @author Vincent Mirian
 * 
 * @date Nov 20, 2017
 *
 */
final public class ApplicationConfiguration extends ProfileObject{

	private void init() {
		this.stages = new CObjectCollection<Stage>();
	}
	
	/**
	 *  Initializes a newly created ApplicationConfiguration using the parameter <i>JObj</i>.
	 *  
	 *  @param JObj the JavaScript Object Notation (JSON) representation of the ApplicationConfiguration Object
	 */
	public ApplicationConfiguration(final JSONObject JObj){
		super(JObj);
		init();
		parse(JObj);
	}
	
	/*
	 * Parse
	 */
	private void parseStages(final JSONObject JObj){
    	JSONArray jsonArr;
    	// parse PartitionProfile
    	jsonArr = (JSONArray) JObj.get("stages");
		if (jsonArr == null) {
			throw new RuntimeException("'stages' missing in ApplicationInfo!");
		}
    	for (int i = 0; i < jsonArr.size(); i++)
    	{
    	    JSONObject jsonObj = (JSONObject) jsonArr.get(i);
    	    Stage S = new Stage(jsonObj);
    	    this.addStage(S);
    	}
	}
	
	private void parse(final JSONObject JObj){
		this.parseStages(JObj);
	}
	
	private void addStage(final Stage stage){
		if (stage != null){
			this.getStages().add(stage);
		}
	}
	
	/**
	 *  Returns the Stage object with its name equivalent to parameter <i>name</i>
	 *  
	 *  @param name name of the stage to return
	 *  @return Stage instance if the Stage exists, otherwise null
	 */
	public Stage getStageByName(final String name){
		Stage rtn = this.getStages().findCObjectByName(name);
		return rtn;
	}
	
	/**
	 * Returns the Stage at the specified position in this instance.
	 * 
	 * @param index index of the Stage to return
	 * @return if the index is within the bounds (0 <= bounds < this.getNumStage()), returns the Stage at the specified position in this instance, otherwise null
	 */
	public Stage getStageAtIdx(int index){
		Stage rtn = null;
		if (
				(index >= 0) &&
				(index < this.getNumStage())
			){
			rtn = this.getStages().get(index);	
		} 
		return rtn;
	}
	
	/**
	 * Returns the number of Stages in this instance.
	 * 
	 * @return the number of Stages in this instance.
	 */
	public int getNumStage(){
		int rtn = this.getStages().size();
		return rtn;
	}
	
	private CObjectCollection<Stage> getStages(){
		return this.stages;
	}
	
	CObjectCollection<Stage> stages;
}

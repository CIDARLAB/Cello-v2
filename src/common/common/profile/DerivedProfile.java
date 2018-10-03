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
package common.profile;

import common.CObject;
import common.Utils;

/**
 * The DerivedProfile is class that its instance are derived from a <i>ProfileObject</i>
 * @param <T> the type of the <i>ProfileObject</i>
 * 
 * @author Vincent Mirian
 * 
 * @date Nov 9, 2017
 *
 */
public class DerivedProfile<T extends ProfileObject> extends CObject{

	/**
	 *  Initializes a newly created DerivedProfile from the parameter <i>PObject</i> of type <i>T</i>,
	 *  with <i>Profile</i> set to parameter <i>PObject</i>
	 *  
	 *  @param PObject the ProfileObject of type <i>T</i>
	 *  @throws RuntimeException if parameter <i>PObject</i> is null
	 */
	public DerivedProfile(T PObject) {
		Utils.isNullRuntimeException(PObject, "PObject");
		this.setProfile(PObject);
		this.setName(PObject.getName());
	}

	/**
	 *  Getter for <i>Profile</i>
	 *  @return the Profile of this instance
	 */
	public T getProfile() {
		return this.Profile;
	}

	/**
	 *  Setter for <i>Profile</i>
	 *  @param Profile the ProfileObject of type <i>T</i> to set <i>Profile</i>
	 */
	protected void setProfile(final T Profile) {
		this.Profile = Profile;
	}
	
	private T Profile;
}

/**
 * Copyright (C) 2019 Boston University (BU)
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
package org.cellocad.v2.placing.algorithm.Eugene.data.ucf;

import java.util.Collection;

import org.cellocad.v2.common.CObject;

/**
 * 
 *
 * @author Timothy Jones
 *
 * @date 2019-07-22
 *
 */
public abstract class ContainerSpecification extends CObject {
	
	public ContainerSpecification(final String name, final Collection<String> gateTypes, final Double copyNumber) {
		this.setName(name);
		this.gateTypes = gateTypes;
		this.copyNumber = copyNumber;
	}

	/**
	 * Getter for <i>gateTypes</i>
	 * @return value of <i>gateTypes</i>
	 */
	public Collection<String> getGateTypes() {
		return gateTypes;
	}

	/**
	 * Getter for <i>copyNumber</i>
	 * @return value of <i>copyNumber</i>
	 */
	public Double getCopyNumber() {
		return copyNumber;
	}
	
	private Collection<String> gateTypes;
	private Double copyNumber;
}

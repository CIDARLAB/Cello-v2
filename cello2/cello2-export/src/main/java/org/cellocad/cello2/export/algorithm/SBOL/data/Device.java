/**
 * Copyright (C) 2018 Boston Univeristy (BU)
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
package org.cellocad.cello2.export.algorithm.SBOL.data;

import org.cellocad.cello2.common.CObjectCollection;

/**
 *
 *
 * @author Timothy Jones
 *
 * @date 2018-12-19
 *
 */
public class Device extends Component{

	void init() {
		this.setComponents(new CObjectCollection<Component>());
	}

	public Device() {
		this.init();
	}

	public Device(final CObjectCollection<Component> components) {
		this.init();
		this.setComponents(components);
	}
	
	CObjectCollection<Component> components;

	/**
	 * Add a part and its component index to this instance
	 * @param part add the <i>part</i> to the component unit
	 * @param index assign component index <i>index</i> to the component unit
	 */
	public void addComponent(Component component) {
		this.getComponents().add(component);
	}

	/**
	 * Returns the Component at the specified position in this instance.
	 * 
	 * @param index index of the Component to return
	 * @return if the index is within the bounds (0 <= bounds < this.getNumComponent()), returns the Component at the specified position in this instance, otherwise null
	 */
	public Component getComponentAtIdx(int index) {
		Component rtn = null;
		if (
				(0 <= index)
				&&
				(index < this.getNumComponent())
				) {
			rtn = this.getComponents().get(index);
		}
		return rtn;
	}
	
	/**
	 * Returns the number of Component in this instance.
	 * 
	 * @return the number of Component in this instance.
	 */
	public int getNumComponent() {
		return this.getComponents().size();
	}

	/**
	 * Getter for <i>components</i>
	 * @return value of <i>components</i>
	 */
	private CObjectCollection<Component> getComponents() {
		return components;
	}

	/**
	 * Setter for <i>components</i>
	 * @param components the value to set <i>components</i>
	 */
	private void setComponents(CObjectCollection<Component> components) {
		this.components = components;
	}

}

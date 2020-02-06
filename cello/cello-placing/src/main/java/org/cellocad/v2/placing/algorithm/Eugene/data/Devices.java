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
package org.cellocad.v2.placing.algorithm.Eugene.data;

import java.util.Collection;

import org.cellocad.v2.common.CObjectCollection;

/**
 *
 *
 * @author Timothy Jones
 *
 * @date 2018-12-19
 *
 */
public class Devices extends Component {

	void init() {
		this.setDevices(new CObjectCollection<Device>());
	}

	public Devices() {
		this.init();
	}

	public Devices(final CObjectCollection<Device> devices) {
		this.init();
		this.setDevices(devices);
	}
	
	CObjectCollection<Device> devices;

	/**
	 * Add a Device to this instance
	 * @param part add the <i>part</i> to the device unit
	 */
	public void add(Device device) {
		this.getDevices().add(device);
	}
	
	/**
	 * Add a Device to this instance
	 * @param part add the <i>part</i> to the device unit
	 */
	public void addAll(Collection<Device> devices) {
		for (Device device : devices) {
			this.getDevices().add(device);
		}
	}
	
	/**
	 * Add a Device to this instance
	 * @param part add the <i>part</i> to the device unit
	 */
	public void addAll(Devices devices) {
		for (int i = 0; i < devices.getNumDevice(); i++) {
			Device device = devices.getDeviceAtIdx(i);
			this.getDevices().add(device);
		}
	}

	/**
	 * Returns the Device at the specified position in this instance.
	 * 
	 * @param index index of the Device to return
	 * @return if the index is within the bounds (0 <= bounds < this.getNumDevice()), returns the Device at the specified position in this instance, otherwise null
	 */
	public Device getDeviceAtIdx(int index) {
		Device rtn = null;
		if (
				(0 <= index)
				&&
				(index < this.getNumDevice())
				) {
			rtn = this.getDevices().get(index);
		}
		return rtn;
	}
	
	/**
	 * Returns the number of Device in this instance.
	 * 
	 * @return the number of Device in this instance.
	 */
	public int getNumDevice() {
		return this.getDevices().size();
	}

	/**
	 * Getter for <i>devices</i>
	 * @return value of <i>devices</i>
	 */
	private CObjectCollection<Device> getDevices() {
		return devices;
	}

	/**
	 * Setter for <i>devices</i>
	 * @param devices the value to set <i>devices</i>
	 */
	private void setDevices(CObjectCollection<Device> devices) {
		this.devices = devices;
	}

}

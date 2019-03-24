/**
 * Copyright (C) 2018 Boston University (BU)
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
package org.cellocad.cello2.export.algorithm.SBOL.data.design;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.cellocad.cello2.common.CObject;
import org.cellocad.cello2.common.CObjectCollection;
import org.cellocad.cello2.export.algorithm.SBOL.data.Component;
import org.cellocad.cello2.export.algorithm.SBOL.data.ucf.CasetteParts;
import org.cellocad.cello2.export.algorithm.SBOL.data.ucf.Gate;
import org.cellocad.cello2.export.algorithm.SBOL.data.ucf.GateParts;
import org.cellocad.cello2.export.algorithm.SBOL.data.ucf.InputSensor;
import org.cellocad.cello2.export.algorithm.SBOL.data.ucf.OutputReporter;
import org.cellocad.cello2.export.algorithm.SBOL.data.ucf.Part;
import org.cellocad.cello2.export.algorithm.SBOL.data.ucf.ResponseFunction;
import org.cellocad.cello2.export.algorithm.SBOL.data.ucf.ResponseFunctionVariable;
import org.cellocad.cello2.results.placing.placement.Placement;
import org.sbolstandard.core2.ComponentDefinition;
import org.sbolstandard.core2.SBOLDocument;

/**
 *
 *
 * @author Timothy Jones
 *
 * @date 2018-06-14
 *
 */
public class TranscriptionalUnit extends CObject {

	private void init() {
		unit = new ArrayList<>();
	}

	protected void addComponent(Component component) {
		unit.add(component);
	}

	protected void addComponent(int index, Component component) {
		unit.add(index,component);
	}

	TranscriptionalUnit(final Placement placement,
	                    final Boolean Up,
	                    final Boolean Down,
	                    final CObjectCollection<Part> parts,
	                    final CObjectCollection<Gate> gates,
	                    final CObjectCollection<InputSensor> sensors,
	                    final CObjectCollection<OutputReporter> reporters) {
		init();
		if (placement.getDirection() == Down) {
			this.setDirection(Down);
		}
		else {
			this.setDirection(Up);
		}
		for (int i = 0; i < placement.getNumComponent(); i++) {
			String name = placement.getComponentAtIdx(i);
			Gate gate = gates.findCObjectByName(name);
			InputSensor sensor = sensors.findCObjectByName(name);
			OutputReporter reporter = reporters.findCObjectByName(name);
			Part part = parts.findCObjectByName(name);
			if (gate != null) {
				this.addComponent(gate);
			}
			else if (sensor != null) {
				this.addComponent(sensor);
			}
			else if (reporter != null) {
				this.addComponent(reporter);
			}
			else if (part != null) {
				this.addComponent(part);
			}
			else {
				throw new RuntimeException("Unknown part or gate.");
			}
		}
	}


	/**
	 * Returns the DNA sequence of this instance.
	 *
	 * @return the DNA sequence of this instance.
	 */
	public String getDNASequence() {
		String rtn = "";
		for (Component component : this.getTranscriptionalUnit()) {
			rtn += this.getComponentDNASequence(component);
		}
		return rtn;
	}

	private String getComponentDNASequence(Component component) {
		String rtn = "";
		if (component instanceof Gate) {
			Gate gate = (Gate)component;
			ResponseFunction rf = gate.getResponseFunction();
			GateParts gateParts = gate.getGateParts();
			for (int i = 0; i < rf.getNumVariable(); i++) {
				ResponseFunctionVariable variable = rf.getVariableAtIdx(i);
				CasetteParts casetteParts = gateParts.getCasetteParts(variable.getName());
				for (int j = 0; j < casetteParts.getNumParts(); j++) {
					Part part = casetteParts.getPartAtIdx(j);
					rtn += part.getDNASequence();
				}
			}
		}
		if (component instanceof InputSensor) {
			InputSensor sensor = (InputSensor)component;
			for (int i = 0; i < sensor.getNumParts(); i++) {
				Part part = sensor.getPartAtIdx(i);
				rtn += part.getDNASequence();
			}
		}
		if (component instanceof OutputReporter) {
			OutputReporter reporter = (OutputReporter)component;
			for (int i = 0; i < reporter.getNumParts(); i++) {
				Part part = reporter.getPartAtIdx(i);
				rtn += part.getDNASequence();
			}
		}
		if (component instanceof Part) {
			rtn += ((Part)component).getDNASequence();
		}
		return rtn;
	}

	/**
	 * Returns the number of parts in this instance.
	 *
	 * @return the number of parts in this instance.
	 */
	public int getNumComponent() {
		return this.getTranscriptionalUnit().size();
	}

	/**
	 *  Returns the part at index <i>index</i>
	 *  @return the part at <i>index</i> if the part exists, null otherwise
	 */
	public Component getComponentAtIdx(final int index) {
		Component rtn = null;
		if (
		    (0 <= index)
		    &&
		    (index < this.getNumComponent())
		    ) {
			rtn = this.getTranscriptionalUnit().get(index);
		}
		return rtn;
	}

	/**
	 * Getter for <i>unit</i>
	 * @return value of <i>unit</i>
	 */
	protected List<Component> getTranscriptionalUnit() {
		return unit;
	}

	protected ComponentDefinition getComponentDefinition(SBOLDocument document) {
		ComponentDefinition rtn = null;
		return rtn;
	}

	/**
	 * Getter for <i>name</i>
	 * @return value of <i>name</i>
	 */
	public String getName() {
		return name;
	}

	/**
	 * Setter for <i>name</i>
	 * @param name the value to set <i>name</i>
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Getter for <i>direction</i>
	 * @return value of <i>direction</i>
	 */
	public Boolean getDirection() {
		return direction;
	}

	/**
	 * Setter for <i>direction</i>
	 * @param direction the value to set <i>direction</i>
	 */
	protected void setDirection(final Boolean direction) {
		this.direction = direction;
	}

	/**
	 * Getter for <i>uri</i>
	 * @return value of <i>uri</i>
	 */
	public URI getUri() {
		return uri;
	}

	/**
	 * Setter for <i>uri</i>
	 * @param uri the value to set <i>uri</i>
	 */
	public void setUri(URI uri) {
		this.uri = uri;
	}

	private String name;
	private Boolean direction;
	List<Component> unit;
	private URI uri;

}

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
package org.cellocad.cello2.export.algorithm.SBOL;

import java.net.URI;
import java.util.Set;

import org.cellocad.cello2.export.algorithm.SBOL.data.Device;
import org.cellocad.cello2.export.algorithm.SBOL.data.SBOLDataUtils;
import org.cellocad.cello2.export.algorithm.SBOL.data.ucf.Part;
import org.sbolstandard.core2.ComponentDefinition;
import org.sbolstandard.core2.SBOLDocument;
import org.sbolstandard.core2.SBOLValidationException;
import org.sbolstandard.core2.Sequence;
import org.synbiohub.frontend.SynBioHubException;
import org.synbiohub.frontend.SynBioHubFrontend;

/**
 * The YosysUtils class is class with utility methods for the <i>Yosys</i> instances.
 * 
 * @author Timothy Jones
 * 
 * @date 2018-05-21
 *
 */
public class SBOLUtils {

	/**
	 * Add a component definition of <i>part</i> to <i>document</i>. Use SynBioHub definition if available, add sequences if found.
	 * @param part the part to add
	 * @param document the <i>SBOLDocument</i> to add the <i>ComponentDefinition</i>
	 * @throws SynBioHubException unable to fetch SBOL from SynBioHub for <i>part</i>
	 * @throws SBOLValidationException unable to create component definition
	 */
	public static ComponentDefinition addPartDefinition(Part part, SBOLDocument document, SynBioHubFrontend sbh) throws SynBioHubException, SBOLValidationException {
		ComponentDefinition rtn = null;
		String uri = part.getUri();

		if (uri != null) {
			rtn = document.getComponentDefinition(URI.create(uri));
			if (rtn != null)
				return rtn;
			if (sbh != null) {
				URI temp = URI.create(uri);
				SBOLDocument sbol = sbh.getSBOL(temp);
				rtn = sbol.getComponentDefinition(temp);
			}
		}

		if (rtn != null) {
			part.setUri(uri);
			document.createCopy(rtn);
			Set<Sequence> sequences = rtn.getSequences();
			if (sequences != null) {
				for (Sequence s : sequences) {
					document.createCopy(s);
				}
			}
		} else {
			rtn = document.createComponentDefinition(part.getName(),"1",ComponentDefinition.DNA_REGION);
			part.setUri(rtn.getIdentity().toString());
			Sequence sequence = document.createSequence(part.getName() + "_sequence",SBOLDataUtils.getDNASequence(part),Sequence.IUPAC_DNA);
			rtn.addSequence(sequence);
		}

		return rtn;
	}

	/**
	 * Add a component definition of <i>device</i> to <i>document</i>. Use SynBioHub definition if available, add sequences if found.
	 * @param device the device to add
	 * @param document the <i>SBOLDocument</i> to add the <i>ComponentDefinition</i>
	 * @throws SynBioHubException unable to fetch SBOL from SynBioHub for <i>device</i>
	 * @throws SBOLValidationException unable to create component definition
	 */
	public static ComponentDefinition addDeviceDefinition(Device device, SBOLDocument document, SynBioHubFrontend sbh) throws SynBioHubException, SBOLValidationException {
		String uri = device.getUri();
		ComponentDefinition rtn = null;

		if (uri != null) {
			rtn = document.getComponentDefinition(URI.create(uri));
			if (rtn != null)
				return rtn;
			if (sbh != null) {
				URI temp = URI.create(uri);
				SBOLDocument sbol = sbh.getSBOL(temp);
				rtn = sbol.getComponentDefinition(temp);
			}
		}

		if (rtn != null) {
			document.createCopy(rtn);
			SBOLUtils.addChildCDsAndSequences(rtn,document);
			Set<Sequence> sequences = rtn.getSequences();
			if (sequences != null) {
				for (Sequence s : sequences) {
					document.createCopy(s);
				}
			}
		} else {
			rtn = document.createComponentDefinition(device.getName(),"1",ComponentDefinition.DNA_REGION);
			device.setUri(rtn.getIdentity().toString());
			Sequence sequence = document.createSequence(device.getName() + "_sequence",SBOLDataUtils.getDNASequence(device),Sequence.IUPAC_DNA);
			rtn.addSequence(sequence);
			// TODO add part definitions
		}

		return rtn;
	}

	protected static void addChildCDsAndSequences(ComponentDefinition cd, SBOLDocument document) throws SynBioHubException, SBOLValidationException {
		Set<org.sbolstandard.core2.Component> components = cd.getComponents();
		if (components != null) {
			for (org.sbolstandard.core2.Component c : components) {
				ComponentDefinition child = c.getDefinition();
				document.createCopy(child);
				Set<Sequence> sequences = child.getSequences();
				if (sequences != null) {
					for (Sequence s : sequences) {
						document.createCopy(s);
					}
				}
				SBOLUtils.addChildCDsAndSequences(child,document);
			}
		}
	}

}

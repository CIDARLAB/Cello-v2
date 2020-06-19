/*
 * Copyright (C) 2018 Boston University (BU)
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

package org.cellocad.v2.export.algorithm.SBOL;

import java.net.URI;
import java.util.Set;
import org.cellocad.v2.common.target.data.data.AssignableDevice;
import org.cellocad.v2.common.target.data.data.Part;
import org.cellocad.v2.export.algorithm.SBOL.data.SBOLDataUtils;
import org.sbolstandard.core2.ComponentDefinition;
import org.sbolstandard.core2.SBOLDocument;
import org.sbolstandard.core2.SBOLValidationException;
import org.sbolstandard.core2.Sequence;
import org.sbolstandard.core2.SequenceOntology;
import org.synbiohub.frontend.SynBioHubException;
import org.synbiohub.frontend.SynBioHubFrontend;

/**
 * Utility methods for the <i>Sbol</i> instances.
 *
 * @author Timothy Jones
 * @date 2018-05-21
 */
public class SBOLUtils {

  /**
   * Add a component definition of {@code part} to {@code document}. Use SynBioHub definition if
   * available, add sequences if found.
   *
   * @param part The part to add.
   * @param document The {@link SBOLDocument} to add the {@link ComponentDefinition}.
   * @throws SynBioHubException unable to fetch SBOL from SynBioHub for {@code part}.
   * @throws SBOLValidationException unable to create component definition.
   */
  public static ComponentDefinition addPartDefinition(
      final Part part, final SBOLDocument document, final SynBioHubFrontend sbh)
      throws SynBioHubException, SBOLValidationException {
    ComponentDefinition rtn = null;
    final URI uri = part.getUri();
    if (uri != null) {
      rtn = document.getComponentDefinition(uri);
      if (rtn != null) {
        return rtn;
      }
      if (sbh != null) {
        final URI temp = uri;
        final SBOLDocument sbol = sbh.getSBOL(temp);
        rtn = sbol.getComponentDefinition(temp);
      }
    }

    if (rtn != null) {
      part.setUri(uri);
      document.createCopy(rtn);
      final Set<Sequence> sequences = rtn.getSequences();
      if (sequences != null) {
        for (final Sequence s : sequences) {
          document.createCopy(s);
        }
      }
    } else {
      rtn = document.createComponentDefinition(part.getName(), "1", ComponentDefinition.DNA_REGION);
      part.setUri(rtn.getIdentity());
      final Sequence sequence =
          document.createSequence(
              part.getName() + "_sequence", SBOLDataUtils.getDnaSequence(part), Sequence.IUPAC_DNA);
      rtn.addSequence(sequence);
      if (part.getPartType().equals(Part.S_PROMOTER)) {
        rtn.addRole(SequenceOntology.PROMOTER);
      }
      if (part.getPartType().equals(Part.S_RIBOZYME)) {
        rtn.addRole(URI.create("http://identifiers.org/so/SO:0001977"));
      }
      if (part.getPartType().equals(Part.S_RBS)) {
        rtn.addRole(SequenceOntology.RIBOSOME_ENTRY_SITE);
      }
      if (part.getPartType().equals(Part.S_CDS)) {
        rtn.addRole(SequenceOntology.CDS);
      }
      if (part.getPartType().equals(Part.S_TERMINATOR)) {
        rtn.addRole(SequenceOntology.TERMINATOR);
      }
      if (part.getPartType().equals("cassette")) {
        rtn.addRole(SequenceOntology.ENGINEERED_REGION);
      }
      if (part.getPartType().equals(Part.S_SCAR)) {
        rtn.addRole(URI.create("http://identifiers.org/so/SO:0001953"));
      }
      if (part.getPartType().equals(Part.S_SPACER)) {
        rtn.addRole(SequenceOntology.ENGINEERED_REGION);
      }
    }

    return rtn;
  }

  /**
   * Add a component definition of <i>device</i> to <i>document</i>. Use SynBioHub definition if
   * available, add sequences if found.
   *
   * @param device The device to add.
   * @param document The <i>SBOLDocument</i> to add the <i>ComponentDefinition</i>.
   * @throws SynBioHubException unable to fetch SBOL from SynBioHub for <i>device</i>
   * @throws SBOLValidationException unable to create component definition.
   */
  public static ComponentDefinition addDeviceDefinition(
      final AssignableDevice device, final SBOLDocument document, final SynBioHubFrontend sbh)
      throws SynBioHubException, SBOLValidationException {
    final URI uri = device.getUri();
    ComponentDefinition rtn = null;

    if (uri != null) {
      rtn = document.getComponentDefinition(uri);
      if (rtn != null) {
        return rtn;
      }
      if (sbh != null) {
        final URI temp = uri;
        final SBOLDocument sbol = sbh.getSBOL(temp);
        rtn = sbol.getComponentDefinition(temp);
      }
    }

    if (rtn != null) {
      document.createCopy(rtn);
      SBOLUtils.addChildCDsAndSequences(rtn, document);
      final Set<Sequence> sequences = rtn.getSequences();
      if (sequences != null) {
        for (final Sequence s : sequences) {
          document.createCopy(s);
        }
      }
    } else {
      rtn =
          document.createComponentDefinition(device.getName(), "1", ComponentDefinition.DNA_REGION);
      device.setUri(rtn.getIdentity());
      final Sequence sequence =
          document.createSequence(
              device.getName() + "_sequence",
              SBOLDataUtils.getDnaSequence(device),
              Sequence.IUPAC_DNA);
      rtn.addSequence(sequence);
      // TODO add part definitions
    }

    return rtn;
  }

  protected static void addChildCDsAndSequences(
      final ComponentDefinition cd, final SBOLDocument document)
      throws SynBioHubException, SBOLValidationException {
    final Set<org.sbolstandard.core2.Component> components = cd.getComponents();
    if (components != null) {
      for (final org.sbolstandard.core2.Component c : components) {
        final ComponentDefinition child = c.getDefinition();
        document.createCopy(child);
        final Set<Sequence> sequences = child.getSequences();
        if (sequences != null) {
          for (final Sequence s : sequences) {
            document.createCopy(s);
          }
        }
        SBOLUtils.addChildCDsAndSequences(child, document);
      }
    }
  }
}

/*
 * Copyright (C) 2020 Boston University (BU)
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

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cellocad.v2.common.CObjectCollection;
import org.cellocad.v2.common.Utils;
import org.cellocad.v2.common.exception.CelloException;
import org.cellocad.v2.common.runtime.environment.ArgString;
import org.cellocad.v2.common.target.data.data.AssignableDevice;
import org.cellocad.v2.common.target.data.data.DnaComponent;
import org.cellocad.v2.common.target.data.data.Gate;
import org.cellocad.v2.common.target.data.data.InputSensor;
import org.cellocad.v2.common.target.data.data.OutputDevice;
import org.cellocad.v2.common.target.data.data.Part;
import org.cellocad.v2.common.target.data.data.Structure;
import org.cellocad.v2.common.target.data.data.StructureDevice;
import org.cellocad.v2.common.target.data.data.StructureObject;
import org.cellocad.v2.common.target.data.data.StructurePart;
import org.cellocad.v2.common.target.data.data.StructureTemplate;
import org.cellocad.v2.export.algorithm.EXAlgorithm;
import org.cellocad.v2.export.algorithm.SBOL.data.SBOLDataUtils;
import org.cellocad.v2.export.algorithm.SBOL.data.SBOLNetlistData;
import org.cellocad.v2.export.algorithm.SBOL.data.SBOLNetlistEdgeData;
import org.cellocad.v2.export.algorithm.SBOL.data.SBOLNetlistNodeData;
import org.cellocad.v2.export.target.data.EXTargetDataInstance;
import org.cellocad.v2.results.common.Result;
import org.cellocad.v2.results.netlist.Netlist;
import org.cellocad.v2.results.netlist.NetlistEdge;
import org.cellocad.v2.results.netlist.NetlistNode;
import org.cellocad.v2.results.placing.placement.Placement;
import org.cellocad.v2.results.placing.placement.PlacementGroup;
import org.cellocad.v2.results.placing.placement.Placements;
import org.sbolstandard.core2.AccessType;
import org.sbolstandard.core2.ComponentDefinition;
import org.sbolstandard.core2.DirectionType;
import org.sbolstandard.core2.ModuleDefinition;
import org.sbolstandard.core2.RestrictionType;
import org.sbolstandard.core2.SBOLConversionException;
import org.sbolstandard.core2.SBOLDocument;
import org.sbolstandard.core2.SBOLValidationException;
import org.sbolstandard.core2.SBOLWriter;
import org.sbolstandard.core2.Sequence;
import org.sbolstandard.core2.SequenceAnnotation;
import org.sbolstandard.core2.SequenceOntology;
import org.synbiohub.frontend.SynBioHubException;
import org.synbiohub.frontend.SynBioHubFrontend;
import org.virtualparts.VPRException;
import org.virtualparts.VPRTripleStoreException;
import org.virtualparts.data.QueryParameters;
import org.virtualparts.data.SBOLInteractionAdder_GeneCentric;

/**
 * The implementation of the <i>SBOL</i> algorithm in the <i>export</i> stage.
 *
 * @author Timothy Jones
 * @date 2018-06-04
 */
public class SBOL extends EXAlgorithm {

  /**
   * Returns the {@link SBOLNetlistNodeData} of the given node.
   *
   * @param node A node within the netlist of this instance.
   * @return The {@link SBOLNetlistNodeData} instance if it exists, null otherwise.
   */
  protected SBOLNetlistNodeData getSbolNetlistNodeData(final NetlistNode node) {
    SBOLNetlistNodeData rtn = null;
    rtn = (SBOLNetlistNodeData) node.getNetlistNodeData();
    return rtn;
  }

  /**
   * Returns the {@link SBOLNetlistEdgeData} of the given edge.
   *
   * @param edge An edge within the netlist of this instance.
   * @return The {@link SBOLNetlistEdgeData} instance if it exists, null otherwise.
   */
  protected SBOLNetlistEdgeData getSbolNetlistEdgeData(final NetlistEdge edge) {
    SBOLNetlistEdgeData rtn = null;
    rtn = (SBOLNetlistEdgeData) edge.getNetlistEdgeData();
    return rtn;
  }

  /**
   * Returns the {@link SBOLNetlistData} of the given netlist.
   *
   * @param netlist The netlist of this instance.
   * @return The {@link SBOLNetlistData} instance if it exists, null otherwise.
   */
  protected SBOLNetlistData getSbolNetlistData(final Netlist netlist) {
    SBOLNetlistData rtn = null;
    rtn = (SBOLNetlistData) netlist.getNetlistData();
    return rtn;
  }

  /** Gets the constraint data from the netlist constraint file. */
  @Override
  protected void getConstraintFromNetlistConstraintFile() {}

  /**
   * Gets the data from the UCF.
   *
   * @throws CelloException Unable to get data from UCF.
   */
  @Override
  protected void getDataFromUcf() throws CelloException {
    setTargetDataInstance(new EXTargetDataInstance(getTargetData()));
  }

  /** Set parameter values of the algorithm. */
  @Override
  protected void setParameterValues() {
    Boolean present = false;
    present = getAlgorithmProfile().getStringParameter("RepositoryUrl").getFirst();
    if (present) {
      setRepositoryUrl(getAlgorithmProfile().getStringParameter("RepositoryUrl").getSecond());
    }
    present = getAlgorithmProfile().getStringParameter("CollectionUri").getFirst();
    if (present) {
      setCollectionUri(getAlgorithmProfile().getStringParameter("CollectionUri").getSecond());
    }
    present = getAlgorithmProfile().getBooleanParameter("AddInteractions").getFirst();
    if (present) {
      setAddInteractions(getAlgorithmProfile().getBooleanParameter("AddInteractions").getSecond());
    }
    present = getAlgorithmProfile().getBooleanParameter("AddDesignModules").getFirst();
    if (present) {
      setAddDesignModules(
          getAlgorithmProfile().getBooleanParameter("AddDesignModules").getSecond());
    }
  }

  /** Validate parameter value for <i>repositoryUrl</i>. */
  protected void validateRepositoryUrlValue() {
    final String url = getRepositoryUrl();
    if (url != null) {
      try {
        new URL(url);
      } catch (final MalformedURLException e) {
        logError(url + " is not a valid URL!");
        Utils.exit(-1);
      }
    }
  }

  /** Validate parameter values of the algorithm. */
  @Override
  protected void validateParameterValues() {
    validateRepositoryUrlValue();
  }

  /** Perform preprocessing. */
  @Override
  protected void preprocessing() {
    // sbh frontend
    if (getRepositoryUrl() != null) {
      setSbhFrontend(new SynBioHubFrontend(getRepositoryUrl()));
    }
    // output directory
    final String outputDir = getRuntimeEnv().getOptionValue(ArgString.OUTPUTDIR);
    // output filename
    final String inputFilename = getNetlist().getInputFilename();
    final String filename = Utils.getFilename(inputFilename);
    setSbolFilename(outputDir + Utils.getFileSeparator() + filename + ".xml");
  }

  /**
   * Add component definitions for all parts of all gates in the netlist.
   *
   * @param document The SBOL document to which to add the definitions.
   * @throws SynBioHubException unable to fetch part SBOL from SynBioHub
   * @throws SBOLValidationException unable to create component definition
   * @throws CelloException Unable to add component definitions.
   */
  protected void addComponentDefinitions(final SBOLDocument document)
      throws SynBioHubException, SBOLValidationException, CelloException {
    final Netlist netlist = getNetlist();
    final Placements placements = netlist.getResultNetlistData().getPlacements();
    for (int i = 0; i < placements.getNumPlacement(); i++) {
      final Placement placement = placements.getPlacementAtIdx(i);
      for (int j = 0; j < placement.getNumPlacementGroup(); j++) {
        final PlacementGroup group = placement.getPlacementGroupAtIdx(j);
        for (int k = 0; k < group.getNumComponent(); k++) {
          final org.cellocad.v2.results.placing.placement.Component component =
              group.getComponentAtIdx(k);
          for (int l = 0; l < component.getNumPart(); l++) {
            final String str = component.getPartAtIdx(l);
            final Part part = getTargetDataInstance().getParts().findCObjectByName(str);
            AssignableDevice ad = getTargetDataInstance().getAssignableDeviceByName(str);
            if (part != null) {
              SBOLUtils.addPartDefinition(part, document, getSbhFrontend());
              continue;
            }
            if (ad != null) {
              SBOLUtils.addDeviceDefinition(ad, document, getSbhFrontend());
              continue;
            }
            final String nodeName = component.getNode();
            final NetlistNode node = getNetlist().getVertexByName(nodeName);
            final String deviceName = node.getResultNetlistNodeData().getDeviceName();
            ad = getTargetDataInstance().getAssignableDeviceByName(deviceName);
            final Structure s = ad.getStructure();
            final StructureDevice sd = s.getDeviceByName(str);
            final Collection<String> parts = SBOL.getFlattenedPartList(sd);
            for (final String partName : parts) {
              final Part c = (Part) getDnaComponentByName(partName);
              SBOLUtils.addPartDefinition(c, document, getSbhFrontend());
            }
          }
        }
      }
    }
  }

  private static Collection<String> getFlattenedPartList(final StructureDevice device)
      throws CelloException {
    final Collection<String> rtn = new ArrayList<>();
    for (final StructureObject o : device.getComponents()) {
      if (o instanceof StructureTemplate) {
        throw new CelloException("Cannot flatten with unreferenced input parts.");
      }
      if (o instanceof StructurePart) {
        rtn.add(o.getName());
      }
      if (o instanceof StructureDevice) {
        rtn.addAll(SBOL.getFlattenedPartList((StructureDevice) o));
      }
    }
    return rtn;
  }

  /**
   * Add transcriptional unit component definitions to an SBOLDocument.
   *
   * @param document The SBOLDocument.
   * @throws SBOLValidationException unable to add transcriptional unit
   * @throws CelloException Unable to get parts from nested device.
   */
  protected void addTranscriptionalUnitDefinitions(final SBOLDocument document)
      throws SBOLValidationException, CelloException {
    final Netlist netlist = getNetlist();
    final Placements placements = netlist.getResultNetlistData().getPlacements();
    for (int i = 0; i < placements.getNumPlacement(); i++) {
      final Placement placement = placements.getPlacementAtIdx(i);
      for (int j = 0; j < placement.getNumPlacementGroup(); j++) {
        final PlacementGroup group = placement.getPlacementGroupAtIdx(j);
        final String name = String.format("Design%d_", i);
        for (int k = 0; k < group.getNumComponent(); k++) {
          final org.cellocad.v2.results.placing.placement.Component component =
              group.getComponentAtIdx(k);

          // ComponentDefinition
          final ComponentDefinition cd =
              document.createComponentDefinition(
                  name + component.getName(), "1", ComponentDefinition.DNA_REGION);
          cd.addRole(SequenceOntology.ENGINEERED_REGION);
          component.setUri(cd.getIdentity());

          // parts
          String sequence = "";
          final CObjectCollection<DnaComponent> components = new CObjectCollection<>();
          for (int l = 0; l < component.getNumPart(); l++) {
            final String componentName = component.getPartAtIdx(l);
            final DnaComponent comp = getDnaComponentByName(componentName);
            if (comp == null) {
              final String nodeName = component.getNode();
              final NetlistNode node = getNetlist().getVertexByName(nodeName);
              final String deviceName = node.getResultNetlistNodeData().getDeviceName();
              final AssignableDevice ad =
                  getTargetDataInstance().getAssignableDeviceByName(deviceName);
              final Structure s = ad.getStructure();
              final StructureDevice sd = s.getDeviceByName(componentName);
              final Collection<String> parts = SBOL.getFlattenedPartList(sd);
              for (final String str : parts) {
                final DnaComponent c = getDnaComponentByName(str);
                components.add(c);
              }
            } else {
              components.add(comp);
            }
          }
          for (int l = 0; l < components.size(); l++) {
            final DnaComponent co = components.get(l);
            // Component
            final String cDisplayId = co.getName() + "_Component";
            final AccessType cAccess = AccessType.PUBLIC;
            final URI cDefinitionURI = co.getUri();
            final org.sbolstandard.core2.Component c =
                cd.createComponent(cDisplayId, cAccess, cDefinitionURI);

            // SequenceAnnotation
            final String s = SBOLDataUtils.getDnaSequence(co);
            final String saDisplayId = "SequenceAnnotation" + String.valueOf(l);
            final String saLocationId = saDisplayId + "_Range";
            final int start = sequence.length() + 1;
            final int end = start + s.length() - 1;
            final SequenceAnnotation sa =
                cd.createSequenceAnnotation(saDisplayId, saLocationId, start, end);
            sa.setComponent(c.getIdentity());
            sequence += s;

            // SequenceConstraint
            if (l != 0) {
              final String scDisplayId = String.format("%s_Constraint%d", cd.getDisplayId(), l);
              final RestrictionType scRestriction = RestrictionType.PRECEDES;
              final URI scSubjectId =
                  cd.getComponent(components.get(l - 1).getName() + "_Component").getIdentity();
              final URI scObjectId = cd.getComponent(co.getName() + "_Component").getIdentity();
              cd.createSequenceConstraint(scDisplayId, scRestriction, scSubjectId, scObjectId);
            }
          }
          // Sequence
          final Sequence s =
              document.createSequence(
                  cd.getDisplayId() + "_Sequence", sequence, Sequence.IUPAC_DNA);
          cd.addSequence(s);
        }
      }
    }
  }

  /**
   * Create an SBOL document.
   *
   * @return The generated {@link SBOLDocument}.
   * @throws SynBioHubException Unable to fetch SBOL from SynBioHub for a part.
   * @throws SBOLValidationException Unable to create {@link Component} or {@link
   *     ComponentDefinition}.
   * @throws CelloException Unable to create SBOL document.
   */
  protected SBOLDocument createSbolDocument()
      throws SynBioHubException, SBOLValidationException, CelloException {
    final SBOLDocument document = new SBOLDocument();
    document.setDefaultURIprefix("http://cellocad.org/v2");

    addComponentDefinitions(document);
    addTranscriptionalUnitDefinitions(document);

    return document;
  }

  /**
   * Add device interactions via the Virtual Parts (VPR) API.
   *
   * @param selectedRepo The specified synbiohub repository the user wants VPR model generator to
   *     connect to.
   * @param generatedModel The file to generate the model from.
   * @param name The top level design name.
   * @return The SBOL Document with interactions.
   * @throws SBOLValidationException Unable to validate SBOL.
   * @throws IOException Unable to read or write the given SBOLDocument
   * @throws SBOLConversionException Unable to perform conversion for the given SBOLDocument.
   * @throws VPRException Unable to perform VPR Model Generation on the given SBOLDocument.
   * @throws VPRTripleStoreException Unable to perform VPR Model Generation on the given
   *     SBOLDocument.
   */
  protected SBOLDocument addInteractions(
      final String selectedRepo, final SBOLDocument document, final String name)
      throws SBOLValidationException, IOException, SBOLConversionException, VPRException,
          VPRTripleStoreException, URISyntaxException {
    final Set<URI> collections = new HashSet<>();
    collections.add(new URI(getCollectionUri()));
    final QueryParameters params = new QueryParameters();
    params.setCollectionURIs(new ArrayList<>(collections));
    final URI endpoint = new URL(new URL(selectedRepo), "/sparql").toURI();
    final SBOLInteractionAdder_GeneCentric interactionAdder =
        new SBOLInteractionAdder_GeneCentric(endpoint, name, params);
    interactionAdder.addInteractions(document);
    return document;
  }

  protected DnaComponent getDnaComponentByName(final String name) {
    DnaComponent rtn = null;
    final Part part = getTargetDataInstance().getParts().findCObjectByName(name);
    final Gate gate = getTargetDataInstance().getGates().findCObjectByName(name);
    final InputSensor sensor = getTargetDataInstance().getInputSensors().findCObjectByName(name);
    final OutputDevice reporter =
        getTargetDataInstance().getOutputDevices().findCObjectByName(name);
    if (part != null) {
      rtn = part;
    }
    if (gate != null) {
      rtn = gate;
    }
    if (sensor != null) {
      rtn = sensor;
    }
    if (reporter != null) {
      rtn = reporter;
    }
    return rtn;
  }

  /**
   * Generate plasmid component definitions.
   *
   * @param document The {@link SBOLDocument}.
   * @throws SBOLValidationException unable to add plasmid definition
   * @throws SynBioHubException Unable to communicate with SynBioHub.
   */
  protected void addGroupDefinitions(final SBOLDocument document)
      throws SBOLValidationException, SynBioHubException {
    final Netlist netlist = getNetlist();
    final Placements placements = netlist.getResultNetlistData().getPlacements();
    for (int i = 0; i < placements.getNumPlacement(); i++) {
      final Placement placement = placements.getPlacementAtIdx(i);
      for (int j = 0; j < placement.getNumPlacementGroup(); j++) {
        final PlacementGroup group = placement.getPlacementGroupAtIdx(j);

        final String plasmidName = String.format("Design%d_Group%d", i, j);
        final String version = "1";
        final URI type = ComponentDefinition.DNA_REGION;

        final ComponentDefinition cd =
            document.createComponentDefinition(plasmidName, version, type);
        cd.addRole(SequenceOntology.ENGINEERED_REGION);

        group.setUri(cd.getIdentity());

        String sequence = "";

        // transcriptional units
        for (int k = 0; k < group.getNumComponent(); k++) {
          final org.cellocad.v2.results.placing.placement.Component component =
              group.getComponentAtIdx(k);
          String seq = "";
          for (int l = 0; l < component.getNumPart(); l++) {
            final String name = component.getPartAtIdx(l);
            final DnaComponent c = getDnaComponentByName(name);
            seq += SBOLDataUtils.getDnaSequence(c);
          }

          // Component
          final String cDisplayId = component.getName() + "_Component";
          final URI cDefinitionId = component.getUri();
          final AccessType cAccess = AccessType.PUBLIC;
          final org.sbolstandard.core2.Component c =
              cd.createComponent(cDisplayId, cAccess, cDefinitionId);

          // SequenceAnnotation
          final String saDisplayId = String.format("SequenceAnnotation%d", k);
          final String saLocationId = saDisplayId + "_Range";
          final int start = sequence.length() + 1;
          final int end = start + seq.length() - 1;
          final SequenceAnnotation sa =
              cd.createSequenceAnnotation(saDisplayId, saLocationId, start, end);
          sa.setComponent(c.getIdentity());
          sequence += seq;

          // SequenceConstraint
          if (k != 0) {
            final String scDisplayId = String.format("%s_Constraint%d", cd.getDisplayId(), k);
            final RestrictionType scRestriction = RestrictionType.PRECEDES;
            final URI scSubjectId =
                cd.getComponent(group.getComponentAtIdx(k - 1).getName() + "_Component")
                    .getIdentity();
            final URI scObjectId =
                cd.getComponent(component.getName() + "_Component").getIdentity();
            cd.createSequenceConstraint(scDisplayId, scRestriction, scSubjectId, scObjectId);
          }
        }

        // Sequence
        final String sDisplayId = cd.getDisplayId() + "_Sequence";
        final URI sEncoding = Sequence.IUPAC_DNA;
        final Sequence s = document.createSequence(sDisplayId, sequence, sEncoding);
        cd.addSequence(s);
      }
    }
  }

  /**
   * Add a {@link ModuleDefinition} representation of each circuit design.
   *
   * @param document The {@link SBOLDocument} to which to add the design modules.
   * @throws SBOLValidationException Unable to validate SBOL.
   */
  protected void addDesignModules(final SBOLDocument document) throws SBOLValidationException {
    final Netlist netlist = getNetlist();
    final Placements placements = netlist.getResultNetlistData().getPlacements();
    for (int i = 0; i < placements.getNumPlacement(); i++) {
      final Placement placement = placements.getPlacementAtIdx(i);

      final String mdDisplayId = String.format("Design%d_Module", i);
      final ModuleDefinition md = document.createModuleDefinition(mdDisplayId);
      placement.setUri(md.getIdentity());

      for (int j = 0; j < placement.getNumPlacementGroup(); j++) {
        final PlacementGroup group = placement.getPlacementGroupAtIdx(j);
        final String fcDisplayId = String.format("Design%d_Group%d", i, j);
        final AccessType fcAccess = AccessType.PUBLIC;
        final URI fcDefinitionURI = group.getUri();
        final DirectionType fcDirection = DirectionType.NONE;
        md.createFunctionalComponent(fcDisplayId, fcAccess, fcDefinitionURI, fcDirection);
      }
    }
  }

  /**
   * Run the (core) algorithm.
   *
   * @throws CelloException Unable to run the (core) algorithm.
   */
  @Override
  protected void run() throws CelloException {
    logInfo("creating SBOL document");

    // create document
    try {
      final SBOLDocument sbolDocument = createSbolDocument();
      setSbolDocument(sbolDocument);
    } catch (SynBioHubException | SBOLValidationException e) {
      throw new CelloException(e);
    }

    // add interactions
    if (getAddInteractions()) {
      logInfo("modeling component interactions");
      try {
        final SBOLDocument sbolDocument =
            addInteractions(getRepositoryUrl().toString(), getSbolDocument(), getDesignName());
        setSbolDocument(sbolDocument);
      } catch (IOException
          | SBOLValidationException
          | SBOLConversionException
          | VPRException
          | VPRTripleStoreException
          | URISyntaxException e) {
        throw new CelloException(e);
      }
    }

    // plasmid and design
    if (getAddDesignModules()) {
      logInfo("grouping inserts");
      try {
        addGroupDefinitions(getSbolDocument());
      } catch (SBOLValidationException | SynBioHubException e) {
        throw new CelloException(e);
      }
      if (!getAddInteractions()) {
        logInfo("adding design modules");
        try {
          addDesignModules(getSbolDocument());
        } catch (final SBOLValidationException e) {
          throw new CelloException(e);
        }
      }
    }
  }

  /**
   * Perform postprocessing.
   *
   * @throws CelloException Unable to perform postprocessing.
   */
  @Override
  protected void postprocessing() throws CelloException {
    logInfo("Writing SBOL document.");
    logDebug("SBOL filename " + getSbolFilename());
    try {
      SBOLWriter.write(getSbolDocument(), getSbolFilename());
    } catch (SBOLConversionException | IOException e) {
      throw new CelloException(e);
    }
    Result result =
        new Result(
            "sbol", "export", "An SBOL representation of the design.", new File(getSbolFilename()));
    try {
      this.getResults().addResult(result);
    } catch (IOException e) {
      throw new CelloException("Unable to write result.", e);
    }
  }

  protected String getDesignName() {
    String rtn = null;
    rtn = getNetlist().getName();
    if (rtn == null) {
      rtn = "circuit";
    }
    return rtn;
  }

  /*
   * Getter and Setter
   */
  /**
   * Getter for {@code repositoryUrl}.
   *
   * @return The value of {@code repositoryUrl}.
   */
  protected String getRepositoryUrl() {
    return repositoryUrl;
  }

  /**
   * Setter for {@code repositoryUrl}.
   *
   * @param repositoryUrl The value to set {@code repositoryUrl}.
   */
  protected void setRepositoryUrl(final String repositoryUrl) {
    this.repositoryUrl = repositoryUrl;
  }

  /**
   * Getter for {@code collectionUri}.
   *
   * @return The value of {@code collectionUri}.
   */
  public String getCollectionUri() {
    return collectionUri;
  }

  /**
   * Setter for {@code collectionUri}.
   *
   * @param collectionUri The value to set {@code collectionUri}.
   */
  public void setCollectionUri(final String collectionUri) {
    this.collectionUri = collectionUri;
  }

  /**
   * Getter for {@code addInteractions}.
   *
   * @return The value of {@code addInteractions}.
   */
  protected Boolean getAddInteractions() {
    return addInteractions;
  }

  /**
   * Setter for {@code addInteractions}.
   *
   * @param addInteractions The value to set {@code addInteractions}.
   */
  protected void setAddInteractions(final Boolean addInteractions) {
    this.addInteractions = addInteractions;
  }

  /**
   * Getter for {@code addPlasmidModules}.
   *
   * @return The value of {@code addPlasmidModules}.
   */
  protected Boolean getAddDesignModules() {
    return addDesignModules;
  }

  /**
   * Setter for {@code addPlasmidModules}.
   *
   * @param addPlasmidModules The value to set {@code addPlasmidModules}.
   */
  protected void setAddDesignModules(final Boolean addPlasmidModules) {
    addDesignModules = addPlasmidModules;
  }

  /**
   * Getter for {@code sbhFrontend}.
   *
   * @return The value of {@code sbhFrontend}.
   */
  protected SynBioHubFrontend getSbhFrontend() {
    return sbhFrontend;
  }

  /**
   * Setter for {@code sbhFrontend}.
   *
   * @param sbhFrontend The value to set {@code sbhFrontend}.
   */
  protected void setSbhFrontend(final SynBioHubFrontend sbhFrontend) {
    this.sbhFrontend = sbhFrontend;
  }

  /**
   * Getter for {@code sbolDocument}.
   *
   * @return The value of {@code sbolDocument}.
   */
  protected SBOLDocument getSbolDocument() {
    return sbolDocument;
  }

  /**
   * Setter for {@code sbolDocument}.
   *
   * @param sbolDocument The value to set {@code sbolDocument}.
   */
  protected void setSbolDocument(final SBOLDocument sbolDocument) {
    this.sbolDocument = sbolDocument;
  }

  /**
   * Getter for {@code sbolFilename}.
   *
   * @return The value of {@code sbolFilename}.
   */
  protected String getSbolFilename() {
    return sbolFilename;
  }

  /**
   * Setter for {@code sbolFilename}.
   *
   * @param sbolFilename The value to set {@code sbolFilename}.
   */
  protected void setSbolFilename(final String sbolFilename) {
    this.sbolFilename = sbolFilename;
  }

  /**
   * Getter for {@code targetDataInstance}.
   *
   * @return The value of {@code targetDataInstance}.
   */
  protected EXTargetDataInstance getTargetDataInstance() {
    return targetDataInstance;
  }

  /**
   * Setter for {@code targetDataInstance}.
   *
   * @param targetDataInstance The targetDataInstance to set.
   */
  protected void setTargetDataInstance(final EXTargetDataInstance targetDataInstance) {
    this.targetDataInstance = targetDataInstance;
  }

  private EXTargetDataInstance targetDataInstance;

  /**
   * Returns the {@link Logger} for the <i>SBOL</i> algorithm.
   *
   * @return The {@link Logger} for the <i>SBOL</i> algorithm.
   */
  @Override
  protected Logger getLogger() {
    return SBOL.logger;
  }

  private String repositoryUrl;
  private String collectionUri;
  private Boolean addInteractions;
  private Boolean addDesignModules;
  private SynBioHubFrontend sbhFrontend;
  private SBOLDocument sbolDocument;
  private String sbolFilename;
  private static final Logger logger = LogManager.getLogger(SBOL.class);
}

/**
 * Copyright (C) 2018-2019 Boston University (BU)
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

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cellocad.cello2.common.CObjectCollection;
import org.cellocad.cello2.common.Utils;
import org.cellocad.cello2.export.algorithm.EXAlgorithm;
import org.cellocad.cello2.export.algorithm.SBOL.data.Component;
import org.cellocad.cello2.export.algorithm.SBOL.data.Device;
import org.cellocad.cello2.export.algorithm.SBOL.data.SBOLDataUtils;
import org.cellocad.cello2.export.algorithm.SBOL.data.SBOLNetlistData;
import org.cellocad.cello2.export.algorithm.SBOL.data.SBOLNetlistEdgeData;
import org.cellocad.cello2.export.algorithm.SBOL.data.SBOLNetlistNodeData;
import org.cellocad.cello2.export.algorithm.SBOL.data.ucf.ContainerSpecification;
import org.cellocad.cello2.export.algorithm.SBOL.data.ucf.Gate;
import org.cellocad.cello2.export.algorithm.SBOL.data.ucf.InputSensor;
import org.cellocad.cello2.export.algorithm.SBOL.data.ucf.OutputReporter;
import org.cellocad.cello2.export.algorithm.SBOL.data.ucf.Part;
import org.cellocad.cello2.export.algorithm.SBOL.data.ucf.PlasmidSpecification;
import org.cellocad.cello2.export.runtime.environment.EXArgString;
import org.cellocad.cello2.results.netlist.Netlist;
import org.cellocad.cello2.results.netlist.NetlistEdge;
import org.cellocad.cello2.results.netlist.NetlistNode;
import org.cellocad.cello2.results.placing.placement.Placement;
import org.cellocad.cello2.results.placing.placement.PlacementGroup;
import org.cellocad.cello2.results.placing.placement.Placements;
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
 * The SBOL class implements the <i>SBOL</i> algorithm in the <i>export</i> stage.
 *
 * @author Timothy Jones
 *
 * @date 2018-06-04
 *
 */
public class SBOL extends EXAlgorithm{

	/**
	 *  Returns the <i>SBOLNetlistNodeData</i> of the <i>node</i>
	 *
	 *  @param node a node within the <i>netlist</i> of this instance
	 *  @return the <i>SBOLNetlistNodeData</i> instance if it exists, null otherwise
	 */
	protected SBOLNetlistNodeData getSBOLNetlistNodeData(NetlistNode node){
		SBOLNetlistNodeData rtn = null;
		rtn = (SBOLNetlistNodeData) node.getNetlistNodeData();
		return rtn;
	}

	/**
	 *  Returns the <i>SBOLNetlistEdgeData</i> of the <i>edge</i>
	 *
	 *  @param edge an edge within the <i>netlist</i> of this instance
	 *  @return the <i>SBOLNetlistEdgeData</i> instance if it exists, null otherwise
	 */
	protected SBOLNetlistEdgeData getSBOLNetlistEdgeData(NetlistEdge edge){
		SBOLNetlistEdgeData rtn = null;
		rtn = (SBOLNetlistEdgeData) edge.getNetlistEdgeData();
		return rtn;
	}

	/**
	 *  Returns the <i>SBOLNetlistData</i> of the <i>netlist</i>
	 *
	 *  @param netlist the netlist of this instance
	 *  @return the <i>SBOLNetlistData</i> instance if it exists, null otherwise
	 */
	protected SBOLNetlistData getSBOLNetlistData(Netlist netlist){
		SBOLNetlistData rtn = null;
		rtn = (SBOLNetlistData) netlist.getNetlistData();
		return rtn;
	}

	/**
	 *  Gets the Constraint data from the NetlistConstraintFile
	 */
	@Override
	protected void getConstraintFromNetlistConstraintFile() {

	}

	/**
	 *  Gets the data from the UCF
	 */
	@Override
	protected void getDataFromUCF() {
		this.setParts(SBOLDataUtils.getParts(this.getTargetData()));
		this.setGates(SBOLDataUtils.getGates(this.getTargetData()));
		this.setInputSensors(SBOLDataUtils.getInputSensors(this.getTargetData()));
		this.setOutputReporters(SBOLDataUtils.getOutputReporters(this.getTargetData()));
		this.setContainerSpecifications(SBOLDataUtils.getContainerSpecifications(this.getTargetData()));
	}

	/**
	 *  Set parameter(s) value(s) of the algorithm
	 */
	@Override
	protected void setParameterValues() {
		Boolean present = false;
		present = this.getAlgorithmProfile().getStringParameter("RepositoryUrl").getFirst();
		if (present) {
			this.setRepositoryUrl(this.getAlgorithmProfile().getStringParameter("RepositoryUrl").getSecond());
		}
		present = this.getAlgorithmProfile().getStringParameter("CollectionUri").getFirst();
		if (present) {
			this.setCollectionUri(this.getAlgorithmProfile().getStringParameter("CollectionUri").getSecond());
		}
		present = this.getAlgorithmProfile().getBooleanParameter("AddInteractions").getFirst();
		if (present) {
			this.setAddInteractions(this.getAlgorithmProfile().getBooleanParameter("AddInteractions").getSecond());
		}
		present = this.getAlgorithmProfile().getBooleanParameter("AddPlasmidModules").getFirst();
		if (present) {
			this.setAddPlasmidModules(this.getAlgorithmProfile().getBooleanParameter("AddPlasmidModules").getSecond());
		}
	}

	/**
	 * Validate parameter value for <i>repositoryUrl</i>
	 */
	protected void validateRepositoryUrlValue() {
		String url = this.getRepositoryUrl();
		if (url != null) {
			try {
				new URL(url);
			} catch (MalformedURLException e) {
				this.logError(url + " is not a valid URL!");
				Utils.exit(-1);
			}
		}
	}

	/**
	 *  Validate parameter value of the algorithm
	 */
	@Override
	protected void validateParameterValues() {
		this.validateRepositoryUrlValue();
	}

	/**
	 *  Perform preprocessing
	 */
	@Override
	protected void preprocessing() {
		// sbh frontend
		if (this.getRepositoryUrl() != null) {
			this.setSbhFrontend(new SynBioHubFrontend(this.getRepositoryUrl()));
		}
		// output directory
		String outputDir = this.getRuntimeEnv().getOptionValue(EXArgString.OUTPUTDIR);
		// output filename
		String inputFilename = this.getNetlist().getInputFilename();
		String filename = Utils.getFilename(inputFilename);
		this.setSbolFilename(outputDir + Utils.getFileSeparator() + filename + ".xml");
	}

	/**
	 * Add component definitions for all parts of all gates in the netlist.
	 * @param netlist the <i>netlist</i> with component definitions to add
	 * @param document the <i>SBOLDocument</i> to which to add the definitions
	 * @throws SynBioHubException unable to fetch part SBOL from SynBioHub
	 * @throws SBOLValidationException unable to create component definition
	 */
	protected void addComponentDefinitions(SBOLDocument document) throws SynBioHubException, SBOLValidationException {
		Netlist netlist = this.getNetlist();
		Placements placements = netlist.getResultNetlistData().getPlacements();
		for (int i = 0; i < placements.getNumPlacement(); i++) {
			Placement placement = placements.getPlacementAtIdx(i);
			for (int j = 0; j < placement.getNumPlacementGroup(); j++) {
				PlacementGroup group = placement.getPlacementGroupAtIdx(j);
				for (int k = 0; k < group.getNumComponent(); k++) {
					org.cellocad.cello2.results.placing.placement.Component component = group.getComponentAtIdx(k);
					for (int l = 0; l < component.getNumPart(); l++) {
						String str = component.getPartAtIdx(l);
						Part part = this.getParts().findCObjectByName(str);
						Gate gate = this.getGates().findCObjectByName(str);
						InputSensor sensor = this.getInputSensors().findCObjectByName(str);
						OutputReporter reporter = this.getOutputReporters().findCObjectByName(str);
						if (part != null) {
							SBOLUtils.addPartDefinition(part, document, this.getSbhFrontend());
							continue;
						}
						Device device = null;
						if (gate != null)
							device = gate;
						if (sensor != null)
							device = sensor;
						if (reporter != null)
							device = reporter;
						if (device != null)
							SBOLUtils.addDeviceDefinition(device, document, this.getSbhFrontend());
					}
				}
			}
		}
	}

	/**
	 * Add transcriptional unit component definitions to an SBOLDocument.
	 * @param document the SBOLDocument
	 * @throws SBOLValidationException unable to add transcriptional unit
	 */
	protected void addTranscriptionalUnitDefinitions(SBOLDocument document) throws SBOLValidationException {
		Netlist netlist = this.getNetlist();
		//String name = netlist.getName();
		Placements placements = netlist.getResultNetlistData().getPlacements();
		for (int i = 0; i < placements.getNumPlacement(); i++) {
			Placement placement = placements.getPlacementAtIdx(i);
			for (int j = 0; j < placement.getNumPlacementGroup(); j++) {
				PlacementGroup group = placement.getPlacementGroupAtIdx(j);
				String name = String.format("d%dp%d",i,j);
				for (int k = 0; k < group.getNumComponent(); k++) {
					org.cellocad.cello2.results.placing.placement.Component component = group.getComponentAtIdx(k);
					/*String nodeName = component.getNode();
					NetlistNode node = null;
					for (int l = 0; l < netlist.getNumVertex(); l++) {
						NetlistNode v = netlist.getVertexAtIdx(l);
						if (v.getName().equals(nodeName)) {
							node = v;
							break;
						}
					}
					String gateType = node.getResultNetlistNodeData().getGateType(); */

					// ComponentDefinition
					ComponentDefinition cd = document.createComponentDefinition(name + "_" + component.getName(),"1",ComponentDefinition.DNA_REGION);
					cd.addRole(SequenceOntology.ENGINEERED_REGION);
					component.setUri(cd.getIdentity());

					// parts
					String sequence = "";
					for (int l = 0; l < component.getNumPart(); l++) {
						String componentName = component.getPartAtIdx(l);
						Component comp = this.getComponentByName(componentName);
						// Component
						String cDisplayId = componentName + "_component";
						AccessType cAccess = AccessType.PUBLIC;
						URI cDefinitionURI = URI.create(comp.getUri());
						org.sbolstandard.core2.Component c = cd.createComponent(cDisplayId,cAccess,cDefinitionURI);

						// SequenceAnnotation
						String s = SBOLDataUtils.getDNASequence(comp);
						String saDisplayId = "SequenceAnnotation" + String.valueOf(l);
						String saLocationId = saDisplayId + "_Range";
						int start = sequence.length() + 1;
						int end = start + s.length() - 1;
						SequenceAnnotation sa = cd.createSequenceAnnotation(saDisplayId,saLocationId,start,end);
						sa.setComponent(c.getIdentity());
						sequence += s;

						// SequenceConstraint
						if (l != 0) {
							String scDisplayId = String.format("%s_Constraint_%d",cd.getDisplayId(),l);
							RestrictionType scRestriction = RestrictionType.PRECEDES;
							URI scSubjectId = cd.getComponent(component.getPartAtIdx(l-1) + "_component").getIdentity();
							URI scObjectId = cd.getComponent(componentName + "_component").getIdentity();
							cd.createSequenceConstraint(scDisplayId,scRestriction,scSubjectId,scObjectId);
						}
					}
					// Sequence
					Sequence s = document.createSequence(cd.getDisplayId() + "_sequence",sequence,Sequence.IUPAC_DNA);
					cd.addSequence(s);
				}
			}
		}
	}

	/**
	 * Create an SBOL document.
	 * @return The generated SBOLDocument.
	 * @throws SynBioHubException - Unable to fetch SBOL from SynBioHub for a part.
	 * @throws SBOLValidationException - Unable to create Component or ComponentDefinition.
	 */
	protected SBOLDocument createSBOLDocument() throws SynBioHubException, SBOLValidationException {
		SBOLDocument document = new SBOLDocument();
		document.setDefaultURIprefix("http://cellocad.org/v2");

		this.addComponentDefinitions(document);
		this.addTranscriptionalUnitDefinitions(document);

		return document;
	}

	/**
	 * Add device interactions via the Virtual Parts (VPR) API.
	 * @param selectedRepo - The specified synbiohub repository the user wants VPR model generator to connect to.
	 * @param generatedModel - The file to generate the model from.
	 * @param name - The top level design name.
	 * @return The SBOL Document with interactions.
	 * @throws SBOLValidationException
	 * @throws IOException - Unable to read or write the given SBOLDocument
	 * @throws SBOLConversionException - Unable to perform conversion for the given SBOLDocument.
	 * @throws VPRException - Unable to perform VPR Model Generation on the given SBOLDocument.
	 * @throws VPRTripleStoreException - Unable to perform VPR Model Generation on the given SBOLDocument.
	 */
	protected SBOLDocument addInteractions(String selectedRepo, SBOLDocument document, String name) throws SBOLValidationException, IOException, SBOLConversionException, VPRException, VPRTripleStoreException, URISyntaxException
	{
		Set<URI> collections = new HashSet<URI>();
		collections.add(new URI(this.getCollectionUri()));
		QueryParameters params = new QueryParameters();
		params.setCollectionURIs(new ArrayList<>(collections));
		URI endpoint = new URL(new URL(selectedRepo),"/sparql").toURI();
		SBOLInteractionAdder_GeneCentric interactionAdder = new SBOLInteractionAdder_GeneCentric(endpoint,name,params);
		interactionAdder.addInteractions(document);
		return document;
	}
	
	protected Component getComponentByName(String name) {
		Component rtn = null;
		Part part = this.getParts().findCObjectByName(name);
		Gate gate = this.getGates().findCObjectByName(name);
		InputSensor sensor = this.getInputSensors().findCObjectByName(name);
		OutputReporter reporter = this.getOutputReporters().findCObjectByName(name);
		if (part != null)
			rtn = part;
		if (gate != null)
			rtn = gate;
		if (sensor != null)
			rtn = sensor;
		if (reporter != null)
			rtn = reporter;
		return rtn;
	}

	/**
	 * Generate plasmid component definitions.
	 * @param document the <i>SBOLDocument</i>
	 * @throws SBOLValidationException unable to add plasmid definition
	 * @throws SynBioHubException 
	 */
	protected void addPlasmidDefinitions(SBOLDocument document) throws SBOLValidationException, SynBioHubException {
		Netlist netlist = this.getNetlist();
		Placements placements = netlist.getResultNetlistData().getPlacements();
		for (int i = 0; i < placements.getNumPlacement(); i++) {
			Placement placement = placements.getPlacementAtIdx(i);
			for (int j = 0; j < placement.getNumPlacementGroup(); j++) {
				PlacementGroup group = placement.getPlacementGroupAtIdx(j);

				String plasmidName = String.format("design_%d_group_%d", i, j);
				String version = "1";
				URI type = ComponentDefinition.DNA_REGION;

				ComponentDefinition cd = document.createComponentDefinition(plasmidName,version,type);
				cd.addRole(SequenceOntology.ENGINEERED_REGION);

				group.setUri(cd.getIdentity());

				String sequence = "";

				// backbone
				ContainerSpecification spec = this.getContainerSpecifications().findCObjectByName(group.getName());
				if (spec == null)
					throw new RuntimeException("Unknown container.");
				if (spec instanceof PlasmidSpecification) {
					cd.addType(SequenceOntology.CIRCULAR);
					PlasmidSpecification plasmid = (PlasmidSpecification) spec;
					Part backbone = this.getParts().findCObjectByName(plasmid.getBackbone());
					String seq = SBOLDataUtils.getDNASequence(backbone);
					
					ComponentDefinition backboneCd = SBOLUtils.addPartDefinition(backbone, document, this.getSbhFrontend());
					backboneCd.addRole(URI.create(S_BACKBONE_ROLE)); // plasmid_vector

					URI cDefinition = URI.create(backbone.getUri());
					String cDisplayId = "backbone_component";
					AccessType cAccess = AccessType.PUBLIC;
					org.sbolstandard.core2.Component bbComponent = cd.createComponent(cDisplayId,cAccess,cDefinition);

					String displayId = "SequenceAnnotation_backbone";
					String locationId = displayId + "_Range";
					int start = sequence.length() + 1;
					int end = start + seq.length() - 1;

					SequenceAnnotation bbAnnotation = cd.createSequenceAnnotation(displayId,locationId,start,end);
					bbAnnotation.setComponent(bbComponent.getIdentity());
					sequence += seq;
				}

				// transcriptional units
				for (int k = 0; k < group.getNumComponent(); k++) {
					org.cellocad.cello2.results.placing.placement.Component component = group.getComponentAtIdx(k);
					String seq = "";
					for (int l = 0; l < component.getNumPart(); l++) {
						String name = component.getPartAtIdx(l);
						Component c = this.getComponentByName(name);
						seq += SBOLDataUtils.getDNASequence(c);
					}

					// Component
					String cDisplayId = component.getName() + "_component";
					URI cDefinitionId = component.getUri();
					AccessType cAccess = AccessType.PUBLIC;
					org.sbolstandard.core2.Component c = cd.createComponent(cDisplayId,cAccess,cDefinitionId);

					// SequenceAnnotation
					String saDisplayId = String.format("SequenceAnnotation_%d",k);
					String saLocationId = saDisplayId + "_Range";
					int start = sequence.length() + 1;
					int end = start + seq.length() - 1;
					SequenceAnnotation sa = cd.createSequenceAnnotation(saDisplayId,saLocationId,start,end);
					sa.setComponent(c.getIdentity());
					sequence += seq;

					// SequenceConstraint
					if (k != 0) {
						String scDisplayId = String.format("%s_Constraint_%d",cd.getDisplayId(),k);
						RestrictionType scRestriction = RestrictionType.PRECEDES;
						URI scSubjectId = cd.getComponent(group.getComponentAtIdx(k-1).getName() + "_component").getIdentity();
						URI scObjectId = cd.getComponent(component.getName() + "_component").getIdentity();
						cd.createSequenceConstraint(scDisplayId,scRestriction,scSubjectId,scObjectId);
					}
				}

				// Sequence
				String sDisplayId = cd.getDisplayId() + "_sequence";
				URI sEncoding = Sequence.IUPAC_DNA;
				Sequence s = document.createSequence(sDisplayId,sequence,sEncoding);
				cd.addSequence(s);
			}
		}
	}

	/**
	 * Add a ModuleDefinition representation of each circuit design.
	 * @param document
	 * @throws SBOLValidationException
	 */
	protected void addDesignModules(SBOLDocument document) throws SBOLValidationException {
		Netlist netlist = this.getNetlist();
		Placements placements = netlist.getResultNetlistData().getPlacements();
		for (int i = 0; i < placements.getNumPlacement(); i++) {
			Placement placement = placements.getPlacementAtIdx(i);

			String mdDisplayId = String.format("design_module_%d",i);
			ModuleDefinition md = document.createModuleDefinition(mdDisplayId);
			placement.setUri(md.getIdentity());

			for (int j = 0; j < placement.getNumPlacementGroup(); j++) {
				PlacementGroup group = placement.getPlacementGroupAtIdx(j);
				String fcDisplayId = String.format("design_%d_plasmid_%d",i,j);
				AccessType fcAccess = AccessType.PUBLIC;
				URI fcDefinitionURI = group.getUri();
				DirectionType fcDirection = DirectionType.NONE;
				md.createFunctionalComponent(fcDisplayId,fcAccess,fcDefinitionURI,fcDirection);
			}

		}

	}

	/**
	 *  Run the (core) algorithm
	 */
	@Override
	protected void run() {
		logInfo("creating SBOL document");

		// create document
		try {
			SBOLDocument sbolDocument = this.createSBOLDocument();
			this.setSbolDocument(sbolDocument);
		} catch (SynBioHubException | SBOLValidationException e) {
			e.printStackTrace();
		}

		// add interactions
		if (this.getAddInteractions()) {
			logInfo("modeling component interactions");
			try {
				SBOLDocument sbolDocument = this.addInteractions(this.getRepositoryUrl().toString(),this.getSbolDocument(),this.getDesignName());
				this.setSbolDocument(sbolDocument);
			} catch (IOException | SBOLValidationException | SBOLConversionException | VPRException | VPRTripleStoreException | URISyntaxException e) {
				e.printStackTrace();
			}
		}

		// plasmid and design
		if (this.getAddPlasmidModules()) {
			logInfo("adding plasmids");
			try {
				this.addPlasmidDefinitions(this.getSbolDocument());
			} catch (SBOLValidationException | SynBioHubException e) {
				e.printStackTrace();
			}
			if (!this.getAddInteractions()) {
				logInfo("adding design modules");
				try {
					this.addDesignModules(this.getSbolDocument());
				} catch (SBOLValidationException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 *  Perform postprocessing
	 */
	@Override
	protected void postprocessing() {
		logInfo("writing SBOL document");
		logDebug("SBOL filename " + getSbolFilename());
		try {
			SBOLWriter.write(this.getSbolDocument(),getSbolFilename());
		} catch (SBOLConversionException | IOException e) {
			e.printStackTrace();
		}
	}

	protected String getDesignName() {
		String rtn = null;
		rtn = this.getNetlist().getName();
		if (rtn == null) {
			rtn = "circuit";
		}
		return rtn;
	}


	/*
	 * Getter and Setter
	 */
	/**
	 * Getter for <i>repositoryUrl</i>
	 * @return value of <i>repositoryUrl</i>
	 */
	protected String getRepositoryUrl() {
		return this.repositoryUrl;
	}

	/**
	 * Setter for <i>repositoryUrl</i>
	 * @param repositoryUrl the value to set <i>repositoryUrl</i>
	 */
	protected void setRepositoryUrl(final String repositoryUrl) {
		this.repositoryUrl = repositoryUrl;
	}

	/**
	 * Getter for <i>collectionUri</i>
	 * @return value of <i>collectionUri</i>
	 */
	public String getCollectionUri() {
		return collectionUri;
	}

	/**
	 * Setter for <i>collectionUri</i>
	 * @param collectionUri the value to set <i>collectionUri</i>
	 */
	public void setCollectionUri(final String collectionUri) {
		this.collectionUri = collectionUri;
	}

	/**
	 * Getter for <i>addInteractions</i>
	 * @return value of <i>addInteractions</i>
	 */
	protected Boolean getAddInteractions() {
		return this.addInteractions;
	}

	/**
	 * Setter for <i>addInteractions</i>
	 * @param addInteractions the value to set <i>addInteractions</i>
	 */
	protected void setAddInteractions(final Boolean addInteractions) {
		this.addInteractions = addInteractions;
	}

	/**
	 * Getter for <i>addPlasmidModules</i>
	 * @return value of <i>addPlasmidModules</i>
	 */
	protected Boolean getAddPlasmidModules() {
		return this.addPlasmidModules;
	}

	/**
	 * Setter for <i>addPlasmidModules</i>
	 * @param addPlasmidModules the value to set <i>addPlasmidModules</i>
	 */
	protected void setAddPlasmidModules(final Boolean addPlasmidModules) {
		this.addPlasmidModules = addPlasmidModules;
	}

	/**
	 * Getter for <i>sbhFrontend</i>
	 * @return value of <i>sbhFrontend</i>
	 */
	protected SynBioHubFrontend getSbhFrontend() {
		return this.sbhFrontend;
	}

	/**
	 * Setter for <i>sbhFrontend</i>
	 * @param sbhFrontend the value to set <i>sbhFrontend</i>
	 */
	protected void setSbhFrontend(final SynBioHubFrontend sbhFrontend) {
		this.sbhFrontend = sbhFrontend;
	}

	/**
	 * Getter for <i>sbolDocument</i>
	 * @return value of <i>sbolDocument</i>
	 */
	protected SBOLDocument getSbolDocument() {
		return sbolDocument;
	}

	/**
	 * Setter for <i>sbolDocument</i>
	 * @param sbolDocument the value to set <i>sbolDocument</i>
	 */
	protected void setSbolDocument(final SBOLDocument sbolDocument) {
		this.sbolDocument = sbolDocument;
	}

	/**
	 * Getter for <i>sbolFilename</i>
	 * @return value of <i>sbolFilename</i>
	 */
	protected String getSbolFilename() {
		return sbolFilename;
	}

	/**
	 * Setter for <i>sbolFilename</i>
	 * @param sbolFilename the value to set <i>sbolFilename</i>
	 */
	protected void setSbolFilename(final String sbolFilename) {
		this.sbolFilename = sbolFilename;
	}

	/**
	 * Getter for <i>parts</i>
	 * @return value of <i>parts</i>
	 */
	protected CObjectCollection<Part> getParts() {
		return parts;
	}

	/**
	 * Setter for <i>parts</i>
	 * @param parts the value to set <i>parts</i>
	 */
	protected void setParts(final CObjectCollection<Part> parts) {
		this.parts = parts;
	}

	/**
	 * Getter for <i>gates</i>
	 * @return value of <i>gates</i>
	 */
	protected CObjectCollection<Gate> getGates() {
		return gates;
	}

	/**
	 * Setter for <i>gates</i>
	 * @param gates the value to set <i>gates</i>
	 */
	protected void setGates(CObjectCollection<Gate> gates) {
		this.gates = gates;
	}

	/**
	 * Getter for <i>sensors</i>
	 * @return value of <i>sensors</i>
	 */
	public CObjectCollection<InputSensor> getInputSensors() {
		return sensors;
	}

	/**
	 * Setter for <i>sensors</i>
	 * @param sensors the value to set <i>sensors</i>
	 */
	protected void setInputSensors(final CObjectCollection<InputSensor> sensors) {
		this.sensors = sensors;
	}

	/**
	 * Getter for <i>reporters</i>
	 * @return value of <i>reporters</i>
	 */
	public CObjectCollection<OutputReporter> getOutputReporters() {
		return reporters;
	}

	/**
	 * Setter for <i>reporters</i>
	 * @param reporters the value to set <i>reporters</i>
	 */
	protected void setOutputReporters(final CObjectCollection<OutputReporter> reporters) {
		this.reporters = reporters;
	}

	/**
	 * Getter for <i>containerSpecifications</i>
	 * @return value of <i>containerSpecifications</i>
	 */
	public CObjectCollection<ContainerSpecification> getContainerSpecifications() {
		return containerSpecifications;
	}

	/**
	 * Setter for <i>containerSpecifications</i>
	 * @param containerSpecifications the value to set <i>containerSpecifications</i>
	 */
	public void setContainerSpecifications(CObjectCollection<ContainerSpecification> containerSpecifications) {
		this.containerSpecifications = containerSpecifications;
	}

	/**
	 *  Returns the Logger for the <i>SBOL</i> algorithm
	 *
	 *  @return the logger for the <i>SBOL</i> algorithm
	 */
	@Override
	protected Logger getLogger() {
		return SBOL.logger;
	}

	private String repositoryUrl;
	private String collectionUri;
	private Boolean addInteractions;
	private Boolean addPlasmidModules;
	private SynBioHubFrontend sbhFrontend;
	private SBOLDocument sbolDocument;
	private String sbolFilename;
	private CObjectCollection<Part> parts;
	private CObjectCollection<Gate> gates;
	private CObjectCollection<InputSensor> sensors;
	private CObjectCollection<OutputReporter> reporters;
	private CObjectCollection<ContainerSpecification> containerSpecifications;
	private static final Logger logger = LogManager.getLogger(SBOL.class);

	private static final String S_BACKBONE_ROLE = "http://identifiers.org/so/SO:0000755";
}

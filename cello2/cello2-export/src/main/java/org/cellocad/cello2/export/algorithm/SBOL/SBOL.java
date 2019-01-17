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

import org.cellocad.cello2.results.netlist.NetlistUtils;

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
import org.cellocad.cello2.export.algorithm.SBOL.data.plasmid.Plasmid;
import org.cellocad.cello2.export.algorithm.SBOL.data.plasmid.Plasmids;
import org.cellocad.cello2.export.algorithm.SBOL.data.plasmid.TranscriptionalUnit;
import org.cellocad.cello2.export.algorithm.SBOL.data.ucf.CasetteParts;
import org.cellocad.cello2.export.algorithm.SBOL.data.ucf.Gate;
import org.cellocad.cello2.export.algorithm.SBOL.data.ucf.GateParts;
import org.cellocad.cello2.export.algorithm.SBOL.data.ucf.Part;
import org.cellocad.cello2.export.algorithm.SBOL.data.ucf.ResponseFunction;
import org.cellocad.cello2.export.algorithm.SBOL.data.ucf.ResponseFunctionVariable;
import org.cellocad.cello2.logicSynthesis.runtime.environment.LSArgString;
import org.cellocad.cello2.results.netlist.Netlist;
import org.cellocad.cello2.results.netlist.NetlistEdge;
import org.cellocad.cello2.results.netlist.NetlistNode;
import org.sbolstandard.core2.AccessType;
import org.sbolstandard.core2.ComponentDefinition;
import org.sbolstandard.core2.DirectionType;
import org.sbolstandard.core2.FunctionalComponent;
import org.sbolstandard.core2.Module;
import org.sbolstandard.core2.ModuleDefinition;
import org.sbolstandard.core2.RefinementType;
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
		this.setGates(SBOLDataUtils.getGates(this.getTargetData()));
		this.setParts(SBOLDataUtils.getParts(this.getTargetData()));
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

		present = this.getAlgorithmProfile().getBooleanParameter("AddBackbone").getFirst();
		if (present) {
			this.setAddBackbone(this.getAlgorithmProfile().getBooleanParameter("AddBackbone").getSecond());
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
		NetlistUtils.writeJSONForNetlist(this.getNetlist(), "/home/tsj/foo.json");
		// plasmids
		this.setPlasmids(new Plasmids(this.getNetlist(),true,false,this.getParts(),this.getGates()));
		// sbh frontend
		if (this.getRepositoryUrl() != null) {
			this.setSbhFrontend(new SynBioHubFrontend(this.getRepositoryUrl()));
		}
		// output directory
		String outputDir = this.getRuntimeEnv().getOptionValue(LSArgString.OUTPUTDIR);
		// output filename
		String inputFilename = this.getNetlist().getInputFilename();
		String filename = Utils.getFilename(inputFilename);
		this.setSbolFilename(outputDir + Utils.getFileSeparator() + filename + ".xml");
	}
	
	/**
	 * Add a component definition of <i>part</i> to <i>document</i>. Use SynBioHub definition if available, add sequences if found.
	 * @param part the part to add
	 * @param document the <i>SBOLDocument</i> to add the <i>ComponentDefinition</i>
	 * @throws SynBioHubException unable to fetch SBOL from SynBioHub for <i>part</i>
	 * @throws SBOLValidationException unable to create component definition
	 */
	protected ComponentDefinition addPartDefinition(Part part, SBOLDocument document) throws SynBioHubException, SBOLValidationException {
		String uri = part.getUri();
		ComponentDefinition rtn = null;

		if ((uri != null) && (this.getSbhFrontend() != null)) {
			URI temp = URI.create(uri);
			SBOLDocument sbol = this.getSbhFrontend().getSBOL(temp);
			rtn = sbol.getComponentDefinition(temp);
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
			rtn = document.createComponentDefinition(part.getName(),"1",ComponentDefinition.DNA);
			part.setUri(rtn.getIdentity().toString());
			Sequence sequence = document.createSequence(part.getName() + "_sequence",part.getDNASequence(),Sequence.IUPAC_DNA);
			rtn.addSequence(sequence);
		}
		
		return rtn;
	}
	
	/**
	 * Add component definitions for all parts of all gates in the netlist.
	 * @param netlist the <i>netlist</i> with component definitions to add
	 * @param document the <i>SBOLDocument</i> to which to add the definitions
	 * @throws SynBioHubException unable to fetch part SBOL from SynBioHub
	 * @throws SBOLValidationException unable to create component definition
	 */
	protected void addPartDefinitions(SBOLDocument document) throws SynBioHubException, SBOLValidationException {
		Netlist netlist = this.getNetlist();
		Plasmid plasmid = this.getPlasmids().getPlasmidAtIdx(0);
		for (int i = 0; i < netlist.getNumVertex(); i++) {
			NetlistNode node = netlist.getVertexAtIdx(i);
			TranscriptionalUnit unit = plasmid.getTranscriptionalUnit(node);
			for (int j = 0; j < unit.getNumComponent(); j++) {
				Component component = unit.getComponentAtIdx(j);
				if (component instanceof Part) {
					Part part = (Part)component;
					this.addPartDefinition(part, document);
				}
			}
		}
	}

	protected String getGateDNASequence(Gate gate) {
		String rtn = "";
		ResponseFunction rf = gate.getResponseFunction();
		GateParts gateParts = gate.getGateParts();
		for (int i = 0; i < rf.getNumVariable(); i++) {
			ResponseFunctionVariable var = rf.getVariableAtIdx(i);
			CasetteParts casetteParts = gateParts.getCasetteParts(var.getName());
			for (int j = 0; j < casetteParts.getNumParts(); j++) {
				Part part = casetteParts.getPartAtIdx(j);
				rtn += part.getDNASequence();
			}
		}
		return rtn;
	}
	
	/**
	 * Add transcriptional unit component definitions to an SBOLDocument.
	 * @param document the SBOLDocument
	 * @throws SBOLValidationException unable to add transcriptional unit
	 */
	protected void addTranscriptionalUnitDefinitions(SBOLDocument document) throws SBOLValidationException {
		Netlist netlist = this.getNetlist();
		String name = netlist.getName();
		
		// TODO: transcriptional units might have different promoter orderings
		Plasmid plasmid = this.getPlasmids().getPlasmidAtIdx(0);
		for (int i = 0; i < netlist.getNumVertex(); i++) {
			NetlistNode node = netlist.getVertexAtIdx(i);
			String gateType = node.getResultNetlistNodeData().getGateType();
			
			// ComponentDefinition
			ComponentDefinition cd = document.createComponentDefinition(name + "_" + gateType,"1",ComponentDefinition.DNA);
			cd.addRole(SequenceOntology.ENGINEERED_REGION);
			
			// parts
			TranscriptionalUnit unit = plasmid.getTranscriptionalUnit(node);
			String sequence = "";
			for (int j = 0; j < unit.getNumComponent(); j++) {
				Component component = unit.getComponentAtIdx(j);
				// TODO
				if (component instanceof Part) {
					Part part = (Part)component;
				
					// Component
					org.sbolstandard.core2.Component c = cd.createComponent(part.getName() + "_component",
																			AccessType.PUBLIC,
																			URI.create(part.getUri()));
				
					// SequenceAnnotation
					SequenceAnnotation sa =
						cd.createSequenceAnnotation("SequenceAnnotation" + String.valueOf(j),
													"SequenceAnnotation" + String.valueOf(j) + "_Range",
													sequence.length() + 1,
													sequence.length() + 1 + part.getDNASequence().length());
					sa.setComponent(c.getIdentity());
					sequence += part.getDNASequence();
				
					// SequenceConstraint
					if (j != 0) {
						cd.createSequenceConstraint(cd.getDisplayId() + "Constraint" + String.valueOf(j),
													RestrictionType.PRECEDES,
													cd.getComponent(unit.getComponentAtIdx(j-1).getName() + "_component").getIdentity(),
													cd.getComponent(part.getName() + "_component").getIdentity());
					}
				}
				else if (component instanceof Device) {
					Device device = (Device)component;
					Gate gate = this.getGates().findCObjectByName(device.getName());

					// Component
					org.sbolstandard.core2.Component c = cd.createComponent(gate.getName() + "_component",
																			AccessType.PUBLIC,
																			URI.create(gate.getUri()));

					// SequenceAnnotation
					SequenceAnnotation sa =
						cd.createSequenceAnnotation("SequenceAnnotation" + String.valueOf(j),
													"SequenceAnnotation" + String.valueOf(j) + "_Range",
													sequence.length() + 1,
													sequence.length() + 1 + this.getGateDNASequence(gate).length());
					sa.setComponent(c.getIdentity());
					sequence += this.getGateDNASequence(gate);

					// SequenceConstraint
					if (j != 0) {
						cd.createSequenceConstraint(cd.getDisplayId() + "Constraint" + String.valueOf(j),
													RestrictionType.PRECEDES,
													cd.getComponent(unit.getComponentAtIdx(j-1).getName() + "_component").getIdentity(),
													cd.getComponent(gate.getName() + "_component").getIdentity());
					}
				}
			}
			// Sequence
			Sequence s = document.createSequence(cd.getDisplayId() + "_sequence",sequence,Sequence.IUPAC_DNA);
			cd.addSequence(s);
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

		this.addPartDefinitions(document);
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
	
	/**
	 * Add the plasmid backbone ComponentDefinition to the <i>document</i>
	 * @param document the SBOLDocument
	 * @throws SynBioHubException unable to fetch backbone ComponentDefinition
	 * @throws SBOLValidationException unable to add backbone ComponentDefinition
	 */
	protected void addBackboneDefinition(SBOLDocument document) throws SynBioHubException, SBOLValidationException {
		ComponentDefinition cd = this.addPartDefinition(this.getParts().findCObjectByName("backbone"), document);
		cd.addRole(URI.create(S_BACKBONE_ROLE)); // plasmid_vector
	}

	/**
	 * Generate plasmid component definitions.
	 * @param document the <i>SBOLDocument</i>
	 * @throws SBOLValidationException unable to add plasmid definition
	 */
	protected void addPlasmidDefinitions(SBOLDocument document) throws SBOLValidationException {
		for (int i = 0; i < this.getPlasmids().getNumPlasmids(); i++) {
			Plasmid plasmid = this.getPlasmids().getPlasmidAtIdx(i);
			ComponentDefinition cd = document.createComponentDefinition("plasmid_" + String.valueOf(i),"1",ComponentDefinition.DNA);
			plasmid.setUri(cd.getIdentity());
			cd.addRole(SequenceOntology.ENGINEERED_REGION);
			cd.addType(SequenceOntology.CIRCULAR);
			
			String sequence = "";
			
			// backbone
			if (this.getAddBackbone()) {
				Part backbone = this.getParts().findCObjectByName("backbone");
				org.sbolstandard.core2.Component bbComponent = cd.createComponent("backbone_component", AccessType.PUBLIC, "backbone");
				SequenceAnnotation bbAnnotation = cd.createSequenceAnnotation("SequenceAnnotation_backbone",
					"SequenceAnnotation_backbone_Range",
					sequence.length() + 1,
					sequence.length() + 1 + backbone.getDNASequence().length());
				bbAnnotation.setComponent(bbComponent.getIdentity());
				sequence += backbone.getDNASequence();
			}
			
			// transcriptional units
			for (int j = 0; j < plasmid.getNumTranscriptionalUnits(); j++) {
				TranscriptionalUnit unit = plasmid.getTranscriptionalUnitAtIdx(j);
				String name = unit.getName();
				// Component
				org.sbolstandard.core2.Component c = cd.createComponent(name + "_component", AccessType.PUBLIC, name);
				
				// SequenceAnnotation
				SequenceAnnotation sa =
						cd.createSequenceAnnotation("SequenceAnnotation" + String.valueOf(j),
								"SequenceAnnotation" + String.valueOf(j) + "_Range",
								sequence.length() + 1,
								sequence.length() + 1 + unit.getDNASequence().length());
				sa.setComponent(c.getIdentity());
				sequence += unit.getDNASequence();
				
				// SequenceConstraint
				if (j != 0) {
					cd.createSequenceConstraint(cd.getDisplayId() + "_Constraint" + String.valueOf(j),
							RestrictionType.PRECEDES,
							cd.getComponent(plasmid.getTranscriptionalUnitAtIdx(j-1).getName() + "_component").getIdentity(),
							cd.getComponent(unit.getName() + "_component").getIdentity());
				}
			}
			
			// Sequence
			Sequence s = document.createSequence(cd.getDisplayId() + "_sequence",sequence,Sequence.IUPAC_DNA);
			cd.addSequence(s);
		}
	}
	
	/**
	 * Add a ModuleDefinition representation of each circuit design.
	 * @param document
	 * @throws SBOLValidationException
	 */
	protected void addDesignModules(SBOLDocument document) throws SBOLValidationException {
		
		for (int j = 0; j < this.getPlasmids().getNumPlasmids(); j++) {

			ModuleDefinition designMD = document.createModuleDefinition("design_module_" + String.valueOf(j));
			Plasmid plasmid = this.getPlasmids().getPlasmidAtIdx(j);

			for (int i = 0; i < plasmid.getNumTranscriptionalUnits(); i++) {

				ComponentDefinition cd = document.createComponentDefinition("plasmid_" + String.valueOf(i*j),"1",ComponentDefinition.DNA);

				plasmid.setUri(cd.getIdentity());
				cd.addRole(SequenceOntology.ENGINEERED_REGION);
				cd.addType(SequenceOntology.CIRCULAR);

				String sequence = "";

				// backbone
				Part backbone = null;
				if (this.getAddBackbone()) {
					backbone = this.getParts().findCObjectByName("backbone");
					org.sbolstandard.core2.Component bbComponent = cd.createComponent("backbone_component", AccessType.PUBLIC, "backbone");
					SequenceAnnotation bbAnnotation = cd.createSequenceAnnotation("SequenceAnnotation_backbone",
																				  "SequenceAnnotation_backbone_Range",
																				  sequence.length() + 1,
																				  sequence.length() + 1 + backbone.getDNASequence().length());
					bbAnnotation.setComponent(bbComponent.getIdentity());
					sequence += backbone.getDNASequence();
				}

				TranscriptionalUnit unit = plasmid.getTranscriptionalUnitAtIdx(i);
				String name = unit.getName();
				// Component
				org.sbolstandard.core2.Component c = cd.createComponent(name + "_component", AccessType.PUBLIC, name);
			
				// SequenceAnnotation
				SequenceAnnotation sa = null;
				sa =
					cd.createSequenceAnnotation("SequenceAnnotation" + String.valueOf(0),
												"SequenceAnnotation" + String.valueOf(0) + "_Range",
												sequence.length() + 1,
												sequence.length() + 1 + unit.getDNASequence().length());

				if (this.getAddBackbone()) {
					cd.createSequenceConstraint(cd.getDisplayId() + "Constraint" + String.valueOf(0),
												RestrictionType.PRECEDES,
												cd.getComponent(backbone.getName() + "_component").getIdentity(),
												cd.getComponent(unit.getName() + "_component").getIdentity());
				}
			
				sa.setComponent(c.getIdentity());
				sequence += unit.getDNASequence();
			
				// Sequence
				Sequence s = document.createSequence(cd.getDisplayId() + "_sequence",sequence,Sequence.IUPAC_DNA);
				cd.addSequence(s);

				designMD.createFunctionalComponent("plasmid_" + String.valueOf(i*j), AccessType.PUBLIC, cd.getIdentity(), DirectionType.NONE);

			}

		}

	}
		
	// protected void addPlasmidModules(SBOLDocument document) throws SBOLValidationException {
	// 	for (int i = 0; i < this.getPlasmids().getNumPlasmids(); i++) {
	// 		Plasmid plasmid = this.getPlasmids().getPlasmidAtIdx(i);
	// 		ModuleDefinition plasmidMD = document.createModuleDefinition("design_module_" + String.valueOf(i));
			
	// 		String name = "plasmid_" + String.valueOf(i);
	// 		// plasmid FC
	// 		FunctionalComponent fc = plasmidMD.createFunctionalComponent(name, AccessType.PUBLIC, name, DirectionType.NONE);
			
	// 		// vpr Module
	// 		Module topMod = plasmidMD.createModule("circuit_design", vpr.getIdentity());
			
	// 		for (int j = 0; j < this.getPlasmids().getPlasmidAtIdx(0).getNumTranscriptionalUnits(); j++) {
	// 			TranscriptionalUnit unit = plasmid.getTranscriptionalUnitAtIdx(j);

	// 			ComponentDefinition txnCD = document.getComponentDefinition(unit.getName(), "");
	// 			FunctionalComponent vprTxnFC = vpr.getFunctionalComponent(unit.getName());

	// 			//Module vprTxnMod = vpr.getModule(this.getDesignName() + "_" + unit.getName() + "_module_sub");
	// 			//FunctionalComponent remote = vprTxnMod.getDefinition().getFunctionalComponent(unit.getName());
	// 			//vprTxnMod.createMapsTo(unit.getName(), RefinementType.VERIFYIDENTICAL, vprTxnFC.getIdentity(), remote.getIdentity());

	// 			FunctionalComponent local = plasmidMD.createFunctionalComponent(unit.getName() + "_pm", AccessType.PUBLIC, unit.getName(), DirectionType.NONE);

	// 			// Plasmid FC
	// 			Component plasmidRemote = document.getComponentDefinition(first.getUri()).getComponent(unit.getName() + "_component");
	// 			fc.createMapsTo(unit.getName(), RefinementType.VERIFYIDENTICAL, local.getIdentity(), plasmidRemote.getIdentity());

	// 			// Top MD
	// 			topMod.createMapsTo(unit.getName(), RefinementType.VERIFYIDENTICAL, local.getIdentity(), vprTxnFC.getIdentity());
	// 		}
	// 	}
	// }
	
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
		
		// plasmid component definitions
		if (this.getAddPlasmidModules()) {
			// logInfo("adding plasmids");
			// try {
			// 	if (this.getAddBackbone()) {
			// 		addBackboneDefinition(this.getSbolDocument());
			// 	}
			// 	addPlasmidModules(this.getSbolDocument());
			// } catch (SBOLValidationException | SynBioHubException e) {
			// 	e.printStackTrace();		
			// }
			// plasmid module
			if (!this.getAddInteractions()) {
				logInfo("adding modules");
				try {
					if (this.getAddBackbone()) {
						this.addBackboneDefinition(this.getSbolDocument());
					}
					this.addDesignModules(this.getSbolDocument());
				} catch (SBOLValidationException | SynBioHubException e) {
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
	 * Getter for <i>addBackbone</i>
	 * @return value of <i>addBackbone</i>
	 */
	protected Boolean getAddBackbone() {
		return this.addBackbone;
	}

	/**
	 * Setter for <i>addBackbone</i>
	 * @param addBackbone the value to set <i>addBackbone</i>
	 */
	protected void setAddBackbone(final Boolean addBackbone) {
		this.addBackbone = addBackbone;
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
	 * Getter for <i>plasmids</i>
	 * @return value of <i>plasmids</i>
	 */
	protected Plasmids getPlasmids() {
		return plasmids;
	}

	/**
	 * Setter for <i>plasmids</i>
	 * @param plasmids the value to set <i>plasmids</i>
	 */
	protected void setPlasmids(final Plasmids plasmids) {
		this.plasmids = plasmids;
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
	private Boolean addBackbone;
	private SynBioHubFrontend sbhFrontend;
	private SBOLDocument sbolDocument;
	private String sbolFilename;
	private CObjectCollection<Gate> gates;
	private CObjectCollection<Part> parts;
	private Plasmids plasmids;
	private static final Logger logger = LogManager.getLogger(SBOL.class.getSimpleName());

	private static final String S_BACKBONE_ROLE = "http://identifiers.org/so/SO:0000755";
}

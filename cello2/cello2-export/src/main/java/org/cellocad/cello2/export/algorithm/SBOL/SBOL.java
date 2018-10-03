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

import java.io.Console;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cellocad.cello2.common.CObjectCollection;
import org.cellocad.cello2.common.Utils;
import org.cellocad.cello2.export.algorithm.EXAlgorithm;
import org.cellocad.cello2.export.algorithm.SBOL.data.SBOLDataUtils;
import org.cellocad.cello2.export.algorithm.SBOL.data.SBOLNetlistData;
import org.cellocad.cello2.export.algorithm.SBOL.data.SBOLNetlistEdgeData;
import org.cellocad.cello2.export.algorithm.SBOL.data.SBOLNetlistNodeData;
import org.cellocad.cello2.export.algorithm.SBOL.data.plasmid.Plasmid;
import org.cellocad.cello2.export.algorithm.SBOL.data.plasmid.Plasmids;
import org.cellocad.cello2.export.algorithm.SBOL.data.plasmid.TranscriptionalUnit;
import org.cellocad.cello2.export.algorithm.SBOL.data.ucf.Gate;
import org.cellocad.cello2.export.algorithm.SBOL.data.ucf.Part;
import org.cellocad.cello2.logicSynthesis.runtime.environment.LSArgString;
import org.cellocad.cello2.results.netlist.Netlist;
import org.cellocad.cello2.results.netlist.NetlistEdge;
import org.cellocad.cello2.results.netlist.NetlistNode;
import org.sbolstandard.core2.AccessType;
import org.sbolstandard.core2.Component;
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
import org.synbiohub.frontend.IdentifiedMetadata;
import org.synbiohub.frontend.SearchCriteria;
import org.synbiohub.frontend.SearchQuery;
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
		Boolean present = true;

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
		// plasmids
		this.setPlasmids(new Plasmids(this.getNetlist(),true,false,this.getParts()));
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
			rtn = document.createComponentDefinition(part.getName(),ComponentDefinition.DNA);
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
			if (unit == null) {
				continue;
			}
			for (int j = 0; j < unit.getNumParts(); j++) {
				Part part = unit.getPartAtIdx(j);
				this.addPartDefinition(part, document);
			}
		}
	}

	protected ComponentDefinition addPromoterDefinition(SBOLDocument document, TranscriptionalUnit unit) throws SBOLValidationException {
		// parts
		String sequence = "";

		String operatorA = unit.getPartAtIdx(2).getName();
		String operatorB = unit.getPartAtIdx(6).getName();
		
		// promoter
		String name = "Pro_" + operatorA + "_" + operatorB;
		ComponentDefinition cd = document.createComponentDefinition(name,ComponentDefinition.DNA);
		cd.addRole(SequenceOntology.PROMOTER);
		for (int j = 0; j < unit.getNumParts() - 1; j++) {
			Part part = unit.getPartAtIdx(j);
			
			// Component
			Component c = cd.createComponent(part.getName() + "_component", AccessType.PUBLIC, URI.create(part.getUri()));
				
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
				cd.createSequenceConstraint(cd.getDisplayId() + "_Constraint" + String.valueOf(j),
											RestrictionType.PRECEDES,
											cd.getComponent(unit.getPartAtIdx(j-1).getName() + "_component").getIdentity(),
											cd.getComponent(part.getName() + "_component").getIdentity());
			}
		}

		// Sequence
		Sequence s = document.createSequence(cd.getDisplayId() + "_sequence",sequence,Sequence.IUPAC_DNA);
		cd.addSequence(s);

		return cd;
	}
	
	protected ComponentDefinition addCdsDefinition(SBOLDocument document, TranscriptionalUnit unit) throws SBOLValidationException {
		// parts
		String sequence = "";
		
		String operatorA = unit.getPartAtIdx(2).getName();
		String operatorB = unit.getPartAtIdx(6).getName();
		String name = operatorA + "_" + operatorB + "_" + unit.getName();
		ComponentDefinition cd = document.createComponentDefinition(operatorA + "_" + operatorB + "_" + unit.getName(),ComponentDefinition.DNA);
		cd.addRole(SequenceOntology.ENGINEERED_REGION);

		Part part = unit.getPartAtIdx(unit.getNumParts()-1);
				
			// Component
		Component c = null;
		c = cd.createComponent(part.getName() + "_component", AccessType.PUBLIC, URI.create(part.getUri()));
				
		// SequenceAnnotation
		SequenceAnnotation sa = cd.createSequenceAnnotation("SequenceAnnotation" + String.valueOf(0),
															"SequenceAnnotation" + String.valueOf(0) + "_Range",
															sequence.length() + 1,
															sequence.length() + 1 + part.getDNASequence().length());
		sa.setComponent(c.getIdentity());
		sequence += part.getDNASequence();
			
		Sequence s = document.createSequence(cd.getDisplayId() + "_sequence",sequence,Sequence.IUPAC_DNA);
		cd.addSequence(s);
			
		return cd;
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
			if (gateType.equals("")) {
				continue;
			}
			TranscriptionalUnit unit = plasmid.getTranscriptionalUnit(node);
			
			// ComponentDefinition
			ComponentDefinition cd = document.createComponentDefinition(name + "_" + gateType,ComponentDefinition.DNA);
			cd.addRole(SequenceOntology.ENGINEERED_REGION);

			// promoter
			ComponentDefinition promoter = this.addPromoterDefinition(document,unit);

			// coding sequence
			ComponentDefinition cds = this.addCdsDefinition(document,unit);

			Component c = null;
			SequenceAnnotation sa = null;
			String sequence = "";
			String temp = null;
			
			/*
			 * transcriptional unit
			 */
			// promoter
			temp = promoter.getSequences().iterator().next().getElements();
			Component pc = cd.createComponent(promoter.getDisplayId() + "_component", AccessType.PUBLIC, promoter.getIdentity());
			sa = cd.createSequenceAnnotation("SequenceAnnotation" + String.valueOf(0),
											 "SequenceAnnotation" + String.valueOf(0) + "_Range",
											 sequence.length() + 1,
											 sequence.length() + 1 + temp.length());
			sa.setComponent(pc.getIdentity());
			sequence += temp;

			// cds
			temp = cds.getSequences().iterator().next().getElements();
			Component cc = cd.createComponent(cds.getDisplayId() + "_component", AccessType.PUBLIC, cds.getIdentity());
			sa = cd.createSequenceAnnotation("SequenceAnnotation" + String.valueOf(1),
											 "SequenceAnnotation" + String.valueOf(1) + "_Range",
											 sequence.length() + 1,
											 sequence.length() + 1 + temp.length());
			sa.setComponent(cc.getIdentity());
			sequence += temp;

			cd.createSequenceConstraint(cd.getDisplayId() + "_Constraint" + String.valueOf(0),
										RestrictionType.PRECEDES,
										pc.getIdentity(),
										cc.getIdentity());
			
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
	 * Perform VPR model generation.
	 * @param selectedRepo - The specified synbiohub repository the user wants VPR model generator to connect to.
	 * @param generatedModel - The file to generate the model from.
	 * @param name - The top level design name.
	 * @return The generated model.
	 * @throws SBOLValidationException
	 * @throws IOException - Unable to read or write the given SBOLDocument
	 * @throws SBOLConversionException - Unable to perform conversion for the given SBOLDocument.
	 * @throws VPRException - Unable to perform VPR Model Generation on the given SBOLDocument.
	 * @throws VPRTripleStoreException - Unable to perform VPR Model Generation on the given SBOLDocument.
	 */
	protected SBOLDocument generateModel(String selectedRepo, SBOLDocument generatedModel, String name) throws SBOLValidationException, IOException, SBOLConversionException, VPRException, VPRTripleStoreException, URISyntaxException
	{
		Set<URI> collections = new HashSet<URI>();
		collections.add(new URI(this.getCollectionUri()));
		QueryParameters params = new QueryParameters();
		params.setCollectionURIs(new ArrayList<>(collections));
		URI endpoint = new URL(new URL(selectedRepo),"/sparql").toURI();
		SBOLInteractionAdder_GeneCentric interactionAdder = new SBOLInteractionAdder_GeneCentric(endpoint,name,params);
		interactionAdder.addInteractions(generatedModel);
		return generatedModel;
	}
	
	/**
	 * Add the plasmid backbone ComponentDefinition to the <i>document</i>
	 * @param document the SBOLDocument
	 * @throws SynBioHubException unable to fetch backbone ComponentDefinition
	 * @throws SBOLValidationException unable to add backbone ComponentDefinition
	 */
	protected void addBackboneDefinition(SBOLDocument document) throws SynBioHubException, SBOLValidationException {
		ComponentDefinition cd = this.addPartDefinition(this.getParts().findCObjectByName("backbone"), document);
		cd.addRole(URI.create("http://identifiers.org/so/SO:0000755")); // plasmid_vector
	}

	/**
	 * Generate plasmid component definitions.
	 * @param document the <i>SBOLDocument</i>
	 * @throws SBOLValidationException unable to add plasmid definition
	 */
	protected void addPlasmidDefinitions(SBOLDocument document) throws SBOLValidationException {
		for (int i = 0; i < this.getPlasmids().getNumPlasmids(); i++) {
			Plasmid plasmid = this.getPlasmids().getPlasmidAtIdx(i);
			ComponentDefinition cd = document.createComponentDefinition("plasmid_" + String.valueOf(i), ComponentDefinition.DNA);
			plasmid.setUri(cd.getIdentity());
			cd.addRole(SequenceOntology.ENGINEERED_REGION);
			cd.addType(SequenceOntology.CIRCULAR);
			
			String sequence = "";
			
			// backbone
			Part backbone = this.getParts().findCObjectByName("backbone");
			Component bbComponent = cd.createComponent("backbone_component", AccessType.PUBLIC, "backbone");
			SequenceAnnotation bbAnnotation = cd.createSequenceAnnotation("SequenceAnnotation_backbone",
					"SequenceAnnotation_backbone_Range",
					sequence.length() + 1,
					sequence.length() + 1 + backbone.getDNASequence().length());
			bbAnnotation.setComponent(bbComponent.getIdentity());
			sequence += backbone.getDNASequence();
			
			// transcriptional units
			for (int j = 0; j < plasmid.getNumTranscriptionalUnits(); j++) {
				TranscriptionalUnit unit = plasmid.getTranscriptionalUnitAtIdx(j);
				String name = unit.getName();
				// Component
				Component c = cd.createComponent(name + "_component", AccessType.PUBLIC, name);
				
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
	 * Add ModuleDefinitions for the designs.
	 * @param document
	 * @throws SBOLValidationException
	 */
	protected void addDesignModules(SBOLDocument document) throws SBOLValidationException {
		ModuleDefinition designMD = document.createModuleDefinition("design_module_" + String.valueOf(0));
		
		Plasmid plasmid = this.getPlasmids().getPlasmidAtIdx(0);

		for (int i = 0; i < plasmid.getNumTranscriptionalUnits(); i++) {
			ComponentDefinition cd = document.createComponentDefinition("plasmid_" + String.valueOf(i), ComponentDefinition.DNA);

			plasmid.setUri(cd.getIdentity());
			cd.addRole(SequenceOntology.ENGINEERED_REGION);
			cd.addType(SequenceOntology.CIRCULAR);

			String sequence = "";

			// backbone
			Part backbone = this.getParts().findCObjectByName("backbone");
			Component bbComponent = cd.createComponent("backbone_component", AccessType.PUBLIC, "backbone");
			SequenceAnnotation bbAnnotation = cd.createSequenceAnnotation("SequenceAnnotation_backbone",
					"SequenceAnnotation_backbone_Range",
					sequence.length() + 1,
					sequence.length() + 1 + backbone.getDNASequence().length());
			bbAnnotation.setComponent(bbComponent.getIdentity());
			sequence += backbone.getDNASequence();

			TranscriptionalUnit unit = plasmid.getTranscriptionalUnitAtIdx(i);
			String name = unit.getName();
			// Component
			Component c = cd.createComponent(name + "_component", AccessType.PUBLIC, name);
			
			// SequenceAnnotation
			SequenceAnnotation sa = null;
			sa =
				cd.createSequenceAnnotation("SequenceAnnotation" + String.valueOf(0),
											"SequenceAnnotation" + String.valueOf(0) + "_Range",
											sequence.length() + 1,
											sequence.length() + 1 + unit.getDNASequence().length());

			cd.createSequenceConstraint(cd.getDisplayId() + "Constraint" + String.valueOf(0),
							RestrictionType.PRECEDES,
							cd.getComponent(backbone.getName() + "_component").getIdentity(),
							cd.getComponent(unit.getName() + "_component").getIdentity());
			
			sa.setComponent(c.getIdentity());
			sequence += unit.getDNASequence();
			
			// Sequence
			Sequence s = document.createSequence(cd.getDisplayId() + "_sequence",sequence,Sequence.IUPAC_DNA);
			cd.addSequence(s);

			designMD.createFunctionalComponent("plasmid_" + String.valueOf(i), AccessType.PUBLIC, cd.getIdentity(), DirectionType.NONE);
		}
	}
	
	// /**
	//  * Add ModuleDefinitions for the designs.
	//  * @param document
	//  * @throws SBOLValidationException
	//  */
	// protected void addDesignModules(SBOLDocument document) throws SBOLValidationException {
	// 	Plasmid first = this.getPlasmids().getPlasmidAtIdx(0);
	// 	ModuleDefinition vpr = document.getModuleDefinition(this.getDesignName(), "");
	// 	for (int j = 0; j < this.getPlasmids().getPlasmidAtIdx(0).getNumTranscriptionalUnits(); j++) {
	// 		TranscriptionalUnit unit = this.getPlasmids().getPlasmidAtIdx(0).getTranscriptionalUnitAtIdx(j);
	// 		ComponentDefinition txnCD = document.getComponentDefinition(unit.getName(), "");
	// 		vpr.createFunctionalComponent(unit.getName(), AccessType.PUBLIC, txnCD.getIdentity(), DirectionType.NONE);
	// 	}
		
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
				SBOLDocument sbolDocument = generateModel(this.getRepositoryUrl().toString(),this.getSbolDocument(),this.getDesignName());
				//addPlasmidFunctionalComponent(sbolDocument);
				this.setSbolDocument(sbolDocument);
			} catch (IOException | SBOLValidationException | SBOLConversionException | VPRException | VPRTripleStoreException | URISyntaxException e) {
				e.printStackTrace();
			}
		}
		
		// plasmid component definitions
		logInfo("adding plasmids");
		try {
			addBackboneDefinition(this.getSbolDocument());
			addDesignModules(this.getSbolDocument());
			// addPlasmidDefinitions(this.getSbolDocument());
		} catch (SBOLValidationException | SynBioHubException e) {
			e.printStackTrace();		
		}
		
		// // design module
		// if (this.getAddInteractions()) {
		// 	logInfo("adding modules");
		// 	try {
		// 		addDesignModules(this.getSbolDocument());
		// 	} catch (SBOLValidationException e) {
		// 		e.printStackTrace();
		// 	}
		// }

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

		Console console = System.console();		
		System.out.println("");
		System.out.println("");
		System.out.println("");
		System.out.println("=== SynBioHub Upload ===");
		System.out.println("");
		System.out.println("Do you want to upload the SBOL representation to SynBioHub [Y/n]?");
		String response = "";
		Scanner uin = new Scanner(System.in);
		response = uin.nextLine();
		if (response.equalsIgnoreCase("n")) {
			return;
		}
		String url = "";
		if (this.getRepositoryUrl().length() != 0) {
			System.out.println("");
			System.out.println("The repository URL supplied in the stage configuration is:");
			System.out.println(this.getRepositoryUrl());
			System.out.println("");
			System.out.println("Is this where you would like to submit the SBOL document [Y/n]?");
		}
		response = uin.nextLine();
		if (this.getRepositoryUrl().length() == 0 || response.equalsIgnoreCase("n")) {
			while (url.equals("")) {
				System.out.println("Please enter a URL.");
				response = uin.nextLine();
				try {
					new URL(response);
				} catch (MalformedURLException e) {
					System.out.println("");
					System.out.println("Invalid URL.");
					continue;
				}
				url = response;
			}
		} else {
			url = this.getRepositoryUrl();
		}
		SynBioHubFrontend sbh = new SynBioHubFrontend(url);
		System.out.println("Login.");
		String username = "";
		String password = "";

		while (username.length() == 0) {
			username = new String(console.readLine("email: "));
		}
		while (password.length() == 0) {
			password = new String(console.readPassword("password: "));
		}
		System.out.println("");
		try {
			sbh.login(username,password);
		} catch (SynBioHubException e) {
			System.out.println("Login error.");
			e.printStackTrace();
			return;
		}

		System.out.println("Upload to Existing collection or add to New collection [E/n]?");
		response = uin.nextLine();
		System.out.println("");
		if (response.equalsIgnoreCase("n")) {
			// id
			String id = "";
			while (id.length() == 0) {
				id = new String(console.readLine("id: "));
			}
			// version
			String version = "";
			while (version.length() == 0) {
				version = new String(console.readLine("version: "));
			}
			// name
			String name = "";
			while (name.length() == 0) {
				name = new String(console.readLine("name: "));
			}
			// description
			String description = "";
			while (description.length() == 0) {
				description = new String(console.readLine("description: "));
			}
			// citations
			String citations = "";
			citations = new String(console.readLine("citations (optional): "));
			// overwrite
			boolean overwrite = true;
			response = new String(console.readLine("overwrite [T/f]: "));
			if (response.equalsIgnoreCase("t")) {
				overwrite = true;
			}
			else if (response.equalsIgnoreCase("f")) {
				overwrite = false;
			}
			else {
				overwrite = true;
			}
			try {
				//sbh.createCollection(id,version,name,description,"",true,this.getSbolDocument());
				sbh.createCollection(id,version,name,description,citations,overwrite,this.getSbolDocument());
			} catch (SynBioHubException e) {
				System.out.println("Error with collection creation. Terminating.");
				e.printStackTrace();
				return;
			}
			System.out.println("");
			System.out.println("Submitted. Bye.");
		} else {
			SearchQuery query = new SearchQuery();
			SearchCriteria crit = new SearchCriteria();
			crit.setKey("objectType");
			crit.setValue("Collection");
			query.addCriteria(crit);
			query.setLimit(10000);
			query.setOffset(0);
			List<IdentifiedMetadata> collections = null;
			try {
				collections = sbh.search(query);
			} catch (SynBioHubException e) {
				System.out.println("Error in collection search. Terminating.");
				return;
			}
			Map<Integer,IdentifiedMetadata> map = new HashMap<>();
			System.out.println("Available collections:");
			int i = 1;
			for (IdentifiedMetadata im : collections) {
				// don't add collections that have "/public" in the URI.
				if (!im.getUri().contains("/public/")) {
					map.put(i,im);
					String str = "";
					str += String.format("%-6s","[" + String.valueOf(i) + "]");
					str += im.getName() + "/" + im.getVersion();
					System.out.println(str);
					i++;
				}
			}
			String choice = "";
			Integer num = -1;
			while (num < 1 || num > map.keySet().size()) {
				choice = new String(console.readLine("choice: "));
				try {
					num = Integer.valueOf(choice);
				} catch (NumberFormatException e) {
					continue;
				}
			}
			// overwrite
			boolean overwrite = true;
			response = new String(console.readLine("overwrite [T/f]: "));
			if (response.equalsIgnoreCase("t")) {
				overwrite = true;
			}
			else if (response.equalsIgnoreCase("f")) {
				overwrite = false;
			}
			else {
				overwrite = true;
			}
			try {
				sbh.addToCollection(URI.create(map.get(num).getUri()),overwrite,this.getSbolDocument());
			} catch (SynBioHubException e) {
				System.out.println("Error with submission. Terminating.");
				e.printStackTrace();
				return;
			}
			System.out.println("");
			System.out.println("Submitted. Bye.");
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
	private SynBioHubFrontend sbhFrontend;
	private SBOLDocument sbolDocument;
	private String sbolFilename;
	private CObjectCollection<Gate> gates;
	private CObjectCollection<Part> parts;
	private Plasmids plasmids;
	private static final Logger logger = LogManager.getLogger(SBOL.class.getSimpleName());
}

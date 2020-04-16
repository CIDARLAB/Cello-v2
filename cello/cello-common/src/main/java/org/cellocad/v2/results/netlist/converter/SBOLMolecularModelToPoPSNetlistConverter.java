/**
 * Copyright (C) 2020 Boston University (BU)
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
package org.cellocad.v2.results.netlist.converter;

import java.net.URI;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.cellocad.v2.common.CelloException;
import org.cellocad.v2.results.netlist.Netlist;
import org.cellocad.v2.results.netlist.NetlistEdge;
import org.cellocad.v2.results.netlist.NetlistNode;
import org.sbolstandard.core2.ComponentDefinition;
import org.sbolstandard.core2.FunctionalComponent;
import org.sbolstandard.core2.Interaction;
import org.sbolstandard.core2.ModuleDefinition;
import org.sbolstandard.core2.Participation;
import org.sbolstandard.core2.SBOLDocument;
import org.sbolstandard.core2.SystemsBiologyOntology;

/**
 * Converts an SBOL representation of a regulatory network to a Cello-style
 * polymerase flux netlist. The SBOL representation must be of the type produced
 * by the <a href="https://github.com/shaunharker/DSGRN">DSGRN</a> tool.
 *
 * @author Timothy Jones
 *
 * @date 2020-04-09
 *
 */
public class SBOLMolecularModelToPoPSNetlistConverter {

	/**
	 * Add nodes derived from root module definition to the netlist.
	 *
	 * @param root    The root module definition.
	 * @param netlist The netlist.
	 * @return A map from a component definition to an associated netlist node.
	 */
	private static Map<ComponentDefinition, NetlistNode> addNodes(ModuleDefinition root, Netlist netlist) {
		Map<ComponentDefinition, NetlistNode> rtn = new HashMap<>();
		Collection<FunctionalComponent> functionalComponents = root.getFunctionalComponents();
		for (FunctionalComponent functionalComponent : functionalComponents) {
			ComponentDefinition definition = functionalComponent.getDefinition();
			NetlistNode node = new NetlistNode();
			node.setName(definition.getDisplayId());
			netlist.addVertex(node);
			rtn.put(definition, node);
		}
		return rtn;
	}

	/**
	 * Add edges derived from a root module definition to the netlist.
	 *
	 * @param root    The root module definition.
	 * @param netlist The netlist.
	 * @param map     A map from a component definition to an associated netlist
	 *                node.
	 */
	private static void addEdges(ModuleDefinition root, Netlist netlist, Map<ComponentDefinition, NetlistNode> map) {
		Collection<Interaction> interactions = root.getInteractions();
		for (Interaction interaction : interactions) {
			Collection<Participation> participations = interaction.getParticipations();
			Participation object = null;
			for (Participation participation : participations) {
				Collection<URI> roles = participation.getRoles();
				if (roles.contains(SystemsBiologyOntology.STIMULATED)
				        || roles.contains(SystemsBiologyOntology.INHIBITED)) {
					object = participation;
					break;
				}
			}
			if (object == null) {
				continue;
			}
			for (Participation participation : participations) {
				Collection<URI> roles = participation.getRoles();
				if ((object.getRoles().contains(SystemsBiologyOntology.STIMULATED)
				        && roles.contains(SystemsBiologyOntology.STIMULATOR))
				        || (object.getRoles().contains(SystemsBiologyOntology.INHIBITED)
				                && roles.contains(SystemsBiologyOntology.INHIBITOR))) {
					Participation subject = participation;
					NetlistEdge edge = new NetlistEdge();
					edge.setName(subject.getDisplayId());
					NetlistNode src = map.get(subject.getParticipantDefinition());
					edge.setSrc(src);
					src.addOutEdge(edge);
					NetlistNode dst = map.get(object.getParticipantDefinition());
					edge.setDst(dst);
					dst.addInEdge(edge);
					netlist.addEdge(edge);
				}
			}
		}
	}

	/**
	 * Convert an SBOL representation of a regulatory network to a Cello-style
	 * polymerase flux netlist. The SBOL representation must be of the type produced
	 * by the <a href="https://github.com/shaunharker/DSGRN">DSGRN</a> tool.
	 *
	 * @param document The document to convert.
	 * @return The netlist.
	 * @throws CelloException Unable to convert document.
	 */
	public Netlist convert(final SBOLDocument document) throws CelloException {
		Netlist rtn = new Netlist();
		Iterator<ModuleDefinition> it = document.getRootModuleDefinitions().iterator();
		rtn = new Netlist();
		if (!it.hasNext()) {
			return rtn;
		}
		ModuleDefinition root = it.next();
		rtn.setName(root.getDisplayId());
		Map<ComponentDefinition, NetlistNode> map = addNodes(root, rtn);
		addEdges(root, rtn, map);
		return rtn;
	}

}

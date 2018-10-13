package jp.kobe_u.sugar;

import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.zip.GZIPInputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * XML2CSP converts files in XCSP 2 format into CSP format. 
 * @author Naoyuki Tamura (tamura@kobe-u.ac.jp)
 *
 */
public class XML2CSP {
    private static int debug = 0;
    private String xmlFileName;
    private String cspFileName;
    private Document document;
    private BufferedWriter cspFile;

    private int nbDomains = 0;
    private int nbVariables = 0;
    private int nbPredicates = 0;
    private int nbRelations = 0;
    private int nbConstraints = 0;
    
//  private static final String DESCRIPTION = "description";
//  private static final String PRESENTATION = "presentation";
//  private static final String SOLUTION = "solution";
//  private static final String NB_SOLUTIONS = "nbSolutions";
//  private static final String FORMAT = "format";
//  private static final String XCSP_2_0 = "XCSP 2.0";
    private static final String NAME = "name";
    private static final String DOMAINS = "domains";
    private static final String NB_DOMAINS = "nbDomains";
    private static final String DOMAIN = "domain";
//  private static final String NB_VALUES = "nbValues";
//  private static final String VALUES = "values";
    private static final String VARIABLES = "variables";
    private static final String NB_VARIABLES = "nbVariables";
    private static final String VARIABLE = "variable";
    private static final String RELATIONS = "relations";
    private static final String NB_RELATIONS = "nbRelations";
    private static final String RELATION = "relation";
//  private static final String SUPPORTS = "supports";
//  private static final String NB_SUPPORTS = "nbSupports";
//  private static final String CONFLICTS = "conflicts";
//  private static final String NB_CONFLICTS = "nbConflicts";
    private static final String ARITY = "arity";
//  private static final String NB_TUPLES = "nbTuples";
    private static final String SEMANTICS = "semantics";
    private static final String PREDICATES = "predicates";
    private static final String NB_PREDICATES = "nbPredicates";
    private static final String PREDICATE = "predicate";
//  private static final String EXPRESSION = "expression";
    private static final String FUNCTIONAL = "functional";
//  private static final String POSTFIX_EXPRESSION = "postfixExpression";
    private static final String PARAMETERS = "parameters";
    private static final String CONSTRAINTS = "constraints";
    private static final String NB_CONSTRAINTS = "nbConstraints";
    private static final String CONSTRAINT = "constraint";
    private static final String SCOPE = "scope";
    private static final String REFERENCE = "reference";

    private static HashSet<String> supportedGlobalConstraints;
    
    static {
        supportedGlobalConstraints = new HashSet<String>();
        supportedGlobalConstraints.add(SugarConstants.ALLDIFFERENT);
        supportedGlobalConstraints.add(SugarConstants.WEIGHTEDSUM);
        supportedGlobalConstraints.add(SugarConstants.CUMULATIVE);
        supportedGlobalConstraints.add(SugarConstants.ELEMENT);
        supportedGlobalConstraints.add(SugarConstants.DISJUNCTIVE);
        supportedGlobalConstraints.add(SugarConstants.LEX_LESS);
        supportedGlobalConstraints.add(SugarConstants.LEX_LESSEQ);
        supportedGlobalConstraints.add(SugarConstants.NVALUE);
        supportedGlobalConstraints.add(SugarConstants.COUNT);
        supportedGlobalConstraints.add(SugarConstants.GLOBAL_CARDINALITY);
        supportedGlobalConstraints.add(SugarConstants.GLOBAL_CARDINALITY_WITH_COSTS);
    }
    
    /**
     * Constructs a XCSP to CSP converter.
     * @param xmlFileName
     * @param cspFileName
     */
    public XML2CSP(String xmlFileName, String cspFileName) {
        this.xmlFileName = xmlFileName;
        this.cspFileName = cspFileName;
    }
    
    private Element getFirstElement(Element elem, String tag) {
        NodeList nodeList = elem.getElementsByTagName(tag);
        if (nodeList.getLength() == 0)
            return null;
        return (Element)nodeList.item(0);
    }
    
    private String getTextContext(Element elem) {
        return elem.getTextContent().replaceAll("\\s+", " ").trim();
    }

    private void output(String s) throws IOException {
        cspFile.write(s);
        cspFile.write('\n');
    }
    
    private void convertDomains(Element domainsElement) throws IOException {
        nbDomains = Integer.parseInt(domainsElement .getAttribute(NB_DOMAINS));
        output("; nbDomains=" + nbDomains);
        NodeList nodeList = domainsElement.getElementsByTagName(DOMAIN);
        for (int i = 0; i < nodeList.getLength(); i++) {
            Element domainElement = (Element) nodeList.item(i);
            String name = domainElement.getAttribute(NAME);
//          int nbValues = Integer.parseInt(domainElement.getAttribute(NB_VALUES));
            String valuesText = getTextContext(domainElement);
            if (valuesText.matches("(-?\\d+)")) {
                output("(domain " + name + " " + valuesText + " " + valuesText + ")");
            } else if (valuesText.matches("(-?\\d+)\\s*\\.\\.\\s*(-?\\d+)")) {
                    String[] s = valuesText.split("\\s*\\.\\.\\s*");
                    output("(domain " + name + " " + s[0] + " " + s[1] + ")");
            } else {
                valuesText = valuesText.replaceAll("(-?\\d+)\\s*\\.\\.\\s*(-?\\d+)", "($1 $2)");
                output("(domain " + name + " (" + valuesText +"))");
            }
        }
    }

    private void convertVariables(Element variablesElement) throws IOException {
        nbVariables = Integer.parseInt(variablesElement.getAttribute(NB_VARIABLES));
        output("; nbVariables=" + nbVariables);
        NodeList nodeList = variablesElement.getElementsByTagName(VARIABLE);
        for (int i = 0; i < nodeList.getLength(); i++) {
            Element variableElement = (Element) nodeList.item(i);
            String name = variableElement.getAttribute(NAME);
            String domainName = variableElement.getAttribute(DOMAIN);
            output("(int " + name + " " + domainName + ")");
        }
    }

    private void convertPredicates(Element predicatesElement) throws IOException {
        if (predicatesElement == null)
            return;
        nbPredicates = Integer.parseInt(predicatesElement.getAttribute(NB_PREDICATES));
        output("; nbPredicates=" + nbPredicates);
        NodeList nodeList = predicatesElement.getElementsByTagName(PREDICATE);
        for (int i = 0; i < nodeList.getLength(); i++) {
            Element predicateElement = (Element) nodeList.item(i);
            String name = predicateElement.getAttribute(NAME);
            Element parameters = getFirstElement(predicateElement, PARAMETERS);
//          Element expression = getFirstElement(predicateElement, EXPRESSION); 
            Element functional = getFirstElement(predicateElement, FUNCTIONAL);  
            String formalParameters = getTextContext(parameters);
            formalParameters = formalParameters.replaceAll("int\\s+", "");
            String body = getTextContext(functional);
            body = body.replaceAll("(\\w+)\\(", "($1 ");
            body = body.replaceAll("\\s*,\\s*", " ");
            output("(predicate (" + name + " " + formalParameters + ") " + body + ")");
        }
    }

    private void convertRelations(Element relationsElement) throws IOException {
        if (relationsElement == null)
            return;
        nbRelations = Integer.parseInt(relationsElement .getAttribute(NB_RELATIONS));
        output("; nbRelations=" + nbRelations);
        NodeList nodeList = relationsElement.getElementsByTagName(RELATION);
        for (int i = 0; i < nodeList.getLength(); i++) {
            Element relationElement = (Element) nodeList.item(i);
            String name = relationElement.getAttribute(NAME);
            int arity = Integer.parseInt(relationElement.getAttribute(ARITY));
//          int nbTuples = Integer.parseInt(relationElement .getAttribute(NB_TUPLES));
            String semantics = relationElement.getAttribute(SEMANTICS);
            String tuples = getTextContext(relationElement);
            tuples = tuples.replaceAll("\\s*\\|\\s*", ") (");
            if (! tuples.matches("\\s*")) {
                tuples = "(" + tuples + ")";
            }
            output("(relation " + name + " " + arity
                    + " (" + semantics + " " + tuples + "))");
        }
    }

    private void convertConstraints(Element constraintsElement) throws IOException {
        nbConstraints = Integer.parseInt(constraintsElement .getAttribute(NB_CONSTRAINTS));
        output("; nbConstraints=" + nbConstraints);
        NodeList nodeList = constraintsElement.getElementsByTagName(CONSTRAINT);
        for (int i = 0; i < nodeList.getLength(); i++) {
            Element constraintElement = (Element) nodeList.item(i);
            String name = constraintElement.getAttribute(NAME);
//          int arity = Integer.parseInt(constraintElement.getAttribute(ARITY));
            String scope = constraintElement.getAttribute(SCOPE);
            String reference = constraintElement.getAttribute(REFERENCE);
            Element parameters = getFirstElement(constraintElement, PARAMETERS);
            String params = "";
            if (reference.startsWith("global:")) {
                reference = reference.replaceFirst("global:", "").toLowerCase();
                if (! supportedGlobalConstraints.contains(reference)) {
                    System.out.println("s UNSUPPORTED");
                    throw new IOException("global:" + reference + " is not supported");
                }
                if (parameters == null) {
                    params = scope;
                } else {
                    // params = parameters.getTextContent();
                    NodeList p = parameters.getChildNodes();
                    String delim = "";
                    for (int j = 0; j < p.getLength(); j++) { 
                        if (p.item(j).getNodeType() == Element.ELEMENT_NODE) {
                            params += delim + p.item(j).getNodeName();
                        } else {
                            params += delim + p.item(j).getTextContent();
                        }
                        delim = " ";
                    }   
                    params = params.replaceAll("[\\[\\{]", "(");
                    params = params.replaceAll("[\\]\\}]", ")");
                }
                output("(" + reference + " " + params + ") ; " + name);
            } else {
                if (parameters == null) {
                    params = scope;
                } else {
                    params = getTextContext(parameters);
                }
                output("(" + reference + " " + params + ") ; " + name);
            }
        }
    }

    public void convert() throws IOException {
//      System.out.println("Converting " + xmlFileName + " " + cspFileName);
        cspFile = new BufferedWriter(new FileWriter(cspFileName));
        output("; BEGIN " + xmlFileName);
        Element root = document.getDocumentElement();
        convertDomains(getFirstElement(root, DOMAINS));
        convertVariables(getFirstElement(root, VARIABLES));
        convertPredicates(getFirstElement(root, PREDICATES));
        convertRelations(getFirstElement(root, RELATIONS));
        convertConstraints(getFirstElement(root, CONSTRAINTS));
        output("; END " + xmlFileName);
        cspFile.close();
        System.out.println("c " +
                nbDomains + " domains, " +
                nbVariables + " variables, " +
                nbPredicates + " predicates, " +
                nbRelations + " relations, " +
                nbConstraints + " constraints");
    }

    public void load() throws IOException, ParserConfigurationException, SAXException {
        InputStream in = new FileInputStream(xmlFileName);
        if (xmlFileName.endsWith(".gz")) {
            in = new GZIPInputStream(in);
        }
        DocumentBuilderFactory documentBuilderFactory = 
            DocumentBuilderFactory.newInstance();
        documentBuilderFactory.setNamespaceAware(true);
        DocumentBuilder documentBuilder =
            documentBuilderFactory.newDocumentBuilder();
        documentBuilderFactory.setIgnoringElementContentWhitespace(true);
        document = documentBuilder.parse(in);
        in.close();
    }
    
    /**
     * Main program of XML2CSP.
     * @param args
     */
    public static void main(String[] args) {
        int i = 0;
        while (i < args.length) {
            if (args[i].equals("-v") || args[i].equals("-verbose")) {
                Logger.verboseLevel++;
            } else if (args[i].equals("-debug") && i + 1 < args.length) {
                debug = Integer.parseInt(args[i+1]);
                i++;
            } else if (args[i].equals("-option") && i + 1 < args.length) {
                i++;
            } else if (! args[i].startsWith("-")) {
                break;
            }
            i++;
        }
        int n = args.length - i;
        if (n != 2) {
            System.out.println("Usage : java XML2CSP [-v] xmlFileName cspFileName");
            System.exit(1);
        }
        String xmlFileName = args[i];
        String cspFileName = args[i + 1];
        try {
            XML2CSP xml2csp = new XML2CSP(xmlFileName, cspFileName);
            xml2csp.load();
            xml2csp.convert();
        } catch (IOException e) {
            System.out.println("c ERROR Exception " + e.getMessage());
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            System.out.println("c ERROR Exception " + e.getMessage());
            e.printStackTrace();
        } catch (SAXException e) {
            System.out.println("c ERROR Exception " + e.getMessage());
            e.printStackTrace();
        }
    }
}

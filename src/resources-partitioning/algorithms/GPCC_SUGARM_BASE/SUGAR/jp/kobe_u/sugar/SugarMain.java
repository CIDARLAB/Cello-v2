package jp.kobe_u.sugar;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.zip.GZIPInputStream;

import jp.kobe_u.sugar.converter.Converter;
import jp.kobe_u.sugar.converter.Simplifier;
import jp.kobe_u.sugar.csp.BooleanVariable;
import jp.kobe_u.sugar.csp.CSP;
import jp.kobe_u.sugar.csp.CSP.Objective;
import jp.kobe_u.sugar.csp.IntegerDomain;
import jp.kobe_u.sugar.csp.IntegerDomainDiet;
import jp.kobe_u.sugar.csp.IntegerDomainIntevals;
import jp.kobe_u.sugar.csp.IntegerVariable;
import jp.kobe_u.sugar.encoder.Encoder;
import jp.kobe_u.sugar.encoder.Encoding;
import jp.kobe_u.sugar.encoder.Problem;
import jp.kobe_u.sugar.expression.Expression;
import jp.kobe_u.sugar.expression.Parser;
import jp.kobe_u.sugar.expression.Sequence;
import jp.kobe_u.sugar.hook.ConverterHook;
import jp.kobe_u.sugar.hybrid.HybridConverter;
import jp.kobe_u.sugar.hybrid.HybridEncoder;
import jp.kobe_u.sugar.pb.PBEncoder;
import jp.kobe_u.sugar.pb.PBFileProblem;
import jp.kobe_u.sugar.pb.PBProblem;

/**
 * SugarMain main class.
 * @author Naoyuki Tamura (tamura@kobe-u.ac.jp)
 */
public class SugarMain {
    CSP csp = null;
    static boolean prolog = false;
    static boolean maxCSP = false;
    static boolean weightedCSP = false;
    static boolean competition = false;
    static boolean incremental = false;
    static boolean propagation = true;
    static boolean simplify_clauses = true;
    static boolean pb = false;
    static PBEncoder.Encoding pbEncoding = null;
    static boolean hybrid = false;
    public static int debug = 0;

    private List<Expression> toMaxCSP(List<Expression> expressions0) throws SugarException {
        List<Expression> expressions = new ArrayList<Expression>();
        List<Expression> sum = new ArrayList<Expression>();
        sum.add(Expression.ADD);
        int n = 0;
        for (Expression x: expressions0) {
            if (x.isSequence(Expression.DOMAIN_DEFINITION)
                || x.isSequence(Expression.INT_DEFINITION)
                || x.isSequence(Expression.BOOL_DEFINITION)
                || x.isSequence(Expression.PREDICATE_DEFINITION)
                || x.isSequence(Expression.RELATION_DEFINITION)) {
                expressions.add(x);
            } else if (x.isSequence(Expression.OBJECTIVE_DEFINITION)) {
                throw new SugarException("Illegal " + x);
            } else {
                // (int _Cn 0 1)
                // (or (ge _Cn 1) constraint)
                Expression c = Expression.create("_C" + n);
                expressions.add(Expression.create(
                        Expression.INT_DEFINITION,
                        c,
                        Expression.ZERO,
                        Expression.ONE));
                x = (c.ge(Expression.ONE)).or(x);
                expressions.add(x);
                sum.add(c);
                n++;
            }
        }
        // (int _COST 0 n)
        // (ge _COST (add _C1 ... _Cn))
        // (objective minimize _COST)
        Expression cost = Expression.create("_COST");
        expressions.add(Expression.create(
                Expression.INT_DEFINITION,
                cost,
                Expression.ZERO,
                Expression.create(n)));
        expressions.add(cost.ge(Expression.create(sum)));
        expressions.add(Expression.create(
                Expression.OBJECTIVE_DEFINITION,
                Expression.MINIMIZE,
                cost));
        Logger.info("MAX CSP: " + n + " constraints");
        return expressions;
    }
    
    private List<Expression> toWeightedCSP(List<Expression> expressions0) throws SugarException {
        List<Expression> expressions = new ArrayList<Expression>();
        List<Expression> sum = new ArrayList<Expression>();
        sum.add(Expression.ADD);
        int n = 0;
        int maxWeight = 0;
        for (Expression x: expressions0) {
            if (x.isSequence(Expression.OBJECTIVE_DEFINITION)) {
                throw new SugarException("Illegal " + x);
            } else if (x.isSequence(Expression.WEIGHTED)) {
                Sequence seq = (Sequence)x;
                if (! seq.matches("WI.")) {
                    throw new SugarException("Bad definition " + seq);
                }
                int weight = seq.get(1).integerValue();
                x = seq.get(2);
                // (int _Cn 0 1)
                // (or (ge _Cn 1) constraint)
                Expression c = Expression.create("_C" + n);
                expressions.add(Expression.create(
                        Expression.INT_DEFINITION,
                        c,
                        Expression.ZERO,
                        Expression.ONE));
                x = (c.ge(Expression.ONE)).or(x);
                expressions.add(x);
                sum.add(c.mul(weight));
                maxWeight += weight;
                n++;
            } else {
                expressions.add(x);
            }
        }
        // (int _COST 0 maxWeight)
        // (ge _COST (add (* _C1 W1) ... (* _Cn Wn)))
        // (objective minimize _COST)
        Expression cost = Expression.create("_COST");
        expressions.add(Expression.create(
                Expression.INT_DEFINITION,
                cost,
                Expression.ZERO,
                Expression.create(maxWeight)));
        expressions.add(cost.ge(Expression.create(sum)));
        expressions.add(Expression.create(
                Expression.OBJECTIVE_DEFINITION,
                Expression.MINIMIZE,
                cost));
        Logger.info("Weighted CSP: " + n + " constraints");
        return expressions;
    }
    
    private List<Expression> toGCNF(List<Expression> expressions0) throws SugarException {
        List<Expression> expressions = new ArrayList<Expression>();
        int n = 0; 
        int label = 0;
        for (Expression x: expressions0) {
            if (x.isSequence(Expression.DOMAIN_DEFINITION)
                || x.isSequence(Expression.INT_DEFINITION)
                || x.isSequence(Expression.BOOL_DEFINITION)
                || x.isSequence(Expression.PREDICATE_DEFINITION)
                || x.isSequence(Expression.RELATION_DEFINITION)) {
                expressions.add(x);
            } else if (x.isSequence(Expression.OBJECTIVE_DEFINITION)) {
                throw new SugarException("Illegal " + x);
            } else {
                // (or (label 1) constraint)
                label++;
                x = Expression.create(Expression.LABEL, Expression.create(label)).or(x);
                expressions.add(x);
                n++;
            }
        }
        int topWeight = label;
        expressions.add(Expression.create(
                Expression.GROUPS_DEFINITION,
                Expression.create(label),
                Expression.create(topWeight)));
        Logger.info("Group CSP: " + n + " constraints");
        return expressions;
    }
    

    public List<Expression> parse(String cspFileName) throws SugarException, IOException {
        // Parse
        Logger.fine("Parsing " + cspFileName);
        InputStream in;
        if (cspFileName.endsWith(".gz")) {
            in = new GZIPInputStream(new FileInputStream(cspFileName));
        } else {
            in = new FileInputStream(cspFileName);
        }
        BufferedReader reader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
        Parser parser = new Parser(reader, prolog);
        List<Expression> expressions = parser.parse();
        reader.close();
        reader = null;
        in.close();
        in = null;
        parser = null;
        Runtime.getRuntime().gc();
        Logger.info("parsed " + expressions.size() + " expressions");
        if (debug > 0) {
            for (Expression x : expressions)
                System.out.println("c " + x);
        }
        Logger.status();
        // GCNF or MaxCSP or WeightedCSP translation 
        if (Problem.GCNF || Problem.GWCNF) {
            expressions = toGCNF(expressions);
        } else if (maxCSP) {
            expressions = toMaxCSP(expressions);
        } else if (weightedCSP) {
            expressions = toWeightedCSP(expressions);
        }
        return expressions;
    }
    
    public void translate(String cspFileName) throws SugarException, IOException {
        List<Expression> expressions = parse(cspFileName);
        // Conversion
        Logger.fine("Converting to clausal form CSP");
        csp = new CSP();
        Converter converter = new Converter(csp);
        Converter.INCREMENTAL_PROPAGATION = propagation;
        converter.convert(expressions);
        converter = null;
        expressions = null;
        Expression.clear();
        Runtime.getRuntime().gc();
        Logger.fine("CSP : " + csp.summary());
        Logger.status();
        if (propagation) {
            // Propagation
            Logger.fine("Propagation in CSP");
            csp.propagate();
            Logger.fine("CSP : " + csp.summary());
            if (debug > 0) {
                csp.output(System.out, "c ");
            }
            Logger.status();
        }
        if (csp.isUnsatisfiable()) {
            Logger.info("CSP is unsatisfiable after propagation");
            Logger.println("s UNSATISFIABLE");
            return;
        }
        if (simplify_clauses) {
            // Simplification
            Logger.fine("Simplifing CSP clauses by introducing new Boolean variables");
            Simplifier simplifier = new Simplifier(csp);
            simplifier.simplify();
            Logger.info("CSP : " + csp.summary());
            if (debug > 0) {
                csp.output(System.out, "c ");
            }
            Logger.status();
        }
        Runtime.getRuntime().gc();
    }
    
    public void encode(String cspFileName, String satFileName, String mapFileName)
    throws SugarException, IOException {
        translate(cspFileName);
        if (csp.isUnsatisfiable()) {
            return;
        }
        Logger.fine("Encoding CSP to SAT : " + satFileName);
        Encoder encoder = new Encoder(csp);
        encoder.encode(satFileName);
        Logger.fine("Writing map file : " + mapFileName);
        encoder.outputMap(mapFileName);
        Logger.status();
        Logger.info("SAT : " + encoder.summary());
    }
    
    public void decode(String outFileName, String mapFileName)
    throws SugarException, IOException {
        Logger.fine("Decoding " + outFileName);
        CSP csp = new CSP();
        List<String> objectiveVariableNames = null;
        BufferedReader rd = new BufferedReader(
                new InputStreamReader(new FileInputStream(mapFileName), "UTF-8"));
        while (true) {
            String line = rd.readLine();
            if (line == null)
                break;
            String[] s = line.split("\\s+");
            if (s[0].equals("objective")) {
                if (s[1].equals(SugarConstants.MINIMIZE)) {
                    csp.setObjective(Objective.MINIMIZE);
                } else if (s[1].equals(SugarConstants.MAXIMIZE)) {
                    csp.setObjective(Objective.MAXIMIZE);
                }
                objectiveVariableNames = new ArrayList<String>();
                for (int i = 2; i < s.length; i++)
                    objectiveVariableNames.add(s[i]);
            } else if (s[0].equals("int")) {
                String name = s[1];
                int code = Integer.parseInt(s[2]);
                IntegerDomain domain = null;
                if (s.length == 4) {
                    int lb;
                    int ub;
                    int pos = s[3].indexOf("..");
                    if (pos < 0) {
                        lb = ub = Integer.parseInt(s[3]);
                    } else {
                        lb = Integer.parseInt(s[3].substring(0, pos));
                        ub = Integer.parseInt(s[3].substring(pos+2));
                    }
                    domain = IntegerDomain.create(lb, ub);
                } else {
                    SortedSet<Integer> d = new TreeSet<Integer>();
                    for (int i = 3; i < s.length; i++) {
                        int lb;
                        int ub;
                        int pos = s[i].indexOf("..");
                        if (pos < 0) {
                            lb = ub = Integer.parseInt(s[i]);
                        } else {
                            lb = Integer.parseInt(s[i].substring(0, pos));
                            ub = Integer.parseInt(s[i].substring(pos+2));
                        }
                        for (int value = lb; value <= ub; value++) {
                            d.add(value);
                        }
                    }
                    domain = IntegerDomain.create(d);
                }
                IntegerVariable v = new IntegerVariable(name, domain);
                v.setCode(code);
                csp.add(v);
            } else if (s[0].equals("bool")) {
                // TODO
                String name = s[1];
                int code = Integer.parseInt(s[2]);
                BooleanVariable v = new BooleanVariable(name);
                v.setCode(code);
                csp.add(v);
            }
        }
        rd.close();
        if (objectiveVariableNames != null) {
            List<IntegerVariable> vs = new ArrayList<IntegerVariable>();
            for (String name : objectiveVariableNames) {
                IntegerVariable v = csp.getIntegerVariable(name);
                if (v == null)
                    throw new SugarException("Unknown objective variable " + name);
                vs.add(v);
            }
            csp.setObjectiveVariables(vs);
        }
        Encoder encoder = new Encoder(csp);
        if (encoder.decode(outFileName)) {
            if (csp.getObjectiveVariables() == null) {
                Logger.println("s SATISFIABLE");
            } else {
                String s = "o";
                for (IntegerVariable v : csp.getObjectiveVariables()) {
                    String name = v.getName();
                    int value = v.getValue();
                    Logger.println("c OBJECTIVE " + name + " " + value);
                    s += " " + value;
                }
                Logger.println(s);
            }
            if (competition) {
                Logger.print("v");
                for (IntegerVariable v : csp.getIntegerVariables()) {
                    if (! v.isAux() && ! v.getName().startsWith("_")) {
                        Logger.print(" " + v.getValue());
                    }
                }
                Logger.println("");
            } else {
                for (IntegerVariable v : csp.getIntegerVariables()) {
                    if (! v.isAux() && ! v.getName().startsWith("_")) {
                        Logger.println("a " + v.getName() + "\t" + v.getValue());
                    }
                }
                for (BooleanVariable v : csp.getBooleanVariables()) {
                    if (! v.isAux() && ! v.getName().startsWith("_")) {
                        Logger.println("a " + v.getName() + "\t" + v.getValue());
                    }
                }
                Logger.println("a");
            }
        } else {
            Logger.println("s UNSATISFIABLE");
        }
    }

    public void encodeHybrid(String cspFileName, String pbFileName, String mapFileName)
    throws SugarException, IOException {
        translate(cspFileName);
        if (csp.isUnsatisfiable()) {
            return;
        }
        Logger.fine("Hybrid Encoding CSP to PB : " + pbFileName);
        HybridConverter hybridConverter = new HybridConverter(csp);
        hybridConverter.convert();
        PBProblem problem = new PBFileProblem(pbFileName);
        HybridEncoder hybridEncoder = new HybridEncoder(csp, problem);
        hybridEncoder.encode();
        Logger.fine("Writing map file : " + mapFileName);
        hybridEncoder.outputMap(mapFileName);
        Logger.status();
        Logger.info("PB : " + problem.summary());
    }
    
    public void decodeHybrid(String outFileName, String mapFileName)
    throws SugarException, IOException {
        Logger.fine("Decoding " + outFileName);
        CSP csp = new CSP();
        List<String> objectiveVariableNames = null;
        BufferedReader rd = new BufferedReader(
                new InputStreamReader(new FileInputStream(mapFileName), "UTF-8"));
        while (true) {
            String line = rd.readLine();
            if (line == null)
                break;
            String[] s = line.split("\\s+");
            if (s[0].equals("objective")) {
                if (s[1].equals(SugarConstants.MINIMIZE)) {
                    csp.setObjective(Objective.MINIMIZE);
                } else if (s[1].equals(SugarConstants.MAXIMIZE)) {
                    csp.setObjective(Objective.MAXIMIZE);
                }
                objectiveVariableNames = new ArrayList<String>();
                for (int i = 2; i < s.length; i++)
                    objectiveVariableNames.add(s[i]);
            } else if (s[0].equals("int")) {
                String name = s[1];
                int code = Integer.parseInt(s[2]);
                IntegerDomain domain = null;
                if (s.length == 4) {
                    int lb;
                    int ub;
                    int pos = s[3].indexOf("..");
                    if (pos < 0) {
                        lb = ub = Integer.parseInt(s[3]);
                    } else {
                        lb = Integer.parseInt(s[3].substring(0, pos));
                        ub = Integer.parseInt(s[3].substring(pos+2));
                    }
                    domain = IntegerDomain.create(lb, ub);
                } else {
                    SortedSet<Integer> d = new TreeSet<Integer>();
                    for (int i = 3; i < s.length; i++) {
                        int lb;
                        int ub;
                        int pos = s[i].indexOf("..");
                        if (pos < 0) {
                            lb = ub = Integer.parseInt(s[i]);
                        } else {
                            lb = Integer.parseInt(s[i].substring(0, pos));
                            ub = Integer.parseInt(s[i].substring(pos+2));
                        }
                        for (int value = lb; value <= ub; value++) {
                            d.add(value);
                        }
                    }
                    domain = IntegerDomain.create(d);
                }
                IntegerVariable v = new IntegerVariable(name, domain);
                v.setCode(code);
                csp.add(v);
            } else if (s[0].equals("bool")) {
                // TODO
                String name = s[1];
                int code = Integer.parseInt(s[2]);
                BooleanVariable v = new BooleanVariable(name);
                v.setCode(code);
                csp.add(v);
            } else if (s[0].equals("encoding")) {
                String name = s[1];
                String encoding = s[2];
                if (encoding.equals("order")) {
                    csp.getIntegerVariable(name).setEncoding(Encoding.ORDER);
                } else if (encoding.equals("log")) {
                    csp.getIntegerVariable(name).setEncoding(Encoding.LOG);
                } else {
                    throw new SugarException("Unknown encoding " + encoding);
                }
            }
        }
        rd.close();
        if (objectiveVariableNames != null) {
            List<IntegerVariable> vs = new ArrayList<IntegerVariable>();
            for (String name : objectiveVariableNames) {
                IntegerVariable v = csp.getIntegerVariable(name);
                if (v == null)
                    throw new SugarException("Unknown objective variable " + name);
                vs.add(v);
            }
            csp.setObjectiveVariables(vs);
        }
        HybridEncoder hybridEncoder = new HybridEncoder(csp, null);
        if (hybridEncoder.decode(outFileName)) {
            if (csp.getObjectiveVariables() == null) {
                Logger.println("s SATISFIABLE");
            } else {
                String s = "o";
                for (IntegerVariable v : csp.getObjectiveVariables()) {
                    String name = v.getName();
                    int value = v.getValue();
                    Logger.println("c OBJECTIVE " + name + " " + value);
                    s += " " + value;
                }
                Logger.println(s);
            }
            if (competition) {
                Logger.print("v");
                for (IntegerVariable v : csp.getIntegerVariables()) {
                    if (! v.isAux() && ! v.getName().startsWith("_")) {
                        Logger.print(" " + v.getValue());
                    }
                }
                Logger.println("");
            } else {
                for (IntegerVariable v : csp.getIntegerVariables()) {
                    if (! v.isAux() && ! v.getName().startsWith("_")) {
                        Logger.println("a " + v.getName() + "\t" + v.getValue());
                    }
                }
                for (BooleanVariable v : csp.getBooleanVariables()) {
                    if (! v.isAux() && ! v.getName().startsWith("_")) {
                        Logger.println("a " + v.getName() + "\t" + v.getValue());
                    }
                }
                Logger.println("a");
            }
        } else {
            Logger.println("s UNSATISFIABLE");
        }
    }

    public void encodePB(String cspFileName, String pbFileName, String mapFileName)
    throws SugarException, IOException {
        translate(cspFileName);
        if (csp.isUnsatisfiable()) {
            return;
        }
        Logger.fine("Encoding CSP to PB : " + pbFileName);
        PBProblem problem = new PBFileProblem(pbFileName);
        PBEncoder pbEncoder = new PBEncoder(csp, problem, pbEncoding);
        pbEncoder.encode();
        Logger.fine("Writing map file : " + mapFileName);
        pbEncoder.outputMap(mapFileName);
        Logger.status();
        Logger.info("PB : " + problem.summary());
    }
    
    public void decodePB(String outFileName, String mapFileName)
    throws SugarException, IOException {
        Logger.fine("Decoding " + outFileName);
        CSP csp = new CSP();
        List<String> objectiveVariableNames = null;
        BufferedReader rd = new BufferedReader(
                new InputStreamReader(new FileInputStream(mapFileName), "UTF-8"));
        while (true) {
            String line = rd.readLine();
            if (line == null)
                break;
            String[] s = line.split("\\s+");
            if (s[0].equals("objective")) {
                if (s[1].equals(SugarConstants.MINIMIZE)) {
                    csp.setObjective(Objective.MINIMIZE);
                } else if (s[1].equals(SugarConstants.MAXIMIZE)) {
                    csp.setObjective(Objective.MAXIMIZE);
                }
                objectiveVariableNames = new ArrayList<String>();
                for (int i = 2; i < s.length; i++)
                    objectiveVariableNames.add(s[i]);
            } else if (s[0].equals("int")) {
                String name = s[1];
                int code = Integer.parseInt(s[2]);
                IntegerDomain domain = null;
                if (s.length == 4) {
                    int lb;
                    int ub;
                    int pos = s[3].indexOf("..");
                    if (pos < 0) {
                        lb = ub = Integer.parseInt(s[3]);
                    } else {
                        lb = Integer.parseInt(s[3].substring(0, pos));
                        ub = Integer.parseInt(s[3].substring(pos+2));
                    }
                    domain = IntegerDomain.create(lb, ub);
                } else {
                    SortedSet<Integer> d = new TreeSet<Integer>();
                    for (int i = 3; i < s.length; i++) {
                        int lb;
                        int ub;
                        int pos = s[i].indexOf("..");
                        if (pos < 0) {
                            lb = ub = Integer.parseInt(s[i]);
                        } else {
                            lb = Integer.parseInt(s[i].substring(0, pos));
                            ub = Integer.parseInt(s[i].substring(pos+2));
                        }
                        for (int value = lb; value <= ub; value++) {
                            d.add(value);
                        }
                    }
                    domain = IntegerDomain.create(d);
                }
                IntegerVariable v = new IntegerVariable(name, domain);
                v.setCode(code);
                csp.add(v);
            } else if (s[0].equals("bool")) {
                // TODO
                String name = s[1];
                int code = Integer.parseInt(s[2]);
                BooleanVariable v = new BooleanVariable(name);
                v.setCode(code);
                csp.add(v);
            }
        }
        rd.close();
        if (objectiveVariableNames != null) {
            List<IntegerVariable> vs = new ArrayList<IntegerVariable>();
            for (String name : objectiveVariableNames) {
                IntegerVariable v = csp.getIntegerVariable(name);
                if (v == null)
                    throw new SugarException("Unknown objective variable " + name);
                vs.add(v);
            }
            csp.setObjectiveVariables(vs);
        }
        PBEncoder pbEncoder = new PBEncoder(csp, null, pbEncoding);
        if (pbEncoder.decode(outFileName)) {
            if (csp.getObjectiveVariables() == null) {
                Logger.println("s SATISFIABLE");
            } else {
                String s = "o";
                for (IntegerVariable v : csp.getObjectiveVariables()) {
                    String name = v.getName();
                    int value = v.getValue();
                    Logger.println("c OBJECTIVE " + name + " " + value);
                    s += " " + value;
                }
                Logger.println(s);
            }
            if (competition) {
                Logger.print("v");
                for (IntegerVariable v : csp.getIntegerVariables()) {
                    if (! v.isAux() && ! v.getName().startsWith("_")) {
                        Logger.print(" " + v.getValue());
                    }
                }
                Logger.println("");
            } else {
                for (IntegerVariable v : csp.getIntegerVariables()) {
                    if (! v.isAux() && ! v.getName().startsWith("_")) {
                        Logger.println("a " + v.getName() + "\t" + v.getValue());
                    }
                }
                for (BooleanVariable v : csp.getBooleanVariables()) {
                    if (! v.isAux() && ! v.getName().startsWith("_")) {
                        Logger.println("a " + v.getName() + "\t" + v.getValue());
                    }
                }
                Logger.println("a");
            }
        } else {
            Logger.println("s UNSATISFIABLE");
        }
    }

    public void outputCSP(String inputFileName, String outputFileName, String format, String outputHook)
            throws SugarException, IOException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        Logger.info("Translate CSP to " + format);
        translate(inputFileName);
        PrintWriter out = null;        
        if (outputFileName != null)
            out = new PrintWriter(new BufferedWriter(new FileWriter(outputFileName)));
        OutputInterface output;
        if (outputHook == null) {
            if (format.equals("smt"))
                output = new OutputSMT();
            else
                output = new Output();
        } else {
            Class<?> clazz = Class.forName(outputHook);
            output = (OutputInterface)clazz.newInstance();
        }
        output.setCSP(csp);
        output.setOut(out);
        output.setFormat(format);
        output.output();
        out.close();
        Logger.status();
    }
    
    private static boolean setOption(String opt) {
        String[] s = opt.split("=", 2);
        if (opt.matches("(no_)?peep(hole)?")) {
            Converter.OPT_PEEPHOLE = ! opt.startsWith("no_");
        } else if (opt.matches("(no_)?linear(ize)?")) {
            Converter.LINEARIZE = ! opt.startsWith("no_");
        } else if (opt.matches("(no_)?norm(alize_linearsum)?")) {
            Converter.NORMALIZE_LINEARSUM = ! opt.startsWith("no_");
        } else if (opt.matches("(no_)?prop(agation)?")) {
            propagation = ! opt.startsWith("no_");
        } else if (opt.matches("(no_)?simp(lify(_clauses)?)?")) {
            simplify_clauses = ! opt.startsWith("no_");
        } else if (opt.matches("(no_)?reduce(_arity)?") || opt.matches("(no_)?new_variable")) {
            Converter.REDUCE_ARITY = ! opt.startsWith("no_");
        } else if (opt.matches("arity=(\\d+)")) {
            Converter.MAX_ARITY = Integer.parseInt(s[1]);
        } else if (opt.matches("(no_)?decomp(ose)?")) {
            Converter.setDecomposeAll(! opt.startsWith("no_"));
        } else if (opt.matches("(no_)?decomp(ose)?_rel(ation)?")) {
            Converter.DECOMPOSE_RELATION = ! opt.startsWith("no_");
        } else if (opt.matches("(no_)?decomp(ose)?_alldiff(erent)?")) {
            Converter.DECOMPOSE_ALLDIFFERENT = ! opt.startsWith("no_");
        } else if (opt.matches("(no_)?decomp(ose)?_(weightedsum|wsum)")) {
            Converter.DECOMPOSE_WEIGHTEDSUM = ! opt.startsWith("no_");
        } else if (opt.matches("(no_)?decomp(ose)?_cumul(ative)?")) {
            Converter.DECOMPOSE_CUMULATIVE = ! opt.startsWith("no_");
        } else if (opt.matches("(no_)?decomp(ose)?_elem(ent)?")) {
            Converter.DECOMPOSE_ELEMENT = ! opt.startsWith("no_");
        } else if (opt.matches("(no_)?decomp(ose)?_disj(unctive)?")) {
            Converter.DECOMPOSE_DISJUNCTIVE = ! opt.startsWith("no_");
        } else if (opt.matches("(no_)?decomp(ose)?_lex_less")) {
            Converter.DECOMPOSE_LEX_LESS = ! opt.startsWith("no_");
        } else if (opt.matches("(no_)?decomp(ose)?_lex_lesseq")) {
            Converter.DECOMPOSE_LEX_LESSEQ = ! opt.startsWith("no_");
        } else if (opt.matches("(no_)?decomp(ose)?_nvalue")) {
            Converter.DECOMPOSE_NVALUE = ! opt.startsWith("no_");
        } else if (opt.matches("(no_)?decomp(ose)?_count")) {
            Converter.DECOMPOSE_COUNT = ! opt.startsWith("no_");
        } else if (opt.matches("(no_)?decomp(ose)?_(global_cardinality|gc)")) {
            Converter.DECOMPOSE_GLOBAL_CARDINALITY = ! opt.startsWith("no_");
        } else if (opt.matches("(no_)?decomp(ose)?_(global_cardinality_with_costs|gcc)")) {
            Converter.DECOMPOSE_GLOBAL_CARDINALITY_WITH_COSTS = ! opt.startsWith("no_");
        } else if (opt.matches("(no_)?hints")) {
            Converter.HINT_ALLDIFF_PIGEON = ! opt.startsWith("no_");
        } else if (opt.matches("(no_)?hint_alldiff_pigeon") || opt.matches("(no_)?pigeon")) {
            Converter.HINT_ALLDIFF_PIGEON = ! opt.startsWith("no_");
        } else if (opt.matches("(no_)?replace(_arguments|_args)?")) {
            Converter.REPLACE_ARGUMENTS = ! opt.startsWith("no_");
        } else if (opt.matches("equiv=(\\d+)")) {
            Converter.MAX_EQUIVMAP_SIZE = Integer.parseInt(s[1]);
        } else if (opt.matches("linearsum=(\\d+)")) {
            Converter.MAX_LINEARSUM_SIZE = Long.parseLong(s[1]);
        } else if (opt.matches("split=(\\d+)")) {
            Converter.SPLITS = Integer.parseInt(s[1]);
        } else if (opt.matches("(no_)?use_eq")) {
            Converter.USE_EQ = ! opt.startsWith("no_");
        } else if (opt.matches("(no_)?equiv(_translation)?")) {
            Converter.EQUIV_TRANSLATION = ! opt.startsWith("no_");
        } else if (opt.matches("(no_)?hold")) {
            Converter.HOLD_CONSTRAINTS = ! opt.startsWith("no_");
        } else if (opt.matches("(no_)?diet")) {
            IntegerDomain.USE_DIET_DOMAIN = ! opt.startsWith("no_");
        } else if (opt.matches("domain=(\\d+)")) {
            int size = Integer.parseInt(s[1]);
            IntegerDomainIntevals.MAX_SET_SIZE = size;
            IntegerDomainDiet.MAX_SET_SIZE = size;
        } else if (opt.startsWith("pb_")) {
            pb = true;
            if (opt.equals("pb_b")) {
                pbEncoding = PBEncoder.Encoding.COMPACT_ORDER_ENCODING;
                PBEncoder.BASE = 2;
            } else if (opt.equals("pb_d")) {
                pbEncoding = PBEncoder.Encoding.DIRECT_ENCODING;
            } else if (opt.equals("pb_o")) {
                pbEncoding = PBEncoder.Encoding.ORDER_ENCODING;
                PBEncoder.ENCODING_OPTION = 1;
            } else if (opt.matches("pb_o=\\d+")) {
                pbEncoding = PBEncoder.Encoding.ORDER_ENCODING;
                PBEncoder.ENCODING_OPTION = Integer.parseInt(s[1]);
            } else if (opt.matches("pb_c=\\d+")) {
                pbEncoding = PBEncoder.Encoding.COMPACT_ORDER_ENCODING;
                PBEncoder.BASE = Integer.parseInt(s[1]);
            } else if (opt.matches("pb_m=\\d+")) {
                pbEncoding = PBEncoder.Encoding.MIXED_ENCODING;
                PBEncoder.MIXED_BASE = Integer.parseInt(s[1]);
            }
        } else if (opt.startsWith("hy")) {
            hybrid = true;
            HybridConverter.HYBRID_OPTION = Integer.parseInt(s[0].substring(2));
            if (s.length > 1)
                HybridConverter.HYBRID_PARAM = Integer.parseInt(s[1]);
        } else if (opt.matches("(no_)?pmin")) {
            HybridConverter.USE_PMINIMAL = ! opt.startsWith("no_");
        } else if (opt.matches("(no_)?gcnf")) {
            Problem.GCNF = ! opt.startsWith("no_");
        } else if (opt.matches("(no_)?gwcnf")) {
            Problem.GWCNF = ! opt.startsWith("no_");
        } else if (opt.matches("simp_cache=(\\d+)")) {
            int size = Integer.parseInt(s[1]);
            if (size <= 0) {
                Simplifier.USE_SIMPLIFYCACHE = false;
            } else {
                Simplifier.USE_SIMPLIFYCACHE = true;
                Simplifier.MAX_SIMPLIFYCACHE_SIZE = size;
            }
        } else {
            return false;
        }
        return true;
    }
    
    public static void setDefaultOptions() {
        Converter.OPT_PEEPHOLE = true;
        Converter.LINEARIZE = true;
        Converter.NORMALIZE_LINEARSUM = true;
        propagation = true;
        simplify_clauses = true;
        Converter.REDUCE_ARITY = true;
        Converter.setDecomposeAll(true);
        Converter.DECOMPOSE_RELATION = false; // "true" has bug ??
        Converter.HINT_ALLDIFF_PIGEON = true;
        Converter.REPLACE_ARGUMENTS = false;
        Converter.USE_EQ = false;
        Converter.EQUIV_TRANSLATION = false;
        Converter.HOLD_CONSTRAINTS = false;
        IntegerDomain.USE_DIET_DOMAIN = false;
        Problem.GCNF = false;
        Problem.GWCNF = false;
        Simplifier.USE_SIMPLIFYCACHE = true;
        Simplifier.MAX_SIMPLIFYCACHE_SIZE = 1000;
        PBEncoder.BASE = 0;
        PBEncoder.ENCODING_OPTION = 1;
        HybridConverter.HYBRID_OPTION = 0;
        HybridConverter.USE_PMINIMAL = false;
    }
    
    public static void init() {
        setDefaultOptions();
        Converter.hooks = null;
    }
    
    /**
     * @param args
     */
    public static void main(String[] args) {
        try {
            setDefaultOptions();
            String outputHook = null;
            String option = "";
            int i = 0;
            while (i < args.length) {
                if (args[i].equals("-prolog")) {
                    prolog = true;
                } else if (args[i].equals("-max")) {
                    maxCSP = true;
                } else if (args[i].equals("-weighted")) {
                    weightedCSP = true;
                } else if (args[i].equals("-competition")) {
                    competition = true;
                } else if (args[i].equals("-incremental")) {
                    incremental = true;
                } else if (args[i].equals("-pb")) {
                    pb = true;
                    simplify_clauses = false;
                    Converter.REDUCE_ARITY = false;
                    if (pbEncoding == null)
                        pbEncoding = PBEncoder.Encoding.ORDER_ENCODING;
                } else if (args[i].equals("-hybrid")) {
                    hybrid = true;
                    simplify_clauses = false;
                    Converter.REDUCE_ARITY = false;
                    Converter.USE_EQ = true;
                } else if (args[i].equals("-option") && i + 1 < args.length) {
                    String[] opts = args[i+1].split(",");
                    for (String opt : opts) {
                        if (! setOption(opt)) 
                            throw new SugarException("Unknown option " + opt);
                    }
                    i++;
                } else if (args[i].equals("-debug") && i + 1 < args.length) {
                    debug = Integer.parseInt(args[i+1]);
                    i++;
                } else if (args[i].equals("-conversionHooks") && i + 1 < args.length) {
                    String[] hookNames = args[i+1].split(",");
                    for (String hookName : hookNames) {
                        Class<?> clazz = Class.forName(hookName);
                        ConverterHook hook = (ConverterHook)clazz.newInstance();
                        Converter.addHook(hook);
                    }
                    i++;
                } else if (args[i].equals("-outputHook") && i + 1 < args.length) {
                    outputHook = args[i+1];
                    i++;
                } else if (args[i].equals("-v") || args[i].equals("-verbose")) {
                    Logger.verboseLevel++;
                } else if (args[i].startsWith("-")) {
                    option = args[i];
                    break;
                }
                i++;
            }
            SugarMain sugarMain = new SugarMain();
            int n = args.length - i;
            if (option.equals("-to") && n == 4) {
                String format = args[i+1];
                String outputFileName = args[i+2];
                String inputFileName = args[i+3];
                if (outputFileName.equals("-"))
                    outputFileName = null;
                sugarMain.outputCSP(inputFileName, outputFileName, format, outputHook);
            } else if (option.equals("-encode") && n == 4) {
                String cspFileName = args[i+1];
                String satFileName = args[i+2];
                String mapFileName = args[i+3];
                if (hybrid)
                    sugarMain.encodeHybrid(cspFileName, satFileName, mapFileName);
                else if (pb || pbEncoding != null)
                    sugarMain.encodePB(cspFileName, satFileName, mapFileName);
                else
                    sugarMain.encode(cspFileName, satFileName, mapFileName);
            } else if (option.equals("-decode") && n == 3) {
                String outFileName = args[i+1];
                String mapFileName = args[i+2];
                if (hybrid)
                    sugarMain.decodeHybrid(outFileName, mapFileName);
                else if (pb || pbEncoding != null)
                    sugarMain.decodePB(outFileName, mapFileName);
                else
                    sugarMain.decode(outFileName, mapFileName);
            } else {
                String s = "";
                for (String a : args) {
                    s += " " + a;
                }
                throw new SugarException("Invalid arguments " + s);
            }
            Logger.status();
        } catch (Exception e) {
            Logger.println("c ERROR Exception " + e.getMessage());          
            for (StackTraceElement t : e.getStackTrace()) {
                Logger.info(t.toString());
            }
            Logger.println("s UNKNOWN");
            System.exit(1);
        } catch (Error e) {
            Logger.println("c ERROR Exception " + e.getMessage());          
            for (StackTraceElement t : e.getStackTrace()) {
                Logger.info(t.toString());
            }
            Logger.println("s UNKNOWN");
            System.exit(1);
        }
    }

}

package jp.kobe_u.sugar.converter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import jp.kobe_u.sugar.SugarException;
import jp.kobe_u.sugar.csp.BooleanVariable;
import jp.kobe_u.sugar.csp.CSP;
import jp.kobe_u.sugar.csp.IntegerDomain;
import jp.kobe_u.sugar.csp.IntegerVariable;
import jp.kobe_u.sugar.csp.Literal;
import jp.kobe_u.sugar.csp.Predicate;
import jp.kobe_u.sugar.csp.Relation;
import jp.kobe_u.sugar.csp.RelationLiteral;
import jp.kobe_u.sugar.csp.CSP.Objective;
import jp.kobe_u.sugar.expression.Expression;
import jp.kobe_u.sugar.expression.Sequence;

/**
 * @author Naoyuki Tamura (tamura@kobe-u.ac.jp)
 *
 */
public class DefinitionConverter {
    private Converter converter;
    private CSP csp;
    private Map<String,IntegerDomain> domainMap;
    private Map<String,IntegerVariable> intMap;
    private Map<String,BooleanVariable> boolMap;
    private Map<String,Predicate> predicateMap;
    private Map<String,Relation> relationMap;
    
    public DefinitionConverter(Converter converter) {
        this.converter = converter;
        csp = converter.csp;
        domainMap = new HashMap<String,IntegerDomain>();
        intMap = new HashMap<String,IntegerVariable>();
        boolMap = new HashMap<String,BooleanVariable>();
        predicateMap = new HashMap<String,Predicate>();
        relationMap = new HashMap<String,Relation>();
    }

    /*
    private LinearLe convertFormula(Expression x) throws SugarException {
        return converter.comparisonConverter.convertFormula(x);
    }
    */

    private IntegerDomain convertRanges(Sequence seq) throws SugarException {
        int[][] ranges = new int[seq.length()][2];
        for (int i = 0; i < seq.length(); i++) {
            if (seq.get(i).isInteger()) {
                int value = seq.get(i).integerValue(); 
                ranges[i] = new int[] { value, value };
            } else if (seq.get(i).isSequence()) {
                Sequence seq1 = (Sequence)seq.get(i);
                if (seq1.matches("II")) {
                    int value0 = ((Sequence)seq.get(i)).get(0).integerValue();
                    int value1 = ((Sequence)seq.get(i)).get(1).integerValue();
                    if (value0 > value1)
                        throw new SugarException("Bad domain definition " + seq);
                    ranges[i] = new int[] { value0, value1 };
                } else {
                    throw new SugarException("Bad domain definition " + seq);
                }
            } else {
                throw new SugarException("Bad domain definition " + seq);
            }
        }
        if (ranges.length == 0)
            throw new SugarException("Bad definition " + seq);
        if (ranges.length == 1)
            return IntegerDomain.create(ranges[0][0], ranges[0][1]);
        SortedSet<Integer> d = new TreeSet<Integer>();
        for (int[] range : ranges) {
            for (int value = range[0]; value <= range[1]; value++) {
                d.add(value);
            }
        }
        return IntegerDomain.create(d);
    }
    
    protected void convertDomainDefinition(Sequence seq) throws SugarException {
        String name = null;
        IntegerDomain domain = null;
        if (seq.matches("WWII")) {
            name = seq.get(1).stringValue();
            int lb = seq.get(2).integerValue();
            int ub = seq.get(3).integerValue();
            domain = IntegerDomain.create(lb, ub);
        } else if (seq.matches("WWI")) {
            name = seq.get(1).stringValue();
            int lb = seq.get(2).integerValue();
            domain = IntegerDomain.create(lb, lb);
        } else if (seq.matches("WWS")) {
            name = seq.get(1).stringValue();
            domain = convertRanges((Sequence)seq.get(2));
        } else {
            throw new SugarException("Bad definition " + seq);
        }
        if (domainMap.containsKey(name)) {
            throw new SugarException("Duplicated definition " + seq);
        }
        domainMap.put(name, domain);
    }

    protected void convertIntDefinition(Sequence seq, boolean dominating) throws SugarException {
        String name = null;
        IntegerDomain domain = null;
        if (seq.matches("WWW")) {
            name = seq.get(1).stringValue();
            String domainName = seq.get(2).stringValue();
            domain = domainMap.get(domainName);
        } else if (seq.matches("WWII")) {
            name = seq.get(1).stringValue();
            int lb = seq.get(2).integerValue();
            int ub = seq.get(3).integerValue();
            domain = IntegerDomain.create(lb, ub);
        } else if (seq.matches("WWI")) {
            name = seq.get(1).stringValue();
            int lb = seq.get(2).integerValue();
            domain = IntegerDomain.create(lb, lb);
        } else if (seq.matches("WWS")) {
            name = seq.get(1).stringValue();
            domain = convertRanges((Sequence)seq.get(2));
        } else {
            throw new SugarException("Bad definition " + seq);
        }
        if (domain == null) {
            throw new SugarException("Unknown domain " + seq);
        }
        /*
        if (intMap.containsKey(name)) {
            throw new SugarException("Duplicated definition " + seq);
        }
        */
        IntegerVariable v = new IntegerVariable(name, domain);
        v.setDominant(dominating);
        csp.add(v);
        intMap.put(name, v);
    }

    protected BooleanVariable toBool(String name) {
        return boolMap.get(name);
    }
    
    protected void convertBoolDefinition(Sequence seq, boolean dominating) throws SugarException {
        String name = null;
        if (seq.matches("WW")) {
            name = seq.get(1).stringValue();
        } else {
            throw new SugarException("Bad definition " + seq);
        }
        /*
        if (boolMap.containsKey(name)) {
            throw new SugarException("Duplicated definition " + seq);
        }
        */
        BooleanVariable v = new BooleanVariable(name);
        v.setDominant(dominating);
        csp.add(v);
        boolMap.put(name, v);
    }

    protected void convertPredicateDefinition(Sequence seq) throws SugarException {
        if (! seq.matches("WSS")) {
            converter.syntaxError(seq);
        }
        Sequence def = (Sequence)seq.get(1);
        String name = def.get(0).stringValue();
        Sequence body = (Sequence)seq.get(2);
        Predicate pred = new Predicate(def, body);
        predicateMap.put(name, pred);
    }

    protected void convertRelationDefinition(Sequence seq) throws SugarException {
        if (! seq.matches("WWIS")) {
            converter.syntaxError(seq);
        }
        String name = seq.get(1).stringValue();
        int arity = seq.get(2).integerValue();
        Sequence body = (Sequence)seq.get(3);
        Relation rel = new Relation(name, arity, body);
        relationMap.put(name, rel);
        csp.addRelation(rel);
    }

    protected boolean isPredicate(Sequence seq) {
        return seq.length() > 0 && seq.get(0).isString() &&
                predicateMap.containsKey(seq.get(0).stringValue());
    }
    
    protected Expression convertPredicate(Sequence seq) throws SugarException {
        String name = seq.get(0).stringValue();
        Predicate pred = predicateMap.get(name);
        if (pred == null) {
            throw new SugarException("Undefined predicate " + name + " in " + seq);
        }
        Expression[] args = new Expression[seq.length() - 1];
        for (int i = 1; i < seq.length(); i++) {
            args[i-1] = seq.get(i);
        }
        Expression x = pred.apply(args);
        return x;
    }
    
    protected boolean isRelation(Sequence seq) {
        return seq.length() > 0 && relationMap.containsKey(seq.get(0).stringValue());
    }
    
    protected Literal convertRelation(Sequence seq, boolean negative) throws SugarException {
        String name = seq.get(0).stringValue();
        Relation rel = relationMap.get(name);
        if (rel == null) {
            throw new SugarException("Undefined relation " + name + " in " + seq);
        }
        IntegerVariable[] vs = new IntegerVariable[seq.length() - 1];
        for (int i = 1; i < seq.length(); i++) {
            IntegerVariable v = intMap.get(seq.get(i).stringValue());
            if (v == null) {
                converter.syntaxError(seq);
            }
            vs[i-1] = v;
        }
        return new RelationLiteral(rel.name, rel.arity, negative, rel.conflicts, rel.tuples, vs);
    }

    protected void convertObjectiveDefinition(Sequence seq) throws SugarException {
        Objective objective = null;
        if (seq.matches("WWW*")) {
            if (seq.get(1).equals(Expression.MINIMIZE)) {
                objective = Objective.MINIMIZE;
            } else if (seq.get(1).equals(Expression.MAXIMIZE)) {
                objective = Objective.MAXIMIZE;
            }
        }
        if (objective == null) {
            throw new SugarException("Bad definition " + seq);
        }
        List<IntegerVariable> vs = new ArrayList<IntegerVariable>();
        for (int i = 2; i < seq.length(); i++) {
            String name = seq.get(i).stringValue();
            if (name == null)
                throw new SugarException("Bad definition " + seq);
            IntegerVariable v = intMap.get(name);
            if (v == null)
                throw new SugarException("Unknown objective variable " + name);
            vs.add(v);
        }
        csp.setObjectiveVariables(vs);
        csp.setObjective(objective);
    }
    
    protected void convertGroupsDefinition(Sequence seq) throws SugarException {
        Objective objective = null;
        if (! seq.matches("WII")) {
            throw new SugarException("Bad definition " + seq);
        }
        int groups = seq.get(1).integerValue();
        int topWeight = seq.get(2).integerValue();
        csp.setGroups(groups);
        csp.setTopWeight(topWeight);
    }
}

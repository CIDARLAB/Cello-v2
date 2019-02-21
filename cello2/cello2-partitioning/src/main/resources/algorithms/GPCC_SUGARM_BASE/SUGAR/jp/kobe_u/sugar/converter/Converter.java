package jp.kobe_u.sugar.converter;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import jp.kobe_u.sugar.Logger;
import jp.kobe_u.sugar.SugarException;
import jp.kobe_u.sugar.SugarMain;
import jp.kobe_u.sugar.csp.BooleanLiteral;
import jp.kobe_u.sugar.csp.BooleanVariable;
import jp.kobe_u.sugar.csp.CSP;
import jp.kobe_u.sugar.csp.Clause;
import jp.kobe_u.sugar.csp.HoldLiteral;
import jp.kobe_u.sugar.csp.IntegerDomain;
import jp.kobe_u.sugar.csp.IntegerVariable;
import jp.kobe_u.sugar.csp.LabelLiteral;
import jp.kobe_u.sugar.csp.LinearLiteral;
import jp.kobe_u.sugar.csp.LinearSum;
import jp.kobe_u.sugar.csp.Literal;
import jp.kobe_u.sugar.csp.RelationLiteral;
import jp.kobe_u.sugar.csp.RelationLiteral.Brick;
import jp.kobe_u.sugar.expression.Expression;
import jp.kobe_u.sugar.expression.Sequence;
import jp.kobe_u.sugar.hook.ConverterHook;

/**
 * Converter class is used to convert input expressions to a CSP.
 * @see Expression
 * @see CSP 
 * @author Naoyuki Tamura (tamura@kobe-u.ac.jp)
 */
public class Converter {
    public static int MAX_EQUIVMAP_SIZE = 1000;
    public static long MAX_LINEARSUM_SIZE = 1024L;
    // public static long MAX_LINEARSUM_SIZE = 2048L;
    public static boolean OPT_PEEPHOLE = true;
    public static boolean OPT_PEEPHOLE_ABS = true;
    public static boolean HINT_ALLDIFF_PIGEON = true;
    public static boolean INCREMENTAL_PROPAGATION = true;
    public static boolean LINEARIZE = true;
    public static boolean NORMALIZE_LINEARSUM = true;
    public static boolean DECOMPOSE_RELATION = false;
    public static boolean DECOMPOSE_ALLDIFFERENT = true;
    public static boolean DECOMPOSE_WEIGHTEDSUM = true;
    public static boolean DECOMPOSE_CUMULATIVE = true;
    public static boolean DECOMPOSE_ELEMENT = true;
    public static boolean DECOMPOSE_DISJUNCTIVE = true;
    public static boolean DECOMPOSE_LEX_LESS = true;
    public static boolean DECOMPOSE_LEX_LESSEQ = true;
    public static boolean DECOMPOSE_NVALUE = true;
    public static boolean DECOMPOSE_COUNT = true;
    public static boolean DECOMPOSE_GLOBAL_CARDINALITY = true;
    public static boolean DECOMPOSE_GLOBAL_CARDINALITY_WITH_COSTS = true;
    public static boolean REPLACE_ARGUMENTS = false;
    public static boolean REDUCE_ARITY = true;
    public static int MAX_ARITY = 0;
    public static int SPLITS = 2;
    public static boolean USE_EQ = false;
    public static boolean EQUIV_TRANSLATION = false;
    public static boolean ESTIMATE_SATSIZE = false; // bad
    public static boolean ADD_GROUP_ID = false;
    public static boolean HOLD_CONSTRAINTS = false;
    
    public static List<ConverterHook> hooks = null;
    
    public static void setDecomposeAll(boolean flag) {
        DECOMPOSE_ALLDIFFERENT = flag;
        DECOMPOSE_WEIGHTEDSUM = flag;
        DECOMPOSE_CUMULATIVE = flag;
        DECOMPOSE_ELEMENT = flag;
        DECOMPOSE_DISJUNCTIVE = flag;
        DECOMPOSE_LEX_LESS = flag;
        DECOMPOSE_LEX_LESSEQ = flag;
        DECOMPOSE_NVALUE = flag;
        DECOMPOSE_COUNT = flag;
        DECOMPOSE_GLOBAL_CARDINALITY = flag;
        DECOMPOSE_GLOBAL_CARDINALITY_WITH_COSTS = flag;
    }
    
    public static void addHook(ConverterHook hook) {
        if (hooks == null)
            hooks = new ArrayList<ConverterHook>();
        hooks.add(hook);
    }
    
    private class EquivMap extends LinkedHashMap<Expression,IntegerVariable> {

        private static final long serialVersionUID = -4882267868872050198L;

        EquivMap() {
            super(100, 0.75f, true);
        }

        /* (non-Javadoc)
         * @see java.util.LinkedHashMap#removeEldestEntry(java.util.Map.Entry)
         */
        @Override
        protected boolean removeEldestEntry(Entry<Expression, IntegerVariable> eldest) {
            return size() > Converter.MAX_EQUIVMAP_SIZE;
        }
        
    }

    public CSP csp;
    public DefinitionConverter definitionConverter;
    public ComparisonConverter comparisonConverter;
    public GlobalConverter globalConverter;
    public ExpressionOptimizer expressionOptimizer;
    private List<Expression> extra;
    private Map<Expression,IntegerVariable> equivMap;
    
    public Converter(CSP csp) {
        this.csp = csp;
        definitionConverter = new DefinitionConverter(this);
        globalConverter = new GlobalConverter(this);
        comparisonConverter = new ComparisonConverter(this);
        expressionOptimizer = new ExpressionOptimizer(this);
        extra = new ArrayList<Expression>();
        equivMap = new EquivMap();
    }
    
    protected void addExtra(Expression x) {
        extra.add(x);
    }
    
    protected IntegerVariable getEquivalence(Expression x) {
        return equivMap.get(x);
    }
    
    protected void addEquivalence(IntegerVariable v, Expression x) {
        equivMap.put(x, v);
    }

    public void syntaxError(String s) throws SugarException {
        throw new SugarException("Syntax error " + s);
    }
    
    public void syntaxError(Expression x) throws SugarException {
        syntaxError(x.toString());
    }
    
    public void checkArity(Expression x, int arity) throws SugarException {
        if (! x.isSequence(arity)) {
            syntaxError(x);
        }
    }
    
    public LinearSum convertFormula(Expression x) throws SugarException {
        return comparisonConverter.convertFormula(x);
    }

    public IntegerVariable newIntegerVariable(IntegerDomain d, Expression x)
    throws SugarException {
        IntegerVariable v = new IntegerVariable(d);
        csp.add(v);
        v.setComment(v.getName() + " : " + x.toString());
        return v;
    }
    
    public IntegerVariable toIntegerVariable(Expression x)
    throws SugarException {
        LinearSum e = comparisonConverter.convertFormula(x);
        IntegerVariable v;
        if (e.isIntegerVariable()) {
            v = e.getCoef().firstKey();
        } else {
            v = newIntegerVariable(e.getDomain(), x);
            Expression eq = Expression.create(v.getName()).eq(x);
            eq.setComment(v.getName() + " == " + x);
            convertConstraint(eq);
            addEquivalence(v, x);
        }
        return v;
    }
    
    private List<Clause> convertDisj(Sequence seq, boolean negative) throws SugarException {
        List<Clause> clauses = null;
        if (seq.length() == 1) {
            clauses = new ArrayList<Clause>();
            clauses.add(new Clause());
        } else if (seq.length() == 2) {
            clauses = convertConstraint(seq.get(1), negative);
        } else {
            clauses = new ArrayList<Clause>();
            Clause clause = new Clause();
            // clause.setComment(seq.toString());
            clauses.add(clause);
            for (int i = 1; i < seq.length(); i++) {
                List<Clause> clauses0 = convertConstraint(seq.get(i), negative);
                if (clauses0.size() == 0) {
                    return clauses0;
                } else if (clauses0.size() == 1) {
                    clause.addAll(clauses0.get(0).getLiterals());
                } else {
                    BooleanVariable v = new BooleanVariable();
                    csp.add(v);
                    // v.setComment(seq.toString());
                    BooleanLiteral v0 = new BooleanLiteral(v, false);
                    BooleanLiteral v1 = new BooleanLiteral(v, true);
                    clause.add(v0);
                    if (EQUIV_TRANSLATION) {
                        for (Clause clause0 : clauses0) {
                            for (Literal lit: clause0.getLiterals()) {
                                Clause cl = new Clause(v0);
                                cl.add(lit.neg());
                                clauses.add(cl);
                            }
                            clause0.add(v1);
                            clauses.add(clause0);
                        }
                    } else {
                        for (Clause clause0 : clauses0) {
                            clause0.add(v1);
                        }
                        clauses.addAll(clauses0);
                    }
                }
            }
        }
        return clauses;
    }

    public void convertAtom(Expression x, boolean negative, List<Clause> clauses) throws SugarException {
        if (x.isInteger()) {
            if (x.integerValue() > 0) {
                x = Expression.TRUE;
            } else {
                x = Expression.FALSE;
            }
        }
        if ((x.equals(Expression.FALSE) && ! negative)
                || (x.equals(Expression.TRUE) && negative)) {
            clauses.add(new Clause());
        } else if ((x.equals(Expression.FALSE) && negative)
                || (x.equals(Expression.TRUE) && ! negative)) {
        } else {
            BooleanVariable v = definitionConverter.toBool(x.stringValue());
            if (v == null)
                syntaxError(x);
            Clause clause = new Clause(new BooleanLiteral(v, negative));
            clauses.add(clause);
        }
    }

    public Expression convertLogical(Sequence seq, boolean negative, List<Clause> clauses) throws SugarException {
        Expression x = null;
        if (seq.isSequence(Expression.IMP)) {
            checkArity(seq, 2);
            x = seq.get(1).not().or(seq.get(2));
        } else if (seq.isSequence(Expression.XOR)) {
            checkArity(seq, 2);
            x = (seq.get(1).or(seq.get(2))).and(seq.get(1).not().or(seq.get(2).not()));
        } else if (seq.isSequence(Expression.IFF)) {
            checkArity(seq, 2);
            x = (seq.get(1).not().or(seq.get(2))).and(seq.get(1).or(seq.get(2).not()));
        } else if ((seq.isSequence(Expression.AND) && ! negative)
                || (seq.isSequence(Expression.OR) && negative)) {
            for (int i = 1; i < seq.length(); i++) {
                List<Clause> clauses0 = convertConstraint(seq.get(i), negative);
                clauses.addAll(clauses0);
            }
        } else if ((seq.isSequence(Expression.OR) && ! negative)
                || (seq.isSequence(Expression.AND) && negative)) {
            clauses.addAll(convertDisj(seq, negative));
        } else {
            syntaxError(seq);
        }
        return x;
    }
    
    /*
    private Expression convertComparison0(Sequence seq, boolean negative, List<Clause> clauses) throws SugarException {
        Expression x = null;
        if (seq.isSequence(Expression.EQ)) {
            checkArity(seq, 2);
            x = (seq.get(1).le(seq.get(2))).and(seq.get(1).ge(seq.get(2)));
        } else if (seq.isSequence(Expression.NE)) {
            checkArity(seq, 2);
            x = (seq.get(1).lt(seq.get(2))).or(seq.get(1).gt(seq.get(2)));
        } else if ((seq.isSequence(Expression.LE) && ! negative)
                || (seq.isSequence(Expression.GT) && negative)) {
            checkArity(seq, 2);
            clauses.addAll(comparisonConverter.convertLE(seq.get(1).sub(seq.get(2))));
        } else if ((seq.isSequence(Expression.LT) && ! negative)
                || (seq.isSequence(Expression.GE) && negative)) {
            checkArity(seq, 2);
            clauses.addAll(comparisonConverter.convertLE(seq.get(1).sub(seq.get(2))
                    .add(Expression.ONE)));
        } else if ((seq.isSequence(Expression.GE) && ! negative)
                || (seq.isSequence(Expression.LT) && negative)) {
            checkArity(seq, 2);
            clauses.addAll(comparisonConverter.convertLE(seq.get(2).sub(seq.get(1))));
        } else if ((seq.isSequence(Expression.GT) && ! negative)
                || (seq.isSequence(Expression.LE) && negative)) {
            checkArity(seq, 2);
            clauses.addAll(comparisonConverter.convertLE(
                    seq.get(2).sub(seq.get(1)).add(Expression.ONE)));
        } else {
            syntaxError(seq);
        }
        return x;
    }
    */
    
    public Expression convertComparison(Sequence seq, boolean negative, List<Clause> clauses) throws SugarException {
        if (NORMALIZE_LINEARSUM) {
            if (seq.isSequence(Expression.EQ))
                return (seq.get(1).le(seq.get(2))).and(seq.get(1).ge(seq.get(2)));
            if (seq.isSequence(Expression.NE))
                return (seq.get(1).lt(seq.get(2))).or(seq.get(1).gt(seq.get(2)));
        }
        if ((seq.isSequence(Expression.EQ) && ! negative)
            || (seq.isSequence(Expression.NE) && negative)) {
            checkArity(seq, 2);
            clauses.addAll(comparisonConverter.convertComp(seq.get(1), seq.get(2), "eq"));
        } else if ((seq.isSequence(Expression.NE) && ! negative)
                || (seq.isSequence(Expression.EQ) && negative)) {
            checkArity(seq, 2);
            clauses.addAll(comparisonConverter.convertComp(seq.get(1), seq.get(2), "ne"));
        } else if ((seq.isSequence(Expression.LE) && ! negative)
                || (seq.isSequence(Expression.GT) && negative)) {
            checkArity(seq, 2);
            clauses.addAll(comparisonConverter.convertComp(seq.get(1), seq.get(2), "le"));
        } else if ((seq.isSequence(Expression.LT) && ! negative)
                || (seq.isSequence(Expression.GE) && negative)) {
            checkArity(seq, 2);
            clauses.addAll(comparisonConverter.convertComp(seq.get(1).add(1), seq.get(2), "le"));
        } else if ((seq.isSequence(Expression.GE) && ! negative)
                || (seq.isSequence(Expression.LT) && negative)) {
            checkArity(seq, 2);
            clauses.addAll(comparisonConverter.convertComp(seq.get(1), seq.get(2), "ge"));
        } else if ((seq.isSequence(Expression.GT) && ! negative)
                || (seq.isSequence(Expression.LE) && negative)) {
            checkArity(seq, 2);
            clauses.addAll(comparisonConverter.convertComp(seq.get(1), seq.get(2).add(1), "ge"));
        } else {
            syntaxError(seq);
        }
        return null;
    }
    
    public Expression convertGlobal(Sequence seq, boolean negative, List<Clause> clauses) throws SugarException {
        Expression x = null;
        if (seq.isSequence(Expression.ALLDIFFERENT)) {
            x = globalConverter.convertAllDifferent(seq);
        } else if (seq.isSequence(Expression.WEIGHTEDSUM)) {
            x = globalConverter.convertWeightedSum(seq);
        } else if (seq.isSequence(Expression.CUMULATIVE)) {
            x = globalConverter.convertCumulative(seq);
        } else if (seq.isSequence(Expression.ELEMENT)) {
            x = globalConverter.convertElement(seq);
        } else if (seq.isSequence(Expression.DISJUNCTIVE)) {
            x = globalConverter.convertDisjunctive(seq);
        } else if (seq.isSequence(Expression.LEX_LESS)) {
            x = globalConverter.convertLex_less(seq);
        } else if (seq.isSequence(Expression.LEX_LESSEQ)) {
            x = globalConverter.convertLex_lesseq(seq);
        } else if (seq.isSequence(Expression.NVALUE)) {
            x = globalConverter.convertNvalue(seq);
        } else if (seq.isSequence(Expression.COUNT)) {
            x = globalConverter.convertCount(seq);
        } else if (seq.isSequence(Expression.GLOBAL_CARDINALITY)) {
            x = globalConverter.convertGlobal_cardinality(seq);
        } else if (seq.isSequence(Expression.GLOBAL_CARDINALITY_WITH_COSTS)) {
            x = globalConverter.convertGlobal_cardinality_with_costs(seq);
        } else {
            syntaxError(seq);
        }
        return x;
    }
    
    private List<Clause> convertConstraint(Expression x, boolean negative) throws SugarException {
        List<Clause> clauses = new ArrayList<Clause>();
        while (true) {
            if (hooks != null) {
                Expression x1 = null;
                for (ConverterHook hook : hooks) {
                    x1 = hook.convertConstraint(this, x, negative, clauses);
                    if (x1 == null)
                        break;
                    x = x1;
                }
                if (x1 == null)
                    break;
            }
            if (x.isAtom()) {
                convertAtom(x, negative, clauses);
                break;
            } else {
                Sequence seq = (Sequence)x;
                if (OPT_PEEPHOLE) {
                    Expression y = expressionOptimizer.peephole(seq, negative);
                    if (y != null) {
                        x = y; negative = false;
                        continue;
                    }
                }
                if (seq.isSequence(Expression.HOLD)) {
                    Clause clause = new Clause(new HoldLiteral(seq.get(1), negative));
                    // clauses = new ArrayList<Clause>();
                    clauses.add(clause);
                    break;
                } else if (definitionConverter.isPredicate(seq)) {
                    x = definitionConverter.convertPredicate(seq);
                } else if (definitionConverter.isRelation(seq)) {
                    if (DECOMPOSE_RELATION) {
                        // TODO Bug
                        RelationLiteral lit = (RelationLiteral)definitionConverter.convertRelation(seq, negative);
                        List<Expression> e = new ArrayList<Expression>();
                        e.add(Expression.AND);
                        List<Brick> bricks = lit.getConflictBricks();
                        for (Brick brick : bricks) {
                            List<Expression> e1 = new ArrayList<Expression>();
                            e1.add(Expression.AND);
                            for (int i = 0; i < lit.arity; i++) {
                                Expression v = Expression.create(lit.vs[i].getName());
                                e1.add(v.ge(brick.lb[i]));
                                e1.add(v.le(brick.ub[i]));
                            }
                            e.add(Expression.create(e1).not());
                        }
                        x = Expression.create(e);
                    } else {
                        Literal lit = definitionConverter.convertRelation(seq, negative);
                        clauses.add(new Clause(lit));
                        break;
                    }
                } else if (Expression.isLogical(seq)) {
                    if (seq.isSequence(Expression.NOT)) {
                        checkArity(seq, 1);
                        x = seq.get(1);
                        negative = ! negative;
                        continue;
                    }
                    x = convertLogical(seq, negative, clauses);
                    if (x == null)
                        break;
                } else if (Expression.isComparison(seq)) {
                    if (! LINEARIZE) {
                        x = seq.hold();
                        continue;
                    }
                    x = convertComparison(seq, negative, clauses);
                    if (x == null)
                        break;
                } else if (Expression.isGlobalConstraint(seq)) {
                    x = convertGlobal(seq, negative, clauses);
                    if (x == null)
                        break;
                } else if (seq.isSequence(Expression.LABEL) && seq.matches("WI")) {
                    int label = seq.get(1).integerValue();
                    Literal lit = new LabelLiteral(label);
                    clauses.add(new Clause(lit));
                    break;
                } else {
                    syntaxError(x);
                }
            }
        }
        return clauses;
    }

    protected void convertConstraint(Expression x) throws SugarException {
        List<Clause> clauses = convertConstraint(x, false);
        // clauses = simplify(clauses);
        if (clauses.size() > 0) {
            if (x.getComment() == null) {
                clauses.get(0).setComment(x.toString());
            } else {
                clauses.get(0).setComment(x.getComment());
            }
        }
        for (Clause clause : clauses) {
            csp.add(clause);
            if (INCREMENTAL_PROPAGATION) {
                clause.propagate();
            }
        }
    }

    public Expression convertHold(Expression x) throws SugarException {
        while (true) {
            if (x.isAtom())
                break;
            Sequence seq = (Sequence)x;
            if (definitionConverter.isPredicate(seq)) {
                x = definitionConverter.convertPredicate(seq);
                continue;
            }
            Expression[] xs = new Expression[seq.length()];
            boolean modified = false;
            for (int i = 0; i < xs.length; i++) {
                xs[i] = convertHold(seq.get(i));
                modified = modified || xs[i].equals(seq.get(i));
            }
            if (modified)
                x = Expression.create(xs);
            break;
        }
        return x;
    }
    
    public void convertExpression(Expression x) throws SugarException {
        if (SugarMain.debug >= 2)
            System.out.println("Converting " + x);
        if (x.isSequence(Expression.DOMAIN_DEFINITION)) {
            definitionConverter.convertDomainDefinition((Sequence)x);
        } else if (x.isSequence(Expression.INT_DEFINITION)) {
            definitionConverter.convertIntDefinition((Sequence)x, false);
        } else if (x.isSequence(Expression.DINT_DEFINITION)) {
            definitionConverter.convertIntDefinition((Sequence)x, true);
        } else if (x.isSequence(Expression.BOOL_DEFINITION)) {
            definitionConverter.convertBoolDefinition((Sequence)x, false);
        } else if (x.isSequence(Expression.DBOOL_DEFINITION)) {
            definitionConverter.convertBoolDefinition((Sequence)x, true);
        } else if (x.isSequence(Expression.PREDICATE_DEFINITION)) {
            definitionConverter.convertPredicateDefinition((Sequence)x);
        } else if (x.isSequence(Expression.RELATION_DEFINITION)) {
            definitionConverter.convertRelationDefinition((Sequence)x);
        } else if (x.isSequence(Expression.OBJECTIVE_DEFINITION)) {
            definitionConverter.convertObjectiveDefinition((Sequence)x);
        } else if (x.isSequence(Expression.GROUPS_DEFINITION)) {
            definitionConverter.convertGroupsDefinition((Sequence)x);
        } else {
            if (HOLD_CONSTRAINTS) {
                x = convertHold(x);
                convertConstraint(Expression.create(Expression.HOLD, x));
            } else {
                convertConstraint(x);
            }
        }
    }
    
    public void convert(Expression x) throws SugarException {
        convertExpression(x);
        while (extra.size() > 0) {
            Expression x1 = extra.remove(0);
            convertExpression(x1);
        }
    }
    
    public void convert(List<Expression> expressions) throws SugarException {
        int n = expressions.size();
        int percent = 10;
        int count = 0;
        for (Expression x : expressions) {
            convertExpression(x);
            count++;
            if ((100*count)/n >= percent) {
                Logger.fine("converted " + count + " (" + percent + "%) expressions");
                percent += 10;
            }
        }
        while (extra.size() > 0) {
            Expression x = extra.remove(0);
            convertExpression(x);
            count++;
            if (count % 1000 == 0) {
                Logger.fine("converted " + count + " extra expressions, remaining " + extra.size());
            }
        }
    }
    
    public Clause reduce(Clause clause) throws SugarException {
        Clause newClause = new Clause();
        for (Literal lit : clause.getLiterals()) {
            if (lit instanceof LinearLiteral) {
                lit = comparisonConverter.reduceArity((LinearLiteral)lit);
            }
            newClause.add(lit);
        }
        return newClause;
    }
    
    public void reduceAll() throws SugarException {
        List<Clause> clauses = csp.getClauses();
        csp.setClauses(new ArrayList<Clause>());
        for (Clause clause : clauses) {
            Clause newClause = reduce(clause);
            csp.add(newClause);
        }
        while (extra.size() > 0) {
            Expression x = extra.remove(0);
            convertExpression(x);
        }
    }
    
}

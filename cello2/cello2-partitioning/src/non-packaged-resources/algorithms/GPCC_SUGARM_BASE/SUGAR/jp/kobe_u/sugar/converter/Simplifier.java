package jp.kobe_u.sugar.converter;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import jp.kobe_u.sugar.SugarException;
import jp.kobe_u.sugar.csp.BooleanLiteral;
import jp.kobe_u.sugar.csp.BooleanVariable;
import jp.kobe_u.sugar.csp.CSP;
import jp.kobe_u.sugar.csp.Clause;
import jp.kobe_u.sugar.csp.Literal;

public class Simplifier {
    public static boolean USE_SIMPLIFYCACHE = true;
    public static int MAX_SIMPLIFYCACHE_SIZE = 1000;
    private static boolean SIMPLIFY_ALL = true;

    private class SimplifyMap extends LinkedHashMap<Literal,BooleanLiteral> {
        SimplifyMap() {
            super(100, 0.75f, true);
        }

        /* (non-Javadoc)
         * @see java.util.LinkedHashMap#removeEldestEntry(java.util.Map.Entry)
         */
        @Override
        protected boolean removeEldestEntry(Entry<Literal,BooleanLiteral> eldest) {
            return size() > Simplifier.MAX_SIMPLIFYCACHE_SIZE;
        }
    }
    
    private CSP csp;
    private Map<Literal,BooleanLiteral> simplifyCache;
    
    public Simplifier(CSP csp) {
        this.csp = csp;
        simplifyCache = new SimplifyMap();
    }

    private List<Clause> simplify(Clause clause) throws SugarException {
        List<Clause> newClauses = new ArrayList<Clause>();
        if (clause.isSimple()) {
            newClauses.add(clause);
        } else {
            List<Literal> literals = clause.getLiterals();
            clause = new Clause();
            int complex = 0;
            for (Literal literal : literals) {
                if (literal.isSimple()) {
                    clause.add(literal);
                } else {
                    complex++;
                    if (! SIMPLIFY_ALL && complex == 1) {
                        clause.add(literal);
                    } else if (USE_SIMPLIFYCACHE && simplifyCache.containsKey(literal)) {
                        Literal lit = simplifyCache.get(literal); 
                        clause.add(lit);
                    } else {
                        BooleanVariable p = new BooleanVariable();
                        csp.add(p);
                        BooleanLiteral posLiteral = new BooleanLiteral(p, false);
                        BooleanLiteral negLiteral = new BooleanLiteral(p, true);
                        if (Converter.EQUIV_TRANSLATION) {
                            Clause newClause = new Clause();
                            newClause.add(negLiteral);
                            newClause.add(literal);
                            newClauses.add(newClause);
                            newClause = new Clause();
                            newClause.add(posLiteral);
                            newClause.add(literal.neg());
                            newClauses.add(newClause);
                        } else {
                            Clause newClause = new Clause();
                            newClause.add(negLiteral);
                            newClause.add(literal);
                            newClauses.add(newClause);
                        }
                        clause.add(posLiteral);
                        if (USE_SIMPLIFYCACHE)
                            simplifyCache.put(literal, posLiteral);
                    }
                }
            }
            newClauses.add(clause);
        }
        return newClauses;
    }
    
    public void simplify() throws SugarException {
        List<Clause> newClauses = new ArrayList<Clause>();
        for (Clause clause : csp.getClauses()) {
            List<Clause> newClauses1 = simplify(clause);
            newClauses.addAll(newClauses1);
        }
        csp.setClauses(newClauses);
    }

}

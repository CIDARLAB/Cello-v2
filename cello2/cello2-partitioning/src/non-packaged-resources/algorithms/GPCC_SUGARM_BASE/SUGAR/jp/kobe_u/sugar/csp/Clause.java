package jp.kobe_u.sugar.csp;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import jp.kobe_u.sugar.SugarConstants;
import jp.kobe_u.sugar.SugarException;

/**
 * This class implements a clause in CSP.
 * @see CSP
 * @author Naoyuki Tamura (tamura@kobe-u.ac.jp)
 */
public class Clause {
    private List<Literal> literals;
    private Set<IntegerVariable> commonVariables = null;
    private String comment = null;
    
    /**
     * Constructs a new clause with give literals.
     * @param literals the literals of the clause
     */
    public Clause(List<Literal> literals) {
        this.literals = literals;
    }

    /**
     * Constructs a new clause.
     */
    public Clause() {
        literals = new ArrayList<Literal>();
    }
    
    public Clause(Literal literal) {
        this();
        literals.add(literal);
    }

    /**
     * Returns the literals of the clause.
     * @return literals the literals
     */
    public List<Literal> getLiterals() {
        return literals;
    }

    /**
     * Adds all given literals to the clause.
     * @param literals the literals to be added
     */
    public void addAll(List<Literal> literals) {
        this.literals.addAll(literals);
    }

    /**
     * Adds the given literal to the clause.
     * @param literal the literal to be added.
     */
    public void add(Literal literal) {
        literals.add(literal);
    }

    public int size() {
        return literals.size();
    }
    
    /**
     * Returns the comment set to the clause.
     * @return the comment
     */
    public String getComment() {
        return comment;
    }

    /**
     * Sets the comment to the clause.
     * @param comment the comment to set
     */
    public void setComment(String comment) {
        this.comment = comment;
    }

    public boolean isModified() {
        for (Literal lit : literals) {
            Set<IntegerVariable> vs = lit.getVariables();
            if (vs != null) {
                for (IntegerVariable v : vs) {
                    if (v.isModified()) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
    
    public Set<IntegerVariable> getCommonVariables() {
        if (commonVariables == null && size() > 0) {
            for (Literal lit : literals) {
                Set<IntegerVariable> vs = lit.getVariables();
                if (vs == null) {
                    commonVariables = null;
                    break;
                } else if (commonVariables == null) {
                    commonVariables = vs;
                } else {
                    Set<IntegerVariable> vars = new TreeSet<IntegerVariable>(); 
                    for (IntegerVariable v : commonVariables) {
                        if (vs.contains(v)) {
                            vars.add(v);
                        }
                    }
                    commonVariables = vars;
                }
            }
            if (commonVariables == null) {
                commonVariables = new TreeSet<IntegerVariable>();
            }
        }
        return commonVariables;
    }
    
    public int simpleSize() {
        int simpleLiterals = 0;
        for (Literal literal : literals) {
            if (literal.isSimple()) {
                simpleLiterals++;
            }
        }
        return simpleLiterals;
    }

    /**
     * Returns true when the clause is simple.
     * A clause is simple when there is at most one non-simple literals.
     * @return true when the clause is simple.
     * @see Literal#isSimple()
     */
    public boolean isSimple() {
        return (size() - simpleSize()) <= 1;
    }
    
    public boolean isValid() throws SugarException {
        for (Literal lit : literals) {
            if (lit.isValid()) {
                return true;
            }
        }
        return false;
    }
    
    public boolean isUnsatisfiable() throws SugarException {
        for (Literal lit : literals) {
            if (! lit.isUnsatisfiable()) {
                return false;
            }
        }
        return true;
    }

    public int propagate() throws SugarException {
        if (size() == 0)
            return 0;
        // if (size() == 1)
        // return literals.get(0).propagate();
        int count = 0;
        for (IntegerVariable v : getCommonVariables()) {
            int[] bound = null;
            for (Literal lit : literals) {
                int[] b = lit.getBound(v);
                if (b == null) {
                    bound = null;
                    break;
                } else {
                    // System.out.println("Bound " + v + " " + b[0] + " " + b[1] + " by " + lit);
                    if (bound == null){
                        bound = b;
                    } else {
                        bound[0] = Math.min(bound[0], b[0]);
                        bound[1] = Math.max(bound[1], b[1]);
                    }
                }
            }
            if (bound != null) {
                // System.out.println("Bound " + v.getName() + " " + bound[0] + " " + bound[1]);
                int lb = Math.max(v.getDomain().getLowerBound(), bound[0]);
                int ub = Math.min(v.getDomain().getUpperBound(), bound[1]); 
                if (lb <= ub)
                    count += v.bound(bound[0], bound[1]);
            }
        }
        return count;
    }

    public int removeFalsefood() throws SugarException {
        int count = 0;
        int i = 0;
        while (i < literals.size()) {
            if (literals.get(i).isUnsatisfiable()) {
                literals.remove(i);
                count++;
            } else {
                i++;
            }
        }
        return count;
    }

    /**
     * Returns true when the clause is satisfied.
     * @return true when the clause is satisfied
     */
    public boolean isSatisfied() {
        for (Literal literal : literals) {
            if (literal.isSatisfied()) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Returns the string representation of the clause.
     * @return the string representation
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (literals.size() == 1) {
            sb.append(literals.get(0).toString());
        } else {
            sb.append("(" + SugarConstants.OR);
            for (Literal literal : literals) {
                sb.append(" ");
                sb.append(literal.toString());
            }
            sb.append(")");
        }
        return sb.toString();
    }

}

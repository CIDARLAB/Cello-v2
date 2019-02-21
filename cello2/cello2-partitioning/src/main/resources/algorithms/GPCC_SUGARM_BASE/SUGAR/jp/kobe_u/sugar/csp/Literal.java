package jp.kobe_u.sugar.csp;

import java.io.IOException;
import java.util.Set;

import jp.kobe_u.sugar.SugarException;

/**
 * This is an abstract class for literals of CSP.
 * @see CSP
 * @author Naoyuki Tamura (tamura@kobe-u.ac.jp)
 */
public abstract class Literal {

    public Set<IntegerVariable> getVariables() {
        return null;
    }

    public int[] getBound(IntegerVariable v) throws SugarException {
        return null;
    }

    /**
     * Returns true when the literal is simple.
     * A literal is simple when it is a boolean literal or
     * a comparison literal with at most one integer variable.
     * @return true when the literal is simple
     */
    public abstract boolean isSimple();
    
    public abstract boolean isValid() throws SugarException;
    
    public abstract boolean isUnsatisfiable() throws SugarException;

    public abstract int propagate() throws SugarException;
    
    public abstract Literal neg() throws SugarException;

    public int getCode() throws SugarException {
        throw new SugarException("Internal error " + toString()); 
    }

    /**
     * Returns true when the literal is satisfied.
     * @return true when the literal is satisfied
     */
    public abstract boolean isSatisfied();

    protected int[] expand(int[] clause0, int n) {
        int[] clause = new int[clause0.length + n];
        for (int i = 0; i < clause0.length; i++) {
            clause[i + n] = clause0[i];
        }
        return clause;
    }

}

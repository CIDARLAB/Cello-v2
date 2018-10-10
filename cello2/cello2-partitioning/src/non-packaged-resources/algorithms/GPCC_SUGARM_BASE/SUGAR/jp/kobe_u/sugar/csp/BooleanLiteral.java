package jp.kobe_u.sugar.csp;

import java.io.IOException;

import jp.kobe_u.sugar.SugarException;

/**
 * This class implements a boolean literal of CSP.
 * @see CSP
 * @author Naoyuki Tamura (tamura@kobe-u.ac.jp)
 */
public class BooleanLiteral extends Literal {
    private BooleanVariable v;

    private boolean negative;

    /**
     * Constructs a new boolean literal of the given boolean variable and negative flag.
     * @param v the boolean variable of CSP
     * @param negative the negative flag
     */
    public BooleanLiteral(BooleanVariable v, boolean negative) {
        this.v = v;
        this.negative = negative;
    }
    
    /**
     * Always returns true since boolean literals are simple.
     * @return always true
     */
    @Override
    public boolean isSimple() {
        return true;
    }
    
    /**
     * Returns the boolean variable of CSP.
     * @return the boolean variable
     */
    public BooleanVariable getBooleanVariable() {
        return v;
    }
    
    /**
     * Returns the negative flag of the boolean literal.
     * @return the negative flag
     */
    public boolean getNegative() {
        return negative;
    }

    @Override
    public boolean isValid() throws SugarException {
        return false;
    }
    
    @Override
    public boolean isUnsatisfiable() throws SugarException {
        return false;
    }
    
    @Override
    public int propagate() {
        return 0;
    }
    
    /* (non-Javadoc)
     * @see Literal#isSatisfied()
     */
    @Override
    public boolean isSatisfied() {
        return v.getValue() ^ negative;
    }

    @Override
    public Literal neg() throws SugarException {
        return new BooleanLiteral(v, ! negative);
    }
    
    @Override
    public int getCode() {
        int code = v.getCode();
        return negative ? -code : code;
    }

    /**
     * Returns the string representation of the boolean literal.
     * @return the string representation
     */
    @Override
    public String toString() {
        String s = getBooleanVariable().getName();
        if (negative) {
            s = "(not " + s + ")";
        }
        return s;
    }

}

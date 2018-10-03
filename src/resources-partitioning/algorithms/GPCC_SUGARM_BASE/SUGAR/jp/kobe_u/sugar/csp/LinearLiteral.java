package jp.kobe_u.sugar.csp;

import java.io.IOException;
import java.util.Iterator;
import java.util.Set;

import jp.kobe_u.sugar.SugarConstants;
import jp.kobe_u.sugar.SugarException;

/**
 * This class implements a comparison literal of CSP.
 * The comparison represents the condition "linearSum cmp 0".
 * @see CSP
 * @see LinearSum
 * @author Naoyuki Tamura (tamura@kobe-u.ac.jp)
 */
public abstract class LinearLiteral extends Literal {
    protected LinearSum linearSum;
    protected String cmp;

    /**
     * Constructs a new comparison literal of given linear expression.
     * @param linearSum the linear expression
     */
    public LinearLiteral(LinearSum linearSum, String cmp) {
        /*
        int factor = linearSum.factor();
        if (factor > 1) {
            linearSum.divide(factor);
        }
        */
        int factor = linearSum.coefGCD();
        if (factor > 1) {
            int b = linearSum.getB();
            linearSum.divide(factor);
            if (cmp.equals("eq")) {
                if (b % factor == 0) {
                } else {
                    // false literal
                    linearSum = LinearSum.ONE;
                    cmp = "le";
                }
            } else if (cmp.equals("ne")) {
                if (b % factor == 0) {
                } else {
                    // true literal
                    linearSum = LinearSum.ONE;
                    cmp = "ge";
                }
            } else if (cmp.equals("le")) {
                linearSum.setB(-(int)Math.floor(-(double)b / factor));
            } else if (cmp.equals("ge")) {
                linearSum.setB(-(int)Math.ceil(-(double)b / factor));
            }
        }
        this.linearSum = linearSum;
        this.cmp = cmp;
    }
    
    @Override
    public Set<IntegerVariable> getVariables() {
        return linearSum.getVariables();
    }

    public int floorDiv(int b, int a) {
        if (a < 0) {
            a = - a;
            b = - b;
        }
        if (b >= 0)
            return b / a;
        else
            return (b - a + 1) / a;
    }
    
    public int ceilDiv(int b, int a) {
        if (a < 0) {
            a = - a;
            b = - b;
        }
        if (b >= 0)
            return (b + a - 1) / a;
        else
            return b / a;
    }
    
    /**
     * Returns the linear expression of the comparison literal.
     * @return the linear expression
     */
    public LinearSum getLinearExpression() {
        return linearSum;
    }

    public String getCmp() {
        return cmp;
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((cmp == null) ? 0 : cmp.hashCode());
        result = prime * result
                + ((linearSum == null) ? 0 : linearSum.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        LinearLiteral other = (LinearLiteral) obj;
        if (cmp == null) {
            if (other.cmp != null)
                return false;
        } else if (!cmp.equals(other.cmp))
            return false;
        if (linearSum == null) {
            if (other.linearSum != null)
                return false;
        } else if (!linearSum.equals(other.linearSum))
            return false;
        return true;
    }

    /**
     * Returns the string representation of the comparison literal.
     * @return the string representation
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("(" + SugarConstants.WSUM + " (");
        String delim = "";
        for (IntegerVariable v : linearSum.getVariables()) {
            sb.append(delim);
            sb.append("(" + linearSum.getA(v) + " " + v.getName() + ")");
            delim = " ";
        }
        sb.append(") " + cmp + " ");
        sb.append(- linearSum.getB());
        sb.append(")");
        return sb.toString();
    }

}

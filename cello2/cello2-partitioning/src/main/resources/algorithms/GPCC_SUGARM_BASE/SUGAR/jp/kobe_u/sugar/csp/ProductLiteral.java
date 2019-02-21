package jp.kobe_u.sugar.csp;

import java.io.IOException;
import java.util.Set;

import jp.kobe_u.sugar.SugarException;

/**
 * NOT IMPLEMENTED YET.
 * This class implements a literal for arithmetic power.
 * @see CSP
 * @author Naoyuki Tamura (tamura@kobe-u.ac.jp)
 */
public class ProductLiteral extends Literal {
    private IntegerVariable v;
    private IntegerVariable v1;
    private IntegerVariable v2;

    public ProductLiteral(IntegerVariable v, IntegerVariable v1, IntegerVariable v2) {
        this.v = v;
        this.v1 = v1;
        this.v2 = v2;
    }

    @Override
    public Set<IntegerVariable> getVariables() {
        // TODO
        return null;
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
        // TODO propagate
        return 0;
    }

    @Override
    public boolean isSatisfied() {
        return v.getValue() == v1.getValue() * v2.getValue();
    }

    @Override
    public boolean isSimple() {
        return false;
    }

    @Override
    public Literal neg() throws SugarException {
        throw new SugarException("Negation of ProductLiteral " + this);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((v == null) ? 0 : v.hashCode());
        result = prime * result + ((v1 == null) ? 0 : v1.hashCode());
        result = prime * result + ((v2 == null) ? 0 : v2.hashCode());
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
        ProductLiteral other = (ProductLiteral) obj;
        if (v == null) {
            if (other.v != null)
                return false;
        } else if (!v.equals(other.v))
            return false;
        if (v1 == null) {
            if (other.v1 != null)
                return false;
        } else if (!v1.equals(other.v1))
            return false;
        if (v2 == null) {
            if (other.v2 != null)
                return false;
        } else if (!v2.equals(other.v2))
            return false;
        return true;
    }

    @Override
    public String toString() {
        String s = "(eq " + v.getName() + " (mul " + v1.getName() + " " + v2.getName() + "))";
        return s;
    }
}

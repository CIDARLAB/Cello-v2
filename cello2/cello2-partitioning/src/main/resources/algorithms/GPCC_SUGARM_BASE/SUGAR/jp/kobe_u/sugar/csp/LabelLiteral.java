package jp.kobe_u.sugar.csp;

import java.util.Set;

import jp.kobe_u.sugar.SugarException;
import jp.kobe_u.sugar.expression.Expression;

/**
 * This class implements a literal for representing labels of group-CNF/labelled-CNF.
 * @see CSP
 * @author Naoyuki Tamura (tamura@kobe-u.ac.jp)
 */
public class LabelLiteral extends Literal {
    private int label;

    public LabelLiteral(int label) {
        this.label = label;
    }

    public int getLabel() {
        return label;
    }

    @Override
    public boolean isSimple() {
        return true;
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

    @Override
    public boolean isSatisfied() {
        // TODO isSatisfied
        return false;
    }

    @Override
    public Literal neg() throws SugarException {
        throw new SugarException("Negation of LabelLiteral " + this);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + label;
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
        LabelLiteral other = (LabelLiteral) obj;
        if (label != other.label)
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "(label " + label + ")";
    }

}

package jp.kobe_u.sugar.csp;

import java.util.Set;

import jp.kobe_u.sugar.SugarException;
import jp.kobe_u.sugar.expression.Expression;

/**
 * This class implements a literal for constraints when they are not linearlized nor decomposed.
 * @see CSP
 * @author Naoyuki Tamura (tamura@kobe-u.ac.jp)
 */
public class HoldLiteral extends Literal {
    private Expression expr;
    private boolean negative;

    public HoldLiteral(Expression expr, boolean negative) {
        this.expr = expr;
        this.negative = negative;
    }
    
    public Expression getExpression() {
        return expr;
    }

    public boolean isNegative() {
        return negative;
    }

    @Override
    public Set<IntegerVariable> getVariables() {
        // TODO
        return null;
    }

    @Override
    public boolean isSimple() {
        return false;
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
        // TODO isSatisfied
        return false;
    }
    
    @Override
    public Literal neg() throws SugarException {
        return new HoldLiteral(expr, ! negative);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((expr == null) ? 0 : expr.hashCode());
        result = prime * result + (negative ? 1231 : 1237);
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
        HoldLiteral other = (HoldLiteral) obj;
        if (expr == null) {
            if (other.expr != null)
                return false;
        } else if (!expr.equals(other.expr))
            return false;
        if (negative != other.negative)
            return false;
        return true;
    }

    @Override
    public String toString() {
        if (negative)
            return "(not " + expr.toString() + ")";
        else
            return expr.toString();
    }

}

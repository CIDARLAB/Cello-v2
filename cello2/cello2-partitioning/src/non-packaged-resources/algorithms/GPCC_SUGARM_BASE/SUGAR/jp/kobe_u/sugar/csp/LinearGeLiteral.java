package jp.kobe_u.sugar.csp;

import java.io.IOException;
import java.util.Iterator;
import java.util.Set;

import jp.kobe_u.sugar.SugarConstants;
import jp.kobe_u.sugar.SugarException;
import jp.kobe_u.sugar.encoder.Encoding;
import jp.kobe_u.sugar.encoder.Problem;

/**
 * This class implements a comparison literal of CSP.
 * The comparison represents the condition "linearSum >= 0".
 * @see CSP
 * @see LinearSum
 * @author Naoyuki Tamura (tamura@kobe-u.ac.jp)
 */
public class LinearGeLiteral extends LinearLiteral {
    /**
     * Constructs a new comparison literal of given linear expression.
     * @param linearSum the linear expression
     */
    public LinearGeLiteral(LinearSum linearSum) {
        super(linearSum, "ge");
    }
    
    @Override
    public int[] getBound(IntegerVariable v) throws SugarException {
        IntegerDomain d = v.getDomain();
        int lb0 = d.getLowerBound();
        int ub0 = d.getUpperBound();
        int lb = lb0;
        int ub = ub0;
        int a = linearSum.getA(v);
        if (a > 0) {
            d = linearSum.getDomainExcept(v).neg();
            lb = ceilDiv(d.getLowerBound(), a);
        } else if (a < 0) {
            d = linearSum.getDomainExcept(v);
            ub = floorDiv(d.getUpperBound(), -a);
        }
        if (lb > ub)
            return null;
        return new int[] { lb, ub };
    }

    @Override
    public boolean isSimple() {
        // return linearSum.isSimple();
        if (! linearSum.isSimple())
            return false;
        if (linearSum.size() == 0)
            return true;
        Encoding encoding = linearSum.getCoef().firstKey().getEncoding();
        switch (encoding) {
        case ORDER:
            return true;
        default:
            return false;
        }
    }
    
    @Override
    public boolean isValid() throws SugarException {
        return linearSum.getDomain().getLowerBound() >= 0;
    }
    
    @Override
    public boolean isUnsatisfiable() throws SugarException {
        return linearSum.getDomain().getUpperBound() < 0;
    }
    
    @Override
    public int propagate() throws SugarException {
        if (linearSum.size() == 0) {
            return 0;
        }
        int removed = 0;
        for (IntegerVariable v : linearSum.getCoef().keySet()) {
            IntegerDomain d = v.getDomain();
            int lb = d.getLowerBound();
            int ub = d.getUpperBound();
            int a = linearSum.getA(v);
            if (a > 0) {
                IntegerDomain d1 = linearSum.getDomainExcept(v).neg().div(a);
                lb = d1.getLowerBound();
                // ub = d.getUpperBound();
                removed += v.bound(lb, ub);
            } else if (a < 0) {
                IntegerDomain d1 = linearSum.getDomainExcept(v).div(- a);
                // lb = d.getLowerBound();
                ub = d1.getUpperBound();
                removed += v.bound(lb, ub);
            }
        }
        return removed;
    }

    /* (non-Javadoc)
     * @see Literal#isSatisfied()
     */
    @Override
    public boolean isSatisfied() {
        return linearSum.getValue() == 0;
    }    
    
    @Override
    public Literal neg() throws SugarException {
        // !(linearSum >= 0) --> linearSum+1 <= 0
        LinearSum newLinearSum = new LinearSum(linearSum);
        newLinearSum.add(LinearSum.ONE);
        return new LinearLeLiteral(newLinearSum);
    }
}

package jp.kobe_u.sugar.csp;

import java.io.IOException;
import java.util.Iterator;
import java.util.Set;

import jp.kobe_u.sugar.SugarConstants;
import jp.kobe_u.sugar.SugarException;
import jp.kobe_u.sugar.SugarMain;
import jp.kobe_u.sugar.encoder.Encoding;
import jp.kobe_u.sugar.encoder.Problem;

/**
 * This class implements a comparison literal of CSP.
 * The comparison represents the condition "linearSum &lt;= 0".
 * @see CSP
 * @see LinearSum
 * @author Naoyuki Tamura (tamura@kobe-u.ac.jp)
 */
public class LinearLeLiteral extends LinearLiteral {

    /**
     * Constructs a new comparison literal of given linear expression.
     * @param linearSum the linear expression
     */
    public LinearLeLiteral(LinearSum linearSum) {
        super(linearSum, "le");
    }
    
    @Override
    public int[] getBound(IntegerVariable v) throws SugarException {
        int a = linearSum.getA(v);
        int lb = v.getDomain().getLowerBound();
        int ub = v.getDomain().getUpperBound();
        if (a != 0) {
            IntegerDomain d = linearSum.getDomainExcept(v);
            d = d.neg();
            int b = d.getUpperBound();
            if (a >= 0) {
                // ub = (int)Math.floor((double)b / a);
                if (b >= 0) {
                    ub = b/a;
                } else {
                    ub = (b-a+1)/a;
                }
            } else {
                // lb = (int)Math.ceil((double)b / a);
                if (b >= 0) {
                    lb = b/a;
                } else {
                    lb = (b+a+1)/a;
                }
            }
        }
        if (lb > ub)
            return null;
        return new int[] { lb, ub };
    }

    /**
     * Returns true when the linear expression is simple.
     * @return true when the linear expression is simple
     * @see LinearSum#isSimple()
     */
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
    
    /**
     * Returns the linear expression of the comparison literal.
     * @return the linear expression
     */
    public LinearSum getLinearExpression() {
        return linearSum;
    }
    
    @Override
    public boolean isValid() throws SugarException {
        return linearSum.getDomain().getUpperBound() <= 0;
    }
    
    @Override
    public boolean isUnsatisfiable() throws SugarException {
        return linearSum.getDomain().getLowerBound() > 0;
    }
    
    @Override
    public int propagate() throws SugarException {
        if (linearSum.size() == 0) {
            return 0;
        /*
        } else if (linearSum.size() == 1) {
            int b = linearSum.getB(); 
            IntegerVariable v = linearSum.getCoef().firstKey();
            int a = linearSum.getA(v);
            if (a > 0) {
                int ub = (int)Math.floor(-(double)b/a);
                return v.bound(v.getDomain().getLowerBound(), ub);
            } else {
                int lb = (int)Math.ceil(-(double)b/a);
                return v.bound(lb, v.getDomain().getUpperBound());
            }
        */
        }
        int removed = 0;
        for (IntegerVariable v : linearSum.getCoef().keySet()) {
            IntegerDomain d = linearSum.getDomainExcept(v);
            d = d.neg();
            int a = linearSum.getA(v);
            int lb = v.getDomain().getLowerBound();
            int ub = v.getDomain().getUpperBound();
            if (a >= 0) {
                // ub = (int)Math.floor((double)d.getUpperBound()/a);
                int b = d.getUpperBound();
                if (b >= 0) {
                    ub = b/a;
                } else {
                    ub = (b-a+1)/a;
                }
            } else {
                // lb = (int)Math.ceil((double)d.getUpperBound()/a);
                int b = d.getUpperBound();
                if (b >= 0) {
                    lb = b/a;
                } else {
                    lb = (b+a+1)/a;
                }
            }
            // if (lb > v.getDomain().getLowerBound() || ub < v.getDomain().getUpperBound()) {
            if (lb > ub) {
                // XXX debug                
                System.out.println(linearSum);
                for (IntegerVariable v1 : linearSum.getCoef().keySet()) {
                    System.out.println(v1.toString());
                }
                System.out.println(linearSum.getDomainExcept(v).toString());
                // System.out.println(d.toString());
                System.out.println(v.getName() + " " + lb + " " + ub);
                System.out.println("==>");
                removed += v.bound(lb, ub);
                System.out.println(v.toString());
                System.out.println();
            } else {
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
        return linearSum.getValue() <= 0;
    }
    
    @Override
    public Literal neg() throws SugarException {
        // !(linearSum <= 0) --> linearSum-1 >= 0
        LinearSum newLinearSum = new LinearSum(linearSum);
        newLinearSum.subtract(LinearSum.ONE);
        return new LinearGeLiteral(newLinearSum);
    }
}

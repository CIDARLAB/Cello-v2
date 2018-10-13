package jp.kobe_u.sugar.csp;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import jp.kobe_u.sugar.SugarConstants;
import jp.kobe_u.sugar.SugarException;
import jp.kobe_u.sugar.expression.Expression;

/**
 * A class for linear expressions.
 * A linear expression represents the following formula:<br>
 * a0*x0+a1*x1+...+an*xn+b<br>
 * where ai's and b are integer constants and xi's are integer variables
 * of CSP.
 * @see CSP
 * @see LinearLeLiteral
 * @author Naoyuki Tamura
 */
public class LinearSum {
    public static LinearSum ONE = new LinearSum(1);
    
    private int b;
    private SortedMap<IntegerVariable,Integer> coef = new TreeMap<IntegerVariable,Integer>();
    private IntegerDomain domain = null;

    public LinearSum(int b) {
        coef = new TreeMap<IntegerVariable,Integer>();
        this.b = b;
    }

    public LinearSum(int a0, IntegerVariable v0, int b) {
        this(b);
        coef.put(v0, a0);
    }

    public LinearSum(IntegerVariable v0) {
        this(1, v0, 0);
    }

    public LinearSum(LinearSum e) {
        b = e.b;
        coef = new TreeMap<IntegerVariable,Integer>(e.coef);
        domain = null;
    }

    /**
     * Returns the size of the linear expression. 
     * @return the size
     */
    public int size() {
        return coef.size();
    }
    
    public int getB() {
        return b;
    }

    public void setB(int b) {
        this.b = b;
    }
    
    public SortedMap<IntegerVariable,Integer> getCoef() {
        return coef;
    }

    public Set<IntegerVariable> getVariables() {
        return coef.keySet();
    }

    public boolean isIntegerVariable() {
        return b == 0 && size() == 1 && getA(coef.firstKey()) == 1;  
    }
    
    /**
     * Returns true when the linear expression is simple.
     * A linear expression is simple if it has at most one integer variable.
     * @return true when the linear expression is simple
     */
    public boolean isSimple() {
        return coef.size() <= 1;
    }

    public Integer getA(IntegerVariable v) {
        Integer a = coef.get(v);
        if (a == null) {
            a = 0;
        }
        return a;
    }
    
    public void setA(int a, IntegerVariable v) {
        if (a == 0) {
            coef.remove(v);
        } else {
            coef.put(v, a);
        }
        domain = null;
    }

    public boolean isDomainLargerThan(long limit) {
        long size = 1;
        for (IntegerVariable v : coef.keySet()) {
            size *= v.getDomain().size();
            if (size > limit)
                return true;
        }
        return false;
    }
    
    public boolean isDomainLargerThanExcept(long limit, IntegerVariable v) {
        long size = 1;
        for (IntegerVariable v0 : coef.keySet()) {
            if (v0.equals(v))
                continue;
            size *= v0.getDomain().size();
            if (size > limit)
                return true;
        }
        return false;
    }
    
    public boolean isDomainLargerThanExcept(long limit) {
        IntegerVariable v = getLargestDomainVariable();
        return isDomainLargerThanExcept(limit, v);
    }
    
    public boolean isModified() {
        for (IntegerVariable v : coef.keySet()) {
            if (v.isModified())
                return true;
        }
        return false;
    }
    
    /**
     * Adds the given linear expression.
     * @param linearLe the linear expression to be added.
     */
    public void add(LinearSum linearLe) {
        b += linearLe.b;
        for (IntegerVariable v : linearLe.coef.keySet()) {
            int a = getA(v) + linearLe.getA(v);
            setA(a, v);
        }
        domain = null;
    }

    /**
     * Subtracts the given linear expression.
     * @param linearLe the linear expression to be subtracted.
     */
    public void subtract(LinearSum linearLe) {
        b -= linearLe.b;
        for (IntegerVariable v : linearLe.coef.keySet()) {
            int a = getA(v) - linearLe.getA(v);
            setA(a, v);
        }
        domain = null;
    }

    /**
     * Multiplies the given constant.
     * @param c the constant to be multiplied by
     */
    public void multiply(int c) {
        b *= c;
        for (IntegerVariable v : coef.keySet()) {
            int a = c * getA(v);
            setA(a, v);
        }
        domain = null;
    }

    public void divide(int c) {
        b /= c;
        for (IntegerVariable v : coef.keySet()) {
            int a = getA(v) / c;
            setA(a, v);
        }
        domain = null;
    }

    private int gcd(int p, int q) {
        while (true) {
            int r = p % q;
            if (r == 0)
                break;
            p = q;
            q = r;
        }
        return q;
    }
    
    public int factor() {
        if (size() == 0) {
            return b == 0 ? 1 : Math.abs(b);
        }
        int gcd = Math.abs(getA(coef.firstKey()));
        for (IntegerVariable v : coef.keySet()) {
            gcd = gcd(gcd, Math.abs(getA(v)));
            if (gcd == 1)
                break;
        }
        if (b != 0) {
            gcd = gcd(gcd, Math.abs(b));
        }
        return gcd;
    }

    public void factorize() {
        int factor = factor();
        if (factor > 1) {
            divide(factor);
        }
    }
    
    public int coefGCD() {
        if (size() == 0) {
            return 1;
        }
        int gcd = Math.abs(getA(coef.firstKey()));
        for (IntegerVariable v : coef.keySet()) {
            gcd = gcd(gcd, Math.abs(getA(v)));
            if (gcd == 1)
                break;
        }
        return gcd;
    }

    public IntegerDomain getDomain() throws SugarException {
        if (domain == null || isModified()) {
            domain = IntegerDomain.create(b, b);
            for (IntegerVariable v : coef.keySet()) {
                int a = getA(v);
                domain = domain.add(v.getDomain().mul(a));
            }
        }
        return domain;
    }
    
    public IntegerDomain getDomainExcept(IntegerVariable v) throws SugarException {
        // Re-calculation is needed since variable domains might be modified. 
        IntegerDomain d = IntegerDomain.create(b, b);
        for (IntegerVariable v1 : coef.keySet()) {
            if (! v1.equals(v)) {
                int a = getA(v1);
                d = d.add(v1.getDomain().mul(a));
            }
        }
        return d;
    }

    public LinearSum[] split(int m) {
        if (true) {
            LinearSum[] es = new LinearSum[m];
            for (int i = 0; i < m; i++) {
                es[i] = new LinearSum(0);
            }
            IntegerVariable[] vs = getVariablesSorted();
            for (int i = 0; i < vs.length; i++) {
                IntegerVariable v = vs[i];
                es[i % m].setA(getA(v), v);
            }
            return es;
        } else {
            LinearSum e1 = new LinearSum(0);
            LinearSum e2 = new LinearSum(0);
            if (false) {
                IntegerVariable[] vs = getVariablesSorted();
                int n2 = vs.length - 2;
                int i = 0;
                for (IntegerVariable v : vs) {
                    if (i < n2) {
                        e1.setA(getA(v), v);
                    } else {
                        e2.setA(getA(v), v);
                    }
                    i++;
                }
            } else if (true) {
                int i = 0;
                for (IntegerVariable v : coef.keySet()) {
                    if (i % 2 == 0) {
                        e1.setA(getA(v), v);
                    } else {
                        e2.setA(getA(v), v);
                    }
                    i++;
                }
            } else {
                int i = 0;
                int n2 = coef.size() / 2;
                for (IntegerVariable v : coef.keySet()) {
                    if (i < n2) {
                        e1.setA(getA(v), v);
                    } else {
                        e2.setA(getA(v), v);
                    }
                    i++;
                }
            }
            return new LinearSum[] { e1, e2 };
        }
    }

    public boolean containsPbPart() {
        for (IntegerVariable v : coef.keySet()) {
            if (v.isPbEncoding())
                return true;
        }
        return false;
    }
    
    public LinearSum[] splitPbPart() {
        if (! containsPbPart())
            return new LinearSum[] { this };
        LinearSum e1 = new LinearSum(0); 
        LinearSum e2 = new LinearSum(b); 
        for (IntegerVariable v : coef.keySet()) {
            if (v.isPbEncoding())
                e2.setA(coef.get(v), v);
            else
                e1.setA(coef.get(v), v);
        }
        return new LinearSum[] { e1, e2 };
    }
    
    public IntegerVariable getLargestDomainVariable() {
        IntegerVariable var = null;
        for (IntegerVariable v : coef.keySet()) {
            if (var == null || var.getDomain().size() < v.getDomain().size()) {
                var = v;
            }
        }
        return var;
    }
    
    public IntegerVariable[] getVariablesSorted() {
        int n = coef.size();
        IntegerVariable[] vs = new IntegerVariable[n];
        vs = coef.keySet().toArray(vs);
        Arrays.sort(vs, new Comparator<IntegerVariable>() {
            public int compare(IntegerVariable v1, IntegerVariable v2) {
                if (true) {
                    int s1 = v1.getDomain().size();
                    int s2 = v2.getDomain().size();
                    if (s1 != s2)
                        return s1 < s2 ? -1 : 1;
                }
                int a1 = Math.abs(getA(v1));
                int a2 = Math.abs(getA(v2));
                if (a1 != a2)
                    return a1 > a2 ? -1 : 1;
                return v1.compareTo(v2);
            }
        });
        return vs;
    }
    
    private long calcSatSize(long limit, IntegerVariable[] vs, int i, int s) throws SugarException {
        long size = 0;
        if (i >= vs.length - 1) {
            size = 1;
        } else {
            int lb0 = s;
            int ub0 = s;
            for (int j = i + 1; j < vs.length; j++) {
                int a = getA(vs[j]); 
                if (a > 0) {
                    lb0 += a * vs[j].getDomain().getLowerBound();
                    ub0 += a * vs[j].getDomain().getUpperBound();
                } else {
                    lb0 += a * vs[j].getDomain().getUpperBound();
                    ub0 += a * vs[j].getDomain().getLowerBound();
                }
            }
            int a = getA(vs[i]);
            IntegerDomain domain = vs[i].getDomain();
            int lb = domain.getLowerBound();
            int ub = domain.getUpperBound();
            if (a >= 0) {
                // ub = Math.min(ub, (int)Math.floor(-(double)lb0 / a));
                if (-lb0 >= 0) {
                    ub = Math.min(ub, -lb0/a);
                } else {
                    ub = Math.min(ub, (-lb0-a+1)/a);
                }
                Iterator<Integer> iter = domain.values(lb, ub); 
                while (iter.hasNext()) {
                    int c = iter.next();
                    // vs[i]>=c -> ...
                    // encoder.writeComment(vs[i].getName() + " <= " + (c-1));
                    size += calcSatSize(limit, vs, i+1, s+a*c);
                    if (size > limit) {
                        return size;
                    }
                }
                size += calcSatSize(limit, vs, i+1, s+a*(ub+1));
                if (size > limit) {
                    return size;
                }
            } else {
                // lb = Math.max(lb, (int)Math.ceil(-(double)lb0/a));
                if (-lb0 >= 0) {
                    lb = Math.max(lb, -lb0/a);
                } else {
                    lb = Math.max(lb, (-lb0+a+1)/a);
                }
                size += calcSatSize(limit, vs, i+1, s+a*(lb-1));
                if (size > limit) {
                    return size;
                }
                Iterator<Integer> iter = domain.values(lb, ub); 
                while (iter.hasNext()) {
                    int c = iter.next();
                    // vs[i]<=c -> ...
                    size += calcSatSize(limit, vs, i+1, s+a*c);
                    if (size > limit) {
                        return size;
                    }
                }
            }
        }
        return size;
    }
    
    public boolean satSizeLE(long limit) throws SugarException {
        if (isSimple()) {
            return 1 <= limit;
        } else {
            IntegerVariable[] vs = getVariablesSorted();
            long size = calcSatSize(limit, vs, 0, getB());
            return size <= limit;
        }
    }
    
    public Expression toExpression() {
        Expression x = Expression.create(b);
        for (IntegerVariable v : coef.keySet()) {
            Expression ax = Expression.create(getA(v));
            Expression vx = Expression.create(v.getName());
            x = x.add(ax.mul(vx));
        }
        return x;
    }
    
    /**
     * Returns the value of the linear expression.
     * @return the value of the linear expression
     */
    public int getValue() {
        int value = b;
        for (IntegerVariable v : coef.keySet()) {
            value += getA(v) * v.getValue();
        }
        return value;
    }

    /**
     * Returns true when the linear expression is equal to the given linear expression.
     * @param linearLe the linear expression to be compared
     * @return true when the linear expression is equal to the given linear expression
     */
    public boolean equals(LinearSum linearLe) {
        if (linearLe == null)
            return false;
        if (this == linearLe)
            return true;
        return b == linearLe.b && coef.equals(linearLe.coef);
    }
    
    /**
     * Returns true when the linear expression is equal to the given object.
     * @param obj the object to be compared
     * @return true when the linear expression is equal to the given object.
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        return equals((LinearSum)obj);
    }

    /**
     * Returns the hash code of the linear expression.
     * @return the hash code
     */
    @Override
    public int hashCode() {
        final int PRIME = 31;
        int result = 1;
        result = PRIME * result + ((coef == null) ? 0 : coef.hashCode());
        result = PRIME * result + b;
        return result;
    }

    /**
     * Returns the string representation of the linear expression.
     * @return the string representation
     */
    public String toString0() {
        StringBuilder sb = new StringBuilder();
        sb.append("(");
        sb.append(SugarConstants.ADD);
        for (IntegerVariable v : coef.keySet()) {
            sb.append(" (");
            sb.append(SugarConstants.MUL);
            sb.append(" ");
            sb.append(coef.get(v));
            sb.append(" ");
            sb.append(v.getName());
            sb.append(")");
        }
        sb.append(" ");
        sb.append(b);
        sb.append(")");
        return sb.toString();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (IntegerVariable v : coef.keySet()) {
            int c = getA(v);
            if (c == 0) {
            } else if (c > 0) {
                if (sb.length() > 0) {
                    sb.append("+");
                }
                if (c != 1) {
                    sb.append(c);
                    sb.append("*");
                }
                sb.append(v.getName());
            } else {
                if (c == -1) {
                    sb.append("-");
                } else {
                    sb.append(c);
                    sb.append("*");
                }
                sb.append(v.getName());
            }
        }
        if (sb.length() == 0) {
            sb.append(b);
        } else {
            if (b >= 0) {
                sb.append("+");
            }
            sb.append(b);
        }
        return sb.toString();
    }
}

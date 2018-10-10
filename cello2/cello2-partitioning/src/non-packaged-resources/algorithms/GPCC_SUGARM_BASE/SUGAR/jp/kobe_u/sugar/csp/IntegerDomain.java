package jp.kobe_u.sugar.csp;
 
import java.util.Iterator;
import java.util.SortedSet;

import jp.kobe_u.sugar.SugarException;

/**
 * This is an abstract class of integer domains.
 * @see CSP
 * @author Naoyuki Tamura (tamura@kobe-u.ac.jp)
 */
public abstract class IntegerDomain {
    public static boolean USE_DIET_DOMAIN = false;
    
    public static IntegerDomain create(int lb, int ub) throws SugarException {
        if (USE_DIET_DOMAIN)
            return new IntegerDomainDiet(lb, ub);
        else
            return new IntegerDomainIntevals(lb, ub);
    }

    public static IntegerDomain create(SortedSet<Integer> domain) {
        if (USE_DIET_DOMAIN)
            return new IntegerDomainDiet(domain);
        else
            return new IntegerDomainIntevals(domain);
    }

    public boolean isEmpty() {
        return size() == 0;
    }

    public abstract int size();

    public abstract int sizeLE(int value);

    public abstract boolean contains(int value);

    public abstract int getLowerBound();

    public abstract int getUpperBound();

    public abstract Iterator<int[]> intervals();

    public abstract Iterator<Integer> values(int lb, int ub) throws SugarException;

    public abstract Iterator<Integer> values() throws SugarException;

    public abstract IntegerDomain bound(int lb, int ub) throws SugarException;

    public abstract IntegerDomain cap(IntegerDomain domain) throws SugarException;
    
    public abstract IntegerDomain cup(IntegerDomain domain) throws SugarException;
    
    public abstract IntegerDomain neg() throws SugarException;

    public abstract IntegerDomain abs() throws SugarException;

    public abstract IntegerDomain add(IntegerDomain domain) throws SugarException;

    public abstract IntegerDomain sub(IntegerDomain domain) throws SugarException;

    public abstract IntegerDomain mul(int a) throws SugarException;

    public abstract IntegerDomain mul(IntegerDomain domain) throws SugarException;

    public abstract IntegerDomain div(int a) throws SugarException;

    public abstract IntegerDomain div(IntegerDomain domain) throws SugarException;

    public abstract IntegerDomain mod(IntegerDomain domain) throws SugarException;

    public abstract IntegerDomain min(IntegerDomain domain) throws SugarException;
    
    public abstract IntegerDomain max(IntegerDomain domain) throws SugarException;

    private String rangeValues(int lb, int ub, boolean useDots) {
        if (useDots)
            return lb + ".." + ub;
        else
            return "(" + lb + " " + ub + ")";
    }
    
    public void appendValues(StringBuilder sb, boolean useDots) {
        Iterator<int[]> iter = intervals();
        String delim = "";
        while (iter.hasNext()) {
            int[] interval = iter.next();
            sb.append(delim);
            if (interval[0] == interval[1])
                sb.append(interval[0]);
            else
                sb.append(rangeValues(interval[0], interval[1], useDots));
            delim = " ";
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("(");
        appendValues(sb, false);
        sb.append(")");
        return sb.toString();
    }

}

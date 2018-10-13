package jp.kobe_u.sugar.csp;
 
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import jp.kobe_u.sugar.SugarException;

/**
 * This class implements an integer domain class.
 * @see CSP
 * @author Naoyuki Tamura (tamura@kobe-u.ac.jp)
 */
public class IntegerDomainIntevals extends IntegerDomain {
    public static int MAX_SET_SIZE = 128;
    // public static int MAX_SET_SIZE = 256;
    private int lb;
    private int ub;
    private SortedSet<Integer> domain;

    private static IntegerDomainIntevals _create(SortedSet<Integer> domain) throws SugarException {
        int lb = domain.first();
        int ub = domain.last();
        if (domain.size() <= MAX_SET_SIZE) {
            boolean sparse = false;
            for (int value = lb; value <= ub; value++) {
                if (! domain.contains(value)) {
                    sparse = true;
                    break;
                }
            }
            if (! sparse) {
                domain = null;
            }
        } else {
            domain = null;
        }
        if (domain == null) {
            return new IntegerDomainIntevals(lb, ub); 
        }
        return new IntegerDomainIntevals(domain);
    }
    
    public IntegerDomainIntevals(int lb, int ub) throws SugarException {
        if (lb > ub) {
            throw new SugarException("Illegal domain instantiation " + lb + " " + ub);
        }
        this.lb = lb;
        this.ub = ub;
        domain = null;
    }

    public IntegerDomainIntevals(SortedSet<Integer> domain) {
        lb = domain.first();
        ub = domain.last();
        this.domain = domain;
    }

    public IntegerDomainIntevals(IntegerDomainIntevals d) {
        lb = d.lb;
        ub = d.ub;
        domain = null;
        if (d.domain != null) {
            domain = new TreeSet<Integer>(d.domain);
        }
    }
    
    public int size() {
        if (domain == null) {
            return lb <= ub ? ub - lb + 1 : 0; 
        } else {
            return domain.size();
        }
    }

    private boolean isContiguous() {
        return domain == null;
    }
    
    public int getLowerBound() {
        if (domain == null) {
            return lb;
        } else {
            return domain.first();
        }
    }

    public int getUpperBound() {
        if (domain == null) {
            return ub;
        } else {
            return domain.last();
        }
    }

    private SortedSet<Integer> getSet() {
        return domain;
    }
    
    public boolean contains(int value) {
        if (domain == null) {
            return lb <= value && value <= ub;
        } else {
            return domain.contains(value);
        }
    }

    public int sizeLE(int value) {
        if (value < lb)
            return 0;
        if (value >= ub)
            return size();
        if (domain == null) {
            return value - lb + 1;
        } else {
            return domain.headSet(value + 1).size();
        }
    }

    public IntegerDomain bound(int lb0, int ub0) throws SugarException {
        int lb = getLowerBound();
        int ub = getUpperBound();
        if (lb0 <= lb && ub <= ub0)
            return this;
        lb0 = Math.max(lb, lb0);
        ub0 = Math.min(ub, ub0);
        if (domain == null) {
            return new IntegerDomainIntevals(lb0, ub0);
        } else {
            // System.out.println("## " + lb0 + " " + ub0 + " " + domain);
            return new IntegerDomainIntevals(domain.subSet(lb0, ub0 + 1));
        }
    }

    private class Iter implements Iterator<Integer> {
        int value;
        int ub;
        
        public Iter(int lb, int ub) {
            value = lb;
            this.ub = ub;
        }
        
        public boolean hasNext() {
            return value <= ub;
        }

        public Integer next() {
            return value++;
        }

        public void remove() {
        }

    }
    
    public Iterator<Integer> values(int lb, int ub) {
        if (lb > ub) {
            return new Iter(lb, ub);
        } else if (domain == null) {
                lb = Math.max(lb, this.lb);
                ub = Math.min(ub, this.ub);
                return new Iter(lb, ub);
        } else {
            return domain.subSet(lb, ub + 1).iterator();
        }
    }
    
    public Iterator<Integer> values() {
        return values(lb, ub);
    }
    
    public Iterator<int[]> intervals() {
        List<int[]> intervals = new ArrayList<int[]>();
        if (isContiguous()) {
            intervals.add(new int[] { getLowerBound(), getUpperBound() });
        } else {
            int value0 = Integer.MIN_VALUE;
            int value1 = Integer.MIN_VALUE;
            for (int value : getSet()) {
                if (value0 == Integer.MIN_VALUE) {
                    value0 = value1 = value;
                } else if (value1 + 1 == value) {
                    value1 = value;
                } else {
                    intervals.add(new int[] { value0, value1 });
                    value0 = value1 = value;
                }
            }
            if (value0 != Integer.MIN_VALUE) {
                intervals.add(new int[] { value0, value1 });
            }
        }
        return intervals.iterator();
    }
    
    public IntegerDomain cap(IntegerDomainIntevals d1) throws SugarException {
        if (d1.domain == null) {
            return bound(d1.lb, d1.ub); 
        } else if (domain == null) {
            return d1.bound(lb, ub);
        } else {
            SortedSet<Integer> d = new TreeSet<Integer>();
            for (int value : domain) {
                if (d1.contains(value)) {
                    d.add(value);
                }
            }
            return new IntegerDomainIntevals(d);
        }
    }

    public IntegerDomain cup(IntegerDomainIntevals d1) throws SugarException {
        if (domain == null || d1.domain == null) {
            if (size() == 1 && d1.size() == 1) {
                SortedSet<Integer> d = new TreeSet<Integer>();
                d.add(lb);
                d.add(d1.lb);
                return _create(d);
            }
            int lb = Math.min(this.lb, d1.lb);
            int ub = Math.max(this.ub, d1.ub);
            return new IntegerDomainIntevals(lb, ub);
        } else {
            SortedSet<Integer> d = new TreeSet<Integer>(domain);
            d.addAll(d1.domain);
            // return new IntegerDomain(d);
            return _create(d);
        }
    }

    public IntegerDomain neg() throws SugarException {
        if (domain == null) {
            return new IntegerDomainIntevals(-ub, -lb);
        } else {
            SortedSet<Integer> d = new TreeSet<Integer>();
            for (int value : domain) {
                d.add(-value);
            }
            // return new IntegerDomain(d);
            return _create(d);
        }
    }

    public IntegerDomain abs() throws SugarException {
        if (domain == null) {
            int lb0 = Math.min(Math.abs(lb), Math.abs(ub));
            int ub0 = Math.max(Math.abs(lb), Math.abs(ub));
            if (lb <= 0 && 0 <= ub) {
                return new IntegerDomainIntevals(0, ub0);
            } else {
                return new IntegerDomainIntevals(lb0, ub0);
            }
        } else {
            SortedSet<Integer> d = new TreeSet<Integer>();
            for (int value : domain) {
                d.add(Math.abs(value));
            }
            // return new IntegerDomain(d);
            return _create(d);
        }
    }
    
    private IntegerDomain add(int a) throws SugarException {
        if (domain == null) {
            return new IntegerDomainIntevals(lb+a, ub+a);
        } else {
            SortedSet<Integer> d = new TreeSet<Integer>();
            for (int value : domain) {
                d.add(value + a);
            }
            // return new IntegerDomain(d);
            return _create(d);
        }
    }

    public IntegerDomain add(IntegerDomainIntevals d) throws SugarException {
        if (d.size() == 1) {
            return add(d.lb);
        } else  if (size() == 1) {
            return d.add(lb);
        }
        if (domain == null || d.domain == null) {
            int lb0 = lb + d.lb;
            int ub0 = ub + d.ub;
            return new IntegerDomainIntevals(lb0, ub0);
        } else {
            SortedSet<Integer> d0 = new TreeSet<Integer>();
            for (int value1 : domain) {
                for (int value2 : d.domain) {
                    d0.add(value1 + value2);
                }
            }
            // return new IntegerDomain(d0);
            return _create(d0);
        }
    }

    private IntegerDomain sub(int a) throws SugarException {
        return add(-a);
    }

    public IntegerDomain sub(IntegerDomainIntevals d) throws SugarException {
        return add(d.neg());
    }

    public IntegerDomain mul(int a) throws SugarException {
        if (domain == null) {
            // TODO domain calculation for multiplication
            if (false && size() <= MAX_SET_SIZE) {
                SortedSet<Integer> d = new TreeSet<Integer>();
                for (int value = lb; value <= ub; value++) {
                    d.add(value * a);
                }
                return _create(d);
            } else if (a < 0) {
                return new IntegerDomainIntevals(ub*a, lb*a);
            } else {
                return new IntegerDomainIntevals(lb*a, ub*a);
            }
        } else {
            SortedSet<Integer> d = new TreeSet<Integer>();
            for (int value : domain) {
                d.add(value * a);
            }
            return _create(d);
        }
    }

    public IntegerDomain mul(IntegerDomainIntevals d) throws SugarException {
        if (d.size() == 1) {
            return mul(d.lb);
        } else  if (size() == 1) {
            return d.mul(lb);
        }
        if (domain == null || d.domain == null
                || size() * d.size() > MAX_SET_SIZE) {
            int b00 = lb * d.lb;
            int b01 = lb * d.ub;
            int b10 = ub * d.lb;
            int b11 = ub * d.ub;
            int lb0 = Math.min(Math.min(b00, b01), Math.min(b10, b11));
            int ub0 = Math.max(Math.max(b00, b01), Math.max(b10, b11));
            return new IntegerDomainIntevals(lb0, ub0);
        } else {
            SortedSet<Integer> d0 = new TreeSet<Integer>();
            for (int value1 : domain) {
                for (int value2 : d.domain) {
                    d0.add(value1 * value2);
                }
            }
            return _create(d0);
        }
    }

    private int div(int x, int y) {
        if (x < 0 && x % y != 0) {
            return x / y - 1;
        }
        return x / y;
    }
    
    public IntegerDomain div(int a) throws SugarException {
        if (domain == null) {
            if (a < 0) {
                return new IntegerDomainIntevals(div(ub,a), div(lb,a));
            } else {
                return new IntegerDomainIntevals(div(lb,a), div(ub,a));
            }
        } else {
            SortedSet<Integer> d = new TreeSet<Integer>();
            for (int value : domain) {
                d.add(div(value, a));
            }
            return _create(d);
        }
    }

    public IntegerDomain div(IntegerDomainIntevals d) throws SugarException {
        if (d.size() == 1) {
            return div(d.lb);
        }
        if (domain == null || d.domain == null
                || size() * d.size() > MAX_SET_SIZE) {
            int b00 = div(lb, d.lb);
            int b01 = div(lb, d.ub);
            int b10 = div(ub, d.lb);
            int b11 = div(ub, d.ub);
            int lb0 = Math.min(Math.min(b00, b01), Math.min(b10, b11));
            int ub0 = Math.max(Math.max(b00, b01), Math.max(b10, b11));
            if (d.lb <= 1 && 1 <= d.ub) {
                lb0 = Math.min(lb0, Math.min(lb, ub));
                ub0 = Math.max(ub0, Math.max(lb, ub));
            }
            if (d.lb <= -1 && -1 <= d.ub) {
                lb0 = Math.min(lb0, Math.min(-lb, -ub));
                ub0 = Math.max(ub0, Math.max(-lb, -ub));
            }
            return new IntegerDomainIntevals(lb0, ub0);
        } else {
            SortedSet<Integer> d0 = new TreeSet<Integer>();
            for (int value1 : domain) {
                for (int value2 : d.domain) {
                    d0.add(div(value1, value2));
                }
            }
            return _create(d0);
        }
    }

    public IntegerDomain mod(int a) throws SugarException {
        a = Math.abs(a);
        if (domain == null) {
            return new IntegerDomainIntevals(0, a - 1);
        } else {
            SortedSet<Integer> d = new TreeSet<Integer>();
            for (int value : domain) {
                d.add(value % a);
            }
            return _create(d);
        }
    }

    public IntegerDomain mod(IntegerDomainIntevals d) throws SugarException {
        if (d.size() == 1) {
            return mod(d.lb);
        }
        if (domain == null || d.domain == null) {
            int lb0 = 0;
            int ub0 = Math.max(Math.abs(d.lb), Math.abs(d.ub)) - 1;
            return new IntegerDomainIntevals(lb0, ub0);
        } else {
            SortedSet<Integer> d0 = new TreeSet<Integer>();
            for (int value1 : domain) {
                for (int value2 : d.domain) {
                    d0.add(value1 % value2);
                }
            }
            return _create(d0);
        }
    }

    public IntegerDomain pow(int a) throws SugarException {
        if (domain == null) {
            int a1 = (int)Math.round(Math.pow(lb, a));
            int a2 = (int)Math.round(Math.pow(ub, a));
            int lb0 = Math.min(a1, a2);
            int ub0 = Math.max(a1, a2);
            if (a % 2 == 0 && lb <= 0 && 0 <= ub) {
                return new IntegerDomainIntevals(0, ub0);
            } else {
                return new IntegerDomainIntevals(lb0, ub0);
            }
        } else {
            SortedSet<Integer> d = new TreeSet<Integer>();
            for (int value : domain) {
                d.add((int)Math.round(Math.pow(value, a)));
            }
            return _create(d);
        }
    }
    
    public IntegerDomain min(IntegerDomainIntevals d) throws SugarException {
        int lb0 = Math.min(lb, d.lb);
        int ub0 = Math.min(ub, d.ub);
        if (ub <= d.lb) {
            return this;
        } else if (d.ub <= lb) {
            return d;
        } 
        if (domain == null) {
            if (d.domain == null) {
                return new IntegerDomainIntevals(lb0, ub0);
            } else {
                return d.min(this);
            }
        } else {
            if (d.domain == null) {
                return _create(domain.subSet(lb0, ub0 + 1));
            } else {
                SortedSet<Integer> d1 = new TreeSet<Integer>(domain);
                d1.addAll(d.domain);
                d1 = d1.subSet(lb0, ub0 + 1);
                // return new IntegerDomain(d1);
                return _create(d1);
            }
        }
    }

    public IntegerDomain max(IntegerDomainIntevals d) throws SugarException {
        int lb0 = Math.max(lb, d.lb);
        int ub0 = Math.max(ub, d.ub);
        if (lb >= d.ub) {
            return this;
        } else if (d.lb >= ub) {
            return d;
        } 
        if (domain == null) {
            if (d.domain == null) {
                return new IntegerDomainIntevals(lb0, ub0);
            } else {
                return d.max(this);
            }
        } else {
            if (d.domain == null) {
                return _create(domain.subSet(lb0, ub0 + 1));
            } else {
                SortedSet<Integer> d1 = new TreeSet<Integer>(domain);
                d1.addAll(d.domain);
                d1 = d1.subSet(lb0, ub0 + 1);
                // return new IntegerDomain(d1);
                return _create(d1);
            }
        }
    }

    private void checkDomain(IntegerDomain domain) throws SugarException {
        if (! (domain instanceof IntegerDomainIntevals))
            throw new SugarException("Incompatible domain " + this + ", " + domain);
    }
    
    @Override
    public IntegerDomain cup(IntegerDomain domain) throws SugarException {
        checkDomain(domain);
        return cup((IntegerDomainIntevals)domain);
    }

    @Override
    public IntegerDomain cap(IntegerDomain domain) throws SugarException {
        checkDomain(domain);
        return cap((IntegerDomainIntevals)domain);
    }

    @Override
    public IntegerDomain add(IntegerDomain domain) throws SugarException {
        checkDomain(domain);
        return add((IntegerDomainIntevals)domain);
    }

    @Override
    public IntegerDomain sub(IntegerDomain domain) throws SugarException {
        checkDomain(domain);
        return sub((IntegerDomainIntevals)domain);
    }

    @Override
    public IntegerDomain mul(IntegerDomain domain) throws SugarException {
        checkDomain(domain);
        return mul((IntegerDomainIntevals)domain);
    }

    @Override
    public IntegerDomain div(IntegerDomain domain) throws SugarException {
        checkDomain(domain);
        return div((IntegerDomainIntevals)domain);
    }

    @Override
    public IntegerDomain mod(IntegerDomain domain) throws SugarException {
        checkDomain(domain);
        return mod((IntegerDomainIntevals)domain);
    }

    @Override
    public IntegerDomain min(IntegerDomain domain) throws SugarException {
        checkDomain(domain);
        return min((IntegerDomainIntevals)domain);
    }

    @Override
    public IntegerDomain max(IntegerDomain domain) throws SugarException {
        checkDomain(domain);
        return max((IntegerDomainIntevals)domain);
    }
}

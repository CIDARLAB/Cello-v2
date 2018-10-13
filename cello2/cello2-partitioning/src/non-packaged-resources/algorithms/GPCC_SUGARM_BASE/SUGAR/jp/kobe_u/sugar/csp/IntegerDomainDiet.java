package jp.kobe_u.sugar.csp;
 
import java.util.Comparator;
import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;

import jp.kobe_u.sugar.SugarException;

/**
 * This class implements an integer domain class.
 * @see CSP
 * @author Naoyuki Tamura (tamura@kobe-u.ac.jp)
 */
public class IntegerDomainDiet extends IntegerDomain {
    public static int MAX_SET_SIZE = 128;

    private static Comparator DIET_COMPARATOR = new Comparator<int[]>() {
        public int compare(int[] r1, int[] r2) {
            if (r1[1] != r2[1])
                return r1[1] < r2[1] ? -1 : 1;
            if (r1[0] != r2[0])
                return r1[0] < r2[0] ? -1 : 1;
            return 0;
        }
    };
    private static IntegerDomainDiet EMPTY = new IntegerDomainDiet();
    private static IntegerDomainDiet ZERO = new IntegerDomainDiet(0, 0);

    private TreeSet<int[]> treeSet;
    private int size = -1;

    public IntegerDomainDiet() {
        treeSet = new TreeSet<int[]>(DIET_COMPARATOR);
    }
    
    public IntegerDomainDiet(int lb, int ub) {
        this();
        treeSet.add(new int[] { lb, ub });
    }
    public IntegerDomainDiet(SortedSet<Integer> domain) {
        this();
        int value0 = Integer.MIN_VALUE;
        int value1 = Integer.MIN_VALUE;
        for (int value : domain) {
            if (value0 == Integer.MIN_VALUE) {
                value0 = value1 = value;
            } else if (value1 + 1 == value) {
                value1 = value;
            } else {
                if (value0 == value1) {
                    treeSet.add(new int[] { value0, value0 });
                } else {
                    treeSet.add(new int[] { value0, value1 });
                }
                value0 = value1 = value;
            }
        }
        if (value0 != Integer.MIN_VALUE) {
            if (value0 == value1) {
                treeSet.add(new int[] { value0, value0 });
            } else {
                treeSet.add(new int[] { value0, value1 });
            }
        }
    }

    @Override
    public boolean isEmpty() {
        return treeSet.isEmpty();
    }
    
    @Override
    public int size() {
        if (size < 0) {
            size = 0;
            for (int[] r : treeSet)
                size += r[1] - r[0] + 1;
        }
        return size;
    }

    @Override
    public int sizeLE(int value) {
        if (getUpperBound() <= value)
            return size();
        int size = 0;
        for (int[] r : treeSet)
            if (r[0] <= value)
                size += Math.min(r[1], value) - r[0] + 1;
        return size;
    }

    @Override
    public boolean contains(int value) {
        int[] r = treeSet.higher(new int[] { Integer.MIN_VALUE, value });
        return r != null && r[0] <= value;
    }

    @Override
    public int getLowerBound() {
        return treeSet.first()[0];
    }

    @Override
    public int getUpperBound() {
        return treeSet.last()[1];
    }

    @Override
    public Iterator<int[]> intervals() {
        return treeSet.iterator();
    }

    private class IteratorValues implements Iterator<Integer> {
        Iterator<int[]> iter = null;
        int[] r;
        int value;

        public IteratorValues() {
            if (size() > 0) {
                iter = intervals();
                r = iter.next();
                value = r[0];
            }
        }
        
        public boolean hasNext() {
            return iter != null && (value <= r[1] || iter.hasNext());
        }

        public Integer next() {
            if (value > r[1]) {
                r = iter.next();
                value = r[0];
            }
            return value++;
        }

        public void remove() {
        }

    }
    
    @Override
    public Iterator<Integer> values() {
        return new IteratorValues();
    }

    @Override
    public Iterator<Integer> values(int lb, int ub) throws SugarException {
        return bound(lb, ub).values();
    }

    @Override
    public IntegerDomain bound(int lb, int ub) throws SugarException {
        if (lb <= getLowerBound() && getUpperBound() <= ub)
            return this;
        IntegerDomainDiet d = new IntegerDomainDiet();
        int[] r0 = new int[] { Integer.MIN_VALUE, lb };
        for (int[] r : treeSet.tailSet(r0)) {
            if (r[1] < lb)
                ;
            else if (r[0] < lb)
                d.treeSet.add(new int[] { lb, r[1] });
            else if (r[0] > ub)
                break;
            else if (r[1] > ub)
                d.treeSet.add(new int[] { r[0], ub });
            else
                d.treeSet.add(r);
        }
        return d;
    }

    public IntegerDomainDiet complement() throws SugarException {
        IntegerDomainDiet d = new IntegerDomainDiet();
        if (isEmpty()) {
            d.treeSet.add(new int[] { Integer.MIN_VALUE, Integer.MAX_VALUE });
        } else {
            int last = Integer.MIN_VALUE - 1;
            for (int[] r : treeSet) {
                if (r[0] > Integer.MIN_VALUE)
                    d.treeSet.add(new int[] { last+1, r[0]-1 });
                last = r[1];
            }
            if (last < Integer.MAX_VALUE)
                d.treeSet.add(new int[] { last+1, Integer.MAX_VALUE });
        }
        return d;
    }
    
    @Override
    public IntegerDomain cap(IntegerDomain domain) throws SugarException {
        if (isEmpty() || domain.isEmpty())
            return EMPTY;
        IntegerDomainDiet d = new IntegerDomainDiet();
        Iterator<int[]> rs1 = intervals();
        Iterator<int[]> rs2 = domain.intervals();
        int[] r1 = rs1.next();
        int[] r2 = rs2.next();
        while (r1 != null && r2 != null) {
            if (r1[1] < r2[0]) {
                r1 = null;
            } else if (r2[1] < r1[0]) {
                r2 = null;
            } else if (r1[1] < r2[1]) {
                d.treeSet.add(new int[] { Math.max(r1[0], r2[0]), r1[1] });
                r1 = null;
            } else {
                d.treeSet.add(new int[] { Math.max(r1[0], r2[0]), r2[1] });
                r2 = null;
            }
            if (r1 == null && rs1.hasNext())
                r1 = rs1.next();
            if (r2 == null && rs2.hasNext())
                r2 = rs2.next();
        }
        return d;
    }

    @Override
    public IntegerDomain cup(IntegerDomain domain) throws SugarException {
        if (isEmpty())
            return domain;
        if (domain.isEmpty())
            return this;
        IntegerDomainDiet d = new IntegerDomainDiet();
        Iterator<int[]> rs1 = intervals();
        Iterator<int[]> rs2 = domain.intervals();
        int[] r1 = null;
        int[] r2 = null;
        while (true) {
            if (r1 == null && rs1.hasNext())
                r1 = rs1.next();
            if (r2 == null && rs2.hasNext())
                r2 = rs2.next();
            if (r1 == null || r2 == null)
                break;
            if (r1[1] + 1 < r2[0]) {
                d.treeSet.add(r1);
                r1 = null;
            } else if (r2[1] + 1 < r1[0]) {
                d.treeSet.add(r2);
                r2 = null;
            } else if (r1[1] < r2[1]) {
                r2 = new int[] { Math.min(r1[0], r2[0]), r2[1] };
                r1 = null;
            } else {
                r1 = new int[] { Math.min(r1[0], r2[0]), r1[1] };
                r2 = null;
            }
        }
        if (r1 != null) {
            d.treeSet.add(r1);
        }
        if (r2 != null) {
            d.treeSet.add(r2);
        }
        while (rs1.hasNext()) {
            d.treeSet.add(rs1.next());
        }
        while (rs2.hasNext()) {
            d.treeSet.add(rs2.next());
        }
        return d;
    }

    @Override
    public IntegerDomain neg() throws SugarException {
        IntegerDomainDiet d = new IntegerDomainDiet();
        for (int[] r : treeSet)
            d.treeSet.add(new int[] { -r[1], -r[0] });
        return d;
    }

    @Override
    public IntegerDomain abs() throws SugarException {
        IntegerDomain d1 = bound(0, Integer.MAX_VALUE);
        IntegerDomain d2 = bound(Integer.MIN_VALUE, -1).neg();
        return d1.cup(d2);
    }

    private IntegerDomainDiet add(int a) {
        if (isEmpty() || a == 0)
            return this;
        IntegerDomainDiet d = new IntegerDomainDiet();
        Iterator<int[]> rs = intervals();
        while (rs.hasNext()) {
            int[] r = rs.next();
            d.treeSet.add(new int[] { r[0]+a, r[1]+a });
        }
        return d;
    }
    
    private IntegerDomainDiet add(int a, int b) {
        if (isEmpty())
            return EMPTY;
        if (a == b)
            return add(a);
        IntegerDomainDiet d = new IntegerDomainDiet();
        int lb = Integer.MIN_VALUE; 
        int ub = Integer.MIN_VALUE; 
        Iterator<int[]> rs = intervals();
        while (rs.hasNext()) {
            int[] r = rs.next();
            if (r[0] + a <= ub + 1) {
                ub = r[1] + b;
            } else {
                if (ub > Integer.MIN_VALUE)
                    d.treeSet.add(new int[] { lb, ub });
                lb = r[0] + a;
                ub = r[1] + b;
            }
        }
        if (ub > Integer.MIN_VALUE)
            d.treeSet.add(new int[] { lb, ub });
        return d;
    }
    
    @Override
    public IntegerDomain add(IntegerDomain domain) throws SugarException {
        if (isEmpty() || domain.isEmpty())
            return EMPTY;
        if (domain.size() < size())
            return domain.add(this);
        IntegerDomain d = new IntegerDomainDiet();
        Iterator<int[]> rs = domain.intervals();
        while (rs.hasNext()) {
            int[] r = rs.next();
            d = d.cup(add(r[0], r[1]));
        }
        return d;
    }

    @Override
    public IntegerDomain sub(IntegerDomain domain) throws SugarException {
        return add(domain.neg());
    }

    @Override
    public IntegerDomain mul(int a) throws SugarException {
        if (isEmpty())
            return EMPTY;
        if (a == 0)
            return ZERO;
        if (a == 1)
            return this;
        if (a < 0)
            return neg().mul(-a);
        IntegerDomainDiet d;
        if (size() <= MAX_SET_SIZE) {
            d = new IntegerDomainDiet();
            Iterator<Integer> vs = values();
            while (vs.hasNext()) {
                int v = vs.next();
                d.treeSet.add(new int[] { v*a, v*a });
            }
        } else {
            // TODO
            d = new IntegerDomainDiet(getLowerBound()*a, getUpperBound()*a); 
        }
        return d;
    }
    
    @Override
    public IntegerDomain mul(IntegerDomain domain) throws SugarException {
        if (isEmpty() || domain.isEmpty())
            return EMPTY;
        if (size() < domain.size())
            return domain.mul(this);
        IntegerDomain d;
        if (domain.size() <= MAX_SET_SIZE) {
            d = new IntegerDomainDiet();
            Iterator<Integer> vs = domain.values();
            while (vs.hasNext()) {
                int v = vs.next();
                d = d.cup(mul(v));
            }
        } else {
            // TODO
            throw new SugarException("Too large IntegerDomainDiet for mul");
        }
        return d;
    }

    @Override
    public IntegerDomain div(int a) throws SugarException {
        // TODO 
        throw new SugarException("Div is not supported in IntegerDomainDiet");
        // return null;
    }
    
    @Override
    public IntegerDomain div(IntegerDomain domain) throws SugarException {
        // TODO 
        throw new SugarException("Div is not supported in IntegerDomainDiet");
        // return null;
    }

    @Override
    public IntegerDomain mod(IntegerDomain domain) throws SugarException {
        // TODO 
        throw new SugarException("Mod is not supported in IntegerDomainDiet");
        // return null;
    }

    @Override
    public IntegerDomain min(IntegerDomain domain) throws SugarException {
        int ub1 = getUpperBound();
        int ub2 = domain.getUpperBound();
        if (ub1 == ub2)
            return cup(domain);
        if (ub1 < ub2)
            return cup(domain.bound(Integer.MIN_VALUE, ub1));
        else
            return bound(Integer.MIN_VALUE, ub2).cup(domain);
    }

    @Override
    public IntegerDomain max(IntegerDomain domain) throws SugarException {
        int lb1 = getLowerBound();
        int lb2 = domain.getLowerBound();
        if (lb1 == lb2)
            return cup(domain);
        if (lb1 > lb2)
            return cup(domain.bound(lb1, Integer.MAX_VALUE));
        else
            return bound(lb2, Integer.MAX_VALUE).cup(domain);
    }

    public static void main(String[] args) {
        try {
            IntegerDomainDiet d1 = new IntegerDomainDiet();
            d1.treeSet.add(new int[] { 1, 3 });
            d1.treeSet.add(new int[] { 20, 21 });
            System.out.println(d1);
            IntegerDomainDiet d2 = new IntegerDomainDiet();
            d2.treeSet.add(new int[] { 2, 5 });
            d2.treeSet.add(new int[] { 8, 8 });
            System.out.println(d2);
            /*
            for (int lb = 0; lb <= 4; lb++) {
                for (int ub = 4; ub <= 8; ub++) {
                    System.out.println("bound(" + lb + "," + ub + ") = " + d1.bound(lb, ub));
                }
            }
            */
            System.out.println("complement = " + d1.complement());
            System.out.println("complement^2 = " + d1.complement().complement());
            System.out.println("cap = " + d1.cap(d2));
            System.out.println("cup = " + d1.cup(d2));
            System.out.println("add = " + d1.add(d2));
            System.out.println("sub = " + d1.sub(d2));
            System.out.println("mul = " + d1.mul(d2));
            System.out.println("min = " + d1.min(d2));
            System.out.println("max = " + d1.max(d2));
            IntegerDomainDiet d3 = new IntegerDomainDiet();
            d3.treeSet.add(new int[] { -5, -2 });
            d3.treeSet.add(new int[] { 4, 6 });
            System.out.println(d3);
            System.out.println("abs = " + d3.abs());
        } catch (SugarException e) {
            e.printStackTrace();
        }
    }

}

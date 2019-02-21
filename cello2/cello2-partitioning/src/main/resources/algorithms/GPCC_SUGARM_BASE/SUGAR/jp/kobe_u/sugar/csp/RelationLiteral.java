package jp.kobe_u.sugar.csp;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import jp.kobe_u.sugar.SugarException;
import jp.kobe_u.sugar.expression.Expression;

/**
 * This class implements a literal for relations.
 * @see CSP
 * @author Naoyuki Tamura (tamura@kobe-u.ac.jp)
 */
public class RelationLiteral extends Literal {
    public String name;
    public int arity;
    public boolean negative;
    public boolean conflicts;
    public IntegerVariable[] vs;
    public int[][] tuples;
    private HashSet<Tuple> tupleSet;
    private static final int UNDEF = Integer.MIN_VALUE;
    
    private class Tuple {
        public int[] values;
        
        public Tuple(int[] values) {
            this.values = values;
        }

        /* (non-Javadoc)
         * @see java.lang.Object#hashCode()
         */
        @Override
        public int hashCode() {
            final int PRIME = 31;
            int result = 1;
            result = PRIME * result + Arrays.hashCode(values);
            return result;
        }

        /* (non-Javadoc)
         * @see java.lang.Object#equals(java.lang.Object)
         */
        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            final Tuple other = (Tuple) obj;
            if (!Arrays.equals(values, other.values))
                return false;
            return true;
        }
    }
    
    public class Brick {
        public int[] lb;
        public int[] ub;
        
        public Brick(int[] lb, int[] ub) {
            this.lb = lb;
            this.ub = ub;
        }
        
        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("(");
            Expression.appendString(sb, lb);
            sb.append(")-(");
            Expression.appendString(sb, ub);
            sb.append(")");
            return sb.toString();
        }
    }
    
    public RelationLiteral(String name, int arity, boolean negative, boolean conflicts, int[][] tuples, IntegerVariable[] vs) {
        this.name = name;
        this.arity = arity;
        this.negative = negative;
        this.conflicts = conflicts;
        this.vs = vs;
        this.tuples = tuples;
        tupleSet = new HashSet<Tuple>();
        for (int[] tuple : tuples) {
            tupleSet.add(new Tuple(tuple.clone()));
            int[] tuple0 = tuple.clone();
            for (int i = tuple.length - 1; i >= 1; i--) {
                tuple0[i] = UNDEF;
                tupleSet.add(new Tuple(tuple0.clone()));
            }
        }
    }

    private boolean conflicts(Tuple tuple) {
        return ((negative ^ conflicts) && tupleSet.contains(tuple))
        || (! (negative ^ conflicts) && ! tupleSet.contains(tuple));
        
    }
    
    @Override
    public boolean isSimple() {
        return false;
    }

    @Override
    public boolean isValid() throws SugarException {
        // TODO
        return false;
    }

    @Override
    public boolean isUnsatisfiable() throws SugarException {
        // TODO
        return false;
    }

    @Override
    public int propagate() throws SugarException {
        int count = 0;
        // TODO
        return count;
    }

    private boolean contactInside(Brick brick1, Brick brick2, int i) {
        int n = brick1.lb.length;
        for (int j = 0; j < n; j++) {
            if (j != i) {
                if (! (brick2.lb[j] <= brick1.lb[j] && brick1.ub[j] <= brick2.ub[j])) {
                    return false;
                }
            }
        }
        return true;
    }
    
    private List<Brick> combineBricks2(List<Brick> bricks1, List<Brick> bricks2,
            int i, int value1, int value2) {
        List<Brick> bricks = new ArrayList<Brick>();
        int j = 0;
        while (j < bricks1.size()) {
            Brick brick = bricks1.get(j);
            if (brick.ub[i] == value1) {
                j++;
            } else {
                bricks.add(brick);
                bricks1.remove(j);
            }
        }
        j = 0;
        while (j < bricks2.size()) {
            Brick brick = bricks2.get(j);
            if (brick.lb[i] == value2) {
                j++;
            } else {
                bricks.add(brick);
                bricks2.remove(j);
            }
        }
        for (Brick brick1 : bricks1) {
            int j2 = 0;
            while (j2 < bricks2.size()) {
                Brick brick2 = bricks2.get(j2);
                if (contactInside(brick1, brick2, i)) {
                    int[] ub = brick1.ub.clone();
                    ub[i] = brick2.ub[i];
                    brick1.ub = ub;
                    if (! contactInside(brick2, brick1, i)) {
                        bricks.add(brick2);
                    }
                    bricks2.remove(j2);
                    break;
                } else if (contactInside(brick2, brick1, i)) {
                    int[] lb = brick2.lb.clone();
                    lb[i] = brick1.lb[i];
                    brick2.lb = lb;
                    bricks.add(brick2);
                    bricks2.remove(j2);
                    break;
                } else {
                    j2++;
                }
            }
        }
        bricks.addAll(bricks1);
        bricks.addAll(bricks2);
        return bricks;
    }
    
    private List<Brick> combineBricks(int i, List<Integer> values, Tuple tuple) throws SugarException {
        List<Brick> bricks = null;
        if (i == vs.length - 1) {
            bricks = new ArrayList<Brick>();
            Iterator<Integer> iter = vs[i].getDomain().values();
            int lb[] = null;
            int ub[] = null;
            while (iter.hasNext()) {
                int value = iter.next();
                tuple.values[i] = value;
                if (conflicts(tuple)) {
                    int[] point = tuple.values.clone();
                    if (lb == null) {
                        lb = ub = point;
                    } else {
                        ub = point;
                    }
                } else {
                    if (lb != null) {
                        bricks.add(new Brick(lb, ub));
                    }
                    lb = ub = null;
                }
            }
            if (lb != null) {
                bricks.add(new Brick(lb, ub));
            }
        } else {
            if (values == null) {
                values = new ArrayList<Integer>();
                Iterator<Integer> iter = vs[i].getDomain().values();
                while (iter.hasNext()) {
                    int value = iter.next();
                    values.add(value);
                }
            }
            int size = values.size();
            if (size == 1) {
                tuple.values[i] = values.get(0);
                for (int j = i + 1; j < tuple.values.length; j++) {
                    tuple.values[j] = UNDEF;
                }
                if (negative ^ conflicts) {
                    if (tupleSet.contains(tuple)) {
                        bricks = combineBricks(i + 1, null, tuple);
                    } else {
                        bricks = new ArrayList<Brick>();
                    }
                } else {
                    if (tupleSet.contains(tuple)) {
                        bricks = combineBricks(i + 1, null, tuple);
                    } else {
                        bricks = new ArrayList<Brick>();
                        int[] lb = tuple.values.clone();
                        int[] ub = tuple.values.clone();
                        for (int j = i + 1; j < vs.length; j++) {
                            lb[j] = vs[j].getDomain().getLowerBound();
                            ub[j] = vs[j].getDomain().getUpperBound();
                        }
                        bricks.add(new Brick(lb, ub));
                    }
                }
            } else {
                int m = size / 2;
                List<Brick> bricks1 = combineBricks(i, values.subList(0, m), tuple);
                List<Brick> bricks2 = combineBricks(i, values.subList(m, size), tuple);
                int value1 = values.get(m - 1);
                int value2 = values.get(m);
                bricks = combineBricks2(bricks1, bricks2, i, value1, value2);
            }
        }
        return bricks;
    }
    
    public List<Brick> getConflictBricks() throws SugarException {
        Tuple tuple = new Tuple(new int[vs.length]);
        List<Brick> bricks = combineBricks(0, null, tuple);
        return bricks;
    }
    
    @Override
    public boolean isSatisfied() {
        int[] point = new int[vs.length];
        for (int i = 0; i < vs.length; i++) {
            point[i] = vs[i].getValue();
        }
        return conflicts(new Tuple(point));
    }

    @Override
    public Literal neg() throws SugarException {
        return new RelationLiteral(name, arity, ! negative, conflicts, tuples, vs);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + arity;
        result = prime * result + (conflicts ? 1231 : 1237);
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + (negative ? 1231 : 1237);
        result = prime * result
                + ((tupleSet == null) ? 0 : tupleSet.hashCode());
        result = prime * result + Arrays.hashCode(vs);
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
        RelationLiteral other = (RelationLiteral) obj;
        if (arity != other.arity)
            return false;
        if (conflicts != other.conflicts)
            return false;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        if (negative != other.negative)
            return false;
        if (tupleSet == null) {
            if (other.tupleSet != null)
                return false;
        } else if (!tupleSet.equals(other.tupleSet))
            return false;
        if (!Arrays.equals(vs, other.vs))
            return false;
        return true;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (negative) {
            sb.append("(" + Expression.NOT + " ");
            sb.append("(" + name + " ");
            Expression.appendString(sb, vs);
            sb.append("))");
        } else {
            sb.append("(" + name + " ");
            Expression.appendString(sb, vs);
            sb.append(")");
        }
        return sb.toString();
        /*
        sb.append("(" + Expression.RELATION_DEFINITION + " (");
        Expression.appendString(sb, vs);
        sb.append(") (");
        sb.append(conflicts ? Expression.CONFLICTS : Expression.SUPPORTS);
        for (Tuple tuple : tupleSet) {
            boolean plane = false;
            for (int x : tuple.values) {
                if (x == UNDEF) {
                    plane = true;
                    break;
                }
            }
            if (! plane) {
                sb.append(" (");
                Expression.appendString(sb, tuple.values);
                sb.append(")");
            }
        }
        sb.append("))");
        return sb.toString();
        */
    }
}

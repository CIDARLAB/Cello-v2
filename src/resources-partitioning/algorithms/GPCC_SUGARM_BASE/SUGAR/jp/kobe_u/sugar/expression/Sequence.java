package jp.kobe_u.sugar.expression;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import jp.kobe_u.sugar.SugarException;

/**
 * This class implements sequence expressions.
 * @author Naoyuki Tamura (tamura@kobe-u.ac.jp)
 */
public class Sequence extends Expression {
    public static boolean flatten = true;
    private Expression[] expressions;
    private Integer hashCode = null;

    public Sequence(Expression[] expressions) {
        this.expressions = expressions;
        init();
    }

    public Sequence(Collection<Expression> expressions) {
        this.expressions = new Expression[0];
        this.expressions = expressions.toArray(this.expressions);
        init();
    }

    private void init() {
        if (flatten) {
            if (expressions.length > 0) {
                Expression op = expressions[0];
                boolean flattable = false;
                if (op.equals(Expression.ADD)
                        || op.equals(Expression.AND)
                        || op.equals(Expression.OR)) {
                    for (int i = 1; i < expressions.length; i++) {
                        if (expressions[i].isSequence(op)) {
                            flattable = true;
                            break;
                        }
                    }
                }
                if (flattable) {
                    List<Expression> list = new ArrayList<Expression>();
                    list.add(op);
                    for (int i = 1; i < expressions.length; i++) {
                        if (expressions[i].isSequence(op)) {
                            Sequence seq = (Sequence)expressions[i];
                            for (int j = 1; j < seq.length(); j++) {
                                list.add(seq.get(j));
                            }
                        } else {
                            list.add(expressions[i]);
                        }
                    }
                    expressions = new Expression[0];
                    expressions = list.toArray(expressions);
                }
            }
        }
        hashCode = hashCode();
    }
    
    public Expression[] getExpressions() {
        return expressions;
    }
    
    public Expression get(int i) {
        return expressions[i];
    }
    
    @Override
    public boolean isSequence() {
        return true;
    }

    @Override
    public boolean isSequence(Expression x) {
        return expressions.length >= 1 && expressions[0].equals(x);
    }

    @Override
    public boolean isSequence(int arity) {
        return expressions.length == arity + 1;
    }

    public int length() {
        return expressions.length;
    }

    public boolean matches(String pattern) throws SugarException {
        int i = 0;
        while (i < pattern.length()) {
            char c = pattern.charAt(i);
            if (c == '*')
                return true;
            if (i >= expressions.length)
                return false;
            Expression x = expressions[i];
            switch (c) {
            case '.':
                break;
            case 'A':
                if (! x.isAtom())
                    return false;
                break;
            case 'W':
                if (! x.isString())
                    return false;
                break;
            case 'I':
                if (! x.isInteger())
                    return false;
                break;
            case 'S':
                if (! x.isSequence())
                    return false;
                break;
            default:
                throw new SugarException("Unknown pattern name " + Character.toString(c));  
            }
            i++;
        }
        return i == expressions.length;
    }
    
    public int compareTo(Expression x) {
        if (x == null)
            return 1;
        if (x instanceof Atom)
            return 1;
        if (this.equals(x))
            return 0;
        Sequence another = (Sequence)x;
        if (expressions.length < another.expressions.length)
            return -1;
        if (expressions.length > another.expressions.length)
            return 1;
        for (int i = 0; i < expressions.length; i++) {
            int c = get(i).compareTo(another.get(i));
            if (c != 0)
                return c;
        }
        return 0;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        if (hashCode == null) {
            final int PRIME = 31;
            int result = 1;
            hashCode = PRIME * result + Arrays.hashCode(expressions);
        }
        return hashCode;
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
        final Sequence other = (Sequence) obj;
        if (!Arrays.equals(expressions, other.expressions))
            return false;
        return true;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        String delim = "(";
        for (Expression x : expressions) {
            sb.append(delim);
            sb.append(x.toString());
            delim = " ";
        }
        sb.append(")");
        return sb.toString();
    }

}

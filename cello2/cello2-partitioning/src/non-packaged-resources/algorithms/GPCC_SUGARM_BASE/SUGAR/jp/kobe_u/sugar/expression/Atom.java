package jp.kobe_u.sugar.expression;

/**
 * This class implements atomic expressions.
 * @author Naoyuki Tamura (tamura@kobe-u.ac.jp)
 */
public class Atom extends Expression {
    private Comparable atom;

    public Atom(Comparable atom) {
        this.atom = atom;
    }

    @Override
    public boolean isAtom() {
        return true;
    }

    @Override
    public boolean isString() {
        return atom instanceof String;
    }

    @Override
    public boolean isString(String s) {
        return isString() && atom.equals(s);
    }

    @Override
    public boolean isInteger() {
        return atom instanceof Integer;
    }

    @Override
    public String stringValue() {
        return (String)atom;
    }

    @Override
    public Integer integerValue() {
        return (Integer)atom;
    }

    @SuppressWarnings("unchecked")
    public int compareTo(Expression x) {
        if (x == null)
            return 1;
        if (x instanceof Sequence)
            return -1;
        if (this.equals(x))
            return 0;
        Atom another = (Atom)x;
        return atom.compareTo(another.atom);
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int PRIME = 31;
        int result = 1;
        result = PRIME * result + ((atom == null) ? 0 : atom.hashCode());
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
        final Atom other = (Atom) obj;
        if (atom == null) {
            if (other.atom != null)
                return false;
        } else if (!atom.equals(other.atom))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return atom.toString();
    }

}

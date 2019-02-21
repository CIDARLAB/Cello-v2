package jp.kobe_u.sugar.csp;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Iterator;
import java.util.List;

import jp.kobe_u.sugar.SugarException;
import jp.kobe_u.sugar.encoder.Encoding;

/**
 * This class implements an integer variable of CSP.
 * @see CSP
 * @author Naoyuki Tamura (tamura@kobe-u.ac.jp)
 */
public class IntegerVariable implements Comparable<IntegerVariable> {
    private static String AUX_NAME_PREFIX = "$I";
    private static int auxIntegerVariablesCount = 0;
    private String name;
    private IntegerDomain domain;
    private boolean aux;
    private String comment = null;
    private boolean modified = true;
    private int code;
    private boolean dominant;
    private int value;
    // private int offset;
    // private IntegerVariable[] vs = null;
    private Encoding encoding = Encoding.ORDER;
    
    public IntegerVariable(String name, IntegerDomain domain) throws SugarException {
        this.name = name;
        this.domain = domain;
        if (domain.isEmpty()) {
            throw new SugarException("Integer variable domain error " + name);
        }
        value = domain.getLowerBound();
        aux = false;
        if (name.startsWith(AUX_NAME_PREFIX)) {
            // Logger.println("c WARNING Auxiliary variable name found : " + name);
            int i = Integer.parseInt(name.substring(AUX_NAME_PREFIX.length()));
            if (i > auxIntegerVariablesCount)
                auxIntegerVariablesCount = i;
            aux = true;
        }
    }

    public IntegerVariable(IntegerDomain domain) throws SugarException {
        this.name = AUX_NAME_PREFIX + (++auxIntegerVariablesCount);
        this.domain = domain;
        if (domain.isEmpty()) {
            throw new SugarException("Integer variable domain error " + name);
        }
        value = domain.getLowerBound();
        aux = true;
    }

    /**
     * Returns the name of the integer variable. 
     * @return the name
     */
    public String getName() {
        return name;
    }

    public IntegerDomain getDomain() {
        return domain;
    }

    /**
     * Returns true when the integer variable is aux.
     * @return true when the integer variable is aux
     */
    public boolean isAux() {
        return aux;
    }

    /**
     * Returns the comment set to the integer variable.
     * @return the comment
     */
    public String getComment() {
        return comment;
    }

    /**
     * Sets the comment to the integer variable.
     * @param comment the comment to set
     */
    public void setComment(String comment) {
        this.comment = comment;
    }
    
    /**
     * @return the modified
     */
    public boolean isModified() {
        return modified;
    }

    /**
     * @param modified the modified to set
     */
    public void setModified(boolean modified) {
        this.modified = modified;
    }

    public int bound(int lb, int ub) throws SugarException {
        IntegerDomain oldDomain = domain;
        if (Math.max(domain.getLowerBound(), lb) > Math.min(domain.getUpperBound(), ub))
            throw new SugarException("Internal error: " + this + " " + lb + " " + ub);
        domain = domain.bound(lb, ub);
        if (! domain.equals(oldDomain)) {
            modified = true;
        }
        return oldDomain.size() - domain.size();
    }

    /**
     * Returns the code value in the encoded representation. 
     * @return the code value in the encoded representation
     */
    public int getCode() {
        return code;
    }

    /**
     * Sets the code value in the encoded representation. 
     * @param code the code value
     */
    public void setCode(int code) {
        this.code = code;
    }

    public boolean isDominant() {
        return dominant;
    }

    public void setDominant(boolean dominant) {
        this.dominant = dominant;
    }

    public Encoding getEncoding() {
        return encoding;
    }

    public void setEncoding(Encoding encoding) {
        this.encoding = encoding;
    }

    public boolean isPbEncoding() {
        return encoding == Encoding.LOG;
    }
    
    public boolean isBoolean() {
        int lb = domain.getLowerBound();
        int ub = domain.getUpperBound();
        return 0 <= lb && lb <= 1 && 0 <= ub && ub <= 1;
    }

    /**
     * Returns the value of the integer variable.
     * @return the value
     */
    public int getValue() {
        return value;
    }

    /**
     * Sets the value of the integer variable.
     * @param value the value to set
     */
    public void setValue(int value) {
        this.value = value;
    }

    public boolean isUnsatisfiable() {
        return domain.isEmpty();
    }

    /**
     * Returns true when the value is within the bounds.
     * @return true when the value is within the bounds
     */
    public boolean isSatisfied() {
        return domain.contains(value);
    }

    public int getSatVariablesSize() {
        /*
        if (vs != null)
            return 0;
        */
        return domain.size() - 1;
    }

    public List<Clause> getDomainClauses() {
        List<Clause> clauses = new ArrayList<Clause>();
        int last = 0;
        Iterator<int[]> intervals = domain.intervals();
        while (intervals.hasNext()) {
            int[] interval = intervals.next();
            if (domain.getLowerBound() < interval[0]) {
                Clause clause = new Clause();
                clause.add(new LinearLeLiteral(new LinearSum(1, this, -last)));
                clause.add(new LinearGeLiteral(new LinearSum(1, this, -interval[0])));
                clauses.add(clause);
            }
            last = interval[1];
        }
        return clauses;
    }
    
    public void decode(BitSet satValues) {
        int lb = domain.getLowerBound();
        int ub = domain.getUpperBound();
        int code = getCode();
        value = ub;
        for (int c = lb; c < ub; c++) {
            if (domain.contains(c)) {
                if (satValues.get(code)) {
                    value = c;
                    break;
                }
                code++;
            }
        }
    }
    
    public int compareTo(IntegerVariable v) {
        if (this == v)
            return 0;
        if (v == null)
            return 1;
        return name.compareTo(v.name);
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int PRIME = 31;
        int result = 1;
        result = PRIME * result + ((name == null) ? 0 : name.hashCode());
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
        final IntegerVariable other = (IntegerVariable) obj;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        return true;
    }

    /**
     * Returns the string representation of the integer variable.
     * @return the string representation
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("(int ");
        sb.append(name);
        sb.append(" ");
        sb.append(domain.toString());
        sb.append(")");
        return sb.toString();
    }

}

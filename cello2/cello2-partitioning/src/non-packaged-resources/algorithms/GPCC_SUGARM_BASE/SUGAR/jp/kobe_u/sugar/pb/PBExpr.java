package jp.kobe_u.sugar.pb;

import java.util.BitSet;
import java.util.TreeMap;
import java.util.Map;
import java.util.Set;

import jp.kobe_u.sugar.SugarException;

public class PBExpr {
    private Map<Integer,Integer> coef = new TreeMap<Integer,Integer>(); 
    private int b = 0;
    private String cmp = null;
    
    public PBExpr() {
    }

    public int size() {
        return coef.size();
    }

    public Set<Integer> getXs() {
        return coef.keySet();
    }
    
    public int getA(int code) {
        return coef.containsKey(code) ? coef.get(code) : 0;
    }

    public int getB() {
        return b;
    }
    
    public void setB(int b) {
        this.b = b;
    }
    
    public String getCmp() {
        return cmp;
    }

    public void setCmp(String cmp) throws SugarException {
        if (cmp != null) {
            if (cmp.equals("le") || cmp.equals("<=")) cmp = "<=";
            // else if (cmp.equals("lt")) cmp = "<";
            else if (cmp.equals("ge") || cmp.equals(">=")) cmp = ">=";
            // else if (cmp.equals("gt")) cmp = ">";
            // else if (cmp.equals("eq")) cmp = "=";
            // else if (cmp.equals("ne")) cmp = "!=";
            else
                throw new SugarException("unsupported comparison " + cmp);
        }
        this.cmp = cmp;
    }

    public int getLB() {
        int lb = b;
        for (int code : coef.keySet()) {
            int a = coef.get(code);
            if (a < 0)
                lb += a;
        }        
        return lb;
    }
    
    public int getUB() {
        int ub = b;
        for (int code : coef.keySet()) {
            int a = coef.get(code);
            if (a > 0)
                ub += a;
        }        
        return ub;
    }
    
    public void add(int b) {
        this.b += b;
    }

    public void add(int a, int code) {
        if (code < 0) {
            code = - code;
            b += a;
            a = - a;
        }
        if (coef.containsKey(code))
            a += coef.get(code);
        if (a == 0 && coef.containsKey(code)) {
            coef.remove(code);
        } else if (a != 0) {
            coef.put(code, a);
        }
    }

    public void add(PBExpr expr, int factor) {
        add(expr.b * factor);
        for (int code : expr.coef.keySet()) {
            int a = expr.coef.get(code);
            add(a * factor, code);
        }        
    }
    
    public void add(PBExpr expr) {
        add(expr, 1);
    }
    
    public void relax(int code) {
        if (cmp.equals("<=")) {
            add(- getUB(), code);
        } else if (cmp.equals(">=")) {
            add(- getLB(), code);
        }
    }
    
    public boolean isValid() {
        if (cmp != null && cmp.equals("<=")) {
            return getUB() <= 0;
        } else if (cmp != null && cmp.equals(">=")) {
            return getLB() >= 0;
        }
        return false;
    }
    
    public boolean isUnsatisfiable() {
        if (cmp != null && cmp.equals("<=")) {
            return getLB() > 0;
        } else if (cmp != null && cmp.equals(">=")) {
            return getUB() < 0;
        }
        return false;
    }
    
    public int value(BitSet pbValues) {
        int value = b;
        for (int code : coef.keySet()) {
            int x = pbValues.get(code) ? coef.get(code) : 0;
            value += x;
        }
        return value;
    }

    @Override
    public String toString() {
        String cmp = this.cmp;
        int sign = 1;
        if (cmp != null && cmp.equals("<=")) {
            sign = -1;
            cmp = ">=";
        }
        StringBuilder sb = new StringBuilder();
        String delim = "";
        for (int code : coef.keySet()) {
            int a = sign * coef.get(code);
            sb.append(delim);
            sb.append(a < 0 ? "" + a : "+" + a);
            sb.append(" x" + code);
            delim = " ";
        }
        int b = sign * this.b;
        if (cmp == null) {
            if (b != 0)
                sb.append(b < 0 ? " " + b : " +" + b);
        } else {
            sb.append(" " + cmp + " ");
            if (b == 0)
                sb.append("0");
            else
                sb.append(b < 0 ? "+" + (-b) : "" + (-b));
        }
        return sb.toString();
    }

}

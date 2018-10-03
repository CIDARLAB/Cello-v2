package jp.kobe_u.sugar.csp;

import java.util.HashMap;
import java.util.Map;

import jp.kobe_u.sugar.SugarConstants;
import jp.kobe_u.sugar.converter.Converter;
import jp.kobe_u.sugar.expression.Atom;
import jp.kobe_u.sugar.expression.Expression;
import jp.kobe_u.sugar.expression.Sequence;

/**
 * Predicate class.
 * @see Converter 
 * @author Naoyuki Tamura (tamura@kobe-u.ac.jp)
 */
public class Predicate {
    public String name;
    public int arity;
    public Atom[] params;
    public Map<Atom,Integer> paramsMap;
    public Expression body;
    private Expression[] args;
    
    public Predicate(Sequence seq, Expression body) {
        name = seq.get(0).stringValue();
        arity = seq.length() - 1;
        params = new Atom[arity];
        paramsMap = new HashMap<Atom,Integer>();
        for (int i = 1; i < seq.length(); i++) {
            params[i-1] = (Atom)seq.get(i);
            paramsMap.put(params[i-1], i-1);
        }
        this.body = body;
    }
    
    private boolean paramsOccurred(Expression x) {
        if (x.isAtom()) {
            Atom p = (Atom)x;
            return paramsMap.containsKey(p);
        }
        Sequence seq = (Sequence)x;
        for (int i = 0; i < seq.length(); i++) {
            if (paramsOccurred(seq.get(i)))
                return true;
        }
        return false;
    }
    
    private Expression substitute(Expression x) {
        if (! paramsOccurred(x))
            return x;
        if (x.isAtom()) {
            int i = paramsMap.get((Atom)x);
            return args[i];
        }
        Sequence seq = (Sequence)x;
        Expression[] xs = new Expression[seq.length()];
        for (int i = 0; i < seq.length(); i++) {
            xs[i] = substitute(seq.get(i));
        }
        x = Expression.create(xs);
        return x;
    }
    
    public Expression apply(Expression[] args) {
        this.args = args;
        return substitute(body);
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("(" + SugarConstants.PREDICATE_DEFINITION + " ");
        sb.append("(" + name);
        for (Atom x : params) {
            sb.append(" " + x.stringValue());
        }
        sb.append(") ");
        sb.append(body.toString());
        sb.append(")");
        return sb.toString();
    }

}

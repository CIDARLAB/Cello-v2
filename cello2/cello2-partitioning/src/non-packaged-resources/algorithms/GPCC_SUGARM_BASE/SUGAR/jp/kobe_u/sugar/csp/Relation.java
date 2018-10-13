package jp.kobe_u.sugar.csp;

import jp.kobe_u.sugar.SugarException;
import jp.kobe_u.sugar.converter.Converter;
import jp.kobe_u.sugar.expression.Expression;
import jp.kobe_u.sugar.expression.Sequence;

/**
 * Relation class.
 * @see Converter 
 * @author Naoyuki Tamura (tamura@kobe-u.ac.jp)
 */
public class Relation {
    public String name;
    public int arity;
    public boolean conflicts;
    public int[][] tuples;
    
    public Relation(String name, int arity, Sequence body) throws SugarException {
        this.name = name;
        this.arity = arity;
        if (body.isSequence(Expression.SUPPORTS)) {
            conflicts = false;
        } else if (body.isSequence(Expression.CONFLICTS)) {
            conflicts = true;
        } else {
            throw new SugarException("Syntax error " + body);
        }
        int n = body.length() - 1;
        tuples = new int[n][];
        for (int i = 1; i <= n; i++) {
            if (! body.get(i).isSequence()) {
                throw new SugarException("Syntax error " + body);
            }
            Sequence seq = (Sequence)body.get(i);
            int[] tuple = new int[arity];
            for (int j = 0; j < arity; j++) {
                tuple[j] = seq.get(j).integerValue();
            }
            tuples[i-1] = tuple;
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("(" + Expression.RELATION_DEFINITION + " ");
        sb.append(name + " " + arity + " (");
        sb.append(conflicts ? Expression.CONFLICTS : Expression.SUPPORTS);
        for (int[] tuple : tuples) {
            sb.append(" (");
            String delim = "";
            for (int j = 0; j < arity; j++) {
                sb.append(delim + tuple[j]);
                delim = " ";
            }
            sb.append(")");
        }
        sb.append("))");
        return sb.toString();
    }
}

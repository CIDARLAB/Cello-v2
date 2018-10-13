package jp.kobe_u.sugar.hook;

import java.util.ArrayList;
import java.util.List;

import jp.kobe_u.sugar.converter.Converter;
import jp.kobe_u.sugar.csp.Clause;
import jp.kobe_u.sugar.csp.IntegerDomain;
import jp.kobe_u.sugar.expression.Expression;
import jp.kobe_u.sugar.expression.Sequence;
import jp.kobe_u.sugar.hook.ConverterHook;
import jp.kobe_u.sugar.SugarException;

/**
 * @author Naoyuki Tamura (tamura@kobe-u.ac.jp)
 * 
 */
public class ExampleHook implements ConverterHook {
    private static Expression MyConstant = Expression.create("myconstant");
    private static Expression MyPredicate = Expression.create("mypredicate");

    @Override
    public Expression convertFunction(Converter converter, Expression x)
            throws SugarException {
        if (x.equals(MyConstant)) {
            // myconstant --> 55
            Expression z = Expression.create(55);
            return z;
        }
        return x;
    }

    @Override
    public Expression convertConstraint(Converter converter, Expression x,
            boolean negative, List<Clause> clauses) throws SugarException {
        if (x.isSequence(MyPredicate)) {
            // (mypredicate x1 x2 x3 ...) --> (and (< x1 x2) (< x2 x3) ...)
            Sequence seq = (Sequence) x;
            List<Expression> xs = new ArrayList<Expression>();
            xs.add(Expression.AND);
            for (int i = 1; i < seq.length() - 1; i++)
                xs.add(seq.get(i).lt(seq.get(i + 1)));
            Expression z = Expression.create(xs);
            return z;
        } else if (x.isSequence(Expression.ALLDIFFERENT)) {
            return convertAlldifferent(converter, (Sequence)x, negative, clauses);
        }
        return x;
    }

    private Expression convertAlldifferent(Converter converter, Sequence seq,
            boolean negative, List<Clause> clauses) throws SugarException {
        // Convert alldifferent by default
        Expression z = converter.convertGlobal(seq, negative, clauses);
        // Get arguments
        Expression[] args;
        if (seq.length() == 2 && seq.get(1).isSequence()) {
            args = ((Sequence) seq.get(1)).getExpressions();
        } else {
            args = new Expression[seq.length() - 1];
            for (int i = 1; i < seq.length(); i++)
                args[i - 1] = seq.get(i);
        }
        int n = args.length;
        // Calculate bounds of the arguments
        int lb = Integer.MAX_VALUE;
        int ub = Integer.MIN_VALUE;
        for (Expression x : args) {
            IntegerDomain d = converter.convertFormula(x).getDomain();
            lb = Math.min(lb, d.getLowerBound());
            ub = Math.max(ub, d.getUpperBound());
        }
        // Add At-Least-One clause when it is a permutation
        if (n == ub - lb + 1) {
            for (int value = lb; value <= ub; value++) {
                List<Expression> alo = new ArrayList<Expression>();
                alo.add(Expression.OR);
                for (Expression x : args) {
                    alo.add(x.eq(value));
                }
                if (z == null)
                    z = Expression.create(alo);
                else
                    z = z.and(Expression.create(alo));
            }
        }
        return z;
    }
}

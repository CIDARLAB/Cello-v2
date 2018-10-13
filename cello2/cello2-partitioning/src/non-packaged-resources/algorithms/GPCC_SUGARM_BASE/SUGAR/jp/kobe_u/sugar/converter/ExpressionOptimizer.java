package jp.kobe_u.sugar.converter;

import jp.kobe_u.sugar.SugarException;
import jp.kobe_u.sugar.expression.Expression;
import jp.kobe_u.sugar.expression.Sequence;

/**
 * @author Naoyuki Tamura (tamura@kobe-u.ac.jp)
 *
 */
public class ExpressionOptimizer {
    private static Expression ZERO = Expression.ZERO;
    private static Expression ANY = Expression.ANY;
    private static Expression leMulZero = ANY.mul(ANY).le(ZERO);
    private static Expression gtMulZero = ANY.mul(ANY).gt(ZERO);
    
    private Converter converter;

    public ExpressionOptimizer(Converter converter) {
        this.converter = converter;
    }

    private boolean isEq(Sequence seq, boolean negative) {
        return (! negative && seq.isSequence(Expression.EQ))
        || (negative && seq.isSequence(Expression.NE));
    }
    
    private boolean isNe(Sequence seq, boolean negative) {
        return (! negative && seq.isSequence(Expression.NE))
        || (negative && seq.isSequence(Expression.EQ));
    }
    
    private boolean isLe(Sequence seq, boolean negative) {
        return (! negative && seq.isSequence(Expression.LE))
        || (negative && seq.isSequence(Expression.GT));
    }
    
    private boolean isLt(Sequence seq, boolean negative) {
        return (! negative && seq.isSequence(Expression.LT))
        || (negative && seq.isSequence(Expression.GE));
    }
    
    private boolean isGe(Sequence seq, boolean negative) {
        return (! negative && seq.isSequence(Expression.GE))
        || (negative && seq.isSequence(Expression.LT));
    }
    
    private boolean isGt(Sequence seq, boolean negative) {
        return (! negative && seq.isSequence(Expression.GT))
        || (negative && seq.isSequence(Expression.LE));
    }

    private Expression peepholeMulZero(Sequence seq, boolean negative) throws SugarException {
        if (isEq(seq, negative)) {
            converter.checkArity(seq, 2);
            if (seq.get(1).isSequence(Expression.MUL) && seq.get(2).equals(ZERO)) {
                Expression a1 = ((Sequence)seq.get(1)).get(1);
                Expression a2 = ((Sequence)seq.get(1)).get(2);
                // a1*a2 == 0
                Expression x = (a1.eq(ZERO)).or(a2.eq(ZERO));
                return x;
            }
            if (seq.get(2).isSequence(Expression.MUL) && seq.get(1).equals(ZERO)) {
                Expression a1 = ((Sequence)seq.get(2)).get(1);
                Expression a2 = ((Sequence)seq.get(2)).get(2);
                // 0 == a1*a2
                Expression x = (a1.eq(ZERO)).or(a2.eq(ZERO));
                return x;
            }
        } else if (isNe(seq, negative)) {
            converter.checkArity(seq, 2);
            if (seq.get(1).isSequence(Expression.MUL) && seq.get(2).equals(ZERO)) {
                Expression a1 = ((Sequence)seq.get(1)).get(1);
                Expression a2 = ((Sequence)seq.get(1)).get(2);
                // a1*a2 != 0
                Expression x = (a1.ne(ZERO)).and(a2.ne(ZERO));
                return x;
            }
            if (seq.get(2).isSequence(Expression.MUL) && seq.get(1).equals(ZERO)) {
                Expression a1 = ((Sequence)seq.get(2)).get(1);
                Expression a2 = ((Sequence)seq.get(2)).get(2);
                // 0 != a1*a2
                Expression x = (a1.ne(ZERO)).and(a2.ne(ZERO));
                return x;
            }
        } else if (isLe(seq, negative)) {
            converter.checkArity(seq, 2);
            if (seq.get(1).isSequence(Expression.MUL) && seq.get(2).equals(ZERO)) {
                Expression a1 = ((Sequence)seq.get(1)).get(1);
                Expression a2 = ((Sequence)seq.get(1)).get(2);
                // a1*a2 <= 0
                Expression x = ((a1.lt(ZERO)).and(a2.gt(ZERO)))
                        .or((a1.gt(ZERO)).and(a2.lt(ZERO)))
                        .or(a1.eq(ZERO))
                        .or(a2.eq(ZERO));
                return x;
            }
            if (seq.get(2).isSequence(Expression.MUL) && seq.get(1).equals(ZERO)) {
                Expression a1 = ((Sequence)seq.get(2)).get(1);
                Expression a2 = ((Sequence)seq.get(2)).get(2);
                // 0 <= a1*a2
                Expression x = ((a1.lt(ZERO)).and(a2.lt(ZERO)))
                        .or((a1.gt(ZERO)).and(a2.gt(ZERO)))
                        .or(a1.eq(ZERO))
                        .or(a2.eq(ZERO));
                return x;
            }
        } else if (isLt(seq, negative)) {
            converter.checkArity(seq, 2);
            if (seq.get(1).isSequence(Expression.MUL) && seq.get(2).equals(ZERO)) {
                Expression a1 = ((Sequence)seq.get(1)).get(1);
                Expression a2 = ((Sequence)seq.get(1)).get(2);
                // a1*a2 < 0
                Expression x = ((a1.lt(ZERO)).and(a2.gt(ZERO)))
                        .or((a1.gt(ZERO)).and(a2.lt(ZERO)));
                return x;
            }
            if (seq.get(2).isSequence(Expression.MUL) && seq.get(1).equals(ZERO)) {
                Expression a1 = ((Sequence)seq.get(1)).get(1);
                Expression a2 = ((Sequence)seq.get(1)).get(2);
                // 0 < a1*a2
                Expression x = ((a1.lt(ZERO)).and(a2.lt(ZERO)))
                        .or((a1.gt(ZERO)).and(a2.gt(ZERO)));
                return x;
            }
        } else if (isGe(seq, negative)) {
            converter.checkArity(seq, 2);
            if (seq.get(1).isSequence(Expression.MUL) && seq.get(2).equals(ZERO)) {
                Expression a1 = ((Sequence)seq.get(1)).get(1);
                Expression a2 = ((Sequence)seq.get(1)).get(2);
                // a1*a2 >= 0
                Expression x = ((a1.lt(ZERO)).and(a2.lt(ZERO)))
                        .or((a1.gt(ZERO)).and(a2.gt(ZERO)))
                        .or(a1.eq(ZERO))
                        .or(a2.eq(ZERO));
                return x;
            }
            if (seq.get(2).isSequence(Expression.MUL) && seq.get(1).equals(ZERO)) {
                Expression a1 = ((Sequence)seq.get(2)).get(1);
                Expression a2 = ((Sequence)seq.get(2)).get(2);
                // 0 >= a1*a2
                Expression x = ((a1.lt(ZERO)).and(a2.gt(ZERO)))
                        .or((a1.gt(ZERO)).and(a2.lt(ZERO)))
                        .or(a1.eq(ZERO))
                        .or(a2.eq(ZERO));
                return x;
            }
        } else if (isGt(seq, negative)) {
            converter.checkArity(seq, 2);
            if (seq.get(1).isSequence(Expression.MUL) && seq.get(2).equals(ZERO)) {
                Expression a1 = ((Sequence)seq.get(1)).get(1);
                Expression a2 = ((Sequence)seq.get(1)).get(2);
                // a1*a2 > 0
                Expression x = ((a1.lt(ZERO)).and(a2.lt(ZERO)))
                        .or((a1.gt(ZERO)).and(a2.gt(ZERO)));
                return x;
            }
            if (seq.get(1).equals(ZERO) && seq.get(2).isSequence(Expression.MUL)) {
                Expression a1 = ((Sequence)seq.get(2)).get(1);
                Expression a2 = ((Sequence)seq.get(2)).get(2);
                // 0 > a1*a2
                Expression x = ((a1.lt(ZERO)).and(a2.gt(ZERO)))
                        .or((a1.gt(ZERO)).and(a2.lt(ZERO)));
                return x;
            }
        }
        return null;
    }
    
    private Expression peepholeAbs(Sequence seq, boolean negative) throws SugarException {
        if (isEq(seq, negative)) {
            converter.checkArity(seq, 2);
            if (seq.get(1).isSequence(Expression.ABS)) {
                Expression a1 = ((Sequence)seq.get(1)).get(1);
                Expression x2 = seq.get(2);
                // abs(a1) == x2
                // Expression x = (x2.ge(0)).and((a1.eq(x2)).or(a1.neg().eq(x2)));
                Expression x = ((a1.le(x2)).and(a1.ge(x2.neg())))
                        .and((a1.ge(x2)).or(a1.le(x2.neg())));
                return x;
            }
            if (seq.get(2).isSequence(Expression.ABS)) {
                Expression a1 = ((Sequence)seq.get(2)).get(1);
                Expression x1 = seq.get(1);
                // x1 == abs(a1)
                // Expression x = (x1.ge(0)).and((a1.eq(x1)).or(a1.neg().eq(x1)));
                Expression x = ((a1.le(x1)).and(a1.ge(x1.neg())))
                        .and((a1.ge(x1)).or(a1.le(x1.neg())));
                return x;
            }
        } else if (isNe(seq, negative)) {
            converter.checkArity(seq, 2);
            if (seq.get(1).isSequence(Expression.ABS)) {
                Expression a1 = ((Sequence)seq.get(1)).get(1);
                Expression x2 = seq.get(2);
                // abs(a1) != x2
                // Expression x = ((x2.ge(0)).and((a1.eq(x2)).or(a1.neg().eq(x2)))).not();
                Expression x = ((a1.lt(x2)).and(a1.gt(x2.neg())))
                        .or((a1.gt(x2)).or(a1.lt(x2.neg())));
                return x;
            }
            if (seq.get(2).isSequence(Expression.ABS)) {
                Expression a1 = ((Sequence)seq.get(2)).get(1);
                Expression x1 = seq.get(1);
                // x1 != abs(a1)
                // Expression x = ((x1.ge(0)).and((a1.eq(x1)).or(a1.neg().eq(x1)))).not();
                Expression x = ((a1.lt(x1)).and(a1.gt(x1.neg())))
                        .or((a1.gt(x1)).or(a1.lt(x1.neg())));
                return x;
            }
        } else if (isLe(seq, negative)) {
            converter.checkArity(seq, 2);
            if (seq.get(1).isSequence(Expression.ABS)) {
                Expression a1 = ((Sequence)seq.get(1)).get(1);
                Expression x2 = seq.get(2);
                // abs(a1) <= x2
                Expression x = (a1.le(x2)).and(a1.ge(x2.neg()));
                return x;
            }
            if (seq.get(2).isSequence(Expression.ABS)) {
                Expression a1 = ((Sequence)seq.get(2)).get(1);
                Expression x1 = seq.get(1);
                // x1 <= abs(a1)
                Expression x = (a1.ge(x1)).or(a1.le(x1.neg()));
                return x;
            }
        } else if (isLt(seq, negative)) {
            converter.checkArity(seq, 2);
            if (seq.get(1).isSequence(Expression.ABS)) {
                Expression a1 = ((Sequence)seq.get(1)).get(1);
                Expression x2 = seq.get(2);
                // abs(a1) < x2
                Expression x = (a1.lt(x2)).and(a1.gt(x2.neg()));
                return x;
            }
            if (seq.get(2).isSequence(Expression.ABS)) {
                Expression a1 = ((Sequence)seq.get(2)).get(1);
                Expression x1 = seq.get(1);
                // x1 < abs(a1)
                Expression x = (a1.gt(x1)).or(a1.lt(x1.neg()));
                return x;
            }
        } else if (isGe(seq, negative)) {
            converter.checkArity(seq, 2);
            if (seq.get(1).isSequence(Expression.ABS)) {
                Expression a1 = ((Sequence)seq.get(1)).get(1);
                Expression x2 = seq.get(2);
                // abs(a1) >= x2
                Expression x = (a1.ge(x2)).or(a1.le(x2.neg()));
                return x;
            }
            if (seq.get(2).isSequence(Expression.ABS)) {
                Expression a1 = ((Sequence)seq.get(2)).get(1);
                Expression x1 = seq.get(1);
                // x1 >= abs(a1)
                Expression x = (a1.le(x1)).and(a1.ge(x1.neg()));
                return x;
            }
        } else if (isGt(seq, negative)) {
            converter.checkArity(seq, 2);
            if (seq.get(1).isSequence(Expression.ABS)) {
                Expression a1 = ((Sequence)seq.get(1)).get(1);
                Expression x2 = seq.get(2);
                // abs(a1) > x2
                Expression x = (a1.gt(x2)).or(a1.lt(x2.neg()));
                return x;
            }
            if (seq.get(2).isSequence(Expression.ABS)) {
                Expression a1 = ((Sequence)seq.get(2)).get(1);
                Expression x1 = seq.get(1);
                // x1 > abs(a1)
                Expression x = (a1.lt(x1)).and(a1.gt(x1.neg()));
                return x;
            }
        }
        return null;
    }
    
    protected Expression peephole(Sequence seq, boolean negative) throws SugarException {
        Expression x = null;
        x = peepholeMulZero(seq, negative);
        if (x != null)
            return x;
        if (Converter.OPT_PEEPHOLE_ABS) {
            x = peepholeAbs(seq, negative);
            if (x != null)
                return x;
        }
        return null;
    }
}

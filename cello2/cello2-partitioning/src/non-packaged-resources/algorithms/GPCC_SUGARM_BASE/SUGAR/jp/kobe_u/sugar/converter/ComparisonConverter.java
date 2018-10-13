package jp.kobe_u.sugar.converter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import jp.kobe_u.sugar.SugarException;
import jp.kobe_u.sugar.csp.CSP;
import jp.kobe_u.sugar.csp.Clause;
import jp.kobe_u.sugar.csp.IntegerDomain;
import jp.kobe_u.sugar.csp.IntegerVariable;
import jp.kobe_u.sugar.csp.LinearEqLiteral;
import jp.kobe_u.sugar.csp.LinearGeLiteral;
import jp.kobe_u.sugar.csp.LinearLeLiteral;
import jp.kobe_u.sugar.csp.LinearLiteral;
import jp.kobe_u.sugar.csp.LinearNeLiteral;
import jp.kobe_u.sugar.csp.LinearSum;
import jp.kobe_u.sugar.csp.Literal;
import jp.kobe_u.sugar.csp.ProductLiteral;
import jp.kobe_u.sugar.expression.Atom;
import jp.kobe_u.sugar.expression.Expression;
import jp.kobe_u.sugar.expression.Sequence;
import jp.kobe_u.sugar.hook.ConverterHook;

/**
 * @author Naoyuki Tamura (tamura@kobe-u.ac.jp)
 *
 */
public class ComparisonConverter {

    private Converter converter;
    private CSP csp;

    public ComparisonConverter(Converter converter) {
        this.converter = converter;
        csp = converter.csp;
    }

    private void convertConstraint(Expression x) throws SugarException {
        converter.convertConstraint(x);
    }

    private IntegerVariable newIntegerVariable(IntegerDomain d, Expression x)
    throws SugarException {
        return converter.newIntegerVariable(d, x);
    }
    
    private IntegerVariable toIntegerVariable(LinearSum e, Expression x)
    throws SugarException {
        IntegerVariable v;
        if (e.isIntegerVariable()) {
            v = e.getCoef().firstKey();
        } else {
            v = newIntegerVariable(e.getDomain(), x);
            Expression eq = Expression.create(v.getName()).eq(x);
            eq.setComment(v.getName() + " == " + x);
            convertConstraint(eq);
            converter.addEquivalence(v, x);
        }
        return v;
    }
    
    private LinearSum convertInteger(Atom x) throws SugarException {
        return new LinearSum(x.integerValue());
    }
    
    private LinearSum convertString(Atom x) throws SugarException {
        String s = x.stringValue();
        if (csp.getIntegerVariable(s) == null) {
            converter.syntaxError(x);
        }
        IntegerVariable v = csp.getIntegerVariable(s);
        return new LinearSum(v);
    }
    
    private LinearSum convertADD(Sequence seq) throws SugarException {
        LinearSum e = new LinearSum(0);
        for (int i = 1; i < seq.length(); i++) {
            LinearSum ei = convertFormula(seq.get(i));
            e.add(ei);
        }
        return e;
    }
    
    private LinearSum convertSUB(Sequence seq) throws SugarException {
        LinearSum e = null;
        if (seq.length() == 1) {
            converter.syntaxError(seq);
        } else if (seq.length() == 2) {
            e = convertFormula(seq.get(1));
            e.multiply(-1);
        } else {
            e = convertFormula(seq.get(1));
            for (int i = 2; i < seq.length(); i++) {
                LinearSum ei = convertFormula(seq.get(i));
                e.subtract(ei);
            }
        }
        return e;
    }
    
    private LinearSum convertABS(Sequence seq) throws SugarException {
        converter.checkArity(seq, 1);
        Expression x1 = seq.get(1);
        LinearSum e1 = convertFormula(x1);
        IntegerDomain d1 = e1.getDomain();
        if (d1.getLowerBound() >= 0) {
            return e1;
        } else if (d1.getUpperBound() <= 0) {
            e1.multiply(-1);
            return e1;
        }
        IntegerDomain d = d1.abs();
        IntegerVariable v = newIntegerVariable(d, seq);
        Expression x = Expression.create(v.getName());
        Expression eq =
            (x.ge(x1))
            .and(x.ge(x1.neg()))
            .and((x.le(x1)).or(x.le(x1.neg())));
        eq.setComment(v.getName() + " == " + seq);
        convertConstraint(eq);
        converter.addEquivalence(v, seq);
        return new LinearSum(v);
    }
    
    private LinearSum convertMUL(Sequence seq) throws SugarException {
        converter.checkArity(seq, 2);
        Expression x1 = seq.get(1);
        Expression x2 = seq.get(2);
        LinearSum e1 = convertFormula(x1);
        LinearSum e2 = convertFormula(x2);
        IntegerDomain d1 = e1.getDomain();
        IntegerDomain d2 = e2.getDomain();
        if (d1.size() == 1) {
            e2.multiply(d1.getLowerBound());
            return e2;
        } else if (d2.size() == 1) {
            e1.multiply(d2.getLowerBound());
            return e1;
        } else if (d1.size() > d2.size()) {
            return convertMUL((Sequence)x2.mul(x1));
        } else if (false) {
            Expression x = null;
            Iterator<Integer> iter = d1.values();
            while (iter.hasNext()) {
                int a1 = iter.next();
                if (x == null) {
                    x = x2.mul(a1);
                } else {
                    x = (x1.ge(a1)).ifThenElse(x2.mul(a1), x);
                }
            }
            return convertIF((Sequence)x);
        } else {
            IntegerVariable prod = newIntegerVariable(d1.mul(d2), seq);
            Expression x = Expression.create(prod.getName());
            Iterator<Integer> iter = d1.values();
            while (iter.hasNext()) {
                int a1 = iter.next();
                converter.addExtra(x1.eq(a1).imp(x.eq(x2.mul(a1))));
            }
            return new LinearSum(prod);
        }
    }
    
    private LinearSum convertDIV(Sequence seq) throws SugarException {
        converter.checkArity(seq, 2);
        Expression x1 = seq.get(1);
        Expression x2 = seq.get(2);
        LinearSum e1 = convertFormula(x1);
        LinearSum e2 = convertFormula(x2);
        IntegerDomain d1 = e1.getDomain();
        IntegerDomain d2 = e2.getDomain();
        IntegerDomain qd = d1.div(d2);
        IntegerDomain rd = d1.mod(d2);
        IntegerVariable qv = newIntegerVariable(qd, seq);
        IntegerVariable rv = newIntegerVariable(rd, x1.mod(x2));
        Expression q = Expression.create(qv.getName());
        Expression r = Expression.create(rv.getName());
        Expression px = x2.mul(q);
        if (d2.size() == 1) {
            int value2 = d2.getLowerBound();
            if (value2 == 1) {
                return e1;
            } else if (value2 == -1) {
                e1.multiply(-1);
                return e1;
            }
            // TODO
            if (value2 <= 0) {
                throw new SugarException("Unsupported " + seq);
            }
            Expression eq =
                (x1.eq(px.add(r)))
                .and((r.ge(Expression.ZERO)).and((x2.abs()).gt(r)));
            eq.setComment(qv.getName() + " == " + seq);
            convertConstraint(eq);
            converter.addEquivalence(qv, seq);
            return new LinearSum(qv);
        }
        // TODO
        if (true) {
            throw new SugarException("Unsupported " + seq);
        }
        IntegerVariable v2 = toIntegerVariable(e2, x2);
        IntegerDomain pd = d1.sub(rd);
        IntegerVariable pv = newIntegerVariable(pd, px);
        Clause clause = new Clause(new ProductLiteral(pv, qv, v2));
        clause.setComment(pv.getName() + " == " + px);
        csp.add(clause);
        Expression p = Expression.create(pv.getName());
        Expression eq =
            (x1.eq(p.add(r)))
            .and((r.ge(Expression.ZERO)).and((x2.abs()).gt(r)));
        eq.setComment(qv.getName() + " == " + seq);
        convertConstraint(eq);
        converter.addEquivalence(qv, seq);
        return new LinearSum(qv);
    }
    
    private LinearSum convertMOD(Sequence seq) throws SugarException {
        converter.checkArity(seq, 2);
        Expression x1 = seq.get(1);
        Expression x2 = seq.get(2);
        LinearSum e1 = convertFormula(x1);
        LinearSum e2 = convertFormula(x2);
        IntegerDomain d1 = e1.getDomain();
        IntegerDomain d2 = e2.getDomain();
        IntegerDomain qd = d1.div(d2);
        IntegerDomain rd = d1.mod(d2);
        IntegerVariable qv = newIntegerVariable(qd, seq);
        IntegerVariable rv = newIntegerVariable(rd, x1.mod(x2));
        Expression q = Expression.create(qv.getName());
        Expression r = Expression.create(rv.getName());
        Expression px = x2.mul(q);
        if (d2.size() == 1) {
            int value2 = d2.getLowerBound();
            // TODO
            if (value2 <= 0) {
                throw new SugarException("Unsupported " + seq);
            }
            Expression eq =
                (x1.eq(px.add(r)))
                .and((r.ge(Expression.ZERO)).and((x2.abs()).gt(r)));
            eq.setComment(qv.getName() + " == " + seq);
            convertConstraint(eq);
            converter.addEquivalence(rv, seq);
            return new LinearSum(rv);
        }
        // TODO
        if (true) {
            throw new SugarException("Unsupported " + seq);
        }
        IntegerVariable v2 = toIntegerVariable(e2, x2);
        IntegerDomain pd = d1.sub(rd);
        IntegerVariable pv = newIntegerVariable(pd, px);
        Clause clause = new Clause(new ProductLiteral(pv, qv, v2));
        clause.setComment(pv.getName() + " == " + px);
        csp.add(clause);
        Expression p = Expression.create(pv.getName());
        Expression eq =
            (x1.eq(p.add(r)))
            .and((r.ge(Expression.ZERO)).and((x2.abs()).gt(r)));
        eq.setComment(rv.getName() + " == " + seq);
        convertConstraint(eq);
        converter.addEquivalence(rv, seq);
        return new LinearSum(rv);
    }
    
    private LinearSum convertPOW(Sequence seq) throws SugarException {
        // TODO pow
        throw new SugarException("Unsupported " + seq);
        // return null;
    }
    
    private LinearSum convertMIN(Sequence seq) throws SugarException {
        converter.checkArity(seq, 2);
        Expression x1 = seq.get(1);
        Expression x2 = seq.get(2);
        LinearSum e1 = convertFormula(x1);
        LinearSum e2 = convertFormula(x2);
        IntegerDomain d1 = e1.getDomain();
        IntegerDomain d2 = e2.getDomain();
        if (d1.getUpperBound() <= d2.getLowerBound()) {
            return e1;
        } else if (d2.getUpperBound() <= d1.getLowerBound()) {
            return e2;
        }
        IntegerDomain d = d1.min(d2);
        IntegerVariable v = newIntegerVariable(d, seq);
        Expression x = Expression.create(v.getName());
        Expression eq =
            (x.le(x1))
            .and(x.le(x2))
            .and((x.ge(x1)).or(x.ge(x2)));
        eq.setComment(v.getName() + " == " + seq);
        convertConstraint(eq);
        converter.addEquivalence(v, seq);
        return new LinearSum(v);
    }
    
    private LinearSum convertMAX(Sequence seq) throws SugarException {
        converter.checkArity(seq, 2);
        Expression x1 = seq.get(1);
        Expression x2 = seq.get(2);
        LinearSum e1 = convertFormula(x1);
        LinearSum e2 = convertFormula(x2);
        IntegerDomain d1 = e1.getDomain();
        IntegerDomain d2 = e2.getDomain();
        if (d1.getUpperBound() <= d2.getLowerBound()) {
            return e2;
        } else if (d2.getUpperBound() <= d1.getLowerBound()) {
            return e1;
        }
        IntegerDomain d = d1.max(d2);
        IntegerVariable v = newIntegerVariable(d, seq);
        Expression x = Expression.create(v.getName());
        Expression eq =
            (x.ge(x1))
            .and(x.ge(x2))
            .and((x.le(x1)).or(x.le(x2)));
        eq.setComment(v.getName() + " == " + seq);
        convertConstraint(eq);
        converter.addEquivalence(v, seq);
        return new LinearSum(v);
    }
    
    private LinearSum convertIF(Sequence seq) throws SugarException {
        converter.checkArity(seq, 3);
        Expression x1 = seq.get(1);
        Expression x2 = seq.get(2);
        Expression x3 = seq.get(3);
        LinearSum e2 = convertFormula(x2);
        LinearSum e3 = convertFormula(x3);
        IntegerDomain d2 = e2.getDomain();
        IntegerDomain d3 = e3.getDomain();
        IntegerDomain d = d2.cup(d3);
        IntegerVariable v = newIntegerVariable(d, seq);
        Expression x = Expression.create(v.getName());
        Expression eq =
            ((x1.not()).or(x.eq(x2)))
            .and(x1.or(x.eq(x3)));
        eq.setComment(v.getName() + " == " + seq);
        convertConstraint(eq);
        converter.addEquivalence(v, seq);
        return new LinearSum(v);
    }
    
    protected LinearSum convertFormula(Expression x) throws SugarException {
        if (Converter.hooks != null) {
            Expression x1;
            do {
                x1 = x;
                for (ConverterHook hook : Converter.hooks) {
                    x = hook.convertFunction(converter, x);
                }
            } while (! x1.equals(x));
        }
        LinearSum e = null;
        IntegerVariable v = converter.getEquivalence(x);
        if (v != null) {
            e = new LinearSum(v);
        } else if (x.isAtom()) {
            if (x.isInteger()) {
                e = convertInteger((Atom)x);
            } else {
                e = convertString((Atom)x);
            }
        } else {
            if (x.isSequence(Expression.ADD)) {
                e = convertADD((Sequence)x);
            } else if (x.isSequence(Expression.NEG) || x.isSequence(Expression.SUB)) {
                e = convertSUB((Sequence)x);
            } else if (x.isSequence(Expression.ABS)) {
                e = convertABS((Sequence)x);
            } else if (x.isSequence(Expression.MUL)) {
                e = convertMUL((Sequence)x);
            } else if (x.isSequence(Expression.DIV)) {
                e = convertDIV((Sequence)x);
            } else if (x.isSequence(Expression.MOD)) {
                e = convertMOD((Sequence)x);
            } else if (x.isSequence(Expression.POW)) {
                e = convertPOW((Sequence)x);
            } else if (x.isSequence(Expression.MIN)) {
                e = convertMIN((Sequence)x);
            } else if (x.isSequence(Expression.MAX)) {
                e = convertMAX((Sequence)x);
            } else if (x.isSequence(Expression.IF)) {
                e = convertIF((Sequence)x);
            } else {
                converter.syntaxError(x);
            }
        }
        return e;
    }

    private LinearSum simplifyLinearLe(LinearSum e) throws SugarException {
        if (e.size() <= 3) {
            return e;
        }
        /*
        if (! e.isDomainLargerThan(MAX_LINEARSUM_SIZE)) {
            return e;
        }
        */
        IntegerVariable var = e.getLargestDomainVariable();
        if (! e.isDomainLargerThanExcept(Converter.MAX_LINEARSUM_SIZE, var)) {
            return e;
        }
        IntegerVariable[] vs = e.getVariablesSorted();
        /*
        LinearLe e0 = new LinearLe(e.getB());
        int domainSize = 1;
        int i = 0;
        for (i = 0; i < vs.length - 2 && domainSize <= MAX_LINEARSUM_SIZE; i++) {
            e0.setA(e.getA(vs[i]), vs[i]);
            domainSize *= vs[i].getDomain().size();
        }
        LinearLe e1 = new LinearLe(0);
        for (int j = i; j < vs.length; j++) {
            e1.setA(e.getA(vs[j]), vs[j]);
        }
        int factor = e1.factor();
        if (factor > 1) {
            e1.divide(factor);
        }
        IntegerVariable v = new IntegerVariable(e1.getDomain());
        v.setComment(v.getName() + " : " + e1);
        csp.add(v);
        Expression x = Expression.create(v.getName());
        Expression ex = e1.toExpression();
        Expression eq = x.eq(ex);
        extra.add(eq);
        e0.setA(factor, v);
        return e0;
        */
        LinearSum e1 = new LinearSum(0);
        for (int i = 2; i < vs.length; i++) {
            e1.setA(e.getA(vs[i]), vs[i]);
        }
        int factor = e1.factor();
        if (factor > 1) {
            e1.divide(factor);
        }
        // v == (a[2]*vs[2] + a[3]*vs[3] + ... + a[n]*vs[n]) / factor
        IntegerVariable v = new IntegerVariable(e1.getDomain());
        v.setComment(v.getName() + " : " + e1);
        csp.add(v);
        Expression x = Expression.create(v.getName());
        Expression ex = e1.toExpression();
        Expression eq = x.eq(ex);
        // eq.setComment(v.getName() + " == " + e1);
        // XXX 
        // convertConstraint(eq);
        converter.addExtra(eq);
        // e0 = b + a[0]*vs[0] + a[1]*vs[1] + factor*v
        LinearSum e0 = new LinearSum(e.getB());
        e0.setA(e.getA(vs[0]), vs[0]);
        e0.setA(e.getA(vs[1]), vs[1]);
        e0.setA(factor, v);
//      System.out.println(e + " ==> " + e0 + " with " + eq);
        return e0;
    }
    
    private LinearSum simplifyLinearExpression(LinearSum e, String cmp, boolean first) throws SugarException {
        if (Converter.ESTIMATE_SATSIZE) {
            // seems bad in general
            if (e.satSizeLE(Converter.MAX_LINEARSUM_SIZE)) {
                return e;
            }
        } else {
            if (e.size() <= 1 || ! e.isDomainLargerThan(Converter.MAX_LINEARSUM_SIZE)) {
            // if (e.size() <= 1 || ! e.isDomainLargerThanExcept(MAX_LINEARSUM_SIZE)) {
                return e;
            }
        }
        int b = e.getB();
        LinearSum[] es = e.split(first ? 3 : Converter.SPLITS);
        e = new LinearSum(b);
        for (int i = 0; i < es.length; i++) {
            LinearSum ei = es[i];
            int factor = ei.factor();
            if (factor > 1) {
                ei.divide(factor);
            }
            // Recursive call is not necessary, but it works better
            ei = simplifyLinearExpression(ei, "eq", false);
            // System.out.println(es[i] + " ==> " + ei);
            if (ei.size() > 1) {
                IntegerVariable v = new IntegerVariable(ei.getDomain());
                v.setComment(v.getName() + " : " + ei);
                csp.add(v);
                Expression x = Expression.create(v.getName());
                Expression ex = ei.toExpression();
                Expression eq;
                if (! Converter.USE_EQ && ! Converter.EQUIV_TRANSLATION && cmp.equals("ge")) {
                    eq = x.le(ex);
                    eq.setComment(v.getName() + " <= " + ex);
                } else if (! Converter.USE_EQ && ! Converter.EQUIV_TRANSLATION && cmp.equals("le")) {
                    eq = x.ge(ex);
                    eq.setComment(v.getName() + " >= " + ex);
                } else {
                    eq = x.eq(ex);
                    eq.setComment(v.getName() + " == " + ex);
                }
                convertConstraint(eq);
                ei = new LinearSum(v);
            }
            if (factor > 1) {
                ei.multiply(factor);
            }
            e.add(ei);
        }
        return e;
    }
    
    private LinearSum reduceLinearExpression(LinearSum e, String cmp) throws SugarException {
        if (e.size() <= Converter.MAX_ARITY)
            return e;
        int b = e.getB();
        LinearSum[] es = e.split(Converter.MAX_ARITY);
        e = new LinearSum(b);
        for (int i = 0; i < es.length; i++) {
            LinearSum ei = es[i];
            int factor = ei.factor();
            if (factor > 1) {
                ei.divide(factor);
            }
            ei = reduceLinearExpression(ei, "eq");
            // System.out.println(es[i] + " ==> " + ei);
            if (ei.size() > 1) {
                IntegerVariable v = new IntegerVariable(ei.getDomain());
                v.setComment(v.getName() + " : " + ei);
                csp.add(v);
                Expression x = Expression.create(v.getName());
                Expression ex = ei.toExpression();
                Expression eq;
                if (! Converter.USE_EQ && cmp.equals("ge")) {
                    eq = x.le(ex);
                    eq.setComment(v.getName() + " <= " + ex);
                } else if (! Converter.USE_EQ && cmp.equals("le")) {
                    eq = x.ge(ex);
                    eq.setComment(v.getName() + " >= " + ex);
                } else {
                    eq = x.eq(ex);
                    eq.setComment(v.getName() + " == " + ex);
                }
                convertConstraint(eq);
                ei = new LinearSum(v);
            }
            if (factor > 1) {
                ei.multiply(factor);
            }
            e.add(ei);
        }
        return e;
    }


    private LinearSum reduceArity(LinearSum e, String cmp) throws SugarException {
        LinearSum[] es = e.splitPbPart();
        e = es[0];
        if (Converter.REDUCE_ARITY) {
            if (Converter.MAX_ARITY > 0) {
                e = reduceLinearExpression(e, cmp);
            } else if (e.size() > 3 && e.isDomainLargerThanExcept(Converter.MAX_LINEARSUM_SIZE)) {
                e = simplifyLinearExpression(e, cmp, true);
            }
        }
        if (es.length >= 2)
            e.add(es[1]);
        return e;
    }

    public LinearLiteral reduceArity(LinearLiteral lit) throws SugarException {
        LinearSum e = lit.getLinearExpression();
        String cmp = lit.getCmp();
        e = reduceArity(e, cmp);
        if (e == lit.getLinearExpression())
            return lit;
        if (cmp.equals("eq"))
            return new LinearEqLiteral(e);
        else if (cmp.equals("ne"))
            return new LinearNeLiteral(e);
        else if (cmp.equals("ge"))
            return new LinearGeLiteral(e);
        else if (cmp.equals("le"))
            return new LinearLeLiteral(e);
        else
            throw new SugarException("Unknown cmp " + cmp);
    }
    
    /*
    protected List<Clause> convertLE(Expression x) throws SugarException {
        LinearSum e = convertFormula(x);
        e.factorize();
        List<Clause> clauses = new ArrayList<Clause>();
        IntegerDomain d = e.getDomain();
        if (d.getUpperBound() <= 0) {
            return clauses;
        }
        if (d.getLowerBound() > 0) {
            clauses.add(new Clause());
            return clauses;
        }
        e = reduceArity(e);
        clauses.add(new Clause(new LinearLeLiteral(e)));
        return clauses;
    }
    */

    public List<Clause> convertComp(Expression x, Expression y, String cmp) throws SugarException {
        LinearSum e = convertFormula(x.sub(y));
        e.factorize();
        e = reduceArity(e, cmp);
        Literal lit = null;
        if (cmp.equals("eq")) {
            lit = new LinearEqLiteral(e);
        } else if (cmp.equals("ne")) {
            lit = new LinearNeLiteral(e);
        } else if (cmp.equals("ge")) {
            lit = new LinearGeLiteral(e);
        } else if (cmp.equals("le")) {
            lit = new LinearLeLiteral(e);
        } else {
            throw new SugarException("Unknown comparison operator in convertComp: " + cmp);
        }
        List<Clause> clauses = new ArrayList<Clause>();
        if (lit.isValid()) {
        } else if (lit.isUnsatisfiable()) {
            clauses.add(new Clause());
        } else {
            clauses.add(new Clause(lit));
        }
        return clauses;
    }

}

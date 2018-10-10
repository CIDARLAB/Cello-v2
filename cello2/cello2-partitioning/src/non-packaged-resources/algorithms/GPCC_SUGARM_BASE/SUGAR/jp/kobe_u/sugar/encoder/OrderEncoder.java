package jp.kobe_u.sugar.encoder;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import jp.kobe_u.sugar.SugarException;
import jp.kobe_u.sugar.csp.BooleanLiteral;
import jp.kobe_u.sugar.csp.CSP;
import jp.kobe_u.sugar.csp.Clause;
import jp.kobe_u.sugar.csp.HoldLiteral;
import jp.kobe_u.sugar.csp.IntegerDomain;
import jp.kobe_u.sugar.csp.IntegerVariable;
import jp.kobe_u.sugar.csp.LabelLiteral;
import jp.kobe_u.sugar.csp.LinearEqLiteral;
import jp.kobe_u.sugar.csp.LinearGeLiteral;
import jp.kobe_u.sugar.csp.LinearLeLiteral;
import jp.kobe_u.sugar.csp.LinearNeLiteral;
import jp.kobe_u.sugar.csp.LinearSum;
import jp.kobe_u.sugar.csp.Literal;
import jp.kobe_u.sugar.csp.PowerLiteral;
import jp.kobe_u.sugar.csp.ProductLiteral;
import jp.kobe_u.sugar.csp.RelationLiteral;
import jp.kobe_u.sugar.csp.RelationLiteral.Brick;

public class OrderEncoder extends AbstractEncoder {

    public OrderEncoder(CSP csp, Problem problem) {
        super(csp, problem);
    }

    private int[] expand(int[] clause0, int n) {
        int[] clause = new int[clause0.length + n];
        for (int i = 0; i < clause0.length; i++) {
            clause[i + n] = clause0[i];
        }
        return clause;
    }

    // v <= value
    private int getCodeLE(IntegerVariable v, int value) {
        if (value < v.getDomain().getLowerBound()) {
            return Problem.FALSE_CODE;
        } else if (value >= v.getDomain().getUpperBound()) {
            return Problem.TRUE_CODE;
        }
        return v.getCode() + v.getDomain().sizeLE(value) - 1;
    }

    // a * v <= b
    private int getCodeLE(IntegerVariable v, int a, int b) {
        int code;
        if (a >= 0) {
//          int c = (int) Math.floor((double) b / a);
            int c;
            if (b >= 0) {
                c = b/a;
            } else {
                c = (b-a+1)/a;
            }
            code = getCodeLE(v, c);
        } else {
//          int c = (int) Math.ceil((double) b / a) - 1;
            int c;
            if (b >= 0) {
                c = b/a - 1;
            } else {
                c = (b+a+1)/a - 1;
            }
            code = negateCode(getCodeLE(v, c));
        }
        return code;
    }

    private int getCode(LinearGeLiteral lit) throws SugarException {
        if (! lit.isSimple()) {
            throw new SugarException("Internal error " + lit.toString()); 
        }
        LinearSum linearSum = lit.getLinearExpression(); 
        int b = linearSum.getB(); 
        int code;
        if (linearSum.size() == 0) {
            code = b >= 0 ? Problem.TRUE_CODE : Problem.FALSE_CODE; 
        } else {
            IntegerVariable v = linearSum.getCoef().firstKey();
            int a = linearSum.getA(v);
            code = getCodeLE(v, -a, b);
        }
        return code;
    }

    private int getCode(LinearLeLiteral lit) throws SugarException {
        if (! lit.isSimple()) {
            throw new SugarException("Internal error " + lit.toString()); 
        }
        LinearSum linearSum = lit.getLinearExpression(); 
        int b = linearSum.getB(); 
        int code;
        if (linearSum.size() == 0) {
            code = b <= 0 ? Problem.TRUE_CODE : Problem.FALSE_CODE; 
        } else {
            IntegerVariable v = linearSum.getCoef().firstKey();
            int a = linearSum.getA(v);
            code = getCodeLE(v, a, -b);
        }
        return code;
    }

    @Override
    public void encodeIntegerVariable(IntegerVariable v) throws SugarException {
        problem.addComment(v.toString());
        IntegerDomain domain = v.getDomain();
        int[] clause = new int[2];
        int a0 = domain.getLowerBound();
        for (int a = a0 + 1; a <= domain.getUpperBound(); a++) {
            if (domain.contains(a)) {
                clause[0] = negateCode(getCodeLE(v, a0));
                clause[1] = getCodeLE(v, a);
                problem.addClause(clause);
                a0 = a;
            }
        }
    }

    /*
     * a1*v1+a2*v2+a3*v3+b <= 0
     * <--> v1>=c1 -> a2*v2+a3*v3+b+a1*c1 <= 0 (when a1>0)
     *      v1<=c1 -> a2*v2+a3*v3+b+a1*c1 <= 0 (when a1<0)
     * <--> v1>=c1 -> v2>=c2 -> a3*v3+b+a1*c1+a2*c2<= 0 (when a1>0, a2>0)
     * 
     */
    private void encodeLinearLe(int[] as, IntegerVariable[] vs, int i, int s, int[] clause) throws SugarException {
        if (i >= vs.length - 1) {
            // encoder.writeComment(a + "*" + vs[i].getName() + " <= " + (-s));
            clause[i] = getCodeLE(vs[i], as[i], -s);
            if (clause[i] != Problem.TRUE_CODE)
                problem.addClause(clause);
        } else {
            int lb0 = s;
            int ub0 = s;
            for (int j = i + 1; j < vs.length; j++) {
                int a = as[j];
                if (a > 0) {
                    lb0 += a * vs[j].getDomain().getLowerBound();
                    ub0 += a * vs[j].getDomain().getUpperBound();
                } else {
                    lb0 += a * vs[j].getDomain().getUpperBound();
                    ub0 += a * vs[j].getDomain().getLowerBound();
                }
            }
            int a = as[i];
            IntegerDomain domain = vs[i].getDomain();
            int lb = domain.getLowerBound();
            int ub = domain.getUpperBound();
            if (a >= 0) {
                // ub = Math.min(ub, (int)Math.floor(-(double)lb0 / a));
                if (-lb0 >= 0) {
                    ub = Math.min(ub, -lb0/a);
                } else {
                    ub = Math.min(ub, (-lb0-a+1)/a);
                }
                Iterator<Integer> iter = domain.values(lb, ub); 
                while (iter.hasNext()) {
                    int c = iter.next();
                    // vs[i]>=c -> ...
                    // encoder.writeComment(vs[i].getName() + " <= " + (c-1));
                    clause[i] = getCodeLE(vs[i], c - 1);
                    if (clause[i] != Problem.TRUE_CODE)
                        encodeLinearLe(as, vs, i+1, s+a*c, clause);
                }
                clause[i] = getCodeLE(vs[i], ub);
                if (clause[i] != Problem.TRUE_CODE)
                    encodeLinearLe(as, vs, i+1, s+a*(ub+1), clause);
            } else {
                // lb = Math.max(lb, (int)Math.ceil(-(double)lb0/a));
                if (-lb0 >= 0) {
                    lb = Math.max(lb, -lb0/a);
                } else {
                    lb = Math.max(lb, (-lb0+a+1)/a);
                }
                clause[i] = negateCode(getCodeLE(vs[i], lb - 1));
                if (clause[i] != Problem.TRUE_CODE)
                    encodeLinearLe(as, vs, i+1, s+a*(lb-1), clause);
                Iterator<Integer> iter = domain.values(lb, ub); 
                while (iter.hasNext()) {
                    int c = iter.next();
                    // vs[i]<=c -> ...
                    clause[i] = negateCode(getCodeLE(vs[i], c));
                    if (clause[i] != Problem.TRUE_CODE)
                        encodeLinearLe(as, vs, i+1, s+a*c, clause);
                }
            }
        }
    }
    
    private void encodeLinearLeLiteral(LinearLeLiteral lit, int[] clause) throws SugarException {
        if (lit.isValid()) {
        } else if (lit.isSimple()) {
            clause = expand(clause, 1);
            clause[0] = getCode(lit);
            problem.addClause(clause);
        } else {
            LinearSum linearSum = lit.getLinearExpression(); 
            int n = linearSum.size();
            IntegerVariable[] vs = linearSum.getVariablesSorted();
            int[] as = new int[n];
            for (int i = 0; i < n; i++)
                as[i] = linearSum.getA(vs[i]);
            clause = expand(clause, n);
            encodeLinearLe(as, vs, 0, linearSum.getB(), clause);
        }
    }

    private void encodeLinearNe(int[] as, IntegerVariable[] vs, int i, int s, int[] clause) throws SugarException {
        int i2 = i*2;
        if (i >= vs.length - 1) {
            // problem.addComment(as[i] + "*" + vs[i].getName() + " != " + (-s));
            clause[i2] = getCodeLE(vs[i], as[i], -s-1);
            clause[i2+1] = getCodeLE(vs[i], -as[i], s-1);
            if (clause[i2] != Problem.TRUE_CODE && clause[i2+1] != Problem.TRUE_CODE)
                problem.addClause(clause);
        } else {
            int a = as[i];
            IntegerDomain domain = vs[i].getDomain();
            Iterator<Integer> iter = domain.values(); 
            while (iter.hasNext()) {
                int c = iter.next();
                // vs[i]=c -> ...
                // encoder.writeComment(vs[i].getName() + " = " + c);
                clause[i2] = getCodeLE(vs[i], 1, c-1);
                clause[i2+1] = getCodeLE(vs[i], -1, -c-1);
                if (clause[i2] != Problem.TRUE_CODE && clause[i2+1] != Problem.TRUE_CODE)
                    encodeLinearNe(as, vs, i+1, s+a*c, clause);
            }
        }
    }
    
    private void encodeLinearNeLiteral(LinearNeLiteral lit, int[] clause) throws SugarException {
        if (lit.isValid()) {
        } else {
            LinearSum linearSum = lit.getLinearExpression(); 
            int n = linearSum.size();
            IntegerVariable[] vs = linearSum.getVariablesSorted();
            int[] as = new int[n];
            for (int i = 0; i < n; i++)
                as[i] = linearSum.getA(vs[i]);
            clause = expand(clause, 2*n);
            encodeLinearNe(as, vs, 0, linearSum.getB(), clause);
        }
    }

    private void encodeRelationLiteral(RelationLiteral lit, int[] clause0) throws SugarException {
        int arity = lit.arity;
        int[] clause = new int[2*arity + clause0.length];
        for (int i = 0; i < clause0.length; i++) {
            clause[2*arity + i] = clause0[i];
        }
        List<Brick> bricks = lit.getConflictBricks();
        for (Brick brick : bricks) {
            for (int i = 0; i < arity; i++) {
                IntegerVariable v = lit.vs[i];
                clause[2*i + 0] = getCodeLE(v, brick.lb[i] - 1);
                clause[2*i + 1] = negateCode(getCodeLE(v, brick.ub[i]));
            }
            problem.addClause(clause);
        }
    }

    private void encodeLiteral(Literal lit, int[] clause0) throws SugarException {
        if (lit instanceof BooleanLiteral) {
            clause0 = expand(clause0, 1);
            clause0[0] = ((BooleanLiteral)lit).getCode();
            problem.addClause(clause0);
        } else if (lit instanceof RelationLiteral) {
            encodeRelationLiteral((RelationLiteral)lit, clause0);
        } else if (lit instanceof LinearLeLiteral) {
            encodeLinearLeLiteral((LinearLeLiteral)lit, clause0);
        } else if (lit instanceof LinearGeLiteral) {
            LinearSum e = new LinearSum(0);
            e.subtract(((LinearGeLiteral)lit).getLinearExpression());
            encodeLinearLeLiteral(new LinearLeLiteral(e), clause0);
        } else if (lit instanceof LinearEqLiteral) {
            LinearLeLiteral lit1 = new LinearLeLiteral(((LinearEqLiteral) lit).getLinearExpression());
            encodeLiteral(lit1, clause0);
            LinearGeLiteral lit2 = new LinearGeLiteral(((LinearEqLiteral) lit).getLinearExpression());
            encodeLiteral(lit2, clause0);
        } else if (lit instanceof LinearNeLiteral) {
            encodeLinearNeLiteral((LinearNeLiteral)lit, clause0);
        } else if (lit instanceof ProductLiteral) {
            throw new SugarException("Cannot encode " + lit.toString());
        } else if (lit instanceof PowerLiteral) {
            throw new SugarException("Cannot encode " + lit.toString());
        } else if (lit instanceof HoldLiteral) {
            throw new SugarException("Cannot encode " + lit.toString());
        } else {
            throw new SugarException("Cannot encode " + lit.toString());
        }
    }
    
    @Override
    public void encodeClause(Clause c) throws SugarException {
        if (c.isValid())
            return;
        if (! c.isSimple())
            throw new SugarException("Cannot encode non-simple clause "
                    + c.toString());
        problem.addComment(c.toString());
        try {
            int[] clause = new int[c.simpleSize()];
            List<Integer> groups = new ArrayList<Integer>();
            int weight = 1;
            Literal complexLit = null;
            int i = 0;
            for (Literal literal : c.getLiterals()) {
                if (literal.isSimple()) {
                    if (literal instanceof LabelLiteral) {
                        groups.add(((LabelLiteral)literal).getLabel());
                        continue;
                    }
                    int code;
                    if (literal instanceof BooleanLiteral) {
                        code = ((BooleanLiteral) literal).getCode();
                    } else if (literal instanceof LinearLeLiteral) {
                        code = getCode((LinearLeLiteral) literal);
                    } else if (literal instanceof LinearGeLiteral) {
                        code = getCode((LinearGeLiteral) literal);
                    } else {
                        throw new SugarException("Cannot encode literal " + literal.toString());
                    }
                    if (code == Problem.TRUE_CODE)
                        return;
                    clause[i++] = code;
                } else {
                    complexLit = literal;
                }
            }
            problem.beginGroups(groups, weight);
            if (complexLit == null) {
                problem.addClause(clause);
            } else {
                encodeLiteral(complexLit, clause);
            }
            problem.endGroups();
        } catch (SugarException e) {
            throw new SugarException(e.getMessage() + " in " + c);
        }
    }
}

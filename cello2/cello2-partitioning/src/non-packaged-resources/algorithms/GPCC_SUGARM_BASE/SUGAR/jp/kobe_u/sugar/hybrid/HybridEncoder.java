package jp.kobe_u.sugar.hybrid;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.StreamTokenizer;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Iterator;
import java.util.List;

import jp.kobe_u.sugar.SugarConstants;
import jp.kobe_u.sugar.SugarException;
import jp.kobe_u.sugar.SugarMain;
import jp.kobe_u.sugar.csp.BooleanLiteral;
import jp.kobe_u.sugar.csp.BooleanVariable;
import jp.kobe_u.sugar.csp.CSP;
import jp.kobe_u.sugar.csp.CSP.Objective;
import jp.kobe_u.sugar.csp.Clause;
import jp.kobe_u.sugar.csp.IntegerDomain;
import jp.kobe_u.sugar.csp.IntegerVariable;
import jp.kobe_u.sugar.csp.LinearEqLiteral;
import jp.kobe_u.sugar.csp.LinearGeLiteral;
import jp.kobe_u.sugar.csp.LinearLeLiteral;
import jp.kobe_u.sugar.csp.LinearNeLiteral;
import jp.kobe_u.sugar.csp.LinearSum;
import jp.kobe_u.sugar.csp.Literal;
import jp.kobe_u.sugar.encoder.Encoding;
import jp.kobe_u.sugar.encoder.Problem;
import jp.kobe_u.sugar.pb.PBExpr;
import jp.kobe_u.sugar.pb.PBProblem;

public class HybridEncoder {
    public CSP csp;
    public PBProblem problem;

    public HybridEncoder(CSP csp, PBProblem problem) {
        this.csp = csp;
        this.problem = problem;
    }

    private int[] expand(int[] clause0, int n) {
        int[] clause = new int[clause0.length + n];
        for (int i = 0; i < clause0.length; i++) {
            clause[i + n] = clause0[i];
        }
        return clause;
    }

    public int negateCode(int code) {
        if (code == Problem.FALSE_CODE) {
            code = Problem.TRUE_CODE;
        } else if (code == Problem.TRUE_CODE) {
            code = Problem.FALSE_CODE;
        } else {
            code = - code;
        }
        return code;
    }

    private void addClause(int[] clause) throws SugarException {
        for (int code : clause) {
            if (code == Problem.TRUE_CODE)
                return;
        }
        PBExpr expr = new PBExpr();
        int b = -1;
        for (int code : clause) {
            if (code != Problem.FALSE_CODE) {
                if (code > 0)
                    expr.add(1, code);
                else {
                    expr.add(-1, -code);
                    b += 1;
                }
            }
        }
        expr.setCmp(">=");
        expr.add(b);
        problem.addPB(expr);
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
            if (v.getEncoding() != Encoding.ORDER)
                throw new SugarException("Internal error " + lit.toString()); 
            int a = linearSum.getA(v);
            code = getCodeLE(v, a, -b);
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
            if (v.getEncoding() != Encoding.ORDER)
                throw new SugarException("Internal error " + lit.toString()); 
            int a = linearSum.getA(v);
            code = getCodeLE(v, -a, b);
        }
        return code;
    }

    private int getCode(LinearEqLiteral lit) throws SugarException {
        if (! lit.isSimple()) {
            throw new SugarException("Internal error " + lit.toString()); 
        }
        LinearSum linearSum = lit.getLinearExpression(); 
        int b = linearSum.getB(); 
        int code;
        if (linearSum.size() == 0) {
            code = b == 0 ? Problem.TRUE_CODE : Problem.FALSE_CODE; 
        } else {
            IntegerVariable v = linearSum.getCoef().firstKey();
            /* TODO
            if (v.getEncoding() != XXX_DIRECT)
                throw new SugarException("Internal error " + lit.toString()); 
            int a = linearSum.getA(v);
            code = getCodeLE(v, -a, b);
            */
            throw new SugarException("Internal error " + lit.toString()); 
        }
        return code;
    }

    private int getCode(LinearNeLiteral lit) throws SugarException {
        if (! lit.isSimple()) {
            throw new SugarException("Internal error " + lit.toString()); 
        }
        LinearSum linearSum = lit.getLinearExpression(); 
        int b = linearSum.getB(); 
        int code;
        if (linearSum.size() == 0) {
            code = b != 0 ? Problem.TRUE_CODE : Problem.FALSE_CODE; 
        } else {
            IntegerVariable v = linearSum.getCoef().firstKey();
            /* TODO
            if (v.getEncoding() != XXX_DIRECT)
                throw new SugarException("Internal error " + lit.toString()); 
            int a = linearSum.getA(v);
            code = getCodeLE(v, a, -b);
            */
            throw new SugarException("Internal error " + lit.toString()); 
        }
        return code;
    }

    public void commit() throws SugarException {
        problem.commit();
    }
    
    public void cancel() throws SugarException {
        problem.cancel();
    }
    
    public void encodeDelta() throws IOException, SugarException {
        int satVariablesCount = problem.variablesCount;
        for (IntegerVariable v : csp.getIntegerVariablesDelta()) {
            v.setCode(satVariablesCount + 1);
            int size = v.getSatVariablesSize();
            satVariablesCount += size;
        }
        for (BooleanVariable v : csp.getBooleanVariablesDelta()) {
            v.setCode(satVariablesCount + 1);
            int size = v.getSatVariablesSize();
            satVariablesCount += size;
        }
        problem.addVariables(satVariablesCount - problem.variablesCount);
        for (IntegerVariable v : csp.getIntegerVariablesDelta()) { 
            encode(v);
        }
        for (Clause c : csp.getClausesDelta()) {
            if (c.isValid())
                continue;
            encode(c);
        }
        problem.done();
    }

    private int getIntegerVariableSize(IntegerVariable v) throws SugarException {
        Encoding myEncoding = v.getEncoding();
        IntegerDomain domain = v.getDomain();
        int size = 0;
        switch (myEncoding) {
        case ORDER: {
            size = domain.size() - 1;
            break;
        }
        case LOG: {
            int BASE = 2;
            int domSize = domain.getUpperBound() - domain.getLowerBound() + 1;
            if (domSize == 1) {
                size = 0;
            } else {
                int m = (int)(Math.log(domSize - 1) / Math.log(BASE)) + 1;
                int msdSize = (domSize - 1) / (int)Math.pow(BASE, m - 1) + 1;
                size = (BASE - 1) * (m - 1) + (msdSize - 1);
            }
            break;
        }
        default:
            throw new SugarException("Unsupported encoding " + myEncoding);
        }
        return size;
    }

    private int getBooleanVariableCode(BooleanVariable v) throws SugarException {
        int code = problem.addVariables(1);
        return code;
    }
    
    private PBExpr getPbLog(IntegerVariable v) throws SugarException {
        int BASE = 2;
        IntegerDomain domain = v.getDomain();
        int code = v.getCode();
        PBExpr expr = new PBExpr();
        int domSize = domain.getUpperBound() - domain.getLowerBound() + 1;
        expr.add(domain.getLowerBound());
        if (domSize > 1) {
            int m = (int)(Math.log(domSize - 1) / Math.log(BASE)) + 1;
            int msdSize = (domSize - 1) / (int)Math.pow(BASE, m - 1) + 1;
            int a = 1;
            for (int i = 0; i < m; i++) {
                for (int j = 1; j < (i < m - 1 ? BASE: msdSize); j++) {
                    expr.add(a, code);
                    code++;
                }
                a *= BASE;
            }
        }
        return expr;
    }

    private void encode(IntegerVariable v) throws SugarException {
        Encoding myEncoding = v.getEncoding();
        IntegerDomain domain = v.getDomain();
        int code = v.getCode();
        switch (myEncoding) {
        case ORDER: {
            int domainSize = domain.size();
            for (int i = 2; i < domainSize; i++) {
                PBExpr expr = new PBExpr();
                expr.add(-1, code);
                expr.add(1, code + 1);
                expr.setCmp(">=");
                problem.addPB(expr);
                code++;
            }
            break;
        }
        case LOG: {
            PBExpr expr = getPbLog(v);
            if (expr.getUB() > domain.getUpperBound()) {
                PBExpr expr1 = new PBExpr();
                expr1.add(expr);
                expr1.add(-domain.getUpperBound());
                expr1.setCmp("<=");
                problem.addPB(expr1);
            }
            // if (domain.size() != domain.getUpperBound() - domain.getLowerBound() + 1)
            // throw new SugarException("Found non-contiguous domain " + v);
            int last = 0;
            Iterator<int[]> intervals = domain.intervals();
            while (intervals.hasNext()) {
                int[] interval = intervals.next();
                if (domain.getLowerBound() < interval[0]) {
                    int p = problem.addVariables(1);
                    PBExpr expr1 = new PBExpr();
                    expr1.add(expr);
                    expr1.add(-last);
                    expr1.setCmp("<=");
                    if (SugarMain.debug >= 2)
                        problem.addComment("# " + p + " " + expr1);
                    expr1.relax(p);
                    problem.addPB(expr1);
                    PBExpr expr2 = new PBExpr();
                    expr2.add(expr);
                    expr2.add(-interval[0]);
                    expr2.setCmp(">=");
                    if (SugarMain.debug >= 2)
                        problem.addComment("# " + p + " " + expr2);
                    expr2.relax(-p);
                    problem.addPB(expr2);
                }
                last = interval[1];
            }
            break;
        }
        default:
            throw new SugarException("Unsupported encoding " + myEncoding);
        }
    }

    private void addClauseWithPb(int[] clause, PBExpr pb, int s) throws SugarException {
        if (SugarMain.debug >= 2) {
            String comment = "";
            for (int code : clause)
                comment += code + " | ";
            comment += pb + " <= " + (-s);
            problem.addComment(comment);
        }
        PBExpr expr = new PBExpr();
        expr.add(pb, -1);
        expr.add(-s);
        expr.setCmp(">=");
        int a = expr.getLB();
        for (int code : clause) {
            if (code != Problem.FALSE_CODE)
                expr.add(-a, code);
        }
        problem.addPB(expr);
    }

    private void encodeLinearLe(int[] as, IntegerVariable[] vs, int i, int s, int[] clause, PBExpr pb) throws SugarException {
        if (pb.size() > 0 && i >= vs.length){
            addClauseWithPb(clause, pb, s);
        } else if (pb.size() == 0 && i >= vs.length - 1) {
            clause[i] = getCodeLE(vs[i], as[i], -s);
            if (clause[i] != Problem.TRUE_CODE)
                addClause(clause);
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
            lb0 += pb.getLB();
            ub0 += pb.getUB();
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
                        encodeLinearLe(as, vs, i+1, s+a*c, clause, pb);
                }
                clause[i] = getCodeLE(vs[i], ub);
                if (clause[i] != Problem.TRUE_CODE)
                    encodeLinearLe(as, vs, i+1, s+a*(ub+1), clause, pb);
            } else {
                // lb = Math.max(lb, (int)Math.ceil(-(double)lb0/a));
                if (-lb0 >= 0) {
                    lb = Math.max(lb, -lb0/a);
                } else {
                    lb = Math.max(lb, (-lb0+a+1)/a);
                }
                clause[i] = negateCode(getCodeLE(vs[i], lb - 1));
                if (clause[i] != Problem.TRUE_CODE)
                    encodeLinearLe(as, vs, i+1, s+a*(lb-1), clause, pb);
                Iterator<Integer> iter = domain.values(lb, ub); 
                while (iter.hasNext()) {
                    int c = iter.next();
                    // vs[i]<=c -> ...
                    clause[i] = negateCode(getCodeLE(vs[i], c));
                    if (clause[i] != Problem.TRUE_CODE)
                        encodeLinearLe(as, vs, i+1, s+a*c, clause, pb);
                }
            }
        }
    }

    private void encodeLinearLeLiteral(LinearLeLiteral lit, int[] clause) throws SugarException {
        if (lit.isValid()) {
        } else if (lit.isUnsatisfiable()) {
            addClause(clause);
        } else if (lit.isSimple()) {
            clause = expand(clause, 1);
            clause[0] = getCode(lit);
            addClause(clause);
        } else {
            LinearSum linearSum = lit.getLinearExpression(); 
            IntegerVariable[] vs = linearSum.getVariablesSorted();
            PBExpr pb = new PBExpr();
            List<Integer> asList = new ArrayList<Integer>();
            List<IntegerVariable> vsList = new ArrayList<IntegerVariable>();
            for (int i = 0; i < vs.length; i++) {
                IntegerVariable v = vs[i];
                int a = linearSum.getA(v);
                Encoding encoding = v.getEncoding();
                switch (encoding) {
                case ORDER:
                    asList.add(a);
                    vsList.add(v);
                    break;
                case LOG:
                    pb.add(getPbLog(v), a);
                    break;
                default:
                    throw new SugarException("Unknown encoding " + encoding + " of " + v);
                }
            }
            int n = vsList.size();
            clause = expand(clause, n);
            int[] as = new int[n];
            vs = new IntegerVariable[n];
            for (int i = 0; i < n; i++) {
                as[i] = asList.get(i);
                vs[i] = vsList.get(i);
            }
            if (pb.size() == 0 && n == 0){
                if (linearSum.getB() > 0)
                    addClause(clause);
                else
                    ;
            } else {
                encodeLinearLe(as, vs, 0, linearSum.getB(), clause, pb);
            }
        }
    }

    private void encodeComplexLiteral(Literal lit, int[] clause0) throws SugarException {
        if (lit instanceof LinearLeLiteral) {
            encodeLinearLeLiteral((LinearLeLiteral)lit, clause0);
        } else if (lit instanceof LinearGeLiteral) {
            LinearSum e = new LinearSum(0);
            e.subtract(((LinearGeLiteral)lit).getLinearExpression());
            encodeLinearLeLiteral(new LinearLeLiteral(e), clause0);
        // } else if (lit instanceof LinearEqLiteral) {
        // } else if (lit instanceof LinearNeLiteral) {
        } else {
            throw new SugarException("Cannot encode " + lit.toString());
        }
    }
    
    private void encode(Clause c) throws SugarException {
        if (c.isValid()) {
            return;
        }
        if (! c.isSimple())
            throw new SugarException("Cannot encode non-simple clause "
                    + c.toString());
        try {
            int[] clause = new int[c.simpleSize()];
            Literal complexLit = null;
            int i = 0;
            for (Literal literal : c.getLiterals()) {
                if (literal.isSimple()) {
                    int code;
                    if (literal instanceof BooleanLiteral) {
                        code = ((BooleanLiteral) literal).getCode();
                    } else if (literal instanceof LinearLeLiteral) {
                        code = getCode((LinearLeLiteral) literal);
                    } else if (literal instanceof LinearGeLiteral) {
                        code = getCode((LinearGeLiteral) literal);
                    } else if (literal instanceof LinearEqLiteral) {
                        code = getCode((LinearEqLiteral) literal);
                    } else if (literal instanceof LinearNeLiteral) {
                        code = getCode((LinearNeLiteral) literal);
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
            if (complexLit == null) {
                addClause(clause);
            } else {
                encodeComplexLiteral(complexLit, clause);
            }
        } catch (SugarException e) {
            throw new SugarException(e.getMessage() + " in " + c);
        }
    }
    
    private void encodeObjective() throws SugarException {
        if (HybridConverter.USE_PMINIMAL) {
            if (csp.getObjective() != Objective.MINIMIZE)
                throw new SugarException("Minimization is only supported in P-minimal model finding");
            StringBuffer sb = new StringBuffer();
            for (IntegerVariable v : csp.getObjectiveVariables()) {
                IntegerDomain domain = v.getDomain();
                int code = v.getCode();
                int domainSize = domain.size();
                for (int i = 2; i < domainSize; i++) {
                    sb.append(" -x" + code);
                    code++;
                }
            }
            problem.addPMin(sb.toString());
            PBExpr minExpr = new PBExpr();
            for (IntegerVariable v : csp.getObjectiveVariables()) {
                IntegerDomain domain = v.getDomain();
                int code = v.getCode();
                int domainSize = domain.size();
                for (int i = 2; i < domainSize; i++) {
                    minExpr.add(-1, code);
                    code++;
                }
            }
            problem.addMinExpr(minExpr);
            return;
        }
        // TODO Multi objective
        if (csp.getObjectiveVariables().size() != 1)
            throw new SugarException("Multi objective is not supported");
        IntegerVariable v = csp.getObjectiveVariables().get(0);
        PBExpr minExpr = new PBExpr();
        if (csp.getObjective() == Objective.MINIMIZE) {
            minExpr.add(getPbLog(v));
        } else {
            minExpr.add(getPbLog(v), -1);
        }
        minExpr.setB(0);
        problem.addMinExpr(minExpr);
    }
    
    public void encode() throws SugarException {
        problem.clear();
        for (IntegerVariable v : csp.getIntegerVariables()) {
            int size = getIntegerVariableSize(v);
            int code = problem.addVariables(size);
            v.setCode(code);
        }
        for (BooleanVariable v : csp.getBooleanVariables()) {
            int code = getBooleanVariableCode(v);
            v.setCode(code);
        }
        if (csp.getObjective() != Objective.NONE) {
            encodeObjective();
        }
        for (IntegerVariable v : csp.getIntegerVariables()) {
            problem.addComment("Variable " + v);
            encode(v);
        }
        for (Clause c : csp.getClauses()) {
            problem.addComment("Constraint " + c);
            encode(c);
        }
        problem.done();
    }

    public void outputMap(String mapFileName) throws IOException, SugarException {
        BufferedWriter mapWriter = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(mapFileName), "UTF-8"));
        if (csp.getObjectiveVariables() != null) {
            String s = "objective ";
            if (csp.getObjective().equals(Objective.MINIMIZE)) {
                s += SugarConstants.MINIMIZE;
            } else if (csp.getObjective().equals(Objective.MAXIMIZE)) {
                s += SugarConstants.MAXIMIZE;
            }
            for (IntegerVariable v : csp.getObjectiveVariables()) {
                s += " " + v.getName();
            }
            mapWriter.write(s);
            mapWriter.write('\n');
        }
        for (IntegerVariable v : csp.getIntegerVariables()) {
            if (! v.isAux() || SugarMain.debug > 0) {
                int code = v.getCode();
                StringBuilder sb = new StringBuilder();
                sb.append("int " + v.getName() + " " + code + " ");
                v.getDomain().appendValues(sb, true);
                sb.append('\n');
                Encoding encoding = v.getEncoding();
                sb.append("encoding " + v.getName());
                switch (encoding) {
                case ORDER:
                    sb.append(" order\n");
                    break;
                case LOG:
                    sb.append(" log\n");
                    break;
                default:
                    throw new SugarException("Uknown encoding " + encoding);
                }
                mapWriter.write(sb.toString());
            }
        }
        for (BooleanVariable v : csp.getBooleanVariables()) {
            if (! v.isAux() || SugarMain.debug > 0) {
                int code = v.getCode();
                String s = "bool " + v.getName() + " " + code;
                mapWriter.write(s);
                mapWriter.write('\n');
            }
        }
        mapWriter.close();
    }

    private void decode(IntegerVariable v, BitSet pbValues) throws SugarException {
        Encoding encoding = v.getEncoding();
        switch (encoding) {
        case ORDER:
            v.decode(pbValues);
            break;
        case LOG:
            PBExpr expr = getPbLog(v);
            int value = expr.value(pbValues);
            v.setValue(value);
            break;
        default:
            throw new SugarException("Unknown encoding " + encoding);
        }
    }
    
    private void decode(BooleanVariable v, BitSet pbValues) {
        int code = v.getCode();
        v.setValue(pbValues.get(code));
    }
    
    public boolean decode(String outFileName) throws IOException, SugarException {
        BufferedReader rd = new BufferedReader(new FileReader(outFileName));
        StreamTokenizer st = new StreamTokenizer(rd);
        st.eolIsSignificant(true);
        String result = null;
        boolean sat = false;
        BitSet pbValues = new BitSet();
        while (true) {
            st.nextToken();
            if (st.ttype == StreamTokenizer.TT_EOF)
                break;
            switch (st.ttype) {
            case StreamTokenizer.TT_EOL:
                break;
            case StreamTokenizer.TT_WORD:
                if (st.sval.equals("s")) {
                    st.nextToken();
                    result = st.sval;
                    do {
                        st.nextToken();
                    } while (st.ttype != StreamTokenizer.TT_EOL);
                } else if (st.sval.equals("c")) {
                    do {
                        st.nextToken();
                    } while (st.ttype != StreamTokenizer.TT_EOL);
                } else if (st.sval.equals("v")) {
                    boolean negative = false;
                    st.nextToken();
                    while (st.ttype != StreamTokenizer.TT_EOL) {
                        if (st.ttype == '-') {
                            negative = true;
                        } else if (st.ttype == StreamTokenizer.TT_WORD) {
                            int value = Integer.parseInt(st.sval.substring(1));
                            if (! negative)
                                pbValues.set(value);
                            negative = false;
                        }
                        st.nextToken();
                    }
                } else {
                    do {
                        st.nextToken();
                    } while (st.ttype != StreamTokenizer.TT_EOL);
                }
                break;
            default:
                // throw new SugarException("Unknown output " + st.sval);
            }
        }
        rd.close();
        if (result.startsWith("SAT") || result.startsWith("OPT")) {
            sat = true;
            for (IntegerVariable v : csp.getIntegerVariables()) {
                decode(v, pbValues);
            }
            for (BooleanVariable v : csp.getBooleanVariables()) {
                decode(v, pbValues);
            }
        } else if (result.startsWith("UNSAT")) {
            sat = false;
        } else {
            throw new SugarException("Unknown output result " + result);
        }
        return sat;
    }

}

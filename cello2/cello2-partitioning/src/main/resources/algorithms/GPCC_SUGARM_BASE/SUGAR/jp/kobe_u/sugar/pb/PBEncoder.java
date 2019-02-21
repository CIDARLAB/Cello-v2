package jp.kobe_u.sugar.pb;

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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;

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
import jp.kobe_u.sugar.csp.LinearGeLiteral;
import jp.kobe_u.sugar.csp.LinearLeLiteral;
import jp.kobe_u.sugar.csp.LinearLiteral;
import jp.kobe_u.sugar.csp.LinearSum;
import jp.kobe_u.sugar.csp.Literal;

public class PBEncoder {
    public static int MAX_VAREXPRMAP_SIZE = 10000;
    public static int BASE = 0;
    public static int ENCODING_OPTION = 1;
    public static int MIXED_BASE = 15;
    
    public static enum Encoding {
        DIRECT_ENCODING,
        ORDER_ENCODING,
        COMPACT_ORDER_ENCODING,
        MIXED_ENCODING
    }
    
    public CSP csp;
    public PBProblem problem;
    public Encoding encoding;
    private VarExprMap intMap;
    
    private class VarExprMap extends LinkedHashMap<IntegerVariable,PBExpr> {
        VarExprMap() {
            super(100, 0.75f, true);
        }

        /* (non-Javadoc)
         * @see java.util.LinkedHashMap#removeEldestEntry(java.util.Map.Entry)
         */
        @Override
        protected boolean removeEldestEntry(Entry<IntegerVariable,PBExpr> eldest) {
            return size() > MAX_VAREXPRMAP_SIZE;
        }
    }

    public PBEncoder(CSP csp, PBProblem problem, Encoding encoding) {
        this.csp = csp;
        this.problem = problem;
        this.encoding = encoding;
        intMap = new VarExprMap();
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

    private Encoding getEncoding(IntegerVariable v) {
        Encoding myEncoding = encoding;
        if (myEncoding.equals(Encoding.MIXED_ENCODING)) { 
            if (v.getDomain().size() <= MIXED_BASE) {
                myEncoding = Encoding.ORDER_ENCODING;
            } else {
                myEncoding = Encoding.COMPACT_ORDER_ENCODING;
                BASE = 2;
            }
        }
        return myEncoding;
    }
    
    private int getIntegerVariableSize(IntegerVariable v) throws SugarException {
        Encoding myEncoding = getEncoding(v);
        IntegerDomain domain = v.getDomain();
        int size = 0;
        switch (myEncoding) {
        case DIRECT_ENCODING: {
            size = domain.size() - 1;
            break;
        }
        case ORDER_ENCODING: {
            size = domain.size() - 1;
            break;
        }
        case COMPACT_ORDER_ENCODING: {
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
            break;
        }
        return size;
    }
    
    private PBExpr getIntegerVariableExpr(IntegerVariable v) throws SugarException {
        if (intMap.containsKey(v)) {
            return intMap.get(v);
        }
        Encoding myEncoding = getEncoding(v);
        int code = v.getCode();
        IntegerDomain domain = v.getDomain();
        PBExpr expr = new PBExpr();
        switch (myEncoding) {
        case DIRECT_ENCODING: {
            int value0 = domain.getLowerBound();
            expr.add(value0);
            Iterator<int[]> intervals = domain.intervals();
            while (intervals.hasNext()) {
                int[] interval = intervals.next();
                for (int value = interval[0]; value <= interval[1]; value++) {
                    if (value != value0) {
                        int a = value - domain.getLowerBound();
                        expr.add(a, code);
                        code++;
                    }
                    value0 = value;
                }
            }
            break;
        }
        case ORDER_ENCODING: {
            int value0 = domain.getLowerBound();
            expr.add(value0);
            Iterator<int[]> intervals = domain.intervals();
            while (intervals.hasNext()) {
                int[] interval = intervals.next();
                for (int value = interval[0]; value <= interval[1]; value++) {
                    if (value != value0) {
                        int a = value - value0;
                        expr.add(a, code);
                        code++;
                    }
                    value0 = value;
                }
            }
            break;
        }
        case COMPACT_ORDER_ENCODING: {
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
            if (code - v.getCode() != getIntegerVariableSize(v)) {
                throw new SugarException("Internal error " + v + " " + (code - v.getCode()) + " " + getIntegerVariableSize(v));
            }
            break;
        }
        default:
            break;
        }
        intMap.put(v, expr);
        return expr;
    }
    
    private int getBooleanVariableCode(BooleanVariable v) throws SugarException {
        int code = problem.addVariables(1);
        return code;
    }
    
    private void encode(IntegerVariable v) throws SugarException {
        Encoding myEncoding = getEncoding(v);
        IntegerDomain domain = v.getDomain();
        int code = v.getCode();
        switch (myEncoding) {
        case DIRECT_ENCODING: {
            int domainSize = domain.size();
            PBExpr expr = new PBExpr();
            for (int i = 1; i < domainSize; i++) {
                expr.add(1, code);
                code++;
            }
            expr.add(-1);
            expr.setCmp("<=");
            problem.addPB(expr);
            break;
        }
        case ORDER_ENCODING: {
            int domainSize = domain.size();
            for (int i = 2; i < domainSize; i++) {
                PBExpr expr = new PBExpr();
                expr.add(1, code);
                expr.add(-1, code + 1);
                expr.setCmp(">=");
                problem.addPB(expr);
                code++;
            }
            break;
        }
        case COMPACT_ORDER_ENCODING: {
            int domSize = domain.getUpperBound() - domain.getLowerBound() + 1;
            if (domSize > 1) {
                int m = (int)(Math.log(domSize - 1) / Math.log(BASE)) + 1;
                int msdSize = (domSize - 1) / (int)Math.pow(BASE, m - 1) + 1;
                for (int i = 0; i < m; i++) {
                    for (int j = 2; j < (i < m - 1 ? BASE: msdSize); j++) {
                        PBExpr expr = new PBExpr();
                        expr.add(1, code);
                        expr.add(-1, code + 1);
                        expr.setCmp(">=");
                        problem.addPB(expr);
                        code++;
                    }
                    code++;
                }
            }
            break;
        }
        default:
            break;
        }
        switch (myEncoding) {
        case COMPACT_ORDER_ENCODING:
            for (Clause clause : v.getDomainClauses()) {
                encode(clause);
            }
            PBExpr expr = getIntegerVariableExpr(v);
            if (expr.getUB() > domain.getUpperBound()) {
                PBExpr expr1 = new PBExpr();
                expr1.add(expr);
                expr1.add(-domain.getUpperBound());
                expr1.setCmp("<=");
                problem.addPB(expr1);
            }
            break;
        default:
            break;
        }
    }

    private int encodeSimpleLiteral(Literal lit) throws SugarException {
        // TODO
        int code = 0;
        if (lit instanceof BooleanLiteral) {
            BooleanLiteral p = (BooleanLiteral)lit;
            code = p.getBooleanVariable().getCode();
            if (p.getNegative())
                code = - code;
        } else if (lit instanceof LinearLeLiteral && lit.isSimple()) {
            throw new SugarException("Internal error " + lit);
        } else if (lit instanceof LinearGeLiteral && lit.isSimple()) {
            throw new SugarException("Internal error " + lit);
        } else {
            throw new SugarException("Internal error " + lit);
        }
        return code;
    }
    
    private int findGe(IntegerVariable v, int b) {
        int code = v.getCode() - 1;
        IntegerDomain domain = v.getDomain();
        Iterator<int[]> intervals = domain.intervals();
        while (intervals.hasNext()) {
            int[] interval = intervals.next();
            for (int value = interval[0]; value <= interval[1]; value++) {
                if (value >= b)
                    return code;
                code++;
            }
        }
        return 0;
    }

    private List<PBExpr> encodeSimpleLinear(LinearLiteral lit) throws SugarException {
        LinearSum sum = lit.getLinearExpression();
        if (sum.size() != 1)
            throw new SugarException("Internal error " + lit);
        IntegerVariable v = sum.getVariablesSorted()[0];
        int a = sum.getA(v);
        int b = - sum.getB();
        String cmp = lit.getCmp();
        if ((cmp.equals("ge") && a > 0) || (cmp.equals("le") && a < 0)) {
            b = lit.ceilDiv(b, a);
            a = 1;
            cmp = "ge";
        } else if ((cmp.equals("ge") && a < 0) || (cmp.equals("le") && a > 0)) {
            b = lit.floorDiv(b, a);
            a = 1;
            cmp = "le";
        } else {
            throw new SugarException("Internal error " + lit);
        }
        List<PBExpr> exprs = new ArrayList<PBExpr>();
        if ((cmp.equals("ge") && v.getDomain().getLowerBound() >= b) ||
                (cmp.equals("le") && v.getDomain().getUpperBound() <= b)) {
            return exprs;
        } else if ((cmp.equals("ge") && v.getDomain().getUpperBound() < b) ||
                (cmp.equals("le") && v.getDomain().getLowerBound() > b)) {
            PBExpr expr = new PBExpr();
            expr.add(-1);
            expr.setCmp(">=");
            exprs.add(expr);
            return exprs;
        }
        switch (encoding) {
        case ORDER_ENCODING:
            PBExpr expr = new PBExpr();
            if (cmp.equals("ge")) {
                int code = findGe(v, b);
                if (code == 0) {
                    expr.add(-1);
                    expr.setCmp(">=");
                    exprs.add(expr);
                    return exprs;
                }
                expr.add(1, code);
                expr.add(-1);
                expr.setCmp(">=");
            } else {
                // v <= b
                int code = findGe(v, b+1);
                if (code == 0) {
                    return exprs;
                }
                expr.add(1, code);
                expr.add(0);
                expr.setCmp("<=");
            }
            exprs.add(expr);
            return exprs;
        default:
            break;
        }
        return null;
    }
    
    private List<PBExpr> encodeLinear(LinearLiteral lit) throws SugarException {
        if (lit.isValid()) {
            List<PBExpr> exprs = new ArrayList<PBExpr>();
            return exprs;
        } else if (lit.isUnsatisfiable()) {
            List<PBExpr> exprs = new ArrayList<PBExpr>();
            PBExpr expr = new PBExpr();
            expr.add(-1);
            expr.setCmp(">=");
            exprs.add(expr);
            return exprs;
        }
        List<PBExpr> exprs = null;
        if (lit.getLinearExpression().size() == 1 && ENCODING_OPTION == 1) { 
            exprs = encodeSimpleLinear(lit);
        }
        if (exprs == null) {
            exprs = new ArrayList<PBExpr>();
            LinearSum sum = lit.getLinearExpression();
            String cmp = lit.getCmp();
            PBExpr expr = new PBExpr();
            expr.add(sum.getB());
            for (IntegerVariable v : sum.getVariables()) {
                int a = sum.getA(v);
                PBExpr expr1 = getIntegerVariableExpr(v);
                expr.add(expr1, a);
            }
            expr.setCmp(cmp);
            exprs.add(expr);
        }
        return exprs;
    }
    
    private void encode(Clause clause) throws SugarException {
        if (clause.isValid())
            return;
        int size = clause.size();
        int linearLiteralSize = 0;
        for (Literal lit : clause.getLiterals()) {
            if (lit instanceof LinearLiteral)
                linearLiteralSize++;
        }
        if (linearLiteralSize == 0) {
            PBExpr expr = new PBExpr();
            for (Literal lit : clause.getLiterals()) {
                int code = encodeSimpleLiteral(lit);
                expr.add(1, code);
            }
            expr.add(-1);
            expr.setCmp(">=");
            // problem.addComment(clause.toString());
            problem.addPB(expr);
        } else if (linearLiteralSize == 1 && size == 1) {
            Literal lit = clause.getLiterals().get(0);
            if (! (lit instanceof LinearLiteral))
                throw new SugarException("unknonw literal " + lit);
            for (PBExpr expr: encodeLinear((LinearLiteral)lit)) {
                // problem.addComment(clause.toString());
                problem.addPB(expr);
            }
        } else {
            PBExpr expr = new PBExpr();
            for (Literal lit : clause.getLiterals()) {
                if (lit.isUnsatisfiable())
                    continue;
                if (lit instanceof BooleanLiteral) {
                    int code = encodeSimpleLiteral(lit);
                    expr.add(1, code);
                } else if (lit instanceof LinearLiteral) {
                    int code = problem.addVariables(1);
                    expr.add(1, code);
                    for (PBExpr expr1: encodeLinear((LinearLiteral)lit)) {
                        // problem.addComment("~x" + code + " || " + lit + " : " + expr1);
                        expr1.relax(- code);
                        problem.addPB(expr1);
                    }
                } else {
                    throw new SugarException("not supported literal " + lit);
                }
            }
            expr.add(-1);
            expr.setCmp(">=");
            // problem.addComment(clause.toString());
            problem.addPB(expr);
        }
    }
    
    private void encodeObjective() throws SugarException {
        // TODO Multi objective
        if (csp.getObjectiveVariables().size() != 1)
            throw new SugarException("Multi objective is not supported");
        IntegerVariable v = csp.getObjectiveVariables().get(0);
        PBExpr minExpr = new PBExpr();
        if (csp.getObjective() == Objective.MINIMIZE) {
            minExpr.add(getIntegerVariableExpr(v));
        } else {
            minExpr.add(getIntegerVariableExpr(v), -1);
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
            problem.addComment("Variable " + v + " : " + getIntegerVariableExpr(v));
            encode(v);
        }
        for (Clause c : csp.getClauses()) {
            problem.addComment("Constraint " + c);
            encode(c);
        }
        problem.done();
    }

    public void outputMap(String mapFileName) throws IOException {
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
                mapWriter.write(sb.toString());
                mapWriter.write('\n');
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
        PBExpr expr = getIntegerVariableExpr(v);
        int value = expr.value(pbValues);
        v.setValue(value);
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

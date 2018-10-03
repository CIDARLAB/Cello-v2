package jp.kobe_u.sugar.hybrid;

import jp.kobe_u.sugar.SugarMain;
import jp.kobe_u.sugar.Logger;
import jp.kobe_u.sugar.SugarException;
import jp.kobe_u.sugar.converter.Converter;
import jp.kobe_u.sugar.converter.Simplifier;
import jp.kobe_u.sugar.csp.CSP;
import jp.kobe_u.sugar.csp.IntegerDomain;
import jp.kobe_u.sugar.csp.IntegerVariable;
import jp.kobe_u.sugar.csp.Clause;
import jp.kobe_u.sugar.csp.LinearLiteral;
import jp.kobe_u.sugar.csp.LinearSum;
import jp.kobe_u.sugar.csp.Literal;
import jp.kobe_u.sugar.encoder.Encoding;

public class HybridConverter {
    public static int HYBRID_OPTION = 0;
    public static int HYBRID_PARAM = 4096;
    public static boolean USE_PMINIMAL = false;

    private CSP csp;
    private Converter converter;
    
    public HybridConverter(CSP csp) {
        this.csp = csp;
        this.converter = new Converter(csp);
    }

    private void classifyByBoolean() {
        for (IntegerVariable v: csp.getIntegerVariables()) {
            if (v.isBoolean())
                v.setEncoding(Encoding.LOG);
        }
    }

    private void classifyByPB() {
        for (Clause clause : csp.getClauses()) {
            for (Literal lit : clause.getLiterals()) {
                if (lit instanceof LinearLiteral) {
                    LinearSum e = ((LinearLiteral)lit).getLinearExpression();
                    boolean isPb = true;
                    for (IntegerVariable v : e.getVariables()) {
                        if (! v.isBoolean()) {
                            isPb = false;
                            break;
                        }
                    }
                    if (isPb) {
                        for (IntegerVariable v : e.getVariables())
                            v.setEncoding(Encoding.LOG);
                    }
                }
            }
        }
    }

    private void classifyByDomainSize() {
        for (IntegerVariable v: csp.getIntegerVariables()) {
            IntegerDomain domain = v.getDomain();
            if (domain.size() > HYBRID_PARAM) {
                v.setEncoding(Encoding.LOG);
            }
        }
    }

    private void classifyByDomainProduct() {
        for (Clause clause : csp.getClauses()) {
            for (Literal lit : clause.getLiterals()) {
                if (lit instanceof LinearLiteral) {
                    LinearSum e = ((LinearLiteral)lit).getLinearExpression();
                    if (e.isDomainLargerThanExcept(HYBRID_PARAM)) {
                        for (IntegerVariable v : e.getVariables())
                            v.setEncoding(Encoding.LOG);
                    }
                }
            }
        }
    }

    public void classifyVariables() throws SugarException {
        switch (HYBRID_OPTION) {
        case 0: {
            // Order encoding all
            Logger.fine("Hybrid 0 (Order encoding)");
            break;
        }
        case 1: {
            // Log encoding all
            Logger.fine("Hybrid 1 (Log encoding)");
            for (IntegerVariable v: csp.getIntegerVariables()) {
                v.setEncoding(Encoding.LOG);
            }
            break;
        }
        case 2: {
            // Log encoding 0-1 variables, order encoding others
            Logger.fine("Hybrid 2 (Log encoding for 0-1 variables)");
            classifyByBoolean();
            break;
        }
        case 3: {
            // Log encoding large domain size
            Logger.fine("Hybrid 3 (by Domain Size " + HYBRID_PARAM + ")");
            classifyByDomainSize();
            break;
        }
        case 4: {
            // Log encoding large domain product
            Logger.fine("Hybrid 4 (by Domain Product " + HYBRID_PARAM + ")");
            classifyByDomainProduct();
            break;
        }
        case 5: {
            // Log encoding 0-1 variables and large domain size
            Logger.fine("Hybrid 5 (by Domain Size " + HYBRID_PARAM + ")");
            classifyByBoolean();
            classifyByDomainSize();
            break;
        }
        case 6: {
            // Log encoding 0-1 variables and large domain product
            Logger.fine("Hybrid 6 (by Domain Product " + HYBRID_PARAM + ")");
            classifyByBoolean();
            classifyByDomainProduct();
            break;
        }
        case 7: {
            // Log encoding large domain size and PB constaint variables
            Logger.fine("Hybrid 7 (by Domain Size " + HYBRID_PARAM + ")");
            classifyByDomainSize();
            classifyByPB();
            break;
        }
        case 8: {
            // Log encoding large domain product and PB constaint variables
            Logger.fine("Hybrid 8 (by Domain Product " + HYBRID_PARAM + ")");
            classifyByDomainProduct();
            classifyByPB();
            break;
        }
        case 9: {
            // Log encoding 0-1, large domain size, and PB constaint variables
            Logger.fine("Hybrid 9 (by Domain Size " + HYBRID_PARAM + ")");
            classifyByBoolean();
            classifyByDomainSize();
            classifyByPB();
            break;
        }
        case 10: {
            // Log encoding 0-1, large domain product, and PB constaint variables
            Logger.fine("Hybrid 10 (by Domain Product " + HYBRID_PARAM + ")");
            classifyByBoolean();
            classifyByDomainProduct();
            classifyByPB();
            break;
        }
        }
        if (csp.getObjectiveVariables() != null) {
            if (USE_PMINIMAL) {
                // We classify objective variables as order encoding variables when searching P-Minimal models
                for (IntegerVariable v: csp.getObjectiveVariables())
                    v.setEncoding(Encoding.ORDER);
            } else {
                // We always classify objective variables as log encoding variables
                for (IntegerVariable v: csp.getObjectiveVariables())
                    v.setEncoding(Encoding.LOG);
            }
        }
    }

    public int[] count() {
        int order = 0;
        int log = 0;
        for (IntegerVariable v: csp.getIntegerVariables()) {
            Encoding encoding = v.getEncoding();
            if (SugarMain.debug >= 1)
                Logger.fine("Encoding : " + encoding + " " + v);
            switch (encoding) {
            case ORDER:
                order++;
                break;
            case LOG:
                log++;
                break;
            default:
                break;
            }
        }
        return new int[] { order, log };
    }
    
    public void convert() throws SugarException {
        csp.propagate();
        if (csp.isUnsatisfiable())
            return;
        classifyVariables();
        int[] count1 = count();
        Logger.fine("First classification of variables : Order=" + count1[0] + " Log=" + count1[1]);
        Converter.REDUCE_ARITY = true;
        converter.reduceAll();
        csp.propagate();
        if (csp.isUnsatisfiable())
            return;
        classifyVariables();
        int[] count2 = count();
        Logger.fine("Second classification of variables : Order=" + count2[0] + " Log=" + count2[1]);
        Simplifier simplifier = new Simplifier(csp);
        simplifier.simplify();
        Logger.info("CSP : " + csp.summary());
        if (SugarMain.debug > 0) {
            csp.output(System.out, "c ");
        }
        Logger.status();
        Runtime.getRuntime().gc();
    }

}

package jp.kobe_u.sugar;

import java.io.PrintWriter;
import java.util.Iterator;
import java.util.List;

import jp.kobe_u.sugar.csp.BooleanLiteral;
import jp.kobe_u.sugar.csp.BooleanVariable;
import jp.kobe_u.sugar.csp.CSP;
import jp.kobe_u.sugar.csp.CSP.Objective;
import jp.kobe_u.sugar.csp.Clause;
import jp.kobe_u.sugar.csp.HoldLiteral;
import jp.kobe_u.sugar.csp.IntegerDomain;
import jp.kobe_u.sugar.csp.IntegerVariable;
import jp.kobe_u.sugar.csp.LinearEqLiteral;
import jp.kobe_u.sugar.csp.LinearGeLiteral;
import jp.kobe_u.sugar.csp.LinearLeLiteral;
import jp.kobe_u.sugar.csp.LinearLiteral;
import jp.kobe_u.sugar.csp.LinearNeLiteral;
import jp.kobe_u.sugar.csp.LinearSum;
import jp.kobe_u.sugar.csp.Literal;
import jp.kobe_u.sugar.csp.PowerLiteral;
import jp.kobe_u.sugar.csp.ProductLiteral;
import jp.kobe_u.sugar.csp.Relation;
import jp.kobe_u.sugar.csp.RelationLiteral;
import jp.kobe_u.sugar.expression.Expression;
import jp.kobe_u.sugar.expression.Sequence;

public class OutputSMT implements OutputInterface {
    public enum Format {
        CSP, ASP, Prolog, SMT
    }

    private CSP csp;
    private Format format; 
    private Formatter formatter;
    private PrintWriter out;
 
    public OutputSMT() {
    }
    
    public void setCSP(CSP csp) {
        this.csp = csp;
    }

    public void setOut(PrintWriter out) {
        if (out == null)
            out = new PrintWriter(System.out);
        this.out = out;
    }

    public void setFormat(Format format) {
        if (format == null)
             format = Format.SMT;
        this.format = format;
        formatter = new Formatter(format);
    }

    public void setFormat(String format) throws SugarException {
        if (format == null || format.equals("smt")) {
            setFormat(Format.SMT);
        } else {
            throw new SugarException("Unknown output format: " + format);
        }
    }

    class Formatter {
        private StringBuilder sb;
        private boolean escape;
        private String quote;
        private String comment;
        private String prefix;
        private String lparen;
        private String rparen;
        private String delimiter;
        private String period;
        private int depth;
        private boolean first;

        Formatter(Format format) {
            sb = new StringBuilder();
            depth = 0;
            first = true;
            switch (format) {
            case CSP:
            case SMT:
                prefix = null;
                escape = false;
                quote = "";
                comment = "; ";
                lparen = "(";
                rparen = ")";
                delimiter = " ";
                period = "";
                break;
            case Prolog:
                prefix = null;
                escape = true;
                quote = "'";
                comment = "% ";
                lparen = "[";
                rparen = "]";
                delimiter = ",";
                period = ".";
                break;
            case ASP:
                prefix = "list";
                escape = true;
                quote = "'";
                comment = "% ";
                lparen = "(";
                rparen = ")";
                delimiter = ",";
                period = ".";
                break;
            }
        }

        String escape(String str) throws SugarException {
            if (escape) {
                if (str.contains(quote))
                    throw new SugarException("String contains quotation mark: " + str);
                if (! str.matches("(-?\\d+|[a-z]\\w*)"))
                    str = quote + str + quote;
            }
            return str;
        }
        
        Formatter reset() {
            sb = new StringBuilder();
            depth = 0;
            first = true;
            return this;
        }
        
        Formatter comment(String msg) {
            sb.append(comment);
            sb.append(msg);
            return this;
        }
        
        Formatter begin(String functor) throws SugarException {
            if (! first)
                sb.append(delimiter);
            first = true;
            if (prefix == null) {
                sb.append(lparen);
                if (functor != null) {
                    sb.append(escape(functor));
                    first = false;
                }
            } else {
                if (functor == null)
                    sb.append(prefix);
                else
                    sb.append(escape(functor));
                sb.append(lparen);
            }
            depth++;
            return this;
        }
        
        Formatter begin() throws SugarException {
            return begin(null);
        }
        
        Formatter addStr(String str) {
            if (! first)
                sb.append(delimiter);
            sb.append(str);
            first = false;
            return this;
        }
        
        Formatter add(String str) throws SugarException {
            return addStr(escape(str));
        }
        
        Formatter add(int n) throws SugarException {
            if (n < 0) {
                begin();
                addStr("-");
                addStr(Integer.toString(-n));
                end();
            } else {
                addStr(Integer.toString(n));
            }
            return this;
        }
        
        Formatter end() throws SugarException {
            sb.append(rparen);
            depth--;
            if (depth < 0)
                throw new SugarException("Too many end()");
            first = false;
            return this;
        }
        
        String finish() throws SugarException {
            if (depth != 0)
                throw new SugarException("Too short end()");
            sb.append(period);
            return toString();
        }

        public String toString() {
            String s = sb.toString();
            reset();
            return s;
        }
    }
    
    private void format(Expression x) throws SugarException {
        if (x.isAtom()) {
            formatter.add(x.toString());
            return;
        }
        Sequence seq = (Sequence)x;
        int i = 0;
        Expression f = seq.get(0);
        if (f.isString() && (Expression.isOperator(f) || csp.getRelation(f.stringValue()) != null)) {
            formatter.begin(f.stringValue());
            i++;
        } else {
            formatter.begin();
        }
        for (; i < seq.length(); i++) {
            format(seq.get(i));
        }
        formatter.end();
    }
    
    private void outputIntegerVariable(IntegerVariable v) throws SugarException {
        String name = v.getName();
        IntegerDomain domain = v.getDomain();
        String comment = v.getComment();
        formatter.reset();
        if (comment != null)
            out.println(formatter.comment(comment));
        formatter.begin("declare-fun").add(name).add("()").add("Int").end();
        out.println(formatter.finish());
        formatter.begin("assert");
        formatter.begin("or");
        Iterator<int[]> iter = domain.intervals();
        while (iter.hasNext()) {
            int[] interval = iter.next();
            formatter.begin("and");
            formatter.begin("<=").add(interval[0]).add(name).end();
            formatter.begin("<=").add(name).add(interval[1]).end();
            formatter.end();
        }
        formatter.end();
        formatter.end();
        out.println(formatter.finish());
    }
    
    private void outputBooleanVariable(BooleanVariable v) throws SugarException {
        String name = v.getName();
        String comment = v.getComment();
        formatter.reset();
        if (comment != null)
            out.println(formatter.comment(comment));
        formatter.begin("declare-fun").add(name).add("()").add("Bool").end();
        out.println(formatter.finish());
    }

    private void outputRelationDefinition(Relation rel) throws SugarException {
        /*
        formatter.reset();
        formatter.begin(SugarConstants.RELATION_DEFINITION);
        formatter.add(rel.name);
        formatter.add(rel.arity);
        if (rel.conflicts) {
            formatter.begin(SugarConstants.CONFLICTS);
        } else {
            formatter.begin(SugarConstants.SUPPORTS);
        }
        for (int[] tuple : rel.tuples) {
            formatter.begin();
            for (int a : tuple)
                formatter.add(a);
            formatter.end();
        }
        formatter.end();
        formatter.end();
        out.println(formatter.finish());
        */
        throw new SugarException("Relation is not supported");
    }

    private void formatLinearLiteral(LinearLiteral lit0, String cmp) throws SugarException {
        LinearSum s = lit0.getLinearExpression();
        String op = null;
        if (cmp.equals(SugarConstants.EQ)) {
            op = "=";
        } else if (cmp.equals(SugarConstants.NE)) {
            op = "distinct";
        } else if (cmp.equals(SugarConstants.LT)) {
            op = "<";
        } else if (cmp.equals(SugarConstants.LE)) {
            op = "<=";
        } else if (cmp.equals(SugarConstants.GT)) {
            op = ">";
        } else if (cmp.equals(SugarConstants.GE)) {
            op = ">=";
        }
        formatter.begin(op);
        if (s.size() == 1) {
            for (IntegerVariable v : s.getVariables()) {
                formatter.begin("*").add(s.getA(v)).add(v.getName()).end();
            }
        } else {
            formatter.begin("+");
            for (IntegerVariable v : s.getVariables()) {
                formatter.begin("*").add(s.getA(v)).add(v.getName()).end();
            }
            formatter.end();
        }
        formatter.add(- s.getB());
        formatter.end();
    }
    
    private void formatLiteral(Literal lit0) throws SugarException {
        if (lit0 instanceof HoldLiteral) {
            HoldLiteral lit = (HoldLiteral)lit0;
            if (lit.isNegative())
                formatter.begin(SugarConstants.NOT);
            Expression x = lit.getExpression();
            if (x.isSequence(Expression.ALLDIFFERENT)) {
                formatter.begin("distinct");
                Sequence args = (Sequence)((Sequence)x).get(1);
                for (int i = 0; i < args.length(); i++) {
                    if (! args.get(i).isAtom())
                        throw new SugarException("Invalid argument in alldifferent: " + args.get(i));
                    formatter.add(args.get(i).toString());
                }
                formatter.end();
            } else {
                format(x);
            }
            if (lit.isNegative())
                formatter.end();
        } else if (lit0 instanceof BooleanLiteral) {
            BooleanLiteral lit = (BooleanLiteral)lit0;
            if (lit.getNegative())
                formatter.begin(SugarConstants.NOT);
            formatter.add(lit.getBooleanVariable().getName());
            if (lit.getNegative())
                formatter.end();
        } else if (lit0 instanceof LinearEqLiteral) {
            formatLinearLiteral((LinearLiteral)lit0, SugarConstants.EQ);
        } else if (lit0 instanceof LinearNeLiteral) {
            formatLinearLiteral((LinearLiteral)lit0, SugarConstants.NE);
        } else if (lit0 instanceof LinearGeLiteral) {
            formatLinearLiteral((LinearLiteral)lit0, SugarConstants.GE);
        } else if (lit0 instanceof LinearLeLiteral) {
            formatLinearLiteral((LinearLiteral)lit0, SugarConstants.LE);
        } else if (lit0 instanceof RelationLiteral) {
            RelationLiteral lit = (RelationLiteral)lit0;
            if (lit.negative)
                formatter.begin(SugarConstants.NOT);
            formatter.begin(lit.name);
            for (IntegerVariable v : lit.vs) {
                formatter.add(v.getName());
            }
            formatter.end();
            if (lit.negative)
                formatter.end();
        } else if (lit0 instanceof ProductLiteral) {
            throw new SugarException("Unsupported literal: " + lit0);
        } else if (lit0 instanceof PowerLiteral) {
            throw new SugarException("Unsupported literal: " + lit0);
        } else {
            throw new SugarException("Unknown literal: " + lit0);
        }
    }
    
    private void outputClause(Clause clause) throws SugarException {
        List<Literal> literals = clause.getLiterals();
        String comment = clause.getComment();
        formatter.reset();
        if (comment != null)
            out.println(formatter.comment(comment));
        formatter.begin("assert");
        if (literals.size() == 1) {
            formatLiteral(literals.get(0));
        } else {
            formatter.begin(SugarConstants.OR);
            for (Literal lit : literals) {
                formatLiteral(lit);
            }
            formatter.end();
        }
        formatter.end();
        out.println(formatter.finish());
    }

    private void outputObjective(Objective objective, List<IntegerVariable> vs) throws SugarException {
        formatter.reset();
        formatter.begin(SugarConstants.OBJECTIVE_DEFINITION);
        if (objective == Objective.MINIMIZE) {
            formatter.add("minimize");
        } else if (objective == Objective.MAXIMIZE) {
            formatter.add("maximize");
        } else {
            throw new SugarException("Unknown objective: " + objective);
        }
        for (IntegerVariable v : vs) {
            formatter.add(v.getName());
        }
        formatter.end();
        out.println(formatter.finish());
    }

    public void outputBody() throws SugarException {
        formatter.reset();
        out.println(formatter.comment("File generated by sugar.Output"));
        formatter.begin("set-option").add(":produce-models").add("true").end();
        out.println(formatter.finish());
        formatter.begin("set-logic").add("QF_LIA").end();
        out.println(formatter.finish());
        if (csp.isUnsatisfiable()) {
            formatter.add(SugarConstants.FALSE);
            out.println(formatter.finish());
            return;
        }
        for (IntegerVariable v : csp.getIntegerVariables()) {
            outputIntegerVariable(v);
        }
        for (BooleanVariable v : csp.getBooleanVariables()) {
            outputBooleanVariable(v);
        }
        for (Relation rel : csp.getRelations()) {
            outputRelationDefinition(rel);
        }
        List<IntegerVariable> vs = csp.getObjectiveVariables();
        if (vs != null) {
            throw new SugarException("Optimization is not supported");
            // outputObjective(csp.getObjective(), vs);
        }
        for (Clause clause : csp.getClauses()) {
            outputClause(clause);
        }
    }
    
    public void outputPost() throws SugarException {
        formatter.begin("check-sat").end();
        out.println(formatter.finish());
        formatter.begin("get-value").begin();
        for (IntegerVariable v : csp.getIntegerVariables()) {
            if (! v.isAux())
                formatter.add(v.getName());
        }
        for (BooleanVariable v : csp.getBooleanVariables()) {
            if (! v.isAux())
                formatter.add(v.getName());
        }
        formatter.end().end();
        out.println(formatter.finish());
        formatter.begin("exit").end();
        out.println(formatter.finish());
    }
    
    public void output() throws SugarException {
        outputBody();
        outputPost();
    }
}

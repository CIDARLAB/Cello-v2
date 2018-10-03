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

public class Output implements OutputInterface {
    public enum Format {
        CSP, ASP, Prolog
    }

    private CSP csp;
    private Format format; 
    private Formatter formatter;
    private PrintWriter out;
 
    public Output() {
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
             format = Format.CSP;
        this.format = format;
        formatter = new Formatter(format);
    }

    public void setFormat(String format) throws SugarException {
        if (format == null || format.equals("csp")) {
            setFormat(Format.CSP);
        } else if (format.equals("prolog")) {
            setFormat(Format.Prolog);
        } else if (format.equals("asp")) {
            setFormat(Format.ASP);
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
        
        Formatter add(int n) {
            return addStr(Integer.toString(n));
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
        formatter.begin(SugarConstants.INT_DEFINITION).add(name);
        formatter.begin();
        Iterator<int[]> iter = domain.intervals();
        while (iter.hasNext()) {
            int[] interval = iter.next();
            formatter.begin().add(interval[0]).add(interval[1]).end();
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
        formatter.begin(SugarConstants.BOOL_DEFINITION).add(name).end();
        out.println(formatter.finish());
    }

    private void outputRelationDefinition(Relation rel) throws SugarException {
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
    }

    private void formatLinearLiteral(LinearLiteral lit0, String cmp) throws SugarException {
        String funcAdd = null;
        String funcMul = null;
        if (format == Format.ASP) {
            funcAdd = SugarConstants.ADD;
            funcMul = SugarConstants.MUL;
        }
        LinearSum s = lit0.getLinearExpression();
        formatter.begin(SugarConstants.WSUM);
        formatter.begin(funcAdd);
        for (IntegerVariable v : s.getVariables()) {
            formatter.begin(funcMul).add(s.getA(v)).add(v.getName()).end();
        }
        formatter.end();
        formatter.add(cmp);
        formatter.add(- s.getB());
        formatter.end();

    }
    
    private void formatLiteral(Literal lit0) throws SugarException {
        if (lit0 instanceof HoldLiteral) {
            HoldLiteral lit = (HoldLiteral)lit0;
            if (lit.isNegative())
                formatter.begin(SugarConstants.NOT);
            format(lit.getExpression());
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
        if (literals.size() == 1) {
            formatLiteral(literals.get(0));
        } else {
            formatter.begin(SugarConstants.OR);
            for (Literal lit : literals) {
                formatLiteral(lit);
            }
            formatter.end();
        }
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

    public void output() throws SugarException {
        formatter.reset();
        out.println(formatter.comment("File generated by sugar.Output"));
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
            outputObjective(csp.getObjective(), vs);
        }
        for (Clause clause : csp.getClauses()) {
            outputClause(clause);
        }
    }
}

package jp.kobe_u.sugar.expression;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StreamTokenizer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import jp.kobe_u.sugar.Logger;

/**
 * The Parser class parses an input and constructs a list of expressions.
 * @see Expression
 * @author Naoyuki Tamura
 */
public class Parser {
    private static Map<String,Expression> conv;
    
    static {
        conv = new HashMap<String,Expression>();
        conv.put("!",  Expression.NOT);
        conv.put("&&", Expression.AND);
        conv.put("||", Expression.OR);
        conv.put("=>", Expression.IMP);
        conv.put("=",  Expression.EQ);
        conv.put("!=", Expression.NE);
        conv.put("<=", Expression.LE);
        conv.put("<",  Expression.LT);
        conv.put(">=", Expression.GE);
        conv.put(">",  Expression.GT);
        conv.put("+",  Expression.ADD);
        conv.put("-",  Expression.SUB);
        conv.put("*",  Expression.MUL);
        conv.put("/",  Expression.DIV);
        conv.put("%",  Expression.MOD);
        conv.put("wsum", Expression.WEIGHTEDSUM);
    }
    
    private BufferedReader reader;
    private boolean prolog;
    private StreamTokenizer st;

    /**
     * Constructs a new parser.
     * @param reader an input reader
     */
    public Parser(BufferedReader reader, boolean prolog) {
        this.reader = reader;
        this.prolog = prolog;
    }

    public Parser(BufferedReader reader) {
        this(reader, false);
    }
    
    /**
     * Parses the input and returns a list of expressions.
     * 
     * @return a list of expressions
     * @throws IOException 
     */
    public List<Expression> parseCSP() throws IOException {
        st = new StreamTokenizer(reader);
        st.resetSyntax();
        st.commentChar(';');
        st.whitespaceChars(0x0000, 0x0020);
        st.wordChars('A', 'Z');
        st.wordChars('a', 'z');
        st.wordChars('_', '_');
        st.wordChars('0', '9');
        char[] chars = {
                '+', '-', '*', '/', '%', 
                '=', '<', '>', '!', '&', '|', '$' };
        for (char c : chars) {
            st.wordChars(c, c);
        }
        st.wordChars(0x000080, 0x10FFFF);
        st.parseNumbers();
        st.eolIsSignificant(false);

        Stack<List<Expression>> stack = new Stack<List<Expression>>();
        List<Expression> expressions = new ArrayList<Expression>();
        st.nextToken();
        while (st.ttype != StreamTokenizer.TT_EOF) {
            Expression x;
            switch (st.ttype) {
            case StreamTokenizer.TT_WORD:
                String s = st.sval;
                x = conv.get(s);
                if (x == null) {
                    x = Expression.create(s);
                }
                expressions.add(x);
                break;
            case StreamTokenizer.TT_NUMBER:
                int value = (int)st.nval;
                x = Expression.create(value);
                expressions.add(x);
                break;
            case '-':
                expressions.add(conv.get("-"));
                break;
            case '(':
                stack.push(expressions);
                expressions = new ArrayList<Expression>();
                break;
            case ')':
                if (expressions.size() == 2 && expressions.get(0).equals(Expression.SUB)) {
                    expressions.remove(0);
                    expressions.add(0, Expression.NEG);
                }
                x = Expression.create(expressions);
                if (stack.isEmpty()) {
                    throw new IOException("Too many right paren at line " + st.lineno());
                }
                expressions = stack.pop();
                expressions.add(x);
                if (stack.isEmpty()) {
                    int n = expressions.size();
                    if (n % 10000 == 0) {
                        Logger.fine("parsed " + n + " expressions");
                    }
                }
                break;
            default:
                char c = (char)st.ttype;
                throw new IOException("Bad character " + c + " at line " + st.lineno());
            }
            st.nextToken();
        }
        if (! stack.isEmpty()) {
            throw new IOException("Missing right paren at line " + st.lineno());
        }
        return expressions;
    }

    /**
     * Parses the input in Prolog format and returns a list of expressions.
     * Not perfect...
     * 
     * @return a list of expressions
     * @throws IOException 
     */
    public List<Expression> parseProlog() throws IOException {
        st = new StreamTokenizer(reader);
        st.resetSyntax();
        st.commentChar('%');
        st.whitespaceChars(0x0000, 0x0020);
        st.wordChars('A', 'Z');
        st.wordChars('a', 'z');
        st.wordChars('_', '_');
        st.wordChars('0', '9');
        char[] chars = {
                '+', '-', '*', '/',
                '=', '<', '>', '!', '&', '|', '$' };
        for (char c : chars) {
            st.wordChars(c, c);
        }
        st.wordChars(0x000080, 0x10FFFF);
        st.quoteChar('\'');
        // st.parseNumbers();
        st.eolIsSignificant(false);

        Stack<List<Expression>> stack = new Stack<List<Expression>>();
        List<Expression> expressions = new ArrayList<Expression>();
        st.nextToken();
        while (st.ttype != StreamTokenizer.TT_EOF) {
            Expression x;
            switch (st.ttype) {
            case StreamTokenizer.TT_WORD:
            case '\'':
                String s = st.sval;
                if (s.matches("-?\\d+")) {
                    int value = Integer.parseInt(s); 
                    x = Expression.create(value);
                } else {
                    x = conv.get(s);
                    if (x == null)
                        x = Expression.create(s);
                }
                expressions.add(x);
                break;
            case StreamTokenizer.TT_NUMBER:
                int value = (int)st.nval;
                x = Expression.create(value);
                expressions.add(x);
                break;
            case '-':
                expressions.add(conv.get("-"));
                break;
            case '[':
                stack.push(expressions);
                expressions = new ArrayList<Expression>();
                break;
            case ']':
                if (expressions.size() == 2 && expressions.get(0).equals(Expression.SUB)) {
                    expressions.remove(0);
                    expressions.add(0, Expression.NEG);
                }
                x = Expression.create(expressions);
                if (stack.isEmpty()) {
                    throw new IOException("Too many right paren at line " + st.lineno());
                }
                expressions = stack.pop();
                expressions.add(x);
                if (stack.isEmpty()) {
                    // System.out.println("## " + x);
                    int n = expressions.size();
                    if (n % 10000 == 0) {
                        Logger.fine("parsed " + n + " expressions");
                    }
                }
                break;
            case ',':
                break;
            case '.':
                break;
            default:
                char c = (char)st.ttype;
                throw new IOException("Bad character " + c + " at line " + st.lineno());
            }
            st.nextToken();
        }
        if (! stack.isEmpty()) {
            throw new IOException("Missing right paren at line " + st.lineno());
        }
        return expressions;
    }

    public List<Expression> parse() throws IOException {
        if (prolog)
            return parseProlog();
        else
            return parseCSP();
    }
    
    /**
     * Test main program for Parser class.
     * 
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Usage : java Parser file");
            System.exit(1);
        }
        String fileName = args[0];
        try {
            BufferedReader reader;
            reader = new BufferedReader(new InputStreamReader(
                    new FileInputStream(fileName), "UTF-8"));
            Parser parser = new Parser(reader);
            List<Expression> expressions = parser.parse();
            for (Expression x : expressions) {
                System.out.println(x);
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

}

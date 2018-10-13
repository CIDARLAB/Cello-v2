package jp.kobe_u.sugar;

import java.io.PrintWriter;

import jp.kobe_u.sugar.csp.CSP;

public interface OutputInterface {
    public void setCSP(CSP csp);
    public void setOut(PrintWriter out);
    public void setFormat(String format) throws SugarException;
    public void output() throws SugarException;
}

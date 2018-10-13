package jp.kobe_u.sugar.encoder;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.StreamTokenizer;
import java.util.BitSet;

import jp.kobe_u.sugar.Logger;
import jp.kobe_u.sugar.SugarConstants;
import jp.kobe_u.sugar.SugarException;
import jp.kobe_u.sugar.SugarMain;
import jp.kobe_u.sugar.csp.BooleanVariable;
import jp.kobe_u.sugar.csp.CSP;
import jp.kobe_u.sugar.csp.CSP.Objective;
import jp.kobe_u.sugar.csp.Clause;
import jp.kobe_u.sugar.csp.IntegerVariable;

/**
 * AbstractEncoder.
 * @see CSP 
 * @author Naoyuki Tamura (tamura@kobe-u.ac.jp)
 */
public abstract class AbstractEncoder {
    public CSP csp;
    public Problem problem;
    
    public AbstractEncoder(CSP csp, Problem problem) {
        this.csp = csp;
        this.problem = problem;
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

    public abstract void encodeIntegerVariable(IntegerVariable v) throws SugarException;
    
    public abstract void encodeClause(Clause c) throws SugarException;

    public void encode() throws SugarException {
        problem.clear();
        int satVariablesCount = 0;
        for (IntegerVariable v : csp.getIntegerVariables()) {
            v.setCode(satVariablesCount + 1);
            int size = v.getSatVariablesSize();
            satVariablesCount += size;
        }
        for (BooleanVariable v : csp.getBooleanVariables()) {
            v.setCode(satVariablesCount + 1);
            int size = v.getSatVariablesSize();
            satVariablesCount += size;
        }
        problem.addVariables(satVariablesCount - problem.variablesCount);
        int count = 0;
        int n = csp.getIntegerVariables().size();
        int percent = 10;
        for (IntegerVariable v : csp.getIntegerVariables()) {
            encodeIntegerVariable(v);
            count++;
            if ((100*count)/n >= percent) {
                Logger.fine(count + " (" + percent + "%) "
                        + "CSP integer variables are encoded"
                        + " (" + problem.summary() + ")");
                percent += 10;
            }
        }
        count = 0;
        n = csp.getClauses().size();
        percent = 10;
        for (Clause c : csp.getClauses()) {
            if (c.isValid())
                continue;
            int clausesCount0 = problem.clausesCount;
            encodeClause(c);
            count++;
            if (SugarMain.debug >= 1) {
                int k = problem.clausesCount - clausesCount0;
                Logger.fine(k + " SAT clauses for " + c);
            }
            if ((100*count)/n >= percent) {
                Logger.fine(count + " (" + percent + "%) "
                        + "CSP clauses are encoded"
                        + " (" + problem.summary() + ")");
                percent += 10;
            }
        }
        if (true) {
            int code0, code1;
            for (IntegerVariable v : csp.getIntegerVariables()) {
                if (! v.isDominant())
                    continue;
                code0 = v.getCode();
                code1 = code0 + v.getSatVariablesSize() - 1;
                if (code0 < code1)
                    problem.addPragmaDominant(code0, code1);
            }
            code0 = code1 = 0;
            for (BooleanVariable v : csp.getBooleanVariables()) {
                if (! v.isDominant())
                    continue;
                int code = v.getCode();
                if (code0 == 0) {
                    code0 = code1 = code;
                } else if (code1 + 1 == code) {
                    code1 = code;
                } else {
                    problem.addPragmaDominant(code0, code1);
                    code0 = code1 = code;
                }
            }
            if (code0 != 0) {
                problem.addPragmaDominant(code0, code1);
            }
        }
        problem.done();
        Logger.fine(count + " CSP clauses encoded");
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
            encodeIntegerVariable(v);
        }
        for (Clause c : csp.getClausesDelta()) {
            if (c.isValid())
                continue;
            encodeClause(c);
        }
        problem.done();
    }

    public void outputMap(String mapFileName) throws SugarException, IOException {
        BufferedWriter mapWriter = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(mapFileName), "UTF-8"));
//      BufferedOutputStream mapFile =
//          new BufferedOutputStream(new FileOutputStream(mapFileName));
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
//          mapFile.write(s.getBytes());
//          mapFile.write('\n');
            mapWriter.write(s);
            mapWriter.write('\n');
        }
        for (IntegerVariable v : csp.getIntegerVariables()) {
            if (! v.isAux() || SugarMain.debug > 0) {
                int code = v.getCode();
                StringBuilder sb = new StringBuilder();
                sb.append("int " + v.getName() + " " + code + " ");
                v.getDomain().appendValues(sb, true);
//              mapFile.write(sb.toString().getBytes());
//              mapFile.write('\n');
                mapWriter.write(sb.toString());
                mapWriter.write('\n');
            }
        }
        for (BooleanVariable v : csp.getBooleanVariables()) {
            if (! v.isAux() || SugarMain.debug > 0) {
                int code = v.getCode();
                String s = "bool " + v.getName() + " " + code;
//              mapFile.write(s.getBytes());
//              mapFile.write('\n');
                mapWriter.write(s);
                mapWriter.write('\n');
            }
        }
//      mapFile.close();
        mapWriter.close();
    }

    /*
    public void solveSAT() throws IOException, InterruptedException {
        File outFile = new File(outFileName);
        if (outFile.exists()) {
            outFile.delete();
        }
        String[] command = { satSolverName, satFileName, outFileName };
        SugarMain.log(satSolverName + " " + satFileName + " " + outFileName);
        Process process = Runtime.getRuntime().exec(command);
        BufferedReader stdout = new BufferedReader(
                new InputStreamReader(process.getInputStream()));
        BufferedReader stderr = new BufferedReader(
                new InputStreamReader(process.getErrorStream()));
        while (true) {
            String line = stderr.readLine();
            if (line == null)
                break;
            SugarMain.log(line);
        }
        stderr.close();
        while (true) {
            String line = stdout.readLine();
            if (line == null)
                break;
            SugarMain.log(line);
        }
        stdout.close();
        process.waitFor();
    }
    */

    public boolean decode(String outFileName) throws SugarException, IOException {
        BufferedReader rd = new BufferedReader(new FileReader(outFileName));
        StreamTokenizer st = new StreamTokenizer(rd);
        st.eolIsSignificant(true);
        String result = null;
        boolean sat = false;
        BitSet satValues = new BitSet();
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
                } else if (st.sval.equals("c")) {
                    do {
                        st.nextToken();
                    } while (st.ttype != StreamTokenizer.TT_EOL);
                } else if (st.sval.equals("v")) {
                    do {
                        st.nextToken();
                        int value = (int)st.nval;
                        int i = Math.abs(value);
                        if (i > 0) {
                            satValues.set(i, value > 0);
                        }
                    } while (st.ttype != StreamTokenizer.TT_EOL);
                } else {
                    result = st.sval;
                }
                break;
            case StreamTokenizer.TT_NUMBER:
                int value = (int)st.nval;
                int i = Math.abs(value);
                if (i > 0) {
                    satValues.set(i, value > 0);
                }
                break;
            default:
                // throw new SugarException("Unknown output " + st.sval);
            }
        }
        rd.close();
        if (result.startsWith("SAT")) {
            sat = true;
            for (IntegerVariable v : csp.getIntegerVariables()) {
                v.decode(satValues);
            }
            for (BooleanVariable v : csp.getBooleanVariables()) {
                v.decode(satValues);
            }
        } else if (result.startsWith("UNSAT")) {
            sat = false;
        } else {
            throw new SugarException("Unknown output result " + result);
        }
        return sat;
    }
}

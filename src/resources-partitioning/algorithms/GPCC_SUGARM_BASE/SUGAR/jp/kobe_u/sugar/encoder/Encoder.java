package jp.kobe_u.sugar.encoder;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.StreamTokenizer;
import java.util.BitSet;

import jp.kobe_u.sugar.SugarConstants;
import jp.kobe_u.sugar.SugarException;
import jp.kobe_u.sugar.SugarMain;
import jp.kobe_u.sugar.csp.BooleanVariable;
import jp.kobe_u.sugar.csp.CSP;
import jp.kobe_u.sugar.csp.CSP.Objective;
import jp.kobe_u.sugar.csp.IntegerVariable;

/**
 * Encoder encodes CSP into SAT.
 * @see CSP 
 * @author Naoyuki Tamura (tamura@kobe-u.ac.jp)
 */
public class Encoder extends OrderEncoder {
    public Encoder(CSP csp) {
        super(csp, null);
    }

    public void commit() throws SugarException {
        problem.commit();
    }
    
    public void cancel() throws SugarException {
        problem.cancel();
    }
    
    public int getSatVariablesCount() {
        return problem.variablesCount;
    }

    public int getSatClausesCount() {
        return problem.clausesCount;
    }

    public long getSatFileSize() {
        return problem.fileSize;
    }
    
    public void encode(Problem problem) throws SugarException {
        this.problem = problem;
        if (csp.getGroups() > 0) {
            problem.setGroups(csp.getGroups(), csp.getTopWeight());
        }
        encode();
    }

    public void encode(String satFileName) throws SugarException {
        encode(new FileProblem(satFileName));
    }

    public void encode(String satFileName, boolean incremental) throws SugarException {
        if (incremental)
            throw new SugarException("incremental is not supported");
        encode(satFileName);
    }

    @Override
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

    @Override
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
                    do {
                        st.nextToken();
                    } while (st.ttype != StreamTokenizer.TT_EOL);
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
                result = "SAT";
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
        if (result.startsWith("SAT") || result.startsWith("OPT")) {
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

    public String summary() {
        return problem.summary();
    }
}

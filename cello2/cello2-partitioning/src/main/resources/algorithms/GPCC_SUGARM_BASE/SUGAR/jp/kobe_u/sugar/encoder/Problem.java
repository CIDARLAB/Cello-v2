package jp.kobe_u.sugar.encoder;

import java.util.List;

import jp.kobe_u.sugar.SugarException;
import jp.kobe_u.sugar.SugarMain;

public abstract class Problem {
    public static boolean GCNF = false;
    public static boolean GWCNF = false;
    public static final int FALSE_CODE = 0;
    public static final int TRUE_CODE = Integer.MIN_VALUE;
    
    public int groups;
    public int topWeight;

    public int variablesCount = 0;
    public int clausesCount = 0;
    public long fileSize = 0;
    private int variablesCountSave = 0;
    private int clausesCountSave = 0;
    private long fileSizeSave = 0;
    protected String groupsString = null;
    protected String weightString = null;
    
    public void clear() throws SugarException {
        variablesCount = 0;
        clausesCount = 0;
        fileSize = 0;
        commit();
    }
    
    public void commit() throws SugarException {
        variablesCountSave = variablesCount;
        clausesCountSave = clausesCount;
        fileSizeSave = fileSize;
    }
    
    public void cancel() throws SugarException {
        variablesCount = variablesCountSave;
        clausesCount = clausesCountSave;
        fileSize = fileSizeSave;
    }
    
    public abstract void done() throws SugarException;

    public void addVariables(int number) throws SugarException {
        variablesCount += number;
    }

    public boolean isValid(int[] clause) {
        for (int code : clause)
            if (code == Problem.TRUE_CODE)
                return true;
        return false;
    }
    
    public int[] normalizeClause(int[] clause) {
        int c = 0;
        for (int code : clause) {
            if (code == Problem.FALSE_CODE)
                c++;
        }
        if (c == clause.length) {
            clause = null;
        } else if (c > 0) {
            int[] clause1 = new int[clause.length - c];
            int i = 0;
            for (int j = 0; j < clause.length; j++) {
                if (clause[j] != Problem.FALSE_CODE) 
                    clause1[i++] = clause[j];
            }
            clause = clause1;
        }
        return clause;
    }
    
    public void addComment(String comment) throws SugarException {
    }

    public void addPragmaDominant(int code0, int code1) throws SugarException {
    }

    public void setGroups(int groups, int topWeight) {
        this.groups = groups;
        this.topWeight = topWeight;
    }
    
    public abstract void addNormalizedClause(int[] clause) throws SugarException;
    
    public void addClause(int[] clause) throws SugarException {
        clause = normalizeClause(clause);
        if (clause == null) {
            if (variablesCount == 0)
                addVariables(1);
            addNormalizedClause(new int[] { 1 });
            clausesCount++;
            addNormalizedClause(new int[] { -1 });
            clausesCount++;
            return;
        } else if (isValid(clause)) {
            if (SugarMain.debug > 0) {
                System.out.print("True clause found:");
                for (int code : clause)
                    System.out.print(" " + code);
                System.out.println();
            }
            return;
        }
        addNormalizedClause(clause);
        clausesCount++;
    }
    
    /*
    public void addClause(List<Integer> clause0) throws SugarException {
        int[] clause = new int[clause0.size()];
        for (int i = 0; i < clause.length; i++) {
            clause[i] = clause0.get(i);
        }
        addClause(clause);
    }

    public void addClauses(List<int[]> clauses) throws SugarException {
        for (int[] clause : clauses)
            addClause(clause);
    }
    */
    
    public void beginGroups(List<Integer> groups, int weight) {
        if (groups == null || groups.size() == 0) {
            groupsString = null;
            weightString = null;
        } else {
            groupsString = "";
            String delim = "";
            for (int g : groups) {
                groupsString += delim + g;
                delim = ",";
            }
            weightString = Integer.toString(weight);
        }
    }

    public void endGroups() {
        groupsString = null;
        weightString = null;
    }

    public String summary() {
        return
        variablesCount + " SAT variables, " +
        clausesCount + " SAT clauses, " +
        fileSize + " bytes";
    }

}

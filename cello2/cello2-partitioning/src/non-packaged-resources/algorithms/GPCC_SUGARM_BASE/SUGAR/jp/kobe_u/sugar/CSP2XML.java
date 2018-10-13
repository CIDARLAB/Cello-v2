package jp.kobe_u.sugar;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.zip.GZIPInputStream;

import jp.kobe_u.sugar.expression.Expression;
import jp.kobe_u.sugar.expression.Parser;

/**
 * NOT IMPLEMENTED.  CSP2XML converts files in Sugar CSP format into XCSP format. 
 * @author Naoyuki Tamura (tamura@kobe-u.ac.jp)
 *
 */
/* TODO Under construction */
public class CSP2XML {
    private static int debug = 0;
    private String cspFileName;
    private String xmlFileName;
    private boolean prolog;
    private List<Expression> expressions;
    
    /**
     * Constructs a CSP to XCSP converter.
     * @param cspFileName
     * @param xmlFileName
     */
    public CSP2XML(String cspFileName, String xmlFileName, boolean prolog) {
        this.cspFileName = cspFileName;
        this.xmlFileName = xmlFileName;
        this.prolog = prolog;
    }

    private void convert() {
        
    }
    
    private void load() throws IOException {
        Logger.fine("Parsing " + cspFileName);
        InputStream in;
        if (cspFileName.endsWith(".gz")) {
            in = new GZIPInputStream(new FileInputStream(cspFileName));
        } else {
            in = new FileInputStream(cspFileName);
        }
        BufferedReader reader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
        Parser parser = new Parser(reader, prolog);
        expressions = parser.parse();
        Logger.info("parsed " + expressions.size() + " expressions");
        reader.close();
    }
    
    /**
     * Main program of CSP2XCSP.
     * @param args
     */
    public static void main(String[] args) {
        boolean prolog = false;
        int i = 0;
        while (i < args.length) {
            if (args[i].equals("-v") || args[i].equals("-verbose")) {
                Logger.verboseLevel++;
            } else if (args[i].equals("-prolog")) {
                prolog = true;
            } else if (args[i].equals("-debug") && i + 1 < args.length) {
                debug = Integer.parseInt(args[i+1]);
                i++;
            } else if (args[i].equals("-option") && i + 1 < args.length) {
                i++;
            } else if (! args[i].startsWith("-")) {
                break;
            }
            i++;
        }
        int n = args.length - i;
        if (n != 2) {
            System.out.println("Usage : java CSP2XML [-v] cspFileName xmlFileName");
            System.exit(1);
        }
        String cspFileName = args[i];
        String xmlFileName = args[i + 1];
        try {
            CSP2XML csp2xml = new CSP2XML(cspFileName, xmlFileName, prolog);
            csp2xml.load();
            csp2xml.convert();
        } catch (IOException e) {
            System.out.println("c ERROR Exception " + e.getMessage());
            e.printStackTrace();
        }
    }
}

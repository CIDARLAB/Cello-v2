/**
 * Copyright (C) 2018 Boston University (BU)
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:

 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.

 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package org.cellocad.v2.common.target.data.data;

/**
 * Rules is a class representing the rules for part and gate placement
 * <i>Eugene</i> algorithm.
 *
 * @author Timothy Jones
 *
 * @date 2018-08-10
 *
 */
public abstract class AbstractRules {

    protected static class Namer {

        public Namer() {
            this.last = "";
        }

        public String next() {
            String rtn = null;
            int len = this.last.length();
            if (this.last.endsWith("Z") || len < 1) {
                rtn = this.last + "A";
            } else {
                String prefix = this.last.substring(0, len - 1);
                char c = this.last.charAt(len - 1);
                rtn = prefix + nextChar(c);
            }
            this.last = rtn;
            return rtn;
        }

        private char nextChar(char c) {
            return (char) (c + 1);
        }

        private String last;
    }

    protected String getOperator(final String op) {
        String rtn = null;
        if (op.equals("AND")) {
            rtn = "&";
        } else if (op.equals("OR")) {
            rtn = "|";
        }
        return rtn;
    }

    protected void trim(StringBuilder builder) {
        boolean f;
        do {
            int l = builder.length();
            char b = builder.charAt(l - 1);
            char a = builder.charAt(l - 2);
            f = false;
            if (a == '(' && b == ')') {
                builder.deleteCharAt(l - 1);
                builder.deleteCharAt(l - 2);
                f = true;
            }
            if (b == '&' || b == '|') {
                builder.deleteCharAt(l - 1);
                f = true;
            }
        } while (f);
    }

    private void init() {
    }

    public AbstractRules() {
        init();
    }

    protected static final String S_RULES = "rules";
    protected static final String S_FUNCTION = "function";

}

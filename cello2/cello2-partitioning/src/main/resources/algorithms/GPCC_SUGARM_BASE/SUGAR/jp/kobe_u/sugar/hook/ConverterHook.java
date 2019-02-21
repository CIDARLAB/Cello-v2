package jp.kobe_u.sugar.hook;

import java.util.List;

import jp.kobe_u.sugar.SugarException;
import jp.kobe_u.sugar.converter.Converter;
import jp.kobe_u.sugar.csp.Clause;
import jp.kobe_u.sugar.expression.Expression;

public interface ConverterHook {
    public Expression convertFunction(Converter converter, Expression x) throws SugarException;
    public Expression convertConstraint(Converter converter, Expression x, boolean negative, List<Clause> clauses) throws SugarException;
}

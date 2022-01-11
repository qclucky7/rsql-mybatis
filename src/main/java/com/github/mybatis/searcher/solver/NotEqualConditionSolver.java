package com.github.mybatis.searcher.solver;

import net.sf.jsqlparser.expression.BinaryExpression;
import net.sf.jsqlparser.expression.operators.relational.NotEqualsTo;
import net.sf.jsqlparser.schema.Column;

/**
 * @author WangChen
 * @since 2021-10-14 14:14
 **/
@Symbol(SolverType.NOT_EQUAL)
public class NotEqualConditionSolver extends AbstractSingleParameterConditionSolver {

    @Override
    public BinaryExpression getExpression(String columnName) {
        NotEqualsTo notEqualsTo = new NotEqualsTo();
        notEqualsTo.setLeftExpression(new Column(columnName));
        return notEqualsTo;
    }
}

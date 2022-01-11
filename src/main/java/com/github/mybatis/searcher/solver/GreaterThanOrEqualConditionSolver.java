package com.github.mybatis.searcher.solver;

import net.sf.jsqlparser.expression.BinaryExpression;
import net.sf.jsqlparser.expression.operators.relational.GreaterThan;
import net.sf.jsqlparser.schema.Column;

/**
 * @author WangChen
 * @since 2021-10-14 14:21
 **/
@Symbol(SolverType.GREATER_THAN)
public class GreaterThanOrEqualConditionSolver extends AbstractSingleParameterConditionSolver {

    @Override
    public BinaryExpression getExpression(String columnName) {
        GreaterThan greaterThan = new GreaterThan();
        greaterThan.setLeftExpression(new Column(columnName));
        return greaterThan;
    }
}

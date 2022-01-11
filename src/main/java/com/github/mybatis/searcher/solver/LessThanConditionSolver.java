package com.github.mybatis.searcher.solver;

import net.sf.jsqlparser.expression.BinaryExpression;
import net.sf.jsqlparser.expression.operators.relational.MinorThan;
import net.sf.jsqlparser.schema.Column;

/**
 * @author WangChen
 * @since 2021-10-14 14:37
 **/
@Symbol(SolverType.LESS_THAN)
public class LessThanConditionSolver extends AbstractSingleParameterConditionSolver {

    @Override
    public BinaryExpression getExpression(String columnName) {
        MinorThan minorThan = new MinorThan();
        minorThan.setLeftExpression(new Column(columnName));
        return minorThan;
    }
}

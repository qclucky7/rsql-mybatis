package com.github.mybatis.searcher.solver;

import net.sf.jsqlparser.expression.BinaryExpression;
import net.sf.jsqlparser.expression.operators.relational.MinorThanEquals;
import net.sf.jsqlparser.schema.Column;

/**
 * @author WangChen
 * @since 2021-10-14 14:38
 **/
@Symbol(SolverType.LESS_THAN_OR_EQUAL)
public class LessThanOrEqualConditionSolver extends AbstractSingleParameterConditionSolver {

    @Override
    public BinaryExpression getExpression(String columnName) {
        MinorThanEquals minorThanEquals = new MinorThanEquals();
        minorThanEquals.setLeftExpression(new Column(columnName));
        return minorThanEquals;
    }
}

package com.github.mybatis.searcher.solver;

import net.sf.jsqlparser.expression.BinaryExpression;
import net.sf.jsqlparser.expression.operators.relational.GreaterThanEquals;
import net.sf.jsqlparser.schema.Column;

/**
 * @author WangChen
 * @since 2021-10-14 14:36
 **/
@Symbol(SolverType.GREATER_THAN_OR_EQUAL)
public class GreaterThanConditionSolver extends AbstractSingleParameterConditionSolver {


    @Override
    public BinaryExpression getExpression(String columnName) {
        GreaterThanEquals greaterThanEquals = new GreaterThanEquals();
        greaterThanEquals.setLeftExpression(new Column(columnName));
        return greaterThanEquals;
    }
}

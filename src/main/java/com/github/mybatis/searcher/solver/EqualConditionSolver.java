package com.github.mybatis.searcher.solver;

import net.sf.jsqlparser.expression.BinaryExpression;
import net.sf.jsqlparser.expression.operators.relational.EqualsTo;
import net.sf.jsqlparser.schema.Column;

/**
 * @author WangChen
 * @since 2021-10-14 14:14
 **/
@Symbol(SolverType.EQUAL)
public class EqualConditionSolver extends AbstractSingleParameterConditionSolver {

    @Override
    public BinaryExpression getExpression(String columnName) {
        EqualsTo equalsTo = new EqualsTo();
        equalsTo.setLeftExpression(new Column(columnName));
        return equalsTo;
    }
}

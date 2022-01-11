package com.github.mybatis.searcher.solver;

import net.sf.jsqlparser.expression.BinaryExpression;
import net.sf.jsqlparser.expression.operators.relational.LikeExpression;
import net.sf.jsqlparser.schema.Column;

/**
 * @author WangChen
 * @since 2021-10-14 14:39
 **/
@Symbol(SolverType.LIKE)
public class LikeConditionSolver extends AbstractSingleParameterConditionSolver {

    @Override
    public BinaryExpression getExpression(String columnName) {
        LikeExpression likeExpression = new LikeExpression();
        likeExpression.setLeftExpression(new Column(columnName));
        return likeExpression;
    }
}

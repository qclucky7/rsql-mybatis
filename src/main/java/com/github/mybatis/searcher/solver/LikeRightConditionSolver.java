package com.github.mybatis.searcher.solver;

import net.sf.jsqlparser.expression.BinaryExpression;
import net.sf.jsqlparser.expression.operators.relational.LikeExpression;
import net.sf.jsqlparser.schema.Column;

/**
 * @author WangChen
 * @since 2021-10-14 14:50
 **/
@Symbol(SolverType.LIKE_RIGHT)
public class LikeRightConditionSolver extends AbstractSingleParameterConditionSolver {

    @Override
    public BinaryExpression getExpression(String columnName) {
        LikeExpression likeExpression = new LikeExpression();
        likeExpression.setLeftExpression(new Column(columnName));
        return likeExpression;
    }
}

package com.github.mybatis.searcher.solver;

import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.operators.relational.InExpression;
import net.sf.jsqlparser.expression.operators.relational.ItemsList;
import net.sf.jsqlparser.schema.Column;

/**
 * @author WangChen
 * @since 2021-10-19 15:13
 **/
@Symbol(SolverType.IN)
public class InConditionSolver extends AbstractInConditionSolver {


    @Override
    public Expression getExpression(String columnName, ItemsList itemsList) {
        InExpression inExpression = new InExpression();
        inExpression.setLeftExpression(new Column(columnName));
        inExpression.setRightItemsList(itemsList);
        return inExpression;
    }


}

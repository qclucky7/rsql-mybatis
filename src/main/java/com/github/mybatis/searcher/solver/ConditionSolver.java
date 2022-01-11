package com.github.mybatis.searcher.solver;

import com.github.mybatis.searcher.SolverContextWrapper;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.StringValue;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.statement.select.PlainSelect;

import java.util.List;

/**
 * @author WangChen
 * @since 2021-10-14 12:00
 **/
public interface ConditionSolver extends Solver {

    String PLACEHOLDER = "?";

    /**
     * 获取占位表达式
     * @return Expression
     */
    default Expression getPlaceholderExpression(){
        return new Column(PLACEHOLDER);
    }

    /**
     * 解析
     * @param plainSelect plainSelect
     * @param solverContextWrapper solverContextWrapper
     * @return ExpressionDelegate ExpressionDelegate
     */
    ExpressionDelegate handle(PlainSelect plainSelect, SolverContextWrapper solverContextWrapper);


}

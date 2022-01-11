package com.github.mybatis.searcher.solver;

import cn.hutool.core.collection.CollUtil;
import com.github.mybatis.searcher.convert.ConverterFactory;
import net.sf.jsqlparser.expression.operators.relational.Between;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.statement.select.PlainSelect;

import java.lang.reflect.Field;
import java.util.List;

/**
 * @author WangChen
 * @since 2021-12-18 14:47
 **/
@Symbol(SolverType.BETWEEN)
public class BetweenConditionSolver extends AbstractConditionSolver {

    @Override
    public ExpressionWrapper doHandle(PlainSelect plainSelect, Field filed, String columnName, List<String> arguments) {
        if (arguments.size() < 2) {
            return null;
        }
        List<Object> result = ConverterFactory.lookupToConvert(filed, arguments);
        if (CollUtil.isEmpty(result) || result.size() < 2) {
            return null;
        }
        Object param1 = result.get(0);
        Object param2 = result.get(1);
        Between between = new Between();
        between.setLeftExpression(new Column(columnName));
        between.setBetweenExpressionStart(getPlaceholderExpression());
        between.setBetweenExpressionEnd(getPlaceholderExpression());
        return new ExpressionWrapper(between, filed.getType(), param1, param2);
    }
}

package com.github.mybatis.searcher.solver;

import cn.hutool.core.collection.CollUtil;
import com.github.mybatis.searcher.convert.ConverterFactory;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.operators.relational.ExpressionList;
import net.sf.jsqlparser.expression.operators.relational.ItemsList;
import net.sf.jsqlparser.statement.select.PlainSelect;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author WangChen
 * @since 2021-12-31 16:23
 **/
public abstract class AbstractInConditionSolver extends AbstractConditionSolver {

    @Override
    public ExpressionWrapper doHandle(PlainSelect plainSelect, Class<?> type, String columnName, List<String> arguments) {
        ExpressionList expressionList = new ExpressionList();
        List<Expression> placeholderExpression;
        List<Object> result = ConverterFactory.lookupToConvert(type, arguments);
        if (CollUtil.isEmpty(result)){
            return null;
        }
        placeholderExpression = result.stream()
                .map(arg -> getPlaceholderExpression())
                .collect(Collectors.toList());
        expressionList.setExpressions(placeholderExpression);
        Expression expression = getExpression(columnName, expressionList);
        return new ExpressionWrapper(expression, type, result);
    }

    /**
     * 获取in not in表达式
     *
     * @param columnName 列名
     * @param itemsList  参数列表
     * @return Expression
     */
    public abstract Expression getExpression(String columnName, ItemsList itemsList);
}

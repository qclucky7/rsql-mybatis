package com.github.mybatis.searcher.solver;

import net.sf.jsqlparser.expression.Expression;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

/**
 * @author WangChen
 * @since 2021-12-31 10:40
 **/
public class ExpressionWrapper implements ExpressionDelegate{

    private Expression expression;
    private Class<?> type;
    private List<Object> params;

    public ExpressionWrapper(Expression expression, Object param) {
        this(expression, null, param);
    }

    public ExpressionWrapper(Expression expression, Class<?> type, Supplier<Object> supplier) {
        this(expression, type, supplier.get());
    }

    public ExpressionWrapper(Expression expression, Class<?> type, Object param) {
        this(expression, type, Collections.singletonList(param));
    }

    public ExpressionWrapper(Expression expression, Class<?> type, Object... param){
        this(expression, type, Arrays.asList(param));
    }

    public ExpressionWrapper(Expression expression, Class<?> type, List<Object> params) {
        this.expression = expression;
        this.type = type;
        this.params = params;
    }

    @Override
    public Expression getExpression() {
        return expression;
    }

    @Override
    public Class<?> getType() {
        return type;
    }

    @Override
    public List<Object> getParams() {
        return params;
    }

    public void setExpression(Expression expression) {
        this.expression = expression;
    }

    public void setType(Class<?> type) {
        this.type = type;
    }

    public void setParams(List<Object> params) {
        this.params = params;
    }
}

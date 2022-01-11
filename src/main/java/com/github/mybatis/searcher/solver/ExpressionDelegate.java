package com.github.mybatis.searcher.solver;

import net.sf.jsqlparser.expression.Expression;

import java.util.List;

/**
 * @author WangChen
 * @since 2021-12-31 10:43
 **/
public interface ExpressionDelegate {

    /**
     * 获取表达式
     * @return Expression
     */
    Expression getExpression();

    /**
     * 获取属性类型
     * @return {@code Class<?>}
     */
    Class<?> getType();

    /**
     * 获取参数值
     * @return {@code List<Object>}
     */
    List<Object> getParams();
}

package com.github.mybatis.searcher.solver;

import cn.hutool.core.util.ObjectUtil;
import com.github.mybatis.searcher.convert.ConverterFactory;
import com.github.mybatis.searcher.tools.SqlUtils;
import net.sf.jsqlparser.expression.BinaryExpression;
import net.sf.jsqlparser.statement.select.PlainSelect;

import java.lang.reflect.Field;
import java.util.List;

/**
 * @author WangChen
 * @since 2021-12-31 11:39
 **/
public abstract class AbstractSingleParameterConditionSolver extends AbstractConditionSolver {

    @Override
    public ExpressionWrapper doHandle(PlainSelect plainSelect, Field filed, String columnName, List<String> arguments) {
        final String param = arguments.get(0);
        Object result = ConverterFactory.lookupToConvert(filed, param);
        if (ObjectUtil.isNull(result)) {
            return null;
        }
        BinaryExpression expression = getExpression(columnName);
        expression.setRightExpression(getPlaceholderExpression());
        return new ExpressionWrapper(expression, filed.getType(), () -> {
            if (LikeConditionSolver.class.equals(this.getClass())) {
                return SqlUtils.concatLike(String.valueOf(result), SqlUtils.SqlLike.DEFAULT);
            } else if (LikeRightConditionSolver.class.equals(this.getClass())) {
                return SqlUtils.concatLike(String.valueOf(result), SqlUtils.SqlLike.RIGHT);
            }
            return result;
        });
    }

    /**
     * 获取表达式
     *
     * @param columnName columnName
     * @return Expression Expression
     */
    public abstract BinaryExpression getExpression(String columnName);
}

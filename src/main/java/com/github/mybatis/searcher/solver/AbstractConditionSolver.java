package com.github.mybatis.searcher.solver;

import cn.hutool.core.annotation.AnnotationUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Filter;
import cn.hutool.core.util.ReflectUtil;
import com.github.mybatis.searcher.SearchEnumType;
import com.github.mybatis.searcher.SolverContextWrapper;
import net.sf.jsqlparser.statement.select.PlainSelect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.List;

/**
 * @author WangChen
 * @since 2021-10-14 11:48
 **/
public abstract class AbstractConditionSolver implements ConditionSolver {

    protected static final Logger logger = LoggerFactory.getLogger(AbstractConditionSolver.class);

    @Override
    public ExpressionDelegate handle(PlainSelect plainSelect, SolverContextWrapper solverContextWrapper) {
        SolverContext solverContext = solverContextWrapper.getSolverContext();
        Field field = solverContext.getField();
        List<String> arguments = solverContextWrapper.getArguments();
        return doHandle(plainSelect, getBaseType(field), solverContext.getColumnName(), arguments);
    }


    private Class<?> getBaseType(Field field){
        Class<?> type = field.getType();
        if (Enum.class.isAssignableFrom(type)){
            Field[] fields = ReflectUtil.getFields(type, val -> AnnotationUtil.hasAnnotation(val, SearchEnumType.class));
            if (fields.length > 0){
                return fields[0].getType();
            } else {
                return String.class;
            }
        }
        return type;
    }

    /**
     * 获取ExpressionWrapper
     *
     * @param plainSelect plainSelect
     * @param type       type
     * @param columnName  columnName
     * @param arguments   arguments
     * @return Expression expression
     */
    public abstract ExpressionWrapper doHandle(PlainSelect plainSelect, Class<?> type, String columnName, List<String> arguments);
}
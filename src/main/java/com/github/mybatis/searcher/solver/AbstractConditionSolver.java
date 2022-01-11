package com.github.mybatis.searcher.solver;

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
        return doHandle(plainSelect, field, solverContext.getColumnName(), arguments);
    }

    /**
     * 获取ExpressionWrapper
     *
     * @param plainSelect plainSelect
     * @param filed       filed
     * @param columnName  columnName
     * @param arguments   arguments
     * @return Expression expression
     */
    public abstract ExpressionWrapper doHandle(PlainSelect plainSelect, Field filed, String columnName, List<String> arguments);
}
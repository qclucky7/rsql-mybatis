package com.github.mybatis.searcher.solver;

import cn.hutool.core.util.ObjectUtil;
import com.github.mybatis.searcher.SearchType;
import com.github.mybatis.searcher.solver.operators.ExtensionRSQLOperators;
import cz.jirutka.rsql.parser.ast.ComparisonOperator;
import cz.jirutka.rsql.parser.ast.RSQLOperators;

import java.util.HashMap;
import java.util.Map;

/**
 * @author WangChen
 * @since 2021-12-10 09:18
 **/
public enum SolverType {

    /**
     * 等于
     */
    EQUAL(RSQLOperators.EQUAL),

    /**
     * 不等于
     */
    NOT_EQUAL(RSQLOperators.NOT_EQUAL),

    /**
     * 大于等于
     */
    GREATER_THAN_OR_EQUAL(RSQLOperators.GREATER_THAN_OR_EQUAL),

    /**
     * 大于
     */
    GREATER_THAN(RSQLOperators.GREATER_THAN),

    /**
     * 小于等于
     */
    LESS_THAN_OR_EQUAL(RSQLOperators.LESS_THAN_OR_EQUAL),

    /**
     * 小于
     */
    LESS_THAN(RSQLOperators.LESS_THAN),

    /**
     * in查询
     */
    IN(RSQLOperators.IN),

    /**
     * not in查询
     */
    OUT(RSQLOperators.NOT_IN),

    /**
     * 区间
     */
    BETWEEN(ExtensionRSQLOperators.BETWEEN),

    /**
     * 模糊查询
     */
    LIKE(ExtensionRSQLOperators.LIKE),

    /**
     * 右模糊
     */
    LIKE_RIGHT(ExtensionRSQLOperators.LIKE_RIGHT),

    /**
     * 可排序的
     */
    SORTABLE(ExtensionRSQLOperators.SORTABLE),

    /**
     * 查询包含字段
     */
    INCLUDE(ExtensionRSQLOperators.INCLUDE_FIELDS),

    /**
     * 查询排除字段
     */
    EXCLUDE(ExtensionRSQLOperators.EXCLUDE_FIELDS);


    private final ComparisonOperator operator;
    private static final Map<String, SolverType> NAME_CACHE = new HashMap<>(SolverType.values().length);

    static {
        for (SolverType solverType : SolverType.values()) {
            NAME_CACHE.put(solverType.name(), solverType);
        }
    }

    SolverType(ComparisonOperator operator) {
        this.operator = operator;
    }

    public ComparisonOperator getOperator() {
        return operator;
    }

    public static String[] getTargetSymbols(SearchType searchType) {
        SolverType solverType = NAME_CACHE.get(searchType.name());
        if (ObjectUtil.isNull(solverType)) {
            return new String[]{};
        }
        return solverType.getOperator().getSymbols();
    }

}

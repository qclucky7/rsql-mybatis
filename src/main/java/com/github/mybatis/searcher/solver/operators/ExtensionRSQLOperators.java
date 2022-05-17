package com.github.mybatis.searcher.solver.operators;

import cz.jirutka.rsql.parser.ast.ComparisonOperator;
import cz.jirutka.rsql.parser.ast.RSQLOperators;

import java.util.Set;

/**
 * @author WangChen
 * @since 2022-01-06 11:41
 **/
public final class ExtensionRSQLOperators extends RSQLOperators {

    public static final ComparisonOperator LIKE = new ComparisonOperator("=like=");
    public static final ComparisonOperator LIKE_RIGHT = new ComparisonOperator("=likeRight=");
    public static final ComparisonOperator SORTABLE = new ComparisonOperator("=sort=");
    public static final ComparisonOperator INCLUDE_FIELDS = new ComparisonOperator("=include=", true);
    public static final ComparisonOperator EXCLUDE_FIELDS = new ComparisonOperator("=exclude=", true);
    public static final ComparisonOperator BETWEEN = new ComparisonOperator("=between=", true);

    public static Set<ComparisonOperator> getAllOperators() {
        Set<ComparisonOperator> comparisonOperators = defaultOperators();
        comparisonOperators.add(LIKE);
        comparisonOperators.add(LIKE_RIGHT);
        comparisonOperators.add(SORTABLE);
        comparisonOperators.add(INCLUDE_FIELDS);
        comparisonOperators.add(EXCLUDE_FIELDS);
        comparisonOperators.add(BETWEEN);
        return comparisonOperators;
    }
}

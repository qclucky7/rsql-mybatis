package com.github.mybatis.searcher.solver;

import cn.hutool.core.collection.CollUtil;

import java.util.Set;

/**
 * @author WangChen
 * @since 2021-12-09 15:18
 **/
@Symbol(SolverType.EXCLUDE)
public class ExcludeSelectSetSolver extends AbstractSearchFieldsSetSolver {
    @Override
    public Set<String> getFilteredSelectFields(Set<String> provideSearchParameters, Set<String> querySearchParameters) {
        return (Set<String>) CollUtil.subtract(provideSearchParameters, querySearchParameters);
    }
}

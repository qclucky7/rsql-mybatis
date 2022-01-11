package com.github.mybatis.searcher.solver;

import cn.hutool.core.collection.CollUtil;

import java.util.Set;

/**
 * @author WangChen
 * @since 2021-12-09 15:17
 **/
@Symbol(SolverType.INCLUDE)
public class IncludeSelectSetSolver extends AbstractSearchFieldsSetSolver {
    @Override
    public Set<String> getFilteredSelectFields(Set<String> provideSearchParameters, Set<String> querySearchParameters) {
        return CollUtil.newHashSet(CollUtil.intersection(provideSearchParameters, querySearchParameters));
    }
}

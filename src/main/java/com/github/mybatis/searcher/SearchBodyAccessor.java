package com.github.mybatis.searcher;

import cn.hutool.core.util.StrUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author WangChen
 * @since 2022-01-06 14:30
 **/
public class SearchBodyAccessor extends ArrayList<SearchBodyAttributeAccessor> {

    public SearchBodyAccessor() {
        super();
    }

    public SearchBodyAccessor(int initialCapacity) {
        super(initialCapacity);
    }

    public static SearchBodyAccessor empty() {
        return new SearchBodyAccessor(0);
    }


    public Map<String, List<SearchBodyAttributeAccessor>> groupingBySymbol() {
        if (this.isEmpty()) {
            return Collections.emptyMap();
        }
        return this.stream()
                .collect(Collectors.groupingBy(SearchBodyAttributeAccessor::getSymbol));
    }


    public SearchBodyAttributeAccessor findByAttributeName(String name) {
        if (this.isEmpty() || StrUtil.isBlank(name)) {
            return null;
        }
        return this.stream()
                .filter(searchBodyAttributeAccessor -> name.equals(searchBodyAttributeAccessor.getAttributeName()))
                .findFirst().orElse(null);
    }
}

package com.github.mybatis.searcher.convert;

import cn.hutool.core.util.NumberUtil;

/**
 * @author WangChen
 * @since 2022-01-07 17:11
 **/
public class StringToIntegerConverter implements SearchConverter<Integer> {
    @Override
    public Integer convert(String target) {
        return NumberUtil.isInteger(target) ? Integer.valueOf(target) : null;
    }
}

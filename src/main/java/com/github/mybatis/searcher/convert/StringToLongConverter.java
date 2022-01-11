package com.github.mybatis.searcher.convert;

import cn.hutool.core.util.NumberUtil;

/**
 * @author WangChen
 * @since 2022-01-07 17:09
 **/
public class StringToLongConverter implements SearchConverter<Long> {
    @Override
    public Long convert(String target) {
        return NumberUtil.isLong(target) ? Long.valueOf(target) : null;
    }
}
